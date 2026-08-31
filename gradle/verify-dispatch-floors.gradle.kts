// The fourth check, and the only one that can see this class of mistake.
//
// verifyFloors reads bytecode, so it catches a class that exceeds its module's floor and a
// lower-floor class that names a higher-floor one. It structurally CANNOT catch a dispatch ladder
// sending an old server to a high-floor module, because the whole point of resolving modules by
// name is that no static reference exists for it to find. Nothing in TeleportProvider's constant
// pool mentions v_latest.
//
// So this check reads the ladders themselves. For every Minecraft version we claim to support it
// works out which module each provider would select, and asserts that module's floor is loadable on
// the JVM that Minecraft version runs on. Raising v_latest to Java 25 while 1.21.11 still fell
// through to it was caught here, by nothing else, before any server was booted.
//
// It never loads a version class and never runs a provider. It parses source and evaluates the
// comparisons.

// The JVM each Minecraft version actually runs on. The server's own requirement, not ours.
val serverJvms = listOf(
    "1.8" to 8, "1.8.3" to 8, "1.8.8" to 8, "1.9.2" to 8, "1.9.4" to 8, "1.10.2" to 8,
    "1.11.2" to 8, "1.12.2" to 8, "1.13" to 8, "1.13.2" to 8, "1.14.4" to 8, "1.15.2" to 8,
    "1.16.1" to 8, "1.16.2" to 8, "1.16.3" to 8, "1.16.5" to 8,
    "1.17.1" to 16,
    "1.18.1" to 17, "1.18.2" to 17, "1.19.2" to 17, "1.19.3" to 17, "1.19.4" to 17,
    "1.20.1" to 17, "1.20.2" to 17, "1.20.4" to 17,
    "1.20.6" to 21, "1.21.4" to 21, "1.21.9" to 21, "1.21.11" to 21,
    // Versions that do not exist yet, and are the point. The table originally jumped straight from
    // 1.21.11 to 26.1.2, so six ladders gated on `ver <= f("1.21.11")` routed a hypothetical 1.21.12
    // into the Java 25 module and nothing here modelled it. An era boundary has to be tested from
    // both sides, not just at the last release anyone has seen.
    "1.21.12" to 21, "1.21.99" to 21,
    "26.1.2" to 25, "26.2" to 25, "26.9.9" to 25,
)

// Three ladders do not branch on the version integer, so this check cannot decide them and they
// were read by hand instead. Naming them here rather than skipping anything unrecognised is what
// keeps the check fail-closed: a NEW undecidable ladder fails the build and forces the same reading.
//
//   WorldEditHook::worldEdit          selects on the WorldEdit plugin instance, not the version
//   WorldGuardHook::get               branches on ver.startsWith("6"), a WorldGuard version string
//   ItemTextProviderPre_1_17::provide throws above 1.16.5 by design, so it has no branch to take
//   ItemNbtProvider::provide          throws above 1.18.1 by design, for the same reason
val handChecked = setOf(
    "library/worldedit/WorldEditHook.java::worldEdit",
    "library/worldguard/WorldGuardHook.java::get",
    "provider/ItemTextProviderPre_1_17.java::provide",
    "provider/ItemNbtProvider.java::provide",
)

val moduleFloors = java.util.Properties().apply {
    rootProject.file("gradle/module-floors.properties").inputStream().use { load(it) }
}.entries.associate { (k, v) -> k.toString() to v.toString().trim().toInt() }

// NmsVersionParser.getFormattedNmsInteger, reproduced rather than called.
//
// Calling the real one would be better if the real one were reachable, but it is not. It ships in
// KamiCommon's standalone-utils, which this project takes as compileOnly and never bundles, so the
// copy on the compile classpath is whatever version the pin in build.gradle.kts names while the
// copy that actually runs comes from the consuming KamiCommon jar. Those two have already been five
// releases apart, far enough that the pinned one throws on the version strings Paper 26.x reports.
// A check that silently used the wrong one would evaluate every comparison against wrong numbers
// and still report success, so the agreement is asserted below instead of assumed.
fun encode(mcVersion: String): Int {
    val m = Regex("""^(\d+)(?:\.(\d+))?(?:\.(\d+))?""").find(mcVersion.trim())
        ?: throw GradleException("cannot read a version out of '$mcVersion'")
    val major = m.groupValues[1].toInt()
    val minor = m.groupValues[2].ifEmpty { "0" }.toInt()
    val patch = m.groupValues[3].ifEmpty { "0" }.toInt()
    // Legacy 1.x is packed textually, which is why 1.21.10 is 12110 rather than 1220.
    if (major == 1) return (if (minor <= 9) "10$minor$patch" else "1$minor$patch").toInt()
    // Calendar era, two digits each, so 26.2 reads as 26|02|00.
    return major * 10_000 + minor * 100 + patch
}

/** Any dispatch call, and the subset whose module is a string literal this check can read. */
val anyCall = Regex("""forModule\(""")
val literalCalls = Regex("forModule\\(\"")

/** `if (cond) {` opening a branch, or a `forModule("x")` call. Ordered, so the pairs stay aligned. */
val token = Regex("""if \(([^\n]*?)\)\s*\{|forModule\("([^"]+)"\)""")

/**
 * Blanks out comments, which are NOT source.
 *
 * Commenting a branch out is the ordinary way to disable a module for a moment, and the parser read
 * the commented rows as live: the dead branch went into the ladder and swallowed the versions the
 * real fallthrough should have caught, so the check went green on a jar that no longer had that
 * branch. The forModule count guard cannot see it either, since a dead call is counted on both
 * sides and cancels.
 *
 * Hand written rather than a regex. The obvious block-comment pattern backtracks catastrophically on
 * these files and took the task out with a StackOverflowError. It also has to respect string and
 * char literals, or a "//" inside a string would blank the rest of the line.
 *
 * Replaced with spaces rather than removed, so lengths and line numbers still line up.
 */
fun stripComments(text: String): String {
    val out = StringBuilder(text.length)
    var i = 0
    while (i < text.length) {
        val c = text[i]
        val next = if (i + 1 < text.length) text[i + 1] else '\u0000'
        when {
            c == '/' && next == '/' -> {
                while (i < text.length && text[i] != '\n') { out.append(' '); i++ }
            }
            c == '/' && next == '*' -> {
                out.append("  "); i += 2
                while (i < text.length && !(text[i] == '*' && i + 1 < text.length && text[i + 1] == '/')) {
                    out.append(if (text[i] == '\n') '\n' else ' '); i++
                }
                if (i < text.length) { out.append("  "); i += 2 }
            }
            c == '"' || c == '\'' -> {
                out.append(c); i++
                while (i < text.length && text[i] != c) {
                    if (text[i] == '\\' && i + 1 < text.length) { out.append(text[i]); i++ }
                    if (i < text.length) { out.append(text[i]); i++ }
                }
                if (i < text.length) { out.append(text[i]); i++ }
            }
            else -> { out.append(c); i++ }
        }
    }
    return out.toString()
}

/** A method declaration at class-body indentation. Its body is taken by matching braces. */
val methodHeader = Regex("""\n {4}(?:private|protected|public)[^\n{]*\b(\w+)\([^)]*\)\s*\{""")

/** `ver < f("26")`, `nmsVersion == f("1.8.8")`, `ver >= 1162`. Anything else is undecidable. */
val comparison = Regex("""^\s*(?:ver|nmsVersion)\s*(<=|>=|==|<|>)\s*(?:f\("([^"]+)"\)|(\d+))\s*$""")

val verifyDispatchFloors = tasks.register("verifyDispatchFloors") {
    group = "verification"
    description = "Checks that no dispatch ladder sends a server to a module its JVM cannot load."

    // Both modules, because a dispatch ladder can live in either. ItemTextProviderPre_1_17 moved to
    // :api so that text-impl can reach it from inside the nested jar, and a scan of :core alone
    // stopped seeing the only fifteen-branch ladder in the project while still reporting success.
    val sourceRoots = listOf(file("src/main/java"), rootProject.file("api/src/main/java"))
    // Every ladder reaches the encoder through an f() that delegates to NmsVersionParser. If any
    // stops delegating, encode() above is no longer what the ladder uses. Most ladders inherit
    // Provider.f, which lives in :api, so scanning only :core missed the one that matters.
    val encoderRoots = listOf(rootProject.file("api/src/main/java"), rootProject.file("core/src/main/java"))

    doLast {
        // Two ways the reproduced encoder could stop matching the real one, both checked.
        //
        // First, the values. These four span both eras and both legacy packing widths.
        val expected = mapOf(
            "1.8.8" to 1088, "1.16.5" to 1165, "1.21.10" to 12110, "1.21.11" to 12111,
            "26.1.2" to 260102, "26.2" to 260200,
        )
        for ((v, want) in expected) {
            val got = encode(v)
            if (got != want) {
                throw GradleException(
                    "the version encoder in this check returned $got for $v, expected $want. Every " +
                            "comparison below would be evaluated against the wrong numbers."
                )
            }
        }
        // Second, the delegation. An f() that stopped calling NmsVersionParser would make the
        // ladders mean something this check cannot see, and a count with no headroom cannot say so:
        // the previous form scanned :core only, found exactly 3 and required 3, which meant the
        // encoder Provider.f actually uses could be replaced wholesale without moving the number.
        //
        // So name the declaring types instead of counting files. These are every f() a ladder can
        // reach; if one appears that is not listed, the last clause reports it.
        val mustDelegate = setOf(
            "api/src/main/java/com/kamikazejam/kamicommon/nms/provider/Provider.java",
            "api/src/main/java/com/kamikazejam/kamicommon/nms/wrappers/NMSWrapper.java",
            "core/src/main/java/com/kamikazejam/kamicommon/nms/provider/event/PreSpawnSpawnerAdapter.java",
            "core/src/main/java/com/kamikazejam/kamicommon/nms/serializer/VersionedComponentSerializer.java",
            "core/src/main/java/com/kamikazejam/kamicommon/nms/util/VersionedComponentUtil.java",
        )
        val declaresF = Regex("""\b(?:int|Integer)\s+f\s*\(""")
        val delegates = Regex("""NmsVersionParser\.getFormattedNmsInteger""")
        val found = LinkedHashSet<String>()
        val notDelegating = ArrayList<String>()
        for (root in encoderRoots) {
            root.walkTopDown().filter { it.extension == "java" }.forEach { f ->
                val body = f.readText()
                if (!declaresF.containsMatchIn(body)) return@forEach
                val rel = f.path.substringAfter("${rootProject.projectDir}/")
                found.add(rel)
                if (!delegates.containsMatchIn(body)) notDelegating.add(rel)
            }
        }
        if (notDelegating.isNotEmpty()) {
            throw GradleException(
                "these declare an f(...) that no longer delegates to NmsVersionParser, so the ladders " +
                        "encode versions some way this check does not model:\n  " +
                        notDelegating.joinToString("\n  ")
            )
        }
        val missing = mustDelegate - found
        if (missing.isNotEmpty()) {
            throw GradleException(
                "expected an f(...) declaration in these and found none, so either they moved or the " +
                        "detection broke:\n  " + missing.joinToString("\n  ")
            )
        }
        val extra = found - mustDelegate
        if (extra.isNotEmpty()) {
            throw GradleException(
                "a new f(...) appeared that this check has never seen:\n  " + extra.joinToString("\n  ") +
                        "\nConfirm it delegates to NmsVersionParser, then add it to mustDelegate in " +
                        "gradle/verify-dispatch-floors.gradle.kts."
            )
        }

        val violations = ArrayList<String>()
        val undecidable = LinkedHashSet<String>()
        val ladders = ArrayList<Pair<String, List<Pair<String?, String>>>>()
        var sites = 0
        var decided = 0
        var callsSeen = 0
        var callsParsed = 0

        sourceRoots.flatMap { it.walkTopDown().filter { f -> f.extension == "java" } }
            .sortedBy { it.path }.forEach { file ->
            val text = stripComments(file.readText())
            // Gate on forModule in ANY form, not just forModule("literal"). WorldEditHook dispatches
            // through a helper taking the module as a parameter, so a literal-only gate skipped that
            // whole file rather than reporting that it could not read it.
            if (!text.contains("forModule")) return@forEach
            callsSeen += literalCalls.findAll(text).count()
            val where = file.path.substringAfter("/nms/")

            for (header in methodHeader.findAll(text)) {
                var depth = 1
                var i = header.range.last + 1
                while (i < text.length && depth > 0) {
                    if (text[i] == '{') depth++
                    if (text[i] == '}') depth--
                    i++
                }
                val body = text.substring(header.range.last + 1, i)
                if (!body.contains("forModule")) continue
                sites++
                val site = "$where::${header.groupValues[1]}"

                // A forModule call whose argument is not a string literal cannot be resolved here.
                // Treat the whole method as undecidable rather than reading only the literals and
                // reporting a clean result for a ladder that was partly invisible.
                if (anyCall.findAll(body).count() != literalCalls.findAll(body).count()) {
                    undecidable.add(site)
                    continue
                }

                // Each `if` guards the next forModule. An `if` with no forModule before the next
                // `if` was guarding something else, a throw or an early return, so it is dropped.
                val ladder = ArrayList<Pair<String?, String>>()
                var pending: String? = null
                for (t in token.findAll(body)) {
                    val cond = t.groupValues[1]
                    if (t.groupValues[2].isEmpty()) pending = cond
                    else { ladder.add(pending to t.groupValues[2]); pending = null }
                }
                callsParsed += ladder.size
                ladders.add(site to ladder)

                for ((mcVersion, jvm) in serverJvms) {
                    val ver = encode(mcVersion)
                    var chosen: String? = null
                    var stuck = false
                    for ((cond, module) in ladder) {
                        if (cond == null) { chosen = module; break }
                        val m = comparison.matchEntire(cond)
                        if (m == null) { stuck = true; break }
                        val rhs = m.groupValues[2].takeIf { it.isNotEmpty() }?.let { encode(it) }
                            ?: m.groupValues[3].toInt()
                        val hit = when (m.groupValues[1]) {
                            "<=" -> ver <= rhs
                            ">=" -> ver >= rhs
                            "==" -> ver == rhs
                            "<" -> ver < rhs
                            else -> ver > rhs
                        }
                        if (hit) { chosen = module; break }
                    }
                    if (stuck || chosen == null) { undecidable.add(site); continue }
                    decided++
                    val floor = moduleFloors[chosen] ?: throw GradleException(
                        "$site dispatches to module '$chosen', which has no entry in " +
                                "gradle/module-floors.properties, so its floor cannot be checked."
                    )
                    if (floor > jvm) {
                        violations.add("$site sends Minecraft $mcVersion (Java $jvm) to $chosen (Java $floor)")
                    }
                }
            }
        }

        // If the method or token regexes stop matching, ladders vanish and this check passes on an
        // empty set. Every forModule call in a file must land in exactly one parsed ladder.
        if (callsSeen != callsParsed) {
            throw GradleException(
                "found $callsSeen forModule(...) calls in the source but parsed only $callsParsed " +
                        "into ladders. The source no longer matches the shape this check reads, so " +
                        "whatever it missed is unchecked."
            )
        }
        if (sites < 10 || decided < 300) {
            throw GradleException(
                "only $sites ladder sites and $decided decisions, far below what this project has. " +
                        "A check evaluating almost nothing reports success no matter what is wrong."
            )
        }

        // Every capability a 26.x server reaches must have a twin in v_latest.
        //
        // This is the requirement the module convention quietly breaks. A class lives in the module
        // named for the FIRST version it works on, which is right for dispatch but means the
        // implementation a 26.x server actually runs is only ever compiled against an old dev
        // bundle. v_latest is the module that recompiles when highestPaperDep moves, so a capability
        // with nothing in v_latest is a capability no bump can check. Ten of them had drifted out
        // before this check existed, and nothing said so.
        // ItemText is pre-1.17 only BY DESIGN, not by drift. ItemTextProviderPre_1_17 throws above
        // 1.16.5 and /kc nmsproviders reports it as "n/a on this version", so a 26.x twin would be a
        // twin of something 26.x never runs. It appears here only because it is a parameter type on
        // adapters that DO serve 26.x. The assertion below keeps the exemption honest.
        // ItemNbt is exempt for the same reason: ItemNbtProvider throws above 1.18.1, so the 26.x
        // ladders that reach v1_17_R1 for commandMapModifier and messageManager never call itemNbt()
        // on it. This scan reads the whole adapter rather than the capabilities those ladders ask
        // for, which is why the class is visible here at all.
        val noLatestByDesign = setOf("ItemText", "ItemNbt")
        val latestDir = rootProject.file("versions/v_latest/src/main/java")
        val latestTwins = latestDir.walkTopDown().filter { it.name.endsWith("_LATEST.java") }
            .map { it.name.removeSuffix(".java").removeSuffix("_LATEST") }.toSet()
        val topVersion = encode(serverJvms.last().first)
        val missingTwins = ArrayList<String>()
        for ((site, ladder) in ladders) {
            var target: String? = null
            for ((cond, module) in ladder) {
                if (cond == null) { target = module; break }
                val m = comparison.matchEntire(cond) ?: break
                val rhs = m.groupValues[2].takeIf { it.isNotEmpty() }?.let { encode(it) }
                    ?: m.groupValues[3].toInt()
                val hit = when (m.groupValues[1]) {
                    "<=" -> topVersion <= rhs; ">=" -> topVersion >= rhs; "==" -> topVersion == rhs
                    "<" -> topVersion < rhs; else -> topVersion > rhs
                }
                if (hit) { target = module; break }
            }
            if (target == null || target == "v_latest") continue
            val impl = rootProject.file("versions/$target/src/main/java/com/kamikazejam/kamicommon/nms/bundle/$target/NmsBundleImpl.java")
            if (!impl.isFile) continue
            // The implementation classes that module's adapter names, e.g. ChatColor_1_16_R2.
            Regex("""\b([A-Z]\w*)_1_\d+_R?\d*\b""").findAll(stripComments(impl.readText()))
                .map { it.groupValues[1] }.toSet()
                .filterNot { it in latestTwins || it in noLatestByDesign }
                .forEach { missingTwins.add("$it (reached on 26.x through $target, from $site)") }
        }
        val wronglyExempt = noLatestByDesign.filter { it in latestTwins }
        if (wronglyExempt.isNotEmpty()) {
            throw GradleException(
                "these are exempt from needing a v_latest twin, but one now exists: " +
                        wronglyExempt.joinToString() + ". Remove them from noLatestByDesign so the " +
                        "twin is actually checked."
            )
        }
        if (missingTwins.isNotEmpty()) {
            throw GradleException(
                "these run on 26.x but have no _LATEST twin in versions/v_latest, so bumping " +
                        "highestPaperDep does not compile-check them against the Paper they run on:\n  " +
                        missingTwins.distinct().sorted().joinToString("\n  ") +
                        "\nCopy each into v_latest as a compile canary. Nothing dispatches to the copy."
            )
        }

        val unexpected = undecidable - handChecked
        if (unexpected.isNotEmpty()) {
            throw GradleException(
                "these ladders cannot be decided from the version integer, so nothing verifies which " +
                        "module they select:\n  " + unexpected.joinToString("\n  ") +
                        "\nRead each one, confirm the module it picks is loadable on that server's " +
                        "JVM, then add it to handChecked in gradle/verify-dispatch-floors.gradle.kts."
            )
        }
        val stale = handChecked - undecidable
        if (stale.isNotEmpty()) {
            throw GradleException(
                "these are listed as hand-checked but this check can now decide them, so the " +
                        "exemption is hiding a real result:\n  " + stale.joinToString("\n  ") +
                        "\nRemove them from handChecked."
            )
        }
        if (violations.isNotEmpty()) {
            throw GradleException(
                "a dispatch ladder selects a module built for a newer JVM than the server runs on. " +
                        "That server gets UnsupportedClassVersionError the moment it touches the " +
                        "provider:\n  " + violations.distinct().take(20).joinToString("\n  ") +
                        "\nFork the implementation into a module at the server's own floor and add a " +
                        "branch above the fallthrough."
            )
        }
        logger.lifecycle(
            "verifyDispatchFloors: $sites ladders, $decided decisions across ${serverJvms.size} " +
                    "server versions, no module selected above its server's JVM"
        )
    }
}
tasks.named("build") { dependsOn(verifyDispatchFloors) }
tasks.named("publish") { dependsOn(verifyDispatchFloors) }

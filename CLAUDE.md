# KamiCommonNMS

Version-abstracted NMS for Minecraft 1.8 through 26.x, published as `spigot-nms` and consumed by
KamiCommon. This file covers only what the code does not make obvious.

## Writing

Apply <https://github.com/cursor/plugins/blob/main/pstack/skills/unslop/SKILL.md> to commit messages,
PR and issue bodies, code comments and javadoc. **Zero em dashes anywhere.** Formal and impersonal, no
marketing adjectives, no narrating the investigation that produced a change.

Commit messages end with exactly this and nothing else:

```
Co-Authored-By: Claude Code <noreply@anthropic.com>
```

No model name, no context window, no session link. History was rewritten once to remove those; do not
reintroduce them.

## Building

**`sh gradlew`, never `./gradlew`.** `gradlew` is tracked mode 644, so the direct form fails.

`VERSION` lives at `build.gradle.kts:2`. Verify a release by downloading it and confirming the previous
version still returns 200 at its original size.

## The ladders decide everything

Each capability has one `Provider` subclass under `core/src/main/java/.../nms/provider/`, and that
ladder is **the only thing** that decides which module a server gets.

**Read the ladder. Never infer version coverage from the modules you happened to open.** Sampling a few
modules produced two confidently wrong answers in a single day: once claiming a capability was
unimplemented below 1.17 when the real gap was 1.13 to 1.16.5, and once calling a whole module dead.

## `v_latest` is a compile canary

It is the only module resolving `paperDevBundle(highestPaperDep)`, so it compiles against the newest
Paper while every other module pins a fixed old bundle. It exists to **fail the build** when Paper
changes something the modern tiers use.

Some ladders deliberately never reach it, because it targets a higher Java floor than the servers below
it can run. **That is the design, not dead code. Never delete or simplify anything under
`versions/v_latest`.**

A `_LATEST` class must stay a **true twin** of the tier it mirrors. `verifyLatestTwins` enforces the
public surface, including interfaces, because one twin implemented the wrong interface and would have
silently downgraded console logging the moment a ladder reached it. If you change a method on a
dispatchable tier, change its twin identically.

## Java floors

`gradle/module-floors.properties` sets each module's floor, and modules genuinely target different
JVMs. An `UnsupportedClassVersionError` naming a module is a floor mismatch, not a build break.

Some modules are reobfuscated through paperweight and some ship their plain jar. `core/build.gradle.kts`
lists both sets, and between them they must account for every entry in `settings.gradle.kts`.

## The relocated Adventure

Servers below 1.18.2 have no Adventure of their own, so a relocated copy ships as a **jar inside the
jar** at `internal-libs/adventure.jar`, loaded through a child classloader by `ShimLoader`.

Nested jar entries are not classpath entries, so no consumer can import it, including a consumer who
shades this library into their own uber jar. **That is the entire point.** Dependency scoping cannot
achieve it, and shading it flat would undo it.

## Verifying behaviour

The `gradle/verify-*.gradle.kts` family checks **packaging**: floors, shading, adapters, isolation.
None of it checks what a serializer emits. All of them passed while `click()` reached no client below
1.18.2 for nine releases.

Behaviour is checked by KamiCommon's `/kc texttest`, which runs from a console on any version. Changes
here that affect text, chat events or item hovers should be proven there before release.

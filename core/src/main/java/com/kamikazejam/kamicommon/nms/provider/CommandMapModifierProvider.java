package com.kamikazejam.kamicommon.nms.provider;

import com.kamikazejam.kamicommon.nms.abstraction.command.CommandMapModifier;
import com.kamikazejam.kamicommon.nms.command.CommandMapModifier_1_17_R1;
import com.kamikazejam.kamicommon.nms.command.CommandMapModifier_1_8_R1;
import org.jetbrains.annotations.NotNull;

/**
 * !!! Gradle Compatability Requires this module to be set to Java16 !!!
 * WE ARE BUILDING FOR Java 8, do not use any Java 9+ features
 */
public class CommandMapModifierProvider extends Provider<CommandMapModifier> {
    @Override
    protected @NotNull CommandMapModifier provide(int ver) {
        if (ver < f("1.8")) {
            throw new IllegalArgumentException("Version not supported (< 1.8): " + ver);
        }

        if (ver < f("1.17")) {
            return new CommandMapModifier_1_8_R1();
        }else {
            return new CommandMapModifier_1_17_R1();
        }
    }
}

package com.kamikazejam.kamicommon.nms.provider;

import com.kamikazejam.kamicommon.nms.wrappers.packet.*;
import org.jetbrains.annotations.NotNull;

/**
 * !!! Gradle Compatability Requires this module to be set to Java16 !!!
 * WE ARE BUILDING FOR Java 8, do not use any Java 9+ features
 */
public class PacketHandlerProvider extends Provider<NMSPacketHandler> {
    @Override
    protected @NotNull NMSPacketHandler provide(int ver) {
        if (ver < f("1.8")) {
            throw new IllegalArgumentException("Version not supported (< 1.8): " + ver);
        }

        if (ver == f("1.8")) {
            return new NMSPacketHandler_1_8_R1();
        }else if (ver <= f("1.8.3")) {
            return new NMSPacketHandler_1_8_R2();
        }else if (ver <= f("1.8.8")) {
            return new NMSPacketHandler_1_8_R3();
        }else if (ver <= f("1.9.2")) {
            return new NMSPacketHandler_1_9_R1();
        }else if (ver <= f("1.9.4")) {
            return new NMSPacketHandler_1_9_R2();
        }else if (ver <= f("1.10.2")) {
            return new NMSPacketHandler_1_10_R1();
        }else if (ver <= f("1.11.2")) {
            return new NMSPacketHandler_1_11_R1();
        }else if (ver <= f("1.12.2")) {
            return new NMSPacketHandler_1_12_R1();
        }else if (ver <= f("1.13")) {
            return new NMSPacketHandler_1_13_R1();
        }else if (ver <= f("1.13.2")) {
            return new NMSPacketHandler_1_13_R2();
        }else if (ver <= f("1.14.4")) {
            return new NMSPacketHandler_1_14_R1();
        }else if (ver <= f("1.15.2")) {
            return new NMSPacketHandler_1_15_R1();
        }else if (ver <= f("1.16.1")) {
            return new NMSPacketHandler_1_16_R1();
        }else if (ver <= f("1.16.3")) {
            return new NMSPacketHandler_1_16_R2();
        }else if (ver <= f("1.16.5")) {
            return new NMSPacketHandler_1_16_R3();
        }else if (ver <= f("1.17.1")) {
            return new NMSPacketHandler_1_17_R1();
        }else if (ver <= f("1.18.1")) {
            return new NMSPacketHandler_1_18_R1();
        }else if (ver <= f("1.18.2")) {
            return new NMSPacketHandler_1_18_R2();
        }else if (ver <= f("1.19.2")) {
            return new NMSPacketHandler_1_19_R1();
        }else if (ver <= f("1.19.3")) {
            return new NMSPacketHandler_1_19_R2();
        }else if (ver <= f("1.19.4")) {
            return new NMSPacketHandler_1_19_R3();
        }else if (ver <= f("1.20.1")) {
            return new NMSPacketHandler_1_20_R1();
        }else if (ver <= f("1.20.2")) {
            return new NMSPacketHandler_1_20_R2();
        }else if (ver <= f("1.20.4")) {
            return new NMSPacketHandler_1_20_R3();
        }
        // With the mojang-mapped paper nms now, we might be good to use this version indefinitely
        return new NMSPacketHandler_1_21_R1(); // Confirmed for 1.20.5, 1.20.6, 1.21
    }
}

package com.kamikazejam.kamicommon.nms.provider;

import com.kamikazejam.kamicommon.nms.wrappers.packet.*;
import org.jetbrains.annotations.NotNull;

/**
 * Provider for version-specific packet handler implementations.
 * <p>
 * This provider selects the appropriate {@link NMSPacketHandler} implementation
 * based on the current Minecraft version, handling the evolution of packet systems
 * and network protocols across different Minecraft releases. Packet handlers provide
 * advanced packet manipulation capabilities for custom client-server communication.
 * </p>
 * <p>
 * The provider covers major changes in packet handling systems:
 * <ul>
 * <li><strong>1.8-1.12:</strong> Legacy packet systems with stable NMS structure</li>
 * <li><strong>1.13+:</strong> Updated packet protocols with protocol version changes</li>
 * <li><strong>1.17+:</strong> Significant NMS restructuring affecting packet access</li>
 * <li><strong>1.20.5+:</strong> Mojang-mapped Paper NMS for enhanced compatibility</li>
 * </ul>
 * </p>
 * <p>
 * Each implementation provides methods for packet creation, wrapping, and transmission
 * that are essential for plugins requiring custom network communication, entity
 * manipulation, or client-side effects that go beyond standard Bukkit capabilities.
 * </p>
 *
 * @see NMSPacketHandler
 */
public class PacketHandlerProvider extends Provider<NMSPacketHandler> {
    /**
     * {@inheritDoc}
     * <p>
     * Selects the appropriate packet handler implementation based on Minecraft version,
     * accounting for protocol changes and NMS restructuring. The selection ensures
     * compatibility with the packet systems and network protocols of each version.
     * </p>
     *
     * @param ver the formatted NMS version integer
     * @return the version-appropriate {@link NMSPacketHandler} implementation
     * @throws IllegalArgumentException if the version is below 1.8 (unsupported)
     */
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
        return new NMSPacketHandler_1_21_CB(); // Confirmed for 1.20.5, 1.20.6, 1.21
    }
}

package com.kamikazejam.kamicommon.nms.provider;

import com.kamikazejam.kamicommon.nms.NmsAPI;
import com.kamikazejam.kamicommon.nms.abstraction.chat.AbstractMessageManager;
import com.kamikazejam.kamicommon.nms.abstraction.itemtext.AbstractItemTextPre_1_17;
import com.kamikazejam.kamicommon.nms.chat.MessageManager_1_17_R1;
import com.kamikazejam.kamicommon.nms.chat.MessageManager_1_8_R1;
import org.jetbrains.annotations.NotNull;

/**
 * Provider for version-specific message manager implementations.
 * <p>
 * This provider selects the appropriate {@link AbstractMessageManager} implementation
 * based on the current Minecraft version, handling the major transition from
 * Bungee/MD5 text components to Kyori Adventure components in Minecraft 1.17.
 * Message managers handle interactive message processing with hover events and click actions.
 * </p>
 * <p>
 * The provider addresses the fundamental text component system change:
 * <ul>
 * <li><strong>Pre-1.17:</strong> Bungee/MD5 BaseComponent system with legacy item text integration</li>
 * <li><strong>1.17+:</strong> Kyori Adventure Component system with modern text handling</li>
 * </ul>
 * </p>
 * <p>
 * This enables consistent interactive message functionality across all versions
 * while utilizing the most appropriate text component system for each Minecraft
 * version. The provider integrates with item text systems for hover event support.
 * </p>
 *
 * @see AbstractMessageManager
 */
public class MessageManagerProvider extends Provider<AbstractMessageManager> {
    /**
     * {@inheritDoc}
     * <p>
     * Selects the message manager implementation based on the text component system
     * transition in Minecraft 1.17. Pre-1.17 versions integrate with legacy item
     * text providers, while 1.17+ uses modern Adventure components.
     * </p>
     *
     * @param ver the formatted NMS version integer
     * @return the version-appropriate {@link AbstractMessageManager} implementation
     * @throws IllegalArgumentException if the version is below 1.8 (unsupported)
     */
    @Override
    protected @NotNull AbstractMessageManager provide(int ver) {
        if (ver < f("1.8")) {
            throw new IllegalArgumentException("Version not supported (< 1.8): " + ver);
        }

        // Use md5 BaseComponent pre 1.17
        if (ver < f("1.17")) {
            AbstractItemTextPre_1_17 itemText = NmsAPI.getItemTextProviderPre_1_17().get();
            return new MessageManager_1_8_R1(itemText);
        }

        // Use kyori adventure Component post 1.17
        return new MessageManager_1_17_R1();
    }
}

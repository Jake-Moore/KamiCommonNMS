package com.kamikazejam.kamicommon.nms.chatcolor;

import com.kamikazejam.kamicommon.nms.abstraction.IChatColorNMS;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Working for: 1.16.2+, 1.21.8, 1.21.9
 */
@SuppressWarnings("deprecation")
public class ChatColor_LATEST implements IChatColorNMS {
    @Override
    public @NotNull Color getColor(ChatColor chatColor) {
        return chatColor.asBungee().getColor();
    }
}

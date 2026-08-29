package com.kamikazejam.kamicommon.nms.chatcolor;

import com.kamikazejam.kamicommon.nms.abstraction.IChatColorNMS;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * The 26.x twin of {@code ChatColor_1_16_R2} in {@code versions/v1_16_R2}, and the only reason it exists is to be
 * compiled.
 * <p>
 * Nothing dispatches here. The ladder sends every server, 26.x included, to the v1_16_R2 copy, because
 * under this project's convention a class lives in the module named for the FIRST version it works
 * on. That is correct for dispatch and bad for early warning: it means the implementation is only
 * ever compiled against an old dev bundle, so an API this code uses could be removed in 26.x and the
 * build would not notice until a server did.
 * </p><p>
 * This copy closes that. It compiles against {@code highestPaperDep}, so bumping that version
 * compile-checks this capability against bleeding-edge Paper. If it stops compiling, that IS the
 * finding: fix it here and in the twin, and add a ladder branch if the two must now differ.
 * </p>
 */
@SuppressWarnings("deprecation")
public class ChatColor_LATEST implements IChatColorNMS {
    @Override
    public @NotNull Color getColor(ChatColor chatColor) {
        return chatColor.asBungee().getColor();
    }
}

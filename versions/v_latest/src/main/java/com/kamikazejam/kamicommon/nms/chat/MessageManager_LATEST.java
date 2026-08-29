package com.kamikazejam.kamicommon.nms.chat;

import com.kamikazejam.kamicommon.actions.*;
import com.kamikazejam.kamicommon.nms.abstraction.chat.AbstractMessageManager;
import com.kamikazejam.kamicommon.nms.abstraction.chat.KMessage;
import com.kamikazejam.kamicommon.util.LegacyColors;
import com.kamikazejam.kamicommon.util.chat.MessagePart;
import com.kamikazejam.kamicommon.util.chat.MessageParter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The 26.x twin of {@code MessageManager_1_17_R1} in {@code versions/v1_17_R1}, and the only reason it exists is to be
 * compiled.
 * <p>
 * Nothing dispatches here. The ladder sends every server, 26.x included, to the v1_17_R1 copy, because
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
public class MessageManager_LATEST extends AbstractMessageManager {

    @Override
    protected void processAndSendInternal(@NotNull CommandSender sender, @NotNull KMessage kMessage) {
        for (String line : kMessage.getLines()) {
            this.processAndSend(sender, line, kMessage.isTranslate(), kMessage.getActions());
        }
    }

    private void processAndSend(@NotNull CommandSender sender, @NotNull String s, boolean translate, @NotNull List<Action> actions) {
        if (translate) { s = LegacyColors.t(s); }

        LegacyComponentSerializer serializer = LegacyComponentSerializer.legacySection();

        List<MessagePart> messageParts = MessageParter.getMessageParts(s, actions.toArray(new Action[0]));
        TextComponent component = Component.empty();
        for (MessagePart messagePart : messageParts) {
            TextComponent part = serializer.deserialize(messagePart.getText());

            @Nullable Click click = messagePart.getClick();
            if (click instanceof ClickCmd) {
                part = part.clickEvent(ClickEvent.runCommand(((ClickCmd) click).getCommand()));
            }else if (click instanceof ClickSuggest) {
                part = part.clickEvent(ClickEvent.suggestCommand(((ClickSuggest) click).getSuggestion()));
            }else if (click instanceof ClickUrl) {
                part = part.clickEvent(ClickEvent.openUrl(((ClickUrl) click).getUrl()));
            }

            @Nullable Hover hover = messagePart.getHover();
            if (hover instanceof HoverText) {
                part = part.hoverEvent(HoverEvent.showText(serializer.deserialize(((HoverText) hover).getText())));
            }else if (hover instanceof HoverItem) {
                ItemStack item = ((HoverItem) hover).getItemStack();
                part = part.hoverEvent(item.asHoverEvent());
            }
            component = component.append(part);
        }

        sender.sendMessage(component);
    }
}

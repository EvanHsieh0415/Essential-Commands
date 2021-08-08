package com.fibermc.essentialcommands.commands;

import com.fibermc.essentialcommands.ECText;
import com.fibermc.essentialcommands.PlayerData;
import com.fibermc.essentialcommands.PlayerDataManager;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;

import java.util.List;

import static com.fibermc.essentialcommands.EssentialCommands.CONFIG;

public class RealNameCommand implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        String nicknameStr = StringArgumentType.getString(context, "player_nickname");
        List<PlayerData> nicknamePlayers = PlayerDataManager.getInstance().getPlayerDataMatchingNickname(nicknameStr);
        MutableText responseText = new LiteralText("");

        // If no players matched the provided nickname
        if (nicknamePlayers.size() == 0) {
            responseText
                .append(ECText.getInstance().getText("cmd.realname.feedback.none_match").setStyle(CONFIG.FORMATTING_DEFAULT.getValue()))
                .append(new LiteralText(nicknameStr).setStyle(CONFIG.FORMATTING_ACCENT.getValue()))
                .append(ECText.getInstance().getText("generic.quote_fullstop").setStyle(CONFIG.FORMATTING_DEFAULT.getValue()));

        } else {
            responseText
                .append(ECText.getInstance().getText("cmd.realname.feedback.matching.1").setStyle(CONFIG.FORMATTING_DEFAULT.getValue()))
                .append(new LiteralText(nicknameStr).setStyle(CONFIG.FORMATTING_ACCENT.getValue()))
                .append(ECText.getInstance().getText("cmd.realname.feedback.matching.2").setStyle(CONFIG.FORMATTING_DEFAULT.getValue()));

            for (PlayerData nicknamePlayer : nicknamePlayers) {
                responseText.append("\n  ");
                responseText.append(nicknamePlayer.getPlayer().getGameProfile().getName());
            }

        }
        context.getSource().sendFeedback(
            responseText, CONFIG.BROADCAST_TO_OPS.getValue()
        );

        return 0;
    }
}

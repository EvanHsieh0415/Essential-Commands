package com.fibermc.essentialcommands.commands;

import com.fibermc.essentialcommands.ECPerms;
import com.fibermc.essentialcommands.ECText;
import com.fibermc.essentialcommands.access.ServerPlayerEntityAccess;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.jpcode.eccore.util.TextUtil;
import eu.pb4.placeholders.PlaceholderAPI;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

import static com.fibermc.essentialcommands.EssentialCommands.CONFIG;

public class NicknameSetCommand implements Command<ServerCommandSource>  {
    public NicknameSetCommand() {}

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        var nicknameText = TextArgumentType.getTextArgument(context, "nickname");
        var nicknameWithContext = ECPerms.check(context.getSource(), ECPerms.Registry.nickname_selector_and_ctx)
            ? Texts.parse(
                context.getSource(),
                nicknameText,
                context.getSource().getPlayer(),
                0)
            : nicknameText;
        //Get specified new nickname
        return exec(context, nicknameWithContext);
    }

    public static int runStringToText(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        NicknameSetCommand.exec(context, TextUtil.parseText(StringArgumentType.getString(context, "nickname_placeholder_api")));
        return 1;
    }

    public static int exec(CommandContext<ServerCommandSource> context, Text rawNicknameText) throws CommandSyntaxException {
        ServerPlayerEntity targetPlayer = CommandUtil.getCommandTargetPlayer(context);
        var source = context.getSource();
        var nicknameText = ECPerms.check(context.getSource(), ECPerms.Registry.nickname_placeholders)
            ? PlaceholderAPI.parseText(rawNicknameText, targetPlayer)
            : rawNicknameText;

        ServerPlayerEntityAccess targetPlayerEntityAccess = (ServerPlayerEntityAccess) targetPlayer;
        int successCode = targetPlayerEntityAccess.getEcPlayerData().setNickname(nicknameText);

        //inform command sender that the nickname has been set
        if (successCode >= 0) {
            source.sendFeedback(TextUtil.concat(
                ECText.getInstance().getText("cmd.nickname.set.feedback").setStyle(CONFIG.FORMATTING_DEFAULT.getValue()),
                (nicknameText != null) ? nicknameText : new LiteralText(targetPlayer.getGameProfile().getName()),
                ECText.getInstance().getText("generic.quote_fullstop").setStyle(CONFIG.FORMATTING_DEFAULT.getValue())
            ), CONFIG.BROADCAST_TO_OPS.getValue());
        } else {
            MutableText failReason = switch (successCode) {
                case -1 -> ECText.getInstance().getText("cmd.nickname.set.error.perms");
                case -2 -> ECText.getInstance().getText(
                    "cmd.nickname.set.error.length",
                    nicknameText.getString().length(),
                    CONFIG.NICKNAME_MAX_LENGTH.getValue()
                );
                default -> ECText.getInstance().getText("generic.error.unknown");
            };
            source.sendError(TextUtil.concat(
                ECText.getInstance().getText("cmd.nickname.set.error.1").setStyle(CONFIG.FORMATTING_ERROR.getValue()),
                nicknameText,
                ECText.getInstance().getText("cmd.nickname.set.error.2").setStyle(CONFIG.FORMATTING_ERROR.getValue()),
                failReason.setStyle(CONFIG.FORMATTING_ERROR.getValue())
            ));
        }

        return successCode;
    }

}

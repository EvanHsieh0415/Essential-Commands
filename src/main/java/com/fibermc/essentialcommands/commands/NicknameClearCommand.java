package com.fibermc.essentialcommands.commands;

import com.fibermc.essentialcommands.ECText;
import com.fibermc.essentialcommands.config.Config;
import com.fibermc.essentialcommands.access.ServerPlayerEntityAccess;
import com.fibermc.essentialcommands.util.TextUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;


public class NicknameClearCommand implements Command<ServerCommandSource>  {
    public NicknameClearCommand() {}

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        //Store command sender
        ServerPlayerEntity senderPlayerEntity = context.getSource().getPlayer();
        ServerPlayerEntity targetPlayer;
        try {
            targetPlayer = EntityArgumentType.getPlayer(context, "target");
        } catch (IllegalArgumentException e) {
            targetPlayer = senderPlayerEntity;
        }

        ServerPlayerEntityAccess targetPlayerEntityAccess = (ServerPlayerEntityAccess) targetPlayer;
        targetPlayerEntityAccess.getEcPlayerData().setNickname(null);

        //inform command sender that the nickname has been set
        context.getSource().sendFeedback(TextUtil.concat(
            ECText.getInstance().getText("cmd.nickname.set.feedback").setStyle(Config.FORMATTING_DEFAULT),
            new LiteralText(senderPlayerEntity.getGameProfile().getName()),
            ECText.getInstance().getText("generic.quote_fullstop").setStyle(Config.FORMATTING_DEFAULT)
        ), Config.BROADCAST_TO_OPS);

        return 1;
    }

}

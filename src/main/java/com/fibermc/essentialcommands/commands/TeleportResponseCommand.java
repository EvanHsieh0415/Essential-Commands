package com.fibermc.essentialcommands.commands;

import com.fibermc.essentialcommands.ECText;
import com.fibermc.essentialcommands.PlayerData;
import com.fibermc.essentialcommands.TeleportRequest;
import com.fibermc.essentialcommands.access.ServerPlayerEntityAccess;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.LinkedHashMap;
import java.util.UUID;

public abstract class TeleportResponseCommand implements Command<ServerCommandSource> {

    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        return exec(
                context,
                context.getSource().getPlayer(),
                EntityArgumentType.getPlayer(context, "target")
        );
    }

    public int runDefault(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity senderPlayer = context.getSource().getPlayer();
        PlayerData senderPlayerData = ((ServerPlayerEntityAccess) senderPlayer).getEcPlayerData();
        LinkedHashMap<UUID, TeleportRequest> incomingTeleportRequests = senderPlayerData.getIncomingTeleportRequests();
        ServerPlayerEntity targetPlayer;

        if (incomingTeleportRequests.size() > 1) {
            throw CommandUtil.createSimpleException(ECText.getInstance().getText("cmd.tpa_reply.error.shortcut_more_than_one"));
        } else if (incomingTeleportRequests.size() < 1) {
            throw CommandUtil.createSimpleException(ECText.getInstance().getText("cmd.tpa_reply.error.shortcut_none_exist"));
        } else {
            targetPlayer = incomingTeleportRequests.values().stream().findFirst().get().getTargetPlayer();
            if (targetPlayer == null) {
                throw CommandUtil.createSimpleException(ECText.getInstance().getText("cmd.tpa_reply.error.no_request_from_target"));
            }
        }

        return exec(
                context,
                senderPlayer,
                targetPlayer
        );
    }

    abstract int exec(CommandContext<ServerCommandSource> context, ServerPlayerEntity senderPlayer, ServerPlayerEntity targetPlayer);

}

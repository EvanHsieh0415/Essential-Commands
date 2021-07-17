package com.fibermc.essentialcommands.commands;

import com.fibermc.essentialcommands.*;
import com.fibermc.essentialcommands.config.Config;
import com.fibermc.essentialcommands.types.MinecraftLocation;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;


public class SpawnSetCommand implements Command<ServerCommandSource> {

    public SpawnSetCommand() {}

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        WorldDataManager worldDataManager = ManagerLocator.INSTANCE.getWorldDataManager();

        ServerCommandSource source = context.getSource();
        //Store command sender
        ServerPlayerEntity senderPlayer = source.getPlayer();

        int successCode = 1;

        //Set spawn
        MinecraftLocation loc = new MinecraftLocation(senderPlayer);
        worldDataManager.setSpawn(loc);

        //inform command sender that the home has been set
        source.sendFeedback(
            ECText.getInstance().getText("cmd.spawn.set.feedback").setStyle(Config.FORMATTING_DEFAULT)
                .append(loc.toLiteralTextSimple().setStyle(Config.FORMATTING_ACCENT))
            , Config.BROADCAST_TO_OPS);

        return successCode;
    }
}

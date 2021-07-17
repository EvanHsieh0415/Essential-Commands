package com.fibermc.essentialcommands.commands;

import com.fibermc.essentialcommands.ECText;
import com.fibermc.essentialcommands.PlayerData;
import com.fibermc.essentialcommands.access.ServerPlayerEntityAccess;
import com.fibermc.essentialcommands.config.Config;
import com.fibermc.essentialcommands.types.MinecraftLocation;
import com.fibermc.essentialcommands.util.TextUtil;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;


public class HomeSetCommand implements Command<ServerCommandSource> {

    public HomeSetCommand() {}

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        //Store command sender
        ServerPlayerEntity senderPlayer = source.getPlayer();
        //Store home name
        String homeName = StringArgumentType.getString(context, "home_name");

        //Add home to PlayerData
        //TODO if home with given name is already set, warn of overwrite and require that the command be typed again, or a confirmation message be given
        PlayerData pData = ((ServerPlayerEntityAccess)senderPlayer).getEcPlayerData();
        int successCode = 0;
        try {
             successCode = pData.addHome(homeName, new MinecraftLocation(senderPlayer));
        } catch (CommandSyntaxException e) {
            source.sendError(TextUtil.concat(
                ECText.getInstance().getText("cmd.home.feedback.1").setStyle(Config.FORMATTING_ERROR),
                new LiteralText(homeName).setStyle(Config.FORMATTING_ACCENT),
                ECText.getInstance().getText("cmd.home.set.error.exists.2").setStyle(Config.FORMATTING_ERROR)
            ));
        }

        pData.markDirty();
        pData.save();
        //inform command sender that the home has been set
        if (successCode == 1) {
            source.sendFeedback(
                ECText.getInstance().getText("cmd.home.feedback.1").setStyle(Config.FORMATTING_DEFAULT)
                    .append(new LiteralText(homeName).setStyle(Config.FORMATTING_ACCENT))
                    .append(ECText.getInstance().getText("cmd.home.set.feedback.2").setStyle(Config.FORMATTING_DEFAULT)),
                Config.BROADCAST_TO_OPS
            );
        } else if (successCode == -1) {
            source.sendError(TextUtil.concat(
                ECText.getInstance().getText("cmd.home.feedback.1").setStyle(Config.FORMATTING_ERROR),
                new LiteralText(homeName).setStyle(Config.FORMATTING_ACCENT),
                ECText.getInstance().getText("cmd.home.set.error.limit.2").setStyle(Config.FORMATTING_ERROR),
                new LiteralText(String.valueOf(Config.HOME_LIMIT)).setStyle(Config.FORMATTING_ACCENT),
                ECText.getInstance().getText("cmd.home.set.error.limit.3").setStyle(Config.FORMATTING_ERROR)
            ));
        }


        return successCode;
    }
}

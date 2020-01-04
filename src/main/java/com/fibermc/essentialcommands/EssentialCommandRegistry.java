package com.fibermc.essentialcommands;

import static net.minecraft.server.command.CommandManager.argument; // argument("bar", word())

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

/**
 * BasicCommands
 */
public class EssentialCommandRegistry {

    PlayerDataManager dataManager;
    TeleportRequestManager tpManager;

    public EssentialCommandRegistry() {
        dataManager = new PlayerDataManager();
        tpManager = new TeleportRequestManager(dataManager);
    }

    public void register() {
        CommandRegistry.INSTANCE.register(false, dispatcher -> {
            //Make some new nodes
            LiteralCommandNode<ServerCommandSource> tpaskNode = CommandManager
                    .literal("tpa")
                    .then(argument("target", EntityArgumentType.player())
                            .executes(new TeleportAskCommand(tpManager)))
                    .build();

            LiteralCommandNode<ServerCommandSource> tpacceptNode = CommandManager
                    .literal("tpaccept")
                    .then(argument("target", EntityArgumentType.player())
                            .suggests(TeleportResponseSuggestion.suggestedStrings(dataManager))
                            .executes(new TeleportAcceptCommand(dataManager)))
                    .build();

            LiteralCommandNode<ServerCommandSource> tpdenyNode = CommandManager
                    .literal("tpdeny")
                    .then(argument("target", EntityArgumentType.player())
                            .suggests(TeleportResponseSuggestion.suggestedStrings(dataManager))
                            .executes(new TeleportDenyCommand(dataManager)))
                    .build();

            LiteralCommandNode<ServerCommandSource> homeNode = CommandManager
                    .literal("home")
                    .build();
            LiteralCommandNode<ServerCommandSource> homeSetNode = CommandManager
                    .literal("set")
                    .then(argument("home_name", StringArgumentType.word())
                            .executes(new HomeSetCommand(dataManager)))
                    .build();

            LiteralCommandNode<ServerCommandSource> homeTpNode = CommandManager
                    .literal("tp")
                    .then(argument("home_name", StringArgumentType.word())
                            .suggests(HomeSuggestion.suggestedStrings(dataManager))
                            .executes(new HomeCommand(dataManager)))
                    .build();

            LiteralCommandNode<ServerCommandSource> homeDeleteNode = CommandManager
                    .literal("delete")
                    .then(argument("home_name", StringArgumentType.word())
                            .suggests(HomeSuggestion.suggestedStrings(dataManager))
                            .executes(new HomeDeleteCommand(dataManager)))
                    .build();

            // LiteralCommandNode<ServerCommandSource> backNode = CommandManager
            //     .literal("home")
            //     .executes(new BackCommand())//todo: make a universal teleportaiton method/class that will 
            //     //todo: allow performing opertaions universally any time a player is teleported (ex: saving last location)
            //     .build();

            dispatcher.getRoot().addChild(tpaskNode);
            dispatcher.getRoot().addChild(tpacceptNode);
            dispatcher.getRoot().addChild(tpdenyNode);


            dispatcher.getRoot().addChild(homeNode);
            homeNode.addChild(homeTpNode);
            homeNode.addChild(homeSetNode);
            homeNode.addChild(homeDeleteNode);

        });
    }
}
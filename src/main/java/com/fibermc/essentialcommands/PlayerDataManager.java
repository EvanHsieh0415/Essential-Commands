package com.fibermc.essentialcommands;

import com.fibermc.essentialcommands.access.PlayerEntityAccess;
import com.fibermc.essentialcommands.events.PlayerConnectCallback;
import com.fibermc.essentialcommands.events.PlayerDeathCallback;
import com.fibermc.essentialcommands.events.PlayerLeaveCallback;
import com.fibermc.essentialcommands.events.PlayerRespawnCallback;
import com.fibermc.essentialcommands.types.MinecraftLocation;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.WorldSavePath;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataManager {

    private final ConcurrentHashMap<UUID, PlayerData> dataMap;

    private static PlayerDataManager INSTANCE;

    public PlayerDataManager() {
        INSTANCE = this;
        this.dataMap = new ConcurrentHashMap<>();
    }

    public static void init() {
        PlayerConnectCallback.EVENT.register(PlayerDataManager::onPlayerConnect);
        PlayerLeaveCallback.EVENT.register(PlayerDataManager::onPlayerLeave);
        PlayerDeathCallback.EVENT.register(PlayerDataManager::onPlayerDeath);
        PlayerRespawnCallback.EVENT.register(PlayerDataManager::onPlayerRespawn);
    }

    public static PlayerDataManager getInstance() {
        return INSTANCE;
    }

    // EVENTS
    public static void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player) {
        try {
            ((PlayerEntityAccess) player).setEcPlayerData(INSTANCE.loadPlayerData(player));
        } catch (IOException e) {
            EssentialCommands.log(Level.WARN, "Failed to load essential_commands player data for {"+player.getName().getString()+"}");
        }
    }

    public static void onPlayerLeave(ServerPlayerEntity player) {
        try {
            INSTANCE.savePlayerData(player);
        } catch (Exception e) {
            //TODO: handle exception
        }

        INSTANCE.unloadPlayerData(player);
    }

    private static void onPlayerRespawn(ServerPlayerEntity serverPlayerEntity) {
        PlayerData pData = INSTANCE.getOrCreate(serverPlayerEntity);
        pData.updatePlayer(serverPlayerEntity);
        ((PlayerEntityAccess) serverPlayerEntity).setEcPlayerData(pData);
    }

    private static void onPlayerDeath(ServerPlayerEntity playerEntity, DamageSource damageSource) {
        PlayerData pData = INSTANCE.getPlayerFromUUID(playerEntity.getGameProfile().getId());
        //EssentialCommands.log(Level.DEBUG, "Worked2 " + pData.getPlayer().getGameProfile().getName());
        if (Config.ALLOW_BACK_ON_DEATH)
            pData.setPreviousLocation(new MinecraftLocation(pData.getPlayer()));
    }

    // SET / ADD
    public PlayerData addPlayerData(ServerPlayerEntity player) {
        PlayerData playerData = PlayerDataFactory.create(player);
        dataMap.put(player.getUuid(), PlayerDataFactory.create(player));
        return playerData;
    }
    public void addPlayerData(PlayerData pData) {

        dataMap.put(pData.getPlayer().getUuid(), pData);
    }

    public PlayerData getOrCreate(ServerPlayerEntity player) {
        //TODO perhaps this should just be "get" and we let it error if null.
        // @body In theory, this should never be null if everything else works correctly.
        UUID uuid = player.getUuid();
        PlayerData playerData = dataMap.get(uuid);

        if (playerData == null) {
            playerData = addPlayerData(player);
        }
        return playerData;
    }
    PlayerData getPlayerFromUUID(UUID playerID) {
        return dataMap.get(playerID);
    }

    // SAVE / LOAD
    private void unloadPlayerData(ServerPlayerEntity player) {
        this.dataMap.remove(player.getUuid());
    }

    private File getPlayerDataFile(ServerPlayerEntity player) {
        String pUuid = player.getUuidAsString();

        //Path mainDirectory = player.getServer().getRunDirectory().toPath();
        Path dataDirectoryPath;
        File playerDataFile = null;
        try {
            try {
                dataDirectoryPath = Files.createDirectories(player.getServer().getSavePath(WorldSavePath.ROOT).resolve("modplayerdata"));
            } catch (NullPointerException e){
                dataDirectoryPath = Files.createDirectories(Paths.get("./world/modplayerdata/"));
                EssentialCommands.log(Level.WARN, "Session save path could not be found. Defaulting to ./world/modplayerdata");
            }
            playerDataFile = dataDirectoryPath.resolve(pUuid+".dat").toFile();
            if (playerDataFile.createNewFile() || playerDataFile.length()==0) {//creates file and returns true only if file did not exist, otherwise returns false
                //Initialize file if just created
                initPlayerDataFile(player, playerDataFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return playerDataFile;
    }

    private void initPlayerDataFile(ServerPlayerEntity player, File playerDataFile) {
        PlayerData pData = this.getOrCreate(player);
        pData.markDirty();
        pData.save(playerDataFile);
    }

    PlayerData loadPlayerData(ServerPlayerEntity player) throws IOException {
        String pUuid = player.getUuidAsString();

        File playerDataFile = getPlayerDataFile(player);

        PlayerData pData = PlayerDataFactory.create(player);

        NbtCompound NbtCompound3 = NbtIo.readCompressed(new FileInputStream(playerDataFile));

        //EssentialCommands.log(Level.INFO, "TagData:\n-=-=-=-=-=-\n"+NbtCompound3.asString()+"\n-=-=-=-=-=-=-=-");
        pData.fromNbt(NbtCompound3);
        //Testing:
        pData.markDirty();
        addPlayerData(pData);
        return pData;
    }

    public void savePlayerData(ServerPlayerEntity player) {
        this.getOrCreate(player).save(this.getPlayerDataFile(player));
    }



    //-=-=-=-=-=-=-=-=-=-=-=-



    
}
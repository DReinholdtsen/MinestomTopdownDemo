package org.example;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

import java.util.HashMap;
import java.util.Random;

public class PlayerData {
    //this class is unnecessary for just this demonstration, but its stuff im working on
    public Entity cursor;
    public Pos targetPos = new Pos(0, 0, 0);
    public Instance playerInstance;
    public static Random random = new Random();
    public static HashMap<Player, PlayerData> playerDataMap = new HashMap<>();
    public static PlayerData registerPlayer(Player player) {
        return new PlayerData(player);
    }
    public static PlayerData getPlayerData(Player player) {
        return playerDataMap.get(player);
    }
    public static boolean isRegistered(Player player) {
        return playerDataMap.containsKey(player);
    }
    private PlayerData(Player player) {
        playerDataMap.put(player, this);
        cursor = new Entity(EntityType.SNOWBALL);
        cursor.setNoGravity(true);
        playerInstance = player.getInstance();
        cursor.setInstance(playerInstance, new Pos(0, 1, 0));
        cursor.setView(90,  0);
        createNewTarget();
        System.out.println(cursor);
    }
    public void createNewTarget() {
        playerInstance.setBlock(targetPos, Block.BLACK_CONCRETE);
        Pos newPos = new Pos(random.nextInt(-5, 5), 0, random.nextInt(-5, 5));
        playerInstance.setBlock(newPos, Block.TARGET);
        targetPos = newPos;
    }
}

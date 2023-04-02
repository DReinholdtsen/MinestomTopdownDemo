package org.example;




import net.minestom.server.MinecraftServer;
import net.minestom.server.ServerProcess;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.*;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.client.ClientPacket;
import net.minestom.server.network.packet.client.play.ClientPlayerRotationPacket;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.TaskSchedule;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        MinecraftServer minecraftServer = MinecraftServer.init();
        MojangAuth.init();
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();
        ServerProcess process = MinecraftServer.process();
        SchedulerManager scheduler = MinecraftServer.getSchedulerManager();

        // Set the ChunkGenerator
        instanceContainer.setGenerator(unit ->
                unit.modifier().fillHeight(0, 1, Block.BLACK_CONCRETE));

        globalEventHandler.addListener(PlayerLoginEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(new Pos(0, 0, 0));
            PlayerInventory inventory = player.getInventory();
            /*
            inventory.addItemStack(ItemStack.of(Material.DIRT, 64));
            inventory.addItemStack(ItemStack.of(Material.STONE, 64));
            inventory.addItemStack(ItemStack.of(Material.DIAMOND, 64));
            inventory.addItemStack(ItemStack.of(Material.IRON_INGOT, 64));
            item testing stuff
             */
            Entity camera = new Entity(EntityType.BAT); //can probably be any entity without a custom shader (i.e. creeper, endermen, spiders, etc.)
            camera.addEffect(new Potion(PotionEffect.INVISIBILITY, (byte) 1, 100));
            camera.setNoGravity(true);
            camera.setInstance(instanceContainer, new Pos(0, 10, 0));
            camera.setView(0, 90);
            scheduler.buildTask(() -> {
                player.setNoGravity(true);
                player.spectate(camera);
                camera.addPassenger(player);
                PlayerData.registerPlayer(player);
            }).delay(TaskSchedule.tick(20)).schedule(); //need to have a delay since PlayerLoginEvent is called before client actually loads, there might be an event that will work better
            //i haven't tested the minimum delay required, its probably lower than 20
        });
        globalEventHandler.addListener(PlayerChatEvent.class, event -> {
            final Player player = event.getPlayer();
            //why is this here
        });
        globalEventHandler.addListener(PlayerPacketEvent.class, event -> {
            ClientPacket packet = event.getPacket();
            Player player = event.getPlayer();
            if (packet instanceof ClientPlayerRotationPacket && PlayerData.isRegistered(player)) {
                ClientPlayerRotationPacket rotationPacket = (ClientPlayerRotationPacket) packet;
                PlayerData playerData = PlayerData.getPlayerData(player);
                Entity cursor = playerData.cursor;
                Pos cursorPos = cursor.getPosition();
                //-8 is sensitivity, higher = slower (sorry)
                cursor.teleport(new Pos(rotationPacket.yaw()/-8, 1, rotationPacket.pitch()/-8));
                if (cursorPos.blockX() == playerData.targetPos.blockX() && cursorPos.blockZ() == playerData.targetPos.blockZ()) {
                    playerData.createNewTarget();
                }
                //prints player rotation (player sends a shit ton of rotation packets, beware)
                //System.out.println(rotationPacket.yaw() + ", " + rotationPacket.pitch());
            }
        });
        globalEventHandler.addListener(PlayerUseItemEvent.class, event -> {
            Player player = event.getPlayer();
            Instance instance = event.getInstance();
            ItemStack item = event.getItemStack();
            //why is this here part 2
        });
        globalEventHandler.addListener(PlayerHandAnimationEvent.class, event -> {
            //why is this here part 3
        });
        minecraftServer.start("0.0.0.0", 25565);
        //why is this he- oh wait this is important
    }
}
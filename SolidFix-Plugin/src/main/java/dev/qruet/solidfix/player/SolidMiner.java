package dev.qruet.solidfix.player;

import dev.qruet.solidfix.SolidFix;
import dev.qruet.solidfix.utils.Reflections;
import dev.qruet.solidfix.utils.java.Pair;
import dev.qruet.solidfix.utils.java.PairQueue;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * A wrapper for player object
 *
 * @author qruet
 * @version 1.9_01
 */
public class SolidMiner {

    private final UUID uid;
    private final PairQueue<Pair<Block, Long>> bq;

    private Priority priority;
    private boolean fast_mining;
    private int ping;

    public SolidMiner(Player player) {
        this.uid = player.getUniqueId();
        this.priority = Priority.LOW;
        this.ping = 0;
        this.bq = new PairQueue<>(null, null);
    }

    /**
     * Retrieves most recently added block to bq
     *
     * @return Recently broken block by player
     */
    public Block getRecentBrokenBlock() {
        return (bq.getRecent() != null ? bq.getRecent().getKey() : null);
    }

    /**
     * Adds a new block and its associated timestamp to PairQueue
     *
     * @param block
     */
    public void logBlockBreak(Block block) {
        bq.add(new Pair<>(block, System.currentTimeMillis()));
    }

    /**
     * Player's UUID
     *
     * @return Player UUID
     */
    public UUID getId() {
        return uid;
    }

    /**
     * Returns global variable ping
     *
     * @return Player's ping
     */
    public int getPing() {
        return ping;
    }

    /**
     * Returns bukkit player instance
     *
     * @return Player
     */
    public Player getPlayer() {
        return Bukkit.getPlayer(getId());
    }

    /**
     * Returns global variable fast_mining
     *
     * @return Is player currently fast mining
     */
    public boolean isFastMining() {
        return fast_mining;
    }

    /**
     * A replacement for Player#sendBlockChange
     *
     * @param location Location to update
     */
    public void sendBlockChange(Location location) {
        Object packet = null;

        Class<?> CraftPlayer = Reflections.getCraftBukkitClass("entity.CraftPlayer");
        Class<?> EntityPlayer = Reflections.getNMSClass("EntityPlayer");
        Object connection = null;
        try {
            connection = Reflections.getField(EntityPlayer, "playerConnection").get(Reflections.invokeMethod("getHandle", CraftPlayer.cast(getPlayer())));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return;
        }

        Chunk chunk = location.getChunk();
        Block block = location.getBlock();
        Location bLoc = block.getLocation();

        Class<?> CraftWorld = Reflections.getCraftBukkitClass("CraftWorld");
        Class<?> World = Reflections.getNMSClass("World");

        Object world = Reflections.invokeMethod("getHandle", CraftWorld.cast(location.getWorld()));

        Class<?> BlockPosition = Reflections.getNMSClass("BlockPosition");
        Object position = Reflections.instantiate(BlockPosition, bLoc.getBlockX(), bLoc.getBlockY(), bLoc.getBlockZ());

        Class<?> PacketPlayOutBlockChange = Reflections.getNMSClass("PacketPlayOutBlockChange");

        packet = Reflections.instantiate(PacketPlayOutBlockChange, world, position);
        if (packet == null) {
            try {
                Object blockAccess = World.getMethod("c", int.class, int.class).invoke(world, chunk.getX(), chunk.getZ());
                Class<?> IBlockAccess = Reflections.getNMSClass("IBlockAccess");

                packet = PacketPlayOutBlockChange.getConstructor(IBlockAccess, BlockPosition).newInstance(blockAccess, position);
            } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                JavaPlugin.getPlugin(SolidFix.class).getLogger().warning("Are you running on a supported version?");
                e.printStackTrace();
                return;
            }
        }

        Reflections.invokeMethodWithArgs("sendPacket", connection, packet);
    }

    /**
     * Updates global variables
     * e.g. ping, priority (determined by ping), fast_mining (depending on time displacement since
     * most recently broken block and the player's current priority sensitivity)
     */
    public void t() {
        Player player = Bukkit.getPlayer(uid);
        if (player == null)
            return;

        ping = 0;
        try {
            Class<?> EntityPlayer = Reflections.getNMSClass("EntityPlayer");
            Class<?> CraftPlayer = Reflections.getCraftBukkitClass("entity.CraftPlayer");
            Method getHandle = Reflections.getMethod(CraftPlayer, "getHandle");
            Field f_ping = EntityPlayer.getField("ping");

            Object cplayer = getHandle.invoke(CraftPlayer.cast(player));
            ping = f_ping.getInt(cplayer);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        priority = Priority.getPriority(ping);

        fast_mining = (bq.getRecent() != null && bq.getOld() != null) &&
                (System.currentTimeMillis() - bq.getRecent().getValue() <= priority.getSensitivity() &&
                        bq.getRecent().getValue() - bq.getOld().getValue() <= priority.getSensitivity());
    }

}

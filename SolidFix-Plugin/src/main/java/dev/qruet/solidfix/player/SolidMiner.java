package dev.qruet.solidfix.player;

import dev.qruet.solidfix.utils.ReflectionUtils;
import dev.qruet.solidfix.utils.java.Pair;
import dev.qruet.solidfix.utils.java.PairQueue;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

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

    public Block getRecentBrokenBlock() {
        return (bq.getRecent() != null ? bq.getRecent().getKey() : null);
    }

    public void logBlockBreak(Block block) {
        bq.add(new Pair<>(block, System.currentTimeMillis()));
    }

    public UUID getId() {
        return uid;
    }

    public int getPing() {
        return ping;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(getId());
    }

    public boolean isFastMining() {
        return fast_mining;
    }

    public void t() {
        Player player = Bukkit.getPlayer(uid);
        if (player == null)
            return;

        ping = 0;
        try {
            Class<?> EntityPlayer = ReflectionUtils.getNMSClass("EntityPlayer");
            Class<?> CraftPlayer = ReflectionUtils.getCraftBukkitClass("entity.CraftPlayer");
            Method getHandle = ReflectionUtils.getMethod(CraftPlayer, "getHandle");
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

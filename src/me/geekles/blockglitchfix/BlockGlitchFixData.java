package me.geekles.blockglitchfix;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

/**
 * You should not have to extend or instantiate this class for any reason. This
 * class is used to store the data.
 */
public class BlockGlitchFixData {
    public Map<UUID, Long> lastBreakTimeSlow = new HashMap<>();
    public Map<UUID, Long> lastBreakTime = new HashMap<>();
    public HashSet<UUID> blockCheck = new HashSet<>();

    public void removeData(Player player) {
        removeData(player.getUniqueId());
    }

    public void removeData(UUID id) { // schedules certain players for removal
        blockCheck.remove(id);
        lastBreakTime.remove(id);
        lastBreakTimeSlow.remove(id);
    }

}

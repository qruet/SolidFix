package dev.qruet.solidfix.timers;

import dev.qruet.solidfix.SolidServer;
import dev.qruet.solidfix.player.SolidMiner;
import dev.qruet.solidfix.utils.ServerUtil;

/**
 * Responsible for "ticking" all player instances on the server
 * @author qruet
 * @version 2.0_02
 */
public class SolidTimer {

    private static final long base_tick = 20l;

    /**
     * Starts tick timer
     * @return Tick timer instance
     */
    public static Tick tick() {
        Tick tick = new Tick();
        tick.start();
        return tick;
    }

    private SolidTimer() {

    }

    /**
     * Loop that will loop through all player instances online and tick them
     */
    public static class Tick extends Thread {

        private boolean cancelled = false;

        public void cancel() {
            this.cancelled = true;
        }

        /**
         * Variable loop that depends on the local delay variable
         * Calls the tick function for all online player instances
         */
        public void run() {
            while (!cancelled) {
                SolidServer.getOnlinePlayers().forEach(SolidMiner::t);
                try {
                    long delay = (long) (base_tick * Math.pow((20.0 / ServerUtil.getTPS()), 5)); //calculate next delay based on server's current tick
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}

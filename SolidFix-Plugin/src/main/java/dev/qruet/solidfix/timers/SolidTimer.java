package dev.qruet.solidfix.timers;

import dev.qruet.solidfix.SolidServer;
import dev.qruet.solidfix.player.SolidMiner;
import dev.qruet.solidfix.utils.ServerUtil;

public class SolidTimer {

    private static final long base_tick = 20l;

    public static Tick tick() {
        Tick tick = new Tick();
        tick.start();
        return tick;
    }

    private SolidTimer() {

    }

    public static class Tick extends Thread {

        private boolean cancelled = false;

        public void cancel() {
            this.cancelled = true;
        }

        public void run() {
            while (!cancelled) {
                SolidServer.getOnlinePlayers().forEach(SolidMiner::t);
                try {
                    long delay = (long) (base_tick * Math.pow((20.0 / ServerUtil.getTPS()), 5));
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}

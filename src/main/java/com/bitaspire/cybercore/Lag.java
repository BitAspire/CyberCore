package com.bitaspire.cybercore;

import lombok.Getter;
import me.croabeast.common.reflect.Craft;

@Getter
public class Lag implements Runnable {

    private static Lag instance = null;

    private final long[] ticks = new long[600];
    private int tickCount = 0;

    private Lag() {}

    public double getTPS(int ticks) {
        if (tickCount < ticks) return 20;

        int target = (tickCount - 1 - ticks) % this.ticks.length;
        long elapsed = System.currentTimeMillis() - this.ticks[target];

        return Math.min(ticks / (elapsed / 1000.0D), 20.0);
    }

    public double getTPS() {
        return getTPS(100);
    }

    public double getNewTPS() {
        try {
            double[] tps = Craft.Server.INSTANCE.callAsReflector("getServer").get("recentTps");
            return tps[0] > 20 ? 20 : tps[0];
        } catch (Exception e) {
            return getTPS();
        }
    }

    public double getLowerTPS() {
        return Math.min(getTPS(), getNewTPS());
    }

    public long getElapsed(int tickId) {
        return System.currentTimeMillis() - (ticks[tickId % this.ticks.length]);
    }

    @Override
    public void run() {
        ticks[tickCount % ticks.length] = System.currentTimeMillis();
        tickCount += 1;
    }

    public static Lag initialize() {
        return instance == null ? (instance = new Lag()) : instance;
    }
}

package com.bitaspire.cybercore.utility;

import lombok.experimental.UtilityClass;

import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public class RandomUtils {

    private final ThreadLocalRandom CURRENT_RANDOM = ThreadLocalRandom.current();

    public double randomDouble() {
        return CURRENT_RANDOM.nextDouble();
    }

    public double randomDouble(double bound) {
        return CURRENT_RANDOM.nextDouble(bound);
    }

    public int randomInt() {
        return CURRENT_RANDOM.nextInt();
    }

    public int randomInt(int bound) {
        return CURRENT_RANDOM.nextInt(bound);
    }

    public long randomLong() {
        return CURRENT_RANDOM.nextLong();
    }

    public long randomLong(long bound) {
        return CURRENT_RANDOM.nextLong(bound);
    }
}

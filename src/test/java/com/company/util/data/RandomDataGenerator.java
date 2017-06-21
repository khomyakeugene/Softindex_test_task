package com.company.util.data;

import java.util.Random;

/**
 * Created by ekhomiak on 15.06.2017.
 */
public class RandomDataGenerator {
    private static Random random = new Random();

    public static int getRandomInteger(int upperBound) {
        return random.nextBoolean() ? random.nextInt(upperBound) * -1 : random.nextInt(upperBound);
    }

    public static long getRandomLong() {
        return random.nextLong();
    }
}

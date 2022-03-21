package tech.goksi.raveolution.utils;

import java.util.Random;

public class LevelUtils {
    public static long xpToNextLevel(int level) {
        return 5 * (((long) Math.pow(level, 2)) + 10L * level + 20);
    }

    public static long levelsToXp(int levels) {
        long xp = 0;

        for (int level = 0; level <= levels; level++) {
            xp += xpToNextLevel(level);
        }

        return xp;
    }

    public static int xpToLevels(long totalXp) {
        boolean calculating = true;
        int level = 0;

        while (calculating) {
            long xp = levelsToXp(level);

            if (totalXp < xp) {
                calculating = false;
            } else {
                level++;
            }
        }

        return level;
    }

    public static long remainingXp(long totalXp) {
        int level = xpToLevels(totalXp); //5

        if (level == 0) return totalXp;

        long xp = levelsToXp(level);

        return xp - totalXp;
    }

    public static int randomXp(int min, int max) {
        Random random = new Random();

        return random.nextInt((max - min) + 1) + min;
    }


}

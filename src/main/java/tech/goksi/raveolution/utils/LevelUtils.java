package tech.goksi.raveolution.utils;

import java.util.Random;

public class LevelUtils {
    public static long randomXP(int min, int max){
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public static long remainingXp(long totalXp) {
        int lvl = xpToLevels(totalXp);

        if (lvl == 0) return totalXp;

        long xp = levelsToXp(lvl);

        return totalXp - xp + xpToNextLevel(lvl);
    }
    public static int xpToLevels(long totalXp) {
        boolean rac = true;
        int level = 0;

        while (rac) {
            long xp = levelsToXp(level);

            if (totalXp < xp) {
                rac = false;
            } else {
                level++;
            }
        }

        return level;
    }
    private static long levelsToXp(int levels) {
        long xp = 0;

        for (int level = 0; level <= levels; level++) {
            xp += xpToNextLevel(level);
        }

        return xp;
    }
    public static long xpToNextLevel(int level) {
        return 5 * (((long) Math.pow(level, 2)) + 10L * level);
    }

}

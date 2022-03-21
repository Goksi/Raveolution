package tech.goksi.raveolution.utils;

public class LevelBar {
    private long currentLevel;
    private long xp;
    private long nextLevel;

    public LevelBar(long currentXP){
        this.xp = currentXP;
        this.currentLevel = LevelUtils.xpToLevels(currentXP);
        this.nextLevel = currentLevel + 1;

    }
}

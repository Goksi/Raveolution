package tech.goksi.raveolution.utils;



public class LevelBar  {
    private final int currentLevel;
    private final long xp;
    private final int nextLevel;

    public LevelBar(long currentXP){
        this.xp = currentXP;
        this.currentLevel = LevelUtils.xpToLevels(currentXP);
        this.nextLevel = currentLevel + 1;
    }

    /*im so bad in math, not sure if this will work, maybe work, don't know yet*/
    public String toString(){
        long difference = LevelUtils.levelsToXp(currentLevel) - LevelUtils.levelsToXp(currentLevel-1);
        long presao = xp - LevelUtils.levelsToXp(currentLevel-1); //really don't know how to call this var in english
        long currentPercentageL = (presao*100L)/difference;
        int currentPercentage = Math.round(currentPercentageL);
        int whatBlock = currentPercentage/10;
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<10; i++){
            if(i>=whatBlock){
                sb.append("░");
            }else{
                sb.append("█");
            }
        }
        return sb.toString();
    }



    public int getNextLevel() {
        return nextLevel;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }


}

package tech.goksi.raveolution.sql;

import net.dv8tion.jda.api.entities.User;
import tech.goksi.raveolution.Bot;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {

    public void createTable(){
        try{
            PreparedStatement ps1 = Bot.getInstance().getSql().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS invites "
            + "(ID BIGINT, TOTAL INTEGER, FAKE INTEGER, 'LEFT' INTEGER, PRIMARY KEY (ID))");
            PreparedStatement ps2 = Bot.getInstance().getSql().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS points "
            + "(ID BIGINT, LEVEL INTEGER DEFAULT  0, XP REAL DEFAULT 0, PRIMARY KEY (ID))");
            ps1.executeUpdate();
            ps2.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void lvlUp(User u){
        int currentLvl = getLvl(u);
        try{
            PreparedStatement ps = Bot.getInstance().getSql().getConnection().prepareStatement("UPDATE points SET LEVEL=? WHERE ID=?");
            ps.setInt(1, currentLvl + 1);
            ps.setLong(2, u.getIdLong());
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    public int getLvl(User u){
        try{
            PreparedStatement ps = Bot.getInstance().getSql().getConnection().prepareStatement("SELECT LEVEL FROM points WHERE ID=?");
            ps.setLong(1, u.getIdLong());
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt("LEVEL");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    public void addXP(User u){
        float currentXP = getXP(u);
        try{
            PreparedStatement ps = Bot.getInstance().getSql().getConnection().prepareStatement("INSERT OR REPLACE INTO points (ID, XP) VALUES (?, ?)");
            ps.setLong(1, u.getIdLong());
            ps.setFloat(2, currentXP + 0.5f);
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }


    public float getXP(User u){
        try{
            PreparedStatement ps = Bot.getInstance().getSql().getConnection().prepareStatement("SELECT XP FROM points WHERE ID=?");
            ps.setLong(1, u.getIdLong());
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getFloat("XP");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }
}

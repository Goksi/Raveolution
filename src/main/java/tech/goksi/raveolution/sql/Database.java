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
            + "(ID BIGINT, LEVEL INTEGER DEFAULT  0, XP BIGINT DEFAULT 0, PRIMARY KEY (ID))");
            ps1.executeUpdate();
            ps2.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void addLvl(User u, int lvl){
        try{
            PreparedStatement ps = Bot.getInstance().getSql().getConnection().prepareStatement("UPDATE points SET LEVEL=? WHERE ID=?");
            ps.setInt(1, lvl);
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

    public void addXP(User u, long xp){
        float currentXP = getXP(u);
        try{
            PreparedStatement ps = Bot.getInstance().getSql().getConnection().prepareStatement("INSERT OR REPLACE INTO points (ID, XP) VALUES (?, ?)");
            ps.setLong(1, u.getIdLong());
            ps.setFloat(2, currentXP + xp);
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }


    public long getXP(User u){
        try{
            PreparedStatement ps = Bot.getInstance().getSql().getConnection().prepareStatement("SELECT XP FROM points WHERE ID=?");
            ps.setLong(1, u.getIdLong());
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getLong("XP");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }
}

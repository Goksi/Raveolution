package tech.goksi.raveolution.sql;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import tech.goksi.raveolution.Bot;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;

public class Database {

    public void createTable(){
        try{
            PreparedStatement ps1 = Bot.getInstance().getSql().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS invites "
            + "(ID BIGINT, TOTAL INTEGER DEFAULT 0, FAKE INTEGER DEFAULT 0, 'LEFT' INTEGER DEFAULT 0, InvitedBy BIGINT, PRIMARY KEY (ID))");
            PreparedStatement ps2 = Bot.getInstance().getSql().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS points "
            + "(ID BIGINT,XP BIGINT DEFAULT 0, LEVEL INTEGER DEFAULT  0,  PRIMARY KEY (ID))");
            ps1.executeUpdate();
            ps2.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void addLvl(User u, int lvl){
        try{
            PreparedStatement ps2 = Bot.getInstance().getSql().getConnection().prepareStatement("INSERT OR IGNORE INTO points (ID) VALUES (?) ");
            PreparedStatement ps1 = Bot.getInstance().getSql().getConnection().prepareStatement("UPDATE points SET LEVEL=? WHERE ID=?");
            ps1.setInt(1, lvl);
            ps2.setLong(1, u.getIdLong());
            ps1.setLong(2, u.getIdLong());
            ps2.executeUpdate();
            ps1.executeUpdate();
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
            PreparedStatement ps2 = Bot.getInstance().getSql().getConnection().prepareStatement("INSERT OR IGNORE INTO points (ID) VALUES (?)");
            PreparedStatement ps = Bot.getInstance().getSql().getConnection().prepareStatement("UPDATE points SET XP=? WHERE ID=?");
            ps.setLong(2, u.getIdLong());
            ps2.setLong(1, u.getIdLong());
            ps.setFloat(1, currentXP + xp);
            ps2.executeUpdate();
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

    public void invitesAdd(User user, User invitedBy){
        try{
            PreparedStatement ps = Bot.getInstance().getSql().getConnection().prepareStatement("INSERT OR IGNORE INTO invites"
                    + "(ID) VALUES (?)");
            PreparedStatement ps1 = Bot.getInstance().getSql().getConnection().prepareStatement("UPDATE invites SET InvitedBy=? WHERE ID=?");
            PreparedStatement ps2 = Bot.getInstance().getSql().getConnection().prepareStatement("UPDATE invites SET TOTAL=? WHERE ID=?");
            ps.setLong(1, user.getIdLong());
            ps1.setLong(1, invitedBy.getIdLong());
            ps1.setLong(2, user.getIdLong());
            ps2.setInt(1, getInvitesTotal(invitedBy) + 1);
            ps2.setLong(2, invitedBy.getIdLong());
            ps.executeUpdate();
            ps2.executeUpdate();
            ps1.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public int getInvitesTotal(User u){
        try{
            PreparedStatement ps = Bot.getInstance().getSql().getConnection().prepareStatement("SELECT TOTAL FROM invites WHERE ID=?");
            ps.setLong(1, u.getIdLong());
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt("TOTAL");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }
    public long getInviter(User u){
        try{
            PreparedStatement ps = Bot.getInstance().getSql().getConnection().prepareStatement("SELECT InvitedBy FROM invites WHERE ID=?");
            ps.setLong(1, u.getIdLong());
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getLong("InvitedBy");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }
    public void addLeave( User inviter){
        try{
            PreparedStatement ps1 = Bot.getInstance().getSql().getConnection().prepareStatement("UPDATE invites SET 'LEFT'=? WHERE ID=?");
            ps1.setLong(2, inviter.getIdLong());
            ps1.setInt(1, getLeft(inviter) + 1);
            ps1.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public int getLeft(User u){
        try{
            PreparedStatement ps = Bot.getInstance().getSql().getConnection().prepareStatement("SELECT 'LEFT' FROM invites WHERE ID=?");
            ps.setLong(1, u.getIdLong());
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt("'LEFT'");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }
    public int getFakeInvites(User u){
        try{
            PreparedStatement ps = Bot.getInstance().getSql().getConnection().prepareStatement("SELECT FAKE FROM invites WHERE ID=?");
            ps.setLong(1, u.getIdLong());
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getInt("FAKE");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }
    public void addFakeInvite(User u){
        try{
            PreparedStatement ps = Bot.getInstance().getSql().getConnection().prepareStatement("UPDATE invites SET FAKE=? WHERE ID=?");
            ps.setInt(1, getFakeInvites(u) + 1);
            ps.setLong(2, u.getIdLong());
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public LinkedHashMap<String, String> getLevelLeaderboards(){
        LinkedHashMap<String, String> temp = new LinkedHashMap<>();
        try{
            PreparedStatement ps = Bot.getInstance().getSql().getConnection().prepareStatement("SELECT ID,LEVEL,XP FROM points ORDER BY XP DESC LIMIT 10");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                User u = Bot.getInstance().getJda().retrieveUserById(rs.getLong("ID")).complete();
                temp.put(u.getAsTag(), String.valueOf(rs.getInt("LEVEL")));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return temp;
    }
    public LinkedHashMap<String, String> getInvitesLeaderboards(){
        LinkedHashMap<String, String> temp = new LinkedHashMap<>();
        try{
            PreparedStatement ps = Bot.getInstance().getSql().getConnection().
                    prepareStatement("SELECT ID,TOTAL,FAKE,'LEFT', TOTAL - invites.FAKE - invites.LEFT AS final FROM invites ORDER BY final DESC LIMIT 10");
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                User u = Bot.getInstance().getJda().retrieveUserById(rs.getLong("ID")).complete();
                int finalN = rs.getInt("TOTAL") - rs.getInt("FAKE") - rs.getInt("'LEFT'");
                if(finalN != 0){
                    temp.put(u.getAsTag(), String.valueOf(finalN));
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return temp;
    }

    public void fixInvites(){
        List<Member> members = Bot.getInstance().getJda().getGuilds().get(0).loadMembers().get();
        for(Member m : members){
            if(!m.getUser().isBot()){
                try{
                    PreparedStatement ps = Bot.getInstance().getSql().getConnection().prepareStatement("INSERT OR IGNORE INTO invites (ID, InvitedBy) VALUES (?, NULL)");
                    ps.setLong(1, m.getIdLong());
                    ps.executeUpdate();
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }

        }
    }

}

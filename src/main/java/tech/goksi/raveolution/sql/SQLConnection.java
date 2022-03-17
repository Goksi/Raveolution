package tech.goksi.raveolution.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnection {
    private Connection connection;


    public void connect(){


        if(!isConnected()){
            try{
                connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }
    private boolean isConnected(){
        return connection != null;
    }

    public Connection getConnection() {
        return connection;
    }

    public void disconnect(){
        if(isConnected()){
            try{
                connection.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }
}

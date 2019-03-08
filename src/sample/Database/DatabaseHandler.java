package sample.Database;

import sample.Alert.AlertMaker;
import java.sql.Connection;
import java.sql.DriverManager;


public class DatabaseHandler {

    private static DatabaseHandler handler = null;
    private static Connection conn = null;

    static {
        createConnection();
    }

    private DatabaseHandler() {
    }

    public static DatabaseHandler getInstance() {
        if (handler == null) {
            handler = new DatabaseHandler();
        }
        return handler;
    }

    public static void createConnection()  {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:mydatabase.sqlite"
                    ,"scott"
                    ,"tiger");
        }catch (Exception e){
            AlertMaker.showErrorMessage(e);
        }
    }

    public Connection getConnection() {
        return conn;
    }
}

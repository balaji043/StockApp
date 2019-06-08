package sample.Database;

import org.jetbrains.annotations.NotNull;
import sample.Alert.AlertMaker;
import sample.Utils.Preferences;
import sample.model.User;

import java.sql.*;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class DatabaseHelper {
    private static PreparedStatement preparedStatement = null;
    private static boolean okay = false;


    public static void create() {
        createLogTable();
        if (createUserTable()) {
            DatabaseHelper_User.insertNewUser(new User("admin"
                    , "admin"
                    , "admin"
                    , "123"
                    , "admin"));
        }
    }

    //product
    //create table
    private static boolean createTable(String createQuery) {
        Connection connection = DatabaseHandler.getInstance().getConnection();
        okay = false;
        try {
            preparedStatement = connection.prepareStatement(createQuery);
            okay = !preparedStatement.execute();
        } catch (Exception e) {
            e.printStackTrace();
            AlertMaker.showErrorMessage(e);
        }
        return okay;
    }

    public static void createProductTable(@NotNull String tableName) {
        okay = false;
        try {
            String createQuery = "create table if not exists " + tableName +
                    " (category text NOT NULL,subcategory" +
                    " text NOT NULL,name text NOT NULL,partno text NOT NULL,quantity " +
                    " text NOT NULL ,mrp text NOT NULL" +
                    ", hsncode text,place text NOT NULL,remarks text NOT NULL, min text NOT NULL , " +
                    "unique(category,subCategory,name,mrp,hsncode,place,quantity,partno))";
            okay = createTable(createQuery);
            if (okay) {
                Preferences preferences = Preferences.getPreferences();
                Set<String> strings = preferences.getTableNames();
                strings.add(tableName);
                preferences.setTableNames(strings);
                Preferences.setPreference(preferences);
            }
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        }

    }

    //log
    // create Log Table
    static void createLogTable() {
        okay = false;
        String query = "CREATE TABLE IF NOT EXISTS Log ( brand TEXT NOT NULL," +
                " category TEXT NOT NULL, subcategory TEXT NOT NULL, name TEXT " +
                "NOT NULL, date TEXT NOT NULL, entry TEXT NOT NULL, received " +
                "TEXT NOT NULL, issued TEXT NOT NULL,  balance TEXT NOT NULL" +
                ", user TEXT NOT NULL, action NOT NULL , remarks TEXT NOT NULL)";
        okay = createTable(query);
    }

    //user
    //create user table
    private static boolean createUserTable() {
        okay = false;
        String create = "CREATE TABLE IF NOT EXISTS Employee (Name text NOT NULL" +
                ",id text NOT NULL UNIQUE,password text NOT NULL" +
                ",access text NOT NULL,UserName text NOT NULL)";
        okay = createTable(create);
        return okay;
    }


    //table
    //get table name
    public static Set<String> getTableNames() {
        Set<String> set = new HashSet<>();
        try {
            DatabaseMetaData md = DatabaseHandler.getInstance().getConnection().getMetaData();
            ResultSet rs = md.getTables(null, null, "%", null);
            while (rs.next()) {
                set.add(rs.getString(3));
            }

            return set;
        } catch (SQLException e) {
            AlertMaker.showErrorMessage(e);
        }
        return set;
    }


    //delete table
    public static boolean deleteTable(@NotNull String tableName) {
        okay = false;
        try {
            String q = "DROP TABLE IF EXISTS " + tableName;
            preparedStatement =
                    DatabaseHandler.getInstance().getConnection().prepareStatement(q);
            okay = !preparedStatement.execute();
            if (okay) {
                Preferences preferences = Preferences.getPreferences();
                Set<String> strings = preferences.getTableNames();
                strings.remove(tableName);
                preferences.setTableNames(strings);
                Preferences.setPreference(preferences);
            }
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
            e.printStackTrace();
        }
        return okay;
    }

    static LinkedHashSet<String> getStrings(PreparedStatement preparedStatement, String columnLabel) {
        LinkedHashSet<String> hashSet = new LinkedHashSet<>();
        try {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
                hashSet.add(resultSet.getString(columnLabel));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hashSet;
    }
}

package sample.Utils;

import com.google.gson.Gson;
import sample.Alert.AlertMaker;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class Preferences {
    private static final String FILE_PATH = "config.txt";
    private String path;
    private Set<String> tableNames;
    private String s;
    private String theme;
    // Constructor


    private Preferences() {
        path = null;
        tableNames = new HashSet<>();
        s = null;
        theme = "blue";
    }

    private static void initConfig() {
        Writer writer = null;
        try {
            File file = new File(FILE_PATH);
            file.createNewFile();
            Preferences preferences = new Preferences();
            writer = new FileWriter(FILE_PATH);
            Gson gson = new Gson();
            gson.toJson(preferences, writer);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                assert writer != null;
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Preferences getPreferences() {
        FileReader fileReader;
        Preferences preferences = null;
        try {
            fileReader = new FileReader(FILE_PATH);
            Gson gson = new Gson();
            preferences = gson.fromJson(fileReader, Preferences.class);
        } catch (Exception e) {
            initConfig();
        } finally {
            try {
                fileReader = new FileReader(FILE_PATH);
                Gson gson = new Gson();
                preferences = gson.fromJson(fileReader, Preferences.class);
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
        return preferences;
    }

    public static void setPreference(Preferences preferences) {
        Writer writer = null;
        try {
            writer = new FileWriter(FILE_PATH);
            Gson gson = new Gson();
            gson.toJson(preferences, writer);
        } catch (Exception e) {
            //System.out.println(e.getMessage());
            e.printStackTrace();

            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                assert writer != null;
                writer.close();
            } catch (IOException e) {
                AlertMaker.showErrorMessage(e);
                e.printStackTrace();
            }
        }
    }


    // Getters and Setters


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Set<String> getTableNames() {
        return tableNames;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public void setTableNames(Set<String> tableNames) {
        this.tableNames = tableNames;
    }

    public String getTheme() {
        return theme;
    }
}

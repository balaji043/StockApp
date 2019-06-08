package sample.Database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jetbrains.annotations.NotNull;
import sample.Alert.AlertMaker;
import sample.Utils.StockManagementUtils;
import sample.model.Log;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedHashSet;


public class DatabaseHelper_Log {

    public static boolean insertNewLog(@NotNull Log log) {
        PreparedStatement preparedStatement;
        boolean okay = false;
        try {
            DatabaseHelper.createLogTable();
            String insert = "INSERT INTO Log ( category , subcategory ," +
                    " name , date , entry , received , issued , balance " +
                    ", user , brand , action , remarks) values" +
                    " (?,?,?,?,?,?,?,?,?,?,?,?)";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(insert);
            preparedStatement.setString(1, log.getCategory());
            preparedStatement.setString(3, log.getName());
            preparedStatement.setString(4, log.getDate());
            preparedStatement.setString(2, log.getSubCategory());
            preparedStatement.setString(5, log.getEntry());
            preparedStatement.setString(6, log.getReceived());
            preparedStatement.setString(7, log.getIssued());
            preparedStatement.setString(8, log.getBalance());
            preparedStatement.setString(9, log.getUser());
            preparedStatement.setString(10, log.getBrand());
            preparedStatement.setString(11, log.getAction());
            preparedStatement.setString(12, log.getRemarks());

            okay = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            AlertMaker.showErrorMessage(e);
        }
        return okay;
    }

    public static ObservableList<Log> getAllLogList() {
        ObservableList<Log> logs = FXCollections.observableArrayList();
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        try {
            String query = "SELECT * FROM LOG WHERE TRUE";
            preparedStatement = DatabaseHandler.getInstance()
                    .getConnection().prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                logs.add(getLog(resultSet));
            }
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        }
        return logs;
    }


    public static boolean deleteLogs(@NotNull ObservableList<Log> logs) {
        PreparedStatement preparedStatement;
        boolean okay = false;
        try {

            String insert = "DELETE FROM Log where category = ? and subcategory = ? and name = ? " +
                    "and entry = ? and received = ? and issued = ? and" +
                    " balance = ? and user = ? and action = ? and remarks = ?";
            for (Log log : logs) {
                preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(insert);
                preparedStatement.setString(1, log.getCategory());
                preparedStatement.setString(2, log.getSubCategory());
                preparedStatement.setString(4, log.getEntry());
                preparedStatement.setString(3, log.getName());
                preparedStatement.setString(5, log.getReceived());
                preparedStatement.setString(6, log.getIssued());
                preparedStatement.setString(7, log.getBalance());
                preparedStatement.setString(8, log.getUser());
                preparedStatement.setString(9, log.getAction());
                preparedStatement.setString(10, log.getRemarks());
                okay = preparedStatement.executeUpdate() > 0;
            }
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        }
        return okay;
    }

    public static ObservableList<Log> getLogListBrand(@NotNull String br) {
        ObservableList<Log> logs = FXCollections.observableArrayList();
        PreparedStatement preparedStatement;
        ResultSet resultSet;

        try {
            String query = "SELECT * FROM LOG WHERE BRAND = ?";
            preparedStatement = DatabaseHandler.getInstance()
                    .getConnection().prepareStatement(query);
            preparedStatement.setString(1, br);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                logs.add(getLog(resultSet));
            }
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        }
        return logs;
    }

    public static ObservableList<Log> getLogListBrandWithDate(@NotNull String br, @NotNull LocalDate a, @NotNull LocalDate b) {

        PreparedStatement preparedStatement;
        ResultSet resultSet = null;
        try {
            String query = "SELECT * FROM LOG WHERE BRAND = ?";
            preparedStatement = DatabaseHandler.getInstance()
                    .getConnection().prepareStatement(query);
            preparedStatement.setString(1, br);
            resultSet = preparedStatement.executeQuery();

        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        }
        assert resultSet != null;
        return getLogList(resultSet, a, b);

    }

    public static ObservableList<Log> getLogListCategory(@NotNull String br, @NotNull String c) {
        ObservableList<Log> logs = FXCollections.observableArrayList();
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        try {
            String query = "SELECT * FROM LOG WHERE CATEGORY = ? AND BRAND = ? ";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, c);
            preparedStatement.setString(2, br);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
                logs.add(getLog(resultSet));
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        }
        return logs;

    }

    public static ObservableList<Log> getLogListCategoryWithDate(@NotNull String brand, @NotNull String c
            , LocalDate a, LocalDate b) {
        PreparedStatement preparedStatement;
        ResultSet resultSet = null;
        try {
            String query = "SELECT * FROM LOG WHERE CATEGORY = ? && BRAND = ?? ";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, c);
            preparedStatement.setString(2, brand);
            resultSet = preparedStatement.executeQuery();

        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        }
        assert resultSet != null;
        return getLogList(resultSet, a, b);

    }

    private static ObservableList<Log> getLogList(ResultSet resultSet, LocalDate a, LocalDate b) {
        ObservableList<Log> logs = FXCollections.observableArrayList();
        try {
            while (resultSet.next()) {
                long l = Long.parseLong(resultSet.getString("date"));
                if (checkDateCondition(l, a, b))
                    logs.add(getLog(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return logs;
    }

    private static boolean checkDateCondition(long l, LocalDate a, LocalDate b) {
        Date date = new Date(l);
        LocalDate d = convertToLocalDateViaInstant(date);
        return (d.isEqual(a) || d.isEqual(b)) || (d.isAfter(a) && d.isBefore(b));
    }

    public static ObservableList<Log> getLogListSubCategory(@NotNull String b, @NotNull String c, @NotNull String s) {
        ObservableList<Log> logs = FXCollections.observableArrayList();
        ResultSet resultSet;

        try {
            resultSet = getLogListThreeParam(b, c, s);
            while (resultSet.next())
                logs.add(getLog(resultSet));

        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        }
        return logs;

    }

    private static ResultSet getLogListThreeParam(String b, String c, String s) throws SQLException {
        String query = "SELECT * FROM LOG WHERE CATEGORY = ? AND SUBCATEGORY = ? AND BRAND = ?";
        return getPreparedStatement(query, c, s, b).executeQuery();
    }

    public static ObservableList<Log> getLogListSubCategoryWithDate(@NotNull String br
            , @NotNull String c, @NotNull String s, @NotNull LocalDate a, @NotNull LocalDate b) {
        ResultSet resultSet = null;
        try {
            resultSet = getLogListThreeParam(br, c, s);
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        }
        assert resultSet != null;
        return getLogList(resultSet, a, b);

    }

    public static ObservableList<Log> getLogListName(@NotNull String b, @NotNull String c, @NotNull String s, @NotNull String n) {
        ObservableList<Log> logs = FXCollections.observableArrayList();
        PreparedStatement preparedStatement;
        ResultSet resultSet;

        try {
            String query = "SELECT * FROM LOG WHERE CATEGORY = ? AND SUBCATEGORY = ? AND NAME = ? AND BRAND = ?";
            preparedStatement = getPreparedStatement(query, c, s, n);
            preparedStatement.setString(4, b);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
                logs.add(getLog(resultSet));
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        }
        return logs;

    }

    public static ObservableList<Log> getLogListNameWithDate(@NotNull String c, @NotNull String s, @NotNull String n
            , @NotNull LocalDate a, @NotNull LocalDate b) {
        ResultSet resultSet = null;

        try {
            String query = "SELECT * FROM LOG WHERE CATEGORY = ? AND SUBCATEGORY = ? AND NAME = ?";
            resultSet = getPreparedStatement(query, c, s, n).executeQuery();
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        }
        assert resultSet != null;
        return getLogList(resultSet, a, b);

    }

    public static ObservableList<Log> getLogListDateOnly(@NotNull LocalDate a, @NotNull LocalDate b) {
        ObservableList<Log> logs = FXCollections.observableArrayList();
        PreparedStatement preparedStatement;
        ResultSet resultSet;

        try {
            String query = "SELECT * FROM LOG WHERE TRUE";
            preparedStatement = DatabaseHandler.getInstance()
                    .getConnection().prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long l = Long.parseLong(resultSet.getString("date"));
                Date date = new Date(l);
                LocalDate d = convertToLocalDateViaInstant(date);
                if ((d.isEqual(a) || d.isEqual(b)) || (d.isAfter(a) && d.isBefore(b))) {
                    logs.add(getLog(resultSet));
                }
            }
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        }
        return logs;
    }

    public static ObservableList<Log> getLogLisWithSearchText(String text) {
        ObservableList<Log> logs = FXCollections.observableArrayList();
        PreparedStatement preparedStatement;
        ResultSet resultSet;

        try {
            String query = "SELECT * FROM LOG WHERE name = ?;";
            preparedStatement = DatabaseHandler.getInstance()
                    .getConnection().prepareStatement(query);
            preparedStatement.setString(1, text);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                logs.add(getLog(resultSet));
            }
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        }
        return logs;
    }

    private static LocalDate convertToLocalDateViaInstant(@NotNull Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    public static LinkedHashSet<String> getCategoriesFromLog(@NotNull String brandName) {
        PreparedStatement preparedStatement = null;
        try {
            String query = "select distinct category from Log WHERE brand = ?;";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, brandName);
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        }
        return DatabaseHelper.getStrings(preparedStatement, "category");
    }


    public static LinkedHashSet<String> getSubCategoryFromLog(@NotNull String brandName, @NotNull String category) {
        String query = "select distinct subcategory from Log where brand  = ? and category = ? ";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, brandName);
            preparedStatement.setString(2, category);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return DatabaseHelper.getStrings(preparedStatement, "subcategory");

    }


    public static LinkedHashSet<String> getProductNameFromLog(String brandName, String category, String subCategory) {
        String query = "select distinct name from Log where brand  = ? and category = ? and  subcategory = ?";
        return DatabaseHelper.getStrings(getPreparedStatement(query, brandName, category, subCategory), "name");
    }

    static PreparedStatement getPreparedStatement(String q, String c1, String c2, String c3) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(q);
            preparedStatement.setString(1, c1);
            preparedStatement.setString(2, c2);
            preparedStatement.setString(3, c3);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return preparedStatement;
    }

    private static Log getLog(ResultSet resultSet) throws SQLException {
        Long l = Long.parseLong(resultSet.getString("date"));
        String dat = StockManagementUtils.formatDateTimeString(l);
        return new Log(resultSet.getString("brand")
                , dat
                , resultSet.getString("category")
                , resultSet.getString("subcategory")
                , resultSet.getString("name")
                , resultSet.getString("entry")
                , resultSet.getString("received")
                , resultSet.getString("issued")
                , resultSet.getString("balance")
                , resultSet.getString("user")
                , resultSet.getString("action")
                , resultSet.getString("remarks"));
    }

    public static LinkedHashSet<String> getNames() {
        String query = "select distinct name from Log;";
        LinkedHashSet<String> names = new LinkedHashSet<>();
        try {
            names = DatabaseHelper.getStrings(DatabaseHandler.getInstance().getConnection().prepareStatement(query), "name");
        } catch (SQLException e) {

            e.printStackTrace();
        }
        return names;
    }
}

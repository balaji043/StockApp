package sample.Database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import sample.Alert.AlertMaker;
import sample.Main;
import sample.Utils.Preferences;
import sample.Utils.StockManagementUtils;
import sample.model.Log;
import sample.model.Product;
import sample.model.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.*;

@SuppressWarnings("ALL")
public class DatabaseHelper {
    public static Main main;
    private static PreparedStatement preparedStatement = null;
    private static ResultSet resultSet = null;
    private static boolean okay = false;
    private static Product p = null;

    public static void create() {
        createLogTable();
        if (createUserTable()) {
            insertNewUser(new User("admin"
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
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return okay;
    }

    public static void setMain(Main main) {
        DatabaseHelper.main = main;
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

    //insert new product
    public static boolean insertNewProduct(@NotNull Product product, @NotNull String tableName) {
        okay = false;
        try {
            String insert = "insert into " + tableName +
                    " ( category , subcategory ,name , partno " +
                    ", quantity , mrp , hsncode , place , remarks,min )" +
                    " values (?,?,?,?,?,?,?,?,?,?)";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(insert);
            preparedStatement.setString(1, product.getCategory());
            preparedStatement.setString(2, product.getSubCategory());
            preparedStatement.setString(3, product.getName());
            preparedStatement.setString(4, product.getPartNo());
            preparedStatement.setString(5, product.getQTY());
            preparedStatement.setString(6, product.getMRP());
            preparedStatement.setString(7, product.getHsnCode());
            preparedStatement.setString(8, product.getPlace());
            preparedStatement.setString(9, product.getRemarks());
            preparedStatement.setString(10, product.getMin());
            okay = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                assert preparedStatement != null;
                preparedStatement.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return okay;
    }

    //update product
    public static boolean updateProduct(@NotNull Product product, @NotNull String tableName) {
        okay = false;
        try {
            String insert = " update " + tableName + " set partno = ? , quantity = ? ," +
                    " mrp = ? , hsncode = ? , place = ? , remarks = ? , min = ? where category = ?" +
                    " and subcategory = ? and name = ?  ";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(insert);
            preparedStatement.setString(1, product.getPartNo());
            preparedStatement.setString(2, product.getQTY());
            preparedStatement.setString(3, product.getMRP());
            preparedStatement.setString(4, product.getHsnCode());
            preparedStatement.setString(5, product.getPlace());
            preparedStatement.setString(6, product.getRemarks());
            preparedStatement.setString(7, product.getMin());
            preparedStatement.setString(8, product.getCategory());
            preparedStatement.setString(9, product.getSubCategory());
            preparedStatement.setString(10, product.getName());
            okay = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            AlertMaker.showErrorMessage(e);
            e.printStackTrace();
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return okay;
    }

    //delete product
    public static boolean deleteProduct(@NotNull Product product, @NotNull String tableName) {
        okay = false;
        try {
            String insert = "DELETE FROM " + tableName +
                    " where category = ? and subcategory = ? and name = ? ";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(insert);
            preparedStatement.setString(1, product.getCategory());
            preparedStatement.setString(2, product.getSubCategory());
            preparedStatement.setString(3, product.getName());
            okay = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return okay;
    }

    //is product exist
    public static boolean isProductExist(@NotNull Product product, @NotNull String tableName) {
        okay = false;
        try {
            String query = " select * from " + tableName + " " +
                    " where category = ? and subcategory = ? and name = ? and  partno = ? and" +
                    " quantity = ?  and mrp = ?  and hsncode = ?  and place = ? " +
                    " and remarks = ? and min  = ? ";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, product.getCategory());
            preparedStatement.setString(2, product.getSubCategory());
            preparedStatement.setString(3, product.getName());
            preparedStatement.setString(4, product.getPartNo());
            preparedStatement.setString(5, product.getQTY());
            preparedStatement.setString(6, product.getMRP());
            preparedStatement.setString(7, product.getHsnCode());
            preparedStatement.setString(8, product.getPlace());
            preparedStatement.setString(9, product.getRemarks());
            preparedStatement.setString(10, product.getMin());
            resultSet = preparedStatement.executeQuery();
            okay = resultSet.next();
        } catch (SQLException e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return okay;
    }

    public static boolean isProductExistS(@NotNull Product product, @NotNull String tableName) {
        okay = false;
        try {
            String query = " select * from " + tableName + " " +
                    " where category = ? and subcategory = ? and name = ?  ";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, product.getCategory());
            preparedStatement.setString(2, product.getSubCategory());
            preparedStatement.setString(3, product.getName());
            resultSet = preparedStatement.executeQuery();
            okay = resultSet.next();
        } catch (SQLException e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return okay;
    }

    //get product info
    public static Product getProductInfo(@NotNull Product product, @NotNull String tableName) {
        p = null;
        try {
            String insert = " select * from " + tableName + " where category = ? and subcategory = ? and name = ?";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(insert);
            preparedStatement.setString(1, product.getCategory());
            preparedStatement.setString(2, product.getSubCategory());
            preparedStatement.setString(3, product.getName());
            resultSet = preparedStatement.executeQuery();
            p = new Product(resultSet.getString("category")
                    , resultSet.getString("subcategory")
                    , resultSet.getString("name")
                    , resultSet.getString("partno")
                    , resultSet.getString("quantity")
                    , resultSet.getString("mrp")
                    , resultSet.getString("hsncode")
                    , resultSet.getString("place")
                    , resultSet.getString("remarks"), tableName);
        } catch (SQLException e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
                assert resultSet != null;
                resultSet.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return p;
    }

    //get neededProduct Info
    public static ObservableList<Product> getNeededProductList(@NotNull String tableName) {
        ObservableList<Product> products = FXCollections.observableArrayList();
        Product p;
        boolean b;
        try {
            preparedStatement = DatabaseHandler.getInstance().getConnection().
                    prepareStatement("SELECT * FROM " + tableName + " where true");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                try {
                    b = Integer.parseInt(resultSet.getString("quantity"))
                            < Integer.parseInt(resultSet.getString("min"));

                } catch (Exception e) {
                    b = false;
                }
                if (b) {
                    p = new Product(
                            resultSet.getString("category")
                            , resultSet.getString("subcategory")
                            , resultSet.getString("name")
                            , resultSet.getString("partno")
                            , resultSet.getString("quantity")
                            , resultSet.getString("mrp")
                            , resultSet.getString("hsncode")
                            , resultSet.getString("place")
                            , resultSet.getString("remarks"), tableName);
                    p.setMin(resultSet.getString("min"));
                    products.add(p);
                }
            }
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
                assert resultSet != null;
                resultSet.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return products;
    }

    //get product names
    public static LinkedHashSet<String> getProductName(@NotNull String tableName,@NotNull  String category,@NotNull  String subCategory) {
        LinkedHashSet<String> hashSet = new LinkedHashSet<>();

        try {
            String query = "select name from " +
                    tableName + " where category = ? and subcategory = ?";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, category);
            preparedStatement.setString(2, subCategory);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                hashSet.add(resultSet.getString("name"));
            }
            return hashSet;
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
                assert resultSet != null;
                resultSet.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return hashSet;
    }

    //get products from db
    public static ObservableList<Product> getProductList(@NotNull String tableName) {
        ObservableList<Product> products = FXCollections.observableArrayList();

        try {
            preparedStatement = DatabaseHandler.getInstance().getConnection().
                    prepareStatement("SELECT * FROM " + tableName + " where true");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                p = new Product(
                        resultSet.getString("category")
                        , resultSet.getString("subcategory")
                        , resultSet.getString("name")
                        , resultSet.getString("partno")
                        , resultSet.getString("quantity")
                        , resultSet.getString("mrp")
                        , resultSet.getString("hsncode")
                        , resultSet.getString("place")
                        , resultSet.getString("remarks"), tableName);
                p.setMin(resultSet.getString("min"));
                products.add(p);

            }
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
                assert resultSet != null;
                resultSet.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return products;
    }

    //Search through Products
    public static ObservableList<Product>  getProductList(@NotNull String tableName, @NotNull String searchKeyword) {
        ObservableList<Product> products = FXCollections.observableArrayList();

        try {
            preparedStatement = DatabaseHandler.getInstance().getConnection().
                    prepareStatement("SELECT * FROM " + tableName + " where category LIKE ? " +
                            " OR SUBCATEGORY LIKE ? " +
                            " OR NAME LIKE ? " +
                            " OR PARTNO LIKE ? " +
                            " OR MRP LIKE ? " +
                            " OR QUANTITY LIKE ? " +
                            " OR HSNCODE LIKE ? " +
                            " OR PLACE LIKE  ? " +
                            " OR REMARKS LIKE ? ");
            preparedStatement.setString(1, searchKeyword);
            preparedStatement.setString(2, searchKeyword);
            preparedStatement.setString(3, searchKeyword);
            preparedStatement.setString(4, searchKeyword);
            preparedStatement.setString(5, searchKeyword);
            preparedStatement.setString(6, searchKeyword);
            preparedStatement.setString(7, searchKeyword);
            preparedStatement.setString(8, searchKeyword);
            preparedStatement.setString(9, searchKeyword);

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                p = new Product(
                        resultSet.getString("category")
                        , resultSet.getString("subcategory")
                        , resultSet.getString("name")
                        , resultSet.getString("partno")
                        , resultSet.getString("quantity")
                        , resultSet.getString("mrp")
                        , resultSet.getString("hsncode")
                        , resultSet.getString("place")
                        , resultSet.getString("remarks"), tableName);
                p.setMin(resultSet.getString("min"));

                products.add(p);

            }
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
                assert resultSet != null;
                resultSet.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return products;
    }

    //get Products based on category
    public static ObservableList<Product> getCategoryProductList(@NotNull String tableName,@NotNull  String searchKeyword) {
        ObservableList<Product> products = FXCollections.observableArrayList();

        try {
            preparedStatement = DatabaseHandler.getInstance().getConnection().
                    prepareStatement("SELECT * FROM " + tableName + " where category LIKE ?");
            preparedStatement.setString(1, searchKeyword);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                p = new Product(
                        resultSet.getString("category")
                        , resultSet.getString("subcategory")
                        , resultSet.getString("name")
                        , resultSet.getString("partno")
                        , resultSet.getString("quantity")
                        , resultSet.getString("mrp")
                        , resultSet.getString("hsncode")
                        , resultSet.getString("place")
                        , resultSet.getString("remarks"), tableName);
                p.setMin(resultSet.getString("min"));

                products.add(p);

            }
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
                assert resultSet != null;
                resultSet.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return products;
    }

    //get Products based on category and Sub-Category
    public static ObservableList<Product> getCSubProductList(@NotNull String tableName
            ,@NotNull  String category
            ,@NotNull  String subCategory) {
        ObservableList<Product> products = FXCollections.observableArrayList();

        try {
            preparedStatement = DatabaseHandler.getInstance().getConnection().
                    prepareStatement("SELECT * FROM " + tableName + " WHERE category LIKE ? " +
                            " AND SUBCATEGORY LIKE ? ");
            preparedStatement.setString(1, category);
            preparedStatement.setString(2, subCategory);

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                p = new Product(
                        resultSet.getString("category")
                        , resultSet.getString("subcategory")
                        , resultSet.getString("name")
                        , resultSet.getString("partno")
                        , resultSet.getString("quantity")
                        , resultSet.getString("mrp")
                        , resultSet.getString("hsncode")
                        , resultSet.getString("place")
                        , resultSet.getString("remarks"), tableName);
                p.setMin(resultSet.getString("min"));

                products.add(p);

            }
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
                assert resultSet != null;
                resultSet.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return products;
    }

    //log
    // create Log Table
    private static void createLogTable() {
        okay = false;
        String query = "CREATE TABLE IF NOT EXISTS Log ( brand TEXT NOT NULL," +
                " category TEXT NOT NULL, subcategory TEXT NOT NULL, name TEXT " +
                "NOT NULL, date TEXT NOT NULL, entry TEXT NOT NULL, received " +
                "TEXT NOT NULL, issued TEXT NOT NULL,  balance TEXT NOT NULL" +
                ", user TEXT NOT NULL, action NOT NULL , remarks TEXT NOT NULL)";
        okay = createTable(query);
    }

    //insert new log
    public static boolean insertNewLog(@NotNull Log log) {
        okay = false;
        try {
            createLogTable();
            String insert = "INSERT INTO Log ( category , subcategory ," +
                    " name , date , entry , received , issued , balance " +
                    ", user , brand , action , remarks) values" +
                    " (?,?,?,?,?,?,?,?,?,?,?,?)";
            preparedStatement = DatabaseHandler.getInstance()
                    .getConnection().prepareStatement(insert);
            preparedStatement.setString(1, log.getCategory());
            preparedStatement.setString(2, log.getSubCategory());
            preparedStatement.setString(3, log.getName());
            preparedStatement.setString(4, log.getDate());
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
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
                assert resultSet != null;
                resultSet.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return okay;
    }

    public static ObservableList<Log> getAllLogList() {
        ObservableList<Log> logs = FXCollections.observableArrayList();
        try {
            String query = "SELECT * FROM LOG WHERE TRUE";
            preparedStatement = DatabaseHandler.getInstance()
                    .getConnection().prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long l = Long.parseLong(resultSet.getString("date"));
                logs.add(new Log(resultSet.getString("brand")
                        , StockManagementUtils.formatDateTimeString(l)
                        , resultSet.getString("category")
                        , resultSet.getString("subcategory")
                        , resultSet.getString("name")
                        , resultSet.getString("entry")
                        , resultSet.getString("received")
                        , resultSet.getString("issued")
                        , resultSet.getString("balance")
                        , resultSet.getString("user")
                        , resultSet.getString("action")
                        , resultSet.getString("remarks")));
            }
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
                assert resultSet != null;
                resultSet.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return logs;
    }

    public static boolean deleteLogs(@NotNull ObservableList<Log> logs) {
        okay = false;
        try {

            String insert = "DELETE FROM Log where category = ? and subcategory = ? and name = ? " +
                    "and entry = ? and received = ? and issued = ? and" +
                    " balance = ? and user = ? and action = ? and remarks = ?";
            for (Log log : logs) {
                preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(insert);
                preparedStatement.setString(1, log.getCategory());
                preparedStatement.setString(2, log.getSubCategory());
                preparedStatement.setString(3, log.getName());
                preparedStatement.setString(4, log.getEntry());
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
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
                assert resultSet != null;
                resultSet.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return okay;

    }

    public static ObservableList<Log> getLogListBrand(@NotNull String br) {
        ObservableList<Log> logs = FXCollections.observableArrayList();

        try {
            String query = "SELECT * FROM LOG WHERE BRAND = ?";
            preparedStatement = DatabaseHandler.getInstance()
                    .getConnection().prepareStatement(query);
            preparedStatement.setString(1, br);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long l = Long.parseLong(resultSet.getString("date"));
                String dat = StockManagementUtils.formatDateTimeString(l);
                logs.add(new Log(resultSet.getString("brand")
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
                        , resultSet.getString("remarks")));
            }
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
                assert resultSet != null;
                resultSet.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return logs;
    }

    public static ObservableList<Log> getLogListBrandWithDate(@NotNull String br, @NotNull LocalDate a,@NotNull  LocalDate b) {
        ObservableList<Log> logs = FXCollections.observableArrayList();

        try {
            String query = "SELECT * FROM LOG WHERE BRAND = ?";
            preparedStatement = DatabaseHandler.getInstance()
                    .getConnection().prepareStatement(query);
            preparedStatement.setString(1, br);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long l = Long.parseLong(resultSet.getString("date"));
                String dat = StockManagementUtils.formatDateTimeString(l);
                Date date = new Date(l);
                LocalDate d = convertToLocalDateViaInstant(date);
                if ((d.isEqual(a) || d.isEqual(b)) || (d.isAfter(a) && d.isBefore(b))) {
                    logs.add(new Log(resultSet.getString("brand")
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
                            , resultSet.getString("remarks")));

                }
            }
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
                assert resultSet != null;
                resultSet.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return logs;

    }

    public static ObservableList<Log> getLogListCategory(@NotNull String br,@NotNull  String c) {
        ObservableList<Log> logs = FXCollections.observableArrayList();
        try {
            String query = "SELECT * FROM LOG WHERE CATEGORY = ? AND BRAND = ? ";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, c);
            preparedStatement.setString(2, br);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long l = Long.parseLong(resultSet.getString("date"));
                String dat = StockManagementUtils.formatDateTimeString(l);
                logs.add(new Log(resultSet.getString("brand")
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
                        , resultSet.getString("remarks")));

            }
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
                assert resultSet != null;
                resultSet.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return logs;

    }

    public static ObservableList<Log> getLogListCategoryWithDate(@NotNull String brand, @NotNull String c
            , LocalDate a, LocalDate b) {
        ObservableList<Log> logs = FXCollections.observableArrayList();
        try {
            String query = "SELECT * FROM LOG WHERE CATEGORY = ? && BRAND = ?? ";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, c);
            preparedStatement.setString(2, brand);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long l = Long.parseLong(resultSet.getString("date"));
                String dat;
                Date date = new Date(l);
                LocalDate d = convertToLocalDateViaInstant(date);
                if ((d.isEqual(a) || d.isEqual(b)) || (d.isAfter(a) && d.isBefore(b))) {

                    l = Long.parseLong(resultSet.getString("date"));
                    dat = StockManagementUtils.formatDateTimeString(l);
                    logs.add(new Log(resultSet.getString("brand")
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
                            , resultSet.getString("remarks")));

                }

            }
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
                assert resultSet != null;
                resultSet.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return logs;

    }

    public static ObservableList<Log> getLogListSubCategory(@NotNull String b, @NotNull String c, @NotNull String s) {
        ObservableList<Log> logs = FXCollections.observableArrayList();

        try {
            String query = "SELECT * FROM LOG WHERE CATEGORY = ? AND SUBCATEGORY = ? AND BRAND = ?";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, c);
            preparedStatement.setString(2, s);
            preparedStatement.setString(3, b);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long l = Long.parseLong(resultSet.getString("date"));
                String dat = StockManagementUtils.formatDateTimeString(l);
                logs.add(new Log(resultSet.getString("brand")
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
                        , resultSet.getString("remarks")));

            }
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
                assert resultSet != null;
                resultSet.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return logs;

    }

    public static ObservableList<Log> getLogListSubCategoryWithDate(@NotNull String br
            , @NotNull String c, @NotNull String s, @NotNull LocalDate a,@NotNull  LocalDate b) {
        ObservableList<Log> logs = FXCollections.observableArrayList();

        try {
            String query = "SELECT * FROM LOG WHERE CATEGORY = ? AND SUBCATEGORY = ? AND BRAND = ?";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, c);
            preparedStatement.setString(2, s);
            preparedStatement.setString(3, br);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long l = Long.parseLong(resultSet.getString("date"));
                String dat;
                Date date = new Date(l);
                LocalDate d = convertToLocalDateViaInstant(date);
                if ((d.isEqual(a) || d.isEqual(b)) || (d.isAfter(a) && d.isBefore(b))) {

                    l = Long.parseLong(resultSet.getString("date"));
                    dat = StockManagementUtils.formatDateTimeString(l);
                    logs.add(new Log(resultSet.getString("brand")
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
                            , resultSet.getString("remarks")));

                }

            }
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
                assert resultSet != null;
                resultSet.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return logs;

    }

    public static ObservableList<Log> getLogListName(@NotNull String b, @NotNull String c, @NotNull String s,@NotNull  String n) {
        ObservableList<Log> logs = FXCollections.observableArrayList();

        try {
            String query = "SELECT * FROM LOG WHERE CATEGORY = ? AND " +
                    "SUBCATEGORY = ? AND NAME = ? AND BRAND = ?";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, c);
            preparedStatement.setString(2, s);
            preparedStatement.setString(3, n);
            preparedStatement.setString(4, b);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long l = Long.parseLong(resultSet.getString("date"));
                String dat = StockManagementUtils.formatDateTimeString(l);
                logs.add(new Log(resultSet.getString("brand")
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
                        , resultSet.getString("remarks")));

            }
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
                assert resultSet != null;
                resultSet.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return logs;

    }

    public static ObservableList<Log> getLogListNameWithDate(@NotNull String c,@NotNull  String s,@NotNull  String n
            ,@NotNull  LocalDate a,@NotNull  LocalDate b) {
        ObservableList<Log> logs = FXCollections.observableArrayList();

        try {
            String query = "SELECT * FROM LOG WHERE CATEGORY = ? AND SUBCATEGORY = ? AND NAME = ?";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, c);
            preparedStatement.setString(2, s);
            preparedStatement.setString(3, n);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {

                long l = Long.parseLong(resultSet.getString("date"));
                String dat;
                Date date = new Date(l);
                LocalDate d = convertToLocalDateViaInstant(date);
                if ((d.isEqual(a) || d.isEqual(b)) || (d.isAfter(a) && d.isBefore(b))) {

                    l = Long.parseLong(resultSet.getString("date"));
                    dat = StockManagementUtils.formatDateTimeString(l);
                    logs.add(new Log(resultSet.getString("brand")
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
                            , resultSet.getString("remarks")));

                }
            }
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
                assert resultSet != null;
                resultSet.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return logs;

    }

    public static ObservableList<Log> getLogListDateOnly(@NotNull LocalDate a,@NotNull  LocalDate b) {
        ObservableList<Log> logs = FXCollections.observableArrayList();

        try {
            String query = "SELECT * FROM LOG WHERE TRUE";
            preparedStatement = DatabaseHandler.getInstance()
                    .getConnection().prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long l = Long.parseLong(resultSet.getString("date"));
                String dat = StockManagementUtils.formatDateTimeString(l);
                Date date = new Date(l);
                LocalDate d = convertToLocalDateViaInstant(date);
                if ((d.isEqual(a) || d.isEqual(b)) || (d.isAfter(a) && d.isBefore(b))) {
                    logs.add(new Log(resultSet.getString("brand")
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
                            , resultSet.getString("remarks")));

                }
            }
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
                assert resultSet != null;
                resultSet.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return logs;


    }

    private static LocalDate convertToLocalDateViaInstant(@NotNull Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    //getCategories
    public static LinkedHashSet<String> getCategories(@NotNull String tableName) {
        LinkedHashSet<String> hashSet = new LinkedHashSet<>();
        try {
            String query = "select distinct category from " + tableName;
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                hashSet.add(resultSet.getString("category"));
            }
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
                assert resultSet != null;
                resultSet.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return hashSet;
    }

    //get sub categories
    public static LinkedHashSet<String> getSubCategory(@NotNull String tableName, @NotNull String category) {
        LinkedHashSet<String> hashSet = new LinkedHashSet<>();
        try {
            String query = "select distinct subcategory from " + tableName + " where category = ? ";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, category);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                hashSet.add(resultSet.getString("subcategory"));
            }

            return hashSet;
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
                assert resultSet != null;
                resultSet.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return hashSet;
    }

    public static boolean changeCategoryOrSubC(@NotNull String oldValue,@NotNull  String newValue,@NotNull  String tableName
            ,  boolean i) {
        okay = false;
        String insert = " update " + tableName;
        try {
            if (i) {
                insert = insert + " set category = ? where category = ?";
            } else {
                insert = insert + " set subcategory = ? where subcategory = ?";
            }
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(insert);
            preparedStatement.setString(1, newValue);
            preparedStatement.setString(2, oldValue);
            okay = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            AlertMaker.showErrorMessage(e);
            e.printStackTrace();
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

        return okay;
    }

    public static boolean changeName(@NotNull String oldValue,@NotNull  String newValue
            ,@NotNull  String c,@NotNull  String s,@NotNull  String tableName
    ) {
        okay = false;
        try {

            String insert = " update " + tableName +
                    " set name = ? where category = ? and subcategory = ? and name = ? ";

            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(insert);
            preparedStatement.setString(1, newValue);
            preparedStatement.setString(2, c);
            preparedStatement.setString(3, s);
            preparedStatement.setString(4, oldValue);

            okay = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            AlertMaker.showErrorMessage(e);
            e.printStackTrace();
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }

        return okay;
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

    //insert new user
    public static boolean insertNewUser(@NotNull User user) {
        okay = false;
        try {
            String query = "insert or ignore into employee ( name , id , password , access , username )  values ( ? , ? , ? , ? , ? ) ";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getId());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setString(4, user.getAccess());
            preparedStatement.setString(5, user.getUserName());
            okay = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return okay;
    }

    //update user
    public static boolean updateUser(@NotNull User user) {
        okay = false;
        try {
            String query = "update employee set name = ?,password = ?,access =" +
                    " ?,username = ? where id = ?";

            preparedStatement = DatabaseHandler.getInstance()
                    .getConnection().prepareStatement(query);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getAccess());
            preparedStatement.setString(4, user.getUserName());
            preparedStatement.setString(5, user.getId());
            okay = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
                assert resultSet != null;
                resultSet.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return okay;
    }

    //get user info
    public static User getUserInfo(@NotNull String userName) {
        User u = null;
        try {
            String query = "select * from employee where username = ?";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, userName);
            resultSet = preparedStatement.executeQuery();
            u = new User(resultSet.getString("name")
                    , resultSet.getString("username")
                    , resultSet.getString("id")
                    , resultSet.getString("password")
                    , resultSet.getString("access"));
        } catch (SQLException e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
                assert resultSet != null;
                resultSet.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return u;
    }

    //delete user
    public static boolean deleteUser(@NotNull User user) {
        okay = false;
        try {
            String insert = "DELETE FROM EMPLOYEE where name = ? and username = ? and id = ? ";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(insert);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getUserName());
            preparedStatement.setString(3, user.getId());
            okay = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return okay;
    }

    //validate user
    public static boolean valid(@NotNull String user,@NotNull  String password) throws SQLException {
        okay = false;
        try {
            String query = "select * from Employee where username = ? and password = ?";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, user);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                okay = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assert preparedStatement != null;
            preparedStatement.close();
            assert resultSet != null;
            resultSet.close();
        }
        return okay;
    }

    //getUser list
    public static ObservableList<User> getUserList() {
        ObservableList<User> users = FXCollections.observableArrayList();
        try {
            String query = "SELECT * FROM EMPLOYEE WHERE TRUE";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                users.add(new User(resultSet.getString("name")
                        , resultSet.getString("username")
                        , resultSet.getString("id")
                        , resultSet.getString("password")
                        , resultSet.getString("access")));
            }
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
                assert resultSet != null;
                resultSet.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return users;
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
        } finally {
            try {
                assert preparedStatement != null;
                preparedStatement.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        return okay;
    }

    // get products from excel
    private static ArrayList<Product> getProducts(@NotNull Sheet sheet) {
        ArrayList<Product> products = new ArrayList<>();
        String c = "", s = "", n = "", pa = "", pl = ""
                , h = "", m = "0", q = "0", r = "", min = "0";
        try {
            int i = 0;
            for (Row currentRow : sheet) {
                if (i == 0) {
                    i++;
                    continue;
                }
                if (currentRow.getCell(0) != null) {
                    currentRow.getCell(0).setCellType(CellType.STRING);
                    c = currentRow.getCell(0).getStringCellValue();
                }
                if (currentRow.getCell(1) != null) {
                    currentRow.getCell(1).setCellType(CellType.STRING);
                    s = currentRow.getCell(1).getStringCellValue();
                }
                if (currentRow.getCell(2) != null) {
                    currentRow.getCell(2).setCellType(CellType.STRING);
                    n = currentRow.getCell(2).getStringCellValue();
                }
                if (currentRow.getCell(3) != null) {
                    currentRow.getCell(3).setCellType(CellType.STRING);
                    pa = currentRow.getCell(3).getStringCellValue();
                }
                if (currentRow.getCell(4) != null) {
                    if (currentRow.getCell(4).getCellType() == CellType.STRING)
                        q = "" + currentRow.getCell(4).getStringCellValue();
                    if (currentRow.getCell(4).getCellType() == CellType.NUMERIC)
                        q = "" + (int) currentRow.getCell(4).getNumericCellValue();
                }
                if (currentRow.getCell(5) != null) {
                    if (currentRow.getCell(5).getCellType() == CellType.STRING)
                        m = "" + currentRow.getCell(5).getStringCellValue();
                    if (currentRow.getCell(5).getCellType() == CellType.NUMERIC)
                        m = "" + (int) currentRow.getCell(5).getNumericCellValue();
                }
                if (currentRow.getCell(6) != null) {
                    if (currentRow.getCell(6).getCellType() == CellType.STRING)
                        h = "" + currentRow.getCell(6).getStringCellValue();
                    if (currentRow.getCell(6).getCellType() == CellType.NUMERIC)
                        h = "" + (int) currentRow.getCell(6).getNumericCellValue();
                }
                if (currentRow.getCell(7) != null) {
                    currentRow.getCell(7).setCellType(CellType.STRING);
                    pl = currentRow.getCell(7).getStringCellValue();
                }
                if (currentRow.getCell(8) != null) {
                    currentRow.getCell(8).setCellType(CellType.STRING);
                    r = currentRow.getCell(8).getStringCellValue();
                }
                if (currentRow.getCell(9) != null) {
                    currentRow.getCell(9).setCellType(CellType.STRING);
                    min = currentRow.getCell(9).getStringCellValue();
                }
                if (currentRow.getCell(0).getStringCellValue().isEmpty() ||
                        currentRow.getCell(1).getStringCellValue().isEmpty() ||
                        currentRow.getCell(2).getStringCellValue().isEmpty()) {
                    continue;
                }

                Product p = new Product(c, s, n, pa, q, m, h, pl, r, sheet.getSheetName());
                p.setMin(min);
                products.add(p);
            }
            return products;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    // EXCEL TO SQLITE
    // PRODUCTS FROM EXCEL TO SQLITE
    public static boolean excelTOSQLite() {
        okay = false;
        String fileName = Preferences.getPreferences()
                .getPath();
        FileInputStream excelFile = null;
        Workbook workbook = null;
        try {
            excelFile = new FileInputStream(new
                    File(fileName));
            workbook = new XSSFWorkbook(excelFile);
            int n = workbook.getNumberOfSheets();
            ArrayList<Product> products;
            Sheet sheet;
            for (int i = 0; i < n; i++) {
                sheet = workbook.getSheetAt(i);
                String name = sheet.getSheetName();
                products = getProducts(sheet);
                createProductTable(name);
                for (Product pa : products) {
                    if (!isProductExist(pa, name))
                        insertNewProduct(pa, name);
                    else
                        updateProduct(pa, name);
                }
            }
            okay = true;
        } catch (Exception e) {
            okay = false;
            e.printStackTrace();
        } finally {
            assert excelFile != null;
            try {
                excelFile.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            assert workbook != null;
            try {
                workbook.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return okay;
    }

    // SQLITE to Excel
    public static boolean productSQLToExcel(@NotNull File dest) {
        okay = false;
        ObservableList<Product> products;
        Set<String> tableNames =
                Preferences.getPreferences().getTableNames();
        FileInputStream excel = null;
        XSSFWorkbook workbook = null;
        try {
            String FILE_NAME = dest.getAbsolutePath();
            XSSFSheet sheet;

            try {
                excel = new FileInputStream(dest);
                workbook = new XSSFWorkbook(excel);
            } catch (Exception e) {
                workbook = new XSSFWorkbook();
            }

            for (String s : tableNames) {
                try {
                    sheet = workbook.createSheet(s);
                } catch (Exception e) {
                    sheet = workbook.getSheet(s);
                }
                products = getProductList(s);
                int rowNum = 0;
                Row row;
                row = sheet.createRow(rowNum);
                rowNum++;
                row.createCell(0).setCellValue("Category");
                row.createCell(1).setCellValue("SubCategory");
                row.createCell(2).setCellValue("Name");
                row.createCell(3).setCellValue("PartNo");
                row.createCell(4).setCellValue("QTY");
                row.createCell(5).setCellValue("MRP");
                row.createCell(6).setCellValue("HSNCode");
                row.createCell(7).setCellValue("Place");
                row.createCell(8).setCellValue("Remarks");
                row.createCell(9).setCellValue("Minimum");

                for (Product p : products) {
                    row = sheet.createRow(rowNum);
                    rowNum++;
                    row.createCell(0).setCellValue(p.getCategory());
                    row.createCell(1).setCellValue(p.getSubCategory());
                    row.createCell(2).setCellValue(p.getName());
                    row.createCell(3).setCellValue(p.getPartNo());
                    row.createCell(4).setCellValue(p.getQTY());
                    row.createCell(5).setCellValue(p.getMRP());
                    row.createCell(6).setCellValue(p.getHsnCode());
                    row.createCell(7).setCellValue(p.getPlace());
                    row.createCell(8).setCellValue(p.getRemarks());
                    row.createCell(9).setCellValue(p.getMin());

                }
            }
            FileOutputStream outputStream = new FileOutputStream(FILE_NAME);
            workbook.write(outputStream);
            okay = true;
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
            e.printStackTrace();
        } finally {
            if (excel != null) {
                try {
                    excel.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            try {
                assert workbook != null;
                workbook.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return okay;
    }

    public static boolean needProductSQLToExcel(@NotNull File dest,@NotNull  Set<String> tableNames) {
        okay = false;
        FileInputStream excel = null;
        XSSFWorkbook workbook = null;

        ObservableList<Product> products;
        try {
            String FILE_NAME = dest.getAbsolutePath();
            XSSFSheet sheet;

            try {
                excel = new FileInputStream(dest);
                workbook = new XSSFWorkbook(excel);
            } catch (Exception e) {
                workbook = new XSSFWorkbook();
            }

            for (String s : tableNames) {
                products = getNeededProductList(s);
                if (products.size() == 0) continue;
                try {
                    sheet = workbook.createSheet(s);
                } catch (Exception e) {
                    sheet = workbook.getSheet(s);
                }
                int rowNum = 0;
                Row row;
                row = sheet.createRow(rowNum);
                rowNum++;
                row.createCell(0).setCellValue("Brand");
                row.createCell(1).setCellValue("Name");
                row.createCell(2).setCellValue("QTY");
                row.createCell(3).setCellValue("Min QTY");
                row.createCell(4).setCellValue("Part NO");
                for (Product p : products) {
                    row = sheet.createRow(rowNum);
                    rowNum++;
                    row.createCell(0).setCellValue(s);
                    row.createCell(1).setCellValue(p.getName());
                    row.createCell(2).setCellValue(p.getQTY());
                    row.createCell(3).setCellValue(p.getMin());
                    row.createCell(4).setCellValue(p.getPartNo());
                }
            }

            FileOutputStream outputStream = new FileOutputStream(FILE_NAME);
            workbook.write(outputStream);
            okay = true;
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
            e.printStackTrace();
        } finally {
            assert excel != null;
            try {
                excel.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            assert workbook != null;
            try {
                workbook.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return okay;
    }

    public static boolean allLogsFromSqlToExcel(@NotNull File dest,@NotNull  ObservableList<Log> logs) {
        okay = false;
        try {
            String FILE_NAME = dest.getAbsolutePath();
            FileInputStream excel;
            XSSFWorkbook workbook;
            XSSFSheet sheet;

            try {
                excel = new FileInputStream(dest);
                workbook = new XSSFWorkbook(excel);
            } catch (Exception e) {
                workbook = new XSSFWorkbook();
            }
            try {
                sheet = workbook.createSheet("History");
            } catch (Exception e) {
                sheet = workbook.getSheet("History");
            }
            int rowNum = 0;
            Row row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue("Brand");
            row.createCell(1).setCellValue("Name");
            row.createCell(2).setCellValue("Date");
            row.createCell(3).setCellValue("Entry");
            row.createCell(4).setCellValue("Received");
            row.createCell(5).setCellValue("Issued");
            row.createCell(6).setCellValue("Balance");
            row.createCell(7).setCellValue("Action");
            rowNum++;
            for (Log l : logs) {
                row = sheet.createRow(rowNum);
                row.createCell(0).setCellValue(l.getBrand());
                row.createCell(1).setCellValue(l.getName());
                row.createCell(2).setCellValue(l.getDate());
                row.createCell(3).setCellValue(l.getEntry());
                row.createCell(4).setCellValue(l.getReceived());
                row.createCell(5).setCellValue(l.getIssued());
                row.createCell(6).setCellValue(l.getBalance());
                row.createCell(7).setCellValue(l.getAction());

                rowNum++;
            }
            FileOutputStream outputStream = new FileOutputStream(FILE_NAME);
            workbook.write(outputStream);
            outputStream.close();
            workbook.close();
            okay = true;
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
        }
        return okay;
    }

}
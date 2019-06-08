package sample.Database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jetbrains.annotations.NotNull;
import sample.Alert.AlertMaker;
import sample.model.Product;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;

public class DatabaseHelper_Product {


    /**
     * INSERT PRODUCT
     */
    public static boolean insertNewProduct(@NotNull Product product, @NotNull String tableName) {
        boolean result = false;
        PreparedStatement preparedStatement;
        try {
            String insert = "insert into " + tableName +
                    " ( category , subcategory ,name , partno " +
                    ", quantity , mrp , hsncode , place , remarks,min )" +
                    " values (?,?,?,?,?,?,?,?,?,?)";
            preparedStatement = getPreparedStatementForProduct(insert, product);
            result = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    //update product
    public static boolean updateProduct(@NotNull Product product, @NotNull String tableName) {
        boolean result = false;
        PreparedStatement preparedStatement;
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
            result = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            AlertMaker.showErrorMessage(e);
            e.printStackTrace();
        }
        return result;
    }

    //delete product
    public static boolean deleteProduct(@NotNull Product product, @NotNull String tableName) {
        boolean okay = false;
        PreparedStatement preparedStatement;
        try {
            String insert = "DELETE FROM " + tableName + " where category = ? and subcategory = ? and name = ? ";
            preparedStatement = getPreparedStatementWithThreeParams(insert, product);
            okay = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return okay;
    }

    //is product exist
    static boolean isProductExist(@NotNull Product product, @NotNull String tableName) {
        boolean okay = false;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        try {
            String query = " select * from " + tableName + " " +
                    " where category = ? and subcategory = ? and name = ? and  partno = ? and" +
                    " quantity = ?  and mrp = ?  and hsncode = ?  and place = ? " +
                    " and remarks = ? and min  = ? ";
            preparedStatement = getPreparedStatementForProduct(query, product);
            resultSet = preparedStatement.executeQuery();
            okay = resultSet.next();
        } catch (SQLException e) {
            AlertMaker.showErrorMessage(e);
            e.printStackTrace();
        }
        return okay;
    }

    public static boolean isProductExistS(@NotNull Product product, @NotNull String tableName) {
        boolean okay = false;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        try {
            String query = " select * from " + tableName + " " +
                    " where category = ? and subcategory = ? and name = ?  ";
            preparedStatement = getPreparedStatementWithThreeParams(query, product);
            resultSet = preparedStatement.executeQuery();
            okay = resultSet.next();
        } catch (SQLException e) {
            AlertMaker.showErrorMessage(e);
            e.printStackTrace();
        }
        return okay;
    }

    //get product info
    public static Product getProductInfo(@NotNull Product product, @NotNull String tableName) {
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Product p = null;
        try {
            String insert = " select * from " + tableName + " where category = ? and subcategory = ? and name = ?";
            preparedStatement = getPreparedStatementWithThreeParams(insert, product);
            resultSet = preparedStatement.executeQuery();
            p = getProduct(resultSet, tableName);
        } catch (SQLException e) {
            AlertMaker.showErrorMessage(e);
            e.printStackTrace();
        }
        return p;
    }

    //get neededProduct Info
    public static ObservableList<Product> getNeededProductList(@NotNull String tableName) {
        ObservableList<Product> products = FXCollections.observableArrayList();
        boolean b;
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        Product p;

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
                    p = getProduct(resultSet, tableName);
                    assert p != null;
                    p.setMin(resultSet.getString("min"));
                    products.add(p);
                }
            }
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
            e.printStackTrace();
        }
        return products;
    }

    //get products from db
    public static ObservableList<Product> getProductList(@NotNull String tableName) {
        PreparedStatement preparedStatement;
        ResultSet resultSet = null;
        try {
            preparedStatement = DatabaseHandler.getInstance().getConnection().
                    prepareStatement("SELECT * FROM " + tableName + " where true");
            resultSet = preparedStatement.executeQuery();
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
            e.printStackTrace();
        }
        assert resultSet != null;
        return getProductList(tableName, resultSet);
    }

    private static ObservableList<Product> getProductList(@NotNull String tableName, ResultSet resultSet) {

        ObservableList<Product> products = FXCollections.observableArrayList();

        Product p;

        try {
            while (resultSet.next()) {
                p = getProduct(resultSet, tableName);
                assert p != null;
                p.setMin(resultSet.getString("min"));
                products.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    //Search through Products
    public static ObservableList<Product> getProductList(@NotNull String tableName, @NotNull String searchKeyword) {
        PreparedStatement preparedStatement;
        ResultSet resultSet = null;

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
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
            e.printStackTrace();
        }

        assert resultSet != null;
        return getProductList(tableName, resultSet);
    }

    //get Products based on category
    public static ObservableList<Product> getCategoryProductList(@NotNull String tableName, @NotNull String searchKeyword) {
        PreparedStatement preparedStatement;
        ResultSet resultSet = null;

        try {
            preparedStatement = DatabaseHandler.getInstance().getConnection().
                    prepareStatement("SELECT * FROM " + tableName + " where category LIKE ?");
            preparedStatement.setString(1, searchKeyword);
            resultSet = preparedStatement.executeQuery();
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
            e.printStackTrace();
        }
        assert resultSet != null;
        return getProductList(tableName, resultSet);
    }

    //get Products based on category and Sub-Category
    public static ObservableList<Product> getCSubProductList(@NotNull String tableName
            , @NotNull String category, @NotNull String subCategory) {
        PreparedStatement preparedStatement;
        ResultSet resultSet = null;

        try {
            preparedStatement = DatabaseHandler.getInstance().getConnection().
                    prepareStatement("SELECT * FROM " + tableName + " WHERE category LIKE ? " + " AND SUBCATEGORY LIKE ? ");
            preparedStatement.setString(1, category);
            preparedStatement.setString(2, subCategory);

            resultSet = preparedStatement.executeQuery();
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
            e.printStackTrace();
        }
        assert resultSet != null;
        return getProductList(tableName, resultSet);

    }

    public static boolean changeCategoryOrSubC(@NotNull String oldValue, @NotNull String newValue
            , @NotNull String tableName, boolean i) {
        boolean okay = false;
        PreparedStatement preparedStatement;
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
        }

        return okay;
    }

    public static boolean changeName(@NotNull String oldValue, @NotNull String newValue
            , @NotNull String c, @NotNull String s, @NotNull String tableName) {
        boolean okay = false;
        PreparedStatement preparedStatement;
        try {

            String insert = " update " + tableName +
                    " set name = ? where category = ? and subcategory = ? and name = ? ";
            preparedStatement = DatabaseHelper_Log.getPreparedStatement(insert, newValue, c, s);
            preparedStatement.setString(4, oldValue);

            okay = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            AlertMaker.showErrorMessage(e);
            e.printStackTrace();
        }

        return okay;
    }

    //getCategories
    public static LinkedHashSet<String> getCategories(@NotNull String tableName) {
        PreparedStatement preparedStatement = null;
        try {
            String query = "select distinct category from " + tableName;
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
            e.printStackTrace();
        }
        return DatabaseHelper.getStrings(preparedStatement, "category");
    }

    //get sub categories
    public static LinkedHashSet<String> getSubCategory(@NotNull String tableName, @NotNull String category) {
        PreparedStatement preparedStatement = null;
        try {
            String query = "select distinct subcategory from " + tableName + " where category = ? ";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, category);
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
            e.printStackTrace();
        }
        return DatabaseHelper.getStrings(preparedStatement, "subcategory");
    }

    //get product names
    public static LinkedHashSet<String> getProductName(@NotNull String tableName, @NotNull String category, @NotNull String subCategory) {
        PreparedStatement preparedStatement = null;
        try {
            String query = "select name from " +
                    tableName + " where category = ? and subcategory = ?";
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, category);
            preparedStatement.setString(2, subCategory);
        } catch (Exception e) {
            AlertMaker.showErrorMessage(e);
            e.printStackTrace();
        }
        return DatabaseHelper.getStrings(preparedStatement, "name");
    }

    private static PreparedStatement getPreparedStatementForProduct(String query, Product product) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = getPreparedStatementWithThreeParams(query, product);
            preparedStatement.setString(4, product.getPartNo());
            preparedStatement.setString(5, product.getQTY());
            preparedStatement.setString(6, product.getMRP());
            preparedStatement.setString(7, product.getHsnCode());
            preparedStatement.setString(8, product.getPlace());
            preparedStatement.setString(9, product.getRemarks());
            preparedStatement.setString(10, product.getMin());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return preparedStatement;
    }

    private static PreparedStatement getPreparedStatementWithThreeParams(String query, Product product) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = DatabaseHandler.getInstance().getConnection().prepareStatement(query);
            preparedStatement.setString(1, product.getCategory());
            preparedStatement.setString(2, product.getSubCategory());
            preparedStatement.setString(3, product.getName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return preparedStatement;
    }

    private static Product getProduct(ResultSet resultSet, String tableName) {
        try {
            return new Product(
                    resultSet.getString("category")
                    , resultSet.getString("subcategory")
                    , resultSet.getString("name")
                    , resultSet.getString("partno")
                    , resultSet.getString("quantity")
                    , resultSet.getString("mrp")
                    , resultSet.getString("hsncode")
                    , resultSet.getString("place")
                    , resultSet.getString("remarks"), tableName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}

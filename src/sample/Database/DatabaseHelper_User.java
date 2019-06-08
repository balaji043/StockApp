package sample.Database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jetbrains.annotations.NotNull;
import sample.Alert.AlertMaker;
import sample.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseHelper_User {
    private static PreparedStatement preparedStatement = null;
    private static ResultSet resultSet = null;
    private static boolean okay = false;
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
    public static boolean valid(@NotNull String user, @NotNull String password) throws SQLException {
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

}

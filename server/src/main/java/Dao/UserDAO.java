package Dao;

import Model.Person;
import Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserDAO {
    private final Connection conn;

    public UserDAO(Connection conn) { this.conn = conn; }

    /**
     *
     * @param newUser
     *
     * Creates a newUser and adds it to the table of users
     */

    public void addUser(Model.User newUser) throws DataAccessException {
        //We can structure our string to be similar to a sql command, but if we insert question
        //marks we can change them later with help from the statement
        String sql = "INSERT INTO User (userName, password, emailAddress, firstName, lastName, " +
                "gender, personID) VALUES(?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            //Using the statements built-in set(type) functions we can pick the question mark we want
            //to fill in and give it a proper value. The first argument corresponds to the first
            //question mark found in our sql String

            stmt.setString(1, newUser.getUserName());
            stmt.setString(2, newUser.getPassword());
            stmt.setString(3, newUser.getEmail());
            stmt.setString(4, newUser.getFirstName());
            stmt.setString(5, newUser.getLastName());
            stmt.setString(6, newUser.getGender());
            if(newUser.getPersonID() != null) {
                stmt.setString(7, newUser.getPersonID());
            }
            else {
                stmt.setString(7, UUID.randomUUID().toString());
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting into the database");
        }
    }

    public User getUser(String userName) throws DataAccessException {
        User user;
        ResultSet rs = null;
        String sql = "SELECT * FROM User WHERE userName = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userName);
            rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User(rs.getString("userName"), rs.getString("password"),
                        rs.getString("emailAddress"), rs.getString("firstName"), rs.getString("lastName"),
                        rs.getString("gender"), rs.getString("personID"));
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding User");
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
        return null;

    }

    /**
     *Clears all users
     */
    public void clear() throws SQLException {
            PreparedStatement stmt = null;
            try {
                String sql = "delete from User";
                stmt = conn.prepareStatement(sql);

                int count = stmt.executeUpdate();

                // Reset the auto-increment counter so new books start over with an id of 1
                sql = "delete from User where userName = 'user'";
                stmt = conn.prepareStatement(sql);
                stmt.executeUpdate();

                System.out.printf("Deleted %d users\n", count);
            } finally {
                if (stmt != null) {
                    stmt.close();
                }
            }
    }

    /**
     *
     * @param toFind
     * checks to see if a user already exists, to prevent creating duplicate accounts also good for people who are logging in..
     * Used to validate existence of account?
     * @return
     */
    public boolean userExists(User toFind) throws DataAccessException {
        //Checks to see if a userName exists already

        if(toFind == null) {
            return false;
        }
        return getUser(toFind.getUserName()) != null;

    }





}

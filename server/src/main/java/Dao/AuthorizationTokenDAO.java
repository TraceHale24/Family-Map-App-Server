package Dao;

import Model.AuthorizationToken;
import Model.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

public class AuthorizationTokenDAO {
        private final Connection conn;
        public AuthorizationTokenDAO(Connection conn) {this.conn = conn;}
    /**
     *
     * @param token
     * Takes an authorization token and stores it in the auth token table under the users userName
     */
    public void addAuthToken(Model.AuthorizationToken token) throws DataAccessException {
        /*
        Random random = new Random();
        StringBuilder authToken = new StringBuilder();
        Character[] charArray = new Character[] {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z', '1','2','3','4','5','6','7','8','9','0','A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
        //add AuthToken to the table(Can have multiple per user

        for(int i = 0; i < 10; i++) {
            authToken.append(random.nextInt(charArray.length));
        }
         */
        String sql = "INSERT INTO AuthorizationToken (Token, AssociatedUsername) VALUES(?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, token.getTokenID());
            stmt.setString(2, token.getUsername());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting into the database");
        }
    }

    public String getUserName(String token) throws DataAccessException {
        AuthorizationToken token1;
        ResultSet rs = null;
        String sql = "SELECT * FROM AuthorizationToken WHERE Token = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            rs = stmt.executeQuery();
            if (rs.next()) {
                token1 = new AuthorizationToken(rs.getString("Token"), rs.getString("AssociatedUsername"));
                return token1.getUsername();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding person");
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
     *
     * @param token
     * @param userName
     * Takes the users userName and checks to see if in the authToken table, there exists and authToken that matches the token that it is passed
     *
     * @return
     */
    public boolean validate(String token, String userName) throws DataAccessException {
        //Takes in the username and the token and then checks the AuthToken Table to make sure its in there
        AuthorizationToken token1;
        ResultSet rs = null;
        String sql = "SELECT * FROM AuthorizationToken WHERE Token = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            rs = stmt.executeQuery();
            if (rs.next()) {
                token1 = new AuthorizationToken(rs.getString("Token"), rs.getString("AssociatedUsername"));
                return token1.getUsername().equals(userName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error encountered while finding person");
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    /**
     *
     * Clears all of the authorizationTokens from the table
     */
    public void clear() throws SQLException {
        //clears

        PreparedStatement stmt = null;
        try {
            String sql = "delete from AuthorizationToken";
            stmt = conn.prepareStatement(sql);

            int count = stmt.executeUpdate();

            // Reset the auto-increment counter so new books start over with an id of 1
            sql = "delete from AuthorizationToken where Token = 'token'";
            stmt = conn.prepareStatement(sql);
            stmt.executeUpdate();

            System.out.printf("Deleted %d Tokens\n", count);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }

    }
}

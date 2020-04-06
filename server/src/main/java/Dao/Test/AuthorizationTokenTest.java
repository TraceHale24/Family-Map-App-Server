
package Dao.Test;

import Dao.AuthorizationTokenDAO;
import Dao.DataAccessException;
import Dao.Database;
import Dao.EventDAO;
import Model.AuthorizationToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.SQLException;


import static org.junit.jupiter.api.Assertions.*;

public class AuthorizationTokenTest {
    private Database db;
    private AuthorizationToken bestToken;

    @BeforeEach
    public void setUp() throws Exception {
        //here we can set up any classes or variables we will need for the rest of our tests
        //lets create a new database
        db = new Database();
        //db.clearTables();
        //and a new event with random data
        bestToken = new AuthorizationToken("abcadpkgja", "thale8");
    }

    @AfterEach
    public void tearDown() throws Exception {
        //here we can get rid of anything from our tests we don't want to affect the rest of our program
        //lets clear the tables so that any data we entered for testing doesn't linger in our files
        db.openConnection();
        db.clearTables();
        db.closeConnection(true);
    }

    @Test
    public void addTokenTest() throws Exception {
        String compareTest = null;
        boolean equals = false;

        try {
            Connection conn = db.openConnection();
            AuthorizationTokenDAO aDao = new AuthorizationTokenDAO(conn);
            aDao.addAuthToken(bestToken);
            compareTest = aDao.getUserName(bestToken.getTokenID());
            System.out.println(compareTest);
            if(compareTest.equals(bestToken.getUsername())) {
                equals = true;
            }

            aDao.clear();
            db.closeConnection(true);
        }
        catch (DataAccessException e) {
            db.closeConnection(false);
        }
        assertTrue(equals);
    }

    @Test
    public void clearTokenTest() throws Exception {
        boolean success = false;

        try {
            Connection conn = db.openConnection();
            AuthorizationTokenDAO aDao = new AuthorizationTokenDAO(conn);
            //insert event to make sure that we can clear it out
            aDao.addAuthToken(bestToken);
            aDao.clear();
            if(aDao.getUserName("abcadpkgja") == null){
                success = true;
            }
            db.closeConnection(true);
        } catch (DataAccessException e) {
            //If we catch an exception we will end up in here, where we can change our boolean to
            //false to show that our function failed to perform correctly
            db.closeConnection(false);
        }
        assertTrue(success);
    }

}




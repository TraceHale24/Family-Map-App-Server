package Services.Tests;

import Dao.DataAccessException;
import Dao.Database;
import Dao.UserDAO;
import Model.User;
import Request.LoginReq;
import Results.Result;
import Services.LoginService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.print.DocFlavor;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginServiceTest {
    Database db = new Database();
    private User user = new User("fakeUsername", "fakeUsername", "T", "H", "M", "Dad", "fakeID");

    @BeforeEach
    public void setUp() throws Exception {

        //lets create a new database
        db = new Database();
        Connection conn = db.openConnection();
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
    public void LoginPass() throws DataAccessException {
        boolean success = false;
        try {
            Connection conn = db.openConnection();
            UserDAO uDao = new UserDAO(conn);
            uDao.addUser(user);
            LoginReq request = new LoginReq();
            request.setUserName(user.getUserName());
            request.setPassword(user.getPassword());
            request.setPersonID(user.getPersonID());
            LoginService service = new LoginService();
            db.closeConnection(true);
            conn.close();

            Result r = service.login(request);

            if(r.isSuccess()) {
                success = true;
            }
        } catch (Exception e) {
            db.closeConnection(false);
        }
        assertTrue(success);
    }

    @Test
    public void BadUserNameFail() throws DataAccessException {
        boolean success = true;

        try {
            Connection conn = db.openConnection();
            UserDAO uDao = new UserDAO(conn);
            uDao.addUser(user);
            LoginReq request = new LoginReq();
            request.setUserName("badUserName");
            request.setPassword(user.getPassword());
            request.setPersonID(user.getPersonID());
            LoginService service = new LoginService();
            db.closeConnection(true);
            conn.close();

            Result r = service.login(request);

            if(!r.isSuccess()) {
                success = false;
            }
        } catch (Exception e) {
            db.closeConnection(false);
        }
        assertFalse(success);

    }
    @Test
    public void BadPassWordFail() throws DataAccessException {
        boolean success = true;

        try {
            Connection conn = db.openConnection();
            UserDAO uDao = new UserDAO(conn);
            uDao.addUser(user);
            LoginReq request = new LoginReq();
            request.setUserName(user.getUserName());
            request.setPassword("reallyBadPassword");
            request.setPersonID(user.getPersonID());
            LoginService service = new LoginService();
            db.closeConnection(true);
            conn.close();

            Result r = service.login(request);

            if(!r.isSuccess()) {
                success = false;
            }
        } catch (Exception e) {
            db.closeConnection(false);
        }
        assertFalse(success);

    }

}

package Services.Tests;

import Dao.*;
import Model.Event;
import Model.Person;
import Model.User;
import Request.LoginReq;
import Request.RegisterReq;
import Results.RegisterResult;
import Results.Result;
import Services.FillService;
import Services.LoginService;
import Services.RegisterService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.print.DocFlavor;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RegisterServiceTest {
    Database db = new Database();
    private User user = new User("TraceHale", "fakeUsername", "T", "H", "M", "Dad", "fakeID");

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
    public void RegisterPass() throws DataAccessException {
        boolean success = false;
        try {
            Connection conn = db.openConnection();
            UserDAO uDao = new UserDAO(conn);
            PersonDAO personDAO = new PersonDAO(conn);
            EventDAO eDao = new EventDAO(conn);
            personDAO.clear();
            eDao.clear();
            uDao.clear();
            db.closeConnection(true);
            conn.close();

            RegisterService service = new RegisterService();
            RegisterReq request = new RegisterReq();
            request.setFirstName("Trace");
            request.setLastName("Hale");
            request.setEmail("fakeEmail@Gmail.com");
            request.setGender("M");
            request.setPassword("BadPass");
            request.setUserName("TraceHale");

            Result r = service.register(request);

            conn = db.openConnection();

            personDAO = new PersonDAO(conn);
            eDao = new EventDAO(conn);
            uDao = new UserDAO(conn);
            ArrayList<Person> persons = personDAO.getEveryone("TraceHale");
            ArrayList<Event> events = eDao.getEvents("TraceHale");
            uDao.clear();
            personDAO.clear();
            eDao.clear();

            db.closeConnection(true);
            conn.close();
            if(r.isSuccess() && persons.size() == 31 && events.size() == 93) {
                success = true;
            }
        } catch (DataAccessException | SQLException e) {
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
            PersonDAO personDAO = new PersonDAO(conn);
            EventDAO eDao = new EventDAO(conn);
            personDAO.clear();
            eDao.clear();
            uDao.clear();
            uDao.addUser(user);
            db.closeConnection(true);
            conn.close();

            RegisterService service = new RegisterService();
            RegisterReq request = new RegisterReq();
            //Register someone who already exists!
            request.setFirstName("Trace");
            request.setLastName("Hale");
            request.setEmail("fakeEmail@Gmail.com");
            request.setGender("M");
            request.setPassword("BadPass");
            request.setUserName("TraceHale");

            Result r = service.register(request);

            conn = db.openConnection();

            personDAO = new PersonDAO(conn);
            eDao = new EventDAO(conn);
            uDao = new UserDAO(conn);
            ArrayList<Person> persons = personDAO.getEveryone("doesnt");
            ArrayList<Event> events = eDao.getEvents("exist");
            uDao.clear();
            personDAO.clear();
            eDao.clear();

            db.closeConnection(true);
            conn.close();
            if(!r.isSuccess() && persons.size() != 31 && events.size() != 93) {
                success = false;
            }
        } catch (DataAccessException | SQLException e) {
            db.closeConnection(false);
        }
        assertFalse(success);

    }
}

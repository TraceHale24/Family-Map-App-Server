package Services.Tests;

import Dao.*;

import Model.Event;
import Model.Person;
import Model.User;
import Results.Result;
import Services.FillService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FillServiceTest {
    private Database db;
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
    public void FillSuccess() throws DataAccessException {
        boolean success = false;
        try {
            Connection conn = db.openConnection();
            UserDAO uDao = new UserDAO(conn);
            uDao.clear();
            uDao.addUser(user);
            db.closeConnection(true);
            conn.close();

            FillService service = new FillService();
            Result r = service.fill("fakeUsername", 4);

            conn = db.openConnection();

            PersonDAO personDAO = new PersonDAO(conn);
            EventDAO eDao = new EventDAO(conn);
            uDao = new UserDAO(conn);
            ArrayList<Person> persons = personDAO.getEveryone("fakeUsername");
            ArrayList<Event> events = eDao.getEvents("fakeUsername");
            uDao.clear();
            personDAO.clear();
            eDao.clear();

            db.closeConnection(true);
            conn.close();
            if(r.isSuccess() && persons.size() == 30 && events.size() == 93) {
                success = true;
            }
        } catch (DataAccessException | SQLException e) {
            db.closeConnection(false);
        }

        assertTrue(success);
    }

    @Test
    public void FillFail() throws DataAccessException {
        boolean success = true;
        try {
            Connection conn = db.openConnection();
            UserDAO uDao = new UserDAO(conn);
            uDao.clear();
            uDao.addUser(user);
            db.closeConnection(true);
            conn.close();

            FillService service = new FillService();
            Result r = service.fill("badUsername", 4);

            conn = db.openConnection();
            uDao = new UserDAO(conn);
            uDao.clear();
            db.closeConnection(true);
            conn.close();

            if(!r.isSuccess()) {
                success = false;
            }
        } catch (DataAccessException | SQLException e) {
            db.closeConnection(false);
        }
        assertFalse(success);
    }
}

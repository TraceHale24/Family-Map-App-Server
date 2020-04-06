package Services.Tests;

import Dao.*;
import Model.AuthorizationToken;
import Model.Event;
import Model.Person;
import Model.User;
import Results.EventResult;
import Results.Result;
import Services.ClearService;
import Services.EventIDService;
import Services.EventService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventServiceTest {
    private Database db;
    Event fakeEvent = new Event("fakeEventID", "fakeUsername", "fakeID", 120, 150, "Fake Country", "Fake City", "Fake", 2018);
    Event fakeEvent2 = new Event("fakeEventID2", "fakeUsername", "fakeID", 120, 150, "Fake Country", "Fake City", "Fake", 2018);
    Event fakeEvent3 = new Event("fakeEventID3", "fakeUsername", "fakeID", 120, 150, "Fake Country", "Fake City", "Fake", 2018);


    @BeforeEach
    public void setUp() throws Exception {

        //lets create a new database
        db = new Database();
        Connection conn = db.openConnection();

        //Create Fake Data here

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
    public void EventSuccess() throws DataAccessException {
        boolean success = false;
        try {
            Connection conn = db.openConnection();
            EventDAO eDao = new EventDAO(conn);
            eDao.addEvent(fakeEvent);
            eDao.addEvent(fakeEvent2);
            eDao.addEvent(fakeEvent3);
            db.closeConnection(true);
            conn.close();

            EventService service = new EventService();
            Result r = service.run(fakeEvent.getAssociatedUsername());

            if(r.isSuccess()) {
                success = true;
            }


        } catch (DataAccessException | SQLException e) {
            db.closeConnection(false);
        }

        assertTrue(success);
    }

    @Test
    public void EventFail() throws DataAccessException {
        boolean success = true;
        try {
            Connection conn = db.openConnection();
            EventDAO eDao = new EventDAO(conn);
            eDao.addEvent(fakeEvent);
            eDao.addEvent(fakeEvent2);
            eDao.addEvent(fakeEvent3);
            db.closeConnection(true);
            conn.close();

            EventService service = new EventService();
            Result result = service.run("RandomUserNameThatDoesNOTEXIST");

            if(!result.isSuccess()) {
                success = false;
            }
        } catch (DataAccessException | SQLException e) {
            db.closeConnection(false);
        }

        assertFalse(success);
    }
}

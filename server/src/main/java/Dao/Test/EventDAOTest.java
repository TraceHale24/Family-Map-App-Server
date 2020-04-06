
package Dao.Test;

import Dao.DataAccessException;
import Dao.Database;
import Dao.EventDAO;
import Dao.UserDAO;
import Model.Event;
import Model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.rowset.serial.SerialDatalink;
import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

//We will use this to test that our insert method is working and failing in the right ways
public class EventDAOTest {
    private Database db;
    private Event bestEvent;
    private Event bestEventOne;
    private Event bestEventTwo;

    @BeforeEach
    public void setUp() throws Exception {
        //here we can set up any classes or variables we will need for the rest of our tests
        //lets create a new database
        db = new Database();
        //and a new event with random data
        bestEvent = new Event("Biking_123A", "thale8", "12345678",
                10.3f, 10.3f, "Japan", "Ushiku",
                "Biking_Around", 2016);
        bestEventOne = new Event("Biking_123AB", "thale8", "12345678",
                10.3f, 10.3f, "Japan", "Ushiku",
                "Biking_Around", 2016);
        bestEventTwo = new Event("Biking_123ABC", "Gale", "Gale123A",
                10.3f, 10.3f, "Japan", "Ushiku",
                "Biking_Around", 2016);
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
    public void insertPass() throws Exception {
        //We want to make sure insert works
        //First lets create an Event that we'll set to null. We'll use this to make sure what we put
        //in the database is actually there.
        Event compareTest = null;

        try {
            //Let's get our connection and make a new DAO
            Connection conn = db.openConnection();
            EventDAO eDao = new EventDAO(conn);
            //While insert returns a bool we can't use that to verify that our function actually worked
            //only that it ran without causing an error
            eDao.addEvent(bestEvent);
            //So lets use a find method to get the event that we just put in back out
            compareTest = eDao.getEvent(bestEvent.getEventName());
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }
        //First lets see if our find found anything at all. If it did then we know that if nothing
        //else something was put into our database, since we cleared it in the beginning
        assertNotNull(compareTest);
        //Now lets make sure that what we put in is exactly the same as what we got out. If this
        //passes then we know that our insert did put something in, and that it didn't change the
        //data in any way
        assertEquals(bestEvent, compareTest);

    }

    @Test
    public void insertFail() throws Exception {
        //lets do this test again but this time lets try to make it fail

        // NOTE: The correct way to test for an exception in Junit 5 is to use an assertThrows
        // with a lambda function. However, lambda functions are beyond the scope of this class
        // so we are doing it another way.
        boolean didItWork = true;
        try {
            Connection conn = db.openConnection();
            EventDAO eDao = new EventDAO(conn);
            //if we call the method the first time it will insert it successfully
            eDao.addEvent(bestEvent);
            //but our sql table is set up so that "eventID" must be unique. So trying to insert it
            //again will cause the method to throw an exception
            eDao.addEvent(bestEvent);
            db.closeConnection(true);
        } catch (DataAccessException e) {
            //If we catch an exception we will end up in here, where we can change our boolean to
            //false to show that our function failed to perform correctly
            db.closeConnection(false);
            didItWork = false;
        }
        //Check to make sure that we did in fact enter our catch statement
        assertFalse(didItWork);

        //Since we know our database encountered an error, both instances of insert should have been
        //rolled back. So for added security lets make one more quick check using our find function
        //to make sure that our event is not in the database
        //Set our compareTest to an actual event
        Event compareTest = bestEvent;
        try {
            Connection conn = db.openConnection();
            EventDAO eDao = new EventDAO(conn);
            //and then get something back from our find. If the event is not in the database we
            //should have just changed our compareTest to a null object
            compareTest = eDao.getEvent(bestEvent.getEventName());
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }

        //Now make sure that compareTest is indeed null
        assertNull(compareTest);
    }

    @Test
    public void clearEventTest() throws Exception {
        boolean success = false;

        try {
            Connection conn = db.openConnection();
            EventDAO eDao = new EventDAO(conn);
            //insert event to make sure that we can clear it out
            eDao.addEvent(bestEvent);
            eDao.clear();
            success = true;
            db.closeConnection(true);
        } catch (DataAccessException e) {
            //If we catch an exception we will end up in here, where we can change our boolean to
            //false to show that our function failed to perform correctly
            db.closeConnection(false);
        }
        assertTrue(success);
    }

    @Test
    public void getEventsTest() throws SQLException, DataAccessException {
        boolean success = false;
        ArrayList<Event> tempEvents = new ArrayList<>();
        try {
            Connection conn = db.openConnection();
            EventDAO eDao = new EventDAO(conn);
            eDao.addEvent(bestEvent);
            eDao.addEvent(bestEventOne);
            eDao.addEvent(bestEventTwo);
            tempEvents = eDao.getEvents(bestEvent.getAssociatedUsername());
            db.closeConnection(true);

        } catch(DataAccessException e) {
            db.closeConnection(false);
        }
        if(tempEvents.size() == 2) {
            success = true;
        }
        assertTrue(success);
    }


    @Test
    public void deleteTestPass() throws DataAccessException {
        boolean success = false;

        try {
            Connection conn = db.openConnection();
            EventDAO eDao = new EventDAO(conn);
            eDao.addEvent(bestEvent);
            eDao.addEvent(bestEventOne);
            eDao.addEvent(bestEventTwo);
            eDao.delete(bestEvent.getAssociatedUsername());
            Event tempEvent = eDao.getEvent(bestEvent.getEventName());
            if(tempEvent == null) {
                success = true;
            }
            db.closeConnection(true);
        } catch(DataAccessException | SQLException e) {
            db.closeConnection(false);
        }
        assertTrue(success);
    }

    @Test
    public void deleteTestFail() throws DataAccessException {
        boolean success = true;

        try {
            Connection conn = db.openConnection();
            EventDAO eDao = new EventDAO(conn);
            eDao.addEvent(bestEvent);
            eDao.addEvent(bestEventOne);
            eDao.addEvent(bestEventTwo);
            eDao.delete(bestEvent.getAssociatedUsername());
            Event tempEvent = eDao.getEvent(bestEventTwo.getEventName());
            if(tempEvent != null) {
                success = false;
            }
            db.closeConnection(true);
        } catch(DataAccessException | SQLException e) {
            db.closeConnection(false);
        }
        assertFalse(success);
    }

    @Test
    public void testUserExists() throws Exception {
        //Make sure our Insert can take two people who are siblings
        Event compareTest = null;
        boolean works = false;

        try {
            //Let's get our connection and make a new DAO
            Connection conn = db.openConnection();
            EventDAO eDao = new EventDAO(conn);
            //While insert returns a bool we can't use that to verify that our function actually worked
            //only that it ran without causing an error
            eDao.addEvent(bestEvent);

            //So lets use a find method to get the event that we just put in back out
            compareTest = eDao.getEvent("Biking_123A");
            works = eDao.eventExists(compareTest);
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }
        //First lets see if our find found anything at all. If it did then we know that if nothing
        //else something was put into our database, since we cleared it in the beginning
        assertTrue(works);

    }

    @Test
    public void testUserDoesNotExist() throws Exception {
        //Make sure our Insert can take two people who are siblings
        Event compareTest = null;
        boolean works = false;

        try {
            //Let's get our connection and make a new DAO
            Connection conn = db.openConnection();
            EventDAO eDao = new EventDAO(conn);
            //While insert returns a bool we can't use that to verify that our function actually worked
            //only that it ran without causing an error
            eDao.addEvent(bestEvent);

            //So lets use a find method to get the event that we just put in back out
            compareTest = eDao.getEvent("thale8");
            works = eDao.eventExists(compareTest);
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }
        //First lets see if our find found anything at all. If it did then we know that if nothing
        //else something was put into our database, since we cleared it in the beginning
        assertFalse(works);

    }

}

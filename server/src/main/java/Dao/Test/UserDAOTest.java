package Dao.Test;

import Dao.DataAccessException;
import Dao.Database;
import Dao.UserDAO;
import Model.Person;
import Model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

//We will use this to test that our insert method is working and failing in the right ways
public class UserDAOTest {
    private Database db;
    private User bestUser;

    @BeforeEach
    public void setUp() throws Exception {

        //here we can set up any classes or variables we will need for the rest of our tests
        //lets create a new database
        db = new Database();
        //and a new Person with random data
        bestUser = new User("thale8", "malaContrasena", "thale8@fake.edu",
                "Trace", "Hale", "M", "12345678");
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
        User compareTest = null;

        try {
            //Let's get our connection and make a new DAO
            Connection conn = db.openConnection();
            UserDAO uDao = new UserDAO(conn);
            //While insert returns a bool we can't use that to verify that our function actually worked
            //only that it ran without causing an error
            uDao.addUser(bestUser);
            //So lets use a find method to get the event that we just put in back out
            compareTest = uDao.getUser(bestUser.getUserName());
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
        assertEquals(bestUser, compareTest);

    }

    @Test
    public void getPersonPass() throws Exception {
        //Make sure our Insert can take two people who are siblings
        User compareTest = null;


        try {
            //Let's get our connection and make a new DAO
            Connection conn = db.openConnection();
            UserDAO uDao = new UserDAO(conn);
            //While insert returns a bool we can't use that to verify that our function actually worked
            //only that it ran without causing an error
            uDao.addUser(bestUser);

            //So lets use a find method to get the event that we just put in back out
            compareTest = uDao.getUser("thale8");

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
        assertEquals(compareTest, bestUser);

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
            UserDAO uDao = new UserDAO(conn);
            //if we call the method the first time it will insert it successfully
            uDao.addUser(bestUser);
            //but our sql table is set up so that "userID" must be unique. So trying to insert it
            //again will cause the method to throw an exception
            uDao.addUser(bestUser);
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
        User compareTest = bestUser;
        try {
            Connection conn = db.openConnection();
            UserDAO uDao = new UserDAO(conn);
            //and then get something back from our find. If the event is not in the database we
            //should have just changed our compareTest to a null object
            compareTest = uDao.getUser(bestUser.getUserName());
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }

        //Now make sure that compareTest is indeed null
        assertNull(compareTest);
    }

    @Test
    public void getPersonFail() throws Exception {
        //Make sure our Insert can take two people who are siblings
        User compareTest = null;


        try {
            //Let's get our connection and make a new DAO
            Connection conn = db.openConnection();
            UserDAO uDao = new UserDAO(conn);
            //While insert returns a bool we can't use that to verify that our function actually worked
            //only that it ran without causing an error
            uDao.addUser(bestUser);

            //So lets use a find method to get the event that we just put in back out
            compareTest = uDao.getUser("thale0");

            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }
        //First lets see if our find found anything at all. If it did then we know that if nothing
        //else something was put into our database, since we cleared it in the beginning
        assertNull(compareTest);
        //Now lets make sure that what we put in is exactly the same as what we got out. If this
        //passes then we know that our insert did put something in, and that it didn't change the
        //data in any way
        assertNotEquals(compareTest, bestUser);
    }

    @Test
    public void testUserExists() throws Exception {
        //Make sure our Insert can take two people who are siblings
        User compareTest = null;
        boolean works = false;

        try {
            //Let's get our connection and make a new DAO
            Connection conn = db.openConnection();
            UserDAO uDao = new UserDAO(conn);
            //While insert returns a bool we can't use that to verify that our function actually worked
            //only that it ran without causing an error
            uDao.addUser(bestUser);

            //So lets use a find method to get the event that we just put in back out
            compareTest = uDao.getUser("thale8");
            works = uDao.userExists(compareTest);
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
        User compareTest = null;
        boolean works = true;

        try {
            //Let's get our connection and make a new DAO
            Connection conn = db.openConnection();
            UserDAO uDao = new UserDAO(conn);
            //While insert returns a bool we can't use that to verify that our function actually worked
            //only that it ran without causing an error
            uDao.addUser(bestUser);

            //So lets use a find method to get the event that we just put in back out
            compareTest = uDao.getUser("thale0");
            works = uDao.userExists(compareTest);
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }
        //First lets see if our find found anything at all. If it did then we know that if nothing
        //else something was put into our database, since we cleared it in the beginning
        assertFalse(works);
    }

    @Test
    public void clearEventTest() throws Exception {
        boolean success = false;

        try {
            Connection conn = db.openConnection();
            UserDAO uDao = new UserDAO(conn);
            //insert event to make sure that we can clear it out
            uDao.addUser(bestUser);
            uDao.clear();
            if(uDao.getUser("thale8") == null) {
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

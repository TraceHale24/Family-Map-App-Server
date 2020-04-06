package Dao.Test;

import Dao.DataAccessException;
import Dao.Database;
import Dao.PersonDAO;
import Model.Person;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

//We will use this to test that our insert method is working and failing in the right ways
public class PersonDAOTest {
    private Database db;
    private Person bestPerson;
    private Person bestPersonTwo;
    private Person bestPersonThree;

    @BeforeEach
    public void setUp() throws Exception {
        //here we can set up any classes or variables we will need for the rest of our tests
        //lets create a new database
        db = new Database();
        //and a new Person with random data
        bestPerson = new Person("12345678", "thale8", "Trace",
                "Hale", "M", "JasonHale", "CatherineHale",
                "null");
        bestPersonTwo = new Person("87654321", "fakeUserName", "Trace",
                "Hale", "M", "JasonHale", "CatherineHale",
                "notNull");
        bestPersonThree = new Person("65165165", "fakeUserName", "Trace",
                "Hale", "M", "JasonHale", "CatherineHale",
                "notNull");
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
        Person compareTest = null;

        try {
            //Let's get our connection and make a new DAO
            Connection conn = db.openConnection();
            PersonDAO pDao = new PersonDAO(conn);
            //While insert returns a bool we can't use that to verify that our function actually worked
            //only that it ran without causing an error
            pDao.addPerson(bestPerson);
            //So lets use a find method to get the event that we just put in back out
            compareTest = pDao.getPerson(bestPerson.getPersonID());
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
        assertEquals(bestPerson, compareTest);

    }

    @Test
    public void getPersonPass() throws Exception {
        //Make sure our Insert can take two people who are siblings
        Person compareTest = null;


        try {
            //Let's get our connection and make a new DAO
            Connection conn = db.openConnection();
            PersonDAO pDao = new PersonDAO(conn);
            //While insert returns a bool we can't use that to verify that our function actually worked
            //only that it ran without causing an error
            pDao.addPerson(bestPerson);

            //So lets use a find method to get the event that we just put in back out
            compareTest = pDao.getPerson("12345678");

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
        assertEquals(compareTest, bestPerson);

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
            PersonDAO pDao = new PersonDAO(conn);
            //if we call the method the first time it will insert it successfully
            pDao.addPerson(bestPerson);
            //but our sql table is set up so that "userID" must be unique. So trying to insert it
            //again will cause the method to throw an exception
            pDao.addPerson(bestPerson);
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
        Person compareTest = bestPerson;
        try {
            Connection conn = db.openConnection();
            PersonDAO pDao = new PersonDAO(conn);
            //and then get something back from our find. If the event is not in the database we
            //should have just changed our compareTest to a null object
            compareTest = pDao.getPerson(bestPerson.getPersonID());
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
        Person compareTest = null;


        try {
            //Let's get our connection and make a new DAO
            Connection conn = db.openConnection();
            PersonDAO pDao = new PersonDAO(conn);
            //While insert returns a bool we can't use that to verify that our function actually worked
            //only that it ran without causing an error
            pDao.addPerson(bestPerson);

            //So lets use a find method to get the event that we just put in back out
            compareTest = pDao.getPerson("123");

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
        assertNotEquals(compareTest, bestPerson);

    }

    @Test
    public void userExistsPass() throws Exception {
        //Make sure our Insert can take two people who are siblings
        Person compareTest = null;
        boolean success = false;


        try {
            //Let's get our connection and make a new DAO
            Connection conn = db.openConnection();
            PersonDAO pDao = new PersonDAO(conn);
            //While insert returns a bool we can't use that to verify that our function actually worked
            //only that it ran without causing an error
            pDao.addPerson(bestPerson);

            //So lets use a find method to get the event that we just put in back out
            compareTest = pDao.getPerson("12345678");
            success = pDao.personExists(compareTest);
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }
        //First lets see if our find found anything at all. If it did then we know that if nothing
        //else something was put into our database, since we cleared it in the beginning
        assertTrue(success);
        //Now lets make sure that what we put in is exactly the same as what we got out. If this
        //passes then we know that our insert did put something in, and that it didn't change the
        //data in any way
    }

    @Test
    public void userExistsFail() throws Exception {
        //Make sure our Insert can take two people who are siblings
        Person compareTest = null;
        boolean success = false;


        try {
            //Let's get our connection and make a new DAO
            Connection conn = db.openConnection();
            PersonDAO pDao = new PersonDAO(conn);
            //While insert returns a bool we can't use that to verify that our function actually worked
            //only that it ran without causing an error
            pDao.addPerson(bestPerson);

            //So lets use a find method to get the event that we just put in back out
            compareTest = pDao.getPerson("badInput");
            success = pDao.personExists(compareTest);
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }
        //First lets see if our find found anything at all. If it did then we know that if nothing
        //else something was put into our database, since we cleared it in the beginning
        assertFalse(success);
        //Now lets make sure that what we put in is exactly the same as what we got out. If this
        //passes then we know that our insert did put something in, and that it didn't change the
        //data in any way
    }


    @Test
    public void clearEventTest() throws Exception {
        boolean success = false;

        try {
            Connection conn = db.openConnection();
            PersonDAO pDao = new PersonDAO(conn);
            //insert event to make sure that we can clear it out
            pDao.addPerson(bestPerson);
            pDao.clear();
            if(pDao.getPerson("12345678") == null) {
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

    @Test
    public void deletePersonPass() throws Exception {
        boolean success = false;
        boolean personTwoExits = false;

        try {
            Connection conn = db.openConnection();
            PersonDAO pDao = new PersonDAO(conn);
            //insert two people to make sure our function is not clearing all of the data
            pDao.addPerson(bestPerson);
            pDao.addPerson(bestPersonTwo);
            pDao.deletePerson(bestPerson.getUsername());
            success = pDao.personExists(bestPerson);
            personTwoExits = pDao.personExists(bestPersonTwo);
            db.closeConnection(true);
        } catch (DataAccessException e) {
            //If we catch an exception we will end up in here, where we can change our boolean to
            //false to show that our function failed to perform correctly
            db.closeConnection(false);
        }
        assertTrue(personTwoExits);
        assertFalse(success);
    }

    @Test
    public void deletePersonFail() throws Exception {
        boolean success = false;
        boolean successTwo = false;

        try {
            Connection conn = db.openConnection();
            PersonDAO pDao = new PersonDAO(conn);
            //insert event to make sure that we can clear it out
            pDao.addPerson(bestPerson);
            pDao.addPerson(bestPersonTwo);
            pDao.deletePerson("thale0");
            success = pDao.personExists(bestPersonTwo);
            successTwo = pDao.personExists(bestPerson);


            db.closeConnection(true);
        } catch (DataAccessException e) {
            //If we catch an exception we will end up in here, where we can change our boolean to
            //false to show that our function failed to perform correctly
            db.closeConnection(false);
        }
        assertTrue(success);
        assertTrue(successTwo);
    }

    @Test
    public void testGetAll() throws SQLException, DataAccessException {
        boolean success = false;
        ArrayList<Person> people = new ArrayList<>();
        try {
            Connection conn = db.openConnection();
            PersonDAO pDao = new PersonDAO(conn);
            pDao.addPerson(bestPersonTwo);
            pDao.addPerson(bestPerson);
            pDao.addPerson(bestPersonThree);
            people = pDao.getEveryone(bestPersonTwo.getUsername());
            db.closeConnection(true);
        } catch (DataAccessException e) {
            db.closeConnection(false);
        }
        if(people.size() == 2) {
            success = true;
        }
        assertTrue(success);
    }


}

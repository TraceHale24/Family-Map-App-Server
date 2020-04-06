package Services.Tests;

import Dao.*;
import Model.AuthorizationToken;
import Model.Event;
import Model.Person;
import Model.User;
import Results.PersonIDResult;
import Results.Result;
import Services.ClearService;
import Services.EventIDService;
import Services.PersonIDService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PersonIDServiceTest {
    private Database db;
    Person fakePerson = new Person("fakePersonID", "fakeUsername", "fakeID", "Hale", "M", "NoDadSad", "NotSure", "Fake");


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
    public void PersonIDSuccess() throws DataAccessException {
        boolean success = false;
        try {
            Connection conn = db.openConnection();
            PersonDAO pDao = new PersonDAO(conn);
            pDao.addPerson(fakePerson);
            db.closeConnection(true);
            conn.close();
            PersonIDService service = new PersonIDService();
            Result r = service.run(fakePerson);

            if(r.isSuccess()) {
                success = true;
            }


        } catch (DataAccessException | SQLException e) {
            db.closeConnection(false);
        }

        assertTrue(success);
    }

    @Test
    public void PersonIDFail() throws DataAccessException {
        boolean success = true;
        Person newPerson = new Person("secondFakeID", "fakeUsername", "FakeName", "BadName", "F", "Ronald McDonald", "DaffyDuck", "Fake");
        try {
            Connection conn = db.openConnection();
            PersonDAO pDao = new PersonDAO(conn);
            pDao.clear();
            pDao.addPerson(fakePerson);
            //pDao.addPerson(newPerson);

            db.closeConnection(true);
            conn.close();

            PersonIDService service = new PersonIDService();
            Result result = service.run(newPerson);

            if(!result.isSuccess()) {
                success = false;
            }
        } catch (DataAccessException | SQLException e) {
            db.closeConnection(false);
        }

        assertFalse(success);
    }
}

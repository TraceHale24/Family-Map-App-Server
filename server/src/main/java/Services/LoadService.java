package Services;

import Dao.*;
import Model.Event;
import Model.Person;
import Model.User;
import Results.Result;
import Request.LoadReq;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;


public class LoadService {
    /**
     * Clears all data from the database, and then loads the posted user, person, and event data into the database
     * @param l
     * @return (valid/invalid result)
     */
    public Result load(LoadReq l) throws DataAccessException, SQLException {
        Database db = new Database();
        Connection conn = null;

        try {
            conn = db.openConnection();
        } catch (DataAccessException e) {
            System.out.println("Failure to open connection: " + e);
        }

        UserDAO uDao = new UserDAO(conn);
        PersonDAO pDao = new PersonDAO(conn);
        EventDAO eDao = new EventDAO(conn);
        Result result = new Result();
        int userCount = 0;
        int personCount = 0;
        int eventCount = 0;
        uDao.clear();
        pDao.clear();
        eDao.clear();

        ArrayList<User> userArray = l.getUserArray();
        ArrayList<Person> personArray = l.getPersonArray();
        ArrayList<Event> eventArray = l.getEventArray();
        for(int i = 0; i < userArray.size(); i++) {
            uDao.addUser(userArray.get(i));
            userCount++;
        }

        for(int i = 0; i < personArray.size(); i++) {
            pDao.addPerson(personArray.get(i));
            personCount++;
        }

        for(int i = 0; i < eventArray.size(); i++) {
            eDao.addEvent(eventArray.get(i));
            eventCount++;
        }

        result.setMessage("Successfully added " + userCount + " users, " + personCount + " persons, and " + eventCount + " events to the data!");
        result.setSuccess(true);
        db.closeConnection(true);
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }
}

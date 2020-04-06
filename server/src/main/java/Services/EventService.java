package Services;

import Dao.DataAccessException;
import Dao.Database;
import Dao.EventDAO;
import Model.Event;
import Results.Result;
import Results.EventResult;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class EventService {
    /**
     * Returns ALL events for ALL family members of the current user. The current user is determined from the provided auth token.
     * @param userName
     * @return (valid/invalid result)
     */
    public Result run(String userName) throws SQLException {
        Database db = new Database();
        Connection conn = null;
        try {
            conn = db.openConnection();
        } catch (DataAccessException e) {
            System.out.println("Failure to open connection: " + e);
        }

        EventDAO eDao = new EventDAO(conn);
        ArrayList<Event> temp = eDao.getEvents(userName);
        EventResult result = new EventResult(eDao.getEvents(userName));

        if(temp.size() != 0) {
            result.setSuccess(true);
        }

        try {
            db.closeConnection(true);
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        conn.close();

        return result;
    }
}

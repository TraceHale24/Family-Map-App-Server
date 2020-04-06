package Services;

import Dao.DataAccessException;
import Dao.Database;
import Dao.EventDAO;
import Results.EventIDResult;
import Results.Result;
import Model.Event;

import java.sql.Connection;
import java.sql.SQLException;

public class EventIDService {
    /**
     * Returns the single Event object with the specified ID
     * @param event
     * @return (valid/invalid result)
     */
    public Result run(Event event) throws DataAccessException {

        Database db = new Database();
        Connection conn = null;
        try {
            conn = db.openConnection();
        } catch (DataAccessException e) {
            System.out.println("Failure to open connection: " + e);
        }
        Result result = new Result();
        EventDAO eDao = new EventDAO(conn);


        if(event == null) {
            result.setMessage("error: Invalid EventID, no such event exists");
            return result;
        }

        else if(eDao.getEvent(event.getEventName()) != null) {
            result = new EventIDResult(event);
            result.setSuccess(true);
        }
        else {
            result.setMessage("error: Your Event does not exist inside of our database!");
            result.setSuccess(false);
        }

        db.closeConnection(true);
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;

    }
}

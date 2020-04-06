package Services;

import Dao.*;
import Results.Result;

import java.sql.Connection;
import java.sql.SQLException;


public class ClearService {
    /**
     * Deletes ALL the data from the database, including user accounts, auth tokens, and generated person and event data.
     * @return (valid/invalid result)
     */
    public Result clear() throws SQLException, DataAccessException {
        Database db = new Database();
        Connection conn = null;
        try {
            conn = db.openConnection();
        } catch (DataAccessException e) {
            System.out.println("Failure to open connection: " + e);
        }
        Result clearResult = new Result();
        UserDAO uDao = new UserDAO(conn);
        PersonDAO pDao = new PersonDAO(conn);
        EventDAO eDao = new EventDAO(conn);
        AuthorizationTokenDAO aDao = new AuthorizationTokenDAO(conn);

        try {
            uDao.clear();
            pDao.clear();
            eDao.clear();
            aDao.clear();
            clearResult.setSuccess(true);
            clearResult.setMessage("Clear Succeeded!");
            db.closeConnection(true);
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
            clearResult.setMessage("error: Clear Failed!");
        }
        return clearResult;
    }
}

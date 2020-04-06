package Services;


import Dao.DataAccessException;
import Dao.Database;
import Dao.EventDAO;
import Dao.PersonDAO;
import Model.Person;
import Results.EventResult;
import Results.Result;
import Results.PersonResult;

import java.sql.Connection;
import java.sql.SQLException;

public class PersonService {
    /**
     * Returns ALL family members of the current user. The current user is determined from the provided auth token
     * @param userName
     * @return (valid/invalid result)
     */
    public Result personService(String userName) throws DataAccessException, SQLException {
        Database db = new Database();
        Connection conn = null;
        try {
            conn = db.openConnection();
        } catch (DataAccessException e) {
            System.out.println("Failure to open connection: " + e);
        }
        PersonDAO pDao = new PersonDAO(conn);
        Person person = pDao.getPerson(userName);
        PersonResult result = new PersonResult(pDao.getEveryone(userName));
        if(person != null) {
            result.setSuccess(true);
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

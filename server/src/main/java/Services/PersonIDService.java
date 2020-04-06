package Services;

import Dao.DataAccessException;
import Dao.Database;
import Dao.PersonDAO;
import Results.PersonIDResult;
import Results.Result;
import Model.Person;

import java.sql.Connection;
import java.sql.SQLException;

public class PersonIDService {
    /**
     * Returns the single Person object with the specified ID
     * @param person
     * @return (valid/invalid result)
     */
    public Result run(Person person) throws DataAccessException, SQLException {
        Database db = new Database();
        Connection conn = null;
        try {
            conn = db.openConnection();
        } catch (DataAccessException e) {
            System.out.println("Failure to open connection: " + e);
        }
        Result result = new Result();
        PersonDAO pDao = new PersonDAO(conn);


        if(pDao.getPerson(person.getPersonID()) == null) {
            result.setMessage("error: Invalid PersonID, no such person exists");
            db.closeConnection(true);
            conn.close();
            return result;
        }

        if(pDao.getPerson(person.getPersonID()) != null) {
            result = new PersonIDResult(pDao.getPerson(person.getPersonID()));
            //result.setErrorMessage("We found your Person!");
            result.setSuccess(true);
        }
        else {
            result.setMessage("error: Your Person does not exist inside of our database!");
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

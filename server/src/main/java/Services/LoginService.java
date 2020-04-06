package Services;

import Dao.AuthorizationTokenDAO;
import Dao.DataAccessException;
import Dao.Database;
import Dao.UserDAO;
import Model.AuthorizationToken;
import Model.User;
import Results.LoginResult;
import Results.Result;
import Request.LoginReq;
import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class LoginService {
    /**
     * Logs in the user and returns an AuthToken
     * @param r
     * @return (valid/invalid result)
     */
    public Result login(LoginReq r) throws DataAccessException, SQLException {

        Database db = new Database();
        Connection conn = null;
        Gson gson = new Gson();

        try {
            conn = db.openConnection();
        } catch (DataAccessException e) {
            System.out.println("Failure to open connection: " + e);
        }


        UserDAO uDao = new UserDAO(conn);
        AuthorizationTokenDAO aDao = new AuthorizationTokenDAO(conn);
        LoginResult result = new LoginResult();
        if(uDao.getUser(r.getUserName()) == null) {
            //result.setUserName(r.getUserName());
            result.setMessage("error: Invalid Username; You shall not pass!");
            db.closeConnection(true);
            conn.close();
            return result;
        }
        User user = uDao.getUser(r.getUserName());
        if(user == null) {
            result.setMessage("error: Username does not exist");
            result.setSuccess(false);
            db.closeConnection(true);
            conn.close();
            return result;


        }
        if(user.getPassword().equals(r.getPassword())) {
            UUID token = UUID.randomUUID();
            String authToken = token.toString();
            result.setUserName(r.getUserName());
            System.out.println(user.getPersonID());

            AuthorizationToken newToken = new AuthorizationToken(authToken, user.getUserName());
            aDao.addAuthToken(newToken);
            result.setAuthToken(authToken);
            result.setPersonID(user.getPersonID());
            result.setSuccess(true);
        }
        else {
            result.setMessage("error: Incorrect Password!");
            db.closeConnection(true);
            conn.close();
            return result;
        }
        db.closeConnection(true);
        conn.close();
        return result;
    }
}

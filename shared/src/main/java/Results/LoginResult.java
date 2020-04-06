package Results;

public class LoginResult extends Result {
    private String authToken;
    private String userName;
    private String personID;

    public LoginResult(String authToken, String userName, String personID) {
        this.authToken = authToken;
        this.userName = userName;
        this.personID = personID;
    }

    public LoginResult() {

    }
/*
    public LoginResult(String userName) {
        this.userName = userName;
    }
*/

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getPersonID() { return personID; }
}

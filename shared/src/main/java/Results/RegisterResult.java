package Results;

public class RegisterResult extends Result {
    private String authToken;
    private String userName;
    private String personID;

    public RegisterResult(String token, String userName, String personID) {
        this.authToken = token;
        this.userName = userName;
        this.personID = personID;
    }
    public RegisterResult() {}

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

    public String getUserName() {
        return userName;
    }

    public String getPersonID() {
        return personID;
    }
}

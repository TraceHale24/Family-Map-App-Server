package Request;

public class LoginReq {
    private String userName;
    private String password;
    private String personID;

    public String getUserName() {
        return userName;
    }
    public String getPassword() { return password; }
    public String getPersonID() { return personID; }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPersonID(String personID) {
        this.personID = personID;
    }
}

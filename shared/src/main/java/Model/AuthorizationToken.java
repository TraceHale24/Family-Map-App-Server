package Model;

public class AuthorizationToken {
    private String tokenID;
    private String userName;

    public AuthorizationToken() {

    }

    public AuthorizationToken(String tokenID, String userName) {
        this.tokenID = tokenID;
        this.userName = userName;
    }

    public String getTokenID() {
        return tokenID;
    }

    public void setTokenID(String userID) {
        this.tokenID = tokenID;
    }

    public String getUsername() {
        return userName;
    }

    public void setUsername(String username) {
        this.userName = userName;
    }
}

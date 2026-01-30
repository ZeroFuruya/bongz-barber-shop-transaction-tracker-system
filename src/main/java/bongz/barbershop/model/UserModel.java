package bongz.barbershop.model;

public class UserModel {
    private int id;
    private String username;
    private String password;
    private String role;
    private int isActive;

    public UserModel(int id, String username, String password, String role, int isActive) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.isActive = isActive;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "UserModel [username=" + username + ", password=" + password + ", role=" + role + ", isActive="
                + isActive + "]";
    }

}

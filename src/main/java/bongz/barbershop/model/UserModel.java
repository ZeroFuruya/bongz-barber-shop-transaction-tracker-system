package bongz.barbershop.model;

public class UserModel {
    private int id;
    private String username;
    private String password;
    private String role;
    private int isActive;
    private String createdAt;

    public UserModel(int id, String username, String password, String role, int isActive, String createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.isActive = isActive;
        this.createdAt = createdAt;
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

    public String getCreatedAt() {
        return createdAt;
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

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "UserModel [username=" + username + ", password=" + password + ", role=" + role + ", isActive="
                + isActive + ", createdAt=" + createdAt + "]";
    }

}

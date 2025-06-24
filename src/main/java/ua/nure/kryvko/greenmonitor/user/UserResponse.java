package ua.nure.kryvko.greenmonitor.user;

public class UserResponse {
    Integer id;
    String email;
    UserRole role;

    public UserResponse() {}

    public UserResponse(Integer id, String email, UserRole role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}

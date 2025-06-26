// LoginResponse.java
package cl.ferremas.dto;

public class LoginResponse {
    
    private String message;
    private String username;
    private String role;
    private Long userId;

    // Constructores
    public LoginResponse() {}

    public LoginResponse(String message, String username, String role, Long userId) {
        this.message = message;
        this.username = username;
        this.role = role;
        this.userId = userId;
    }

    // Getters y setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
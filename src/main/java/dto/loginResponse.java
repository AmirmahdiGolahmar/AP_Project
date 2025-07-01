package dto;

import entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class loginResponse {
    String message;
    String token;
    UserDto user;

    public loginResponse(String message, String token, User user) {
        this.message = message;
        this.token = token;
        this.user = new UserDto(user);
    }
}

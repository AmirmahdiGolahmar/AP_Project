package dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationResponse {
    String message, user_id, token, role;
}

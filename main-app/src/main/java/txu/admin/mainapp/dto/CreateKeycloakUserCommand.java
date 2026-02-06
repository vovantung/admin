package txu.admin.mainapp.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CreateKeycloakUserCommand implements Serializable {
    private String sagaId;
    private String userId;
    private String username;
    private String email;
}

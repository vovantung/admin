package txu.admin.mainapp.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Data
public class CreateKeycloakUserCommand {
    private String sagaId;
    private String userId;
    private String username;
    private String email;
}

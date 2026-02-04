package txu.admin.mainapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateHRUserCommand {
    private String sagaId;
    private String keycloakUserId;
}

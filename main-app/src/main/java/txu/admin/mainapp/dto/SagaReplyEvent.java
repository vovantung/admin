package txu.admin.mainapp.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
public class SagaReplyEvent implements Serializable {

    private String sagaId;
    private String step;     // KEYCLOAK_CREATE | HR_CREATE | KEYCLOAK_DELETE
    private boolean success;
    private String error;

    private Map<String, Object> payload;

}


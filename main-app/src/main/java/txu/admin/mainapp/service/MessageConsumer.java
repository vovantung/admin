package txu.admin.mainapp.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import txu.admin.mainapp.dto.CreateHRUserCommand;
import txu.admin.mainapp.dto.CreateKeycloakUserCommand;
import txu.admin.mainapp.dto.DeleteUserCommand;
import txu.admin.mainapp.dto.SagaReplyEvent;

import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class MessageConsumer {
//    private final MessageProducer messageProducer;
    private final JmsTemplate jmsTemplate;
    private  final  KeycloakService keycloakService;

    @JmsListener(destination = "keycloak.create.user.queue")
    public void createKeycloakUser(CreateKeycloakUserCommand cmd) {

        try {
            String keycloakUserId = keycloakService.createKeycloakUser(cmd.getUsername(), cmd.getEmail(),"ABC", "SSS");
            log.info("Da tao KeycloakUser");


            SagaReplyEvent event = new SagaReplyEvent();
            event.setSagaId(cmd.getSagaId());
            event.setStep("KEYCLOAK_CREATE");
            event.setSuccess(true);
            event.setPayload(
                    Map.of("keycloakUserId", keycloakUserId)
            );
            jmsTemplate.convertAndSend("saga.reply.queue", event);

        } catch (Exception ex) {
            SagaReplyEvent event = new SagaReplyEvent();
            event.setSagaId(cmd.getSagaId());
            event.setStep("KEYCLOAK_CREATE");
            event.setSuccess(false);
            event.setError(ex.getMessage());
            jmsTemplate.convertAndSend("saga.reply.queue", event);
        }
    }


    @JmsListener(destination = "hr.create.user.queue")
    @Transactional
    public void createHRUser(CreateHRUserCommand cmd) {

        try {
//            hrUserRepository.createUser(cmd.getKeycloakUserId());
            log.info("Da tao HRUser");

            SagaReplyEvent event = new SagaReplyEvent();
            event.setSagaId(cmd.getSagaId());
            event.setStep("HR_CREATE");
            event.setSuccess(true);
            jmsTemplate.convertAndSend("saga.reply.queue", event);

        } catch (Exception ex) {
            SagaReplyEvent event = new SagaReplyEvent();
            event.setSagaId(cmd.getSagaId());
            event.setStep("HR_CREATE");
            event.setSuccess(false);
            event.setError(ex.getMessage());
            jmsTemplate.convertAndSend("saga.reply.queue", event);
        }
    }

    @JmsListener(destination = "keycloak.delete.user.queue")
    public void handleDeleteUser(DeleteUserCommand cmd) {
//        keycloakService.deleteUser(cmd.getUserId());
//        sendSuccess(cmd.getSagaId(), "KEYCLOAK_DELETE");
    }

}


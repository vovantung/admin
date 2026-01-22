package txu.admin.mainapp.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import txu.admin.mainapp.base.AbstractApi;
import txu.admin.mainapp.dto.IdRequest;
import txu.admin.mainapp.dto.LimitRequest;
import txu.admin.mainapp.dto.TestRequest;
import txu.admin.mainapp.entity.DepartmentEntity;
import txu.admin.mainapp.service.DepartmentService;
import txu.admin.mainapp.service.MessageProducer;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class TestApi extends AbstractApi {

    private final DepartmentService departmentService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final MessageProducer messageProducer;


    @PostMapping(value = "get-by-id")
//    @Cacheable(value = "department", key = "#request.id")
    public DepartmentEntity getById(@RequestBody IdRequest request){
        return  departmentService.getById(request.getId());
    }



    @PostMapping(value = "send-message")
    public void send() {
        String str = "Vo Thi Ngoc Uyen";
        kafkaTemplate.send("orders-events", str);
    }

    @PostMapping(value = "send-message-activemq")
    public void send_activemq(@RequestBody TestRequest request) {
        messageProducer.send(request.getStr());
    }

    @GetMapping(value = "/test")
    public String test() {
        return "Phan Thi Xuyen";
    }

}

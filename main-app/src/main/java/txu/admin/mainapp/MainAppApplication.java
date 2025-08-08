package txu.admin.mainapp;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import txu.common.grpc.GrpcServer;
import txu.admin.mainapp.grpc.HrmGrpcService;

import java.io.IOException;
import java.util.TimeZone;

@SpringBootApplication
public class MainAppApplication {

    public static void main(String[] args) throws IOException, InterruptedException {
//        SpringApplication.run(MainAppApplication.class, args);
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        GrpcServer.start(MainAppApplication.class, HrmGrpcService.class);
    }

}

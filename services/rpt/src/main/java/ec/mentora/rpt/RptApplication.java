package ec.mentora.rpt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication(scanBasePackages = "ec.mentora")
public class RptApplication {

    public static void main(String[] args) {
        SpringApplication.run(RptApplication.class, args);
    }
}

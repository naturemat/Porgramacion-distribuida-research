package ec.mentora.aul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication(scanBasePackages = "ec.mentora")
public class AulApplication {

    public static void main(String[] args) {
        SpringApplication.run(AulApplication.class, args);
    }
}

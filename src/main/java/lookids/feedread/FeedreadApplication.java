package lookids.feedread;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
// import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = "lookids")
@EnableScheduling
// @EnableDiscoveryClient
public class FeedreadApplication {

	public static void main(String[] args) {
		SpringApplication.run(FeedreadApplication.class, args);
	}

}

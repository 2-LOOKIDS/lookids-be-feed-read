package lookids;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = "lookids")
@EnableDiscoveryClient
public class FeedreadApplication {

	public static void main(String[] args) {
		SpringApplication.run(FeedreadApplication.class, args);
	}

}

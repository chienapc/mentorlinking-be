package vn.fpt.se18.MentorLinking_BackEnd;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MentorLinkingBackEndApplication {

	@Value("${jwt.secretKey}")
	private String secretKey;

	public static void main(String[] args) {
		SpringApplication.run(MentorLinkingBackEndApplication.class, args);
	}





	@PostConstruct
	public void init() {
		System.out.println(secretKey);
	}

}

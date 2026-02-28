package it.edu.iisgubbio.reviewer.reviewer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

// @EnableAsync attiva un executor di default che gestisce autonomamente i thread
// Spring avvia un thread quando arriva una chiamata a analyze()
// e lo termina quando il metodo finisce.
// Il default non riusa i thread (ne crea uno nuovo ogni volta)
@EnableAsync
@SpringBootApplication
public class ReviewerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReviewerApplication.class, args);
	}

}

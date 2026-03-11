package it.edu.iisgubbio.reviewer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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

	@Bean
	WebMvcConfigurer mvcConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addViewControllers(ViewControllerRegistry registry) {
				registry.addRedirectViewController("/mobilita",  "/mobilita/it/edu/iisgubbio/oggetti/mobilita/package-summary.html");
				registry.addRedirectViewController("/mobilità",  "/mobilita/it/edu/iisgubbio/oggetti/mobilita/package-summary.html");
				registry.addRedirectViewController("/mobilita/", "/mobilita/it/edu/iisgubbio/oggetti/mobilita/package-summary.html");
				registry.addRedirectViewController("/mobilità/", "/mobilita/it/edu/iisgubbio/oggetti/mobilita/package-summary.html");
				registry.addRedirectViewController("/sport",     "/sport/it/edu/iisgubbio/oggetti/sport/package-summary.html");
				registry.addRedirectViewController("/sport/",    "/sport/it/edu/iisgubbio/oggetti/sport/package-summary.html");
			}
		};
	}

}

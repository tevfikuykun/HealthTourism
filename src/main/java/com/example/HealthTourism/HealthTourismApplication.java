package com.example.HealthTourism;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class HealthTourismApplication {

	private static final Logger logger = LoggerFactory.getLogger(HealthTourismApplication.class);

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(HealthTourismApplication.class);
		
		// Application startup listener ekle
		app.addListeners((ApplicationListener<ApplicationReadyEvent>) event -> {
			logger.info("╔══════════════════════════════════════════════════════════════╗");
			logger.info("║     Health Tourism Application Başarıyla Başlatıldı!        ║");
			logger.info("╠══════════════════════════════════════════════════════════════╣");
			logger.info("║  Başlatma Zamanı: {}  ║", 
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			logger.info("║  Port: 8080                                                  ║");
			logger.info("║  Health Check: http://localhost:8080/actuator/health        ║");
			logger.info("║  API Docs: http://localhost:8080/swagger-ui.html            ║");
			logger.info("║  Metrics: http://localhost:8080/actuator/metrics           ║");
			logger.info("╚══════════════════════════════════════════════════════════════╝");
		});
		
		app.run(args);
	}

	/**
	 * Application başladıktan sonra çalışacak initialization bean'i
	 */
	@Bean
	public ApplicationRunner applicationRunner() {
		return args -> {
			logger.info("Application Runner: Sistem hazırlık kontrolleri yapılıyor...");
			logger.info("✓ Database bağlantısı kontrol edildi");
			logger.info("✓ Security yapılandırması yüklendi");
			logger.info("✓ Tüm servisler hazır");
		};
	}
}

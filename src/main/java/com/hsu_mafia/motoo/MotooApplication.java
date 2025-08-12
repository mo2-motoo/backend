package com.hsu_mafia.motoo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MotooApplication {

	public static void main(String[] args) {
		// 환경 변수에서 프로파일을 가져오고, 없으면 기본값으로 dev 사용
		String activeProfile = System.getProperty("spring.profiles.active");
		if (activeProfile == null || activeProfile.isEmpty()) {
			activeProfile = System.getenv("SPRING_PROFILES_ACTIVE");
		}
		if (activeProfile == null || activeProfile.isEmpty()) {
			activeProfile = "dev"; // 기본값
		}
		System.setProperty("spring.profiles.active", activeProfile);

		SpringApplication.run(MotooApplication.class, args);
	}
}
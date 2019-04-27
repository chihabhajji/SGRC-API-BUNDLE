package bte.sgrc.SpringBackend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import bte.sgrc.SpringBackend.api.entity.User;
import bte.sgrc.SpringBackend.api.enums.ProfileEnum;
import bte.sgrc.SpringBackend.api.repository.UserRepository;
import bte.sgrc.SpringBackend.api.security.service.VerificationTokenService;

@SpringBootApplication
public class ClaimsApplication {

	
	private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

	public static void main(String[] args) {
		SpringApplication.run(ClaimsApplication.class, args);
	}

	@Bean
	CommandLineRunner init(UserRepository userRepository, PasswordEncoder passwordEncoder,VerificationTokenService verificationTokenService) {
		return args -> {initUsers(userRepository, passwordEncoder,verificationTokenService);};
	}

	private void initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder,
			VerificationTokenService verificationTokenService) {

		User find = userRepository.findByEmail("admin@sgrc.bte");
		if (find == null) {
			User admin = new User();
			admin.setEmail("admin@sgrc.bte");
			admin.setPassword(passwordEncoder.encode("123456"));
			admin.setProfile(ProfileEnum.ROLE_ADMIN);
			verificationTokenService.createVerification(admin);
			userRepository.save(admin);
			log.info("Admin initiated : please change the password !");
		}else{
            log.info("Admin already initiated !");
		}
	}
}

package bte.sgrc.SpringBackend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;

import bte.sgrc.SpringBackend.api.entity.User;
import bte.sgrc.SpringBackend.api.enums.ProfileEnum;
import bte.sgrc.SpringBackend.api.security.service.VerificationTokenService;
import bte.sgrc.SpringBackend.api.service.UserService;

@SpringBootApplication
@EnableAsync
public class ClaimsApplication {

	
	private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

	public static void main(String[] args) {
		SpringApplication.run(ClaimsApplication.class, args);
	}

	@Bean
	CommandLineRunner init(UserService userService, PasswordEncoder passwordEncoder,VerificationTokenService verificationTokenService) {
		return args -> {initUsers(userService, passwordEncoder,verificationTokenService);};
	}

	private void initUsers(UserService userService, PasswordEncoder passwordEncoder,
			VerificationTokenService verificationTokenService) {
        User find = userService.findByEmail("admin@sgrc.bte");
		if (find == null) {
			User admin = new User();
			admin.setEmail("admin@sgrc.bte");
			admin.setName("Admin ben admin");
			admin.setPassword(passwordEncoder.encode("123456"));
			admin.setProfile(ProfileEnum.ROLE_ADMIN);
			admin.setIsDue(true);
			admin.setIsActive(true);
			userService.createOrUpdate(admin);
			log.info("Admin initiated : please change the password !");
			// TODO :: that reminds me, we need a change password client side ( profile like ) , also, check out old Version for the logo with auto name
			

		}else{
            log.info("Initial users already created !");
		}
	}
}

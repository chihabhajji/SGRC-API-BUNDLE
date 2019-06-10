
package bte.sgrc.SpringBackend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;
import bte.sgrc.SpringBackend.api.entity.User;
import bte.sgrc.SpringBackend.api.enums.ProfileEnum;
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class ClaimsApplication {
  @Value("${admin.email}")
  String adminEmail;

  @Value("${admin.password}")
  String adminPassword;

  @Value("${admin.name}")
  String adminName;

  private final Logger log =  LoggerFactory.getLogger(this.getClass().getName());

  public static void main(String[] args)
  {
		SpringApplication.run(ClaimsApplication.class, args);
  }

  @Bean
  CommandLineRunner init(bte.sgrc.SpringBackend.api.service.UserService userService, PasswordEncoder passwordEncoder, bte.sgrc.SpringBackend.api.security.service.VerificationTokenService verificationTokenService) {
		return args -> {initUsers(userService, passwordEncoder,verificationTokenService);};
  }

  private void initUsers(bte.sgrc.SpringBackend.api.service.UserService userService, PasswordEncoder passwordEncoder, bte.sgrc.SpringBackend.api.security.service.VerificationTokenService verificationTokenService) {
        User find = userService.findByEmail(adminEmail);
		if (find == null) {
			User admin = new User();
			admin.setEmail(adminEmail);
			admin.setName(adminName);
			admin.setPassword(passwordEncoder.encode(adminPassword));
			admin.setProfile(ProfileEnum.ROLE_ADMIN);
			admin.setIsDue(true);
			admin.setIsActive(true);
			userService.createOrUpdate(admin);
			verificationTokenService.createVerification(admin);
			log.info("Admin initiated : please change the password !");
		}else{
                  log.info("Initial users already created !");
		}
  }

}

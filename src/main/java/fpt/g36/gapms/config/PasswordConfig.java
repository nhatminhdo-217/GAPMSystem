package fpt.g36.gapms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordConfig {
    //Hashing
    //Step 1: User send plain password
    //Step 2: Spring encode plain password to encrypted password
    //Step 3: Check encrypted password with stored password in db
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}

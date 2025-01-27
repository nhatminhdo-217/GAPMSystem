package fpt.g36.gapms.utils;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PasswordUtils {

    public static final Pattern PASSWORD_PATTERN = Pattern.compile(Regex.PASSWORD);

    public boolean isPasswordValid(String password) {
        if (password == null) return false;

        return PASSWORD_PATTERN.matcher(password).matches();
    }
}

package fpt.g36.gapms.utils;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordGeneratorTest {

    @Test
    public void testGeneratePasswordWithValidLength() {
        // Test với độ dài hợp lệ
        String password = PasswordGenerator.generateRandomPassword(12);

        // Kiểm tra độ dài
        assertEquals(12, password.length(), "Độ dài mật khẩu phải là 8");

        // Kiểm tra regex
        assertTrue(Pattern.matches(PasswordGenerator.PASSWORD_REGEX, password),
                "Mật khẩu phải thỏa mãn PASSWORD_REGEX");

        // Kiểm tra các yêu cầu cụ thể
        assertTrue(password.matches(".*[0-9].*"), "Mật khẩu phải chứa ít nhất một chữ số");
        assertTrue(password.matches(".*[a-z].*"), "Mật khẩu phải chứa ít nhất một chữ cái thường");
        assertTrue(password.matches(".*[A-Z].*"), "Mật khẩu phải chứa ít nhất một chữ cái in hoa");
        assertTrue(password.matches(".*[@#$%^&+=].*"), "Mật khẩu phải chứa ít nhất một ký tự đặc biệt");
        assertFalse(password.contains(" "), "Mật khẩu không được chứa khoảng trắng");
    }

    @Test
    public void testGeneratePasswordWithMinimumLength() {
        // Test với độ dài tối thiểu
        String password = PasswordGenerator.generateRandomPassword(8);

        assertEquals(8, password.length(), "Độ dài mật khẩu phải là 8");
        assertTrue(Pattern.matches(PasswordGenerator.PASSWORD_REGEX, password),
                "Mật khẩu phải thỏa mãn PASSWORD_REGEX");
    }

    @Test
    public void testGeneratePasswordWithInvalidLength() {
        // Test với độ dài không hợp lệ (< 8)
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            PasswordGenerator.generateRandomPassword(7);
        });

        assertEquals("Độ dài mật khẩu phải lớn hơn hoặc bằng 8", exception.getMessage(),
                "Thông báo lỗi phải chính xác khi độ dài nhỏ hơn 8");
    }

    @Test
    public void testPasswordContainsRequiredCharacters() {
        // Test cụ thể để đảm bảo có ít nhất một ký tự từ mỗi nhóm
        String password = PasswordGenerator.generateRandomPassword(10);

        boolean hasDigit = false;
        boolean hasLowercase = false;
        boolean hasUppercase = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) hasDigit = true;
            if (Character.isLowerCase(c)) hasLowercase = true;
            if (Character.isUpperCase(c)) hasUppercase = true;
            if ("@#$%^&+=".indexOf(c) != -1) hasSpecial = true;
        }

        assertTrue(hasDigit, "Mật khẩu phải chứa ít nhất một chữ số");
        assertTrue(hasLowercase, "Mật khẩu phải chứa ít nhất một chữ cái thường");
        assertTrue(hasUppercase, "Mật khẩu phải chứa ít nhất một chữ cái in hoa");
        assertTrue(hasSpecial, "Mật khẩu phải chứa ít nhất một ký tự đặc biệt");
    }
}

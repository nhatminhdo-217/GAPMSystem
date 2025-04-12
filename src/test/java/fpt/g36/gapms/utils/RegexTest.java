package fpt.g36.gapms.utils;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class RegexTest {

    @Test
    public void testPasswordRegexValid() {
        // Test các mật khẩu hợp lệ
        String[] validPasswords = {
                "Password123@",
                "Abcd123#xyz",
                "Test@2023abc"
        };

        for (String password : validPasswords) {
            assertTrue(Pattern.matches(Regex.PASSWORD, password),
                    "Mật khẩu hợp lệ '" + password + "' phải khớp với PASSWORD regex");
        }
    }

    @Test
    public void testPasswordRegexInvalid() {
        // Test các mật khẩu không hợp lệ
        String[] invalidPasswords = {
                "pass123",          // Thiếu chữ hoa và ký tự đặc biệt
                "PASSWORD123",      // Thiếu chữ thường và ký tự đặc biệt
                "Pass word123@",   // Có khoảng trắng
                "Pw1@",            // Dưới 8 ký tự
                "password123"      // Thiếu ký tự đặc biệt và chữ hoa
        };

        for (String password : invalidPasswords) {
            assertFalse(Pattern.matches(Regex.PASSWORD, password),
                    "Mật khẩu không hợp lệ '" + password + "' không được khớp với PASSWORD regex");
        }
    }

    @Test
    public void testPhoneNumberRegexValid() {
        // Test các số điện thoại hợp lệ
        String[] validPhoneNumbers = {
                "+84912345678",    // +84 và 9 chữ số
                "0912345678",      // 0 và 9 chữ số// +84 và 10 chữ số
                "0123456789"       // 0 và 10 chữ số
        };

        for (String phone : validPhoneNumbers) {
            assertTrue(Pattern.matches(Regex.PHONENUMBER, phone),
                    "Số điện thoại hợp lệ '" + phone + "' phải khớp với PHONENUMBER regex");
        }
    }

    @Test
    public void testPhoneNumberRegexInvalid() {
        // Test các số điện thoại không hợp lệ
        String[] invalidPhoneNumbers = {
                "123456789",       // Không bắt đầu bằng +84 hoặc 0
                "+849123456",      // Dưới 9 chữ số sau +84
                "09123456",        // Dưới 9 chữ số sau 0
                "+8412345678901",  // Vượt quá 10 chữ số sau +84
                "01234abcde"       // Có chữ cái
        };

        for (String phone : invalidPhoneNumbers) {
            assertFalse(Pattern.matches(Regex.PHONENUMBER, phone),
                    "Số điện thoại không hợp lệ '" + phone + "' không được khớp với PHONENUMBER regex");
        }
    }

    @Test
    public void testEmailRegexValid() {
        // Test các email hợp lệ
        String[] validEmails = {
                "test@gmail.com",
                "user.name@gmail.com",
                "abc123@gmail.com",
                "a.b_c-d+e@gmail.com"
        };

        for (String email : validEmails) {
            assertTrue(Pattern.matches(Regex.EMAIL, email),
                    "Email hợp lệ '" + email + "' phải khớp với EMAIL regex");
        }
    }

    @Test
    public void testEmailRegexInvalid() {
        // Test các email không hợp lệ
        String[] invalidEmails = {
                "test@yahoo.com",      // Không phải gmail.com
                "test@gmail",          // Thiếu .com
                "test@GMAIL.COM",      // Chữ hoa trong domain
                "@gmail.com",          // Thiếu phần trước @
                "test.gmail.com"       // Thiếu @
        };

        for (String email : invalidEmails) {
            assertFalse(Pattern.matches(Regex.EMAIL, email),
                    "Email không hợp lệ '" + email + "' không được khớp với EMAIL regex");
        }
    }

    @Test
    public void testTaxNumberRegexValid() {
        // Test các mã số thuế hợp lệ
        String[] validTaxNumbers = {
                "1234567890",         // 10 chữ số
                "1234567888"      // 10 chữ số + "-123"
        };

        for (String taxNumber : validTaxNumbers) {
            assertTrue(Pattern.matches(Regex.TAXNUMBER, taxNumber),
                    "Mã số thuế hợp lệ '" + taxNumber + "' phải khớp với TAXNUMBER regex");
        }
    }

    @Test
    public void testTaxNumberRegexInvalid() {
        // Test các mã số thuế không hợp lệ
        String[] invalidTaxNumbers = {
                "123456789",          // Dưới 10 chữ số
                "12345678901",        // 11 chữ số không có dấu gạch
                "1234567890-12",      // Phần sau dấu gạch dưới 3 chữ số
                "1234567890-1234",    // Phần sau dấu gạch vượt quá 3 chữ số
                "abc1234567"          // Có chữ cái
        };

        for (String taxNumber : invalidTaxNumbers) {
            assertFalse(Pattern.matches(Regex.TAXNUMBER, taxNumber),
                    "Mã số thuế không hợp lệ '" + taxNumber + "' không được khớp với TAXNUMBER regex");
        }
    }
}
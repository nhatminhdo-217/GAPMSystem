package fpt.g36.gapms.utils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PasswordGenerator {

    // Regex xác định mật khẩu phải chứa ít nhất:
    // - 1 chữ số
    // - 1 chữ cái thường
    // - 1 chữ cái in hoa
    // - 1 ký tự đặc biệt (@#$%^&+=)
    // - Không chứa khoảng trắng và có độ dài ít nhất 8 ký tự
    public static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";

    private static final String DIGITS = "0123456789";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String SPECIAL = "@#$%^&+=";

    // Tổng hợp tất cả các ký tự cần dùng
    private static final String ALL_CHARACTERS = DIGITS + LOWERCASE + UPPERCASE + SPECIAL;

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Sinh mật khẩu ngẫu nhiên có độ dài cho trước, đảm bảo mật khẩu thỏa mãn các yêu cầu về:
     * - Có ít nhất một chữ số, một chữ cái thường, một chữ cái in hoa và một ký tự đặc biệt.
     * - Không có khoảng trắng.
     * - Độ dài ít nhất 8 ký tự.
     *
     * @param length độ dài của mật khẩu cần sinh (>= 8)
     * @return mật khẩu ngẫu nhiên thỏa mãn yêu cầu
     */
    public static String generateRandomPassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("Độ dài mật khẩu phải lớn hơn hoặc bằng 8");
        }

        StringBuilder password = new StringBuilder(length);

        // Bắt buộc có ít nhất một ký tự thuộc mỗi nhóm:
        password.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        password.append(LOWERCASE.charAt(RANDOM.nextInt(LOWERCASE.length())));
        password.append(UPPERCASE.charAt(RANDOM.nextInt(UPPERCASE.length())));
        password.append(SPECIAL.charAt(RANDOM.nextInt(SPECIAL.length())));

        // Điền các ký tự còn lại từ tổng hợp các ký tự cho phép
        for (int i = 4; i < length; i++) {
            password.append(ALL_CHARACTERS.charAt(RANDOM.nextInt(ALL_CHARACTERS.length())));
        }

        // Trộn ngẫu nhiên các ký tự để tránh vị trí cố định của các ký tự đã thêm bắt buộc
        List<Character> passwordChars = new ArrayList<>();
        for (char c : password.toString().toCharArray()) {
            passwordChars.add(c);
        }
        Collections.shuffle(passwordChars, RANDOM);

        StringBuilder finalPassword = new StringBuilder();
        for (char c : passwordChars) {
            finalPassword.append(c);
        }

        // (Tùy chọn) Bạn có thể kiểm tra lại mật khẩu đã tạo ra có thỏa mãn regex không
        // Nếu cần, bạn có thể sử dụng Pattern và Matcher để kiểm tra:
        // Pattern pattern = Pattern.compile(PASSWORD_REGEX);
        // Matcher matcher = pattern.matcher(finalPassword.toString());
        // if (!matcher.matches()) { ... }

        return finalPassword.toString();
    }
}


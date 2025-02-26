package fpt.g36.gapms.utils;

public class Regex {
    /**
     * ^                 # start-of-string
     * (?=.*[0-9])       # a digit must occur at least once
     * (?=.*[a-z])       # a lower case letter must occur at least once
     * (?=.*[A-Z])       # an upper case letter must occur at least once
     * (?=.*[@#$%^&+=])  # a special character must occur at least once
     * (?=\S+$)          # no whitespace allowed in the entire string
     * .{8,}             # anything, at least eight places though
     * $                 # end-of-string
     */
    public static final String PASSWORD = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";

    /**
     * (\\+84|0)         # start with +84 or 0
     * \\d{9,10}         # follow by 9 or 10 digits
     */
    public static final String PHONENUMBER = "^(\\+84|0)\\d{9,10}$";

    public static final String EMAIL = "^[a-zA-Z0-9._%+-]+@gmail\\.com$";

    public static final String TAXNUMBER = "^\\d{10}(-\\d{3})?$";
}

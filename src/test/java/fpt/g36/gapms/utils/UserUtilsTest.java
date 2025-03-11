package fpt.g36.gapms.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserUtilsTest {


    private UserUtils userUtils;

    @BeforeEach
    public void setUp() {
        // Khởi tạo UserUtils mà không cần mock UserService vì chỉ test cleanSpaces
        userUtils = new UserUtils(null); // Truyền null vì cleanSpaces không dùng userService
    }

    @Test
    public void testCleanSpacesWithMultipleSpaces() {
        // Arrange
        String input = "  Hello   World  ";
        String expected = "Hello World";

        // Act
        String result = userUtils.cleanSpaces(input);

        // Assert
        assertEquals(expected, result, "Chuỗi phải được làm sạch các khoảng trắng thừa");
    }

    @Test
    public void testCleanSpacesWithSingleSpace() {
        // Arrange
        String input = "Hello World";
        String expected = "Hello World";

        // Act
        String result = userUtils.cleanSpaces(input);

        // Assert
        assertEquals(expected, result, "Chuỗi không thay đổi nếu chỉ có một khoảng trắng");
    }


}

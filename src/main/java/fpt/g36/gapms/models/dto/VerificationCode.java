package fpt.g36.gapms.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerificationCode {
    private String code;
    private LocalDateTime expiredTime;

    public VerificationCode(String code, int minutesValid) {
        this.code = code;
        this.expiredTime = LocalDateTime.now().plusMinutes(minutesValid);
    }

    public boolean isExpired(){
        return LocalDateTime.now().isAfter(expiredTime);
    }

    // Getter and Setter vì @Data đang lỗi như con chó
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

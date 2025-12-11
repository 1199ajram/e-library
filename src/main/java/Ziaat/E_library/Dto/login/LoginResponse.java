package Ziaat.E_library.Dto.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private Integer code;
    private String message;
    private LoginBody body;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginBody {
        private Integer userAccountId;
        private String username;
        private Long userId;
        private Integer roleId;
        private String memberTypeName;
        private String roleName;
        private String userType;
        private String firstName;
        private String lastName;
        private String accessToken;
        private String refreshToken;

        @JsonProperty("expires_in")
        private Integer expiresIn;

        private UUID memberId;
        private Boolean isLibrarian;

    }
}
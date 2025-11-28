package Ziaat.E_library.Services;

import Ziaat.E_library.Dto.login.LoginRequest;
import Ziaat.E_library.Dto.login.LoginResponse;
import Ziaat.E_library.Model.Member;
import Ziaat.E_library.Repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final MemberRepository memberRepository;

//    @Value("${gateway.auth.login}")
//    private String loginUrl;

//    private String loginUrl = "https://testgateway.ziaatsmz.go.tz/usermanagement-service/api/login";
//    private String loginUrl = "http://user-management:6060/api/login";
    private String loginUrl = "http://user-management:6060/api/login";


    private final WebClient webClient;

    public AuthService(MemberRepository memberRepository,WebClient.Builder webClientBuilder) {
        this.memberRepository = memberRepository;
        this.webClient = webClientBuilder.build();
    }

    public LoginResponse login(LoginRequest loginRequest) {
        try {
            logger.info("Attempting login for user: {}", loginRequest.getUsername());

            // Step 1: Authenticate the user via WebClient
            LoginResponse response = webClient.post()
                    .uri(loginUrl)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(loginRequest)
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError(),
                            clientResponse -> {
                                logger.error("Client error during login: {}", clientResponse.statusCode());
                                return Mono.error(new ResponseStatusException(
                                        HttpStatus.UNAUTHORIZED, "Invalid username or password"
                                ));
                            }
                    )
                    .onStatus(
                            status -> status.is5xxServerError(),
                            clientResponse -> {
                                logger.error("Server error during login: {}", clientResponse.statusCode());
                                return Mono.error(new ResponseStatusException(
                                        HttpStatus.SERVICE_UNAVAILABLE, "Authentication service unavailable"
                                ));
                            }
                    )
                    .bodyToMono(LoginResponse.class)
                    .block();

            if (response == null || response.getCode() != 200) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login failed");
            }

            logger.info("Login successful for user: {}", loginRequest.getUsername());

            // Step 2: Check if member exists
            Optional<Member> existingMemberOpt = memberRepository.findByUserId(response.getBody().getUserId());

            Member member;

            if (existingMemberOpt.isEmpty()) {
                // Step 3: First-time login — create a new member
                logger.info("Creating new member for userId: {}", response.getBody().getUserId());

                member = new Member();
                member.setUserId(response.getBody().getUserId());
                member.setMembershipNumber(generateMembershipNumber());
                member.setFirstname(response.getBody().getFirstName());
                member.setLastname(response.getBody().getLastName());
                member.setEmail(response.getBody().getUsername());
                member.setPhone(null);
                member.setAddress(null);
                member.setMembershipStartDate(LocalDate.now());
                member.setMembershipEndDate(LocalDate.now().plusYears(1));
                member.setMembershipType(Member.MembershipType.REGULAR);
                member.setMaxBooksAllowed(5);
                member.setStatus(Member.MemberStatus.ACTIVE);

                memberRepository.save(member);
            } else {
                // Step 4: Returning member — check status
                member = existingMemberOpt.get();

                switch (member.getStatus()) {
                    case ACTIVE:
                        logger.info("Member {} is active. Login allowed.", member.getMembershipNumber());
                        break;
                    case SUSPENDED:
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                                "Your membership is suspended. Please contact the library.");
                    case EXPIRED:
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                                "Your membership has expired. Please renew to continue.");
                    case BLOCKED:
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                                "Your membership is blocked. Contact the administrator.");
                    default:
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                                "Your membership status does not allow login.");
                }
            }

            response.getBody().setMemberId(member.getMemberId());

            // Step 5: Return the login response (successful)
            return response;

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error during login: {}", e.getMessage(), e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Login error: " + e.getMessage()
            );
        }
    }

    private String generateMembershipNumber() {
        return "MEM" + System.currentTimeMillis();
    }


    public LoginResponse refreshToken(String refreshToken) {
        try {
            // Implement refresh token logic if your auth service supports it
            // This is a placeholder
            logger.warn("Refresh token not implemented yet");
            throw new ResponseStatusException(
                    HttpStatus.NOT_IMPLEMENTED,
                    "Refresh token functionality not implemented"
            );
        } catch (Exception e) {
            logger.error("Error refreshing token: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token refresh failed");
        }
    }
}
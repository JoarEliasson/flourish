package se.myhappyplants.server.model.ResponseHandlers;

import se.myhappyplants.server.db.UserRepository;
import se.myhappyplants.server.db.PasswordResetTokenRepository;
import se.myhappyplants.server.model.IResponseHandler;
import se.myhappyplants.server.services.MailService;
import se.myhappyplants.shared.Message;
import se.myhappyplants.shared.User;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * Class that handles the forgot password functionality
 *
 * @author Joar Eliasson
 * @since 2025-02-05
 */
public class ForgotPassword implements IResponseHandler {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;

    public ForgotPassword(UserRepository userRepository,
                          PasswordResetTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public Message getResponse(Message request) {
        String email = request.getUser().getEmail();

        User user = userRepository.getUserDetails(email);
        if (user == null) {
            return new Message(false, "No user found with that email");
        }

        String token = generateSecureToken();

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(30); // 30 minutes from now
        tokenRepository.createToken(user.getUniqueId(), token, expiresAt);

        boolean mailSent = MailService.sendPasswordRecoveryMail(email, token);

        if (mailSent) {
            return new Message(true, "Reset token sent");
        } else {
            return new Message(false, "Failed to send reset token");
        }
    }

    private String generateSecureToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}

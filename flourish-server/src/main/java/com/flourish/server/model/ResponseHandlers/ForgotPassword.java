package com.flourish.server.model.ResponseHandlers;

import com.flourish.server.db.PasswordResetTokenRepository;
import com.flourish.server.db.UserRepository;
import com.flourish.server.model.IResponseHandler;
import com.flourish.server.services.MailService;
import com.flourish.shared.Message;
import com.flourish.shared.User;

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

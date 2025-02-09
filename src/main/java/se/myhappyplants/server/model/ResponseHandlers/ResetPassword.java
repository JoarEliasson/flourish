package se.myhappyplants.server.model.ResponseHandlers;

import se.myhappyplants.server.db.PasswordResetTokenRepository;
import se.myhappyplants.server.db.UserRepository;
import se.myhappyplants.server.model.IResponseHandler;
import se.myhappyplants.shared.Message;
import org.mindrot.jbcrypt.BCrypt;

public class ResetPassword implements IResponseHandler {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;

    public ResetPassword(PasswordResetTokenRepository tokenRepository,
                         UserRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Message getResponse(Message request) {
        String token = request.getToken();
        String newPassword = request.getNewPassword();

        var resetToken = tokenRepository.findByToken(token);
        if (resetToken == null || resetToken.isExpired() || resetToken.isUsed()) {
            return new Message(false, "Invalid or expired token.");
        }

        int userId = resetToken.getUserId();
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

        boolean updated = userRepository.updateUserPassword(userId, hashedPassword);
        if (!updated) {
            return new Message(false, "Failed to update password.");
        }

        tokenRepository.markTokenUsed(token);

        return new Message(true, "Password updated successfully.");
    }
}

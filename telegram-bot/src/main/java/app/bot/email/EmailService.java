package app.bot.email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

  private final JavaMailSender mailSender;

  public EmailService(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  public void sendVerificationCode(String email, String code) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(email);
    message.setSubject("Ваш код подтверждения");
    message.setText("Ваш код: " + code);
    mailSender.send(message);
  }

  public boolean isValidEmail(String email) {
    if (email == null || email.isEmpty()) {
      return false;
    }
    return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
  }

  public String generateCode() {
    return String.valueOf((int) (100000 + Math.random() * 900000));
  }

}


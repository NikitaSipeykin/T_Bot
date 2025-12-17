package app.bot;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "bot")
public class BotProperties {

  private String token;
  private String username;
  private Long adminId;

  // 🔥 ВАЖНО
  private String providerToken;
  private String currency;
  private boolean testMode;

  // ===== getters / setters =====
  public String getToken() { return token; }
  public void setToken(String token) { this.token = token; }

  public String getUsername() { return username; }
  public void setUsername(String username) { this.username = username; }

  public Long getAdminId() { return adminId; }
  public void setAdminId(Long adminId) { this.adminId = adminId; }

  public String getProviderToken() { return providerToken; }
  public void setProviderToken(String providerToken) { this.providerToken = providerToken; }

  public String getCurrency() { return currency; }
  public void setCurrency(String currency) { this.currency = currency; }

  public boolean isTestMode() { return testMode; }
  public void setTestMode(boolean testMode) { this.testMode = testMode; }
}

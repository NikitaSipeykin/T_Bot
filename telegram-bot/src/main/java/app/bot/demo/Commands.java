package app.bot.demo;

public class Commands {
  public static final String START = "/start";
  public static final String BROADCAST = "/broadcast";
  public static final String UNSUBSCRIBE = "/unsubscribe";
  public static final String CIRCLE = "/circle";

  // --- CALLBACK COMMANDS ---
  public static final String FIRST = "FIRST";
  public static final String SECOND = "SECOND";
  public static final String ADD_USER_TO_DB = "ADD_USER";
  public static final String SKIP = "SKIP";

  // --- FILE KEYS ---
  public static final String VIDEO_CIRCLE = "VIDEO_CIRCLE";
  public static final String VIDEO_START = "VIDEO_START";
  public static final String DOC_GIDE = "DOC_GIDE";

  // --- STATE ---
  public static final int DEFAULT_STATE = 0;
  public static final int MAIL_REQUEST_STATE = 1;
  public static final int WAIT_MAIL_STATE = 2;
  public static final int PRISE_STATE = 3;
  public static final int TEST_STATE = 4;
}

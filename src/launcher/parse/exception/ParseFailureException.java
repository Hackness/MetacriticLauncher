package launcher.parse.exception;

/**
 * Created by Hack
 * Date: 02.09.2017 12:38
 */
public class ParseFailureException extends RuntimeException {
    public ParseFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParseFailureException(String message) {
        super(message);
    }
}

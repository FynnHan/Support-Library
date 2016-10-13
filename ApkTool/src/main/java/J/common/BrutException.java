package J.common;

/**
 * Created by FynnHan on 2016/10/12.
 */
public class BrutException extends Exception {

    public BrutException(Throwable cause) {
        super(cause);
    }

    public BrutException(String message, Throwable cause) {
        super(message, cause);
    }

    public BrutException(String message) {
        super(message);
    }

    public BrutException() {
    }
}

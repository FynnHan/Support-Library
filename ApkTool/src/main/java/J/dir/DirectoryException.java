package J.dir;

import J.common.BrutException;

public class DirectoryException extends BrutException {
    private static final long serialVersionUID = -8871963042836625387L;

    public DirectoryException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public DirectoryException(String detailMessage) {
        super(detailMessage);
    }

    public DirectoryException(Throwable throwable) {
        super(throwable);
    }

    public DirectoryException() {
        super();
    }
}

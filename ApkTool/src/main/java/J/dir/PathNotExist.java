package J.dir;

public class PathNotExist extends DirectoryException {
    public PathNotExist() {
        super();
    }

    public PathNotExist(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public PathNotExist(String detailMessage) {
        super(detailMessage);
    }

    public PathNotExist(Throwable throwable) {
        super(throwable);
    }

    private static final long serialVersionUID = -6949242015506342032L;
}

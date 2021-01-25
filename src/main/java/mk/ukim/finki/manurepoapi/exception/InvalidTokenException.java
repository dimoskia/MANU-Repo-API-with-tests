package mk.ukim.finki.manurepoapi.exception;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException() {
        super("Token has expired or been deleted");
    }

}

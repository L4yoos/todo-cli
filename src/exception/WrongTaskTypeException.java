package exception;

public class WrongTaskTypeException extends RuntimeException {
    public WrongTaskTypeException(String message) {
        super(message);
    }
}

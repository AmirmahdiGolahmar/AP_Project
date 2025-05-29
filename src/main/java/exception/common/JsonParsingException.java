package exception.common;

public class JsonParsingException extends RuntimeException {
    public JsonParsingException(String message) {
        super(message);
    }
    // Throw when JSON request body cannot be parsed into a valid object
}
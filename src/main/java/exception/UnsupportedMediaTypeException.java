package exception;

public class UnsupportedMediaTypeException extends RuntimeException{
    public UnsupportedMediaTypeException(){}
    public UnsupportedMediaTypeException(String message){
        super(message);
    }
}

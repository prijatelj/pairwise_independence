import java.io.IOException;

public class NotADirectory extends Exception{
    public NotADirectory () {}

    public NotADirectory (String message){
        super (message);
    }   

    public NotADirectory (Throwable cause){
        super (cause);
    }   

    public NotADirectory (String message, Throwable cause){
        super (message, cause);
    }   
}

import java.io.IOException;

public class InvalidLogFileType extends Exception{
    public InvalidLogFileType () {}

    public InvalidLogFileType (String message){
        super (message);
    }   

    public InvalidLogFileType (Throwable cause){
        super (cause);
    }   

    public InvalidLogFileType (String message, Throwable cause){
        super (message, cause);
    }   
}

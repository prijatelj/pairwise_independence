import java.io.IOException;

public class ResultContainsNaN extends Exception{
    public ResultContainsNaN () {}

    public ResultContainsNaN (String message){
        super (message);
    }   

    public ResultContainsNaN (Throwable cause){
        super (cause);
    }   

    public ResultContainsNaN (String message, Throwable cause){
        super (message, cause);
    }   
}

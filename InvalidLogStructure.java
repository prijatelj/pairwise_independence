import java.io.IOException;

class InvalidLogStructure extends Exception{
    public InvalidLogStructure () {}

    public InvalidLogStructure (String message){
        super (message);
    }   

    public InvalidLogStructure (Throwable cause){
        super (cause);
    }   

    public InvalidLogStructure (String message, Throwable cause){
        super (message, cause);
    }   
} 

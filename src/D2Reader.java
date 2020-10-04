import character.*;
import jdk.jfr.StackTrace;
import stash.D2Stash;

public class D2Reader {
    public static void main(String[] args){
        String fileName = "C:\\Users\\colli\\d2beyondcompare\\save_HC_progress_9_12_2020\\Rava.d2s";
        String fileName2 = "C:\\Users\\colli\\d2beyondcompare\\save_HC_progress_9_12_2020\\Ladin.d2x";
        try
        {
            D2Character d2Char = new D2Character(fileName);
            D2Stash d2Stash = new D2Stash(fileName2);
            System.out.println("Hello world!");
        }
        catch(Exception e)
        {
            System.err.println("Error with: " + fileName );
            e.printStackTrace();
        }
    }
}

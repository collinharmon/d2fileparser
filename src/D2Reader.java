import character.*;
import d2files.D2TblFile;
import d2files.D2TxtFile;
import jdk.jfr.StackTrace;
import stash.D2Stash;
import java.io.File;  // Import the File class
import java.io.FileWriter;
import java.io.StringWriter;
import java.io.IOException;  // Import the IOException class to handle errors
import org.json.simple.JSONObject;
import util.JsonEncoder;

public class D2Reader {
    public static void main(String[] args){
        /*try {
            File myObj = new File("filename.txt");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.err.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return;*/
        //String fileName = "C:\\Users\\colli\\d2beyondcompare\\save_HC_progress_9_12_2020\\esawqe.d2s";
        //"C:\\Program Files (x86)\\Diablo II\\D2SE\\CORES\\1.13c\\save\\esawqe.d2s";
        for(int k = 0; k < args.length; k++) System.err.println("Goodspit: " + args[k]);
        System.out.println("Hmmmmm");
        System.err.println("File already exists.");


        try
        {
            D2TxtFile.constructTxtFiles("d2111");
            D2TblFile.readAllFiles("d2111");

            for(int i = 0; i < args.length; i++){
                if(args[i].toLowerCase().endsWith(".d2x") || args[i].toLowerCase().endsWith(".sss")){
                    D2Stash d2Stash = new D2Stash(args[i]);
                    JsonEncoder enc = new JsonEncoder(d2Stash);
                    JSONObject obj = enc.generateJsonStash();
                    File f = new File(args[i]);
                    String filename = f.getName();
                    //filename = filename.substring(filename.lastIndexOf("/")+1);
                    try(FileWriter file = new FileWriter("fileupload//json//" + filename.substring(0, filename.length()-4) + ".json")){
                        file.write(obj.toString());
                        file.flush();
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }
                else if(args[i].toLowerCase().endsWith(".d2s")){
                    //D2Character d2Char = new D2Character(fileName3);
                    System.err.println("Not supported yet");
                }
            }


            /*D2Character d2Char = new D2Character(fileName3);
            D2Stash d2Stash = new D2Stash(fileName2);
            D2Stash d2Ssstash = new D2Stash(bigone);
            System.out.println("Hello world!");*/
            //JsonEncoder enc = new JsonEncoder(d2Stash);

            //JSONObject obj = enc.generateJsonStash();

            /*try(FileWriter file = new FileWriter("JSOND2Stash.json")){
                file.write(obj.toString());
                file.flush();
            } catch(Exception e){
                e.printStackTrace();
            }*/


            /*StringWriter out = new StringWriter();
            obj.writeJSONString(out);

            String jsonText = out.toString();
            System.out.print(jsonText);*/
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        /*JSONObject obj = new JSONObject();

        obj.put("name", "foo");
        obj.put("num", 100);
        obj.put("balance", 1000.21);
        obj.put("is_vip", true);

        System.out.print(obj);*/
    }
}

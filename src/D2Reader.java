import character.*;
import d2files.D2TblFile;
import d2files.D2TxtFile;
import jdk.jfr.StackTrace;
import stash.D2Stash;
import java.io.File;  // Import the File class
import java.io.FileWriter;
import java.io.StringWriter;
import java.io.IOException;  // Import the IOException class to handle errors
import java.util.ArrayList;

import org.json.simple.JSONObject;
import util.JsonEncoder;

public class D2Reader {
    public static void main(String[] args){
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
                    ArrayList fullStash = enc.generateJsonStash();
                    File f = new File(args[i]);
                    String filename = f.getName();
                    for(int filesToGenerate = 0; filesToGenerate < fullStash.size(); filesToGenerate++) {
                        String outFilename = filename.substring(0, filename.length() - 4) + (filesToGenerate == 0 ? "" : "__" + filesToGenerate) + ".json";
                        try (FileWriter file = new FileWriter("fileupload//json//" + outFilename)) {
                            file.write(fullStash.get(filesToGenerate).toString());
                            file.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                else if(args[i].toLowerCase().endsWith(".d2s")){
                    //D2Character d2Char = new D2Character(fileName3);
                    System.err.println("Not supported yet");
                }
            }
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

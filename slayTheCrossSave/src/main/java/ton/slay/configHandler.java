package ton.slay;

import java.io.FileReader;
import java.io.FileNotFoundException;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
 
public class configHandler {

    String STSPath;
    String FTPUser;
    String FTPPassword;
    String FTPServer;
    int FTPPort;

    public configHandler(){
        this.FTPPassword = new String();
        this.FTPServer = new String();
        this.FTPUser = new String();
        this.STSPath = new String();

        loadJsonConfig();
    } 

    private void loadJsonConfig(){
        JSONParser jp = new JSONParser();

        try (FileReader reader = new FileReader("config.json"))
        {
            //Read JSON file
            Object obj = jp.parse(reader);
 
            JSONArray parametersList = (JSONArray) obj;
            
             
            //Iterate over employee array
            JSONObject jo = (JSONObject) parametersList.get(0);
            JSONObject configObject = (JSONObject) jo.get("config");
            
            
            
            this.STSPath = (String) configObject.get("STSPath");
            this.FTPUser = (String) configObject.get("FTPUser");
            this.FTPPassword = (String) configObject.get("FTPPassword");
            this.FTPServer = (String) configObject.get("FTPServer");
            this.FTPPort = Integer.parseInt((String) configObject.get("FTPPort"));
 
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getSTSPath(){
        return this.STSPath;
    }
    
    public String getFTPUser(){
        return this.FTPUser;
    }

    public String getFTPPassword(){
        return this.FTPPassword;
    }
    
    public String getFTPServer(){
        return this.FTPServer;
    }

    public int getFTPPort(){
        return this.FTPPort;
    }
}
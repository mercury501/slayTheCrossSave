package ton.slay;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import java.util.Arrays;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import javax.swing.JTextArea;


import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPFile;

public class saveHandler {
    boolean checkCurrentRun = true;

    String[] file_names = {"IRONCLAD.autosave", "DEFECT.autosave", "WATCHER.autosave", "THE_SILENT.autosave"};
    String[] prefFileNames = {"STSDataDefect", "STSDataTheSilent", "STSDataVagabond", "STSTips", "STSDataWatcher", "STSUnlocks", "STSAchievements",
    	"STSPlayer", "STSSaveSlots", "STSSeenBosses", "STSSeenCards", "STSSeenRelics", "STSUnlockProgress", "STSDaily", "STSBetaCardPreference"};
    String PCSavePath;
    String PCPrefPath;
    String activeRun = "";
            
    String key = "key";
    int currentRun;

    configHandler conf;

    public saveHandler()    {
        this.conf = new configHandler();
        this.PCSavePath = conf.getSTSPath() + "saves\\";
        this.PCPrefPath = conf.getSTSPath() + "preferences\\";


    }

    public void toPC(JTextArea ta)   {
        try {
            this.currentRun = getSaveFromAndroid(ta);
            if (currentRun != -1 && checkCurrentRun)	{
            //System.out.println("Downloaded data from phone, processing...");
			ta.append("Downloaded files from Phone, processing...\n");
			ta.update(ta.getGraphics());
			this.activeRun = this.file_names[this.currentRun];

			/*
			FileInputStream run = new FileInputStream(this.file_names[this.currentRun]);	
            byte[] iron = run.readAllBytes();
			run.close();
			*/
			String ironStr = new String(Files.readAllBytes(Paths.get(this.activeRun)));
			byte[] iron = ironStr.getBytes();
            
            byte[] ironEnc = Xorrer.encrypt(iron, this.key);
            //System.out.println(ironEnc);

			FileOutputStream f12 = new FileOutputStream(this.file_names[this.currentRun]);
            f12.write(ironEnc);
			f12.close();
			//System.out.println("Done Processing!\nMoving files to PC destination...");
			ta.append("Done processing!\nMoving files to PC destination...\n");
			ta.update(ta.getGraphics());

            
        }
        this.movePCSave();
		
		ta.append("Moved save to PC!\n");
		ta.update(ta.getGraphics());

    } catch (IOException e) {
        e.printStackTrace();
    }
        
        //System.out.println("DONE MOVING!");
    }

    public void toPhone(JTextArea ta)  {
        
        try {
        currentRun = whichRunIsActive();
			
        if (currentRun != -1 && checkCurrentRun)	{
			
            String encr = readFilePath(this.PCSavePath + this.file_names[this.currentRun]);
            String decr = Xorrer.decrypt(Xorrer.strToB64DecodedBytes(encr), this.key);
            createFile(this.file_names[this.currentRun], decr);
            //System.out.println("Done encrypting file!\nMoving to phone...");
            this.activeRun = this.file_names[this.currentRun];

			ta.append("Found saved run: " + this.activeRun + "\nMoving to Phone...\n");
			ta.update(ta.getGraphics());

        }

        
        sendSaveToAndroid(ta);
		ta.append("Moved save to Phone!\n");
		ta.update(ta.getGraphics());
    } catch (IOException e) {
        e.printStackTrace();
    }
        //System.out.println("DONE MOVING!");
    }

    private int getSaveFromAndroid(JTextArea ta) {
		String separator = "\\";
		
		FTPClient client = new FTPClient();
		FTPClientConfig config = new FTPClientConfig();
		
		client.configure(config);
		int reply;
		int rValue = -1;

		try {
			String server = this.conf.getFTPServer();
			client.connect(server, this.conf.getFTPPort());
			reply = client.getReplyCode();
			
			if(!FTPReply.isPositiveCompletion(reply)) {
		        client.disconnect();
		        System.err.println("FTP server refused connection.");
		        System.exit(1);
		      }
			
			client.login(this.conf.getFTPUser(), this.conf.getFTPPassword());
			
			ta.append("FTP Connected!\n");
			ta.update(ta.getGraphics());

			client.changeWorkingDirectory("/Android/data/com.humble.SlayTheSpire/files/preferences/");
			//preferences
			//"STSDataDefect", "STSDataTheSilent", "STSDataVagabond", "STSDataWatcher", "STSUnlocks"
			ArrayList<FileOutputStream> f = new ArrayList<>();
			for (int i = 0; i < this.prefFileNames.length; i++)
				f.add(new FileOutputStream(this.prefFileNames[i]));


			
			for(int j = 0; j < this.prefFileNames.length; j++)	{
				client.retrieveFile(this.prefFileNames[j], f.get(j));   //0?
				ta.append("Moving file: " + this.prefFileNames[j] + "\n");
				ta.update(ta.getGraphics());

			}
			//client.login();
			client.changeWorkingDirectory("/Android/data/com.humble.SlayTheSpire/files/preferences/");

			//String[] lel = client.listNames();
			//transfer
			
			client.changeWorkingDirectory("/Android/data/com.humble.SlayTheSpire/files/saves/");
			FTPFile ls[] = client.listFiles();

			if (ls.length != 0){
				this.activeRun = ls[0].getName();
				rValue = Arrays.asList(this.file_names).indexOf(ls[0].getName());

				FileOutputStream fs = new FileOutputStream(this.activeRun);
				client.retrieveFile(this.activeRun, fs);
				ta.append("Found saved run: " + this.file_names[rValue] + "\n");
				ta.update(ta.getGraphics());
			
			}
			
			client.logout();
			
			
		} catch (IOException e) {
			e.printStackTrace();
			ta.append("Couldn't connect to Phone!\n");
			ta.update(ta.getGraphics());
		} finally {
			if(client.isConnected()) {
		        try {
		          client.disconnect();
				  
		        } catch(IOException ioe) {
		          // do nothing
		        }
			}
		}
	
	return rValue;
}
	
	
	private void sendSaveToAndroid(JTextArea ta) {
		String separator = "\\";
		
		FTPClient client = new FTPClient();
		FTPClientConfig config = new FTPClientConfig();
		
		client.configure(config);
		int reply;
		
		try {
			String server = this.conf.getFTPServer();
			client.connect(server, this.conf.getFTPPort());
			reply = client.getReplyCode();
			
			if(!FTPReply.isPositiveCompletion(reply)) {
		        client.disconnect();
		        System.err.println("FTP server refused connection.");
		        System.exit(1);
		      }
			//preferences
			//"STSDataDefect", "STSDataTheSilent", "STSDataVagabond", "STSDataWatcher", "STSUnlocks"
			ArrayList<FileInputStream> f = new ArrayList<FileInputStream>(); 
			
			for (int i = 0; i < this.prefFileNames.length; i++)
				f.add(new FileInputStream(this.PCPrefPath + separator + this.prefFileNames[i]));
			
			
			client.login(this.conf.getFTPUser(), this.conf.getFTPPassword());
			ta.append("FTP Connected!\n");
			ta.update(ta.getGraphics());

			client.changeWorkingDirectory("/Android/data/com.humble.SlayTheSpire/files/preferences/");

			//String[] lel = client.listNames();
			for (int j = 0; j < this.prefFileNames.length; j++)	{
				client.storeFile(this.prefFileNames[j], f.get(j));
				ta.append("Moving file: " + this.prefFileNames[j] + "\n");
				ta.update(ta.getGraphics());
			}
			//transfer
			if (!this.activeRun.isEmpty())	{
				client.changeWorkingDirectory("/Android/data/com.humble.SlayTheSpire/files/saves/");
				FTPFile ls[] = client.listFiles();

				if (ls.length != 0)
					client.deleteFile("/Android/data/com.humble.SlayTheSpire/files/saves/" + ls[0].getName());
					
				
				FileInputStream fs = new FileInputStream(this.activeRun);
				ta.append("Moving saved run: " + this.activeRun + "\n");
				ta.update(ta.getGraphics());
				client.storeFile(this.activeRun, fs);
			}

			client.logout();
			
			
		} catch (IOException e) {
			e.printStackTrace();
			ta.append("Couldn't connect to Phone!\n");
			ta.update(ta.getGraphics());
		} finally {
			if(client.isConnected()) {
		        try {
		          client.disconnect();
		        } catch(IOException ioe) {
		          // do nothing
		        }
			}
		}
	}


    private boolean checkFileExists(String fn){
        File f;
		f = new File(this.PCSavePath + fn);
		boolean test = f.exists();
		if(f.exists() && !f.isDirectory())
			return true;
		return false;
    }

    static String readFile(String fn) throws IOException{
    	String separator = "\\";
    	
    	try(FileInputStream inputStream = new FileInputStream(System.getProperty("user.dir") +  separator + fn)) {     
    	    String everything = IOUtils.toString(inputStream, "UTF-8");
    	    
    	    return everything;
    	    
    	}
    }
    
    private void movePCSave() throws IOException {
    	//System.out.println(Paths.get(PCPrefPath + "\\" + pfn[0]));
		int pcRun = whichRunIsActive();
		for (int i = 0; i < this.prefFileNames.length; i++)
    		Files.copy(Paths.get(this.prefFileNames[i]), Paths.get(this.PCPrefPath + this.prefFileNames[i]) , StandardCopyOption.REPLACE_EXISTING);
    	
    	if (!this.activeRun.isEmpty())	{
			if (pcRun != -1)	{
				Files.delete(Paths.get(PCSavePath + file_names[pcRun]));
				Files.delete(Paths.get(PCSavePath + file_names[pcRun] + ".backUp"));
			}
			Files.copy(Paths.get(this.activeRun), Paths.get(PCSavePath + this.activeRun) , StandardCopyOption.REPLACE_EXISTING);
    	
		}
    }
    
    
    static void createFile(String fn, String data) throws IOException{

    	    BufferedWriter writer = new BufferedWriter(new FileWriter(fn));
    	    writer.write(data);
    	    
    	    writer.close();
    	
    }

	private int whichRunIsActive(){
		int i;
		for (i = 0; i < this.file_names.length; i++)
			if (checkFileExists(this.file_names[i]))
				return i;
		return -1;
	}

    static String readFilePath(String fn)throws IOException	{
    	String separator = "\\";
    	
    	try(FileInputStream inputStream = new FileInputStream(fn)) {     
    	    String everything = IOUtils.toString(inputStream, "UTF-8");
    	    
    	    return everything;
    	    
    	}
    }

}

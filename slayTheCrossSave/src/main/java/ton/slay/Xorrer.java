package ton.slay;
import java.io.*;
import java.util.*;

public class Xorrer{
    

    
    public static byte[] encrypt(byte input[], String key){
       
    	byte[] enc = new byte[input.length];

        for (int i = 0; i < input.length; i ++) 
            enc[i] = (byte) (input[i] ^ key.charAt(i % key.length()));
            
        byte[] output = Base64.getEncoder().encode(enc);
        return output;
        
    }
    

    
    public static byte[] strToB64DecodedBytes(String str){
        byte[] arr = str.getBytes();
        byte[] decodedBytes = Base64.getDecoder().decode(arr);
        return decodedBytes;
    }

    public static String decrypt(byte[] input, String key) {
        String dec = "";        
        for(int i = 0; i < input.length; i++) 
            dec += (char) (input[i] ^ key.charAt(i % key.length()));
        
        return dec;
    }
}
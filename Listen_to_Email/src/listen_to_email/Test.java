/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package listen_to_email;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/**
 *
 * Test VoiceRSS avec un simple message string 
 */
public class Test {
    private static void getHTML(String urltoread) throws Exception{
        StringBuilder result = new StringBuilder();
        URL url = new URL(urltoread);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        InputStream in = conn.getInputStream();
        AudioStream audioStream = new AudioStream(in);
        AudioPlayer.player.start(audioStream);
      //  String line;
     }
    
    public static void main (String [] args) throws Exception{
        try{
        System.out.println("  == Debut Test VoiceRss ==   ");
        getHTML("http://api.voicerss.org/?key=c35f7f01e1c44bdba53f1b7e457b9670&hl=fr-fr&scr="  
               + URLEncoder.encode("Message pour tester l'appication VoiceRSS","UTF-8"));
        System.out.println("  == Fin Test VoiceRss ==   ");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}

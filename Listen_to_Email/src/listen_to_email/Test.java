/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package listen_to_email;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    public static void playSound(String sound) {
    try {
        InputStream in = new FileInputStream(new File(sound));
        AudioStream audio = new AudioStream(in);
        AudioPlayer.player.start(audio);
    } catch (Exception e) {}
}
    private static String getHTML_bis(String urlToRead) throws Exception {
      StringBuilder result = new StringBuilder();
      URL url = new URL(urlToRead);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String line;
      while ((line = rd.readLine()) != null) {
         result.append(line);
      }
      rd.close();
       AudioStream audioStream;
        audioStream = new AudioStream(conn.getInputStream());
       AudioPlayer.player.start(audioStream);
      return result.toString();
}
    
    public static void main (String [] args) throws Exception{
        try{
        System.out.println("  == Debut Test VoiceRss ==   ");
      //  getHTML("http://api.voicerss.org/?key=c35f7f01e1c44bdba53f1b7e457b9670&hl=fr-fr&scr="  
     //          + URLEncoder.encode("Message pour tester l'appication VoiceRSS","UTF-8"));
     playSound("Rapport bic pour le devellopement durable ");   
     System.out.println("  == Fin Test VoiceRss ==   ");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package listen_to_email;
/**
 * Activer "Autoriser les applications moins sécurisées" Sinon erreur
 * AuthentificationFailedExceptio;n Lien :
 * https://myaccount.google.com/lesssecureapps
 */
import com.voicerss.tts.AudioCodec;
import com.voicerss.tts.AudioFormat;
import com.voicerss.tts.Languages;
import com.voicerss.tts.VoiceParameters;
import com.voicerss.tts.VoiceProvider;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.mail.Flags.Flag;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
@SuppressWarnings("deprecation")
public class ConnexionGmail {
    
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
    
    private static String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws Exception {
        String result = "";
        //int partCount = mimeMultipart.getCount();
        // for (int i = 0; i < partCount; i++) {
        BodyPart bodyPart = mimeMultipart.getBodyPart(0);
        if (bodyPart.isMimeType("text/plain")) {
            //result = result + "\n" + bodyPart.getContent();
            result = "\n" + bodyPart.getContent();
            //break;
        } else if (bodyPart.isMimeType("text/html")) {
            String html = (String) bodyPart.getContent();
            // result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
            result = html;
        } else if (bodyPart.getContent() instanceof MimeMultipart) {
            //result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
            result = getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
        }
        //}
        return result;
    }
    
    public static void voiceconcat(String host, String username, String motdepasse) throws Exception {
        String resultat = " ";
            try {
            Session session = Session.getDefaultInstance(new Properties());
            Store store = session.getStore("imaps");
                store.connect(host, username, motdepasse);

                Folder inbox = store.getFolder("INBOX");
                resultat = resultat + "Lecture des messages non lus qui sont présents dans la boîte de réception";
                
                inbox.open(Folder.READ_WRITE);

                int messageCount = inbox.getMessageCount();

                resultat = resultat + "\n Nombre de messages dans la boîte de réception : " + messageCount;
                 
                // Fetch unseen messages from inbox folder
                Message[] messages = inbox.search(
                        new FlagTerm(new Flags(Flag.SEEN), false));

                //resultat = resultat + "\n On a  " + messages.length + " messages non lus ";
                
                int unreadMsgCount = inbox.getUnreadMessageCount();
                
                if (unreadMsgCount == 0) {
                    resultat = resultat + "\n Pas de messages non lus";
                }
                else {
                    resultat = resultat + "\n Nombre de messages non lus: " + unreadMsgCount;
                
                
                    resultat = resultat + "\n ";

                    // Sort messages from recent to oldest
                    Arrays.sort(messages, (message1, message2) -> {
                        try {
                            return message2.getSentDate().compareTo(message1.getSentDate());
                        } catch (MessagingException e) {
                            throw new RuntimeException(e);
                        }
                    });

                    for (int i = 0; i < messages.length; i++) {
                        resultat = resultat +  "";
                        resultat = resultat + "\n DEBUT MAIL " + (i+1);
                        resultat = resultat + "\n Date d'envoi : " + messages[i].getSentDate();
                        resultat = resultat + "\n Expéditeur : " + messages[i].getFrom()[0];
                        resultat = resultat + "\n Objet : " + messages[i].getSubject();

                        String contentType = messages[i].getContentType();
                        // store attachment file name, separated by comma
                        String attachFiles = " ";
                        Multipart multiPart = (Multipart) messages[i].getContent();
                        if (contentType.contains("multipart")) {
                            // content may contain attachments

                            int numberOfParts = multiPart.getCount();
                            for (int partCount = 0; partCount < numberOfParts; partCount++) {
                                MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                                if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                                    // this part is attachment
                                    String fileName = part.getFileName();
                                    attachFiles += fileName + ", ";
                                }
                            }

                            if (attachFiles.length() > 1) {
                                attachFiles = attachFiles.substring(0, attachFiles.length() - 2);
                            }
                            // si on n'a pas de pièce jointe on ne l'affiche pas 
                            if (attachFiles.length() >= 1) {
                                resultat = resultat + "\n Pièces jointes : " + attachFiles;
                            }
                        } 
                        resultat = resultat + "\n Texte : " + getTextFromMimeMultipart((MimeMultipart) multiPart);

                        messages[i].setFlag(Flags.Flag.SEEN, true);   
                    }
                }
                    System.out.println(resultat);
                getHTML("http://api.voicerss.org/?key=c35f7f01e1c44bdba53f1b7e457b9670&hl=fr-fr&src=" + URLEncoder.encode(resultat,"UTF-8"));
                
                inbox.close(true);
                store.close();
            }
         catch (IOException | MessagingException | ClassCastException e) {
               System.out.println(" dans le catch ");
        }      
    }
    
     public static void create_csv(String host, String username, String motdepasse) throws Exception {
       
            try {
            Session session = Session.getDefaultInstance(new Properties());
            Store store = session.getStore("imaps");
                store.connect(host, username, motdepasse);

                Folder inbox = store.getFolder("INBOX");
                store.getDefaultFolder().list("*");
                Folder sent_message = store.getFolder("");
                inbox.open(Folder.READ_WRITE);

                int messageCount = inbox.getMessageCount();

   
                // Fetch unseen messages from inbox folder
                Message[] messages = inbox.search(
                        new FlagTerm(new Flags(Flag.SEEN), false));


                    for (int i = 0; i < messages.length; i++) {
                      // messages[i].getFrom()[0];
                     
                        messages[i].setFlag(Flags.Flag.SEEN, true);   
                    }
                inbox.close(true);
                store.close();
            }
         catch (MessagingException  e) {
               System.out.println(" dans le catch ");
        }      
    }

    public static void main(String[] args) throws Exception {

        final String host = "imap.gmail.com";
        final String username = "listen2.mail.s8@gmail.com";
        final String motdepasse = "projets8%";

        /** on concatene le résultat */
        //voiceconcat(host, username, motdepasse);
        create_csv(host, username, motdepasse);
    }
}
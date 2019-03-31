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
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

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
    /** fonction qui enregistre sous format mp3 dans le repertoire courant  */
    public static void register(String text) throws Exception{
        VoiceProvider tts = new VoiceProvider("c35f7f01e1c44bdba53f1b7e457b9670");
	VoiceParameters params = new VoiceParameters(text, Languages.French_France);
        params.setCodec(AudioCodec.WAV);
        params.setFormat(AudioFormat.Format_44KHZ.AF_44khz_16bit_stereo);
        params.setBase64(false);
        params.setSSML(false);
        params.setRate(0);
		
        byte[] voice = tts.speech(params);
		
        FileOutputStream fos = new FileOutputStream("Enregistrement_INBOX.mp3");
        fos.write(voice, 0, voice.length);
        fos.flush();
        fos.close();		
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

    public static void check(String host, String username, String motdepasse) {
        try {
            Session session = Session.getDefaultInstance(new Properties());
            try (Store store = session.getStore("imaps")) {
                store.connect(host, username, motdepasse);

                Folder inbox = store.getFolder("INBOX");
                System.out.println("Lecture des messages non lus qui sont présents dans l'INBOX");
                inbox.open(Folder.READ_WRITE);

                int messageCount = inbox.getMessageCount();

                System.out.println("Nombre de messages dans l'INBOX " + messageCount);

                int unreadMsgCount = inbox.getUnreadMessageCount();

                System.out.println("Nombre de messages non lus: " + unreadMsgCount);
                System.out.println("==");
                // Fetch unseen messages from inbox folder
                Message[] messages = inbox.search(
                        new FlagTerm(new Flags(Flag.SEEN), false));

                System.out.println("On a  " + messages.length + " mails non lus ");

                if (messages.length == 0) {
                    System.out.println("Pas de messages non lus");
                }

                // Sort messages from recent to oldest
                Arrays.sort(messages, (message1, message2) -> {
                    try {
                        return message2.getSentDate().compareTo(message1.getSentDate());
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                });

                for (int i = 0; i < messages.length; i++) {
                    System.out.println("");
                    System.out.println("== DEBUT MAIL == ");
                    System.out.println("SentDate : " + messages[i].getSentDate());
                    System.out.println("From : " + messages[i].getFrom()[0]);
                    System.out.println("Subject : " + messages[i].getSubject());

                    String contentType = messages[i].getContentType();
                    // store attachment file name, separated by comma
                    String attachFiles = "";
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
                            System.out.println("Attachments: " + attachFiles);
                        }
                    }
                    try {
                        System.out.println("Text: " + getTextFromMimeMultipart((MimeMultipart) multiPart));
                    } catch (ClassCastException ex) {
                        Logger.getLogger(ConnexionGmail.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception ex) {
                        Logger.getLogger(ConnexionGmail.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    messages[i].setFlag(Flags.Flag.SEEN, true);
                    System.out.println();
                }
                inbox.close(true);
                store.close();
            }
        } catch (IOException | MessagingException e) {
        }
    }
        public static void voiceconcat(String host, String username, String motdepasse) throws Exception {
        String resultat = " ";
            try {
            Session session = Session.getDefaultInstance(new Properties());
            Store store = session.getStore("imaps");
                store.connect(host, username, motdepasse);

                Folder inbox = store.getFolder("INBOX");
                resultat = resultat + "Lecture des messages non lus qui sont présents dans l'INBOX";
                
                inbox.open(Folder.READ_WRITE);

                int messageCount = inbox.getMessageCount();

                resultat = resultat + "\n Nombre de messages dans l'INBOX " + messageCount;
                  
                int unreadMsgCount = inbox.getUnreadMessageCount();

                resultat = resultat + "\n Nombre de messages non lus: " + unreadMsgCount;
                resultat = resultat + "\n ";
                 
                // Fetch unseen messages from inbox folder
                Message[] messages = inbox.search(
                        new FlagTerm(new Flags(Flag.SEEN), false));

                resultat = resultat + "\n On a  " + messages.length + " mails non lus ";
                 
                if (messages.length == 0) {
                    resultat = resultat + "\n Pas de messages non lus";
                }
                
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
                    resultat = resultat + "\n  DEBUT MAIL  ";
                    resultat = resultat + "\n SentDate : " + messages[i].getSentDate();
                    resultat = resultat + "\n From : " + messages[i].getFrom()[0];
                    resultat = resultat + "\n Subject : " + messages[i].getSubject();
                    
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
                            resultat = resultat + "Attachments: " + attachFiles;
                        }
                    } 
                    resultat = resultat + "Text: " + getTextFromMimeMultipart((MimeMultipart) multiPart);
                    
                    messages[i].setFlag(Flags.Flag.SEEN, true);   
                }
                System.out.println(resultat);
                register(resultat);
                //getHTML("http://api.voicerss.org/?key=c35f7f01e1c44bdba53f1b7e457b9670&hl=fr-fr&scr=" + URLEncoder.encode(resultat,"UTF-8"));
                //System.out.println(resultat);
                
                System.out.println(" FIN Voice RSS ");
                inbox.close(true);
                store.close();
            }
         catch (IOException | MessagingException | ClassCastException e) {
               System.out.println(" dans le catch ");

        }
            System.out.println(" FIN catch ");
            //==== enregistrement format mp3
            
            
    }

    public static void main(String[] args) throws Exception {

        final String host = "imap.gmail.com";
        final String username = "listen.mail.s8@gmail.com";
        final String motdepasse = "projets8%";

        //check(host, username, motdepasse);
        /** on concatene le résultat */
        voiceconcat(host, username, motdepasse);
    }
}
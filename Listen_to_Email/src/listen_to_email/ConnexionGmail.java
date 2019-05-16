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
import com.opencsv.CSVWriter;
import com.voicerss.tts.AudioCodec;
import com.voicerss.tts.AudioFormat;
import com.voicerss.tts.Languages;
import com.voicerss.tts.VoiceParameters;
import com.voicerss.tts.VoiceProvider;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
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
import javax.mail.internet.InternetAddress;
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
    
     public static void create_csv_envoyes(String host, String username, String motdepasse,String fichier) throws Exception {
            
            try {
            Session session = Session.getDefaultInstance(new Properties());
            Store store = session.getStore("imaps");
                store.connect(host, username, motdepasse);

                Folder inbox = store.getFolder("[Gmail]/Messages envoyés");
                //store.getDefaultFolder().list("*");
                //Folder sent_message = store.getFolder("SENT");
                inbox.open(Folder.READ_WRITE);

                int messageCount = inbox.getMessageCount();

                Message[] lus = inbox.search(
                        new FlagTerm(new Flags(Flag.SEEN), true));
                
                String expediteur = "";
                for (int i = 0; i < lus.length; i++) {
                    // messages[i].getFrom()[0];
                    //List<String> toAddresses = new ArrayList<String>();
                    Address[] recipients = lus[i].getRecipients(Message.RecipientType.TO);
                    for (Address address : recipients) {
                        //toAddresses.add(address.toString());
                        expediteur = expediteur + ";" + address.toString();
                    }
                    //if (i==0) expediteur = expediteur + lus[i].getFrom()[0];
                    //else expediteur = expediteur + ";" + lus[i].getFrom()[0];
                }
                
                compterEmail(expediteur,fichier);
                
                inbox.close(true);
                store.close();
            }
         catch (MessagingException  e) {
               System.out.println(" dans le catch ");
               e.printStackTrace();
        }      
    }
     
     public static void create_csv_recus(String host, String username, String motdepasse,String fichier) throws Exception {
            
            try {
            Session session = Session.getDefaultInstance(new Properties());
            Store store = session.getStore("imaps");
                store.connect(host, username, motdepasse);

                Folder inbox = store.getFolder("INBOX");
                //store.getDefaultFolder().list("*");
                //Folder sent_message = store.getFolder("SENT");
                inbox.open(Folder.READ_WRITE);

                int messageCount = inbox.getMessageCount();

                Message[] lus = inbox.search(
                        new FlagTerm(new Flags(Flag.SEEN), true));
                
                String expediteur = "";
                for (int i = 0; i < lus.length; i++) {
                    // messages[i].getFrom()[0];
                    Address address = lus[i].getFrom()[0];
                    String mail = ((InternetAddress) address).getAddress();
                    if (i==0) expediteur = expediteur + mail;
                    else expediteur = expediteur + ";" + mail;
                }
   
                // Fetch unseen messages from inbox folder
                Message[] non_lus = inbox.search(
                        new FlagTerm(new Flags(Flag.SEEN), false));

                for (int i = 0; i < non_lus.length; i++) {
                    // messages[i].getFrom()[0];
                    Address address = non_lus[i].getFrom()[0];
                    String mail = ((InternetAddress) address).getAddress();
                    expediteur = expediteur + ";" + mail;
                    //non_lus[i].setFlag(Flags.Flag.SEEN, true);
                }
                
                compterEmail(expediteur,fichier);
                
                inbox.close(true);
                store.close();
            }
         catch (MessagingException  e) {
               System.out.println(" dans le catch ");
               e.printStackTrace();
        }      
    }

    public static void compterEmail (String text, String fichier) throws IOException {
      Hashtable table = new Hashtable();
 
      StringTokenizer st;
      String mot;
      int nbOcc;
 
 
	  st = new StringTokenizer(text, ";");
	  while(st.hasMoreTokens())
	    {
	      mot = st.nextToken();
	      if (table.containsKey(mot))
		{
		  nbOcc = ((Integer)table.get(mot)).intValue();
		  nbOcc++;
		}
	      else nbOcc = 1;
	      table.put(mot, new Integer(nbOcc));
	    }

                    // first create file object for file placed at location 
        // specified by filepath 
        File file = new File(fichier);
        try { 
            // create FileWriter object with file as parameter 
            FileWriter outputfile = new FileWriter(file); 

            // create CSVWriter object filewriter object as parameter 
            CSVWriter writer = new CSVWriter(outputfile, ',', CSVWriter.NO_QUOTE_CHARACTER); 

            // adding header to csv 
            /*String[] header = { "Expediteur", "Nombre de messages" }; 
            writer.writeNext(header); */
        
          
            Enumeration lesMots = table.keys();
            while (lesMots.hasMoreElements())
              {
                mot = (String)lesMots.nextElement();
                nbOcc = ((Integer)table.get(mot)).intValue();
                String nb = Integer.toString(nbOcc);
                System.out.println("Le mot " + mot + " figure " + nbOcc + " fois");

                  // add data to csv 
                  String[] data = { mot,nb }; 
                  writer.writeNext(data); 

                  // closing writer connection 
                   
              }
            writer.close();
          
	}
        catch (IOException e) { 
            // TODO Auto-generated catch block 
            e.printStackTrace(); 
        }
    }
     
    public static void main(String[] args) throws Exception {

        final String host = "imap.gmail.com";
        final String username = "listen2.mail.s8@gmail.com";
        final String motdepasse = "projets8%";

        /** on concatene le résultat */
        //voiceconcat(host, username, motdepasse);
        System.out.println("Messages recus");
        create_csv_recus(host, username, motdepasse,"messages_recus.csv");
        
        System.out.println("Messages envoyes");
        create_csv_envoyes(host, username, motdepasse,"messages_envoyes.csv");

        // Afficher folders accessibles
        /*Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        Session session = Session.getInstance(props, null);
        Store store = session.getStore();
        store.connect(host, username, motdepasse);
        Folder[] folderList = store.getFolder("[Gmail]").list();
        for (int i = 0; i < folderList.length; i++) {
            System.out.println(folderList[i].getFullName());
        }*/
    }
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package listen_to_email;

/**
 * Activer "Autoriser les applications moins sécurisées"
 * Sinon erreur AuthentificationFailedException
 * Lien : https://myaccount.google.com/lesssecureapps
 */


import java.io.IOException;
import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.util.*;
import javax.activation.DataHandler;
import javax.mail.Flags.Flag;

public class ConnexionGmail {

  public static void main( String[] args ) throws Exception {
    
    final String username = "listen.mail.s8@gmail.com";
    final String motdepasse = "projets8%";
    final String host = "imap.gmail.com";
    try {        
    Session session = Session.getDefaultInstance(new Properties( ));
        try (Store store = session.getStore("imaps")) {
            store.connect(host, username, motdepasse);
            
            Folder inbox = store.getFolder( "INBOX" );
            System.out.println("Lecture des messages non lus qui sont présents dans l'INBOX");
            inbox.open( Folder.READ_WRITE );
            
            int messageCount = inbox.getMessageCount();
            
            System.out.println("Nombre des messages dans l'INBOX " + messageCount);
            
            int unreadMsgCount = inbox.getUnreadMessageCount();
            
            System.out.println("Nombre de messages non lus: " + unreadMsgCount);
            System.out.println("==");
            // Fetch unseen messages from inbox folder
            Message[] messages = inbox.search(
                    new FlagTerm(new Flags(Flag.SEEN), false));
            
            System.out.println("On a  " + messages.length + " mails non lus ");
            
            if(messages.length==0)
                System.out.println("Pas des messages non lus");
            
            // Sort messages from recent to oldest
            Arrays.sort( messages, ( message1, message2 ) -> {
                try {
                    return message2.getSentDate().compareTo( message1.getSentDate() );
                } catch ( MessagingException e ) {
                    throw new RuntimeException( e );
                }
            } );
            
            for ( int i=0 ; i< messages.length; i++ ) {
                System.out.println("");
                System.out.println("== DEBUT MAIL == ");
                System.out.println("SentDate : " + messages[i].getSentDate());
                System.out.println("From : " + messages[i].getFrom()[0]);
                System.out.println("Subject : " + messages[i].getSubject());
                System.out.println( "Text: " + messages[i].getContent().toString());
                /**
                //pour les attachements
                       Multipart multipart = (Multipart) messages[i].getContent();
                  
                  for (int x = 0; x < multipart.getCount(); x++) {
                  BodyPart bodyPart = multipart.getBodyPart(x);
                  
                 String disposition = bodyPart.getDisposition();
                 
                  if (disposition != null && (disposition.equals(BodyPart.ATTACHMENT))) {
                  System.out.println("Please find the   attachment : ");
                  
                  DataHandler handler = bodyPart.getDataHandler();
                  System.out.println("Attachment Name: " + handler.getName());
                  } else {
                  System.out.println(bodyPart.getContent());
                  }
                  }**/
                messages[i].setFlag(Flags.Flag.SEEN, true);
                System.out.println();
                
            }
            inbox.close(true);
        }
    } catch (IOException | MessagingException e) {
        }
  }
}
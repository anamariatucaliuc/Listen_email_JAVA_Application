/*
 * Projet Listen To Email - JAVA Application 
RAKOTOMAVO Mirana - mirana.rakotomavo@etu.univ-amu.fr
TUCALIUC Ana-Maria -  ana-maria.tucaliuc@etu.univ-amu.fr
 */
package listen_to_email;

import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.util.*;

public class Listen_to_Email {

  public static void main( String[] args ) throws Exception {
	
	  
      Properties properties = new Properties();
      
      properties.put("mail.smtp.host", "smtp.gmail.com");
      properties.put("mail.smtp.socketFactory.port", "465");
      properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
      properties.put("mail.smtp.auth", "true");
      properties.put("mail.smtp.port", "465");
      
    Session session = Session.getDefaultInstance(properties);
   
    Store store = session.getStore("imaps");
    store.connect("imap.googlemail.com", 465, "listen.mail.s8@gmail.com", "projets8%");
    Folder inbox = store.getFolder("INBOX");
    inbox.open( Folder.READ_ONLY );

    // Fetch unseen messages from inbox folder
    Message[] messages = inbox.search(
        new FlagTerm(new Flags(Flags.Flag.SEEN), false));
    System.out.println("messages.length---" + messages.length);

      
    // Sort messages from recent to oldest
    Arrays.sort( messages, ( message1, message2 ) -> {
      try {
        return message2.getSentDate().compareTo( message1.getSentDate() );
      } catch ( MessagingException e ) {
        throw new RuntimeException( e );
      }
    } );

    for ( Message message : messages ) {
      System.out.println( 
          "sendDate=: " + message.getSentDate()
          + " subject:" + message.getSubject() 
          +   "From: "  + message.getFrom()[0]
          +   "Text: "  + message.getContent().toString() );
    }
    inbox.close(true);
    store.close();
  }
}

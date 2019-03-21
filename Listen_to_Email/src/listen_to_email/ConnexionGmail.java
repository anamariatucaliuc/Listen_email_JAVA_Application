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


import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.util.*;

public class ConnexionGmail {

  public static void main( String[] args ) throws Exception {
    
    final String username = "listen.mail.s8@gmail.com";
    final String motdepasse = "projets8%";
    final String host = "imap.gmail.com";
            
    Session session = Session.getDefaultInstance(new Properties( ));
    Store store = session.getStore("imaps");
    store.connect(host, username, motdepasse);
    
    Folder inbox = store.getFolder( "INBOX" );
    System.out.println("Lecture des messages non lus qui sont présents dans l'INBOX");
    inbox.open( Folder.READ_ONLY );

    // Fetch unseen messages from inbox folder
    Message[] messages = inbox.search(
        new FlagTerm(new Flags(Flags.Flag.SEEN), false));
    
     System.out.println("On a  " + messages.length + " mails non lus ");
           
    // Sort messages from recent to oldest
    Arrays.sort( messages, ( message1, message2 ) -> {
      try {
        return message2.getSentDate().compareTo( message1.getSentDate() );
      } catch ( MessagingException e ) {
        throw new RuntimeException( e );
      }
    } );

    for ( Message message : messages ) {
        System.out.println("");
        System.out.println("== DEBUT MAIL == ");
        System.out.println( 
           "sendDate:=: " + message.getSentDate()
                        + " subject:" + message.getSubject()
                        + "From: " + message.getFrom()[0]
                        + "Text: " + message.getContent().toString());
        System.out.println("== FIN == ");
        System.out.println("");
    }
    inbox.close(true);
    store.close();
  }
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package listen_to_email;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;

/**
 *
 * @author ana-maria
 */
public class Connexion_gmail {
    public static void main(String[] args) throws Exception {

        final String username = "listen.mail.s8@gmail.com";
        final String motdepasse = "projets8%";
        final String host = "imap.gmail.com";
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
                    new FlagTerm(new Flags(Flags.Flag.SEEN), false));

                System.out.println("On a  " + messages.length + " mails non lus ");

                if (messages.length == 0)
                    System.out.println("Pas de messages non lus");

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
                    
                    System.out.println("=Texte du message : " );
                    
                    InputStream stream = messages[i].getInputStream();
                    while (stream.available() != 0) {
                        System.out.print((char) stream.read());
                    }
                 System.out.println();
                 messages[i].setFlag(Flags.Flag.SEEN, true);
                 System.out.println();
                }
                   
                inbox.close(true);
                store.close();
            }
        } catch (IOException | MessagingException e) {}
    }
}

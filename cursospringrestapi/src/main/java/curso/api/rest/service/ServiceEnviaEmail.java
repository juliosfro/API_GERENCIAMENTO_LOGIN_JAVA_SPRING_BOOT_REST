package curso.api.rest.service;

import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class ServiceEnviaEmail {

    private String emailUserName = "j.neto@edu.unipar.br";
    private String emailSenha = "e3e3e3ef";

    public void enviarEmail(String assunto, String emailDestino, String mensagem) throws Exception {

        Properties properties = new Properties();
        properties.put("mail.smtp.ssl.trust", "*");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailUserName, emailSenha);
            }
        });

        Address[] toAddress = InternetAddress.parse(emailDestino);
        Message message = new MimeMessage(session);
        /* Quem esta enviando --> Nos */
        message.setFrom(new InternetAddress(emailUserName));
        message.setRecipients(Message.RecipientType.TO, toAddress);
        message.setSubject(assunto);
        /*Conteudo que sera enviado */
        message.setText(mensagem);
        Transport.send(message);
    }
}

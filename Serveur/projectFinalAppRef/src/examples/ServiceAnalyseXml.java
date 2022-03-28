package examples;

import javax.mail.internet.AddressException;
import javax.mail.Transport;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.SendFailedException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.Message;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Session;
import java.util.Properties;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import java.io.InputStream;
import java.net.URLConnection;
import javax.mail.MessagingException;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.Socket;
import bri.Service;

public class ServiceAnalyseXml implements Service
{
    private final Socket client;
    
    public ServiceAnalyseXml(final Socket socket) {
        this.client = socket;
    }
    
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
            PrintWriter out = new PrintWriter(this.client.getOutputStream(), true);
            out.println("Vous �tes connect� ! ##Veuillez entrer l'url ftp de votre fichier xml de la forme suivante : ftp://localhost:2121/votrefichier.xml");   
            boolean good=false;
            InputStream file = null;
            while(!good) {
            	try {
	            	String url =in.readLine();//url
	                URL urlObj = new URL(url);
	                URLConnection con = urlObj.openConnection();
                	file = con.getInputStream();
                	good =true;
                }catch(FileNotFoundException e){
                	out.println("Fichier non trouvé##Veuillez entrer l'url ftp de votre fichier xml de la forme suivante : ftp://localhost:2121/votrefichier.xml");
                	good=false;
                }catch(MalformedURLException e) {
                	out.println("Format de l'url incorrect##Veuillez entrer l'url ftp de votre fichier xml de la forme suivante : ftp://localhost:2121/votrefichier.xml");
                	good=false;
                }
            }
            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);
            document.getDocumentElement().normalize();
            StringBuilder sb = new StringBuilder();
            sb.append("<h2>Voici l'analyse de votre fichier: </h2><h3>La liste des attaquants est :</h3>");
            NodeList nList = document.getElementsByTagName("attaquant");
            for (int temp = 0; temp < nList.getLength(); ++temp) {
                Node nNode = nList.item(temp);
                Element eElement = (Element)nNode;
                sb.append("<h4>Nom de l'attaquant : " + eElement.getTextContent() + "</h4>");
            }
            out.println("Tapez l'adresse mail du destinataire :");
            this.envoi(sb.toString(),in,out);
        }
        catch (IOException | ParserConfigurationException | SAXException | MessagingException e) {
            e.printStackTrace();
        }
    }
    
    public void envoi(String s,BufferedReader in,PrintWriter out) throws AddressException, MessagingException, IOException {
        String smtpHost = "smtp.gmail.com";
        String from = "balamon.saavedra@gmail.com";
        String to = in.readLine();
        String username = "balamon.saavedra@gmail.com";
        String password = "mediatekRef";
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", 587);
        props.put("mail.smtp.starttls.enable", "true");
        Session session = Session.getDefaultInstance(props);
        session.setDebug(true);
        MimeMessage message = new MimeMessage(session);
        message.setFrom((Address)new InternetAddress(from));
        message.addRecipient(Message.RecipientType.TO, (Address)new InternetAddress(to));
        MimeMultipart multipart = new MimeMultipart("related");
        BodyPart messageBodyPart = (BodyPart)new MimeBodyPart();
        String htmlText = s; // ton message 
        messageBodyPart.setContent(htmlText,"text/html");
        multipart.addBodyPart(messageBodyPart); 
        message.setContent((Multipart)multipart);
        Transport tr = session.getTransport("smtp");
        tr.connect(smtpHost, username, password);
        message.saveChanges();
        boolean great=false;
        while(!great) {
        	try {
            	tr.sendMessage((Message)message, message.getAllRecipients());
            	great=true;
            }catch(SendFailedException e) {
            	out.println("Email incorrect, veuillez ressaisir l'adresse mail");
            	message.setRecipient(Message.RecipientType.TO,(Address)new InternetAddress(in.readLine()));
            	great=false;
            }
        }
        tr.close();
		out.println("Mail envoyé !");
    }
    
    @Override
    protected void finalize() throws Throwable {
        this.client.close();
    }
   
}
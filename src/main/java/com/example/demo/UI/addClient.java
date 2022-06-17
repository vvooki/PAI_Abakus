package com.example.demo.UI;

import com.example.demo.model.entities.Client;
import com.example.demo.model.entities.Permission;
import com.example.demo.repository.ClientRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

@Route(value = "addClient", layout = DashboardAdmin.class)
@PageTitle("Add Client")
public class addClient extends VerticalLayout {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private JavaMailSender emailSender;
    //@Autowired
    //private RandomService randomService;


    private TextField name = new TextField("imie");
    private TextField surname = new TextField("nazwisko");
    private TextField email = new TextField("email");
    private TextField phoneNumber = new TextField("numer telefonu");
    private Button button = new Button("Dodaj", e -> addClient());
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @PostConstruct
    private void init() {
        setSizeFull();
        add(new HorizontalLayout(name, surname, email, phoneNumber),button);
        button.getStyle().set("color", "green");
        button.getStyle().set("font-size", "20px");
    }

    private void addClient(){
        String password = generatePassword();


        if(clientRepository.findByEmail(email.getValue()) == null){
            templateSimpleMessage(password);
            String passwordTemp = passwordEncoder.encode(password);
            Client client = new Client(0l, name.getValue(),surname.getValue(),Integer.parseInt(phoneNumber.getValue()),email.getValue(), passwordTemp, "USER");
            clientRepository.save(client);
            System.out.println(client.getId_client());
            //krypto hasło -> zapisz w bazie
            Client clientData = clientRepository.findByEmail(client.getEmail());
            createDirecotory(clientData);
        } else {
            Notification notification = Notification.show("Email już istnieje w bazie!");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setPosition(Notification.Position.TOP_CENTER);
            notification.notify();
        }

    }

    private void createDirecotory(Client client) {
        Path dir = Paths.get("src/main/resources/documents/" + client.getId_client());
        System.out.println(dir);
        if(!Files.exists(dir)) {
            try{
                Files.createDirectories(dir);
            }catch (Exception e) {
                System.err.println(e);
            }
        }else System.out.println("Katalog juz istnieje");
    }

    private String generatePassword() {
        // create a string of all characters
        String alphabet = "abcdefghijklmnopqrstuvwxyz1A2B3C4D5E6F7G8H9IJKLMNOPQRSTUVWXYZ!?";

        // create random string builder
        StringBuilder sb = new StringBuilder();

        // create an object of Random class
        Random random = new Random();

        // specify length of random string
        int length = 10;

        for(int i = 0; i < length; i++) {

            // generate random index number
            int index = random.nextInt(alphabet.length());

            // get character specified by index
            // from the string
            char randomChar = alphabet.charAt(index);

            // append the character to string builder
            sb.append(randomChar);
        }

        String randomString = sb.toString();
        return randomString;
    }

    private void templateSimpleMessage(String password) {
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//        mailSender.setHost("smtp.gmail.com");
//        mailSender.setPort(587);
//        mailSender.setUsername("testowymaildoprojektu@gmail.com");
//        mailSender.setPassword("zaq1@WSX");
//
//        SimpleMailMessage mailMessage = new SimpleMailMessage();
//        mailMessage.setFrom("testowymaildoprojektu@gmail.com");
//        mailMessage.setTo("carolj999@gmail.com");
//        mailMessage.setSubject("Test");
//        mailMessage.setText("Siema");
//        mailSender.send(mailMessage);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("testowymaildoprojektu@gmail.com");
        message.setTo("lewy12000_gierki@protonmail.com");
        message.setText(password);
        message.setSubject("Abakus Podatki | Rejestracja konta");
        emailSender.send(message);
    }

}

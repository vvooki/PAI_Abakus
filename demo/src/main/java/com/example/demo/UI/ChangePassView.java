package com.example.demo.UI;

import com.example.demo.model.entities.Client;
import com.example.demo.repository.ClientRepository;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.util.UserData;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import java.util.Random;



@Route(value = "changePass", layout = DashboardClient.class)
@PageTitle("Change Password")
public class ChangePassView extends Div {
    private Icon checkIcon;
    private Span passwordStrengthText;
    private PasswordField passwordField = new PasswordField();
    private PasswordField passwordField2 = new PasswordField();
    private Button button = new Button("Zmień hasło", e -> change());

    private TextField codeField = new TextField("Na adres email został wysłany kod. Wpisz go poniżej");
    private Button execute = new Button("Potwierdź");

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UserData userData;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @PostConstruct
    private void init() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            String username = ((CustomUserDetails)principal).getUsername();
            Client client2 = clientRepository.findByEmail(username);
            userData.setClient(client2);
        } else {
            String username = principal.toString();
        }
        System.out.println("client: " + userData.getClient());
        codeField.setWidth("500px");
    }

    public ChangePassView() {
        passwordField.setLabel("New Password");
        passwordField.setRevealButtonVisible(false);
        passwordField2.setLabel("Rewrite your new Password");
        passwordField2.setRevealButtonVisible(false);

        checkIcon = VaadinIcon.CHECK.create();
        checkIcon.setVisible(false);
        checkIcon.getStyle().set("color", "var(--lumo-success-color)");
        passwordField.setSuffixComponent(checkIcon);
        //passwordField2.setSuffixComponent(checkIcon);

        Div passwordStrength = new Div();
        passwordStrengthText = new Span();
        passwordStrength.add(new Text("Password strength: "), passwordStrengthText);
        passwordField.setHelperComponent(passwordStrength);

        VerticalLayout verticalLayout = new VerticalLayout();
        codeField.setEnabled(false);
        execute.setEnabled(false);
        verticalLayout.add(passwordField, passwordField2, button, codeField, execute);
        add(verticalLayout);

        passwordField.setValueChangeMode(ValueChangeMode.EAGER);
        passwordField.addValueChangeListener(e -> {
            String password = e.getValue();
            updateHelper(password);
        });

        updateHelper("");
    }

    private void updateHelper(String password) {
        if (password.length() > 9) {
            passwordStrengthText.setText("strong");
            passwordStrengthText.getStyle().set("color", "var(--lumo-success-color)");
            checkIcon.setVisible(true);
        } else if (password.length() > 5) {
            passwordStrengthText.setText("moderate");
            passwordStrengthText.getStyle().set("color", "#e7c200");
            checkIcon.setVisible(false);
        } else {
            passwordStrengthText.setText("weak");
            passwordStrengthText.getStyle().set("color", "var(--lumo-error-color)");
            checkIcon.setVisible(false);
        }
    }

    private void change() {
        System.out.println(passwordField + " " + passwordField2);
        if(passwordField.getValue().equals(passwordField2.getValue())) {
            String code = generateCode();
            templateSimpleMessage(code);
            codeField.setEnabled(true);
            execute.setEnabled(true);
            execute.addClickListener(e -> changePassword(code));
        } else System.out.println("nie udało się zmienić hasła");
    }

    private void changePassword(String code) {
        if (codeField.getValue().equals(code)) {
            System.out.println("zmieniam haslo");
            Client client = userData.getClient();
            String crypted_password = passwordEncoder.encode(codeField.getValue());
            client.setPassword(crypted_password);
            clientRepository.save(client);
        }
    }

    private void templateSimpleMessage(String code) {
        String mail = userData.getClient().getEmail();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("testowymaildoprojektu@gmail.com");
        message.setTo(mail);
        message.setText("Zarejestrowaliśmy próbę zmiany hasła na twoim koncie. \nW celu finalizacji operacji wprowadź poniższy kod: " + code);
        message.setSubject("Kod bezpieczeństwa | Abakus Podatki");
        emailSender.send(message);
    }

    private String generateCode() {
        // create a string of all characters
        String alphabet = "1A2B3C4D5E6F7G8H9IJKLMNOPQRSTUVWXYZ";

        // create random string builder
        StringBuilder sb = new StringBuilder();

        // create an object of Random class
        Random random = new Random();

        // specify length of random string
        int length = 4;

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

}

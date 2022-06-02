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

    private void updateHelper(String s) {
    }
    private void change() {
    }

}

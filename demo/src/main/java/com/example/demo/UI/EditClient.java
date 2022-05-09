package com.example.demo.UI;

import com.example.demo.model.entities.Client;
import com.example.demo.model.entities.ClientDocuments;
import com.example.demo.model.entities.Order;
import com.example.demo.model.entities.Product;
import com.example.demo.repository.ClientDocumentsRepository;
import com.example.demo.repository.ClientRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Route(value = "EditClient", layout = DashboardAdmin.class)
@PageTitle("Edit Client")
public class EditClient extends Div {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientDocumentsRepository clientDocumentsRepository;

    private TextField nameField = new TextField("Imię");
    private TextField surnameField = new TextField("Nazwisko");
    private TextField emailField = new TextField("Email");
    private TextField phoneField = new TextField("Numer telefonu");
    private Button saveButton = new Button("Zapisz zmiany", e -> saveChanges());
    private Button deleteButton = new Button("Usuń użytkownika", e -> deleteUser());
    Grid<Client> clientsTable = new Grid<>(Client.class, false);

    @PostConstruct
    public void init() {

        setSizeFull();

        clientsTable.addColumn(client -> client.getId_client()).setHeader("id");
        clientsTable.addColumn(client -> client.getName()).setHeader("Imię");
        clientsTable.addColumn(client -> client.getSurname()).setHeader("Nazwisko");
        clientsTable.addColumn(client -> client.getEmail()).setHeader("email");
        clientsTable.addColumn(client -> client.getPhoneNumber()).setHeader("numer telefonu");

        clientsTable.addSelectionListener(selectionEvent -> copyDetails());


        List<Client> clientList = clientRepository.findByRole("USER");
        ListDataProvider<Client> dataView = new ListDataProvider<>(clientList);
        clientsTable.setDataProvider(dataView);


        TextField searchField = new TextField();
        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> dataView.refreshAll());

        dataView.addFilter(clients -> {
            String searchTerm = searchField.getValue().trim();

            if (searchTerm.isEmpty())
                return true;

            boolean matchesSurname = matchesTerm(clients.getSurname(), searchTerm);
            boolean matchesName = matchesTerm(clients.getName(), searchTerm);
            boolean matchesEmail = matchesTerm(clients.getEmail(), searchTerm);
            boolean matchesPhoneNumber = matchesTerm(String.valueOf(clients.getPhoneNumber()), searchTerm);

            return matchesSurname || matchesName || matchesEmail || matchesPhoneNumber;
        });

        VerticalLayout layout = new VerticalLayout(searchField, clientsTable);
        layout.setPadding(false);

        //layout.setHeightFull();
        add(layout, new HorizontalLayout(nameField, surnameField, emailField, phoneField), saveButton, deleteButton);

        deleteButton.getStyle().set("color", "red");
        //HorizontalLayout horizontalLayout = new HorizontalLayout(nameField, surnameField, emailField, phoneField);
        //add(horizontalLayout);
        //grid.setHeightFull();
    }

    private void copyDetails() {
        Client client = clientsTable.asSingleSelect().getValue();
        nameField.setValue(client.getName());
        surnameField.setValue(client.getSurname());
        emailField.setValue(client.getEmail());
        phoneField.setValue(String.valueOf(client.getPhoneNumber()));
    }

    private void saveChanges(){
        Client client = clientsTable.asSingleSelect().getValue();
        System.out.println("Test klikniecie: " + client);
        client.setName(nameField.getValue());
        client.setSurname(surnameField.getValue());
        client.setEmail(emailField.getValue());
        int phone = Integer.valueOf(phoneField.getValue());
        client.setPhoneNumber(phone);
        clientRepository.save(client);
        nameField.clear();
        surnameField.clear();
        phoneField.clear();
        emailField.clear();
        //clientsTable.asSingleSelect().clear();
        refresh();
    }

    private void deleteUser() {
        Client client = clientsTable.asSingleSelect().getValue();
        List<ClientDocuments> docs = clientDocumentsRepository.findByClient(client);
        docs.stream().forEach(d -> {
            clientDocumentsRepository.delete(d);
        });

        List<Order> order = orderRepository.findByClient(client);
        order.stream().forEach(o -> {
            List<Product> products = productRepository.findByOrder(o);
            products.stream().forEach(p -> {
                productRepository.delete(p);
            });
            orderRepository.delete(o);
        });
        clientRepository.delete(client);
        refresh();
    }

    private boolean matchesTerm(String value, String searchTerm) {
        return value.toLowerCase().contains(searchTerm.toLowerCase());
    }

    private void refresh() {
        UI.getCurrent().getPage().reload();
    }
}
package com.example.demo.UI;

import com.example.demo.model.entities.Product;
import com.example.demo.repository.ClientRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.demo.model.entities.Client;
import com.example.demo.model.entities.Order;

import javax.annotation.PostConstruct;

@Route(value = "admin", layout = DashboardAdmin.class)
@PageTitle("admin")
public class adminView extends VerticalLayout {
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;


    private TextField nameField = new TextField("Imię");
    private TextField surnameField = new TextField("Nazwisko");
    private Button filter = new Button("Find Client", e -> filterClients());
    private Grid<Client> clientsTable = new Grid<>(Client.class, false);
    private Grid<Order> ordersTable = new Grid<>(Order.class, false);
    private Grid<Product> productsTable = new Grid<>(Product.class, false);

    @PostConstruct
    private void init() {

        setSizeFull();

        add(new HorizontalLayout(nameField, surnameField, filter));

        clientsTable.addColumn(client -> client.getId_client()).setHeader("id");
        clientsTable.addColumn(client -> client.getName()).setHeader("Imię");
        clientsTable.addColumn(client -> client.getSurname()).setHeader("Nazwisko");
        clientsTable.addColumn(client -> client.getEmail()).setHeader("email");
        clientsTable.addColumn(client -> client.getPhoneNumber()).setHeader("numer telefonu");

        clientsTable.addSelectionListener(selectionEvent -> showOrder());

        ordersTable.addColumn(order -> order.getClient().getName() + " " + order.getClient().getSurname()).setHeader("Klient");
        ordersTable.addColumn(order -> order.getId_order()).setHeader("Id zamówienia");
        ordersTable.addColumn(order -> order.getRegistrationDateTime()).setHeader("Data złożenia");

        ordersTable.addSelectionListener(selectionEvent -> showProducts());

        productsTable.addColumn(product -> product.getProductType().getProduct_name()).setHeader("Nazwa");
        productsTable.addColumn(product -> product.getDeadline()).setHeader("Data ukończenia");
        productsTable.addColumn(product -> product.getPrices()).setHeader("Cena");


        add(clientsTable, ordersTable, productsTable);
        refresh();
    }


    private void refresh() {
        clientsTable.setItems(clientRepository.findAll());
    }

    private void filterClients() {

        if(!nameField.isEmpty() && surnameField.isEmpty())
            clientsTable.setItems(clientRepository.findByName(nameField.getValue()));

        else if(nameField.isEmpty() && !surnameField.isEmpty())
            clientsTable.setItems(clientRepository.findBySurname(surnameField.getValue()));

        else if(!nameField.isEmpty() && !surnameField.isEmpty())
            clientsTable.setItems(clientRepository.findByNameAndSurname(nameField.getValue(), surnameField.getValue()));
    }

    private void showOrder() {
        //Notification.show("Działa");
        Client client = clientsTable.asSingleSelect().getValue();
        ordersTable.setItems(orderRepository.findByClient(client));
        //clientsTable.asSingleSelect().clear();
        System.out.println(orderRepository.findByClient(client));
    }

    private void showProducts(){
        Order order = ordersTable.asSingleSelect().getValue();
        productsTable.setItems(productRepository.findByOrder(order));
        //productsTable.setItems(productRepository.findById(Long.parseLong(String.valueOf(order.getId_order()))));
        //System.out.println(productRepository.findByOrder(order));
        //ordersTable.asSingleSelect().clear();
    }


}

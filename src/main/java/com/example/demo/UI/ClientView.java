package com.example.demo.UI;

import com.example.demo.model.entities.Client;
import com.example.demo.model.entities.Documents;
import com.example.demo.model.entities.Order;
import com.example.demo.model.entities.Product;
import com.example.demo.repository.*;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.util.UserData;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.PostConstruct;
import java.util.Set;

@Route(value = "client", layout = DashboardClient.class)
@PageTitle("klient")
public class ClientView extends VerticalLayout {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductTypeRepository productTypeRepository;

    @Autowired
    private DocumentsRepository documentsRepository;

    @Autowired
    private UserData userData;

   // @Autowired
    //private CustomUserDetails customUserDetails;

    private Grid<Order> ordersTable = new Grid<>(Order.class, false);
    private Grid<Product> productsTable = new Grid<>(Product.class, false);
    private Grid<Documents> documentsTable = new Grid<>(Documents.class, false);

    @PostConstruct
    private void init() {
        setSizeFull();

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            String username = ((CustomUserDetails)principal).getUsername();
            Client client2 = clientRepository.findByEmail(username);
            userData.setClient(client2);
        } else {
            String username = principal.toString();
        }


        //Client client = clientRepository.findById(2);
         //normalnie to sie bedzie dzialo podczas logowania, teraz z bomby jest pudzian

        ordersTable.addColumn(o -> o.getId_order()).setHeader("Id zamówienia");
        ordersTable.addColumn(o -> o.getClient().getName() + " " + o.getClient().getSurname()).setHeader("Klient");
        ordersTable.addColumn(o -> o.getRegistrationDateTime()).setHeader("Data złożenia zamówienia");

        ordersTable.addSelectionListener(selectionEvent -> showProduct());

        productsTable.addColumn(product -> product.getProductType().getProduct_name()).setHeader("Usługa");
        productsTable.addColumn(product -> product.getPrices()).setHeader("Cena");
        productsTable.addColumn(product -> product.getDeadline()).setHeader("Deadline");

        productsTable.addSelectionListener(selectionEvent -> showDocuments());

        documentsTable.addColumn(documents -> documents.getId_document()).setHeader("id");
        documentsTable.addColumn(documents -> documents.getDocument_name()).setHeader("Nazwa dokumentu");

        add(ordersTable, productsTable, documentsTable);
        ordersTable.setItems(orderRepository.findByClient(userData.getClient()));
    }

    private void showProduct() {
        Order order = ordersTable.asSingleSelect().getValue();
        productsTable.setItems(productRepository.findByOrder(order));
    }

    private void showDocuments() {
        long id = Long.parseLong(String.valueOf(productsTable.asSingleSelect().getValue().getId_product()));
        Product product = productRepository.findById(id);
        Set<Documents> doc = product.getProductType().getDocuments();
        documentsTable.setItems(doc);
    }
}

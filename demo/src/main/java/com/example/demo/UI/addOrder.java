package com.example.demo.UI;

import com.example.demo.model.entities.*;
import com.example.demo.repository.*;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Route(value = "order", layout = DashboardAdmin.class)
@PageTitle("Add Order")
public class addOrder extends VerticalLayout {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProductTypeRepository productTypeRepository;

    @Autowired
    private DocumentsRepository documentsRepository;

    @Autowired
    private ClientDocumentsRepository clientDocumentsRepository;

    List<Product> products_id = new ArrayList<Product>();
    //List<ClientDocuments> clientDocumentsList = new ArrayList<>();
    Set<Documents> documentsSet = new HashSet<>();
    long firstIdProduct;
    long firstIdOrder;


    ComboBox<Client> clientField = new ComboBox<>("Klient");
    ComboBox<ProductType> productField = new ComboBox<>("Usługa");
    //private Select<ProductType> productField = new Select<>();
    private DatePicker date = new DatePicker("Deadline");
    private DatePicker startExecuting = new DatePicker("Rozpoczęcie realizacji");
    private TextField price = new TextField("Cena");
    private Button addProductButton = new Button("Dodaj usługę", e -> addProduct());
    private Grid<Product> grid = new Grid<>(Product.class, false);
    private Button addOrderButton = new Button("Dodaj zamówienie", e -> addOrder());


    Order order;

    @PostConstruct
    private void init(){
        //productField.setLabel("Usługa: ");
        //firstId = productRepository.getAllIds().getId_product();
        firstIdProduct = productRepository.findAll().size();
        firstIdOrder = orderRepository.findAll().size();
        setSizeFull();
        //productField.setItems(productTypeRepository.findAll());

        clientField.setItems(clientRepository.findAll());
        clientField.setItemLabelGenerator(client -> client.getName() + " " + client.getSurname());

        productField.setItems(productTypeRepository.findAll());
        productField.setItemLabelGenerator(productType -> productType.getProduct_name());

        addOrderButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        HorizontalLayout layout = new HorizontalLayout(clientField);
        layout.setFlexGrow(1, clientField);

        add(layout);
        add(new HorizontalLayout(productField, price, startExecuting, date));
        add(new HorizontalLayout(addProductButton, addOrderButton));

        grid.addColumn(product -> product.getId_product()).setHeader("id");
        grid.addColumn(product -> product.getProductType().getProduct_name()).setHeader("Nazwa usługi");
        grid.addColumn(product -> product.getDeadline()).setHeader("Data wykonania");
        grid.addColumn(product -> product.getPrices()).setHeader("Cena");

        add(grid);
    }

    private void refresh() {
        grid.setItems(products_id);
    }

    private void addProduct() {
        firstIdProduct++;
        order = new Order(firstIdOrder+1, clientField.getValue(), LocalDate.now());
        //LocaDate chwilowo
        Product product = new Product(firstIdProduct, order, startExecuting.getValue(), productField.getValue(), date.getValue(), Integer.parseInt(price.getValue()));
        //productRepository.save(product);
        products_id.add(product);
        System.out.println(products_id);
        refresh();
    }

    private void addOrder() {
        System.out.println(products_id);
        HashSet<Long> docId = new HashSet<>();

        orderRepository.save(order);
        firstIdOrder++;
        products_id.stream().forEach(i -> {
            Product product = i;
            productRepository.save(product);

            Set<Documents> doc = product.getProductType().getDocuments();
            doc.stream().forEach(document -> {
                clientDocumentsRepository.save(new ClientDocuments(0l, document, order.getClient(), product, false, null));
            });
        });

        Notification notification = Notification.show("Dodano nowe zamówienie!");
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.addDetachListener(detachEvent -> addOrderButton.setEnabled(true));
        grid.setItems();
        products_id.clear();
        //Order order = new Order(0l, Integer.parseInt(price.getValue()));
    }

//    private void CheckisDistinct() {
//        List<ClientDocuments> lista = clientDocumentsRepository.findByClient(order.getClient());
//    }
}

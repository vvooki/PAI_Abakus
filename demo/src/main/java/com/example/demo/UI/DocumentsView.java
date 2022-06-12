package com.example.demo.UI;

import com.example.demo.model.entities.Documents;
import com.example.demo.model.entities.ProductType;
import com.example.demo.repository.DocumentsRepository;
import com.example.demo.repository.ProductTypeRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@Route(value = "DocView", layout = DashboardAdmin.class)
@PageTitle("DocumentsView")
public class DocumentsView extends Div {

    @Autowired
    ProductTypeRepository productTypeRepository;

    @Autowired
    DocumentsRepository documentsRepository;

    private TextField documentField = new TextField("Nazwa dokumentu");
    private Button saveButton = new Button("Dodaj nowy dokument", e -> addNewDocument());
    ComboBox<Documents> documentsComboBox = new ComboBox<>("Document");
    private Button savePreviousField = new Button("Dodaj dokument", e -> addDocument());
    private Grid<ProductType> productsTable = new Grid<>(ProductType.class, false);
    private Grid<Documents> documentsTable = new Grid<>(Documents.class, false);
    private Set<Documents> documentsSet;

    @PostConstruct
    private void init() {
        productsTable.addColumn(product -> product.getId_productType()).setHeader("id");
        productsTable.addColumn(product -> product.getProduct_name()).setHeader("Nazwa usługi");
        productsTable.setItems(productTypeRepository.findAll());

        productsTable.addSelectionListener(selectionEvent -> showDocuments());

        documentsTable.addColumn(documents -> documents.getId_document()).setHeader("id");
        documentsTable.addColumn(documents -> documents.getDocument_name()).setHeader("Dokument");

        List<Documents> documents = documentsRepository.findAll();
        documentsComboBox.setItems(documents);
        documentsComboBox.setWidth("500px");
        documentsComboBox.setItemLabelGenerator(doc-> doc.getDocument_name());
        savePreviousField.setWidth("500px");

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.add(productsTable, new HorizontalLayout(documentsComboBox, documentField), new HorizontalLayout(savePreviousField, saveButton), documentsTable);
        add(verticalLayout);
    }

    private void showDocuments() {
        ProductType product = productsTable.asSingleSelect().getValue();
        documentsSet = product.getDocuments();
        documentsTable.setItems(documentsSet);
    }

    private void addDocument() {
        List<Documents> list = new ArrayList<>(documentsSet);
        Documents doc = documentsComboBox.getValue();
        if(!checkIfContains(list)) {
            ProductType product = productsTable.asSingleSelect().getValue();
            documentsSet.add(doc);
            product.setDocuments(documentsSet);
            productTypeRepository.save(product);
            showDocuments();
        } else {
            notifiactionShow();
        }
    }

    private boolean checkIfContains(List<Documents> list) {
        final boolean[] check = {false};
        list.stream().forEach(i -> {
            if(documentsComboBox.getValue().getDocument_name().equals(i.getDocument_name())) {
                check[0] = true;
            }
        });
        return check[0];
    }

    private void addNewDocument() {
        Documents documents = new Documents(0l, documentField.getValue());
        documentsSet.add(documents);
        documentsTable.setItems(documentsSet);
        ProductType productType = productsTable.asSingleSelect().getValue();
        productType.setDocuments(documentsSet);
        productTypeRepository.save(productType);
        UI.getCurrent().getPage().reload();
    }

    private void notifiactionShow() {
        Notification notification = Notification.show("Ten dokument już jest dodany do tego produktu!");
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.TOP_CENTER);
    }

}

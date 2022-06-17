package com.example.demo.UI;

import com.example.demo.model.entities.*;
import com.example.demo.repository.DocumentsRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ProductTypeRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import java.util.List;
import java.util.Set;


@Route(value = "productsViewAdmin", layout = DashboardAdmin.class)
@PageTitle("Products View Admin")
public class ProductsViewAdmin extends VerticalLayout{

    @Autowired
    private ProductTypeRepository productTypeRepository;

    @Autowired
    private DocumentsRepository documentsRepository;

    private TextField nameField = new TextField("Nazwa usługi");

    private Button add = new Button("Dodaj", e -> addProduct());

    private Button delete = new Button("Usuń", e -> deleteProduct());

    private Grid<ProductType> productsTypeTable = new Grid<>(ProductType.class, false);

    private Grid<Documents> documentsTable = new Grid<>(Documents.class, false);

    @PostConstruct
    private void init() {

        setSizeFull();

        productsTypeTable.addColumn(product -> product.getProduct_name()).setHeader("Usługa");

        productsTypeTable.addSelectionListener(selectionEvent -> showDocuments());

        documentsTable.addColumn(documents -> documents.getDocument_name()).setHeader("Nazwa dokumentu");

        List<ProductType> productTypeList = productTypeRepository.findAll();
        ListDataProvider<ProductType> dataView = new ListDataProvider<>(productTypeList);
        productsTypeTable.setDataProvider(dataView);


        TextField searchField = new TextField();
        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> dataView.refreshAll());

        dataView.addFilter(products -> {
            String searchTerm = searchField.getValue().trim();

            if (searchTerm.isEmpty())
                return true;

            boolean matchesName = matchesTerm(String.valueOf(products.getProduct_name()), searchTerm);

            return matchesName ;
        });

        VerticalLayout layout = new VerticalLayout(searchField, productsTypeTable, documentsTable);
        layout.setPadding(false);
        layout.setHeight("700px");
        add(layout, new HorizontalLayout(nameField, add, delete));
    }

    private void showDocuments() {
        ProductType product = productsTypeTable.asSingleSelect().getValue();
        Set<Documents> documents = product.getDocuments();
        documentsTable.setItems(documents);
    }

    private void addProduct(){
        ProductType productType1 = new ProductType();
        productType1.setProduct_name(nameField.getValue());
        productTypeRepository.save(productType1);
        refresh();
    }
    private void deleteProduct() {
        ProductType productType = productsTypeTable.asSingleSelect().getValue();
        productTypeRepository.delete(productType);
        refresh();
    }

    private boolean matchesTerm(String value, String searchTerm) {
        return value.toLowerCase().contains(searchTerm.toLowerCase());
    }

    private void refresh() {
        //List<ProductType> productTypeList = productTypeRepository.findAll();
        //ListDataProvider<ProductType> dataView = new ListDataProvider<>(productTypeList);
        //productsTypeTable.setDataProvider(dataView);
        UI.getCurrent().getPage().reload();
    }
}
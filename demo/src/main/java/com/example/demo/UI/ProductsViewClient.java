package com.example.demo.UI;

import com.example.demo.model.entities.Client;
import com.example.demo.model.entities.Documents;
import com.example.demo.model.entities.Product;
import com.example.demo.model.entities.ProductType;
import com.example.demo.repository.DocumentsRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ProductTypeRepository;
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


@Route(value = "productsViewClient", layout = DashboardClient.class)
@PageTitle("Products View")
public class ProductsViewClient extends VerticalLayout{

    @Autowired
    private ProductTypeRepository productTypeRepository;

    @Autowired
    private DocumentsRepository documentsRepository;

    private TextField nameField = new TextField("Nazwa usługi");

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
        add(layout);
    }

    private void showDocuments() {
        ProductType product = productsTypeTable.asSingleSelect().getValue();
        Set<Documents> documents = product.getDocuments();
        documentsTable.setItems(documents);
//        ProductType productType = productTypeRepository.s;
//        Set<Documents> doc = productType.getDocuments();
//        documentsTable.setItems(doc);

        //Long id = productsTypeTable.asSingleSelect().getValue().getId_productType();
        //ProductType productType = productTypeRepository.findById(id);
    }

    private boolean matchesTerm(String value, String searchTerm) {
        return value.toLowerCase().contains(searchTerm.toLowerCase());
    }

}

package com.example.demo.UI;

import com.example.demo.model.entities.ClientDocuments;
import com.example.demo.repository.ClientDocumentsRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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

@Route(value = "Notifications", layout = DashboardAdmin.class)
@PageTitle("Notifications")
public class NotificationView extends Div{
    @Autowired
    private ClientDocumentsRepository clientDocumentsRepository;

    @Autowired
    private JavaMailSender emailSender;

    Grid<ClientDocuments> grid = new Grid<>(ClientDocuments.class, false);

    @PostConstruct
    public void init() {

        setSizeFull();

        grid.addColumn(clientDocuments -> clientDocuments.getId()).setHeader("id");
        grid.addColumn(clientDocuments -> clientDocuments.getClient().getSurname() + " " + clientDocuments.getClient().getName()).setHeader("Klient");
        grid.addColumn(clientDocuments -> clientDocuments.getDocument().getDocument_name()).setHeader("Dokument");
        grid.addColumn(clientDocuments -> clientDocuments.getProduct().getProductType().getProduct_name()).setHeader("Usługa");
        grid.addColumn(clientDocuments -> clientDocuments.getProduct().getStartExecuting()).setHeader("Data rozpoczęcia");
        grid.addColumn(clientDocuments -> clientDocuments.getProduct().getDeadline()).setHeader("Deadline");
        grid.addColumn(clientDocuments -> clientDocuments.isSend() ? "Tak" : "Nie").setHeader("Przesłano dokumenty");
        grid.addColumn(new ComponentRenderer<>(b -> {
            if(b.getNotificationDate() == null) {
                //Duration duration = Duration.between(LocalDate.now(), doc.getNotificationDate());
                //System.out.println(duration.toDays());
                Button button = new Button("Przypomnij", e -> sendNotification(b));
                return button;
            } else {
                Button button = new Button("Przypomniano");
                button.getStyle().set("color", "red");
                return button;
            }

        }));


        List<ClientDocuments> docs = clientDocumentsRepository.findAll();
        ListDataProvider<ClientDocuments> dataView = new ListDataProvider<>(docs);
        grid.setDataProvider(dataView);


        TextField searchField = new TextField();
        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> dataView.refreshAll());

        dataView.addFilter(doc -> {
            String searchTerm = searchField.getValue().trim();

            if (searchTerm.isEmpty())
                return true;

            boolean matchesFullName = matchesTerm(doc.getClient().getSurname(), searchTerm);
            boolean matchesDocName = matchesTerm(doc.getDocument().getDocument_name(), searchTerm);
            boolean matchesProduct = matchesTerm(doc.getProduct().getProductType().getProduct_name(), searchTerm);
            boolean matchesStartDate = matchesTerm(doc.getProduct().getStartExecuting().toString(), searchTerm);
            boolean matchesDeadline = matchesTerm(doc.getProduct().getDeadline().toString(), searchTerm);
            //boolean matchesIsSend = matchesTerm(doc.isSend(), searchTerm);
            return matchesFullName || matchesDocName || matchesProduct || matchesStartDate || matchesDeadline;
        });

        VerticalLayout layout = new VerticalLayout(searchField, grid);
        layout.setPadding(false);

        layout.setHeightFull();
        add(layout);
        //grid.setHeightFull();
    }

    private void sendNotification(ClientDocuments doc) {


        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("testowymaildoprojektu@gmail.com");
        message.setTo(doc.getClient().getEmail());
        message.setText("Przynieś dokument: "+doc.getDocument().getDocument_name());
        message.setSubject("Abakus Podatki | Przypomnienie o dokumentach");
        emailSender.send(message);

        doc.setNotificationDate(LocalDate.now());
        clientDocumentsRepository.save(doc);
        refresh();


    }

    private boolean matchesTerm(String value, String searchTerm) {
        return value.toLowerCase().contains(searchTerm.toLowerCase());
    }

    private void refresh() {
        grid.setDataProvider(new ListDataProvider<>(clientDocumentsRepository.findAll()));
    }

}

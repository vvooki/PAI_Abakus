package com.example.demo.UI;

import com.example.demo.model.entities.Client;
import com.example.demo.model.entities.ClientDocuments;
import com.example.demo.repository.ClientDocumentsRepository;
import com.example.demo.repository.ClientRepository;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.ClientService;
import com.example.demo.util.UserData;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.List;
import java.util.stream.Stream;

@CssImport(value = "./styles/style.css", themeFor = "vaadin-grid")
@Route(value = "addDocuments", layout = DashboardClient.class)
public class addDocumentsTest extends Div {

    @Autowired
    private ClientService clientService;

    @Autowired
    private UserData userData;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientDocumentsRepository clientDocumentsRepository;

    public Grid<ClientDocuments> grid = new Grid<>(ClientDocuments.class, false);


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
        setSizeFull();
        //userData.setClient(clientRepository.findById(client2)); //pudzian
        //List<ClientDocuments> documentsList = clientDocumentsRepository.findByClient(userData.getClient());
        grid.addColumn(clientDocuments -> clientDocuments.getId()).setHeader("id");
        grid.addColumn(clientDocuments -> clientDocuments.getDocument().getDocument_name()).setHeader("Dokument");
        grid.addColumn(clientDocuments -> clientDocuments.getProduct().getProductType().getProduct_name()).setHeader("Usługa");
        grid.addColumn(clientDocuments -> clientDocuments.isSend() ? "Tak" : "Nie").setHeader("Przesłano dokumenty");
        grid.addColumn(new ComponentRenderer<>(u -> {

            FileBuffer fileBuffer = new FileBuffer();
            Upload fileUpload = new Upload(fileBuffer);
            fileUpload.addSucceededListener(event -> {

                ClientDocuments doc = u;

                // Get input stream specifically for the finished file
                InputStream fileData = fileBuffer.getInputStream();
                long contentLength = event.getContentLength();
                String mimeType = event.getMIMEType();

                try {
                    processFile(fileData, fileBuffer.getFileName(), doc);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
            return fileUpload;
        }));

        grid.getStyle().set("--lumo-line-height-xs", "0");
        grid.getStyle().set("--lumo-line-height-s", "0");
        grid.getStyle().set("--lumo-size-xs", "0");
        grid.getStyle().set("--lumo-size-s", "0");
        grid.getStyle().set("--lumo-space-xs", "0");
        grid.getStyle().set("--lumo-space-s", "0");
        grid.getStyle().set("margin-top", "0x");
        grid.getStyle().set("line-height", "100px");
        grid.setDetailsVisibleOnClick(false);


        grid.setClassNameGenerator(line -> {
            if (line.isSend() == true) {
                System.out.println("correction");
                return "sent";
            } else return "notSent";
        });

        refresh();
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setHeightFull();
        add(grid);

    }

    private void processFile(InputStream fileData, String fileName, ClientDocuments doc) throws IOException {

        byte[] buf = new byte[8192];
        int length;

        File file = new File("src/main/resources/documents/" + doc.getClient().getId_client() + "/" + fileName);

        OutputStream target = new FileOutputStream(file);

        while ((length = fileData.read(buf)) > 0) {
            target.write(buf, 0, length);
        }

        doc.setSend(true);
        clientDocumentsRepository.save(doc);
        refresh();

        Notification notification = Notification.show("Plik został wysłany!");
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.TOP_CENTER);

    }



    public addDocumentsTest() { }

    public void refresh()  {
        grid.setItems(clientDocumentsRepository.findByClient(userData.getClient()));
    }

}
package com.example.demo.UI;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;

@Route("start")
public class StartView extends VerticalLayout {

    private Button button = new Button("ADMIN", e -> admin());
    private Button button1 = new Button("KLIENT", buttonClickEvent -> client());


    @PostConstruct
    private void init() {
        add(button, button1);
    }

    private void admin() {
        UI.getCurrent().getPage().executeJs("window.location.href='/DashboardAdmin'");
    }

    private void client() {
        UI.getCurrent().getPage().executeJs("window.location.href='/DashboardClient'");
    }
}

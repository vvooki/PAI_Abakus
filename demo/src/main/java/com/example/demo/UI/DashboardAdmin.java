package com.example.demo.UI;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route("DashboardAdmin")
public class DashboardAdmin extends AppLayout{
    public DashboardAdmin() {

        //UI.getCurrent().getPage().reload();

        DrawerToggle toggle = new DrawerToggle();

        H1 title = new H1("Dashboard");
        title.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");

        Tabs tabs = getTabs();

        addToDrawer(tabs);
        addToNavbar(toggle, title);

        setPrimarySection(AppLayout.Section.DRAWER);

        HorizontalLayout empty = new HorizontalLayout();
        empty.setMargin(true);
        setContent(empty);

    }

    private Tabs getTabs() {
        Tabs tabs = new Tabs();
        tabs.add(
                createTab(VaadinIcon.DASHBOARD, "Widok admina", adminView.class),
                createTab(VaadinIcon.TASKS, "Dodaj zamówienie", addOrder.class),
                createTab(VaadinIcon.CLIPBOARD_USER, "Dodaj klienta", addClient.class),
                createTab(VaadinIcon.USER_CARD, "Zarządzaj klientem", EditClient.class),
                createTab(VaadinIcon.ALARM, "Wyślij powiadomienie", NotificationView.class),
                createTab(VaadinIcon.BOOK, "Baza usług", ProductsViewAdmin.class),
                createTab(VaadinIcon.ARCHIVE, "Zarządzaj dokumentami", DocumentsView.class)
        );
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        return tabs;
    }

    private Tab createTab(VaadinIcon viewIcon, String viewName, Class viewClass) {
        Icon icon = viewIcon.create();
        icon.getStyle()
                .set("box-sizing", "border-box")
                .set("margin-inline-end", "var(--lumo-space-m)")
                .set("padding", "var(--lumo-space-xs)");

        RouterLink link = new RouterLink();
        link.add(icon, new Span(viewName));
        // Demo has no routes
        link.setRoute(viewClass);
        link.setTabIndex(-1);
        RouterLink viewLink = new RouterLink(viewName,viewClass);
        viewLink.setHighlightCondition(HighlightConditions.sameLocation());

        HorizontalLayout hl = new HorizontalLayout();
        hl.setMargin(true);
        hl.add(viewLink);


        setContent(hl);

        return new Tab(link);
    }
}

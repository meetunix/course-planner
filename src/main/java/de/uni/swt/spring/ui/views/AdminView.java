package de.uni.swt.spring.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import de.uni.swt.spring.app.security.SecurityUtils;
import de.uni.swt.spring.backend.Verwaltung.Nutzerverwaltung;
import de.uni.swt.spring.backend.bohnen.TestBohne;
import de.uni.swt.spring.backend.data.entity.Dozent;
import de.uni.swt.spring.backend.data.entity.Nutzer;
import de.uni.swt.spring.backend.repositories.DozentRepository;
import de.uni.swt.spring.ui.MainView;
import de.uni.swt.spring.ui.utils.SwtConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

import static de.uni.swt.spring.ui.utils.SwtConst.PAGE_LOGIN;
import static de.uni.swt.spring.ui.utils.SwtConst.PAGE_PASSWORT_VERGABE;

@HtmlImport("frontend://styles/shared-styles.html")
@Route(value = SwtConst.PAGE_ADMIN, layout = MainView.class)
@Secured("Admin")
public class AdminView extends HorizontalLayout implements BeforeEnterObserver {

    private static final long serialVersionUID = 1L;
    final Grid<Dozent> dgrid;
    private final DozentRepository drepo;
    private final String FIELD_WIDTH = "300px";
    @Autowired
    TestBohne bohne;
    private TextField emailField;
    private TextField vorname;
    private TextField nachname;
    @Autowired
    private Nutzerverwaltung nv;
    private Binder<Nutzer> binderDozent = new Binder<>(Nutzer.class);

    public AdminView(DozentRepository drepo) {
        this.drepo = drepo;

        this.dgrid = new Grid<>(Dozent.class);
        dgrid.setColumns("vorname", "nachname", "email");

        VerticalLayout links = new VerticalLayout();
        VerticalLayout rechts = new VerticalLayout();

        rechts.add(dgrid);

        emailField = new TextField("Email");
        vorname = new TextField("Vorname");
        nachname = new TextField("Nachname");
        Button button = new Button("hinzufügen");
        button.addClickListener(e -> save());

        //Eingabevalidierung

        emailField.setWidth(FIELD_WIDTH);
        emailField.setPlaceholder("vorname.nachname@uni-rostock.de");
        emailField.setValueChangeMode(ValueChangeMode.EAGER);

        binderDozent.forField(emailField)
                .asRequired("Pflichtfeld")
                .withValidator(v -> v.matches(SwtConst.REGEX_EMAIL), SwtConst.REGEX_EMAIL_ERROR)
                .bind(Nutzer::getEmail, Nutzer::setEmail);

        vorname.setWidth(FIELD_WIDTH);
        vorname.setPlaceholder("Erik");
        vorname.setValueChangeMode(ValueChangeMode.EAGER);

        binderDozent.forField(vorname)
                .asRequired("Pflichtfeld")
                .withValidator(v -> v.matches(SwtConst.REGEX_NAME), SwtConst.REGEX_NAME_ERROR)
                .bind(Nutzer::getVorname, Nutzer::setVorname);

        nachname.setWidth(FIELD_WIDTH);
        nachname.setPlaceholder("Musterman");
        nachname.setValueChangeMode(ValueChangeMode.EAGER);

        binderDozent.forField(nachname)
                .asRequired("Pflichtfeld")
                .withValidator(v -> v.matches(SwtConst.REGEX_NAME), SwtConst.REGEX_NAME_ERROR)
                .bind(Nutzer::getNachname, Nutzer::setNachname);

        links.add(emailField, vorname, nachname, button);
        links.setAlignItems(Alignment.END);

        //links.setMaxWidth("200px");
        links.setWidth("30%");
        rechts.setWidth("50%");
        rechts.setAlignItems(Alignment.START);
        setAlignItems(Alignment.CENTER);
        add(links, rechts);

        filldgrid();
    }

    private void filldgrid() {
        dgrid.setItems(drepo.findAll());
    }

    void save() {
        List<TextField> fields = new ArrayList<>();
        fields.add(emailField);
        fields.add(vorname);
        fields.add(nachname);
        //Alle Felder müssen befüllt sein
        if (emailField.isInvalid() || emailField.isEmpty()
                || vorname.isInvalid() || vorname.isEmpty()
                || nachname.isInvalid() || nachname.isEmpty()
        ) {

            for (TextField field : fields) {

                if (field.isEmpty() || field.isInvalid()) {
                    field.setInvalid(true);
                } else {
                    field.setInvalid(false);
                }
            }

            Notification.show("Bitte füllen die rot markierten Felder korrekt aus");
            //Wenn ein Dozent existiert muss dieser nur aktualisiert werden, ansonsten wird er neu angelegt
        } else {
            if (drepo.existsById(emailField.getValue())) {
                Dozent currDoz = drepo.getOne(emailField.getValue());
                nv.changeDozent(currDoz,
                        vorname.getValue(),
                        nachname.getValue(),
                        currDoz.getUebungsgruppeList());
                Notification.show("Dozent aktualisiert");
            } else {
                nv.erstelleDozent(emailField.getValue(), vorname.getValue(), nachname.getValue());
                Notification.show("Dozent hinzugefügt");
            }
        }
        filldgrid();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        final boolean accessGranted = SecurityUtils.isAccessGranted(beforeEnterEvent.getNavigationTarget());
        if (!accessGranted) {
            if (SecurityUtils.isUserLoggedIn()) {
                beforeEnterEvent.rerouteTo(PAGE_LOGIN); // AccessDeniedException einfügen
            } else {
                beforeEnterEvent.rerouteTo(PAGE_LOGIN);
            }
        }
        if(SecurityUtils.isUserLoggedIn()){
            if(!nv.getNutzer(SecurityContextHolder.getContext().getAuthentication().getName()).isPasswortChanged()){
                beforeEnterEvent.rerouteTo(PAGE_PASSWORT_VERGABE);
            }
        }
    }
}

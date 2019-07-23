package de.uni.swt.spring.ui.views;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import de.uni.swt.spring.backend.data.entity.*;
import de.uni.swt.spring.backend.repositories.DozentRepository;
import de.uni.swt.spring.backend.repositories.ProjektgruppeRepository;
import de.uni.swt.spring.backend.repositories.StudentRepository;
import de.uni.swt.spring.backend.repositories.UebungsgruppeRepository;
import de.uni.swt.spring.ui.MainView;
import de.uni.swt.spring.ui.utils.SwtConst;
import org.springframework.security.access.annotation.Secured;

//import de.uni.swt.spring.backend.repositories.ZusammensetzungRepository;

@HtmlImport("frontend://styles/shared-styles.html")
@Tag("uebungsgruppe-view")
@Route(value = SwtConst.PAGE_GRUPPEN, layout = MainView.class)
@Secured({"Dozent", "Student"})
public class UebungsgruppeView extends VerticalLayoutSecured {
    private final UebungsgruppeRepository gruppeRepo;
    private final DozentRepository dozentRepo;
    private final ProjektgruppeRepository teamRepo;
    //private final ZusammensetzungRepository zsmsetzungRepo;
    private final StudentRepository studentRepo;
    private Button neueGruppeBtn;
    private Grid<Uebungsgruppe> gruppeGrid;

    private final String WIDTH_NAME = "300px";
    private final String WIDTH_NUMBER = "120px";

    public UebungsgruppeView(UebungsgruppeRepository gruppeRepo, DozentRepository dozentRepo,
                             ProjektgruppeRepository teamRepo/*, ZusammensetzungRepository zsmsetzungRepo*/, StudentRepository studentRepo) {
        this.gruppeRepo = gruppeRepo;
        this.dozentRepo = dozentRepo;
        this.teamRepo = teamRepo;
        //this.zsmsetzungRepo = zsmsetzungRepo;
        this.studentRepo = studentRepo;

        /* Button, um eine neue Gruppe zu erstellen
         * Durch Klick wird ein Dialog geöffnet, in dem Daten eingegeben werden
         * */
        VerticalLayout übungsgruppeLayout = new VerticalLayout();

        neueGruppeBtn = new Button("Neue Gruppe erstellen");
        neueGruppeBtn.addClickListener(e -> {
            neueGruppeDialog().open();
        });

        //Grid zur Darstellung aller Gruppen
        gruppeGrid = new Grid<>(Uebungsgruppe.class);
        gruppeGrid.setColumns("name", "dozent", "tag", "termin");
        gruppeGrid.addComponentColumn(gruppe -> bearbeitenBtn(gruppeGrid, gruppe)).setHeader("Bearbeiten");
        gruppeGrid.addComponentColumn(gruppe -> löschenBtn(gruppeGrid, gruppe)).setHeader("Löschen");
        updateGrid();

        setAlignItems(Alignment.CENTER);
        übungsgruppeLayout.add(neueGruppeBtn);
        übungsgruppeLayout.add(gruppeGrid);
        übungsgruppeLayout.setAlignItems(Alignment.CENTER);
        übungsgruppeLayout.setWidth("70%");
       
        // Zentrieren auf der Seite
        VerticalLayout vl = new VerticalLayout();
        vl.add(übungsgruppeLayout);
        vl.setAlignItems(Alignment.CENTER);

        add(vl);
    }

    // Baut Dialog zum Erstellen einer Gruppe auf
    private Dialog neueGruppeDialog() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(false);
        VerticalLayout dialogLayout = new VerticalLayout();

        // Items zur Eingabe von Daten
        TextField name = new TextField("Gruppenname");
        name.focus();
        name.setValueChangeMode(ValueChangeMode.EAGER);
        name.setWidth(WIDTH_NAME);
        name.setRequired(true);
        
        ComboBox<Dozent> dozent = new ComboBox<>("Dozent");
        dozent.setItemLabelGenerator(Dozent::getNachname);
        dozent.setItems(dozentRepo.findAll());
        dozent.setWidth(WIDTH_NAME);
        dozent.setRequired(true);

        ComboBox<Wochentag> tag = new ComboBox<>("Wochentag");
        tag.setItems(Wochentag.values());
        tag.setRequired(true);

        TimePicker uhrzeit = new TimePicker("Uhrzeit");
        uhrzeit.setMin("07:00");
        uhrzeit.setMax("17:00");
        uhrzeit.setRequired(true);

        HorizontalLayout termin = new HorizontalLayout(tag, uhrzeit);

        //Buttons zur Navigation
        Button zurück = new Button("Zurück");
        Button erstellen = new Button("Erstellen");
        erstellen.setEnabled(false);
        HorizontalLayout buttons = new HorizontalLayout(zurück, erstellen);

        //Listener für Input und Buttons
        name.addValueChangeListener(e -> {
            erstellen.setEnabled(checkEnableButton(name, dozent, tag, uhrzeit));
        });
        dozent.addValueChangeListener(e -> {
            erstellen.setEnabled(checkEnableButton(name, dozent, tag, uhrzeit));
        });
        tag.addValueChangeListener(e -> {
            erstellen.setEnabled(checkEnableButton(name, dozent, tag, uhrzeit));
        });
        uhrzeit.addValueChangeListener(e -> {
            erstellen.setEnabled(checkEnableButton(name, dozent, tag, uhrzeit));
        });
        zurück.addClickListener(e -> {
            dialog.close();
        });
        erstellen.addClickListener(e -> {
            if (checkGruppennameFrei(name.getValue())) {
                erstelleGruppe(name, dozent, tag, uhrzeit);
                dialog.close();
                Notification.show("Die neue Übungsgruppe wurde erstellt");
                updateGrid();
            } else {
                name.setInvalid(true);
                name.setErrorMessage("Name schon vergeben");
                Notification.show("Die Übungsgruppe existiert bereits");
            }
        });

        dialogLayout.add(name, dozent, termin, buttons);

        dialog.add(dialogLayout);
        return dialog;
    }

    // True, falls alle Inputs einen Wert haben (zur Aktivierung des Buttons)
    private Boolean checkEnableButton(TextField name, ComboBox<Dozent> dozent,
                                      ComboBox<Wochentag> tag, TimePicker uhrzeit) {
        if (name.getValue().equals(""))
            return false;
        else return name.getValue() != null && dozent.getValue() != null && tag.getValue() != null
                && uhrzeit.getValue() != null;
    }

    //Überprüft, ob bereits eine Übungsgruppe mit dem übergebenen Namen existiert
    private Boolean checkGruppennameFrei(String name) {
        return gruppeRepo.findByName(name).isEmpty();
    }

    // Erstellt eine Gruppe mit den übergebenen Daten und fügt sie dem Dozenten hinzu
    private void erstelleGruppe(TextField name, ComboBox<Dozent> dozent, ComboBox<Wochentag> tag, TimePicker uhrzeit) {
        Uebungsgruppe neueGruppe = new Uebungsgruppe();
        neueGruppe.setName(name.getValue());
        neueGruppe.setDozent(dozent.getValue());
        neueGruppe.setTag(tag.getValue());
        neueGruppe.setTermin(uhrzeit.getValue());
        dozent.getValue().getUebungsgruppeList().add(neueGruppe);
        gruppeRepo.save(neueGruppe);
    }

    // füllt das Grid mit allen vorhandenen Übungsgruppen
    private void updateGrid() {
        gruppeGrid.setItems(gruppeRepo.findAll());
    }

    // erstellt einen Button zum Bearbeiten der Gruppendaten
    private Button bearbeitenBtn(Grid<Uebungsgruppe> grid, Uebungsgruppe gruppe) {
        Button button = new Button("Gruppe bearbeiten");
        button.addClickListener(e -> {
            bearbeitenDialog(gruppe).open();
        });
        return button;
    }

    //öffnet einen Dialog zum Bearbeiten der Gruppendaten
    private Dialog bearbeitenDialog(Uebungsgruppe gruppe) {
        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(false);
        VerticalLayout dialogLayout = new VerticalLayout();

        // Items zur Eingabe von Daten, bisherige Daten sind voreingestellt
        TextField name = new TextField("Gruppenname");
        name.setValue(gruppe.getName());
        name.setEnabled(false);
        name.setRequired(true);
        name.setWidth(WIDTH_NAME);
        
        
        ComboBox<Dozent> dozent = new ComboBox<>("Dozent");
        dozent.setItemLabelGenerator(Dozent::getNachname);
        dozent.setItems(dozentRepo.findAll());
        dozent.setValue(gruppe.getDozent());
        dozent.setRequired(true);
        dozent.setWidth(WIDTH_NAME);
        
        ComboBox<Wochentag> tag = new ComboBox<>("Wochentag");
        tag.setItems(Wochentag.values());
        tag.setValue(gruppe.getTag());
        tag.setRequired(true);
        
        TimePicker uhrzeit = new TimePicker("Uhrzeit");
        uhrzeit.setMin("07:00");
        uhrzeit.setMax("17:00");
        uhrzeit.setValue(gruppe.getTermin());
        tag.setRequired(true);

        HorizontalLayout termin = new HorizontalLayout(tag, uhrzeit);

        //Buttons zur Navigation
        Button zurück = new Button("Zurück");
        Button ändern = new Button("Änderungen übernehmen");
        ändern.setEnabled(false);
        HorizontalLayout buttons = new HorizontalLayout(zurück, ändern);

        //Listener für Input und Buttons
        dozent.addValueChangeListener(e -> {
            ändern.setEnabled(checkEnableButton(name, dozent, tag, uhrzeit));
        });
        tag.addValueChangeListener(e -> {
            ändern.setEnabled(checkEnableButton(name, dozent, tag, uhrzeit));
        });
        uhrzeit.addValueChangeListener(e -> {
            ändern.setEnabled(checkEnableButton(name, dozent, tag, uhrzeit));
        });
        zurück.addClickListener(e -> {
            dialog.close();
        });
        ändern.addClickListener(e -> {
            gruppe.setDozent(dozent.getValue());
            gruppe.setTag(tag.getValue());
            gruppe.setTermin(uhrzeit.getValue());
            gruppeRepo.save(gruppe);
            dialog.close();
            updateGrid();
        });

        dialogLayout.add(name, dozent, termin, buttons);

        dialog.add(dialogLayout);
        return dialog;
    }

    //erstellt einen Button zum Löschen einer Gruppe
    private Button löschenBtn(Grid<Uebungsgruppe> grid, Uebungsgruppe gruppe) {
        Button button = new Button("Gruppe löschen");
        button.addClickListener(e -> {
            löschenBestätigen(gruppe).open();
        });
        return button;
    }

    // öffnet einen Dialog zum Bestätigen der Löschung einer Gruppe
    private Dialog löschenBestätigen(Uebungsgruppe gruppe) {
        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(false);
        Paragraph text1 = new Paragraph("Sind Sie sich sicher, dass Sie diese Aktion durchführen möchten?");
        Paragraph text2 = new Paragraph("Alle assoziierten Teams und deren Zusammensetzungen werden ebenfalls gelöscht");
        Paragraph text3 = new Paragraph(" und Studenten verlieren ihre Zugehörigkeit zu Teams und Gruppen.");
        Button zurück = new Button("Nein");
        zurück.addClickListener(e -> {
            dialog.close();
        });
        Button bestätigen = new Button("Ja");
        bestätigen.addClickListener(e -> {
            //kaskadiertes Löschen
            //Löschen von Team/Gruppe aller Studenten der Gruppe
            for (Student s : gruppe.getStudentList()) {
                s.setUebungsgruppe(null);
                s.setProjektgruppe(null);
                studentRepo.save(s);
            }
            //Löschen aller assoziierten Projektgruppen
            for (Projektgruppe p : gruppe.getProjektgruppeList()) {
                //Löschen aller Zusammensetzungen zu den Projektgruppen
    			/*for(Zusammensetzung z : p.getZusammensetzungList()) {
    				zsmsetzungRepo.delete(z);
    			}*/
                teamRepo.delete(p);
            }
            //Löschen der Gruppe aus der Liste des Dozenten
            Dozent dozent = gruppe.getDozent();
            dozent.getUebungsgruppeList().remove(gruppe);
            dozentRepo.save(dozent);

            gruppeRepo.delete(gruppe);

            dialog.close();
            updateGrid();
        });
        HorizontalLayout buttonLayout = new HorizontalLayout(zurück, bestätigen);
        VerticalLayout dialogLayout = new VerticalLayout(text1, text2, text3, buttonLayout);
        dialog.add(dialogLayout);
        return dialog;
    }
}

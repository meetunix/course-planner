package de.uni.swt.spring.ui.views;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;

import de.uni.swt.spring.backend.Verwaltung.Bewertungsverwaltung;
import de.uni.swt.spring.backend.Verwaltung.KonfigurationVerwaltung;
import de.uni.swt.spring.backend.data.entity.Leistung;
import de.uni.swt.spring.backend.data.entity.Leistungsblock;
import de.uni.swt.spring.backend.data.entity.Leistungskomplex;
import de.uni.swt.spring.backend.data.entity.Projektgruppe;
import de.uni.swt.spring.backend.data.entity.Student;
import de.uni.swt.spring.backend.data.entity.Studiengang;
import de.uni.swt.spring.backend.data.entity.Uebungsgruppe;
import de.uni.swt.spring.backend.data.entity.Zusammensetzung;
import de.uni.swt.spring.backend.repositories.KonfigurationRepository;
import de.uni.swt.spring.backend.repositories.LeistungRepository;
import de.uni.swt.spring.backend.repositories.LeistungsblockRepository;
import de.uni.swt.spring.backend.repositories.LeistungskomplexRepository;
import de.uni.swt.spring.backend.repositories.ProjektgruppeRepository;
import de.uni.swt.spring.backend.repositories.StudentLeistungRepository;
import de.uni.swt.spring.backend.repositories.StudentRepository;
import de.uni.swt.spring.backend.repositories.UebungsgruppeRepository;
import de.uni.swt.spring.backend.repositories.ZusammensetzungRepository;
import de.uni.swt.spring.ui.MainView;
import de.uni.swt.spring.ui.utils.SwtConst;
import org.hibernate.LazyInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@HtmlImport("frontend://styles/shared-styles.html")
@Tag("projektgruppe-view")
@Route(value = SwtConst.PAGE_TEAMS, layout = MainView.class)
@Secured({"Dozent", "Student"})
//TODO: Zusammensetzung-Buttons, Zusammensetzungen bei Teamlöschung löschen, Globales Thema/Deadline
public class ProjektgruppeView extends VerticalLayoutSecured {
    private final ProjektgruppeRepository teamRepo;
    private final UebungsgruppeRepository groupRepo;
    private final KonfigurationRepository konfigRepo;
    private final StudentRepository studentRepo;
    @Autowired
    private KonfigurationVerwaltung konfigVerw; // = new KonfigurationVerwaltung();
    private Button neuesTeamBtn;
    private Button globThemaBtn;
    private Button alleSchließenBtn;
    private Button alleÖffnenBtn;
    private Grid<Projektgruppe> teamGrid;
    
    @Autowired
    Bewertungsverwaltung bewertungVerw;
    @Autowired
    private LeistungskomplexRepository leistungskomplexRepo;
    @Autowired
    private LeistungsblockRepository leistungsblockRepo;
    @Autowired
    private LeistungRepository leistungRepo;
    @Autowired
    private StudentLeistungRepository studLeistungRepo;
    @Autowired
    private ZusammensetzungRepository zusammensetzungRepo;
    private Leistungskomplex selectedLK;
    private Leistungsblock selectedLB;
    private Leistung selectedL;
    Button speichernBtn = new Button ("Speichern");
    Label bewDiaErrorLabel = new Label("");
    
    private final String WIDTH_NAME = "300px";
    private final String WIDTH_NUMBER = "120px";

    public ProjektgruppeView(ProjektgruppeRepository teamRepo, UebungsgruppeRepository groupRepo,
                             KonfigurationRepository konfigRepo, StudentRepository studentRepo) {
        this.teamRepo = teamRepo;
        this.groupRepo = groupRepo;
        this.konfigRepo = konfigRepo;
        this.studentRepo = studentRepo;

        //Buttons zur Verwaltung der Projektgruppen
        neuesTeamBtn = new Button("Neues Team anlegen");
        globThemaBtn = new Button("Globales Thema für alle nutzen");
        //globThemaBtn.setEnabled(false);
        alleSchließenBtn = new Button("Alle Teams schließen");
        alleÖffnenBtn = new Button("Alle Teams öffnen");
        HorizontalLayout buttonsVerwaltung = new HorizontalLayout();
        buttonsVerwaltung.add(neuesTeamBtn, globThemaBtn, alleSchließenBtn, alleÖffnenBtn);
        buttonsVerwaltung.setAlignItems(Alignment.CENTER);

        //Listener für die Buttons
        neuesTeamBtn.addClickListener(e -> {
            erstelleNeuesTeamDialog().open();
        });
        globThemaBtn.addClickListener(e -> {
            globalesThemaDialog().open();
        });
        alleSchließenBtn.addClickListener(e -> {
            schließeAlleTeams();
            updateGrid();
        });
        alleÖffnenBtn.addClickListener(e -> {
            öffneAlleTeams();
            updateGrid();
        });


        //Grid zur Darstellung der Teams
        teamGrid = new Grid<>(Projektgruppe.class);
        teamGrid.setColumns("name", "thema", "uebungsgruppe", "anzahlMax", "anzahlAktuell");
        teamGrid.addColumn(Projektgruppe::getOffen).setHeader("Gruppe geöffnet");
        teamGrid.addComponentColumn(team -> bearbeitenBtn(teamGrid, team)).setHeader("Bearbeiten");
        teamGrid.addComponentColumn(team -> bewertenBtn(teamGrid,team)).setHeader("Bewerten");
        teamGrid.addComponentColumn(team -> löschenBtn(teamGrid, team)).setHeader("Löschen");
        teamGrid.getColumnByKey("uebungsgruppe").setHeader("Übungsgruppe");
        teamGrid.asSingleSelect().addValueChangeListener(e -> {
            memberDialog(e.getValue()).open();
        });
        updateGrid();

        VerticalLayout pageLayout = new VerticalLayout(buttonsVerwaltung, teamGrid);

        this.setAlignItems(Alignment.CENTER);
        pageLayout.setAlignItems(Alignment.CENTER);
        pageLayout.setWidth("70%");
        VerticalLayout vl = new VerticalLayout();
        vl.add(pageLayout);
        vl.setAlignItems(Alignment.CENTER);

        add(vl);
    }

    //Füllt das Grid mit allen vorhandenen Teams
    private void updateGrid() {
        //checkDeadlineErreicht();
        teamGrid.setItems(teamRepo.findAll());
        //Überprüfung, ob Deadline zum automatischen Schließen schon erreicht ist
    }

    // Öffnet einen Dialog, um eine neues Team zu erstellen.
    private Dialog erstelleNeuesTeamDialog() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(false);
        VerticalLayout dialogLayout = new VerticalLayout();

        //Items zur Eingabe von Daten
        TextField name = new TextField();
        name.setLabel("Teamname");
        name.setValueChangeMode(ValueChangeMode.EAGER);
        name.setWidth(WIDTH_NAME);
        name.focus();
        name.setRequired(true);

        TextField thema = new TextField();
        thema.setLabel("Thema");
        thema.setValueChangeMode(ValueChangeMode.EAGER);
        thema.setWidth(WIDTH_NAME);
        thema.setRequired(true);

        ComboBox<Uebungsgruppe> gruppe = new ComboBox<>();
        gruppe.setLabel("Zugeordnete Übungsgruppe");
        gruppe.setItemLabelGenerator(Uebungsgruppe::getName);
        gruppe.setItems(groupRepo.findAll());
        gruppe.setWidth(WIDTH_NAME);
        gruppe.setRequired(true);

        NumberField mitgliederMax = new NumberField();
        mitgliederMax.setLabel("Max. Mitglieder");
        mitgliederMax.setValueChangeMode(ValueChangeMode.EAGER);
        mitgliederMax.setWidth(WIDTH_NUMBER);
        mitgliederMax.setRequiredIndicatorVisible(true);

        //Buttons zur Bearbeitung der Zusamensetzung und Schließen der Gruppe
        Button zsmsetzungBearbeiten = new Button("Zusammensetzung bearbeiten");
        Zusammensetzung zs = new Zusammensetzung();
        zsmsetzungBearbeiten.addClickListener(e -> {
        	zusammensetzungBearbeiten(zs, (int)Math.round(mitgliederMax.getValue())).open();
        });
        zsmsetzungBearbeiten.setEnabled(false);
        mitgliederMax.addValueChangeListener(e -> {
        	if (e.getValue() != null) {
        		zsmsetzungBearbeiten.setEnabled(true);
        	}
        	else {
        		zsmsetzungBearbeiten.setEnabled(false);
        	}
        });

        Checkbox öffneTeam = new Checkbox("Team öffnen");

        //Buttons zur Navigation
        Button zurückBtn = new Button("Zurück");
        Button erstellen = new Button("Erstellen");
        erstellen.setEnabled(false);
        HorizontalLayout navigationBtns = new HorizontalLayout();
        navigationBtns.add(zurückBtn, erstellen);

        //Listener für Input-Felder und Buttons
        name.addValueChangeListener(e -> {
            erstellen.setEnabled(checkEnableButton(name, thema, gruppe, mitgliederMax));
        });
        thema.addValueChangeListener(e -> {
            erstellen.setEnabled(checkEnableButton(name, thema, gruppe, mitgliederMax));
        });
        gruppe.addValueChangeListener(e -> {
            erstellen.setEnabled(checkEnableButton(name, thema, gruppe, mitgliederMax));
        });
        mitgliederMax.addValueChangeListener(e -> {
            erstellen.setEnabled(checkEnableButton(name, thema, gruppe, mitgliederMax));
        });
        zurückBtn.addClickListener(e -> {
            dialog.close();
        });
        erstellen.addClickListener(e -> {
            if (checkGruppennameFrei(name.getValue())) {
                Projektgruppe pg = erstelleNeuesTeam(name, thema, gruppe, öffneTeam, mitgliederMax, zs);
                dialog.close();
                Notification.show("Team " + name.getValue() + " wurde angelegt.");
                updateGrid();
            } else {
                name.setInvalid(true);
                name.setErrorMessage("Name schon vergeben");
                Notification.show("Das Team mit dem Namen " + name.getValue() + " existiert bereits.");
            }
        });

        //Layout
        dialogLayout.add(name, thema, gruppe, mitgliederMax,
                zsmsetzungBearbeiten, öffneTeam, navigationBtns);
        dialog.add(dialogLayout);
        return dialog;
    }

    private Dialog zusammensetzungBearbeiten(Zusammensetzung zs, Integer max) {
    	Dialog dialog = new Dialog();
    	dialog.setWidth("420px");
    	VerticalLayout dLayout = new VerticalLayout();
    	ArrayList<NumberField> nfsMax = new ArrayList<>();
    	ArrayList<NumberField> nfsMin = new ArrayList<>();
    	ArrayList<Studiengang> studiengänge = konfigVerw.getAlleStudiengänge();
    	dLayout.add(new Label("Fügen sie die maximale Anzahl an Studenten für jeden Studiengang für dieses Team ein"));
    	for (Studiengang s : studiengänge) {
    		NumberField currMax = new NumberField("Max. Studenten");
    		currMax.setMax(max);
    		currMax.setMin(0.0);
    		if (zs.getMax(s) != null)
				currMax.setValue((double)zs.getMax(s));
			currMax.setWidth("46%");
    		currMax.setHasControls(true);
    		dLayout.add(currMax);
    		nfsMax.add(currMax);
    		NumberField currMin = new NumberField("Min. Studenten");
    		currMin.setMax(max);
    		currMin.setMin(0.0);
			currMin.setValue((double)zs.getMin(s));
			currMin.setWidth("46%");
    		currMin.setHasControls(true);
    		nfsMin.add(currMin);
    		HorizontalLayout hl = new HorizontalLayout(currMin, currMax);
    		hl.setWidth("100%");
    		VerticalLayout vl = new VerticalLayout(new H5(s.getStudiengang()), hl);
    		vl.getElement().getStyle().set("background-color", "var(--lumo-shade-5pct)");
    		dLayout.add(vl);
    	}
    	// Save und cancel
    	HorizontalLayout saveCancelLayout = new HorizontalLayout();
    	Button zurückBtn = new Button("Zurück");
    	zurückBtn.addClickListener(e -> {
    		dialog.close();
    	});
    	Button speichernBtn = new Button("Speichern");
    	speichernBtn.addClickListener(e -> {
			for (int i = 0; i < nfsMax.size(); i++) {
				zs.add(studiengänge.get(i), (int)Math.round(nfsMin.get(i).getValue()), (int)Math.round(nfsMax.get(i).getValue()));
			}
			dialog.close();
    	});
    	saveCancelLayout.add(zurückBtn, speichernBtn);
    	dLayout.add(saveCancelLayout);

    	dialog.add(dLayout);
    	return dialog;
    }

    //True, falls alle Input-Felder einen Wert haben (zur Aktivierung eines Buttons)
    private Boolean checkEnableButton(TextField name, TextField thema, ComboBox<Uebungsgruppe> gruppe,
                                      NumberField mitgliederMax) {
        if (name.getValue().equals("") || thema.getValue().equals("") || mitgliederMax.isEmpty())
            return false;
        else return name.getValue() != null && thema.getValue() != null && gruppe.getValue() != null &&
				mitgliederMax.getValue() != null;
	}

    //True, falls noch keine Projektgruppe mit dem übergebenen Namen existiert
    private Boolean checkGruppennameFrei(String name) {
        return teamRepo.findByName(name).isEmpty();
    }

    /**
     * Erstellt ein neues Team und speichert es in der Datenbank
     * @param name
     * @param thema
     * @param gruppe
     * @param öffneTeam
     * @param anzahlMax
     */
    private Projektgruppe erstelleNeuesTeam(TextField name, TextField thema, ComboBox<Uebungsgruppe> gruppe,
                                   Checkbox öffneTeam, NumberField anzahlMax, Zusammensetzung zs) {
        Projektgruppe neu = new Projektgruppe();
        neu.setName(name.getValue());
        neu.setThema(thema.getValue());
        neu.setUebungsgruppe(gruppe.getValue());
        neu.setOffen(öffneTeam.getValue());
        neu.setAnzahlAktuell(0);
        neu.setAnzahlMax((int)Math.round(anzahlMax.getValue()));
        neu.setZusammensetzung(zs);
        teamRepo.save(neu);
        gruppe.getValue().getProjektgruppeList().add(neu);
        groupRepo.save(gruppe.getValue());
        zs.setProjektgruppe(neu);
        zusammensetzungRepo.save(zs);
        return neu;
    }

    //Öffnet alle Teams im Repository
    private void öffneAlleTeams() {
        for (Projektgruppe p : teamRepo.findAll()) {
            p.setOffen(true);
            teamRepo.save(p);
        }
    }

    //Schließt alle Teams im Repository
    private void schließeAlleTeams() {
        for (Projektgruppe p : teamRepo.findAll()) {
            p.setOffen(false);
            teamRepo.save(p);
        }
    }

    // erstellt einen Button zum Bearbeiten der Teamdaten
    private Button bearbeitenBtn(Grid<Projektgruppe> grid, Projektgruppe team) {
        Button button = new Button("Bearbeiten");
        button.addClickListener(e -> {
            bearbeitenDialog(team).open();
        });
        return button;
    }

    //erstellt einen Button zum Löschen einer Gruppe
    private Button löschenBtn(Grid<Projektgruppe> grid, Projektgruppe team) {
        Button button = new Button("Löschen");
        button.addClickListener(e -> {
            löschenBestätigen(team).open();
        });
        return button;
    }

    // Öffnet einen Dialog zum Bearbeiten der Teamdaten.
    private Dialog bearbeitenDialog(Projektgruppe team) {
        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(false);
        VerticalLayout dialogLayout = new VerticalLayout();

        // Items zur Eingabe von Daten, bisherige Daten sind voreingestellt
        TextField name = new TextField("Teamname");
        name.setValue(team.getName());
        name.setEnabled(false);
        name.setWidth(WIDTH_NAME);
        name.setRequired(true);

        TextField themaInput = new TextField("Thema");
        themaInput.setValueChangeMode(ValueChangeMode.EAGER);
        themaInput.setValue(team.getThema());
        themaInput.setWidth(WIDTH_NAME);
        themaInput.setRequired(true);
        
        ComboBox<Uebungsgruppe> gruppeInput = new ComboBox<>();
        gruppeInput.setLabel("Zugeordnete Übungsgruppe");
        gruppeInput.setItemLabelGenerator(Uebungsgruppe::getName);
        gruppeInput.setItems(groupRepo.findAll());
        gruppeInput.setValue(team.getUebungsgruppe());
        gruppeInput.setRequired(true);
        gruppeInput.setWidth(WIDTH_NAME);

        NumberField mitgliederInput = new NumberField();
        mitgliederInput.setLabel("Max. Mitglieder:");
        mitgliederInput.setValueChangeMode(ValueChangeMode.EAGER);
        mitgliederInput.setValue((double) team.getAnzahlMax());
        mitgliederInput.setWidth(WIDTH_NUMBER);
        mitgliederInput.setRequiredIndicatorVisible(true);

        //Buttons zum Bearbeiten der Zusammensetzung und zum Öffnen/Schließen
        Button zsmsetzungBearbeiten = new Button("Zusammensetzung bearbeiten");
        zsmsetzungBearbeiten.setEnabled(false);
        Checkbox öffneTeam = new Checkbox("Team öffnen");
        öffneTeam.setValue(team.getOffen());

        //Buttons zur Navigation
        HorizontalLayout navigationLayout = new HorizontalLayout();
        Button zurückBtn = new Button("Zurück");
        Button speichern = new Button("Änderungen speichern");
        speichern.setEnabled(false);
        navigationLayout.add(zurückBtn, speichern);

        //Listener für Input und Buttons
        themaInput.addValueChangeListener(e -> {
            speichern.setEnabled(checkEnableButton(name, themaInput, gruppeInput, mitgliederInput));
        });
        gruppeInput.addValueChangeListener(e -> {
            speichern.setEnabled(checkEnableButton(name, themaInput, gruppeInput, mitgliederInput));
        });
        mitgliederInput.addValueChangeListener(e -> {
            speichern.setEnabled(checkEnableButton(name, themaInput, gruppeInput, mitgliederInput));
        });
        öffneTeam.addValueChangeListener(e -> speichern.setEnabled(true));
        zurückBtn.addClickListener(e -> dialog.close());
        speichern.addClickListener(e -> {
            team.setThema(themaInput.getValue());
            team.setUebungsgruppe(gruppeInput.getValue());
            if (Math.round(mitgliederInput.getValue()) >= team.getAnzahlAktuell())
                team.setAnzahlMax((int)Math.round(mitgliederInput.getValue()));
            else {
                mitgliederInput.setInvalid(true);
                mitgliederInput.setErrorMessage("Team hat mehr Mitglieder als angegeben");
            }
            team.setOffen(öffneTeam.getValue());
            teamRepo.save(team);
            dialog.close();
            updateGrid();
        });

        dialogLayout.add(name, gruppeInput, themaInput, mitgliederInput,
                zsmsetzungBearbeiten, öffneTeam, navigationLayout);
        dialog.add(dialogLayout);
        return dialog;
    }

    // öffnet einen Dialog zum Bestätigen der Löschung eines Teams
    private Dialog löschenBestätigen(Projektgruppe team) {
        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(false);
        // Text zum Nachfragen
        Paragraph text1 = new Paragraph("Sind Sie sich sicher, dass Sie diese Aktion durchführen möchten?");
        Paragraph text2 = new Paragraph("Alle zugehörigen Studenten verlieren dadurch Team und Übungsgruppe.");

        //Button ablehnen
        Button zurück = new Button("Nein");
        zurück.addClickListener(e -> {
            dialog.close();
        });

        //Button bestätigen
        Button bestätigen = new Button("Ja");
        bestätigen.addClickListener(e -> {
            //kaskadiertes Löschen
            //Löschen von Team/Gruppe aller Studenten des Teams
            for (Student s : team.getStudentList()) {
                s.setUebungsgruppe(null);
                s.setProjektgruppe(null);
                studentRepo.save(s);
            }
            //Löschen des Teams aus der Liste der Gruppe
            Uebungsgruppe gruppe = team.getUebungsgruppe();
            gruppe.getProjektgruppeList().remove(team);
            groupRepo.save(gruppe);
            //Löschen des Teams
            teamRepo.delete(team);

            dialog.close();
            updateGrid();
        });

        //Layout
        HorizontalLayout buttonLayout = new HorizontalLayout(zurück, bestätigen);
        VerticalLayout dialogLayout = new VerticalLayout(text1, text2, buttonLayout);
        dialog.add(dialogLayout);
        return dialog;
    }

    // Öffnet einen Dialog zur Bestätigung der Änderung des globalen Themas
    private Dialog globalesThemaDialog() {
        Dialog dialog = new Dialog();

        //TextField zur Eingabe des Themas
        TextField thema = new TextField("Globales Thema");
        thema.setValue(konfigVerw.getKonfiguration().getGlobalesThema());
        thema.setWidth(WIDTH_NAME);
        
        thema.addValueChangeListener(e -> {
            konfigVerw.getKonfiguration().setGlobalesThema(e.getValue());
        });

        Paragraph text = new Paragraph("Sind Sie sich sicher, dass Sie das festgelegte globale "
                + "Thema für alle Teams setzen wollen? Damit überschreiben Sie ggf. "
                + "bereits gesetzte Themen.");
        Button zurück = new Button("Zurück");
        zurück.addClickListener(e -> {
            dialog.close();
        });
        Button ja = new Button("Bestätigen");
        ja.addClickListener(e -> {
            for (Projektgruppe p : teamRepo.findAll()) {
                konfigVerw.changeGlobalesThema(konfigVerw.getLehrgangname(), thema.getValue());
                p.setThema(konfigVerw.getKonfiguration().getGlobalesThema());
                teamRepo.save(p);
            }
            dialog.close();
            updateGrid();
        });
        HorizontalLayout buttons = new HorizontalLayout(zurück, ja);
        dialog.add(thema, text, buttons);
        return dialog;
    }

    //Überprüft, ob die Deadline schon erreicht ist und schließt ggf alle Gruppen
    private void checkDeadlineErreicht() {
        try {
            LocalDate deadline = konfigRepo.getOne("swt-planer").getDeadline();
            if (deadline != null && (deadline.equals(LocalDate.now()) || deadline.isBefore(LocalDate.now()))) {
                //Alle Teams schließen
                for (Projektgruppe p : teamRepo.findAll()) {
                    p.setOffen(false);
                    teamRepo.save(p);
                }
            }
        } catch (LazyInitializationException e) {
        }
	}

    //Erstellt einen Dialog, der alle Mitglieder eines Teams anzeigt
    private Dialog memberDialog(Projektgruppe team) {
        Dialog dialog = new Dialog();
        Grid<Student> grid = new Grid<>(Student.class);
        grid.setColumns("email", "vorname", "nachname", "matrikelNr");
        grid.setItems(team.getStudentList());
        dialog.add(grid);
        dialog.setHeight("600px");
        dialog.setWidth("800px");
        return dialog;
    }
    private Button bewertenBtn(Grid<Projektgruppe> grid, Projektgruppe p) {
        Button button = new Button("Bewerten");
        button.addClickListener(e -> {
        	bewertenDialog(p).open();
        });
        return button;
    }
    
    private Dialog bewertenDialog(Projektgruppe p) {
    	Dialog dialog = new Dialog();
    	Label ezBewDiaStud = new Label();
    	NumberField ezBewDialogErrPunkte = new NumberField("Erreichte Punkte");
    	NumberField ezBewDialogMaxPunkte = new NumberField("Maximale Punkte");
    	ComboBox<Leistungskomplex> lkAusw = new ComboBox<Leistungskomplex>("Leistungskomplex");
        ComboBox<Leistungsblock> lbAusw = new ComboBox<Leistungsblock>("Leistungsblock");
        ComboBox<Leistung> lAusw = new ComboBox<Leistung>("Leistung");
        VerticalLayout fullDialogL = new VerticalLayout();
        HorizontalLayout buttonsL = new HorizontalLayout();
        HorizontalLayout punkteFL = new HorizontalLayout();
        
        lkAusw.setItemLabelGenerator(Leistungskomplex::getName);
        lkAusw.setItems(leistungskomplexRepo.findAll());

        lbAusw.setItemLabelGenerator(Leistungsblock::getName);
        lbAusw.setItems(leistungsblockRepo.findAll());
        lbAusw.setEnabled(false);

        lAusw.setItemLabelGenerator(Leistung::getName);
        lAusw.setItems(leistungRepo.findAll());
        lAusw.setEnabled(false);
        
    	ezBewDiaStud.setText("Bewertung für Team " + p.getName() +" für:");
        ezBewDialogErrPunkte.setValue(0.00);
        ezBewDialogMaxPunkte.setValue(0.00);
        ezBewDialogMaxPunkte.setEnabled(false);
        Label slash = new Label(" / ");
        speichernBtn.setEnabled(false);
        
        if(p.getStudentList().isEmpty()) {
        	bewDiaErrorLabel.setText("Team hat keine Mitglieder!");
        }
        
        if(selectedLK != null) {
        	lkAusw.setValue(selectedLK);
        	lbAusw.setEnabled(true);
            if(selectedLB != null) {
            	lbAusw.setItems(leistungsblockRepo.findByLeistungskomplex(selectedLK));
            	lbAusw.setValue(selectedLB);
            	lAusw.setEnabled(true);
                if(selectedL != null) {
                	lAusw.setItems(leistungRepo.findByLeistungsblock(selectedLB));
                	lAusw.setValue(selectedL);
                	if(!p.getStudentList().isEmpty()) {
	                	if(!studLeistungRepo.findByStudentAndLeistung(p.getStudentList().get(0), selectedL).isEmpty()) {
	                		ezBewDialogErrPunkte.setValue((double) studLeistungRepo.findByStudentAndLeistung(p.getStudentList().get(0), selectedL).get(0).getPunktzahl());
	                	} else {
	                		ezBewDialogErrPunkte.setValue(0.00);
	                	}
	                	speichernBtn.setEnabled(true);
                	}
                    ezBewDialogMaxPunkte.setValue((double) selectedL.getMaxPunkte());	
                }
            }
        }

        lkAusw.addValueChangeListener(e -> {
            selectedLK = e.getValue();
            lAusw.clear();
            lbAusw.clear();
            selectedL = null;
            selectedLB = null;
            if (e.getSource().isEmpty()) {
                lbAusw.setEnabled(false);
                lAusw.setEnabled(false);
            } else {
                lbAusw.setItems(leistungsblockRepo.findByLeistungskomplex(selectedLK));
                lbAusw.setEnabled(true);
            }
        });
        
        lbAusw.addValueChangeListener(e -> {
            lAusw.clear();
            selectedL = null;
            selectedLB = e.getValue();
            if (e.getSource().isEmpty()) {
                lAusw.setEnabled(false);
            } else {
                List<Leistung> leistungLKList = new ArrayList<Leistung>();
                leistungLKList = leistungRepo.findByLeistungsblock(selectedLB);

                lAusw.setItems(leistungLKList);
                lAusw.setEnabled(true);
            }
        });
        
        lAusw.addValueChangeListener(e -> {
            selectedL = e.getValue();
            if (e.getSource().isEmpty()) {
                ezBewDialogErrPunkte.setValue(0.00);
                ezBewDialogMaxPunkte.setValue(0.00);
                speichernBtn.setEnabled(false);
            } else {
            	if(!p.getStudentList().isEmpty()) {
                	if(!studLeistungRepo.findByStudentAndLeistung(p.getStudentList().get(0), selectedL).isEmpty()) {
                		ezBewDialogErrPunkte.setValue((double) studLeistungRepo.findByStudentAndLeistung(p.getStudentList().get(0), selectedL).get(0).getPunktzahl());
                	} else {
                		ezBewDialogErrPunkte.setValue(0.00);
                	}
                	speichernBtn.setEnabled(true);
            	}
                ezBewDialogMaxPunkte.setValue((double) selectedL.getMaxPunkte());	
            }
        });
        
        Button bewDialogAbbrechen = new Button("Abbrechen");
        bewDialogAbbrechen.addClickListener(e -> dialog.close());
        
        speichernBtn.addClickListener(e -> {
            double errPunkte = ezBewDialogErrPunkte.getValue();
            if (errPunkte >= 0) {
	            	for (Student s : p.getStudentList()) {
	                	if(!studLeistungRepo.findByStudentAndLeistung(s, selectedL).isEmpty()) {
	                		bewertungVerw.changeErgebnis(studLeistungRepo.findByStudentAndLeistung(s, selectedL).get(0), (float) errPunkte);
	                	}
	                	else { 
	                		bewertungVerw.addErgebnis((float) errPunkte, s, selectedL);
	                	}
	            	}
	                dialog.close();  
            } else {
                bewDiaErrorLabel.setText("Ungültige Eingabe!");
            }
        });
        
        punkteFL.add(ezBewDialogErrPunkte, slash, ezBewDialogMaxPunkte);
        punkteFL.setAlignItems(Alignment.BASELINE);
        buttonsL.add(bewDialogAbbrechen, speichernBtn);
        
        fullDialogL.add(ezBewDiaStud, lkAusw, lbAusw, lAusw, punkteFL, bewDiaErrorLabel, buttonsL);
        fullDialogL.setAlignItems(Alignment.CENTER);
        dialog.add(fullDialogL);
        
    	return dialog;	
    }
    
    
    
    /*private Dialog zsmsetzungNeuesTeamDialog(List<Zusammensetzung> list) {
    	Dialog dialog = new Dialog();
    	VerticalLayout dialogLayout = new VerticalLayout();
    	
    	Label dialogTitel = new Label("Zusammensetzung für neues Team bearbeiten");
    	
    	RadioButtonGroup<String> radioButtons = new RadioButtonGroup<>();
    	radioButtons.setItems("Prozent", "Ganzzahlig");
    	
    	Button addZsmsetzung = new Button("Studiengang hinzufügen");
    	addZsmsetzung.addClickListener(e -> {
    		HorizontalLayout studiengangLayout = new HorizontalLayout();
    		ComboBox<Studiengang> studiengang = new ComboBox<>();
    		studiengang.setLabel("Studiengang");
    		studiengang.setItemLabelGenerator(Studiengang::getStudiengang);
    		
    		NumberField anzahl = new NumberField();
    		anzahl.setLabel("Anteil/Anzahl");
    		
    		Button speichern = new Button("Speichern");
    		speichern.addClickListener(e -> {
    			
    		});
    	});
    	
    	return dialog;
    }*/
    
    /*private Zusammensetzung erstelleNeueZsmsetzung(String modus, Float anzahl, Studiengang studiengang, Projektgruppe) {
    	Zusammensetzung zsm = new Zusammensetzung();
    	zsm.setInProzent(modus.equals("Prozent"));
    	zsm.setZusammensetzung(anzahl);
    	zsm.setStudiengang(studiengang);
    	zsm.setProjektgruppe(projektgruppe);
    }*/
}

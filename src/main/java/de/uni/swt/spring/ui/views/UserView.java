package de.uni.swt.spring.ui.views;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import de.uni.swt.spring.backend.Verwaltung.Bewertungsverwaltung;
import de.uni.swt.spring.backend.Verwaltung.Nutzerverwaltung;
import de.uni.swt.spring.backend.data.entity.Leistung;
import de.uni.swt.spring.backend.data.entity.Leistungsblock;
import de.uni.swt.spring.backend.data.entity.Leistungskomplex;
import de.uni.swt.spring.backend.data.entity.Projektgruppe;
import de.uni.swt.spring.backend.data.entity.Student;
import de.uni.swt.spring.backend.data.entity.Studiengang;
import de.uni.swt.spring.backend.data.entity.Uebungsgruppe;
import de.uni.swt.spring.backend.repositories.LeistungRepository;
import de.uni.swt.spring.backend.repositories.LeistungsblockRepository;
import de.uni.swt.spring.backend.repositories.LeistungskomplexRepository;
import de.uni.swt.spring.backend.repositories.ProjektgruppeRepository;
import de.uni.swt.spring.backend.repositories.StudentLeistungRepository;
import de.uni.swt.spring.backend.repositories.StudentRepository;
import de.uni.swt.spring.backend.repositories.StudiengangRepository;
import de.uni.swt.spring.backend.repositories.UebungsgruppeRepository;
import de.uni.swt.spring.ui.MainView;
import de.uni.swt.spring.ui.utils.MyDoubleToStringConverter;
import de.uni.swt.spring.ui.utils.MyStringToIntegerConverter;
import de.uni.swt.spring.ui.utils.SwtConst;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

@HtmlImport("frontend://styles/shared-styles.html")
@Route(value = SwtConst.PAGE_NUTZER, layout = MainView.class)
@Secured("Dozent")
public class UserView extends VerticalLayoutSecured {

    private final StudentRepository studentRepo;
    private final StudiengangRepository studiengangRepo;
    private final UebungsgruppeRepository gruppeRepo;
    private final ProjektgruppeRepository teamRepo;
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

    private Grid<Student> studentFreischaltenGrid;
    private Grid<Student> studentAlleGrid;
    private Label freischaltenLabel;
    private Label alleLabel;
    private Button alleFreischalten;
    private Button alleLöschen;
    private TextField filterFrei;
    private TextField filterAlle;

    private final String WIDTH_NAME = "300px";
    private final String WIDTH_NUMBER = "100px";
    
    private Leistungskomplex selectedLK;
    private Leistungsblock selectedLB;
    private Leistung selectedL;
    Button speichernBtn = new Button ("Speichern");
    Label bewDiaErrorLabel = new Label("");
    
    private Binder<Student> binderStudent = new Binder<>(Student.class);

    public UserView(StudentRepository repo, StudiengangRepository studiengangRepo,
                    UebungsgruppeRepository gruppeRepo, ProjektgruppeRepository teamRepo) {
        this.studentRepo = repo;
        this.studiengangRepo = studiengangRepo;
        this.gruppeRepo = gruppeRepo;
        this.teamRepo = teamRepo;

        //Alle Studenten Bedienleiste
        alleLabel = new Label("Freigeschaltete Studenten");
        filterAlle = new TextField("Tabelle filtern nach E-Mail, Vorname, Nachname");
        filterAlle.setPlaceholder("E-Mail, Vorname, Nachname");
        filterAlle.setValueChangeMode(ValueChangeMode.EAGER);
        filterAlle.setWidth("400px");
        filterAlle.addValueChangeListener(e -> {
            updateGridFilter(studentAlleGrid, filterAlle);
        });
        HorizontalLayout alleLeiste = new HorizontalLayout(alleLabel, filterAlle);
        alleLeiste.setAlignItems(Alignment.BASELINE);

        //Grid zur Darstellung der freigeschalteten Studenten
        studentAlleGrid = new Grid<>(Student.class);
        studentAlleGrid.setColumns("email", "vorname", "nachname", "matrikelNr", "uebungsgruppe",
                "projektgruppe", "studiengang", "wunschtermin");
        //studentAlleGrid.addColumn(student -> bewertungVerw.zugelassen(student)).setHeader("Zugelassen"); -> NullPointerException
        studentAlleGrid.addComponentColumn(student -> new Label(bewertungVerw.zugelassen(student) ? "Ja" : "Nein")).setHeader("Zugelassen");
        studentAlleGrid.addComponentColumn(student -> bearbeitenBtn(studentAlleGrid, student)).setHeader("Bearbeiten");
        studentAlleGrid.addComponentColumn(student -> bewertenBtn(studentAlleGrid, student)).setHeader("Bewerten");
        studentAlleGrid.addComponentColumn(student -> löschenBtn(studentAlleGrid, student)).setHeader("Löschen");
        studentAlleGrid.getColumnByKey("projektgruppe").setHeader("Team");
        studentAlleGrid.getColumnByKey("uebungsgruppe").setHeader("Übungsgruppe");
        updateGridAlle();

        //Freischalten Bedienleiste
        freischaltenLabel = new Label("Freizuschaltende Studenten");
        alleFreischalten = new Button("Alle Studenten freischalten");
        alleFreischalten.addClickListener(e -> {
            alleFreischalten();
        });
        alleLöschen = new Button("Alle Studenten Löschen");
        alleLöschen.addClickListener(e -> {
            alleLöschenDialog().open();
        });
        filterFrei = new TextField("Tabelle filtern nach E-Mail, Vorname, Nachname");
        filterFrei.setPlaceholder("E-Mail, Vorname, Nachname");
        filterFrei.setClearButtonVisible(true);
        filterFrei.setValueChangeMode(ValueChangeMode.EAGER);
        filterFrei.setWidth("400px");
        filterFrei.addValueChangeListener(e -> {
            updateGridFilter(studentFreischaltenGrid, filterFrei);
        });
        HorizontalLayout freischaltenLeiste = new HorizontalLayout(freischaltenLabel, alleFreischalten, alleLöschen, filterFrei);
        freischaltenLeiste.setAlignItems(Alignment.BASELINE);

        //Grid zur Darstellung der freizuschaltenden Studenten
        studentFreischaltenGrid = new Grid<>(Student.class);
        studentFreischaltenGrid.setColumns("email", "vorname", "nachname", "matrikelNr", "studiengang");
        studentFreischaltenGrid.addComponentColumn(student -> freischaltenBtn(studentFreischaltenGrid,
                student)).setHeader("Freischalten");
        studentFreischaltenGrid.addComponentColumn(student -> löschenBtn(studentFreischaltenGrid,
                student)).setHeader("Löschen");
        updateGridFrei();


        setAlignItems(Alignment.CENTER);
        add(alleLeiste, studentAlleGrid, freischaltenLeiste, studentFreischaltenGrid);
    }

    // Füllt das Grid mit allen freigeschalteten Studenten
    private void updateGridAlle() {
        studentAlleGrid.setItems(studentRepo.findByFreigeschaltetTrue());
    }

    //Füllt das Grid mit allen freizuschaltenden Studenten
    private void updateGridFrei() {
        studentFreischaltenGrid.setItems(studentRepo.findByFreigeschaltetFalse());
    }

    //Füllt das Grid
    private void updateGridFilter(Grid<Student> grid, TextField input) {
        grid.setItems(studentRepo.findByEmailContainingIgnoreCaseOrVornameContainingIgnoreCaseOrNachnameContainingIgnoreCase(input.getValue(), input.getValue(), input.getValue()));
    }

    //erstellt einen Button zum Freischalten der Studenten
    private Button freischaltenBtn(Grid<Student> freischaltenGrid, Student student) {
        Button btn = new Button("Freischalten");
        btn.addClickListener(e -> {
            student.setFreigeschaltet(true);
            studentRepo.save(student);
            updateGridFrei();
            updateGridAlle();
        });
        return btn;
    }

    //erstellt einen Button zum Löschen der Studenten
    private Button löschenBtn(Grid<Student> grid, Student student) {
        Button btn = new Button("Löschen");
        btn.addClickListener(e -> {
            löschenBestätigen(student).open();
        });
        return btn;
    }

    //öffnet einen Dialog zum Bestätigen der Löschung
    private Dialog löschenBestätigen(Student student) {
        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(false);
        //Text
        Paragraph text1 = new Paragraph("Sind Sie sicher, dass Sie diese Aktion durchführen wollen?");
        Paragraph text2 = new Paragraph("Damit gehen alle assoziierten Daten (u.a. erzielte Leistungen)");
        Paragraph text3 = new Paragraph("unwiederbringlich verloren.");
        //Buttons
        Button nein = new Button("Nein");
        nein.addClickListener(e -> {
            dialog.close();
        });
        Button ja = new Button("Ja");
        ja.addClickListener(e -> {
            nv.deleteStudent(student);
            dialog.close();
            updateGridAlle();
            updateGridFrei();
        });
        //Layout
        HorizontalLayout btnLayout = new HorizontalLayout(nein, ja);
        VerticalLayout dialogLayout = new VerticalLayout(text1, text2, text3, btnLayout);
        dialog.add(dialogLayout);
        return dialog;
    }

    //öffnet einen Dialog zur Bestätigung der Löschung aller freizuschaltenden Studenten
    private Dialog alleLöschenDialog() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(false);
        //Text
        Paragraph text1 = new Paragraph("Sind Sie sicher, dass Sie diese Aktion durchführen wollen?");
        Paragraph text2 = new Paragraph("Damit gehen alle assoziierten Daten unwiederbringlich");
        Paragraph text3 = new Paragraph("verloren.");
        //Buttons
        Button nein = new Button("Nein");
        nein.addClickListener(e -> {
            dialog.close();
        });
        Button ja = new Button("Ja");
        ja.addClickListener(e -> {
            for (Student s : studentRepo.findByFreigeschaltetFalse())
                nv.deleteStudent(s);
            dialog.close();
            updateGridAlle();
            updateGridFrei();
        });
        //Layout
        HorizontalLayout btnLayout = new HorizontalLayout(nein, ja);
        VerticalLayout dialogLayout = new VerticalLayout(text1, text2, text3, btnLayout);
        dialog.add(dialogLayout);
        return dialog;
    }

    private Button bewertenBtn(Grid<Student> grid, Student s) {
        Button button = new Button("Bewerten");
        button.addClickListener(e -> {
        	bewertenDialog(s).open();
        });
        return button;
    }
    
    private Dialog bewertenDialog(Student s) {
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
        
    	ezBewDiaStud.setText("Bewertung für Student " + s.getVorname() +" " + s.getNachname() +" - Matrikelnummer: " +s.getMatrikelNr() + " für:");
        ezBewDialogErrPunkte.setValue(0.00);
        ezBewDialogMaxPunkte.setValue(0.00);
        ezBewDialogMaxPunkte.setEnabled(false);
        Label slash = new Label(" / ");
        speichernBtn.setEnabled(false);
        
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
                	if(!studLeistungRepo.findByStudentAndLeistung(s, selectedL).isEmpty()) {
                		ezBewDialogErrPunkte.setValue((double) studLeistungRepo.findByStudentAndLeistung(s, selectedL).get(0).getPunktzahl());
                	}
                	else {
                		ezBewDialogErrPunkte.setValue(0.00);
                	}
                    ezBewDialogMaxPunkte.setValue((double) selectedL.getMaxPunkte());
                	speichernBtn.setEnabled(true);
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
            	if(!studLeistungRepo.findByStudentAndLeistung(s, selectedL).isEmpty()) {
            		ezBewDialogErrPunkte.setValue((double) studLeistungRepo.findByStudentAndLeistung(s, selectedL).get(0).getPunktzahl());
            	}
            	else {
            		ezBewDialogErrPunkte.setValue(0.00);
            	}
                ezBewDialogMaxPunkte.setValue((double) selectedL.getMaxPunkte());
                speichernBtn.setEnabled(true);
            }
        });
        
        Button bewDialogAbbrechen = new Button("Abbrechen");
        bewDialogAbbrechen.addClickListener(e -> dialog.close());
        
        speichernBtn.addClickListener(e -> {
            double errPunkte = ezBewDialogErrPunkte.getValue();
            if (errPunkte >= 0) {
               
                	if(!studLeistungRepo.findByStudentAndLeistung(s, selectedL).isEmpty()) {
                		bewertungVerw.changeErgebnis(studLeistungRepo.findByStudentAndLeistung(s, selectedL).get(0), (float) errPunkte);
                	}
                	else { 
                		bewertungVerw.addErgebnis((float) errPunkte, s, selectedL);
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

    
    //erstellt einen Button zum Bearbeiten eines Studenten
    private Button bearbeitenBtn(Grid<Student> grid, Student student) {
        Button btn = new Button("Bearbeiten");
        btn.addClickListener(e -> {
            bearbeitenDialog(student).open();
        });
        return btn;
    }

    //öffnet einen Dialog zum Bearbeiten der Studentendaten
    private Dialog bearbeitenDialog(Student student) {
        Dialog dialog = new Dialog();

        //Items zum Input
        TextField vorname = new TextField("Vorname");
        vorname.setValue(student.getVorname());
        vorname.setValueChangeMode(ValueChangeMode.EAGER);
        vorname.setWidth(WIDTH_NAME);

        TextField nachname = new TextField("Nachname");
        nachname.setValue(student.getNachname());
        nachname.setValueChangeMode(ValueChangeMode.EAGER);
        nachname.setWidth(WIDTH_NAME);

        NumberField matrNr = new NumberField("Matrikelnummer");
        matrNr.setValue((double)student.getMatrikelNr());
        matrNr.setValueChangeMode(ValueChangeMode.EAGER);
        
        ComboBox<Studiengang> studiengang = new ComboBox<>("Studiengang");
        studiengang.setItemLabelGenerator(Studiengang::getKurzname);
        studiengang.setItems(studiengangRepo.findAll());
        studiengang.setValue(student.getStudiengang());
        studiengang.setWidth(WIDTH_NAME);

        ComboBox<Uebungsgruppe> gruppe = new ComboBox<>("Übungsgruppe");
        gruppe.setItemLabelGenerator(Uebungsgruppe::getName);
        gruppe.setItems(gruppeRepo.findAll());
        gruppe.setValue(student.getUebungsgruppe());
        gruppe.setWidth(WIDTH_NAME);
        
        ComboBox<Projektgruppe> team = new ComboBox<>("Team");
        team.setItemLabelGenerator(Projektgruppe::getName);
        team.setItems(teamRepo.findAll());
        team.setValue(student.getProjektgruppe());
        team.setWidth(WIDTH_NAME);

        //Buttons zur Navigation
        Button zurück = new Button("Zurück");
        Button speichern = new Button("Änderungen übernehmen");
        speichern.setEnabled(false);
        
        //Eingabevalidierung
        ArrayList<TextField> tfLeer = new ArrayList<TextField>();
        tfLeer.add(vorname);
        tfLeer.add(nachname);
        ArrayList<NumberField> nfLeer = new ArrayList<NumberField>();
        nfLeer.add(matrNr);
        ArrayList<ComboBox> cbLeer = new ArrayList<ComboBox>();
        cbLeer.add(team);
        cbLeer.add(gruppe);
        cbLeer.add(studiengang);

        binderStudent.forField(vorname)
                .asRequired("Pflichtfeld")
                .withValidator(v -> v.matches(SwtConst.REGEX_NAME), SwtConst.REGEX_NAME_ERROR)
                .bind(Student::getVorname, Student::setVorname);
        
        binderStudent.forField(nachname)
                .asRequired("Pflichtfeld")
                .withValidator(v -> v.matches(SwtConst.REGEX_NAME), SwtConst.REGEX_NAME_ERROR)
                .bind(Student::getNachname, Student::setNachname);
        
        binderStudent.forField(matrNr)
                .asRequired("Pflichtfeld")
                .withConverter(new MyDoubleToStringConverter())
                .withValidator(v -> v.matches(SwtConst.REGEX_MATRIKEL), SwtConst.REGEX_MATRIKEL_ERROR)
                .withConverter(new MyStringToIntegerConverter())
                .bind(Student::getMatrikelNr, Student::setMatrikelNr);
        

        studiengang.setRequired(true);
        studiengang.setRequiredIndicatorVisible(true);
        
        team.setRequired(true);
        team.setRequiredIndicatorVisible(true);
        
        gruppe.setRequired(true);
        gruppe.setRequiredIndicatorVisible(true);

        //Listener für Inputs und Buttons
        vorname.addValueChangeListener(e -> {
            speichern.setEnabled(checkEnableButton(tfLeer,nfLeer,cbLeer));
        });
        nachname.addValueChangeListener(e -> {
            speichern.setEnabled(checkEnableButton(tfLeer,nfLeer,cbLeer));
        });
        matrNr.addValueChangeListener(e -> {
            speichern.setEnabled(checkEnableButton(tfLeer,nfLeer,cbLeer));
        });
        studiengang.addValueChangeListener(e -> {
            speichern.setEnabled(checkEnableButton(tfLeer,nfLeer,cbLeer));
        });
        gruppe.addValueChangeListener(e -> {
            speichern.setEnabled(checkEnableButton(tfLeer,nfLeer,cbLeer));
        });
        team.addValueChangeListener(e -> {
            speichern.setEnabled(checkEnableButton(tfLeer,nfLeer,cbLeer));
        });
        zurück.addClickListener(e -> {
            dialog.close();
        });
        speichern.addClickListener(e -> {
            nv.changeStudent(student,
            		vorname.getValue(),
            		nachname.getValue(),
            		(int) Math.round(matrNr.getValue()),
                    studiengang.getValue(),
                    gruppe.getValue(),
                    team.getValue());

            //Hinzufügen des Studenten zu Team und Gruppe
//            Projektgruppe studentTeam = teamRepo.findByName(student.getProjektgruppe().getName()).get(0);
//            studentTeam.getStudentList().add(student);
//            studentTeam.setAnzahlAktuell(studentTeam.getAnzahlAktuell() + 1);
//            teamRepo.save(studentTeam);
            //Uebungsgruppe studentGruppe = gruppeRepo.findById(student.getUebungsgruppe().getName()).get();	//->org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException
            //studentGruppe.getStudentList().add(student);
            //gruppeRepo.save(studentGruppe);

            dialog.close();
            updateGridAlle();
        });
        
        

        //Layout
        HorizontalLayout buttons = new HorizontalLayout(zurück, speichern);
        VerticalLayout dialogLayout = new VerticalLayout(vorname, nachname, matrNr, studiengang, gruppe, team, buttons);
        dialog.add(dialogLayout);
        return dialog;
    }

    /**
     * Methode prüft, ob die übergeben Listen von Vaadin-Feldern leer oder als ungültig markiert sind.
     *
     * @param textFields
     * @param numFields
     * @param comboBoxes
     * @return true, wenn alle Felder weder ungültig noch leer sind.
     */
    private Boolean checkEnableButton(
    		ArrayList<TextField> textFields,
    		ArrayList<NumberField> numFields,
    		ArrayList<ComboBox> comboBoxes) {
        if (textFields != null) {
            for (TextField comp : textFields) {
                if (comp.isEmpty() || comp.isInvalid()) {
                    return false;
                }
            }
        }

        if (numFields != null) {
            for (NumberField comp : numFields) {
                if (comp.isEmpty() || comp.isInvalid()) {
                    return false;
                }
            }
        }

        if (comboBoxes != null) {
            for (ComboBox comp : comboBoxes) {
                if (comp.isEmpty() || comp.isInvalid()) {
                    return false;
                }
            }
        }
        return true;
    }

    //schaltet alle noch nicht freigeschalteten Studenten frei
    private void alleFreischalten() {
        for (Student s : studentRepo.findByFreigeschaltetFalse()) {
            s.setFreigeschaltet(true);
            studentRepo.save(s);
        }
        updateGridFrei();
        updateGridAlle();
    }
}

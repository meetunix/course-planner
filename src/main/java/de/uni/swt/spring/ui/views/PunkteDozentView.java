package de.uni.swt.spring.ui.views;

import com.helger.commons.csv.CSVWriter;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;

import de.uni.swt.spring.backend.Verwaltung.Bewertungsverwaltung;
import de.uni.swt.spring.backend.data.entity.*;
import de.uni.swt.spring.backend.repositories.*;
import de.uni.swt.spring.ui.MainView;
import de.uni.swt.spring.ui.utils.SwtConst;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;

import javax.annotation.PostConstruct;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

@HtmlImport("frontend://styles/shared-styles.html")
@Route(value = SwtConst.PAGE_PUNKTE_DOZENT, layout = MainView.class)
@Secured("Dozent")
public class PunkteDozentView extends VerticalLayoutSecured {
    Leistungskomplex selectedLK = new Leistungskomplex();
    Leistungsblock selectedLB = new Leistungsblock();
    Leistung selectedL = new Leistung();
    Projektgruppe selectedProjgruppe = new Projektgruppe();
    
    ComboBox<Projektgruppe> gruppenAusw = new ComboBox<Projektgruppe>("Team");
    ComboBox<Leistungskomplex> lkAusw = new ComboBox<Leistungskomplex>("Leistungskomplex");
    ComboBox<Leistungsblock> lbAusw = new ComboBox<Leistungsblock>("Leistungsblock");
    ComboBox<Leistung> lAusw = new ComboBox<Leistung>("Leistung");
    
    Grid<StudentLeistung> studentLeistungen = new Grid<>(StudentLeistung.class);
    Grid<Student> studentExportGrid = new Grid <>();
    
    Button projGrBewerten = new Button("Ganzes Team bewerten");
    Dialog bewDialog = new Dialog();
    Label bewDiaTeam = new Label("");
    Label bewDiaLeistung = new Label("");
    Label bewDiaErrorLabel = new Label("");
    VerticalLayout bewDiaLayout = new VerticalLayout();
    HorizontalLayout bewDiaPunkteLayout = new HorizontalLayout();
    HorizontalLayout bewDiaButtonLayout = new HorizontalLayout();
    NumberField bewDialogErrPunkte = new NumberField();
    NumberField bewDialogMaxPunkte = new NumberField();
    
    Dialog ezBewDialog = new Dialog();
    Label ezBewDiaStud = new Label("");
    Label ezBewDiaLeistung = new Label("");
    Label ezBewDiaErrorLabel = new Label("");
    VerticalLayout ezBewDiaLayout = new VerticalLayout();
    HorizontalLayout ezBewDiaPunkteLayout = new HorizontalLayout();
    HorizontalLayout ezBewDiaButtonLayout = new HorizontalLayout();
    NumberField ezBewDialogErrPunkte = new NumberField("Erreichte Punkte");
    NumberField ezBewDialogMaxPunkte = new NumberField("Maximale Punkte");
    Student selectedStud = new Student();
    Leistung savingSl = new Leistung();
    Label slash = new Label(" / ");
    
    @Autowired
    private LeistungskomplexRepository leistungskomplexRepo;
    @Autowired
    private LeistungsblockRepository leistungsblockRepo;
    @Autowired
    private LeistungRepository leistungRepo;
    @Autowired
    private UebungsgruppeRepository uebungsgruppenRepo;
    @Autowired
    private StudentLeistungRepository studLeistungRepo;
    @Autowired
    private StudentRepository studentRepo;
    @Autowired
    private ProjektgruppeRepository projektgruppeRepo;
    @Autowired
    private Bewertungsverwaltung bewVerw;

    @PostConstruct
    public void initDozent() {
        VerticalLayout page = new VerticalLayout();
        HorizontalLayout kopfzeile = new HorizontalLayout();
        selectedLB = null;
        selectedLK = null;
        selectedL = null;
        selectedProjgruppe = null;
        gruppenAusw.setItemLabelGenerator(Projektgruppe::getName);
        gruppenAusw.setItems(projektgruppeRepo.findAll());

        lkAusw.setItemLabelGenerator(Leistungskomplex::getName);
        lkAusw.setItems(leistungskomplexRepo.findAll());

        lbAusw.setItemLabelGenerator(Leistungsblock::getName);
        lbAusw.setEnabled(false);

        lAusw.setItemLabelGenerator(Leistung::getName);
        lAusw.setEnabled(false);

        NumberField punkteMax = new NumberField("Maximale Punkte");
        punkteMax.setEnabled(false);

        kopfzeile.setAlignItems(Alignment.BASELINE);
        page.setAlignItems(Alignment.CENTER);
        setAlignItems(Alignment.CENTER);
        page.setWidth("70%");

        gruppenAusw.addValueChangeListener(e -> {
            selectedProjgruppe = e.getValue();
            if (selectedProjgruppe != null && selectedL != null) {
                projGrBewerten.setEnabled(true);
            } else {
                projGrBewerten.setEnabled(false);
            }
            refreshGrid();
        });

        projGrBewerten.addClickListener(e -> {
            refreshBewDialog();
            bewDialog.open();

        });
        projGrBewerten.setEnabled(false);

        //Bei Veränderung der LK Combobox werden LB- und L-Comboboxen entsprechend des ausgewählten LK aktualisiert
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
            refreshGrid();
        });

        //Bei Veränderung der LB Combobox werden L-Comboboxen entsprechend des ausgewählten LK & LB aktualisiert
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
            refreshGrid();
        });

        //Bei Veränderung der L Combobox wird das NumberField punkteMax entsprechend der erreichbaren Maximalpunktzahl der Leistung aktualisiert
        lAusw.addValueChangeListener(e -> {
            selectedL = e.getValue();
            if (e.getSource().isEmpty()) {
                punkteMax.setValue(0.00);
            } else {
                punkteMax.setValue((double) selectedL.getMaxPunkte());
            }
            if (selectedProjgruppe != null && selectedL != null) {
                projGrBewerten.setEnabled(true);
            } else {
                projGrBewerten.setEnabled(false);
            }
            refreshGrid();
        });

        //fügt dem horizontalen Layout 'kopfzeile' die items der Gruppenauswahl, LK-,LB-, L-Auswahl und maximale Punkte hinzu
        kopfzeile.add(gruppenAusw, lkAusw, lbAusw, lAusw, /*projGrBewerten,*/ punkteMax);
        kopfzeile.setAlignItems(Alignment.BASELINE);

        studentLeistungen.setColumns("student.matrikelNr", "student.nachname", "student.vorname", "leistung.leistungsblock.leistungskomplex.name", "leistung.leistungsblock.name", "leistung.name", "punktzahl", "leistung.maxPunkte");
        studentLeistungen.getColumnByKey("leistung.leistungsblock.leistungskomplex.name").setHeader("Komplex");
        studentLeistungen.getColumnByKey("leistung.leistungsblock.name").setHeader("Block");
        studentLeistungen.getColumnByKey("leistung.name").setHeader("Leistung");
        studentLeistungen.addComponentColumn(studLeistung -> bewertenBtn(studentLeistungen, studLeistung)).setHeader("Korrektur");
        studentLeistungen.setItems(studLeistungRepo.findAll());
       
        studentExportGrid.addColumn(Student :: getMatrikelNr).setKey("matrikelNr").setHeader("MatrikelNr").setFrozen(true).setWidth("120px");
        studentExportGrid.addColumn(Student :: getNachname).setKey("nachname").setHeader("Nachname").setFrozen(true).setWidth("150px");
        studentExportGrid.addColumn(Student :: getVorname).setKey("vorname").setHeader("Vorname").setFrozen(true).setWidth("120px");
        studentExportGrid.addComponentColumn(student -> new Label(bewVerw.zugelassen(student) ? "Ja" : "Nein")).setHeader("Zugelassen").setSortable(true).setFrozen(true).setWidth("130px");
        //studentExportGrid.addColumn(student -> (bewVerw.zugelassen(student))).setHeader("Zugelassen").setSortable(true).setFrozen(true).setWidth("150px");
        for(Leistungskomplex lk : leistungskomplexRepo.findAll()) {
        	studentExportGrid.addColumn(student -> (berechneErrPunkte(lk, student))).setHeader(lk.getName() +": Punkte").setSortable(true).setWidth("200px");
        	studentExportGrid.addColumn(student -> (berechneGesamtpunkte(lk))).setHeader(lk.getName() +": max. P.").setWidth("150px");
        	studentExportGrid.addColumn(student -> (bewVerw.berechneProzentVomKomplex(lk, student))).setHeader(lk.getName() +": %").setSortable(true).setWidth("150px");
        	studentExportGrid.addColumn(student -> (lk.getHuerde())).setHeader(lk.getName() +": % Hürde").setWidth("150px");
        }
        studentExportGrid.setItems(studentRepo.findAll());
   //     Anchor exportCSV = new Anchor(new StreamResource("StudentLeistungen.csv", Exporter.exportAsCSV(studentExportGrid)), "Download als .csv");
        page.add(new Label("Verzeichnis aller Studentleistungen"),kopfzeile, studentLeistungen, new Label ("Übersicht über Leistungsstatus aller Studenten"),studentExportGrid, new Anchor(new StreamResource("grid.csv", this::getInputStream), "Export als CSV"));
        page.setAlignItems(Alignment.CENTER);
        add(page);

        //Dialog für Gruppenbewertung
        
        bewDialog.setCloseOnEsc(false);
        bewDialog.setCloseOnOutsideClick(false);
        bewDialogMaxPunkte.setEnabled(false);
        
        Button bewDialogSpeichern = new Button("Bewertung für gesamtes Team speichern");
        bewDialogSpeichern.addClickListener(e -> {
            double errPunkte = bewDialogErrPunkte.getValue();
            if (errPunkte >= 0) {
                for (Student s : selectedProjgruppe.getStudentList()) {
                	if(!studLeistungRepo.findByStudentAndLeistung(s, selectedL).isEmpty()) {
                		bewVerw.changeErgebnis(studLeistungRepo.findByStudentAndLeistung(s, selectedL).get(0), (float) errPunkte);
                	}
                	else { 
                		bewVerw.addErgebnis((float) errPunkte, s, selectedL);
                	}
                }
                bewDialog.close();
                
            } else {
                bewDiaErrorLabel.setText("Ungültige Eingabe!");
            }
        });
        
        Button bewDialogAbbrechen = new Button("Abbrechen");
        bewDialogAbbrechen.addClickListener(e -> bewDialog.close());

        bewDiaButtonLayout.add(bewDialogAbbrechen, bewDialogSpeichern);

        bewDiaPunkteLayout.add(bewDialogErrPunkte, slash, bewDialogMaxPunkte);
        bewDiaLayout.add(bewDiaTeam, bewDiaLeistung, bewDiaPunkteLayout, bewDiaErrorLabel, bewDiaButtonLayout);
        bewDialog.add(bewDiaLayout);
        
        //Dialog für Einzelbewertung
        
        ezBewDialog.setCloseOnEsc(false);
        ezBewDialog.setCloseOnOutsideClick(false);
        ezBewDialogMaxPunkte.setEnabled(false);
        
        Button ezBewDialogSpeichern = new Button("Bewertung speichern");
        ezBewDialogSpeichern.addClickListener(e -> {
            double errPunkte = ezBewDialogErrPunkte.getValue();
            if (errPunkte >= 0) {
               
                	if(!studLeistungRepo.findByStudentAndLeistung(selectedStud, savingSl).isEmpty()) {
                		bewVerw.changeErgebnis(studLeistungRepo.findByStudentAndLeistung(selectedStud, savingSl).get(0), (float) errPunkte);
                	}
                	else { 
                		bewVerw.addErgebnis((float) errPunkte, selectedStud, savingSl);
                	}
                ezBewDialog.close();
                refreshGrid();
                
            } else {
                ezBewDiaErrorLabel.setText("Ungültige Eingabe!");
            }
        });

        Button ezBewDialogAbbrechen = new Button("Abbrechen");
        ezBewDialogAbbrechen.addClickListener(e -> ezBewDialog.close());

        ezBewDiaButtonLayout.add(ezBewDialogAbbrechen, ezBewDialogSpeichern);
        
        ezBewDiaPunkteLayout.add(ezBewDialogErrPunkte, slash, ezBewDialogMaxPunkte);
        ezBewDiaPunkteLayout.setAlignItems(Alignment.BASELINE);
        ezBewDiaLayout.add(ezBewDiaStud, ezBewDiaLeistung, ezBewDiaPunkteLayout, ezBewDiaErrorLabel, ezBewDiaButtonLayout);
        ezBewDiaLayout.setAlignItems(Alignment.CENTER);
        ezBewDialog.add(ezBewDiaLayout);
        

    }

    public NumberField createErreichtePunkteFeld(Student student) {
        NumberField erreichtePunkte = new NumberField();

        //sucht zunächst nach einer vorhandenen StudentLeistung, wenn nicht vorhanden -> Wert auf 0.00 gesetzt
        List<StudentLeistung> studLeistung = studLeistungRepo.findByStudentAndLeistung(student, selectedL);
        if (!studLeistung.isEmpty()) {
            erreichtePunkte.setValue(studLeistung.get(0).getPunktzahl().doubleValue());
        } else {
            erreichtePunkte.setValue(0.00);
        }
        erreichtePunkte.addValueChangeListener(e -> {
            if ((!e.getSource().isEmpty()) && (e.getValue() <= selectedL.getMaxPunkte())) {
                bewVerw.addErgebnis((e.getValue().floatValue()), student, selectedL);
            }
        });
        return erreichtePunkte;
    }

    public void refreshGrid() {
        List<StudentLeistung> studLeistungen = new ArrayList<StudentLeistung>();

        if (selectedLK == null && selectedLB == null && selectedL == null) {
            studLeistungen = studLeistungRepo.findAll();
        }

        else if (selectedLK != null && selectedLB == null && selectedL == null) {
            List<StudentLeistung> studLeistungPerLK = new ArrayList<StudentLeistung>();
            for (Leistungsblock lb : leistungsblockRepo.findByLeistungskomplex(selectedLK)) {
                for (Leistung l : leistungRepo.findByLeistungsblock(lb)) {
                    for (StudentLeistung sl : l.getStudentLeistungList()) {
                        studLeistungPerLK.add(sl);
                    }
                }
            }
            studLeistungen = studLeistungPerLK;
        }

        else if (selectedLK != null && selectedLB != null && selectedL == null) {
            List<StudentLeistung> studLeistungPerLB = new ArrayList<StudentLeistung>();
            for (Leistung l : leistungRepo.findByLeistungsblock(selectedLB)) {
                for (StudentLeistung sl : l.getStudentLeistungList()) {
                    studLeistungPerLB.add(sl);
                }
            }
            studLeistungen = studLeistungPerLB;
        }

        else if (selectedLK != null && selectedLB != null && selectedL != null) {
            studLeistungen = studLeistungRepo.findByLeistung(selectedL);
        }

        if (selectedProjgruppe != null) {
        	List <StudentLeistung> newStudLeistungen = new ArrayList<StudentLeistung>();
            for (StudentLeistung sl : studLeistungen) {
            	if (sl.getStudent().getProjektgruppe() != null) {
	                if (sl.getStudent().getProjektgruppe().getName() == selectedProjgruppe.getName()) {
	                    newStudLeistungen.add(sl);
	                }
            	}
            }
            studLeistungen = newStudLeistungen;
        }
        studentLeistungen.setItems(studLeistungen);
    }

    public void refreshBewDialog() {
        bewDiaLeistung.setText(selectedLK.getName() + " - " + selectedLB.getName() + " - " + selectedL.getName());
        bewDiaTeam.setText("Bewertung für Team " + selectedProjgruppe.getName() + " für:");
        if(!selectedProjgruppe.getStudentList().isEmpty() && !studLeistungRepo.findByStudentAndLeistung(selectedProjgruppe.getStudentList().get(0), selectedL).isEmpty()) {
            bewDialogErrPunkte.setValue((double)studLeistungRepo.findByStudentAndLeistung(selectedProjgruppe.getStudentList().get(0), selectedL).get(0).getPunktzahl());
        }
        else {
        	bewDialogErrPunkte.setValue(0.0);
        }
        bewDialogMaxPunkte.setValue((double) selectedL.getMaxPunkte());
    }
    
    private Button bewertenBtn(Grid<StudentLeistung> grid, StudentLeistung sl) {
        Button button = new Button("Korrektur");
        button.addClickListener(e -> {
        	refreshEzBewDialog(sl);
        	ezBewDialog.open();
        });
        return button;
    }

    public void refreshEzBewDialog(StudentLeistung sl) {
    	selectedStud = sl.getStudent();
    	savingSl = sl.getLeistung();
        ezBewDiaLeistung.setText(sl.getLeistung().getLeistungsblock().getLeistungskomplex().getName() + " - " + sl.getLeistung().getLeistungsblock().getName() + " - " + sl.getLeistung().getName());
        ezBewDiaStud.setText("Bewertung für Student " + sl.getStudent().getVorname() +" " + sl.getStudent().getNachname() +" - " +sl.getStudent().getMatrikelNr() + " für:");
        ezBewDialogErrPunkte.setValue((double)sl.getPunktzahl());
        ezBewDialogMaxPunkte.setValue((double)sl.getLeistung().getMaxPunkte());
    }
    
    public float berechneGesamtpunkte(Leistungskomplex lk) {
    	float gesamtpunkte = 0.0f;
    	for (Leistungsblock lb : lk.getLeistungsblockList()) {
			for (Leistung l : lb.getLeistungsList()) {
				gesamtpunkte +=l.getMaxPunkte();
			}
		}
    	return gesamtpunkte;
    }
    
    public float berechneErrPunkte(Leistungskomplex lk, Student student) {
    	float errPunkte = 0.0f;
    	for (StudentLeistung sl : student.getStudentLeistungList()) {
    		if (sl.getLeistung().getLeistungsblock().getLeistungskomplex().getName() == lk.getName()) {
    			errPunkte += sl.getPunktzahl();
    		}
    	}    	
    	return errPunkte;
    }
    
    private InputStream getInputStream() {
        try {
            StringWriter stringWriter = new StringWriter();

            CSVWriter csvWriter = new CSVWriter(stringWriter);
            String header = "";
            header += "Matrikelnummer, Nachname, Vorname, zugelassen,";
            for (Leistungskomplex lk : leistungskomplexRepo.findAll()) {
            	header += "" +lk.getName() +": erreichte Punkte,";
            	header += "" +lk.getName() +": maximale Punkte,";
            	header += "" +lk.getName() +": erreichte %,";
            	header += "" +lk.getName() +": erforderliche %,";
            }
            csvWriter.writeNext(header);
            for(Student s : studentRepo.findAll()) {
            	String student = "";
            	student += s.getMatrikelNr() +",";
            	student += s.getNachname() +",";
            	student += s.getVorname() +",";
            	if(bewVerw.zugelassen(s) == true) {
            		student += "Ja,";
            	}
            	else {
            		student+= "Nein,";
            		}
            	for(Leistungskomplex lk : leistungskomplexRepo.findAll()) {
                	student += berechneErrPunkte(lk, s) +",";
                	student += berechneGesamtpunkte(lk) +",";
                	student += bewVerw.berechneProzentVomKomplex(lk, s) +",";
                	student += lk.getHuerde() +",";
            	}
            	csvWriter.writeNext(student);
            }
            return IOUtils.toInputStream(stringWriter.toString(), "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
}


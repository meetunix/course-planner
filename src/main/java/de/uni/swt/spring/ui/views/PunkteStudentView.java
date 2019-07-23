package de.uni.swt.spring.ui.views;

import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.details.DetailsVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.progressbar.ProgressBarVariant;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.Route;
import de.uni.swt.spring.backend.Verwaltung.Bewertungsverwaltung;
import de.uni.swt.spring.backend.data.entity.Leistung;
import de.uni.swt.spring.backend.data.entity.Leistungsblock;
import de.uni.swt.spring.backend.data.entity.Leistungskomplex;
import de.uni.swt.spring.backend.data.entity.Student;
import de.uni.swt.spring.backend.data.entity.StudentLeistung;
import de.uni.swt.spring.backend.repositories.*;
import de.uni.swt.spring.ui.MainView;
import de.uni.swt.spring.ui.utils.SwtConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

@HtmlImport("frontend://styles/shared-styles.html")
@Route(value = SwtConst.PAGE_PUNKTE_STUDENT, layout = MainView.class)
@Secured("Student")
public class PunkteStudentView extends VerticalLayoutSecured {
    Accordion accordion = new Accordion();
    @Autowired
    private LeistungskomplexRepository leistungskomplexRepo;
    @Autowired
    private LeistungsblockRepository leistungsblockRepo;
    @Autowired
    private LeistungRepository leistungRepo;
    @Autowired
    private StudentLeistungRepository studentLeistungRepo;
    @Autowired
    private UebungsgruppeRepository uebungsgruppenRepo;
    @Autowired
    private StudentRepository studentRepo;
    @Autowired
    private Bewertungsverwaltung bv;

    @PostConstruct
    public void init() {
        String studentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Student currStudent = studentRepo.findByEmail(studentEmail);

        VerticalLayout pageLayout = new VerticalLayout();
        for (Leistungskomplex lk : leistungskomplexRepo.findAll()) {
            VerticalLayout lkProgress = new VerticalLayout();
            VerticalLayout lkL = new VerticalLayout();

            for (Leistungsblock lb : lk.getLeistungsblockList()) {
                //Füllt den Leistungskomplexcontainer mit allen Leistungsbl�cken
                VerticalLayout lbL = new VerticalLayout();
                HorizontalLayout nameGewichtung = new HorizontalLayout();
                Integer gewichtung = lb.getGewichtung();
                Label leistungsbF = new Label(lb.getName());
                Label gewichtungL = new Label(" - Gewichtung: " + gewichtung + "%");
                nameGewichtung.add(leistungsbF, gewichtungL);
                lbL.add(nameGewichtung);

                for (Leistung l : lb.getLeistungsList()) {
                    //Setzt für jede Einzelleistung ein HorizontalLayout mit Name, erreichten und maximal erreichbaren Punkten zusammen
                    HorizontalLayout einzelleistungen = new HorizontalLayout();
                    Label leistungF = new Label(l.getName());
                    NumberField punkteErreicht = new NumberField("");
                    if (!studentLeistungRepo.findByStudentAndLeistung(currStudent,l).isEmpty()) {
                    	List <StudentLeistung> slList = new ArrayList<>();
                    	slList = studentLeistungRepo.findByStudentAndLeistung(currStudent, l);
                    	punkteErreicht.setValue((double)slList.get(0).getPunktzahl());
                    }
                    else punkteErreicht.setValue(0.0);
                    punkteErreicht.setEnabled(false);
                    Label slash = new Label(" / ");
                    NumberField punkteMax = new NumberField("");
                    punkteMax.setValue((double) l.getMaxPunkte());
                    punkteMax.setEnabled(false);
                    Label punkteL = new Label("Punkten");

                    einzelleistungen.setAlignItems(Alignment.CENTER);

                    einzelleistungen.add(leistungF, punkteErreicht, slash, punkteMax, punkteL);

                    //Füllt den Leistungsblockcontainer mit allen Einzelleistungen
                    lbL.add(einzelleistungen);
                }
                lkL.add(lbL);
            }
            Integer prozentHuerde = lk.getHuerde();


            float prozentErreicht = bv.berechneProzentVomKomplex(lk, currStudent); // TODO

            //initialisiert die Fortschrittsleiste (rot so lange unter der H�rde, gr�n sobald �ber der H�rde
            ProgressBar progressBar = new ProgressBar(0.00, 100.00);
            progressBar.setValue((double)prozentErreicht);
            if (prozentErreicht < prozentHuerde) {
                progressBar.addThemeVariants(ProgressBarVariant.LUMO_ERROR);
            } else {
                progressBar.addThemeVariants(ProgressBarVariant.LUMO_SUCCESS);
            }

            //Füllt das Akkordion mit LK Fortschrittsbalken, darunter der Leistungskomplexcontainer
            lkProgress.add(progressBar, lkL);
            accordion.add(lk.getName() + ": " + prozentErreicht + "% (" + prozentHuerde + "% benötigt)", lkProgress)
                    .addThemeVariants(DetailsVariant.FILLED);
        }
        Label überschrift = new Label();
        String text = "";
        if (currStudent != null) {
            text = currStudent.getVorname() + " " +
                    currStudent.getNachname() + " - Matrikelnummer: " +
                    currStudent.getMatrikelNr();
            if (currStudent.getStudiengang() != null)
            	text += ", " + currStudent.getStudiengang();
        } else {
            text = "Sie sind nicht als Student angemeldet!";
        }
        überschrift.setText(text);
//		Label ueberschrift = new Label ("Max Mustermann - Matrikelnummer 205747182, Informatik"); //TODO student �bergeben

        //Initialisiert die Seite mit �berschrift und dem Leistungskomplexakkordion
        pageLayout.add(überschrift, accordion);
        pageLayout.setAlignItems(Alignment.CENTER);
        setAlignItems(Alignment.CENTER);
        pageLayout.setWidth("70%");
        add(pageLayout);
    }
}

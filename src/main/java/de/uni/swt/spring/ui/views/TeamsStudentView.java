package de.uni.swt.spring.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.uni.swt.spring.backend.data.entity.Projektgruppe;
import de.uni.swt.spring.backend.data.entity.Student;
import de.uni.swt.spring.backend.data.entity.Uebungsgruppe;
import de.uni.swt.spring.backend.repositories.ProjektgruppeRepository;
import de.uni.swt.spring.backend.repositories.StudentRepository;
import de.uni.swt.spring.backend.repositories.UebungsgruppeRepository;
import de.uni.swt.spring.ui.MainView;
import de.uni.swt.spring.ui.utils.SwtConst;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;


@HtmlImport("frontend://styles/shared-styles.html")
@Route(value = SwtConst.PAGE_TEAMS_GRUPPEN, layout = MainView.class)
@Secured("Student")
public class TeamsStudentView extends VerticalLayoutSecured {
    private final ProjektgruppeRepository teamRepo;
    private final UebungsgruppeRepository gruppeRepo;
    private final StudentRepository studentRepo;
    private final Label gruppenLabel = new Label("Übungsgruppen");
    private final Label teamsLabel = new Label("Teams");
    private Grid<Uebungsgruppe> gridGruppen;
    private Grid<Projektgruppe> gridTeams;
    private ComboBox<Uebungsgruppe> wunschtermin;
    private Button terminSpeichern;
    private Student currentUser;
    private Checkbox teamsWunschtermin;

    public TeamsStudentView(ProjektgruppeRepository t, UebungsgruppeRepository g, StudentRepository s) {
        this.teamRepo = t;
        this.gruppeRepo = g;
        this.studentRepo = s;
        VerticalLayout page = new VerticalLayout();
        currentUser = aktuellerNutzer();

        //Textfeld zum Eintragen des Wunschtermins
        wunschtermin = new ComboBox<>("Wunschübungsgruppe");
        wunschtermin.setItems(gruppeRepo.findAll());
        try {
            wunschtermin.setValue(currentUser.getWunschtermin());
        } catch (NullPointerException e) {
            wunschtermin.setValue(null);
        }

        //Button zum Speichern der Änderung des Wunschtermins
        terminSpeichern = new Button("Änderung übernehmen");
        terminSpeichern.addClickListener(e -> {
            currentUser.setWunschtermin(wunschtermin.getValue());
            studentRepo.save(currentUser);
            currentUser = aktuellerNutzer();
        });

        //Layout für Wunschtermin
        HorizontalLayout wunschterminLayout = new HorizontalLayout(wunschtermin, terminSpeichern);
        wunschterminLayout.setAlignItems(Alignment.BASELINE);

        //Grid zur Anzeige der Übungsgruppen
        gridGruppen = new Grid<>(Uebungsgruppe.class);
        gridGruppen.setColumns("name", "dozent", "tag", "termin");
        gridGruppen.setItems(gruppeRepo.findAll());
        
        //Checkbox, mit der nur die Projektgruppen zum Wunschtermin angezeigt werden
        teamsWunschtermin = new Checkbox("Nur Teams anzeigen, die an meinem Wunschtermin stattfinden");
        teamsWunschtermin.addValueChangeListener(e -> {
        	if(e.getValue() && currentUser.getWunschtermin() != null)
        		gridTeams.setItems(teamRepo.findByUebungsgruppe(currentUser.getWunschtermin()));
        	else
        		updateTeamGrid();
        });

        //Grid zur Anzeige der Projektgruppen
        gridTeams = new Grid<>(Projektgruppe.class);
        gridTeams.setColumns("name", "thema", "anzahlAktuell", "anzahlMax", "uebungsgruppe");
        gridTeams.addComponentColumn(team -> beitretenBtn(gridTeams, team)).setHeader("Beitreten");
        gridTeams.addComponentColumn(team -> verlassenBtn(gridTeams, team)).setHeader("Austreten");
        gridTeams.addComponentColumn(team -> mitgliederBtn(gridTeams, team)).setHeader("Mitglieder");
        gridTeams.getColumnByKey("uebungsgruppe").setHeader("Übungsgruppe");
        updateTeamGrid();

        gridGruppen.setHeightByRows(true);
        gridTeams.setHeightByRows(true);

        page.setAlignItems(Alignment.CENTER);
        setAlignItems(Alignment.CENTER);
        page.setWidth("70%");
        page.add(wunschterminLayout, gruppenLabel, gridGruppen, teamsLabel, teamsWunschtermin, gridTeams);

        add(page);
    }

    //Gibt den aktuellen Datenbankeintrag zum Nutzer zurück
    private Student aktuellerNutzer() {
        return studentRepo.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    //Füllt das Teamgrid mit allen Projektgruppen
    private void updateTeamGrid() {
        gridTeams.setItems(teamRepo.findAll());
    }

    //Erstellt einen Button, bei Klick wird der Student/Nutzer in das Team eingetragen
    private Button beitretenBtn(Grid<Projektgruppe> grid, Projektgruppe team) {
        Button button = new Button("Beitreten");
        //Nur aktiviert, wenn Team geöffnet, Nutzer in keiner Projektgruppe, AnzahlMax noch nicht erreicht, Nutzer
        button.setEnabled(
			team.getOffen() &&
			currentUser.getProjektgruppe() == null &&
			team.getAnzahlAktuell() < team.getAnzahlMax() &&
			!team.getStudentList().contains(currentUser) //&&
			//team.getZusammensetzung().nochPlatz(aktuellerNutzer().getStudiengang(), team)
        );

        button.addClickListener(e -> {
            beitreten(team);
            updateTeamGrid();
        });

        return button;
    }

    //Trägt den Nutzer in die Projektgruppe ein und ändert die nötigen Referenzen
    private void beitreten(Projektgruppe team) {
        //Referenz bei Team
        team.addStudent(currentUser);
        team.setAnzahlAktuell(team.getAnzahlAktuell() + 1);
        teamRepo.save(team);
        //Referenz bei Gruppe
        Uebungsgruppe gruppe = team.getUebungsgruppe();
        gruppe.addStudent(currentUser);
        gruppeRepo.save(gruppe);
        //Referenz bei Student
        currentUser.setProjektgruppe(team);
        currentUser.setUebungsgruppe(gruppe);
        studentRepo.save(currentUser);
        //Notification zum Beitritt
        beitretenNotification(team).open();
        //Aktualisieren des Grids/des aktuellen Nutzers
        updateTeamGrid();
        currentUser = aktuellerNutzer();
    }

    //Erstellt eine Notification, die den erfolgreichen Gruppenbeitritt meldet
    private Notification beitretenNotification(Projektgruppe team) {
        Notification not = new Notification();
        Label label = new Label(
                "Sie sind erfolgreich dem Team " + team.getName() + " beigetreten.");
        Button button = new Button("Schließen");
        button.addClickListener(e -> not.close());
        not.add(label, button);
        not.setDuration(3000);
        not.setPosition(Position.MIDDLE);
        return not;
    }
    
    //Erstellt einen Button zur Anzeige der Mitglieder einer Projektgruppe
    private Button mitgliederBtn(Grid<Projektgruppe> grid, Projektgruppe team) {
    	Button button = new Button("Mitglieder");
    	button.addClickListener(e -> {
    		memberDialog(team).open();
    	});
    	return button;
    }

    //Öffnet einen Dialog mit einem Grid, in dem alle Studenten der Projektgruppe angezeigt werden
    private Dialog memberDialog(Projektgruppe team) {
        Dialog dialog = new Dialog();
        //Grid
        Grid<Student> grid = new Grid<>(Student.class);
        grid.setColumns("email", "vorname", "nachname", "matrikelNr");
        try {
            grid.setItems(team.getStudentList());
        } catch (NullPointerException e) {
        }
        dialog.add(grid);
        //Schließen-Button
        Button but = new Button("Zurück", e -> dialog.close()); 
        //Layout
        VerticalLayout layout = new VerticalLayout(grid, but);
        dialog.add(layout);
        dialog.setHeight("600px");
        dialog.setWidth("800px");
        return dialog;
    }

    //Erstellt einen Button, bei Klick wird der Student/Nutzer aus dem Team ausgetragen
    private Button verlassenBtn(Grid<Projektgruppe> grid, Projektgruppe team) {
        Button button = new Button("Verlassen");
        //Nur aktiviert, wenn Team geöffnet, Nutzer in der aktuellen Projektgruppe
        try {
            button.setEnabled(team.getOffen() &&
                    currentUser.getProjektgruppe().getId().equals(team.getId()));
        } catch (NullPointerException e) {
            button.setEnabled(false);
        }

        button.addClickListener(e -> {
            verlassen(team);
            updateTeamGrid();
        });

        return button;
    }

    //Löscht den Nutzer aus der Projektgruppe und ändert die entsprechenden Referenzen
    private void verlassen(Projektgruppe team) {
        //Referenz bei Team
        team.removeStudentFromList(currentUser);
        team.setAnzahlAktuell(team.getAnzahlAktuell() - 1);
        teamRepo.save(team);
        //Referenz bei Gruppe
        Uebungsgruppe gruppe = team.getUebungsgruppe();
        gruppe.getStudentList().remove(currentUser);
        gruppeRepo.save(gruppe);
        //Referenz bei Student
        currentUser.setProjektgruppe(null);
        currentUser.setUebungsgruppe(null);
        studentRepo.save(currentUser);
        //Notification zum Austritt
        verlassenNotification(team).open();
        //Aktualisieren des Grids/des aktuellen Nutzers
        updateTeamGrid();
        currentUser = aktuellerNutzer();
    }

    //Erstellt eine Notification, die den Teamaustritt bestätigt
    private Notification verlassenNotification(Projektgruppe team) {
        Notification not = new Notification();
        Label label = new Label(
                "Sie sind erfolgreich aus dem Team " + team.getName() + " ausgetreten.");
        Button button = new Button("Schließen");
        button.addClickListener(e -> not.close());
        not.add(label, button);
        not.setDuration(3000);
        not.setPosition(Position.MIDDLE);
        return not;
    }
}

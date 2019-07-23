package de.uni.swt.spring.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import de.uni.swt.spring.backend.Verwaltung.Nutzerverwaltung;
import de.uni.swt.spring.backend.bohnen.TestBohne;
import de.uni.swt.spring.backend.data.entity.Student;
import de.uni.swt.spring.backend.data.entity.Studiengang;
import de.uni.swt.spring.backend.repositories.KonfigurationRepository;
import de.uni.swt.spring.backend.repositories.StudentRepository;
import de.uni.swt.spring.backend.repositories.StudiengangRepository;
import de.uni.swt.spring.ui.utils.MyDoubleToStringConverter;
import de.uni.swt.spring.ui.utils.MyStringToIntegerConverter;
import de.uni.swt.spring.ui.utils.SwtConst;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@HtmlImport("frontend://styles/shared-styles.html")
@Route(value = "newUser")
public class RegistrierungView extends VerticalLayout {

    private static final long serialVersionUID = 1L;
    private final StudentRepository studentRepo;
    private final StudiengangRepository studiengangRepo;
    private final String FIELD_WIDTH = "300px";
    private TextField email = new TextField("E-Mail-Adresse");
    private TextField vorname = new TextField("Vorname");
    private TextField nachname = new TextField("Nachname");
    private NumberField matrNr = new NumberField("Matrikelnummer");
    private ComboBox<Studiengang> studiengang = new ComboBox<>("Studiengang");
    private Button bestätigen = new Button("Registrieren");
    private Button zurück = new Button("Zurück zum Login");

    private Binder<Student> binderStudent = new Binder<>(Student.class);

    @Autowired
    private Nutzerverwaltung nv;

    public RegistrierungView(StudentRepository studentRepo, StudiengangRepository studiengangRepo,
                             KonfigurationRepository konfigRepo, @Autowired TestBohne bohne) {
        this.studentRepo = studentRepo;
        this.studiengangRepo = studiengangRepo;

        studiengang.setItems(this.studiengangRepo.findAll());
        studiengang.setItemLabelGenerator(Studiengang::getStudiengang);

        HorizontalLayout buttons = new HorizontalLayout();
        buttons.add(zurück, bestätigen);

        bestätigen.addClickListener(e -> {
            erstelleNeuenStudent();
        });
        zurück.addClickListener(e -> zurück.getUI().ifPresent(ui -> ui.navigate("login")));

        //Eingabevalidierung

        matrNr.setValueChangeMode(ValueChangeMode.EAGER);

        email.setWidth(FIELD_WIDTH);
        email.setPlaceholder("vorname.nachname@uni-rostock.de");
        email.setValueChangeMode(ValueChangeMode.EAGER);

        binderStudent.forField(email)
                .asRequired("Pflichtfeld")
                .withValidator(v -> v.matches(SwtConst.REGEX_EMAIL), SwtConst.REGEX_EMAIL_ERROR)
                .bind(Student::getEmail, Student::setEmail);

        vorname.setWidth(FIELD_WIDTH);
        vorname.setPlaceholder("Erik");
        vorname.setValueChangeMode(ValueChangeMode.EAGER);

        binderStudent.forField(vorname)
                .asRequired("Pflichtfeld")
                .withValidator(v -> v.matches(SwtConst.REGEX_NAME), SwtConst.REGEX_NAME_ERROR)
                .bind(Student::getVorname, Student::setVorname);

        nachname.setWidth(FIELD_WIDTH);
        nachname.setPlaceholder("Musterman");
        nachname.setValueChangeMode(ValueChangeMode.EAGER);

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


        setAlignItems(Alignment.CENTER);
        add(email, vorname, nachname, matrNr, studiengang, buttons);
    }

    public void erstelleNeuenStudent() {
        List<TextField> fields = new ArrayList<>();

        fields.add(email);
        fields.add(vorname);
        fields.add(nachname);

        //Alle Felder müssen befüllt sein
        if (email.isInvalid() || email.isEmpty()
                || vorname.isInvalid() || vorname.isEmpty()
                || nachname.isInvalid() || nachname.isEmpty()
                || matrNr.isInvalid() || matrNr.isEmpty()
                || studiengang.isEmpty()
        ) {

            for (TextField field : fields) {

                if (field.isEmpty() || field.isInvalid()) {
                    field.setInvalid(true);
                } else {
                    field.setInvalid(false);
                }
            }

            if (studiengang.isEmpty()) {
                studiengang.setInvalid(true);
            } else {
                studiengang.setInvalid(false);
            }

            if (matrNr.isEmpty() || matrNr.isInvalid()) {
                matrNr.setInvalid(true);
            } else {
                matrNr.setInvalid(false);
            }

            Notification.show("Bitte füllen die rot markierten Felder korrekt aus");

            //Wenn ein Student existiert muss dieser nur aktualisiert werden, ansonsten wird er neu angelegt
        } else {
            if (studentRepo.existsById(email.getValue())) {
                Notification.show("Student existiert bereits");
            } else {
                nv.erstelleStudent(email.getValue(),
                        vorname.getValue(),
                        nachname.getValue(),
                        matrNr.getValue().intValue(),
                        studiengang.getValue());
                Notification.show("Student hinzugefügt");
                //weiterleitung zum login
                bestätigen.getUI().ifPresent(ui -> ui.navigate("login"));
            }
        }
    }
}
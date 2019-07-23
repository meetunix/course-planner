package de.uni.swt.spring.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.router.Route;
import de.uni.swt.spring.app.security.MyNutzerPrincipal;
import de.uni.swt.spring.backend.Verwaltung.Nutzerverwaltung;
import de.uni.swt.spring.backend.data.entity.Admin;
import de.uni.swt.spring.backend.data.entity.Dozent;
import de.uni.swt.spring.backend.data.entity.Student;
import de.uni.swt.spring.backend.repositories.AdminRepository;
import de.uni.swt.spring.backend.repositories.DozentRepository;
import de.uni.swt.spring.backend.repositories.StudentRepository;
import de.uni.swt.spring.ui.utils.SwtConst;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@HtmlImport("frontend://styles/shared-styles.html")
@Route(value = SwtConst.PAGE_PASSWORT_RESET)
public class PasswortvergessenView extends VerticalLayout {

    private Nutzerverwaltung verwaltung;
    private StudentRepository srep;
    private DozentRepository drep;
    private AdminRepository arep;

    private EmailField emailField;
    private Button button;

    @Autowired
    public PasswortvergessenView(Nutzerverwaltung verwaltung, StudentRepository srep, DozentRepository drep, AdminRepository arep) {
        this.verwaltung = verwaltung;
        this.srep = srep;
        this.drep = drep;
        this.arep = arep;

        H3 passwordReset = new H3("Passwort Vergessen");
        emailField = new EmailField("Ihre @uni-rostock.de E-Mail");
        button = new Button("Neues Passwort senden");
        add(passwordReset, emailField, button);
        button.addClickListener(e -> resetPassword());
        HorizontalLayout hLayout = new HorizontalLayout();
        this.setAlignItems(Alignment.CENTER);
    }

    private void resetPassword() {
        String email = emailField.getValue();
        Student student = srep.findByEmail(email);
        if (student == null) {
            Dozent dozent = drep.findByEmail(email);
            if (dozent == null) {
                Admin admin = arep.findByEmail(email);
                if (admin == null) {
                    return;
                }
                verwaltung.changePasswortAdmin(admin);
                return;
            }
            verwaltung.changePasswortDozent(dozent);
            return;
        }
        verwaltung.changePasswortStudent(student);
    }
}

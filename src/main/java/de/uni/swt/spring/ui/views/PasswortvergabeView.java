package de.uni.swt.spring.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import de.uni.swt.spring.app.security.SecurityUtils;
import de.uni.swt.spring.backend.Verwaltung.Nutzerverwaltung;
import de.uni.swt.spring.backend.data.entity.Admin;
import de.uni.swt.spring.backend.data.entity.Dozent;
import de.uni.swt.spring.backend.data.entity.Student;
import de.uni.swt.spring.backend.repositories.AdminRepository;
import de.uni.swt.spring.backend.repositories.DozentRepository;
import de.uni.swt.spring.backend.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;

import static de.uni.swt.spring.ui.utils.SwtConst.*;

@HtmlImport("frontend://styles/shared-styles.html")
@Route(value = "passvergabe")
@Secured({"Student", "Dozent", "Admin"})
public class PasswortvergabeView extends VerticalLayoutSecured {

    @Autowired
    private StudentRepository srep;
    @Autowired
    private DozentRepository drep;
    @Autowired
    private AdminRepository arep;

    private PasswordField pwdField;
    private Button button;

    PasswortvergabeView() {
        pwdField = new PasswordField();
        button = new Button("Passwort ändern");

        button.addClickListener(e -> changePassword());
        add(pwdField, button);

    }

    private void changePassword() {
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String passwd = pwdField.getValue();

        if (role.equalsIgnoreCase("[Student]")) {
            Student student = srep.findByEmail(email);
            nv.changePasswortStudent(student, passwd);
            getUI().ifPresent(ui -> ui.navigate(PAGE_TEAMS_GRUPPEN));
        } else if (role.equalsIgnoreCase("[Dozent]")) {
            Dozent dozent = drep.findByEmail(email);
            nv.changePasswortDozent(dozent, passwd);
            getUI().ifPresent(ui -> ui.navigate(PAGE_GRUPPEN));
        } else if (role.equalsIgnoreCase("[Admin]")) {
            Admin admin = arep.findByEmail(email);
            nv.changePasswortAdmin(admin, passwd);
            getUI().ifPresent(ui -> ui.navigate(PAGE_ADMIN));
        }

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
    }
}

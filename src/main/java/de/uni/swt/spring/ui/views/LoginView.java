package de.uni.swt.spring.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.uni.swt.spring.ui.utils.SwtConst;

@Route(value = SwtConst.PAGE_LOGIN)
@PageTitle("SWT Planer 2019")

@HtmlImport("frontend://styles/shared-styles.html")
@Viewport(SwtConst.VIEWPORT)
public class LoginView extends VerticalLayout {
    public LoginView() {

        this.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        LoginForm login = new LoginForm();
        login.setAction("login");
        login.addForgotPasswordListener(e -> getUI().ifPresent(ui -> ui.navigate("passreset")));

        // Verdeutschen
        login.setI18n(loginDeutsch());
//        H1 title = new H1();
//        title.getStyle().set("color", "var(--lumo-base-color)");
//        Icon icon = VaadinIcon.VAADIN_H.create();
//        icon.setSize("30px");
//        icon.getStyle().set("top", "-4px");
//        title.add(icon);
//        title.add(new Text("SWT-Planer 2019"));
//        add(icon);
//
//        add(title);
        add(login);

        Button btnNewUser = new Button("Neuen Benutzer anlegen");
        btnNewUser.addClickListener(e ->
                btnNewUser.getUI().ifPresent(ui -> ui.navigate("newUser")));
        add(btnNewUser);
    }

    private LoginI18n loginDeutsch() {
        final LoginI18n l = LoginI18n.createDefault();

        l.setHeader(new LoginI18n.Header());
        l.getHeader().setTitle("Login");
//        l.getHeader().setDescription("Descrição do aplicativo");
        l.getForm().setUsername("E-Mail");
        l.getForm().setTitle("Login");
        l.getForm().setSubmit("Einloggen");
        l.getForm().setPassword("Passwort");
        l.getForm().setForgotPassword("Passwort vergessen");
        l.getErrorMessage().setTitle("Die E-Mail oder das Passwort sind nicht vorhanden");
//        l.getErrorMessage().setMessage("Confira seu usuário e senha e tente novamente.");
        return l;
    }
}

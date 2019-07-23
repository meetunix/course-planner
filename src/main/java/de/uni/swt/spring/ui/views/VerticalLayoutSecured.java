package de.uni.swt.spring.ui.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import de.uni.swt.spring.app.security.SecurityUtils;
import de.uni.swt.spring.backend.Verwaltung.Nutzerverwaltung;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import static de.uni.swt.spring.ui.utils.SwtConst.PAGE_LOGIN;
import static de.uni.swt.spring.ui.utils.SwtConst.PAGE_PASSWORT_VERGABE;

public class VerticalLayoutSecured extends VerticalLayout implements BeforeEnterObserver {

    @Autowired
    protected Nutzerverwaltung nv;

    VerticalLayoutSecured() {
        super();
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
        if(SecurityUtils.isUserLoggedIn()){
            if(!nv.getNutzer(SecurityContextHolder.getContext().getAuthentication().getName()).isPasswortChanged()){
                beforeEnterEvent.rerouteTo(PAGE_PASSWORT_VERGABE);
            }
        }
    }
}

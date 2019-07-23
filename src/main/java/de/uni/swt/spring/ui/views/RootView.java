package de.uni.swt.spring.ui.views;

import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.uni.swt.spring.ui.utils.SwtConst;

@Route(value = SwtConst.PAGE_ROOT)
@PageTitle("SWT Planer 2019")
@Viewport(SwtConst.VIEWPORT)
public class RootView extends LoginView {
    public RootView() {
        super();
    }
}

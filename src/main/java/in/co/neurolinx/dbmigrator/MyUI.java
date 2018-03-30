package in.co.neurolinx.dbmigrator;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import in.co.neurolinx.dbmigrator.migrators.LocalServer;

/**
 * This UI is the application entry point. A UI may either represent a browser
 * window (or tab) or some part of an HTML page where a Vaadin application is
 * embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is
 * intended to be overridden to add component to the user interface and
 * initialize non-component functionality.
 */
@Theme("mytheme")
public class MyUI extends UI {

	@Override
	protected void init(VaadinRequest vaadinRequest) {
		final VerticalLayout layout = new VerticalLayout();
		Label label = new Label("<b>Database Migrator</b>",ContentMode.HTML);
		final ComboBox<String> cbSelectType = new ComboBox<String>("Select Type");

		layout.addComponent(label);
		layout.addComponent(cbSelectType);

		setContent(layout);
		
		layout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
		layout.setComponentAlignment(cbSelectType, Alignment.MIDDLE_CENTER);

		cbSelectType.setItems("Local Server", "Remote Server", "Different DB");

		cbSelectType.addValueChangeListener(e -> {
			if (cbSelectType.getSelectedItem().get().equals("Local Server")) {
				Navigator navigator = new Navigator(getUI(), this);
				navigator.addView("Local", LocalServer.class);
				navigator.navigateTo("Local");
			}
		});
	}

	@WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
	public static class MyUIServlet extends VaadinServlet {
	}
}

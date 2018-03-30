package in.co.neurolinx.dbmigrator.migrators;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;

import in.co.neurolinx.dbmigrator.data.MigratorService;
import in.co.neurolinx.dbmigrator.data.entities.Database;
import in.co.neurolinx.dbmigrator.data.entities.Tables;

public class LocalServer extends VerticalLayout implements View, Migrator {
	private static final long serialVersionUID = 1L;
	MigratorService migratorService;
	HorizontalLayout hlTableFields;
	GridLayout glLocalServer;
	GridLayout glDestinationTableFields;
	GridLayout glLocalTableFields;
	// UI components
	Label lbMigrator;
	CheckBox chbSelectAll;
	ComboBox<Database> cbDestination;
	ComboBox<Database> cbLocal;
	ComboBox<Tables> cbDestinationTable;
	ComboBox<Tables> cbLocalTable;
	CheckBox[] chbDestinationTableFields;
	CheckBox[] chbLocalTableFields;
	Set<String> selectedDestinationTablesFields;
	Set<String> selectedLocalTablesFields;
	Button bMigrate;

	List<Database> databaseList;
	List<Tables> tablesList;
	List<Tables> localTableFieldsList;
	List<Tables> destinationTableFieldsList;
	boolean hasMigrated = false;
	int i;
	String destinationFields = new String();
	String localFields = new String();

	public LocalServer() {
		super();
		buildUI();
		setItems();
	}

	@Override
	public void initializeComponents() {
		hlTableFields = new HorizontalLayout();
		glLocalServer = new GridLayout(3, 3);
		lbMigrator = new Label("<b>Local Database Migrator</b>", ContentMode.HTML);
		cbDestination = new ComboBox<Database>("Destination Database");
		cbLocal = new ComboBox<Database>("Local Database");
		cbDestinationTable = new ComboBox<Tables>("Destination Table name");
		cbLocalTable = new ComboBox<Tables>("Local Table name");
		bMigrate = new Button("Migrate");
		chbSelectAll = new CheckBox("select all");
		selectedDestinationTablesFields = new HashSet<>();
		selectedLocalTablesFields = new HashSet<>();
	}

	@Override
	public void buildUI() {
		// TODO Auto-generated method stub
		initializeComponents();

		addComponent(lbMigrator);
		glLocalServer.setSpacing(true);
		glLocalServer.addComponent(cbDestination);
		glLocalServer.addComponent(cbLocal);
		glLocalServer.addComponent(cbDestinationTable);
		glLocalServer.addComponent(cbLocalTable);
		glLocalServer.addComponent(bMigrate);
		
		requestDBInfo();
		
		addComponent(glLocalServer);

		setComponentAlignment(lbMigrator, Alignment.MIDDLE_CENTER);
		setComponentAlignment(glLocalServer, Alignment.MIDDLE_CENTER);
	}

	/**
	 * This method responsible to capture any user input or action performed by user
	 * and perform specific operations respectively.
	 */
	@Override
	public void performOperations() {

		cbDestination
				.addValueChangeListener(e -> getTablesFromDestinationDatabase(cbDestination.getSelectedItem().get()));

		cbLocal.addValueChangeListener(e -> getTablesFromLocalDatabase(cbLocal.getSelectedItem().get()));

		cbDestinationTable.addValueChangeListener(e -> getTableFieldsFromDestinationDatabase(
				cbDestination.getSelectedItem().get(), cbDestinationTable.getSelectedItem().get()));

		cbLocalTable.addValueChangeListener(e -> getTableFieldsFromLocalDatabase(cbLocal.getSelectedItem().get(),
				cbLocalTable.getSelectedItem().get()));

		bMigrate.addClickListener(e -> migrate());

	}

	/**
	 * sets the call to performOperation method so no other class can call this from
	 * outside
	 */
	private void setItems() {
		performOperations();
	}

	/**
	 * clears all the fields after successful migration
	 */
	@SuppressWarnings("unused")
	private void clear() {
		cbDestination.clear();
		cbLocal.clear();
		cbDestinationTable.clear();
		cbLocalTable.clear();
	}

	/**
	 * Gets the instance of migrator service before drawing any UI components on the
	 * screen and then populates databaseList with the database names from the
	 * provided connection object in DBHandler
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		View.super.enter(event);
		migratorService = new MigratorService();

		databaseList = migratorService.databaseNames();
		if (!databaseList.isEmpty()) {
			cbDestination.setItems(databaseList);
			cbLocal.setItems(databaseList);
		}

	}

	@Override
	public void migrate() {
		Notification.show("Data migration initiated");

		try {
			hasMigrated = migratorService.migrateFromLocal(cbDestination.getSelectedItem().get(),
					cbDestinationTable.getSelectedItem().get(), cbLocal.getSelectedItem().get(),
					cbLocalTable.getSelectedItem().get(), selectedDestinationTablesFields);

			if (hasMigrated) {
				Notification.show("Data successfully migrated to " + cbDestinationTable.getValue(),
						Type.HUMANIZED_MESSAGE);
				// clear();
			} else {
				Notification.show("Data cannot migrated to " + cbDestinationTable.getValue(), Type.HUMANIZED_MESSAGE);
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	/**
	 * Get table names from destination database
	 */
	@Override
	public void getTablesFromDestinationDatabase(Database databaseName) {
		tablesList = migratorService.tablesNames(databaseName.toString());

		if (!tablesList.isEmpty()) {
			cbDestinationTable.setItems(tablesList);
		} else {
			Tables tables = new Tables();
			tables.setNames("There are no tables available");
			tablesList.add(tables);
			cbDestinationTable.setItems(tablesList);
		}
	}

	/**
	 * Get table names from source database;
	 */
	@Override
	public void getTablesFromLocalDatabase(Database databaseName) {
		tablesList = migratorService.tablesNames(databaseName.toString());

		if (!tablesList.isEmpty()) {
			cbLocalTable.setItems(tablesList);
		} else {
			Tables tables = new Tables();
			tables.setNames("There are no tables available");
			tablesList.add(tables);
			cbLocalTable.setItems(tablesList);
		}
	}

	/**
	 * This method will fetch all the fields in the given table and then construct
	 * the checkboxes to display on the screen
	 */
	@Override
	public void getTableFieldsFromDestinationDatabase(Database databaseName, Tables tableName) {
		destinationTableFieldsList = migratorService.tableFields(databaseName.toString(), tableName.toString());

		glDestinationTableFields = new GridLayout(2, destinationTableFieldsList.size());
		chbDestinationTableFields = new CheckBox[destinationTableFieldsList.size()];

		if (!destinationTableFieldsList.isEmpty()) {
			for (int i = 0; i < chbDestinationTableFields.length; i++) {
				chbDestinationTableFields[i] = new CheckBox();
				chbDestinationTableFields[i]
						.setCaption(destinationTableFieldsList.get(i).getFields().toString().toLowerCase());
			}
			glDestinationTableFields.setCaption("Fields in " + databaseName + "." + tableName);
			glDestinationTableFields.setSpacing(true);
			glDestinationTableFields.addComponents(chbDestinationTableFields);
			hlTableFields.addComponent(glDestinationTableFields);
			addComponent(chbSelectAll);
			addComponent(hlTableFields);
			setComponentAlignment(hlTableFields, Alignment.MIDDLE_CENTER);

			//for loop for selecting all values at once
			chbSelectAll.addValueChangeListener(e->{
				for (i = 0; i < chbDestinationTableFields.length; i++) {
					chbDestinationTableFields[i].setValue(true);
				}
			});
			
			// key forloop for storing selected fields
			for (i = 0; i < chbDestinationTableFields.length; i++) {
				chbDestinationTableFields[i].addValueChangeListener(e -> {

					/**
					 * if checkbos is checked add it to the selected field list otherwise remove it
					 */
					for (int j = 0; j < chbDestinationTableFields.length - 1; j++) {
						if (chbDestinationTableFields[j].getValue()) {

							// check the similar fields in another table automatically
							for (int k = 0; k < chbLocalTableFields.length; k++) {
								if (chbDestinationTableFields[j].getCaption()
										.equals(chbLocalTableFields[k].getCaption())) {
									chbLocalTableFields[k].setValue(true);
									selectedDestinationTablesFields
											.add(chbDestinationTableFields[j].getCaption().toString());
								}

							}

						} else {
							if (selectedDestinationTablesFields.contains(chbDestinationTableFields[j].getCaption())) {
								selectedDestinationTablesFields.remove(chbDestinationTableFields[j].getCaption());
							}
						}
					}

				});
			}
		}
	}

	@Override
	public void getTableFieldsFromLocalDatabase(Database databaseName, Tables tableName) {
		localTableFieldsList = migratorService.tableFields(databaseName.toString(), tableName.toString());

		glLocalTableFields = new GridLayout(2, localTableFieldsList.size());
		chbLocalTableFields = new CheckBox[localTableFieldsList.size()];

		if (!localTableFieldsList.isEmpty()) {
			for (int i = 0; i < chbLocalTableFields.length; i++) {
				chbLocalTableFields[i] = new CheckBox();
				chbLocalTableFields[i].setCaption(localTableFieldsList.get(i).getFields().toString().toLowerCase());
			}
			glLocalTableFields.setCaption("Fields in " + databaseName + "." + tableName);
			glLocalTableFields.setSpacing(true);
			glLocalTableFields.addComponents(chbLocalTableFields);
			hlTableFields.addComponent(glLocalTableFields);
			addComponent(chbSelectAll);
			addComponent(hlTableFields);
			setComponentAlignment(hlTableFields, Alignment.MIDDLE_CENTER);

			
			//for loop for selecting all values at once
			chbSelectAll.addValueChangeListener(e->{
				for (i = 0; i < chbLocalTableFields.length; i++) {
					chbLocalTableFields[i].setValue(true);
				}
			});
			
			// key forloop for storing selected fields
			for (int i = 0; i < chbLocalTableFields.length; i++) {
				chbLocalTableFields[i].addValueChangeListener(e -> {
					for (int j = 0; j < chbLocalTableFields.length - 1; j++) {
						if (chbLocalTableFields[j].getValue()) {
							System.out.println(chbLocalTableFields[j].getCaption() + " checked");
							selectedLocalTablesFields.add(chbLocalTableFields[j].getCaption().toString());
						} else {
							System.out.println(chbLocalTableFields[j].getCaption() + " unchecked");
							if (selectedLocalTablesFields.contains(chbLocalTableFields[j].getCaption())) {
								selectedLocalTablesFields.remove(chbLocalTableFields[j].getCaption());
							}
						}
					}
				});
			}

		}
	}

	@Override
	public void requestDBInfo() {
		// TODO Auto-generated method stub
		migratorService.DATABASE_URL = "jdbc:mysql://localhost:3306/";
		migratorService.DATABASE_USERNAME = "root";
		migratorService.DATABSE_PASSWORD = "access@1";
	}
}

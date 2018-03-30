package in.co.neurolinx.dbmigrator.migrators;

import in.co.neurolinx.dbmigrator.data.entities.Database;
import in.co.neurolinx.dbmigrator.data.entities.Tables;

public interface Migrator {
	void initializeComponents();

	void buildUI();

	void performOperations();

	void getTablesFromDestinationDatabase(Database databaseName);

	void getTablesFromLocalDatabase(Database databaseName);

	void getTableFieldsFromDestinationDatabase(Database databaseName, Tables tableName);

	void getTableFieldsFromLocalDatabase(Database databaseName, Tables tableName);

	void migrate();

	void requestDBInfo();
}

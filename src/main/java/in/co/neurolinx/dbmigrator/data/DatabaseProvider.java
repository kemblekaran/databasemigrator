package in.co.neurolinx.dbmigrator.data;

import java.util.List;

import in.co.neurolinx.dbmigrator.data.entities.Database;
import in.co.neurolinx.dbmigrator.data.entities.Tables;

public interface DatabaseProvider {
	List<Database> databaseNames();

	List<Tables> tablesNames(String databaseName);

	List<Tables> tableFields(String databaseName,String tableName);
}

package brunomasunaga.propointsnotes.database;

public class ScriptDLL {

    public static String createTables(){
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS REGISTRES (\n" +
                "        RegID    INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                "        Day      VARCHAR (10) NOT NULL,\n" +
                "        FoodID   INT     NOT NULL REFERENCES FOODS (FoodID),\n" +
                "        Quantity DECIMAL NOT NULL\n" +
                ");\n");

        sql.append("CREATE TABLE IF NOT EXISTS FOODS (\n" +
                "    FoodID          INTEGER       PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                "    DescriptionFood VARCHAR (100) NOT NULL,\n" +
                "    UnityFood       INT           NOT NULL,\n" +
                "    AmountUnity     DECIMAL       NOT NULL,\n" +
                "    PointsUnity     DECIMAL       NOT NULL\n" +
                ");\n");
        sql.append("CREATE TABLE SETTINGS (\n" +
                "    SettingsProfile INTEGER       PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                "    Quota           INT           NOT NULL,\n" +
                "    Name            VARCHAR (100) NOT NULL\n" +
                ");\n");
        return sql.toString();
    }
}

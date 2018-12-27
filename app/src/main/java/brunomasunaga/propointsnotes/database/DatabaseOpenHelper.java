package brunomasunaga.propointsnotes.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    public DatabaseOpenHelper(Context context) {
        super(context, "DADOS", null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE FOODS (FoodID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, DescriptionFood VARCHAR (0, 100) NOT NULL, UnityFood VARCHAR(0,100) NOT NULL, AmountUnity DECIMAL NOT NULL, PointsUnity INT NOT NULL)");
        db.execSQL("CREATE TABLE REGISTRES (RegID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, Day DATE NOT NULL, FoodID INT NOT NULL REFERENCES FOODS (FoodID), QuantityFood DECIMAL NOT NULL)");
        db.execSQL("CREATE TABLE SETTINGS (SettingsProfile INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, Name VARCHAR (100) NOT NULL, Age INT NOT NULL, Gender INT NOT NULL, Weight DECIMAL NOT NULL, Height INT NOT NULL, Quota INT NOT NULL, DateSaved TEXT)");
        db.execSQL("INSERT INTO SETTINGS(Name, Age, Gender, Weight, Height, Quota, DateSaved) VALUES (' ',0,0,0,0,0, null)");
        db.execSQL("INSERT INTO FOODS(DescriptionFood, UnityFood, AmountUnity, PointsUnity) VALUES ('Doritos','g', 25, 4)");
        db.execSQL("INSERT INTO FOODS(DescriptionFood, UnityFood, AmountUnity, PointsUnity) VALUES ('Suco de Laranja','ml', 200, 2)");
        db.execSQL("INSERT INTO REGISTRES(Day, FoodID, QuantityFood) VALUES ('27/12/2018', 1, 4)");
        db.execSQL("INSERT INTO REGISTRES(Day, FoodID, QuantityFood) VALUES ('27/12/2018', 2, 2.5)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS FOODS");
        db.execSQL("DROP TABLE IF EXISTS SETTINGS");
        db.execSQL("DROP TABLE IF EXISTS REGISTRES");
        onCreate(db);
    }
}

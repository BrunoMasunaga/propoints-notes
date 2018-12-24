package brunomasunaga.propointsnotes.dominio.repositorio;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import brunomasunaga.propointsnotes.dominio.entidades.Setting;

public class SettingsRepositorio {

    private SQLiteDatabase connection;
    public SettingsRepositorio(SQLiteDatabase connection){
        this.connection = connection;
    }

    public void alter(Setting profile){
        ContentValues contentValues = new ContentValues();
        contentValues.put("Name", profile.Name);
        contentValues.put("Age", profile.Age);
        contentValues.put("Gender", profile.Gender);
        contentValues.put("Weight", profile.Weight);
        contentValues.put("Height", profile.Height);
        contentValues.put("Quota", profile.Quota);
        contentValues.put("DateSaved", profile.DateSaved);
        connection.update("SETTINGS", contentValues, "SettingsProfile = 1", null);
    }

    public Setting findSettings(){
        Setting profile = new Setting();
        String query = "SELECT Name, Age, Gender, Weight, Height, Quota, DateSaved FROM SETTINGS WHERE SettingsProfile = 1";
        Cursor cursor = connection.rawQuery(query, null);
        cursor.moveToFirst();
        profile.Name = cursor.getString(0);
        profile.Age = cursor.getInt(1);
        profile.Gender = cursor.getInt(2);
        profile.Weight = cursor.getDouble(3);
        profile.Height = cursor.getInt(4);
        profile.Quota = cursor.getInt(5);
        profile.DateSaved = cursor.getString(6);
        cursor.close();
        return profile;
    }
}

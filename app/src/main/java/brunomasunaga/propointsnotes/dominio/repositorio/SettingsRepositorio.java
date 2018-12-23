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

    public void insert(Setting profile){
        ContentValues contentValues = new ContentValues();
        contentValues.put("Quota", profile.Quota);
        contentValues.put("Name", profile.Name);
        connection.insertOrThrow("SETTINGS", "", contentValues);
    }

    public void alter(Setting profile){
        ContentValues contentValues = new ContentValues();
        contentValues.put("Quota", String.valueOf(profile.Quota));
        contentValues.put("Name", profile.Name);
        connection.update("SETTINGS", contentValues, "SettingsProfile = 1", null);
    }

    public Setting findSettings(){
        Setting profile = new Setting();
        String query = "SELECT Name, Quota FROM SETTINGS WHERE SettingsProfile = 1";
        Cursor cursor = connection.rawQuery(query, null);
        if (cursor.moveToFirst()){
            profile.Name = cursor.getString(0);
            profile.Quota = cursor.getInt(1);
            cursor.close();
            return profile;
        }
        cursor.close();
        return null;
    }
}

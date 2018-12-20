package brunomasunaga.propointsnotes.dominio.repositorio;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import brunomasunaga.propointsnotes.dominio.entidades.Registre;

public class RegistreRepositorio {

    private SQLiteDatabase connection;

    public RegistreRepositorio(SQLiteDatabase connection){
        this.connection = connection;
    }

    public void insert(Registre registre){
        ContentValues contentValues = new ContentValues();
        contentValues.put("Day", String.valueOf(registre.Day));
        contentValues.put("FoodID", registre.FoodID);
        contentValues.put("Quantity", registre.Quantity);
        connection.insertOrThrow("REGISTRES", null, contentValues);
    }

    public void remove(int RegID){
        String[] parameters = new String[1];
        parameters[0] = String.valueOf(RegID);
        connection.delete("REGISTRES", "RegID = ?", parameters);
    }

    public void alter(Registre registre){
        ContentValues contentValues = new ContentValues();
        contentValues.put("Day", String.valueOf(registre.Day));
        contentValues.put("FoodID", registre.FoodID);
        contentValues.put("Quantity", registre.Quantity);
        String[] parameters = new String[1];
        parameters[0] = String.valueOf(registre.RegID);
        connection.update("REGISTRES", contentValues, "RegID = ?", parameters);
    }

    public List<Registre> findAll(){

        List<Registre> registres = new ArrayList<Registre>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT RegID, Day, FoodID, Quantity FROM REGISTRES");
        Cursor result = connection.rawQuery(sql.toString(), null);
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {
                Registre reg = new Registre();
                reg.RegID = result.getInt(result.getColumnIndexOrThrow("RegID"));
                reg.Day = result.getString(result.getColumnIndexOrThrow("Day"));
                reg.FoodID = result.getInt(result.getColumnIndexOrThrow("FoodID"));
                reg.Quantity = result.getFloat(result.getColumnIndexOrThrow("Quantity"));
                registres.add(reg);
            } while (result.moveToNext());
        }
        return registres;
    }

    public Registre findRegistre(int RegID){
        Registre registre = new Registre();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM CLIENTE WHERE RegID = ?");
        String[] parameters = new String[1];
        parameters[0] = String.valueOf(RegID);
        Cursor result = connection.rawQuery(sql.toString(), parameters);
        if (result.getCount() > 0) {
            result.moveToFirst();
            Registre reg = new Registre();
            reg.RegID = result.getInt(result.getColumnIndexOrThrow("RegID"));
            reg.Day = result.getString(result.getColumnIndexOrThrow("Day"));
            reg.FoodID = result.getInt(result.getColumnIndexOrThrow("FoodID"));
            reg.Quantity = result.getFloat(result.getColumnIndexOrThrow("Quantity"));
            return registre;
        }
        return null;
    }
}

package brunomasunaga.propointsnotes.dominio.repositorio;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
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
        contentValues.put("Hour", registre.Hour);
        contentValues.put("FoodID", registre.FoodID);
        contentValues.put("QuantityFood", registre.QuantityFood);
        connection.insertOrThrow("REGISTRES", null, contentValues);
    }

    public void removeAll(){
        connection.delete("REGISTRES", "RegID > 0", null);
    }

    public void remove(int RegID){
        String[] parameters = new String[1];
        parameters[0] = String.valueOf(RegID);
        connection.delete("REGISTRES", "RegID = ?", parameters);
    }

    public void alterByID(Registre registre){
        ContentValues contentValues = new ContentValues();
        contentValues.put("Day", String.valueOf(registre.Day));
        contentValues.put("FoodID", registre.FoodID);
        contentValues.put("QuantityFood", registre.QuantityFood);
        String[] parameters = new String[1];
        parameters[0] = String.valueOf(registre.RegID);
        connection.update("REGISTRES", contentValues, "RegID = ?", parameters);
    }

    public List<Registre> findAll(){
        List<Registre> registres = new ArrayList<>();
        String query = "SELECT * FROM REGISTRES";
        Cursor result = connection.rawQuery(query, null);
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {
                Registre reg = new Registre();
                reg.RegID = result.getInt(result.getColumnIndexOrThrow("RegID"));
                reg.Day = result.getString(result.getColumnIndexOrThrow("Day"));
                reg.Hour = result.getString(result.getColumnIndexOrThrow("Hour"));
                reg.QuantityFood = result.getDouble(result.getColumnIndexOrThrow("QuantityFood"));
                reg.FoodID = result.getInt(result.getColumnIndexOrThrow("FoodID"));
                registres.add(reg);
            } while (result.moveToNext());
        }
        return registres;
    }

    public List<Registre> findByDate(String day){
        List<Registre> registres = new ArrayList<>();
        String query = "SELECT * FROM REGISTRES NATURAL JOIN FOODS WHERE Day = '" + day+"'";
        Cursor result = connection.rawQuery(query, null);
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {
                Registre reg = new Registre();
                reg.RegID = result.getInt(result.getColumnIndexOrThrow("RegID"));
                reg.Day = result.getString(result.getColumnIndexOrThrow("Day"));
                reg.Hour = result.getString(result.getColumnIndexOrThrow("Hour"));
                reg.QuantityFood = result.getDouble(result.getColumnIndexOrThrow("QuantityFood"));
                reg.FoodID = result.getInt(result.getColumnIndexOrThrow("FoodID"));
                reg.AmountUnity = result.getInt(result.getColumnIndexOrThrow("AmountUnity"));
                reg.UnityFood = result.getString(result.getColumnIndexOrThrow("UnityFood"));
                reg.DescriptionFood = result.getString(result.getColumnIndexOrThrow("DescriptionFood"));
                reg.Carbs = result.getDouble(result.getColumnIndexOrThrow("Carbs"));
                reg.Prots = result.getDouble(result.getColumnIndexOrThrow("Prots"));
                reg.Fats = result.getDouble(result.getColumnIndexOrThrow("Fats"));
                reg.Fiber = result.getDouble(result.getColumnIndexOrThrow("Fiber"));
                reg.PointsUnity = result.getInt(result.getColumnIndexOrThrow("PointsUnity"));
                registres.add(reg);
            } while (result.moveToNext());
        }
        return registres;
    }

}

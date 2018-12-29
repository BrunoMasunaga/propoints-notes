package brunomasunaga.propointsnotes.dominio.repositorio;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import brunomasunaga.propointsnotes.ProPointsNotes;
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
        contentValues.put("Quantity", registre.QuantityFood);
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
        contentValues.put("Quantity", registre.QuantityFood);
        String[] parameters = new String[1];
        parameters[0] = String.valueOf(registre.RegID);
        connection.update("REGISTRES", contentValues, "RegID = ?", parameters);
    }

    public List<Registre> findAll(){
        List<Registre> registres = new ArrayList<>();
        String query = "SELECT * FROM REGISTRES NATURAL JOIN FOODS";
        Cursor result = connection.rawQuery(query, null);
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {
                Registre reg = new Registre();
                reg.RegID = result.getInt(result.getColumnIndexOrThrow("RegID"));
                reg.Day = result.getString(result.getColumnIndexOrThrow("Day"));
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

    public Registre findRegistre(int RegID){
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM REGISTRES WHERE RegID = ?");
        String[] parameters = new String[1];
        parameters[0] = String.valueOf(RegID);
        Cursor result = connection.rawQuery(sql.toString(), parameters);
        if (result.getCount() > 0) {
            result.moveToFirst();
            Registre reg = new Registre();
            reg.RegID = result.getInt(result.getColumnIndexOrThrow("RegID"));
            reg.Day = result.getString(result.getColumnIndexOrThrow("Day"));
            reg.FoodID = result.getInt(result.getColumnIndexOrThrow("FoodID"));
            reg.QuantityFood = result.getDouble(result.getColumnIndexOrThrow("QuantityFood"));
            return reg;
        }
        return null;
    }
}

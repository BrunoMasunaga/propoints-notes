package brunomasunaga.propointsnotes.dominio.repositorio;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import brunomasunaga.propointsnotes.dominio.entidades.Food;

public class FoodRepositorio {

    private SQLiteDatabase connection;
    public FoodRepositorio(SQLiteDatabase connection){
        this.connection = connection;
    }

    public void insert(Food food){
        ContentValues contentValues = new ContentValues();
        contentValues.put("DescriptionFood", String.valueOf(food.DescriptionFood));
        contentValues.put("UnityFood", String.valueOf(food.UnityFood));
        contentValues.put("AmountUnity", food.AmountUnity);
        contentValues.put("PointsUnity", food.PointsUnity);
        connection.insertOrThrow("FOODS", null, contentValues);
    }

    public void remove(int FoodID){
        String[] parameters = new String[1];
        parameters[0] = String.valueOf(FoodID);
        connection.delete("FOODS", "FoodID = ?", parameters);
    }

    public void alter(Food food){
        ContentValues contentValues = new ContentValues();
        contentValues.put("DescriptionFood", String.valueOf(food.DescriptionFood));
        contentValues.put("UnityFood", String.valueOf(food.UnityFood));
        contentValues.put("AmountUnity", food.AmountUnity);
        contentValues.put("PointsUnity", food.PointsUnity);
        String[] parameters = new String[1];
        parameters[0] = String.valueOf(food.FoodID);
        connection.update("FOODS", contentValues, "FoodID = ?", parameters);
    }

    public List<Food> findAll(){
        List<Food> foods = new ArrayList<Food>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT FoodID, DescriptionFood, UnityFood, AmountUnity, PointsUnity FROM FOODS");
        Cursor result = connection.rawQuery(sql.toString(), null);
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {
                Food food = new Food();
                food.FoodID = result.getInt(result.getColumnIndexOrThrow("FoodID"));
                food.AmountUnity = result.getFloat(result.getColumnIndexOrThrow("AmountUnity"));
                food.DescriptionFood = result.getString(result.getColumnIndexOrThrow("DescriptionFood"));
                food.UnityFood = result.getString(result.getColumnIndexOrThrow("UnityFood"));
                food.PointsUnity = result.getInt(result.getColumnIndexOrThrow("PointsUnity"));
                foods.add(food);
            } while (result.moveToNext());
        }
        return foods;
    }

    public Food findFood(int FoodID){
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM FOODS WHERE FoodID = ?");
        String[] parameters = new String[1];
        parameters[0] = String.valueOf(FoodID);
        Cursor result = connection.rawQuery(sql.toString(), parameters);
        if (result.getCount() > 0) {
            result.moveToFirst();
            Food food = new Food();
            food.FoodID = result.getInt(result.getColumnIndexOrThrow("FoodID"));
            food.AmountUnity = result.getFloat(result.getColumnIndexOrThrow("AmountUnity"));
            food.DescriptionFood = result.getString(result.getColumnIndexOrThrow("DescriptionFood"));
            food.UnityFood = result.getString(result.getColumnIndexOrThrow("UnityFood"));
            food.PointsUnity = result.getInt(result.getColumnIndexOrThrow("PointsUnity"));
            return food;
        }
        return null;
    }
}

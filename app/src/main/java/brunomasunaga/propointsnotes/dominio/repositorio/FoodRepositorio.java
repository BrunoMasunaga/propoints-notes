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
        contentValues.put("Carbs", food.Carbs);
        contentValues.put("Prots", food.Prots);
        contentValues.put("Fats", food.Fats);
        contentValues.put("Fiber", food.Fiber);
        contentValues.put("PointsUnity", food.PointsUnity);
        connection.insertOrThrow("FOODS", null, contentValues);
    }

    public void removeByName(String name){
        String[] parameters = new String[1];
        parameters[0] = name;
        connection.delete("FOODS", "FoodID = ?", parameters);
    }

    public void alterByName(String name, Food newFood){
        ContentValues contentValues = new ContentValues();
        contentValues.put("DescriptionFood", String.valueOf(newFood.DescriptionFood));
        contentValues.put("UnityFood", String.valueOf(newFood.UnityFood));
        contentValues.put("AmountUnity", newFood.AmountUnity);
        contentValues.put("Carbs", newFood.Carbs);
        contentValues.put("Prots", newFood.Prots);
        contentValues.put("Fats", newFood.Fats);
        contentValues.put("Fiber", newFood.Fiber);
        contentValues.put("PointsUnity", newFood.PointsUnity);
        String[] parameters = new String[1];
        parameters[0] = name;
        connection.update("FOODS", contentValues, "DescriptionFood = ?", parameters);
    }

    public List<Food> findAll(){
        List<Food> foods = new ArrayList<Food>();
        String query = "SELECT * FROM FOODS ORDER BY DescriptionFood";
        Cursor result = connection.rawQuery(query, null);
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {
                Food food = new Food();
                food.FoodID = result.getInt(result.getColumnIndexOrThrow("FoodID"));
                food.AmountUnity = result.getDouble(result.getColumnIndexOrThrow("AmountUnity"));
                food.DescriptionFood = result.getString(result.getColumnIndexOrThrow("DescriptionFood"));
                food.UnityFood = result.getString(result.getColumnIndexOrThrow("UnityFood"));
                food.Carbs = result.getDouble(result.getColumnIndexOrThrow("Carbs"));
                food.Prots = result.getDouble(result.getColumnIndexOrThrow("Prots"));
                food.Fats = result.getDouble(result.getColumnIndexOrThrow("Fats"));
                food.Fiber = result.getDouble(result.getColumnIndexOrThrow("Fiber"));
                food.PointsUnity = result.getInt(result.getColumnIndexOrThrow("PointsUnity"));
                foods.add(food);
            } while (result.moveToNext());
        }
        return foods;
    }

    public Food findFoodByName(String name){
        Food searched = new Food();
        String query = "SELECT * FROM FOODS WHERE DescriptionFood = '" + name+"'";
        Cursor result = connection.rawQuery(query, null);
        if (result.getCount() > 0) {
            result.moveToFirst();
            searched.FoodID = result.getInt(result.getColumnIndexOrThrow("FoodID"));
            searched.AmountUnity = result.getDouble(result.getColumnIndexOrThrow("AmountUnity"));
            searched.DescriptionFood = result.getString(result.getColumnIndexOrThrow("DescriptionFood"));
            searched.UnityFood = result.getString(result.getColumnIndexOrThrow("UnityFood"));
            searched.Carbs = result.getDouble(result.getColumnIndexOrThrow("Carbs"));
            searched.Prots = result.getDouble(result.getColumnIndexOrThrow("Prots"));
            searched.Fats = result.getDouble(result.getColumnIndexOrThrow("Fats"));
            searched.Fiber = result.getDouble(result.getColumnIndexOrThrow("Fiber"));
            searched.PointsUnity = result.getInt(result.getColumnIndexOrThrow("PointsUnity"));
            return searched;
        }
        return null;
    }

}

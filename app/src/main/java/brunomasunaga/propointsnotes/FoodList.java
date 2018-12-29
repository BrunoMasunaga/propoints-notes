package brunomasunaga.propointsnotes;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.List;

import brunomasunaga.propointsnotes.database.DatabaseOpenHelper;
import brunomasunaga.propointsnotes.dominio.entidades.Food;
import brunomasunaga.propointsnotes.dominio.entidades.Registre;
import brunomasunaga.propointsnotes.dominio.repositorio.FoodRepositorio;
import brunomasunaga.propointsnotes.dominio.repositorio.RegistreRepositorio;

public class FoodList extends AppCompatActivity {
    private SQLiteDatabase connection;
    private DatabaseOpenHelper databaseOpenHelper;
    private FoodRepositorio foodRepositorio;

    private RecyclerView foodList;
    private FoodAdapter foodAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        createConnection();

        foodList = findViewById(R.id.foodList);
        foodList.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        foodList.setLayoutManager(linearLayoutManager);
        foodRepositorio = new FoodRepositorio(connection);
        List<Food> foods = foodRepositorio.findAll();
        foodAdapter = new FoodAdapter(foods);
        foodList.setAdapter(foodAdapter);

        FloatingActionButton addFood = findViewById(R.id.addFood);
        addFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent actAddFood = new Intent(FoodList.this, AddFood.class);
                startActivity(actAddFood);
                finish();
            }
        });

    }

    private void createConnection(){
        databaseOpenHelper = new DatabaseOpenHelper(this);
        connection = databaseOpenHelper.getWritableDatabase();
    }


}

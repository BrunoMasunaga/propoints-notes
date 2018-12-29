package brunomasunaga.propointsnotes;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import brunomasunaga.propointsnotes.database.DatabaseOpenHelper;
import brunomasunaga.propointsnotes.dominio.entidades.Food;
import brunomasunaga.propointsnotes.dominio.repositorio.FoodRepositorio;

public class AddFood extends AppCompatActivity {
    private SQLiteDatabase connection;
    private DatabaseOpenHelper databaseOpenHelper;
    private FoodRepositorio foodRepositorio;
    private Boolean allowOver;
    private AlertDialog conflict;

    private EditText foodName;
    private EditText qtUnity;
    private EditText tpUnity;
    private EditText carbs;
    private EditText prots;
    private EditText fats;
    private EditText fiber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        createConnection();
        foodRepositorio = new FoodRepositorio(connection);

        foodName = findViewById(R.id.foodName);
        qtUnity = findViewById(R.id.qtUnity);
        tpUnity = findViewById(R.id.tpUnity);
        carbs = findViewById(R.id.carbs);
        prots = findViewById(R.id.prots);
        fats = findViewById(R.id.fats);
        fiber = findViewById(R.id.fiber);

        FloatingActionButton confirmAdd = findViewById(R.id.confirmAdd);
        confirmAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(insertFood(foodName.getText().toString().toUpperCase())) {
                    Intent actFoodList = new Intent(AddFood.this, FoodList.class);
                    startActivity(actFoodList);
                    finish();
                }
            }
        });
    }

    public boolean insertFood(String name){
        boolean alreadyExist = (foodRepositorio.findFoodByName(name) != null);
        allowOver = false;
        if(alreadyExist){
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message mesg) {
                    throw new RuntimeException();
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Alimento já registrado");
            builder.setMessage("O nome do alimento digitado já está no banco de dados.\nDeseja sobrescrever as informações?");
            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    allowOver = true;
                    handler.sendMessage(handler.obtainMessage());
                }
            });
            builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    handler.sendMessage(handler.obtainMessage());
                }
            });
            conflict = builder.create();
            conflict.show();
            try { Looper.loop(); }catch(RuntimeException e) {}
            if (allowOver == false) return false;
        }
        Food food = new Food();
        food.DescriptionFood = name;
        food.AmountUnity = Double.parseDouble(qtUnity.getText().toString());
        food.UnityFood = tpUnity.getText().toString().toLowerCase();
        food.Carbs = Double.parseDouble(carbs.getText().toString());
        food.Prots = Double.parseDouble(prots.getText().toString());
        food.Fats = Double.parseDouble(fats.getText().toString());
        food.Fiber = Double.parseDouble(fiber.getText().toString());
        food.PointsUnity = Food.calculatePoints(food.Carbs, food.Prots, food.Fats, food.Fiber, 1);
        if(alreadyExist) foodRepositorio.alterByName(name, food);
        else foodRepositorio.insert(food);
        return true;
    }

    private void createConnection(){
        databaseOpenHelper = new DatabaseOpenHelper(this);
        connection = databaseOpenHelper.getWritableDatabase();
    }


}

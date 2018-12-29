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
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import brunomasunaga.propointsnotes.database.DatabaseOpenHelper;
import brunomasunaga.propointsnotes.dominio.entidades.Food;
import brunomasunaga.propointsnotes.dominio.repositorio.FoodRepositorio;

public class AddRegistry extends AppCompatActivity {
    private SQLiteDatabase connection;
    private DatabaseOpenHelper databaseOpenHelper;

    private TextView date;
    private TextView points;
    private TextView unity;
    private EditText foodName;
    private Calendar dataAtual;
    private SimpleDateFormat formato;
    private ImageButton searchFood;
    private FoodRepositorio foodRepositorio;
    private boolean createFood;
    private AlertDialog conflict;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_registry);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        createConnection();
        foodRepositorio = new FoodRepositorio(connection);

        date = findViewById(R.id.date);
        dataAtual = new GregorianCalendar();
        formato = new SimpleDateFormat("dd/MM/yyyy");
        searchFood = findViewById(R.id.bt_search);
        foodName = findViewById(R.id.foodName);
        points = findViewById(R.id.points);
        unity = findViewById(R.id.unity);

        FloatingActionButton confirmRegistry = findViewById(R.id.confirmRegistry);
        confirmRegistry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getDate();
    }

    private void createConnection(){
        databaseOpenHelper = new DatabaseOpenHelper(this);
        connection = databaseOpenHelper.getWritableDatabase();
    }

    private void getDate(){
        formato.setCalendar(dataAtual);
        date.setText(formato.format(dataAtual.getTime()));
    }

    public void search(View view){
        createFood = false;
        String name = foodName.getText().toString().toUpperCase();
        Food food = foodRepositorio.findFoodByName(name);
        if(food == null){
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message mesg) {
                    throw new RuntimeException();
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Alimento não existe");
            builder.setMessage("O nome do alimento digitado não está no banco de dados.\nDeseja adicioná-lo?");
            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    createFood = true;
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
            if (createFood == false) return;
            else{
                Intent actAddFood = new Intent(AddRegistry.this, AddFood.class);
                startActivity(actAddFood);
                finish();
            }
        }
        else {
            foodName.setText(food.DescriptionFood);
            points.setText(String.valueOf(food.PointsUnity));
            unity.setText(String.valueOf(food.AmountUnity) + food.UnityFood);
        }
    }
}

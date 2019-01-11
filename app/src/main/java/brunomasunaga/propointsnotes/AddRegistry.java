package brunomasunaga.propointsnotes;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
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
import brunomasunaga.propointsnotes.dominio.entidades.Registre;
import brunomasunaga.propointsnotes.dominio.repositorio.FoodRepositorio;
import brunomasunaga.propointsnotes.dominio.repositorio.RegistreRepositorio;

public class AddRegistry extends AppCompatActivity {
    private SQLiteDatabase connection;
    private DatabaseOpenHelper databaseOpenHelper;

    private TextView date;
    private TextView points;
    private TextView unity;
    private EditText foodName;
    private Calendar dataAtual;
    private EditText quantity;
    private SimpleDateFormat formato;
    private ImageButton searchFood;
    private FoodRepositorio foodRepositorio;
    private RegistreRepositorio registreRepositorio;
    private Food food;
    private Registre registre;
    private boolean createFood;
    private AlertDialog conflict;
    private boolean cameFromList;
    private Registre reg;
    private ConstraintLayout coordinator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_registry);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        createConnection();
        foodRepositorio = new FoodRepositorio(connection);
        registreRepositorio = new RegistreRepositorio(connection);

        coordinator = findViewById(R.id.coordinator);
        cameFromList = false;
        date = findViewById(R.id.date);
        dataAtual = new GregorianCalendar();
        formato = new SimpleDateFormat("dd/MM/yyyy");
        searchFood = findViewById(R.id.bt_search);
        foodName = findViewById(R.id.foodName);
        points = findViewById(R.id.points);
        unity = findViewById(R.id.unity);
        quantity = findViewById(R.id.quantity);

        FloatingActionButton confirmRegistry = findViewById(R.id.confirmRegistry);
        confirmRegistry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isValidated()){
                    Snackbar snackbar = Snackbar.make(coordinator, "Preencha as informações.", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
                else insert();
            }
        });
        loadRegistry();
    }

    private void loadRegistry(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("REGISTRE")){
            reg = (Registre) bundle.getSerializable("REGISTRE");
            foodName.setText(reg.DescriptionFood);
            points.setText(String.valueOf(reg.PointsUnity));
            if(Calculator.verifyInteger(reg.AmountUnity)){
                int q = (int) reg.AmountUnity;
                unity.setText(String.valueOf("("+q) + " " + reg.UnityFood+")");
            }
            else unity.setText(String.valueOf(reg.AmountUnity) + reg.UnityFood);
            quantity.setText(String.valueOf(reg.QuantityFood));
            food = foodRepositorio.findFoodByName(reg.DescriptionFood);
            date.setText(reg.Day);
            cameFromList = true;
            return;
        }
        if (bundle != null && bundle.containsKey("DATE")) {
            String dateString = (String) bundle.getSerializable("DATE");
            date.setText(dateString);
        }
    }

    private void createConnection(){
        databaseOpenHelper = new DatabaseOpenHelper(this);
        connection = databaseOpenHelper.getWritableDatabase();
    }

    public void search(View view){
        if (!isValidatedSearch()){
            Snackbar snackbar = Snackbar.make(coordinator, "Insira o nome do alimento.", Snackbar.LENGTH_SHORT);
            snackbar.show();
            return;
        }
        createFood = false;
        String name = foodName.getText().toString().toLowerCase();
        name = name.substring(0,1).toUpperCase() + name.substring(1);
        food = foodRepositorio.findFoodByName(name);
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
                actAddFood.putExtra("NAMEFOOD", name);
                (AddRegistry.this).startActivityForResult(actAddFood, 1);
            }
        }
        else {
            foodName.setText(food.DescriptionFood);
            points.setText(String.valueOf(food.PointsUnity));
            if(Calculator.verifyInteger(food.AmountUnity)){
                int q = (int) food.AmountUnity;
                unity.setText(String.valueOf("("+q) + " " + food.UnityFood+")");
            }
            else unity.setText(String.valueOf(food.AmountUnity) + food.UnityFood);
        }
    }

    private void insert(){
        registre = new Registre();
        registre.Day = date.getText().toString();
        registre.QuantityFood = Double.parseDouble(quantity.getText().toString());
        registre.FoodID = food.FoodID;
        if (!cameFromList){
            registreRepositorio.insert(registre);
        }
        else {
            registre.RegID = reg.RegID;
            registreRepositorio.alterByID(registre);
        }
        finish();
    }

    private boolean isValidated(){
        if (foodName.getText().toString().matches("")) return false;
        if (quantity.getText().toString().matches("")) return false;
        if (points.getText().toString().matches("Sem valor")) return false;
        return true;
    }

    private boolean isValidatedSearch(){
        if (foodName.getText().toString().matches("")) return false;
        return true;
    }

}

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
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import brunomasunaga.propointsnotes.database.DatabaseOpenHelper;
import brunomasunaga.propointsnotes.dominio.entidades.Food;
import brunomasunaga.propointsnotes.dominio.repositorio.FoodRepositorio;

public class AddFood extends AppCompatActivity {
    private SQLiteDatabase connection;
    private DatabaseOpenHelper databaseOpenHelper;
    private FoodRepositorio foodRepositorio;
    private Boolean allowOver;
    private AlertDialog conflict;
    private boolean noInfo;
    private boolean cameFromRegistre;

    private EditText foodName;
    private EditText qtUnity;
    private EditText tpUnity;
    private EditText carbs;
    private EditText prots;
    private EditText fats;
    private EditText fiber;
    private EditText points;
    private CheckBox noInformation;
    private ConstraintLayout coordinator;

    private Food foodCameFromList;
    private boolean cameFromList;

    private CardView cardCarbs;
    private CardView cardProts;
    private CardView cardFats;
    private CardView cardFiber;
    private CardView cardPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        createConnection();
        foodRepositorio = new FoodRepositorio(connection);

        coordinator = findViewById(R.id.coordinator);
        noInfo = false;
        cameFromRegistre = false;
        cameFromList = false;
        cardCarbs = findViewById(R.id.cardCarbs);
        cardProts = findViewById(R.id.cardProteins);
        cardFats = findViewById(R.id.cardFats);
        cardFiber = findViewById(R.id.cardFiber);
        cardPoints = findViewById(R.id.cardPoints);
        cardPoints.setVisibility(View.GONE);
        foodName = findViewById(R.id.foodName);
        qtUnity = findViewById(R.id.qtUnity);
        tpUnity = findViewById(R.id.tpUnity);
        carbs = findViewById(R.id.carbs);
        prots = findViewById(R.id.prots);
        fats = findViewById(R.id.fats);
        fiber = findViewById(R.id.fiber);
        points = findViewById(R.id.points);

        noInformation = findViewById(R.id.noInformation);
        noInformation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if(isChecked) {
                    noInfo = true;
                    cardCarbs.setVisibility(View.GONE);
                    cardProts.setVisibility(View.GONE);
                    cardFats.setVisibility(View.GONE);
                    cardFiber.setVisibility(View.GONE);
                    cardPoints.setVisibility(View.VISIBLE);
                }
                else{
                    noInfo = false;
                    cardCarbs.setVisibility(View.VISIBLE);
                    cardProts.setVisibility(View.VISIBLE);
                    cardFats.setVisibility(View.VISIBLE);
                    cardFiber.setVisibility(View.VISIBLE);
                    cardPoints.setVisibility(View.GONE);
                }
            }
        });

        loadFood();
        FloatingActionButton confirmAdd = findViewById(R.id.confirmAdd);
        confirmAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isValidated()){
                    Snackbar snackbar = Snackbar.make(coordinator, "Preencha as informações.", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
                else {
                    String name = foodName.getText().toString().toLowerCase();
                    name = name.substring(0, 1).toUpperCase() + name.substring(1);
                    if (insertFood(name)) {
                        if (!cameFromRegistre) {
                            Intent actFoodList = new Intent(AddFood.this, FoodList.class);
                            startActivity(actFoodList);
                        }
                        finish();
                    }
                }
            }
        });
    }

    private void loadFood(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("FOOD")){
            setTitle("Modificar alimento");
            foodCameFromList = (Food) bundle.getSerializable("FOOD");
            foodName.setText(foodCameFromList.DescriptionFood);
            qtUnity.setText(Calculator.integerIfPossible(foodCameFromList.AmountUnity));
            tpUnity.setText(foodCameFromList.UnityFood);
            if(foodCameFromList.Carbs == -1){
                points.setText(String.valueOf(foodCameFromList.PointsUnity));
                noInformation.setChecked(true);
            }
            else {
                carbs.setText(Calculator.integerIfPossible(foodCameFromList.Carbs));
                prots.setText(Calculator.integerIfPossible(foodCameFromList.Prots));
                fats.setText(Calculator.integerIfPossible(foodCameFromList.Fats));
                fiber.setText(Calculator.integerIfPossible(foodCameFromList.Fiber));
                noInformation.setChecked(false);
            }
            cameFromList = true;
            return;
        }
        if (bundle != null && bundle.containsKey("FCALC")){
            foodCameFromList = (Food) bundle.getSerializable("FCALC");
            qtUnity.setText((Calculator.integerIfPossible(foodCameFromList.AmountUnity)));
            tpUnity.setText(foodCameFromList.UnityFood);
            carbs.setText(Calculator.integerIfPossible(foodCameFromList.Carbs));
            prots.setText(Calculator.integerIfPossible(foodCameFromList.Prots));
            fats.setText(Calculator.integerIfPossible(foodCameFromList.Fats));
            fiber.setText(Calculator.integerIfPossible(foodCameFromList.Fiber));
            noInformation.setChecked(false);
            cameFromList = true;
            return;
        }
        if (bundle != null && bundle.containsKey("NAMEFOOD")){
            String name = (String) bundle.getSerializable("NAMEFOOD");
            foodName.setText(name);
            cameFromRegistre = true;
        }
    }

    public boolean insertFood(String name){
        Food searched = foodRepositorio.findFoodByName(name);
        boolean alreadyExist = (searched != null);
        allowOver = false;
        if(alreadyExist && cameFromList == false){
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
        if(noInfo == false) {
            food.Carbs = Double.parseDouble(carbs.getText().toString());
            food.Prots = Double.parseDouble(prots.getText().toString());
            food.Fats = Double.parseDouble(fats.getText().toString());
            food.Fiber = Double.parseDouble(fiber.getText().toString());
            food.PointsUnity = Food.calculatePointsInfo(food.Carbs, food.Prots, food.Fats, food.Fiber, 1);
        }
        else{
            food.Carbs = -1;
            food.Prots = -1;
            food.Fats = -1;
            food.Fiber = -1;
            food.PointsUnity = Integer.parseInt(points.getText().toString());
        }
        if(cameFromList){
            food.FoodID = foodCameFromList.FoodID;
            if (alreadyExist && food.FoodID != searched.FoodID){
                Snackbar snackbar = Snackbar.make(coordinator, "O nome inserido já existe no banco.", Snackbar.LENGTH_SHORT);
                snackbar.show();
                return false;
            }
            foodRepositorio.alter(food);
        }
        else if(alreadyExist){
            food.FoodID = searched.FoodID;
            foodRepositorio.alter(food);
        }
        else foodRepositorio.insert(food);
        return true;
    }

    private void createConnection(){
        databaseOpenHelper = new DatabaseOpenHelper(this);
        connection = databaseOpenHelper.getWritableDatabase();
    }

    private boolean isValidated(){
        if (foodName.getText().toString().matches("")) return false;
        if (qtUnity.getText().toString().matches("")) return false;
        if (tpUnity.getText().toString().matches("")) return false;
        if (noInformation.isChecked()){
            if (points.getText().toString().matches("")) return false;
            return true;
        }
        if (carbs.getText().toString().matches("")) return false;
        if (prots.getText().toString().matches("")) return false;
        if (fats.getText().toString().matches(""))return false;
        if (fiber.getText().toString().matches("")) return false;
        return true;
    }


}

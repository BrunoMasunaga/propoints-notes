package brunomasunaga.propointsnotes;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
    private LinearLayoutManager linearLayoutManager;

    private RecyclerView foodList;
    private FoodAdapter foodAdapter;
    private boolean importAutorized;
    private AlertDialog decision;
    private StringBuilder list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        createConnection();

        foodList = findViewById(R.id.foodList);
        foodList.setHasFixedSize(true);

        linearLayoutManager = new LinearLayoutManager(this);

        FloatingActionButton addFood = findViewById(R.id.addFood);
        addFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent actAddFood = new Intent(FoodList.this, AddFood.class);
                startActivity(actAddFood);
                finish();
            }
        });
        reloadList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manu_main_foodlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.uploadFoods) {
            if(uploadSuccess()){
                dialogInsertion();
            }
            else{
                Toast toast = Toast.makeText(this, "Erro. O arquivo foodList.txt não foi encontrado no armazenamento.", Toast.LENGTH_LONG);
                toast.show();
            }
        }
        return true;
    }

    public void reloadList(){
        foodList.setLayoutManager(linearLayoutManager);
        foodRepositorio = new FoodRepositorio(connection);
        List<Food> foods = foodRepositorio.findAll();
        foodAdapter = new FoodAdapter(foods, this);
        foodList.setAdapter(foodAdapter);
    }

    private void createConnection(){
        databaseOpenHelper = new DatabaseOpenHelper(this);
        connection = databaseOpenHelper.getWritableDatabase();
    }

    private boolean uploadSuccess(){
        String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
        String path = sdcard + File.separator + "foodList.txt";
        list = new StringBuilder();
        try {
            BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(path), "utf8"), 8192);
            String line;
            while ((line = buffer.readLine()) != null) {
                list.append(line);
                list.append('\n');
            }
            buffer.close();
        }
        catch (IOException e) {
            return false;}
        return true;
    }

    private void dialogInsertion(){
        importAutorized = false;
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException();
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Atenção");
        builder.setMessage("Alimentos já existentes no banco de dados não serão modificados nem inseridos novamente.");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                importAutorized = true;
                handler.sendMessage(handler.obtainMessage());
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                handler.sendMessage(handler.obtainMessage());
            }
        });
        decision = builder.create();
        decision.show();
        try { Looper.loop(); }catch(RuntimeException e) {}
        if (importAutorized == false) return;
        else{
            makeInsertion();
        }
    }

    private void makeInsertion(){
        String newList = list.toString();
        String[] foods = newList.split("\n");
        for (String line : foods){
            String[] data = line.split(",");
            if (foodRepositorio.findFoodByName(data[0]) != null) continue;
            Food newFood = new Food();
            newFood.DescriptionFood = data[0];
            newFood.UnityFood = data[1];
            newFood.AmountUnity = Double.parseDouble(data[2]);
            newFood.Carbs = Double.parseDouble(data[3]);
            newFood.Prots = Double.parseDouble(data[4]);
            newFood.Fats = Double.parseDouble(data[5]);
            newFood.Fiber = Double.parseDouble(data[6]);
            newFood.PointsUnity = Integer.parseInt(data[7]);
            if(newFood.PointsUnity == -1){
                newFood.PointsUnity = Food.calculatePointsInfo(newFood.Carbs, newFood.Prots, newFood.Fats, newFood.Fiber, 1);
            }
            foodRepositorio.insert(newFood);
        }
        reloadList();
    }

}

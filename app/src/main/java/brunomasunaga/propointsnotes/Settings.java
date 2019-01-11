package brunomasunaga.propointsnotes;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.Math;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import brunomasunaga.propointsnotes.database.DatabaseOpenHelper;
import brunomasunaga.propointsnotes.dominio.entidades.Food;
import brunomasunaga.propointsnotes.dominio.entidades.Registre;
import brunomasunaga.propointsnotes.dominio.entidades.Setting;
import brunomasunaga.propointsnotes.dominio.repositorio.FoodRepositorio;
import brunomasunaga.propointsnotes.dominio.repositorio.RegistreRepositorio;
import brunomasunaga.propointsnotes.dominio.repositorio.SettingsRepositorio;

public class Settings extends AppCompatActivity {
    private SQLiteDatabase connection;
    private DatabaseOpenHelper databaseOpenHelper;

    private SettingsRepositorio settingsRepositorio;
    private FoodRepositorio foodRepositorio;
    private RegistreRepositorio registreRepositorio;
    private Setting setting;
    private Setting searched;
    private EditText nome;
    private EditText idade;
    private RadioButton fem;
    private RadioButton masc;
    private EditText peso;
    private EditText altura;
    private TextView cota;
    private TextView dataSalva;
    private Calendar dataAtual;
    private SimpleDateFormat formato;
    private FloatingActionButton confirmSettings;
    private ConstraintLayout coordinator;
    private boolean allowRecover;
    private AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinator = findViewById(R.id.coordinator);
        confirmSettings = findViewById(R.id.fab);
        nome = findViewById(R.id.nameSettings);
        idade = findViewById(R.id.ageSettings);
        fem = findViewById(R.id.rd_Woman);
        masc = findViewById(R.id.rd_Man);
        peso = findViewById(R.id.weightSettings);
        altura = findViewById(R.id.heightSettings);
        cota = findViewById(R.id.cotaSettings);
        dataSalva = findViewById(R.id.dateSaved);
        dataAtual = new GregorianCalendar();
        formato = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        confirmSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isValidated()){
                    Snackbar snackbar = Snackbar.make(coordinator, "Preencha as informações.", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
                else confirmSettings();
            }
        });
        createConnection();
        getSettings();
    }

    private void createConnection(){
        databaseOpenHelper = new DatabaseOpenHelper(this);
        connection = databaseOpenHelper.getWritableDatabase();
        settingsRepositorio = new SettingsRepositorio(connection);
        foodRepositorio = new FoodRepositorio(connection);
        registreRepositorio = new RegistreRepositorio(connection);
    }

    private void getSettings(){
        searched = new Setting();
        searched = settingsRepositorio.findSettings();
        if(searched.Gender == 0) return;
        nome.setText(searched.Name);
        idade.setText(String.valueOf(searched.Age));
        peso.setText(String.valueOf(searched.Weight));
        altura.setText(String.valueOf(searched.Height));
        cota.setText(String.valueOf(searched.Quota));
        dataSalva.setText("Última vez salvo: " + searched.DateSaved);
        if(searched.Gender == 1) fem.setChecked(true);
        else masc.setChecked(true);
    }

    private void confirmSettings(){
        setting = new Setting();
        setting.Name = nome.getText().toString();
        setting.Age = Integer.parseInt(idade.getText().toString());
        setting.Weight = Double.parseDouble(peso.getText().toString());
        setting.Height = Integer.parseInt(altura.getText().toString());
        setting.Quota = Integer.parseInt(cota.getText().toString());
        formato.setCalendar(dataAtual);
        setting.DateSaved = formato.format(dataAtual.getTime());
        if(fem.isChecked()) setting.Gender = 1;
        else setting.Gender = 2;
        settingsRepositorio.alter(setting);
        finish();
    }

    public void calculateQuota(View view){
        if (!isValidatedCalc()){
            Snackbar snackbar = Snackbar.make(coordinator, "Preencha as informações.", Snackbar.LENGTH_SHORT);
            snackbar.show();
            return;
        }
        double calculatedGender;
        int age = Integer.parseInt(idade.getText().toString());
        double weight = Double.parseDouble(peso.getText().toString());
        int height = Integer.parseInt(altura.getText().toString());
        if (fem.isChecked()) calculatedGender = 387-(7.31*age)+1.14*(10.9*weight+6.607*height);
        else calculatedGender = 864-(9.72*age)+1.12*(14.2*weight+5.03*height);
        int calculatedQuota = (int) Math.min(Math.max(Math.round(Math.max(0.9*calculatedGender - 800, 1000)/35.0)-11,26),71);
        cota.setText(String.valueOf(calculatedQuota));
    }

    private boolean isValidated(){
        if (!isValidatedCalc()) return false;
        if (cota.getText().toString().matches("Não calculada")) return false;
        return true;
    }

    private boolean isValidatedCalc(){
        if (nome.getText().toString().matches("")) return false;
        if (idade.getText().toString().matches("")) return false;
        if (peso.getText().toString().matches("")) return false;
        if (altura.getText().toString().matches("")) return false;
        if (!masc.isChecked() && !fem.isChecked()) return false;
        return true;
    }

    public void createBackup(View view){
        String foodData = getFoodData();
        String registreData = getRegistreData();
        String settingsData = getSettingsData();
        createTxtFile("propoints-bkFoodList.txt", foodData);
        createTxtFile("propoints-bkRegisteList.txt", registreData);
        createTxtFile("propoints-bkSettings.txt", settingsData);
        Toast toast = Toast.makeText(this, "Backup criado e salvo no armazenamento interno.", Toast.LENGTH_LONG);
        toast.show();
    }

    private void createTxtFile(String name, String data){
        String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
        String path = sdcard + File.separator + name;
        try {
            File created = new File(path);
            if (created.exists()){
                created.delete();
            }
            FileWriter writer = new FileWriter(created);
            writer.append(data);
            writer.flush();
            writer.close();
        }
        catch (IOException e) {Toast toast = Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG);
            toast.show();}
    }

    private String getFoodData(){
        List<Food> foods = foodRepositorio.findAll();
        StringBuilder foodList = new StringBuilder();
        for (Food food : foods){
            foodList.append(food.FoodID);
            foodList.append(',');
            foodList.append(food.DescriptionFood);
            foodList.append(',');
            foodList.append(food.UnityFood);
            foodList.append(',');
            foodList.append(food.AmountUnity);
            foodList.append(',');
            foodList.append(food.Carbs);
            foodList.append(',');
            foodList.append(food.Prots);
            foodList.append(',');
            foodList.append(food.Fats);
            foodList.append(',');
            foodList.append(food.Fiber);
            foodList.append(',');
            foodList.append(food.PointsUnity);
            foodList.append('\n');
        }
        return foodList.toString();
    }

    private String getRegistreData(){
        List<Registre> registres = registreRepositorio.findAll();
        StringBuilder registreList = new StringBuilder();
        for (Registre registre : registres){
            registreList.append(registre.RegID);
            registreList.append(',');
            registreList.append(registre.Day);
            registreList.append(',');
            registreList.append(registre.FoodID);
            registreList.append(',');
            registreList.append(registre.QuantityFood);
            registreList.append('\n');
        }
        return registreList.toString();
    }

    private String getSettingsData(){
        Setting settings = settingsRepositorio.findSettings();
        StringBuilder settingsInfo = new StringBuilder();
        settingsInfo.append(settings.Name);
        settingsInfo.append(',');
        settingsInfo.append(settings.Age);
        settingsInfo.append(',');
        settingsInfo.append(settings.Gender);
        settingsInfo.append(',');
        settingsInfo.append(settings.Weight);
        settingsInfo.append(',');
        settingsInfo.append(settings.Height);
        settingsInfo.append(',');
        settingsInfo.append(settings.Quota);
        settingsInfo.append(',');
        settingsInfo.append(settings.DateSaved);
        settingsInfo.append('\n');
        return settingsInfo.toString();
    }

    public void restoreData(View view){
        String foodList = readBackup("propoints-bkFoodList.txt");
        String registreList = readBackup("propoints-bkRegisteList.txt");
        String settingsList = readBackup("propoints-bkSettings.txt");
        if (foodList == null || registreList == null || settingsList == null){
            Toast toast = Toast.makeText(this, "Erro. Os arquivos de backup não foram encontrados no armazenamento.", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        dialogRestore(foodList, registreList, settingsList);
    }

    private void dialogRestore(String foodList, String registreList, String settingsList){
        allowRecover = false;
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException();
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Restaurar dados");
        builder.setMessage("Os dados serão recuperados a partir do backup localizado. Todos os dados atuais serão apagados.\nDeseja continuar?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                allowRecover = true;
                handler.sendMessage(handler.obtainMessage());
            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                handler.sendMessage(handler.obtainMessage());
            }
        });
        alert = builder.create();
        alert.show();
        try { Looper.loop(); }catch(RuntimeException e) {}
        if (allowRecover == false) return;
        else{
            foodRepositorio.removeAll();
            registreRepositorio.removeAll();
            makeFoodInsertion(foodList);
            makeSettingsInsertion(settingsList);
            makeRegistreInsertion(registreList);
        }
    }

    private String readBackup(String name){
        String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
        String path = sdcard + File.separator + name;
        StringBuilder readed = new StringBuilder();
        try {
            BufferedReader buffer = new BufferedReader(new InputStreamReader(new FileInputStream(path), "utf8"), 8192);
            String line;
            while ((line = buffer.readLine()) != null) {
                readed.append(line);
                readed.append('\n');
            }
            buffer.close();
        }
        catch (IOException e) {return null;}
        return readed.toString();
    }

    private void makeFoodInsertion(String foodList){
        String[] foods = foodList.split("\n");
        for (String line : foods){
            String[] data = line.split(",");
            Food newFood = new Food();
            newFood.FoodID = Integer.parseInt(data[0]);
            newFood.DescriptionFood = data[1];
            newFood.UnityFood = data[2];
            newFood.AmountUnity = Double.parseDouble(data[3]);
            newFood.Carbs = Double.parseDouble(data[4]);
            newFood.Prots = Double.parseDouble(data[5]);
            newFood.Fats = Double.parseDouble(data[6]);
            newFood.Fiber = Double.parseDouble(data[7]);
            newFood.PointsUnity = Integer.parseInt(data[8]);
            foodRepositorio.insertID(newFood);
        }
    }

    private void makeRegistreInsertion(String registreList){
        String[] registres = registreList.split("\n");
        for (String line : registres){
            String[] data = line.split(",");
            Registre newRegistre = new Registre();
            newRegistre.Day = data[0];
            newRegistre.FoodID = Integer.parseInt(data[1]);
            newRegistre.QuantityFood = Double.parseDouble(data[2]);
            registreRepositorio.insert(newRegistre);
        }
    }

    private void makeSettingsInsertion(String settings){
        String[] settingsData = settings.split(",");
        Setting newSetting = new Setting();
        newSetting.Name = settingsData[0];
        newSetting.Age = Integer.parseInt(settingsData[1]);
        newSetting.Gender = Integer.parseInt(settingsData[2]);
        newSetting.Weight = Double.parseDouble(settingsData[3]);
        newSetting.Height = Integer.parseInt(settingsData[4]);
        newSetting.Quota = Integer.parseInt(settingsData[5]);
        newSetting.DateSaved = settingsData[6];
        settingsRepositorio.alter(newSetting);
    }
}

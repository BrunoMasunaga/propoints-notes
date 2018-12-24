package brunomasunaga.propointsnotes;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import java.lang.Math;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import brunomasunaga.propointsnotes.database.DatabaseOpenHelper;
import brunomasunaga.propointsnotes.dominio.entidades.Setting;
import brunomasunaga.propointsnotes.dominio.repositorio.SettingsRepositorio;

public class Settings extends AppCompatActivity {
    private SQLiteDatabase connection;
    private DatabaseOpenHelper databaseOpenHelper;

    private SettingsRepositorio settingsRepositorio;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                confirmSettings();
            }
        });

        createConnection();
        getSettings();
    }

    private void createConnection(){
        databaseOpenHelper = new DatabaseOpenHelper(this);
        connection = databaseOpenHelper.getWritableDatabase();
        settingsRepositorio = new SettingsRepositorio(connection);
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
        double calculatedGender;
        int age = Integer.parseInt(idade.getText().toString());
        double weight = Double.parseDouble(peso.getText().toString());
        int height = Integer.parseInt(altura.getText().toString());
        if (fem.isChecked()) calculatedGender = 387-(7.31*age)+1.14*(10.9*weight+6.607*height);
        else calculatedGender = 864-(9.72*age)+1.12*(14.2*weight+5.03*height);
        int calculatedQuota = (int) Math.min(Math.max(Math.round(Math.max(0.9*calculatedGender - 800, 1000)/35.0)-11,26),71);
        cota.setText(String.valueOf(calculatedQuota));
    }
}

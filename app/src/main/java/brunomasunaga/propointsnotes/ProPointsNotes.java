package brunomasunaga.propointsnotes;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import brunomasunaga.propointsnotes.database.DatabaseOpenHelper;
import brunomasunaga.propointsnotes.dominio.entidades.Registre;
import brunomasunaga.propointsnotes.dominio.entidades.Setting;
import brunomasunaga.propointsnotes.dominio.repositorio.RegistreRepositorio;
import brunomasunaga.propointsnotes.dominio.repositorio.SettingsRepositorio;

public class ProPointsNotes extends AppCompatActivity {
    private SQLiteDatabase connection;
    private DatabaseOpenHelper databaseOpenHelper;
    private RegistreRepositorio registreRepositorio;

    private TextView data;
    private TextView pontosRestantes;
    private Calendar dataAtual;
    private SimpleDateFormat formato;
    private FloatingActionButton registrarConsumo;
    private FloatingActionButton abrirListaComidas;
    private FloatingActionButton abrirCalculadora;
    private RecyclerView registresList;
    private RegistreAdapter registreAdapter;
    private ProgressBar quotaProgress;
    private int sumCons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        createConnection();

        registrarConsumo = findViewById(R.id.regConsumo);
        abrirListaComidas = findViewById(R.id.foodList);
        abrirCalculadora = findViewById(R.id.calculator);
        data = findViewById(R.id.data);
        pontosRestantes = findViewById(R.id.pontosRestantes);
        dataAtual = new GregorianCalendar();
        formato = new SimpleDateFormat("dd/MM/yyyy");
        quotaProgress = findViewById(R.id.quotaProgress);
        registresList = findViewById(R.id.registresList);
        registresList.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        registresList.setLayoutManager(linearLayoutManager);
        registreRepositorio = new RegistreRepositorio(connection);
        List<Registre> registros = registreRepositorio.findAll();
        registreAdapter = new RegistreAdapter(registros);
        registresList.setAdapter(registreAdapter);
        sumCons = 0;
        for(Registre r : registros){
            sumCons = sumCons + Registre.calculatePoints(r);
        }

        registrarConsumo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent actAdd = new Intent(ProPointsNotes.this, AddRegistry.class);
                startActivity(actAdd);
            }
        });

        abrirCalculadora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent actCalculator = new Intent(ProPointsNotes.this, Calculator.class);
                startActivity(actCalculator);
            }
        });

        abrirListaComidas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent actFoodList = new Intent(ProPointsNotes.this, FoodList.class);
                startActivity(actFoodList);
            }
        });

        getDate();
        getPoints();
    }

    @Override
    protected void onStart(){
        super.onStart();
        getDate();
        getPoints();
    }

    @Override
    protected void onResume(){
        super.onResume();
        getDate();
        getPoints();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.settings){
            openSettings();
        }
        if (item.getItemId() == R.id.setDate){
            setDate();
        }
        return true;
    }

    public void openSettings(){
        Intent actSettings = new Intent(this, Settings.class);
        startActivity(actSettings);
    }

    public void setDate(){

    }

    private void createConnection(){
        databaseOpenHelper = new DatabaseOpenHelper(this);
        connection = databaseOpenHelper.getWritableDatabase();
    }

    private void getDate(){
        formato.setCalendar(dataAtual);
        data.setText(formato.format(dataAtual.getTime()));
    }

    private void getPoints(){
        SettingsRepositorio settingsRepositorio = new SettingsRepositorio(connection);
        Setting searched = settingsRepositorio.findSettings();
        if(searched.Quota == 0) return;
        else{
            pontosRestantes.setText(String.valueOf(searched.Quota - sumCons));
            quotaProgress.setMax(searched.Quota);
            quotaProgress.setProgress(Integer.parseInt(pontosRestantes.getText().toString()));
            pontosRestantes.setTextSize(48);
            TextView st = findViewById(R.id.staticRestantes);
            st.setText("pontos restantes");
        }
    }
}

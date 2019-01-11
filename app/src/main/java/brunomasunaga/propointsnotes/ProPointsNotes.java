package brunomasunaga.propointsnotes;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
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
    private TextView st;
    private TextView pontosRestantes;
    private Calendar dataAtual;
    private Calendar dataSelect;
    private SimpleDateFormat formato;
    private FloatingActionButton registrarConsumo;
    private FloatingActionButton abrirListaComidas;
    private FloatingActionButton abrirCalculadora;
    private RecyclerView registresList;
    private RegistreAdapter registreAdapter;
    private ProgressBar quotaProgress;
    private ImageButton leftArrow;
    private ImageButton rightArrow;
    private LinearLayoutManager linearLayoutManager;
    private int sumCons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        createConnection();

        st = findViewById(R.id.staticRestantes);
        registrarConsumo = findViewById(R.id.regConsumo);
        abrirListaComidas = findViewById(R.id.foodList);
        abrirCalculadora = findViewById(R.id.calculator);
        data = findViewById(R.id.data);
        pontosRestantes = findViewById(R.id.pontosRestantes);
        dataAtual = new GregorianCalendar();
        dataSelect = new GregorianCalendar();
        formato = new SimpleDateFormat("dd/MM/yyyy");
        quotaProgress = findViewById(R.id.quotaProgress);
        registresList = findViewById(R.id.registresList);
        registresList.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        leftArrow = findViewById(R.id.leftArrow);
        rightArrow = findViewById(R.id.rightArrow);
        rightArrow.setClickable(false);

        registrarConsumo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent actAdd = new Intent(ProPointsNotes.this, AddRegistry.class);
                actAdd.putExtra("DATE", data.getText().toString());
                startActivityForResult(actAdd, 2);

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
        reloadConsumed();
    }

    @Override
    protected void onStart(){
        super.onStart();
        getDate();
        reloadConsumed();
    }

    @Override
    protected void onResume(){
        super.onResume();
        getDate();
        reloadConsumed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.settings) {
            openSettings();
        }
        return true;
    }

    public void reloadConsumed(){
        if (dataAtual.equals(dataSelect)){
            rightArrow.setClickable(false);
            int newcolor = getResources().getColor(R.color.inativy);
            rightArrow.getDrawable().setColorFilter(newcolor, PorterDuff.Mode.SRC_ATOP);
        }
        registresList.setLayoutManager(linearLayoutManager);
        registreRepositorio = new RegistreRepositorio(connection);
        List<Registre> registros = registreRepositorio.findByDate(data.getText().toString());
        registreAdapter = new RegistreAdapter(registros, this);
        registresList.setAdapter(registreAdapter);
        sumCons = 0;
        for(Registre r : registros){
            sumCons = sumCons + Registre.calculatePoints(r);
        }
        getPoints();
    }

    public void openSettings(){
        Intent actSettings = new Intent(this, Settings.class);
        startActivity(actSettings);
    }

    private void createConnection(){
        databaseOpenHelper = new DatabaseOpenHelper(this);
        connection = databaseOpenHelper.getWritableDatabase();
    }

    private void getDate(){
        formato.setCalendar(dataSelect);
        data.setText(formato.format(dataSelect.getTime()));
    }

    public void increaseDate(View view){
        dataSelect.add(Calendar.DAY_OF_MONTH, 1);
        formato.setCalendar(dataSelect);
        data.setText(formato.format(dataSelect.getTime()));
        if (rightArrow.isClickable() == false) return;
        reloadConsumed();
    }
    public void decreaseDate(View view){
        dataSelect.add(Calendar.DAY_OF_MONTH, -1);
        formato.setCalendar(dataSelect);
        data.setText(formato.format(dataSelect.getTime()));
        rightArrow.setClickable(true);
        int newcolor = getResources().getColor(R.color.colorSecondary);
        rightArrow.getDrawable().setColorFilter(newcolor, PorterDuff.Mode.SRC_ATOP);
        reloadConsumed();
    }

    private void getPoints(){
        SettingsRepositorio settingsRepositorio = new SettingsRepositorio(connection);
        Setting searched = settingsRepositorio.findSettings();
        if(searched.Quota == 0) return;
        else{
            int rest = searched.Quota - sumCons;
            if (rest >= 0){
                pontosRestantes.setText(String.valueOf(rest));
                st.setText("pontos restantes");
                quotaProgress.setProgress(rest);
                pontosRestantes.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
                st.setTextColor(ContextCompat.getColor(this, R.color.defaultColor));
                if(rest == 0){
                    pontosRestantes.setTextColor(ContextCompat.getColor(this, R.color.error));
                    st.setTextColor(ContextCompat.getColor(this, R.color.error));
                }
            }
            else{
                pontosRestantes.setText(String.valueOf(-rest));
                st.setText("pontos ultrapassados");
                quotaProgress.setProgress(0);
                pontosRestantes.setTextColor(ContextCompat.getColor(this, R.color.error));
                st.setTextColor(ContextCompat.getColor(this, R.color.error));
            }
            quotaProgress.setMax(searched.Quota);
            pontosRestantes.setTextSize(48);
        }
    }
}

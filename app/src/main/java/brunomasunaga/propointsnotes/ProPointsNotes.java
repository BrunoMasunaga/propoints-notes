package brunomasunaga.propointsnotes;

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

    private TextView quota;
    private TextView consumed;
    private TextView stQuota;
    private TextView stConsumed;
    private boolean notSetted;

    private TextView noRegistry;

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
    private SimpleDateFormat hourFormat;
    private int sumCons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        createConnection();

        quota = findViewById(R.id.dayQuota);
        consumed = findViewById(R.id.ptsConsumidos);
        stQuota = findViewById(R.id.st_quota);
        stConsumed = findViewById(R.id.st_pontosConsumidos);
        notSetted = true;

        noRegistry = findViewById(R.id.st_noRegistry);

        st = findViewById(R.id.staticRestantes);
        registrarConsumo = findViewById(R.id.regConsumo);
        abrirListaComidas = findViewById(R.id.foodList);
        abrirCalculadora = findViewById(R.id.calculator);
        data = findViewById(R.id.data);
        pontosRestantes = findViewById(R.id.pontosRestantes);
        dataAtual = new GregorianCalendar();
        dataSelect = new GregorianCalendar();
        formato = new SimpleDateFormat("dd/MM/yyyy");
        hourFormat = new SimpleDateFormat("HH:mm");
        quotaProgress = findViewById(R.id.quotaProgress);
        registresList = findViewById(R.id.registresList);
        registresList.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        leftArrow = findViewById(R.id.leftArrow);
        rightArrow = findViewById(R.id.rightArrow);

        registrarConsumo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent actAdd = new Intent(ProPointsNotes.this, AddRegistry.class);
                String[] date = new String[2];
                date[0] = data.getText().toString();
                if (dataSelect.equals(dataAtual)){
                    date[1] = hourFormat.format(dataSelect.getTime());
                }
                else date[1] = null;
                actAdd.putExtra("DATE", date);
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
        if (dataSelect.equals(dataAtual)){
            rightArrow.setClickable(false);
            int newcolor = getResources().getColor(R.color.inativy);
            rightArrow.getDrawable().setColorFilter(newcolor, PorterDuff.Mode.SRC_ATOP);
        }
        if (compareStartLimit()){
            leftArrow.setClickable(false);
            int newcolor = getResources().getColor(R.color.inativy);
            leftArrow.getDrawable().setColorFilter(newcolor, PorterDuff.Mode.SRC_ATOP);
        }
        registresList.setLayoutManager(linearLayoutManager);
        registreRepositorio = new RegistreRepositorio(connection);
        List<Registre> registros = registreRepositorio.findByDate(data.getText().toString());
        if (registros.size() == 0) noRegistry.setVisibility(View.VISIBLE);
        else noRegistry.setVisibility(View.GONE);
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
        if (rightArrow.isClickable() == false) return;
        dataSelect.add(Calendar.DAY_OF_MONTH, 1);
        formato.setCalendar(dataSelect);
        data.setText(formato.format(dataSelect.getTime()));
        leftArrow.setClickable(true);
        int newcolor = getResources().getColor(R.color.colorSecondary);
        leftArrow.getDrawable().setColorFilter(newcolor, PorterDuff.Mode.SRC_ATOP);
        reloadConsumed();
    }
    public void decreaseDate(View view){
        if (leftArrow.isClickable() == false) return;
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
        if(searched.Quota == 0){
            notSetted = true;
            return;
        }
        if(notSetted){
            quota.setVisibility(View.VISIBLE);
            consumed.setVisibility(View.VISIBLE);
            stQuota.setVisibility(View.VISIBLE);
            stConsumed.setVisibility(View.VISIBLE);
            notSetted = false;
        }
        else{
            int rest = searched.Quota - sumCons;
            quota.setText(String.valueOf(searched.Quota));
            consumed.setText(String.valueOf(sumCons));
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

    private boolean compareStartLimit(){
        formato.setCalendar(dataSelect);
        String select = formato.format(dataSelect.getTime());
        if (select.equals("28/04/2019")) return true;
        return false;
    }
}

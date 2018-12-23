package brunomasunaga.propointsnotes;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import brunomasunaga.propointsnotes.database.DatabaseOpenHelper;

public class ProPointsNotes extends AppCompatActivity {
    private SQLiteDatabase connection;
    private DatabaseOpenHelper databaseOpenHelper;

    private ConstraintLayout constraintLayoutPrincipal;
    private FloatingActionButton registrarConsumo;
    private TextView data;
    private TextView pontosRestantes;
    private Calendar dataAtual;
    private SimpleDateFormat formato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        registrarConsumo = (FloatingActionButton) findViewById(R.id.regConsumo);
        registrarConsumo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        data = (TextView) findViewById(R.id.data);
        pontosRestantes = (TextView) findViewById(R.id.pontosRestantes);
        dataAtual = new GregorianCalendar();
        formato = new SimpleDateFormat("dd/MM/yyyy");

        createConnection();
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
        return true;
    }

    private void getDate(){
        formato.setCalendar(dataAtual);
        data.setText(formato.format(dataAtual.getTime()));
    }

    private void getPoints(){
        View inflatedView = getLayoutInflater().inflate(R.layout.content_settings, null);
        TextView cota = (TextView) inflatedView.findViewById(R.id.cotaSettings);
        pontosRestantes.setText(cota.getText());
    }

    private void createConnection(){
        databaseOpenHelper = new DatabaseOpenHelper(this);
        connection = databaseOpenHelper.getWritableDatabase();
    }

    private void decreaseDate(int days){
        dataAtual.add(Calendar.DAY_OF_MONTH, -days);
        formato.setCalendar(dataAtual);
        data.setText(formato.format(dataAtual.getTime()));
    }

    public void openSettings(){
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }


}

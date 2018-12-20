package brunomasunaga.propointsnotes;

import android.app.AlertDialog;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
        data = (TextView) findViewById(R.id.data);
        pontosRestantes = (TextView) findViewById(R.id.pontosRestantes);
        dataAtual = new GregorianCalendar();
        formato = new SimpleDateFormat("dd/MM/yyyy");
        constraintLayoutPrincipal = (ConstraintLayout) findViewById(R.id.constraintLayoutPrincipal);

        createConnection();
        getDate();
    }

    private void getDate(){
        formato.setCalendar(dataAtual);
        data.setText(formato.format(dataAtual.getTime()));
    }

    private void createConnection(){
        try{
            databaseOpenHelper = new DatabaseOpenHelper(this);
            connection = databaseOpenHelper.getWritableDatabase();
            Snackbar.make(constraintLayoutPrincipal, "Conexão criada.", Snackbar.LENGTH_SHORT)
                    .setAction("OK", null).show();
        }catch (SQLException e){
            AlertDialog.Builder dlg = new AlertDialog.Builder(this);
            dlg.setTitle("Erro.");
            dlg.setMessage(e.getMessage());
            dlg.setNeutralButton("OK", null);
            dlg.show();
        }
    }

    private void decreaseDate(int days){
        dataAtual.add(Calendar.DAY_OF_MONTH, -days);
        formato.setCalendar(dataAtual);
        data.setText(formato.format(dataAtual.getTime()));
    }

}

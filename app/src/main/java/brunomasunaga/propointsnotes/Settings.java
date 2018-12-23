package brunomasunaga.propointsnotes;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import brunomasunaga.propointsnotes.database.DatabaseOpenHelper;
import brunomasunaga.propointsnotes.dominio.entidades.Setting;
import brunomasunaga.propointsnotes.dominio.repositorio.SettingsRepositorio;

public class Settings extends AppCompatActivity {
    private SQLiteDatabase connection;
    private DatabaseOpenHelper databaseOpenHelper;
    private SettingsRepositorio settingsRepositorio;
    private FloatingActionButton confirmSettings;
    private Setting setting;
    private Setting searched;
    private EditText nome;
    private TextView cota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nome = (EditText) findViewById(R.id.nameSettings);
        cota = (TextView) findViewById(R.id.cotaSettings);
        createConnection();

        confirmSettings = (FloatingActionButton) findViewById(R.id.fab);
        confirmSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmSettings();
            }
        });
        getSettings();
    }

    private void getSettings(){
        searched = new Setting();
        searched = settingsRepositorio.findSettings();
        if(searched != null) {
            nome.setText(searched.Name);
            cota.setText(String.valueOf(searched.Quota));
        }
    }

    private void createConnection(){
        databaseOpenHelper = new DatabaseOpenHelper(this);
        connection = databaseOpenHelper.getWritableDatabase();
        settingsRepositorio = new SettingsRepositorio(connection);
    }

    private void confirmSettings(){
        setting = new Setting();
        setting.Name = nome.getText().toString();
        setting.Quota = Integer.parseInt(cota.getText().toString());
        if(searched != null) settingsRepositorio.alter(setting);
        else settingsRepositorio.insert(setting);
        finish();
    }

}

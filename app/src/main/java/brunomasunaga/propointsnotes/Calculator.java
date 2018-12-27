package brunomasunaga.propointsnotes;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import java.lang.Math;

public class Calculator extends AppCompatActivity {

    private EditText carboidratos;
    private EditText proteinas;
    private EditText gorduras;
    private EditText fibra;
    private EditText qtUnidade;
    private EditText tpUnidade;
    private EditText quantidade;
    private TextView pontos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        carboidratos = findViewById(R.id.carbs);
        proteinas = findViewById(R.id.prots);
        gorduras = findViewById(R.id.fats);
        fibra = findViewById(R.id.fiber);
        qtUnidade = findViewById(R.id.qtUnity);
        tpUnidade = findViewById(R.id.tpUnity);
        quantidade = findViewById(R.id.quantity);
        pontos = findViewById(R.id.pointsCalc);

        FloatingActionButton fab = findViewById(R.id.confirmCalc);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void calculatePoints(View view){
        int calculatedPoints;
        double carbs = Double.parseDouble(carboidratos.getText().toString());
        double prots = Double.parseDouble(proteinas.getText().toString());
        double fats = Double.parseDouble(gorduras.getText().toString());
        double fiber = Double.parseDouble(fibra.getText().toString());
        double multiplier = Double.parseDouble(quantidade.getText().toString());
        calculatedPoints = (int) Math.max(Math.round(multiplier*(16*prots + 19*carbs + 45*fats + 5*fiber)/175), 0);
        pontos.setText(String.valueOf(calculatedPoints));
    }

}

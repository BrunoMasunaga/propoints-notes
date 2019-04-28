package brunomasunaga.propointsnotes;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import brunomasunaga.propointsnotes.dominio.entidades.Food;

public class Calculator extends AppCompatActivity {

    private EditText carboidratos;
    private EditText proteinas;
    private EditText gorduras;
    private EditText fibra;
    private EditText qtUnidade;
    private EditText tpUnidade;
    private EditText quantidade;
    private TextView pontos;
    private ConstraintLayout coordinator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinator = findViewById(R.id.coordinator);
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
                if (!isValidatedCalc() || !isValidatedInfo()){
                    Snackbar snackbar = Snackbar.make(coordinator, "Preencha as informações.", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    return;
                }
                Food food = new Food();
                food.Carbs = Double.parseDouble(carboidratos.getText().toString());
                food.Prots = Double.parseDouble(proteinas.getText().toString());
                food.Fats = Double.parseDouble(gorduras.getText().toString());
                food.Fiber = Double.parseDouble(fibra.getText().toString());
                food.AmountUnity = Double.parseDouble(qtUnidade.getText().toString());
                food.UnityFood = tpUnidade.getText().toString();
                Intent actAddFood = new Intent(Calculator.this, AddFood.class);
                actAddFood.putExtra("FCALC", food);
                startActivityForResult(actAddFood, 3);
                finish();
            }
        });
    }

    public void calculatePoints(View view){
        if (!isValidatedCalc()){
            Snackbar snackbar = Snackbar.make(coordinator, "Preencha as informações.", Snackbar.LENGTH_SHORT);
            snackbar.show();
            return;
        }
        double carbs = Double.parseDouble(carboidratos.getText().toString());
        double prots = Double.parseDouble(proteinas.getText().toString());
        double fats = Double.parseDouble(gorduras.getText().toString());
        double fiber = Double.parseDouble(fibra.getText().toString());
        double mult = Double.parseDouble(quantidade.getText().toString());
        int calculatedPoints = Food.calculatePointsInfo(carbs, prots, fats, fiber, mult);
        pontos.setText(String.valueOf(calculatedPoints));
    }

    public static String integerIfPossible(double number){
        int rounded = (int) number;
        double dif = number - rounded;
        if (dif < 0.1){
            return String.valueOf(rounded);
        }
        return String.valueOf(number);
    }

    private boolean isValidatedCalc(){
        if (carboidratos.getText().toString().matches("")) return false;
        if (proteinas.getText().toString().matches("")) return false;
        if (gorduras.getText().toString().matches("")) return false;
        if (fibra.getText().toString().matches("")) return false;
        if (quantidade.getText().toString().matches("")) return false;
        return true;
    }

    private boolean isValidatedInfo(){
        if (qtUnidade.getText().toString().matches("")) return false;
        if (tpUnidade.getText().toString().matches("")) return false;
        return true;
    }

}
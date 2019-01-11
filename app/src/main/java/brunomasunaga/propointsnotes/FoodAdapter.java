package brunomasunaga.propointsnotes;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import brunomasunaga.propointsnotes.database.DatabaseOpenHelper;
import brunomasunaga.propointsnotes.dominio.entidades.Food;
import brunomasunaga.propointsnotes.dominio.repositorio.FoodRepositorio;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolderFood> {

    private List<Food> foods;
    private FoodRepositorio foodRepositorio;
    private boolean remove;
    private AlertDialog decision;
    private SQLiteDatabase connection;
    private DatabaseOpenHelper databaseOpenHelper;
    private Context ct;

    public FoodAdapter(List<Food> foods, Context context){
        this.foods = foods;
        this.ct = context;
    }

    private void createConnection(Context context){
        databaseOpenHelper = new DatabaseOpenHelper(context);
        connection = databaseOpenHelper.getWritableDatabase();
    }

    @NonNull
    @Override
    public FoodAdapter.ViewHolderFood onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.fooditem, viewGroup, false);
        ViewHolderFood holderFood = new ViewHolderFood(view, viewGroup.getContext());
        return holderFood;
    }

    @Override
    public void onBindViewHolder(@NonNull FoodAdapter.ViewHolderFood holder, int i) {
        if (foods != null && foods.size() > 0) {
            Food food = foods.get(i);
            holder.nomeComida.setText(food.DescriptionFood);
            if(Calculator.verifyInteger(food.AmountUnity)){
                int q = (int) food.AmountUnity;
                holder.unity.setText(String.valueOf("("+q) + " " + food.UnityFood+")");
            }
            else holder.unity.setText(String.valueOf("("+food.AmountUnity) + " " + food.UnityFood+")");
            holder.pontos.setText(String.valueOf(food.PointsUnity));
            if(food.Carbs == -1) holder.info.setText("Sem informações nutricionais");
            else{
                String information = "Carb: ";
                if(Calculator.verifyInteger(food.Carbs)){
                    int q = (int) food.Carbs;
                    information = information + q;
                }
                else information = information + food.Carbs;
                information = information + " g | Prot: ";
                if(Calculator.verifyInteger(food.Prots)){
                    int q = (int) food.Prots;
                    information = information + q;
                }
                else information = information + food.Prots;
                information = information + " g | Gord: ";
                if(Calculator.verifyInteger(food.Fats)){
                    int q = (int) food.Fats;
                    information = information + q;
                }
                else information = information + food.Fats;
                information = information + " g | Fib: ";
                if(Calculator.verifyInteger(food.Fiber)){
                    int q = (int) food.Fiber;
                    information = information + q;
                }
                else information = information + food.Fiber;
                information = information + " g";
                holder.info.setText(information);
            }
        }
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    public class ViewHolderFood extends RecyclerView.ViewHolder{

        public TextView nomeComida;
        public TextView pontos;
        public TextView unity;
        public TextView info;

        public ViewHolderFood(@NonNull final View itemView, final Context context) {
            super(itemView);
            nomeComida = itemView.findViewById(R.id.nomeComida);
            pontos = itemView.findViewById(R.id.points);
            unity = itemView.findViewById(R.id.unity);
            info = itemView.findViewById(R.id.info);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(foods.size() == 0) return true;
                    createConnection(context);
                    foodRepositorio = new FoodRepositorio(connection);
                    remove = false;
                    final Handler handler = new Handler() {
                        @Override
                        public void handleMessage(Message mesg) {
                        throw new RuntimeException();
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Modificar ou excluir?");
                    builder.setMessage("Deseja modificar ou excluir o alimento selecionado?");
                    builder.setPositiveButton("Modificar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        handler.sendMessage(handler.obtainMessage());
                    }
                    });
                    builder.setNegativeButton("Excluir", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            remove = true;
                            handler.sendMessage(handler.obtainMessage());
                        }
                    });
                    decision = builder.create();
                    decision.show();
                    try { Looper.loop(); }catch(RuntimeException e) {}
                    if (remove == true){
                        foodRepositorio.removeByName(nomeComida.getText().toString());
                        if (ct instanceof FoodList){
                            ((FoodList)ct).reloadList();
                        }
                    }
                    else{
                        Food food = foods.get(getLayoutPosition());
                        Intent actAddFood = new Intent(context, AddFood.class);
                        actAddFood.putExtra("FOOD", food);
                        ((AppCompatActivity) context).startActivityForResult(actAddFood, 0);
                        ((AppCompatActivity) context).finish();
                    }
                    return true;
                }
            });
        }
    }

}

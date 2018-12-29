package brunomasunaga.propointsnotes;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import brunomasunaga.propointsnotes.dominio.entidades.Food;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolderFood> {

    private List<Food> foods;

    public FoodAdapter(List<Food> foods){
        this.foods = foods;
    }

    @NonNull
    @Override
    public FoodAdapter.ViewHolderFood onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.fooditem, viewGroup, false);
        ViewHolderFood holderFood = new ViewHolderFood(view);
        return holderFood;
    }

    @Override
    public void onBindViewHolder(@NonNull FoodAdapter.ViewHolderFood holder, int i) {
        if (foods != null && foods.size() > 0) {
            Food food = foods.get(i);
            holder.nomeComida.setText(food.DescriptionFood);
            holder.unity.setText(String.valueOf("("+food.AmountUnity) + " " + food.UnityFood+")");
            holder.pontos.setText(String.valueOf(food.PointsUnity));
            holder.info.setText("Carbs: "+String.valueOf(food.Carbs)+"g | Prots: "
                    +String.valueOf(food.Prots)+"g | Gord: "+String.valueOf(food.Fats)+
                    "g | Fib: "+String.valueOf(food.Fiber));
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

        public ViewHolderFood(@NonNull View itemView) {
            super(itemView);
            nomeComida = itemView.findViewById(R.id.nomeComida);
            pontos = itemView.findViewById(R.id.points);
            unity = itemView.findViewById(R.id.unity);
            info = itemView.findViewById(R.id.info);
        }
    }

}

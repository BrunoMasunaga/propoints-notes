package brunomasunaga.propointsnotes;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import brunomasunaga.propointsnotes.dominio.entidades.Registre;

public class RegistreAdapter extends RecyclerView.Adapter<RegistreAdapter.ViewHolderRegistre> {

    private List<Registre> registros;

    public RegistreAdapter(List<Registre> registros){
        this.registros = registros;
    }

    @NonNull
    @Override
    public RegistreAdapter.ViewHolderRegistre onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.registreitem, viewGroup, false);
        ViewHolderRegistre holderRegistre = new ViewHolderRegistre(view);
        return holderRegistre;
    }

    @Override
    public void onBindViewHolder(@NonNull RegistreAdapter.ViewHolderRegistre holder, int i) {
        if (registros != null && registros.size() > 0) {
            Registre registre = registros.get(i);
            holder.nomeComida.setText(registre.DescriptionFood);
            holder.dataCons.setText(registre.Day);
            holder.unity.setText(String.valueOf(registre.AmountUnity) + " " + registre.UnityFood);
            holder.pontos.setText(String.valueOf((int)registre.QuantityFood*registre.PointsUnity));
            holder.quantidadeCons.setText(String.valueOf(registre.QuantityFood)+"x");
        }
    }

    @Override
    public int getItemCount() {
        return registros.size();
    }

    public class ViewHolderRegistre extends RecyclerView.ViewHolder{

        public TextView nomeComida;
        public TextView pontos;
        public TextView unity;
        public TextView quantidadeCons;
        public TextView dataCons;

        public ViewHolderRegistre(@NonNull View itemView) {
            super(itemView);
            nomeComida = itemView.findViewById(R.id.nomeComida);
            pontos = itemView.findViewById(R.id.points);
            unity = itemView.findViewById(R.id.unity);
            dataCons = itemView.findViewById(R.id.dataCons);
            quantidadeCons = itemView.findViewById(R.id.quantidadeCons);
        }
    }

}

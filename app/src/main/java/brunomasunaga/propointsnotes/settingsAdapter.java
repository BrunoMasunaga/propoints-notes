package brunomasunaga.propointsnotes;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

import brunomasunaga.propointsnotes.dominio.entidades.Setting;

public class settingsAdapter extends RecyclerView.Adapter {
    private List<Setting> dados;

    public settingsAdapter(List<Setting> dados){
        this.dados = dados;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}

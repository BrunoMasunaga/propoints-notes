package brunomasunaga.propointsnotes;

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
import brunomasunaga.propointsnotes.dominio.entidades.Registre;
import brunomasunaga.propointsnotes.dominio.repositorio.RegistreRepositorio;

public class RegistreAdapter extends RecyclerView.Adapter<RegistreAdapter.ViewHolderRegistre> {

    private List<Registre> registros;
    private SQLiteDatabase connection;
    private DatabaseOpenHelper databaseOpenHelper;
    private RegistreRepositorio registreRepositorio;
    private boolean remove;
    private AlertDialog decision;
    private Context ct;

    public RegistreAdapter(List<Registre> registros, Context context){
        this.registros = registros;
        this.ct = context;
    }

    private void createConnection(Context context){
        databaseOpenHelper = new DatabaseOpenHelper(context);
        connection = databaseOpenHelper.getWritableDatabase();
    }

    @NonNull
    @Override
    public RegistreAdapter.ViewHolderRegistre onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.registreitem, viewGroup, false);
        ViewHolderRegistre holderRegistre = new ViewHolderRegistre(view, viewGroup.getContext());
        return holderRegistre;
    }

    @Override
    public void onBindViewHolder(@NonNull RegistreAdapter.ViewHolderRegistre holder, int i) {
        if (registros != null && registros.size() > 0) {
            Registre registre = registros.get(i);
            holder.nomeComida.setText(registre.DescriptionFood);
            if (registre.Hour == null) holder.hourCons.setText("Sem registro de horário");
            else holder.hourCons.setText(registre.Hour);
            double q = registre.AmountUnity*registre.QuantityFood;
            holder.quantidadeCons.setText(Calculator.integerIfPossible(q) + " " + registre.UnityFood + " - ");
            holder.pontos.setText(String.valueOf(Registre.calculatePoints(registre)));
        }
    }

    @Override
    public int getItemCount() {
        return registros.size();
    }

    public class ViewHolderRegistre extends RecyclerView.ViewHolder{

        public TextView nomeComida;
        public TextView pontos;
        public TextView quantidadeCons;
        public TextView hourCons;

        public ViewHolderRegistre(@NonNull View itemView, final Context context) {
            super(itemView);
            nomeComida = itemView.findViewById(R.id.nomeComida);
            pontos = itemView.findViewById(R.id.points);
            hourCons = itemView.findViewById(R.id.hourCons);
            quantidadeCons = itemView.findViewById(R.id.quantidadeCons);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(registros.size() == 0) return true;
                    createConnection(context);
                    registreRepositorio = new RegistreRepositorio(connection);
                    remove = false;
                    final Handler handler = new Handler() {
                        @Override
                        public void handleMessage(Message mesg) {
                            throw new RuntimeException();
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Modificar ou excluir?");
                    builder.setMessage("Deseja modificar ou excluir o registro selecionado?");
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
                        registreRepositorio.remove(registros.get(getLayoutPosition()).RegID);
                        if (ct instanceof ProPointsNotes){
                            ((ProPointsNotes)ct).reloadConsumed();
                        }
                    }
                    else{
                        Registre reg = registros.get(getLayoutPosition());
                        Intent actAddRegistry = new Intent(context, AddRegistry.class);
                        actAddRegistry.putExtra("REGISTRE", reg);
                        ((AppCompatActivity) context).startActivityForResult(actAddRegistry, 1);
                    }
                    return true;
                }
            });
        }
    }

}

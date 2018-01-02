package barinfo.navdev.barinfo.Adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import barinfo.navdev.barinfo.Clases.Tipo;
import barinfo.navdev.barinfo.R;

public class TipoAdapter extends RecyclerView.Adapter<TipoAdapter.ViewHolder>{

    List<Tipo> mTipos;

    Activity mActivity;


    public interface OnItemClick{
        void onClick(Tipo tipo);
    };

    OnItemClick monItemClick;

    public TipoAdapter(List<Tipo> tipos, Activity activity, OnItemClick onItemClick) {
        this.mTipos = tipos;
        this.mActivity = activity;
        this.monItemClick = onItemClick;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tipobar, parent, false);
        ViewHolder pvh = new ViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Tipo tipo = mTipos.get(position);

        holder.tipo = tipo;

        holder.nombre.setText(tipo.getNombre());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (monItemClick != null){
                    monItemClick.onClick(holder.tipo);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTipos.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre;
        View itemView;
        Tipo tipo;

        ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            nombre = (TextView)itemView.findViewById(R.id.textTipo);
        }
    }
}
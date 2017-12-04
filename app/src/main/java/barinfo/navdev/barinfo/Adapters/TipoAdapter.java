package barinfo.navdev.barinfo.Adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

import barinfo.navdev.barinfo.Clases.Tipo;
import barinfo.navdev.barinfo.R;

public class TipoAdapter extends RecyclerView.Adapter<TipoAdapter.ViewHolder>{

    List<Tipo> mTipos;

    Activity mActivity;

    ImageLoader imageLoader;

    public TipoAdapter(List<Tipo> tipos, Activity activity) {
        this.mTipos = tipos;
        this.mActivity = activity;

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mActivity).build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tipobar, parent, false);
        ViewHolder pvh = new ViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Tipo tipo = mTipos.get(position);


        holder.nombre.setText(tipo.getNombre());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // irAFicha(bar);
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

        ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            nombre = (TextView)itemView.findViewById(R.id.textTipo);
        }
    }
}
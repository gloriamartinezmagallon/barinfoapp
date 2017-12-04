package barinfo.navdev.barinfo.Adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

import barinfo.navdev.barinfo.Clases.Bar;
import barinfo.navdev.barinfo.R;


public class BarAdapter extends RecyclerView.Adapter<BarAdapter.ClienteViewHolder>{

    List<Bar> mBares;

    Activity mActivity;

    ImageLoader imageLoader;

    public interface OnItemClick{
        void onClick(Bar bar);
    };

    OnItemClick monItemClick;



    public BarAdapter(List<Bar> bares, Activity activity, OnItemClick onItemClick) {
        this.mBares = bares;
        this.mActivity = activity;

        this.monItemClick = onItemClick;

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mActivity)
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }

    @Override
    public ClienteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_baradapter, parent, false);
        ClienteViewHolder pvh = new ClienteViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(final ClienteViewHolder holder, int position) {

        final Bar bar = mBares.get(position);
        holder.setBar(bar);
        if (bar.getImgFicheroGN() != null && !bar.getImgFicheroGN().equalsIgnoreCase("")){

            imageLoader.loadImage(bar.getImgFicheroGN(), new SimpleImageLoadingListener(){
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    BitmapDrawable ob = new BitmapDrawable(mActivity.getResources(), loadedImage);
                    holder.photo.setBackground(ob);
                }
            });
        }

        holder.nombre.setText(bar.getNombre());

        if(bar.getDistance() == 0){
            holder.distancia.setVisibility(View.GONE);
        }else{
            holder.distancia.setVisibility(View.VISIBLE);
            holder.distancia.setText(bar.getDistanceWithFormat());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (monItemClick != null){
                   monItemClick.onClick(holder.bar);
               }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBares.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class ClienteViewHolder extends RecyclerView.ViewHolder {
        ImageView photo;
        TextView nombre;
        TextView distancia;
        View itemView;

        Bar bar;

        void setBar(Bar bar){
            this.bar = bar;
        }

        ClienteViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;

            photo = (ImageView) itemView.findViewById(R.id.photo);
            nombre = (TextView)itemView.findViewById(R.id.nombre);
            distancia = (TextView)itemView.findViewById(R.id.distancia);
        }
    }
}

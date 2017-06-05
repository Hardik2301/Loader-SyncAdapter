package com.example.imac.loaderexample.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.imac.loaderexample.localDB.DBItem;
import com.example.imac.loaderexample.R;

import java.util.List;

/**
 * Created by imac on 5/23/17.
 */

public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.ViewHolder> {

    private Context mContext;
    private List<DBItem> mList;
    private OnItemClickListener onItemClickListener;

    public RecyclerviewAdapter(Context mContext, List<DBItem> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        DBItem item=mList.get(position);
        holder.tv_id.setText("ID : "+item.getId());
        holder.tv_name.setText(item.getName());
        holder.tv_city.setText(item.getCity());
        holder.tv_country.setText(item.getCountry());
        holder.tv_mno.setText(item.getMobileNo()+"");
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void UpdateList(List<DBItem> mlist){
        this.mList = mlist;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_id,tv_name,tv_city,tv_country,tv_mno;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_id=(TextView)itemView.findViewById(R.id.tv_item_id);
            tv_name=(TextView)itemView.findViewById(R.id.tv_item_name);
            tv_city=(TextView)itemView.findViewById(R.id.tv_item_city);
            tv_country=(TextView)itemView.findViewById(R.id.tv_item_country);
            tv_mno=(TextView)itemView.findViewById(R.id.tv_item_mono);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener != null){
                        onItemClickListener.onItemClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}

package com.example.imac.loaderexample.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.imac.loaderexample.localDB.SqliteDB;
import com.example.imac.loaderexample.R;

/**
 * Created by imac on 5/23/17.
 */

public class RecyclerviewCursorAdapter extends RecyclerView.Adapter<RecyclerviewCursorAdapter.ViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    private OnItemClickListener onItemClickListener;

    public RecyclerviewCursorAdapter(Context mContext, Cursor mCursor) {
        this.mContext = mContext;
        this.mCursor = mCursor;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.tv_id.setText("ID : "+mCursor.getInt(mCursor.getColumnIndex(SqliteDB.TB_Id)));
        holder.tv_name.setText(mCursor.getString(mCursor.getColumnIndex(SqliteDB.TB_Name)));
        holder.tv_city.setText(mCursor.getString(mCursor.getColumnIndex(SqliteDB.TB_City)));
        holder.tv_country.setText(mCursor.getString(mCursor.getColumnIndex(SqliteDB.TB_Country)));
        holder.tv_mno.setText(mCursor.getInt(mCursor.getColumnIndex(SqliteDB.TB_Mno))+"");

    }

    @Override
    public int getItemCount() {
        if(mCursor != null)
            return mCursor.getCount();
        else
            return 0;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
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

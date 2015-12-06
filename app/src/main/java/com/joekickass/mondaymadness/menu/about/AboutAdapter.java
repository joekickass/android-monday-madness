package com.joekickass.mondaymadness.menu.about;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import java.util.List;

/**
 * About menu adapter
 */
public class AboutAdapter extends RecyclerView.Adapter<AboutAdapter.ViewHolder> {

    private List<AboutItem> mData;

    public AboutAdapter(List<AboutItem> data) {
        mData = data;
    }

    @Override
    public AboutAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(parent.getContext());
        TwoLineListItem v = (TwoLineListItem) li.inflate(android.R.layout.simple_list_item_2,
                parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mText1.setText(mData.get(position).title);
        holder.mText2.setText(mData.get(position).text);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mText1;
        TextView mText2;
        public ViewHolder(TwoLineListItem v) {
            super(v);
            mText1 = (TextView) v.findViewById(android.R.id.text1);
            mText2 = (TextView) v.findViewById(android.R.id.text2);
        }
    }
}

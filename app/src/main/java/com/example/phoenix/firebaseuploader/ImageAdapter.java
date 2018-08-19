package com.example.phoenix.firebaseuploader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

public class ImageAdapter extends BaseAdapter {

    @Override
    public int getCount() {
        return MainActivity.arrayList.size();
    }

    @Override
    public ImageDetails getItem(int i) {
        return MainActivity.arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        NotesViewHolder holder;
        if (view == null) {
            LayoutInflater li = (LayoutInflater) viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = li.inflate(R.layout.item_note, viewGroup, false);
            holder = new NotesViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (NotesViewHolder) view.getTag();
        }
        ImageDetails imageDetails = getItem(i);
        holder.textView.setText(imageDetails.getName());
        String url = imageDetails.getUrl();
        Picasso.get().load(url).into(holder.imageView);
        return view;
    }

    class NotesViewHolder {
        TextView textView;
        ImageView imageView;
        NotesViewHolder(View view) {
            imageView =  view.findViewById(R.id.imageView);
            textView = view.findViewById(R.id.textView);
        }
    }
}
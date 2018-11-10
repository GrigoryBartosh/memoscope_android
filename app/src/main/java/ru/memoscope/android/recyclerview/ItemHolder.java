package ru.memoscope.android.recyclerview;

import ru.memoscope.android.R;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ru.memoscope.android.utils.CircleTransform;

public class ItemHolder extends RecyclerView.ViewHolder {

    private final ImageView iconView;
    private final TextView nameView;
    private final TextView timeView;
    private final TextView textView;
    private final ImageView imageView;

    public ItemHolder(@NonNull View itemView) {
        super(itemView);

        iconView = itemView.findViewById(R.id.icon_view);
        nameView = itemView.findViewById(R.id.name_view);
        timeView = itemView.findViewById(R.id.time_view);
        textView = itemView.findViewById(R.id.text_view);
        imageView = itemView.findViewById(R.id.image_view);
    }

    public void setTextViewText(String text) {
        textView.setText(text);
    }

    public void setTimeViewText(String time) {
        timeView.setText(time);
    }

    public void setNameViewText(String name) {
        nameView.setText(name);
    }

    public void setImageViewPictrue(String url) {
        Picasso.get()
                .load(url)
                .into(imageView);
    }

    public void setIconViewPicture(String url) {
        Picasso.get()
                .load(url)
                .transform(new CircleTransform())
                .into(iconView);
    }
}

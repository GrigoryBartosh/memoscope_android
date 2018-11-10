package ru.memoscope.android.recyclerview;

import ru.memoscope.android.R;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ru.memoscope.android.utils.CircleTransform;

public class ItemHolder extends RecyclerView.ViewHolder {
    private List<String> urls;
    private int current = 0;
    private final ImageView iconView;
    private final TextView nameView;
    private final TextView timeView;
    private final TextView textView;
    private final ImageView imageView;
    //private final Button prevButton;
    //private final Button nextButton;

    public ItemHolder(@NonNull View itemView) {
        super(itemView);
        iconView = itemView.findViewById(R.id.icon_view);
        nameView = itemView.findViewById(R.id.name_view);
        timeView = itemView.findViewById(R.id.time_view);
        textView = itemView.findViewById(R.id.text_view);
        imageView = itemView.findViewById(R.id.image_view);
        /*prevButton = itemView.findViewById(R.id.prev_button);
        nextButton = itemView.findViewById(R.id.next_button);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPrevPicture();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNextPicture();
            }
        });
        */
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

    public void setUrls(List<String> urls) {
        this.urls = urls;
        current = 0;
        setImageViewPicture();
    }

    public void setNextPicture() {
        if (current + 1 < urls.size())
            current++;
        setImageViewPicture();
    }

    public void setPrevPicture() {
        if (current - 1 >= 0)
            current--;
        setImageViewPicture();
    }

    public void setImageViewPicture() {
        if (urls.size() > 0) {
            Picasso.get()
                    .load(urls.get(current))
                    .into(imageView);
        }
    }

    public void setIconViewPicture(String url) {
        Picasso.get()
                .load(url)
                .transform(new CircleTransform())
                .into(iconView);
    }
}

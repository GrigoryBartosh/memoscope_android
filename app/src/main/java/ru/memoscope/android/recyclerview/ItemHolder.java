package ru.memoscope.android.recyclerview;

import ru.memoscope.android.Pub;
import ru.memoscope.android.R;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import ru.memoscope.android.utils.CircleTransform;
import ru.memoscope.android.utils.Utils;

import static ru.memoscope.android.utils.Utils.openLink;
import static ru.memoscope.android.utils.Utils.shareLink;

public class ItemHolder extends RecyclerView.ViewHolder {
    private List<String> urls;
    private int current = 0;
    private final ImageView iconView;
    private final TextView nameView;
    private final TextView timeView;
    private final TextView textView;
    private final ImageView imageView;
    private final Button prevButton;
    private final Button nextButton;
    private final Button shareButton;
    private final ImageButton openVKButton;
    private final ToggleButton likeButton;
    private Context context;

    public ItemHolder(@NonNull View itemView, Context context) {
        super(itemView);
        this.context = context;
        iconView = itemView.findViewById(R.id.icon_view);
        nameView = itemView.findViewById(R.id.name_view);
        timeView = itemView.findViewById(R.id.time_view);
        textView = itemView.findViewById(R.id.text_view);
        imageView = itemView.findViewById(R.id.image_view);
        prevButton = itemView.findViewById(R.id.prev_button);
        nextButton = itemView.findViewById(R.id.next_button);
        shareButton = itemView.findViewById(R.id.share_button);
        openVKButton = itemView.findViewById(R.id.open_vk_button);
        likeButton = itemView.findViewById(R.id.like_button);
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
        if (urls == null || urls.size() <= 1) {
            nextButton.setVisibility(View.GONE);
            prevButton.setVisibility(View.GONE);
        } else {
            for (int i = 0; i < urls.size(); i++) {
                Picasso.get()
                        .load(urls.get(i))
                        .fetch();
            }
        }

        current = 0;
        setImageViewPicture();
    }

    public void setNextPicture() {
        if (urls == null || urls.size() == 1)
            return;
        if (current + 1 < urls.size())
            current++;
        setImageViewPicture();
    }

    public void setPrevPicture() {
        if (urls == null || urls.size() == 1)
            return;
        if (current - 1 >= 0)
            current--;
        setImageViewPicture();
    }

    public void setImageViewPicture() {
        if (urls.size() == 0)
            return;

        prevButton.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.VISIBLE);
        if (current == 0) {
            prevButton.setVisibility(View.GONE);
        }
        if (current == urls.size() - 1) {
            nextButton.setVisibility(View.GONE);
        }

        Picasso.get()
                .load(urls.get(current))
                .placeholder(R.drawable.load_bg)
                .into(imageView);

    }

    public void setVKPostId(int groupId, int postId) {
        final String url = "http://vk.com/wall" + groupId + "_" + postId;
        openVKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink((Activity) context, url);
            }
        });
        final VKParameters parameters = VKParameters.from("type", "post", VKApiConst.OWNER_ID, groupId, "item_id", postId);
        new VKRequest("likes.isLiked", parameters).executeSyncWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                try {
                    int liked = response.json
                            .getJSONObject("response")
                            .getInt("liked");
                    likeButton.setChecked(liked == 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VKRequest request;
                if (likeButton.isChecked()) {
                    request = new VKRequest("likes.add", parameters);
                } else {
                    request = new VKRequest("likes.delete", parameters);
                }
                request.executeWithListener(new VKRequest.VKRequestListener() {});
            }
        });
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareLink((Activity) context, url);
            }
        });
    }

    public void setIconViewPicture(String url) {
        Picasso.get()
                .load(url)
                .transform(new CircleTransform())
                .into(iconView);
    }
}

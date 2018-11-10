package ru.memoscope.android.recyclerview;

import android.content.Context;
import ru.memoscope.android.R;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static ru.memoscope.android.utils.Utils.formatTime;
import static ru.memoscope.android.utils.Utils.getBestQualityURL;

public class CustomAdapter extends RecyclerView.Adapter<ItemHolder> {

    private LayoutInflater inflater;
    private List<JSONObject> posts;
    private SparseArray<JSONObject> groups;

    public CustomAdapter(Context context, List<JSONObject> posts, SparseArray<JSONObject> groups) {
        inflater = LayoutInflater.from(context);
        this.posts = posts;
        this.groups = groups;
    }

    public void update(List<JSONObject> posts, SparseArray<JSONObject> groups) {
        this.posts = posts;
        this.groups = groups;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        JSONObject post = posts.get(position);
        try {
            String text = post.getString("text");
            holder.setTextViewText(text);
        } catch (JSONException e) {
            Log.e("CustomAdapter", "post has no text");
        }

        try {
            int owner_id = post.getInt("owner_id");
            JSONObject group = groups.get(owner_id);

            String iconUrl = getBestQualityURL(group);
            holder.setIconViewPicture(iconUrl);

            String name = group.getString("name");
            holder.setNameViewText(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            String time = formatTime(post.getLong("date"));
            holder.setTimeViewText(time);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (post.has("attachments")) {
            try {
                List<String> urls = new ArrayList<>();
                JSONArray attachments = post.getJSONArray("attachments");
                for (int i = 0; i < attachments.length(); i++) {
                    JSONObject attachment = attachments.getJSONObject(i);
                    if (attachment.has("photo")) {
                        String url = getBestQualityURL(attachment.getJSONObject("photo"));
                        Log.d("CustomAdapter", url);
                        urls.add(url);
                    }
                }
                holder.setUrls(urls);
                holder.setImageViewPicture();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}

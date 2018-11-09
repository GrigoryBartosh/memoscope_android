package android.memoscope.ru.memoscope;

import android.content.Context;
import android.memoscope.ru.memoscope.utils.CircleTransform;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class CustomAdapter extends BaseAdapter {

    private ArrayList<JSONObject> posts = new ArrayList<>();
    private SparseArray<JSONObject> groups = new SparseArray<>();
    private LayoutInflater inflater;

    CustomAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void add(JSONObject post) {
        posts.add(post);
    }

    public void addGroup(JSONObject group) {
        try {
            int id = -group.getInt("id");
            groups.put(id, group);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            rowView = inflater.inflate(R.layout.list_item, parent, false);
        }

        JSONObject post = posts.get(position);

        try {
            int owner_id = post.getInt("owner_id");
            JSONObject group = groups.get(owner_id);

            ImageView iconView = rowView.findViewById(R.id.icon_view);
            String iconUrl = getBestQualityURL(group);
            Picasso.get()
                    .load(iconUrl)
                    .transform(new CircleTransform())
                    .into(iconView);

            TextView nameView = rowView.findViewById(R.id.name_view);
            String name = group.getString("name");
            nameView.setText(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            TextView timeView = rowView.findViewById(R.id.time_view);
            String time = formatTime(post.getLong("date"));
            timeView.setText(time);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView textView = rowView.findViewById(R.id.text_view);
        try {
            textView.setText(post.getString("text"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (post.has("attachments")) {
            try {
                JSONObject attachment = post.getJSONArray("attachments").getJSONObject(0);
                Log.d("CustomAdapter", attachment.toString());
                if (attachment.has("photo")) {
                    ImageView imageView = rowView.findViewById(R.id.image_view);
                    String url = getBestQualityURL(attachment.getJSONObject("photo"));
                    Log.d("CustomAdapter", url);
                    Picasso.get()
                            .load(url)
                            .into(imageView);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        return rowView;
    }

    private String getBestQualityURL(JSONObject photo) throws JSONException {
        int max = -1;
        String url = "";
        for(Iterator<String> iterator = photo.keys(); iterator.hasNext(); ) {
            String key = iterator.next();
            if (key.matches("photo_.*")) {
                int size = Integer.parseInt(key.substring(6));
                if (size > max) {
                    max = size;
                    url = photo.getString(key);
                }
            }
        }
        return url;
    }

    private String formatTime(long time) {
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
        return format.format(new Date(time * 1000));
    }

    public void clear() {
        posts.clear();
    }
}


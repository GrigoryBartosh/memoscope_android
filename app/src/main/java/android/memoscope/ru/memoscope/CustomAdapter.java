package android.memoscope.ru.memoscope;

import android.content.Context;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.Iterator;

public class CustomAdapter extends BaseAdapter {

    private ArrayList<JSONObject> posts = new ArrayList<>();
    private LayoutInflater inflater;

    CustomAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void add(JSONObject post) {
        posts.add(post);
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
        //Log.d("CustomAdapter", post.toString());

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
                    Picasso
                            .get()
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
}


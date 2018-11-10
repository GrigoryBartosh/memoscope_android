package android.memoscope.ru.memoscope;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.memoscope.ru.memoscope.utils.CircleTransform;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomPubsDialogFragment extends DialogFragment {
    private ArrayList<String> pubs;
    private ArrayList<String> supportedPubList;
    private Map<String, String> pubUrls = new HashMap<>();

    public CustomPubsDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static CustomPubsDialogFragment newInstance(String title) {
        CustomPubsDialogFragment frag = new CustomPubsDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pubs, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView listView = view.findViewById(R.id.pub_list);
        listView.setAdapter(new PubAdapter(getContext(), supportedPubList));
    }

    public void setPubs(ArrayList<String> pubList, ArrayList<String> supportedPubList, Map<String, String> urls) {
        pubs = pubList;
        this.pubUrls = urls;
        this.supportedPubList = supportedPubList;
    }

    public class PubAdapter extends ArrayAdapter<String> {
        public PubAdapter(Context context, ArrayList<String> users) {
            super(context, 0, users);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.pub_item, parent, false);
            CheckBox box = convertView.findViewById(R.id.box);
            String pubName = getItem(position);
            box.setChecked(pubs.contains(pubName));
            box.setText(pubName);

            final LayerDrawable ld = (LayerDrawable) getResources().getDrawable(R.drawable.checkbox_background);
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pain);
            Drawable customImage = new BitmapDrawable(getResources(), bitmap);
            Drawable drawable = (Drawable)ld.findDrawableByLayerId(R.id.pub_icon);
            //ld.setDrawableByLayerId(R.id.pub_icon, customImage);
            /*Picasso.get()
                    .load(pubUrls.get(pubName))
                    .transform(new CircleTransform())
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            Drawable customImage = new BitmapDrawable(getResources(), bitmap);
                            ld.setDrawableByLayerId(R.id.pub_icon, customImage);                            }
                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                        }
                    });
            */
            return convertView;
        }
    }
}

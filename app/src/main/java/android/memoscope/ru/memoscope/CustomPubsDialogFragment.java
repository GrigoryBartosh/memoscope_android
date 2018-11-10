package android.memoscope.ru.memoscope;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.memoscope.ru.memoscope.utils.CircleTransform;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.squareup.picasso.Picasso;

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
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);

        ListView listView = view.findViewById(R.id.pub_list);
        ViewGroup.LayoutParams lp = listView.getLayoutParams();
        lp.height = metrics.heightPixels * 7 / 10;
        listView.setLayoutParams(lp);
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
            
            ImageView img = convertView.findViewById(R.id.pub_img);
            Picasso.get()
                    .load(pubUrls.get(pubName))
                    .transform(new CircleTransform())
                    .into(img);

            return convertView;
        }
    }
}

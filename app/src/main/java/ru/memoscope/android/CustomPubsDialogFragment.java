package ru.memoscope.android;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import java.util.Set;

import ru.memoscope.android.utils.CircleTransform;

public class CustomPubsDialogFragment extends DialogFragment {
    private Set<Integer> pubs;
    private ArrayList<Pub> supportedPubs;

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
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);

        ListView listView = view.findViewById(R.id.pub_list);
        ViewGroup.LayoutParams lp = listView.getLayoutParams();
        lp.height = metrics.heightPixels * 7 / 10;
        listView.setLayoutParams(lp);
        listView.setAdapter(new PubAdapter(getContext(), supportedPubs));
    }

    public void setPubs(Set<Integer> pubList, ArrayList<Pub> supportedPubs) {
        pubs = pubList;
        this.supportedPubs = supportedPubs;
    }

    public class PubAdapter extends ArrayAdapter<Pub> {
        public PubAdapter(Context context, ArrayList<Pub> users) {
            super(context, 0, users);
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (rowView == null) {
                rowView = LayoutInflater.from(getContext()).inflate(R.layout.pub_item, parent, false);
            }
            CheckBox box = rowView.findViewById(R.id.box);
            final Pub pub = getItem(position);

            box.setChecked(pubs.contains(pub.getId()));
            box.setText(pub.getName());

            box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        pubs.add(pub.getId());
                    } else {
                        pubs.remove(pub.getId());
                    }
                }
            });

            ImageView img = rowView.findViewById(R.id.pub_img);
            Picasso.get()
                    .load(pub.getImageURL())
                    .transform(new CircleTransform())
                    .into(img);

            return rowView;
        }
    }
}

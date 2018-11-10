package android.memoscope.ru.memoscope;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import java.util.List;

public class CustomPubsDialogFragment extends DialogFragment {
    private List<String> pubs;
    private List<String> supportedPubList;

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
        // Get field from view
    }

    public void setPubs(List<String> pubList, List<String> supportedPubList) {
        pubs = pubList;
        this.supportedPubList = supportedPubList;
    }
}

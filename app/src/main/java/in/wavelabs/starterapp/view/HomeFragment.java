package in.wavelabs.starterapp.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.Validator;

import in.wavelabs.starterapp.R;
import in.wavelabs.starterapp.util.Prefrences;

/**
 * Created by vivekkiran on 7/5/16.
 */

public class HomeFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        final Validator validator = new Validator(getActivity());
//        validator.setValidationListener(getActivity());

        View v = inflater.inflate(R.layout.home_fragment, container, false);
        TextView welcome = (TextView) v.findViewById(R.id.welcomeTxt);
        welcome.setText("Welcome " + Prefrences.getFirstName(getActivity()) + "!");
        return v;
    }
}

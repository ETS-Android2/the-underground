package com.example.finalproject;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public void SaveData(String User, String Password){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("login details",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("User",User);
        editor.putString("Password",Password);
        editor.commit();

    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        Button submitB = view.findViewById(R.id.Submit);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation);
        navBar.setVisibility(View.INVISIBLE);


        final Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.bounch);

        submitB.setOnClickListener(v -> {
            EditText idTxt = getView().findViewById(R.id.Userid);
            String id = idTxt.getText().toString();
            EditText PasswordTxt = getView().findViewById(R.id.password);
            String Password = PasswordTxt.getText().toString();
            submitB.startAnimation(animation);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                Toast toast = null;

                @Override
                public void run() {
                    MainActivity mainActivity = new MainActivity();
                    String name = mainActivity.CanConnect(id,Password);
                    System.out.println(name);
                    if(!name.equals("")){
                        toast = Toast.makeText(getActivity(),"Welcome, " + name + "!", Toast.LENGTH_SHORT);
                        SaveData(name,Password);
                        openFragment(HomeFragment.newInstance("", ""));
                        navBar.setVisibility(View.VISIBLE);
                    }
                    else {
                        toast = Toast.makeText(getActivity(),"Wrong details!", Toast.LENGTH_SHORT);
                    }
                    toast.setGravity(Gravity.BOTTOM| Gravity.CENTER_HORIZONTAL, 0, 150);
                    toast.show();
                }
            },600);

        });

        return view;
    }
}
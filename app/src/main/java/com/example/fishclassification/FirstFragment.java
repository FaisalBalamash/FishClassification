package com.example.fishclassification;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class FirstFragment extends Fragment {

    public FirstFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        Button loginButton = view.findViewById(R.id.loginButton);
        Button signUpButton = view.findViewById(R.id.signUpButton);
        ((MainActivity) getActivity()).hideBottomNavigation();

        loginButton.setOnClickListener(v -> navigateTo(new LoginFragment()));
        signUpButton.setOnClickListener(v -> navigateTo(new RegisterFragment()));

        return view;
    }

    private void navigateTo(Fragment fragment) {
        ((MainActivity) getActivity()).showBottomNavigation();

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}

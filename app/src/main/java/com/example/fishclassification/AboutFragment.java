package com.example.fishclassification;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.cardview.widget.CardView;

public class AboutFragment extends Fragment {

    private CardView cardSummary;
    private TextView textSummary;

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        initializeViews(view);
        textSummary.setText(getTextSummary());
        Button signOutButton = view.findViewById(R.id.signOutButton);
        signOutButton.setOnClickListener(v -> ((MainActivity) getActivity()).signOut());
        return view;
    }

    private void initializeViews(View view) {
        cardSummary = view.findViewById(R.id.card_summary);
        textSummary = view.findViewById(R.id.text_summary);
    }

    private String getTextSummary() {
        return "This app is a tool for identifying fish species using machine learning. " +
                "With the DetectorFragment, users can take a photo or upload an image from their gallery, " +
                "and the app will classify the fish species using a pre-trained TensorFlow Lite model. " +
                "The SummaryFragment provides a textual summary of the identified species, including " +
                "details and characteristics. The app aims to make species identification accessible " +
                "and informative for everyone, from fishing enthusiasts to marine biologists.";
    }
}

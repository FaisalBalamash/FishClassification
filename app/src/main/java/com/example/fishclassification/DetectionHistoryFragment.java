package com.example.fishclassification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DetectionHistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private DetectionHistoryAdapter adapter;
    private List<DetectionHistoryItem> itemList = new ArrayList<>();

    // onCreateView is called to draw the UI of the fragment
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detection_history, container, false);

        // Setup the RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DetectionHistoryAdapter(getContext(), itemList);
        recyclerView.setAdapter(adapter);

        // Fetch the history data from Firebase
        fetchHistoryData();

        return view;
    }

    // Fetches historical data from Firebase
    private void fetchHistoryData() {
        // Get the current user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // Construct the URL for the user's data
            String userId = currentUser.getUid();
            String url = "https://cpit498-51677-default-rtdb.europe-west1.firebasedatabase.app/" + userId + ".json";

            // Create a request queue and a JSON object request
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        // Parse the data and populate the RecyclerView
                        parseDataAndPopulate(response);
                    },
                    error -> {
                        // Handle error
                    });

            // Add the request to the queue
            requestQueue.add(jsonObjectRequest);
        }
    }

    // Parses the JSON response and populates the RecyclerView
    private void parseDataAndPopulate(JSONObject response) {
        try {
            itemList.clear();
            Iterator<String> keys = response.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject itemJson = response.getJSONObject(key);
                String currentDate = itemJson.getString("currentDate");
                String label = itemJson.getString("label");
                DetectionHistoryItem item = new DetectionHistoryItem(currentDate, label);
                itemList.add(item);
            }
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            // Handle exception
        }
    }
}

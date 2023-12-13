package com.example.fishclassification;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.fishclassification.ml.FishSpeciesInceptionV3;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetectorFragment extends Fragment {
    private static final int SELECT_PICTURE = 200;
    private static final int CAPTURE_IMAGE = 1;
    private static final int PERMISSION_CODE_STORAGE = 102;
    private static final int PERMISSION_CODE_CAMERA = 101;
    private static final int MODEL_IMAGE_HEIGHT = 299;
    private static final int MODEL_IMAGE_WIDTH = 299;
    private static final int MODEL_NUM_CHANNELS = 3;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
    private TextView result, confidence;
    private ImageView imageView;
    private Button picture, upload;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detector, container, false);
        bindViews(view);
        setClickListeners();
        return view;
    }

    private void bindViews(View view) {
        result = view.findViewById(R.id.result);
        confidence = view.findViewById(R.id.confidence);
        imageView = view.findViewById(R.id.imageView);
        picture = view.findViewById(R.id.button);
        upload = view.findViewById(R.id.uploadButton);
        Button signOutButton = view.findViewById(R.id.signOutButton);
        signOutButton.setOnClickListener(v -> ((MainActivity) getActivity()).signOut());
    }

    private void setClickListeners() {
        picture.setOnClickListener(v -> requestPermission(Manifest.permission.CAMERA, PERMISSION_CODE_CAMERA));
        upload.setOnClickListener(v -> requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, PERMISSION_CODE_STORAGE));
    }

    private void requestPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_GRANTED) {
            performActionBasedOnPermission(requestCode);
        } else {
            requestPermissions(new String[]{permission}, requestCode);
        }
    }

    private void performActionBasedOnPermission(int requestCode) {
        if (requestCode == PERMISSION_CODE_CAMERA) {
            openCamera();
        } else if (requestCode == PERMISSION_CODE_STORAGE) {
            openGallery();
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, SELECT_PICTURE);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAPTURE_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            handleImage(requestCode, data);
        }
    }

    private void handleImage(int requestCode, Intent data) {
        try {
            Bitmap image = extractBitmapFromIntentData(requestCode, data);
            if (image != null) {
                displayAndClassifyImage(image);
            }
        } catch (IOException e) {
            Log.e("DetectorFragment", "Error handling image", e);
        }
    }

    private Bitmap extractBitmapFromIntentData(int requestCode, Intent data) throws IOException {
        if (requestCode == CAPTURE_IMAGE) {
            Bundle extras = data.getExtras();
            return (Bitmap) extras.get("data");
        } else if (requestCode == SELECT_PICTURE) {
            Uri selectedImageUri = data.getData();
            return BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(selectedImageUri));
        }
        return null;
    }

    private void displayAndClassifyImage(Bitmap image) {
        Bitmap thumbnail = ThumbnailUtils.extractThumbnail(image, Math.min(image.getWidth(), image.getHeight()), Math.min(image.getWidth(), image.getHeight()));
        imageView.setImageBitmap(thumbnail);
        Bitmap resizedImage = Bitmap.createScaledBitmap(thumbnail, MODEL_IMAGE_WIDTH, MODEL_IMAGE_HEIGHT, false);
        classifyImage(resizedImage, userId);
    }

    private void classifyImage(Bitmap image, String userId) {
        FishSpeciesInceptionV3 model = null;
        try {
            model = FishSpeciesInceptionV3.newInstance(getContext());
            ByteBuffer byteBuffer = convertBitmapToByteBuffer(image);
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, MODEL_IMAGE_HEIGHT, MODEL_IMAGE_WIDTH, MODEL_NUM_CHANNELS}, DataType.FLOAT32);
            inputFeature0.loadBuffer(byteBuffer);

            FishSpeciesInceptionV3.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            List<String> labels = loadLabels();
            int maxPos = getMaxResult(outputFeature0.getFloatArray());
            String label = labels.get(maxPos);

            result.setText(label);

            postClassificationResultToFirebase(new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(new Date()), label, userId);
        } catch (IOException e) {
            Log.e("DetectorFragment", "Error in image classification", e);
        }
    }


    /**
     * Posts the classification result to Firebase.
     *
     * @param currentDate The current date and time of the classification.
     * @param label       The label of the classified image.
     * @param userId      The unique ID of the user.
     */
    private void postClassificationResultToFirebase(String currentDate, String label, String userId) {
        try {
            // Creating a new JSON object to hold the data
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("currentDate", currentDate); // Adds the current date to the JSON object
            jsonObject.put("label", label);             // Adds the label to the JSON object

            // Constructing the URL for Firebase. The data is stored under the user's ID
            String firebaseUrl = "https://cpit498-51677-default-rtdb.europe-west1.firebasedatabase.app/";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    firebaseUrl + userId + ".json",
                    jsonObject,
                    response -> {
                        // This block is executed if the request was successful.
                        // Logging success message
                        Log.d("Firebase", "Data saved successfully");
                    },
                    error -> {
                        // This block is executed if there was an error in the request.
                        // Logging error message
                        Log.e("Firebase", "Error saving data", error);
                    }
            );

            // Creating a RequestQueue and adding the JSON object request to it
            RequestQueue queue = Volley.newRequestQueue(getContext());
            queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            // This block catches JSON exceptions
            // Logging exception message
            Log.e("DetectorFragment", "Error creating JSON object", e);
        }
    }



    private ByteBuffer convertBitmapToByteBuffer(Bitmap image) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * MODEL_IMAGE_HEIGHT * MODEL_IMAGE_WIDTH * MODEL_NUM_CHANNELS);
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[MODEL_IMAGE_HEIGHT * MODEL_IMAGE_WIDTH];
        image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
        for (int value : intValues) {
            byteBuffer.putFloat(((value >> 16) & 0xFF) * (1.f / 255.f));
            byteBuffer.putFloat(((value >> 8) & 0xFF) * (1.f / 255.f));
            byteBuffer.putFloat((value & 0xFF) * (1.f / 255.f));
        }
        return byteBuffer;
    }

    private List<String> loadLabels() {
        List<String> labels = new ArrayList<>();
        try (InputStream is = getContext().getAssets().open("labels.txt");
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                labels.add(line);
            }
        } catch (IOException e) {
            // Handle exceptions
        }
        return labels;
    }

    private int getMaxResult(float[] confidences) {
        int maxPos = 0;
        float maxConfidence = 0;
        for (int i = 0; i < confidences.length; i++) {
            if (confidences[i] > maxConfidence) {
                maxConfidence = confidences[i];
                maxPos = i;
            }
        }
        return maxPos;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            performActionBasedOnPermission(requestCode);
        }
    }
}


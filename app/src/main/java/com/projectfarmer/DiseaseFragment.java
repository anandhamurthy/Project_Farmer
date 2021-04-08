package com.projectfarmer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.projectfarmer.Models.Crops;
import com.projectfarmer.Models.Fertilizer;
import com.projectfarmer.Models.Users;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

public class DiseaseFragment extends Fragment {

    private TextView Name;
    private ImageView Image;
    private Button Upload, Predict;

    private Uri mImageUri;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String Crop_Key, Crop_model;

    private CardView Disease_Details;

    private ProgressDialog mProgressDialog ;

    public DiseaseFragment() {
    }

    public DiseaseFragment(String crop_key, String crop_model) {
        Crop_Key=crop_key;
        Crop_model = crop_model;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_disease, container, false);

        mProgressDialog = new ProgressDialog(getContext());

        Image = view.findViewById(R.id.disease_image);
        Name = view.findViewById(R.id.disease_name);
        Disease_Details = view.findViewById(R.id.disease_details);
        Upload = view.findViewById(R.id.upload);
        Predict = view.findViewById(R.id.predict);

        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .start(getContext(), DiseaseFragment.this);
            }
        });

        Predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PredictImage(Crop_model);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {


            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();

            Disease_Details.setVisibility(View.VISIBLE);
            Image.setVisibility(View.VISIBLE);
            Predict.setVisibility(View.VISIBLE);

            Image.setImageURI(mImageUri);

        } else {
            Toast.makeText(getContext(), "Something gone wrong!", Toast.LENGTH_SHORT).show();
        }
    }

    private void PredictImage(String url) {

        mProgressDialog.setTitle("Predict");
        mProgressDialog.setMessage("predicting Disease..");

        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            // Read BitMap by file path.
            Bitmap bitmap = BitmapFactory.decodeFile(mImageUri.getPath(), options);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        }catch(Exception e){
            Toast.makeText(getContext(), "Please Make Sure the Selected File is an Image.", Toast.LENGTH_SHORT).show();
            return;
        }
        byte[] byteArray = stream.toByteArray();

        multipartBodyBuilder.addFormDataPart("image", "Android_Flask.jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray));

        RequestBody postBodyImage = multipartBodyBuilder.build();

        postRequest(url, postBodyImage);
    }

    void postRequest(String postUrl, RequestBody postBody) {

        mProgressDialog.setTitle("Predict");
        mProgressDialog.setMessage("predicting Disease..");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                Log.d("FAIL", e.getMessage());

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "Failed to Connect to Server. Please Try Again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            JSONObject json = new JSONObject(response.body().string());
                            Log.i("re", json.toString());
                            String disease_name = json.getString("name");

                            Disease_Details.setVisibility(View.VISIBLE);
                            Image.setVisibility(View.VISIBLE);
                            Name.setVisibility(View.VISIBLE);
                            Predict.setVisibility(View.GONE);

                            Image.setImageURI(mImageUri);
                            Name.setText(disease_name);
                            mProgressDialog.dismiss();

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}
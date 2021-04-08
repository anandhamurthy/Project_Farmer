package com.projectfarmer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.projectfarmer.Adapters.FertilizerAdapter;
import com.projectfarmer.Adapters.MultiDaysAdapter;
import com.projectfarmer.Models.Fertilizer;
import com.projectfarmer.Models.MultiDays;
import com.projectfarmer.Models.common.Temp;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class NewCropActivity extends AppCompatActivity {

    private Button Upload;

    private Uri mImageUri;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    String postUrl = "https://ph-predict-api.herokuapp.com/";

    private ProgressDialog mProgressDialog;

    private StorageReference mLandImageStorage;
    private StorageTask mUploadTask;

    private ImageView Image;
    private TextView PH_Value, Description, Land_Title, Suggest_Title;
    private RecyclerView Fertilizer_List;
    private CardView Land_Details;

    private FertilizerAdapter fertilizerAdapter;
    List<Fertilizer> fertilizerList;
    LinearLayoutManager layoutManager;

    private FloatingActionButton Save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_crop);

        mProgressDialog = new ProgressDialog(this);

        Upload = findViewById(R.id.upload);

        mLandImageStorage = FirebaseStorage.getInstance().getReference("Land_Images");

        Fertilizer_List = findViewById(R.id.next_weather_list);

        Image = findViewById(R.id.image);
        PH_Value = findViewById(R.id.ph_value);
        Description = findViewById(R.id.desc);
        Fertilizer_List = findViewById(R.id.fertilizer_list);
        Save = findViewById(R.id.save);
        Land_Details = findViewById(R.id.fertilizer_details);
        Land_Title = findViewById(R.id.land_title);
        Suggest_Title = findViewById(R.id.suggest_title);


        layoutManager = new LinearLayoutManager(NewCropActivity.this);
        Fertilizer_List.setLayoutManager(layoutManager);
        Fertilizer_List.setHasFixedSize(false);
        fertilizerList = new ArrayList<>();
        fertilizerAdapter = new FertilizerAdapter(NewCropActivity.this, fertilizerList);
        Fertilizer_List.setAdapter(fertilizerAdapter);


        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .start(NewCropActivity.this);
            }
        });

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!PH_Value.getText().toString().isEmpty() && mImageUri!=null && !fertilizerList.isEmpty() && !Description.getText().toString().isEmpty())
                    UploadImage(PH_Value.getText().toString(), Description.getText().toString());
                else
                    Toast.makeText(NewCropActivity.this, "Upload Image & Predict First!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {


            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();

            PredictImage();

        } else {
            Toast.makeText(this, "Something gone wrong!", Toast.LENGTH_SHORT).show();
        }
    }

    private void PredictImage() {


        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            // Read BitMap by file path.
            Bitmap bitmap = BitmapFactory.decodeFile(mImageUri.getPath(), options);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        }catch(Exception e){
            Toast.makeText(this, "Please Make Sure the Selected File is an Image.", Toast.LENGTH_SHORT).show();
            return;
        }
        byte[] byteArray = stream.toByteArray();

        multipartBodyBuilder.addFormDataPart("image0", "Android_Flask.jpg", RequestBody.create(MediaType.parse("image/*jpg"), byteArray));

        RequestBody postBodyImage = multipartBodyBuilder.build();

        postRequest(postUrl, postBodyImage);
    }

    void postRequest(String postUrl, RequestBody postBody) {

        mProgressDialog.setTitle("Predict");
        mProgressDialog.setMessage("predicting pH Value..");
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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(NewCropActivity.this, "Failed to Connect to Server. Please Try Again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            fertilizerList.clear();

                            JSONObject json = new JSONObject(response.body().string());
                            Log.i("re", json.toString());
                            String ph_value = json.getString("ph_value");
                            String description = json.getString("desc");
                            JSONObject fertilizer = json.getJSONObject("fertilizer");
                            JSONArray list = fertilizer.getJSONArray("list");
                            for (int i=0;i<list.length();i++){

                                JSONObject object = list.getJSONObject(i);
                                String name = object.getString("name");
                                String image = object.getString("image");
                                String material = object.getString("material");
                                Fertilizer fertilizer1 = new Fertilizer();
                                fertilizer1.setImage(image);
                                fertilizer1.setMaterial(material);
                                fertilizer1.setName(name);

                                fertilizerList.add(fertilizer1);

                                Log.i("gr",fertilizer.toString());

                            }
                            Land_Title.setVisibility(View.VISIBLE);
                            Suggest_Title.setVisibility(View.VISIBLE);
                            Land_Details.setVisibility(View.VISIBLE);
                            Fertilizer_List.setVisibility(View.VISIBLE);
                            Save.setVisibility(View.VISIBLE);
                            Image.setImageURI(mImageUri);
                            String result = String.format("%.2f", Double.parseDouble(ph_value));
                            PH_Value.setText(result);
                            Description.setText(description);
                            fertilizerAdapter.notifyDataSetChanged();
                            mProgressDialog.dismiss();
                            //UploadImage(ph_value, description);
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

    private void UploadImage(String ph_value, String desc) {

        if (mImageUri != null) {

            mProgressDialog.setTitle("Save");
            mProgressDialog.setMessage("saving pH Value..");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();

            final StorageReference fileReference = mLandImageStorage.child(System.currentTimeMillis()
                    + ".jpg");

            mUploadTask = fileReference.putFile(mImageUri);
            mUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String miUrlOk = downloadUri.toString();

                        final String crop_key = db.collection("Crops").document().getId();
                        HashMap cropMap = new HashMap<>();
                        cropMap.put("crop_id", crop_key);
                        cropMap.put("level", 1);
                        cropMap.put("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        cropMap.put("ph_land_image", miUrlOk);
                        cropMap.put("ph_value", ph_value);
                        cropMap.put("ph_description", desc);
                        cropMap.put("fertilizers", fertilizerList);

                        cropMap.put("crop_name", "");
                        cropMap.put("crop_image", "");
                        cropMap.put("crop_temperature", "");
                        cropMap.put("crop_growth_period", "");
                        cropMap.put("crop_irrigation_pattern", "");
                        cropMap.put("crop_disease", "");
                        cropMap.put("crop_disease_model", "");

                        cropMap.put("crop_harvest_note", "");
                        cropMap.put("failure", false);



                        db.collection("Crops").document(crop_key).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        db.collection("Crops").document(crop_key).update(cropMap)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        mProgressDialog.dismiss();
                                                        Intent intent = new Intent(NewCropActivity.this, MainActivity.class);
                                                        startActivity(intent);
                                                        finish();

                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(NewCropActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                                        Log.d(TAG, e.toString());
                                                    }
                                                });
                                    }else{
                                        db.collection("Crops").document(crop_key).set(cropMap)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        mProgressDialog.dismiss();
                                                        Intent intent = new Intent(NewCropActivity.this, MainActivity.class);
                                                        startActivity(intent);
                                                        finish();

                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(NewCropActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                                        Log.d(TAG, e.toString());
                                                    }
                                                });
                                    }
                                }
                            }
                        });


                    } else {
                        Toast.makeText(NewCropActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(NewCropActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(this, "Upload a Image", Toast.LENGTH_SHORT).show();
        }
    }
}
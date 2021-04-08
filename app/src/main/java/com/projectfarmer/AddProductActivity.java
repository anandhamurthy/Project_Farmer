package com.projectfarmer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddProductActivity extends AppCompatActivity {

    private CircleImageView Image;
    private String mCurrentUserId;

    private FloatingActionButton Save;
    private TextInputEditText Name, Description, Tips, Price;
    private Button Upload;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ProgressDialog mProgressDialog;

    private Uri mImageUri;
    private StorageTask mUploadTask;
    private StorageReference mImageStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        Intent intent = getIntent();

        Name = findViewById(R.id.product_name);
        Image = findViewById(R.id.product_image);
        Description = findViewById(R.id.description);
        Tips = findViewById(R.id.tips);
        Price = findViewById(R.id.price);
        Upload = findViewById(R.id.upload);
        Save = findViewById(R.id.save);

        Name.setText(intent.getStringExtra("name"));

        mImageStorage = FirebaseStorage.getInstance().getReference("product_image");

        mCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .start(AddProductActivity.this);
            }
        });

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isEmpty(Name.getText().toString(),Description.getText().toString(),
                        Tips.getText().toString(),
                        Price.getText().toString())) {

                    if (mImageUri!=null){

                        UploadImage(Name.getText().toString(),Description.getText().toString(),
                                Tips.getText().toString(),
                                Price.getText().toString());
                    }else{
                        Toast.makeText(AddProductActivity.this, "Upload Image", Toast.LENGTH_SHORT).show();
                    }


                }else{
                    Toast.makeText(AddProductActivity.this, "Complete all details", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private boolean isEmpty(String name, String desc, String tip, String price) {
        if (name.isEmpty() || desc.isEmpty() || tip.isEmpty() || price.isEmpty() ) {
            Toast.makeText(AddProductActivity.this, "Complete All Details", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void UploadImage(String name, String desc, String tips, String price) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Save");
        pd.setMessage("Uploading");
        pd.show();
        if (mImageUri != null) {
            final StorageReference fileReference = mImageStorage.child(System.currentTimeMillis() + ".jpg");

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
                        pd.setMessage("Saving");
                        final String product_key = db.collection("Products").document().getId();
                        HashMap<String, Object> productMap = new HashMap<>();
                        productMap.put("image", "" + miUrlOk);
                        productMap.put("product_id", product_key);
                        productMap.put("user_id", mCurrentUserId);
                        productMap.put("description", desc);
                        productMap.put("name", name);
                        productMap.put("tips", tips);
                        productMap.put("price", price);
                        db.collection("Products").document(product_key).set(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(AddProductActivity.this, "Crop Added.", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });

                        pd.dismiss();

                    } else {
                        Toast.makeText(AddProductActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(AddProductActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {


            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();
            Image.setImageURI(mImageUri);

        } else {
            Toast.makeText(this, "Something gone wrong!", Toast.LENGTH_SHORT).show();
        }
    }
}
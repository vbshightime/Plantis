package com.example.webczar.plantis;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.webczar.plantis.Permissions.PackageManagerUtils;
import com.example.webczar.plantis.Permissions.PermissionUtils;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequest;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    //ToDo:Create static string field for cloud vision api
    private static final String CLOUD_VISION_API_KEY = "MY_API_KEY";
    //ToDo:Create static field for permissions
    private static final int GALLERY_PERMISSION_REQUEST = 1001;
    private static final int GALLERY_IMAGE_REQUEST = 1002;
    private static final int CAMERA_PERMISSION_REQUEST = 1003;
    private static final int CAMERA_IMAGE_REQUEST = 1004;

    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static final String TAG = MainActivity.class.getSimpleName();


    private ImageView img_clicked;
    private TextView text_labels;
    private Button btn_image, btn_tooth;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img_clicked = (ImageView) findViewById(R.id.img_pic_id);
        text_labels = (TextView) findViewById(R.id.textView);
        btn_image = (Button) findViewById(R.id.btn_pic_id);
        btn_tooth = (Button) findViewById(R.id.btn_blue);
        progressBar = (ProgressBar) findViewById(R.id.pro_pic_id);
        progressBar.setVisibility(View.VISIBLE);

        btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage(getString(R.string.alert_message))
                        .setTitle(getString(R.string.alert_title))
                        .setPositiveButton(getString(R.string.alert_pos_btn), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                openCamera();
                            }
                        })
                        .setNegativeButton(getString(R.string.alert_neg_btn), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                openGallery();
                            }
                        }).create().show();
            }
        });

        btn_tooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    private void openGallery() {
        if (PermissionUtils.requestPermission(MainActivity.this,
                GALLERY_PERMISSION_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, getString(R.string.gallery_select)), GALLERY_IMAGE_REQUEST);
        }
    }

    private void openCamera() {
        if (PermissionUtils.requestPermission(MainActivity.this,
                CAMERA_PERMISSION_REQUEST,
                Manifest.permission.CAMERA)) {
            /*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //File dir  = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            //File file = new File(dir,FILE_NAME);
            //Uri photoUri = FileProvider.getUriForFile(this,
                    //getApplicationContext().getPackageName() + ".provider", getCameraFile());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST);
       */ }
    }

    private File getCameraFile() {
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(dir, FILE_NAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            uploadImage(data.getData());
        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            data = getIntent();
            Uri myPhotoUri = FileProvider.getUriForFile(this,
                    getApplicationContext().getPackageName() + ".provider", getCameraFile());
            uploadImage(myPhotoUri);
        }
    }

    private void uploadImage(Uri data) {
        if (data != null) {
            try {
                Bitmap bitmap = scaleDownBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), data), 1200);
                callCloudVision(bitmap);
                img_clicked.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            Toast.makeText(this, R.string.image_picker_error, Toast.LENGTH_LONG).show();
        }

    }


    private void callCloudVision(final Bitmap bitmap) throws IOException {
        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... objects) {
                try {
                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
                    VisionRequestInitializer visionRequestInitializer = new VisionRequestInitializer(getString(R.string.my_api_key)) {
                        @Override
                        protected void initializeVisionRequest(VisionRequest<?> request) throws IOException {
                            super.initializeVisionRequest(request);
                            String packageName = getPackageName();
                            request.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                            String sig = PackageManagerUtils.getSignature(getPackageManager(), packageName);

                            request.getRequestHeaders().set(ANDROID_CERT_HEADER, sig);

                        }
                    };
                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(visionRequestInitializer);

                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest =
                            new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(new ArrayList<AnnotateImageRequest>() {{
                        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();
                        Image base64EncodedImage = new Image();
                        // Convert the bitmap to a JPEG
                        // Just in case it's a format that Android understands but Cloud Vision
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();

                        // Base64 encode the JPEG
                        base64EncodedImage.encodeContent(imageBytes);
                        annotateImageRequest.setImage(base64EncodedImage);
                        annotateImageRequest.setFeatures(new ArrayList<Feature>() {{
                            Feature feature = new Feature();
                            feature.setType("LABEL_DETECTION");
                            feature.setMaxResults(10);
                            add(feature);
                        }});
                        add(annotateImageRequest);
                    }});
                    Vision.Images.Annotate annotateRequest =
                            vision.images().annotate(batchAnnotateImagesRequest);
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true);
                    Log.d(TAG, "created Cloud Vision request object, sending request");

                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return convertResponseToString(response);

                } catch (GoogleJsonResponseException e) {
                    Log.d(TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return "Cloud Vision API request failed. Check logs for details.";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressBar.setVisibility(View.GONE);
                if(s.contains("rose")== true ){
                    if (s.contains("white")==true){
                        text_labels.setText("I have found a white rose");
                    }else if(s.contains("red")==true){
                        text_labels.setText("I have found a red rose");
                    }
                }else {text_labels.setText(s);}
            }
        }.execute();
    }

    private Bitmap scaleDownBitmap(Bitmap bitmap, int maxDimension) {
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {

            case GALLERY_PERMISSION_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, GALLERY_PERMISSION_REQUEST, grantResults)) {
                    openGallery();
                }
                break;
            case CAMERA_PERMISSION_REQUEST:
                if (PermissionUtils.permissionGranted(requestCode, CAMERA_PERMISSION_REQUEST, grantResults)) {
                    openCamera();
                }

        }
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response) {
        String message = "I found these things:\n\n";

        List<EntityAnnotation> labels = response.getResponses().get(0).getLabelAnnotations();
        if (labels != null) {
            for (EntityAnnotation label : labels) {
                message += String.format(Locale.US, "%.3f: %s", label.getScore(), label.getDescription());
                message += "\n";
            }
        } else {
            message += "nothing";
        }

        return message;
    }
}
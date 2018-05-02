package com.example.webczar.plantis.Fragments;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.webczar.plantis.BuildConfig;
import com.example.webczar.plantis.MainActivity;
import com.example.webczar.plantis.Permissions.PackageManagerUtils;
import com.example.webczar.plantis.Permissions.PermissionUtils;
import com.example.webczar.plantis.R;
import com.example.webczar.plantis.SearchActivity;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class Search extends Fragment {

    //ToDo:Create static field for permissions
    private static final int GALLERY_IMAGE_REQUEST = 1001;
    private static final int CAMERA_IMAGE_REQUEST = 1002;
    private static final int GALLERY_PERMISSION_REQUEST = 1003;

    public static final String FILE_NAME = "temp.jpg";
    private static final String ANDROID_CERT_HEADER = "X-Android-Cert";
    private static final String ANDROID_PACKAGE_HEADER = "X-Android-Package";
    private static final String TAG = MainActivity.class.getSimpleName();


    private ImageView img_clicked;
    private TextView text_labels;
    private Button btn_image;
    private ProgressBar progressBar;
    private File photoFile;
    private FloatingActionButton floatingActionButton;
    private Bundle bundle;

    public Search() {
        // Required empty public constructor
    }

//ToDo:for targetSDK 24 or higher we have parse the FileProvider with content Provider, create <provider> tag in manifest and create a xml providing the name and path
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        bundle = new Bundle();
        img_clicked = view.findViewById(R.id.img_pic_id);
        text_labels = view.findViewById(R.id.tv_pic_id);
        btn_image = view.findViewById(R.id.btn_pic_id);
        progressBar = view.findViewById(R.id.pro_pic_id);
        floatingActionButton = view.findViewById(R.id.floatingButton);
        btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
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

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), SearchActivity.class);
                if (bundle != null){
                    intent.putExtras(bundle);
                    startActivity(intent);
                }else {
                    Log.d("Search", "null bundle");
                }

            }
        });
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void openCamera() {
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener(){
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        //intent for capturing the image
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        //create referance for the clicked picture
                        //photoFile = getPhotoFileURI(FILE_NAME);
                        //ToDo:for targetSDK 24 or higher we have parse the FileProvider with content Provider, create <provider> tag in manifest and create a xml providing the name and path
                        //Uri fileProvider = FileProvider.getUriForFile(getContext(), "${applicationId}.provider", photoFile);
                        //after this share your content
                        //${applicationId}.provider
                        //intent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile);
                        //// If you call startActivityForResult() using an intent that no app can handle, your app will crash.
                        // So as long as the result is not null, it's safe to use the intent.
                        if (intent.resolveActivity(getContext().getPackageManager()) != null){
                            startActivityForResult(intent,CAMERA_IMAGE_REQUEST);
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()){
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Need Permissions");
                            builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
                            builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", ANDROID_PACKAGE_HEADER, null);
                                    intent.setData(uri);
                                    startActivityForResult(intent, 101);
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private File getPhotoFileURI(String fileName) {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)){
           File mediaStorage = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);
            // Return the file target for the photo based on filename
            if (!mediaStorage.exists()){
                mediaStorage.mkdirs();
                if (!mediaStorage.mkdirs()){
                    Log.d("Search", "Failed to create directory, Try something different");
                  }
            }
            File file = new File(mediaStorage.getPath() + File.separator + fileName);
            Log.d("Search File","Accessing " + mediaStorage.getPath() );
            return file;
        }
        return null;
    }

    private void openGallery() {
        if (PermissionUtils.requestPermission(getActivity(),
                GALLERY_PERMISSION_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, getString(R.string.gallery_select)), GALLERY_IMAGE_REQUEST);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            uploadImage(data.getData());
        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == RESULT_OK ) {
            //now get the uri from file provider
            photoFile = getPhotoFileURI(FILE_NAME);
            if (photoFile == null){
                Log.d("Search", "did not get file");
               }

            /*Uri myPhotoUri = FileProvider.getUriForFile(getContext(),
                    "${applicationId}.provider",photoFile);
            */
            Uri myPhotoUri = FileProvider.getUriForFile(getContext(),
                    BuildConfig.APPLICATION_ID + ".provider",photoFile);
            uploadImage(myPhotoUri);
        }
    }

    private void uploadImage(Uri data) {
        if (data != null) {
            try {
//                Bitmap bitmap = scaleDownBitmap(MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data), 1200);
  //              if (bitmap == null){
    //               try{
                        InputStream image_stream =getContext().getContentResolver().openInputStream(data);
                        Bitmap clicked_image = BitmapFactory.decodeStream(image_stream);
                        image_stream.close();
                        Bitmap bitmap1 = scaleDownBitmap(clicked_image,1200);
                        img_clicked.setImageBitmap(bitmap1);
                        callCloudVision(bitmap1);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap1.compress(Bitmap.CompressFormat.JPEG, 90 , byteArrayOutputStream);
                        byte[] imageBytes = byteArrayOutputStream.toByteArray();
                        bundle.putByteArray("imageByte", imageBytes);
      //              }catch (FileNotFoundException e){
        //                e.printStackTrace();
          //          }
            //     }
                //callCloudVision(bitmap);
                //img_clicked.setImageBitmap(bitmap);
                //ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                //bitmap.compress(Bitmap.CompressFormat.JPEG, 90 , byteArrayOutputStream);
                //byte[] imageBytes = byteArrayOutputStream.toByteArray();
                //bundle.putByteArray("imageByte", imageBytes);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                Log.d(TAG, "Image picking failed because " + e.getMessage());
                Toast.makeText(getContext(), R.string.image_picker_error, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "Image picker gave us a null image.");
            Toast.makeText(getContext(), R.string.image_picker_error, Toast.LENGTH_LONG).show();
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
                            String packageName = getContext().getPackageName();
                            request.getRequestHeaders().set(ANDROID_PACKAGE_HEADER, packageName);

                            String sig = PackageManagerUtils.getSignature(getContext().getPackageManager(), packageName);

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
                    String message;
                    message = convertResponseToString(response);
                    //bundle.putString("string_message", message);
                    return message;
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
                String rose = "rose";
                String sun = "sunflower";
                String daisy ="daisy";
                progressBar.setVisibility(View.GONE);
                if(s.contains(rose)== true ){
                        text_labels.setText("I have found a rose");
                    }else if (s.contains(sun)){
                    text_labels.setText("I have found a sunflower");
                }else if (s.contains(daisy)){
                    text_labels.setText("I have found a daisy");
                }
                else {text_labels.setText(s);}
            }
        }.execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GALLERY_IMAGE_REQUEST){
            PermissionUtils.permissionGranted(requestCode,GALLERY_PERMISSION_REQUEST,grantResults);
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

    private Bitmap scaleDownBitmap(Bitmap bitmap, int maxDimension) {
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (bitmap == null){
            Log.d("Search", "bitmap is null");
        }
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
}

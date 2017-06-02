package com.imageupload;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

import static android.text.TextUtils.isEmpty;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonChoose;
    private Button buttonUpload;

    private ImageView imageView;

    private EditText etName, etVillage, etUpozilla, etZilla;

    private Bitmap bitmap;

    private int PICK_IMAGE_REQUEST = 1;

   // private String UPLOAD_URL = "https://round-table-creeks.000webhostapp.com/uploadingimage1.php";
    private String UPLOAD_URL = "https://psychoactive-diseas.000webhostapp.com/UserInformation/info.php";

    private String KEY_IMAGE = "image";
    private String KEY_NAME = "name";
    private String KEY_VILLAGE = "village";
    private String KEY_UPOZILLA = "upozilla";
    private String KEY_ZILLA = "zilla";
    private String KEY_IMAGENAME = "imagename";
    String name;
    String image;
    String village, upozilla, zilla;
    String RandomAudioFileName = "ABCDE56489716FGHIJKLMNOP12345QRSTUVWXYZ";
    Random random;
    public static final int REQUEST_INTERNET = 2;
    public static final int REQUEST_CODE = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //super code goes here

        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);

        etName = (EditText) findViewById(R.id.etName);
        etVillage = (EditText) findViewById(R.id.etVillage);
        etUpozilla = (EditText) findViewById(R.id.etUpozilla);
        etZilla = (EditText) findViewById(R.id.etZilla);

        imageView = (ImageView) findViewById(R.id.imageView);

        buttonChoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);
        random = new Random();


        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET,Manifest.permission.CAMERA}, REQUEST_INTERNET);
        }


    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage.toString();
    }

    private void uploadImage() {

        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
                        Log.e("SUCCESSFULL : ", "" + s.toString());
                        etName.getText().clear();
                        etVillage.getText().clear();
                        etUpozilla.getText().clear();
                        etZilla.getText().clear();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(MainActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                        Log.e("ERROR : ", "" + volleyError.getMessage().toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                image = getStringImage(bitmap);
                //   String image = getStringImage(bitmap);
                Map<String, String> params = new Hashtable<String, String>();
//                Toast.makeText(MainActivity.this, "image  : "+image.toString(), Toast.LENGTH_LONG).show();
                Log.e("IMAGE : ", "" + encodeTobase64(bitmap).toString() + "");
                params.put(KEY_NAME, name);
                params.put(KEY_IMAGE, "" + encodeTobase64(bitmap));
                params.put(KEY_VILLAGE, "" + village);
                params.put(KEY_UPOZILLA, "" + upozilla);
                params.put(KEY_ZILLA, "" + zilla);
                params.put(KEY_IMAGENAME, "" + createRandomImagePath(20));
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();

            //Getting the Bitmap from Gallery
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Setting the Bitmap to ImageView
            imageView.setImageBitmap(bitmap);


        } else if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();

            //Getting the Bitmap from Gallery
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Setting the Bitmap to ImageView
            imageView.setImageBitmap(bitmap);


        } else if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            //Getting the Bitmap from Gallery

            bitmap = (Bitmap) data.getExtras().get("data");

            //Setting the Bitmap to ImageView
            imageView.setImageBitmap(bitmap);


        }

    }

    public static String encodeTobase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.buttonChoose) {
            // showFileChooser();
            takePhoto(imageView);

        } else if (v.getId() == R.id.buttonUpload) {
            name = etName.getText().toString();
            village = etVillage.getText().toString();
            upozilla = etUpozilla.getText().toString();
            zilla = etZilla.getText().toString();
            if (name.equals("") || village.equals("") || upozilla.equals("") || zilla.equals("")
                    || isEmpty(String.valueOf(bitmap))) {
                Toast.makeText(getApplicationContext(), "please give all information", Toast.LENGTH_LONG).show();
            } else {
                uploadImage();
            }
        }
    }

    public String createRandomImagePath(int length) {
        StringBuilder stringBuilder = new StringBuilder(length);
        int i = 0;
        while (i < length) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));

            i++;
        }
        return stringBuilder.toString();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_INTERNET) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //start audio recording or whatever you planned to do
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.INTERNET)) {
                    //Show an explanation to the user *asynchronously*
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, REQUEST_INTERNET);
                } else {
                    //Never ask again and handle your app without permission.
                }
            }
        }
    }

    public void takePhoto(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_for_add_image);
        LinearLayout llTakePhoto, llGallery;
        TextView tvCancel;

        llTakePhoto = (LinearLayout) dialog.findViewById(R.id.llTakePhoto);
        llGallery = (LinearLayout) dialog.findViewById(R.id.llFromGallery);
        tvCancel = (TextView) dialog.findViewById(R.id.tvCancel);

        llTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT,
//                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI.getPath());
                startActivityForResult(intent, 2);
                dialog.dismiss();

            }
        });

        llGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE);
                dialog.dismiss();

            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}

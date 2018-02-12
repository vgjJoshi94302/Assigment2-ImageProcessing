package com.example.viraljoshi.assigment2_imageprocessing;
import android.Manifest;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.content.pm.PackageInfo;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import static android.R.attr.bitmap;
import static android.R.attr.dial;
import static android.R.attr.visible;

public class MainActivity extends AppCompatActivity
{
//https://stackoverflow.com/questions/19922384/add-image-and-text-dynamically-by-user-inputuser-is-providing-data-in-android
    public static final int IMAGE_GALLERY_REQUEST = 20;
    public static final int CAMERA_REQUEST_CODE = 10;
    Integer REQUEST_IMAGE_CAPTURE = 1, SELECT_FILE = 0;
    Button b, bsave,openImage;
    EditText editText;
    ImageView im;
    AlertDialog dialog;
    RelativeLayout mDrawingPad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DV mDrawingView=new DV(this);
        setContentView(R.layout.activity_main);
        // Create a RealtiveLayout in which to add the ImageView
        mDrawingPad=(RelativeLayout) findViewById(R.id.linearlayout);
        mDrawingPad.addView(mDrawingView);
        b= (Button) findViewById(R.id.button);
        bsave = (Button) findViewById(R.id.button2);
        openImage=(Button) findViewById(R.id.OpenImage);
        im = (ImageView) findViewById(R.id.imageView3);
        editText=(EditText)findViewById(R.id.writename);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });
//save image method is called
          bsave.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  saveImage();

              }});
        //This method is used to open the image from the gallery
        openImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File imageFile = new  File("/DrawTextOnImg/image.jpg");
                if(imageFile.exists()){
                   // im.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
                    ImageView img=new ImageView(MainActivity.this);
                    img.setImageURI(Uri.fromFile(imageFile));
                }
            }
        });

    }
              @RequiresApi(api = Build.VERSION_CODES.M)
              protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                  super.onActivityResult(requestCode, resultCode, data);
                  Bitmap photo = (Bitmap) data.getExtras().get("data");
                  im.setImageBitmap(photo);
                  if(resultCode==RESULT_OK){
                      //if we are here,Everything processed successfully
                      if(requestCode==IMAGE_GALLERY_REQUEST){
                          //Hearing back from the Image Gallery

                          //the address of the image on the SD card
                          Uri ImageUri = data.getData();
                          //decalre a stream to read the image data from the sd card
                          InputStream inputStream;
                          //we are getting an input stream, based on the URI of the image
                          try {
                              inputStream=getContentResolver().openInputStream(ImageUri);
                              //get a bitmap from the stream
                              Bitmap img = BitmapFactory.decodeStream(inputStream);
                              im.setImageBitmap(img);

                          } catch (FileNotFoundException e) {
                              e.printStackTrace();
                              //show a message to the user that image is unavailable
                              Toast.makeText(this,"Unable to open Image",Toast.LENGTH_LONG).show();
                          }
                      }
                  }
                  //on the image view user touch the screen and this method is invoked
                  im.setOnTouchListener(new View.OnTouchListener() {
                      @Override
                      public boolean onTouch(View v, MotionEvent event) {
                          //condition to get the x and y cordinates on the screen
                          if (event.getAction()==MotionEvent.ACTION_DOWN){
                              System.out.println("Touch coordinates : " +
                                      String.valueOf(event.getX()) + "x" + String.valueOf(event.getY()));
                              ActionBar.LayoutParams params=new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT,1);
                              editText.setLayoutParams(params);
                              mDrawingPad.addView(editText);
                              //write the text on the text view when the user touches the screen on the and the textview enables
                              String ed=editText.getText().toString();
                              editText.setText(ed);

                              Toast.makeText(MainActivity.this,"writeText",Toast.LENGTH_SHORT).show();
                          }else {
                              editText.setVisibility(View.INVISIBLE);
                              Toast.makeText(MainActivity.this,"Failed to write",Toast.LENGTH_SHORT).show();

                          }

                          return true;
                      }
                  });
              }

    //references: -https://stackoverflow.com/questions/36812567/android-draw-text-on-imageview-with-finger-touch
    //Image is
public void saveImage() {
    Bitmap image = Bitmap.createBitmap(im.getWidth(), im.getHeight(), Bitmap.Config.RGB_565);
    im.draw(new Canvas(image));
    mDrawingPad.setDrawingCacheEnabled(true);
    mDrawingPad.buildDrawingCache();
    image=mDrawingPad.getDrawingCache();
    String uri = MediaStore.Images.Media.insertImage(getContentResolver(), image, "title", null);

    Log.e("uri", uri);
    try {
        // Save the image to the SD card.

        File folder = new File(Environment.getExternalStorageDirectory() + "/DrawTextOnImg");

        if (!folder.exists()) {
            folder.mkdir();
            //folder.mkdirs();  //For creating multiple directories
        }
        String timestamp= new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //saves image into external memory
        File file = new File(Environment.getExternalStorageDirectory()+"/DrawTextOnImg/tempImg.png");
        FileOutputStream stream = new FileOutputStream(file+timestamp);
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        Toast.makeText(MainActivity.this, "Picture saved", Toast.LENGTH_SHORT).show();

        // Android equipment Gallery application will only at boot time scanning system folder
        // The simulation of a media loading broadcast, for the preservation of images can be viewed in Gallery

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MEDIA_MOUNTED);
            intent.setData(Uri.fromFile(Environment.getExternalStorageDirectory()));
            sendBroadcast(intent);

    } catch (Exception e) {
        Toast.makeText(MainActivity.this, "Save failed", Toast.LENGTH_SHORT).show();
        e.printStackTrace();
    }
editText.setText("");
}
}

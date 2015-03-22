package com.beyowi.multitest;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.IOException;


public class ScanActivity extends ActionBarActivity {

    protected Button _button;
    protected Button open_button;
    protected ImageView _image;
    protected TextView _field;
    protected Uri picUri;
    protected boolean _taken;
    protected Bitmap croppedPic;

    protected static final String PHOTO_TAKEN = "photo_taken";
    final int CAMERA_CAPTURE = 1;
    final int CROPPING_DONE = 2;
    final int PICKFILE_RESULT_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        _image = ( ImageView ) findViewById( R.id.image );
        _field = ( TextView ) findViewById( R.id.field );
        _button = ( Button ) findViewById( R.id.button );
        _button.setOnClickListener( new ButtonClickHandler() );
        open_button = ( Button ) findViewById( R.id.button_open );
        open_button.setOnClickListener(new OpenButtonClickHandler());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class ButtonClickHandler implements View.OnClickListener
    {
        public void onClick( View view ){
            startCameraActivity();
        }
    }

    public class OpenButtonClickHandler implements View.OnClickListener
    {
        public void onClick( View view ){
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            try {
                startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), PICKFILE_RESULT_CODE);
            } catch (ActivityNotFoundException e) {
                Log.e("tag", "No activity can handle picking a file. Showing alternatives.");
            }
        }
    }

    protected void startCameraActivity()
    {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE );
        startActivityForResult( intent, CAMERA_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i("MakeMachine", "resultCode: " + resultCode);
        switch( resultCode )
        {
            case 0:
                Log.i( "MakeMachine", "User cancelled" );
                break;

            case -1:
                if(requestCode == CAMERA_CAPTURE) {
                    //get the Uri for the captured image
                    picUri = data.getData();
                    performCrop(picUri);
                }
                else if(requestCode == CROPPING_DONE){
                    //get the returned data
                    Bundle extras = data.getExtras();
                    //get the cropped bitmap
                    croppedPic = extras.getParcelable("data");
                    Log.i( "MAKEMACHINE", "CROPPED" );
                    onPhotoTaken(croppedPic);
                }
                else if(requestCode == PICKFILE_RESULT_CODE){
                    Uri FilePath = data.getData();
                    Log.i( "MAKEMACHINE", "FILE CHOSED:"+ FilePath );
                    performCrop(FilePath);
                }
                break;
        }
    }

    protected void onPhotoTaken(Bitmap bitmap)
    {
        _taken = true;
        Log.i( "MakeMachine", "photo_taken");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;

        //bitmap = rotatePhoto(bitmap, picUri);
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap ModBitmap = changeBitmapContrastBrightness(bitmap, 4, 0);
        Bitmap BWBitmap = convertGray(ModBitmap);

        _image.setImageBitmap(BWBitmap);
        Bitmap processed = Bitmap.createScaledBitmap(BWBitmap, 2*BWBitmap.getWidth(), 2*BWBitmap.getHeight(), false);

        String recognizedText = OCRTessBase(processed);
        Log.i( "RESULT", recognizedText);
        _field.setText(recognizedText);
    }

    private void performCrop(Uri picUri){
        try {
            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, CROPPING_DONE);
        }
        catch(ActivityNotFoundException anfe){
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public String OCRTessBase(Bitmap processed){
        Log.i( "MakeMachine", "before tessbase");
        TessBaseAPI baseApi = new TessBaseAPI();
        Log.i( "MakeMachine", "before lib lang");
        String lang_path = Environment.getExternalStorageDirectory() + "/media";
        Log.i( "MAKEMACHINE", lang_path);
        baseApi.init(lang_path, "eng");
        baseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SPARSE_TEXT);
        Log.i( "MakeMachine", "after lib lang");
        baseApi.setImage(processed);
        String recognizedText = baseApi.getUTF8Text();
        baseApi.end();

        return recognizedText;
    }

    public static Bitmap convertGray(Bitmap bmp){
        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        ColorMatrix saturationMatrix = new ColorMatrix();
        saturationMatrix.setSaturation(0); //you set color saturation to 0 for b/w
        //apply new saturation
        Paint saturationPaint = new Paint();
        saturationPaint.setColorFilter(new ColorMatrixColorFilter(saturationMatrix));
        canvas.drawBitmap(bmp, 0, 0, saturationPaint);
        return ret;
    }

    public static Bitmap convertGray2(Bitmap colorBitmap){
        int height = colorBitmap.getHeight();
        int width = colorBitmap.getWidth();
        Bitmap bwBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bwBitmap);
        //set contrast
        ColorMatrix contrastMatrix = new ColorMatrix();
        //change contrast
        float contrast = 50.f;
        float shift = (-.5f * contrast + .5f) * 255.f;
        contrastMatrix .set(new float[] {
                contrast , 0, 0, 0, shift ,
                0, contrast , 0, 0, shift ,
                0, 0, contrast , 0, shift ,
                0, 0, 0, 1, 0 });
        //apply contrast
        Paint contrastPaint = new Paint();
        contrastPaint.setColorFilter(new ColorMatrixColorFilter(contrastMatrix));
        canvas.drawBitmap(colorBitmap, 0, 0, contrastPaint);

        //set saturation
        ColorMatrix saturationMatrix = new ColorMatrix();
        saturationMatrix.setSaturation(0); //you set color saturation to 0 for b/w
        //apply new saturation
        Paint saturationPaint = new Paint();
        saturationPaint.setColorFilter(new ColorMatrixColorFilter(saturationMatrix));
        canvas.drawBitmap(colorBitmap, 0, 0, saturationPaint);

        return bwBitmap;
    }

    public static Bitmap changeBitmapContrastBrightness(Bitmap bmp, float contrast, float brightness)
    {
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, brightness,
                        0, contrast, 0, 0, brightness,
                        0, 0, contrast, 0, brightness,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    public static Bitmap rotatePhoto(Bitmap bitmap, Uri uri){
        // _path = path to the image to be OCRed
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(uri.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);

        int rotate = 0;

        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
        }

        if (rotate != 0) {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();

            // Setting pre rotate
            Matrix mtx = new Matrix();
            mtx.preRotate(rotate);

            // Rotating Bitmap & convert to ARGB_8888, required by tess
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
        }
        return bitmap;
    }
}

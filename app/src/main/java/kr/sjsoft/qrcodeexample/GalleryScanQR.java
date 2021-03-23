package kr.sjsoft.qrcodeexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class GalleryScanQR extends AppCompatActivity {
    private static final int PICK_IMAGE = 100;
    TextView textView;
    ImageView imageView;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_scan_q_r);

        textView = (TextView) findViewById(R.id.textViewResult);
        imageView = (ImageView) findViewById(R.id.imageView);
    }

    public void onClickLoadImageBtn(View view) {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight) {
        float width = bm.getWidth();
        float height = bm.getHeight();

        if(height > newHeight) {
            float percente = (float) (height / 100);
            float scale = (float) (newHeight / percente);
            width *= (scale / 100);
            height *= (scale / 100);
        }

        Bitmap sizingBmp = Bitmap.createScaledBitmap(bm, (int) width, (int) height, true);
        return sizingBmp;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
            scanQRUsingZXing(imageUri);
            scanQRUsingMLKit(imageUri);
        }
    }

    public void scanQRUsingZXing(Uri imageUri) {
        try
        {
            InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, bitmap.isMutable());
            bitmap = getResizedBitmap(bitmap, 400);
            if (bitmap == null)
            {
                textView.setText("ZXing: 인식이 안 되고 있습니다.\n");
            }
            int width = bitmap.getWidth(), height = bitmap.getHeight();
            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            bitmap.recycle();
            bitmap = null;
            RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
            BinaryBitmap bBitmap = new BinaryBitmap(new HybridBinarizer(source));
            MultiFormatReader reader = new MultiFormatReader();
            try
            {
                Result result = reader.decode(bBitmap);
                textView.setText("ZXing: " + result.getText() + " \n");
            } catch (NotFoundException e)
            {
                textView.setText("ZXing: 결과가 없습니다. \n");
            }
        }
        catch (FileNotFoundException e)
        {
            textView.setText("ZXing: 인식이 안 되고 있습니다. \n");
        }
    }

    public void scanQRUsingMLKit(Uri imageUri) {
        InputImage image;
        try {
            InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, bitmap.isMutable());
            bitmap = getResizedBitmap(bitmap, 1024);
            // image = InputImage.fromFilePath(this, imageUri);
            image = InputImage.fromBitmap(bitmap, 0);

            BarcodeScannerOptions options =
                    new BarcodeScannerOptions.Builder()
                            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                            .build();
            BarcodeScanner scanner = BarcodeScanning.getClient();

            Task<List<Barcode>> result = scanner.process(image)
                    .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                        @Override
                        public void onSuccess(List<Barcode> barcodes) {
                            if (barcodes.size() == 0) {
                                textView.append("ML Kit: 결과가 없습니다. \n");
                            }
                            for (Barcode barcode: barcodes) {
                                Rect bounds = barcode.getBoundingBox();
                                Point[] corners = barcode.getCornerPoints();

                                String rawValue = barcode.getRawValue();
                                // See API reference for complete list of supported types
                                textView.append("ML Kit: " + rawValue + " \n");

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Task failed with an exception
                            // ...
                            textView.append("ML Kit: 인식이 안 되고 있습니다. \n");
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
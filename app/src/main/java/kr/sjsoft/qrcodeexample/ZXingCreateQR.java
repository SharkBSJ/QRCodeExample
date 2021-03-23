package kr.sjsoft.qrcodeexample;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

public class ZXingCreateQR extends AppCompatActivity {
    private ImageView imageView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_z_xing_create_q_r);

        imageView = (ImageView) findViewById(R.id.QRImage);
        textView = (TextView) findViewById(R.id.QRtext);
        textView.setText("Hello, QR Code Example!");

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try{
            int width = 200;
            int height = 200;
            BitMatrix bitMatrix = multiFormatWriter.encode(textView.getText().toString(), BarcodeFormat.QR_CODE,width,height);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            for (int i = 0; i < width; i++) {// width
                for (int j = 0; j < height; j++) {// height
                    bitmap.setPixel(i, j, bitMatrix.get(i, j) ? Color.BLACK
                            : Color.WHITE);
                }
            }

            imageView.setImageBitmap(bitmap);
        } catch (Exception e){}
    }
}
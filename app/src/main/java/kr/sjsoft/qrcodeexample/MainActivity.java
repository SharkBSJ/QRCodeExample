package kr.sjsoft.qrcodeexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickCreateQRBtn(View v) {
        Intent intent = new Intent(this, ZXingCreateQR.class);
        startActivity(intent);
    }

    public void onClickScanQRBtn(View v) {
        Intent intent = new Intent(this, ZXingScanQR.class);
        startActivity(intent);
    }

    public void onClickScanQRBtn2(View v) {
        Intent intent = new Intent(this, MLKitScanQR.class);
        startActivity(intent);
    }

    public void onClickScanQRBtn3(View v) {
        Intent intent = new Intent(this, GalleryScanQR.class);
        startActivity(intent);
    }
}
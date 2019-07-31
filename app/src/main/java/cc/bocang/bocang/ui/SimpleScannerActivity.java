package cc.bocang.bocang.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import cc.bocang.bocang.R;
import cc.bocang.bocang.utils.PermissionUtils;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by bocang02 on 16/10/13.
 */

public class SimpleScannerActivity extends BaseActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private final String TAG = SimpleScannerActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);
        //沉浸式状态栏
        setColor(this,getResources().getColor(R.color.colorPrimary));// Set the scanner view as the content view

        PermissionUtils.requestPermission(SimpleScannerActivity.this, PermissionUtils.CODE_CAMERA, new PermissionUtils.PermissionGrant() {
            @Override
            public void onPermissionGranted(int requestCode) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, new PermissionUtils.PermissionGrant() {
            @Override
            public void onPermissionGranted(int requestCode) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Toast.makeText(SimpleScannerActivity.this, rawResult.getText(), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent();
        intent.setClass(SimpleScannerActivity.this, ScannerWebActivity.class);
        intent.putExtra("tempURL", rawResult.getText());
        //调用一个新的Activity
        startActivity(intent);

        SimpleScannerActivity.this.finish();

        Log.v(TAG, rawResult.getText()); // Prints scan results
        Log.v(TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

    }
}

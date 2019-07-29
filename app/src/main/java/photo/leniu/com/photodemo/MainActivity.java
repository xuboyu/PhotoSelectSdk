package photo.leniu.com.photodemo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import photo.leniu.com.photosdk_lib.PhotoCallbackHandler;
import photo.leniu.com.photosdk_lib.PhotoTool;

public class MainActivity extends Activity {

    private ImageView imageView;
    private Button button1;
    private Button button2;
    private Button button3;

    private EditText dt;
    private EditText dm;
    private EditText cx;
    private EditText cy;
    private EditText ox;
    private EditText oy;

    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;

        dt = (EditText) findViewById(R.id.dt);
        dm = (EditText) findViewById(R.id.dm);
        cx = (EditText) findViewById(R.id.cx);
        cy = (EditText) findViewById(R.id.cy);
        ox = (EditText) findViewById(R.id.ox);
        oy = (EditText) findViewById(R.id.oy);

        imageView = (ImageView) findViewById(R.id.head);

        button1 = (Button) findViewById(R.id.doit);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSomeThing();
                PhotoTool.getInstance(activity).showDialog(dt.getText().toString(),dm.getText().toString());
            }
        });

        button2 = (Button) findViewById(R.id.take);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 拍照及文件权限申请
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // 权限还没有授予，进行申请
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 300); // 申请的 requestCode 为 300
                } else {
                    // 权限已经申请，直接拍照
                    setSomeThing();
                    PhotoTool.getInstance(activity).cameraCapture();
                }
            }
        });

        button3 = (Button) findViewById(R.id.choose);

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 文件权限申请
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // 权限还没有授予，进行申请
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200); // 申请的 requestCode 为 200
                } else {
                    setSomeThing();
                    PhotoTool.getInstance(activity).pictureCapture();
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 200:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Main======>","图库权限申请通过");
                    setSomeThing();
                    PhotoTool.getInstance(activity).pictureCapture();
                }
                break;
            case 300:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setSomeThing();
                    PhotoTool.getInstance(activity).cameraCapture();
                }
                break;
        }
    }

    private void setSomeThing() {
        //输出大小
        if (cx.getText().toString().length() > 0 && cy.getText().toString().length() > 0) {
            Log.e("1有",cx.getText().toString().length()+"/"+cy.getText().toString().length());
            PhotoTool.getInstance(activity).setOutput(Integer.valueOf(cx.getText().toString()),Integer.valueOf(cy.getText().toString()));
        } else {
            Log.e("1无",cx.getText().toString().length()+"/"+cy.getText().toString().length());
            PhotoTool.getInstance(activity).setOutput(300,300);
        }
        //裁剪比例
        if (ox.getText().toString().length() > 0 && oy.getText().toString().length() > 0) {
            Log.e("2有",cx.getText().toString().length()+"/"+cy.getText().toString().length());
            PhotoTool.getInstance(activity).setAspect(Integer.valueOf(ox.getText().toString()),Integer.valueOf(oy.getText().toString()));
        } else {
            Log.e("2无",cx.getText().toString().length()+"/"+cy.getText().toString().length());
            PhotoTool.getInstance(activity).setAspect(1,1);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        PhotoTool.getInstance(activity).onActivityResult(requestCode, resultCode, data, new PhotoCallbackHandler.PhotoListener() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onFailure(String errorMsg) {
                //TODO onFailure
            }
        });
    }
}

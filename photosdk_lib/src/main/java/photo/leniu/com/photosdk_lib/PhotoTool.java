package photo.leniu.com.photosdk_lib;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * use：调用系统相机/相册
 * author: XuBoYu
 * time: 2019/3/26
 **/
public class PhotoTool extends Activity {

    private static PhotoTool sInstance;
    private Activity mActivity;

    //默认弹出框文字
    private static String title = "上传/更换头像";
    private static String message = "请选择新头像";
    //默认裁剪比例
    private int aspectX = 1;
    private int aspectY = 1;
    //默认输出大小
    private int outputX = 300;
    private int outputY = 300;

    private static String pn;

    private static final int REQUEST_IMAGE_GET = 0;//相册
    private static final int REQUEST_IMAGE_CAPTURE = 1;//照相
    private static final int REQUEST_SMALL_IMAGE_CUTTING = 2;
    private static final String IMAGE_FILE_NAME = "user_head_icon.jpg";

    private static final String PHOTO = Environment.getExternalStorageDirectory() + File.separator+"Head";

    private static final String TAG = "PhotoTool";

    /**
     * 无参构造函数
     */
    public PhotoTool() { }

    /**
     * 构造函数
     * @param activity 实例对象
     */
    public PhotoTool(Activity activity) {
        this.mActivity = activity;
    }

    /**
     * 获取单例实例
     * @param activity
     * @return
     */
    public synchronized static PhotoTool getInstance(Activity activity) {
        if (null == sInstance) {
            sInstance = new PhotoTool(activity);
            CreateFile();
        }
        return sInstance;
    }

    /**
     * 创建需要的文件夹
     */
    private static void CreateFile() {
        //创建文件夹
        Tool.createFile(PHOTO);
        Log.e(TAG,"create======>");
    }

    /**
     * 选择弹窗
     * @param title 弹窗标题
     * @param message 弹窗内容
     */
    public void showDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(title == null || title.equals("") ? "上传/更换头像" : title);
        builder.setMessage(message == null || message.equals("") ? "请选择新头像" : message);
        //相册入口按钮
        builder.setPositiveButton("拍照", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cameraCapture();
            }
        });
        //图库入口按钮
        builder.setNegativeButton("图库", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pictureCapture();
            }
        });
        //    显示出该对话框
        builder.show();
    }

    /**
     * 相机调用
     */
    public void cameraCapture() {
        Intent intent;
        Uri pictureUri;
        File pictureFile = new File(PHOTO, IMAGE_FILE_NAME);
        // 判断当前系统
        Log.e(TAG,"VERSION.SDK_INT======>"+Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= 24) {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //临时访问读权限 intent的接受者将被授予 INTENT 数据uri 或者 在ClipData 上的读权限。
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //""中的内容是随意的，但最好用package名.provider名的形式，清晰明了
            Log.e(TAG,"getPackageName======>"+Tool.getPackageName(mActivity));
            pictureUri = FileProvider.getUriForFile(mActivity,
                    Tool.getPackageName(mActivity) +".fileprovider", pictureFile);
            pn = Tool.getPackageName(mActivity);
        } else {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            pictureUri = Uri.fromFile(pictureFile);
        }
        // 去拍照,拍照的结果存到pictureUri对应的路径中
        intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri);
        Log.e(TAG,"before take photo"+pictureUri.toString());
        mActivity.startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    /**
     * 相册调用
     */
    public void pictureCapture() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        // 判断系统中是否有处理该 Intent 的 Activity
        if (intent.resolveActivity(mActivity.getPackageManager()) != null) {
            mActivity.startActivityForResult(intent, REQUEST_IMAGE_GET);
        } else {
            Toast.makeText(mActivity, "未找到图片查看器", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 回调
     * @param requestCode
     * @param resultCode
     * @param data
     * @param photoListener
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data, PhotoCallbackHandler.PhotoListener photoListener) {
        Log.e(TAG,"result = "+resultCode+",request = "+requestCode);
        // 回调成功
        if (resultCode == RESULT_OK) {
            Log.e(TAG,"OK======>"+"result = "+resultCode+",request = "+requestCode);
            switch (requestCode) {
                // 切割
                case REQUEST_SMALL_IMAGE_CUTTING:
                    Log.e(TAG,"before show");
                    File cropFile = new File(PHOTO,"crop.jpg");
                    Uri cropUri = Uri.fromFile(cropFile);
                    if (cropUri != null) {
                        setPicToView(cropUri,photoListener);
                    }
                    break;

                // 相册选取
                case REQUEST_IMAGE_GET:
                    Log.e(TAG,"图库调用======>"+"uri:"+data.getData());
                    Uri uri= Tool.getImageUri(mActivity,data,Tool.getPackageName(mActivity));
                    startPhotoZoom(uri,mActivity);
                    break;

                // 拍照
                case REQUEST_IMAGE_CAPTURE:
                    Log.e(TAG,"相机调用======>");
                    File pictureFile = new File(PHOTO, IMAGE_FILE_NAME);
                    Uri pictureUri;
                    if (Build.VERSION.SDK_INT >= 24) {
                        pictureUri = FileProvider.getUriForFile(this, pn+".fileprovider", pictureFile);
                        Log.e(TAG,"picURI1="+pictureUri.toString());
                    } else {
                        pictureUri = Uri.fromFile(pictureFile);
                        Log.e(TAG,"picURI2="+pictureUri.toString());
                    }
                    startPhotoZoom(pictureUri,mActivity);
                    break;

                default:
            }
        }else{
            Log.e(TAG,"Failure======>"+"result = "+resultCode+",request = "+requestCode);
        }
    }

    /**
     * 自定义输出图片大小
     * @param ox
     * @param oy
     */
    public void setOutput(int ox, int oy) {
        this.outputX = ox;
        this.outputY = oy;
    }

    /**
     * 自定义裁剪比例
     * @param ax
     * @param ay
     */
    public void setAspect(int ax, int ay) {
        this.aspectX = ax;
        this.aspectY = ay;
    }

    /**
     * 裁剪图片
     * @param uri
     * @param activity
     */
    private void startPhotoZoom(Uri uri,Activity activity) {
        //保存裁剪后的图片
        File cropFile = new File(PHOTO,"crop.jpg");
        try{
            if(cropFile.exists()){
                cropFile.delete();
                Log.e(TAG,"delete");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        Uri cropUri = Uri.fromFile(cropFile);

        Log.e(TAG,"裁剪图片比例=====>"+"x:"+aspectX+"/y:"+aspectY);
        Log.e(TAG,"输出图片大小=====>"+"x:"+outputX+"/y:"+outputY);

        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= 24) {
            Log.e(TAG,"SDK-INT======>"+Build.VERSION.SDK_INT);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", aspectX); // 裁剪框比例
        intent.putExtra("aspectY", aspectY);
//        intent.putExtra("outputX", outputX); // 输出图片大小
//        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);

        Log.e(TAG,"cropUri = "+cropUri.toString());

        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        activity.startActivityForResult(intent, REQUEST_SMALL_IMAGE_CUTTING);

    }

    public void setPicToView(Uri uri, PhotoCallbackHandler.PhotoListener photoListener) {
        Bitmap photo = null;
        try {
            photo = BitmapFactory.decodeStream(mActivity.getContentResolver().openInputStream(uri));
            if (photo != null) {
                photo = toBigZoom(photo,300,300);
            }
        } catch (FileNotFoundException e) {
            if (null != photoListener) {
                photoListener.onFailure(e.toString());
            }
            e.printStackTrace();
        }
        Log.e(TAG, "setPicToView=====>uri");
        if (photoListener != null) {
            photoListener.onSuccess(photo);
        }
    }

    /**
     * 裁剪后，根据裁剪框的长宽比，同时根据图片的需求缩放尺寸进行缩放
     * @param x 原始的需求尺寸width
     * @param y 原始的需求尺寸heiht
     * @return
     */
    public static Bitmap toBigZoom(Bitmap bitmap, float x, float y) {
        Log.e("bitmaputil", "--x--y--" + x + "--" + y);
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        float sx = 0;
        float sy = 0;
        if ((float) w / h >= 1) {
            sx = (float) y / w;
            sy = (float) x / h;
        } else {
            sx = (float) x / w;
            sy = (float) y / h;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(sx, sy); // 长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return resizeBmp;
    }

}


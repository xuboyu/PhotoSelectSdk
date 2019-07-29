package photo.leniu.com.photosdk_lib;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;

/**
 * use：一些工具类
 * author: XuBoYu
 * time: 2019/3/26
 **/
public class Tool {

    /**
     * 获取应用程序版本名称信息
     * @param context
     * @return 当前应用的版本名称
     */
    public static String getPackageName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建文件夹
     * @param file
     */
    public static void createFile(String file) {
        boolean isSdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);// 判断sdcard是否存在
        if (isSdCardExist) {
            Log.d("Tool======>", file);
            File MyRoot = new File(file);
            if (!MyRoot.exists()) {
                try {
                    MyRoot.mkdir();
                    Log.d("Tool======>", "mkdir success");
                } catch (Exception e) {
                    Log.e("Tool======>", "exception->" + e.toString());
                }
            }
            Log.e("Tool======>","file exists");
        }
    }

    public static Uri getImageUri(Context context, Intent data, String pn){
        String imagePath = null;
        Uri uri = data.getData();
        if(Build.VERSION.SDK_INT >= 19){
            if(DocumentsContract.isDocumentUri(context,uri)){
                String docId = DocumentsContract.getDocumentId(uri);
                if("com.android.providers.media.documents".equals(uri.getAuthority())){
                    String id = docId.split(":")[1];
                    String selection = MediaStore.Images.Media._ID+"="+id;
                    imagePath = getImagePath(context,MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
                }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                    imagePath = getImagePath(context,contentUri,null);
                }
            }else if("content".equalsIgnoreCase(uri.getScheme())){
                imagePath = getImagePath(context,uri,null);
            }else if("file".equalsIgnoreCase(uri.getScheme())){
                imagePath = uri.getPath();
            }
        }else{
            uri= data.getData();
            imagePath = getImagePath(context,uri,null);
        }
        Log.e("Tool======>imagePath:",imagePath);
        File file = new File(imagePath);
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(context,
                    pn+".fileprovider", file);
        } else {
            uri = Uri.fromFile(file);
        }

        return uri;
    }

    private static String getImagePath(Context context,Uri uri, String selection) {
        String path = null;
        Cursor cursor = context.getContentResolver().query(uri,null,selection,null,null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

}


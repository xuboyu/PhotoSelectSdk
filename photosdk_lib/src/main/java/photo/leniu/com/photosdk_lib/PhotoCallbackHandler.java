package photo.leniu.com.photosdk_lib;

import android.graphics.Bitmap;

/**
 * use：相机/相册 回调
 * author: XuBoYu
 * time: 2019/3/27
 **/
public class PhotoCallbackHandler {

    /**
     * 相机/相册操作回调
     */
    public interface PhotoListener {
        void onSuccess(Bitmap bitmap);
        void onFailure(String errorMsg);
    }

}

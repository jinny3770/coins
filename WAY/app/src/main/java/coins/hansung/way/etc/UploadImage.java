package coins.hansung.way.etc;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.net.URL;

/**
 * Created by sora on 2016-05-29.
 */
public class UploadImage extends AsyncTask {

    Bitmap image;
    String imageName;
    URL url;

    public UploadImage(Bitmap image, String imageName) {
        this.image = image;
        this.imageName = imageName;
    }

    @Override
    protected Object doInBackground(Object[] params) {

        try {
            url = new URL(Links.uploadImageURL);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

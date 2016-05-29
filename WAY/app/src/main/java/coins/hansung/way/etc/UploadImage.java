package coins.hansung.way.etc;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by sora on 2016-05-29.
 */
public class UploadImage extends AsyncTask {
    @Override
    protected Object doInBackground(Object[] params) {
        return null;
    }
/**
    Bitmap image;
    String imageName;
    URL url;

    String data;

    public UploadImage(Bitmap image, String imageName) {
        this.image = image;
        this.imageName = imageName;
    }

    @Override
    protected Object doInBackground(Object[] params) {

        try {

            url = new URL(Links.uploadImageURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);

            conn.setDoOutput(true);
            conn.setDoInput(true);

            data = URLEncoder.encode("ImageName", "UTF-8") + "=" + URLEncoder.encode(imageName, "UTF-8") + "&" +
                    URLEncoder.encode("Image", "UTF-8" ) +"=" + URLEncoder.encode(image, "UTF-8");


            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("UploadImage", e.getMessage());
        }

        return null;
    }

*/
}

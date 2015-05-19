package sdis.twitterclient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    String url;
    ImageView image;
    public DownloadImageTask(String url,  ImageView image) {
        this.url = url;
        this.image = image;
    }

    private Bitmap getImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e("Bitmap image", "Error getting bitmap", e);
        }
        return bm;
    }

    protected Bitmap doInBackground(String... urls) {
        return getImageBitmap(url);
    }

    protected void onPostExecute(Bitmap result) {
        image.setImageBitmap(result);
    }
}
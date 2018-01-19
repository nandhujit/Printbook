package android.app.printerapp.database;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.ContentValues.TAG;

/**
 * Created by Geek on 12/2/2017.
 */

public class FetchMagic extends AsyncTask<String, Bitmap, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                Log.d("what am here? Before" ,   ":" + urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                Log.d("what am here? Before" , myBitmap + "" + ":" + urls[0]);
                return myBitmap;
            }catch (Exception e){
                Log.d(TAG,e.getMessage());
            }
            return null;
            /*Drawable drawable = null;
             InputStream is = null;
            try {
                is = (InputStream) new URL(urls[0]).getContent();
                Log.d("what am here? Before" , drawable + "" + ":" + urls[0]);
                drawable.createFromStream(is, "src name");
                Log.d("what am here? after" , drawable + "");
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return drawable;*/
        }

    @Override
    protected void onPostExecute(Bitmap drawable) {
        super.onPostExecute(drawable);
    }
}

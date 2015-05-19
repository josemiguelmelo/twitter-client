package sdis.twitterclient.GUI;

import android.content.Context;
import android.widget.Toast;

public class ToastRunnable implements Runnable {
    private String message;
    private Context context;
    public ToastRunnable(String message, Context context) {
        this.message = message;
        this.context = context;
    }

    public void run() {
        Toast.makeText(this.context, this.message,
                Toast.LENGTH_LONG).show();
    }
}
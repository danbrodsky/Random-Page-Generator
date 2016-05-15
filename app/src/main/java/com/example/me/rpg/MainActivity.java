package com.example.me.rpg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;
import android.graphics.pdf.PdfRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity {


    private ViewFlipper viewFlipper;

    private ParcelFileDescriptor fileDescriptor;

    private PdfRenderer pdfRenderer;

    private PdfRenderer.Page currentPage;

    private ImageView imageView;

    private float lastX;

    private boolean page = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewFlipper = (ViewFlipper) findViewById(R.id.viewflipper);
        try {
            openRenderer(this.getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }
        showPage(0 + (int) (Math.random() * ((10 - 0) + 1)), R.id.imageView1);
        viewFlipper.setDisplayedChild(0);
    }

    private void openRenderer(Context context) throws IOException {
        File file = FileUtils.fileFromAsset(context, "the_hobbit.pdf");
        ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE);

        pdfRenderer = new PdfRenderer(parcelFileDescriptor);
    }

    public static class FileUtils {
        private FileUtils() {
        }

        public static File fileFromAsset(Context context, String assetName) throws IOException {
            File outFile = new File(context.getCacheDir(), assetName);
            copy(context.getAssets().open(assetName), outFile);

            return outFile;
        }

        public static void copy(InputStream inputStream, File output) throws IOException {
            FileOutputStream outputStream = null;

            try {
                outputStream = new FileOutputStream(output);
                boolean read = false;
                byte[] bytes = new byte[1024];

                int read1;
                while ((read1 = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read1);
                }
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } finally {
                    if (outputStream != null) {
                        outputStream.close();
                    }

                }

            }

        }
    }

    private void showPage(int index, int view) {
        if (pdfRenderer.getPageCount() <= index) {
            return;
        }
        // Make sure to close the current page before opening another one.
        if (null != currentPage) {
            currentPage.close();
        }
        currentPage = pdfRenderer.openPage(index);

        Bitmap bitmap = Bitmap.createBitmap(200 / 72 * currentPage.getWidth(),
                200 / 72 * currentPage.getHeight(),
                Bitmap.Config.ARGB_8888);
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

        imageView = (ImageView) findViewById(view);
        imageView.setImageBitmap(bitmap);
    }

    // Using the following method, we will handle all screen swaps.
    public boolean onTouchEvent(MotionEvent touchevent) {
        int rand = 0 + (int) (Math.random() * ((210 - 0) + 1));
        switch (touchevent.getAction()) {

            case MotionEvent.ACTION_DOWN:
                lastX = touchevent.getX();
                break;
            case MotionEvent.ACTION_UP:
                float currentX = touchevent.getX();

                // Handling left to right screen swap.
                if (lastX < currentX) {

                    // If there aren't any other children, just break.

                    // Next screen comes in from left.
                    viewFlipper.setInAnimation(this, R.anim.slide_in_from_left);
                    // Current screen goes out from right.
                    viewFlipper.setOutAnimation(this, R.anim.slide_out_to_right);

                    // Display next screen.
                    if (viewFlipper.getDisplayedChild() == 2) {
                        showPage(rand, R.id.imageView2);
                        viewFlipper.setDisplayedChild(1);
                        break;
                    }
                    if (viewFlipper.getDisplayedChild() == 1) {
                        showPage(rand, R.id.imageView1);
                        viewFlipper.setDisplayedChild(0);
                        break;
                    }
                    if (viewFlipper.getDisplayedChild() == 0) {
                        showPage(rand, R.id.imageView3);
                        viewFlipper.setDisplayedChild(2);
                        break;
                    }
                }

                // Handling right to left screen swap.
                else if (lastX > currentX) {

                    // Next screen comes in from right.
                    viewFlipper.setInAnimation(this, R.anim.slide_in_from_right);
                    // Current screen goes out from left.
                    viewFlipper.setOutAnimation(this, R.anim.slide_out_to_left);

                    // Display previous screen.
                    if (viewFlipper.getDisplayedChild() == 1) {
                        showPage(rand, R.id.imageView3);
                        viewFlipper.setDisplayedChild(2);
                        break;
                    }
                    if (viewFlipper.getDisplayedChild() == 2) {
                        showPage(rand, R.id.imageView1);
                        viewFlipper.setDisplayedChild(0);
                        break;
                    }
                    if (viewFlipper.getDisplayedChild() == 0) {
                        showPage(rand, R.id.imageView2);
                        viewFlipper.setDisplayedChild(1);
                        break;
                    }
                }
        }
        return false;
    }
}
package com.jj.fakecam;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.io.OutputStream;

public class FakeCameraActivity extends Activity {
    private static final String TAG = "FaceCam_Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        File fakeFile = new File(getFilesDir(), "fake_camera_source.jpg");

        if (!fakeFile.exists()) {
            Toast.makeText(this, "FaceCam Қатесі: Алдымен қолданбадан сурет таңдаңыз!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        Bundle extras = getIntent().getExtras();
        Uri outputUri = null;
        if (extras != null) {
            outputUri = (Uri) extras.getParcelable(MediaStore.EXTRA_OUTPUT);
        }

        try {
            // 🔥 ШЕШІМІ: inSampleSize арқылы жадты шамадан тыс жүктеуден (OOM) қорғау
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(fakeFile.getAbsolutePath(), options);

            // Максималды рұқсат етілген өлшем ретінде 2048px есептейміз
            options.inSampleSize = calculateInSampleSize(options, 2048, 2048);
            options.inJustDecodeBounds = false;

            Bitmap bitmap = BitmapFactory.decodeFile(fakeFile.getAbsolutePath(), options);

            if (bitmap == null) {
                setResult(RESULT_CANCELED);
                finish();
                return;
            }

            if (outputUri != null) {
                // 🔥 ШЕШІМІ: openOutputStream үшін Тaza Null Safety және try-with-resources
                try (OutputStream os = getContentResolver().openOutputStream(outputUri)) {
                    if (os != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, os);
                        Log.d(TAG, "Сурет EXTRA_OUTPUT арқылы сәтті жіберілді.");
                        setResult(RESULT_OK);
                    } else {
                        Log.e(TAG, "Қате: openOutputStream null қайтарды.");
                        setResult(RESULT_CANCELED);
                    }
                }
            } else {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("data", bitmap);
                setResult(RESULT_OK, resultIntent);
            }
        } catch (Exception e) {
            Log.e(TAG, "Камера симуляциясының қатесі: " + e.getMessage());
            setResult(RESULT_CANCELED);
        }
        
        finish();
    }

    // Оптималды кішірейту коэффициентін есептейтін ресми функция
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}

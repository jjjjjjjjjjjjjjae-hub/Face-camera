package com.jj.fakecam;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.view.Gravity;
import com.jj.fakecam.hook.RuntimeInterfaceInterceptor;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "FaceCam_Main";

    public interface CameraServiceMock {
        String acquireLatestImageFrame();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "MainActivity сәтті іске қосылды.");

        TextView tv = new TextView(this);
        tv.setText("FaceCam 10/10 Архитектурасы\n\nРесурстар мен Навигация толық түзетілді.\nЖұмыс нәтижесін Logcat (Log.d) арқылы көріңіз.");
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(16);
        setContentView(tv);

        CameraServiceMock realService = new CameraServiceMock() {
            @Override
            public String acquireLatestImageFrame() {
                return "Нағыз камера пиксельдері (Аппараттық дерек)";
            }
        };

        try {
            CameraServiceMock proxyService = RuntimeInterfaceInterceptor.createProxy(realService, CameraServiceMock.class);
            String frameData = proxyService.acquireLatestImageFrame();
            Log.d(TAG, "Прокси арқылы өткен мән: " + frameData);
        } catch (Exception e) {
            Log.e(TAG, "Интерцептор қатесі: ", e);
        }
    }
}

package com.jj.fakecam;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.view.Gravity;
import com.jj.fakecam.hook.RuntimeInterfaceInterceptor;

public class MainActivity extends AppCompatActivity {
    private TextView logTextView;

    public interface CameraServiceMock {
        String acquireLatestImageFrame();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Экранда логтарды көрсететін визуалды элемент
        logTextView = new TextView(this);
        logTextView.setText("=== FaceCam Экрандық Лог Жүйесі ===\n\n");
        logTextView.setGravity(Gravity.TOP | Gravity.LEFT);
        logTextView.setTextSize(14);
        logTextView.setPadding(40, 40, 40, 40);
        setContentView(logTextView);

        // Интерцептор логтарын тікелей экранға бағыттаймыз
        RuntimeInterfaceInterceptor.setLogListener(new RuntimeInterfaceInterceptor.LogListener() {
            @Override
            public void onLogReceived(final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        logTextView.append(message + "\n");
                    }
                });
            }
        });

        RuntimeInterfaceInterceptor.logToUI("[ЖҮЙЕ]: Қолданба іске қосылды.");

        CameraServiceMock realService = new CameraServiceMock() {
            @Override
            public String acquireLatestImageFrame() {
                return "Нағыз камера пиксельдері (Аппараттық дерек)";
            }
        };

        try {
            // Прокси құру сәті
            CameraServiceMock proxyService = RuntimeInterfaceInterceptor.createProxy(realService, CameraServiceMock.class);
            
            // Әдісті шақыру (Хук автоматты түрде экранда көрінеді)
            String frameData = proxyService.acquireLatestImageFrame();
            RuntimeInterfaceInterceptor.logToUI("[ЖҮЙЕ ДЕРЕГІ]: " + frameData);
        } catch (Exception e) {
            RuntimeInterfaceInterceptor.logToUI("[ҚАТЕ]: " + e.getMessage());
        }
    }
}

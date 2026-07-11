package com.jj.fakecam;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    private TextView statusTextView;

    // Кәсіби ActivityResultLauncher - ескірген onActivityResult орнына
    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    copyImageToInternalStorage(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 40, 40, 40);

        Button pickButton = new Button(this);
        pickButton.setText("Галереядан сурет таңдау (Старт)");

        statusTextView = new TextView(this);
        statusTextView.setText("Күйі: Сурет дайын емес. Батырманы басыңыз.");
        statusTextView.setTextSize(14);
        statusTextView.setPadding(0, 40, 0, 0);

        layout.addView(pickButton);
        layout.addView(statusTextView);
        setContentView(layout);

        updateStatus();

        // Жаңа API арқылы галереяны іске қосу
        pickButton.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
    }

    private void updateStatus() {
        File fakeFile = new File(getFilesDir(), "fake_camera_source.jpg");
        if (fakeFile.exists()) {
            statusTextView.setText("Күйі: СТАРТ БЕРІЛДІ!\nСурет ішкі қауіпсіз жадта тұр.\nЕнді басқа қолданбадан камера ашқанда осы қолданбаны таңдаңыз.");
        }
    }

    private void copyImageToInternalStorage(Uri imageUri) {
        try {
            File localFile = new File(getFilesDir(), "fake_camera_source.jpg");
            try (InputStream is = getContentResolver().openInputStream(imageUri);
                 OutputStream os = new FileOutputStream(localFile)) {
                if (is == null) return;
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }
            updateStatus();
            Toast.makeText(this, "Сурет сәтті бекітілді!", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            statusTextView.setText("Қате: " + e.getMessage());
        }
    }
}

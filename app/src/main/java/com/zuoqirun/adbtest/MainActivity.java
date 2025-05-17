package com.zuoqirun.adbtest;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // 定义日志标签
    private static final String TAG = "MainActivity";
    TextView copyingFilesStatus;
    // 定义复制状态文本

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        copyingFilesStatus = findViewById(R.id.copying_files_status);

        // 拷贝 assets 目录下的所有文件到 files 目录
        copyAllAssetsToFilesDir();

    }

    /**
     * 将 assets 目录下的所有文件拷贝到应用的 files 目录下。
     */
    private void copyAllAssetsToFilesDir() {
        try {
            // 开始递归拷贝 assets 中的根目录到 files 的根目录
            FileUtil.copyAssetsDirToFilesDir(this, "", "");
            Log.d(TAG, "Copying files from assets to files directory is done.");
            copyingFilesStatus.setText("ADB及相关文件复制成功");
        } catch (Exception e) {
            Log.e(TAG, "Failed to copy files from assets to files directory.", e);
            copyingFilesStatus.setText("ADB及相关文件复制失败");
        }
    }
}

package com.zuoqirun.adbtest;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;

public class FileUtil {

    /**
     * 将 assets 目录下的文件递归拷贝到应用的 files 目录下。
     *
     * @param context        应用的 Context 对象
     * @param assetsDir      assets 目录下的路径（相对路径，例如 "" 表示根目录，"folder/" 表示子目录）
     * @param filesDir       files 目录下的目标路径（相对路径）
     */
    public static void copyAssetsDirToFilesDir(Context context, String assetsDir, String filesDir) {
        try {
            // 获取 assets 管理器
            AssetManager assetManager = context.getAssets();

            // 拼接目标文件夹路径
            File targetDir = new File(context.getFilesDir(), filesDir);
            if (!targetDir.exists()) {
                targetDir.mkdirs(); // 如果文件夹不存在，创建它
            }

            // 列出 assets 目录下的内容
            String[] assetsEntries = assetManager.list(assetsDir);

            // 如果 assetsDir为空，则返回 null 或空数组
            if (assetsEntries == null) {
                return;
            }
            Log.d("FileUtil", "copyAssetsDirToFilesDir: " + assetsDir + " has " + assetsEntries.length + " entries");
            // 遍历每个内容
            for (String entry : assetsEntries) {
                String entryPath = assetsDir.isEmpty() ? entry : assetsDir + "/" + entry;

                // 判断是文件还是目录
                if (IsDirectoryInAssets(assetManager, entryPath)) {
                    // Log.d("FileUtil", "copyAssetsDirToFilesDir: " + entryPath + " is a directory");
                    // 如果是目录，递归拷贝
                    String subDirPath = assetsDir.isEmpty() ? entry + "/" : assetsDir + "/" + entry + "/";
                    copyAssetsDirToFilesDir(context, subDirPath, filesDir + "/" + entry);
                } else {
                    // 如果是文件，拷贝到目标文件夹
                    Log.d("FileUtil", "copyAssetsDirToFilesDir: " + entryPath + " is a file");
                    String fileName = entryPath.substring(entryPath.lastIndexOf("/") + 1);
                    FileUtil.copyAssetFileToAppDir(context, entryPath, filesDir + "/" + fileName);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断 assets 目录下的路径是否是一个子目录。
     *
     * @param assetManager 资源管理器
     * @param path        资源路径
     * @return 如果路径是一个子目录，则返回 true，否则返回 false
     */
    private static boolean IsDirectoryInAssets(AssetManager assetManager, String path) {
        /*
        try {
            // 列出传入路径下的内容，如果返回非空数组，则认为是目录
            return assetManager.list(path) != null;
        } catch (IOException e) {
            return false;
        }
        */
        Log.d("isDirectoryInAssets", "IsDirectoryInAssets: " + path + "false");
        return false;
    }

    /**
     * 将单个 assets 文件拷贝到应用的 files 目录。
     *
     * @param context       应用的 Context 对象
     * @param assetFileName assets 中的文件名或路径
     * @param destFileName  files 目录下的目标文件名
     */
    public static void copyAssetFileToAppDir(Context context, String assetFileName, String destFileName) {
        AssetManager assetManager = context.getAssets();
        InputStream in = null;
        FileOutputStream out = null;
        try {
            // 打开 assets 目录下的文件
            in = assetManager.open(assetFileName);
            // 创建目标文件对象，位于应用的 files 目录下
            File outFile = new File(context.getFilesDir(), destFileName);
            // 创建文件输出流以写入目标文件
            out = new FileOutputStream(outFile);
            // 调用 copyFile 方法将文件内容从输入流拷贝到输出流
            copyFile(in, out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 确保输入流关闭
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // 确保输出流关闭
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 将输入流中的内容写入输出流。
     *
     * @param in  输入流
     * @param out 输出流
     * @throws IOException 如果读取或写入文件时发生错误
     */
    private static void copyFile(InputStream in, FileOutputStream out) throws IOException {
        // 创建一个缓冲区，用于存储读取的数据
        byte[] buffer = new byte[1024];
        int read;
        // 从输入流读取数据并写入输出流，直到输入流结束
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}
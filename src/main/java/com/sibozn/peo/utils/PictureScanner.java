package com.sibozn.peo.utils;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;

import java.io.File;

/**
 * 采用MediaScannerConnection扫描制定路径下的图片文件，并启动系统相册进行浏览
 * Created by Administrator on 2016/12/28.
 */

public class PictureScanner implements MediaScannerConnection.MediaScannerConnectionClient {
    private MediaScannerConnection mMs;
    private File mFile;
    private Context context;
    private File[] allFiles;

    public PictureScanner(Context context, String pictureFolderPath) {
        this.context = context;
        File folder = new File(pictureFolderPath);
        allFiles = folder.listFiles();
        swap(allFiles);
        mFile = allFiles[0];
        mMs = new MediaScannerConnection(context, this);
        mMs.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        mMs.scanFile(mFile.getAbsolutePath(), null);
    }

    @Override
    public void onScanCompleted(String s, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW)
                .setData(uri);
        context.startActivity(intent);
        mMs.disconnect();
    }

    private void swap(File a[]) {
        int len = a.length;
        for (int i = 0; i < len / 2; i++) {
            File tmp = a[i];
            a[i] = a[len - 1 - i];
            a[len - 1 - i] = tmp;
        }
    }
}

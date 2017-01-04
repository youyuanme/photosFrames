package com.sibozn.peo.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2016/11/1.
 */

public class MyUtils {

    /**
     * 跳转到google play更新app
     *
     * @param context
     */
    public static void Rate(Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        //startActivity(it);
        it.setClassName("com.android.vending", "com.android.vending.AssetBrowserActivity");
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(it);
//			 SharedPreferences mShared = context.getSharedPreferences("data", Context.MODE_PRIVATE);
//	        Editor editor = mShared.edit();
//	        editor.putBoolean("RATE", true);
//	        editor.commit();
    }

    /**
     * 获取当前应用程序的版本号
     */
    public static int getAppVersion(Context context) {
        int version = 0;
        try {
            version = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(MyUtils.class.getName()
                    + "the application not found");
        }
        return version;
    }
/*
    public static void showSUM() {
        StringBuffer sb = new StringBuffer();
        for (int j = 16; j < 28; j++) {
            for (int i = j; i < 600; i += 13) {
                sb.append("C" + i + ",");
            }
            sb.append("\n");
        }
        Log.e(TAG, "showSUM: " + sb.toString());
    }*/

    /**
     * 默认显示toast位置
     *
     * @param context
     * @param words
     */
    public static void showToast(Context context, String words) {
        Toast.makeText(context, words, Toast.LENGTH_SHORT).show();
    }


    /**
     * 获取当前屏幕的截图
     *
     * @param activity
     * @param rightWidth
     * @return
     */
    public static Bitmap GetCurrentImage(Activity activity, int rightWidth, int bottomHiht) {
        // 获取屏幕
        View decorview = activity.getWindow().getDecorView();
        decorview.setDrawingCacheEnabled(true);
        decorview.buildDrawingCache();
        Bitmap bitmap = decorview.getDrawingCache();
        // 获取截取屏幕的宽高
        int w = MyUtils.getWindowWidth(activity) - rightWidth;
        int h = MyUtils.getWindowHeight(activity) - bottomHiht;
        // 截取
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h);
        // bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        decorview.destroyDrawingCache();
        decorview.setDrawingCacheEnabled(false);
        return bitmap;
    }

    /**
     * 获取SD卡下指定文件夹的绝对路径
     *
     * @return 返回SD卡下的指定文件夹的绝对路径
     */
    public static String getSavePath(String folderName) {
        return getSaveFolder(folderName).getAbsolutePath();
    }

    /**
     * 获取url中文件名字(bu包括文件的后缀名)
     *
     * @param url
     * @return
     */
    public static String getUrlFileName(String url) {
        String substring = null;
        String suffixes = "avi|mpeg|3gp|mp3|mp4|wav|jpeg|gif|jpg|png|apk|exe|txt|html|zip|java|doc";
        Pattern pat = Pattern.compile("[\\w]+[\\.](" + suffixes + ")");//正则判断
        Matcher mc = pat.matcher(url);//条件匹配
        while (mc.find()) {
            substring = mc.group();//截取文件名后缀名
        }
        return substring.substring(0, substring.indexOf("."));// 去掉后缀名
    }

    /**
     * 获取url中文件名字(包括文件的后缀名)
     *
     * @param url
     * @return
     */
    public static String getUrlFileName1(String url) {
        String substring = null;
        String suffixes = "avi|mpeg|3gp|mp3|mp4|wav|jpeg|gif|jpg|png|apk|exe|txt|html|zip|java|doc";
        Pattern pat = Pattern.compile("[\\w]+[\\.](" + suffixes + ")");//正则判断
        Matcher mc = pat.matcher(url);//条件匹配
        while (mc.find()) {
            substring = mc.group();//截取文件名后缀名
        }
        return substring;
    }

    /**
     * 获取链接的后缀名
     *
     * @return
     */
    public static String parseSuffix(String url) {
        final Pattern pattern = Pattern.compile("\\S*[?]\\S*");
        Matcher matcher = pattern.matcher(url);
        String[] spUrl = url.toString().split("/");
        int len = spUrl.length;
        String endUrl = spUrl[len - 1];
        if (matcher.find()) {
            String[] spEndUrl = endUrl.split("\\?");
            return spEndUrl[0].split("\\.")[1];
        }
        return endUrl.split("\\.")[1];
    }


    /***
     * 获取下载文件路径
     * 如果SD卡存在就放在/Android/data/包名/cache
     * 否则放在/ /data/data/包名/cache中
     *
     * @param context
     * @return
     */
    public static String getDownloadFileFolder(Context context) {
        if (checkSDcard()) {
            return getExternalCacheDir(context);
        } else {
            return getCacheDir(context);
        }
    }

    /**
     * 获取解压路径
     * 如果SD卡存在就放在/Android/data/包名/files
     * 否则放在/ /data/data/包名/files中
     *
     * @param context
     * @return
     */
    public static String getUpZipFileFolder(Context context) {
        if (checkSDcard()) {
            return getExternalFilesDir(context);
        } else {
            return getFilesDir(context);
        }
    }

    /**
     * 路径一般为: 外置储存卡路径(机型有关)/Android/data/包名/cache
     *
     * @param context
     * @return
     */
    public static String getExternalCacheDir(Context context) {
        return context.getExternalCacheDir().getAbsolutePath();
    }

    /**
     * 路径一般为:外置储存卡路径(机型有关)/Android/data/包名/files
     *
     * @return
     */
    private static String getExternalFilesDir(Context context) {
        return context.getExternalFilesDir(null).getAbsolutePath();
    }

    /**
     * 获取SD卡的绝对路径
     *
     * @return
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * 内置储存卡路径的获取
     * 路径一般为: /data/data/包名/files
     */
    private static String getFilesDir(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }

    /**
     * 内置储存卡路径的获取
     * 路径一般为: /data/data/包名/cache
     *
     * @param context
     * @return
     */
    public static String getCacheDir(Context context) {
        return context.getCacheDir().getAbsolutePath();
    }

    /**
     * 获取文件夹对象
     *
     * @return 返回SD卡下的指定文件夹对象，若文件夹不存在则创建
     */
    public static File getSaveFolder(String folderName) {
        File file = new File(getSDCardPath() + File.separator + folderName
                + File.separator);
        file.mkdirs();
        return file;
    }

    /**
     * 判断指定文件夹目录中是否存在指定文件
     *
     * @param folderPath
     * @param fileNmae
     * @return
     */
    public static boolean isFileExists(String folderPath, String fileNmae) {
        File file = new File(folderPath + File.separator + fileNmae);
        return file.exists();
    }

    /**
     * 判断下载的文件是否存在
     *
     * @param context
     * @param fileName
     * @return
     */
    public static boolean isFileExists(Context context, String fileName) {
        File file = new File(getDownloadFileFolder(context) + File.separator + fileName);
        return file.exists();
    }

    /**
     * 删除指定路径文件
     *
     * @param fileName
     */
    public static void delFile(String fileName) {
        File file = new File(fileName);
        if (file.isFile()) {
            file.delete();
        }
    }

    /**
     * 判断解压缩的图片文件是否有删除
     *
     * @param mContext
     * @param downloadFileName
     * @return
     * @throws JSONException
     */
    public static boolean isDownloadImageIntegrity(Context mContext, String downloadFileName) {
        String filePath = MyUtils.getUpZipFileFolder(mContext) + File.separator + downloadFileName
                + File.separator + downloadFileName + File.separator;
        if (!isFileExists(filePath, "readme.txt")) {
            return false;
        }
        boolean isDownloadImageIntegrity = true;
        String readmeString = readFile(filePath + "readme.txt");
        try {
            JSONObject jsonObject = new JSONObject(readmeString);
            int pic_num = Integer.parseInt(jsonObject.getString("pic_num"));
            JSONObject json = jsonObject.getJSONObject("e_list");
            Iterator<String> iterator = json.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                JSONObject jsonObject1 = json.getJSONObject(key);
                String pic = jsonObject1.getString("pic");
                if (!isFileExists(filePath, pic)) {
                    isDownloadImageIntegrity = false;
                    break;
                }
            }
        } catch (JSONException e) {
            isDownloadImageIntegrity = false;// 出现异常也表示图片有删除
            e.printStackTrace();
        }
        return isDownloadImageIntegrity;
    }

    /**
     * 从文件中读取文本
     *
     * @param filePath
     * @return
     */
    public static String readFile(String filePath) {
        InputStream is = null;
        try {
            is = new FileInputStream(filePath);
        } catch (Exception e) {
            throw new RuntimeException(MyUtils.class.getName()
                    + "readFile---->" + filePath + " not found");
        }
        return inputStream2String(is);
    }

    /**
     * 输入流转字符串
     *
     * @param is
     * @return 一个流中的字符串
     */
    public static String inputStream2String(InputStream is) {
        if (null == is) {
            return null;
        }
        StringBuilder resultSb = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            resultSb = new StringBuilder();
            String len;
            while (null != (len = br.readLine())) {
                resultSb.append(len);
            }
        } catch (Exception ex) {
        } finally {
            closeIO(is);
        }
        return null == resultSb ? null : resultSb.toString();
    }


    /**
     * 检测SD卡是否存在
     */
    public static boolean checkSDcard() {
        return Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState());
    }

    /**
     * 指定格式返回当前系统时间
     */
    public static String getDataTime(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(new Date());
    }

    /**
     * 判断文件是否下载完成（是否解压成功）
     *
     * @param downloadFileName
     * @return
     */
    public static boolean getDownloadOver(Context context, String downloadFileName) {
        String fileName = downloadFileName.split(".")[0];
        String folderPath = getDownloadFileFolder(context) + File.separator + fileName;
        File folderFile = new File(folderPath);
        if (!folderFile.exists()) {
            return false;
        }
        return false;
    }

    //  质量压缩
    public static void compressImageToFile(Bitmap bmp, File file) {
        // 0-100 100为不压缩
        int options = 100;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 把压缩后的数据存放到baos中
        bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 设置图片的采样率，降低图片像素
    public static void compressBitmap(String filePath, File file) {
        // 数值越高，图片像素越低
        int inSampleSize = 4;
        BitmapFactory.Options options = new BitmapFactory.Options();
        //采样率
        options.inSampleSize = inSampleSize;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 把压缩后的数据存放到baos中
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 回收一个未被回收的Bitmap
     *
     * @param bitmap
     */
    public static void doRecycledIfNot(Bitmap bitmap) {
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    /**
     * 图片压缩方法：（使用compress的方法） <br>
     * <p>
     * <b>注意</b> bitmap实际并没有被回收，如果你不需要，请手动置空 <br>
     * <b>说明</b> 如果bitmap本身的大小小于maxSize，则不作处理
     *
     * @param bitmap  要压缩的图片
     * @param maxSize 压缩后的大小，单位kb
     */
    public static Bitmap imageZoom(Bitmap bitmap, double maxSize) {
        // 将bitmap放至数组中，意在获得bitmap的大小（与实际读取的原文件要大）
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 格式、质量、输出流
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos);
        byte[] b = baos.toByteArray();
        // 将字节换成KB
        double mid = b.length / 1024;
        // 获取bitmap大小 是允许最大大小的多少倍
        double i = mid / maxSize;
        // 判断bitmap占用空间是否大于允许最大空间 如果大于则压缩 小于则不压缩
        if (i > 1) {
            // 缩放图片 此处用到平方根 将宽带和高度压缩掉对应的平方根倍
            // （保持宽高不变，缩放后也达到了最大占用空间的大小）
            bitmap = scaleWithWH(bitmap, bitmap.getWidth() / Math.sqrt(i),
                    bitmap.getHeight() / Math.sqrt(i));
        }
        return bitmap;
    }

    /***
     * 图片的缩放方法,如果参数宽高为0,则不处理<br>
     * <p>
     * <b>注意</b> src实际并没有被回收，如果你不需要，请手动置空
     *
     * @param src ：源图片资源
     * @param w   ：缩放后宽度
     * @param h   ：缩放后高度
     */
    public static Bitmap scaleWithWH(Bitmap src, double w, double h) {
        if (w == 0 || h == 0 || src == null) {
            return src;
        } else {
            // 记录src的宽高
            int width = src.getWidth();
            int height = src.getHeight();
            // 创建一个matrix容器
            Matrix matrix = new Matrix();
            // 计算缩放比例
            float scaleWidth = (float) (w / width);
            float scaleHeight = (float) (h / height);
            // 开始缩放
            matrix.postScale(scaleWidth, scaleHeight);
            // 创建缩放后的图片
            return Bitmap.createBitmap(src, 0, 0, width, height, matrix, true);
        }
    }

    /**
     * 把uri转为File对象
     */
    @SuppressLint("NewApi")
    public static File uri2File(Activity aty, Uri uri) {
        if (getSDKVersion() < 11) {
            // 在API11以下可以使用：managedQuery
            String[] proj = {MediaStore.Images.Media.DATA};
            @SuppressWarnings("deprecation")
            Cursor actualimagecursor = aty.managedQuery(uri, proj, null, null,
                    null);
            int actual_image_column_index = actualimagecursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualimagecursor.moveToFirst();
            String img_path = actualimagecursor
                    .getString(actual_image_column_index);
            return new File(img_path);
        } else {
            // 在API11以上：要转为使用CursorLoader,并使用loadInBackground来返回
            String[] projection = {MediaStore.Images.Media.DATA};
            CursorLoader loader = new CursorLoader(aty, uri, projection, null,
                    null, null);
            Cursor cursor = loader.loadInBackground();
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return new File(cursor.getString(column_index));
        }
    }

    /**
     * 获取手机系统SDK版本
     *
     * @return 如API 17 则返回 17
     */
    public static int getSDKVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }

    /**
     * 解压缩
     *
     * @param zipFileName     压缩文件的路径名
     * @param outputDirectory 解压缩文件的路径名（不包含文件名路径）
     * @throws IOException
     */
    public static boolean unzip(String zipFileName, String outputDirectory) {
        boolean isSucceed = true;
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(zipFileName);
            Enumeration e = zipFile.entries();
            ZipEntry zipEntry = null;
            File dest = new File(outputDirectory);
            dest.mkdirs();
            while (e.hasMoreElements()) {
                zipEntry = (ZipEntry) e.nextElement();
                String entryName = zipEntry.getName();
                InputStream in = null;
                FileOutputStream out = null;
                try {
                    if (zipEntry.isDirectory()) {
                        String name = zipEntry.getName();
                        name = name.substring(0, name.length() - 1);
                        File f = new File(outputDirectory + File.separator
                                + name);
                        f.mkdirs();
                    } else {
                        int index = entryName.lastIndexOf("\\");
                        if (index != -1) {
                            File df = new File(outputDirectory + File.separator
                                    + entryName.substring(0, index));
                            df.mkdirs();
                        }
                        index = entryName.lastIndexOf("/");
                        if (index != -1) {
                            File df = new File(outputDirectory + File.separator
                                    + entryName.substring(0, index));
                            df.mkdirs();
                        }
                        File f = new File(outputDirectory + File.separator
                                + zipEntry.getName());
                        // f.createNewFile();
                        in = zipFile.getInputStream(zipEntry);
                        out = new FileOutputStream(f);
                        int c;
                        byte[] by = new byte[1024];
                        while ((c = in.read(by)) != -1) {
                            out.write(by, 0, c);
                        }
                        out.flush();
                    }
                } catch (IOException ex) {
                    isSucceed = false;
                    ex.printStackTrace();
                    //throw new IOException("解压失败：" + ex.toString());
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException ex) {
                        }
                    }
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException ex) {
                        }
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            isSucceed = false;
            //throw new IOException("解压失败：" + ex.toString());
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException ex) {
                }
            }
        }
        return isSucceed;
    }

    /**
     * 图片写入文件
     *
     * @param bitmap   图片
     * @param filePath 文件路径
     * @return 是否写入成功
     */
    public static boolean bitmapToFile(Bitmap bitmap, String filePath) {
        boolean isSuccess = false;
        if (bitmap == null) {
            return isSuccess;
        }
        File file = new File(filePath.substring(0,
                filePath.lastIndexOf(File.separator)));
        if (!file.exists()) {
            file.mkdirs();
        }

        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(filePath),
                    8 * 1024);
            isSuccess = bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            closeIO(out);
        }
        return isSuccess;
    }

    /**
     * 关闭流
     *
     * @param closeables
     */
    public static void closeIO(Closeable... closeables) {
        if (null == closeables || closeables.length <= 0) {
            return;
        }
        for (Closeable cb : closeables) {
            try {
                if (null == cb) {
                    continue;
                }
                cb.close();
            } catch (IOException e) {
                throw new RuntimeException(
                        MyUtils.class.getClass().getName(), e);
            }
        }
    }

    /**
     * 获取手机屏幕的宽度
     *
     * @param ctx
     * @return
     */
    public static int getWindowWidth(Context ctx) {
        int width = 0;
        WindowManager wm = (WindowManager) ctx
                .getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();// 手机屏幕的宽度
        return width;
    }

    /**
     * 获取手机屏幕的高度
     *
     * @param ctx
     * @return
     */
    public static int getWindowHeight(Context ctx) {
        int width = 0;
        WindowManager wm = (WindowManager) ctx
                .getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getHeight();// 手机屏幕的高度
        return width;
    }

    /**
     * dip转化成px
     *
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * sp转化成px
     *
     * @param context
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

}

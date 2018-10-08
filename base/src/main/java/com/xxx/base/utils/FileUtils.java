package com.xxx.base.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class FileUtils {

    public static final String ROOT_DIR = "na";
    public static final String DOWNLOAD_DIR = "download";
    public static final String CACHE_DIR = "cache";
    public static final String IMG_DIR = "img";

    /**
     * 判断SD卡是否挂载
     */
    public static boolean isSDCardAvailable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取下载目录
     */
    public static String getDownloadDir() {
        return getDir(DOWNLOAD_DIR);
    }

    /**
     * 获取缓存目录
     */
    public static String getCacheDir() {
        return getDir(CACHE_DIR);
    }


    /**
     * 获取应用目录，当SD卡存在时，获取SD卡上的目录，当SD卡不存在时，获取应用的cache目录
     */
    public static String getDir(String name) {
        StringBuilder sb = new StringBuilder();
        if (isSDCardAvailable()) {
            sb.append(getExternalStoragePath());
        } else {
            sb.append(getCachePath());
        }
        sb.append(name);
        sb.append(File.separator);
        String path = sb.toString();
        if (createDirs(path)) {
            return path;
        } else {
            return null;
        }
    }


    /**
     * 获取图片目录
     * @return
     */
    public static String getImgDir() {
        return getDir(IMG_DIR);
    }

    /**
     * 获取SD下的应用目录
     */
    public static String getExternalStoragePath() {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        sb.append(File.separator);
        sb.append(ROOT_DIR);
        sb.append(File.separator);
        createDirs(sb.toString());
        return sb.toString();
    }

    /**
     * 获取应用的cache目录
     */
    public static String getCachePath() {
        File f = AppUtils.getContext().getCacheDir();
        if (null == f) {
            return null;
        } else {
            return f.getAbsolutePath() + "/";
        }
    }

    /**
     * 创建文件夹
     */
    public static boolean createDirs(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists() || !file.isDirectory()) {
            return file.mkdirs();
        }
        return true;
    }

    /**
     * 复制文件，可以选择是否删除源文件
     */
    public static boolean copyFile(String srcPath, String destPath, boolean deleteSrc) {
        File srcFile = new File(srcPath);
        File destFile = new File(destPath);
        return copyFile(srcFile, destFile, deleteSrc);
    }

    /**
     * 复制文件，可以选择是否删除源文件
     */
    public static boolean copyFile(File srcFile, File destFile, boolean deleteSrc) {
        if (!srcFile.exists() || !srcFile.isFile()) {
            return false;
        }
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(srcFile);
            out = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];
            int i = -1;
            while ((i = in.read(buffer)) > 0) {
                out.write(buffer, 0, i);
                out.flush();
            }
            if (deleteSrc) {
                srcFile.delete();
            }
        } catch (Exception e) {
            LogUtils.e(e);
            return false;
        } finally {
            IOUtils.close(out);
            IOUtils.close(in);
        }
        return true;
    }

    /**
     * 判断文件是否可写
     */
    public static boolean isWriteable(String path) {
        try {
            if (StringUtils.isEmpty(path)) {
                return false;
            }
            File f = new File(path);
            return f.exists() && f.canWrite();
        } catch (Exception e) {
            LogUtils.e(e);
            return false;
        }
    }

    /**
     * 修改文件的权限,例如"777"等
     */
    public static void chmod(String path, String mode) {
        try {
            String command = "chmod " + mode + " " + path;
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(command);
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    /**
     * 把数据写入文件
     *
     * @param is       数据流
     * @param path     文件路径
     * @param recreate 如果文件存在，是否需要删除重建
     * @return 是否写入成功
     */
    public static boolean writeFile(InputStream is, String path, boolean recreate) {
        boolean res = false;
        File f = new File(path);
        FileOutputStream fos = null;
        try {
            if (recreate && f.exists()) {
                f.delete();
            }
            if (!f.exists() && null != is) {
                File parentFile = new File(f.getParent());
                parentFile.mkdirs();
                int count = -1;
                byte[] buffer = new byte[1024];
                fos = new FileOutputStream(f);
                while ((count = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, count);
                }
                res = true;
            }
        } catch (Exception e) {
            LogUtils.e(e);
        } finally {
            IOUtils.close(fos);
            IOUtils.close(is);
        }
        return res;
    }

    /**
     * 把字符串数据写入文件
     *
     * @param content 需要写入的字符串
     * @param path    文件路径名称
     * @param append  是否以添加的模式写入
     * @return 是否写入成功
     */
    public static boolean writeFile(byte[] content, String path, boolean append) {
        boolean res = false;
        File f = new File(path);
        RandomAccessFile raf = null;
        try {
            if (f.exists()) {
                if (!append) {
                    f.delete();
                    f.createNewFile();
                }
            } else {
                f.createNewFile();
            }
            if (f.canWrite()) {
                raf = new RandomAccessFile(f, "rw");
                raf.seek(raf.length());
                raf.write(content);
                res = true;
            }
        } catch (Exception e) {
            LogUtils.e(e);
        } finally {
            IOUtils.close(raf);
        }
        return res;
    }

    /**
     * 把字符串数据写入文件
     *
     * @param content 需要写入的字符串
     * @param path    文件路径名称
     * @param append  是否以添加的模式写入
     * @return 是否写入成功
     */
    public static boolean writeFile(String content, String path, boolean append) {
        return writeFile(content.getBytes(), path, append);
    }

    /**
     * 把键值对写入文件
     *
     * @param filePath 文件路径
     * @param key      键
     * @param value    值
     * @param comment  该键值对的注释
     */
    public static void writeProperties(String filePath, String key, String value, String comment) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(filePath)) {
            return;
        }
        FileInputStream fis = null;
        FileOutputStream fos = null;
        File f = new File(filePath);
        try {
            if (!f.exists() || !f.isFile()) {
                f.createNewFile();
            }
            fis = new FileInputStream(f);
            Properties p = new Properties();
            p.load(fis);// 先读取文件，再把键值对追加到后面
            p.setProperty(key, value);
            fos = new FileOutputStream(f);
            p.store(fos, comment);
        } catch (Exception e) {
            LogUtils.e(e);
        } finally {
            IOUtils.close(fis);
            IOUtils.close(fos);
        }
    }

    /**
     * 根据值读取
     */
    public static String readProperties(String filePath, String key, String defaultValue) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(filePath)) {
            return null;
        }
        String value = null;
        FileInputStream fis = null;
        File f = new File(filePath);
        try {
            if (!f.exists() || !f.isFile()) {
                f.createNewFile();
            }
            fis = new FileInputStream(f);
            Properties p = new Properties();
            p.load(fis);
            value = p.getProperty(key, defaultValue);
        } catch (IOException e) {
            LogUtils.e(e);
        } finally {
            IOUtils.close(fis);
        }
        return value;
    }

    /**
     * 把字符串键值对的map写入文件
     */
    public static void writeMap(String filePath, Map<String, String> map, boolean append, String comment) {
        if (map == null || map.size() == 0 || StringUtils.isEmpty(filePath)) {
            return;
        }
        FileInputStream fis = null;
        FileOutputStream fos = null;
        File f = new File(filePath);
        try {
            if (!f.exists() || !f.isFile()) {
                f.createNewFile();
            }
            Properties p = new Properties();
            if (append) {
                fis = new FileInputStream(f);
                p.load(fis);// 先读取文件，再把键值对追加到后面
            }
            p.putAll(map);
            fos = new FileOutputStream(f);
            p.store(fos, comment);
        } catch (Exception e) {
            LogUtils.e(e);
        } finally {
            IOUtils.close(fis);
            IOUtils.close(fos);
        }
    }

    /**
     * 把字符串键值对的文件读入map
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Map<String, String> readMap(String filePath, String defaultValue) {
        if (StringUtils.isEmpty(filePath)) {
            return null;
        }
        Map<String, String> map = null;
        FileInputStream fis = null;
        File f = new File(filePath);
        try {
            if (!f.exists() || !f.isFile()) {
                f.createNewFile();
            }
            fis = new FileInputStream(f);
            Properties p = new Properties();
            p.load(fis);
            map = new HashMap<String, String>((Map) p);// 因为properties继承了map，所以直接通过p来构造一个map
        } catch (Exception e) {
            LogUtils.e(e);
        } finally {
            IOUtils.close(fis);
        }
        return map;
    }

    /**
     * 改名
     */
    public static boolean copy(String src, String des, boolean delete) {
        File file = new File(src);
        if (!file.exists()) {
            return false;
        }
        File desFile = new File(des);
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(file);
            out = new FileOutputStream(desFile);
            byte[] buffer = new byte[1024];
            int count = -1;
            while ((count = in.read(buffer)) != -1) {
                out.write(buffer, 0, count);
                out.flush();
            }
        } catch (Exception e) {
            LogUtils.e(e);
            return false;
        } finally {
            IOUtils.close(in);
            IOUtils.close(out);
        }
        if (delete) {
            file.delete();
        }
        return true;
    }

    /**
     * 在缓存目录中查找是否存在对应路径的gif图片
     *
     * @return
     */
    public static boolean findGifFile(String gifPath) {
        //拼接路径
        String path = getGifpath(gifPath);
        File gifFile = new File(path);
        // 返回是否存在
        return gifFile.exists();
    }

    /**
     * 通过gif图片地址，返回在缓存目录中的对应路径
     *
     * @param gifPath
     * @return gif文件路径
     */
    public static String getGifpath(String gifPath) {
        return getExternalStoragePath() + "gif" + File.separator + "GIF_" + gifPath.substring(gifPath.lastIndexOf("/") + 1, gifPath.indexOf(".gif") + 4);
    }

    /**
     * 保存gif到本地
     *
     * @param gifByte
     */
    public static void saveGif(byte[] gifByte, String gifPath) {
        writeFile(new ByteArrayInputStream(gifByte), getGifpath(gifPath), true);
    }

    /**
     * 保存bitmap到本地
     *
     * @param bitName bitmap名字
     * @param mBitmap
     * @throws IOException
     */
    public static void saveBitmap(String bitName, Bitmap mBitmap) {
        File f = new File(getExternalStoragePath() + "img/" + bitName + ".png");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存bitmap到本地
     *
     * @param mBitmap
     */
    public static String saveBitmap(Bitmap mBitmap) {


        String dir = getExternalStoragePath() + "cacheImage" + File.separator;
        createDirs(dir);
        String path = dir + "shareImage_"+ System.currentTimeMillis() +".jpg";
        File file = new File(path);


        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return path;
    }


    /**
     * sp
     *
     * @return
     */
    private static SharedPreferences sp;

    public static SharedPreferences getSharedPreferences() {
        if (sp == null) {
            sp = AppUtils.getContext().getSharedPreferences(ROOT_DIR, Context.MODE_PRIVATE);
        }
        return sp;
    }

    /**
     * 把字符串进行 md5加密
     *
     * @param password 被加密的字符串
     * @return 加密后的字符串
     */
    public static String md5Encoded(String password) {
        try {
            MessageDigest mDigest = MessageDigest.getInstance("md5");
            byte[] bys = mDigest.digest(password.getBytes());
            //
            StringBuilder sb = new StringBuilder();
            for (byte b : bys) {
                String temp = Integer.toHexString((int) ((b & 0xff) / 0.9));//修
                if (temp.length() == 1) {
                    //补0
                    sb.append("0");
                }
                sb.append(temp);
            }
            System.out.println(sb);
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取文件的MD5码
     *
     * @param file 源文件
     * @return MD5
     */
    public static String getFileMD5(File file) {

        //信息摘要
        try {
            MessageDigest mDigest = MessageDigest.getInstance("MD5");

            //读文件
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            int l = 0;
            byte[] bys = new byte[1024];
            while ((l = bis.read(bys)) != -1) {
                mDigest.update(bys, 0, l);
            }
            //把文件摘要成 字节数组
            byte[] result = mDigest.digest();
            //md5
            StringBuilder sb = new StringBuilder();
            for (byte b : result) {
                String temp = Integer.toHexString(b & 0xff);
                //补位
                if (temp.length() == 1) {
                    sb.append("0");
                }
                sb.append(temp);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 保存图片数据到应用缓存，返回保存路径
     *
     * @param image
     * @param url
     * @return
     */
    public static String saveImage(byte[] image, String url) {

        String path = createImageFilePath(url);

        BufferedOutputStream bufferedOutputStream = null;
        try {
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File(path)));
            bufferedOutputStream.write(image);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return path;
    }

    /**
     * 根据图片url生成图片路径 (缓存目录)
     * @param url
     * @return
     */
    private static String createImageFilePath(String url){
        if (TextUtils.isEmpty(url)) return "";

        String dir = getExternalStoragePath() + "cacheImage" + File.separator + "adImage" + File.separator;
        createDirs(dir);
        if (url.contains(".gif")){
            return dir + "I.gif" + md5Encoded(url);
        }else {
            return dir + "I.p" + md5Encoded(url);
        }
    }

    /**
     * 判断缓存目录对应文件是否存在，根据url存储的
     *
     * @param url
     * @return
     */
    public static boolean isExist(String url) {
        String path = createImageFilePath(url);
        if (TextUtils.isEmpty(path)) return false;
        File file = new File(path);
        return file.exists();
    }

    /**
     * 通过url得到保存文件路径
     * @param url
     * @return
     */
    public static String getImagePath(String url){
        return createImageFilePath(url);
    }

    /**
     * 通过指定格式的时间，判断时间差，day  -->  yyyy-MM-dd kk:mm:ss
     * startTime - endTime
     * @param startTime
     * @param endTime
     * @return
     */
    public static int getTimeDiff(String startTime, String endTime) throws Exception {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        Date startDate = null;
        try {
            startDate = simpleDateFormat.parse(startTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date endDate = null;
        try {
            endDate = simpleDateFormat.parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (startDate == null || endDate == null) throw new Exception("时间格式错误");

        //天数差
        GregorianCalendar cal1 = new GregorianCalendar();
        GregorianCalendar cal2 = new GregorianCalendar();
        cal1.setTime(startDate);
        cal2.setTime(endDate);
        double day = (cal1.getTimeInMillis() - cal2.getTimeInMillis()) / (1000 * 3600 * 24);

        return (int) day;
    }

    /**
     * 是否在指定时间范围内 yyyy-MM-dd kk:mm:ss
     * @return
     */
    public static boolean isInRange(String startTime, String endTime, String currentTime) throws Exception {
        if (getTimeDiff(startTime, endTime) > 0) return false;

        if ((getTimeDiff(startTime, currentTime) <= 0) && (getTimeDiff(endTime, currentTime) >= 0)){
            return true;
        }
        return false;
    }


    /**
     * 通过指定格式的时间，判断时间差，day  -->  yyyy-MM-dd kk:mm:ss
     * startTime - endTime
     * @param startTime
     * @param endTime
     * @return
     */
    public static int getTimeDiffForHour(String startTime, String endTime) throws Exception {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        Date startDate = null;
        try {
            startDate = simpleDateFormat.parse(startTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date endDate = null;
        try {
            endDate = simpleDateFormat.parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (startDate == null || endDate == null) throw new Exception("时间格式错误");

        //天数差
        GregorianCalendar cal1 = new GregorianCalendar();
        GregorianCalendar cal2 = new GregorianCalendar();
        cal1.setTime(startDate);
        cal2.setTime(endDate);
        double day = (cal1.getTimeInMillis() - cal2.getTimeInMillis()) / (1000 * 3600 * 24);

        return (int) day;
    }

}

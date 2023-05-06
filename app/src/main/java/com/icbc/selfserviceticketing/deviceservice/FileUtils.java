package com.icbc.selfserviceticketing.deviceservice;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * adb shell am start -n com.yishengkj.testtools/MainActivity
 * Created by Administrator on 2017/12/14.
 */

public class FileUtils {

    private static final String TAG = "FileUtils";

    public static boolean writeFile(File file, String content) {
        OutputStream out = null;
        boolean success = false;
        try {
            out = new FileOutputStream(file);
            out.write(content.getBytes());
            out.close();
            success = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return success;
    }

    public static String readFile(File file) {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String readline = "";
            while ((readline = br.readLine()) != null) {
                sb.append(readline + "\n");
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }



    public static void setValueToProp(String key, String val) {
        try {
            Class<?> classType = Class.forName("android.os.SystemProperties");
            Method method = classType.getDeclaredMethod("set", String.class, String.class);
            method.invoke(classType, key, val);
        } catch (ClassNotFoundException var4) {
            var4.printStackTrace();
        } catch (NoSuchMethodException var5) {
            var5.printStackTrace();
        } catch (InvocationTargetException var6) {
            var6.printStackTrace();
        } catch (Exception var7) {
            var7.printStackTrace();
        }

    }

    public static String getValueFromProp(String key) {
        String value = "";

        try {
            Class<?> classType = Class.forName("android.os.SystemProperties");
            Method getMethod = classType.getDeclaredMethod("get", String.class);
            value = (String)getMethod.invoke(classType, key);
        } catch (Exception var4) {
        }

        return value;
    }

    public static String getValueFromProp(String key, String def)
    {
        String ret = def;
        try
        {
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            Method mthd = clazz.getMethod("get", new Class[] { String.class, String.class });
            mthd.setAccessible(true);
            Object obj = mthd.invoke(clazz, new Object[] { key, def });
            if (obj != null && obj instanceof String)
            {
                ret = (String) obj;

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();

        }

        return ret;
    }


}

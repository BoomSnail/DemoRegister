package com.knigego.nimo.demoregister.util;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ThinkPad on 2017/3/31.
 */

public class BitmapUtil {

    public static boolean bitmapToFile(Bitmap bitmap, String imagePath) {
        if (bitmap == null || imagePath == null) {
            return false;
        }
        File file = new File(imagePath);
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
            }
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return false;
        }

        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);

            outputStream.flush();
            outputStream.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}

package com.knigego.nimo.demoregister.util;

import java.io.File;

/**
 * Created by ThinkPad on 2017/4/2.
 */

public class FileHelper {

    //遍历删除
    public static void delete(File mediaFile) {
        if (mediaFile != null && mediaFile.exists()) {
            if (mediaFile.isFile()) {//如果是文件则删除
                mediaFile.delete();
            } else {
                if (mediaFile.isDirectory()) {//如果是目录，则获取目录里面的文件或者目录，循环遍历删除
                    File[] files;
                    if ((files = mediaFile.listFiles()) == null || files.length == 0) {
                        mediaFile.delete();
                        return;
                    }
                    for (int i = 0; i < files.length; i++) {
                        delete(files[i]);
                    }

                    mediaFile.delete();
                }
            }
        }
    }
}

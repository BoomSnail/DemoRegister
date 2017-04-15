package com.knigego.nimo.demoregister;


import java.io.File;

/**
 * Created by ThinkPad on 2017/3/23.
 */

public class JavaTest {

    public static void main(String[] args) {
        File file = new File("D:/QQ");
        String[] lists = file.list();
        for (int i = 0; i < lists.length; i++) {
            System.out.println(lists[i]);
        }

        System.out.println("-------------------------------");

        File[] files = file.listFiles();

        for (int i = 0; i < files.length; i++) {
            System.out.println(files[i]);
        }
    }

//    private static int getCount(int c, int d) {
//        int itemCount = 1;
//        int count;
//        count = (itemCount / 3 + (itemCount < 9 ? 1 : 0)) * c//item总高度
//                + (itemCount / 3 - (itemCount < 9 ? 0 : 1)) * d;
//        return count;
//    }


}

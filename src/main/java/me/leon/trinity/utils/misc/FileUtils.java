package me.leon.trinity.utils.misc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class FileUtils {
    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static String prettyPrint(String s) {
        return gson.toJson(new JsonParser().parse(s));
    }

    public static void makeIfDoesntExist(String file) {
        final File file1 = new File(file);
        if(!file1.exists()) {
            file1.mkdirs();
        }
    }

    public static void makeIfDoesntExist(File file) {
        if(!file.exists()) {
            file.mkdirs();
        }
    }

    public static PrintWriter writer(String path, String name) {
        try {
            makeIfDoesntExist(path);
            return new PrintWriter(new FileOutputStream(new File(path.endsWith("/") ? path + name : path + "/" + name)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static PrintWriter writer(File path, String name) {
        try {
            makeIfDoesntExist(path);
            return new PrintWriter(new FileOutputStream(new File(path.getAbsolutePath().endsWith("/") ? path + name : path + "/" + name)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File[] list(String path) {
        return new File(path).listFiles();
    }
}

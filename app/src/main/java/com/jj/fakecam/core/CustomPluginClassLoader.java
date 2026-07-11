package com.jj.fakecam.core;

import android.util.Log;
import dalvik.system.DexClassLoader;

public class CustomPluginClassLoader extends DexClassLoader {
    private static final String TAG = "FaceCam_ClassLoader";

    public CustomPluginClassLoader(String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, librarySearchPath, parent);
        Log.d(TAG, "Оқшауланған ClassLoader дайын.");
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Log.d(TAG, "Класс іздеу: " + name);
        try {
            return super.findClass(name);
        } catch (ClassNotFoundException e) {
            return super.loadClass(name, false);
        }
    }
}

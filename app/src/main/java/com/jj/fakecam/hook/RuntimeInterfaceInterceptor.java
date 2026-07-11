package com.jj.fakecam.hook;

import android.util.Log;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RuntimeInterfaceInterceptor {
    private static final String TAG = "FaceCam_Interceptor";

    @SuppressWarnings("unchecked")
    public static <T> T createProxy(final T realService, Class<T> interfaceClass) {
        Log.d(TAG, "Интерфейс үшін прокси құрылуда: " + interfaceClass.getName());

        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Log.d(TAG, "[HOOKED] Шақырылған әдіс: " + method.getName());
                        return method.invoke(realService, args);
                    }
                }
        );
    }
}

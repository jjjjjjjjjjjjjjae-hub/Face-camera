package com.jj.fakecam.hook;

import android.util.Log;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RuntimeInterfaceInterceptor {
    private static final String TAG = "FaceCam_Interceptor";

    public interface LogListener {
        void onLogReceived(String message);
    }

    private static LogListener uiListener;

    public static void setLogListener(LogListener listener) {
        uiListener = listener;
    }

    public static void logToUI(String message) {
        Log.d(TAG, message);
        if (uiListener != null) {
            uiListener.onLogReceived(message);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T createProxy(final T realService, Class<T> interfaceClass) {
        logToUI("[PROXY]: Интерфейс үшін прокси құрылуда -> " + interfaceClass.getSimpleName());

        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        logToUI("[HOOKED EVENT]: Әдіс ұсталды -> " + method.getName() + "()");

                        if (method.getName().startsWith("get") || method.getName().startsWith("acquire")) {
                            logToUI("[ИНФО]: Деректерді беру сәті сәтті тіркелді!");
                        }

                        return method.invoke(realService, args);
                    }
                }
        );
    }
}

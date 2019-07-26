package study.hank.com.ipc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.lang.reflect.Method;

import study.hank.com.ipc.model.Parameter;
import study.hank.com.ipc.model.Request;
import study.hank.com.ipc.model.Response;

/**
 * IPC 服务通信
 * 既然涉及到通信，那么肯定需要AIDL咯，先去定AIDL
 */
public abstract class IpcService extends Service {

    private static final String TAG = "IpcServiceTag";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    private Gson gson = new Gson();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new IIpcService.Stub() {//返回一个binder对象,让客户端可以binder对象来调用服务端的方法
            @Override
            public Response send(Request request) throws RemoteException {
                //当客户端调用了send之后
                //IPC框架层应该要 反射执行服务端业务类的指定方法,并且视情况返回不同的回应
                //客户端会告诉框架，我要执行哪个类的哪个方法，我传什么参数
                String serviceId = request.getServiceId();
                String methodName = request.getMethodName();
                Object[] paramObjs = restoreParams(request.getParameters());
                //所有准备就绪，可以开始反射调用了？
                //先获取Method
                Method method = Registry.getInstance().findMethod(serviceId, methodName, paramObjs);
                switch (request.getType()) {
                    case Request.TYPE_CREATE_INSTANCE:
                        try {
                            Object instance = method.invoke(null, paramObjs);
                            Registry.getInstance().putObject(serviceId, instance);
                            return new Response("业务类对象生成成功", true);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return new Response("业务类对象生成失败", false);
                        }
                    case Request.TYPE_BUSINESS_METHOD:
                        Object o = Registry.getInstance().getObject(serviceId);
                        if (o != null) {
                            try {
                                Log.d(TAG, "1:methodName:" + method.getName());
                                for (int i = 0; i < paramObjs.length; i++) {
                                    Log.d(TAG, "1:paramObjs     " + paramObjs[i]);
                                }
                                Object res = method.invoke(o, paramObjs);
                                Log.d(TAG, "2");
                                return new Response(gson.toJson(res), true);
                            } catch (Exception e) {
                                return new Response("业务方法执行失败" + e.getMessage(), false);
                            }
                        }
                        Log.d(TAG, "3");
                        break;
                }
                return null;
            }
        };
    }

    private Object[] restoreParams(Parameter[] parameters) {
        Object[] objects = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            try {//gson这个东西，貌似可以把对象转json串，也可以反过来
                objects[i] = gson.fromJson(parameter.getValue(), Class.forName(parameter.getType()));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return objects;
    }

    /**
     * 暂时我还没有看出，这几个预留的接口有什么用，
     * 如果有两个客户端，需要连接到这个IPC通讯的Service，需要使用两个Service,两份注册表
     */
    public static class IpcService0 extends IpcService {
    }

    public static class IpcService1 extends IpcService {
    }

    public static class IpcService2 extends IpcService {
    }
}

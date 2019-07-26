package study.hank.com.ipc;

import android.content.Context;

import java.lang.reflect.Proxy;

import study.hank.com.ipc.model.Request;
import study.hank.com.ipc.model.Response;

/**
 * 框架层与使用者交互的唯一窗口
 */
public class Ipc {

    /**
     * @param business
     */
    public static void register(Class<?> business) {
        //注册是一个单独过程，所以单独提取出来，放在一个类里面去
        Registry.getInstance().register(business);//注册机是一个单例，启动服务端，
        // 就会存在一个注册机对象，唯一，不会随着服务的绑定解绑而受影响
    }

    /**
     * 使用binder连接IPC服务
     *
     * @param service
     */
    public static void connect(Context context, String packageName, Class<? extends IpcService> service) {
        Channel.getInstance().bind(context, packageName, service);
    }

    //再提供一个2个参数的
    public static void connect(Context context, Class<? extends IpcService> service) {
        Channel.getInstance().bind(context, null, service);
    }

    /**
     * @param ipcService0Class 服务service的class
     * @param classType        业务接口的class
     * @param <T>
     * @return
     */
    public static <T> T getInstance(Class<IpcService.IpcService0> ipcService0Class, Class<T> classType) {
        return getInstanceWithName(ipcService0Class, classType, "getInstance");
    }

    /**
     * @param service
     * @param classType
     * @param getInstanceMethodName
     * @param params
     * @param <T>                   泛型，
     * @return
     */
    public static <T> T getInstanceWithName(Class<? extends IpcService> service,
                                            Class<T> classType, String getInstanceMethodName, Object... params) {

        //这里之前不是创建了一个binder么，用binder去调用远程方法，在服务端创建业务类对象并保存起来
        if (!classType.isInterface()) {
            throw new RuntimeException("getInstanceWithName方法 此处必须传接口的class");
        }
        ServiceId serviceId = classType.getAnnotation(ServiceId.class);
        if (serviceId == null) {
            throw new RuntimeException("接口没有使用指定ServiceId注解");
        }
        Response response = Channel.getInstance().send(Request.TYPE_CREATE_INSTANCE, service, serviceId.value(), getInstanceMethodName, params);
        if (response.isSuccess()) {
            //如果服务端的业务类对象创建成功，那么我们就构建一个代理对象，实现RPC
            return (T) Proxy.newProxyInstance(
                    classType.getClassLoader(), new Class[]{classType},
                    new IpcInvocationHandler(service, serviceId.value()));
        }
        return null;
    }
}

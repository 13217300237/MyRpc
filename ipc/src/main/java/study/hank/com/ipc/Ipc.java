package study.hank.com.ipc;

import android.content.Context;

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
        connect(context, null, service);
    }

}

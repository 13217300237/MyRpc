package study.hank.com.ipc;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import java.util.concurrent.ConcurrentHashMap;

public class Channel {
    private static final Channel ourInstance = new Channel();

    public static Channel getInstance() {
        return ourInstance;
    }

    private Channel() {
    }

    /**
     * 考虑app内外的调用,因为外部的调用需要传入包名
     */
    public void bind(Context context, String packageName, Class<? extends IpcService> service) {
        Log.d(TAG, "bind: start");
        Intent i;
        if (!TextUtils.isEmpty(packageName)) {
            i = new Intent();
            i.setComponent(new ComponentName(packageName, service.getName()));
        } else {
            i = new Intent(context, service);
        }
        Log.d(TAG, "bind:" + service);
        context.bindService(i, new IpcConnection(service), Context.BIND_AUTO_CREATE);
    }

    /**
     * 考虑到多重连接的情况，把获取到的binder对象保存到map中，每一个服务一个binder
     */
    private ConcurrentHashMap<Class<? extends IpcService>, IIpcService> binders = new ConcurrentHashMap<>();

    private class IpcConnection implements ServiceConnection {

        private final Class<? extends IpcService> mService;

        public IpcConnection(Class<? extends IpcService> service) {
            this.mService = service;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IIpcService binder = IIpcService.Stub.asInterface(service);
            binders.put(mService, binder);//给不同的客户端进程预留不同的binder对象
            Log.d(TAG, "onServiceConnected:" + mService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binders.remove(mService);
            Log.d(TAG, "onServiceDisconnected:" + mService);
        }
    }

    String TAG = "ChannelTag";
}

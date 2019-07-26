package study.hank.com.ipc;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import study.hank.com.ipc.model.Parameter;
import study.hank.com.ipc.model.Request;
import study.hank.com.ipc.model.Response;

public class Channel {
    String TAG = "ChannelTag";
    private static final Channel ourInstance = new Channel();

    /**
     * 考虑到多重连接的情况，把获取到的binder对象保存到map中，每一个服务一个binder
     */
    private ConcurrentHashMap<Class<? extends IpcService>, IIpcService> binders = new ConcurrentHashMap<>();

    public static Channel getInstance() {
        return ourInstance;
    }

    private Channel() {
    }

    /**
     * 考虑app内外的调用,因为外部的调用需要传入包名
     */
    public void bind(Context context, String packageName, Class<? extends IpcService> service) {
        Intent intent;
        if (!TextUtils.isEmpty(packageName)) {
            intent = new Intent();
            Log.d(TAG, "bind:" + packageName + "-" + service.getName());
            intent.setClassName(packageName, service.getName());
        } else {
            intent = new Intent(context, service);
        }
        Log.d(TAG, "bind:" + service);
        context.bindService(intent, new IpcConnection(service), Context.BIND_AUTO_CREATE);
    }

    private class IpcConnection implements ServiceConnection {

        private final Class<? extends IpcService> mService;


        public IpcConnection(Class<? extends IpcService> service) {
            this.mService = service;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IIpcService binder = IIpcService.Stub.asInterface(service);
            binders.put(mService, binder);//给不同的客户端进程预留不同的binder对象
            Log.d(TAG, "onServiceConnected:" + mService + ";bindersSize=" + binders.size());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            binders.remove(mService);
            Log.d(TAG, "onServiceDisconnected:" + mService + ";bindersSize=" + binders.size());
        }

    }

    public Response send(int type, Class<? extends IpcService> service, String serviceId, String methodName, Object[] params) {
        Response response;
        Request request = new Request(type, serviceId, methodName, makeParams(params));
        Log.d(TAG, ";bindersSize=" + binders.size());
        IIpcService iIpcService = binders.get(service);
        try {
            response = iIpcService.send(request);
            Log.d(TAG, "1 " + response.isSuccess() + "-" + response.getResult());
        } catch (RemoteException e) {
            e.printStackTrace();
            response = new Response(null, false);
            Log.d(TAG, "2");
        } catch (NullPointerException e) {
            response = new Response("没有找到binder", false);
            Log.d(TAG, "3");
        }
        return response;
    }

    private static Gson gson = new Gson();

    private Parameter[] makeParams(Object[] params) {
        Parameter[] ps;
        if (params != null) {
            ps = new Parameter[params.length];
            for (int i = 0; i < params.length; i++) {
                Object o = params[i];
                String type = o.getClass().getName();
                String value = gson.toJson(o);
                ps[i] = new Parameter(type, value);
            }
        } else {
            ps = new Parameter[0];
        }
        return ps;
    }


}

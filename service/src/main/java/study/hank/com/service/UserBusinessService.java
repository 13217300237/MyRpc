package study.hank.com.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import study.hank.com.ipc.Ipc;
import study.hank.com.service.business.UserBusiness;

public class UserBusinessService extends Service {

    public static String userName = "zhou";
    public static String password = "hankZhou";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        UserBusiness.getInstance().setDefault(userName, password);
        //这是一个登录接口，那么我在这里先定义可以登录的用户名和密码,后面会用来模拟登录
        Ipc.register(UserBusiness.class);
    }
}

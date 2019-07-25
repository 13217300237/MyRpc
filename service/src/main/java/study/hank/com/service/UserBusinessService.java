package study.hank.com.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import study.hank.com.ipc.Ipc;
import study.hank.com.service.business.UserBusiness;

public class UserBusinessService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //这是一个登录接口，那么我在这里先定义可以登录的用户名和密码,后面会用来模拟登录
        UserBusiness.getInstance().setDefault("zhou", "hankZhou");
        Ipc.register(UserBusiness.class);
    }
}

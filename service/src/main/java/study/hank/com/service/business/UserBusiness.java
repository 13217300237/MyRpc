package study.hank.com.service.business;

import android.text.TextUtils;
import android.util.Log;

import study.hank.com.ipc.ServiceId;

/**
 * 业务接口
 */
@ServiceId("UserBusiness")
public class UserBusiness implements IUserBusiness {
    private String mUsername;
    private String mPassword;

    private UserBusiness() {
    }

    private static UserBusiness instance = new UserBusiness();

    public static UserBusiness getInstance() {
        return instance;
    }

    public void setDefault(String userName, String password) {
        this.mUsername = userName;
        this.mPassword = password;
    }

    @Override
    public boolean login(String userName, String password) {
        Log.d("UserBusinessTag", "参数:" + userName + "-" + password);
        Log.d("UserBusinessTag", "本地的:" + mUsername + "-" + mPassword);
        if (TextUtils.isEmpty(userName) && TextUtils.isEmpty(password)) {
            return false;
        }
        return userName.equals(mUsername) && password.equals(mPassword);
    }
}

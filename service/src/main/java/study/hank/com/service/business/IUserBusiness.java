package study.hank.com.service.business;

import study.hank.com.ipc.ServiceId;

@ServiceId("UserBusiness")
public interface IUserBusiness {
    boolean login(String userName,String password);
}

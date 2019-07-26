package study.hank.com.client;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import study.hank.com.client.business.IUserBusiness;
import study.hank.com.ipc.Ipc;
import study.hank.com.ipc.IpcService;

public class MainActivity extends AppCompatActivity {
    private String remotePkgName = "study.hank.com.UserBusinessService";
    private Class<? extends IpcService> targetServiceClz = IpcService.IpcService0.class;

    private String userName = "zhou";
    private String password = "hankZhou";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvUserName = findViewById(R.id.userName);
        TextView tvPwd = findViewById(R.id.password);
        tvUserName.setText("用户名：" + userName);
        tvPwd.setText("密码：" + password);

        //客户端，需要连接到 通信框架中的IpcService01
        Ipc.connect(this, remotePkgName, targetServiceClz);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test(v);
            }
        });

    }

    public void test(View view) {
        //这里就要使用到 多进程RPC方式调用
        IUserBusiness business = Ipc.getInstanceWithName(targetServiceClz, IUserBusiness.class, "getInstance");
        boolean loginRes = business.login(userName, password);
        Toast.makeText(this, loginRes ? "登录成功" : "登录失败", Toast.LENGTH_SHORT).show();
    }
}

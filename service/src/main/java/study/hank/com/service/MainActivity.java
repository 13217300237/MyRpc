package study.hank.com.service;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import study.hank.com.ipc.Ipc;
import study.hank.com.ipc.IpcService;
import study.hank.com.service.business.UserBusiness;

/**
 * 服务端
 */
public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, UserBusinessService.class));

        TextView tvUserName = findViewById(R.id.userName);
        TextView tvPwd = findViewById(R.id.password);
        tvUserName.setText(UserBusinessService.userName);
        tvPwd.setText(UserBusinessService.password);

        Log.d("ChannelTag", "服务端mainActivity");
        Ipc.connect(this, IpcService.IpcService0.class);//为什么service1不能启动呢?
        Ipc.connect(this, IpcService.IpcService1.class);
    }
}

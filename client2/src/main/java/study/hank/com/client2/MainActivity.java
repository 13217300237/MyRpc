package study.hank.com.client2;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import study.hank.com.ipc.Ipc;
import study.hank.com.ipc.IpcService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //客户端，需要连接到 通信框架中的IpcService1
        Ipc.connect(this, "study.hank.com.service", IpcService.IpcService1.class);
    }
}

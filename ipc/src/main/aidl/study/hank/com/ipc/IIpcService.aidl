// IIpcService.aidl
package study.hank.com.ipc;

// Declare any non-default types here with import statements
import study.hank.com.ipc.model.Request;
import study.hank.com.ipc.model.Response;

interface IIpcService {
    Response send(in Request request);
}

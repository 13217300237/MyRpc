package study.hank.com.ipc.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 客户端向服务端发Request，需要传什么？
 * 客户端需要告诉框架，我客户端需要调用哪个业务类的哪个方法，并且把参数值都传过来
 */
public class Request implements Parcelable {

    private int type;
    /**
     * 创建业务类实例,并且保存到注册表中
     */
    public final static int TYPE_CREATE_INSTANCE = 0;
    /**
     * 执行普通业务方法
     */
    public final static int TYPE_BUSINESS_METHOD = 1;

    public int getType() {
        return type;
    }

    private String serviceId;
    private String methodName;
    private Parameter[] parameters;

    protected Request(Parcel in) {
        serviceId = in.readString();
        methodName = in.readString();
        parameters = in.createTypedArray(Parameter.CREATOR);
        type = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(serviceId);
        dest.writeString(methodName);
        dest.writeTypedArray(parameters, flags);
        dest.writeInt(type);
    }

    public static final Creator<Request> CREATOR = new Creator<Request>() {
        @Override
        public Request createFromParcel(Parcel in) {
            return new Request(in);
        }

        @Override
        public Request[] newArray(int size) {
            return new Request[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Parameter[] getParameters() {
        return parameters;
    }

    public void setParameters(Parameter[] parameters) {
        this.parameters = parameters;
    }

    public Request(String serviceId, String methodName, Parameter[] parameters) {
        this.serviceId = serviceId;
        this.methodName = methodName;
        this.parameters = parameters;
    }
}

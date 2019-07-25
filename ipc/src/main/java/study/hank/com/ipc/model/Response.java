package study.hank.com.ipc.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 服务端给 客户端的回应
 * 请求是否成功，返回的结果是什么？
 */
public class Response implements Parcelable {

    private String result;//结果json串
    private boolean isSuccess;//是否成功

    public Response(String result, boolean isSuccess) {
        this.result = result;
        this.isSuccess = isSuccess;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    protected Response(Parcel in) {
        result = in.readString();
        isSuccess = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(result);
        dest.writeByte((byte) (isSuccess ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Response> CREATOR = new Creator<Response>() {
        @Override
        public Response createFromParcel(Parcel in) {
            return new Response(in);
        }

        @Override
        public Response[] newArray(int size) {
            return new Response[size];
        }
    };
}

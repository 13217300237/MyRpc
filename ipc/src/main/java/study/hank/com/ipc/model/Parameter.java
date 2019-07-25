package study.hank.com.ipc.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 参数也需要传递，序列化之后的结果吧
 */
public class Parameter implements Parcelable {

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private String type;//参数类型 obj.getClass
    private String value;//参数值序列化之后的json

    public Parameter(String type, String value) {
        this.type = type;
        this.value = value;
    }

    protected Parameter(Parcel in) {
        type = in.readString();
        value = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Parameter> CREATOR = new Creator<Parameter>() {
        @Override
        public Parameter createFromParcel(Parcel in) {
            return new Parameter(in);
        }

        @Override
        public Parameter[] newArray(int size) {
            return new Parameter[size];
        }
    };
}

package study.hank.com.ipc;

import android.util.Log;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OK，业务注册机，完成
 */
public class Registry {
    private static final Registry ourInstance = new Registry();

    public static Registry getInstance() {
        return ourInstance;
    }

    private Registry() {
    }

    /**
     * 业务表
     */
    private ConcurrentHashMap<String, Class<?>> mBusinessMap
            = new ConcurrentHashMap<>();
    /**
     * 业务方法表, 二维map，key为serviceId字符串值，value为 一个方法map  - key，方法名；value
     */
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Method>> mMethodMap
            = new ConcurrentHashMap<>();

    /**
     * 业务类的实例,要反射执行方法，如果不是静态方法的话，还是需要一个实例的，所以在这里把实例也保存起来
     */
    private ConcurrentHashMap<String, Object> mObjectMap = new ConcurrentHashMap<>();

    /**
     * 业务注册
     * 将业务class的class和method对象都保存起来，以便后面反射执行需要的method
     */
    public void register(Class<?> business) {
        //这里有个设计，使用注解，标记所使用的业务类是属于哪一个业务ID，在本类中，ID唯一
        ServiceId serviceId = business.getAnnotation(ServiceId.class);//获取那个类头上的注解
        if (serviceId == null) {
            throw new RuntimeException("业务类必须使用ServiceId注解");
        }
        String value = serviceId.value();
        mBusinessMap.put(value, business);//把业务类的class对象用 value作为key，保存到map中

        //然后要保存这个business类的所有method对象
        ConcurrentHashMap<String, Method> tempMethodMap = mMethodMap.get(value);//先看看方法表中是否已经存在整个业务对应的方法表
        if (tempMethodMap == null) {
            tempMethodMap = new ConcurrentHashMap<>();//不存在，则new
            mMethodMap.put(value, tempMethodMap);// 并且将它存进去
        }
        for (Method method : business.getMethods()) {
            String methodName = method.getName();
            Class<?>[] parameterTypes = method.getParameterTypes();
            String methodMapKey = getMethodMapKey(methodName, parameterTypes);
            tempMethodMap.put(methodMapKey, method);
        }

        //现在看看两个map里面都是一些什么东西
        Set<Map.Entry<String, Class<?>>> entrySet = mBusinessMap.entrySet();
        for (Map.Entry<String, Class<?>> e : entrySet) {
            Log.d("registerTag-BusinessMap", "" + e.getKey() + "-" + e.getValue());
        }

        Set<Map.Entry<String, ConcurrentHashMap<String, Method>>> entries = mMethodMap.entrySet();
        for (Map.Entry<String, ConcurrentHashMap<String, Method>> e2 : entries) {
            String key = e2.getKey();
            Log.d("registerTag-MethodMap", "开始遍历：" + key + "的方法");
            ConcurrentHashMap<String, Method> svalue = e2.getValue();
            Set<Map.Entry<String, Method>> entries1 = svalue.entrySet();
            for (Map.Entry<String, Method> em : entries1) {
                Log.d("registerTag-MethodMap", em.getKey());
            }
        }
    }


    /**
     * 把方法名map中key的构建过程独立出来
     *
     * @param methodName
     * @param paras
     * @param <T>
     * @return
     */
    private <T> String getMethodMapKey(String methodName, T[] paras) {
        StringBuilder builder = new StringBuilder(methodName + "(");
        for (int i = 0; i < paras.length; i++) {
            if (i != 0) {
                builder.append(",");
            }
            builder.append(paras[i].getClass().getName()); //把参数列表拼装起来，一起作为一个key
        }
        builder.append(")");
        return builder.toString();
    }


    /**
     * 如何寻找到一个Method？
     * 参照上面的构建过程，
     *
     * @param serviceId
     * @param methodName
     * @param paras
     * @return
     */
    public Method findMethod(String serviceId, String methodName, Object[] paras) {
        ConcurrentHashMap<String, Method> map = mMethodMap.get(serviceId);
        String methodMapKey = getMethodMapKey(methodName, paras); //同样的方式，构建一个StringBuilder
        return map.get(methodMapKey);
    }

    /**
     * 放入一个实例
     *
     * @param serviceId
     * @param object
     */
    public void putObject(String serviceId, Object object) {
        mObjectMap.put(serviceId, object);
    }

    /**
     * 取出一个实例
     *
     * @param serviceId
     */
    public Object getObject(String serviceId) {
        return mObjectMap.get(serviceId);
    }

}

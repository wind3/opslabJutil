package evilp0s.Bean;

import evilp0s.ValidUtil;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.MarshalledObject;
import java.util.Map;
import java.util.Set;

/**
 * JavaBean相关的一些操作
 */
public class BeanUtil {

    private static Map<String, BeanStruct> simpleProperties(Object obj) {
        return BeanFactory.BEAN_SIMPLE_PROPERTIES.get(obj.getClass().getName());
    }

    private static Map<String, BeanStruct> simplePropertiesIgnore(Object obj) {
        return BeanFactory.BEAN_SIMPLE_PROPERTIESIGNORE.get(obj.getClass().getName());
    }

    private static Method getReadMethod(Object obj, String pro) {
        BeanStruct st = (BeanStruct) simpleProperties(obj).get(pro);
        return st.getReadMethod();
    }

    private static Method getWriteMethod(Object obj, String pro) {
        BeanStruct st = (BeanStruct) simpleProperties(obj).get(pro);
        return st.getWriteMethod();
    }

    private static Method getReadMethodIgnore(Object obj, String pro) {
        BeanStruct st = (BeanStruct) simplePropertiesIgnore(obj).get(pro);
        return st.getReadMethod();
    }

    private static Method getWriteMethodIgnore(Object obj, String pro) {
        BeanStruct st = (BeanStruct) simplePropertiesIgnore(obj).get(pro);
        return st.getWriteMethod();
    }

    private static Object readMethod(Object bean, Method readMethod) throws InvocationTargetException, IllegalAccessException {
        return readMethod.invoke(bean);
    }

    private static void writeMethod(Object bean, Method writeMethod, Object value) throws InvocationTargetException, IllegalAccessException {
        writeMethod.invoke(bean, value);
    }


    /**
     * 添加Bean到BeanFactory的解析范围中
     *
     * @param obj
     */
    public static void add(Object obj) {
        try {
            BeanFactory.add(obj);
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加Bean到BeanFactory的解析范围中
     *
     * @param clazz
     */
    public static void add(Class clazz) {
        try {
            BeanFactory.add(clazz);
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * 判断属性是否存在
     *
     * @param obj
     * @param pro
     * @return
     */
    public static boolean hasProperty(Object obj, String pro) {
        Map map = simpleProperties(obj);
        return map.containsKey(pro);
    }


    /**
     * 判断自己定义的而非继承的属性pro是否存在
     *
     * @param obj
     * @param pro
     * @return
     */
    public static boolean hasDeclaredProperty(Object obj, String pro) {
        Map map = simpleProperties(obj);
        BeanStruct st = (BeanStruct) map.get(pro);
        if (ValidUtil.isValid(st)) {
            return st.isDeclared();
        }
        return false;
    }

    /**
     * 判断属性是否存在忽略大小写
     *
     * @param obj
     * @param pro
     * @return
     */
    public static boolean hasPropertyIgnoreCase(Object obj, String pro) {
        Map map = simplePropertiesIgnore(obj);
        return map.containsKey(pro.toLowerCase());
    }


    /**
     * 使用自定义的过滤器
     *
     * @param obj
     * @param pro
     * @param filter
     * @return
     */
    public static boolean hasPropertyFilter(Object obj, String pro, PropertyFilter filter) {
        pro = filter.Properties(pro);
        Map map = simpleProperties(obj);
        if (ValidUtil.isValid(map)) {
            Set<String> set = map.keySet();
            for (String s : set) {
                if (pro.equals(filter.Properties(s))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取对象的属性
     *
     * @param bean
     * @param pro
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Object getProperty(Object bean, String pro) throws InvocationTargetException, IllegalAccessException {
        return readMethod(bean, getReadMethod(bean, pro));
    }

    /**
     * 获取对象的属性
     *
     * @param bean
     * @param pro
     * @return 如果发生异常返回空
     */
    public static Object getPropertyPeaceful(Object bean, String pro) {
        Object result = null;
        try {
            result = readMethod(bean, getReadMethod(bean, pro));
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取对象自定义的属性
     *
     * @param bean
     * @param pro
     * @return
     */
    public static Object getDeclaredPropertyPeaceful(Object bean, String pro) throws InvocationTargetException, IllegalAccessException {
        Object result = null;
        if (hasDeclaredProperty(bean, pro)) {
            result = readMethod(bean, getReadMethod(bean, pro));
        }
        return result;
    }

    /**
     * 获取对象自定义的属性
     *
     * @param bean
     * @param pro
     * @return
     */
    public static Object getDeclaredProperty(Object bean, String pro) {
        Object result = null;
        if (hasDeclaredProperty(bean, pro)) {
            try {
                result = readMethod(bean, getReadMethod(bean, pro));
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    /**
     * 获取对象的属性(忽略属性名字大小写)
     *
     * @param bean
     * @param pro
     * @return
     */
    public static Object getPropertyIgnoreCase(Object bean, String pro) throws InvocationTargetException, IllegalAccessException {
        return readMethod(bean, getReadMethodIgnore(bean, pro));
    }


    public static Object getPropertyIgnoreCasePeaceful(Object bean, String pro) {
        Object result = null;
        try {
            result = readMethod(bean, getReadMethodIgnore(bean, pro));
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 使用自定义的过滤器获取对象的属性获取对象的属性
     *
     * @param bean
     * @param pro
     * @param filter
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static Object getPropertyFilter(Object bean, String pro, PropertyFilter filter) throws InvocationTargetException, IllegalAccessException {
        Object result = null;
        pro = filter.Properties(pro);
        Map map = simpleProperties(bean);
        if (ValidUtil.isValid(map)) {
            Set<String> set = map.keySet();
            for (String s : set) {
                if (pro.equals(filter.Properties(s))) {
                    result = readMethod(bean, getReadMethod(bean, s));
                }
            }
        }
        return result;
    }

    /**
     * 使用自定义的过滤器获取对象的属性
     *
     * @param bean
     * @param pro
     * @param filter
     * @return
     */
    public static Object getPropertyFilterPeaceful(Object bean, String pro, PropertyFilter filter) {
        Object result = null;
        pro = filter.Properties(pro);
        Map map = simpleProperties(bean);
        if (ValidUtil.isValid(map)) {
            Set<String> set = map.keySet();
            try {
                for (String s : set) {
                    if (pro.equals(filter.Properties(s))) {
                        result = readMethod(bean, getReadMethod(bean, s));
                    }
                }
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    /**
     * 设置对象的属性
     *
     * @param bean
     * @param pro
     * @param value
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public void setProperty(Object bean, String pro, Object value) throws InvocationTargetException, IllegalAccessException {
        writeMethod(bean, getWriteMethod(bean, pro), value);
    }

    /**
     * 设置对象的属性
     *
     * @param bean
     * @param pro
     * @param value
     */
    public void setPropertyPeaceful(Object bean, String pro, Object value) {
        try {
            writeMethod(bean, getWriteMethod(bean, pro), value);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    /**
     * 设置对象的自定义属性
     *
     * @param bean
     * @param pro
     * @param value
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public void setDeclaredProperty(Object bean, String pro, Object value) throws InvocationTargetException, IllegalAccessException {
        if (hasDeclaredProperty(bean, pro)) {
            writeMethod(bean, getWriteMethod(bean, pro), value);
        }
    }

    /**
     * 设置对象的自定义属性
     *
     * @param bean
     * @param pro
     * @param value
     */
    public void setDeclaredPropertyPeaceful(Object bean, String pro, Object value) {
        if (hasDeclaredProperty(bean, pro)) {
            try {
                writeMethod(bean, getWriteMethod(bean, pro), value);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 设置对象的属性忽略大小写
     *
     * @param bean
     * @param pro
     * @param value
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public void setPropertyIgnoreCase(Object bean, String pro, Object value) throws InvocationTargetException, IllegalAccessException {
        writeMethod(bean, getWriteMethodIgnore(bean, pro), value);
    }

    /**
     * 设置对象的属性忽略大小写
     *
     * @param bean
     * @param pro
     * @param value
     */
    public void setPropertyIgnoreCasePeaceful(Object bean, String pro, Object value) {
        try {
            writeMethod(bean, getWriteMethodIgnore(bean, pro), value);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    /**
     * 使用自定义的filter进行属性设值
     *
     * @param bean
     * @param pro
     * @param value
     * @param filter
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public void setPropertyFilter(Object bean, String pro, Object value, PropertyFilter filter) throws InvocationTargetException, IllegalAccessException {
        pro = filter.Properties(pro);
        Map map = simpleProperties(bean);
        if (ValidUtil.isValid(map)) {
            Set<String> set = map.keySet();
            for (String s : set) {
                if (pro.equals(filter.Properties(s))) {
                    writeMethod(bean, getWriteMethodIgnore(bean, pro), value);
                }
            }

        }
    }

    /**
     * 使用自定义的filter进行属性设值
     *
     * @param bean
     * @param pro
     * @param value
     * @param filter
     */
    public void setPropertyFilterPeaceful(Object bean, String pro, Object value, PropertyFilter filter) {
        pro = filter.Properties(pro);
        Map map = simpleProperties(bean);
        if (ValidUtil.isValid(map)) {
            Set<String> set = map.keySet();
            try {
                for (String s : set) {
                    if (pro.equals(filter.Properties(s))) {
                        writeMethod(bean, getWriteMethodIgnore(bean, pro), value);
                    }
                }
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
    }


    /**
     * 拷贝对象指定的属性
     * @param srcBean
     * @param destBean
     * @param pros
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public void copyProperty(Object srcBean, Object destBean, String[] pros) throws InvocationTargetException, IllegalAccessException {
        if (ValidUtil.isValid(pros)) {
            for (String s : pros) {
                Object value = readMethod(srcBean, getReadMethod(srcBean, s));
                writeMethod(destBean, getWriteMethod(destBean, s), value);
            }
        }
    }

    public void copyPropertyPeaceful(Object srcBean, Object destBean, String[] pros) {
        if (ValidUtil.isValid(pros)) {
            try {
                for (String s : pros) {
                    writeMethod(destBean, getWriteMethod(destBean, s), readMethod(srcBean, getReadMethod(srcBean, s)));
                }
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void copyProperties(Object srcBean, Object destBean) {
        Map srcMap =  simpleProperties(srcBean);
        Map dstMap =  simpleProperties(destBean);
    }

    public void copyProperties(Object srcBean, Object destBean, Map<String, String> propertesMap) {

    }

    public void copyProperties(Object srcBean, Object destBean, PropertyFilter filter) {

    }
}

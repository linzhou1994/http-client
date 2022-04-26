package com.http.client.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ////////////////////////////////////////////////////////////////////
 * //                          _ooOoo_                               //
 * //                         o8888888o                              //
 * //                         88" . "88                              //
 * //                         (| ^_^ |)                              //
 * //                         O\  =  /O                              //
 * //                      ____/`---'\____                           //
 * //                    .'  \\|     |//  `.                         //
 * //                   /  \\|||  :  |||//  \                        //
 * //                  /  _||||| -:- |||||-  \                       //
 * //                  |   | \\\  -  /// |   |                       //
 * //                  | \_|  ''\---/''  |   |                       //
 * //                  \  .-\__  `-`  ___/-. /                       //
 * //                ___`. .'  /--.--\  `. . ___                     //
 * //              ."" '<  `.___\_<|>_/___.'  >'"".                  //
 * //            | | :  `- \`.;`\ _ /`;.`/ - ` : | |                 //
 * //            \  \ `-.   \_ __\ /__ _/   .-` /  /                 //
 * //      ========`-.____`-.___\_____/___.-`____.-'========         //
 * //                           `=---='                              //
 * //      ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^        //
 * //         佛祖保佑           永无BUG           永不修改              //
 * //          佛曰:                                                  //
 * //                 写字楼里写字间，写字间里程序员;                      //
 * //                 程序人员写程序，又拿程序换酒钱.                      //
 * //                 酒醒只在网上坐，酒醉还来网下眠;                      //
 * //                 酒醉酒醒日复日，网上网下年复年.                      //
 * //                 但愿老死电脑间，不愿鞠躬老板前;                      //
 * //                 奔驰宝马贵者趣，公交自行程序员.                      //
 * //                 别人笑我忒疯癫，我笑自己命太贱;                      //
 * //                 不见满街漂亮妹，哪个归得程序员?                      //
 * ////////////////////////////////////////////////////////////////////
 *
 * @date : 2022/4/26 13:58
 * @author: linzhou
 * @description : ReflectUtil
 */
public class ReflectUtil {

    public static List<Class<?>> getAllSuperclass(Class<?> c) {
        List<Class<?>> superClassList = new ArrayList<>();
        while (c.getSuperclass() != Object.class) {
            superClassList.add(c.getSuperclass());
            c = c.getSuperclass();
        }
        return superClassList;
    }

    public static List<ReflectField> getReflectField(Object o) {
        return getReflectField(o, o.getClass());
    }

    public static List<ReflectField> getReflectField(Object o, Class<?> c) {
        Field[] declaredFields = c.getDeclaredFields();
        List<ReflectField> rlt = new ArrayList<>(declaredFields.length);
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            try {
                Object value = declaredField.get(o);
                ReflectField reflectField = new ReflectField();
                reflectField.setField(declaredField);
                reflectField.setaClass(c);
                reflectField.setValue(value);
                rlt.add(reflectField);
            } catch (IllegalAccessException e) {

            }
        }
        return rlt;
    }

    public static List<ReflectField> getAllFieldAndValue(Object o) {
        if (Objects.isNull(o)) {
            return new ArrayList<>();
        }
        List<Class<?>> allClass = getAllSuperclass(o.getClass());
        allClass.add(o.getClass());

        List<ReflectField> rlt = new ArrayList<>();
        for (Class<?> aClass : allClass) {
            rlt.addAll(getReflectField(o,aClass));
        }

        return rlt;
    }


    public static class ReflectField {
        private Class<?> aClass;
        private Object value;

        private Field field;

        public Class<?> getaClass() {
            return aClass;
        }

        public void setaClass(Class<?> aClass) {
            this.aClass = aClass;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public Field getField() {
            return field;
        }

        public void setField(Field field) {
            this.field = field;
        }
    }

}

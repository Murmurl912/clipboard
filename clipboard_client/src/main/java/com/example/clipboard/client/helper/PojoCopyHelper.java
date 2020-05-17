package com.example.clipboard.client.helper;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

public class PojoCopyHelper {

    public static <M> void merge(M target, M destination) {
        Field[] fields = destination.getClass().getFields();
        for(Field field : fields) {
            try {
                field.setAccessible(true);
                Object destinationValue = field.get(destination);
                Object targetValue = field.get(target);
                if(destinationValue == null) {
                    destinationValue = targetValue;
                } else if(targetValue != null) {  // source is not null
                    destinationValue = targetValue;
                }
                field.set(destination, destinationValue);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

}

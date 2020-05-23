package com.example.clipboard.server.helper;

import java.lang.reflect.Field;

public class CopyHelper {

    public static <M> void merge(M target, M destination) throws IllegalAccessException {
        Field[] fields = destination.getClass().getFields();
        for(Field field : fields) {
            field.setAccessible(true);
            Object destinationValue = field.get(destination);
            Object targetValue = field.get(target);
            if(destinationValue == null) {
                destinationValue = targetValue;
            } else if(targetValue != null) {  // source is not null
                destinationValue = targetValue;
            }
            field.set(destination, destinationValue);
        }
    }

}

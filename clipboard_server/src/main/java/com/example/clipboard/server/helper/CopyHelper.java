package com.example.clipboard.server.helper;

import java.lang.reflect.Field;

public class CopyHelper {

    public static <T> void copyIfDifferent(T source, T data) throws IllegalAccessException {
        Field[] fields = source.getClass().getFields();
        for(Field field : fields) {
            field.setAccessible(true);
            Object sourceValue = field.get(field);
            Object dataValue = field.get(data);
            if(sourceValue == null) {
                sourceValue = dataValue;
            } else if(dataValue != null) {  // source is not null
                sourceValue = dataValue;
            }
        }
    }

}

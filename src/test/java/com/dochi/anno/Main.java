package com.dochi.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;


@Target({
    ElementType.METHOD
})
@Retention(RetentionPolicy.RUNTIME) 
@Repeatable(RepeatedAnnos.class) 
@interface Value { 
    String text()   default "Hello"; 
    int num()       default 1; 
} 

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME) 
@interface RepeatedAnnos { 
    Value[] value(); 
}

public class Main {
    
    public Main() {
        
    }
    
    private static void getConnection() {
        StackTraceElement stack = Thread.currentThread().getStackTrace()[2];
        
        Class<?> clazz = null;
        Method method = null;
        Value[] values = null;
        try {
            String methodName = stack.getMethodName();
            
            clazz = Class.forName(stack.getClassName());
            method = clazz.getMethod(methodName);
            values = method.getAnnotationsByType(Value.class);
            
            if( values != null && values.length > 0 ) {
                for(Value value : values) {
                    System.out.printf("word: %s, value: %d \n",
                        value.text(),
                        value.num()
                    );
                }
            }
        } catch (NoSuchMethodException | SecurityException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
       
    @Value(text="First")
    @Value(text="Second", num = 2)
    public void newMethod(){
        getConnection();
    } 
    
    
    public static void main(String[] args) { 
        Main main = new Main();
        main.newMethod(); 
    } 
} 
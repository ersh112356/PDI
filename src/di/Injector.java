/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di;

import di.annotation.Inject;
import di.annotation.Injects;
import di.exception.InjectionException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 *
 * @author Eran
 * 
 * @param <T> - the type of the object to inject.
 */
public class Injector<T>{
    
    /**
     * All statics.
     * 
     */
    private Injector(){
    }
    
    /**
     * Try to create a new Object.
     * 
     * @param <T> - the type of the Object to inject.
     * @param name- a fully qualified name of the Class.
     * 
     * @return a new Object, or null if failed.
     * 
     * @exception InjectionException on errors.
     */
    public static <T> T of(String name){
        
        try
        {
            Class clss = Class.forName(name);
            T candidate = (T)clss.newInstance();
            
            return candidate;
        }
        catch(ClassNotFoundException | InstantiationException | IllegalAccessException e)
        {
            throw new InjectionException("An error occured.",e);
        }
    }
    
    /**
     * Try to create a new Object.
     * 
     * @param <T> - the type of the Object to inject.
     * @param name- a fully qualified name of the Class.
     * @param params- the parameters to inject into the constructor. 
     *                For instance: "Dummy", 0, "Third"...
     * 
     * @return a new Object, or null if failed.
     * 
     * @exception InjectionException on errors.
     */
    public static <T> T of(String name, Object... params){
        
        try
        {
            int len = params.length;
            Class[] types = new Class[len];
            
            for(int i=0;i<len;i++)
            {     
                types[i] = params[i].getClass();
            }
            
            Class clss = Class.forName(name);
            Constructor<T> cons = clss.getConstructor(types);
            T candidate = cons.newInstance(params);
            
            return candidate;
        }
        catch(ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
        {
            throw new InjectionException("An error occured.",e);
        }
    }
    
    /**
     * Try to create a new Object.
     * 
     * @param <T> - the type of the Object to inject.
     * 
     * @return a new Object, or null if failed.
     * 
     * @exception InjectionException on errors.
     */
    public static <T> T of(){
       
        return of(-1);
    }
    
    /**
     * Try to create a new Object.
     * 
     * @param <T> - the type of the Object to inject.
     * @param counter- in the case of a repeatable annotations, take only the right one.
     * 
     * @return a new Object, or null if failed.
     * 
     * @exception InjectionException on errors.
     */
    public static <T> T of(int counter){
       
        StackTraceElement[] e = Thread.currentThread().getStackTrace();
        int len = counter<0 ? 3 : 2;
        
        if(e.length<len)
        {
            throw new InjectionException("Got a bad parameter.");
        }
        
        String m = e[len].getMethodName();
        String c = e[len].getClassName();
        
        Inject annotated = getAnnotation(c,m,counter);
        String name = annotated.name();
        String[] values = annotated.values();

        if(values==null)
        {
            return of(name);
        }
        
        return of(name,values);
    }
    
    /**
     * Try to fetch the annotation here.
     * If the subclass wasn't annotated, an exception will be thrown.
     * 
     * @param c- a fully qualified name of the hosting class.
     * @param m- the name of the method.
     * @param counter- in the case of a repeatable annotations, take only the right one.
     * 
     * @return the tied annotation object.
     * 
     * @exception RuntimeException if the subclass wasn't annotated.
     */
    private static Inject getAnnotation(String c, String m, int counter){
        
        try
        {
            Class clss = Class.forName(c);
            Method[] methods = clss.getDeclaredMethods();
            
            for(Method method : methods)
            {
                if(method.getName().equals(m) && (method.isAnnotationPresent(Inject.class) || method.isAnnotationPresent(Injects.class)))
                {
                    Inject annotation = (Inject)method.getDeclaredAnnotation(Inject.class);
                    
                    if(annotation==null)
                    {   // A repeatable annotation. Take only the one we need to.
                        Injects ann = (Injects)method.getDeclaredAnnotation(Injects.class);
                        Inject[] injects = ann.value();
                        annotation = injects[counter];
                    }

                    return annotation;
                }
            }
            
            return null;
        }
        catch(ClassNotFoundException e)
        {
            throw new InjectionException("Bad method name",e);
        }
    }
}
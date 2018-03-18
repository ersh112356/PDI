/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.exception;

/**
 *
 * @author eran.s
 */
public class InjectionException extends RuntimeException{
 
    /**
     * The constructor.
     */
    public InjectionException(){
        
        super();
    }
    
    /**
     * The constructor.
     * 
     * @param message- a message to tie to the exception.
     */
    public InjectionException(String message){
        
        super(message);
    }
    
    /**
     * The constructor.
     * 
     * @param message- a message to tie to the exception.
     * @param e- the root cause of the exception.
     */
    public InjectionException(String message, Throwable e){
        
        super(message,e);
    }
}
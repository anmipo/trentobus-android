package com.anmipo.android.trentobus;

/**
 * Base class for application's exceptions
 * @author Andrei Popleteev
 */
public class TrentoBusException extends Exception {

    private int errorCode = -1;
    
    public TrentoBusException(String message) {
        super(message);
    }
    public TrentoBusException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode; 
    }
    public int getErrorCode() {
        return errorCode;
    }
}

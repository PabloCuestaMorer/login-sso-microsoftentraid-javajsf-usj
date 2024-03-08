/*
 * CustomException.java
 *
 * v1.0
 *
 * 16-feb-2024
 *
 * © Universidad San Jorge
 */
package es.usj.lg.exception;

/**
 * Excepcion propia
 *
 * @author Jose Luis Bailo
 */
public class CustomException extends Exception {

    /**
     * Creates a new instance of <code>CustomException</code> without detail
     * message.
     */
    public CustomException() {
    }

    /**
     * Constructs an instance of <code>CustomException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public CustomException(String msg) {
        super(msg);
    }
}

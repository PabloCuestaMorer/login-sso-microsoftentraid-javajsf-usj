/*
 * UsuarioDto.java
 *
 * v1.0
 *
 * 16-feb-2024
 *
 * © Universidad San Jorge
 */
package es.usj.lg.dto;

import java.io.Serializable;

/**
 * DTO de usuario
 *
 * @author Jose Luis Bailo
 */
public class UsuarioDto implements Cloneable, Serializable {

    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Object clone() {
        UsuarioDto clone = null;
        try {
            clone = (UsuarioDto) super.clone();
        } catch (CloneNotSupportedException e) {
            // No deberia ocurrir
        }
        return clone;
    }
}

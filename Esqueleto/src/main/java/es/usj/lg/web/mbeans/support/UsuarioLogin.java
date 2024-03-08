/*
 * UsuarioLogin.java
 *
 * v1.0
 *
 * 16-feb-2024
 *
 * © Universidad San Jorge
 */
package es.usj.lg.web.mbeans.support;

import es.usj.lg.dto.UsuarioDto;
import java.io.Serializable;
import org.apache.log4j.Logger;

/**
 * Datos del usuario o empresa logueada en la aplicación
 *
 * @author Jose Luis Bailo
 */
public class UsuarioLogin implements Serializable {

    protected Logger logger = Logger.getLogger(UsuarioLogin.class);
    private UsuarioDto usuario = new UsuarioDto();

    public UsuarioDto getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioDto usuario) {
        this.usuario = usuario;
    }

}

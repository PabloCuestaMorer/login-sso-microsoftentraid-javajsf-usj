/*
 * LoginForm.java
 *
 * v1.0
 *
 * 16-feb-2024
 *
 * © Universidad San Jorge
 */
package es.usj.lg.web.mbeans.backing;

import es.usj.lg.dto.UsuarioDto;
import es.usj.lg.web.comun.Constants;
import es.usj.lg.web.comun.Util;
import es.usj.lg.web.mbeans.support.UsuarioLogin;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import java.io.Serializable;
import java.util.Calendar;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

/**
 * Bean de login
 *
 * @author Jose Luis Bailo
 */
public class LoginForm implements Serializable {

    // <editor-fold defaultstate="collapsed" desc="Atributos">
    protected static final Logger logger = Logger.getLogger(LoginForm.class);
    private UsuarioDto usuario = new UsuarioDto();
    private UsuarioLogin usuarioLogin;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Propiedades">
    public UsuarioDto getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioDto usuario) {
        this.usuario = usuario;
    }

    public UsuarioLogin getUsuarioLogin() {
        return usuarioLogin;
    }

    public void setUsuarioLogin(UsuarioLogin usuarioLogin) {
        this.usuarioLogin = usuarioLogin;
    }

    public String getAnyo() {
        Calendar cal = Calendar.getInstance();
        return String.valueOf(cal.get(Calendar.YEAR));
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Acciones">
    @PostConstruct
    public void initMetadata() {
        //se ejecuta despues de construir el bean
    }

    public String submit() {
        try {
            if (!validarDatos()) {
                return "";
            }

            usuarioLogin.setUsuario(usuario);
            return "vacia?faces-redirect=true";

        } catch (Exception ex) {
            logger.error(ex);
            Util.showMsgGrowl("msgs", "ErrorGeneral", null, Util.getLocale(), FacesMessage.SEVERITY_ERROR);
            PrimeFaces.current().ajax().update("messages");
        }
        return "";
    }

    public boolean validarDatos() {
        try {
            boolean correcto = true;

            if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
                //Comprueba si ha introducido usuario
                Util.showMsgGrowl("msgs", "ErrorNombreUsuario", null, Util.getLocale(), FacesMessage.SEVERITY_ERROR);
                PrimeFaces.current().ajax().update("messages");
                correcto = false;
            } else if (usuario.getUsername().contains("@usj.es")) {
                //Debe indicarse sin @usj.es al final
                Util.showMsgGrowl("msgs", "Error_No_usjes", null, Util.getLocale(), FacesMessage.SEVERITY_ERROR);
                PrimeFaces.current().ajax().update("messages");
                correcto = false;
            } else if (!Util.compruebaNombreUsuario(usuario.getUsername())) {
                //Comprueba que solo se ha introducido mayusculas, minusculas y los simbolos .-_
                Util.showMsgGrowl("msgs", "ErrorNombreUsuario", null, Util.getLocale(), FacesMessage.SEVERITY_ERROR);
                PrimeFaces.current().ajax().update("messages");
                correcto = false;
            } else if (!usuario.getUsername().toLowerCase().equals(Constants.USUARIO)) {
                Util.showMsgGrowl("msgs", "ErrorNombreUsuario", null, Util.getLocale(), FacesMessage.SEVERITY_ERROR);
                PrimeFaces.current().ajax().update("messages");
                correcto = false;
            }

            if (usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
                Util.showMsgGrowl("msgs", "ContrasenaIncorrecta", null, Util.getLocale(), FacesMessage.SEVERITY_ERROR);
                PrimeFaces.current().ajax().update("messages");
                correcto = false;
            } else if (!usuario.getPassword().toLowerCase().equals(Constants.PASSWORD)) {
                Util.showMsgGrowl("msgs", "ContrasenaIncorrecta", null, Util.getLocale(), FacesMessage.SEVERITY_ERROR);
                PrimeFaces.current().ajax().update("messages");
                correcto = false;
            }

            return correcto;
        } catch (Exception e) {
            logger.error(e);
            return false;
        }
    }
    // </editor-fold>
}

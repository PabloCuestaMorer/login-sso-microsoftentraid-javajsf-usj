/*
 * AvisoForm.java
 *
 * v1.0
 *
 * 16-feb-2024
 *
 * © Universidad San Jorge
 */
package es.usj.lg.web.mbeans.backing;

import es.usj.lg.web.comun.Util;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.NavigationHandler;
import jakarta.faces.context.FacesContext;
import java.io.Serializable;
import org.apache.log4j.Logger;

/**
 * Bean de aviso
 *
 * @author Jose Luis Bailo
 */
public class AvisoForm implements Serializable {

    // <editor-fold defaultstate="collapsed" desc="Atributos">
    protected static final Logger logger = Logger.getLogger(AvisoForm.class);
    private String texto;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Propiedades">
    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Acciones">
    @PostConstruct
    public void initMetadata() {
        if (Util.flashScope().get("texto") != null) {
            texto = Util.flashScope().get("texto").toString();
        } else {
            FacesContext fc = FacesContext.getCurrentInstance();
            NavigationHandler nh = fc.getApplication().getNavigationHandler();
            nh.handleNavigation(fc, null, "Login");
        }
    }
    // </editor-fold>    
}

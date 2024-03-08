/*
 * Menu.java
 *
 * v1.0
 *
 * 16-feb-2024
 *
 * © Universidad San Jorge
 */
package es.usj.lg.web.mbeans.menu;

import es.usj.lg.web.comun.Util;
import java.io.Serializable;
import java.util.Calendar;

/**
 * Managed bean del menu de la aplicacion
 *
 * @author Jose Luis Bailo
 */
public class Menu implements Serializable {
    
    private String version;

    public String getVersion() {
        if (version == null) {
            version = Util.getVersion();
        }
        return version;
    }

    public String getAnyo() {
        Calendar cal = Calendar.getInstance();
        return String.valueOf(cal.get(Calendar.YEAR));
    }

    public String desconexion() {
        return Util.desconexion();
    }    

}

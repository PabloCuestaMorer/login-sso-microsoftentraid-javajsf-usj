/*
 * LoggedInCheck.java
 *
 * v1.0
 *
 * 16-feb-2024
 *
 * © Universidad San Jorge
 */
package es.usj.lg.seguridad;

import es.usj.lg.web.mbeans.support.UsuarioLogin;
import jakarta.faces.application.NavigationHandler;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.PhaseEvent;
import jakarta.faces.event.PhaseId;
import jakarta.faces.event.PhaseListener;
import java.io.Serializable;
import org.apache.log4j.Logger;

/**
 * Clase para controlar el acceso a las paginas que requieren usuario logueado
 *
 * @author Jose Luis Bailo
 */
public class LoggedInCheck implements PhaseListener, Serializable {

    protected static final Logger logger = Logger.getLogger(LoggedInCheck.class);

    @Override
    public void afterPhase(PhaseEvent pe) {
        FacesContext fc = pe.getFacesContext();
        
        boolean loginPage = fc.getViewRoot().getViewId().lastIndexOf("login") > -1;
        boolean errorPage = fc.getViewRoot().getViewId().lastIndexOf("error") > -1;
        boolean accessPage = fc.getViewRoot().getViewId().lastIndexOf("access") > -1;
        boolean notFoundPage = fc.getViewRoot().getViewId().lastIndexOf("notfound") > -1;
        boolean logoutPage = fc.getViewRoot().getViewId().lastIndexOf("logout") > -1;
        if (!loginPage && !errorPage && !accessPage && !notFoundPage && !logoutPage) {
            UsuarioLogin bean = (UsuarioLogin) fc.getApplication().evaluateExpressionGet(fc, "#{usuarioLogin}", UsuarioLogin.class);
            if (bean == null || bean.getUsuario() == null || bean.getUsuario().getUsername() == null || bean.getUsuario().getUsername().isEmpty()) {
                NavigationHandler nh = fc.getApplication().getNavigationHandler();
                nh.handleNavigation(fc, null, "Login");
            }
        }
    }

    @Override
    public void beforePhase(PhaseEvent pe) {
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }
}

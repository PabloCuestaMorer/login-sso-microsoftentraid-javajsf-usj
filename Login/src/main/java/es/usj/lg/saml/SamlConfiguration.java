/*
 * 
 */
package es.usj.lg.saml;

import com.onelogin.saml2.Auth;
import com.onelogin.saml2.settings.Saml2Settings;
import com.onelogin.saml2.settings.SettingsBuilder;
import jakarta.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Properties;

/**
 *
 * @author pablo
 */
public class SamlConfiguration {

    public static Saml2Settings createSaml2Settings() throws Exception {
        Properties prop = new Properties();
        // Load properties from file or another source
        prop.load(SamlConfiguration.class.getClassLoader().getResourceAsStream("onelogin.saml.properties"));

        // Create a settings builder
        SettingsBuilder settingsBuilder = new SettingsBuilder();

        // Build settings from properties
        Saml2Settings settings = settingsBuilder.fromProperties(prop).build();

        return settings;
    }

    public void samlLogin() {
        try {
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();

            Auth auth = new Auth(request, response);
            auth.login();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}

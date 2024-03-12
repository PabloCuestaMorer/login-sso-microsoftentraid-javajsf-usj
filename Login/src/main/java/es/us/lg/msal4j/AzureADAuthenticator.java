/*
 */
package es.us.lg.msal4j;

import com.microsoft.aad.msal4j.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author pablo
 */
public class AzureADAuthenticator {

    private static ConfidentialClientApplication app = null;
    private static String authority;
    private static String clientId;
    private static Set<String> scope;

    static {
        try {
            Properties props = new Properties();
            props.load(AzureADAuthenticator.class.getClassLoader().getResourceAsStream("azure-ad.properties"));

            clientId = props.getProperty("azure.clientId");
            authority = props.getProperty("azure.authority").replace("{tenant}", props.getProperty("azure.tenantId"));
            String clientSecret = props.getProperty("azure.clientSecret");
            IClientSecret secret = ClientCredentialFactory.createFromSecret(clientSecret);
            
            scope = Collections.singleton(props.getProperty("azure.scopes"));

            ExecutorService service = Executors.newFixedThreadPool(1);
            app = ConfidentialClientApplication.builder(clientId, secret)
                    .authority(authority)
                    .executorService(service)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String buildAuthorizationUrl(String redirectUri) throws MalformedURLException {
        AuthorizationRequestUrlParameters parameters = AuthorizationRequestUrlParameters
                .builder(redirectUri, scope)
                .responseMode(ResponseMode.QUERY)
                .prompt(Prompt.SELECT_ACCOUNT)
                .build();

        return app.getAuthorizationRequestUrl(parameters).toString();
    }

    public static IAuthenticationResult acquireToken(String authorizationCode, String redirectUri) throws Exception {
        AuthorizationCodeParameters parameters = AuthorizationCodeParameters.builder(
                authorizationCode,
                new URI(redirectUri))
                .scopes(scope)
                .build();

        CompletableFuture<IAuthenticationResult> future = app.acquireToken(parameters);
        return future.join(); // Esto puede bloquear. Cosiderar implementacion asincrona
    }
}

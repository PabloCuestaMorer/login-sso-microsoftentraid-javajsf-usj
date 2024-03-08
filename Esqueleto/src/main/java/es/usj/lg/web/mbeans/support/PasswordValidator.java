/*
 * PasswordValidator.java
 *
 * v1.0
 *
 * 20-feb-2018
 *
 * © Universidad San Jorge
 */
package es.usj.lg.web.mbeans.support;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validador de password
 *
 * @author Jose Luis Bailo
 */
public class PasswordValidator {

    private Pattern pattern;
    private Matcher matcher;
    private static final String PASSWORD_PATTERN
            = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[\\!\\\"\\$\\(\\)\\*\\+\\,\\-\\.\\<\\=\\>\\?\\\\\\^\\_\\`%&'/:;]).{8,13})";

    public PasswordValidator() {
        pattern = Pattern.compile(PASSWORD_PATTERN);
    }

    /**
     * Validate password with regular expression
     *
     * @param password password for validation
     * @return true valid password, false invalid password
     */
    public boolean validate(String password) {
        matcher = pattern.matcher(password);
        return matcher.matches();
    }
}

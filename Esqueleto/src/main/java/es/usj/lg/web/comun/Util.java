/*
 * Util.java
 *
 * v1.0
 *
 * 16-feb-2024
 *
 * © Universidad San Jorge
 */
package es.usj.lg.web.comun;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.application.FacesMessage.Severity;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.Flash;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Clase de funciones utiles
 *
 * @author Jose Luis Bailo
 */
public class Util {

    private static Locale locale;
    private static final Logger logger = Logger.getLogger(Util.class);
    private static final String PASSWORD_PATTERN
            = "abcdefghijklmnopqrstuvwyxzABCDEFGHIJKLMNOPQRSTUVWYXZ1234567890!\"$()*+,-.<=>?[]\\_{}|@#%&'/:;";
    private static final String PASSWORD_PATTERN_LOWERCASE
            = "abcdefghijklmnopqrstuvwyxz";
    private static final String PASSWORD_PATTERN_UPPERCASE
            = "ABCDEFGHIJKLMNOPQRSTUVWYXZ";
    private static final String PASSWORD_PATTERN_NUMBERS
            = "1234567890";
    private static final String PASSWORD_PATTERN_SYMBOLS
            = "!\"$()*+,-.<=>?[]\\_{}|@#%&'/:"; //El ; lo quitamos porque si no al enviar el correo lo sustituye por un intro.

    protected static ClassLoader getCurrentClassLoader(Object defaultObject) {

        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        if (loader == null) {
            loader = defaultObject.getClass().getClassLoader();
        }

        return loader;
    }

    /**
     * Obtiene de los recursos de mensaje de JSF (systemMessages) el valor de la
     * clave deseada
     *
     * @param bundleName Nombre de la clase usada para los mensajes de sistema
     * para la aplicación
     * @param key Clave
     * @param params Parámetros del contenido del texto
     * @param locale Idioma
     * @return Valor del texto de la clave
     */
    public static String getMessageJSFString(String bundleName,
            String key,
            Object params[],
            Locale locale) {

        String text;

        ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale, getCurrentClassLoader(params));

        try {
            text = bundle.getString(key);
        } catch (MissingResourceException e) {
            text = "?? key " + key + " no encontrada ??";
        }

        if (params != null) {
            MessageFormat mf = new MessageFormat(text, locale);
            text = mf.format(params, new StringBuffer(), null).toString();
        }

        return text;
    }

    /**
     * Obtiene de los recursos de internacionalización (messages) el valor de la
     * clave deseada
     *
     * @param bundleName Nombre del recurso definido en faces-config
     * @param key Clave
     * @param params Parámetros del contenido del texto
     * @param locale Idioma
     * @return Valor del texto de la clave
     */
    public static String getMessageResourceString(String bundleName,
            String key,
            Object params[],
            Locale locale) {

        String text = null;
        FacesContext context = FacesContext.getCurrentInstance();

        ResourceBundle bundle = context.getApplication().getResourceBundle(context, bundleName);

        try {
            text = bundle.getString(key);
        } catch (MissingResourceException e) {
            text = "?? key " + key + " no encontrada ??";
        }

        if (params != null) {
            MessageFormat mf = new MessageFormat(text, locale);
            text = mf.format(params, new StringBuffer(), null).toString();
        }

        return text;
    }

    /**
     * Obtiene de los recursos de internacionalización (messages) el valor de la
     * clave deseada
     *
     * @param bundle Nombre del recurso definido en faces-config
     * @param key Clave
     * @param params Parámetros del contenido del texto
     * @param locale Idioma
     * @return Valor del texto de la clave
     */
    public static String getMessageResourceString(ResourceBundle bundle,
            String key,
            Object params[],
            Locale locale) {

        String text;

        try {
            text = bundle.getString(key);
        } catch (MissingResourceException e) {
            text = "?? key " + key + " no encontrada ??";
        }

        if (params != null) {
            MessageFormat mf = new MessageFormat(text, locale);
            text = mf.format(params, new StringBuffer(), null).toString();
        }

        return text;
    }

    public static void showMsgGrowl(ResourceBundle bundle,
            String key,
            Object params[],
            Locale locale,
            Severity severity) {

        String text = getMessageResourceString(bundle, key, params, locale);

        String textSeverity = "";

        if (severity.equals(FacesMessage.SEVERITY_INFO)) {
            textSeverity = getMessageResourceString(bundle, "Informacion", null, locale);
        } else if (severity.equals(FacesMessage.SEVERITY_WARN)) {
            textSeverity = getMessageResourceString(bundle, "Aviso", null, locale);
        } else if (severity.equals(FacesMessage.SEVERITY_ERROR)) {
            textSeverity = getMessageResourceString(bundle, "Error", null, locale);
        }

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, textSeverity, text));
    }

    public static void showMsgGrowl(String bundleName,
            String key,
            Object params[],
            Locale locale,
            Severity severity) {

        String text = getMessageResourceString(bundleName, key, params, locale);

        String textSeverity = "";

        if (severity.equals(FacesMessage.SEVERITY_INFO)) {
            textSeverity = getMessageResourceString(bundleName, "Informacion", null, locale);
        } else if (severity.equals(FacesMessage.SEVERITY_WARN)) {
            textSeverity = getMessageResourceString(bundleName, "Aviso", null, locale);
        } else if (severity.equals(FacesMessage.SEVERITY_ERROR)) {
            textSeverity = getMessageResourceString(bundleName, "Error", null, locale);
        }

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, textSeverity, text));
    }

    public static void showMsgGrowl(String bundleName,
            String text,
            Locale locale,
            Severity severity) {

        String textSeverity = "";

        if (severity.equals(FacesMessage.SEVERITY_INFO)) {
            textSeverity = getMessageResourceString(bundleName, "Informacion", null, locale);
        } else if (severity.equals(FacesMessage.SEVERITY_WARN)) {
            textSeverity = getMessageResourceString(bundleName, "Aviso", null, locale);
        } else if (severity.equals(FacesMessage.SEVERITY_ERROR)) {
            textSeverity = getMessageResourceString(bundleName, "Error", null, locale);
        }

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, textSeverity, text));
    }

    public static Locale getLocale() {
        try {
            if (locale == null) {
                locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
            }
            return locale;
        } catch (Exception ex) {
            return new Locale("es");
        }
    }

    public static void setLocale(Locale locale) {
        Util.locale = locale;
//        FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
    }

    /**
     * Obtiene los idiomas soportados en la aplicación del faces-config
     *
     * @return Lista de idiomas soportados
     */
    public static List<String> idiomasDisponibles() {
        List<String> list = new ArrayList<>(0);
        list.add(FacesContext.getCurrentInstance().getApplication().getDefaultLocale().getLanguage());
        for (Iterator<Locale> iter = FacesContext.getCurrentInstance().getApplication().getSupportedLocales(); iter.hasNext();) {
            list.add(iter.next().getLanguage());
        }

        return list;
    }

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9)) {
                    buf.append((char) ('0' + halfbyte));
                } else {
                    buf.append((char) ('a' + (halfbyte - 10)));
                }
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String SHA1(String text)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md;
        md = MessageDigest.getInstance("SHA-1");
        byte[] sha1hash = new byte[40];
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

    /**
     * Envía un mensaje de validación a un elemento de la vista
     *
     * @param grado Grado de severidad del mensaje
     * @param rutaElm Ruta del elemento que se valida; null para generico
     * @param mensaje Mensaje a mostrar por pantalla
     */
    public static void mostrarMensaje(FacesMessage.Severity grado, String rutaElm, String mensaje) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        FacesMessage message = new FacesMessage();
        message.setSeverity(grado);
        message.setSummary(mensaje);
        message.setDetail(mensaje);
        facesContext.addMessage(rutaElm, message);
    }

    /**
     * Establece la hora en la medianoche
     *
     * @param date Fecha a modificar
     * @return Fecha con la hora establecida a medianoche
     */
    public static Date setTimeToMidnight(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * Establece la hora
     *
     * @param date Fecha a modificar
     * @param hour Hora a establecer
     * @param min Minutos a establecer
     * @param seg Segundos a establecer
     * @return Fecha con la hora establecida
     */
    public static Calendar setTime(Date date, int hour, int min, int seg) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, seg);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    /**
     * Establece la hora en la medianoche
     *
     * @param date Fecha a modificar
     * @return Fecha con la hora establecida a medianoche
     */
    public static Date setTimeToEndDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 59);
        return calendar.getTime();
    }

    /**
     * Convierte uan fecha a su representación en texto (p.e. 27 de Enero de
     * 2012)
     *
     * @param fecha Fecha a formatear
     * @return Texto de la fecha
     */
    public static String getFechaEnTexto(Date fecha) {
        Calendar calendar = Calendar.getInstance();
        String fechaTxt;
        calendar.setTime(fecha);
        fechaTxt = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + " de "
                + calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("es")) + " de " + String.valueOf(calendar.get(Calendar.YEAR));
        return fechaTxt;
    }

    /**
     * Formatea una fecha a dd/MM/yyyy
     *
     * @param fecha Fecha
     * @return Fecha formateada
     */
    public static String getFechaFormateada(Date fecha) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fechaTxt = sdf.format(fecha);
        return fechaTxt;
    }

    /**
     * Formatea una fecha a dd/MM/yyyy
     *
     * @param fecha Fecha
     * @return Fecha formateada
     */
    public static String getFechaFormateadaSql(Date fecha) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fechaTxt = sdf.format(fecha);
        return fechaTxt;
    }

    /**
     * Formatea una fecha a dd/MM/yyyy HH:mm
     *
     * @param fecha Fecha
     * @return Fecha formateada
     */
    public static String getFechaHoraFormateada(Date fecha) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String fechaTxt = sdf.format(fecha);
        return fechaTxt;
    }

    /**
     * Formatea una fecha a dd/MM/yyyy HH:mm
     *
     * @param fechaFormateada Fecha
     * @return Fecha formateada
     */
    public static Date getFechaHoraFormateada(String fechaFormateada) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date fecha = sdf.parse(fechaFormateada);
        return fecha;
    }

    /**
     * Formatea una fecha a dd/MM/yyyy HH:mm
     *
     * @param fecha Fecha
     * @return Fecha formateada
     */
    public static String getFechaFileFormateada(Date fecha) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String fechaTxt = sdf.format(fecha);
        return fechaTxt;
    }

    /**
     * Formatea una fecha a yyyy-MM-dd'T'HH:mm:ssZ
     *
     * @param fecha Fecha
     * @return Fecha formateada
     */
    public static String getFechaHoraFormateada8601(Date fecha) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String fechaTxt = sdf.format(fecha);

        return fechaTxt;
    }

    /**
     * Formatea una fecha a dd/MM/yyyy HH:mm
     *
     * @param fecha Fecha
     * @return Fecha formateada
     */
    public static String getHoraFormateada(Date fecha) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String fechaTxt = sdf.format(fecha);
        return fechaTxt;
    }

    /**
     * Comprueba si una fecha es válida
     *
     * @param fechax Fecha
     * @return Verdadero si la fecha es correcta; falso en caso contrario
     */
    public static boolean isFechaValida(String fechax, Locale locale) {
        try {
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy", locale);
            formatoFecha.setLenient(false);
            formatoFecha.parse(fechax);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    public static Date getFechaInicioAnyo(String anyo) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.YEAR, Integer.parseInt(anyo));

        return setTimeToMidnight(cal.getTime());
    }

    public static Date getFechaFinAnyo(String anyo) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 31);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.YEAR, Integer.parseInt(anyo));

        return setTimeToMidnight(cal.getTime());
    }

    /**
     * Obtiene la diferencia entre las fechas dadas según el parámetro indicado
     *
     * @param fechaNueva Fecha nueva
     * @param fechaAnterior Fecha anterior
     * @param unidad Unidad de diferencia de las constantes de Calendar
     * @return Diferencia entre fechas
     */
    public static long getDateDiff(Date fechaNueva, Date fechaAnterior, int unidad) {
        long diff = 0;
        Calendar calNueva = Calendar.getInstance();
        calNueva.setTime(fechaNueva);
        Calendar calAnterior = Calendar.getInstance();
        calAnterior.setTime(fechaAnterior);
        long milisNueva = calNueva.getTimeInMillis();
        long milisAnterior = calAnterior.getTimeInMillis();
        diff = milisNueva - milisAnterior;
        switch (unidad) {
            case Calendar.SECOND ->
                diff = diff / 1000;
            case Calendar.MINUTE ->
                diff = diff / (60 * 1000);
            case Calendar.HOUR ->
                diff = diff / (60 * 60 * 1000);
            case Calendar.DAY_OF_WEEK ->
                diff = diff / (24 * 60 * 60 * 1000);
        }
        return diff;
    }

    public static String getAnyoAnteriorUXXI(String anyoNatural) {
        String nombre = null;
        if (anyoNatural != null && !anyoNatural.isEmpty()) {
            Integer numAnyo = Integer.valueOf(anyoNatural);
            Integer num = numAnyo - 1;
            nombre = String.valueOf(num) + "-" + anyoNatural.substring(2);
        }
        return nombre;
    }

    public static String getAnyoPosteriorUXXI(String anyoNatural) {
        String nombre = null;
        if (anyoNatural != null && !anyoNatural.isEmpty()) {
            Integer numAnyo = Integer.valueOf(anyoNatural);
            Integer num = numAnyo + 1;
            nombre = anyoNatural + "-" + String.valueOf(num).substring(2);
        }
        return nombre;
    }

    public static StreamedContent setResourceFile(String rutaCompleta) {

        StreamedContent res = null;
//        final FileInputStream fis = null;
        //resource        
        try {
            File file = new File(rutaCompleta);
            final FileInputStream fis = new FileInputStream(rutaCompleta);
//            res = new ByteArrayResource(toByteArray(fis));

            res = DefaultStreamedContent.builder()
                    .name(file.getName())
                    .contentType(URLConnection.guessContentTypeFromName(file.getName()))
                    .stream(() -> fis)
                    .build();

//            if (fis != null) {
//            fis.close();
//            }
        } catch (Exception ex) {
            logger.error(ex);
        }
        return res;

    }

    public static byte[] toByteArray(InputStream input) throws IOException {

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int len;
        while ((len = input.read(buf)) > -1) {
            output.write(buf, 0, len);
        }
        return output.toByteArray();
    }

    /**
     * Devuelve el nombre de la página actual de la aplicación
     *
     * @return Nombre de la página actual
     */
    public static String paginaActual() {
        String pagina = "";
        FacesContext ctx = FacesContext.getCurrentInstance();
        HttpServletRequest servletRequest = (HttpServletRequest) ctx.getExternalContext().getRequest();
        // returns something like "/myapplication/home.faces"
        String fullURI = servletRequest.getRequestURI();
        String[] parse = fullURI.split("/");
        if (parse.length > 0) {
            pagina = parse[parse.length - 1];
        }
        return pagina;
    }

    public static void recargarPagina() {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        try {
            ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI());
        } catch (IOException ex) {
            logger.error("No se ha podido recargar la página: " + ex);
        }
    }

    public static String desconexion() {
        HttpSession sesion = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        if (sesion != null) {
            sesion.invalidate();
        }
        return "Login";
    }

    public static Flash flashScope() {
        return (FacesContext.getCurrentInstance().getExternalContext().getFlash());
    }

    public static void registraUsuarioEnLog(String usuario, String texto) {
        String nombre = " - username " + usuario + " -";
        logger.info(texto + ": " + nombre);
    }

    public static String getVersion() {
        String version = "";
        try {
            InputStream is = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/META-INF/MANIFEST.MF");
            if (is != null) {
                Manifest manifest = new Manifest(is);
                Attributes attr = manifest.getMainAttributes();
                version = attr.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
                is.close();
            }
        } catch (IOException e) {
            logger.error("No se ha podido obtener el nº de version: ", e);
        }
        return version;
    }

    public static String quitarAcentos(String strToStrip) {
        String strStripped;
        //Normalizamos en la forma NFD (Canonical decomposition)
        strToStrip = Normalizer.normalize(strToStrip, Normalizer.Form.NFD);
        //Reemplazamos los acentos con una una expresión regular de Bloque Unicode
        strStripped = strToStrip.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        return strStripped;
    }

    public static String getNombreFichero(String ruta) {
        String nombre = "";
        if (ruta != null) {
            String aux = ruta.replace("\\", "#");
            String path[] = aux.split("#");
            if (path.length > 1) {
                nombre = path[path.length - 1];
            } else {
                path = ruta.split("/");
                nombre = path[path.length - 1];
            }
        }
        return nombre;
    }

    /**
     * Comprueba si existe un directorio
     *
     * @param ruta Ruta del directorio a comprobar
     * @param crea Verdadero si se crea el directorio en caso de no existir;
     * false en caso contrario
     * @return Verdadero si el directorio existe; falso en caso contrario
     * @throws java.io.IOException
     */
    public static boolean compruebaDirectorio(String ruta, boolean crea) throws IOException {
        boolean existe = true;
        File dir = new File(ruta);
        if (!dir.exists()) {
            existe = false;
            if (crea) {
                dir.mkdirs();
                if (dir.isDirectory()) {
                    existe = true;
                } else {
                    throw new IOException("Error al acceder a JOBDATA");
                }
            } else {
                throw new IOException("Error al acceder a JOBDATA");
            }
        }

        return existe;
    }

    /**
     * Función para eliminar un directorio con todo su contenido
     *
     * @param directorio
     */
    public static void eliminarDirectorio(File directorio) {
        try {
            File[] ficheros = directorio.listFiles();
            if (ficheros != null) {
                for (int x = 0; x < ficheros.length; x++) {
                    if (ficheros[x].isDirectory()) {
                        eliminarDirectorio(ficheros[x]);
                    }
                    ficheros[x].delete();
                }
            }
            if (directorio.exists()) {
                directorio.delete();
            }
        } catch (Exception e) {
            logger.error("ERROR: Hubo un error al eliminar el directorio: ", e);
        }
    }

    /**
     * Función para vaciar un directorio de todo su contenido
     *
     * @param directorio
     */
    public static void vaciarDirectorio(File directorio) {
        try {
            File[] ficheros = directorio.listFiles();
            if (ficheros != null) {
                for (int x = 0; x < ficheros.length; x++) {
                    if (ficheros[x].isDirectory()) {
                        eliminarDirectorio(ficheros[x]);
                    }
                    ficheros[x].delete();
                }
            }
        } catch (Exception e) {
            logger.error("ERROR: Hubo un error al vaciar el directorio: ", e);
        }
    }

    public static Boolean moverArchivo(String sourceFile, String destinationFile, String srcDir) {
        try {
            File inFile = new File(sourceFile);
            File outFile = new File(destinationFile);
            File srcDirFile = new File(srcDir);
            if (!srcDirFile.exists()) {
                srcDirFile.mkdirs();
            }
            if (!outFile.exists()) {
                outFile.createNewFile();
            }

            FileInputStream in = new FileInputStream(inFile);
            FileOutputStream out = new FileOutputStream(outFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();

            File file = new File(sourceFile);
            if (file.exists()) {
                file.delete();
            }
            return true;
        } catch (IOException e) {
            logger.error("ERROR: Hubo un error al mover el archivo: ", e);
            return false;
        }
    }

    public static Boolean copiarArchivo(String sourceFile, String destinationFile, String dstDir) {
        try {
            File inFile = new File(sourceFile);
            File outFile = new File(destinationFile);
            File dstDirFile = new File(dstDir);
            if (!dstDirFile.exists()) {
                dstDirFile.mkdirs();
            }
            if (!outFile.exists()) {
                outFile.createNewFile();
            }

            FileInputStream in = new FileInputStream(inFile);
            FileOutputStream out = new FileOutputStream(outFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();

            return true;
        } catch (IOException e) {
            logger.error("ERROR: Hubo un error al mover el archivo " + sourceFile + " : ", e);
            return false;
        }
    }

    /**
     * Función para eliminar un directorio con todo su contenido
     *
     * @param ruta
     */
    public static void eliminarFichero(String ruta) {
        try {
            File file = new File(ruta);
            file.delete();
        } catch (Exception e) {
            logger.error("ERROR: Hubo un error al eliminar el fichero " + ruta + ": ", e);
        }
    }

    /**
     * Función que procesa la cabecera de un fichero de intercambio
     *
     * @param cabecera
     */
    public static String procesaCabecera(String cabecera) {
        return String.format("%1$-354s", cabecera);
    }

    /**
     * Función que procesa el detalle de un fichero de intercambio
     *
     * @param detalle
     * @return
     */
    public static String procesaDetalle(String detalle) {
        return detalle;
    }

    public static void writeTextFile(String aFileName, String line) throws IOException {
        final Charset ENCODING = StandardCharsets.UTF_8;
        Path path = Paths.get(aFileName);
        try ( BufferedWriter writer = Files.newBufferedWriter(path, ENCODING)) {
            writer.write(line);
        }
    }

    public static void resize(File file, String outRuta) throws Exception {
        try {
            File infile = file;
            File outfile = new File(outRuta);
            int w = 149, h = 189; //pixeles
            ImageReader reader = ImageIO.getImageReadersByFormatName("jpeg").next();
            reader.setInput(new FileImageInputStream(infile), true, false);

            // Image writer
            ImageWriter imageWriter = ImageIO.getImageWritersBySuffix("jpeg").next();
            try ( ImageOutputStream ios = ImageIO.createImageOutputStream(outfile)) {
                imageWriter.setOutput(ios);
                BufferedImage image = reader.read(0);
                IIOMetadata data = reader.getImageMetadata(0);
                Image rescaled = image.getScaledInstance(w, h, Image.SCALE_AREA_AVERAGING);
                BufferedImage output = toBufferedImage(rescaled, BufferedImage.TYPE_INT_RGB);
                IIOImage iioImage = new IIOImage(output, null, null);
                iioImage.setMetadata(data);
                imageWriter.write(data, iioImage, null);
            }
            imageWriter.dispose();
        } catch (Exception ex) {
            logger.error("No se ha podido cambiar el tamaño de la imagen");
            throw ex;
        }

    }

    public static BufferedImage toBufferedImage(Image image, int type) {
        int w = image.getWidth(null);
        int h = image.getHeight(null);
        BufferedImage result = new BufferedImage(w, h, type);
        Graphics2D g = result.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return result;
    }

    public static File changeDPI(File file, String outRuta) throws IOException {
        try {

            File outFile = new File(outRuta);

            ImageWriter imageWriter;
            try ( ImageOutputStream ios = ImageIO.createImageOutputStream(outFile)) {
                imageWriter = ImageIO.getImageWritersBySuffix("jpeg").next();
                imageWriter.setOutput(ios);
                BufferedImage image = ImageIO.read(file);
                int dpi = 240;
                float quality = 0.8f;
                // some code loading the image
                IIOMetadata metadata = imageWriter.getDefaultImageMetadata(new ImageTypeSpecifier(image), null);
                ImageWriteParam writeParam = imageWriter.getDefaultWriteParam();
                // set the DPI in the image metadata
                Element tree = (Element) metadata.getAsTree("javax_imageio_jpeg_image_1.0");
                NodeList nodeList = tree.getElementsByTagName("app0JFIF");
                Element jfif = (Element) nodeList.item(0);
                jfif.setAttribute("Xdensity", Integer.toString(dpi));
                jfif.setAttribute("Ydensity", Integer.toString(dpi));
                jfif.setAttribute("resUnits", "1");
                metadata.setFromTree("javax_imageio_jpeg_image_1.0", tree);
                // set the compression to use
                writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                writeParam.setCompressionQuality(quality);
                IIOImage imageWithMetadata = new IIOImage(image, null, metadata);
                imageWriter.write(metadata, imageWithMetadata, null);
            }
            imageWriter.dispose();
            return outFile;

        } catch (IOException ex) {
            logger.error("No se ha podido cambiar la resolución de la imagen");
            return null;
        }
    }

    public static File changeDPI(InputStream file, String outRuta) throws IOException {
        try {

            File outFile = new File(outRuta);

            ImageWriter imageWriter;
            try ( ImageOutputStream ios = ImageIO.createImageOutputStream(outFile)) {
                imageWriter = ImageIO.getImageWritersBySuffix("jpeg").next();
                imageWriter.setOutput(ios);
                BufferedImage image = ImageIO.read(file);
                int dpi = 240;
                float quality = 0.8f;
                // some code loading the image
                IIOMetadata metadata = imageWriter.getDefaultImageMetadata(new ImageTypeSpecifier(image), null);
                ImageWriteParam writeParam = imageWriter.getDefaultWriteParam();
                // set the DPI in the image metadata
                Element tree = (Element) metadata.getAsTree("javax_imageio_jpeg_image_1.0");
                NodeList nodeList = tree.getElementsByTagName("app0JFIF");
                Element jfif = (Element) nodeList.item(0);
                jfif.setAttribute("Xdensity", Integer.toString(dpi));
                jfif.setAttribute("Ydensity", Integer.toString(dpi));
                jfif.setAttribute("resUnits", "1");
                metadata.setFromTree("javax_imageio_jpeg_image_1.0", tree);
                // set the compression to use
                writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                writeParam.setCompressionQuality(quality);
                IIOImage imageWithMetadata = new IIOImage(image, null, metadata);
                imageWriter.write(metadata, imageWithMetadata, null);
            }
            imageWriter.dispose();
            return outFile;

        } catch (IOException ex) {
            logger.error("No se ha podido cambiar la resolución de la imagen");
            return null;
        }
    }

    private static TrustManager[] getTrustManager() {
        return new TrustManager[]{
            new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                }

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                }
            }
        };
    }

    public static void comprimir(String inFile, String inputzip, String nb) throws Exception {
        final int BUFFER_SIZE = 1024;
        // objetos en memoria
        FileOutputStream fos;
        try {
            byte[] buffer = new byte[BUFFER_SIZE];

            fos = new FileOutputStream(inputzip);

            ZipOutputStream zout = new ZipOutputStream(fos);
            FileInputStream fin = new FileInputStream(inFile);
            zout.putNextEntry(new ZipEntry(nb));
            int length;
            while ((length = fin.read(buffer)) > 0) {
                zout.write(buffer, 0, length);
            }
            zout.closeEntry();
            fin.close();
            zout.close();

        } catch (Exception e) {
            logger.error("No se ha podido comprimir el fichero");
            throw e;
        }
    }

    public static boolean copyFile(String origen, String destino) {
        boolean ok = true;
        Path FROM = Paths.get(origen);
        Path TO = Paths.get(destino);
        //sobreescribir el fichero de destino, si existe, y copiar
        // los atributos, incluyendo los permisos rwx
        CopyOption[] options = new CopyOption[]{
            StandardCopyOption.REPLACE_EXISTING,
            StandardCopyOption.COPY_ATTRIBUTES
        };
        try {
            Files.copy(FROM, TO, options);
            return ok;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Creates a Zip archive.If the name of the file passed in is a directory,
     * the directory's contents will be made into a Zip file.
     *
     * @param file
     * @param nbZip
     * @return
     */
    public static boolean comprimirDirectorio(File file, String nbZip) {
        ZipOutputStream zos;
        /// PREPARACIÓN DEL FICHERO ZIP.
        ZipEntry ze;
        String[] ficheros = file.list();
        try {
            zos = new ZipOutputStream(new FileOutputStream(new File(nbZip)));

            // INSERCIÓN DE LOS FICHEROS AL FICHERO ZIP.
            if (ficheros != null) {
                for (String fichero : ficheros) {
                    // INTRODUCIMOS EL FICHERO
                    ze = new ZipEntry(fichero);
                    zos.putNextEntry(ze);
                    // INTRODUCIMOS LOS DATOS DEL FICHERO
                    File file2 = new File(file.getPath() + "/" + fichero);
                    byte[] readAllBytesOfFile = Files.readAllBytes(file2.toPath());
                    zos.write(readAllBytesOfFile, 0, readAllBytesOfFile.length);
                }
            }
            // CERRAMOS LOS FLUJOS DE DATOS.
            zos.closeEntry();
            zos.close();

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void deleteFolder(String srcfile) {
        File f = new File(srcfile);

        String[] entries = f.list();
        for (String s : entries) {
            File currentFile = new File(f.getPath(), s);
            currentFile.delete();
        }
        f.delete();
    }

    public static String obtenerAnyoAnterior(String anyo) {
        String[] parseAnyo = anyo.split("-");
        Integer a1 = Integer.parseInt(parseAnyo[0]) - 1;
        Integer a2 = Integer.parseInt(parseAnyo[1]) - 1;
        String anterior = a1.toString() + "-" + a2.toString();

        return anterior;
    }

    public static String fileToString(File fichero) {
        String result = "";
        try {
            FileInputStream file = new FileInputStream(fichero);
            byte[] b = new byte[file.available()];
            file.read(b);
            file.close();
            result = new String(b, "UTF-8");
        } catch (IOException e) {
            logger.error(e);
        } catch (Throwable e) {
            logger.error(e);
        }
        return result;
    }

    public static String getAnyoUXXI(String anyoNatural, int distancia) {
        String nombre = null;
        if (anyoNatural != null && !anyoNatural.isEmpty()) {
            Integer numAnyo = Integer.valueOf(anyoNatural);
            Integer num = numAnyo + distancia;
            nombre = String.valueOf(num) + "-" + String.valueOf(num + 1).substring(2);
        }
        return nombre;
    }

    public static String getAnyoActual() {
        Calendar cal = Calendar.getInstance();
        return String.valueOf(cal.get(Calendar.YEAR));
    }

    public static String limpiarTagsWord(String texto) {
        if (texto != null) {
            texto = texto.replace("<o:p>", "");
            texto = texto.replace("</o:p>", "");
            texto = texto.replace("<kbd>", "");
            texto = texto.replace("</kbd>", "");
            texto = texto.replace("<tt>", "");
            texto = texto.replace("</tt>", "");
            texto = texto.replace("</br>", "<br/>");
            texto = texto.replaceAll("(/)(?![^<]*>)", "/ ");
            //si encuentra un & tal cual y no pertenece a un nombre html lo sustituye por &amp;
            String[] parse = texto.split("&");
            for (int i = 0; i < parse.length; i++) {
                if (i == 0) {
                    texto = parse[i];
                } else {
                    texto += parse[i];
                }
                if (i < parse.length - 1) {
                    String[] parse2 = parse[i + 1].split(";");
                    if (parse2.length != 0) {
                        if (!parse2[0].equals("quot") && !parse2[0].equals("amp") && !parse2[0].equals("lt") && !parse2[0].equals("gt")
                                && !parse2[0].equals("nbsp") && !parse2[0].equals("iexcl") && !parse2[0].equals("cent") && !parse2[0].equals("pound")
                                && !parse2[0].equals("curren") && !parse2[0].equals("yen") && !parse2[0].equals("brvbar") && !parse2[0].equals("sect")
                                && !parse2[0].equals("uml") && !parse2[0].equals("copy") && !parse2[0].equals("ordf") && !parse2[0].equals("laquo")
                                && !parse2[0].equals("not") && !parse2[0].equals("shy") && !parse2[0].equals("reg") && !parse2[0].equals("macr")
                                && !parse2[0].equals("deg") && !parse2[0].equals("plusmn") && !parse2[0].equals("sup2") && !parse2[0].equals("sup3")
                                && !parse2[0].equals("acute") && !parse2[0].equals("micro") && !parse2[0].equals("para") && !parse2[0].equals("middot")
                                && !parse2[0].equals("cedil") && !parse2[0].equals("sup1") && !parse2[0].equals("ordm") && !parse2[0].equals("raquo")
                                && !parse2[0].equals("frac14") && !parse2[0].equals("frac12") && !parse2[0].equals("frac34") && !parse2[0].equals("iquest")
                                && !parse2[0].equals("Agrave") && !parse2[0].equals("Aacute") && !parse2[0].equals("Acirc") && !parse2[0].equals("Atilde")
                                && !parse2[0].equals("Auml") && !parse2[0].equals("Aring") && !parse2[0].equals("AElig") && !parse2[0].equals("Ccedil")
                                && !parse2[0].equals("Egrave") && !parse2[0].equals("Eacute") && !parse2[0].equals("Ecirc") && !parse2[0].equals("Euml")
                                && !parse2[0].equals("Igrave") && !parse2[0].equals("Iacute") && !parse2[0].equals("Icirc") && !parse2[0].equals("Iuml")
                                && !parse2[0].equals("ETH") && !parse2[0].equals("Ntilde") && !parse2[0].equals("Ograve") && !parse2[0].equals("Oacute")
                                && !parse2[0].equals("Ocirc") && !parse2[0].equals("Otilde") && !parse2[0].equals("Ouml") && !parse2[0].equals("times")
                                && !parse2[0].equals("Oslash") && !parse2[0].equals("Ugrave") && !parse2[0].equals("Uacute") && !parse2[0].equals("Ucirc")
                                && !parse2[0].equals("Uuml") && !parse2[0].equals("Yacute") && !parse2[0].equals("THORN") && !parse2[0].equals("szlig")
                                && !parse2[0].equals("agrave") && !parse2[0].equals("aacute") && !parse2[0].equals("acirc") && !parse2[0].equals("atilde")
                                && !parse2[0].equals("auml") && !parse2[0].equals("aring") && !parse2[0].equals("aelig") && !parse2[0].equals("ccedil")
                                && !parse2[0].equals("egrave") && !parse2[0].equals("eacute") && !parse2[0].equals("ecirc") && !parse2[0].equals("euml")
                                && !parse2[0].equals("igrave") && !parse2[0].equals("iacute") && !parse2[0].equals("icirc") && !parse2[0].equals("iuml")
                                && !parse2[0].equals("eth") && !parse2[0].equals("ntilde") && !parse2[0].equals("ograve") && !parse2[0].equals("oacute")
                                && !parse2[0].equals("ocirc") && !parse2[0].equals("otilde") && !parse2[0].equals("ouml") && !parse2[0].equals("divide")
                                && !parse2[0].equals("oslash") && !parse2[0].equals("ugrave") && !parse2[0].equals("uacute") && !parse2[0].equals("ucirc")
                                && !parse2[0].equals("uuml") && !parse2[0].equals("yacute") && !parse2[0].equals("thorn") && !parse2[0].equals("yuml")
                                && !parse2[0].equals("euro") && !parse2[0].equals("rsquo") && !parse2[0].equals("rsaquo") && !parse2[0].equals("lsaquo")
                                && !parse2[0].equals("rdquo") && !parse2[0].equals("ldquo") && !parse2[0].equals("lsquo") && !parse2[0].equals("apos")
                                && !parse2[0].equals("bull") && !parse2[0].equals("infin") && !parse2[0].equals("permil") && !parse2[0].equals("sdot")
                                && !parse2[0].equals("dagger") && !parse2[0].equals("mdash") && !parse2[0].equals("trade") && !parse2[0].equals("alpha")
                                && !parse2[0].equals("beta") && !parse2[0].equals("gamma") && !parse2[0].equals("delta") && !parse2[0].equals("epsilon")
                                && !parse2[0].equals("zeta") && !parse2[0].equals("eta") && !parse2[0].equals("theta") && !parse2[0].equals("iota")
                                && !parse2[0].equals("kappa") && !parse2[0].equals("lambda") && !parse2[0].equals("mu") && !parse2[0].equals("nu")
                                && !parse2[0].equals("xi") && !parse2[0].equals("omicron") && !parse2[0].equals("pi") && !parse2[0].equals("rho")
                                && !parse2[0].equals("sigma") && !parse2[0].equals("tau") && !parse2[0].equals("upsilon") && !parse2[0].equals("phi")
                                && !parse2[0].equals("chi") && !parse2[0].equals("psi") && !parse2[0].equals("omega") && !parse2[0].equals("Alpha")
                                && !parse2[0].equals("Beta") && !parse2[0].equals("Gamma") && !parse2[0].equals("Delta") && !parse2[0].equals("Epsilon")
                                && !parse2[0].equals("Zeta") && !parse2[0].equals("Eta") && !parse2[0].equals("Theta") && !parse2[0].equals("Iota")
                                && !parse2[0].equals("Kappa") && !parse2[0].equals("Lambda") && !parse2[0].equals("Mu") && !parse2[0].equals("Nu")
                                && !parse2[0].equals("Xi") && !parse2[0].equals("Omicron") && !parse2[0].equals("Pi") && !parse2[0].equals("Rho")
                                && !parse2[0].equals("Sigma") && !parse2[0].equals("Tau") && !parse2[0].equals("Upsilon") && !parse2[0].equals("Phi")
                                && !parse2[0].equals("Chi") && !parse2[0].equals("Psi") && !parse2[0].equals("Omega") && !parse2[0].equals("sbquo")
                                && !parse2[0].equals("fnof") && !parse2[0].equals("bdquo") && !parse2[0].equals("hellip") && !parse2[0].equals("Dagger")
                                && !parse2[0].equals("Scaron") && !parse2[0].equals("scaron") && !parse2[0].equals("OElig") && !parse2[0].equals("ndash")
                                && !parse2[0].equals("Omega") && !parse2[0].equals("oelig")
                                && !parse2[0].startsWith("#")) {
                            texto += "&amp;";
                        } else {
                            texto += "&";
                        }
                    }
                }
            }
        }
        return texto;
    }

    public static boolean caracteresProhibidosPDF(String texto) {
        if (texto != null) {
            if (texto.contains("!--{") || texto.contains("!--[")
                    || texto.contains("OfficeDocumentSettings")) {
                return true;
            }
        }
        return false;
    }

    public static String caracteresAmpPDF(String texto) {
        String[] parse = texto.split("&");
        for (int i = 0; i < parse.length; i++) {
            if (i == 0) {
                texto = parse[i];
            } else {
                texto += parse[i];
            }
            if (i < parse.length - 1) {
                String[] parse2 = parse[i + 1].split(";");
                if (parse2.length != 0) {
                    if (!parse2[0].equals("amp")) {
                        texto += "&amp;";
                    } else {
                        texto += "&";
                    }

                }
            }
        }
        return texto;
    }

    public static boolean validarISSN(String issn) {
        if (issn == null || issn.length() != 9) {
            return false;
        }
        if (!issn.contains("-")) {
            return false;
        }
        char[] arrayChar = issn.toCharArray();
        int cont = 0;
        for (int i = 0; i < arrayChar.length; i++) {
            if (arrayChar[i] == '-') {
                cont++;
            }
        }
        if (cont > 1) {
            return false;
        }
        // String newIssn = issn.trim().replace(" ", "");//elimina espacios en blanco
        String newIssn = issn.replace("-", ""); //elimina los guiones    
        Pattern pat = Pattern.compile("[A-Za-z0-9]*");
        Matcher mat = pat.matcher(newIssn);
        if (!mat.matches()) {
            return false;
        }
        char checksumChar;
        int checksum = getCheckSumISSN(newIssn);
        if (checksum == 10) {
            checksumChar = 'X';
        } else {
            checksumChar = Character.forDigit(checksum, 10);
        }
        char fileDigit = newIssn.charAt(7);
        return checksumChar == fileDigit;
    }

    private static int getCheckSumISSN(String issn) {
        int sum = 0;
        int weight = 0;

        for (int i = 0; i < 7; i++) {
            switch (i) {
                case 0:
                    weight = 8;
                    break;
                case 1:
                    weight = 7;
                    break;
                case 2:
                    weight = 6;
                    break;
                case 3:
                    weight = 5;
                    break;
                case 4:
                    weight = 4;
                    break;
                case 5:
                    weight = 3;
                    break;
                case 6:
                    weight = 2;
                    break;
            }

            char c = issn.charAt(i);
            int n = Character.digit(c, 10); //convierte el caracter a digito
            sum += (n * weight);
        }

        int cSum = sum % 11;

        return cSum == 0 ? cSum : 11 - cSum;
    }

    public static float round(float value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (float) Math.round(value * scale) / scale;
    }

    public static BigDecimal round(BigDecimal numBig, int precision) {
        return numBig.setScale(precision, RoundingMode.HALF_UP);
    }

    public static Date getFechaInicioCurso(String anyo) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MONTH, Calendar.SEPTEMBER);
        String[] anyos = anyo.split("-");
        cal.set(Calendar.YEAR, Integer.parseInt(anyos[0]));

        return setTimeToMidnight(cal.getTime());
    }

    public static Date getFechaFinCurso(String anyo) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 31);
        cal.set(Calendar.MONTH, Calendar.AUGUST);
        String[] anyos = anyo.split("-");
        cal.set(Calendar.YEAR, Integer.parseInt(anyos[0]) + 1);

        return setTime(cal.getTime(), 23, 59, 59).getTime();
    }

    public static String quitarCaracteresEspeciales(String strToStrip) {
        String strStripped = null;
        //Normalizamos en la forma NFD (Canonical decomposition)
        strToStrip = Normalizer.normalize(strToStrip, Normalizer.Form.NFD);
        //Reemplazamos los acentos con una expresión regular de Bloque Unicode
        strStripped = strToStrip.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        return strStripped;
    }

    public static boolean compruebaNombreUsuario(String username) {

        Pattern pattern;
        Matcher matcher;
        String UsernamePattern = "([a-zA-Z0-9\\@\\-\\.\\_]+)";

        pattern = Pattern.compile(UsernamePattern);

        matcher = pattern.matcher(username);
        return matcher.matches();

    }

    public static String ocultarMail(String mail) {
        StringBuilder mailOculto = new StringBuilder(mail);

        String[] partes = mail.split("@");
        int longitud = partes[0].length();
        if (longitud == 2) {
            mailOculto.setCharAt(1, '*');
        } else {
            for (int i = 1; i < longitud - 2; i++) {
                mailOculto.setCharAt(i, '*');
            }

        }

        return mailOculto.toString();
    }

    public static String obtenerPassword() {
        char[] lowercase = PASSWORD_PATTERN_LOWERCASE.toCharArray();
        char[] uppercase = PASSWORD_PATTERN_UPPERCASE.toCharArray();
        char[] numbers = PASSWORD_PATTERN_NUMBERS.toCharArray();
        char[] symbols = PASSWORD_PATTERN_SYMBOLS.toCharArray();
        char[] all = PASSWORD_PATTERN.toCharArray();

        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        char c = lowercase[random.nextInt(lowercase.length)];
        sb.append(c);

        c = numbers[random.nextInt(numbers.length)];
        sb.append(c);

        c = uppercase[random.nextInt(uppercase.length)];
        sb.append(c);

        c = symbols[random.nextInt(symbols.length)];
        sb.append(c);

        for (int i = 0; i < 4; i++) {
            c = all[random.nextInt(all.length)];
            sb.append(c);
        }

        return sb.toString();
    }
}

package com.sysman.jsfutil;

import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.logica.DatosSesion;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.AccionesImp;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.builder.column.TextColumnBuilder;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * @version 1.0, 01/01/2015
 * @author cmanrique
 * 
 * 
 * @version 2.0, 01/08/2017
 * @author cmanrique. Se cambia el metodo
 * {@link #tratarMensaje(String)}, debido a que se tubo que cortar la
 * cadena del mensaje a la cadena log mas cercana a la
 * etiqueta @#INI#@
 * 
 * 
 * @author ybecerra
 * @version 3, 05/07/2017, Se crea metodo armarExcel
 * 
 * @author jgomez
 * @version 4, 10/08/2018, Se crea metodo para generar el informe con
 * excel plano desde una consulta, generada en base a la consulta
 * principal del reporte
 */
public class JsfUtil {

    private static String tituloMensajes;
    private static String tituloPaginaEmpresaParametrizada;
    private static final Log LOGGER = LogFactory.getLog(JsfUtil.class);
    

    public static final ResourceBundle RECURSO = ResourceBundle
                    .getBundle("jsfutiltext");

    static {
        Context ctx;
        try {
            ctx = new InitialContext();
            Context myenv = (javax.naming.Context) ctx.lookup("java:comp/env");
            tituloMensajes = (java.lang.String) myenv.lookup("tituloMensajes");            
            tituloPaginaEmpresaParametrizada = JsfUtil.obtenerParametroMarcaBlanca("TITULOPAG");
            tituloMensajes  = tituloMensajes.replace("Sysman", JsfUtil.obtenerParametroMarcaBlanca("TITULOMSJ"));
        }
        catch (NamingException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

    }

    private JsfUtil() {
    }

    private static void agregarMensaje(String mensaje, Severity serv,
        boolean ventana) {
        if (mensaje != null && !mensaje.isEmpty()) {
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext ec = context.getExternalContext();
            ec.getFlash().setKeepMessages(true);

            mensaje = mensaje.replace(String.valueOf((char) 10), "</br>");
            mensaje = tratarMensaje(mensaje);
            mensaje = mensaje.replace("\\n", "</br>");
            tituloMensajes  = JsfUtil.obtenerParametroMarcaBlanca("TITULOMSJ")+" Software";
            String titulo = ventana ? null : tituloMensajes;
            context.addMessage(null, new FacesMessage(serv, titulo, mensaje));
        }

    }
    
    public static void agregarMensajeGlobal(String mensaje, Severity serv) {
            if (mensaje != null && !mensaje.isEmpty()) {
                FacesContext context = FacesContext.getCurrentInstance();
                ExternalContext ec = context.getExternalContext();
                ec.getFlash().setKeepMessages(true);

                mensaje = mensaje.replace(String.valueOf((char) 10), "</br>");
                mensaje = tratarMensaje(mensaje);
                mensaje = mensaje.replace("\\n", "</br>");
                String titulo =  JsfUtil.obtenerParametroMarcaBlanca("TITULOMSJ")+" Software";
                context.addMessage(null, new FacesMessage(serv, titulo, mensaje));
            }

        }

    private static String tratarMensaje(String mensaje) {
        // Reemplazos necesarios para generar adecuadamente los
        // errores
        // capturados de ORACLE
        String rta = mensaje;
        if (rta.contains("@#INI#@")) {
            rta = rta.substring(mensaje.indexOf("@#INI#@"));
            rta = rta.substring(rta.indexOf("Log:"), rta.indexOf("@#FIN#@"));
        }

        return rta;
    }

    private static void agregarMensajeDialogo(String mensaje, Severity serv) {
        if (mensaje != null && !mensaje.isEmpty()) {
            mensaje = mensaje.replace(String.valueOf((char) 10), "</br>");
            mensaje = tratarMensaje(mensaje);
            tituloMensajes  = JsfUtil.obtenerParametroMarcaBlanca("TITULOMSJ")+" Software";
            FacesMessage message = new FacesMessage(serv, tituloMensajes,
                            mensaje);
            RequestContext.getCurrentInstance().showMessageInDialog(message);
        }

    }

    public static void agregarMensajeInformativo(String mensaje) {
        agregarMensaje(mensaje, FacesMessage.SEVERITY_INFO, false);

    }

    public static void agregarMensajeAlerta(String mensaje) {
        agregarMensaje(mensaje, FacesMessage.SEVERITY_WARN, false);
    }

    public static void agregarMensajeError(String mensaje) {
        agregarMensaje(mensaje, FacesMessage.SEVERITY_ERROR, false);
    }

    public static void agregarMensajeFatal(String mensaje) {
        agregarMensaje(mensaje, FacesMessage.SEVERITY_FATAL, false);
    }

    public static void agregarMensajeInformativoVentana(String mensaje) {
        agregarMensaje(mensaje, FacesMessage.SEVERITY_INFO, true);
    }

    public static void agregarMensajeAlertaVentana(String mensaje) {
        agregarMensaje(mensaje, FacesMessage.SEVERITY_WARN, true);
    }

    public static void agregarMensajeErrorVentana(String mensaje) {
        agregarMensaje(mensaje, FacesMessage.SEVERITY_ERROR, true);
    }

    public static void agregarMensajeFatalVentana(String mensaje) {
        agregarMensaje(mensaje, FacesMessage.SEVERITY_FATAL, true);
    }

    public static void agregarMensajeInformativoDialogo(String mensaje) {
        agregarMensajeDialogo(mensaje, FacesMessage.SEVERITY_INFO);
    }

    public static void agregarMensajeAlertaDialogo(String mensaje) {
        agregarMensajeDialogo(mensaje, FacesMessage.SEVERITY_WARN);
    }

    public static void agregarMensajeErrorDialogo(String mensaje) {
        agregarMensajeDialogo(mensaje, FacesMessage.SEVERITY_ERROR);
    }

    public static void agregarMensajeFatalDialogo(String mensaje) {
        agregarMensajeDialogo(mensaje, FacesMessage.SEVERITY_FATAL);
    }

    /**
     *
     * @param nombreReporte
     * nombre del reporte que se desea descargar, debe estar
     * alamacenado en la ruta de archivos de la entidad y tener la
     * extension .jasper
     * @param parametros
     * parametros del reporte que se va a generar maneja la estructura
     * llave valor
     * @param conection
     * conexi�n con la base de datos existente
     * @param formato
     * fromato en el que se debe gnerar el reporte segun enumerado
     * FORMATOS de la clase ReportesBean
     * @throws JRException
     * @throws IOException
     * @throws SysmanException
     * @deprecated usar metodo exportarStreamed y retornar en la
     * variable de tipo StreamedContent .
     */
    @Deprecated
    public static void exportar(String nombreReporte,
        Map<String, Object> parametros, String nombreConexion,
        ReportesBean.FORMATOS formato)
                    throws JRException, IOException, SysmanException {

        ReportesBean bean = ReportesBean.getInstance();
        ConectorPool con = new ConectorPool();
        try {
            con.conectar(nombreConexion);
            bean.exportar(nombreReporte, parametros, con.getConection(),
                            formato);
        }
        catch (NamingException | SQLException e) {
            throw new SysmanException(e, e.getMessage());
        }
        finally {
            try {
                con.getConection().close();
            }
            catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     *
     * @param nombreReporte
     * nombre del reporte que se desea descargar, debe estar
     * alamacenado en la ruta de archivos de la entidad y tener la
     * extension .jasper
     * @param parametros
     * parametros del reporte que se va a generar maneja la estructura
     * llave valor
     * @param conection
     * conexi�n con la base de datos existente
     * @param formato
     * formato en el que se debe generar el reporte segun enumerado
     * FORMATOS de la clase ReportesBean
     * @return Objeto de tipo StreamedContent para devolver a la forma
     * @throws JRException
     * @throws IOException
     * @throws SysmanException
     * @throws DRException
     */
    public static StreamedContent exportarStreamed(String nombreReporte,
        Map<String, Object> parametros,
        String nombreConexion, ReportesBean.FORMATOS formato)
                    throws JRException, IOException, SysmanException {
        StreamedContent archivoDescarga = null;
        
        //INI JM 01-08-2025 CC2199
        int valido = 0;
        ReportesBean.FORMATOS miFormato = formato;
        try {
        	 String consulta =  parametros.get("PR_STRSQL").toString().replace("'", "''");
			 valido = (int) AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
			        "PCK_SYSMAN_UTL.FC_CONSULTARCONSULTA", 
			        "  UN_NOMBRE          => '" + nombreReporte + "'"
			      + ", UN_CONSULTA        =>TO_CLOB('" + consulta + "')", Types.INTEGER);
			 
			 if(valido == 1) {
				 miFormato = ReportesBean.FORMATOS.CSV;
				 agregarMensajeGlobal("Se genera en fortmato CSV, Demasiados registros para mostrar en PDF verifique los filtros para la consulta: "+nombreReporte, FacesMessage.SEVERITY_ERROR);
			 }
			 if(valido == 2) {
				 agregarMensajeGlobal("Número de registros a generar excede de 100.000, verifique los filtros para la consulta: "+nombreReporte, FacesMessage.SEVERITY_ERROR);
				 return archivoDescarga;
			 }
		} catch (SystemException e1) {
			e1.printStackTrace();
		}
        //FIN JM 01-08-2025 CC2199

        ReportesBean bean = ReportesBean.getInstance();
        ConectorPool con = new ConectorPool();
        try {
            con.conectar(nombreConexion);
            archivoDescarga = bean.exportarStreamed(nombreReporte,
                            parametros, con.getConection(), miFormato);
        }
        catch (NamingException | SQLException e) {
            throw new SysmanException(e, e.getMessage());
        }
        finally {
            try {
                con.getConection().close();
            }
            catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        return archivoDescarga;
    }

    public static StreamedContent exportarXmlStreamed(String nombreArchivo, String xmlGenerado)
            throws IOException {

        ByteArrayInputStream salida = new ByteArrayInputStream(
                xmlGenerado.getBytes("UTF-8")
        );

        return new DefaultStreamedContent(
                salida,
                "application/xml",
                nombreArchivo + ".xml"
        );
    }
    
    /**
     *
     * @param nombreReporte
     * nombre del reporte que se desea descargar, debe estar
     * alamacenado en la ruta de archivos de la entidad y tener la
     * extension .jasper
     * @param parametros
     * parametros del reporte que se va a generar maneja la estructura
     * llave valor
     * @param conection
     * conexi�n con la base de datos existente
     * @param formato
     * formato en el que se debe generar el reporte segun enumerado
     * FORMATOS de la clase ReportesBean
     * @return Objeto de tipo StreamedContent para devolver a la forma
     * @throws JRException
     * @throws IOException
     * @throws SysmanException
     * @throws DRException
     */
    public static StreamedContent exportarStreamed(String nombreReporte,
        Map<String, Object> parametros,
        String nombreConexion, ReportesBean.FORMATOS formato,
        DatosSesion datosSesion)
                    throws JRException, IOException, SysmanException {
        StreamedContent archivoDescarga = null;

        ReportesBean bean = datosSesion != null
            ? ReportesBean.getInstance(datosSesion)
            : ReportesBean.getInstance();
        ConectorPool con = new ConectorPool();
        try {
            con.conectar(nombreConexion);
            archivoDescarga = bean.exportarStreamed(nombreReporte,
                            parametros, con.getConection(), formato);
        }
        catch (NamingException | SQLException e) {
            throw new SysmanException(e, e.getMessage());
        }
        finally {
            try {
                con.getConection().close();
            }
            catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        return archivoDescarga;
    }

    public static TextColumnBuilder[] getDataSource(String consulta,
        String nombreConexion,
        Collection<Map<String, Object>> collection)
                    throws IOException, DRException, SysmanException {

        ReportesBean bean = ReportesBean.getInstance();
        ConectorPool con = new ConectorPool();
        TextColumnBuilder<?>[] arr;
        try {
            con.conectar(nombreConexion);
            arr = bean.getDataSource(consulta, collection, con.getConection());

        }
        catch (NamingException | SQLException e) {
            throw new SysmanException(e, e.getMessage());
        }
        finally {
            try {
                con.getConection().close();
            }
            catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return arr;
    }

    /**
     *
     * @param consulta
     * Consulta que se desea exportar
     * @param conection
     * conexi�n con la base de datos existente
     * @param formato
     * formato en el que se debe generar el reporte segun enumerado
     * FORMATOS de la clase ReportesBean
     * @throws JRException
     * @throws IOException
     * @throws SQLException
     * @throws DRException
     * @throws SysmanException
     * @deprecated usar metodo exportarHojaDatosStreamed y retornar en
     * la variable de tipo StreamedContent .
     */
    @Deprecated
    public static void exportarHojaDeDatos(String consulta,
        String nombreConexion, ReportesBean.FORMATOS formato)
                    throws JRException, IOException, DRException,
                    SysmanException {

        ReportesBean bean = ReportesBean.getInstance();
        ConectorPool con = new ConectorPool();
        try {
            con.conectar(nombreConexion);
            bean.exportarHojaDatos(con.getConection(), consulta, formato);
        }
        catch (NamingException | SQLException e) {
            throw new SysmanException(e, e.getMessage());
        }
        finally {
            try {
                con.getConection().close();
            }
            catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     *
     * @param consulta
     * Consulta que se desea exportar
     * @param conection
     * conexi�n con la base de datos existente
     * @param formato
     * formato en el que se debe generar el reporte segun enumerado
     * FORMATOS de la clase ReportesBean
     * @return Objeto de tipo StreamedContent para devolver a la forma
     * @throws JRException
     * @throws IOException
     * @throws SQLException
     * @throws DRException
     * @throws SysmanException
     */
    public static StreamedContent exportarHojaDatosStreamed(String consulta,
        String nombreConexion,
        ReportesBean.FORMATOS formato) throws JRException, IOException,
                    SQLException, DRException, SysmanException {

        ReportesBean bean = ReportesBean.getInstance();
        ConectorPool con = new ConectorPool();
        try {
            con.conectar(nombreConexion);
            return bean.exportarHojaDatosStreamed(con.getConection(), consulta,
                            formato);

        }
        catch (NamingException | SQLException e) {
            throw new SysmanException(e, e.getMessage());
        }
        finally {
            try {
                con.getConection().close();
            }
            catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     *
     * @param consulta
     * Consulta que se desea exportar
     * @param conection
     * conexion con la base de datos existente
     * @param formato
     * formato en el que se debe generar el reporte segun enumerado
     * FORMATOS de la clase ReportesBean
     * @param nombreArchivo
     * Nombre del archivo.
     * @return Objeto de tipo StreamedContent para devolver a la forma
     * @throws JRException
     * @throws IOException
     * @throws SQLException
     * @throws DRException
     * @throws SysmanException
     */
    public static StreamedContent exportarHojaDatosStreamed(String consulta,
        String nombreConexion,
        ReportesBean.FORMATOS formato, String nombreArchivo)
                    throws JRException, IOException, SQLException, DRException,
                    SysmanException {
        ReportesBean bean = ReportesBean.getInstance();
        ConectorPool con = new ConectorPool();

        try {
            con.conectar(nombreConexion);
            return bean.exportarHojaDatosStreamed(con.getConection(), consulta,
                            formato, nombreArchivo);
        }
        catch (NamingException | SQLException e) {
            throw new SysmanException(e, e.getMessage());
        }
        finally {
            try {
                con.getConection().close();
            }
            catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
    //mrosero 02/01/2024

    /**
     *
     * @param consulta
     * Consulta que se desea exportar
     * @param conection
     * conexion con la base de datos existente
     * @param formato
     * formato en el que se debe generar el reporte segun enumerado
     * FORMATOS de la clase ReportesBean
     * @param nombreArchivo
     * Nombre del archivo.
     * @param datasesion
     * Opcional. Datos de sesi&oacute;n que deben ser enviados si el
     * llamado se realiza desde una API; de lo contrario
     * env&iacute;elo como nulo.
     * @return Objeto de tipo StreamedContent para devolver a la forma
     * @throws JRException
     * @throws IOException
     * @throws SQLException
     * @throws DRException
     * @throws SysmanException
     */
    public static StreamedContent exportarHojaDatosStreamed(String consulta,
        String nombreConexion,
        ReportesBean.FORMATOS formato, String nombreArchivo, DatosSesion datosSesion)
                    throws JRException, IOException, SQLException, DRException,
                    SysmanException {
    	ReportesBean bean = datosSesion != null
                ? ReportesBean.getInstance(datosSesion)
                : ReportesBean.getInstance();
        ConectorPool con = new ConectorPool();

        try {
            con.conectar(nombreConexion);
            return bean.exportarHojaDatosStreamed(con.getConection(), consulta,
                            formato, nombreArchivo);
        }
        catch (NamingException | SQLException e) {
            throw new SysmanException(e, e.getMessage());
        }
        finally {
            try {
                con.getConection().close();
            }
            catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    //mrosero

    /**
     *
     * @param consultas
     * vector de consultas de las hojas de datos que se desean generar
     * @param conexion
     * conexi�n con la base de datos existente
     * @param formato
     * formato en el que se debe generar el reporte segun enumerado
     * FORMATOS de la clase ReportesBean
     * @param nombreHojas
     * vector opcional que contiene los nombres de las hojas de
     * trabajo para el caso de archivos Excel
     * @return Objeto de tipo StreamedContent para devolver a la forma
     * @throws SQLException
     * @throws IOException
     * @throws DRException
     * @throws SysmanException
     */
    public static StreamedContent exportarHojaDatosStreamed(String[] consultas,
        String nombreConexion,
        ReportesBean.FORMATOS formato, String... nombreHojas)
                    throws IOException, DRException, SysmanException {

        ReportesBean bean = ReportesBean.getInstance();
        ConectorPool con = new ConectorPool();
        try {
            con.conectar(nombreConexion);
            return bean.exportarHojaDatosStreamed(con.getConection(), consultas,
                            formato, nombreHojas);
        }
        catch (NamingException | SQLException e) {
            throw new SysmanException(e, e.getMessage());
        }
        finally {
            try {
                con.getConection().close();
            }
            catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Este metodo genera un archivo de extension .zip que contiene
     * las hojas de datos especificadas segun los parametros
     *
     * @param nombres
     * vector con los nombres de las hojas de datos que se deben
     * generar y comprimir
     * @param consultas
     * vector de consultas de las hojas de datos que se desean
     * comprimir
     * @param conection
     * Conexion con la base de datos
     * @param formatos
     * Formato o formatos en los que se deben generar los reportes, si
     * se ingresa como vector dene llevara el mismo orden que el
     * vector de nombres
     * @throws IOException
     * @throws JRException
     * @throws SysmanException
     * @deprecated usar metodo exportarComprimidoHojaDatosStreamed y
     * retornar en la variable de tipo StreamedContent .
     */
    @Deprecated
    public static void exportarComprimidoHojaDatos(String[] nombres,
        String[] consultas, Connection conection,
        ReportesBean.FORMATOS... formatos)
                    throws JRException, IOException, SQLException, DRException,
                    SysmanException {

        ReportesBean bean = ReportesBean.getInstance();

        bean.generarComprimidoHojasDatos(nombres, consultas, conection,
                        formatos);
    }

    /**
     *
     * @param nombres
     * vector con los nombres de las hojas de datos que se deben
     * generar y comprimir
     * @param consultas
     * vector de consultas de las hojas de datos que se desean
     * comprimir
     * @param conection
     * Conexion con la base de datos
     * @param formatos
     * Formato o formatos en los que se deben generar los reportes, si
     * se ingresa como vector dene llevara el mismo orden que el
     * vector de nombres
     * @return Objeto de tipo StreamedContent para devolver a la forma
     * @throws JRException
     * @throws IOException
     * @throws SQLException
     * @throws DRException
     * @throws SysmanException
     */
    public static StreamedContent exportarComprimidoHojaDatosStreamed(
        String[] nombres, String[] consultas,
        String nombreConexion, ReportesBean.FORMATOS... formatos)
                    throws JRException, IOException, SQLException, DRException,
                    SysmanException {
        ReportesBean bean = ReportesBean.getInstance();
        ConectorPool con = new ConectorPool();
        try {
            con.conectar(nombreConexion);
            return bean.generarComprimidoHojasDatosStreamed(nombres, consultas,
                            con.getConection(), formatos);
        }
        catch (NamingException | SQLException e) {
            throw new SysmanException(e, e.getMessage());
        }
        finally {
            try {
                con.getConection().close();
            }
            catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Este metodo genera un archivo de extension .zip que contiene
     * los reportes especificados segun los parametros
     *
     * @param nombres
     * vector con los nombre de los reportes que se deben generar y
     * comprimir
     * @param listaParametros
     * vector de Maps de parametros correspondientes a los reportes
     * que se deben generar, el orden de los parametros debe ser igual
     * al orden de los reportes en el vector de nombres
     * @param conection
     * Conexion con la base de datos
     * @param formatos
     * Formato o formatos en los que se deben generar los reportes, si
     * se ingresa como vector dene llevara el mismo orden que el
     * vector de nombres
     * @throws IOException
     * @throws JRException
     * @throws SysmanException
     * @deprecated usar metodo exportarComprimidoReportesStreamed y
     * retornar en la variable de tipo StreamedContent .
     */
    @Deprecated
    public static void exportarComprimidoReportes(String[] nombres,
        Map<String, Object>[] listaParametros,
        Connection conection, ReportesBean.FORMATOS... formatos)
                    throws JRException, IOException, SQLException, DRException,
                    SysmanException {

        ReportesBean bean = ReportesBean.getInstance();

        bean.generarComprimidoReporte(nombres, listaParametros, conection,
                        formatos);
    }

    /**
     * Este metodo genera un archivo de extension .zip que contiene
     * los reportes especificados segun los parametros
     *
     * @param nombres
     * vector con los nombre de los reportes que se deben generar y
     * comprimir
     * @param listaParametros
     * vector de Maps de parametros correspondientes a los reportes
     * que se deben generar, el orden de los parametros debe ser igual
     * al orden de los reportes en el vector de nombres
     * @param conection
     * Conexion con la base de datos
     * @param formatos
     * Formato o formatos en los que se deben generar los reportes, si
     * se ingresa como vector dene llevara el mismo orden que el
     * vector de nombres
     * @return Objeto de tipo StreamedContent para devolver a la forma
     * @throws IOException
     * @throws JRException
     * @throws SysmanException
     */
    public static StreamedContent exportarComprimidoReportesStreamed(
        String[] nombres,
        Map<String, Object>[] listaParametros, String nombreConexion,
        ReportesBean.FORMATOS... formatos)
                    throws JRException, IOException, SQLException, DRException,
                    SysmanException {
        ReportesBean bean = ReportesBean.getInstance();
        ConectorPool con = new ConectorPool();
        try {
            con.conectar(nombreConexion);

            return bean.generarComprimidoReporteStreamed(nombres,
                            listaParametros, con.getConection(), formatos);
        }
        catch (NamingException | SQLException e) {
            throw new SysmanException(e, e.getMessage());
        }
        finally {
            try {
                con.getConection().close();
            }
            catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     *
     * @param nombresReportes
     * vector con los nombre de los reportes que se deben generar y
     * comprimir
     * @param listaParametrosReportes
     * vector de Maps de parametros correspondientes a los reportes
     * que se deben generar, el orden de los parametros debe ser igual
     * al orden de los reportes en el vector de nombres de reportes
     * @param formatoReportes
     * Formato en el que se deben generar todos los reportes
     * @param nombresHojas
     * vector con los nombres de las hojas de datos que se deben
     * generar y comprimir
     * @param consultasHojas
     * vector de consultas de las hojas de datos que se desean
     * comprimir
     * @param formatoHojas
     * Formato en el que se deben generar todas las hojas de datos
     * @param conection
     * Conexion con la base de datos
     * @throws IOException
     * @throws JRException
     * @throws SQLException
     * @throws DRException
     * @throws SysmanException
     * @deprecated usar metodo exportarComprimidoMixtoStreamed y
     * retornar en la variable de tipo StreamedContent .
     */
    @Deprecated
    public static void exportarComprimidoMixto(String[] nombresReportes,
        Map<String, Object>[] listaParametrosReportes,
        ReportesBean.FORMATOS formatoReportes, String[] nombresHojas,
        String[] consultasHojas,
        ReportesBean.FORMATOS formatoHojas, Connection conection)
                    throws JRException, IOException, SQLException, DRException,
                    SysmanException {

        ReportesBean bean = ReportesBean.getInstance();

        bean.generarComprimidoMixto(nombresReportes, listaParametrosReportes,
                        formatoReportes, nombresHojas,
                        consultasHojas, formatoHojas, conection);
    }

    /**
     *
     * @param nombresReportes
     * vector con los nombre de los reportes que se deben generar y
     * comprimir
     * @param listaParametrosReportes
     * vector de Maps de parametros correspondientes a los reportes
     * que se deben generar, el orden de los parametros debe ser igual
     * al orden de los reportes en el vector de nombres de reportes
     * @param formatoReportes
     * Formato en el que se deben generar todos los reportes
     * @param nombresHojas
     * vector con los nombres de las hojas de datos que se deben
     * generar y comprimir
     * @param consultasHojas
     * vector de consultas de las hojas de datos que se desean
     * comprimir
     * @param formatoHojas
     * Formato en el que se deben generar todas las hojas de datos
     * @param conection
     * Conexion con la base de datos
     * @return Objeto de tipo StreamedContent para devolver a la forma
     * @throws IOException
     * @throws JRException
     * @throws SQLException
     * @throws DRException
     * @throws SysmanException
     */
    public static StreamedContent exportarComprimidoMixtoStreamed(
        String[] nombresReportes,
        Map<String, Object>[] listaParametrosReportes,
        ReportesBean.FORMATOS formatoReportes, String[] nombresHojas,
        String[] consultasHojas, ReportesBean.FORMATOS formatoHojas,
        String nombreConexion)
                    throws JRException, IOException, SQLException, DRException,
                    SysmanException {

        ReportesBean bean = ReportesBean.getInstance();
        ConectorPool con = new ConectorPool();
        try {
            con.conectar(nombreConexion);
            return bean.generarComprimidoMixtoStreamed(nombresReportes,
                            listaParametrosReportes, formatoReportes,
                            nombresHojas, consultasHojas, formatoHojas,
                            con.getConection());
        }
        catch (NamingException | SQLException e) {
            throw new SysmanException(e, e.getMessage());
        }
        finally {
            try {
                con.getConection().close();
            }
            catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

    }

    /**
     *
     * @param nombresReportes
     * vector con los nombre de los reportes que se deben generar y
     * comprimir
     * @param listaParametrosReportes
     * vector de Maps de parametros correspondientes a los reportes
     * que se deben generar, el orden de los parametros debe ser igual
     * al orden de los reportes en el vector de nombres de reportes
     * @param formatoReportes
     * Vector de formatos en el que se deben generar todos los
     * reportes
     * @param nombresHojas
     * vector con los nombres de las hojas de datos que se deben
     * generar y comprimir
     * @param consultasHojas
     * vector de consultas de las hojas de datos que se desean
     * comprimir
     * @param formatoHojas
     * Vector de formatos en el que se deben generar todas las hojas
     * de datos
     * @param conection
     * Conexion con la base de datos
     * @throws IOException
     * @throws JRException
     * @throws SQLException
     * @throws DRException
     * @throws SysmanException
     * @deprecated usar metodo exportarComprimidoMixtoStreamed y
     * retornar en la variable de tipo StreamedContent .
     */
    @Deprecated
    public static void exportarComprimidoMixto(String[] nombresReportes,
        Map<String, Object>[] listaParametrosReportes,
        ReportesBean.FORMATOS[] formatoReportes, String[] nombresHojas,
        String[] consultasHojas,
        ReportesBean.FORMATOS[] formatoHojas, Connection conection)
                    throws JRException, IOException, SQLException, DRException,
                    SysmanException {

        ReportesBean bean = ReportesBean.getInstance();

        bean.generarComprimidoMixto(nombresReportes, listaParametrosReportes,
                        formatoReportes, nombresHojas,
                        consultasHojas, formatoHojas, conection);
    }

    /**
     *
     * @param nombresReportes
     * vector con los nombre de los reportes que se deben generar y
     * comprimir
     * @param listaParametrosReportes
     * vector de Maps de parametros correspondientes a los reportes
     * que se deben generar, el orden de los parametros debe ser igual
     * al orden de los reportes en el vector de nombres de reportes
     * @param formatoReportes
     * Vector de formatos en el que se deben generar todos los
     * reportes
     * @param nombresHojas
     * vector con los nombres de las hojas de datos que se deben
     * generar y comprimir
     * @param consultasHojas
     * vector de consultas de las hojas de datos que se desean
     * comprimir
     * @param formatoHojas
     * Vector de formatos en el que se deben generar todas las hojas
     * de datos
     * @param conection
     * Conexion con la base de datos
     * @return
     * @throws IOException
     * @throws JRException
     * @throws SQLException
     * @throws DRException
     * @throws SysmanException
     */
    public static StreamedContent exportarComprimidoMixtoStreamed(
        String[] nombresReportes,
        Map<String, Object>[] listaParametrosReportes,
        ReportesBean.FORMATOS[] formatoReportes,
        String[] nombresHojas, String[] consultasHojas,
        ReportesBean.FORMATOS[] formatoHojas, String nombreConexion)
                    throws JRException, IOException, SQLException, DRException,
                    SysmanException {

        ReportesBean bean = ReportesBean.getInstance();
        ConectorPool con = new ConectorPool();
        try {
            con.conectar(nombreConexion);
            return bean.generarComprimidoMixtoStreamed(nombresReportes,
                            listaParametrosReportes, formatoReportes,
                            nombresHojas, consultasHojas, formatoHojas,
                            con.getConection());
        }
        catch (NamingException | SQLException e) {
            throw new SysmanException(e, e.getMessage());
        }
        finally {
            try {
                con.getConection().close();
            }
            catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     *
     * @param salidas
     * Vector de objetos serializados para generar archivos
     * @param nombresArchivos
     * Vector con los nombres de los archivos y su respectiva
     * extension, en el mismo orden del vector de salidas
     * @throws JRException
     * @throws IOException
     * @throws SQLException
     * @throws DRException
     * @deprecated usar metodo exportarComprimidoGeneralStream y
     * retornar en la variable de tipo StreamedContent .
     */
    @Deprecated
    public static void exportarComprimidoGeneral(ByteArrayInputStream[] salidas,
        String[] nombresArchivos)
                    throws JRException, IOException, SQLException, DRException {

        ReportesBean bean = ReportesBean.getInstance();

        bean.generarComprimidoGeneral(salidas, nombresArchivos);
    }

    /**
     *
     * @param salidas
     * Vector de objetos serializados para generar archivos
     * @param nombresArchivos
     * Vector con los nombres de los archivos y su respectiva
     * extension, en el mismo orden del vector de salidas
     * @return Objeto de tipo StreamedContent para devolver a la forma
     * @throws JRException
     * @throws IOException
     * @throws SQLException
     * @throws DRException
     */
    public static StreamedContent exportarComprimidoGeneralStreamed(
        ByteArrayInputStream[] salidas,
        String[] nombresArchivos)
                    throws JRException, IOException, SQLException, DRException {

        ReportesBean bean = ReportesBean.getInstance();

        return bean.generarComprimidoGeneralStreamed(salidas, nombresArchivos);
    }

    /**
     *
     * @param salidas
     * Vector de objetos serializados para generar archivos.
     * @param nombresArchivos
     * Vector con los nombres de los archivos y su respectiva
     * extension, en el mismo orden del vector de salidas.
     * @param nombreComprimido
     * Nombre, sin incluir extensión, que va a tener el archivo
     * comprimido que se va a generar.
     * @return Objeto de tipo StreamedContent para devolver a la
     * forma.
     * @throws JRException
     * @throws IOException
     * @throws SQLException
     * @throws DRException
     * @author jrodrigueza
     */
    public static StreamedContent exportarComprimidoGeneralStreamed(
        ByteArrayInputStream[] salidas,
        String[] nombresArchivos, String nombreComprimido)
                    throws JRException, IOException, SQLException, DRException {

        ReportesBean bean = ReportesBean.getInstance();

        return bean.generarComprimidoGeneralStreamed(salidas, nombresArchivos,
                        nombreComprimido);
    }

    public static ByteArrayInputStream serializarReporte(String nombreReporte,
        Map<String, Object> parametros,
        String nombreConexion, ReportesBean.FORMATOS formato)
                    throws JRException, IOException, SysmanException {

        ReportesBean bean = ReportesBean.getInstance();
        ConectorPool con = new ConectorPool();
        try {
            con.conectar(nombreConexion);
            return bean.serializarReporte(nombreReporte, parametros,
                            con.getConection(), formato);
        }
        catch (NamingException | SQLException e) {
            throw new SysmanException(e, e.getMessage());
        }
        finally {
            try {
                con.getConection().close();
            }
            catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Obtiene el arreglo de bytes de un reporte determinado.
     * 
     * @param nombreReporte
     * c&oacute;digo que identifica el reporte
     * @param parametros
     * par&aacute;metros que recibe el reporte.
     * @param nombreConexion
     * nombre de la conexi&oacute;n a la base de datos.
     * @param formato
     * tipo de archivo que se desea generar.
     * @param datosSesion
     * Opcional. Datos de sesi&oacute;n que deben ser enviados si el
     * llamado se realiza desde una API; de lo contrario
     * env&iacute;elo como nulo.
     * @return reporte como arreglo de bytes.
     * @throws JRException
     * en caso de que se presenten problemas al compilar y construir
     * el reporte en iReport.
     * @throws IOException
     * en caso de que no se pueda recuperar el archivo.
     * @throws SysmanException
     * en caso de que no existan datos para mostrar el informe.
     */
    public static byte[] serializarReporteBase64(
        String nombreReporte,
        Map<String, Object> parametros,
        String nombreConexion,
        FORMATOS formato, DatosSesion datosSesion)
                    throws JRException, IOException, SysmanException {
        ReportesBean bean = datosSesion != null
            ? ReportesBean.getInstance(datosSesion)
            : ReportesBean.getInstance();
        ConectorPool con = new ConectorPool();
        try {
            con.conectar(nombreConexion);
            return bean.serializarReporteBase64(nombreReporte, parametros,
                            con.getConection(), formato);
        }
        catch (NamingException | SQLException e) {
            throw new SysmanException(e, e.getMessage());
        }
        finally {
            try {
                con.getConection().close();
            }
            catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    public static ByteArrayInputStream serializarHojaDatos(String consulta,
        String nombreConexion,
        ReportesBean.FORMATOS formato) throws JRException, IOException,
                    SQLException, DRException, SysmanException {

        ReportesBean bean = ReportesBean.getInstance();
        ConectorPool con = new ConectorPool();
        try {
            con.conectar(nombreConexion);
            return bean.serializarHojaDatos(con.getConection(), consulta,
                            formato);
        }
        catch (NamingException | SQLException e) {
            throw new SysmanException(e, e.getMessage());
        }
        finally {
            try {
                con.getConection().close();
            }
            catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    public static ByteArrayInputStream serializarPlano(String contenido)
                    throws JRException, IOException {

        ArchivosBean bean = ArchivosBean.getInstance();
        return bean.serializarPlano(contenido);
    }

    public static ByteArrayInputStream serializarPlano(String contenido,
        String encoding)
                    throws JRException, IOException {

        ArchivosBean bean = ArchivosBean.getInstance();
        return bean.serializarPlano(contenido, encoding);
    }

    public static ByteArrayInputStream serializarReporteConstrasenia(
        String nombreReporte,
        Map<String, Object> parametros, String nombreConexion,
        ReportesBean.FORMATOS formato, String contrasena)
                    throws JRException, IOException, SysmanException {

        ReportesBean bean = ReportesBean.getInstance();
        ConectorPool con = new ConectorPool();
        try {
            con.conectar(nombreConexion);

            return bean.exportarConContrasenaSerializado(nombreReporte,
                            parametros, con.getConection(), formato,
                            contrasena);
        }
        catch (NamingException | SQLException e) {
            throw new SysmanException(e, e.getMessage());
        }
        finally {
            try {
                con.getConection().close();
            }
            catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
    
    public static ByteArrayInputStream serializarReporteContrasenia(
            String nombreReporte,
            Map<String, Object> parametros, String nombreConexion,
            ReportesBean.FORMATOS formato, String contrasena,DatosSesion datosSesion)
                        throws JRException, IOException, SysmanException {

	    	ReportesBean bean = datosSesion != null
	    			? ReportesBean.getInstance(datosSesion)
	                : ReportesBean.getInstance();
            ConectorPool con = new ConectorPool();
            try {
                con.conectar(nombreConexion);

                return bean.exportarConContrasenaSerializado(nombreReporte,
                                parametros, con.getConection(), formato,
                                contrasena);
            }
            catch (NamingException | SQLException e) {
                throw new SysmanException(e, e.getMessage());
            }
            finally {
                try {
                    con.getConection().close();
                }
                catch (SQLException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }

    public static StreamedContent getArchivoDescarga(InputStream contenido,
        String nombre)
                    throws JRException, IOException {

        ArchivosBean bean = ArchivosBean.getInstance();
        return bean.getArchivoDescarga(contenido, nombre);
    }

    public static StreamedContent getArchivoDescarga(InputStream contenido,
        String nombre, String contentType)
                    throws JRException, IOException {

        ArchivosBean bean = ArchivosBean.getInstance();
        return bean.getArchivoDescarga(contenido, nombre, contentType);
    }

    public static StreamedContent getArchivoDescarga(InputStream contenido,
        String nombre, String contentType,
        String encoding) throws JRException, IOException {

        ArchivosBean bean = ArchivosBean.getInstance();
        return bean.getArchivoDescarga(contenido, nombre, contentType,
                        encoding);
    }

    /**
     * Permite la descarga de un archivo. Internamente, genera el tipo
     * MIME adecuado para la descarga.
     *
     * @param contenido
     * Inputstream del archivo
     * @param nombre
     * Nombre del archivo
     * @return Archivo para descargar del navegador.
     * @throws JRException
     * @throws IOException
     */
    public static StreamedContent getArchivoDescargaStreamed(
        InputStream contenido, String nombre)
                    throws JRException, IOException {

        ArchivosBean bean = ArchivosBean.getInstance();
        return bean.getStreamedContentFromFile(contenido, nombre);
    }

    public static void ejecutarJavaScript(String codigo) {
        RequestContext requestContext = RequestContext.getCurrentInstance();
        requestContext.execute(codigo);
    }

    public static String getTituloMensajes() {
        return tituloMensajes;
    }

    /**
     * Crea el archivo a partir de un arreglo de bytes, con nombre y
     * ubicacion determinada.
     * 
     * @param bs
     * Archivo como arreglo de bytes.
     * @param path
     * Directorio en donde se ubicara el archivo.
     * @param fileName
     * Nombre del archivo.
     */
    public static void upload(byte[] bs, String path, String fileName) {
        File file = new File(path + fileName);
        try (BufferedOutputStream stream = new BufferedOutputStream(
                        new FileOutputStream(file))) {
            stream.write(bs);
            JsfUtil.agregarMensajeInformativo("El archivo " + file.getName()
                + " ha sido guardado correctamente.");
        }
        catch (IOException e) {
            Logger.getLogger(JsfUtil.class.getName()).log(Level.SEVERE, null,
                            e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public static File upload(InputStream event, String nombreArchivo,
        String ruta) {
        File file = new File(ruta + nombreArchivo);
        try (BufferedOutputStream stream = new BufferedOutputStream(
                        new FileOutputStream(file))) {
            byte[] bytes = IOUtils.toByteArray(event);
            stream.write(bytes);
            JsfUtil.agregarMensajeInformativo("El archivo " + file.getName()
                + " ha sido guardado correctamente.");

        }
        catch (IOException ex) {
            Logger.getLogger(JsfUtil.class.getName()).log(Level.SEVERE, null,
                            ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        return file;

    }

    public static File upload(InputStream event, String ruta) {
        File file = new File(ruta);
        try (BufferedOutputStream stream = new BufferedOutputStream(
                        new FileOutputStream(file))) {
            byte[] bytes = IOUtils.toByteArray(event);
            stream.write(bytes);
        }
        catch (IOException ex) {
            Logger.getLogger(JsfUtil.class.getName()).log(Level.SEVERE, null,
                            ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        return file;

    }

    public static String encodeImage(String ruta) throws FileNotFoundException {
        String image;
        if ((ruta == null) || ruta.isEmpty()) {
            return null;
        }
        String tipo = ruta.substring(ruta.lastIndexOf('.') + 1, ruta.length());
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            BufferedImage imageB = ImageIO.read(new File(ruta));
            ImageIO.write(imageB, tipo, bos);
            byte[] imagenBytes = bos.toByteArray();
            image = new String(Base64.encodeBase64(imagenBytes));
            return image;
        }
        catch (IOException ex) {
            Logger.getLogger(JsfUtil.class.getName()).log(Level.SEVERE, null,
                            ex);
            return null;
        }

    }

    public static String encodeImage(byte[] imagenBytes) {

        return new String(Base64.encodeBase64(imagenBytes));
    }

    /**
     * @author cmanrique
     * @return parametros cuando vienen de la propiedad del migrador.
     */
    @Deprecated
    public static Map<String, String> getParametros() {
        return FacesContext.getCurrentInstance().getExternalContext()
                        .getRequestParameterMap();
    }

    /**
     * Trae el contenido de un reporte en binario.
     *
     * @param nombreReporte
     * Nombre del reporte.
     * @param parametros
     * Parametros que recibe el reporte.
     * @param formato
     * Formato en el que se debe generar el reporte.
     * @param conexion
     * Conexión con la base de datos.
     * @return reporte como arreglo de bytes
     * @author jrodrigueza
     * @throws SysmanException
     */
    public static byte[] generarReporte(String nombreReporte,
        Map<String, Object> parametros,
        ReportesBean.FORMATOS formato, String conexion)
                    throws NamingException, JRException, IOException,
                    SysmanException {
        ReportesBean bean = ReportesBean.getInstance();
        return bean.generarReporte(nombreReporte, parametros, formato,
                        conexion);
    }

    /**
     * @param cadena
     * Cadena que se desea exportar
     * @param fila
     * identificador para el separador de filas
     * @param columna
     * identificador para el separador de Columnas
     * @param nombreHoja
     * Nombre de la hoja de trabajo
     * @param nombreDocumento
     * Nombre, sin incluir extensión, que va a tener el archivo excel
     * que se va a generar.
     * @return
     */
    public static StreamedContent armarExcel(String cadena, String fila,
        String columna, String nombreHoja,
        String nombreDocumento) {
        try {
            String hoja;
            Workbook workbook = new HSSFWorkbook();
            if (nombreHoja == null) {
                hoja = "Hoja1";
            }
            else {
                hoja = nombreHoja;
            }

            workbook.createSheet(hoja);
            Sheet sheet = workbook.getSheetAt(0);
            String[] separadorFila = cadena.split(fila);

            for (int i = 0; i < separadorFila.length; i++) {
                Row row = sheet.createRow(i);

                String[] separadorColumna = separadorFila[i].split(columna);
                for (int j = 0; j < separadorColumna.length; j++) {

                    Cell newCell = row.createCell(j);
                    newCell.setCellValue(separadorColumna[j]);
                    // sheet.autoSizeColumn(j);
                }
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            out.close();
            workbook.close();
            return JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(out.toByteArray()),
                            nombreDocumento + ".xls");
        }
        catch (IOException | JRException e) {

            Logger.getLogger(ArchivosBean.class.getName()).log(Level.SEVERE,
                            null, e);
            return null;
        }

    }

    /**
     * @author jgomez
     * 
     * @param cadena
     * Cadena que se desea exportar
     * @param fila
     * identificador para el separador de filas
     * @param columna
     * identificador para el separador de Columnas
     * @param nombreDocumento
     * Nombre, sin incluir extensión, que va a tener el archivo excel
     * que se va a generar.
     * @return
     */
    public static StreamedContent armarExcelconHoja(String cadena,
        String separadorHojas,
        String fila,
        String columna,
        String nombreDocumento) {
        try {
            String hoja;
            Workbook workbook = new HSSFWorkbook();
            String[] separadorHoja = cadena.split(separadorHojas);
            for (int h = 0; h < separadorHoja.length; h++) {
                String[] separadorFila = separadorHoja[h].split(fila);
                hoja = separadorFila[0];
                workbook.createSheet(hoja);
                Sheet sheet = workbook.getSheetAt(h);

                for (int i = 1; i < separadorFila.length; i++) {
                    Row row = sheet.createRow(i);

                    String[] separadorColumna = separadorFila[i].split(columna);
                    for (int j = 0; j < separadorColumna.length; j++) {

                        Cell newCell = row.createCell(j);
                        newCell.setCellValue(separadorColumna[j]);
                        // sheet.autoSizeColumn(j);
                    }
                    sheet.autoSizeColumn(i);
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            out.close();
            workbook.close();
            return JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(out.toByteArray()),
                            nombreDocumento + ".xls");
        }
        catch (IOException | JRException e) {

            Logger.getLogger(ArchivosBean.class.getName()).log(Level.SEVERE,
                            null, e);
            return null;
        }

    }

    public static String generarRuta(String modulo, String cedula,
        String constante, String nombreArchivo) {

        StringBuilder ruta = new StringBuilder();

        ruta.append(SessionUtil.getRutaDocumentos(modulo));
        ruta.append(cedula);
        ruta.append(constante);

        File verificar = new File(ruta.toString());
        if (!verificar.isDirectory()) {
            verificar.mkdirs();
        }

        ruta.append(nombreArchivo);

        return ruta.toString();
    }

    public static String generarNombreArchivo(String modulo,
        Map<String, Object> llave, String constante,
        String extension, String cedula) {

        String rutaCompleta;
        StringBuilder nombre = new StringBuilder();

        for (Object valor : llave.values()) {
            nombre.append(valor);
            nombre.append("_");
        }
        nombre.append(".");
        nombre.append(extension);

        rutaCompleta = generarRuta(modulo, cedula, constante,
                        nombre.toString());

        return rutaCompleta;
    }

    /**
     * 
     * @param nombreReporte
     * nombre del reporte
     * @param parametros
     * parametros de reporte
     * @param nombreConexion
     * nombre de la conexion
     * @param formato
     * tipo de formato con el que se genera el informe
     * @return areglo de bytes que represena el informe generado
     * @throws JRException
     * @throws SysmanException
     * @throws IOException
     */
    public static byte[] exportarStreamedSerializado(String nombreReporte,
        Map<String, Object> parametros,
        String nombreConexion, FORMATOS formato)
                    throws JRException, SysmanException, IOException {
        ConectorPool con = new ConectorPool();
        try {
            con.conectar(nombreConexion);
            ReportesBean bean = ReportesBean.getInstance();
            return bean.exportarStreamedSerializado(nombreReporte, parametros,
                            con.getConection(), formato);
        }
        catch (NamingException | SQLException e) {
            throw new SysmanException(e, e.getMessage());
        }
        finally {
            try {
                con.getConection().close();
            }
            catch (SQLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    public static StreamedContent reportesFut(String consulta,
        Map<String, Object> reemplazos,
        String encabezado,
        ReportesBean.FORMATOS formato, String nombreInforme, String modulo,
        boolean totales)
                    throws JRException, IOException, SQLException, DRException,
                    SysmanException {

        String cadena = encabezado;
        ConectorPool conexion = new ConectorPool();
        Statement consultaPlano = null;
        ResultSet totalConsulta = null;
        ResultSet resultadoConsulta = null;
        String strSqlTotal = "";

        String strSql = Reporteador.resuelveConsulta(consulta,
                        Integer.parseInt(modulo),
                        reemplazos);
        if (totales) {
            reemplazos.put("consultabase", strSql);
            strSqlTotal = Reporteador.resuelveConsulta(
                            SysmanFunciones.concatenar(consulta, "total"),
                            Integer.parseInt(modulo),
                            reemplazos);
        }
        if (ReportesBean.FORMATOS.EXCEL.equals(formato)) {
            return JsfUtil.exportarHojaDatosStreamed(strSql,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato, nombreInforme);
        }
        else {
            try {
                try {
                    conexion.conectar(ConectorPool.ESQUEMA_SYSMAN);
                }
                catch (NamingException e) {
                    LOGGER.error(e.getMessage(), e);
                    JsfUtil.agregarMensajeError(e.getMessage());
                }
                consultaPlano = conexion.getConection().createStatement();
                if (totales) {
                    totalConsulta = consultaPlano.executeQuery(strSqlTotal);

                    while (totalConsulta.next()) {
                        cadena = SysmanFunciones.concatenar(cadena, "\r\n");
                        cadena = SysmanFunciones.concatenar(cadena, "D", "\t",
                                        "VAL");
                        for (int i = 1; i <= totalConsulta.getMetaData()
                                        .getColumnCount(); i++) {
                            cadena = SysmanFunciones.concatenar(cadena, "\t",
                                            totalConsulta.getString(i));
                        }

                    }
                }
                // Ahora volcamos los datos
                resultadoConsulta = consultaPlano.executeQuery(strSql);
                while (resultadoConsulta.next()) {
                    cadena = SysmanFunciones.concatenar(cadena, "\r\n");
                    cadena = SysmanFunciones.concatenar(cadena, "D");
                    for (int i = 1; i <= resultadoConsulta.getMetaData()
                                    .getColumnCount(); i++) {
                        cadena = SysmanFunciones.concatenar(cadena, "\t",
                                        resultadoConsulta.getString(i));
                    }

                }

            }
            catch (Exception e) {
                LOGGER.error("Error en funcion reportesFut " + e.getMessage(),
                                e);
            }

            finally {
                try {
                    conexion.getConection().close();
                }
                catch (SQLException e) {
                    LOGGER.error("Error al cerrar la conexion reportesfut "
                        + e.getMessage(), e);
                }
            }

            return JsfUtil.getArchivoDescarga(JsfUtil.serializarPlano(
                            cadena),
                            SysmanFunciones.concatenar(
                                            SessionUtil.getCompaniaIngreso()
                                                            .getNombre(),
                                            nombreInforme, ".txt"));
        }
    }

    /**
     * Se crea metodo para generar el informe con excel plano desde
     * una consulta, generada en base a la consulta principal del
     * reporte
     * 
     * @author jgomez
     * @version 1
     * @serialData 10/2018/2018
     * 
     * @param reporte
     * :nombre del informe y consulta general, es el nombre de la
     * consulta del DDF
     * @param consultaExcel
     * :nombre de la consulta que se generara en excel plano
     * @param nombreConexion:
     * conexión sobre la cual se genera el reporte
     * @param formato
     * : formato de salida del reporte
     * @param reemplazos
     * : mapa con los reemplazos necesarios para las consultas
     * @param parametros
     * : mapa con los parametros que se entregan al informe
     * @param modulo
     * : modulo al cual pertenece el reporte
     * @return archivo de salida en formato StreamedContent
     * @throws JRException
     * @throws IOException
     * @throws SQLException
     * @throws DRException
     * @throws SysmanException
     */
    public static StreamedContent exportarExcelPlano(String reporte,
        String consultaExcel,
        String nombreConexion,
        ReportesBean.FORMATOS formato, Map<String, Object> reemplazos,
        Map<String, Object> parametros, int modulo)
                    throws JRException, IOException, SQLException, DRException,
                    SysmanException {
        if ((FORMATOS.EXCEL).equals(formato)
            || (FORMATOS.EXCEL97).equals(formato)) {
            reemplazos.put("consultaBase", Reporteador.resuelveConsulta(
                            reporte, Integer.valueOf(modulo),
                            reemplazos));
            String salida = Reporteador.resuelveConsulta(
                            consultaExcel,
                            Integer.valueOf(modulo),
                            reemplazos);
            return JsfUtil.exportarHojaDatosStreamed(salida,
                            nombreConexion, formato,
                            consultaExcel);
        }
        else {
            Reporteador.resuelveConsulta(reporte,
                            Integer.valueOf(modulo), reemplazos,
                            parametros);
            return JsfUtil.exportarStreamed(reporte,
                            parametros, nombreConexion,
                            formato);
        }
    }

    /**
     * Pemite generar reporte en base a un String que representa un
     * json
     *
     * @param nombreReporte
     * nombre del reporte que se desea descargar, debe estar
     * almacenado en la ruta de archivos de la entidad y tener la
     * extension .jasper
     * @param parametros
     * parametros del reporte que se va a generar maneja la estructura
     * llave valor
     * @param json
     * Parametro que representa el json que se desea para generar el
     * reporte
     * @param formato
     * formato en el que se debe generar el reporte segun enumerado
     * FORMATOS de la clase ReportesBean
     * @return Objeto de tipo StreamedContent para devolver a la forma
     * @throws JRException
     * @throws IOException
     * @throws SysmanException
     */
    public static StreamedContent exportarStreamedJson(String nombreReporte,
        Map<String, Object> parametros,
        String json, ReportesBean.FORMATOS formato)
                    throws JRException, IOException, SysmanException {
        StreamedContent archivoDescarga = null;

        ReportesBean bean = ReportesBean.getInstance();

        archivoDescarga = bean.exportarStreamed(nombreReporte,
                        parametros, json, formato);

        return archivoDescarga;
    }
    public static String obtenerParametroMarcaBlanca(String nombreParametro) {
    	ConectorPool conexion = new ConectorPool();
        Statement consultaPlano = null;
        ResultSet totalConsulta = null;
        String valor = "";
        boolean manejaMarcaBlanca = false;
        //Si maneja busca y resuelve consulta sino, me traigo los datos de la misma forma 
        //se deja compañia default 002 porque al inicio no se cual compañia se esta logeando 
        /* boolean manejaMarcaBlanca= "SI".equals(ejbSysmanUtil.consultarParametro("001",
		"MANEJA PARAMETROS MARCA BLANCA","-1", new Date(), false)); */
         
        
        
        
		try {
			String[] parametros = { "UN_COMPANIA          =>'001', ",
	                "UN_NOMBRE           => 'MANEJA PARAMETROS MARCA BLANCA', ",
	                "UN_MODULO              => -1, ",
	                "UN_FECHA_PAR               =>TO_DATE('", SysmanFunciones.convertirAFechaCadena(new Date()).toString() , "','DD/MM/YYYY'), ",
	                "UN_IND_MAYUS           => -1" };
			
			manejaMarcaBlanca = "SI".equals(AccionesImp.ejecutarFuncion(ConectorPool.ESQUEMA_SYSMAN,
			        "PCK_SYSMAN_UTL.FC_PAR",
			        SysmanFunciones.concatenar(parametros),
			        Types.VARCHAR));
		} catch (SystemException | ParseException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        
        if(manejaMarcaBlanca) {
        // la mejora es que esto pueda ser una consulta de la tabla consultas
        Map<String, Object> reemplazos = new HashMap<>();
        reemplazos.put("nombreParametro", nombreParametro);

        String strSql = Reporteador.resuelveConsulta("800550PARMARCABLANCA",
                reemplazos);
        try {
        	conexion.conectar(ConectorPool.ESQUEMA_SYSMAN);
        	
        	consultaPlano = conexion.getConection().createStatement();
                
        	totalConsulta = consultaPlano.executeQuery(strSql);
        	while(totalConsulta.next()) {
        		valor = totalConsulta.getString("VALOR");
        	}
        }catch (Exception e) {
				// TODO: handle exception
        	e.printStackTrace();
        	try {
				conexion.getConection().close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	return null;
		}
        finally {
            try {
                conexion.getConection().close();
            }
            catch (SQLException e) {
                LOGGER.error("Error al cerrar la conexion reportesfut "
                    + e.getMessage(), e);
            }
        }
        
        }
       else {
     //aqui busco mis valores 	
    /* 	
	PIE_PAGINA("Sysman ®."),
	BANNER("/opt/sysman/data/imagenes/banner.jpg"),
	TITULOPAG("Sysman ®."),
	TITULOLOGIN("Sysman ®."),
	TITULOMSJ("Sysman ®."),
	TITULOESPECIAL("Sysman ®."),
	IMPRESOEMPRPARM("Sysman S.A.S.   NIT 800.021.261-8.   Tel-Fax 7851420.  Cra 15 No. 16a-14, Centro-Sur, Duitama-Boyacá.    www.sysman.com.co"); */
    	   
       switch (nombreParametro) {
		case "BANNER":
			valor =  "/opt/sysman/data/imagenes/banner.jpg" ;
		break;
		case "IMPRESOEMPRPARM":
			valor =  "Sysman S.A.S.   NIT 800.021.261-8.   Tel-Fax 7851420.  Cra 15 No. 16a-14, Centro-Sur, Duitama-Boyacá.    www.sysman.com.co";
		break;

		default:
			valor = "Sysman ®.";
			break;
		}
    	   
       }
        return valor;
    }

	public static String getTituloPaginaEmpresaParametrizada() {
		return tituloPaginaEmpresaParametrizada;
	}

	public static void setTituloPaginaEmpresaParametrizada(String tituloPaginaEmpresaParametrizada) {
		JsfUtil.tituloPaginaEmpresaParametrizada = tituloPaginaEmpresaParametrizada;
	}
	
	/**
	 * Obtiene el valor de un parámetro general del sistema desde la base de datos.
	 *
	 * Ejecuta la función PCK_SYSMAN_UTL.FC_PAR del esquema SYSMAN
	 * utilizando la compañía activa en sesión y la fecha actual.
	 *
	 * Retorna el valor del parámetro configurado en la tabla de parámetros
	 * del sistema. Si ocurre un error o no existe el parámetro, retorna
	 * una cadena vacía.
	 *
	 * @param nombre nombre del parámetro general
	 * @return valor del parámetro o cadena vacía si no existe o hay error
	 */
	public static String obtenerParametrosGeneral(String nombre) {

	    try {

	        Object resultado = AccionesImp.ejecutarFuncion(
	                ConectorPool.ESQUEMA_SYSMAN,
	                "PCK_SYSMAN_UTL.FC_PAR",
	                "UN_COMPANIA => '" + SessionUtil.getCompania() + "', " +
	                "UN_NOMBRE => '" + nombre + "', " +
	                "UN_MODULO => -1, " +
	                "UN_FECHA_PAR => TO_DATE('" +
	                SysmanFunciones.convertirAFechaCadena(new Date()) +
	                "','DD/MM/YYYY HH24:MI:SS'), " +
	                "UN_IND_MAYUS => 0",
	                Types.VARCHAR
	        );

	        return resultado != null ? resultado.toString().trim() : "";

	    } catch (SystemException | ParseException e) {
	        LOGGER.error(e.getMessage(), e);
	        return "";
	    }
	}
	
	/**
	 * Obtiene un registro en formato JSON desde la base de datos
	 * usando la función PCK_AUDITORIA.FC_SELECTJSON.
	 *
	 * Ejecuta una consulta dinámica sobre la tabla indicada
	 * aplicando la cláusula WHERE enviada y retorna el resultado
	 * como JSON en formato String.
	 *
	 * Este método es utilizado principalmente para obtener
	 * los valores anteriores de un registro en procesos de auditoría.
	 *
	 * @param tabla nombre de la tabla a consultar
	 * @param where condición WHERE de la consulta
	 * @return JSON del registro o cadena vacía si ocurre un error
	 */
	public static String obtenerRegistroJson(String tabla, String where) {

	    try {
	        return Acciones.clobToString(
                    (Clob) AccionesImp.ejecutarFuncion(
                                    ConectorPool.ESQUEMA_SYSMAN,
                                    "PCK_AUDITORIA.FC_SELECTJSON",
                	                "UN_TABLA => '" + tabla + "', " +
                	                "UN_WHERE => '" + where + "'",
                                    Types.CLOB));

	    } catch (Exception e) {
	        LOGGER.error(e.getMessage(), e);
	        return "";
	    }
	}

}

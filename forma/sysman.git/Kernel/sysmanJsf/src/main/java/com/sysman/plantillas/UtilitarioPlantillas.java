/*-
 * UtilitarioPlantillas.java
 *
 * 1.0
 *
 * 30/05/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.plantillas;

import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.logica.DatosSesion;
import com.sysman.plantillas.enums.PlantillasEnum;
import com.sysman.plantillas.enums.PlantillasUrlEnum;
import com.sysman.plantillas.poixml.UtilitarioXssf;
import com.sysman.plantillas.poixml.UtilitarioXwpf;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.io.FilenameUtils;
import org.primefaces.model.StreamedContent;

/**
 * Utilidades para la gesti&oacute;n de plantillas.
 *
 * @version 1.0, 30/05/2018
 * @author jrodrigueza
 *
 */
public class UtilitarioPlantillas
{

    /**
     * Nombre predeterminado para archivos de Microsoft Word
     */
    public static final CharSequence NOMBRE_ARCHIVO_WORD = "Documento";
    /**
     * Nombre predeterminado para archivos de Microsoft Excel
     */
    public static final CharSequence NOMBRE_ARCHIVO_EXCEL = "Libro";
    /**
     * Extensi&oacute;n asociada a documentos de Microsoft Excel
     */
    public static final CharSequence EXTENSION_EXCEL = "xlsx";
    /**
     * Extensi&oacute;n asociada a documentos de Microsoft Excel 97-2003
     */
    public static final CharSequence EXTENSION_EXCEL_97_2003 = "xls";
    /**
     * Extensi&oacute;n asociada a documentos de Microsoft Word
     */
    public static final CharSequence EXTENSION_WORD = "docx";
    /**
     * Extensi&oacute;n asociada a documentos de Microsoft Word 97-2003
     */
    public static final CharSequence EXTENSION_WORD_97_2003 = "doc";
    
    
    public static final CharSequence EXTENSION_PDF = "pdf";
    /**
     * Nombre del directorio donde se alojan las plantillas.
     */
    public static final CharSequence DIRECTORIO_PLANTILLAS = "plantillasword";

    /**
     * Constructor privado.
     */
    private UtilitarioPlantillas()
    {
        throw new IllegalAccessError("Utility class");
    }

    /**
     * Permite obtener un registro consumiendo un servicio determinado identificado por un identificador y unos parametros de entrada.
     *
     * @param idServicio
     * identificador del servicio
     * @param parametrosEntrada
     * parametros que recibe el servicio
     * @return registro que representa los parametros de salida retornados de la petici&oacute;n GET
     * @throws SystemException
     * en caso de que se presenten problemas al procesar la petici&oacute;n.
     */
    public static Registro getRegistro(String idServicio,
        Map<String, Object> parametrosEntrada) throws SystemException
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(idServicio);
        RequestManager requestManager = new RequestManager();
        if (urlBean == null)
        {
            ResourceBundle idioma = ResourceBundle
                            .getBundle(SysmanConstantes.RUTA_IDIOMA);
            String msg = idioma.getString("TB_TB4134");
            msg = msg.replace("s$idServicio$s", idServicio);
            throw new SystemException(msg);
        }
        Parameter parameter = requestManager.get(urlBean.getUrl(),
                        parametrosEntrada);
        return RegistroConverter.toRegistro(parameter);
    }

    /**
     * Trae los datos configurados para la plantilla.
     *
     * @param codigoPlantilla
     * c&oacute;digo de la plantilla.
     * @param fechaPlantilla
     * cadena con la fecha de creaci&oacute;n de la plantilla. La fecha debe tener el formato: <strong>dd/MM/yyyy</strong>.
     *
     *
     * @return registro con los datos almacenados en la tabla MODELO_PLANTILLA
     * @throws SystemException
     * en caso de que se presenten problemas al procesar la petici&oacute;n.
     */
    public static Registro getModeloPlantilla(String codigoPlantilla,
        String fechaPlantilla) throws SystemException
    {
        Map<String, Object> parametrosEntrada = new HashMap<>();
        parametrosEntrada.put(GeneralParameterEnum.CODIGO.getName(),
                        codigoPlantilla);
        parametrosEntrada.put(GeneralParameterEnum.FECHA.getName(),
                        fechaPlantilla);
        return getRegistro(PlantillasUrlEnum.DSS_104067.getValue(),
                        parametrosEntrada);
    }

    /**
     * Trae el nombre del documento plantilla, incluida extensi&oacute;n.
     *
     * @param codigoPlantilla
     * c&oacute;digo de la plantilla.
     * @param fechaPlantilla
     * cadena con la fecha de creaci&oacute;n de la plantilla. La fecha debe tener el formato: <code>dd/MM/yyyy</code>.
     *
     * @return nombre archivo plantilla
     * @throws SystemException
     * en caso de que se presenten problemas al procesar la petici&oacute;n.
     */
    public static String getNombrePlantilla(String codigoPlantilla,
        String fechaPlantilla) throws SystemException
    {
        Registro registroPlantilla = getModeloPlantilla(codigoPlantilla,
                        fechaPlantilla);
        Map<String, Object> datosPlantilla = registroPlantilla.getCampos();
        return SysmanFunciones.toString(datosPlantilla
                        .get(PlantillasEnum.PLANTILLA.getValue()));
    }

    /**
     * Genera una plantilla Word/Excel dependiendo de la configuraci&oacute;n de la plantilla y serializa el archivo.
     *
     * @param codigoPlantilla
     * c&oacute;digo de plantilla
     * @param fechaPlantilla
     * fecha de plantilla
     * @param variablesConsulta
     * variables para resolver la consulta
     * @return documento serializado
     * @throws SystemException
     * en caso de que se presenten problemas al generar o exportar la plantilla.
     */
    public static byte[] serializarDocumento(String codigoPlantilla,
        Date fechaPlantilla, Map<String, String> variablesConsulta)
                    throws SystemException
    {
        return serializarDocumento(codigoPlantilla, fechaPlantilla,
                        variablesConsulta, null, null);
    }

    /**
     * Genera una plantilla Word/Excel dependiendo de la configuraci&oacute;n de la plantilla y serializa el archivo.
     *
     * @param codigoPlantilla
     * c&oacute;digo de plantilla
     * @param fechaPlantilla
     * fecha de plantilla
     * @param variablesConsulta
     * variables para resolver la consulta
     * @param datosSesion
     * datos de sesi&oacute;n.
     * @param salida
     * en caso de que la funcionalidad requiera devolver datos adicionales.
     * @return documento serializado
     * @throws SystemException
     * en caso de que se presenten problemas al generar o exportar la plantilla.
     */
    public static byte[] serializarDocumento(String codigoPlantilla,
        Date fechaPlantilla, Map<String, String> variablesConsulta,
        DatosSesion datosSesion, Map<String, Object> salida)
                    throws SystemException
    {
        byte[] bs;
        String fechaCadena;
        try
        {
            fechaCadena = SysmanFunciones.convertirAFechaCadena(fechaPlantilla);
        }
        catch (ParseException e)
        {
            throw new SystemException(e);
        }
        String nombrePlantilla = getNombrePlantilla(codigoPlantilla,
                        fechaCadena);
        validarNombrePlantilla(nombrePlantilla, codigoPlantilla, fechaCadena);
        String extension = FilenameUtils.getExtension(nombrePlantilla);
        Map<String, String> variablesModelo = new HashMap<>();
        String fechaFormatoOracle = SysmanFunciones
                        .formatearFecha(fechaPlantilla);
        if ((extension != null) && extension.contains(EXTENSION_EXCEL_97_2003))
        {
            UtilitarioXssf utilitarioXssf = new UtilitarioXssf(codigoPlantilla,
                            fechaFormatoOracle, variablesModelo,
                            variablesConsulta, datosSesion);
            bs = utilitarioXssf.serializarPlantilla();
        }
        else if ((extension != null)
            && extension.contains(EXTENSION_WORD_97_2003))
        {
            UtilitarioXwpf utilitarioXwpf = new UtilitarioXwpf(codigoPlantilla,
                            fechaFormatoOracle, variablesModelo,
                            variablesConsulta, datosSesion);
            bs = utilitarioXwpf.serializarPlantilla();
        }
        else
        {
            ResourceBundle idioma = ResourceBundle
                            .getBundle(SysmanConstantes.RUTA_IDIOMA);
            String msg = idioma.getString("TB_TB4110");
            msg = msg.replace("s$extension$s", extension);
            throw new SystemException(msg);
        }
        if (salida != null)
        {
            salida.put("extension", extension);
        }
        return bs;
    }

    /**
     * Genera una plantilla Word/Excel dependiendo de la configuraci&oacute;n de la plantilla y la exporta para ser descargada desde la aplicación web.
     *
     * @param codigoPlantilla
     * c&oacute;digo de plantilla
     * @param fechaPlantilla
     * fecha de plantilla
     * @param variablesConsulta
     * variables de consulta
     * @param nombreDocDescarga
     * nombre del documento a descargar
     * @param listaInicial
     * listado de variables de plantilla
     * @return Objeto de tipo StreamedContent para devolver a la forma.
     * @throws SystemException
     * en caso de que se presenten problemas al generar o exportar la plantilla.
     */
    public static StreamedContent exportarDocumento(String codigoPlantilla,
        String fechaPlantilla, Map<String, String> variablesConsulta,
        String nombreDocDescarga, List<Registro> listaInicial)
                    throws SystemException
    {

        StreamedContent streamedContent;
        ResourceBundle idioma = ResourceBundle
                        .getBundle(SysmanConstantes.RUTA_IDIOMA);
        Map<String, String> variablesModelo;
        try
        {
            variablesModelo = asignarVariablesUsuario(
                            listaInicial);
        }
        catch (ParseException parseExc)
        {
            throw new SystemException(parseExc);
        }
        catch (ClassCastException castExc)
        {

            throw new SystemException(idioma.getString("MSM_VARIABLE_FORMATO"));
        }

        String fechaParametro;
        if(SysmanFunciones.esBdSqlServer()) {
            fechaParametro = fechaPlantilla.substring(13, 23);
            
        } else {
            fechaParametro = fechaPlantilla.substring(9, 19);
        }
        String nombrePlantilla = getNombrePlantilla(codigoPlantilla,
                        fechaParametro);
        validarNombrePlantilla(nombrePlantilla, codigoPlantilla,
                        fechaParametro);
        String extension = FilenameUtils.getExtension(nombrePlantilla);
        if ((extension != null) && extension.contains(EXTENSION_EXCEL_97_2003))
        {
            UtilitarioXssf utilitarioXssf = new UtilitarioXssf(codigoPlantilla,
                            fechaPlantilla, variablesConsulta,
                            variablesConsulta);
            streamedContent = utilitarioXssf.exportarPlantilla();
        }
        else if ((extension != null)
            && extension.contains(EXTENSION_WORD_97_2003))
        {
            UtilitarioXwpf utilitarioXwpf = new UtilitarioXwpf(codigoPlantilla,
                            fechaPlantilla, variablesModelo, variablesConsulta);
            streamedContent = utilitarioXwpf
                            .exportarPlantilla(nombreDocDescarga);
        }
        else
        {

            String msg = idioma.getString("TB_TB4110");
            msg = msg.replace("s$extension$s", extension);
            throw new SystemException(msg);
        }
        return streamedContent;
    }
    
  
    /**
     * Creado para generar un archivo word especifico para aguazul sin afectar las demas entides 
     *
     */
    public static StreamedContent exportarDocumento1(String codigoPlantilla,
            String fechaPlantilla, Map<String, String> variablesConsulta,
            String nombreDocDescarga, List<Registro> listaInicial)
                        throws SystemException
        {

            StreamedContent streamedContent;
            ResourceBundle idioma = ResourceBundle
                            .getBundle(SysmanConstantes.RUTA_IDIOMA);
            Map<String, String> variablesModelo;
            try
            {
                variablesModelo = asignarVariablesUsuario(
                                listaInicial);
            }
            catch (ParseException parseExc)
            {
                throw new SystemException(parseExc);
            }
            catch (ClassCastException castExc)
            {

                throw new SystemException(idioma.getString("MSM_VARIABLE_FORMATO"));
            }

            String fechaParametro;
            if(SysmanFunciones.esBdSqlServer()) {
                fechaParametro = fechaPlantilla.substring(13, 23);
                
            } else {
                fechaParametro = fechaPlantilla.substring(9, 19);
            }
            String nombrePlantilla = getNombrePlantilla(codigoPlantilla,
                            fechaParametro);
            validarNombrePlantilla(nombrePlantilla, codigoPlantilla,
                            fechaParametro);
            String extension = FilenameUtils.getExtension(nombrePlantilla);
            if ((extension != null) && extension.contains(EXTENSION_EXCEL_97_2003))
            {
                UtilitarioXssf utilitarioXssf = new UtilitarioXssf(codigoPlantilla,
                                fechaPlantilla, variablesConsulta,
                                variablesConsulta);
                streamedContent = utilitarioXssf.exportarPlantilla();
            }
            else if ((extension != null)
                && extension.contains(EXTENSION_WORD_97_2003))
            {
                UtilitarioXwpf utilitarioXwpf = new UtilitarioXwpf(codigoPlantilla,
                                fechaPlantilla, variablesModelo, variablesConsulta);
                streamedContent = utilitarioXwpf
                                .exportarPlantilla1(nombreDocDescarga);
            }
            else
            {

                String msg = idioma.getString("TB_TB4110");
                msg = msg.replace("s$extension$s", extension);
                throw new SystemException(msg);
            }
            return streamedContent;
        }
    
    
    
    /**
     * Creado para generar un archivo word especifico para aguazul sin afectar las demas entides 
     *
     */
    public static StreamedContent exportarDocumentoAPdf(String codigoPlantilla,
            String fechaPlantilla, Map<String, String> variablesConsulta,
            String nombreDocDescarga, List<Registro> listaInicial, String rutaCertificado, String rutaPlantilla, boolean permiteEliminar)
                        throws SystemException
        {

            StreamedContent streamedContent;
            ResourceBundle idioma = ResourceBundle
                            .getBundle(SysmanConstantes.RUTA_IDIOMA);
            Map<String, String> variablesModelo;
            try
            {
                variablesModelo = asignarVariablesUsuario(
                                listaInicial);
            }
            catch (ParseException parseExc)
            {
                throw new SystemException(parseExc);
            }
            catch (ClassCastException castExc)
            {

                throw new SystemException(idioma.getString("MSM_VARIABLE_FORMATO"));
            }

            String fechaParametro;
            if(SysmanFunciones.esBdSqlServer()) {
                fechaParametro = fechaPlantilla.substring(13, 23);
                
            } else {
                fechaParametro = fechaPlantilla.substring(9, 19);
            }
            String nombrePlantilla = getNombrePlantilla(codigoPlantilla,
                            fechaParametro);
            validarNombrePlantilla(nombrePlantilla, codigoPlantilla,
                            fechaParametro);
            String extension = FilenameUtils.getExtension(nombrePlantilla);
            
            if ((extension != null) && extension.contains(EXTENSION_EXCEL_97_2003))
            {
                UtilitarioXssf utilitarioXssf = new UtilitarioXssf(codigoPlantilla,
                                fechaPlantilla, variablesConsulta,
                                variablesConsulta);
                streamedContent = utilitarioXssf.exportarPlantilla();
            }
            else if ((extension != null)
                && extension.contains(EXTENSION_WORD_97_2003))
            {
                UtilitarioXwpf utilitarioXwpf = new UtilitarioXwpf(codigoPlantilla,
                                fechaPlantilla, variablesModelo, variablesConsulta);
                rutaPlantilla += nombrePlantilla;
                streamedContent = utilitarioXwpf
                                .exportarPlantillaPdf(nombreDocDescarga,rutaCertificado,rutaPlantilla, permiteEliminar);
            }
            else
            {

                String msg = idioma.getString("TB_TB4110");
                msg = msg.replace("s$extension$s", extension);
                throw new SystemException(msg);
            }
            return streamedContent;
        }
    

    /**
     * Aplica formato a las variables creadas por el usuario y las asigna a las variables del modelo.
     *
     * @param listaInicial
     * Lista de variables creadas por el usuario, es decir, variables personalizadas que NO provienen de la consulta de plantilla.
     * @param variablesModelo
     * @return variables de usuario con el formato respectivo.
     * @throws ParseException
     * si se presentan problemas al parsear la fecha
     */
    private static Map<String, String> asignarVariablesUsuario(
        List<Registro> listaInicial) throws ParseException, ClassCastException
    {
        Map<String, String> variablesModelo = new HashMap<>();
        if (listaInicial != null)
        {
            for (Registro reg : listaInicial)
            {
                Map<String, Object> campos = reg.getCampos();
                String formato = SysmanFunciones.toString(
                                campos.get(PlantillasEnum.FORMATO.getValue()));
                String etiqueta = SysmanFunciones.toString(
                                campos.get(PlantillasEnum.ETIQUETA.getValue()));
                Object object = campos.get(
                                PlantillasEnum.STR_VALOR_ETIQUETA.getValue());
                String aux = null;

                if (Arrays.asList("T", "I").contains(formato))
                {
                    aux = SysmanFunciones.toString(object);
                }
                else if ("D".equals(formato))
                {
                    aux = SysmanFunciones.convertirAFechaCadena((Date) object);
                }
                else if ("L".equals(formato))
                {
                    if (object instanceof Date)
                    {
                        aux = darFormatoFechaLarga((Date) object);
                    }
                    else
                    {
                        aux = object == null ? " " : object.toString();
                    }
                }

                String valor = SysmanFunciones.nvlStr(aux, " ");
                variablesModelo.put(etiqueta, valor);
            }
        }
        return variablesModelo;
    }

    /**
     * Valida si el nombre de la plantilla es nulo.
     *
     * @param nombrePlantilla
     * nombre de plantilla
     * @param codigoPlantilla
     * identificador de plantilla
     * @param fechaPlantilla
     * cadena con la fecha de creaci&oacute;n de la plantilla. La fecha debe tener el formato: <code>dd/MM/yyyy</code>.
     * @throws SystemException
     * cuando sea nulo el nombre
     */
    private static void validarNombrePlantilla(String nombrePlantilla,
        String codigoPlantilla, String fechaPlantilla) throws SystemException
    {
        ResourceBundle idioma = ResourceBundle
                        .getBundle(SysmanConstantes.RUTA_IDIOMA);
        if (nombrePlantilla == null)
        {
            String msg = idioma.getString("TB_TB4109");
            msg = msg.replace("s$codigoPlantilla$s", codigoPlantilla);
            msg = msg.replace("s$fechaPlantilla$s", fechaPlantilla);
            throw new SystemException(msg);
        }
    }

    /**
     * Validar el valor del parametro fecha y le aplica formato de fehca larga. Si la fecha es nula retorna un espacio en blanco.
     *
     * @param fecha
     * valor a evaluar
     * @return Fecha formateada o espacio en blanco
     */
    public static String darFormatoFechaLarga(Date fecha)
    {
        SimpleDateFormat formateador = new SimpleDateFormat(
                        " dd 'de' MMMM  'de' yyyy",
                        new Locale("es_ES"));
        return fecha == null ? " " : formateador.format(fecha);
    }
    
    /**
     * Genera plantillas Word dependiendo de la configuraci&oacute;n de la plantilla y la exporta para ser descargadas desde la aplicación web.
     *
     * @param codigoPlantilla
     * c&oacute;digo de plantilla
     * @param fechaPlantilla
     * fecha de plantilla
     * @param variablesConsulta
     * variables de consulta
     * @param nombreDocDescarga
     * nombre del documento a descargar
     * @param listaInicial
     * listado de variables de plantilla
     * @return Objeto de tipo StreamedContent para devolver a la forma.
     * @throws SystemException
     * en caso de que se presenten problemas al generar o exportar la plantilla.
     */
    public static StreamedContent exportarDocumentoMasiva(String codigoPlantilla,
            String fechaPlantilla, Map<String, String> variablesConsulta,
            String nombreDocDescarga, List<Registro> listaInicial)
                        throws SystemException
        {

    	   StreamedContent  streamedContent;
            ResourceBundle idioma = ResourceBundle
                            .getBundle(SysmanConstantes.RUTA_IDIOMA);
            Map<String, String> variablesModelo;
            try
            {
                variablesModelo = asignarVariablesUsuario(
                                listaInicial);
            }
            catch (ParseException parseExc)
            {
                throw new SystemException(parseExc);
            }
            catch (ClassCastException castExc)
            {

                throw new SystemException(idioma.getString("MSM_VARIABLE_FORMATO"));
            }

            String fechaParametro;
            if(SysmanFunciones.esBdSqlServer()) {
                fechaParametro = fechaPlantilla.substring(13, 23);
                
            } else {
                fechaParametro = fechaPlantilla.substring(9, 19);
            }
            String nombrePlantilla = getNombrePlantilla(codigoPlantilla,
                            fechaParametro);
            validarNombrePlantilla(nombrePlantilla, codigoPlantilla,
                            fechaParametro);
            String extension = FilenameUtils.getExtension(nombrePlantilla);
       if ((extension != null)
                && extension.contains(EXTENSION_WORD_97_2003))
            {
                UtilitarioXwpf utilitarioXwpf = new UtilitarioXwpf(codigoPlantilla,
                                fechaPlantilla, variablesModelo, variablesConsulta);
                streamedContent = utilitarioXwpf
                                .exportarPlantillaMasiva(nombreDocDescarga);
            }
            else
            {

                String msg = idioma.getString("TB_TB4110");
                msg = msg.replace("s$extension$s", extension);
                throw new SystemException(msg);
            }
            return streamedContent;
        }
}

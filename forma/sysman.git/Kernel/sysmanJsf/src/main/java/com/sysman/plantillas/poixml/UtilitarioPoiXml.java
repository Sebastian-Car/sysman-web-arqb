/*-
 * UtilitarioPoiXml.java
 *
 * 1.0
 * 
 * 17/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.plantillas.poixml;

import com.sysman.dao.Registro;
import com.sysman.exception.SystemException;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.logica.DatosSesion;
import com.sysman.persistencia.ConectorPool;
import com.sysman.plantillas.UtilitarioPlantillas;
import com.sysman.plantillas.enums.PlantillasEnum;
import com.sysman.plantillas.enums.PlantillasUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.FormContinuoService;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Contiene las funcionalidades para todas las clases de documento POI
 * OOXML.
 * 
 * @version 1.0, 17/01/2018
 * @author jrodrigueza
 *
 */
public class UtilitarioPoiXml {

    /**
     * c&oacute;digo de la plantilla
     */
    protected String codigoPlantilla;
    /**
     * cadena que representa la fecha de la plantilla, con formato
     * Oracle.
     */
    protected String fechaPlantilla;

    /**
     * objeto para registro de mensajes tipo logging
     */
    protected final Log logger = LogFactory.getLog(this.getClass());

    /**
     * recurso para traer los textos del properties de idiomas
     */
    protected ResourceBundle idioma;
    /**
     * servicio
     */
    protected FormContinuoService service;
    /**
     * map con los datos de la plantilla
     */
    protected Map<String, Object> modeloPlantilla;
    /**
     * consulta asociada a la plantilla
     */
    protected String consultaPlantilla;
    /**
     * nombre de los campos llamados en la consulta de la plantilla
     */
    protected List<String> columnasConsulta;
    /**
     * datos que retorna la consulta de la plantilla, preparados con
     * el respectivo formato y listos para ser reemplazados en el
     * documento final
     */
    protected List<Registro> datos;
    /**
     * variables tipo consulta definidas en el documento plantilla
     */
    protected List<Registro> variablesConsulta;
    /**
     * variables tipo tabla definidas en el documento plantilla
     */
    protected List<Registro> variablesTabla;
    /**
     * variables definidas en el modelo
     */
    protected Map<String, String> variablesModelo;
    /**
     * variables de reemplazo para la consulta de la plantilla
     */
    protected Map<String, String> variablesReemplazoConsulta;
    /**
     * Conjunto de datos asociados a la sesi&oacute;n.
     */
    private DatosSesion datosSesion;

    /**
     * 
     * @param codigoPlantilla
     * c&oacute;digo de la plantilla
     * @param fechaPlantilla
     * cadena que representa la fecha de la plantilla con formato
     * Oracle
     * @param variablesModelo
     * variables personzalizadas creadas por el usuario
     * @param variablesReemplazoConsulta
     * variables de reemplazo para la consulta
     * @param datosSesion
     * datos de sesi&oacute;n en caso de que no se pueda usar
     * variables de sesi&oacute;n.
     */
    public UtilitarioPoiXml(String codigoPlantilla,
        String fechaPlantilla, Map<String, String> variablesModelo,
        Map<String, String> variablesReemplazoConsulta,
        DatosSesion datosSesion) {
        this.service = FormContinuoService.getInstance();
        this.codigoPlantilla = codigoPlantilla;
        this.fechaPlantilla = fechaPlantilla;
        this.variablesModelo = variablesModelo;
        this.variablesReemplazoConsulta = variablesReemplazoConsulta;
        this.datosSesion = datosSesion;
        idioma = ResourceBundle.getBundle(SysmanConstantes.RUTA_IDIOMA);
        modeloPlantilla = new HashMap<>();
        columnasConsulta = new ArrayList<>();
        variablesConsulta = new ArrayList<>();
        datos = new ArrayList<>();
        cargarDatosPlantillas();
    }

    /**
     * inicializacion de atributos gen&eacute;ricos
     */
    public void cargarDatosPlantillas() {
        Registro registro = getModeloPlantilla();
        if (registro != null) {
            modeloPlantilla = registro.getCampos();
        }
        if (!modeloPlantilla.isEmpty()) {
            consultaPlantilla = SysmanFunciones
                            .toString(modeloPlantilla
                                            .get(PlantillasEnum.CONSULTA
                                                            .getValue()));
            if (datosSesion != null) {
                Reporteador.setDatosSesion(datosSesion);
            }
            consultaPlantilla = Reporteador.reemplazaSql(consultaPlantilla);
            consultaPlantilla = Reporteador.reemplazarInicial(
                            variablesReemplazoConsulta,
                            consultaPlantilla);
            if (consultaPlantilla != null && !consultaPlantilla.isEmpty()) {
                columnasConsulta = service.getCamposListado(
                                ConectorPool.ESQUEMA_SYSMAN,
                                consultaPlantilla);
                List<Registro> datosConsulta = service.getListado(
                                ConectorPool.ESQUEMA_SYSMAN, consultaPlantilla);
                variablesConsulta = getVariablesConsulta();
                try {
                    datos = darFormatoColumnas(datosConsulta);
                }
                catch (ParseException e) {
                    logger.error(e.getMessage(), e);
                }
                variablesTabla = getVariablesTabla();
            }
        }
    }

    /**
     * Aplica el formato definido en las variables de usuario, para
     * cada campo que arroje la consulta.
     * 
     * @param datosConsulta
     * datos que retorna la consulta
     * 
     * @return datos preparados para ser reemplazados en el documento
     * final
     * @throws ParseException
     * en caso de que se presente un error al parsear campos tipo
     * fecha
     */
    private List<Registro> darFormatoColumnas(List<Registro> datosConsulta)
                    throws ParseException {
        List<Registro> list = datosConsulta;
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < variablesConsulta.size(); j++) {
                Map<String, Object> map = variablesConsulta.get(j).getCampos();
                String formato = SysmanFunciones.toString(
                                map.get(PlantillasEnum.FORMATO.getValue()));
                String etiqueta = SysmanFunciones.toString(
                                map.get(PlantillasEnum.ETIQUETA.getValue()));
                etiqueta = etiqueta.replace("<#", "");
                etiqueta = etiqueta.replace("#>", "");
                if ("M".equals(formato)) {
                    String tmp = list.get(i).getCampos()
                                    .get(etiqueta)
                                    .toString();
                    /*
                     * Formato para valores tipo Moneda. Se omite el
                     * simbolo de moneda.
                     */
                    NumberFormat formatoMoneda = NumberFormat
                                    .getCurrencyInstance(
                                                    new Locale("en", "US"));
                    DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) formatoMoneda)
                                    .getDecimalFormatSymbols();
                    decimalFormatSymbols.setCurrencySymbol("");
                    ((DecimalFormat) formatoMoneda).setDecimalFormatSymbols(
                                    decimalFormatSymbols);
                    String valor = formatoMoneda
                                    .format(Double.valueOf(tmp));

                    list.get(i).getCampos().put(etiqueta, valor);
                }
                else if ("D".equals(formato)) {
                    Date tmp = (Date) list.get(i).getCampos()
                                    .get(etiqueta);
                    String valor = SysmanFunciones.convertirAFechaCadena(
                                    tmp, "DD/MM/YYYY");
                    list.get(i).getCampos().put(etiqueta, valor);
                }
                else if ("L".equals(formato)) {
                    Date tmp = (Date) list.get(i).getCampos()
                                    .get(etiqueta);
                    String valor = SysmanFunciones.convertirAFechaCadena(
                                    tmp, "dd 'de' MMMM 'de' YYYY ");
                    list.get(i).getCampos().put(etiqueta, valor);
                }
                else if ("P".equals(formato)) {
                    Integer valor = 100 * Integer.valueOf(list.get(i)
                                    .getCampos().get(etiqueta).toString());
                    list.get(i).getCampos().put(etiqueta, valor + "%");
                }
            }
        }
        return list;
    }

    /**
     * retorna servicio
     * 
     * @return the service
     */
    public FormContinuoService getService() {
        return service;
    }

    /**
     * Trae los datos configurados para la plantilla.
     * 
     * @return registro con los datos almacenados en la tabla
     * MODELO_PLANTILLA
     */
    private Registro getModeloPlantilla() {
        String sql = "SELECT   \n"
            + "     MODELO_PLANTILLA.CODIGO,   \n"
            + "     MODELO_PLANTILLA.NOMBRE,   \n"
            + "     MODELO_PLANTILLA.PLANTILLA,   \n"
            + "     MODELO_PLANTILLA.TIPO, "
            + "    MODELO_TIPO.NOMBRE as  NOMBRETIPO,  \n"
            + "     MODELO_PLANTILLA.VERSION,   \n"
            + "     MODELO_PLANTILLA.FECHA,   \n"
            + "     MODELO_PLANTILLA.CONSULTA,   \n"
            + "     TIPO_VARIABLES_CONSULTA  \n"
            + "  FROM MODELO_PLANTILLA INNER JOIN MODELO_TIPO \n"
            + "    ON MODELO_PLANTILLA.TIPO = MODELO_TIPO.CODIGO \n"
            + "  WHERE MODELO_PLANTILLA.CODIGO = '"
            + codigoPlantilla
            + "' AND MODELO_PLANTILLA.FECHA =  "
            + fechaPlantilla;
        return service.getRegistro(ConectorPool.ESQUEMA_SYSMANK, sql);
    }

    /**
     * trae el listado de variables identificadas en la consulta
     * definida para el documento plantilla
     * 
     * @return listado variables de consulta
     */
    private List<Registro> getVariablesConsulta() {
        String sql = "SELECT ETIQUETA,FORMATO  FROM MODELO_VARIABLES  WHERE PLANTILLA ='"
            + codigoPlantilla
            + "'  AND  FECHA  = " + fechaPlantilla + " AND TIPO = 'C'";
        return service.getListado(ConectorPool.ESQUEMA_SYSMANK, sql);
    }

    /**
     * trae el listado de variables tipo tabla definidas en el
     * documento plantilla
     * 
     * @return listado de variables de tabla
     */
    private List<Registro> getVariablesTabla() {
        String sql = "SELECT T.NOMBRE, T.CONSULTA, T.ENLACE_PRINCIPAL, " +
            " T.ENLACE_SECUNDARIO, T.CONDICION, T.TIPO_VARIABLES_CONSULTA, " +
            " T.MODO_CREACION, M.MANEJA_ESTILOS FROM MODELO_TABLA T " +
            " INNER JOIN MODO_CREACION_TABLA M ON T.MODO_CREACION = M.CODIGO " +
            " WHERE T.PLANTILLA = '" + codigoPlantilla + "' AND  T.FECHA  = "
            + fechaPlantilla;
        return service.getListado(ConectorPool.ESQUEMA_SYSMANK, sql);
    }

    /**
     * trae el archivo que representa la plantilla
     * 
     * @return archivo plantilla
     * @throws FileNotFoundException
     * si no encuentra el documento plantilla
     * @throws SystemException
     * en caso de que no se pueda identificar la ruta de los archivos.
     */
    public File getArchivoPlantilla()
                    throws FileNotFoundException, SystemException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getRutaArchivos());
        stringBuilder.append(UtilitarioPlantillas.DIRECTORIO_PLANTILLAS);
        stringBuilder.append(File.separator);
        stringBuilder.append(modeloPlantilla
                        .get(PlantillasEnum.NOMBRETIPO.getValue()));
        stringBuilder.append(File.separator);
        stringBuilder.append(modeloPlantilla
                        .get(PlantillasEnum.PLANTILLA.getValue()));
        File file = new File(stringBuilder.toString());
        if (!file.isFile()) {
            String msg = idioma.getString("TB_TB4113");
            msg = msg.replace("s$ruta$s", stringBuilder.toString());
            throw new FileNotFoundException(msg);
        }
        return file;
    }

    /**
     * Obtiene el directorio en donde se almacenan los reportes y
     * plantillas en general.
     * 
     * @return ruta de los archivos asociados a la aplicaci&oacute;n
     * General.
     * @throws SystemException
     */
    private String getRutaArchivos() throws SystemException {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlantillasUrlEnum.DSS_58002.getValue());
        RequestManager requestManager = new RequestManager();
        Parameter parameter = requestManager.get(urlBean.getUrl(), null);
        Map<String, Object> campos = parameter.getFields();
        return SysmanFunciones.toString(
                        campos.get(PlantillasEnum.RUTA_ARCHIVOS.getValue()));

    }

    /**
     * verifica que la consulta configurada para la plantilla tenga
     * datos
     * 
     * @return verdadero si hay datos para generar la plantilla
     */
    protected boolean datosValidos() {
        return !datos.isEmpty();
    }

}

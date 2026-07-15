/*-
 * FrmnovedadesplanoControlador.java
 *
 * 1.0
 *
 * 07/04/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.FrmnovedadesplanoControladorEnum;
import com.sysman.serviciospublicos.enums.FrmnovedadesplanoControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Clase del formulario frmnovedadesplano, El cual permite subir un
 * archivo de excel que se registrara en la tabla SP_FACTURADO_EXTERNO
 *
 * @version 1.0, 07/04/2017
 * @author mzanguna
 * 
 * @version 2.0, 12/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.<br>
 * Se aplicaron las recomendaciones de SonarLint.<br>
 */
@ManagedBean
@ViewScoped
public class FrmnovedadesplanoControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Variables para el ciclo periodo yño del ciclo actual.
     */
    private String ciclo;
    private String periodoCiclo;
    private String anioCiclo;
    private String rutaArchivo;
    private String mensajeError;
    private boolean validacionExcel;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Atributo permite acceder a la informacion del archivo que ha
     * sido cargado
     */
    private Workbook workbook;
    private String extension;

    /**
     * Lista de ciclos registrados.
     */
    private RegistroDataModelImpl listaCiclo;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmnovedadesplanoControlador
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    private StreamedContent archivoDescarga;

    public FrmnovedadesplanoControlador() {
        super();

        compania = SessionUtil.getCompania();

        try {
            // 1396
            numFormulario = GeneralCodigoFormaEnum.FRMNOVEDADESPLANO_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCiclo();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        cargarListaCiclo();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaCiclo
     *
     * Listado de ciclos en el cual se subira el plano de novedades
     */
    public void cargarListaCiclo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmnovedadesplanoControladorUrlEnum.URL4421
                                                        .getValue());

        listaCiclo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, "NUM");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>

    /**
     *
     * Metodo ejecutado al oprimir el boton CmdAceptar en la vista
     * Boton que se encarga de subir el plano de novedades.
     *
     */
    public void oprimirCmdAceptar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        if (extension == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1901"));
            return;

        }
        // Validación existencia de datos.
        Map<String, Object> parametros = new HashMap<>();
        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametros.put(FrmnovedadesplanoControladorEnum.CIC.getValue(), ciclo);
        parametros.put(FrmnovedadesplanoControladorEnum.ANIO.getValue(),
                        anioCiclo);
        parametros.put(FrmnovedadesplanoControladorEnum.PER.getValue(),
                        periodoCiclo);

        HashMap<String, Object> rs = new HashMap<>();
        try {
            UrlBean urlReg = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmnovedadesplanoControladorUrlEnum.URL12251
                                                            .getValue());
            rs = (HashMap<String, Object>) requestManager
                            .get(urlReg.getUrl(), parametros).getFields();

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if ((rs.get("RECAUDADO") != null) && ((int) rs.get("RECAUDADO") > 0)) {
            JsfUtil.agregarMensajeAlertaVentana(idioma.getString("TB_TB3077"));
            return;
        }

        // Eliminación del registro.
        UrlBean urlReg = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmnovedadesplanoControladorUrlEnum.URL12216
                                                        .getValue());
        try {
            requestManager.delete(urlReg.getUrl(), parametros);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        int numeroHoja = 0;
        importarDatos(workbook, numeroHoja);

        if (validacionExcel) {
            try {
                workbook.setForceFormulaRecalculation(true);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                workbook.write(out);
                out.close();
                archivoDescarga = JsfUtil.getArchivoDescarga(
                                new ByteArrayInputStream(out.toByteArray()),
                                "Informe Validación. -.xlsx");
            }
            catch (IOException | JRException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        else {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3076"));
        }
    }

    // </CODIGO_DESARROLLADO>

    /**
     * Metodo ejecutado al oprimir el boton CmdCancelar en la vista
     * Boton para cerrar el formulario.
     */
    public void oprimirCmdCancelar() {
        // <CODIGO_DESARROLLADO>
        String[] campos = {};
        Object[] valores = {};

        SessionUtil.redireccionar(
                        "/serviciosPublicos/calculofacturacion.sysman", campos,
                        valores);

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cargar un archivo desde el control LecExcel
     * Boton para cargar el archivo en excel de novedades.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     *
     */
    public void cargarArchivoLecExcel(FileUploadEvent event) {
        // <CODIGO_DESARROLLADO>
        workbook = null;

        try {
            InputStream is = event.getFile().getInputstream();
            if (is == null) {
                return;
            }
            rutaArchivo = event.getFile().getFileName();
            extension = rutaArchivo.substring(
                            rutaArchivo.indexOf('.'), rutaArchivo.length())
                            .substring(1, rutaArchivo.substring(
                                            rutaArchivo.indexOf('.'),
                                            rutaArchivo.length()).length());

            // Inicializa el workbook de acuerdo a la extension del
            // archivo (xls o xlsx)
            if ("xls".equals(extension)) {
                workbook = new HSSFWorkbook(is);
            }
            else {
                workbook = new XSSFWorkbook(is);
            }
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Recorre la hoja en la que se encuentran los datos que se van a
     * importar y realiza el proceso de insercion de los mismos
     *
     * @param workbook
     * Contenido del archivo seleccionado
     * @param numeroHoja
     * Numero de hoja con la informacion a importar
     */
    private void importarDatos(Workbook workbook, int numeroHoja) {
        String valor;
        String errorExcel;
        HashMap<String, Object> campos = new HashMap<>();
        HashMap<String, Object> nombreCampos = new HashMap<>();
        int fila = 1;
        Sheet sheet = workbook.getSheetAt(numeroHoja);
        // Almacena el nombre de las columans que se usaran en el
        // insert.
        if (Math.max(sheet.getRow(fila).getLastCellNum(), 0) != 3) {
            validacionExcel = false;
            return;

        }
        else {
            validacionExcel = true;

        }

        nombreCampos.put("0",
                        FrmnovedadesplanoControladorEnum.CODEXTER.getValue());
        nombreCampos.put("1",
                        FrmnovedadesplanoControladorEnum.CONCEP.getValue());
        nombreCampos.put("2", "VALOR_FACTURADO");

        // Almacena los valores que se usaran en el insert.
        // Filas
        for (int rowNum = fila; rowNum <= sheet.getLastRowNum(); rowNum++) {
            errorExcel = "";
            for (int column = 0; column < Math.max(sheet.getRow(fila - 1)
                            .getLastCellNum(), 0); column++) {
                if (validarFilaCeldaVacia(sheet, rowNum, column)) {

                    Row r = sheet.getRow(rowNum);
                    Cell cell = r.getCell(column);

                    cell.setCellType(Cell.CELL_TYPE_STRING);

                    valor = !SysmanFunciones.validarVariableVacio(
                                    cell.getStringCellValue())
                                        ? cell.getStringCellValue()
                                        : null;

                    campos.put(nombreCampos.get(String.valueOf(column))
                                    .toString(), valor);
                }
                else {
                    errorExcel = idioma.getString("TB_TB3145");
                }
            }
            if (errorExcel.isEmpty()) {
                insertarDato(campos);
                if (!mensajeError.isEmpty()) {
                    sheet.createRow(rowNum).createCell(0)
                                    .setCellValue(mensajeError);

                }

            }
            else {
                sheet.createRow(rowNum).createCell(3).setCellValue(errorExcel);
            }

            campos = new HashMap<>();
        }
        JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2903"));
    }

    private boolean validarFilaCeldaVacia(Sheet sheet, int rowNum, int column) {
        boolean respuesta = false;
        Row rAux = sheet.getRow(rowNum);
        if (rAux != null) {
            Cell cellAux = rAux.getCell(column, Row.RETURN_BLANK_AS_NULL);
            if (cellAux != null) {
                cellAux.setCellType(Cell.CELL_TYPE_STRING);
                if (!SysmanFunciones.validarVariableVacio(
                                cellAux.getStringCellValue())) {
                    respuesta = true;
                }
            }
        }
        return respuesta;
    }

    private void insertarDato(HashMap<String, Object> camposInsertar) {
        mensajeError = "";

        camposInsertar.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        Map<String, Object> parametros = new HashMap<>();
        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametros.put(FrmnovedadesplanoControladorEnum.CODEXTERNO.getValue(),
                        camposInsertar.get(
                                        FrmnovedadesplanoControladorEnum.CODEXTER
                                                        .getValue())
                                        .toString());

        Registro rs = null;
        try {
            UrlBean urlReg;
            if (!"T".equals(ciclo)) {
                // Si se filtra por ciclo
                parametros.put(FrmnovedadesplanoControladorEnum.CIC.getValue(),
                                ciclo);
                urlReg = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FrmnovedadesplanoControladorUrlEnum.URL12366
                                                                .getValue());
            }
            else {
                urlReg = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FrmnovedadesplanoControladorUrlEnum.URL12365
                                                                .getValue());
            }
            rs = RegistroConverter.toRegistro(
                            requestManager.get(urlReg.getUrl(), parametros));

            if (rs != null) {
                if ("R".equals(rs.getCampos()
                                .get(GeneralParameterEnum.ESTADO.getName())
                                .toString())) {
                    mensajeError = parametros.get("CODEXTERNO") + ": "
                        + idioma.getString("TB_TB3148");
                }
                else {
                    camposInsertar.put(
                                    FrmnovedadesplanoControladorEnum.CIC
                                                    .getValue(),
                                    rs.getCampos().get(
                                                    FrmnovedadesplanoControladorEnum.CIC
                                                                    .getValue())
                                                    .toString());

                    camposInsertar.put(FrmnovedadesplanoControladorEnum.CODRUTA
                                    .getValue(),
                                    rs.getCampos().get(
                                                    FrmnovedadesplanoControladorEnum.CODRUTA
                                                                    .getValue())
                                                    .toString());
                    camposInsertar.put(GeneralParameterEnum.ANO.getName(),
                                    rs.getCampos().get(GeneralParameterEnum.ANO
                                                    .getName())
                                                    .toString());
                    camposInsertar.put(
                                    FrmnovedadesplanoControladorEnum.PER
                                                    .getValue(),
                                    rs.getCampos().get(
                                                    FrmnovedadesplanoControladorEnum.PER
                                                                    .getValue())
                                                    .toString());

                }
            }
            else {
                mensajeError = parametros
                                .get(FrmnovedadesplanoControladorEnum.CODEXTERNO
                                                .getValue())
                    + ": "
                    + idioma.getString("TB_TB3146");

            }

            parametros.clear();
            parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            parametros.put(FrmnovedadesplanoControladorEnum.CONCEP.getValue(),
                            camposInsertar.get(
                                            FrmnovedadesplanoControladorEnum.CONCEP
                                                            .getValue()));

            urlReg = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FrmnovedadesplanoControladorUrlEnum.URL12419
                                                            .getValue());

            Registro rs2 = RegistroConverter.toRegistro(
                            requestManager.get(urlReg.getUrl(), parametros));

            if ((int) rs2.getCampos().get("CUENTA") <= 0) {
                mensajeError = "Usuario: " + parametros.get("CODEXTERNO")
                    + " Concepto: "
                    + parametros.get(FrmnovedadesplanoControladorEnum.CONCEP
                                    .getValue())
                    + ": "
                    + idioma.getString("TB_TB3147");
            }
            if (mensajeError.isEmpty()) {

                String condicion = "COMPANIA = ''" + compania + "'' " +
                    " AND CODIGORUTA = ''"
                    + camposInsertar.get(
                                    FrmnovedadesplanoControladorEnum.CODRUTA
                                                    .getValue())
                                    .toString()
                    + "'' " +
                    " AND ANO = "
                    + camposInsertar.get(GeneralParameterEnum.ANO.getName())
                                    .toString()
                    + " " +
                    " AND PERIODO = ''"
                    + camposInsertar.get(GeneralParameterEnum.PERIODO.getName())
                                    .toString()
                    + "'' " +
                    " and CONCEPTO = "
                    + camposInsertar.get(FrmnovedadesplanoControladorEnum.CONCEP
                                    .getValue()).toString()
                    + "  ";

                String consecutivo = Long.toString(
                                ejbSysmanUtil.generarSiguienteConsecutivo(
                                                "SP_FACTURADO_EXTERNO",
                                                condicion,
                                                GeneralParameterEnum.CONSECUTIVO
                                                                .getName()));

                camposInsertar.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                                consecutivo);
                camposInsertar.remove(FrmnovedadesplanoControladorEnum.CODEXTER
                                .getValue());

                camposInsertar.put(GeneralParameterEnum.DATE_CREATED.getName(),
                                new Date());
                camposInsertar.put(GeneralParameterEnum.CREATED_BY.getName(),
                                SessionUtil.getUser().getCodigo());

                Map<String, Object> fields = new TreeMap<>();
                fields.putAll(camposInsertar);
                Parameter parameter = new Parameter();
                parameter.setFields(fields);

                UrlBean urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FrmnovedadesplanoControladorUrlEnum.URL12505
                                                                .getValue());
                requestManager.save(urlCreate.getUrl(),
                                urlCreate.getMetodo(),
                                parameter);
                JsfUtil.agregarMensajeInformativo(idioma
                                .getString("MSM_REGISTRO_INGRESADO"));

            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCiclo
     * Selección del ciclo actual del cual se obtiene su ano y periodo
     * actual.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCiclo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        ciclo = SysmanFunciones.nvl(registroAux.getCampos().get("NUM"), "")
                        .toString();
        anioCiclo = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.ANO.getName()), "")
                        .toString();
        periodoCiclo = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(FrmnovedadesplanoControladorEnum.PER.getValue()),
                        "")
                        .toString();
        if (anioCiclo.length() > 4) {
            anioCiclo = anioCiclo.substring(0, 4);
        }
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ciclo
     *
     * @return ciclo
     */
    public String getCiclo() {
        return ciclo;
    }

    /**
     * Asigna la variable ciclo
     *
     * @param ciclo
     * Variable a asignar en ciclo
     */
    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCiclo
     *
     * @return listaCiclo
     */
    public RegistroDataModelImpl getListaCiclo() {
        return listaCiclo;
    }

    /**
     * Asigna la lista listaCiclo
     *
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(RegistroDataModelImpl listaCiclo) {
        this.listaCiclo = listaCiclo;
    }

    public String getPeriodoCiclo() {
        return periodoCiclo;
    }

    public void setPeriodoCiclo(String periodoCiclo) {
        this.periodoCiclo = periodoCiclo;
    }

    public String getAnioCiclo() {
        return anioCiclo;
    }

    public void setAnioCiclo(String anioCiclo) {
        this.anioCiclo = anioCiclo;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}

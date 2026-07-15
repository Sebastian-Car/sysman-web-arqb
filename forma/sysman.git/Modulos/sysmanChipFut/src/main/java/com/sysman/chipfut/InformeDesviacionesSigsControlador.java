/*-
 * InformeDesviacionesSigsControlador.java
 *
 * 1.0
 * 
 * 15/12/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.chipfut;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.chipfut.enums.InformeDesviacionesSigControladorEnum;
import com.sysman.chipfut.enums.InformeDesviacionesSigControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Clase migrada para la generacion del informe desviaciones
 * significativas de cuentas contables
 *
 * @version 1.0, 15/12/2018
 * @author ybecerra
 */
@ManagedBean
@ViewScoped
public class InformeDesviacionesSigsControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Atributo que almacena el codigo del modulo por el cual se
     * ingresa en la aplicacion
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el codigo seleccionado en el combo codigo
     * inicial
     */
    private String codigoInicial;
    /**
     * Atributo que almacena el numero del registro seleccionado en el
     * combo de ano
     */
    private String anoAnterior;
    /**
     * Atributo que almacena el numero del registro seleccionado en el
     * combo de mes
     */
    private String mesAnterior;
    /**
     * Atributo que almacena el numero del ano del registro
     * selecciondo en el combo del ano
     */
    private String anoComparar;
    /**
     * Atributo que almacena el numero del mes del registro
     * seleccionado en el combo de mes
     */
    private String mesComparar;
    /**
     * Atributo que valida el valor del filtro del mes en el combo
     * mesComparar
     */
    private String mesCompararAux;
    /**
     * Atributo que almacena el codigo seleccionado en el combo de
     * codigo final
     */
    private String codigoFinal;
    /**
     * Atributo que almacena el numero digitado en el campo Digitos
     */
    private String digitos;
    /**
     * Atributo que almacena el valor del nombre del codigo inicial
     * seleccionado
     */
    private String nombreInicial;
    /**
     * Atributo que almacena el valor del nombre del codigo final
     * seleccionado
     */
    private String nombreFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Atributo que almacena el valor del numero digitado en el campo
     * de limite superior
     */
    private String limiteSuperior;
    /**
     * Atributo que almacena el valor del numero digitado en el campo
     * de limite inferior
     */
    private String limiteInferior;
    /**
     * Atributo que almacena el valor digitado en el campo de "Codigo
     * Entidad"
     */
    private String codigoEntidad;

    /**
     * Atributo permite acceder a la informacion del archivo que ha
     * sido cargado
     */
    private Workbook workbook;
    /**
     * Atributo que permite la lectura de los datos contenidos en el
     * archivo que se carga
     */
    private String extension;

    /**
     * Atributi que almacena el contenido del excel leido
     */
    private String valoresImportar = "";

    /**
     * Atributo que almacena la fila en la que se encuentra el
     * encabezado en el archivo a importar
     */
    private String fila;

    /**
     * Almacena el valor de la fila
     */
    private String valorFila = "";
    /**
     * Atributo que valida si un registro se imprime en el archivo
     * plano o no
     */
    private boolean imprime = true;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de registros de la tabla ano
     */
    private List<Registro> listaAnoAnterior;
    /**
     * Lista de registros de la tabla mes
     */
    private List<Registro> listaMesAnterior;
    /**
     * Lista de registros de la tabla ano
     */
    private List<Registro> listaAnoComparar;
    /**
     * Lista de registros de la tabla mes
     */
    private List<Registro> listaMesComparar;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de registros de la tabla plan contable
     */
    private RegistroDataModelImpl listaCodigoInicial;
    /**
     * Lista de registros de la tabla plan contable
     */
    private RegistroDataModelImpl listaCodigoFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de InformeDesviacionesSigsControlador
     */
    public InformeDesviacionesSigsControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        codigoEntidad = SessionUtil.getCompaniaIngreso().getCodigoContaduria();
        try {
            // 2010
            numFormulario = GeneralCodigoFormaEnum.INFORME_DESVIACIONES_SIG_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            fila = "1";
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
        abrirFormulario();
        cargarListaAnoAnterior();
        cargarListaMesAnterior();
        cargarListaAnoComparar();
        cargarListaMesComparar();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>

    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        anoAnterior = String.valueOf(SysmanFunciones.ano(new Date()));
        anoComparar = String.valueOf(SysmanFunciones.ano(new Date()));

        mesAnterior = String.valueOf(SysmanFunciones.mes(new Date()));
        mesComparar = String
                        .valueOf(SysmanFunciones.mes(new Date()));
        mesCompararAux = mesComparar;
        digitos = "6";
        limiteInferior = "0";
        limiteSuperior = "0";
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAnoAnterior
     *
     */
    public void cargarListaAnoAnterior() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnoAnterior = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformeDesviacionesSigControladorUrlEnum.URL204
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * 
     * Carga la lista listaMesAnterior
     *
     */
    public void cargarListaMesAnterior() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano(new Date()));

        try {
            listaMesAnterior = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformeDesviacionesSigControladorUrlEnum.URL232
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Carga la lista listaAnoComparar
     *
     */
    public void cargarListaAnoComparar() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoAnterior);

        try {
            listaAnoComparar = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformeDesviacionesSigControladorUrlEnum.URL263
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * 
     * Carga la lista listaMesComparar
     *
     */
    public void cargarListaMesComparar() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano(new Date()));
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        mesCompararAux);

        try {
            listaMesComparar = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InformeDesviacionesSigControladorUrlEnum.URL289
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * 
     * Carga la lista listaCodigoInicial
     *
     */
    public void cargarListaCodigoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformeDesviacionesSigControladorUrlEnum.URL306
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(InformeDesviacionesSigControladorEnum.ANIOINICIAL.getValue(),
                        anoAnterior);
        param.put(InformeDesviacionesSigControladorEnum.ANIOFINAL.getValue(),
                        anoComparar);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaCodigoFinal
     *
     */
    public void cargarListaCodigoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InformeDesviacionesSigControladorUrlEnum.URL311
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(InformeDesviacionesSigControladorEnum.ANIOINICIAL.getValue(),
                        anoAnterior);
        param.put(InformeDesviacionesSigControladorEnum.ANIOFINAL.getValue(),
                        anoComparar);
        param.put(InformeDesviacionesSigControladorEnum.CODIGOINICIAL
                        .getValue(),
                        codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Generar en la vista
     *
     *
     */
    public void oprimirGenerar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        exportarDatos(workbook);
        try {
            archivoDescarga = JsfUtil
                            .getArchivoDescarga(JsfUtil.serializarPlano(
                                            valoresImportar),
                                            "VARIACIONES SIGNIFICATIVAS.txt");
        }
        catch (JRException | IOException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }

        valoresImportar = "";
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo invocado al ejecutar el comando remoto Alerta en la
     * vista
     *
     *
     */
    public void ejecutarAlerta() {
        // <CODIGO_DESARROLLADO>
        JsfUtil.agregarMensajeAlerta(idioma.getString(
                        "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * 
     * Metodo ejecutado al cargar un archivo desde el control
     * ExcelPlano
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     *
     */
    public void cargarArchivoExcelPlano(FileUploadEvent event) {
        // <CODIGO_DESARROLLADO>
        workbook = null;
        try {
            InputStream is = event.getFile().getInputstream();
            if (is == null) {
                return;
            }
            String rutaArchivo = event.getFile().getFileName();
            extension = FilenameUtils.getExtension(rutaArchivo);
            // Inicializa el workbook de acuerdo a la extension del
            // archivo (xls o xlsx)
            if (workbook == null) {
                if ("xls".equals(extension)) {
                    workbook = new HSSFWorkbook(is);
                }
                else {
                    workbook = new XSSFWorkbook(is);
                }
            }
        }

        catch (IOException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control AnoAnterior
     * 
     * 
     */
    public void cambiarAnoAnterior() {
        // <CODIGO_DESARROLLADO>
        anoComparar = null;
        mesComparar = null;
        codigoInicial = null;
        codigoFinal = null;
        nombreInicial = null;
        nombreFinal = null;
        cargarListaAnoComparar();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control MesAnterior
     * 
     * 
     */
    public void cambiarMesAnterior() {
        // <CODIGO_DESARROLLADO>
        if (anoAnterior != null
            && anoComparar != null) {
            if (Integer.parseInt(anoAnterior) < Integer.parseInt(anoComparar)) {
                mesComparar = null;
                cargarListaMesComparar();
                mesCompararAux = "1";
            }
            else {
                mesCompararAux = mesAnterior;
                cargarListaMesComparar();
            }
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control AnoComparar
     * 
     */
    public void cambiarAnoComparar() {
        // <CODIGO_DESARROLLADO>
        if (Integer.parseInt(anoAnterior) < Integer.parseInt(anoComparar)) {
            mesCompararAux = "1";
            cargarListaMesComparar();
        }
        else {
            mesCompararAux = mesAnterior;
            cargarListaMesComparar();
        }

        codigoInicial = null;
        codigoFinal = null;
        nombreInicial = null;
        nombreFinal = null;
        cargarListaCodigoInicial();

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    /**
     * Metodo que se ejecuta en la invocacion del evento de
     * oprimirExcel
     */
    public void generarInforme() {
        try {
            String strSql = "";
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("codigoInicial", "'" + codigoInicial + "'");
            reemplazar.put("codigoFinal", "'" + codigoFinal + "'");
            reemplazar.put("mesAnterior", mesAnterior);
            reemplazar.put("mesComparar", mesComparar);
            reemplazar.put("manTer", "0");
            reemplazar.put("manAux", "0");
            reemplazar.put("manCen", "0");
            reemplazar.put("manRef", "0");
            reemplazar.put("manFue", "0");
            reemplazar.put("digitos", digitos);
            reemplazar.put("anio", anoAnterior);
            reemplazar.put("limiteInferior", limiteInferior);
            reemplazar.put("limiteSuperior", limiteSuperior);
            reemplazar.put("baseBalance", Reporteador.resuelveConsulta(
                            "800046BaseBalances",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar));
            reemplazar.put("anio", anoComparar);
            reemplazar.put("baseBalanceUnion", Reporteador.resuelveConsulta(
                            "800046BaseBalances",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar));
            reemplazar.put("condicionCC", "");

            strSql = Reporteador.resuelveConsulta(
                            "800284DesviacionesSignificativas",
                            Integer.parseInt(modulo), reemplazar);
            List<Registro> rs = service.getListado(
                            ConectorPool.ESQUEMA_SYSMAN,
                            strSql);
            if (!rs.isEmpty()) {

                archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                                ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL,
                                "VariacionesSignificativas");

            }
            else {
                ejecutarAlerta();
            }
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }

    }

    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
        nombreInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                        "").toString();
        nombreFinal = null;
        codigoFinal = null;
        cargarListaCodigoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
        nombreFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                        "").toString();
    }

    // </METODOS_COMBOS_GRANDES>
    /**
     * Recorre la hoja en la que se encuentran los datos que se van a
     * importar y realiza el proceso de insercion de los mismos
     * 
     * @param workbook
     * Contenido del archivo seleccionado
     * @param numeroHoja
     * Numero de hoja con la informacion a importar
     * 
     */
    private void exportarDatos(Workbook workbook) {

        valoresImportar = SysmanFunciones.concatenar("S", "\t",
                        codigoEntidad, "\t", "1", nombreTrimestre(), "\t",
                        anoComparar,
                        "\t",
                        "CGN2016C01_VARIACIONES_TRIMESTRALES_SIGNIFICATIVAS",
                        "\r\n");
        Sheet sheet = workbook.getSheetAt(0);
        // Filas
        for (int numFila = Integer.parseInt(fila); numFila <= sheet
                        .getLastRowNum(); numFila++) {
            // Columnas
            for (int numColumna = 0; numColumna < Math
                            .max(sheet.getRow(Integer.parseInt(fila))
                                            .getLastCellNum(),
                                            0); numColumna++) {

                if (validarFilaCeldaVacia(sheet, numFila, numColumna)) {
                    Row r = sheet.getRow(numFila);
                    Cell cell = r.getCell(numColumna,
                                    Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    // Almacena temporalmente el valor de
                    // la
                    // celda
                    String valorColumna = !SysmanFunciones
                                    .validarVariableVacio(
                                                    cell.getStringCellValue())
                                                        ? cell.getStringCellValue()
                                                        : null;
                    validarDigitosCuenta(numColumna, valorColumna);
                }
                else {
                    valorFila = "";
                    imprime = false;
                    break;
                }
            }
            if (imprime) {
                if (!valorFila.isEmpty()) {
                    valoresImportar = SysmanFunciones.concatenar(
                                    valoresImportar, "D", "\t", valorFila,
                                    "\r\n");
                    valorFila = "";
                }
            }
            imprime = true;
        }

    }

    /**
     * Metodo que retorna el trimestre seleccionado en numero, para
     * encabezado del archivo plano
     * 
     * @return
     */
    public String nombreTrimestre() {

        String valorTrimestre;
        switch (mesComparar) {
        case "3":
            valorTrimestre = "0103";
            break;
        case "6":
            valorTrimestre = "0406";
            break;
        case "9":
            valorTrimestre = "0709";
            break;
        case "12":
            valorTrimestre = "1012";
            break;
        default:
            valorTrimestre = "";
            break;
        }
        return valorTrimestre;
    }

    /**
     * Valida si una celda especifica dentro de una hoja de datos
     * viene nula o esta en blanco
     * 
     * @param sheet
     * Hoja de datos que se va a analizar
     * @param rowNum
     * Numero de fila que se evaluara
     * @param column
     * Numero de la columna dentro de la fila que se evaluara
     * @return Verdadero si la celda posee valor
     */
    private boolean validarFilaCeldaVacia(Sheet sheet, int rowNum, int column) {
        boolean respuesta = false;

        Row rAux = sheet.getRow(rowNum);
        if (rAux != null) {
            Cell cellAux = rAux.getCell(column,
                            Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

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

    /**
     * Metodo que permite identificar si el archivo descarga con todas
     * las cuentas o las cuentas con digitos de 6
     * 
     * @param numColumna
     * @param valorColumna
     */
    public void validarDigitosCuenta(int numColumna, String valorColumna) {

        if (numColumna == 0) {
            valorColumna = traerCuenta(valorColumna);
        }

        valorFila = SysmanFunciones.concatenar(valorFila, valorColumna,
                        " \t");
    }

    /**
     * Metodo que recibe codigo de cuenta y dependiendo la longitud de
     * esta se separa con un punto(.)
     * 
     * @param cuenta
     * @return
     */
    public String traerCuenta(String cuenta) {
        String codigoCuenta = "";
        if (cuenta.length() > 0 && cuenta.length() < 2) {
            codigoCuenta = cuenta;
        }
        else if (cuenta.length() > 0 && cuenta.length() < 3) {
            codigoCuenta = SysmanFunciones.concatenar(cuenta.substring(0, 1),
                            ".", cuenta.substring(1, 2));
        }
        else if (cuenta.length() > 0 && cuenta.length() < 5) {
            codigoCuenta = SysmanFunciones.concatenar(cuenta.substring(0, 1),
                            ".", cuenta.substring(1, 2), ".",
                            cuenta.substring(2, 4));
        }
        else if (cuenta.length() > 0 && cuenta.length() < 7) {
            codigoCuenta = SysmanFunciones.concatenar(cuenta.substring(0, 1),
                            ".", cuenta.substring(1, 2), ".",
                            cuenta.substring(2, 4), ".",
                            cuenta.substring(4, 6));
        }
        return codigoCuenta;
    }

    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable codigoInicial
     * 
     * @return codigoInicial
     */
    public String getCodigoInicial() {
        return codigoInicial;
    }

    /**
     * Asigna la variable codigoInicial
     * 
     * @param codigoInicial
     * Variable a asignar en codigoInicial
     */
    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    /**
     * Retorna la variable anoAnterior
     * 
     * @return anoAnterior
     */
    public String getAnoAnterior() {
        return anoAnterior;
    }

    /**
     * Asigna la variable anoAnterior
     * 
     * @param anoAnterior
     * Variable a asignar en anoAnterior
     */
    public void setAnoAnterior(String anoAnterior) {
        this.anoAnterior = anoAnterior;
    }

    /**
     * Retorna la variable mesAnterior
     * 
     * @return mesAnterior
     */
    public String getMesAnterior() {
        return mesAnterior;
    }

    /**
     * Asigna la variable mesAnterior
     * 
     * @param mesAnterior
     * Variable a asignar en mesAnterior
     */
    public void setMesAnterior(String mesAnterior) {
        this.mesAnterior = mesAnterior;
    }

    /**
     * Retorna la variable anoComparar
     * 
     * @return anoComparar
     */
    public String getAnoComparar() {
        return anoComparar;
    }

    /**
     * Asigna la variable anoComparar
     * 
     * @param anoComparar
     * Variable a asignar en anoComparar
     */
    public void setAnoComparar(String anoComparar) {
        this.anoComparar = anoComparar;
    }

    /**
     * Retorna la variable mesComparar
     * 
     * @return mesComparar
     */
    public String getMesComparar() {
        return mesComparar;
    }

    /**
     * Asigna la variable mesComparar
     * 
     * @param mesComparar
     * Variable a asignar en mesComparar
     */
    public void setMesComparar(String mesComparar) {
        this.mesComparar = mesComparar;
    }

    /**
     * Retorna la variable mesCompararAux
     * 
     * @return mesCompararAux
     */
    public String getMesCompararAux() {
        return mesCompararAux;
    }

    /**
     * Asigna la variable mesCompararAux
     * 
     * @param mesCompararAux
     * Variable a asignar en mesCompararAux
     */
    public void setMesCompararAux(String mesCompararAux) {
        this.mesCompararAux = mesCompararAux;
    }

    /**
     * Retorna la variable codigoFinal
     * 
     * @return codigoFinal
     */
    public String getCodigoFinal() {
        return codigoFinal;
    }

    /**
     * Asigna la variable codigoFinal
     * 
     * @param codigoFinal
     * Variable a asignar en codigoFinal
     */
    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    /**
     * Retorna la variable digitos
     * 
     * @return digitos
     */
    public String getDigitos() {
        return digitos;
    }

    /**
     * Asigna la variable digitos
     * 
     * @param digitos
     * Variable a asignar en digitos
     */
    public void setDigitos(String digitos) {
        this.digitos = digitos;
    }

    /**
     * Retorna la variable nombreInicial
     * 
     * @return nombreInicial
     */
    public String getNombreInicial() {
        return nombreInicial;
    }

    /**
     * Asigna la variable nombreInicial
     * 
     * @param nombreInicial
     * Variable a asignar en nombreInicial
     */
    public void setNombreInicial(String nombreInicial) {
        this.nombreInicial = nombreInicial;
    }

    /**
     * Retorna la variable nombreFinal
     * 
     * @return nombreFinal
     */
    public String getNombreFinal() {
        return nombreFinal;
    }

    /**
     * Asigna la variable nombreFinal
     * 
     * @param nombreFinal
     * Variable a asignar en nombreFinal
     */
    public void setNombreFinal(String nombreFinal) {
        this.nombreFinal = nombreFinal;
    }

    /**
     * Retorna la variable codigoEntidad
     * 
     * @return codigoEntidad
     */
    public String getCodigoEntidad() {
        return codigoEntidad;
    }

    /**
     * Asigna la variable codigoEntidad
     * 
     * @param codigoEntidad
     * Variable a asignar en codigoEntidad
     */
    public void setCodigoEntidad(String codigoEntidad) {
        this.codigoEntidad = codigoEntidad;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAnoAnterior
     * 
     * @return listaAnoAnterior
     */
    public List<Registro> getListaAnoAnterior() {
        return listaAnoAnterior;
    }

    /**
     * Asigna la lista listaAnoAnterior
     * 
     * @param listaAnoAnterior
     * Variable a asignar en listaAnoAnterior
     */
    public void setListaAnoAnterior(List<Registro> listaAnoAnterior) {
        this.listaAnoAnterior = listaAnoAnterior;
    }

    /**
     * Retorna la lista listaMesAnterior
     * 
     * @return listaMesAnterior
     */
    public List<Registro> getListaMesAnterior() {
        return listaMesAnterior;
    }

    /**
     * Asigna la lista listaMesAnterior
     * 
     * @param listaMesAnterior
     * Variable a asignar en listaMesAnterior
     */
    public void setListaMesAnterior(List<Registro> listaMesAnterior) {
        this.listaMesAnterior = listaMesAnterior;
    }

    /**
     * Retorna la lista listaAnoComparar
     * 
     * @return listaAnoComparar
     */
    public List<Registro> getListaAnoComparar() {
        return listaAnoComparar;
    }

    /**
     * Asigna la lista listaAnoComparar
     * 
     * @param listaAnoComparar
     * Variable a asignar en listaAnoComparar
     */
    public void setListaAnoComparar(List<Registro> listaAnoComparar) {
        this.listaAnoComparar = listaAnoComparar;
    }

    /**
     * Retorna la lista listaMesComparar
     * 
     * @return listaMesComparar
     */
    public List<Registro> getListaMesComparar() {
        return listaMesComparar;
    }

    /**
     * Asigna la lista listaMesComparar
     * 
     * @param listaMesComparar
     * Variable a asignar en listaMesComparar
     */
    public void setListaMesComparar(List<Registro> listaMesComparar) {
        this.listaMesComparar = listaMesComparar;
    }

    /**
     * Retorna la lista limiteSuperior
     * 
     * @return limiteSuperior
     */
    public String getLimiteSuperior() {
        return limiteSuperior;
    }

    /**
     * Asigna la lista limiteSuperior
     * 
     * @param limiteSuperior
     * Variable a asignar en limiteSuperior
     */
    public void setLimiteSuperior(String limiteSuperior) {
        this.limiteSuperior = limiteSuperior;
    }

    /**
     * Retorna la lista limiteInferior
     * 
     * @return limiteInferior
     */
    public String getLimiteInferior() {
        return limiteInferior;
    }

    /**
     * Asigna la lista limiteInferior
     * 
     * @param limiteInferior
     * Variable a asignar en limiteInferior
     */
    public void setLimiteInferior(String limiteInferior) {
        this.limiteInferior = limiteInferior;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCodigoInicial
     * 
     * @return listaCodigoInicial
     */
    public RegistroDataModelImpl getListaCodigoInicial() {
        return listaCodigoInicial;
    }

    /**
     * Asigna la lista listaCodigoInicial
     * 
     * @param listaCodigoInicial
     * Variable a asignar en listaCodigoInicial
     */
    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial) {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    /**
     * Retorna la lista listaCodigoFinal
     * 
     * @return listaCodigoFinal
     */
    public RegistroDataModelImpl getListaCodigoFinal() {
        return listaCodigoFinal;
    }

    /**
     * Asigna la lista listaCodigoFinal
     * 
     * @param listaCodigoFinal
     * Variable a asignar en listaCodigoFinal
     */
    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
        this.listaCodigoFinal = listaCodigoFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}

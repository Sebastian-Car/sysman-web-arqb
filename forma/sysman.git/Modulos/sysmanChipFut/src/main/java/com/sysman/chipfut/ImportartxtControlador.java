/*-
 * ImportartxtControlador.java
 *
 * 1.0
 * 
 * 13/03/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.chipfut;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.chipfut.enums.ImportartxtControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que permite importar los archivos de configuraci�n
 * despu�s de haber generado los protocolos de importaci�n CHIP.
 *
 * @version 1.0, 13/03/2017
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class ImportartxtControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante que almacena la cade de caracteres de la columna de
     * compania
     */
    private final String cCompania;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    /**
     * Variable que almacena el valor del par�metro ACTIVAR ELIMINAR
     * CONFIGURACION EN FUT
     */
    private String parEliminarConfiguracionFut;
    /**
     * Atributo que almacena el estado de visibilidad del check de
     * eliminar
     */
    private Boolean visibleEliminar;
    /**
     * Atributo que almacena el valor de selecci�n del check de
     * eliminar
     */
    private boolean eliminando;
    /**
     * Atributo que almacena el estado de visibilidad del label de
     * eliminaci�n a partir de la selecci�n del check
     */
    private Boolean visibleMensaje;

    /**
     * Atributo que almacena el c�digo de detalle
     */
    private String codigoDetalle;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    /**
     * Atributo que permite almacena la extensi�n del archivo que se
     * sube a la vista
     */
    private String extension;
    /**
     * Atributo utilizado para extraeer el nombred e larchivo Excel
     * que se sube a la vista
     */
    private String nombreArchivo;
    /**
     * Atributo permite acceder a la informacion del archivo que ha
     * sido cargado
     */
    private Workbook workbook;
    /**
     * Atributo utilizado para contar el numero de registros
     * ingresados desde la plantilla de Excel a la aplciaci�n
     */
    private int contador;

    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     */
    private List<Registro> listaAnoTrabajo;
    /**
     */
    private List<String> listaEncabezados;
    /**
     * 
     */
    private String labelMensajeEmpresaParametrizada;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ImportartxtControlador
     */
    public ImportartxtControlador() {
        super();
        compania = SessionUtil.getCompania();
        cCompania = "COMPANIA";
        
        labelMensajeEmpresaParametrizada = idioma.getString("TT_LB36049");
        labelMensajeEmpresaParametrizada = labelMensajeEmpresaParametrizada.replace("s$empresaparam$s", JsfUtil.getTituloPaginaEmpresaParametrizada());
        try {
            // 1358
            numFormulario = GeneralCodigoFormaEnum.IMPORTARTXT_CONTROLADOR
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
        cargarListaAnoTrabajo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
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

        try {
            parEliminarConfiguracionFut = ejbSysmanUtil.consultarParametro(
                            compania, "ACTIVAR ELIMINAR CONFIGURACION EN FUT",
                            SessionUtil.getModulo(), new Date(), false);

            if ("SI".equals(parEliminarConfiguracionFut)) {
                visibleEliminar = true;
            }
            else
                visibleEliminar = false;

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAnoTrabajo
     *
     */
    public void cargarListaAnoTrabajo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaAnoTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ImportartxtControladorUrlEnum.URL7781
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Imprimir en la vista
     *
     */
    public void oprimirCargarInformacion() {
        if (!nombreArchivo.equals(codigoDetalle)) {
            JsfUtil.agregarMensajeAlerta(
                            "Por favor revisar que la plantilla coincida con el tipo de archivo a importar");
            return;
        }

        contador = 0;
        if (extension == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1901"));
            return;
        }
        try {
            Sheet sheet = workbook.getSheet("Report");

            if (sheet == null) {
                return;
            }
            if (eliminando) {
                eliminarDatos();

            }

            importarDatos(sheet);
            workbook.close();
            if (contador <= 0) {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString(
                                                Constantes.MSM_TRANS_INTERRUMPIDA));
            }
            else {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_PROCESO_EJECUTADO")
                                    + "Se insertaron " + contador
                                    + " registros");
            }
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public boolean validarCompania() {

        String consultaEncabezados = "SELECT COMPANIA FROM " + codigoDetalle
            + "  WHERE ROWNUM <1";

        listaEncabezados = service.getCamposListado(
                        ConectorPool.ESQUEMA_SYSMAN, consultaEncabezados);

        return listaEncabezados.contains(cCompania);

    }

    public boolean validarCampoAnio() {

        String consultaEncabezadoAnio = "SELECT ANO FROM " + codigoDetalle
            + " WHERE ROWNUM <1";

        listaEncabezados = service.getCamposListado(
                        ConectorPool.ESQUEMA_SYSMAN, consultaEncabezadoAnio);

        return listaEncabezados.contains(GeneralParameterEnum.ANO.getName());

    }

    /**
     * Recorre la hoja en la que se encuentran los datos que se van a
     * importar y realiza el proceso de insercion de los mismos
     * 
     * @param workbook
     * Contenido del archivo seleccionado
     * @param sheet
     * Hoja que contiene los datos
     */
    private void importarDatos(Sheet sheet) {
        // Almacena los campo y valores que se enviaran en la
        // insercion
        HashMap<String, Object> campos = new HashMap<>();
        // Almacena el valor de los encabezados, que para este
        // caso es el nombre de los campos de las tablas en las
        // que se realizara la insercion
        HashMap<String, Object> nombreCampos = new HashMap<>();
        for (int i = 0; i < Math
                        .max(sheet.getRow(0)
                                        .getLastCellNum(), 0); i++) {
            Row r = sheet.getRow(0);
            nombreCampos.put(String.valueOf(i),
                            r.getCell(i, Row.RETURN_BLANK_AS_NULL)
                                            .getStringCellValue()
                                            .toUpperCase().replace("'", ""));
        }

        // Filas
        for (int rowNum = 1; rowNum <= sheet
                        .getLastRowNum(); rowNum++) {
            // Columnas
            for (int column = 0; column < Math
                            .max(sheet.getRow(0)
                                            .getLastCellNum(),
                                            0); column++) {

                // Valida que la fila y la celda especifica que se
                // va a leer no sean nulas
                if (validarFilaCeldaVacia(sheet, rowNum, column)) {

                    Row r = sheet.getRow(rowNum);
                    Cell cell = r.getCell(column, Row.RETURN_BLANK_AS_NULL);
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    String valor;
                    valor = !SysmanFunciones.validarVariableVacio(
                                    cell.getStringCellValue())
                                        ? cell.getStringCellValue()
                                        : null;

                    campos.put(nombreCampos.get(String.valueOf(column))
                                    .toString(), valor);
                }
                else {
                    return;
                }
            }
            if (validarCompania()) {
                campos.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            }

            campos.put("DATE_CREATED", new Date());
            campos.put("CREATED_BY", SessionUtil.getUser().getCodigo());
            try {
                Acciones.insertar(ConectorPool.ESQUEMA_SYSMAN, codigoDetalle,
                                campos);
            }
            catch (IllegalAccessException | InstantiationException
                            | ClassNotFoundException | SQLException
                            | NamingException e) {

                JsfUtil.agregarMensajeError(e.getMessage());
                Logger.getLogger(ImportartxtControlador.class.getName())
                                .log(Level.SEVERE, null, e);
                JsfUtil.agregarMensajeError(
                                "Error en la fila " + rowNum + " del archivo");
                contador = contador - 1;
            }
            campos = new HashMap<>();
            contador = contador + 1;
        }
    }

    /**
     * Procedimiento que elimina los registros de la tabla seg�n el
     * a�o actual y la compa�ia
     */
    private void eliminarDatos() {
        HashMap<String, Object> campos = new HashMap<>();

        if (validarCampoAnio()) {
            campos.put("ANO", SysmanFunciones.ano(
                            Acciones.getSysDate(ConectorPool.ESQUEMA_SYSMAN)));
        }

        campos.put(cCompania, compania);

        try {
            Acciones.eliminar(ConectorPool.ESQUEMA_SYSMAN, codigoDetalle,
                            campos);
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

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

    /**
     * Metodo ejecutado al oprimir el boton generarplantilla en la
     * vista
     */

    public boolean validarCampos(String columna) {
        if (cCompania.equals(columna)
            || "CREATED_BY".equals(columna)
            || "MODIFIED_BY".equals(columna)) {
            return true;
        }
        if ("DATE_MODIFIED".equals(columna)
            || "DATE_CREATED".equals(columna)) {
            return true;

        }
        return false;
    }

    public void oprimirgenerarplantilla() {

        archivoDescarga = null;

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {

            String consultaEncabezados = "SELECT * FROM " + codigoDetalle
                + " WHERE ROWNUM <1";

            listaEncabezados = service.getCamposListado(
                            ConectorPool.ESQUEMA_SYSMAN, consultaEncabezados);

            StringBuilder encabezados = new StringBuilder();
            for (int i = 0; i < listaEncabezados.size(); i++) {
                String columna = listaEncabezados.get(i);
                if (!validarCampos(columna)) {

                    encabezados.append("'" + columna + "',");
                }

            }
            encabezados.deleteCharAt(encabezados.lastIndexOf(","));

            String consulta = "SELECT * FROM(" +
                "     SELECT COLUMN_NAME " +
                "     FROM ALL_TAB_COLUMNS " +
                "     WHERE TABLE_NAME ='" + codigoDetalle + "'" +
                "     AND COLUMN_NAME NOT IN('COMPANIA','CREATED_BY','MODIFIED_BY','DATE_MODIFIED','DATE_CREATED')"
                +
                "     ORDER BY COLUMN_ID " +
                "     )PIVOT ( " +
                "     MAX('') " +
                "     FOR COLUMN_NAME IN(" + encabezados + ")" +
                "     )";

            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(consulta,
                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL97,
                            codigoDetalle);

        }
        catch (SQLException | JRException | IOException
                        | DRException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Metodo ejecutado al cargar un archivo desde el control Excel
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     *
     */
    public void cargarArchivoExcel(FileUploadEvent event) {
        InputStream is;
        try {
            is = event.getFile().getInputstream();
            if (is == null) {
                return;
            }
            String rutaArchivo = event.getFile().getFileName();
            extension = rutaArchivo.substring(
                            rutaArchivo.indexOf('.'), rutaArchivo.length())
                            .substring(1, rutaArchivo.substring(
                                            rutaArchivo.indexOf('.'),
                                            rutaArchivo.length()).length());

            nombreArchivo = rutaArchivo.substring(0, rutaArchivo.indexOf('.'));
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
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cambiareliminando() {
        // <CODIGO_DESARROLLADO>
        if (eliminando) {
            visibleMensaje = true;
        }
        else
            visibleMensaje = false;
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>

    public String getParEliminarConfiguracionFut() {
        return parEliminarConfiguracionFut;
    }

    public void setParEliminarConfiguracionFut(
        String parEliminarConfiguracionFut) {
        this.parEliminarConfiguracionFut = parEliminarConfiguracionFut;
    }

    public Boolean getVisibleEliminar() {
        return visibleEliminar;
    }

    public void setVisibleEliminar(Boolean visibleEliminar) {
        this.visibleEliminar = visibleEliminar;
    }

    public boolean isEliminando() {
        return eliminando;
    }

    public void setEliminando(boolean eliminando) {
        this.eliminando = eliminando;
    }

    /**
     * Retorna la variable codigoDetalle
     * 
     * @return codigoDetalle
     */
    public String getCodigoDetalle() {
        return codigoDetalle;
    }

    /**
     * Asigna la variable codigoDetalle
     * 
     * @param codigoDetalle
     * Variable a asignar en codigoDetalle
     */
    public void setCodigoDetalle(String codigoDetalle) {
        this.codigoDetalle = codigoDetalle;
    }

    public Boolean getVisibleMensaje() {
        return visibleMensaje;
    }

    public void setVisibleMensaje(Boolean visibleMensaje) {
        this.visibleMensaje = visibleMensaje;
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
     * Retorna la lista listaAnoTrabajo
     * 
     * @return listaAnoTrabajo
     */
    public List<Registro> getListaAnoTrabajo() {
        return listaAnoTrabajo;
    }

    /**
     * Asigna la lista listaAnoTrabajo
     * 
     * @param listaAnoTrabajo
     * Variable a asignar en listaAnoTrabajo
     */
    public void setListaAnoTrabajo(List<Registro> listaAnoTrabajo) {
        this.listaAnoTrabajo = listaAnoTrabajo;
    }

    public List<String> getListaEncabezados() {
        return listaEncabezados;
    }

    public void setListaEncabezados(List<String> listaEncabezados) {
        this.listaEncabezados = listaEncabezados;
    }

	public String getLabelMensajeEmpresaParametrizada() {
		return labelMensajeEmpresaParametrizada;
	}

	public void setLabelMensajeEmpresaParametrizada(String labelMensajeEmpresaParametrizada) {
		this.labelMensajeEmpresaParametrizada = labelMensajeEmpresaParametrizada;
	}

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}

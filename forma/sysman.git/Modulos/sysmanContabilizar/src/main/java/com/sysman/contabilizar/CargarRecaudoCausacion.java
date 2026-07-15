/*-
 * CargarRecaudoCausacion.java
 *
 * 1.0
 * 
 * 6/07/2020
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.contabilizar;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilizar.enums.CargarRecaudoCausacionUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralLocal;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
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
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que se encarga de cargar la informacion de casuacion y
 * recaudo
 *
 * @version 1.0, 06/07/2020
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class CargarRecaudoCausacion extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Indicador para cargar recaudo
     */
    private boolean cargarRecaudo;
    /**
     * Indicador para cargar causacion
     */
    private boolean cargarCausacion;
    /**
     * Tipo de cobro seleccionado del combo
     */
    private String tipoCobro;

    /**
     * Anio seleccionado en el combo
     */
    private String anio;

    /**
     * Variable que almacena la fecha insertada en el campo
     */
    private Date fecha;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    /**
     * Titulo que tendra la etiqueta de fecha
     */
    private String tituloFecha;
    /**
     * Este atributo se usa como auxiliar del componente selector de
     * archivos CargarExcel y funciona como contenedor del archivo que
     * se debe guardar
     */
    private ContenedorArchivo contArchivoCargarExcel;
    // </DECLARAR_ATRIBUTOS>
    /**
     * Variable que almacena la informacion del excel
     */
    private StringBuilder cadena;
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que carga los anios
     */
    private List<Registro> listaAnio;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que carga los tipos de cobro
     */
    private RegistroDataModelImpl listaTipoCobro;

    @EJB
    private EjbFacturacionGeneralLocal ejbFacturacionGeneralCero;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de CargarRecaudoCausacion
     */
    public CargarRecaudoCausacion() {
        super();
        compania = SessionUtil.getCompania();
        anio = Integer.toString(SysmanFunciones.ano(new Date()));
        fecha = new Date();
        tituloFecha = idioma.getString("TG_FECHA5");
        contArchivoCargarExcel = new ContenedorArchivo();
        try {
            // 2177
            numFormulario = GeneralCodigoFormaEnum.CARGAR_RECAUDO_CAUSACION
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
        cargarListaAnio();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTipoCobro();
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
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>

    /**
     * 
     * Carga la lista listaAnio
     *
     */
    public void cargarListaAnio() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaAnio = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CargarRecaudoCausacionUrlEnum.URL4564
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaTipoCobro
     *
     */
    public void cargarListaTipoCobro() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CargarRecaudoCausacionUrlEnum.URL4966
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaTipoCobro = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton CargarExcel en la vista
     *
     *
     */
    public void oprimirCargarExcel() {

        FileInputStream file = null;
        cadena = new StringBuilder();
        archivoDescarga = null;
        try {
            if (validarArchivo()) {

                String rutaArchivo = contArchivoCargarExcel.getArchivo()
                                .getPath();

                String extension = rutaArchivo
                                .substring(rutaArchivo.indexOf('.'),
                                                rutaArchivo.length())
                                .substring(1, rutaArchivo.substring(
                                                rutaArchivo.indexOf('.'),
                                                rutaArchivo.length()).length());

                file = new FileInputStream(new File(rutaArchivo));

                Workbook workbook = null;

                if ("xls".equals(extension)) {
                    workbook = new HSSFWorkbook(file);
                }
                else {
                    workbook = new XSSFWorkbook(file);
                }

                leerHoja(workbook, 0, 13, cadena, 6);
                cargarDatos();

                file.close();
                workbook.close();

            }
        }
        catch (IOException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void cargarDatos() {
        String salida = null;

        String parametro = (SysmanFunciones.esBdSqlServer())
            ? cadena.toString().replace("TO_CLOB(", "")
                            .replace(")", "")
            : cadena.toString();
        try {

            salida = ejbFacturacionGeneralCero.cargarRecaudoCausacion(compania,
                            parametro, tipoCobro, Integer.parseInt(anio),
                            SessionUtil.getCompaniaIngreso().getNit(),
                            SessionUtil.getUser().getCodigo(),0);

            archivoDescarga = JsfUtil.getArchivoDescarga(
                            JsfUtil.serializarPlano(salida),
                            "Reporte de Carga.txt");

            JsfUtil.agregarMensajeInformativo(idioma.getString(
                            "MSM_PROCESO_EJECUTADO"));

        }
        catch (IOException | JRException | NumberFormatException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void leerHoja(Workbook workbook, int hoja, int columnas,
        StringBuilder cadena, int filainicial) {
        cadena.append("TO_CLOB('");
        Sheet sheet = workbook.getSheetAt(hoja);
        Row fila;
        Cell celda;
        int num = 0;
        for (int i = filainicial; i < sheet.getLastRowNum() + 1; i++) {
            fila = sheet.getRow(i);

            if (fila.getCell(0) != null) {

                for (int j = 0; j < columnas; j++) {
                    celda = fila.getCell(j);
                    if (celda != null) {
                        num = num
                            + (celda.getCellType() == 1
                                ? celda.getStringCellValue()
                                                .replaceFirst("'", " ").length()
                                : NumberToTextConverter
                                                .toText(celda.getNumericCellValue())
                                                .length());
                        cadena.append(celda.getCellType() == 1
                            ? celda.getStringCellValue().replaceFirst("'", " ")
                            : NumberToTextConverter
                                            .toText(celda.getNumericCellValue()));

                    }
                    else {
                        cadena.append("");
                    }
                    if (num >= 10000) {
                        cadena.append("') || TO_CLOB('");
                        num = 0;
                    }
                    cadena.append(SysmanConstantes.SEPARADOR_COL);
                }
                cadena.append(SysmanConstantes.SEPARADOR_REG);
            }

        }
        cadena.append("')"
            + "");
    }

    public boolean validarArchivo() {

        if (contArchivoCargarExcel.getArchivo() == null) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3002"));
            return false;
        }
        else {
            return true;
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Anio
     * 
     */
    public void cambiarAnio() {
        cargarListaTipoCobro();
    }

    /**
     * Metodo ejecutado al cambiar el control CargarRecaudo
     * 
     */
    public void cambiarCargarRecaudo() {
        cargarCausacion = false;
        tituloFecha = idioma.getString("TT_LB19350");

    }

    /**
     * Metodo ejecutado al cambiar el control CargarCausacion
     * 
     */
    public void cambiarCargarCausacion() {
        cargarRecaudo = false;
        tituloFecha = idioma.getString("TB_TB4354");
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoCobro
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoCobro(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoCobro = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>

    public boolean isCargarRecaudo() {
        return cargarRecaudo;
    }

    public void setCargarRecaudo(boolean cargarRecaudo) {
        this.cargarRecaudo = cargarRecaudo;
    }

    public boolean isCargarCausacion() {
        return cargarCausacion;
    }

    public void setCargarCausacion(boolean cargarCausacion) {
        this.cargarCausacion = cargarCausacion;
    }

    /**
     * Retorna la variable tipoCobro
     * 
     * @return tipoCobro
     */
    public String getTipoCobro() {
        return tipoCobro;
    }

    /**
     * Asigna la variable tipoCobro
     * 
     * @param tipoCobro
     * Variable a asignar en tipoCobro
     */
    public void setTipoCobro(String tipoCobro) {
        this.tipoCobro = tipoCobro;
    }

    /**
     * Retorna la variable anio
     * 
     * @return anio
     */
    public String getAnio() {
        return anio;
    }

    /**
     * Asigna la variable anio
     * 
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(String anio) {
        this.anio = anio;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Retorna el objeto contArchivoCargarExcel
     * 
     * @return contArchivoCargarExcel
     */
    public ContenedorArchivo getContArchivoCargarExcel() {
        return contArchivoCargarExcel;
    }

    /**
     * Asigna el objeto contArchivoCargarExcel
     * 
     * @param contArchivoCargarExcel
     * Variable a asignar en contArchivoCargarExcel
     */
    public void setContArchivoCargarExcel(
        ContenedorArchivo contArchivoCargarExcel) {
        this.contArchivoCargarExcel = contArchivoCargarExcel;
    }

    public String getTituloFecha() {
        return tituloFecha;
    }

    public void setTituloFecha(String tituloFecha) {
        this.tituloFecha = tituloFecha;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAnio
     * 
     * @return listaAnio
     */
    public List<Registro> getListaAnio() {
        return listaAnio;
    }

    /**
     * Asigna la lista listaAnio
     * 
     * @param listaAnio
     * Variable a asignar en listaAnio
     */
    public void setListaAnio(List<Registro> listaAnio) {
        this.listaAnio = listaAnio;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaTipoCobro
     * 
     * @return listaTipoCobro
     */
    public RegistroDataModelImpl getListaTipoCobro() {
        return listaTipoCobro;
    }

    /**
     * Asigna la lista listaTipoCobro
     * 
     * @param listaTipoCobro
     * Variable a asignar en listaTipoCobro
     */
    public void setListaTipoCobro(RegistroDataModelImpl listaTipoCobro) {
        this.listaTipoCobro = listaTipoCobro;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}
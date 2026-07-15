/*-
 * FrmSubirPresupuestoControlador.java
 *
 * 1.0
 * 
 * 27/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.plandesarrollo;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.plandesarrollo.ejb.EjbPlanDesarrolloCeroLocal;
import com.sysman.plandesarrollo.enums.FrmSubirPresupuestoControladorUrlEnum;
import com.sysman.util.ContenedorArchivo;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que permite realizar la actualización financiera a
 * partir de los datos cargados en un archivo Excel
 *
 * @version 1.0, 27/02/2018
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class FrmSubirPresupuestoControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante a nivel de clase que almacena el codigo del usuario
     */
    private final String usuario;
    /**
     * Constante que almacena el codigo de la dependencia asociada al
     * usuario que ingresa a la aplicacion
     */
    private final String dependenciaAsignada;

    /**
     * Constante que almacena el nombre de la dependencia asociada al
     * usuario que ingresa a la aplicacion
     */
    private final String nombreDependenciaAsignada;
    /**
     * Constante que almacena el codigo del responsable asociado a la
     * dependencia asociada al usuario que ingresa a la aplicacion
     */
    private final String responsableAsignado;

    /**
     * Constante a nivel de clase que almacena la sucural del
     * responsable que se encuentra asociado al usuario loggueado
     */
    private final String sucursalResponsable;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que alamcena el nombre de la hoja de excel
     * seleccionada
     */

    private String nombreHoja;

    /**
     * Atributo que almacena la vigencia gubernamental seleccionada en
     * la vista
     */
    private String vigenciaGubernamental;
    /**
     * Atributo que almacena la vigencia presupuestal seleccionada en
     * la vista
     */
    private String vigenciaPresupuestal;

    /**
     * Este atributo se usa como auxiliar del componente selector de
     * archivos SelectorArchivo y funciona como contenedor del archivo
     * que se debe guardar
     */
    private ContenedorArchivo contArchivoSelectorArchivo;

    /**
     * Atributo que almacena los datos de los registros del Excel que
     * se sube a la aplicacion
     */

    private String datosExcel;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que almacena las hojas del archivo excel subido al
     * formulario
     */
    private List<Registro> listacmbHoja;
    /**
     * Lista que carga las vigencias gubernamentales
     */
    private List<Registro> listaVigenciaGubernamental;
    /**
     * Lista que carga las vigencias presupuestales
     */
    private List<Registro> listaVigenciaPresupuestal;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    @EJB
    private EjbPlanDesarrolloCeroLocal ejbPlanDesCero;

    /**
     * Crea una nueva instancia de FrmSubirPresupuestoControlador
     */
    public FrmSubirPresupuestoControlador() {
        super();
        compania = SessionUtil.getCompania();
        usuario = SessionUtil.getUser().getCodigo();
        dependenciaAsignada = SysmanFunciones
                        .nvl(SessionUtil.getUser().getDependencia().getCodigo(),
                                        SysmanConstantes.CONS_DEPENDENCIA)
                        .toString();

        nombreDependenciaAsignada = SysmanFunciones
                        .nvl(SessionUtil.getUser().getDependencia().getNombre(),
                                        "")
                        .toString();

        responsableAsignado = SysmanFunciones
                        .nvl(SessionUtil.getUser().getResponsableAso()
                                        .getResponsable(),
                                        SysmanConstantes.CONS_TERCERO)
                        .toString();

        sucursalResponsable = SessionUtil.getUser().getResponsableAso()
                        .getSucursal();
        contArchivoSelectorArchivo = new ContenedorArchivo();
        try {
            numFormulario = 1726;
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
        cargarListacmbHoja();
        cargarListaVigenciaGubernamental();
        cargarListaVigenciaPresupuestal();
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

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listacmbHoja
     *
     */
    public void cargarListacmbHoja() {
        listacmbHoja = new ArrayList<>();
    }

    /**
     * 
     * Carga la lista listaVigenciaGubernamental
     *
     */
    public void cargarListaVigenciaGubernamental() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaVigenciaGubernamental = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmSubirPresupuestoControladorUrlEnum.URL001
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaVigenciaPresupuestal
     *
     */
    public void cargarListaVigenciaPresupuestal() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaVigenciaPresupuestal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmSubirPresupuestoControladorUrlEnum.URL002
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
     * Metodo ejecutado al oprimir el boton Aceptar en la vista
     *
     *
     */
    public void oprimirAceptar() {
        archivoDescarga = null;
        datosExcel = "";
        try (FileInputStream fileIs = new FileInputStream(
                        contArchivoSelectorArchivo.getArchivo());) {

            long consecutivo = ejbPlanDesCero.generarTransaccion(compania,
                            "FIN",
                            Integer.parseInt(vigenciaGubernamental),
                            dependenciaAsignada, responsableAsignado,
                            sucursalResponsable, usuario);

            Workbook workbook = null;

            String rutaArchivo = contArchivoSelectorArchivo.getArchivo()
                            .getPath();

            String extension = rutaArchivo.substring(
                            rutaArchivo.indexOf('.'), rutaArchivo.length());

            if (".xlsx".equals(extension)) {
                workbook = new XSSFWorkbook(fileIs);
            }
            else {
                workbook = new HSSFWorkbook(fileIs);
            }

            Sheet sheet = workbook.getSheet(nombreHoja);

            for (Row row : sheet) {

                iniciarFuncion(row);

            }

            String inconsistencias = ejbPlanDesCero
                            .cargarInformacionPresupuestal(compania,
                                            BigInteger.valueOf(consecutivo),
                                            Integer.parseInt(
                                                            vigenciaGubernamental),
                                            Integer.parseInt(
                                                            vigenciaPresupuestal),
                                            nombreDependenciaAsignada, usuario,
                                            datosExcel);

            if (!SysmanFunciones.validarVariableVacio(inconsistencias)) {

                ByteArrayInputStream archivo = JsfUtil
                                .serializarPlano(inconsistencias);
                archivoDescarga = JsfUtil.getArchivoDescarga(archivo,
                                "inconsistencias.log");
            }

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));

        }
        catch (NumberFormatException | IOException | SystemException
                        | JRException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    private void iniciarFuncion(Row row) {

        if (row.getRowNum() > 0) {

            for (int i = 0; i < row.getLastCellNum(); i++) {

                datosExcel = datosExcel + row.getCell(i)
                    + SysmanConstantes.SEPARADOR_COL;
            }
            datosExcel = datosExcel.substring(0, datosExcel.length()
                - SysmanConstantes.SEPARADOR_COL.length());

            datosExcel = datosExcel + SysmanConstantes.SEPARADOR_REG;
        }

    }

    /**
     * 
     * Metodo invocado al ejecutar el comando remoto ActualizarHojas
     * en la vista
     *
     */
    public void ejecutarActualizarHojas() {
        Workbook workbook = null;
        nombreHoja = "";

        if (contArchivoSelectorArchivo.getArchivo() != null) {

            String rutaArchivo = contArchivoSelectorArchivo.getArchivo()
                            .getPath();

            String extension = rutaArchivo.substring(
                            rutaArchivo.indexOf('.'), rutaArchivo.length());

            if (extension == null) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2761"));
            }
            try (FileInputStream fileIs = new FileInputStream(
                            contArchivoSelectorArchivo.getArchivo());) {
                if (".xlsx".equals(extension)) {
                    workbook = new XSSFWorkbook(fileIs);
                }
                else {
                    workbook = new HSSFWorkbook(fileIs);
                }
                cargarNombreHojas(workbook);

            }
            catch (IOException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        else

        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1291"));
            return;
        }

    }

    /**
     * Carga la lista de hojas que contien el archivo Excel
     * seleccionado
     *
     * @param workbook
     */
    private void cargarNombreHojas(Workbook workbook) {
        listacmbHoja.clear();

        int hojas = workbook.getNumberOfSheets();

        for (int i = 0; i < hojas; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            String hoja = sheet.getSheetName();
            Registro reg = new Registro();
            reg.getCampos().put("ANO", hoja);
            reg.getCampos();
            listacmbHoja.add(reg);
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable nombreHoja
     * 
     * @return nombreHoja
     */
    public String getNombreHoja() {
        return nombreHoja;
    }

    /**
     * Asigna la variable nombreHoja
     * 
     * @param nombreHoja
     * Variable a asignar en nombreHoja
     */
    public void setNombreHoja(String nombreHoja) {
        this.nombreHoja = nombreHoja;
    }

    /**
     * Retorna la variable vigenciaGubernamental
     * 
     * @return vigenciaGubernamental
     */
    public String getVigenciaGubernamental() {
        return vigenciaGubernamental;
    }

    /**
     * Asigna la variable vigenciaGubernamental
     * 
     * @param vigenciaGubernamental
     * Variable a asignar en vigenciaGubernamental
     */
    public void setVigenciaGubernamental(String vigenciaGubernamental) {
        this.vigenciaGubernamental = vigenciaGubernamental;
    }

    /**
     * Retorna la variable vigenciaPresupuestal
     * 
     * @return vigenciaPresupuestal
     */
    public String getVigenciaPresupuestal() {
        return vigenciaPresupuestal;
    }

    /**
     * Asigna la variable vigenciaPresupuestal
     * 
     * @param vigenciaPresupuestal
     * Variable a asignar en vigenciaPresupuestal
     */
    public void setVigenciaPresupuestal(String vigenciaPresupuestal) {
        this.vigenciaPresupuestal = vigenciaPresupuestal;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Retorna el objeto contArchivoSelectorArchivo
     * 
     * @return contArchivoSelectorArchivo
     */
    public ContenedorArchivo getContArchivoSelectorArchivo() {
        return contArchivoSelectorArchivo;
    }

    /**
     * Asigna el objeto contArchivoSelectorArchivo
     * 
     * @param contArchivoSelectorArchivo
     * Variable a asignar en contArchivoSelectorArchivo
     */
    public void setContArchivoSelectorArchivo(
        ContenedorArchivo contArchivoSelectorArchivo) {
        this.contArchivoSelectorArchivo = contArchivoSelectorArchivo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listacmbHoja
     * 
     * @return listacmbHoja
     */
    public List<Registro> getListacmbHoja() {
        return listacmbHoja;
    }

    /**
     * Asigna la lista listacmbHoja
     * 
     * @param listacmbHoja
     * Variable a asignar en listacmbHoja
     */
    public void setListacmbHoja(List<Registro> listacmbHoja) {
        this.listacmbHoja = listacmbHoja;
    }

    /**
     * Retorna la lista listaVigenciaGubernamental
     * 
     * @return listaVigenciaGubernamental
     */
    public List<Registro> getListaVigenciaGubernamental() {
        return listaVigenciaGubernamental;
    }

    /**
     * Asigna la lista listaVigenciaGubernamental
     * 
     * @param listaVigenciaGubernamental
     * Variable a asignar en listaVigenciaGubernamental
     */
    public void setListaVigenciaGubernamental(
        List<Registro> listaVigenciaGubernamental) {
        this.listaVigenciaGubernamental = listaVigenciaGubernamental;
    }

    /**
     * Retorna la lista listaVigenciaPresupuestal
     * 
     * @return listaVigenciaPresupuestal
     */
    public List<Registro> getListaVigenciaPresupuestal() {
        return listaVigenciaPresupuestal;
    }

    /**
     * Asigna la lista listaVigenciaPresupuestal
     * 
     * @param listaVigenciaPresupuestal
     * Variable a asignar en listaVigenciaPresupuestal
     */
    public void setListaVigenciaPresupuestal(
        List<Registro> listaVigenciaPresupuestal) {
        this.listaVigenciaPresupuestal = listaVigenciaPresupuestal;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}

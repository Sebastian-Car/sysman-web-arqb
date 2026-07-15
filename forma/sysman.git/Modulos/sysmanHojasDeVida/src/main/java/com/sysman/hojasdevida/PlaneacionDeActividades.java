/*-
 * PlaneacionDeActividades.java
 *
 * 1.0
 * 
 * 31/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.PlaneacionDeActividadesControladorEnum;
import com.sysman.hojasdevida.enums.PlaneacionDeActividadesControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Esta clase me permite gestionar la funcionalidad del formulario
 * PLANEACIONDEACTIVIDADES
 *
 * @version 1.0, 31/01/2018
 * @author mvenegas
 */
@ManagedBean
@ViewScoped
public class PlaneacionDeActividades extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /* variable para capturar el año inicial selecionado */
    private String anoInicial;
    /* variable para capturar el año final selecionado */
    private String anoFinal;
    /* variable para capturar el tipo de actividad selecionado */
    private String tipoActividad;
    /* variable para capturar el tipo de actividad selecionado */
    private String codigoTipoActividad;

    /**
     * Variable encargada de almacenar los registros de la
     * listatxtAnoFinal
     */
    private List<Registro> listatxtAnoFinal;
    /**
     * Variable encargada de almacenar los registros de la
     * listatxtAnoInicial
     */
    private List<Registro> listatxtAnoInicial;

    /**
     * Variable encargada de almacenar los registros de la
     * listacmbTipoActividad
     */
    private RegistroDataModelImpl listacmbTipoActividad;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de PlaneacionDeActividades
     */
    public PlaneacionDeActividades() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.PLANEACION_DE_ACTIVIDADES_CONTROLADOR
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
        cargarListatxtAnoFinal();
        cargarListatxtAnoInicial();
        cargarListacmbTipoActividad();
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
        /*
         * FR1673-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAno
     *
     * 
     */
    public void cargarListatxtAnoFinal() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listatxtAnoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PlaneacionDeActividadesControladorUrlEnum.URL100
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    public void cargarListatxtAnoInicial() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listatxtAnoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PlaneacionDeActividadesControladorUrlEnum.URL100
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    public void cambiartxtAnoFinal() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiartxtAnoInicial() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Carga la lista listacmbTipoActividad
     *
     */
    public void cargarListacmbTipoActividad() {
        if (SessionUtil.getMenuActual().equals("21040302")) {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            PlaneacionDeActividadesControladorUrlEnum.URL102
                                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put("MENU", "2104");

            listacmbTipoActividad = new RegistroDataModelImpl(
                            urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true,
                            GeneralParameterEnum.CODIGO.getName());
        }
        else {
            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            PlaneacionDeActividadesControladorUrlEnum.URL102
                                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put("MENU", "2108");

            listacmbTipoActividad = new RegistroDataModelImpl(
                            urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true,
                            GeneralParameterEnum.CODIGO.getName());

        }
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbTipoActividad
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbTipoActividad(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoActividad = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();

        codigoTipoActividad = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();
    }

    public void generarInforme(ReportesBean.FORMATOS formato) {

        /* VALIDAR QUE EL AÑO INICIAL NO SEA MAYOR AL USUARIO FINAL */
        if (Integer.parseInt(anoInicial) > Integer.parseInt(anoFinal)) {
            JsfUtil.agregarMensajeAlerta(idioma
                            .getString(PlaneacionDeActividadesControladorEnum.TB_TB3957
                                            .getValue()));
            return;
        }

        try {
            /*
             * ENVIAR LOS PARAMETROS AL DSS PARA PODER TRAER EL NOMBRE
             */
            Map<String, Object> parametrosNombre = new TreeMap<>();

            parametrosNombre.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            parametrosNombre.put(GeneralParameterEnum.CODIGO.getName(),
                            codigoTipoActividad);

            Registro rsIdEmpleado = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PlaneacionDeActividadesControladorUrlEnum.URL101
                                                                            .getValue())
                                            .getUrl(), parametrosNombre));

            /* CAPTURAR EL NOMBRE RESULTANTE DEL DSS */

            String nombreActividad = (!codigoTipoActividad
                            .equals(PlaneacionDeActividadesControladorEnum.NUMERO_OCHO
                                            .getValue()))
                                                ? SysmanFunciones.concatenar(
                                                                "PLANEACIÓN DE ACTIVIDADES ",
                                                                SysmanFunciones
                                                                                .toString(rsIdEmpleado
                                                                                                .getCampos()
                                                                                                .get(GeneralParameterEnum.NOMBRE
                                                                                                                .getName())))
                                                : SysmanFunciones
                                                                .toString("PLANEACIÓN DE ACTIVIDADES");

            /*
             * ENVIAR LOS PARAMETROS NECESARIOS PARA EL FUNCIONAMIENTO
             * DE LA CONSULTA DEL REPOIRTE
             */
            Map<String, Object> remplazar = new HashMap<>();
            remplazar.put("compania", compania);
            remplazar.put("anoInicial", anoInicial);
            remplazar.put("anoFinal", anoFinal);
            remplazar.put("tipoPlaneacion", codigoTipoActividad);

            /* ENVIAR LOS PARAMETROS QUE SOLICITA EL REPORTE */
            Map<String, Object> params = new HashMap<>();

            params.put(PlaneacionDeActividadesControladorEnum.PR_TITULO
                            .getValue(), nombreActividad);
            params.put(PlaneacionDeActividadesControladorEnum.PR_TXTANOINICIAL
                            .getValue(), anoInicial);
            params.put(PlaneacionDeActividadesControladorEnum.PR_TXTANOFINAL
                            .getValue(), anoFinal);
            params.put(PlaneacionDeActividadesControladorEnum.PR_NOMBRECOMPANIA
                            .getValue(),
                            SessionUtil.getCompaniaIngreso().getNombre());

            /*
             * APUNTAR A LA CONSULTA QUE SE ENCUENTRA EN LA TABLA
             * CONSULTAS DE IRIS
             */

            if (codigoTipoActividad
                            .equals(PlaneacionDeActividadesControladorEnum.NUMERO_OCHO
                                            .getValue())) {

                Reporteador.resuelveConsulta("001679RptPlaSalud",
                                Integer.parseInt(SessionUtil.getModulo()),
                                remplazar, params);

                archivoDescarga = JsfUtil.exportarStreamed(
                                "001682RptPlaTodas", params,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
            }
            else if (codigoTipoActividad
                            .equals(PlaneacionDeActividadesControladorEnum.NUMERO
                                            .getValue())) {
                params.put(PlaneacionDeActividadesControladorEnum.PR_TITULO
                                .getValue(),
                                "PLANEACIÓN DE TODAS LAS ACTIVIDADES SGSST");

                Reporteador.resuelveConsulta("001899RptPlaSaludSgsst",
                                Integer.parseInt(SessionUtil.getModulo()),
                                remplazar, params);

                archivoDescarga = JsfUtil.exportarStreamed(
                                "001682RptPlaTodas", params,
                                ConectorPool.ESQUEMA_SYSMAN, formato);

            }
            else if (codigoTipoActividad.equals("101")
                || codigoTipoActividad.equals("102")
                || codigoTipoActividad.equals("103")) {

                params.put(PlaneacionDeActividadesControladorEnum.PR_TITULO
                                .getValue(),
                                "PLANEACIÓN DE TODAS LAS ACTIVIDADES SGSST");

                Reporteador.resuelveConsulta("001899RptPlaSaludSgsst",
                                Integer.parseInt(SessionUtil.getModulo()),
                                remplazar, params);

                archivoDescarga = JsfUtil.exportarStreamed(
                                "001682RptPlaTodas", params,
                                ConectorPool.ESQUEMA_SYSMAN, formato);

            }
            else {

                Reporteador.resuelveConsulta("001679RptPlaSalud",
                                Integer.parseInt(SessionUtil.getModulo()),
                                remplazar, params);

                archivoDescarga = JsfUtil.exportarStreamed(
                                "001679RptPlaSalud", params,
                                ConectorPool.ESQUEMA_SYSMAN, formato);

            }

        }
        catch (JRException | IOException | SysmanException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton generarPdf en la vista
     *
     *
     */
    public void oprimirgenerarPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton generarExcel en la vista
     *
     *
     */
    public void oprimirgenerarExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);
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
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    public String getAnoInicial() {
        return anoInicial;
    }

    public void setAnoInicial(String anoInicial) {
        this.anoInicial = anoInicial;
    }

    public String getAnoFinal() {
        return anoFinal;
    }

    public void setAnoFinal(String anoFinal) {
        this.anoFinal = anoFinal;
    }

    public List<Registro> getListatxtAnoFinal() {
        return listatxtAnoFinal;
    }

    public void setListatxtAnoFinal(List<Registro> listatxtAnoFinal) {
        this.listatxtAnoFinal = listatxtAnoFinal;
    }

    public List<Registro> getListatxtAnoInicial() {
        return listatxtAnoInicial;
    }

    public void setListatxtAnoInicial(List<Registro> listatxtAnoInicial) {
        this.listatxtAnoInicial = listatxtAnoInicial;
    }

    public String getTipoActividad() {
        return tipoActividad;
    }

    public void setTipoActividad(String tipoActividad) {
        this.tipoActividad = tipoActividad;
    }
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacmbTipoActividad
     * 
     * @return listacmbTipoActividad
     */
    public RegistroDataModelImpl getListacmbTipoActividad() {
        return listacmbTipoActividad;
    }

    /**
     * Asigna la lista listacmbTipoActividad
     * 
     * @param listacmbTipoActividad
     * Variable a asignar en listacmbTipoActividad
     */
    public void setListacmbTipoActividad(
        RegistroDataModelImpl listacmbTipoActividad) {
        this.listacmbTipoActividad = listacmbTipoActividad;
    }

    public String getCodigoTipoActividad() {
        return codigoTipoActividad;
    }

    public void setCodigoTipoActividad(String codigoTipoActividad) {
        this.codigoTipoActividad = codigoTipoActividad;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}

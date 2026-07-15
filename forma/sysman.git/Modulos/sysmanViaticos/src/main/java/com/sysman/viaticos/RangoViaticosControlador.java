/*-
 * RangoViaticosControlador.java
 *
 * 1.0
 * 
 * 8 ago. 2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.viaticos;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import java.io.IOException;
import java.util.Date;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;
import com.sysman.viaticos.enums.RangoViaticosControladorUrlEnum;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Formulario que administra los valores de viaticos nacionales e
 * internacionales entre rangos salariales
 *
 * @version 1.0, 08/08/2018
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class RangoViaticosControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Variable que almacena la vigencia seleccionada en el combo
     */
    private String vigencia;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que almacena las vigencias
     */
    private List<Registro> listaAnio;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de RangoViaticosControlador
     */
    public RangoViaticosControlador() {
        super();
        compania = SessionUtil.getCompania();
        vigencia = Integer.toString(SysmanFunciones.ano(new Date()));
        try {
            numFormulario = GeneralCodigoFormaEnum.RANGO_VIATICOS_CONTROLADOR
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
        enumBase = GenericUrlEnum.VI_RANGOS_VIATICOS;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaAnio();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosListado.put(GeneralParameterEnum.ANO.getName(), vigencia);

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
                                                            RangoViaticosControladorUrlEnum.URL3968
                                                                            .getValue())
                                            .getUrl(),
                            param));
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
     * Metodo ejecutado al oprimir el boton ImprimirResolucion en la
     * vista
     *
     */
    public void oprimirImprimirResolucion() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        generarReporte(FORMATOS.PDF);

        // </CODIGO_DESARROLLADO>
    }

    private void generarReporte(FORMATOS formato) {
        String informe = "001928RangoViaticos";
        Map<String, Object> reemplazos = new TreeMap<>();
        Map<String, Object> parametros = new TreeMap<>();

        try {

            reemplazos.put("compania", compania);
            reemplazos.put("vigencia", vigencia);

            parametros.put("PR_VIGENCIA", vigencia);

            Reporteador.resuelveConsulta(informe,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazos, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            informe, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Anio
     * 
     */
    public void cambiarAnio() {
        reasignarOrigen();
    }

    /**
     * Metodo ejecutado al cambiar el control valorManual en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarvalorManualC(int rowNum) {

        if (Double.parseDouble(listaInicial.getDatasource().get(rowNum
            % 10).getCampos().get("VALOR_MANUAL").toString()) > Double
                            .parseDouble(listaInicial.getDatasource()
                                            .get(rowNum % 10).getCampos()
                                            .get("VALOR_VIATICO_NACIONAL")
                                            .toString())) {

            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4218"));

            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put("VALOR_MANUAL", 0);

        }

        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // METODO_NO_IMPLEMENTADO
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado
     * 
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {

        registro.getCampos().put(GeneralParameterEnum.VIGENCIA.getName(),
                        vigencia);

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        registro.getCampos().put("CODIGO_RANGO", generarConsecutivo());

        return validarRangos();
    }

    private boolean validarRangos() {
        double rangoInferior = Double.parseDouble(
                        registro.getCampos().get("LIMITE_INFERIOR").toString());
        double rangoSuperior = Double.parseDouble(
                        registro.getCampos().get("LIMITE_SUPERIOR").toString());

        if (rangoSuperior < rangoInferior) {

            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4187"));
            return false;
        }
        return true;
    }

    private Object generarConsecutivo() {
        long consecutivo = 0;

        try {
            consecutivo = ejbSysmanUtil.generarSiguienteConsecutivo(
                            "VI_RANGOS_VIATICOS",
                            "COMPANIA = ''" + compania + "''",
                            "CODIGO_RANGO");
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return consecutivo;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * 
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return validarRangos();
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * 
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * 
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * 
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {

        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // METODO_NO_IMPLEMENTADO
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable vigencia
     * 
     * @return vigencia
     */
    public String getVigencia() {
        return vigencia;
    }

    /**
     * Asigna la variable vigencia
     * 
     * @param vigencia
     * Variable a asignar en vigencia
     */
    public void setVigencia(String vigencia) {
        this.vigencia = vigencia;
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
    // </SET_GET_LISTAS_COMBO_GRANDE>
}

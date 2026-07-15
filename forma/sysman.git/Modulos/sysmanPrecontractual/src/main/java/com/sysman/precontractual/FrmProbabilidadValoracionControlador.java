/*-
 * FrmProbabilidadValoracionControlador.java
 *
 * 1.0
 * 
 * 31/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 * Clase que premite gestionar los formularios de la ruta
 * PRECONTRACTUAL/ARCHIVO/RIESGOS/BOTONES 5 Y 6
 *
 * @version 1.0, 31/07/2018
 * @author mvenegas
 */
@ManagedBean
@ViewScoped
public class FrmProbabilidadValoracionControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Variable que almacena la opcion desde la cual es llamado el
     * formulario
     */
    private String opcion;
    private String opcionMenu;

    /**
     * Varible que cambiara el titulo del formulario
     */

    private String tituloFormulario;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Constante quer almacena la palabra TIPO_OPCION
     */
    private final String cTipoOpcion;
    /**
     * Listado de valoraciones
     */
    private List<Registro> listatxtValoracion;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de
     * FrmProbabilidadValoracionControlador
     */
    public FrmProbabilidadValoracionControlador() {
        super();
        compania = SessionUtil.getCompania();
        cTipoOpcion = "TIPO_OPCION";

        opcionMenu = SessionUtil.getMenuActual();

        configurarFormuario();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_ES_PROBABILIDAD_VALORACION_CONTROLADOR.getCodigo();
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
     * Este metodo configura el formulario Segun la opcion de menu
     * desde la cual se abra
     */
    public void configurarFormuario() {
        if (opcionMenu.equals("19011505")) {
            tituloFormulario = "Probabilidad";
            opcion = "4";
        }
        else {
            tituloFormulario = "Valoración y Categoria";
            opcion = "6";
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
        enumBase = GenericUrlEnum.ES_PROBABILIDAD_VALORACION;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        cargarListatxtValoracion();
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

        parametrosListado.put(cTipoOpcion, opcion);
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
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
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Carga la lista listatxtValoracion
     *
     * 
     */
    public void cargarListatxtValoracion() {

        Map<String, Object> param = new TreeMap<>();

        if (opcion.equals("4")) {
            param.put("INICIA_EN_CERO",
                            "-1");
            param.put("INICIA_EN_DOS", "0");

            param.put("FINAL",
                            "5");
        }
        else {
            param.put("INICIA_EN_CERO",
                            "0");
            param.put("INICIA_EN_DOS", "-1");

            param.put("FINAL",
                            "10");
        }

        try {
            listatxtValoracion = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            "118034")
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(), generarConsecutivo());
        registro.getCampos().put(cTipoOpcion, opcion);
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
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
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
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
        registro.getCampos().remove(cTipoOpcion);
        registro.getCampos().remove("CODIGO");
        registro.getCampos().remove("COMPANIA");
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public String generarConsecutivo() {

        String consecutivoSalida = "0";
        try {
            long consecutivoGenerado = ejbSysmanUtil
                            .generarSiguienteConsecutivo(
                                            "ES_PROBABILIDAD_VALORACION",
                                            SysmanFunciones.concatenar("COMPANIA = ''", compania, "''", " AND TIPO_OPCION= ", opcion),
                                            GeneralParameterEnum.CODIGO.getName());

            consecutivoSalida = String.valueOf(consecutivoGenerado);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return consecutivoSalida;
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // Auto-generated method stub
    }
    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public String getTituloFormulario() {
        return tituloFormulario;
    }

    public void setTituloFormulario(String tituloFormulario) {
        this.tituloFormulario = tituloFormulario;
    }

    /**
     * Retorna la lista listatxtValoracion
     * 
     * @return listatxtValoracion
     */
    public List<Registro> getListatxtValoracion() {
        return listatxtValoracion;
    }

    /**
     * Asigna la lista listatxtValoracion
     * 
     * @param listatxtValoracion
     * Variable a asignar en listatxtValoracion
     */
    public void setListatxtValoracion(List<Registro> listatxtValoracion) {
        this.listatxtValoracion = listatxtValoracion;
    }
}

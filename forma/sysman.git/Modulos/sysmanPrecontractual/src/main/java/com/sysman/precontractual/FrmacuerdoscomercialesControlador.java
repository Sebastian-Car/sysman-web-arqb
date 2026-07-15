/*-
 * FrmacuerdoscomercialesControlador.java
 *
 * 1.0
 * 
 * 10/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Esta clase se encarga de permitir gestionar los acuerdos
 * comerciales
 *
 * @version 1.0, 10/07/2018
 * @author mvenegas
 */
@ManagedBean
@ViewScoped
public class FrmacuerdoscomercialesControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * lista de paises
     */
    private RegistroDataModelImpl listacmbPais;
    /**
     * lista de paises al seleccionar editar
     */
    private RegistroDataModelImpl listacmbPaisE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    /**
     * Variable que va a almacenar el codigo del pais
     */
    private String codigoPais;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmacuerdoscomercialesControlador
     */
    public FrmacuerdoscomercialesControlador() {
        super();
        compania = SessionUtil.getCompania();
        codigoPais = "";
        try {
            numFormulario = 1853;
            validarPermisos();
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
        try {
            enumBase = GenericUrlEnum.ACUERDOS_COMERCIALES;
            reasignarOrigen();
            buscarLlave();
            registro = new Registro();
            // <CARGAR_LISTA>
            // </CARGAR_LISTA>
            // <CARGAR_LISTA_COMBO_GRANDE>
            cargarListacmbPais();
            cargarListacmbPaisE();
            // </CARGAR_LISTA_COMBO_GRANDE>
            abrirFormulario();
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
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

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listacmbPais
     *
     */
    public void cargarListacmbPais() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID("1003");
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacmbPais = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listacmbPais
     *
     */
    public void cargarListacmbPaisE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID("1003");
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacmbPaisE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbPais
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbPais(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("PAIS", registroAux.getCampos().get("NOMBRE"));

        codigoPais = SysmanFunciones.toString(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbPais
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbPaisE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos().get("NOMBRE");
        codigoPais = SysmanFunciones.toString(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
    }

    public String generarConsecutivo() {

        String consecutivoSalida = "0";
        try {
            long consecutivoGenerado = ejbSysmanUtil
                            .generarSiguienteConsecutivo(
                                            "ACUERDOS_COMERCIALES",
                                            SysmanFunciones.concatenar("COMPANIA = ''", compania, "''"),
                                            GeneralParameterEnum.CODIGO.getName());

            consecutivoSalida = String.valueOf(consecutivoGenerado);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return consecutivoSalida;
    }

    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1853-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
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
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().remove("PAIS");
        registro.getCampos().put("PAIS", codigoPais);
        registro.getCampos().put(GeneralParameterEnum.CODIGO.getName(), generarConsecutivo());
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
        registro.getCampos().put("PAIS", codigoPais);
        return true;
    }

    public String obtenerCodigoPais(String nombrePais) {
        String salida = "";

        Map<String, Object> parametros = new TreeMap<>();

        parametros.put("NOMBRE_PAIS", nombrePais);

        Registro rsPais;
        try {
            rsPais = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil
                                            .getInstance()
                                            .getUrlServiceByUrlByEnumID("1008")
                                            .getUrl(),
                                            parametros));
            salida = SysmanFunciones.toString(
                            rsPais.getCampos()
                                            .get("PAIS"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return salida;

    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
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
        registro.getCampos().remove("COMPANIA");
        registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
        codigoPais = obtenerCodigoPais(registro.getCampos().get("PAIS").toString());
        registro.getCampos().put("PAIS", codigoPais);
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        //
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listacmbPais
     * 
     * @return listacmbPais
     */
    public RegistroDataModelImpl getListacmbPais() {
        return listacmbPais;
    }

    /**
     * Asigna la lista listacmbPais
     * 
     * @param listacmbPais
     * Variable a asignar en listacmbPais
     */
    public void setListacmbPais(RegistroDataModelImpl listacmbPais) {
        this.listacmbPais = listacmbPais;
    }

    /**
     * Retorna la lista listacmbPais
     * 
     * @return listacmbPais
     */
    public RegistroDataModelImpl getListacmbPaisE() {
        return listacmbPaisE;
    }

    /**
     * Asigna la lista listacmbPais
     * 
     * @param listacmbPais
     * Variable a asignar en listacmbPais
     */
    public void setListacmbPaisE(RegistroDataModelImpl listacmbPaisE) {
        this.listacmbPaisE = listacmbPaisE;
    }

    /**
     * Retorna la variable auxiliar
     * 
     * @return auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * Asigna la variable auxiliar
     * 
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}

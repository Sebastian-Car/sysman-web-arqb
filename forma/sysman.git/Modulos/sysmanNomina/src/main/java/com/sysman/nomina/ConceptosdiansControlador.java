/*-
 * ConceptosdiansControlador.java
 *
 * 1.0
 *
 * 11/02/2019
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaSeisRemote;
import com.sysman.nomina.enums.ConceptosdiansControladorEnum;
import com.sysman.nomina.enums.ConceptosdiansControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @version 1.0, 11/02/2019
 * @author mzanguna
 */
@ManagedBean
@ViewScoped
public class ConceptosdiansControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private boolean muestraMensaje = false;
    private boolean muestraMensajeDatos = false;
    // <DECLARAR_ATRIBUTOS>

    private int ano;
    private int anoIni;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Combo de años
     */
    private List<Registro> listaano;
    private List<Registro> listacbAnio1;
    /**
     * Combo de código Dian.
     */
    private List<Registro> listaCbCodDian;

    @EJB
    private EjbNominaSeisRemote ejbNominaSeis;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que carga los conceptos
     */
    private RegistroDataModelImpl listaConcepto;
    /**
     * Lista que carga los conceptos en la grilla
     */
    private RegistroDataModelImpl listaConceptoE;

    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ConceptosdiansControlador
     */
    public ConceptosdiansControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.CONCEPTOS_DIAN_CONTROLADOR
                            .getCodigo();
            ano = SysmanFunciones.ano(new Date());
            anoIni = ano - 1;
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

        enumBase = GenericUrlEnum.CONCEPTOSANO;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro(new HashMap<String, Object>());
        // <CARGAR_LISTA>
        cargarListaano();
        cargarListacbAnio1();
        cargarListaCbCodDian();
        cargarListaConcepto();
        cargarListaConceptoE();
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
        parametrosListado.put(ConceptosdiansControladorEnum.ANIO.getValue(),
                        ano);

        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        ConceptosdiansControladorUrlEnum.URL4926.getValue());
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaano
     *
     */
    public void cargarListaano() {
        try {
            listaano = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ConceptosdiansControladorUrlEnum.URL4925
                                                                            .getValue())
                                            .getUrl(), null));

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListacbAnio1() {
        listacbAnio1 = listaano;
    }

    /**
     * 
     * Carga la lista listaConcepto
     *
     */
    public void cargarListaConcepto() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ConceptosdiansControladorUrlEnum.URL4928
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaConcepto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.ID_DE_CONCEPTO.getName());

    }

    /**
     * 
     * Carga la lista listaConcepto
     *
     */
    public void cargarListaConceptoE() {
        listaConceptoE = listaConcepto;
    }

    /**
     *
     * Carga la lista listaCbCodDian
     *
     */
    public void cargarListaCbCodDian() {
        try {
            listaCbCodDian = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil
                                            .getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ConceptosdiansControladorUrlEnum.URL4927
                                                                            .getValue())
                                            .getUrl(), null));
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
     * Metodo ejecutado al oprimir el boton BtPrepararAnos en la vista
     *
     *
     */
    public void oprimirBtPrepararAnos() {
        anoIni = ano - 1;

        if (listaInicial.getRowCount() > 0) {
            muestraMensajeDatos = true;
        }
        else {
            muestraMensaje = true;
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control ano
     *
     *
     */
    public void cambiarano() {
        reasignarOrigen();
        cargarForma();
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * DgAniosPrep en la vista
     *
     *
     */
    public void aceptarDgAniosPrep() {
        pasarConfiguracionAnos();
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * DgConfirmacion en la vista
     *
     *
     */
    public void aceptarDgConfirmacion() {
        muestraMensajeDatos = false;
        muestraMensaje = true;

    }

    /**
     * Metodo que pasa la configuracion de un año a otro;
     */
    public void pasarConfiguracionAnos() {
        try {
            ejbNominaSeis.pasarConfiguracionDian(compania, ano, anoIni,
                            SessionUtil.getUser().getCodigo());
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB874"));
            muestraMensaje = false;
            muestraMensajeDatos = false;
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaConcepto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConcepto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.ID_DE_CONCEPTO.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.ID_DE_CONCEPTO
                                                        .getName()));

        registro.getCampos().put(
                        ConceptosdiansControladorEnum.NOMBRE_CONCEPTO
                                        .getValue(),
                        registroAux.getCampos().get(
                                        ConceptosdiansControladorEnum.NOMBRE_CONCEPTO
                                                        .getValue()));

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaConcepto
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConceptoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos()
                        .get(GeneralParameterEnum.ID_DE_CONCEPTO.getName());
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
        ano = SysmanFunciones.ano(new Date());
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
     * @return boolean
     */
    @Override
    public boolean insertarAntes() {

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), ano);
        registro.getCampos().remove("DESCRIPCION");
        registro.getCampos()
                        .remove(ConceptosdiansControladorEnum.NOMBRE_CONCEPTO
                                        .getValue());

        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     *
     * @return boolean
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
     * @return boolean
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
     *
     *
     * @return boolean
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
     * @return boolean
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR2032-ANTES_ELIMINAR Private Sub Form_Delete(Cancel As
         * Integer) If Me![Ano] = "0000" And Me![Mes] = "00" And
         * Me![Periodo] = "00" Then Cancel = True Else Cancel = False
         * End If End Sub
         */
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     *
     * @return boolean
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
        registro.getCampos().remove("ANO");
        registro.getCampos().remove("COMPANIA");
        registro.getCampos().remove("DESCRIPCION");
        registro.getCampos()
                        .remove(ConceptosdiansControladorEnum.NOMBRE_CONCEPTO
                                        .getValue());
        registro.getCampos()
                        .remove(GeneralParameterEnum.ID_DE_CONCEPTO.getName());
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
    /**
     * Retorna la variable ano
     *
     * @return ano
     */
    public int getAno() {
        return ano;
    }

    /**
     * Asigna la variable ano
     *
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(int ano) {
        this.ano = ano;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaano
     *
     * @return listaano
     */
    public List<Registro> getListaano() {
        return listaano;
    }

    /**
     * Asigna la lista listaano
     *
     * @param listaano
     * Variable a asignar en listaano
     */
    public void setListaano(List<Registro> listaano) {
        this.listaano = listaano;
    }

    /**
     * Retorna la lista listaCbCodDian
     *
     * @return listaCbCodDian
     */
    public List<Registro> getListaCbCodDian() {
        return listaCbCodDian;
    }

    /**
     * Asigna la lista listaCbCodDian
     *
     * @param listaCbCodDian
     * Variable a asignar en listaCbCodDian
     */
    public void setListaCbCodDian(List<Registro> listaCbCodDian) {
        this.listaCbCodDian = listaCbCodDian;
    }

    public boolean isMuestraMensaje() {
        return muestraMensaje;
    }

    public void setMuestraMensaje(boolean muestraMensaje) {
        this.muestraMensaje = muestraMensaje;
    }

    public int getAnoIni() {
        return anoIni;
    }

    public void setAnoIni(int anoIni) {
        this.anoIni = anoIni;
    }

    public List<Registro> getListacbAnio1() {
        return listacbAnio1;
    }

    public void setListacbAnio1(List<Registro> listacbAnio1) {
        this.listacbAnio1 = listacbAnio1;
    }

    public boolean isMuestraMensajeDatos() {
        return muestraMensajeDatos;
    }

    public void setMuestraMensajeDatos(boolean muestraMensajeDatos) {
        this.muestraMensajeDatos = muestraMensajeDatos;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaConcepto
     * 
     * @return listaConcepto
     */
    public RegistroDataModelImpl getListaConcepto() {
        return listaConcepto;
    }

    /**
     * Asigna la lista listaConcepto
     * 
     * @param listaConcepto
     * Variable a asignar en listaConcepto
     */
    public void setListaConcepto(RegistroDataModelImpl listaConcepto) {
        this.listaConcepto = listaConcepto;
    }

    /**
     * Retorna la lista listaConcepto
     * 
     * @return listaConcepto
     */
    public RegistroDataModelImpl getListaConceptoE() {
        return listaConceptoE;
    }

    /**
     * Asigna la lista listaConcepto
     * 
     * @param listaConcepto
     * Variable a asignar en listaConcepto
     */
    public void setListaConceptoE(RegistroDataModelImpl listaConceptoE) {
        this.listaConceptoE = listaConceptoE;
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

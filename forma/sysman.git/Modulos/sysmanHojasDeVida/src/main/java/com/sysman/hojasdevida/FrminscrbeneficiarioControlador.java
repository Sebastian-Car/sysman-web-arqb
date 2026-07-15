/*-
 * FrminscrbeneficiarioControlador.java
 *
 * 1.0
 * 
 * 05/02/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.ejb.EjbHojasDeVidaCeroRemote;
import com.sysman.hojasdevida.enums.FrmgrupocriteriosControladorEnum;
import com.sysman.hojasdevida.enums.FrminscrbeneficiarioControladorEnum;
import com.sysman.hojasdevida.enums.FrminscrbeneficiarioControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @version 1.0, 05/02/2018
 * @author dnino
 */
@ManagedBean
@ViewScoped
public class FrminscrbeneficiarioControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante a nivel de clase que almacena el nombre con el cual
     * inicio sesion el usuario, el valor de esta constante es
     * asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String usuario;

    private String numeroDcto;

    private String sucursalEmpleado;

    private String auxiliar;

    private String documento;
    /**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
    private int indice;

    private String tipoEvento;

    private String evento;

    private Date fechaInicial;

    private Date fechaInicio;

    /**
     * @return the evento
     */
    public String getEvento() {
        return evento;
    }

    /**
     * @param evento
     * the evento to set
     */
    public void setEvento(String evento) {
        this.evento = evento;
    }

    /**
     * @return the fechaInicio
     */
    public Date getFechaInicio() {
        return fechaInicio;
    }

    /**
     * @param fechaInicio
     * the fechaInicio to set
     */
    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    /**
     * @return the parametrosEntrada
     */
    public Map<String, Object> getParametrosEntrada() {
        return parametrosEntrada;
    }

    /**
     * @param parametrosEntrada
     * the parametrosEntrada to set
     */
    public void setParametrosEntrada(Map<String, Object> parametrosEntrada) {
        this.parametrosEntrada = parametrosEntrada;
    }

    private Map<String, Object> ridDatos;
    private Map<String, Object> parametrosEntrada;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Listado que despliega las opciones de parentesco del
     * beneficiario.
     */
    private RegistroDataModelImpl listaParentesco;
    /**
     * Listado que despliega las opciones de editar parentesco del
     * beneficiario.
     */
    private RegistroDataModelImpl listaParentescoE;
    /**
     * Listado que despliega las opciones del tipo de documento del
     * beneficiario.
     */
    private RegistroDataModelImpl listaTipoDocumento;
    /**
     * Listado que despliega las opciones de edicion del tipo de
     * documento del beneficiario.
     */
    private RegistroDataModelImpl listaTipoDocumentoE;

    /**
     * Implementacion del EJB de SysmanUtil para acceder a funciones
     * y/o procedimientos definidos en el paquete PCK_SYSMAN_UTL
     */
    @EJB
    private EjbHojasDeVidaCeroRemote ejbHojasdevida;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrminscrbeneficiarioControlador
     */
    public FrminscrbeneficiarioControlador() {
        super();
        compania = SessionUtil.getCompania();
        usuario = SessionUtil.getUser().toString();
        parametrosEntrada = SessionUtil.getFlash();
        if (parametrosEntrada != null) {
            ridDatos = (Map<String, Object>) parametrosEntrada.get("rid");
            numeroDcto = ridDatos.get("KEY_NUMERO_DCTO").toString();

            sucursalEmpleado = ridDatos.get("KEY_SUCURSAL").toString();
            tipoEvento = ridDatos.get("KEY_TIPOEVENTO").toString();
            evento = ridDatos.get("KEY_IDEVENTO").toString();
            fechaInicial = (Date) SysmanFunciones.nvl(parametrosEntrada
                            .get(GeneralParameterEnum.FECHAINICIAL
                                            .getName()),
                            null);
        }
        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_INSCR_BENEFICIARIO_CONTROLADOR
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
        enumBase = GenericUrlEnum.FAMILIARES;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();

        // <CARGAR_LISTA>
        cargarListaTipoDocumento();
        cargarListaTipoDocumentoE();
        cargarListaParentesco();
        cargarListaParentescoE();
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
        parametrosListado.put(FrminscrbeneficiarioControladorEnum.DCTO_EMPLEADO
                        .getValue(),
                        numeroDcto);
        parametrosListado.put(
                        FrminscrbeneficiarioControladorEnum.SUCURSAL_EMPLEADO
                                        .getValue(),
                        sucursalEmpleado);

        parametrosListado.put("TIPOEVENTO", tipoEvento);

        parametrosListado.put("IDEVENTO", evento);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID("723013");
        urlCreacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID("723005");
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID("723006");
        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID("723010");
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaTipoDocumento
     *
     */
    public void cargarListaTipoDocumento() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrminscrbeneficiarioControladorUrlEnum.URL802
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        listaTipoDocumento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrminscrbeneficiarioControladorEnum.DCTO_IDENTIDAD
                                        .getValue());
    }

    public void cargarListaTipoDocumentoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrminscrbeneficiarioControladorUrlEnum.URL802
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        listaTipoDocumentoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrminscrbeneficiarioControladorEnum.DCTO_IDENTIDAD
                                        .getValue());

    }

    public void cargarListaParentesco() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrminscrbeneficiarioControladorUrlEnum.URL801
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        listaParentesco = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrminscrbeneficiarioControladorEnum.PARENTESCO
                                        .getValue());
    }

    public void cargarListaParentescoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrminscrbeneficiarioControladorUrlEnum.URL801
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        listaParentescoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrminscrbeneficiarioControladorEnum.PARENTESCO
                                        .getValue());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control TipoDocumento en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarTipoDocumentoC(int rowNum) {
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(FrminscrbeneficiarioControladorEnum.DCTO_IDENTIDAD
                                        .getValue(), documento);
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista lista
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaParentesco(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        FrminscrbeneficiarioControladorEnum.PARENTESCO
                                        .getValue(),
                        registroAux.getCampos()
                                        .get(FrminscrbeneficiarioControladorEnum.PARENTESCO
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista lista
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaParentescoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = (String) registroAux.getCampos()
                        .get(FrminscrbeneficiarioControladorEnum.PARENTESCO
                                        .getValue());
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista Tipo
     * Documento
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoDocumento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        FrminscrbeneficiarioControladorEnum.DCTO_IDENTIDAD
                                        .getValue(),
                        registroAux.getCampos()
                                        .get(FrminscrbeneficiarioControladorEnum.DCTO_IDENTIDAD
                                                        .getValue()));
        documento = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        FrminscrbeneficiarioControladorEnum.DCTO_EMPLEADO
                                                        .getValue()),
                                        "")
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista lista
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoDocumentoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get("DCTO_IDENTIDAD"), "")
                        .toString();
        documento = SysmanFunciones
                        .nvl(registroAux.getCampos().get("DCTO_IDENTIDAD"), "")
                        .toString();
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
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return true si realiza acciones previas a la insercion del
     * registro.
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(
                        FrmgrupocriteriosControladorEnum.COMPANIA.getValue(),
                        compania);
        registro.getCampos().put(
                        FrminscrbeneficiarioControladorEnum.DCTO_EMPLEADO
                                        .getValue(),
                        numeroDcto);
        registro.getCampos().put(
                        FrminscrbeneficiarioControladorEnum.SUCURSAL_EMPLEADO
                                        .getValue(),
                        sucursalEmpleado);
        registro.getCampos()
                        .remove(FrminscrbeneficiarioControladorEnum.DOCUMENTO
                                        .getValue());
        registro.getCampos().remove(GeneralParameterEnum.DESCRIPCION.getName());
        // // </CODIGO_DESARROLLADO>
        return true;
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
     * @return true si requiere realizar operaciones posteriores a la
     * eliminacion de un registro.
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
        registro.getCampos()
                        .remove(FrminscrbeneficiarioControladorEnum.DCTO_EMPLEADO
                                        .getValue());
        registro.getCampos()
                        .remove(FrminscrbeneficiarioControladorEnum.SUCURSAL_EMPLEADO
                                        .getValue());
        registro.getCampos().remove(GeneralParameterEnum.DESCRIPCION.getName());
        registro.getCampos()
                        .remove(FrminscrbeneficiarioControladorEnum.DOCUMENTO
                                        .getValue());

    }

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void cerrarFormulario() {
        // CODIGO_DESARROLLADO
        // CODIGO_DESARROLLADO
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario, el cual invoca el procedimiento para insertar los
     * registros con el indicador de beneficiario activo en la tabla
     * NAT_ACTIVIDADESINSCRITOS.
     * 
     */
    public void ejecutarrcCerrar() {
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.ACTIVIDADES_INSCRITOS_CONTROLADOR
                                        .getCodigo()));
        try {

            ejbHojasdevida.actualizaractividadesinscritos(compania,
                            evento,
                            tipoEvento,
                            fechaInicial,
                            numeroDcto, sucursalEmpleado, usuario);
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        SessionUtil.redireccionarForma(direccionador,
                        SessionUtil.getModulo());
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // CODIGO_DESARROLLADO
        // CODIGO_DESARROLLADO
    }

    // <SET_GET_ATRIBUTOS>

    /**
     * @return the numeroDcto
     */
    public String getNumeroDcto() {
        return numeroDcto;
    }

    /**
     * @return the sucursalEmpleado
     */
    public String getSucursalEmpleado() {
        return sucursalEmpleado;
    }

    /**
     * @param sucursalEmpleado
     * the sucursalEmpleado to set
     */
    public void setSucursalEmpleado(String sucursalEmpleado) {
        this.sucursalEmpleado = sucursalEmpleado;
    }

    /**
     * @param numeroDcto
     * the numeroDcto to set
     */
    public void setNumeroDcto(String numeroDcto) {
        this.numeroDcto = numeroDcto;
    }

    /**
     * @return the compania
     */
    public String getCompania() {
        return compania;
    }

    // </SET_GET_ATRIBUTOS>

    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>

    /**
     * @return the auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * @return the listaTipoDocumento
     */
    public RegistroDataModelImpl getListaTipoDocumento() {
        return listaTipoDocumento;
    }

    /**
     * @param listaTipoDocumento
     * the listaTipoDocumento to set
     */
    public void setListaTipoDocumento(
        RegistroDataModelImpl listaTipoDocumento) {
        this.listaTipoDocumento = listaTipoDocumento;
    }

    /**
     * @return the usuario
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * @return the indice
     */
    public int getIndice() {
        return indice;
    }

    /**
     * @param indice
     * the indice to set
     */
    public void setIndice(int indice) {
        this.indice = indice;
    }

    /**
     * @param auxiliar
     * the auxiliar to set
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    /**
     * @return the documento
     */
    public String getDocumento() {
        return documento;
    }

    /**
     * @param documento
     * the documento to set
     */
    public void setDocumento(String documento) {
        this.documento = documento;
    }

    /**
     * @return the fechaInicial
     */
    public Date getFechaInicial() {
        return fechaInicial;
    }

    /**
     * @param fechaInicial
     * the fechaInicial to set
     */
    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    /**
     * @return the listaTipoDocumentoE
     */
    public RegistroDataModelImpl getListaTipoDocumentoE() {
        return listaTipoDocumentoE;
    }

    /**
     * @param listaTipoDocumentoE
     * the listaTipoDocumentoE to set
     */
    public void setListaTipoDocumentoE(
        RegistroDataModelImpl listaTipoDocumentoE) {
        this.listaTipoDocumentoE = listaTipoDocumentoE;
    }

    /**
     * @return the listaParentesco
     */
    public RegistroDataModelImpl getListaParentesco() {
        return listaParentesco;
    }

    /**
     * @return the listaParentescoE
     */
    public RegistroDataModelImpl getListaParentescoE() {
        return listaParentescoE;
    }

    /**
     * @param listaParentescoE
     * the listaParentescoE to set
     */
    public void setListaParentescoE(RegistroDataModelImpl listaParentescoE) {
        this.listaParentescoE = listaParentescoE;
    }

    /**
     * @param listaParentesco
     * the listaParentesco to set
     */
    public void setListaParentesco(RegistroDataModelImpl listaParentesco) {
        this.listaParentesco = listaParentesco;
    }

}

// </SET_GET_LISTAS>
// <SET_GET_LISTAS_COMBO_GRANDE>
// </SET_GET_LISTAS_COMBO_GRANDE>
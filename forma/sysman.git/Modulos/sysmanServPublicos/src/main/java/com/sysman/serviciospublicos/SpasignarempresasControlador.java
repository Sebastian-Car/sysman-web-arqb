/*-
 * SpasignarempresasControlador.java
 *
 * 1.0
 *
 * 26/07/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosTresRemote;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosUtlRemote;
import com.sysman.serviciospublicos.enums.SpasignarempresasControladorUrlEnum;
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
 * Clase que permite asignar a los usuarios del módulo de servicios
 * públicos, una empresa de aseo externa.
 *
 * @version 1.0, 26/07/2017
 * @author jrodriguezr
 */
@ManagedBean
@ViewScoped
public class SpasignarempresasControlador extends BeanBaseContinuoAcmeImpl {
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
     * Lista de objetos pertenecientes al combo EmpresaAseoExt
     */
    private RegistroDataModelImpl listaEmpresaAseoExt;
    /**
     * Lista de objetos pertenecientes al combo EmpresaAseoExt
     */
    private RegistroDataModelImpl listaEmpresaAseoExtE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    /**
     * Define el ciclo de usuarios seleccionado
     */
    private String ciclo;
    /**
     * variable que almacena el titulo en el formulario
     */
    private String tituloPrincipal;
    /**
     * variable que almacena el titulo secundario
     */
    private String tituloSecundario;
    /**
     * Llamado al EJB del paquete sercivios publicos utl
     */
    @EJB
    private EjbServiciosPublicosUtlRemote ejbServiciosPublicosUtl;

    @EJB
    private EjbServiciosPublicosTresRemote ejbServiciosPublicosTres;

    /**
     * Variable que define si se puede editar o no en el formulario
     */
    private boolean permiteEditar;
    private String periodo;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de SpasignarempresasControlador
     */
    public SpasignarempresasControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.SPASIGNAREMPRESAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if ((parametrosEntrada != null) && !parametrosEntrada.isEmpty()) {
                ciclo = parametrosEntrada.get("ciclo").toString();
            }
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
        tabla = GenericUrlEnum.SP_USUARIO.getTable();
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaEmpresaAseoExt();
        cargarListaEmpresaAseoExtE();
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
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SpasignarempresasControladorUrlEnum.URL0001
                                                        .getValue());
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SpasignarempresasControladorUrlEnum.URL0002
                                                        .getValue());
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaEmpresaAseoExt
     */
    public void cargarListaEmpresaAseoExt() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SpasignarempresasControladorUrlEnum.URL5683
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaEmpresaAseoExt = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    /**
     * Carga la lista listaEmpresaAseoExt
     */
    public void cargarListaEmpresaAseoExtE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SpasignarempresasControladorUrlEnum.URL5683
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaEmpresaAseoExtE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
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
     * listaEmpresaAseoExt
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEmpresaAseoExt(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("EMPRESAASEOEXT",
                        registroAux.getCampos().get("ID"));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaEmpresaAseoExt
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEmpresaAseoExtE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get("ID").toString();
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
        tituloPrincipal = SysmanFunciones
                        .concatenar(idioma.getString("TB_TB3340"), " ", ciclo);
        tituloSecundario = SysmanFunciones.concatenar(
                        idioma.getString("TB_TB3341"), " ",
                        SessionUtil.getCompaniaIngreso().getNombre());
        try {
            if (ejbServiciosPublicosUtl.obtenerCicloCalculado(compania,
                            Integer.parseInt(ciclo))) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3489"));
                permiteEditar = false;
            }
            else {
                permiteEditar = true;
            }
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
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
     * @return VARIABLE
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     *
     * @return VARIABLE
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
     * @return VARIABLE
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        boolean rta = false;
        try {
            rta = ejbServiciosPublicosUtl.validarEmpresaExterna(compania,
                            Integer.parseInt(ciclo),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.CODIGORUTA
                                                            .getName())
                                            .toString(),
                            registro.getCampos().get("EMPRESAASEOEXT")
                                            .toString(),
                            registro.getCampos().get("CODIGO_EXTERNO")
                                            .toString());
            periodo = registro.getCampos()
                            .get(GeneralParameterEnum.PERIODO.getName())
                            .toString();
            registro.getCampos()
                            .remove(GeneralParameterEnum.COMPANIA.getName());
            registro.getCampos().remove(GeneralParameterEnum.CICLO.getName());
            registro.getCampos().remove(GeneralParameterEnum.PERIODO.getName());
            registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
            registro.getCampos().remove("NOMBREEMPRESA");
            registro.getCampos().remove("ASEOBARRIDO");
            registro.getCampos()
                            .remove(GeneralParameterEnum.CODIGORUTA.getName());
            registro.getCampos().remove("CODIGOINTERNO");
            registro.getCampos().remove(GeneralParameterEnum.ESTADO.getName());
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return rta;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     *
     * @return VARIABLE
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        try {
            ejbServiciosPublicosTres.auditarRegistroComparar(compania,
                            "ASIGNACION EMPRESA ASEO TERCERIZADO ", "TODOS",
                            "EMPRESAASEOEXT,CODIGO_EXTERNO",
                            SessionUtil.getUser().getCodigo(),
                            Integer.parseInt(ciclo),
                            registro.getCampos()
                                            .get("KEY_"
                                                + GeneralParameterEnum.CODIGORUTA
                                                                .getName())
                                            .toString(),
                            Integer.parseInt(registro.getCampos()
                                            .get(GeneralParameterEnum.ANO
                                                            .getName())
                                            .toString()),
                            periodo,
                            1);
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     *
     * @return VARIABLE
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
     * @return VARIABLE
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
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaEmpresaAseoExt
     *
     * @return listaEmpresaAseoExt
     */
    public RegistroDataModelImpl getListaEmpresaAseoExt() {
        return listaEmpresaAseoExt;
    }

    /**
     * Asigna la lista listaEmpresaAseoExt
     *
     * @param listaEmpresaAseoExt
     * Variable a asignar en listaEmpresaAseoExt
     */
    public void setListaEmpresaAseoExt(
        RegistroDataModelImpl listaEmpresaAseoExt) {
        this.listaEmpresaAseoExt = listaEmpresaAseoExt;
    }

    /**
     * Retorna la lista listaEmpresaAseoExt
     *
     * @return listaEmpresaAseoExt
     */
    public RegistroDataModelImpl getListaEmpresaAseoExtE() {
        return listaEmpresaAseoExtE;
    }

    /**
     * Asigna la lista listaEmpresaAseoExt
     *
     * @param listaEmpresaAseoExt
     * Variable a asignar en listaEmpresaAseoExt
     */
    public void setListaEmpresaAseoExtE(
        RegistroDataModelImpl listaEmpresaAseoExtE) {
        this.listaEmpresaAseoExtE = listaEmpresaAseoExtE;
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

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public String getTituloPrincipal() {
        return tituloPrincipal;
    }

    public void setTituloPrincipal(String tituloPrincipal) {
        this.tituloPrincipal = tituloPrincipal;
    }

    public String getTituloSecundario() {
        return tituloSecundario;
    }

    public void setTituloSecundario(String tituloSecundario) {
        this.tituloSecundario = tituloSecundario;
    }

    public boolean isPermiteEditar() {
        return permiteEditar;
    }

    public void setPermiteEditar(boolean permiteEditar) {
        this.permiteEditar = permiteEditar;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}

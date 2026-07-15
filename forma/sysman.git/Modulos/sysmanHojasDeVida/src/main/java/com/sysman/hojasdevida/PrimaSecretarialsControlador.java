/*-
 * PrimaSecretarialsControlador.java
 *
 * 1.0
 * 
 * 21/12/2017
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
import com.sysman.hojasdevida.enums.PrimaSecretarialsControladorEnum;
import com.sysman.hojasdevida.enums.PrimaSecretarialsControladorUrlEnum;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Esta clase es el controlador para el formulario "Prima Servicios"
 * en Access "Nat_SubPrimaSecretarial ", el cual es llamado desde Menu
 * Principal\Hojas De Vida\ Hojas De Vida \Datos Hoja De Vida\Datos
 * Basicos --> Pestaña Adicionales, Botón Primas Secretarial
 *
 * 
 * @version 1.0, 12/04/2018, Se realiza desarrollo a partir del
 * analisis descrito en el tar 1000081152
 * @author eamaya
 * 
 */
@ManagedBean
@ViewScoped
public class PrimaSecretarialsControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Numero de documento de identificacion perteneciente al empleado
     * con el que se esta trabajando
     */
    private String numeroDcto;
    /**
     * Sucursal definida para el empleado con el que se esta
     * trabajando
     */
    private String sucursal;
    /**
     * Codigo asignado al empleado con el que se esta trabajando
     */
    private String codigoPersona;
    /**
     * Identificador del empleado con el que se esta trabajando
     */
    private String idEmpleado;
    /**
     * Estructura que almacena los campos llave del personal con el
     * que se eta trabajando
     */
    private Map<String, Object> ridDatosPersonales;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Listado de registros para el combo de Documento Administrativo
     */
    private RegistroDataModelImpl listatipoDocumentoActo;
    /**
     * Listado de registros para el combo de Documento Administrativo
     * al editar un registro
     */
    private RegistroDataModelImpl listatipoDocumentoActoE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;
    /**
     * Atributo que almacena el nombre del documento seleccionado al
     * editar el registro
     */
    private String nombreDocumento;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de PrimaSecretarialsControlador
     */
    @SuppressWarnings("unchecked")
    public PrimaSecretarialsControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 1546
            numFormulario = GeneralCodigoFormaEnum.PRIMA_SECRETARIALS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                ridDatosPersonales = (Map<String, Object>) parametrosEntrada
                                .get("rid");
                numeroDcto = (String) parametrosEntrada.get("numeroDcto");
                sucursal = (String) parametrosEntrada.get("sucursal");
                idEmpleado = (String) parametrosEntrada.get("idEmpleado");
                codigoPersona = (String) parametrosEntrada.get("codigoPersona");
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
        tabla = PrimaSecretarialsControladorEnum.NAT_PRIMA_SERVICIOS.getValue();
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListatipoDocumentoActo();
        cargarListatipoDocumentoActoE();
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
        parametrosListado.put(GeneralParameterEnum.NUMERO_DCTO.getName(),
                        numeroDcto);
        parametrosListado.put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PrimaSecretarialsControladorUrlEnum.URL004
                                                        .getValue());

        urlCreacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PrimaSecretarialsControladorUrlEnum.URL001
                                                        .getValue());
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PrimaSecretarialsControladorUrlEnum.URL002
                                                        .getValue());
        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PrimaSecretarialsControladorUrlEnum.URL003
                                                        .getValue());
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listatipoDocumentoActo
     *
     */
    public void cargarListatipoDocumentoActo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PrimaSecretarialsControladorUrlEnum.URL005
                                                        .getValue());
        listatipoDocumentoActo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listatipoDocumentoActo
     *
     */
    public void cargarListatipoDocumentoActoE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PrimaSecretarialsControladorUrlEnum.URL006
                                                        .getValue());
        listatipoDocumentoActoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control tipoDocumentoActo en la
     * fila seleccionada dentro de la grilla
     * 
     * Adiciona al registro que se esta editando el codigo y nombre
     * del Tipo Documento seleccionado
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiartipoDocumentoActoC(int rowNum) {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put("PS_TIPODOCUACTO", auxiliar);
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put("TIPOACTOADTIVO", nombreDocumento);
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listatipoDocumentoActo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilatipoDocumentoActo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("PS_TIPODOCUACTO",
                        registroAux.getCampos().get("CODIGO"));
        registro.getCampos().put("TIPOACTOADTIVO",
                        registroAux.getCampos().get("TIPOACTOADTIVO"));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listatipoDocumentoActo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilatipoDocumentoActoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornarString(registroAux, "CODIGO");
        nombreDocumento = retornarString(registroAux, "TIPOACTOADTIVO");
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
     * seleccionado
     * 
     * Recarga el listado del formulario
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return Si el proceso previo a la insercion fue exitoso
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.DP_NUMEDOCU.getName(),
                        numeroDcto);
        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);
        registro.getCampos().put(GeneralParameterEnum.ID_DE_EMPLEADO.getName(),
                        idEmpleado);
        registro.getCampos()
                        .put(PrimaSecretarialsControladorEnum.PS_CODIGOPERSONA
                                        .getValue(), codigoPersona);
        registro.getCampos().remove("TIPOACTOADTIVO");
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return Si el proceso de insercion fue exitoso
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
     * @return Si el proceso previo a la actualizacion fue exitoso
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
     * @return Si el proceso de actualizacion fue exitoso
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
     * @return Si el proceso previo a la eliminacion fue exitoso
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
     * @return Si el proceso de eliminacion fue exitoso
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
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.DP_NUMEDOCU.getName());
        registro.getCampos().remove(GeneralParameterEnum.SUCURSAL.getName());
        registro.getCampos()
                        .remove(GeneralParameterEnum.ID_DE_EMPLEADO.getName());
        registro.getCampos()
                        .remove(PrimaSecretarialsControladorEnum.PS_CODIGOPERSONA
                                        .getValue());
        registro.getCampos()
                        .remove(PrimaSecretarialsControladorEnum.TIPOACTOADTIVO
                                        .getValue());

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     * 
     * Realiza el rediceccionamiento al formulario
     * "Natdatospersonales"(1519)
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("rid", ridDatosPersonales);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.NAT_DATOS_PERSONALES_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
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

    /**
     * Evalua si el campo ingresado por parametro se encuentra nulo
     * dentro del registro que tambien ha sido ingresado por parametro
     * 
     * @param reg
     * Registro en el que se desea evaluar el campo
     * @param campo
     * Campo que se desea consultar
     * @return Cadena vacia o el valor del campo
     */
    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listatipoDocumentoActo
     * 
     * @return listatipoDocumentoActo
     */
    public RegistroDataModelImpl getListatipoDocumentoActo() {
        return listatipoDocumentoActo;
    }

    /**
     * Asigna la lista listatipoDocumentoActo
     * 
     * @param listatipoDocumentoActo
     * Variable a asignar en listatipoDocumentoActo
     */
    public void setListatipoDocumentoActo(
        RegistroDataModelImpl listatipoDocumentoActo) {
        this.listatipoDocumentoActo = listatipoDocumentoActo;
    }

    /**
     * Retorna la lista listatipoDocumentoActo
     * 
     * @return listatipoDocumentoActo
     */
    public RegistroDataModelImpl getListatipoDocumentoActoE() {
        return listatipoDocumentoActoE;
    }

    /**
     * Asigna la lista listatipoDocumentoActo
     * 
     * @param listatipoDocumentoActo
     * Variable a asignar en listatipoDocumentoActo
     */
    public void setListatipoDocumentoActoE(
        RegistroDataModelImpl listatipoDocumentoActoE) {
        this.listatipoDocumentoActoE = listatipoDocumentoActoE;
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

/*-
 * FrmtipoactividadsstsControlador.java
 *
 * 1.0
 * 
 * 29/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.FrmTipoActividadsstsControladorEnum;
import com.sysman.hojasdevida.enums.FrmTipoActividadsstsControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
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
 * Clase para la creación de registros en Tipo Actividad.
 *
 * @version 1.0, 29/12/2017
 * @author fperez
 */
/**
 *
 */
@ManagedBean
@ViewScoped
public class FrmtipoactividadsstsControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el código de la
     * compañía en la cual inició sesión el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesión
     * correspondiente.
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>

    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo CODIGO en el formulario, almacena el texto
     * CODIGO el cual es un campo del registro
     */
    private final String vCodigo;
    /**
     * Atributo que valida si el campo de observacion se hace visible
     * o no
     */
    private boolean mostrarObservacion;

    /**
     * Atributo que valida si el campo de plantilla se hace visible o
     * no
     */
    private boolean mostrarPlantilla;

    /**
     * Atributo que valida si el boton de guardar se hace visible o no
     */
    private boolean mostrarInsertar;

    /**
     * Atributo que almacena el valor del tipo recibido por
     * parametro,este valida por que formulario es llamado
     */
    private String tipo;

    /**
     * Atributo que valida si los campso de codigo y nombre se
     * bloquean o no
     */
    private boolean bloquearCampo;

    /**
     * Implementación del EJB de SysmanUtil para acceder a funciones
     * y/o procedimientos definidos en el paquete PCK_SYSMAN_UTL.
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Atributo que almacena el valor del clase de transaccion del
     * registro seleccionado en el formulario anterior
     */
    private String tipotransaccion;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de Tipo Transacción para inserción de registros.
     */
    private RegistroDataModelImpl listaTipoTransaccion;
    /**
     * Lista de Tipo Transacción para edición de registros.
     */
    private RegistroDataModelImpl listaTipoTransaccionE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se almacena el identificador del registro que se
     * seleccionó.
     */
    private String auxiliar;

    /**
     * Lista de registro de las plantillas para el tipo de actividad
     */
    private RegistroDataModelImpl listaModeloPlantilla;
    /**
     * Lista de registro de las plantillas para el tipo de actividad
     */
    private RegistroDataModelImpl listaModeloPlantillaE;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmtipoactividadsstsControlador.
     */
    public FrmtipoactividadsstsControlador() {
        super();
        compania = SessionUtil.getCompania();
        vCodigo = GeneralParameterEnum.CODIGO.getName();

        Map<String, Object> parametros = SessionUtil.getFlash();
        if (parametros != null) {

            tipotransaccion = (String) parametros.get("transaccion");

        }

        try {
            numFormulario = GeneralCodigoFormaEnum.FRM_TIPO_ACTIVIDADSS_CONTOLADOR
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
     * Este método se ejecuta justo después de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualización del
     * formulario, como son tablas, origenes de datos, inicialización
     * de listas y demás necesarios.
     */
    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.SST_TIPO_ACTIVIDAD;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTipoTransaccion();
        cargarListaTipoTransaccionE();
        cargarListaModeloPlantilla();
        cargarListaModeloPlantillaE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    /**
     * En este método se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. También carga la lista
     * del formulario por primera vez.
     */
    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put("TIPOTRANSACCION", tipotransaccion);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaTipoTransaccion.
     */
    public void cargarListaTipoTransaccion() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmTipoActividadsstsControladorUrlEnum.URL149
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(FrmTipoActividadsstsControladorEnum.COMPANIA.getValue(),
                        compania);

        listaTipoTransaccion = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        vCodigo);
    }

    /**
     * Carga la lista listaTipoTransaccion.
     */
    public void cargarListaTipoTransaccionE() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmTipoActividadsstsControladorUrlEnum.URL149
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(FrmTipoActividadsstsControladorEnum.COMPANIA.getValue(),
                        compania);

        listaTipoTransaccionE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true, vCodigo);
    }

    /**
     * 
     * Carga la lista listaModeloPlantilla
     *
     */
    public void cargarListaModeloPlantilla() {

        HashMap<String, Object> param = new HashMap<>();

        param.put(FrmTipoActividadsstsControladorEnum.TIPO.getValue(), "53");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmTipoActividadsstsControladorUrlEnum.URL221
                                                        .getValue());
        listaModeloPlantilla = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaModeloPlantilla
     */
    public void cargarListaModeloPlantillaE() {
        HashMap<String, Object> param = new HashMap<>();

        param.put(FrmTipoActividadsstsControladorEnum.TIPO.getValue(), "53");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmTipoActividadsstsControladorUrlEnum.URL221
                                                        .getValue());
        listaModeloPlantillaE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Plantilla
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirPlantilla(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        try {
            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.CODIGO.getName(),
                            reg.getCampos().get(
                                            FrmTipoActividadsstsControladorEnum.CODIGO_PLANTILLA
                                                            .getValue()));

            param.put(FrmTipoActividadsstsControladorEnum.TIPO.getValue(),
                            "53");

            param.put(FrmTipoActividadsstsControladorEnum.FECHAGENERACION
                            .getValue(),
                            new Date());

            Registro rs;

            rs = RegistroConverter
                            .toRegistro(requestManager.get(UrlServiceUtil
                                            .getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmTipoActividadsstsControladorUrlEnum.URL300
                                                                            .getValue())
                                            .getUrl(), param));

            Date fecha = (Date) rs.getCampos()
                            .get(GeneralParameterEnum.FECHA.getName());
            String[] campos = new String[3];
            String[] valores = new String[3];
            campos[0] = "codigoPlantilla";
            campos[1] = "fechaPlantilla";
            campos[2] = "nombreDocDescarga";

            valores[0] = String
                            .valueOf(reg.getCampos()
                                            .get(FrmTipoActividadsstsControladorEnum.CODIGO_PLANTILLA
                                                            .getValue()));
            valores[1] = SysmanFunciones.formatearFecha(fecha);
            valores[2] = SysmanFunciones.initCap(String.valueOf(reg.getCampos()
                            .get(GeneralParameterEnum.NOMBRE.getName())));

            HashMap<String, String> variablesConsultaW = new HashMap<>();
            variablesConsultaW.put("s$compania$s",
                            SysmanFunciones.concatenar("'", compania, "'"));
            variablesConsultaW.put("s$tipotransaccion$s",
                            tipotransaccion);
            variablesConsultaW.put("s$codigo$s",
                            reg.getCampos().get(GeneralParameterEnum.CODIGO
                                            .getName()).toString());

            // variables por parametro para documento word
            SessionUtil.setSessionVar("variablesConsultaWord",
                            variablesConsultaW);
            String numForm = String
                            .valueOf(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                            .getCodigo());
            SessionUtil.cargarModalDatosFlash(numForm,
                            SessionUtil.getModulo(),
                            campos, valores);

        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Método ejecutado al seleccionar una fila de la lista
     * listaTipoTransaccion.
     *
     * @param event
     * objeto que encapsula la acción proveniente de la vista.
     */
    public void seleccionarFilaTipoTransaccion(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos()
                        .put(FrmTipoActividadsstsControladorEnum.TIPO_TRANSACCION
                                        .getValue(),
                                        retornarString(registroAux, vCodigo));
    }

    /**
     * Método ejecutado al seleccionar una fila de la lista
     * listaTipoTransaccion.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista.
     */
    public void seleccionarFilaTipoTransaccionE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = retornarString(registroAux, vCodigo);
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaModeloPlantilla
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaModeloPlantilla(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CODIGO_PLANTILLA",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaModeloPlantilla
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaModeloPlantillaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGO
                                                        .getName()),
                                        "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    /**
     * Este método es invocado el método inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario.
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        if ("21080101".equals(SessionUtil.getMenuActual())) {
            mostrarPlantilla = true;
            mostrarObservacion = false;
            mostrarInsertar = true;
            bloquearCampo = false;

        }
        else {
            mostrarObservacion = true;
            mostrarPlantilla = false;
            mostrarInsertar = false;
            bloquearCampo = true;

        }
        /*
         * FR1564-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * formularioAbrir 27, Me.Name End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Método ejecutado cuando se cancela la edición del registro
     * seleccionado.
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Método ejecutado antes de realizar la inserción del registro.
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("TIPO_TRANSACCION", tipotransaccion);
        determinarConsecutivo();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Método ejecutado después de realizar la inserción del registro.
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Método ejecutado antes de realizar la inserción y actualización
     * del registro.
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Método ejecutado después de realizar la inserción y
     * actualización del registro.
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Método ejecutado antes de realizar la eliminación del registro.
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Método ejecutado después de realizar la eliminación del
     * registro.
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este método se ejecuta antes enviar la acción de actualización,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro.
     */
    @Override
    public void removerCombos() {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.CODIGO.getName());
        registro.getCampos().remove("TIPO_TRANSACCION");
        registro.getCampos()
                        .remove(FrmTipoActividadsstsControladorEnum.NOMBRETIPOTX
                                        .getValue());
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario.
     */
    public void cerrarFormulario() {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Método para determinar el código a inertar en el nuevo
     * registro.
     */
    public void determinarConsecutivo() {
        try {
            registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            String criterio = SysmanFunciones.concatenar("COMPANIA = ''",
                            compania, "'' AND TIPO_TRANSACCION =",
                            tipotransaccion);
            long consecutivo = ejbSysmanUtil.generarSiguienteConsecutivo(
                            "SST_TIPO_ACTIVIDAD", criterio,
                            "CODIGO");
            registro.getCampos().put(vCodigo, consecutivo);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Este método es ejecutado después de finalizar la inserción y
     * edición del registro se usa cuando se desean agregar valores al
     * registro después de dichas acciones.
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
    /**
     * Retorna el objeto mostrarObservacion
     * 
     * @return mostrarObservacion
     */
    public boolean isMostrarObservacion() {
        return mostrarObservacion;
    }

    /**
     * Asigna el objeto mostrarObservacion
     * 
     * @param mostrarObservacion
     * Variable a asignar en mostrarObservacion
     */
    public void setMostrarObservacion(boolean mostrarObservacion) {
        this.mostrarObservacion = mostrarObservacion;
    }

    /**
     * Retorna el objeto mostrarPlantilla
     * 
     * @return mostrarPlantilla
     */
    public boolean isMostrarPlantilla() {
        return mostrarPlantilla;
    }

    /**
     * Asigna el objeto mostrarPlantilla
     * 
     * @param mostrarPlantilla
     * Variable a asignar en mostrarPlantilla
     */
    public void setMostrarPlantilla(boolean mostrarPlantilla) {
        this.mostrarPlantilla = mostrarPlantilla;
    }

    /**
     * Retorna el objeto mostrarInsertar
     * 
     * @return mostrarInsertar
     */
    public boolean isMostrarInsertar() {
        return mostrarInsertar;
    }

    /**
     * Asigna el objeto mostrarInsertar
     * 
     * @param mostrarInsertar
     * Variable a asignar en mostrarInsertar
     */
    public void setMostrarInsertar(boolean mostrarInsertar) {
        this.mostrarInsertar = mostrarInsertar;
    }

    /**
     * Retorna el objeto bloquearCampo
     * 
     * @return bloquearCampo
     */
    public boolean isBloquearCampo() {
        return bloquearCampo;
    }

    /**
     * Asigna el objeto bloquearCampo
     * 
     * @param bloquearCampo
     * Variable a asignar en bloquearCampo
     */
    public void setBloquearCampo(boolean bloquearCampo) {
        this.bloquearCampo = bloquearCampo;
    }

    /**
     * Retorna el objeto tipo
     * 
     * @return tipo
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Asigna el objeto tipo
     * 
     * @param tipo
     * Variable a asignar en tipo
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaTipoTransaccion.
     */
    public RegistroDataModelImpl getListaTipoTransaccion() {
        return listaTipoTransaccion;
    }

    /**
     * Asigna la lista listaTipoTransaccion.
     * 
     * @param listaTipoTransaccion.
     * Variable a asignar en listaTipoTransaccion.
     */
    public void setListaTipoTransaccion(
        RegistroDataModelImpl listaTipoTransaccion) {
        this.listaTipoTransaccion = listaTipoTransaccion;
    }

    /**
     * Retorna la lista listaTipoTransaccion.
     * 
     * @return listaTipoTransaccion.
     */
    public RegistroDataModelImpl getListaTipoTransaccionE() {
        return listaTipoTransaccionE;
    }

    /**
     * Retorna la lista listaModeloPlantilla
     * 
     * @return listaModeloPlantilla
     */
    public RegistroDataModelImpl getListaModeloPlantilla() {
        return listaModeloPlantilla;
    }

    /**
     * Asigna la lista listaModeloPlantilla
     * 
     * @param listaModeloPlantilla
     * Variable a asignar en listaModeloPlantilla
     */
    public void setListaModeloPlantilla(
        RegistroDataModelImpl listaModeloPlantilla) {
        this.listaModeloPlantilla = listaModeloPlantilla;
    }

    /**
     * Retorna la lista listaModeloPlantilla
     * 
     * @return listaModeloPlantilla
     */
    public RegistroDataModelImpl getListaModeloPlantillaE() {
        return listaModeloPlantillaE;
    }

    /**
     * Asigna la lista listaModeloPlantilla
     * 
     * @param listaModeloPlantilla
     * Variable a asignar en listaModeloPlantilla
     */
    public void setListaModeloPlantillaE(
        RegistroDataModelImpl listaModeloPlantillaE) {
        this.listaModeloPlantillaE = listaModeloPlantillaE;
    }

    /**
     * Asigna la lista listaTipoTransaccion.
     * 
     * @param listaTipoTransaccion.
     * Variable a asignar en listaTipoTransaccion.
     */
    public void setListaTipoTransaccionE(
        RegistroDataModelImpl listaTipoTransaccionE) {
        this.listaTipoTransaccionE = listaTipoTransaccionE;
    }

    /**
     * Retorna la variable auxiliar.
     * 
     * @return auxiliar.
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * Asigna la variable auxiliar.
     * 
     * @param auxiliar.
     * Variable a asignar en auxiliar.
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}

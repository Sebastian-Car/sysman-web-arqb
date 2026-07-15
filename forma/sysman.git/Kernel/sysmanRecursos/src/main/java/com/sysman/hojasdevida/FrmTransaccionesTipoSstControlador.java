/*-
 * FrmTransaccionesTipoSstControlador.java
 *
 * 1.0
 * 
 * 28/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.hojasdevida.enums.FrmTransaccionesTipoSstControladorEnum;
import com.sysman.hojasdevida.enums.FrmTransaccionesTipoSstControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 * Clase encargada de redirreccionar al formulario transacciones
 *
 * @version 1.0, 28/12/2017
 * @author jeguerrero
 * 
 * @modifier amonroy
 * @version 2, 18/01/2018 Se adiciona el envio de los parametros
 * responsable, comite y claseTransaccion en la redireccion al
 * formulario FrmtransaccionessstsControlador (1563)
 * 
 * * @modifier dnino
 * @version 3, 25/05/2018 Se modifica para redireccionar
 * adicionalmente al formulario cronograma. formulario
 * FrmtransaccionessstsControlador (1563)
 */
@ManagedBean
@ViewScoped
public class FrmTransaccionesTipoSstControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Atributo que almacena el nombre del "Tipo de Transaccion" que
     * ha sido seleccionado
     */
    private String nombreTipocontrato;
    /**
     * Atributo que almacena el título de la ventana modal.
     */
    private String titulo;
    /**
     * Atributo que indica si el "Tipo de Transaccion" que ha sido
     * seleccionado posee empleados a cargo
     */
    private boolean indicadorResponsable;
    /**
     * Atributo que indica si el "Tipo de Transaccion" que ha sido
     * seleccionado es Comite
     */
    private boolean indicadorComite;
    /**
     * Atributo que indicado si el "Tipo de Transaccion" queha sido
     * selecciodo es Agente
     */
    private boolean indicadorAgente;
    /**
     * Atributo que almacena la Clase de Transaccion a la que
     * pertenece el "Tipo de Transaccion" que ha sido seleccionado
     */
    private String claseTransaccion;
    // <DECLARAR_ATRIBUTOS>
    /**
     * variable encargada de almacenar temporalmente el tipo de
     * contrato
     */
    private String tipoContrato;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista encargada de almacenar todos los tipos contratos
     */
    private RegistroDataModelImpl listaTipoContrato;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmTransaccionesTipoSstControlador
     */
    public FrmTransaccionesTipoSstControlador()
    {
        super();
        if ("21080201".equals(SessionUtil.getMenuActual()))
        {
            titulo = "Abrir Transacciones";
        }
        else
        {
            if ("21080202".equals(SessionUtil.getMenuActual()))
            {
                titulo = "Abrir Cronograma";
            }
        }
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMTRANSACCIONESTIPOSST_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
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
    public void inicializar()
    {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTipoContrato();
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
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaTipoContrato Lista encargada de almacenar
     * los datos de respuesta de la base de datos y almacenarlos
     * temporalemtne
     */
    public void cargarListaTipoContrato()
    {

        Map<String, Object> parametros = new HashMap<>();
        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmTransaccionesTipoSstControladorUrlEnum.URL10934
                                                        .getValue());
        listaTipoContrato = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), parametros, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar en la vista
     *
     *
     */
    public void oprimirAceptar()
    {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("tipoTransaccion", tipoContrato);
        parametros.put("nombreTransaccion", nombreTipocontrato);
        parametros.put("responsable", indicadorResponsable);
        parametros.put("comite", indicadorComite);
        parametros.put("agente", indicadorAgente);
        parametros.put("claseTransaccion", claseTransaccion);
        Direccionador direccionador = new Direccionador();
        if ("21080201".equals(SessionUtil.getMenuActual()))
        {
            direccionador.setNumForm(
                            String.valueOf(GeneralCodigoFormaEnum.FRMTRANSACCIONESSSTS_CONTROLADOR
                                            .getCodigo()));
        }
        else
        {
            if ("21080202".equals(SessionUtil.getMenuActual()))
            {
                direccionador.setNumForm(
                                String.valueOf(GeneralCodigoFormaEnum.FRMCRONOGRAMASST_CONTROLADOR
                                                .getCodigo()));
            }
        }
        direccionador.setParametros(parametros);
        direccionador.getRuta();

        RequestContext.getCurrentInstance()
                        .closeDialog(direccionador);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Cancelar en la vista
     *
     *
     */
    public void oprimirCancelar()
    {
        // <CODIGO_DESARROLLADO>
        JsfUtil.ejecutarJavaScript("cerrarModalContinuoModulo();");
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoContrato
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoContrato(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        tipoContrato = retornarString(registroAux,
                        GeneralParameterEnum.CODIGO.getName());
        nombreTipocontrato = retornarString(registroAux,
                        GeneralParameterEnum.NOMBRE.getName());
        indicadorResponsable = (boolean) registroAux.getCampos()
                        .get(FrmTransaccionesTipoSstControladorEnum.IND_REPONSABLE
                                        .getValue());
        indicadorComite = (boolean) registroAux.getCampos()
                        .get(FrmTransaccionesTipoSstControladorEnum.IND_COMITE
                                        .getValue());
        indicadorAgente = (boolean) registroAux.getCampos()
                        .get(FrmTransaccionesTipoSstControladorEnum.IND_AGENTE
                                        .getValue());
        claseTransaccion = retornarString(registroAux,
                        FrmTransaccionesTipoSstControladorEnum.CLASE_TRANSACCION
                                        .getValue());
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable tipoContrato
     * 
     * @return tipoContrato
     */
    public String getTipoContrato()
    {
        return tipoContrato;
    }

    /**
     * Asigna la variable tipoContrato
     * 
     * @param tipoContrato
     * Variable a asignar en tipoContrato
     */
    public void setTipoContrato(String tipoContrato)
    {
        this.tipoContrato = tipoContrato;
    }

    /**
     * @return the titulo
     */
    public String getTitulo()
    {
        return titulo;
    }

    /**
     * @param titulo
     * the titulo to set
     */
    public void setTitulo(String titulo)
    {
        this.titulo = titulo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaTipoContrato
     * 
     * @return listaTipoContrato
     */
    public RegistroDataModelImpl getListaTipoContrato()
    {
        return listaTipoContrato;
    }

    /**
     * Asigna la lista listaTipoContrato
     * 
     * @param listaTipoContrato
     * Variable a asignar en listaTipoContrato
     */
    public void setListaTipoContrato(RegistroDataModelImpl listaTipoContrato)
    {
        this.listaTipoContrato = listaTipoContrato;
    }

    /**
     * Retorna la variable nombreTipocontrato
     * 
     * @return nombreTipocontrato
     */
    public String getNombreTipocontrato()
    {
        return nombreTipocontrato;
    }

    /**
     * Asigna la variable nombreTipocontrato
     * 
     * @param nombreTipocontrato
     * Variable a asignar en nombreTipocontrato
     */
    public void setNombreTipocontrato(String nombreTipocontrato)
    {
        this.nombreTipocontrato = nombreTipocontrato;
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
    private String retornarString(Registro reg, String campo)
    {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();

    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}

/*-
 * TransaccionactividadsControlador.java
 *
 * 1.0
 * 
 * 30/04/2018
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
import com.sysman.hojasdevida.enums.FrmTipoActividadsstsControladorEnum;
import com.sysman.hojasdevida.enums.TransaccionactividadsControladorEnum;
import com.sysman.hojasdevida.enums.TransaccionactividadsControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Clase migrada para eliminar actividad en la transaccion
 *
 * @version 1.0, 30/04/2018
 * @author ybecerra
 */
@ManagedBean
@ViewScoped
public class TransaccionactividadsControlador extends BeanBaseContinuoAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Estructura que almacena los campos llave de la transaccion con
     * la que se esta trabajando
     */
    private Map<String, Object> ridTransaccion;
    /**
     * Valor que se recibe por parametro, almacena la Clase
     * Transaccion que es un campo de la llave principal
     */
    private String claseTransaccion;
    /**
     * Atributo que almacena el nombre de la transaccion que se esta
     * realizando
     */
    private String nombreTransaccion;
    /**
     * Atributo que indica si el "Tipo de Transaccion" que ha sido
     * seleccionado posee empleados a cargo
     */
    private boolean responsable;
    /**
     * Atributo que indica si el "Tipo de Transaccion" que ha sido
     * seleccionado es Comite
     */
    private boolean comite;
    /**
     * Valor que se recibe por parametro, almacena el consecutivo de
     * la Transaccion que es un campo de la llave principal
     */
    private String transaccion;

    /**
     * Atributo que almacena el valor del clase de transaccion del
     * registro seleccionado en el formulario anterior
     */
    private String tipotransaccion;
    /**
     * Atributo que recibe por parametro el valor delindicador del
     * agente del tipo de transaccion
     */
    private boolean indicadorAgente;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de registros de las actividades
     */
    private RegistroDataModelImpl listacodigo;
    /**
     * Lista de registros de las actividades
     */
    private RegistroDataModelImpl listacodigoE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de TransaccionactividadsControlador
     */
    @SuppressWarnings("unchecked")
    public TransaccionactividadsControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            // 1781
            numFormulario = GeneralCodigoFormaEnum.TRANSACCION_ACTIVIDAD
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametros = SessionUtil.getFlash();
            if (parametros != null)
            {
                ridTransaccion = (Map<String, Object>) parametros
                                .get("rid");
                transaccion = parametros.get("consecutivo").toString();
                claseTransaccion = (String) parametros
                                .get("claseTransaccion");
                tipotransaccion = (String) parametros.get("tipoTransaccion");
                responsable = (boolean) parametros.get("responsable");
                comite = (boolean) parametros.get("comite");
                indicadorAgente = (boolean) parametros.get("agente");
                nombreTransaccion = (String) parametros
                                .get("nombreTransaccion");

            }

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

        enumBase = GenericUrlEnum.SST_TRANSACCION_ACTIVIDAD;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListacodigo();
        cargarListacodigoE();
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
    public void reasignarOrigen()
    {
        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado
                        .put(TransaccionactividadsControladorEnum.TIPO_TRANSACCION
                                        .getValue(), tipotransaccion);
        parametrosListado.put(TransaccionactividadsControladorEnum.TRANSACCION
                        .getValue(), transaccion);

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listacodigo
     */
    public void cargarListacodigo()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TransaccionactividadsControladorUrlEnum.URL149
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(TransaccionactividadsControladorEnum.TIPO_TRANSACCION
                        .getValue(),
                        tipotransaccion);

        listacodigo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listacodigo
     */
    public void cargarListacodigoE()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        TransaccionactividadsControladorUrlEnum.URL149
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(TransaccionactividadsControladorEnum.TIPO_TRANSACCION
                        .getValue(),
                        tipotransaccion);

        listacodigoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton plantilla
     * 
     * @param reg
     * registro en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     * @param indice
     * indice en el cual esta ubicado el boton oprimido dentro de la
     * grilla
     */
    public void oprimirplantilla(Registro reg, int indice)
    {
        // <CODIGO_DESARROLLADO>

        if (SysmanFunciones
                        .validarVariableVacio(
                                        SysmanFunciones.nvl(reg.getCampos()
                                                        .get(TransaccionactividadsControladorEnum.CODIGO_PLANTILLA
                                                                        .getValue()),
                                                        "")
                                                        .toString()))
        {

            JsfUtil.agregarMensajeError(idioma.getString("TB_TB4080"));
            return;

        }
        else
        {
            try
            {
                Map<String, Object> param = new TreeMap<>();

                param.put(GeneralParameterEnum.CODIGO.getName(),
                                reg.getCampos().get(
                                                TransaccionactividadsControladorEnum.CODIGO_PLANTILLA
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
                                                                TransaccionactividadsControladorUrlEnum.URL300
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
                                                .get(TransaccionactividadsControladorEnum.CODIGO_PLANTILLA
                                                                .getValue()));
                valores[1] = SysmanFunciones.formatearFecha(fecha);
                valores[2] = SysmanFunciones
                                .initCap(String.valueOf(reg.getCampos()
                                                .get(GeneralParameterEnum.NOMBRE
                                                                .getName())));

                HashMap<String, String> variablesConsultaW = new HashMap<>();
                variablesConsultaW.put("s$compania$s",
                                SysmanFunciones.concatenar("'", compania, "'"));
                variablesConsultaW.put("s$tipotransaccion$s",
                                tipotransaccion);
                variablesConsultaW.put("s$consecutivotransaccion$s",
                                transaccion);
                variablesConsultaW.put("s$consecutivo$s",
                                reg.getCampos().get(
                                                GeneralParameterEnum.ACTIVIDAD
                                                                .getName())
                                                .toString());

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
            catch (SystemException e)
            {
                JsfUtil.agregarMensajeError(e.getMessage());
                logger.error(e.getMessage(), e);

            }
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacodigo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacodigo(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("ACTIVIDAD",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
        registro.getCampos()
                        .put(TransaccionactividadsControladorEnum.TIPO_TRANSACCION
                                        .getValue(),
                                        registroAux.getCampos().get(
                                                        TransaccionactividadsControladorEnum.TIPO_TRANSACCION
                                                                        .getValue()));
        registro.getCampos()
                        .put(TransaccionactividadsControladorEnum.CODIGO_PLANTILLA
                                        .getValue(),
                                        registroAux.getCampos().get(
                                                        TransaccionactividadsControladorEnum.CODIGO_PLANTILLA
                                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacodigo
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacodigoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
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

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos()
                        .put(TransaccionactividadsControladorEnum.TRANSACCION
                                        .getValue(),
                                        transaccion);

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * @return true
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * @return true
     */
    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * @return true
     */
    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * @return true
     */
    @Override
    public boolean eliminarDespues()
    {
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
    public void removerCombos()
    {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos()
                        .remove(TransaccionactividadsControladorEnum.TIPO_TRANSACCION
                                        .getValue());
        registro.getCampos()
                        .remove(TransaccionactividadsControladorEnum.TRANSACCION
                                        .getValue());
        registro.getCampos().remove(GeneralParameterEnum.ACTIVIDAD.getName());
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        registro.getCampos().remove("NOMBRETRANSACCION");

    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     * 
     */
    public void ejecutarrcCerrar()
    {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("nombreTransaccion", nombreTransaccion);
        parametros.put("tipoTransaccion", tipotransaccion);
        parametros.put("responsable", responsable);
        parametros.put("comite", comite);
        parametros.put("claseTransaccion", claseTransaccion);
        parametros.put("agente", indicadorAgente);
        parametros.put("rid", ridTransaccion);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRMTRANSACCIONESSSTS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);

        SessionUtil.redireccionarForma(direccionador,
                        SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */

    @Override
    public void asignarValoresRegistro()
    {
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
     * Retorna la lista listacodigo
     * 
     * @return listacodigo
     */
    public RegistroDataModelImpl getListacodigo()
    {
        return listacodigo;
    }

    /**
     * Asigna la lista listacodigo
     * 
     * @param listacodigo
     * Variable a asignar en listacodigo
     */
    public void setListacodigo(RegistroDataModelImpl listacodigo)
    {
        this.listacodigo = listacodigo;
    }

    /**
     * Retorna la lista listacodigo
     * 
     * @return listacodigo
     */
    public RegistroDataModelImpl getListacodigoE()
    {
        return listacodigoE;
    }

    /**
     * Asigna la lista listacodigo
     * 
     * @param listacodigo
     * Variable a asignar en listacodigo
     */
    public void setListacodigoE(RegistroDataModelImpl listacodigoE)
    {
        this.listacodigoE = listacodigoE;
    }

    /**
     * Retorna la variable auxiliar
     * 
     * @return auxiliar
     */
    public String getAuxiliar()
    {
        return auxiliar;
    }

    /**
     * Asigna la variable auxiliar
     * 
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}

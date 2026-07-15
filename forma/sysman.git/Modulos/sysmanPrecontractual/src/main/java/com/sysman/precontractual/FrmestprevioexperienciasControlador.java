/*-
 * FrmestprevioexperienciasControlador.java
 *
 * 1.0
 * 
 * 28/08/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.precontractual.enums.FrmestprevioexperienciasControladorEnum;
import com.sysman.precontractual.enums.FrmestprevioexperienciasControladorUrlEnum;
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

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * * @author vmolano
 * 
 * @version 1, 26/08/2016
 *
 * @version 2 -- sdaza 01/06/2016 adicionar campos de auditoria del
 * registro
 * 
 * @version 3 -- amonroy 28/08/2017 <br>
 * <t> 1) Se realiza el cambio de apariencia del formulario de Modal -
 * Datos a Continuo, debido a que su apariencia no era entendible para
 * el usuario. <br>
 * <t> 2) Se realiza el Proceso de Refactoring para las operaciones
 * CRUD del formulario y el listado del combo listaCodigobys e
 * implementacion de EJBs
 * 
 */
@ManagedBean
@ViewScoped
public class FrmestprevioexperienciasControlador
                extends BeanBaseContinuoAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida por el numero de veces que se utiliza la
     * palabra "NOMBRE" en el controlador
     */
    private final String cNombre;
    /**
     * Constante definida por el numero de veces que se utiliza la
     * palabra "CODIGO" en el controlador
     */
    private final String cCodigo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el texto digitado por el usuario en el
     * campo "Perfil y Experiencia"
     */
    private String txtExperiencia;
    /**
     * Atributo que gestiona el bloqueo de los controles de creacion,
     * edicion y eliminacion
     */
    private boolean vobo;
    /**
     * Almacena el codigo del estudio en el que se esta trabajando,
     * valor recibido por parametro
     */
    private String codigoEstudio;
    /**
     * Almacena el anio de vigencia del estudio en el que se esta
     * trabajando
     */
    private String vigenciaPeriodo;
    /**
     * Nombre asociado al "Codigo clasificador de bienes y servicios"
     * seleccionado en el formulario
     */
    private String nombreClasificador;
    /**
     * Campos y valores llave del registro en el que se esta
     * trabajando, recibidos por parametro
     */
    private Map<String, Object> codRID;
    /**
     * Implementacion del EJB de SysmanUtil para hacer el llamado a la
     * funcion FC_GENCONSECUTIVO
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Listado para el seleccionar el codigo del clasificador de benes
     * y servicios
     */
    private RegistroDataModelImpl listaCodigobys;
    /**
     * Listado para el seleccionar el codigo del clasificador de benes
     * y servicios cuando se esta editando un registro
     */
    private RegistroDataModelImpl listaCodigobysE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmestprevioexperienciasControlador
     */
    public FrmestprevioexperienciasControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        cNombre = GeneralParameterEnum.NOMBRE.getName();
        cCodigo = GeneralParameterEnum.CODIGO.getName();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMESTPREVIOEXPERIENCIAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null)
            {
                codRID = (HashMap<String, Object>) parametrosEntrada.get("RID");
                codigoEstudio = (String) parametrosEntrada.get("codEstudio");
                txtExperiencia = (String) parametrosEntrada.get("perfil");
                vigenciaPeriodo = (String) parametrosEntrada.get("vigenciaPeriodo");
                vobo = Boolean.parseBoolean(
                                parametrosEntrada.get("vobo").toString());
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
        enumBase = GenericUrlEnum.ES_ESTPREVIO_EXPERIENCIA;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigobys();
        cargarListaCodigobysE();
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
        parametrosListado.put(GeneralParameterEnum.CODIGO.getName(),
                        codigoEstudio);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaCodigobys
     *
     */
    public void cargarListaCodigobys()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmestprevioexperienciasControladorUrlEnum.URL6061
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCodigobys = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    /**
     * 
     * Carga la lista listaCodigobys
     *
     */
    public void cargarListaCodigobysE()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmestprevioexperienciasControladorUrlEnum.URL6500
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCodigobysE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton guardarExperiencia en la
     * vista
     *
     *
     */
    public void oprimirGuardarExperiencia()
    {
        // <CODIGO_DESARROLLADO>

        Map<String, Object> campos = new HashMap<>();
        campos.put(FrmestprevioexperienciasControladorEnum.PYE_CONTRATISTA
                        .getValue(), txtExperiencia);
        campos.put(GeneralParameterEnum.MODIFIED_BY.getName(),
                        SessionUtil.getUser().getCodigo());
        campos.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());

        Map<String, Object> camposLlave = new HashMap<>();
        camposLlave.put(FrmestprevioexperienciasControladorEnum.KEY_COMPANIA
                        .getValue(),
                        codRID.get(FrmestprevioexperienciasControladorEnum.KEY_COMPANIA
                                        .getValue()));
        camposLlave.put(FrmestprevioexperienciasControladorEnum.KEY_COD_ESTUDIO
                        .getValue(),
                        codRID.get(FrmestprevioexperienciasControladorEnum.KEY_COD_ESTUDIO
                                        .getValue()));

        UrlBean urlUpdate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmestprevioexperienciasControladorUrlEnum.URL001
                                                        .getValue());

        try
        {
            requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(),
                            campos,
                            camposLlave);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        JsfUtil.agregarMensajeInformativo(
                        idioma.getString("MSM_REGISTRO_MODIFICADO"));
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Codigobys en la fila
     * seleccionada dentro de la grilla
     * 
     * Actualiza el valor del campo NOMBRE al editar el registro
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarCodigobysC(int rowNum)
    {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put(GeneralParameterEnum.NOMBRE.getName(),
                                        nombreClasificador);
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigobys
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigobys(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("COD_UNSPSC",
                        registroAux.getCampos().get(cCodigo));
        registro.getCampos().put(cNombre,
                        registroAux.getCampos().get(cNombre));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigobys
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigobysE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(cCodigo).toString();
        nombreClasificador = registroAux.getCampos().get(cNombre).toString();
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
     * Se remueven los campos que no pertenecen a la tala principal
     * del formulario, Se calcula el consecutivo para realizar la
     * insercion
     * 
     * @return Verdadero si es posible comtinuar con el proceso de
     * insercion
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(cNombre);
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.COD_ESTUDIO.getName(),
                        codigoEstudio);

        String condicion = SysmanFunciones.concatenar("COMPANIA = ''", compania,
                        "'' AND COD_ESTUDIO = ''", codigoEstudio,
                        "'' AND COD_UNSPSC = ''",
                        registro.getCampos().get("COD_UNSPSC").toString(),
                        "'' ");
        String cConsecutivo = GeneralParameterEnum.CONSECUTIVO.getName();

        try
        {
            long consecutivo = ejbSysmanUtil.generarConsecutivoConValorInicial(
                            FrmestprevioexperienciasControladorEnum.ES_ESTPREVIO_EXPERIENCIA
                                            .getValue(),
                            condicion,
                            cConsecutivo,
                            "1");
            registro.getCampos().put(cConsecutivo, consecutivo);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
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
     */
    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Realiza la redireccion al formulario "Frmestprevioproys"
     */
    public void ejecutarrcCerrar()
    {
        Map<String, Object> parametros = new HashMap<>();
        // Parametros de regreso
        parametros.put("ridEstPrevios", codRID);
        parametros.put("txtCodEstudio", codigoEstudio);
        parametros.put("vigenciaPeriodo", vigenciaPeriodo);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRMESTPREVIOPROYS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     * 
     * Eliminacion de campos que no han sido definidos en el servicio
     * de actualizacion del formulario
     */
    @Override
    public void removerCombos()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.COD_ESTUDIO.getName());
        registro.getCampos().remove(GeneralParameterEnum.CONSECUTIVO.getName());
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
    /**
     * Retorna la variable txtExperiencia
     * 
     * @return txtExperiencia
     */
    public String getTxtExperiencia()
    {
        return txtExperiencia;
    }

    /**
     * Asigna la variable txtExperiencia
     * 
     * @param txtExperiencia
     * Variable a asignar en txtExperiencia
     */
    public void setTxtExperiencia(String txtExperiencia)
    {
        this.txtExperiencia = txtExperiencia;
    }

    /**
     * Retorna la variable vobo
     * 
     * @return vobo
     */
    public boolean isVobo()
    {
        return vobo;
    }

    /**
     * Asigna la variable vobo
     * 
     * @param vobo
     * Variable a asignar en vobo
     */
    public void setVobo(boolean vobo)
    {
        this.vobo = vobo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCodigobys
     * 
     * @return listaCodigobys
     */
    public RegistroDataModelImpl getListaCodigobys()
    {
        return listaCodigobys;
    }

    /**
     * Asigna la lista listaCodigobys
     * 
     * @param listaCodigobys
     * Variable a asignar en listaCodigobys
     */
    public void setListaCodigobys(RegistroDataModelImpl listaCodigobys)
    {
        this.listaCodigobys = listaCodigobys;
    }

    /**
     * Retorna la lista listaCodigobys
     * 
     * @return listaCodigobys
     */
    public RegistroDataModelImpl getListaCodigobysE()
    {
        return listaCodigobysE;
    }

    /**
     * Asigna la lista listaCodigobys
     * 
     * @param listaCodigobys
     * Variable a asignar en listaCodigobys
     */
    public void setListaCodigobysE(RegistroDataModelImpl listaCodigobysE)
    {
        this.listaCodigobysE = listaCodigobysE;
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

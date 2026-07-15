/*-
 * PlusvaliaHechoBeneficiarioControlador.java
 *
 * 1.0
 * 
 * 13/03/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.plusvalia;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.plusvalia.ejb.EjbPlusvaliaCeroRemote;
import com.sysman.plusvalia.enums.PlusvaliaBeneficiariosControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @version 1.0, 13/03/2019
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class PlusvaliaHechoBeneficiarioControlador
                extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private BigInteger idProyecto;
    private String codigoProyecto;
    private String claseProyecto;
    private String nombreClase;
    private String claseVP;
    private String ipCodigo;
    private String numeroOrden;
    private BigInteger idBeneficiario;
    private Map<String, Object> ridProyecto;
    private Map<String, Object> parametrosEntrada;
    private int acuerdoInicial;
    private int acuerdoFinal;
    private String totalAcuerdo;
    private boolean hechoEspecial;
    private int anioBase;
    private int mesBase;
    private String totalIPC;

    private boolean varVolver;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaHechogenerador;
    private RegistroDataModelImpl listaAcuerdoInicial;
    private RegistroDataModelImpl listaAcuerdoFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbPlusvaliaCeroRemote ejbPlusvaliaCeroRemote;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de
     * PlusvaliaHechoBeneficiarioControlador
     */
    public PlusvaliaHechoBeneficiarioControlador() {
        super();
        compania = SessionUtil.getCompania();

        claseVP = "44";
        parametrosEntrada = SessionUtil.getFlash();
        if (parametrosEntrada != null) {
            idProyecto = (BigInteger) parametrosEntrada
                            .get("idProyecto");
            codigoProyecto = (String) parametrosEntrada
                            .get("codigoProyecto");
            claseProyecto = (String) parametrosEntrada.get("claseProyecto");
            ipCodigo = (String) parametrosEntrada.get("ipCodigo");
            numeroOrden = (String) parametrosEntrada.get("numeroOrden");
            idBeneficiario = (BigInteger) parametrosEntrada
                            .get("idBeneficiario");

            ridProyecto = (Map<String, Object>) parametrosEntrada.get("rid");
        }
        try {
            // 2043
            numFormulario = GeneralCodigoFormaEnum.PLUSVALIA_HECHO_BENEFICIARIO_CONTROLADOR
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
     * Retorna la variable varVolver
     * 
     * @return var
     */
    public boolean isVarVolver() {
        return varVolver;
    }

    /**
     * Asigna la variable varVolver
     * 
     * @param var
     * Variable a asignar en varVolver
     */
    public void setVarVolver(boolean varVolver) {
        this.varVolver = varVolver;
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaHechogenerador();
        cargarListaAcuerdoInicial();

        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
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

        enumBase = GenericUrlEnum.VP_HECHOS_BENEFICIARIOS;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la
     * consulta correspondiente del formulario
     * 
     * 
     */
    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosListado.put(GeneralParameterEnum.PROYECTO.getName(),
                        idProyecto);

        parametrosListado.put("BENEFICIARIO",
                        idBeneficiario);

        parametrosListado.put("PAGTAMANIO", 5);
    }

    /**
     * Metodo ejecutado desde un comando remoto en el boton volver del
     * formulario
     * 
     */
    public void ejecutarrcVolver() {
        // <CODIGO_DESARROLLADO>
        if (varVolver) {
            accion = null;
            varVolver = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaHechogenerador
     *
     */
    public void cargarListaHechogenerador() {

        Map<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.PROYECTO.getName(), idProyecto);
        param.put(GeneralParameterEnum.CLASE.getName(), claseVP);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlusvaliaBeneficiariosControladorUrlEnum.URL1776
                                                        .getValue());

        listaHechogenerador = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaAcuerdoInicial
     *
     */
    public void cargarListaAcuerdoInicial() {

        Map<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlusvaliaBeneficiariosControladorUrlEnum.URL1791001
                                                        .getValue());

        listaAcuerdoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "ID");

    }

    /**
     * 
     *
     */
    public void cargarListaAcuerdoFinal() {

        Map<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("ACUERDO_INICIAL", acuerdoInicial);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlusvaliaBeneficiariosControladorUrlEnum.URL1791003
                                                        .getValue());

        listaAcuerdoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "ID");

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaHechogenerador
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaHechogenerador(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CODIGO_HECHO",
                        registroAux.getCampos().get("CODIGO"));

        registro.getCampos().put("ID_HECHOS_PROYECTOS",
                        registroAux.getCampos().get("ID"));

        registro.getCampos().put("NOMBRE",
                        registroAux.getCampos().get("NOMBRE"));

        registro.getCampos().put("TARIFA",
                        registroAux.getCampos().get("TARIFA"));

        registro.getCampos().put("BASE_PLUSVALIA",
                        registroAux.getCampos().get("BASE_PLUSVALIA"));

        registro.getCampos().put("BASE_CONTRIBUCION",
                        registroAux.getCampos().get("BASE_CONTRIBUCION"));
        
        registro.getCampos().put("BASE_LIQUIDACION",
                registroAux.getCampos().get("BASE_LIQUIDACION"));
        
        registro.getCampos().put("DIVIDE_ACUERDO",
                registroAux.getCampos().get("DIVIDE_ACUERDO"));
        
        

        BigInteger codigo = (BigInteger) SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "");

        anioBase = (Integer) SysmanFunciones
                        .nvl(registroAux.getCampos().get("ANO_BASE"), "");

        mesBase = (Integer) SysmanFunciones
                        .nvl(registroAux.getCampos().get("MES_BASE"), "");

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAcuerdoInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAcuerdoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        SysmanFunciones
                        .nvl(registro.getCampos().put("ACUERDO_INICIAL",
                                        registroAux.getCampos().get("ID")), "");

        acuerdoInicial = (Integer) SysmanFunciones
                        .nvl(registroAux.getCampos().get("ID"), "");

        ejecutarCalculo(acuerdoInicial);

        registro.getCampos().put("CAMPO1", totalAcuerdo);

        cargarListaAcuerdoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaAcuerdoFinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaAcuerdoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("ACUERDO_FINAL",
                        registroAux.getCampos().get("ID"));

        acuerdoFinal = (Integer) SysmanFunciones
                        .nvl(registroAux.getCampos().get("ID"), "");

        ejecutarCalculo(acuerdoFinal);

        registro.getCampos().put("CAMPO2", totalAcuerdo);
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>

    public void ejecutarCalculo(int acuerdo) {
        try {
            totalAcuerdo = ejbPlusvaliaCeroRemote.calcularAcuerdos(compania,
                            Long.parseLong(idProyecto.toString()),
                            Long.parseLong(idBeneficiario.toString()), acuerdo)
                            .toString();
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_ADICIONALES>
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
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();


        hechoEspecial = (Boolean) SysmanFunciones
                .nvl(registro.getCampos().get("DIVIDE_ACUERDO"), false);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().put("ID_BENEFICIARIOS", idBeneficiario);
        registro.getCampos().put("ID_PROYECTOS", idProyecto);
        registro.getCampos().put("CODIGO_PROYECTO", codigoProyecto);
        registro.getCampos().put("CLASE", claseVP);
        registro.getCampos().put("IP_CODIGO", ipCodigo);
        registro.getCampos().put("IP_NUMERO_ORDEN", numeroOrden);
       
        registro.getCampos().remove("PRODESC");
        registro.getCampos().remove("CAMPO1");
        registro.getCampos().remove("CAMPO2");
        registro.getCampos().remove("ACUERDO_INICIAL");
        registro.getCampos().remove("ACUERDO_FINAL");
        registro.getCampos().remove("TARIFA");
        registro.getCampos().remove("BASE_PLUSVALIA");
        registro.getCampos().remove("BASE_LIQUIDACION");
        registro.getCampos().remove("DIVIDE_ACUERDO");
        registro.getCampos().remove("ID");
        // </CODIGO_DESARROLLADO>
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
     * 
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().put("ID_BENEFICIARIOS", idBeneficiario);
        registro.getCampos().put("ID_PROYECTOS", idProyecto);
        registro.getCampos().put("CODIGO_PROYECTO", codigoProyecto);
        registro.getCampos().put("CLASE", claseVP);
        registro.getCampos().put("IP_CODIGO", ipCodigo);
        registro.getCampos().put("IP_NUMERO_ORDEN", numeroOrden);
        if(!registro.getCampos().get("CAMPO1").equals("0")
        		&& !registro.getCampos().get("CAMPO2").equals("0")) {
        	registro.getCampos().put("BASE_CONTRIBUCION",
            		new BigDecimal(String.valueOf(registro.getCampos().get("CAMPO2"))).subtract(
            				new BigDecimal((String.valueOf(registro.getCampos().get("CAMPO1"))))));
        }
        registro.getCampos().remove("PRODESC");
        registro.getCampos().remove("TARIFA");
        registro.getCampos().remove("BASE_PLUSVALIA");
        registro.getCampos().remove("BASE_LIQUIDACION");
        registro.getCampos().remove("DIVIDE_ACUERDO");
        registro.getCampos().remove("ID");
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
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaHechogenerador
     * 
     * @return listaHechogenerador
     */
    public RegistroDataModelImpl getListaHechogenerador() {
        return listaHechogenerador;
    }

    /**
     * Asigna la lista listaHechogenerador
     * 
     * @param listaHechogenerador
     * Variable a asignar en listaHechogenerador
     */
    public void setListaHechogenerador(
        RegistroDataModelImpl listaHechogenerador) {
        this.listaHechogenerador = listaHechogenerador;
    }

    /**
     * @return the listaAcuerdoInicial
     */
    public RegistroDataModelImpl getListaAcuerdoInicial() {
        return listaAcuerdoInicial;
    }

    /**
     * @param listaAcuerdoInicial
     * the listaAcuerdoInicial to set
     */
    public void setListaAcuerdoInicial(
        RegistroDataModelImpl listaAcuerdoInicial) {
        this.listaAcuerdoInicial = listaAcuerdoInicial;
    }

    /**
     * @return the listaAcuerdoFinal
     */
    public RegistroDataModelImpl getListaAcuerdoFinal() {
        return listaAcuerdoFinal;
    }

    /**
     * @param listaAcuerdoFinal
     * the listaAcuerdoFinal to set
     */
    public void setListaAcuerdoFinal(RegistroDataModelImpl listaAcuerdoFinal) {
        this.listaAcuerdoFinal = listaAcuerdoFinal;
    }

    /**
     * @return the hechoEspecial
     */
    public boolean isHechoEspecial() {
        return hechoEspecial;
    }

    /**
     * @param hechoEspecial
     * the hechoEspecial to set
     */
    public void setHechoEspecial(boolean hechoEspecial) {
        this.hechoEspecial = hechoEspecial;
    }

    /**
     * @return the anioBase
     */
    public int getAnioBase() {
        return anioBase;
    }

    /**
     * @param anioBase
     * the anioBase to set
     */
    public void setAnioBase(int anioBase) {
        this.anioBase = anioBase;
    }

    /**
     * @return the mesBase
     */
    public int getMesBase() {
        return mesBase;
    }

    /**
     * @param mesBase
     * the mesBase to set
     */
    public void setMesBase(int mesBase) {
        this.mesBase = mesBase;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}

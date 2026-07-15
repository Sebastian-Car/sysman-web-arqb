/*-
 * PlusvaliaAcuerdoControlador.java
 *
 * 1.0
 * 
 * 11/03/2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.plusvalia;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
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
import com.sysman.plusvalia.enums.PlusvaliaProyectosControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 *
 * @version 1.0, 11/03/2019
 * @author bcardenas
 */
@ManagedBean
@ViewScoped
public class PlusvaliaAcuerdoControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    private BigInteger idProyecto;
    private String codigoProyecto;
    private String claseProyecto;
    private Map<String, Object> ridProyecto;
    private String Clase;
    private RegistroDataModelImpl listaTipoComprobante;

    private int anio;
    private int mes;
    private Date fechaInicial;
    private Date fechaFinal;

    private List<Registro> listaAnio;
    private List<Registro> listaMes;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de PlusvaliaAcuerdoControlador
     */
    public PlusvaliaAcuerdoControlador() {
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        Clase = "44";

        Map<String, Object> parametros = SessionUtil.getFlash();
        if (parametros != null) {
            idProyecto = (BigInteger) parametros.get("idProyecto");
            codigoProyecto = parametros
                            .get("codigoProyecto").toString();
            claseProyecto = (String) parametros
                            .get("claseProyecto");
            rid = (Map<String, Object>) parametros
                            .get("rid");
        }

        try {
            // 2038
            numFormulario = GeneralCodigoFormaEnum.PLUSVALIA_ACUERDO_CONTROLADOR
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
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTipoComprobante();
        cargarListaAnio();
        cargarListaMes();
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


        tabla = GenericUrlEnum.VP_PROYECTOS.getTable();
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

        urlLectura = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        PlusvaliaProyectosControladorUrlEnum.URL00R
                                        .getValue());
        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        PlusvaliaProyectosControladorUrlEnum.URL00G
                                        .getValue());

        urlCreacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlusvaliaProyectosControladorUrlEnum.URL00C
                                                        .getValue());

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlusvaliaProyectosControladorUrlEnum.URL00U
                                                        .getValue());

        urlEliminacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlusvaliaProyectosControladorUrlEnum.URL00D
                                                        .getValue());
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosListado.put("CLASEVP", Clase);
        parametrosListado.put(GeneralParameterEnum.CLASE.getName(), Clase);

    }

    // <METODOS_CARGAR_LISTA>

    /**
     * 
     * Carga la lista listaTipoComprobante
     *
     */
    public void cargarListaTipoComprobante() {

        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PlusvaliaProyectosControladorUrlEnum.URL1022
                                                        .getValue());

        listaTipoComprobante = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaAnio
     *
     */
    public void cargarListaAnio() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnio = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PlusvaliaProyectosControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(), param));

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
  
    }
    
    /**
     * 
     * Carga la lista listaMes
     *
     */
public void cargarListaMes(){
 
 Map<String, Object> param = new TreeMap<>();
 param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
 param.put(GeneralParameterEnum.ANO.getName(), anio);
 try {
	 
	 listaMes = RegistroConverter.toListRegistro(
                     requestManager.getList(UrlServiceUtil.getInstance()
                                     .getUrlServiceByUrlByEnumID(
                                    		 PlusvaliaProyectosControladorUrlEnum.URL0002
                                                                     .getValue())
                                     .getUrl(), param));
 }
 catch (SystemException e) {
     logger.error(e.getMessage(), e);
     JsfUtil.agregarMensajeError(e.getMessage());
 }
}

    public void cambiarAnio() {
        // <CODIGO_DESARROLLADO>
        cargarListaMes();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoComprobante
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoComprobante(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("TIPO_COMPROBANTE",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Beneficiarios en la vista
     *
     *
     */
    public void oprimirBeneficiarios() {
        // <CODIGO_DESARROLLADO>
        if (css == null) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2634"));
            return;
        }
        Map<String, Object> param = new HashMap<>();
        param.put("idProyecto", registro.getCampos().get("ID"));
        param.put("codigoProyecto", registro.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
        param.put("claseProyecto", Clase);
        param.put("rid", css);

        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(param);

        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.PLUSVALIA_BENEFICIARIOS_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, modulo);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Conceptos en la vista
     *
     *
     */
    public void oprimirConceptos() {
        // <CODIGO_DESARROLLADO>
        if (css == null) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2634"));
            return;
        }

        Map<String, Object> param = new HashMap<>();
        param.put("idProyecto", registro.getCampos().get("ID"));
        param.put("codigoProyecto",
                        registro.getCampos().get("CODIGO"));
        param.put("claseProyecto",
                        Clase);
        param.put("rid", css);

        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(param);

        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.PLUSVALIA_CONCEPTOS_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, modulo);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Anexos en la vista
     *
     *
     */
    public void oprimirAnexos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton HechoGenerador en la vista
     *
     *
     */
    public void oprimirHechoGenerador() {
        // <CODIGO_DESARROLLADO>


        if (css == null) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2634"));
            return;
        }

        Map<String, Object> param = new HashMap<>();
        param.put("idProyecto", registro.getCampos().get("ID"));
        param.put("codigoProyecto",
                        registro.getCampos().get("CODIGO"));
        param.put("claseProyecto",
                        Clase);
        param.put("rid", css);

        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(param);

        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.PLUSVALIA_HECHO_GENERADOR_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        if ((rid != null) && !rid.isEmpty()) {
            cargarRegistro(rid, ACCION_MODIFICAR);
        }

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
        if (css == null) {
            anio = SysmanFunciones.ano(new Date());
            mes = SysmanFunciones.mes(new Date());

            registro.getCampos().put("FECHA_INICIAL_PROYECTO", new Date());
            registro.getCampos().put("FECHA_FINAL_PROYECTO", new Date());
            registro.getCampos().put("ANO_BASE", anio);
            registro.getCampos().put("MES_BASE", mes);
            registro.getCampos().put("PORCENTAJE_DESCUENTO", "0");
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
        registro.getCampos().put("CLASE", Clase);
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
        registro.getCampos().remove("ID");
        registro.getCampos().remove("NOMBREACTIVIDAD");
        registro.getCampos().remove("NOMBRECLASE");
        registro.getCampos().put("CLASE", Clase);
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
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        SessionUtil.cleanFlash();
        SessionUtil.redireccionarMenu();
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la lista listaAnio
     * 
     * @return listaAnio
     */
    public List<Registro> getListaAnio() {
        return listaAnio;
    }

    /**
     * Asigna la lista listaAnio
     * 
     * @param listaAnio
     * Variable a asignar en listaAnio
     */
    public void setListaAnio(List<Registro> listaAnio) {
        this.listaAnio = listaAnio;
    }
    /**
     * Retorna la lista listaMes
     * 
     * @return listaMes
     */
public List<Registro> getListaMes() {
        return listaMes;
    }
    /**
     * Asigna la lista listaMes
     * 
     * @param listaMes
     * Variable a asignar en  listaMes
     */
public void setListaMes(List<Registro> listaMes) {
        this.listaMes = listaMes;
    }

    /**
     * @return the anio
     */
    public int getAnio() {
        return anio;
    }

    /**
     * @param anio
     * the anio to set
     */
    public void setAnio(int anio) {
        this.anio = anio;
    }

    /**
     * @return the mes
     */
    public int getMes() {
        return mes;
    }

    /**
     * @param mes
     * the mes to set
     */
    public void setMes(int mes) {
        this.mes = mes;
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
     * @return the fechaFinal
     */
    public Date getFechaFinal() {
        return fechaFinal;
    }

    /**
     * @param fechaFinal
     * the fechaFinal to set
     */
    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    /**
     * @return the listaTipoComprobante
     */
    public RegistroDataModelImpl getListaTipoComprobante() {
        return listaTipoComprobante;
    }

    /**
     * @param listaTipoComprobante
     * the listaTipoComprobante to set
     */
    public void setListaTipoComprobante(
        RegistroDataModelImpl listaTipoComprobante) {
        this.listaTipoComprobante = listaTipoComprobante;
    }
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}

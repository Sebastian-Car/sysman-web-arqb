/*-
 * EstadosConsolidadaControlador.java
 *
 * 1.0
 * 
 * 26/11/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.ejb.EjbContabilidadSieteRemote;
import com.sysman.contabilidad.enums.EstadosConsolidadaControladorEnum;
import com.sysman.contabilidad.enums.EstadosConsolidadaControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
/**
 * Permite configurar los estados de las compañías consolidadas.
 *
 * @version 1.0, 26/11/2018
 * @author asana
 */
@ManagedBean
@ViewScoped
public class  EstadosConsolidadaControlador extends BeanBaseModal{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ;
    //<DECLARAR_ATRIBUTOS>
    /**
     * Carga el año que configurará el estado
     */
    private int anio;
    /**
     * Carga el mes Inicial que configurará el estado
     */
    private int mesInicial;
    /**
     * Carga el mes Final que configurará el estado
     */
    private int mesFinal;
    /**
     * Carga la compania que configurará el estado
     */
    private String companias;
    //</DECLARAR_ATRIBUTOS>
    //<DECLARAR_PARAMETROS>
    //</DECLARAR_PARAMETROS>
    //<DECLARAR_LISTAS>
    /**
     * Carga la lista del año que configurará el estado
     */
    private List<Registro> listaano;
    /**
     * Carga lista del mes Inicial que configurará el estado
     */
    private List<Registro> listamesInicial;
    /**
     * Carga lista del mes Final que configurará el estado
     */
    private List<Registro> listamesFinal;
    
    Map<String, Object> parametroEntrada;
    //</DECLARAR_LISTAS>
    //<DECLARAR_LISTAS_COMBO_GRANDE>
    //</DECLARAR_LISTAS_COMBO_GRANDE>
    
    @EJB
    private EjbContabilidadSieteRemote ejbContabilidadSiete;
    /**
     * Crea una nueva instancia de EstadosConsolidadaControlador
     */
    public EstadosConsolidadaControlador() {
        super();
        parametroEntrada = SessionUtil.getFlash();
        compania = SessionUtil.getCompania();
        try {

            //1996
            numFormulario=GeneralCodigoFormaEnum.CONFIG_ESTADOS_CONSOLIDADA_CONTROLADOR.getCodigo();
            validarPermisos();
            
            if (parametroEntrada != null) {
                companias = (String) parametroEntrada.get("companias");
                anio = (int) parametroEntrada.get("ano");
            }
            //<INI_ADICIONAL>       
            //</INI_ADICIONAL>
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
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
    public void inicializar(){
        //<CARGAR_LISTA>
        cargarListaano();
        cargarListamesInicial();
        cargarListamesFinal();
        //</CARGAR_LISTA>
        //<CARGAR_LISTA_COMBO_GRANDE>
        //</CARGAR_LISTA_COMBO_GRANDE>
        //<CREAR_ARBOLES>
        //</CREAR_ARBOLES>
        abrirFormulario();
    }
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario(){
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
    }
    //<METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaano
     *
     */
    public void cargarListaano(){
        
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try
        {
            listaano = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(EstadosConsolidadaControladorUrlEnum.URL5742
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    /**
     * 
     * Carga la lista listamesInicial
     *
     */
    public void cargarListamesInicial(){
        
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        try
        {
            listamesInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EstadosConsolidadaControladorUrlEnum.URL6376
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        
    }
    
    public void cambiarmesInicial() {
        cargarListamesFinal();
    }
    /**
     * 
     * Carga la lista listamesFinal
     *
     */
    public void cargarListamesFinal(){
        
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);
        param.put(EstadosConsolidadaControladorEnum.PARAM3.getValue(),
                        mesInicial);

        try
        {
            listamesFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EstadosConsolidadaControladorUrlEnum.URL5173
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    //</METODOS_CARGAR_LISTA>
    //<METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Activar
     * en la vista
     *
     *
     */
    public void oprimirActivar() {
        //<CODIGO_DESARROLLADO>
        BigDecimal cantCompanias = new BigDecimal(0);
        try {
            
            
            cantCompanias = ejbContabilidadSiete.actualizarEstadoConsol(compania,anio, mesInicial, mesFinal,
                                    SessionUtil.getUser().toString(), Integer.parseInt(SessionUtil.getModulo()), "A");
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4262").replace("$#cantidad#$", String.valueOf(cantCompanias)));
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeInformativo(e.getMessage());
        }
        
        //</CODIGO_DESARROLLADO>
    }
    /**
     * 
     * Metodo ejecutado al oprimir el boton Cerrar
     * en la vista
     *
     */
    public void oprimirCerrar() {
        //<CODIGO_DESARROLLADO>
        try {
            BigDecimal cantCompanias;
            
            cantCompanias = ejbContabilidadSiete.actualizarEstadoConsol(compania,anio, mesInicial, mesFinal,
                                    SessionUtil.getUser().toString(), Integer.parseInt(SessionUtil.getModulo()), "C");
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4262").replace("$#cantidad#$", String.valueOf(cantCompanias)));
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeInformativo(e.getMessage());
        }
        //</CODIGO_DESARROLLADO>
    }
    //</METODOS_BOTONES>
    //<METODOS_CAMBIAR>
    //</METODOS_CAMBIAR>
    //<METODOS_COMBOS_GRANDES>
    //</METODOS_COMBOS_GRANDES>
    //<METODOS_ARBOL>
    //</METODOS_ARBOL>
    //<SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable anio
     * 
     * @return  anio
     */
    public int getAnio() {
        return anio;
    }
    /**
     * Asigna la variable  anio
     * 
     * @param  anio
     * Variable a asignar en  anio
     */
    public void setAnio(int anio) {
        this.anio = anio;
    }
    /**
     * Retorna la variable mesInicial
     * 
     * @return  mesInicial
     */
    public int getMesInicial() {
        return mesInicial;
    }
    /**
     * Asigna la variable  mesInicial
     * 
     * @param  mesInicial
     * Variable a asignar en  mesInicial
     */
    public void setMesInicial(int mesInicial) {
        this.mesInicial = mesInicial;
    }
    /**
     * Retorna la variable mesFinal
     * 
     * @return  mesFinal
     */
    public int getMesFinal() {
        return mesFinal;
    }
    /**
     * Asigna la variable  mesFinal
     * 
     * @param  mesFinal
     * Variable a asignar en  mesFinal
     */
    public void setMesFinal(int mesFinal) {
        this.mesFinal = mesFinal;
    }
    /**
     * Retorna la variable companias
     * 
     * @return  companias
     */
    public String getCompanias() {
        return companias;
    }
    /**
     * Asigna la variable  companias
     * 
     * @param  companias
     * Variable a asignar en  companias
     */
    public void setCompanias(String companias) {
        this.companias = companias;
    }
    //</SET_GET_ATRIBUTOS>
    //<SET_GET_PARAMETROS>
    //</SET_GET_PARAMETROS>
    //<SET_GET_LISTAS>
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
     * Variable a asignar en  listaano
     */
    public void setListaano(List<Registro> listaano) {
        this.listaano = listaano;
    }
    /**
     * Retorna la lista listamesInicial
     * 
     * @return listamesInicial
     */
    public List<Registro> getListamesInicial() {
        return listamesInicial;
    }
    /**
     * Asigna la lista listamesInicial
     * 
     * @param listamesInicial
     * Variable a asignar en  listamesInicial
     */
    public void setListamesInicial(List<Registro> listamesInicial) {
        this.listamesInicial = listamesInicial;
    }
    /**
     * Retorna la lista listamesFinal
     * 
     * @return listamesFinal
     */
    public List<Registro> getListamesFinal() {
        return listamesFinal;
    }
    /**
     * Asigna la lista listamesFinal
     * 
     * @param listamesFinal
     * Variable a asignar en  listamesFinal
     */
    public void setListamesFinal(List<Registro> listamesFinal) {
        this.listamesFinal = listamesFinal;
    }
    //</SET_GET_LISTAS>
    //<SET_GET_LISTAS_COMBO_GRANDE>	
    //</SET_GET_LISTAS_COMBO_GRANDE>
}

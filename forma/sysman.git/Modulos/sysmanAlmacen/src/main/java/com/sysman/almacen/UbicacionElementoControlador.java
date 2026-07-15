/*-
 * UbicacionElemento.java
 *
 * 1.0
 * 
 * 26/09/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;
import com.sysman.almacen.enums.UbicacionElementoControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
/**
 * Permite configurar ubicacion del elemento.
 *
 * @version 1.0, 26/09/2018
 * @author asana
 */
@ManagedBean
@ViewScoped
public class  UbicacionElementoControlador extends BeanBaseModal{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ;
    //<DECLARAR_ATRIBUTOS>
    /**
     * almacena pais
     */
    private String pais;
    /**
     * almacena departamento
     */
    private String departamento;
    /**
     * almacena ciudad
     */
    private String ciudad;
    /**
     * almacena ubicacion
     */
    private String ubicacion;
    
    private String movimiento;
    private String tipoMovimiento;
    private String codigo;
    private String elemento;
    private String serie;
    
    private String ciudadNombre;
    private String paisNombre;
    private String departamentoNombre;
    private String tipoEstacionAdic;
    private boolean bloqueo;
    
    //</DECLARAR_ATRIBUTOS>
    //<DECLARAR_PARAMETROS>
    //</DECLARAR_PARAMETROS>
    //<DECLARAR_LISTAS>
    //</DECLARAR_LISTAS>
    //<DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Almacena la lista del país
     */
    private RegistroDataModelImpl listapais;
    /**
     * Almacena la lista del departamento
     */
    private RegistroDataModelImpl listadepartamento;
    /**
     * Almacena la lista del ciudad
     */
    private RegistroDataModelImpl listaciudad;
    
    Map<String, Object> parametrosMovimientos;
    //</DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de UbicacionElemento
     */
    public UbicacionElementoControlador() {
        super();
        compania = SessionUtil.getCompania();
        parametrosMovimientos = SessionUtil.getFlash();
        try {
            numFormulario = GeneralCodigoFormaEnum.UBICACIONELEMENTO_CONTROLADOR.getCodigo();
            validarPermisos();
            
            if (parametrosMovimientos != null) {
                movimiento = (String) parametrosMovimientos.get("tipoMovimiento");
                tipoMovimiento = (String) parametrosMovimientos.get("movimiento");
                codigo = (String) parametrosMovimientos.get("codigo");
                elemento = (String) parametrosMovimientos.get("elemento");
                serie = (String) parametrosMovimientos.get("serie");
                bloqueo = (boolean) parametrosMovimientos.get("bloqueo");
                                
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
        //</CARGAR_LISTA>
        //<CARGAR_LISTA_COMBO_GRANDE>
        
        
        
        //</CARGAR_LISTA_COMBO_GRANDE>
        //<CREAR_ARBOLES>
        //</CREAR_ARBOLES>
        
        if(bloqueo) {
            try {
          
        Map<String, Object> param = new HashMap<>();
        
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ELEMENTO.getName(), elemento);
        param.put(GeneralParameterEnum.SERIE.getName(), serie);
        
            Registro registro = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            UbicacionElementoControladorUrlEnum.URL10009
                                                                            .getValue())
                                            .getUrl(), param));
            
            pais = registro.getCampos().get("PAIS").toString();
            departamento = registro.getCampos().get("DEPARTAMENTO").toString();
            ciudad = registro.getCampos().get("CIUDAD").toString();
            paisNombre = registro.getCampos().get("NOMBREPAIS").toString();
            departamentoNombre = registro.getCampos().get("NOMBREDEPARTAMENTO").toString();
            ciudadNombre = registro.getCampos().get("NOMBRECIUDAD").toString();
            ubicacion = registro.getCampos().get(GeneralParameterEnum.DIRECCION.getName()).toString();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        } else {
            pais = SessionUtil.getCompaniaIngreso().getCodigoPais();
            departamento = SessionUtil.getCompaniaIngreso().getCodigoDepartamento();
            ciudad = SessionUtil.getCompaniaIngreso().getCodigoCiudad();
            paisNombre = SessionUtil.getCompaniaIngreso().getCiudad();
            departamentoNombre = SessionUtil.getCompaniaIngreso().getDepartamento();
            ciudadNombre = SessionUtil.getCompaniaIngreso().getCiudad();
            ubicacion = SessionUtil.getCompaniaIngreso().getDireccion();
        }
        cargarListapais(); 
        cargarListadepartamento(); 
        cargarListaciudad();
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
     * Carga la lista listapais
     */
    public void cargarListapais(){
        
        String urlEnumId = UbicacionElementoControladorUrlEnum.URL0001.getValue();
        
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        Map<String, Object> param = new TreeMap<>();

        listapais = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
        
    }
    /**
     * 
     * Carga la lista listadepartamento
     */
    public void cargarListadepartamento(){
        
        String urlEnumId = UbicacionElementoControladorUrlEnum.URL0002.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        Map<String, Object> param = new TreeMap<>();
        param.put("PAIS", pais);

        listadepartamento = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
        
    }
    /**
     * 
     * Carga la lista listaciudad
     */
    public void cargarListaciudad(){
        
        String urlEnumId = UbicacionElementoControladorUrlEnum.URL0003.getValue();
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId);
        Map<String, Object> param = new TreeMap<>();
        param.put("PAIS", pais);
        param.put("DEPARTAMENTO", departamento);

        listaciudad = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
        
    }
    //</METODOS_CARGAR_LISTA>
    //<METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton BT3373
     * en la vista
     *
     */
    public void oprimirGuardar() {
        //<CODIGO_DESARROLLADO>
        
        try {
        
        Map<String, Object> parametros = new HashMap<>();
        parametros.put(GeneralParameterEnum.COMPANIA.name(), compania);
        parametros.put(GeneralParameterEnum.TIPOMOVIMIENTO.name(), tipoMovimiento);
        parametros.put(GeneralParameterEnum.MOVIMIENTO.name(), movimiento);
        parametros.put(GeneralParameterEnum.CODIGO.name(), codigo);
        parametros.put("PAIS", pais);
        parametros.put(GeneralParameterEnum.DEPARTAMENTO.name(), departamento);
        parametros.put(GeneralParameterEnum.MUNICIPIO.name(), ciudad);
        parametros.put(GeneralParameterEnum.DIRECCION.name(), ubicacion);
        Parameter parameter = new Parameter();
        parameter.setFields(parametros);
        UrlBean urlUpdateM = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        UbicacionElementoControladorUrlEnum.URL9526
                                                        .getValue());
            requestManager.update(urlUpdateM.getUrl(),
                            urlUpdateM.getMetodo(), parameter);
        
        Map<String, Object> paramDevol = new HashMap<>();
        paramDevol.put(GeneralParameterEnum.COMPANIA.name(), compania);
        paramDevol.put(GeneralParameterEnum.ELEMENTO.name(), elemento);
        paramDevol.put(GeneralParameterEnum.SERIE.name(), serie);
        paramDevol.put("PAIS", pais);
        paramDevol.put(GeneralParameterEnum.DEPARTAMENTO.name(), departamento);
        paramDevol.put(GeneralParameterEnum.MUNICIPIO.name(), ciudad);
        paramDevol.put(GeneralParameterEnum.DIRECCION.name(), ubicacion);
        paramDevol.put("TIPOESTACIONADIC", tipoEstacionAdic);
        
        
        Parameter parameterD = new Parameter();

        parameterD.setFields(paramDevol);
        
        UrlBean urlUpdateD = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        UbicacionElementoControladorUrlEnum.URL27810
                                                        .getValue());
            
            requestManager.update(urlUpdateD.getUrl(),
                            urlUpdateD.getMetodo(), parameterD);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_MODIFICADO"));
        //</CODIGO_DESARROLLADO>
    }
    //</METODOS_BOTONES>
    //<METODOS_CAMBIAR>
    //</METODOS_CAMBIAR>
    //<METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listapais
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilapais(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        pais = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
        paisNombre = registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();
        cargarListadepartamento();
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listadepartamento
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFiladepartamento(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        departamento= registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
        departamentoNombre = registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();
        cargarListaciudad();
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaciudad
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaciudad(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        ciudad= registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
        ciudadNombre = registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();
    }
    //</METODOS_COMBOS_GRANDES>
    //<METODOS_ARBOL>
    //</METODOS_ARBOL>
    //<SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable pais
     * 
     * @return  pais
     */
    public String getPais() {
        return pais;
    }
    /**
     * Asigna la variable  pais
     * 
     * @param  pais
     * Variable a asignar en  pais
     */
    public void setPais(String pais) {
        this.pais = pais;
    }
    /**
     * Retorna la variable departamento
     * 
     * @return  departamento
     */
    public String getDepartamento() {
        return departamento;
    }
    /**
     * Asigna la variable  departamento
     * 
     * @param  departamento
     * Variable a asignar en  departamento
     */
    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }
    /**
     * Retorna la variable ciudad
     * 
     * @return  ciudad
     */
    public String getCiudad() {
        return ciudad;
    }
    /**
     * Asigna la variable  ciudad
     * 
     * @param  ciudad
     * Variable a asignar en  ciudad
     */
    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }
    /**
     * Retorna la variable ubicacion
     * 
     * @return  ubicacion
     */
    public String getUbicacion() {
        return ubicacion;
    }
    /**
     * Asigna la variable  ubicacion
     * 
     * @param  ubicacion
     * Variable a asignar en  ubicacion
     */
    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
    //</SET_GET_ATRIBUTOS>
    //<SET_GET_PARAMETROS>
    //</SET_GET_PARAMETROS>
    //<SET_GET_LISTAS>
    //</SET_GET_LISTAS>
    //<SET_GET_LISTAS_COMBO_GRANDE>	
    /**
     * Retorna la lista listapais
     * 
     * @return listapais
     */
    public RegistroDataModelImpl getListapais() {
        return listapais;
    }
    /**
     * Asigna la lista listapais
     * 
     * @param listapais
     * Variable a asignar en  listapais
     */
    public void setListapais(RegistroDataModelImpl listapais) {
        this.listapais = listapais;
    }
    /**
     * Retorna la lista listadepartamento
     * 
     * @return listadepartamento
     */
    public RegistroDataModelImpl getListadepartamento() {
        return listadepartamento;
    }
    /**
     * Asigna la lista listadepartamento
     * 
     * @param listadepartamento
     * Variable a asignar en  listadepartamento
     */
    public void setListadepartamento(RegistroDataModelImpl listadepartamento) {
        this.listadepartamento = listadepartamento;
    }
    /**
     * Retorna la lista listaciudad
     * 
     * @return listaciudad
     */
    public RegistroDataModelImpl getListaciudad() {
        return listaciudad;
    }
    /**
     * Asigna la lista listaciudad
     * 
     * @param listaciudad
     * Variable a asignar en  listaciudad
     */
    public void setListaciudad(RegistroDataModelImpl listaciudad) {
        this.listaciudad = listaciudad;
    }
    public String getCiudadNombre() {
        return ciudadNombre;
    }
    public void setCiudadNombre(String ciudadNombre) {
        this.ciudadNombre = ciudadNombre;
    }
    public String getPaisNombre() {
        return paisNombre;
    }
    public void setPaisNombre(String paisNombre) {
        this.paisNombre = paisNombre;
    }
    public String getDepartamentoNombre() {
        return departamentoNombre;
    }
    public void setDepartamentoNombre(String departamentoNombre) {
        this.departamentoNombre = departamentoNombre;
    }
    public boolean isBloqueo() {
        return bloqueo;
    }
    public void setBloqueo(boolean bloqueo) {
        this.bloqueo = bloqueo;
    }
    public String getTipoEstacionAdic() {
        return tipoEstacionAdic;
    }
    public void setTipoEstacionAdic(String tipoEstacionAdic) {
        this.tipoEstacionAdic = tipoEstacionAdic;
    }
    
    
    //</SET_GET_LISTAS_COMBO_GRANDE>
}

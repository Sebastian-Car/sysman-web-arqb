/*-
 * DesicionRecursoControlador.java
 *
 * 1.0
 * 
 * 05/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.DesicionRecursoControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import java.util.HashMap;
import java.util.Map;
/**
 * Permite configurar la desicion del recurso
 *
 * @version 1.0, 05/07/2018
 * @author asana
 */
@ManagedBean
@ViewScoped
public class DesicionRecursoControlador extends BeanBaseDatosAcmeImpl{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania ; 
    private String cedulaEvaluado;
    private String cedulaEvaluador;
    private String sucursalEvaluado;
    private String sucursalEvaluador;
    private String numeroEvaluacion;
    private String claseEvaluacion;
    private String tipoEvaluacion;
    private Registro existe;
    
    Map<String, Object> ridDatos;
    //<DECLARAR_ATRIBUTOS>
    //</DECLARAR_ATRIBUTOS>
    //<DECLARAR_LISTAS>
    //</DECLARAR_LISTAS>
    //<DECLARAR_LISTAS_COMBO_GRANDE>
    //</DECLARAR_LISTAS_COMBO_GRANDE>
    //<DECLARAR_LISTAS_SUBFORM>
    //</DECLARAR_LISTAS_SUBFORM>
    //<DECLARAR_PARAMETROS>
    //</DECLARAR_PARAMETROS>
    //<DECLARAR_ADICIONALES>
    //</DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de DesicionRecursoControlador
     */
    public DesicionRecursoControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 1849
            numFormulario = GeneralCodigoFormaEnum.FRM_DESICIONRECURSO_CONTROLADOR.getCodigo();
            validarPermisos();
            //<INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            
            if (parametrosEntrada != null) {
                ridDatos = (Map<String, Object>)parametrosEntrada.get("rid");
                cedulaEvaluado = parametrosEntrada.get("CedulaEvaluado").toString();
                cedulaEvaluador = parametrosEntrada.get("CedulaEvaluador").toString();
                sucursalEvaluado = parametrosEntrada.get("SucursalEvaluado").toString();
                sucursalEvaluador = parametrosEntrada.get("SucursalEvaluador").toString();
                numeroEvaluacion = parametrosEntrada.get("NumeroEvaluacion").toString();
                claseEvaluacion = parametrosEntrada.get("ClaseEvaluacion").toString();
                tipoEvaluacion = parametrosEntrada.get("TipoEvaluacion").toString();
            }
            //</INI_ADICIONAL>
        } catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }
    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas(){
        //<CARGAR_LISTA_COMBO_GRANDE>
        //</CARGAR_LISTA_COMBO_GRANDE>
        //<CARGAR_LISTA>
        //</CARGAR_LISTA>
    }
    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub(){
        //<CARGAR_LISTAS_SUBFORM>
        //</CARGAR_LISTAS_SUBFORM>
        //<CREAR_ARBOLES>
        //</CREAR_ARBOLES>
    }
    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo(){
        //<CARGAR_LISTAS_SUBFORM_NULL>
        //</CARGAR_LISTAS_SUBFORM_NULL>
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
        enumBase = GenericUrlEnum.DESICIONRECURSO;
        buscarLlave();
        asignarOrigenDatos();
        abrirFormulario();
        
        
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
        parametrosListado.put("KEY_COMPANIA", compania);
        parametrosListado.put("KEY_CEDULA_EVALUADO", cedulaEvaluado);
        parametrosListado.put("KEY_CEDULA_EVALUADOR", cedulaEvaluador);
        parametrosListado.put("KEY_SUCURSAL_EVALUADO", sucursalEvaluado);
        parametrosListado.put("KEY_SUCURSAL_EVALUADOR", sucursalEvaluador);
        parametrosListado.put("KEY_NUMERO_EVALUACION", numeroEvaluacion);
        parametrosListado.put("KEY_CLASE_EVALUACION", claseEvaluacion);
        parametrosListado.put("KEY_TIPO_EVALUACION", tipoEvaluacion);
        
    }
    
    //<METODOS_CARGAR_LISTA>	
    //</METODOS_CARGAR_LISTA>
    //<METODOS_CAMBIAR>	
    //</METODOS_CAMBIAR>
    //<METODOS_COMBOS_GRANDES>	
    //</METODOS_COMBOS_GRANDES>
    //<METODOS_ARBOL>	
    //</METODOS_ARBOL>
    //<METODOS_BOTONES>	
    //</METODOS_BOTONES>	
    //<METODOS_SUBFORM>	
    //</METODOS_SUBFORM>	
    //<METODOS_ADICIONALES>	
    //</METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario(){
        //<CODIGO_DESARROLLADO>
        precargarRegistro();
        try {
            existe = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DesicionRecursoControladorUrlEnum.URL6229
                                                                            .getValue())
                                            .getUrl(), parametrosListado));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        
        if ("0".equals(existe.getCampos().get("EXISTE").toString())) {
            cargarRegistro(null, ACCION_INSERTAR);
        } else {
            cargarRegistro(parametrosListado, ACCION_MODIFICAR);
        }
        
        //</CODIGO_DESARROLLADO>
    }
    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     * 
     */
    @Override
    public void cargarRegistro() {
        //<CODIGO_DESARROLLADO>
        precargarRegistro();
        //</CODIGO_DESARROLLADO>
    }
    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     */
    
    public void ejecutarrcCerrar() {
        Map<String, Object> parametros = new HashMap<>();
        
        parametros.put("rid", ridDatos);
        parametros.put("evaluacion", numeroEvaluacion);
        parametros.put("claseEvaluacion", claseEvaluacion);
        parametros.put("tipo", tipoEvaluacion);
        
        
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer.toString(GeneralCodigoFormaEnum.FRM_EVALUACIONESDET_CONTROLADOR.getCodigo()));
        direccionador.setParametros(parametros);
        
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        
        
    }
    @Override
    public boolean insertarAntes(){
        //<CODIGO_DESARROLLADO>
        cargarParametros();
        //</CODIGO_DESARROLLADO>
        return true;
    }
    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     */
    @Override
    public boolean insertarDespues(){
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
        return true;
    }
    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     */
    @Override
    public boolean actualizarAntes(){
        //<CODIGO_DESARROLLADO>
        
        if ((boolean)registro.getCampos().get("APELACION") && (!(boolean) registro.getCampos().get("REPOSICION"))) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4145"));
            return false;
        } else {
            
        
        cargarParametros();
        return true;
        }
        //</CODIGO_DESARROLLADO>
        
    }
    
    public void cargarParametros() {
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
        registro.getCampos().put("NUMERO_EVALUACION", numeroEvaluacion);
        registro.getCampos().put("CLASE_EVALUACION", claseEvaluacion);
        registro.getCampos().put("TIPO_EVALUACION", tipoEvaluacion);
        registro.getCampos().put("CEDULA_EVALUADO", cedulaEvaluado);
        registro.getCampos().put("SUCURSAL_EVALUADO", sucursalEvaluado);
        registro.getCampos().put("CEDULA_EVALUADOR", cedulaEvaluador);
        registro.getCampos().put("SUCURSAL_EVALUADOR", sucursalEvaluador);
        
    }
    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion
     * del registro
     * 
     */
    @Override
    public boolean actualizarDespues(){
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
        return true;
    }
    /**
     * Metodo ejecutado antes de realizar la eliminacion del
     * registro
     * 
     */
    @Override
    public boolean eliminarAntes(){
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
        return true;
    }
    /**
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     */
    @Override
    public boolean eliminarDespues(){
        //<CODIGO_DESARROLLADO>
        //</CODIGO_DESARROLLADO>
        return true;
    }
    //<SET_GET_ATRIBUTOS>
    //</SET_GET_ATRIBUTOS>
    //<SET_GET_LISTAS>
    //</SET_GET_LISTAS>
    //<SET_GET_LISTAS_COMBO_GRANDE>	
    //</SET_GET_LISTAS_COMBO_GRANDE>
    //<SET_GET_LISTAS_SUBFORM>
    //</SET_GET_LISTAS_SUBFORM>
    //<SET_GET_PARAMETROS>
    //</SET_GET_PARAMETROS>
    //<SET_GET_ADICIONALES>	
    //</SET_GET_ADICIONALES>
}

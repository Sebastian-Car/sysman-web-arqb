/*-
 * BeneficiariosupcsControlador.java
 *
 * 1.0
 * 
 * 15/03/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
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
import com.sysman.nomina.enums.BeneficiariosupcsControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 * 
 * @version 1, 02/10/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos, en el origen de grilla, de datos.
 * 
 */

@ManagedBean
@ViewScoped
public class BeneficiariosupcsControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String consFecha;

    private String edad;
    private List<Registro> listatipoDctoIdentidad;
    private List<Registro> listaParentesco;

    private final String idEmpleadoCons;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>

    private RegistroDataModelImpl listaEmpleado;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de BeneficiariosupcsControlador
     */
    public BeneficiariosupcsControlador() {
        super();
        compania = SessionUtil.getCompania();
        idEmpleadoCons = "ID_DE_EMPLEADO";
        consFecha="FECHAN_NCTO";
        try {
            numFormulario = GeneralCodigoFormaEnum.BENEFICIARIOSUPCS_CONTROLADOR
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
        cargarListaEmpleado();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListatipoDctoIdentidad();
        cargarListaParentesco();
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
        enumBase=GenericUrlEnum.BENEFICIARIOS_UPC;
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listatipoDctoIdentidad
     *
     */
    public void cargarListatipoDctoIdentidad() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listatipoDctoIdentidad = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            BeneficiariosupcsControladorUrlEnum.URL4837
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
     * Carga la lista listaParentesco
     *
     */
    public void cargarListaParentesco() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            
            listaParentesco = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            BeneficiariosupcsControladorUrlEnum.URL5411
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
     * Carga la lista listaEmpleado
     *
     */
    public void cargarListaEmpleado() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BeneficiariosupcsControladorUrlEnum.URL5875
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaEmpleado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, idEmpleadoCons);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control FechaNacimiento
     * 
     * 
     */
    public void cambiarFechaNacimiento() {
        // <CODIGO_DESARROLLADO>
        edad = SysmanFunciones.concatenar(SysmanFunciones.calcularEdad(
                        (Date) registro.getCampos().get(consFecha)), " años");
        // </CODIGO_DESARROLLADO>
    }
    
    /**
     * Metodo ejecutado al cambiar el control fechanRetiroUpc
     * 
     */
    public void cambiarfechanRetiroUpc() {
             //<CODIGO_DESARROLLADO>
        if(registro.getCampos().get("FECHAN_RETIROUPC") != null){
            registro.getCampos().put("ESTADO_ACTUALUPC", 3);
        }
            //</CODIGO_DESARROLLADO>
        }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaEmpleado
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaEmpleado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(idEmpleadoCons,
                        registroAux.getCampos().get(idEmpleadoCons));
        registro.getCampos().put("NOMBRECOMPLETO",registroAux.getCampos().get("NOMBRE_COMP").toString());
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
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
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     */
    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
            if (css == null) {
                edad = null;
            }
            else {
                edad = registro.getCampos().get(consFecha)!= null ?SysmanFunciones.concatenar(SysmanFunciones.calcularEdad((Date) registro
                                .getCampos().get(consFecha)), " años"):"";
                registro.getCampos().put("MODIFIED_BY",
                                SessionUtil.getUser().getCodigo());
                registro.getCampos().put("DATE_MODIFIED", new Date());
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
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove("NOMBRECOMPLETO");
        
        if(registro.getCampos().get("FECHAN_RETIROUPC") != null){
            registro.getCampos().put("ESTADO_ACTUALUPC", 3);
        }
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
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable edad
     * 
     * @return edad
     */
    public String getEdad() {
        return edad;
    }

    /**
     * Asigna la variable edad
     * 
     * @param edad
     * Variable a asignar en edad
     */
    public void setEdad(String edad) {
        this.edad = edad;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listatipoDctoIdentidad
     * 
     * @return listatipoDctoIdentidad
     */
    public List<Registro> getListatipoDctoIdentidad() {
        return listatipoDctoIdentidad;
    }

    /**
     * Asigna la lista listatipoDctoIdentidad
     * 
     * @param listatipoDctoIdentidad
     * Variable a asignar en listatipoDctoIdentidad
     */
    public void setListatipoDctoIdentidad(
        List<Registro> listatipoDctoIdentidad) {
        this.listatipoDctoIdentidad = listatipoDctoIdentidad;
    }

    /**
     * Retorna la lista listaParentesco
     * 
     * @return listaParentesco
     */
    public List<Registro> getListaParentesco() {
        return listaParentesco;
    }

    /**
     * Asigna la lista listaParentesco
     * 
     * @param listaParentesco
     * Variable a asignar en listaParentesco
     */
    public void setListaParentesco(List<Registro> listaParentesco) {
        this.listaParentesco = listaParentesco;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaEmpleado
     * 
     * @return listaEmpleado
     */
    public RegistroDataModelImpl getListaEmpleado() {
        return listaEmpleado;
    }

    /**
     * Asigna la lista listaEmpleado
     * 
     * @param listaEmpleado
     * Variable a asignar en listaEmpleado
     */
    public void setListaEmpleado(RegistroDataModelImpl listaEmpleado) {
        this.listaEmpleado = listaEmpleado;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    // </SET_GET_ADICIONALES>
}

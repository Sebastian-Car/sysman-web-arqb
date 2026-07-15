/*-
 * EliminarDetallePptalLoteControlador.java
 *
 * 1.0
 * 
 * 29 abr. 2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.presupuesto.enums.EliminarDetallePptalLoteControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 * Formulario que permite eliminar los detalles de comprobante
 * presupuestal en lote
 *
 * @version 1.0, 29/04/2019
 * @author eamaya
 */
@ManagedBean
@ViewScoped
public class EliminarDetallePptalLoteControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que alacena el anio del comprobante
     */
    private String anioComprobante;

    /**
     * Variable que almacena el numero del comprobante
     */
    private String numeroComprobante;

    /**
     * Variable que almacena el tipo de comprobante
     */
    private String tipoComprobante;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que carga los detalles del comprobante
     */
    private RegistroDataModelImpl listaDetalles;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de EliminarDetallePptalLoteControlador
     */
    public EliminarDetallePptalLoteControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 2068
            numFormulario = GeneralCodigoFormaEnum.ELIMINAR_DETALLE_PPTAL_LOTE_CONTROLADOR
                            .getCodigo();

            Map<String, Object> parametros = SessionUtil.getFlash();
            if (parametros != null) {

                numeroComprobante = parametros.get("comprobante").toString();
                anioComprobante = parametros.get("anio").toString();
                tipoComprobante = parametros.get("tipo").toString();
            }
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
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaDetalles();
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
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaDetalles
     *
     */
    public void cargarListaDetalles() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EliminarDetallePptalLoteControladorUrlEnum.URL4249
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        try {
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.ANO.getName(),
                            anioComprobante);
            param.put(GeneralParameterEnum.TIPO.getName(),
                            tipoComprobante);
            param.put(GeneralParameterEnum.NUMERO.getName(),
                            numeroComprobante);

            listaDetalles = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param, false,
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            "DETALLE_COMPROBANTE_PPTAL"),
                            true);
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar en la vista
     *
     *
     */
    public void oprimirAceptar() {

        List<Registro> listaSeleccionados;
        if (listaDetalles.getSeleccionados().isEmpty()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB686"));
        }
        else {

            listaSeleccionados = listaDetalles.getSeleccionados();

            for (Registro reg : listaSeleccionados) {

                UrlBean urlDelete = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                GenericUrlEnum.DETALLE_COMPROBANTE_PPTAL
                                                                .getDeleteKey());
                try {
                    requestManager.delete(urlDelete.getUrl(), reg.getLlave());
                }
                catch (SystemException e) {
                    logger.error(e.getMessage(), e);
                    JsfUtil.agregarMensajeError(e.getMessage());
                }
                
            }

            listaDetalles.getSeleccionados().clear();
            cargarListaDetalles();
            eliminarCpteWs();
        }
        
    }
    private void eliminarCpteWs() {
    	    // se elimina el detalle del comprobante pero de la tabla detalle_cpte_afect_ws
		    	try {
			    	int delete = 0;
			    	
			    	Map<String, Object> param2 = new TreeMap<>();
			        param2.put(GeneralParameterEnum.KEY_COMPANIA.getName(), compania);
			        param2.put(GeneralParameterEnum.KEY_ANO.getName(), anioComprobante);
			        param2.put(GeneralParameterEnum.KEY_COMPROBANTE.getName(), numeroComprobante);
			        param2.put(GeneralParameterEnum.KEY_TIPO_CPTE.getName(), tipoComprobante);
			        			        
			        UrlBean urlBean = UrlServiceUtil.getInstance()
						.getUrlServiceByUrlByEnumID(EliminarDetallePptalLoteControladorUrlEnum.URL1914001.getValue());
		
				
					delete = requestManager.delete(urlBean.getUrl(), param2);
				} catch (SystemException e) {
					Logger.getLogger(EliminarDetallePptalLoteControlador.class
		                    .getName()).log(Level.SEVERE, null, e);
					JsfUtil.agregarMensajeError(e.getMessage());
				}
    }
    /**
     * 
     * Metodo ejecutado al oprimir el boton Cerrar en la vista
     *
     */
    public void oprimirCerrar() {
        RequestContext.getCurrentInstance().closeDialog(null);

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaDetalles
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaDetalles(SelectEvent event) {
        // CODIGO_MIGRADO
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaDetalles
     * 
     * @return listaDetalles
     */
    public RegistroDataModelImpl getListaDetalles() {
        return listaDetalles;
    }

    /**
     * Asigna la lista listaDetalles
     * 
     * @param listaDetalles
     * Variable a asignar en listaDetalles
     */
    public void setListaDetalles(RegistroDataModelImpl listaDetalles) {
        this.listaDetalles = listaDetalles;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}

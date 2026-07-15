/*-
 * ConfCertificadoDianNomControlador.java
 *
 * 1.0
 * 
 * 05/11/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.rest.ApiNominaElectronica;

import java.io.IOException;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 * Controlador para subir el certifido Dian
 *
 * @version 1.0, 05/11/2021
 * @author mzanguna
 */
@ManagedBean
@ViewScoped
public class ConfCertificadoDianNomControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    
    private String nitCompania;
    
    private String ambiente;
    
    private static String URL_SERVICIO_REST_NOMINA_ELECTRONICA = "URL SERVICIO REST NOMINA ELECTRONICA";
    
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    /**
     * Crea una nueva instancia de ConfCertificadoDianNomControlador
     */
    public ConfCertificadoDianNomControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.CONFCERTIFICADODIANMON
                            .getCodigo();
            nitCompania = SessionUtil.getCompaniaIngreso().getNit();
            validarPermisos();

        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
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
        enumBase = GenericUrlEnum.CONF_CERTIFICADONOM;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        abrirFormulario();
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
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

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>

    }

    @Override
    public void reasignarOrigen() {
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        buscarUrls();
    }

    @Override
    public boolean insertarAntes() {
        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
    	ambiente = registro.getCampos().get("TIPO_AMBIENTE").toString();
        registro.getCampos().put("KEY_COMPANIA", compania);
        registro.getCampos().put("KEY_ID", registro.getCampos().get("ID"));
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove("AMBIENTE");
        registro.getCampos().remove("ID");
        return true;
    }

    @Override
    public boolean actualizarDespues() {
    	ApiNominaElectronica apiNE = new ApiNominaElectronica();
    	    	
    	try {
    		String url =  SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania, URL_SERVICIO_REST_NOMINA_ELECTRONICA, SessionUtil.getModulo(), new Date(), false).toString(), "");
			apiNE.putProveedorNE(url, nitCompania,  ambiente, SessionUtil.getUser().getCodigo());
		} catch (IOException | SysmanException | SystemException e) {
			logger.error(e.getMessage(), e);
            SessionUtil.redireccionarMenuPermisos();
		}
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        return true;
    }
}

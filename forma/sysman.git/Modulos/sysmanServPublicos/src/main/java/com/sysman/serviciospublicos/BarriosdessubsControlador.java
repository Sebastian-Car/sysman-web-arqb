/*-
 * BarriosdessubsControlador.java
 *
 * 1.0
 *
 * 29/09/2016
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.serviciospublicos.enums.BarriosdessubsControladorEnum;
import com.sysman.serviciospublicos.enums.BarriosdessubsControladorUrlEnum;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;

/**
 * Formulario que se llama de Gestiondesincentivo, ejecutando el
 * evento del boton ver
 *
 * @version 1.0, 29/09/2016
 * @author ybecerra
 * 
 * @version 2, 16/05/2017
 * @author jreina se realizaron los cambios de refactoring 
 * en en el origen de grilla.
 */
@ManagedBean
@ViewScoped
public class BarriosdessubsControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante que contiene el texto compania utilizado para filtar
     * en el origenDeDatos del formulario
     */
    private final String compania;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    /**
     * Alamcena el valor del parametro "ciclo" recibido del formulario
     * PedirCiclo
     */
    private String ciclo;

    /**
     * Alamcena el valor del parametro "barrio" recibido del
     * formulario PedirCiclo
     */
    private String barrio; 
    /**
     * Crea una nueva instancia de BarriosdessubsControlador
     */
    public BarriosdessubsControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.BARRIOSDESSUBS_CONTROLADOR.getCodigo();
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {

                ciclo = (String) parametrosEntrada.get("ciclo");
                barrio = (String) parametrosEntrada.get("barrio");

            }
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void inicializar() {

        tabla = BarriosdessubsControladorEnum.TABLA.getValue();
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();      
        abrirFormulario();

    }

    @Override
    public void reasignarOrigen() {
        urlListado = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                        BarriosdessubsControladorUrlEnum.URL15084.getValue());
        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BarriosdessubsControladorUrlEnum.URL9050
                                        .getValue());
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametrosListado.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        parametrosListado.put(GeneralParameterEnum.BARRIO.getName(), barrio);
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove("SUSCRIPTOR");
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public void removerCombos() {
        // Este metodo no ejecuta ningun evento
    }

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void asignarValoresRegistro() {
        // Este metodo no ejecuta ningun evento

    }
}

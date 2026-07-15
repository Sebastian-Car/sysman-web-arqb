/*-
 * DatosinicialesControlador.java
 *
 * 1.0
 * 
 * 16/12/2016
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
import com.sysman.enums.GenericUrlEnum;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.serviciospublicos.enums.DatosinicialesControladorUrlEnum;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 * Controlador de la forma Datosiniciales asociado al formulario
 * "Lecturas/Deudas".
 *
 * @version 1.0, 16/12/2016
 * @author yrojas
 * 
 * @author eamaya
 * @version 2, 18/05/2017 Proceso de Refactoring
 * 
 */
@ManagedBean
@ViewScoped

public class DatosinicialesControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Atributo asociado al ciclo y que es usado como filtro en la
     * consulta del origen de datos.
     */
    private String ciclo;

    /**
     * Atributo asociado al a�o y que es usado como filtro en la
     * consulta del origen de datos.
     */
    private String anio;

    /**
     * Atributo asociado al periodo y que es usado como filtro en la
     * consulta del origen de datos.
     */
    private String periodo;
    /**
     * Crea una nueva instancia de DatosinicialesControlador
     */
    public DatosinicialesControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario =GeneralCodigoFormaEnum.DATOSINICIALES_CONTROLADOR.getCodigo();
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            ciclo = parametrosEntrada.get("ciclo").toString();
            periodo = parametrosEntrada.get("periodo").toString();
            anio = parametrosEntrada.get("anio").toString();    
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este m�todo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado. En este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y dem�s necesarios
     */
    @PostConstruct
    public void inicializar() { 
        enumBase = GenericUrlEnum.SP_USUARIO;
        reasignarOrigen();
        buscarLlave();  
        registro = new Registro();
        abrirFormulario();            
    }

    /**
     * En este m�todo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DatosinicialesControladorUrlEnum.URL5304
                                        .getValue());

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CICLO.getName(),
                        ciclo);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);
        parametrosListado.put(GeneralParameterEnum.PERIODO.getName(), periodo);

        urlActualizacion = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DatosinicialesControladorUrlEnum.URL162
                                        .getValue());

    }

    /**
     * Este m�todo es invocado en el m�todo inicializar. Se ejecutan
     * las acciones a tener en cuenta en el momento de apertura del
     * formulario.
     */
    @Override
    public void abrirFormulario() {
        //heredado del bean padre
    }

    /**
     * M�todo ejecutado cuando se cancela la edicion del registro
     * seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * M�todo ejecutado antes de realizar la insercion del registro
     * 
     * @return Valor booleano
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * M�todo ejecutado despu�s de realizar la insercion del registro
     * 
     * @return Valor booleano
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * M�todo ejecutado antes de realizar la inserci�n y actualizaci�n
     * del registro. En este caso se remueve el campo NOMBRE de la
     * consulta, debido a que es la concatenaci�n de varios campos
     * 
     * @return Valor booleano
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove("NOMBRE");
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * M�todo ejecutado despu�s de realizar la inserci�n y
     * actualizacion del registro
     * 
     * @return Variable booleana
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * M�todo ejecutado antes de realizar la eliminaci�n del registro.
     * 
     * @return Variable booleana
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * M�todo ejecutado despu�s de realizar la eliminaci�n del
     * registro
     * 
     * @return Variable booleana
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este m�todo se ejecuta antes enviar la accion de actualizaci�n,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.CICLO.getName());
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
        registro.getCampos().remove(GeneralParameterEnum.PERIODO.getName());
        registro.getCampos().remove("LECTURA");
        registro.getCampos().remove("LECTURA1");
        registro.getCampos().remove("LECTURAAFORO");

    }

    /**
     * Este m�todo es ejecutado despu�s de finalizar la inserci�n y
     * edici�n del registro. Se usa cuando se desean agregar valores
     * al registro despu�s de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // NO IMPLEMENTADO
    }

    // <SET_GET_ATRIBUTOS>

    /**
     * M�todo que retorna la variable ciclo
     * 
     * @return Variable de ciclo
     */
    public String getCiclo() {
        return ciclo;
    }

    /**
     * M�todo que asigna la variable ciclo
     * 
     * @param ciclo
     * Variable a asignar en ciclo
     */
    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    /**
     * M�todo que retorna la variable anio
     * 
     * @return Variable de anio
     */
    public String getAnio() {
        return anio;
    }

    /**
     * M�todo que asigna la variable anio
     * 
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(String anio) {
        this.anio = anio;
    }

    /**
     * M�todo que retorna la variable periodo
     * 
     * @return Variable de periodo
     */
    public String getPeriodo() {
        return periodo;
    }

    /**
     * M�todo que asigna la variable periodo
     * 
     * @param periodo
     * Variable a asignar en periodo
     */
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    /**
     * M�todo que retorna la variable compania
     * 
     * @return Variable de compania
     */
    public String getCompania() {
        return compania;
    }   
}

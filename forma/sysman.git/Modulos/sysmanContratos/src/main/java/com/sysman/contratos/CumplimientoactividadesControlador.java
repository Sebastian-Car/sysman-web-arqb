/*-
 * CumplimientoactividadesControlador.java
 *
 * 1.0
 * 
 * 27/09/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.contratos.enums.CumplimientoactividadesControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 27/09/2018
 * @author jmalaver
 */
@ManagedBean
@ViewScoped
public class CumplimientoactividadesControlador
                extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
    private int indice;

    private HashMap<String, Object> rid;

    /**
     * Atributo que almacena el Valor del CODIGOESTUDIO con el que se
     * esta trabajando
     */
    private String codNovedad;

    /**
     * Atributo que almacena el Valor del CODIGOESTUDIO con el que se
     * esta trabajando
     */
    private String codActa;

    /**
     * Atributo que almacena el Valor del CODIGOESTUDIO con el que se
     * esta trabajando
     */
    private String tipoNovedad;

    /**
     * Atributo que almacena el Valor del CODIGOESTUDIO con el que se
     * esta trabajando
     */
    private String codContrato;

    /**
     * Atributo que almacena el Valor del CODIGOESTUDIO con el que se
     * esta trabajando
     */
    private String tipoContrato;

    private Registro rs;

    private Double porcAcumuladoAnterior;

    private Map<String, Object> parametrosEntrada;

    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de CumplimientoactividadesControlador
     */
    public CumplimientoactividadesControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.CUMPLIMIENTOACTIVIDADES_CONTROLADOR
                            .getCodigo();
            parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                codNovedad = parametrosEntrada.get("novedad").toString();
                codActa = parametrosEntrada.get("codActa").toString();
                tipoNovedad = parametrosEntrada.get("tipot").toString();
                codContrato = parametrosEntrada.get("numero").toString();
                tipoContrato = parametrosEntrada.get("claseOrden").toString();

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
        enumBase = GenericUrlEnum.CUMPLIMIENTO_ACTIVIDADES;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put("CODIGO_ACTA", codActa);
        parametrosListado.put("CODIGO_NOVEDAD", codNovedad);
        parametrosListado.put("TIPO_NOVEDAD", tipoNovedad);
        parametrosListado.put("COD_CONTRATO", codContrato);
        parametrosListado.put("TIPO_CONTRATO", tipoContrato);

    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control CpPorcCumplimiento en la
     * fila seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarCpPorcCumplimientoC(int rowNum) {
        // Para el cambio en una fila selecciona (PARA FORMULARIOS
        // CONTINUOS) se realiza como lo muestra la siguiente linea
        // listaInicial.getDatasource().get(rowNum %
        // 10).getCampos().put("FECHALARGA", "hola ");
        // Para el cambio en una fila selecciona (PARA SUBFORMULARIOS)
        // se realiza como lo muestra la siguiente linea
        // listaInicial.get(rowNum).getCampos().put("FECHALARGA",
        // "hola ");
        // <CODIGO_DESARROLLADO>

        Map<String, Object> param = new TreeMap<>();
        param.put("CODIGO_ACTIVIDAD",
                        listaInicial.getDatasource().get(rowNum % 10)
                                        .getCampos().get("CODIGO_ACTIVIDAD"));
        param.put("COD_CONTRATO", codContrato);
        param.put("TIPO_CONTRATO", tipoContrato);
        param.put("CODIGO_NOVEDAD", codNovedad);

        try {
            rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CumplimientoactividadesControladorUrlEnum.URL001
                                                                            .getValue())
                                            .getUrl(), param));

            if (rs != null) {

                double porcentajeRestante = 100 - porcAcumuladoAnterior;
                double acumuladoTotal = porcAcumuladoAnterior
                    - Double.parseDouble(
                                    rs.getCampos().get("PORC_ACUM").toString())
                    + Double.parseDouble(listaInicial.getDatasource()
                                    .get(rowNum % 10).getCampos()
                                    .get("PORCENTAJE_CUMPLIMIENTO").toString());

                if (acumuladoTotal > 100) {
                    JsfUtil.agregarMensajeAlerta(
                                    "El porcentaje acumulado supera el 100%. Únicamente dispone de "
                                        + porcentajeRestante
                                        + "% para completar la actividad.");
                    listaInicial.getDatasource()
                                    .get(rowNum % 10).getCampos()
                                    .put("PORCENTAJE_CUMPLIMIENTO", 0);
                }
                else {
                    listaInicial.getDatasource().get(rowNum %
                        10).getCampos()
                                    .put("PORCENTAJE_ACUMULADO",
                                                    acumuladoTotal);

                    param.put("PORCENTAJE_ACUMULADO", acumuladoTotal);
                    param.put("COMPANIA", compania);
                    param.put("USUARIO", SessionUtil.getUser().getCodigo());
                    param.put("CODIGO_ACTA", codActa);
                    param.remove("CODIGO_ACTIVIDAD");
                    param.remove("CODIGO_NOVEDAD");

                    Parameter parameter = new Parameter();
                    parameter.setFields(param);

                    UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    CumplimientoactividadesControladorUrlEnum.URL002
                                                                    .getValue());
                    requestManager.update(urlUpdate.getUrl(),
                                    urlUpdate.getMetodo(),
                                    parameter);
                }

            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

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

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado
     * 
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return true
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
     * @return true
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * @return true
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>

        // Map<String, Object> param = new TreeMap<>();
        // param.put("CODIGO_ACTIVIDAD",
        // registro.getCampos().get("CODIGO_ACTIVIDAD"));
        // param.put("COD_CONTRATO", codContrato);
        // param.put("TIPO_CONTRATO", tipoContrato);
        // param.put("CODIGO_NOVEDAD", codNovedad);
        //
        // try {
        // rs = RegistroConverter.toRegistro(
        // requestManager.get(UrlServiceUtil.getInstance()
        // .getUrlServiceByUrlByEnumID(
        // CumplimientoactividadesControladorUrlEnum.URL001
        // .getValue())
        // .getUrl(), param));
        //
        // if (rs != null) {
        // registro.getCampos().put(
        // "PORCENTAJE_ACUMULADO",
        // rs.getCampos().get("PORC_ACUM"));
        // }
        // }
        // catch (SystemException e) {
        // logger.error(e.getMessage(), e);
        // JsfUtil.agregarMensajeError(e.getMessage());
        // }
        //
        // listaInicial.load();

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * @return true
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
     * @return true
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
    }

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     * 
     * TODO DOCUMENTACION ADICIONAL
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();

        porcAcumuladoAnterior = Double.parseDouble(listaInicial.getDatasource()
                        .get(indice % 10)
                        .getCampos()
                        .get("PORCENTAJE_ACUMULADO").toString());

    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // TODO Auto-generated method stub
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     * 
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Direccionador direccionador = new Direccionador();

        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.SUBNOVEDADCONTRATOS_CONTROLADOR
                                        .getCodigo()));
        direccionador.setParametros(parametrosEntrada);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }
    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * @return the indice
     */
    public int getIndice() {
        return indice;
    }

    /**
     * @param indice
     * the indice to set
     */
    public void setIndice(int indice) {
        this.indice = indice;
    }
}

/*-
 * SublegalizacionviaticosControlador.java
 *
 * 1.0
 * 
 * 18/01/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.viaticos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
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
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import com.sysman.viaticos.ejb.EjbViaticosCeroRemote;
import com.sysman.viaticos.enums.SubLegalizacionViaticosControladorEnum;
import com.sysman.viaticos.enums.SubLegalizacionViaticosControladorUrlEnum;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Clase migrada para visualizar los detalles de la legalizacion de
 * viaticos
 * 
 * @version 1.0, 18/01/2018
 * @author ybecerra
 */
@ManagedBean
@ViewScoped

public class SubLegalizacionViaticosControlador
                extends BeanBaseContinuoAcmeImpl {

    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el valor recibido por parametro del ano
     */
    private String ano;
    /**
     * Atributo que almacena el valor recibido por parametro del
     * numero
     */
    private String numero;
    /**
     * Atributo que almacena el valor recibido por parametro del
     * tercero
     */
    private String tercero;
    /**
     * Atributo que almacena el valor recibido por parametro del
     * nombre del tercero
     */
    private String nombreTercero;
    /**
     * Atributo almacena el valor recibido por parametro de la
     * sucursal del tercero
     */
    private String sucursal;

    /**
     * Atributo que almacena el valor recibido por parametro del tipo
     * de viatico
     */
    private String tipoViatico;

    /**
     * Variable que permite almacenar el valor del indice de la fila
     * del subformulario SubInfoOperacion que va a ser editada.
     */
    private int indice;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    /**
     * Map recibida por parametro que trae la llave del registro por
     * el cual se carga este formulario
     */
    Map<String, Object> ridSub;

    /**
     * Variable creada para guardar el registro de la grilla antes de
     * ser editado el registro seleccionado en el formulario de
     * detalle de legalizacion de viaticos
     */
    private Registro regSubLegalizacion;
    /**
     * Variable a nivel local que toma el valor de la columna valor
     * del formulario
     */
    private double valorTotal;
    /**
     * variable a nivel local que toma el total de la tabla
     * vi_detalle_viaticos
     */
    private double total;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de registros de los conceptos
     */
    private RegistroDataModelImpl listaConcepto;
    /**
     * Lista de registros de los conceptos
     */
    private RegistroDataModelImpl listaConceptoE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    @EJB
    private EjbViaticosCeroRemote ejbViaticosCero;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de SublegalizacionviaticosControlador
     */
    @SuppressWarnings("unchecked")
    public SubLegalizacionViaticosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            // 1618
            numFormulario = GeneralCodigoFormaEnum.SUB_LEGALIZACION_VIATICOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {

                ridSub = (Map<String, Object>) parametrosEntrada
                                .get("ridSub");

                ano = ridSub.get(
                                SubLegalizacionViaticosControladorEnum.KEY_ANO
                                                .getValue())
                                .toString();

                numero = ridSub.get(
                                SubLegalizacionViaticosControladorEnum.KEY_NUMERO
                                                .getValue())
                                .toString();

                tipoViatico = ridSub.get(
                                SubLegalizacionViaticosControladorEnum.KEY_TIPO_VIATICO
                                                .getValue())
                                .toString();

                tercero = parametrosEntrada.get("tercero").toString();

                sucursal = parametrosEntrada.get("sucursal").toString();

                nombreTercero = parametrosEntrada.get("nombreTercero")
                                .toString();

            }
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
        enumBase = GenericUrlEnum.VI_DETALLE_LEGALIZA_VIATICOS;
        reasignarOrigen();
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaConcepto();
        cargarListaConceptoE();
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
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), ano);
        parametrosListado.put(GeneralParameterEnum.NUMERO.getName(), numero);
        parametrosListado.put(GeneralParameterEnum.TERCERO.getName(), tercero);
        parametrosListado.put(GeneralParameterEnum.SUCURSAL.getName(),
                        sucursal);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaConcepto
     *
     */
    public void cargarListaConcepto() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubLegalizacionViaticosControladorUrlEnum.URL188
                                                        .getValue());
        listaConcepto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        SubLegalizacionViaticosControladorEnum.CODIGO_CONCEPTO
                                        .getValue());

    }

    /**
     * 
     * Carga la lista listaConcepto
     *
     */
    public void cargarListaConceptoE() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        ano);
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SubLegalizacionViaticosControladorUrlEnum.URL188
                                                        .getValue());
        listaConceptoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        SubLegalizacionViaticosControladorEnum.CODIGO_CONCEPTO
                                        .getValue());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Valor en la fila
     * seleccionada dentro de la grilla
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarValorC(int rowNum) {

        // <CODIGO_DESARROLLADO>
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(SubLegalizacionViaticosControladorEnum.TIPOVIATICO.getValue(),
                        tipoViatico);
        param.put(GeneralParameterEnum.TERCERO.getName(), tercero);
        param.put(GeneralParameterEnum.SUCURSAL.getName(), sucursal);
        param.put(GeneralParameterEnum.CONCEPTO.getName(),
                        listaInicial.getDatasource()
                                        .get(rowNum % 10).getCampos()
                                        .get(SubLegalizacionViaticosControladorEnum.CODIGO_CONCEPTO
                                                        .getValue())
                                        .toString());
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        listaInicial.getDatasource()
                                        .get(rowNum % 10).getCampos()
                                        .get(SubLegalizacionViaticosControladorEnum.NUMERO_AFECTADO
                                                        .getValue())
                                        .toString());

        try {
            Registro rsTotal = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SubLegalizacionViaticosControladorUrlEnum.URL301
                                                                            .getValue())
                                            .getUrl(), param));

            valorTotal = Double
                            .parseDouble(listaInicial.getDatasource()
                                            .get(rowNum % 10).getCampos()
                                            .get(GeneralParameterEnum.VALOR
                                                            .getName())
                                            .toString());
            total = Double
                            .parseDouble(rsTotal.getCampos()
                                            .get(GeneralParameterEnum.TOTAL
                                                            .getName())
                                            .toString());
            if (valorTotal > total) {

                listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                .put(GeneralParameterEnum.VALOR
                                                .getName(),
                                                regSubLegalizacion.getCampos()
                                                                .get(GeneralParameterEnum.VALOR
                                                                                .getName()));
            }

            listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                            SubLegalizacionViaticosControladorEnum.VALOR_AFECTADO
                                            .getValue(),
                            listaInicial.getDatasource().get(rowNum % 10)
                                            .getCampos()
                                            .get(GeneralParameterEnum.VALOR
                                                            .getName()));
            double saldoAnterior = Double
                            .parseDouble(regSubLegalizacion.getCampos()
                                            .get(SubLegalizacionViaticosControladorEnum.SALDO
                                                            .getValue())
                                            .toString());
            double valor = Double
                            .parseDouble(listaInicial.getDatasource()
                                            .get(rowNum % 10)
                                            .getCampos()
                                            .get(GeneralParameterEnum.VALOR
                                                            .getName())
                                            .toString());

            double valorAnterior = Double
                            .parseDouble(regSubLegalizacion.getCampos()
                                            .get(SubLegalizacionViaticosControladorEnum.SALDO
                                                            .getValue())
                                            .toString());
            double saldo = saldoAnterior + valor - valorAnterior;

            listaInicial.getDatasource().get(rowNum % 10).getCampos().put(
                            SubLegalizacionViaticosControladorEnum.SALDO
                                            .getValue(),
                            saldo);

        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaConcepto
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConcepto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(
                        SubLegalizacionViaticosControladorEnum.CODIGO_CONCEPTO
                                        .getValue(),
                        registroAux.getCampos().get(
                                        SubLegalizacionViaticosControladorEnum.CODIGO_CONCEPTO
                                                        .getValue()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaConcepto
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConceptoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(
                        SubLegalizacionViaticosControladorEnum.CODIGO_CONCEPTO
                                        .getValue()),
                        "").toString();
    }

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
     **/
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
        if (valorTotal > total) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3927"));

        }

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
     * 
     * 
     * @return true
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        try {
            ejbViaticosCero.actualizarDetalleLegalizaViaticos(compania,
                            Integer.parseInt(ano), numero,
                            registro.getCampos()
                                            .get(SubLegalizacionViaticosControladorEnum.NUMERO_AFECTADO
                                                            .getValue())
                                            .toString(),
                            registro.getCampos()
                                            .get(SubLegalizacionViaticosControladorEnum.CODIGO_CONCEPTO
                                                            .getValue())
                                            .toString(),
                            SessionUtil.getUser().getCodigo(),
                            new BigDecimal(registro.getCampos()
                                            .get(GeneralParameterEnum.VALOR
                                                            .getName())
                                            .toString()),
                            new BigDecimal(regSubLegalizacion.getCampos()
                                            .get(
                                                            GeneralParameterEnum.VALOR
                                                                            .getName())
                                            .toString()));

        }
        catch (NumberFormatException | SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
            return false;

        }
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
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
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.ANO.getName());
        registro.getCampos().remove(GeneralParameterEnum.NUMERO.getName());
        registro.getCampos().remove("TIPO_VIATICO");
        registro.getCampos().remove(GeneralParameterEnum.TERCERO.getName());
        registro.getCampos().remove(GeneralParameterEnum.SUCURSAL.getName());
        registro.getCampos().remove("NOMBRECONCEPTO");

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro) {
        indice = registro.getIndice();
        regSubLegalizacion = new Registro(new HashMap<>(registro.getCampos()));
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>

        String[] campos = { "rid" };
        Object[] valores = { ridSub };

        SessionUtil.redireccionarPorFormulario(SessionUtil.getModulo(),
                        Integer.toString(
                                        GeneralCodigoFormaEnum.LEGALIZACION_VIATICOS_CONTROLADOR
                                                        .getCodigo()),
                        campos, valores, true);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ano
     * 
     * @return ano
     */
    public String getAno() {
        return ano;
    }

    /**
     * Asigna la variable ano
     * 
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(String ano) {
        this.ano = ano;
    }

    /**
     * Retorna la variable numero
     * 
     * @return numero
     */
    public String getNumero() {
        return numero;
    }

    /**
     * Asigna la variable numero
     * 
     * @param numero
     * Variable a asignar en numero
     */
    public void setNumero(String numero) {
        this.numero = numero;
    }

    /**
     * Retorna la variable tercero
     * 
     * @return tercero
     */
    public String getTercero() {
        return tercero;
    }

    /**
     * Asigna la variable tercero
     * 
     * @param tercero
     * Variable a asignar en tercero
     */
    public void setTercero(String tercero) {
        this.tercero = tercero;
    }

    /**
     * Retorna la variable nombreTercero
     * 
     * @return nombreTercero
     */
    public String getNombreTercero() {
        return nombreTercero;
    }

    /**
     * Asigna la variable nombreTercero
     * 
     * @param nombreTercero
     * Variable a asignar en nombreTercero
     */
    public void setNombreTercero(String nombreTercero) {
        this.nombreTercero = nombreTercero;
    }

    /**
     * Retorna la variable indice
     * 
     * @return indice
     */
    public int getIndice() {
        return indice;
    }

    /**
     * Asigna la variable indice
     * 
     * @param indice
     * Variable a asignar en indice
     */
    public void setIndice(int indice) {
        this.indice = indice;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaConcepto
     * 
     * @return listaConcepto
     */
    public RegistroDataModelImpl getListaConcepto() {
        return listaConcepto;
    }

    /**
     * Asigna la lista listaConcepto
     * 
     * @param listaConcepto
     * Variable a asignar en listaConcepto
     */
    public void setListaConcepto(RegistroDataModelImpl listaConcepto) {
        this.listaConcepto = listaConcepto;
    }

    /**
     * Retorna la lista listaConcepto
     * 
     * @return listaConcepto
     */
    public RegistroDataModelImpl getListaConceptoE() {
        return listaConceptoE;
    }

    /**
     * Asigna la lista listaConcepto
     * 
     * @param listaConcepto
     * Variable a asignar en listaConcepto
     */
    public void setListaConceptoE(RegistroDataModelImpl listaConceptoE) {
        this.listaConceptoE = listaConceptoE;
    }

    /**
     * Retorna la variable auxiliar
     * 
     * @return auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * Asigna la variable auxiliar
     * 
     * @param auxiliar
     * Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}

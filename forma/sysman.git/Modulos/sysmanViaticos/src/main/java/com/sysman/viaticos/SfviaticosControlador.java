/*-
 * SfviaticosControlador.java
 *
 * 1.0
 *
 * 19/01/2018
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.viaticos;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import com.sysman.viaticos.enums.SfviaticosControladorEnum;
import com.sysman.viaticos.enums.SfviaticosControladorUrlEnum;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Formulario que gestiona los detalles de viaticos
 *
 * @version 1.0, 19/01/2018
 * @author spina
 * 
 * @version 2.0,29/01/2018
 * @author eamaya, Cambio de DSS,regeneracion de forma y correcciones
 * SonarQube
 * 
 */
@ManagedBean
@ViewScoped
public class SfviaticosControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private Map<String, Object> rid;
    /**
     * Esta variable se usa como auxiliar para subformularios y en
     * esta se alamcena el identificador del registro que se
     * selecciono
     */
    private String auxiliar;

    private String codigoSolicitud;
    private String nombreResponsable;
    private String descripcion;
    private String ano;
    private String numerosolicitud;
    private String tipoviatico;
    private Date fechaInicio;
    private Date fechaFinal;
    private boolean sabado;
    private boolean domingo;
    private boolean festivo;
    private String codigoTercero;
    private String escalafon;
    private String categoria;

    private String totalValDiasSinPer;
    private String totalValDiasPer;
    private String totalVlrconcepto;
    private String totalVlrabonado;
    private String totalSaldo;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que carga las cuentas del anio por el cual se creo la
     * solicitud
     */
    private RegistroDataModelImpl listaRubro;

    /**
     * Lista que carga las cuentas en la grilla del anio por el cual
     * se creo la solicitud
     */
    private RegistroDataModelImpl listaRubroE;
    /**
     * Lista que carga los conceptos a partir del anio de solicitud de
     * viatico
     */
    private RegistroDataModelImpl listaConcepto;
    /**
     * Lista que carga los conceptos en la grilla a partir del anio de
     * solicitud de viatico
     */
    private RegistroDataModelImpl listaConceptoE;
    /**
     * Lista que carga los terceros
     */
    private RegistroDataModelImpl listaTercero;
    /**
     * Lista que carga los terceros en la grilla
     */
    private RegistroDataModelImpl listaTerceroE;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    private Map<String, Object> parametrosEntrada;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtl;

    /**
     * Crea una nueva instancia de SfviaticosControlador
     */
    @SuppressWarnings("unchecked")
    public SfviaticosControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.SFVIATICOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();

            parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {

                codigoSolicitud = SysmanFunciones
                                .nvl(parametrosEntrada.get("codigoSolicitud"),
                                                "")
                                .toString();

                tipoviatico = SysmanFunciones
                                .nvl(parametrosEntrada.get("tipoviatico"), "")
                                .toString();
                numerosolicitud = SysmanFunciones
                                .nvl(parametrosEntrada.get("numerosolicitud"),
                                                "")
                                .toString();
                nombreResponsable = SysmanFunciones
                                .nvl(parametrosEntrada.get("nombreResponsable"),
                                                "")
                                .toString();
                descripcion = SysmanFunciones
                                .nvl(parametrosEntrada.get("descripcion"), "")
                                .toString();
                ano = SysmanFunciones
                                .nvl(parametrosEntrada.get("ano"), "")
                                .toString();

                fechaInicio = (Date) parametrosEntrada.get("fechaInicio");

                fechaFinal = (Date) parametrosEntrada.get("fechaFinal");

                sabado = (boolean) parametrosEntrada.get("sabado");

                domingo = (boolean) parametrosEntrada.get("domingo");

                festivo = (boolean) parametrosEntrada.get("festivo");

                codigoTercero = SysmanFunciones
                                .nvl(parametrosEntrada.get("codigoTercero"), "")
                                .toString();

                escalafon = parametrosEntrada.get("escalafon").toString();

                categoria = parametrosEntrada.get("categoria").toString();
            }
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
        enumBase = GenericUrlEnum.VI_DETALLE_VIATICOS;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaConcepto();
        cargarListaConceptoE();
        cargarListaTercero();
        cargarListaTerceroE();
        cargarListaRubro();
        cargarListaRubroE();
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
        parametrosListado.put(GeneralParameterEnum.CODIGO.getName(),
                        codigoSolicitud);
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaRubro
     *
     *
     */
    public void cargarListaRubro() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfviaticosControladorUrlEnum.URL4130
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaRubro = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    /**
     *
     * Carga la lista listaRubroE
     *
     *
     */
    public void cargarListaRubroE() {
        listaRubroE = listaRubro;
    }

    /**
     *
     * Carga la lista listaConcepto
     *
     *
     */
    public void cargarListaConcepto() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfviaticosControladorUrlEnum.URL4131
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano(new Date()));
        param.put(SfviaticosControladorEnum.ESCALAFON.getValue(), escalafon);

        param.put(SfviaticosControladorEnum.CATEGORIA.getValue(), categoria);

        listaConcepto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        SfviaticosControladorEnum.CODIGO_CONCEPTO.getValue());
    }

    /**
     *
     * Carga la lista listaConceptoE
     *
     *
     */
    public void cargarListaConceptoE() {
        listaConceptoE = listaConcepto;
    }

    /**
     *
     * Carga la lista listaTercero
     *
     *
     */
    public void cargarListaTercero() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SfviaticosControladorUrlEnum.URL4132
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ESTADO.getName(), "1");

        listaTercero = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.NUMERO_DCTO.getName());
    }

    /**
     *
     * Carga la lista listaTerceroE
     *
     *
     */
    public void cargarListaTerceroE() {
        listaTerceroE = listaTercero;
    }

    public void calcularTotales() {
        try {
            Map<String, Object> params = new TreeMap<>();
            params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            params.put(GeneralParameterEnum.CODIGO.getName(), codigoSolicitud);

            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SfviaticosControladorUrlEnum.URL4133
                                                                            .getValue())
                                            .getUrl(), params));

            if (rs != null) {
                totalValDiasSinPer = retornarConFormato(SysmanFunciones
                                .nvlDbl(rs.getCampos().get("VALDIASSINPER"),
                                                0));

                totalValDiasPer = retornarConFormato(SysmanFunciones
                                .nvlDbl(rs.getCampos().get("VALDIASPER"),
                                                0));

                totalVlrconcepto = retornarConFormato(SysmanFunciones
                                .nvlDbl(rs.getCampos().get("TOTAL"),
                                                0));

                totalVlrabonado = retornarConFormato(SysmanFunciones
                                .nvlDbl(rs.getCampos().get("VALOR_ABONADO"),
                                                0));

                totalSaldo = retornarConFormato(SysmanFunciones
                                .nvlDbl(rs.getCampos().get("SALDO"),
                                                0));
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo ejecutado al cambiar el control Concepto en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarConceptoC(int rowNum) {
        // METODO_NO_IMPLEMENTADO
    }

    /**
     * Metodo ejecutado al cambiar el control Tercero en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarTerceroC(int rowNum) {
        listaInicial.getDatasource().get(rowNum % 10).getCampos()
                        .put("NOMBREPERSONA", registro
                                        .getCampos()
                                        .get("NOMBREPERSONA"));
    }

    public String retornarConFormato(double valor) {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
        return new DecimalFormat("#,##0.00", dfs).format(valor);
    }

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.FRM_VIATICOS_CONTROLADOR
                                        .getCodigo()));

        parametrosEntrada.put("retorno", true);

        direccionador.setParametros(parametrosEntrada);
        SessionUtil.redireccionarForma(direccionador, SessionUtil.getModulo());
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>

    public void seleccionarFilaRubro(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("RUBRO",
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()));
    }

    public void seleccionarFilaRubroE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaConcepto
     *
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConcepto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put("CONCEPTO",
                        registroAux.getCampos()
                                        .get(SfviaticosControladorEnum.CODIGO_CONCEPTO
                                                        .getValue()));

        registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.CONSECUTIVO
                                                        .getName()));

        if (SysmanFunciones.validarVariableVacio(SysmanFunciones.nvl(
                        registro.getCampos()
                                        .get(GeneralParameterEnum.TERCERO
                                                        .getName()),
                        "").toString())) {

            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3954"));
            registro.getCampos().put("CONCEPTO", "");
            return;
        }

        validarDiasPernoctando();

    }

    private void validarDiasPernoctando() {

        try {

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            param.put(GeneralParameterEnum.TERCERO.getName(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.TERCERO
                                                            .getName()));

            Registro regPersonal = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SfviaticosControladorUrlEnum.URL5151
                                                                            .getValue())
                                            .getUrl(), param));

            if (regPersonal == null) {

                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3955")
                                .replace("#$TERCERO#$", registro.getCampos()
                                                .get(GeneralParameterEnum.TERCERO
                                                                .getName())
                                                .toString()));

                registro.getCampos().put("NUMDIASSINPER", SysmanFunciones.nvl(
                                ejbSysmanUtl.retornarDiasPernoctando(compania,
                                                false, fechaInicio,
                                                fechaFinal, sabado, domingo,
                                                festivo),
                                "0"));

                registro.getCampos().put("NUMDIASPER", SysmanFunciones.nvl(
                                ejbSysmanUtl.retornarDiasPernoctando(compania,
                                                true, fechaInicio,
                                                fechaFinal, sabado, domingo,
                                                festivo),
                                "0"));

            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaConcepto
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConceptoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(SfviaticosControladorEnum.CODIGO_CONCEPTO
                                                        .getValue()),
                                        "")
                        .toString();

        registro.getCampos().put(GeneralParameterEnum.CONSECUTIVO.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.CONSECUTIVO
                                                        .getName()));
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTercero
     *
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTercero(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.TERCERO.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.NUMERO_DCTO
                                                        .getName()));

        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));

        registro.getCampos().put("NOMBREPERSONA", registroAux.getCampos()
                        .get("NOMBRECOMPLETO"));

    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTercero
     *
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTerceroE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.NUMERO_DCTO
                                                        .getName()),
                                        "")
                        .toString();

        registro.getCampos().put(GeneralParameterEnum.SUCURSAL.getName(),
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.SUCURSAL
                                                        .getName()));

        registro.getCampos().put("NOMBREPERSONA", registroAux.getCampos()
                        .get("NOMBRECOMPLETO"));
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
        calcularTotales();

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
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().put("TIPO_VIATICO", tipoviatico);
        registro.getCampos().put("DESCRIPCION", descripcion);
        registro.getCampos().put("NUMERO", codigoSolicitud);
        registro.getCampos().put("ANO", ano);
        registro.getCampos().remove("NOMBREPERSONA");

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
        calcularTotales();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     *
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
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>

        calcularTotales();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
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
        calcularTotales();
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
        registro.getCampos().remove("NOMBREPERSONA");

    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // metodo heredado
    }

    // <SET_GET_ATRIBUTOS>
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
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
     * Retorna la lista listaTercero
     * 
     * @return listaTercero
     */
    public RegistroDataModelImpl getListaTercero() {
        return listaTercero;
    }

    /**
     * Asigna la lista listaTercero
     * 
     * @param listaTercero
     * Variable a asignar en listaTercero
     */
    public void setListaTercero(RegistroDataModelImpl listaTercero) {
        this.listaTercero = listaTercero;
    }

    /**
     * Retorna la lista listaTercero
     * 
     * @return listaTercero
     */
    public RegistroDataModelImpl getListaTerceroE() {
        return listaTerceroE;
    }

    /**
     * Asigna la lista listaTercero
     * 
     * @param listaTercero
     * Variable a asignar en listaTercero
     */
    public void setListaTerceroE(RegistroDataModelImpl listaTerceroE) {
        this.listaTerceroE = listaTerceroE;
    }

    /**
     * Retorna la lista listaRubro
     * 
     * @return listaRubro
     */
    public RegistroDataModelImpl getListaRubro() {
        return listaRubro;
    }

    /**
     * Asigna la lista listaRubro
     * 
     * @param listaRubro
     * Variable a asignar en listaRubro
     */
    public void setListaRubro(RegistroDataModelImpl listaRubro) {
        this.listaRubro = listaRubro;
    }

    /**
     * Retorna la lista listaRubro
     * 
     * @return listaRubro
     */
    public RegistroDataModelImpl getListaRubroE() {
        return listaRubroE;
    }

    /**
     * Asigna la lista listaRubro
     * 
     * @param listaRubro
     * Variable a asignar en listaRubro
     */
    public void setListaRubroE(RegistroDataModelImpl listaRubroE) {
        this.listaRubroE = listaRubroE;
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

    public String getCodigoSolicitud() {
        return codigoSolicitud;
    }

    public void setCodigoSolicitud(String codigoSolicitud) {
        this.codigoSolicitud = codigoSolicitud;
    }

    public String getNombreResponsable() {
        return nombreResponsable;
    }

    public void setNombreResponsable(String nombreResponsable) {
        this.nombreResponsable = nombreResponsable;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getNumerosolicitud() {
        return numerosolicitud;
    }

    public void setNumerosolicitud(String numerosolicitud) {
        this.numerosolicitud = numerosolicitud;
    }

    public String getTipoviatico() {
        return tipoviatico;
    }

    public void setTipoviatico(String tipoviatico) {
        this.tipoviatico = tipoviatico;
    }

    public String getTotalValDiasSinPer() {
        return totalValDiasSinPer;
    }

    public void setTotalValDiasSinPer(String totalValDiasSinPer) {
        this.totalValDiasSinPer = totalValDiasSinPer;
    }

    public String getTotalValDiasPer() {
        return totalValDiasPer;
    }

    public void setTotalValDiasPer(String totalValDiasPer) {
        this.totalValDiasPer = totalValDiasPer;
    }

    public String getTotalVlrconcepto() {
        return totalVlrconcepto;
    }

    public void setTotalVlrconcepto(String totalVlrconcepto) {
        this.totalVlrconcepto = totalVlrconcepto;
    }

    public String getTotalVlrabonado() {
        return totalVlrabonado;
    }

    public void setTotalVlrabonado(String totalVlrabonado) {
        this.totalVlrabonado = totalVlrabonado;
    }

    public String getTotalSaldo() {
        return totalSaldo;
    }

    public void setTotalSaldo(String totalSaldo) {
        this.totalSaldo = totalSaldo;
    }

    public Map<String, Object> getRid() {
        return rid;
    }

    public void setRid(Map<String, Object> rid) {
        this.rid = rid;
    }

    public String getCodigoTercero() {
        return codigoTercero;
    }

    public void setCodigoTercero(String codigoTercero) {
        this.codigoTercero = codigoTercero;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}

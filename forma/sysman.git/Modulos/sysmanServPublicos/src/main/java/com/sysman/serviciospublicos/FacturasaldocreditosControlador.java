/*-
 * FacturasaldocreditosControlador.java
 *
 * 1.0
 * 
 * 19 de sept. de 2016
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyac�.
 * All rights reserved.
 */

package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosTresRemote;
import com.sysman.serviciospublicos.enums.FacturasaldocreditosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Contiene la migracion de la pestana Saldo Credito del formulario
 * "Factura" en Access. Se compone de dos subformularios definidos en
 * Access como FrmSaldoCredito y SubFacturadoSCredito.
 * 
 * @version 1.0, 19 de sept. de 2016
 * @author amonroy
 * 
 * @author eamaya
 * @version 2, 22/05/2017 Proceso de Refactoring y Manejo de EJBs
 *
 */
@ManagedBean
@ViewScoped

public class FacturasaldocreditosControlador extends BeanBaseDatosAcmeImpl {
    /**
     * Constante que almacena la compania de la factura
     */
    private final String compania;
    /**
     * Constante que almacena el formato de Moneda para el pie de los
     * subformularios
     */
    private final String formatoMoneda;
    /**
     * Constante definida porque se realiza el llamado al campo VALOR
     * varias veces
     */
    private final String strValor;
    /**
     * Constante definida porque se realiza el llamado al campo
     * VALOR_SALDO varias veces
     */
    private final String strValorSaldo;
    /**
     * Constante definida porque se realiza el llamado al campo CODIGO
     * varias veces
     */
    private final String strCodigo;
    /**
     * Constante definida porque se realiza el llamado al campo
     * PERIODO varias veces
     */
    private final String strPeriodo;
    /**
     * Constante definida porque se realiza el llamado al campo FECHA
     * varias veces
     */
    private final String strFecha;

    /**
     * Constante definida porque se realiza el llamado a la tabla
     * SP_FACTURADO
     */
    private final String tablaFacturado;
    /**
     * Constante definida porque se realiza el llamado a la tabla
     * SP_TBLHIST_SALDO_CREDITO
     */
    private final String tablaTbHisSaldo;
    /**
     * Constante definida por el n�mero de veces que se llama el
     * campo COMPANIA
     */
    private final String strCompania;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el anio de la factura
     */
    private String ano;
    /**
     * Atributo que almacena el ciclo de la factura
     */
    private String ciclo;
    /**
     * Atributo que almacena el codigo de ruta de la factura
     */
    private String codigoruta;
    /**
     * Atributo que almacena el periodo de la factura
     */
    private String periodo;
    /**
     * Atributo que almacena el valor del total de saldo credito para
     * la factura
     */
    private String totalSaldoCredito;
    /**
     * Atributo que almacena el valor del total del credito abonado en
     * la factura
     */
    private String totalCreditoAbonado;
    /**
     * Atributo que almacena el valor del numero consecutivo para
     * realizar la insercion en la tabla "SP_TBLHIST_SALDO_CREDITO"
     */
    private int consecutivo;
    /**
     * Atributo que almacena el valor del numero consecutivo
     */
    private int codsalcredito;
    /**
     * Atributo que permite definir si se puede o no editar un
     * registro seleccionado en el subformulario "FrmSaldoCredito"
     */
    private boolean eliminaSaldos;
    /**
     * Atributo que almacena el valor de la nota credito de la factura
     */
    private String notacredito;
    /**
     * Atributo que almacena el valor del campo bancoperproceso de la
     * factura
     */
    private String bancoperproceso;
    /**
     * Atributo que almacena el indice del registro selecionado en el
     * subformulario "FrmSaldoCredito"
     */
    private int indiceFrmsaldocredito;
    /**
     * Atributo que permite almacenar valores temporales
     */
    private String auxiliar;
    /**
     * Atributo que almacena el valor del anio
     */
    private String anoAux;

    /**
     * Atributo usado como bandera para realizar la inserci�n de un
     * nuevo registro dependiendo la opcion seleccionada en el dialogo
     * "confirmarValor(DG133)"
     */
    private boolean confirmarValorVisible;
    /**
     * Permite actualizar el mensaje que se visualiza en el dialogo
     * para confirmar el nuevo valor del saldo
     */
    private String mensajeDialogoValor;

    // <DECLARAR_LISTAS>
    /**
     * Listado de conceptos para el subformulario
     * "SubFacturadoSCredito"
     */
    private List<Registro> listaConcepto;
    /**
     * Listado de nombres de nobre de conceptos para el subformulario
     * "SubFacturadoSCredito"
     */
    private List<Registro> listaNombre;
    /**
     * Listado de a�os para el subformulario "FrmSaldoCredito"
     */
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Listado para el comboBox de Periodo para el subformulario
     * "FrmSaldoCredito"
     */
    private RegistroDataModelImpl listaPeriodo;
    /**
     * Listado para el comboBox de Periodo en el subformulario
     * "FrmSaldoCredito" cuando se edita un registro
     */
    private RegistroDataModelImpl listaPeriodoE;
    /**
     * Listado para el comboBox de banco en el subformulario
     * "FrmSaldoCredito"
     */
    private RegistroDataModelImpl listabanco;
    /**
     * Listado para el comboBox de banco en el subformulario
     * "FrmSaldoCredito" cuando se edita un registro
     */
    private RegistroDataModelImpl listabancoE;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    /**
     * Listado de registros que se visualizan en el subformulario
     * "Frmsaldocredito"
     */
    private List<Registro> listaFrmsaldocredito;
    /**
     * Listado de registros que se visualizan en el subformulario
     * "Subfacturadoscredito"
     */
    private List<Registro> listaSubfacturadoscredito;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Referencia para el registro seleccionado en el subformulario
     * "Subfacturadoscredito"
     */
    private Registro registroSubSubFacturadoSCredito;
    /**
     * Referencia para el registro seleccionado en el subformulario
     * "Frmsaldocredito"
     */
    private Registro registroSubFrmSaldoCredito;

    @EJB
    private EjbSysmanUtilRemote ejbConsecutivo;

    @EJB
    private EjbServiciosPublicosTresRemote ejbServiciosPublicosTres;

    // </DECLARAR_ADICIONALES>
    /**
     * Constructor de la clase FacturasaldocreditosControlador que
     * recibe los parametros de inicializacion desde el formulario
     * "Facturaintegrado".
     * 
     * Inicializacion de los registros de los subformularios.
     */
    public FacturasaldocreditosControlador() {
        super();
        compania = SessionUtil.getCompania();
        formatoMoneda = "$ #,##0.00";
        strValor = "VALOR";
        strValorSaldo = "VALOR_SALDO";
        strCodigo = "CODIGO";
        strPeriodo = "PERIODO";
        strFecha = "FECHA";
        tablaFacturado = "SP_FACTURADO";
        tablaTbHisSaldo = "SP_TBLHIST_SALDO_CREDITO";
        strCompania = "COMPANIA";

        try {
            numFormulario = GeneralCodigoFormaEnum.FACTURASALDOCREDITOS_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                ciclo = parametrosEntrada.get("ciclo").toString();
                codigoruta = parametrosEntrada.get("codigoruta").toString();
                ano = parametrosEntrada.get("ano").toString();
                periodo = parametrosEntrada.get("periodo").toString();
                notacredito = parametrosEntrada.get("notacredito").toString();
                bancoperproceso = parametrosEntrada.get("bancoperproceso")
                                .toString();

            }
            registroSubSubFacturadoSCredito = new Registro(
                            new HashMap<String, Object>());
            registroSubFrmSaldoCredito = new Registro(
                            new HashMap<String, Object>());
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(FacturasaldocreditosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListabanco();
        cargarListabancoE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaConcepto();
        cargarListaNombre();
        cargarListaAno();

        // </CARGAR_LISTA>
    }

    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaFrmsaldocredito();
        cargarListaSubfacturadoscredito();

        // </CARGAR_LISTAS_SUBFORM>
    }

    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        listaSubfacturadoscredito = null;
        listaFrmsaldocredito = null;
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.SP_USUARIO;
        buscarLlave();
        iniciarListasSub();
        cargarListaAno();
        cargarListabanco();
        cargarListabancoE();
        cargarListaPeriodo();
        asignarOrigenDatos();

        registroSubFrmSaldoCredito.getCampos().put(strFecha,
                        new Date());

    }

    /**
     * Realiza la carga del listado de registros para el Subformulario
     * "Subfacturadoscredito"
     */
    public void cargarListaSubfacturadoscredito() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.CICLO.getName(),
                            ciclo);
            param.put(GeneralParameterEnum.CODIGORUTA.getName(),
                            codigoruta);
            param.put(GeneralParameterEnum.ANO.getName(),
                            ano);
            param.put(GeneralParameterEnum.PERIODO.getName(),
                            periodo);

            listaSubfacturadoscredito = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FacturasaldocreditosControladorUrlEnum.URL11018
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            tablaFacturado));

            // Permite obtener el valor total de saldo credito para la
            // factura, es el valor que se muestra en el pie del
            // subformulario "Subfacturadoscredit"

            Registro regSaldo = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FacturasaldocreditosControladorUrlEnum.URL35414
                                                                            .getValue())
                                            .getUrl(), param));
            totalSaldoCredito = new java.text.DecimalFormat(formatoMoneda)
                            .format(Double.parseDouble(regSaldo.getCampos()
                                            .get(strValor).toString()));
            // Permite obtener el valor total del credito abonado para
            // la
            // factura, este valor se muestra en el pie del
            // subformulario "Subfacturadoscredit"

            Registro regCredito = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FacturasaldocreditosControladorUrlEnum.URL21346
                                                                            .getValue())
                                            .getUrl(), param));
            totalCreditoAbonado = new java.text.DecimalFormat(formatoMoneda)
                            .format(Double.parseDouble(regCredito.getCampos()
                                            .get(strValor).toString()));

        }
        catch ( SystemException | SysmanException e) {
            Logger.getLogger(FacturasaldocreditosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Realiza la carga del listado de registros para el Subformulario
     * "Frmsaldocredito"
     */
    public void cargarListaFrmsaldocredito() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.CICLO.getName(),
                            ciclo);
            param.put(GeneralParameterEnum.CODIGORUTA.getName(),
                            codigoruta);
            param.put(GeneralParameterEnum.ANO.getName(),
                            ano);
            param.put(GeneralParameterEnum.PERIODO.getName(),
                            periodo);

            listaFrmsaldocredito = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FacturasaldocreditosControladorUrlEnum.URL19510
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            tablaTbHisSaldo));

            Registro reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FacturasaldocreditosControladorUrlEnum.URL1213
                                                                            .getValue())
                                            .getUrl(), param));

            notacredito = new java.text.DecimalFormat(formatoMoneda).format(
                            Double.parseDouble(reg.getCampos().get(strValor)
                                            .toString()));

        }
        catch ( SystemException | SysmanException e) {
            Logger.getLogger(FacturasaldocreditosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Realiza la carga del listado de registros para el combo de
     * Concepto en el subformulario "Subfacturadoscredit"
     */
    public void cargarListaConcepto() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaConcepto = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FacturasaldocreditosControladorUrlEnum.URL20824
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Realiza la carga del listado de registros para el combo de
     * Nombre en el subformulario "Subfacturadoscredit"
     */
    public void cargarListaNombre() {
        listaNombre = listaConcepto;
    }

    /**
     * Realiza la carga del listado de registros para el combo de Ano
     * en el subformulario "Frmsaldocredito"
     */
    public void cargarListaAno() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FacturasaldocreditosControladorUrlEnum.URL21857
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Realiza la carga del listado de registros para el combo de
     * Periodo en el subformulario "Frmsaldocredito"
     */
    public void cargarListaPeriodo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturasaldocreditosControladorUrlEnum.URL31099
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.ANO.getName(),
                        ano);

        listaPeriodo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);
    }

    /**
     * Realiza la carga del listado de registros para el combo de
     * Periodo en el subformulario "Frmsaldocredito" cuando se realiza
     * la edicion de un registro
     */
    public void cargarListaPeriodoE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturasaldocreditosControladorUrlEnum.URL32099
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.ANO.getName(),
                        ano);

        listaPeriodoE = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);
    }

    /**
     * Realiza la carga del listado de registros para el combo de
     * banco en el subformulario "Frmsaldocredito"
     */
    public void cargarListabanco() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturasaldocreditosControladorUrlEnum.URL22842
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listabanco = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);
    }

    /**
     * Realiza la carga del listado de registros para el combo de
     * banco en el subformulario "Frmsaldocredito" cuando se realiza
     * la edicion de un registro
     */
    public void cargarListabancoE() {
        listabancoE = listabanco;

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Carga la lista de periodos de acuerdo al anio seleccionado
     */
    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        cargarListaPeriodo();

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Validaciones que se realizan al campo fecha del subformulario
     * "FrmSaldoCredito"
     */
    public void cambiarFecha() {
        try {
            confirmarValorVisible = true;

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            param.put(GeneralParameterEnum.CICLO.getName(),
                            ciclo);

            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FacturasaldocreditosControladorUrlEnum.URL42882
                                                                            .getValue())
                                            .getUrl(), param));

            Date fechaPreparacion = (Date) rs.getCampos()
                            .get("FECHA_PREPARACION");
            Date fechaNueva = (Date) registroSubFrmSaldoCredito.getCampos()
                            .get(strFecha);

            if (fechaNueva.before(fechaPreparacion)) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1649"));
                registroSubFrmSaldoCredito.getCampos()
                                .put(strFecha, null);

            }

            param.put(GeneralParameterEnum.CODIGORUTA.getName(),
                            codigoruta);

            Registro rs1 = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FacturasaldocreditosControladorUrlEnum.URL23473
                                                                            .getValue())
                                            .getUrl(), param));

            Date fechaPerProceso = (Date) rs1.getCampos()
                            .get("FECHAPAGOPERPROCESO");

            if (fechaPerProceso != null) {
                if (fechaNueva.before(fechaPerProceso)) {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB1650"));

                }
            }
            else if (!Boolean.parseBoolean(
                            rs.getCampos().get("INDCALCULADO").toString())) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1651"));

            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Genera un dialogo de confirmacion para el nuevo valor de saldo
     * que se esta registrando
     */
    public void cambiarValorSaldo() {
        // <CODIGO_DESARROLLADO>
        String saldo = new java.text.DecimalFormat(formatoMoneda).format(Double
                        .parseDouble(registroSubFrmSaldoCredito.getCampos()
                                        .get(strValorSaldo).toString()));

        mensajeDialogoValor = idioma.getString("TB_TB1645").replace("#saldo#",
                        saldo);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Actualiza el valor del atributo bandera definido para realizar
     * la insercion de un nuevo registro
     */
    public void aceptarconfirmarValor() {
        // <CODIGO_DESARROLLADO>
        confirmarValorVisible = true;
        agregarRegistroSub();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Actualiza el valor del atributo bandera definido para realizar
     * la insercion de un nuevo registro
     */
    public void cancelarconfirmarValor() {
        // <CODIGO_DESARROLLADO>

        confirmarValorVisible = true;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Carga la lista de periodos de acuerdo al anio seleccionado
     * cuando se esta editando un registro
     */
    public void cambiarAnoC() {
        cargarListaPeriodoE();
    }

    /**
     * Metodo implementado al cambiar la opcion seleccionada en el
     * conBox de Periodo
     */
    public void cambiarPeriodoC() {
        throw new UnsupportedOperationException();
    }

    /**
     * Acciones a realizar cuando se ejecuten cambios en el campo de
     * NotaCredito
     */
    public void cambiarNotaCredito() {
        if ((" ").equalsIgnoreCase(bancoperproceso)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1565"));
        }
        else {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1566"));
        }
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * Actualizacion del campo Periodo al seleccionar en el comboBox
     * Periodo del subformulario "Frmsaldocredito"
     * 
     * @param event
     * Evento generado al seleccionar una opcion en el comboBox
     * Periodo
     */
    public void seleccionarFilaPeriodo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSubFrmSaldoCredito.getCampos().put(strPeriodo,
                        registroAux.getCampos().get("MES"));
    }

    /**
     * Actualizacion del campo Periodo al seleccionar en el comboBox
     * Periodo cuando se est� editando un registro
     * 
     * @param event
     * Evento generado al seleccionar una opcion en el comboBox
     * Periodo
     */
    public void seleccionarFilaPeriodoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvlStr(
                        registroAux.getCampos().get("MES").toString(), "");
    }

    /**
     * Actualizacion del campo Banco al seleccionar en el comboBox
     * banco del subformulario "Frmsaldocredito"
     * 
     * @param event
     * Evento generado al seleccionar una opcion en el comboBox
     * Periodo
     */
    public void seleccionarFilabanco(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registroSubFrmSaldoCredito.getCampos().put(
                        GeneralParameterEnum.BANCO.getName(),
                        registroAux.getCampos().get(strCodigo));
    }

    /**
     * Actualizacion del campo banco al seleccionar en el comboBox
     * Periodo cuando se est� editando un registro
     * 
     * @param event
     * Evento generado al seleccionar una opcion en el comboBox
     * Periodo
     */
    public void seleccionarFilabancoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvlStr(
                        registroAux.getCampos().get(strCodigo).toString(), "");
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_BOTONES>
    /**
     * Redirecciona al formulario "Subfrmhistsaldocredito"(1106) y
     * realiza el envio de los paramtros necesario para la carga del l
     * formulario
     */
    public void oprimircmdSaldoCredito() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "ciclo", "codigoruta", "ano", "periodo",
                            "notacredito", "bancoperproceso" };
        Object[] valores = { ciclo, codigoruta, ano, periodo, notacredito,
                             bancoperproceso };

        SessionUtil.redireccionar("/subfrmhistsaldocredito.sysman", campos,
                        valores);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    /**
     * Agrega un registro al subformulario "Subfacturadoscredit"
     */
    public void agregarRegistroSubSubfacturadoscredito() {
        // METODO_NO_IMPLEMENTADO
    }

    /**
     * Acciones realizadas al editar un registro del subformulario
     * "Subfacturadoscredit"
     * 
     * @param event
     * Evento generado al editar un registro del subformulario
     */
    public void editarRegSubSubfacturadoscredito(RowEditEvent event) {
        // METODO_NO_IMPLEMENTADO
    }

    /**
     * Acciones realizadas al eliminar un registro del subformulario
     * "Subfacturadoscredit"
     * 
     * @param reg
     * El rgistro que va a ser elimmindo
     */
    public void eliminarRegSubSubfacturadoscredito(Registro reg) {
        // METODO_NO_IMPLEMENTADO
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Subfacturadoscredito
     *
     */
    public void cancelarEdicionSubfacturadoscredito() {
        cargarListaSubfacturadoscredito();
        cargarListaFrmsaldocredito();
    }

    /**
     * Metodo que deselecciona el registro que se estaba editando en
     * el formulario "Subfacturadoscredit"
     */
    public void onCancelSubfacturadoscredito() {
        cargarListaSubfacturadoscredito();
        cargarListaFrmsaldocredito();
    }

    /**
     * Evalua el valor del campo valor_saldo y dependiendo el
     * resultado permite �a insercion del nuevo registro
     */
    public void agregarRegistroSubFrmsaldocredito() {
        if (registroSubFrmSaldoCredito.getCampos()
                        .get(strValorSaldo) != null) {
            if (Integer.parseInt(registroSubFrmSaldoCredito.getCampos()
                            .get(strValorSaldo).toString()) <= 0) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1550"));
            }
            else {
                cambiarValorSaldo();
                JsfUtil.ejecutarJavaScript("PF('DG133').show()");
            }
        }
    }

    /**
     * Agrega un registro al subformulario "Frmsaldocredito"
     */
    public void agregarRegistroSub() {

        if (confirmarValorVisible) {
            try { // <CODIGO DESARROLLADO>

                registroSubFrmSaldoCredito.getCampos().put(strCompania,
                                compania);
                registroSubFrmSaldoCredito.getCampos().put("CICLO",
                                ciclo);
                registroSubFrmSaldoCredito.getCampos().put("CODIGORUTA",
                                codigoruta);
                registroSubFrmSaldoCredito.getCampos().put("ANOCREADO",
                                ano);
                registroSubFrmSaldoCredito.getCampos().put(
                                "PERIODOCREADO",
                                periodo);

                String anioAux = String.valueOf(SysmanFunciones
                                .ano((Date) registroSubFrmSaldoCredito
                                                .getCampos()
                                                .get(strFecha)));

                String periodoAux = String.valueOf(SysmanFunciones.mes(
                                (Date) registroSubFrmSaldoCredito
                                                .getCampos()
                                                .get(strFecha)));
                periodoAux = periodoAux.length() == 1
                    ? "0" + periodoAux
                    : periodoAux;

                registroSubFrmSaldoCredito.getCampos().put("ANO",
                                anioAux);
                registroSubFrmSaldoCredito.getCampos().put(strPeriodo,
                                periodoAux);

                // Realiza el calculo del siguiente consecutivo en la
                // tabla "SP_TBLHIST_SALDO_CREDITO"
                // Realiza el calculo del siguiente consecutivo en la
                // tabla "SP_TBLHIST_SALDO_CREDITO"
                codsalcredito = (int) ejbConsecutivo
                                .generarConsecutivoConValorInicial(
                                                tablaTbHisSaldo,
                                                "COMPANIA=" +
                                                    compania
                                                    + " AND CODIGORUTA = "
                                                    + codigoruta,
                                                "CODSALDOCREDITO", "1");

                int consecutivoAux = (int) ejbConsecutivo
                                .generarConsecutivoConValorInicial(
                                                tablaTbHisSaldo,
                                                "COMPANIA=" +
                                                    compania
                                                    + " AND CODIGORUTA = "
                                                    + codigoruta,
                                                "CONSECUTIVO", "1");

                registroSubFrmSaldoCredito.getCampos().put(
                                "CONSECUTIVO",
                                consecutivoAux);
                // Asignacion del valor para el campo
                // USUARIOREG del registro
                registroSubFrmSaldoCredito.getCampos().put("USUARIOREG",
                                SessionUtil.getUser().getCodigo());

                registroSubFrmSaldoCredito.getCampos().put(
                                GeneralParameterEnum.CREATED_BY.getName(),
                                SessionUtil.getUser().getCodigo());

                registroSubFrmSaldoCredito.getCampos().put(
                                GeneralParameterEnum.DATE_CREATED.getName(),
                                new Date());

                // Actualiza el valor del campo valor total
                // cada vez que se realiza una inserci�n en el
                // formulario"Frmsaldocredito"

                if (!validaReg()) {
                    return;
                }

                // </CODIGO DESARROLLADO>

                UrlBean urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FacturasaldocreditosControladorUrlEnum.URL39459
                                                                .getValue());
                requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                                registroSubFrmSaldoCredito.getCampos());

                cargarListaFrmsaldocredito();

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_INGRESADO"));

                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);

                param.put(GeneralParameterEnum.CICLO.getName(),
                                ciclo);

                param.put(GeneralParameterEnum.CODIGORUTA.getName(),
                                codigoruta);

                param.put(GeneralParameterEnum.ANO.getName(),
                                registroSubFrmSaldoCredito.getCampos()
                                                .get("ANO").toString());

                param.put(GeneralParameterEnum.PERIODO.getName(),
                                registroSubFrmSaldoCredito.getCampos()
                                                .get(strPeriodo).toString());

                param.put(GeneralParameterEnum.CONSECUTIVO.getName(),
                                consecutivoAux);

                Registro reg = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                FacturasaldocreditosControladorUrlEnum.URL2222
                                                                                .getValue())
                                                .getUrl(), param));

                String sub = idioma.getString("TB_TB1560");

                fAuditoriaGeneral(reg, sub);

            }
            catch (SystemException ex) {
                Logger.getLogger(FacturasaldocreditosControlador.class
                                .getName())
                                .log(Level.SEVERE, null, ex);
                JsfUtil.agregarMensajeError(ex.getMessage());
            }
            finally {
                registroSubFrmSaldoCredito = new Registro(
                                new HashMap<String, Object>());
            }
        }
        else {
            return;
        }

        registroSubFrmSaldoCredito.getCampos().put(strFecha,
                        new Date());
    }

    /**
     * Acciones realizadas al editar un registro del subformulario
     * "Frmsaldocredito"
     * 
     * Realiza el llamado a la funcion AuditoriaGeneral indicando la
     * edicion de un registro del subformulario
     * 
     * @param event
     * Evento generado al editar un registro del subformulario
     */
    public void editarRegSubFrmsaldocredito(RowEditEvent event) {
        try {
            Registro reg = (Registro) event.getObject();

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.FECHA.getName(),
                            reg.getCampos().get("FECHA"));

            param.put(GeneralParameterEnum.OBSERVACIONES.getName(),
                            reg.getCampos().get("OBSERVACION"));

            param.put("ANIOCREADO",
                            reg.getCampos().get("ANIOCREADO"));

            param.put(GeneralParameterEnum.BANCO.getName(),
                            reg.getCampos().get(GeneralParameterEnum.BANCO
                                            .getName()));

            param.put(strValorSaldo,
                            reg.getCampos().get(strValorSaldo));

            param.put(GeneralParameterEnum.ANO.getName(), anoAux);

            param.put(GeneralParameterEnum.USUARIO.getName(),
                            reg.getCampos().get("USUARIOREG"));

            param.put(GeneralParameterEnum.PERIODO.getName(),
                            reg.getCampos().get("PERIODOCREADO"));

            fAuditoriaGeneral(reg, idioma.getString("TB_TB1538"));

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FacturasaldocreditosControladorUrlEnum.URL32534
                                                            .getValue());
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                            reg.getCampos(), reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_MODIFICADO"));

        }
        catch (SystemException ex) {
            Logger.getLogger(FacturasaldocreditosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally

        {
            cargarListaFrmsaldocredito();
        }

    }

    /**
     * Acciones realizadas al eliminar un registro del subformulario
     * "Frmsaldocredito"
     * 
     * @param reg
     * El rgistro que va a ser elimmindo
     */
    public void eliminarRegSubFrmsaldocredito(Registro reg) {

        try {

            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            FacturasaldocreditosControladorUrlEnum.URL45620
                                                            .getValue());
            requestManager.delete(urlDelete.getUrl(),
                            reg.getLlave());

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));

            cargarListaFrmsaldocredito();

            fAuditoriaGeneral(reg, idioma.getString("TB_TB1568"));

        }
        catch (SystemException ex) {
            Logger.getLogger(FacturasaldocreditosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        validaReg();
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado para el subformulario Frmsaldocredito
     *
     */
    public void cancelarEdicionFrmsaldocredito() {
        cargarListaFrmsaldocredito();
    }

    /**
     * Metodo que deselecciona el registro que se estaba editando en
     * el formulario "Frmsaldocredito"
     */
    public void onCancelFrmsaldocredito() {
        cargarListaFrmsaldocredito();
        registroSubFrmSaldoCredito = new Registro(
                        new HashMap<String, Object>());
    }

    /**
     * Obtiene el indice del registro que se esta editando para no
     * perder su referencia. Actualiza el listado del combo de Periodo
     * cuando se esta editando un registro
     * 
     * @param reg
     * Registro que esta siendo editado
     */
    public void activarEdicionFrmsaldocredito(Registro reg) {
        indiceFrmsaldocredito = reg.getIndice();
        anoAux = reg.getCampos().get("ANO").toString();
        cargarListaPeriodoE();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    /**
     * Realiza la ejecucion de la funcion AuditoriaGeneral
     * 
     * @param reg
     * El registro con la informacion a almacenar en la tabla
     * "SP_AUDIGEN"
     * @param subprocesop
     * El tipo de proceso que se est� realizando: crear, eliminar,
     * editar
     */
    public void fAuditoriaGeneral(Registro reg, String subprocesop) {
        try {
            reg.getCampos().put("ANO", anoAux);
            String usuario = SessionUtil.getUser().getCodigo();
            String macroproceso = "SALDOS CREDITO";
            String subproceso = subprocesop;
            String anio = String.valueOf(SysmanFunciones
                            .ano(new Date()));
            String periodoA = String.valueOf(SysmanFunciones
                            .mes(new Date()));
            periodoA = periodoA.length() == 1
                ? "0" + periodoA
                : periodoA;
            String codinterno = SysmanFunciones
                            .nvl(reg.getCampos().get("CODINTERNO"), " ")
                            .toString();
            // Para formar la descripcion
            String valor = reg.getCampos().get(strValorSaldo).toString();
            String valorDes = reg.getCampos().get("VALORDES").toString();
            String banco = reg.getCampos()
                            .get(GeneralParameterEnum.BANCO.getName())
                            .toString();

            String fecha = reg.getCampos().get(strFecha) == null
                ? " "
                : SysmanFunciones.convertirAFechaCadena(
                                (Date) reg.getCampos().get(strFecha));
            String fechareg = reg.getCampos().get("FECHAREG") == null
                ? "01/01/1900"
                : SysmanFunciones.convertirAFechaCadena(
                                (Date) reg.getCampos().get("FECHAREG"));

            // Fin campos descripcion

            String descripcion = "ValorSaldo:" + valor + ";Descontado:"
                + valorDes
                + ";Banco:" + banco + ";Fecha:" + fecha
                + ";Fecha Sistema:" + fechareg + ";NotaCredito:" + notacredito
                + " ";

            // Ejecuta la funcion

            ejbServiciosPublicosTres.auditoriaGeneral(compania, usuario,
                            macroproceso, subproceso, Integer.parseInt(anio),
                            periodoA,
                            codinterno, descripcion);

        }
        catch (NumberFormatException | SystemException | ParseException e) {
            Logger.getLogger(FacturasaldocreditosControlador.class.getName())
                            .log(Level.SEVERE, null, e);

            JsfUtil.agregarMensajeError(e.getMessage());

        }

    }

    /**
     * Evalua el valor de los campos bancoperproceso, Valor_Saldo y de
     * acuerdo a su resultado genera unos mensajes informativos al
     * usuario
     * 
     * @return Si los valores ingresados son adecuados para realizar
     * la insercion del registro en el subformulario "Frmsaldocredito"
     */
    public boolean validaReg() {
        boolean valida = true;
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            param.put(GeneralParameterEnum.CICLO.getName(),
                            ciclo);

            param.put(GeneralParameterEnum.CODIGORUTA.getName(),
                            codigoruta);

            param.put(GeneralParameterEnum.ANO.getName(), ano);

            param.put(GeneralParameterEnum.PERIODO.getName(), periodo);

            Registro rs1;

            rs1 = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FacturasaldocreditosControladorUrlEnum.URL3535
                                                                            .getValue())
                                            .getUrl(), param));

            double valorFacturado = Double.parseDouble(
                            rs1.getCampos().get("VALOR_FACTURADO").toString());

            Registro regValor = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FacturasaldocreditosControladorUrlEnum.URL1213
                                                                            .getValue())
                                            .getUrl(), param));

            notacredito = regValor.getCampos().get(strValor).toString();

            if (("").equalsIgnoreCase(bancoperproceso)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1549"));
                valida = false;
            }
            else if ((valorFacturado >= Integer.parseInt(notacredito))
                && (" ").equalsIgnoreCase(bancoperproceso)) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1551"));
                valida = false;
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        cambiarNotaCredito();
        return valida;
    }

    // </METODOS_ADICIONALES>
    /**
     * Se envia la referencia desde el formulario para poder cargar el
     * formulario "facturaintegrado" al elegir la opcion de cerrar
     */
    public void ejecutarrcCerrar() {
        HashMap<String, Object> ridR = new HashMap<>();
        ridR.put(strCompania, compania);
        ridR.put("CICLO", ciclo);
        ridR.put("CODIGORUTA", codigoruta);

        String[] campos = { "rid" };
        Object[] valores = { ridR };

        SessionUtil.redireccionar("/facturaintegrado.sysman", campos, valores);
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(strCompania, compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1087-DESPUES_INSERTAR Private Sub Form_AfterInsert()
         * AuditarModif Me, 1, Me!Compania & "^" & Me!Ciclo & "^" &
         * Me!CodigoRuta End Sub
         */
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1087-DESPUES_ACTUALIZAR Private Sub Form_AfterUpdate()
         * Dim str As String If Not Me.NewRecord Then
         * AudRegistroComparar Me, Me!Compania & "^" & Me!Ciclo & "^"
         * & Me!CodigoRuta, Me!Compania, Me!Ciclo, Me!CodigoRuta End
         * If End Sub
         */
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1087-ANTES_ELIMINAR Private Sub Form_Delete(Cancel As
         * Integer) AuditarModif Me, 2, Me!Compania & "^" & Me!Ciclo &
         * "^" & Me!CodigoRuta End Sub
         */
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public String getTotalSaldoCredito() {
        return totalSaldoCredito;
    }

    public void setTotalSaldoCredito(String totalSaldoCredito) {
        this.totalSaldoCredito = totalSaldoCredito;
    }

    public String getTotalCreditoAbonado() {
        return totalCreditoAbonado;
    }

    public void setTotalCreditoAbonado(String totalCreditoAbonado) {
        this.totalCreditoAbonado = totalCreditoAbonado;
    }

    public int getConsecutivo() {
        return consecutivo;
    }

    public void setConsecutivo(int consecutivo) {
        this.consecutivo = consecutivo;
    }

    public String getCodigoruta() {
        return codigoruta;
    }

    public void setCodigoruta(String codigoruta) {
        this.codigoruta = codigoruta;
    }

    public int getCodsalcredito() {
        return codsalcredito;
    }

    public void setCodsalcredito(int codsalcredito) {
        this.codsalcredito = codsalcredito;
    }

    public boolean isEliminaSaldos() {
        return eliminaSaldos;
    }

    public void setEliminaSaldos(boolean eliminaSaldos) {
        this.eliminaSaldos = eliminaSaldos;
    }

    public String getNotacredito() {
        return notacredito;
    }

    public void setNotacredito(String notacredito) {
        this.notacredito = notacredito;
    }

    public String getBancoperproceso() {
        return bancoperproceso;
    }

    public void setBancoperproceso(String bancoperproceso) {
        this.bancoperproceso = bancoperproceso;
    }

    public int getIndiceFrmsaldocredito() {
        return indiceFrmsaldocredito;
    }

    public void setIndiceFrmsaldocredito(int indiceFrmsaldocredito) {
        this.indiceFrmsaldocredito = indiceFrmsaldocredito;
    }

    public String getAnoAux() {
        return anoAux;
    }

    public void setAnoAux(String anoAux) {
        this.anoAux = anoAux;
    }

    public String getMensajeDialogoValor() {
        return mensajeDialogoValor;
    }

    public void setMensajeDialogoValor(String mensajeDialogoValor) {
        this.mensajeDialogoValor = mensajeDialogoValor;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaConcepto() {
        return listaConcepto;
    }

    public void setListaConcepto(List<Registro> listaConcepto) {
        this.listaConcepto = listaConcepto;
    }

    public List<Registro> getListaNombre() {
        return listaNombre;
    }

    public void setListaNombre(List<Registro> listaNombre) {
        this.listaNombre = listaNombre;
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaPeriodo() {
        return listaPeriodo;
    }

    public void setListaPeriodo(RegistroDataModelImpl listaPeriodo) {
        this.listaPeriodo = listaPeriodo;
    }

    public RegistroDataModelImpl getListaPeriodoE() {
        return listaPeriodoE;
    }

    public void setListaPeriodoE(RegistroDataModelImpl listaPeriodoE) {
        this.listaPeriodoE = listaPeriodoE;
    }

    public RegistroDataModelImpl getListabanco() {
        return listabanco;
    }

    public void setListabanco(RegistroDataModelImpl listabanco) {
        this.listabanco = listabanco;
    }

    public RegistroDataModelImpl getListabancoE() {
        return listabancoE;
    }

    public void setListabancoE(RegistroDataModelImpl listabancoE) {
        this.listabancoE = listabancoE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public boolean isConfirmarValorVisible() {
        return confirmarValorVisible;
    }

    public void setConfirmarValorVisible(boolean confirmarValorVisible) {
        this.confirmarValorVisible = confirmarValorVisible;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    public List<Registro> getListaSubfacturadoscredito() {
        return listaSubfacturadoscredito;
    }

    public void setListaSubfacturadoscredito(
        List<Registro> listaSubfacturadoscredito) {
        this.listaSubfacturadoscredito = listaSubfacturadoscredito;
    }

    public List<Registro> getListaFrmsaldocredito() {
        return listaFrmsaldocredito;
    }

    public void setListaFrmsaldocredito(List<Registro> listaFrmsaldocredito) {
        this.listaFrmsaldocredito = listaFrmsaldocredito;
    }

    public Registro getRegistroSubSubFacturadoSCredito() {
        return registroSubSubFacturadoSCredito;
    }

    public void setRegistroSubSubFacturadoSCredito(
        Registro registroSubSubFacturadoSCredito) {
        this.registroSubSubFacturadoSCredito = registroSubSubFacturadoSCredito;
    }

    public Registro getRegistroSubFrmSaldoCredito() {
        return registroSubFrmSaldoCredito;
    }

    public void setRegistroSubFrmSaldoCredito(
        Registro registroSubFrmSaldoCredito) {
        this.registroSubFrmSaldoCredito = registroSubFrmSaldoCredito;
    }
    // </SET_GET_ADICIONALES>

    @Override
    public void asignarOrigenDatos() {
        // No es un formulario de datos pero es necesario para la
        // carga del formulario
    }
}

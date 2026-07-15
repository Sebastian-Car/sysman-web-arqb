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
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.ActualizacionpagosControladorEnum;
import com.sysman.serviciospublicos.enums.ActualizacionpagosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author jguerrero
 * @version 1, 15/09/2016
 * 
 * @author cmanrique - Refactoring y demas validaciones de parte
 * grafica
 * @version 2, 14/08/2017
 */
@ManagedBean
@ViewScoped
public class ActualizacionpagosControlador extends BeanBaseDatosAcmeImpl {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String granTotal;
    private String cuponesRegistrados;
    private String valorRegistrado;
    private String valorAseo;
    private String valorRealRegistradoTercerizado;
    private String valorConve;
    private String valorAbonos;
    private String totalUsuarios;
    private String totalValor;
    private String totalSinAbono;
    private String totalUsuariosSInAbono;
    private String totTerce;
    private String granTotal1;
    private String totConve;
    private String totalAbonos;
    private String nombreBanco;
    private String diferenciaCupones;
    private String diferenciaValores;
    private String strFechaPago;
    private String valorTotal;
    private int numeroCupones;
    private String valorConvenios;
    private String valorGranTotal;

    private Registro regPago;

    private final String codigoBarrasCons;
    private final String codigoInternoCons;
    private final String nombreCons;
    private final String consecutivoCons;
    private final String numeroPaqueteCons;
    private final String spPagoCons;
    private final String sysdateCons;
    private final String codigoRutaCons;
    private final String valorPagoCons;
    private final String totFacturaPerActualCons;
    private final String totFacturaPago2Cons;
    private final String periodosAtrasoCons;
    private final String periodoCons;
    private final String bancoPerProcesoCons;
    private final String cicloCons;

    private String dblTotalBarras;
    private String fechaPreparacion;
    private String textoDialogoPagosDobles;
    private boolean vuelveCodigoRuta;
    private boolean dialogoVisible;
    private boolean dialogoVisibleRegistrarTotDeuda;
    private boolean dialogoVisiblePagosDobles;
    private boolean tipoBloqueado;

    private boolean fechaUsuario;
    private boolean unCodTer;
    private boolean unCodConv;
    private boolean autoAbono;
    private boolean terce;
    private Boolean finsitu;

    private boolean recaudoRuta;

    private double dblValorTer;
    private double dblValorConve;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaBanco;
    private Registro regUsuario;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    private RegistroDataModelImpl listaSubpago;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    private Registro registroSub;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_ADICIONALES>
    public ActualizacionpagosControlador() {
        super();
        compania = SessionUtil.getCompania();

        codigoBarrasCons = "CODIGOBARRAS";
        codigoInternoCons = "CODIGOINTERNO";
        nombreCons = "NOMBRE";
        consecutivoCons = "CONSECUTIVO";
        numeroPaqueteCons = "NUMEROPAQUETE";
        spPagoCons = "SP_PAGO";
        sysdateCons = "SYSDATE";
        codigoRutaCons = "CODIGORUTA";
        dblValorConve = 0;
        dblValorTer = 0;
        valorPagoCons = "VALORPAGO";
        totFacturaPerActualCons = "TOTFACTURAPERACTUAL";
        totFacturaPago2Cons = "TOTFACTURAPAGO2";
        periodosAtrasoCons = "PERIODOSATRASO";
        periodoCons = "PERIODO";
        bancoPerProcesoCons = "BANCOPERPROCESO";
        cicloCons = "CICLO";

        try {
            numFormulario = GeneralCodigoFormaEnum.ACTUALIZACIONPAGOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            registroSub = new Registro(new HashMap<String, Object>());

            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(ActualizacionpagosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            // aqui tengo qe
            SessionUtil.cleanFlash();
        }
    }

    @Override
    public void iniciarListas() {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaBanco();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
    }

    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaSubpago();
        // </CARGAR_LISTAS_SUBFORM>
    }

    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        listaSubpago = null;
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.SP_RECAUDOS;
        buscarLlave();
        asignarOrigenDatos();
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

    }

    public void cargarListaSubpago() {
        try {

            UrlBean urlSubPagos = UrlServiceUtil
                            .getUrlBeanById(ActualizacionpagosControladorUrlEnum.URL6766
                                            .getValue());

            Map<String, Object> params = new TreeMap<>();
            params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            params.put(GeneralParameterEnum.FECHA.getName(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.FECHA
                                                            .getName()));

            params.put(GeneralParameterEnum.BANCO.getName(),
                            registro.getCampos()
                                            .get(GeneralParameterEnum.BANCO
                                                            .getName()));
            params.put(ActualizacionpagosControladorEnum.NUMEROPAQUETE
                            .getValue(),
                            registro.getCampos().get(
                                            ActualizacionpagosControladorEnum.NUMEROPAQUETE
                                                            .getValue()));

            listaSubpago = new RegistroDataModelImpl(urlSubPagos.getUrl(),
                            urlSubPagos.getUrlConteo().getUrl(), params,
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            spPagoCons));
            cargarTotales();
        }
        catch (SysmanException e) {
            Logger.getLogger(ActualizacionpagosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarTotales() {
        UrlBean urlSubPagosTotales = UrlServiceUtil
                        .getUrlBeanById(ActualizacionpagosControladorUrlEnum.URL59048
                                        .getValue());
        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(GeneralParameterEnum.FECHA.getName(),
                        registro.getCampos()
                                        .get(GeneralParameterEnum.FECHA
                                                        .getName()));

        params.put(GeneralParameterEnum.BANCO.getName(),
                        registro.getCampos()
                                        .get(GeneralParameterEnum.BANCO
                                                        .getName()));
        params.put(ActualizacionpagosControladorEnum.NUMEROPAQUETE
                        .getValue(),
                        registro.getCampos().get(
                                        ActualizacionpagosControladorEnum.NUMEROPAQUETE
                                                        .getValue()));
        try {
            Parameter par = requestManager.get(urlSubPagosTotales.getUrl(),
                            params);
            numeroCupones = (int) par.getFields()
                            .get(ActualizacionpagosControladorEnum.CUPONES
                                            .getValue());
            valorTotal = SysmanFunciones.nvl(par.getFields()
                            .get(ActualizacionpagosControladorEnum.VALORPAGO
                                            .getValue()),
                            "0").toString();
            valorAbonos = SysmanFunciones.nvl(par.getFields()
                            .get(ActualizacionpagosControladorEnum.VALORABONOS
                                            .getValue()),
                            "0").toString();
            valorAseo = SysmanFunciones.nvl(par.getFields()
                            .get(ActualizacionpagosControladorEnum.VALORTERCERIZADO
                                            .getValue()),
                            "0").toString();
            valorConvenios = SysmanFunciones.nvl(par.getFields()
                            .get(ActualizacionpagosControladorEnum.VALORCONVENIOS
                                            .getValue()),
                            "0").toString();

            valorGranTotal = SysmanFunciones.nvl(par.getFields()
                            .get(ActualizacionpagosControladorEnum.VALORGRANTOTAL
                                            .getValue()),
                            "0").toString();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaBanco() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ActualizacionpagosControladorUrlEnum.URL8030
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        String.valueOf(compania));

        listaBanco = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    public void cambiarCodigoBarras() {
        // <CODIGO_DESARROLLADO>

        String codigoBarras = "";

        boolean verificar = false;

        if (registroSub.getCampos().get(codigoBarrasCons) != null) {
            codigoBarras = registroSub.getCampos().get(codigoBarrasCons)
                            .toString();
            verificar = true;
        }
        if (codigoBarras.length() > 20) {

            valorPagoCodigoBarras(codigoBarras);
        }
        else {
            if (recaudoRuta) {
                recaudoRuta();
            }
            else {
                codigoInternoCorrecto(codigoBarras);
            }

            fechaPago(codigoBarras);

        }

        if (verificar) {

            codigoInternoAferUpdate();
        }

    }

    // </CODIGO_DESARROLLADO>

    public void cambiarDialogoAbonosAutorizados() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void aceptarDialogRegistrarTotalDeuda() {
        // <CODIGO_DESARROLLADO>
        registroSub.getCampos().put(codigoBarrasCons, null);
        dialogoVisible = false;
        // </CODIGO_DESARROLLADO>
    }

    public void cancelarDialogRegistrarTotalDeuda() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void aceptarDialogoPagosDobles() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("TIPO", "D");
        tipoBloqueado = true;
        dialogoVisiblePagosDobles = false;
        // </CODIGO_DESARROLLADO>
    }

    public void cancelarDialogoPagosDobles() {
        registro.getCampos().put(codigoBarrasCons, null);
        dialogoVisiblePagosDobles = false;
    }
    // <CODIGO_DESARROLLADO>

    public void aceptarDialogoAbonosAutorizados() {
        // <CODIGO_DESARROLLADO>
        try {

            updateAbonoAutorizado();
            String parametros = "'" + compania + "','"
                + regPago.getCampos()
                                .get(codigoRutaCons)
                + "','" + regPago.getCampos().get(periodoCons) + "',"
                + registro.getCampos().get(GeneralParameterEnum.FECHA.getName())
                + ",'"
                + registro.getCampos().get(GeneralParameterEnum.BANCO.getName())
                + "','"
                + SessionUtil.getUser().getCodigo() + "',"
                + regPago.getCampos().get("ANO") + ","
                + regPago.getCampos().get(cicloCons) + ","
                + regPago.getCampos()
                                .get(consecutivoCons)
                + "," + regPago.getCampos()
                                .get("VALORABONADOPERIDO");

            JsfUtil.agregarMensajeAlerta(
                            Acciones.ejecutarFuncion(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            "PCK_SERVICIOS_PUBLICOS_COM2.FC_REGISTRARABONO",
                                            parametros, Types.VARCHAR)
                                            .toString());

            dialogoVisible = false;
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException |

                        NamingException e)

        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void cancelarDialogoAbonosAutorizados() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>

    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaBanco(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.BANCO.getName(),
                        registroAux.getCampos().get("CODIGO"));

        if (registroAux.getCampos().get(nombreCons) != null) {

            nombreBanco = registroAux.getCampos().get(nombreCons).toString();
        }

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_BOTONES>
    public void oprimirCmdNovedad(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCmdModConcExt() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    public void agregarRegistroSubSubpago() {
        try {
            String condicion = "COMPANIA='" + compania + "' AND FECHA="
                + SysmanFunciones.formatearFecha(
                                (Date) registro.getCampos()
                                                .get(GeneralParameterEnum.FECHA
                                                                .getName()))
                + " AND BANCO='"
                + registro.getCampos().get(GeneralParameterEnum.BANCO.getName())
                + "' AND  NUMEROPAQUETE='"
                + registro.getCampos().get(numeroPaqueteCons)
                + "'";

            Long consecutivo = Acciones.genConsecutivo(
                            ConectorPool.ESQUEMA_SYSMAN, spPagoCons, condicion,
                            consecutivoCons, "1");

            registroSub.getCampos().put("COMPANIA", compania);
            registroSub.getCampos().put(GeneralParameterEnum.FECHA.getName(),
                            registro.getCampos().get(GeneralParameterEnum.FECHA
                                            .getName()));
            registroSub.getCampos().put(GeneralParameterEnum.BANCO.getName(),
                            registro.getCampos().get(GeneralParameterEnum.BANCO
                                            .getName()));
            registroSub.getCampos().put(numeroPaqueteCons,
                            registro.getCampos().get(numeroPaqueteCons));
            registroSub.getCampos().put(consecutivoCons, consecutivo);

            int conteo;
            conteo = Acciones.insertar(ConectorPool.ESQUEMA_SYSMAN, spPagoCons,
                            registroSub.getCampos());
            cargarListaSubpago();
            if (conteo > 0) {
                JsfUtil.agregarMensajeInformativo(
                                "Registro ingresado correctamente");
            }
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException ex) {
            Logger.getLogger(ActualizacionpagosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally {
            registroSub = new Registro(new HashMap<String, Object>());
        }
    }

    public void editarRegSubSubpago(RowEditEvent event) {
        // NO IMPLEMENTADO
    }

    public void eliminarRegSubSubpago(Registro reg) {
        try {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SP_PAGO
                                                            .getDeleteKey());
            requestManager.delete(urlDelete.getUrl(), reg.getLlave());
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_ELIMINADO"));
            listaSubpago.load();

        }

        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void onCancelSubpago() {
        cargarListaSubpago();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        try {
            recaudoRuta = "SI".equalsIgnoreCase((String) SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "MANEJA DOLARES",
                                            SessionUtil.getModulo(),
                                            new Date(), false), "NO"));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        if (css == null) {
            nombreBanco = null;
        }
        else {
            if (registro.getCampos().get(
                            GeneralParameterEnum.BANCO.getName()) != null) {
                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.CODIGO.getName(),
                                registro.getCampos()
                                                .get(GeneralParameterEnum.BANCO
                                                                .getName()));
                try {
                    nombreBanco = listaBanco
                                    .getRegistroUnico(param)
                                    .getCampos().get(nombreCons).toString();
                }
                catch (SystemException e) {
                    logger.error(e.getMessage(), e);
                    JsfUtil.agregarMensajeError(e.getMessage());
                }
            }

        }

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put("COMPANIA", compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1102-DESPUES_INSERTAR Private Sub Form_AfterInsert()
         * Me!Sub_Pago.visible = True End Sub
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
         * FR1102-DESPUES_ACTUALIZAR Private Sub Form_AfterUpdate()
         * Me!Sub_Pago.visible = True End Sub
         */
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

    // <SET_GET_ATRIBUTOS>
    public String getGranTotal() {
        return granTotal;
    }

    public void setGranTotal(String granTotal) {
        this.granTotal = granTotal;
    }

    public String getCuponesRegistrados() {
        return cuponesRegistrados;
    }

    public void setCuponesRegistrados(String cuponesRegistrados) {
        this.cuponesRegistrados = cuponesRegistrados;
    }

    public String getValorRegistrado() {
        return valorRegistrado;
    }

    public void setValorRegistrado(String valorRegistrado) {
        this.valorRegistrado = valorRegistrado;
    }

    public String getValorAseo() {
        return valorAseo;
    }

    public void setValorAseo(String valorAseo) {
        this.valorAseo = valorAseo;
    }

    public String getValorRealRegistradoTercerizado() {
        return valorRealRegistradoTercerizado;
    }

    public void setValorRealRegistradoTercerizado(
        String valorRealRegistradoTercerizado) {
        this.valorRealRegistradoTercerizado = valorRealRegistradoTercerizado;
    }

    public String getValorConve() {
        return valorConve;
    }

    public void setValorConve(String valorConve) {
        this.valorConve = valorConve;
    }

    public String getValorAbonos() {
        return valorAbonos;
    }

    public void setValorAbonos(String valorAbonos) {
        this.valorAbonos = valorAbonos;
    }

    public String getTotalUsuarios() {
        return totalUsuarios;
    }

    public void setTotalUsuarios(String totalUsuarios) {
        this.totalUsuarios = totalUsuarios;
    }

    public String getTotalValor() {
        return totalValor;
    }

    public void setTotalValor(String totalValor) {
        this.totalValor = totalValor;
    }

    public String getTotalSinAbono() {
        return totalSinAbono;
    }

    public void setTotalSinAbono(String totalSinAbono) {
        this.totalSinAbono = totalSinAbono;
    }

    public String getTotalUsuariosSInAbono() {
        return totalUsuariosSInAbono;
    }

    public void setTotalUsuariosSInAbono(String totalUsuariosSInAbono) {
        this.totalUsuariosSInAbono = totalUsuariosSInAbono;
    }

    public String getTotTerce() {
        return totTerce;
    }

    public void setTotTerce(String totTerce) {
        this.totTerce = totTerce;
    }

    public String getGranTotal1() {
        return granTotal1;
    }

    public void setGranTotal1(String granTotal1) {
        this.granTotal1 = granTotal1;
    }

    public String getTotConve() {
        return totConve;
    }

    public void setTotConve(String totConve) {
        this.totConve = totConve;
    }

    public String getTotalAbonos() {
        return totalAbonos;
    }

    public void setTotalAbonos(String totalAbonos) {
        this.totalAbonos = totalAbonos;
    }

    public String getNombreBanco() {
        return nombreBanco;
    }

    public void setNombreBanco(String nombreBanco) {
        this.nombreBanco = nombreBanco;
    }

    public String getDiferenciaCupones() {
        return diferenciaCupones;
    }

    public void setDiferenciaCupones(String diferenciaCupones) {
        this.diferenciaCupones = diferenciaCupones;
    }

    public String getDiferenciaValores() {
        return diferenciaValores;
    }

    public void setDiferenciaValores(String diferenciaValores) {
        this.diferenciaValores = diferenciaValores;
    }

    public boolean isDialogoVisibleRegistrarTotDeuda() {
        return dialogoVisibleRegistrarTotDeuda;
    }

    public void setDialogoVisibleRegistrarTotDeuda(
        boolean dialogoVisibleRegistrarTotDeuda) {
        this.dialogoVisibleRegistrarTotDeuda = dialogoVisibleRegistrarTotDeuda;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaBanco() {
        return listaBanco;
    }

    public void setListaBanco(RegistroDataModelImpl listaBanco) {
        this.listaBanco = listaBanco;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    public Registro getRegistroSub() {
        return registroSub;
    }

    public RegistroDataModelImpl getListaSubpago() {
        return listaSubpago;
    }

    public void setListaSubpago(RegistroDataModelImpl listaSubpago) {
        this.listaSubpago = listaSubpago;
    }

    public void setRegistroSub(Registro registroSub) {
        this.registroSub = registroSub;
    }

    public boolean isVuelveCodigoRuta() {
        return vuelveCodigoRuta;
    }

    public void setVuelveCodigoRuta(boolean vuelveCodigoRuta) {
        this.vuelveCodigoRuta = vuelveCodigoRuta;
    }

    public String getStrFechaPago() {
        return strFechaPago;
    }

    public void setStrFechaPago(String strFechaPago) {
        this.strFechaPago = strFechaPago;
    }

    public String getDblTotalBarras() {
        return dblTotalBarras;
    }

    public void setDblTotalBarras(String dblTotalBarras) {
        this.dblTotalBarras = dblTotalBarras;
    }

    public boolean isDialogoVisible() {
        return dialogoVisible;
    }

    public void setDialogoVisible(boolean dialogoVisible) {
        this.dialogoVisible = dialogoVisible;
    }

    public Registro getRegPago() {
        return regPago;
    }

    public void setRegPago(Registro regPago) {
        this.regPago = regPago;
    }

    // </SET_GET_ADICIONALES>

    public Registro getRegUsuario() {
        return regUsuario;
    }

    public void setRegUsuario(Registro regUsuario) {
        this.regUsuario = regUsuario;
    }

    public String getTextoDialogoPagosDobles() {
        return textoDialogoPagosDobles;
    }

    public void setTextoDialogoPagosDobles(String textoDialogoPagosDobles) {
        this.textoDialogoPagosDobles = textoDialogoPagosDobles;
    }

    public boolean isDialogoVisiblePagosDobles() {
        return dialogoVisiblePagosDobles;
    }

    public void setDialogoVisiblePagosDobles(
        boolean dialogoVisiblePagosDobles) {
        this.dialogoVisiblePagosDobles = dialogoVisiblePagosDobles;
    }

    public boolean isTipoBloqueado() {
        return tipoBloqueado;
    }

    public void setTipoBloqueado(boolean tipoBloqueado) {
        this.tipoBloqueado = tipoBloqueado;
    }

    /**
     * @return the valorTotal
     */
    public String getValorTotal() {
        return valorTotal;
    }

    /**
     * @param valorTotal
     * the valorTotal to set
     */
    public void setValorTotal(String valorTotal) {
        this.valorTotal = valorTotal;
    }

    /**
     * @return the numeroCupones
     */
    public int getNumeroCupones() {
        return numeroCupones;
    }

    /**
     * @param numeroCupones
     * the numeroCupones to set
     */
    public void setNumeroCupones(int numeroCupones) {
        this.numeroCupones = numeroCupones;
    }

    /**
     * @return the valorConvenios
     */
    public String getValorConvenios() {
        return valorConvenios;
    }

    /**
     * @param valorConvenios
     * the valorConvenios to set
     */
    public void setValorConvenios(String valorConvenios) {
        this.valorConvenios = valorConvenios;
    }

    /**
     * @return the valorGranTotal
     */
    public String getValorGranTotal() {
        return valorGranTotal;
    }

    /**
     * @param valorGranTotal
     * the valorGranTotal to set
     */
    public void setValorGranTotal(String valorGranTotal) {
        this.valorGranTotal = valorGranTotal;
    }

    private void codigoInternoAferUpdate() {

        try {

            String codigoInterno = registroSub.getCampos()
                            .get(codigoInternoCons)
                            .toString();
            fechaUsuario = SysmanFunciones
                            .nvl(Acciones.getParametro(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            compania,
                                            "CAMBIAR NOMBRE SERVICIO ACUEDUCTO",
                                            SessionUtil.getModulo(),
                                            sysdateCons),
                                            "NO").equalsIgnoreCase("SI");

            String strSqlM;

            if (fechaUsuario) {
                strSqlM = "SELECT COMPANIA,"
                    + "CICLO,"
                    + "CODIGORUTA,"
                    + "ANO,"
                    + "PERIODO,"
                    + "CODIGOINTERNO,"
                    + "BANCOPERPROCESO,"
                    + "FIMM, "
                    + "PAQUETEPAGOPERPROCESO,"
                    + "RECAUDADOPROCESO,"
                    + "TOTFACTURAPERACTUAL,"
                    + "TOTFACTURAPAGO2,"
                    + "NOFECHAPAGOPERANTERIOR,"
                    + "FECHALIMITE,"
                    + "NOFECHAPAGOPERPROCESO,"
                    + "NOTACREDITO,"
                    + "CREDITOABONADO,"
                    + "FECHALIMITE2,"
                    + "PERIODOSATRASO "
                    + " FROM SP_USUARIO "
                    + " WHERE COMPANIA= '" + compania + "' " +
                    "AND CODIGOINTERNO='" + codigoInterno + "'";
            }
            else {
                strSqlM = "SELECT COMPANIA, "
                    + "CICLO, "
                    + "CODIGORUTA,"
                    + " ANO, PERIODO ,"
                    + " CODIGOINTERNO, "
                    + "BANCOPERPROCESO, "
                    + "FECHAPAGOPERPROCESO, "
                    + "FIMM,  "
                    + "PAQUETEPAGOPERPROCESO,"
                    + " RECAUDADOPROCESO,  "
                    + "TOTFACTURAPERACTUAL,"
                    + "TOTFACTURAPAGO2, "
                    + "NOFECHAPAGOPERANTERIOR, "
                    + "FECHALIMITE,  "
                    + "NOFECHAPAGOPERPROCESO,"
                    + "NOTACREDITO,"
                    + "CREDITOABONADO "
                    + "FROM SP_USUARIO "
                    + "WHERE COMPANIA='" + compania + "' "
                    + "AND CODIGOINTERNO='" + codigoInterno + "'";
            }

            regUsuario = service
                            .getRegistro(ConectorPool.ESQUEMA_SYSMAN, strSqlM);
            if (regUsuario == null) {
                JsfUtil.agregarMensajeAlerta(
                                "El c�digo est� errado o esta bloqueado por otro usuario del sistema.");
                return;
            }

            etapa1CodigoAfterUpdate();
            etapa2CodigoAfterUpdate();
            etapa3CodigoAfterUpdate();
            etapa4CodigoAfterUpdate();
            etapa5CodigoAfterUpdate();
            etapa6CodigoAfterUpdate();
            etapa7CodigoAfterUpdate();
            etapa8CodigoAfterUpdate();
            etapa9CodigoAfterUpdate();
            actualizarRegUsuario();

            String sqlConceptos = "SELECT SUM(VALOR_FACTURADO+deuda) AS SumaFac "
                +
                "  FROM SP_FACTURADO " +
                " WHERE COMPANIA='" + compania + "' " +
                "   AND CICLO     =" + regUsuario.getCampos().get(cicloCons) +
                "   AND CODIGORUTA='"
                + regUsuario.getCampos().get(codigoRutaCons)
                + "' " +
                "   AND ANO       =" + regUsuario.getCampos().get("ANO") +
                "   AND PERIODO   ='" + regUsuario.getCampos().get(periodoCons)
                + "'" +
                "   AND (CONCEPTO BETWEEN 0 AND 50" +
                "    OR CONCEPTO IN (201,202,203,204,205,206,207,246,247,248,249))) "
                +
                "HAVING Sum(VALOR_FACTURADO+deuda)<> "
                + regUsuario.getCampos().get(totFacturaPerActualCons) + ")";

            Registro rsConceptos = service.getRegistro(
                            ConectorPool.ESQUEMA_SYSMAN, sqlConceptos);
            if ((rsConceptos != null)
                && "D".equals(registro.getCampos().get("TIPO").toString())
                && !regUsuario.getCampos().get(totFacturaPerActualCons)
                                .equals(SysmanFunciones.nvl(
                                                rsConceptos.getCampos()
                                                                .get("SumaFac"),
                                                0))) {
                JsfUtil.agregarMensajeAlerta(
                                "El total de la factura no coincide con los valores facturados en el sistema. <br> Verifique los valores con el encargado de facturacion. <br> No se permite registrar pago");
                return;
            }

            etapa10CodigoAfterUpdate();
            etapa11CodigoAfterUpdate();
            etapa12CodigoAfterUpdate();

        }
        catch (NamingException | SQLException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public String mid(String valor, int ini, int len) {
        return valor.substring(ini - 1, (ini - 1) + len);

    }

    private void recaudoRuta() {

        String sql = "SELECT COUNT (CODIGOINTERNO) EXISTE, CODIGOINTERNO FROM SP_USUARIO WHERE COMPANIA='"
            + compania + "' AND CODIGORUTA='"
            + registroSub.getCampos().get(codigoRutaCons)
            + " GROUP BY CODIGOINTERNO";

        Registro reg = service.getRegistro(
                        ConectorPool.ESQUEMA_SYSMAN, sql);
        if ((reg == null) || (reg.getCampos()
                        .get(codigoInternoCons) == null)) {

            JsfUtil.agregarMensajeAlerta(
                            "No se encontr� informaci�n relacionada para el c�digo de ruta ingresado");
            vuelveCodigoRuta = true;
            return;
        }
        else {
            registro.getCampos().put(codigoInternoCons, reg
                            .getCampos()
                            .get(codigoInternoCons));
        }

    }

    private void fechaPago(String codigoBarras) {
        String controlFechas = codigoBarras.substring(
                        codigoBarras.length() - 10,
                        codigoBarras.length());
        String resutladoControlFechas = mid(controlFechas, 1, 2);

        if ("96".equals(resutladoControlFechas)) {
            String right = codigoBarras
                            .substring(codigoBarras.length()
                                - 10, codigoBarras.length());

            strFechaPago = right.substring(2,
                            codigoBarras.length());

        }
    }

    private void valorPagoCodigoBarras(String codigoBarras) {

        int posInicial;
        int posFinal;
        int posVlorIni;

        posInicial = codigoBarras.indexOf("8020")
            + 5;
        posFinal = codigoBarras.indexOf("3900")
            + 1;

        if (posFinal == 0) {
            posFinal = codigoBarras.length()
                - 1;
        }
        posVlorIni = posFinal + 4;
        if (posVlorIni < codigoBarras.length()) {
            dblTotalBarras = mid(codigoBarras, posVlorIni, 10);
        }

        if (mid(codigoBarras, posInicial, posFinal - posInicial)
                        .length() > 7) {
            JsfUtil.agregarMensajeAlerta(
                            "La referencia de pago no pertenece a una factura valida");
            vuelveCodigoRuta = true;
            return;
        }
        registroSub.getCampos()
                        .put(codigoInternoCons, mid(codigoBarras,
                                        posInicial, posFinal
                                            - posInicial));
    }

    private void codigoInternoCorrecto(String codigoBarras) {
        if (codigoBarras.length() > 7) {
            JsfUtil.agregarMensajeAlerta("C�digo errado");
            vuelveCodigoRuta = true;
            return;
        }
        else {
            registroSub.getCampos().put(codigoInternoCons,
                            mid(codigoBarras, 1, 7));
        }
    }

    public void updateAbonoAutorizado() {

        String campos = "FECHA="
            + SysmanFunciones.formatearFecha(
                            (Date) regPago.getCampos()
                                            .get(GeneralParameterEnum.FECHA
                                                            .getName()))
            + ", BANCO='" + regPago.getCampos().get("BAMCO") + "'";
        String condicion = "COMPANIA='" + compania + "' AND CICLO="
            + regUsuario.getCampos().get(cicloCons) + " AND CODIGORUTA='"
            + regUsuario.getCampos().get(codigoRutaCons)
            + " AND INDAUTORIZADO <>0 AND BANCO IS NULL";

        try {
            Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN, "SP_ABONOS",
                            campos, condicion);
        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void validarFechaPago(int ciclo) {
        String sql = " SELECT NVL(TO_CHAR(FECHA_PREPARACION,'DD/MM/YYYY'),'01/01/1900') AS FECHAFROM CP_CICLO"
            + " WHERE COMPANIA='" + compania + "' AND NUMERO=" + ciclo;

        Registro reg = service.getRegistro(ConectorPool.ESQUEMA_SYSMAN, sql);
        if (reg != null) {
            fechaPreparacion = reg.getCampos()
                            .get(GeneralParameterEnum.FECHA.getName())
                            .toString();
        }
        else {
            fechaPreparacion = "01/01/1900";
        }

    }

    public void etapa1CodigoAfterUpdate() {

        try {
            autoAbono = SysmanFunciones
                            .nvl(Acciones.getParametro(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            compania,
                                            "PERMITE AUTORIZACION DE ABONOS",
                                            SessionUtil.getModulo(),
                                            sysdateCons),
                                            "NO").equalsIgnoreCase("SI");

            String sql = "SELECT" +
                "ANO ," +
                "PERIODO ," +
                "CONSECUTIVO ," +
                "VALOR ," +
                "INDABONADO ," +
                "VALORABONADOPERIODO ," +
                "BANCO ," +
                "VALORANTFACT ," +
                "CODIGOINTERNO ," +
                "USUARIO ," +
                "INDAUTORIZADO ," +
                "FACTAUTORIZADA ," +
                "PAGOTERCERIZADO ," +
                "PAGOCONVENIOS ," +
                "OBSERVACION ," +
                "CREATED_BY ," +
                "MODIFIED_BY ," +
                "FECHA ," +
                "FECHAPROCESO " +
                "FROM SP_ABONOS " +
                "WHERE COMPANIA     = '" + compania + "' " +
                "AND CICLO          = '"
                + regUsuario.getCampos().get(cicloCons) + "' " +
                "AND CODIGORUTA     = '"
                + regUsuario.getCampos().get(codigoRutaCons) + "' " +
                "AND INDAUTORIZADO <> 0 " +
                "AND BANCO         IS NULL";

            regPago = service
                            .getRegistro(ConectorPool.ESQUEMA_SYSMAN, sql);
            if (autoAbono && (regPago != null)) {

                if (dblTotalBarras == regPago.getCampos()
                                .get("VALORABONADOPERIODO")) {

                    String parametros = "'" + compania + "','"
                        + regPago.getCampos()
                                        .get(codigoRutaCons)
                        + "','" + regPago.getCampos().get(periodoCons) + "',"
                        + registro.getCampos().get(
                                        GeneralParameterEnum.FECHA.getName())
                        + ",'"
                        + registro.getCampos().get(
                                        GeneralParameterEnum.BANCO.getName())
                        + "','"
                        + SessionUtil.getUser().getCodigo() + "',"
                        + regPago.getCampos().get("ANO") + ","
                        + regPago.getCampos().get(cicloCons) + ","
                        + regPago.getCampos()
                                        .get(consecutivoCons)
                        + "," + regPago.getCampos()
                                        .get("VALORABONADOPERIDO");

                    JsfUtil.agregarMensajeAlerta(Acciones.ejecutarFuncion(
                                    ConectorPool.ESQUEMA_SYSMAN,
                                    "PCK_SERVICIOS_PUBLICOS_COM2.FC_REGISTRARABONO",
                                    parametros, Types.VARCHAR).toString());

                }
                else if (Double.parseDouble(dblTotalBarras) > 0) {
                    dialogoVisibleRegistrarTotDeuda = true;
                }
                else {
                    dialogoVisible = true;
                }

                registroSub.getCampos().put(valorPagoCons,
                                regPago.getCampos().get(valorPagoCons));
                registroSub.getCampos().put("OPERACION", "A");

                if (("CAJEROS").equals(
                                SessionUtil.getGrupo(SessionUtil.getModulo())
                                                .getCodigo())) {
                    // SE DEBE CREAR EL METODO DISPARARMOVIENTO CAJA
                    // EN
                    // DONDE HAY QUE MIGRAR OTRO FORMULARIO

                }

            }
            else {

                validarFechaPago(Integer.parseInt(
                                regUsuario.getCampos().get(cicloCons)
                                                .toString()));

                Date fechaFormulario = (Date) registro.getCampos()
                                .get(GeneralParameterEnum.FECHA.getName());
                if ("01/01/1900".equals(fechaPreparacion)) {
                    JsfUtil.agregarMensajeAlerta(
                                    "La GeneralParameterEnum.FECHA.getName() de preparaci�n del ciclo se encuentra nula. Por favor informar al administrador del sistema");
                }
                else if (fechaFormulario.before(SysmanFunciones
                                .convertirAFecha(fechaPreparacion))) {
                    JsfUtil.agregarMensajeAlerta(
                                    "La GeneralParameterEnum.FECHA.getName() de pago es inferior a la GeneralParameterEnum.FECHA.getName() de preparaci�n del ciclo al cual corresponde el usuario");
                }
            }

        }
        catch (NamingException | SQLException | ParseException
                        | IllegalAccessException | InstantiationException
                        | ClassNotFoundException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void etapa2CodigoAfterUpdate() {
        if (fechaUsuario) {
            if (!"".equals(SysmanFunciones.nvl(
                            regUsuario.getCampos().get("FECHALIMITE2"),
                            ""))
                && ((Date) registro.getCampos()
                                .get(GeneralParameterEnum.FECHA.getName()))
                                                .before((Date) regUsuario
                                                                .getCampos()
                                                                .get("FECHALIMITE2"))) {

                registro.getCampos().put("TIPO", 1);

            }
            else {
                if ("1".equals(regUsuario.getCampos()
                                .get(periodosAtrasoCons))) {
                    registro.getCampos().put("TIPO", 2);
                }
                else {
                    registro.getCampos().put("TIPO", 1);
                }
            }
        }
        else if (!"".equals(SysmanFunciones.nvl(
                        regUsuario.getCampos().get("FECHALIMITE"), ""))
            && "1".equals(regUsuario.getCampos().get(periodosAtrasoCons))) {
            registro.getCampos().put("TIPO", 2);
        }
        else {
            registro.getCampos().put("TIPO", 1);
        }
    }

    public void etapa3CodigoAfterUpdate() {

        Date fechaLimite = (Date) SysmanFunciones.nvl(
                        regUsuario.getCampos().get("FECHALIMITE"),
                        Acciones.getSysDate(
                                        ConectorPool.ESQUEMA_SYSMAN));
        if (!fechaUsuario) {
            String strSqlM = "SELECT " +
                "COMPANIA ," +
                "NUMERO ," +
                "ANO ," +
                "PERIODO ," +
                "CODIGOINICIAL ," +
                "CODIGOFINAL ," +
                "INDFACTURADO ," +
                "INDPREPARADO ," +
                "INDESTADISTICAS ," +
                "INDCALCULADO ," +
                "INDRECAUDADO ," +
                "INDAUTORMODIFICACION ," +
                "ANOINICIAL ," +
                "PERIODOINICIAL ," +
                "TASARECARGO ," +
                "INDBLOQUEOMANUAL ," +
                "INDSELLOFIMM ," +
                "PREFACTURANDO ," +
                "APLICADESCUENTO ," +
                "EXCLUIR_ESTADISTICA_MENSUAL ," +
                "FECHA_CIERREPREFAC ," +
                "HORA_PREPARACION ," +
                "FECHAPAGO1 ," +
                "FECHAPAGO2 ," +
                "FECHAPAGO3 ," +
                "FECHA_PREPARACION " +
                "FROM SP_CICLO " +
                "WHERE COMPANIA='" + compania + "' " +
                "AND NUMERO=" + regUsuario.getCampos().get(cicloCons);

            regPago = service.getRegistro(ConectorPool.ESQUEMA_SYSMAN, strSqlM);
            if (!"".equals(SysmanFunciones
                            .nvl(regPago.getCampos().get("FECHAPAGO1"), ""))) {
                if (((Date) registro.getCampos()
                                .get(GeneralParameterEnum.FECHA.getName()))
                                                .before(
                                                                (Date) regPago.getCampos()
                                                                                .get("FECHAPAGO1"))) {
                    registro.getCampos().put("TIPO", 1);
                }
                else {
                    registro.getCampos().put("TIPO", 2);
                }
            }
            else if (!"".equals(SysmanFunciones
                            .nvl(regUsuario.getCampos().get("FECHALIMITE"), ""))
                && (fechaLimite.before(
                                (Date) registro.getCampos()
                                                .get(GeneralParameterEnum.FECHA
                                                                .getName())))) {

                registro.getCampos().put("TIPO", 2);
            }
        }
    }

    public void etapa4CodigoAfterUpdate() {
        try {
            String parametros = compania + ","
                + regUsuario.getCampos().get(cicloCons) + ","
                + regUsuario.getCampos().get(codigoRutaCons) + ","
                + regUsuario.getCampos().get("ANO") + ","
                + regUsuario.getCampos().get(periodoCons) + "," + terce;
            if (unCodTer && autoAbono) {

                dblValorTer = (double) Acciones.ejecutarFuncion(
                                ConectorPool.ESQUEMA_SYSMAN,
                                "FC_VALORPAGOTERCERIZADO", parametros,
                                Types.INTEGER);
            }

            if (unCodConv && !autoAbono) {
                dblValorConve = (double) Acciones.ejecutarFuncion(
                                ConectorPool.ESQUEMA_SYSMAN,
                                "FC_VALORPAGOCONVENIOS", parametros,
                                Types.INTEGER);
            }
            String total = String.valueOf(Double.parseDouble(
                            regUsuario.getCampos().get(
                                            totFacturaPerActualCons)
                                            .toString())
                + dblValorTer + dblValorConve);
            if (!dblTotalBarras.equals(total) && !"0".equals(dblTotalBarras)) {
                JsfUtil.agregarMensajeAlerta(
                                "El valor del c�digo de barras no coincide con el valor actual del sistema. Por favor verifique");

            }
            dblValorTer = 0;

        }
        catch (IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void etapa5CodigoAfterUpdate() {
        if ("P".equals(SysmanFunciones.nvl(regUsuario.getCampos().get("FIMM"),
                        ""))) {
            JsfUtil.agregarMensajeAlerta(
                            "El usuario est� en terreno. No se permite registrar pago");
            return;
        }
    }

    private void etapa6CodigoAfterUpdate() {

        try {
            finsitu = SysmanFunciones
                            .nvl(Acciones.getParametro(
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            compania,
                                            "FACTURACION EN SITIO",
                                            SessionUtil.getModulo(),
                                            sysdateCons),
                                            "NO").equalsIgnoreCase("SI");
            if ("".equals(SysmanFunciones
                            .nvl(regUsuario.getCampos().get("FIMM"), ""))
                && finsitu) {
                JsfUtil.agregarMensajeAlerta(
                                "El usuario se prepar� pero no ha sido calculado. No se permite registrar pago");
                return;
            }
        }
        catch (NamingException | SQLException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void etapa7CodigoAfterUpdate() {
        if ("P".equals(SysmanFunciones.nvl(regUsuario.getCampos().get("FIMM"),
                        ""))
            && finsitu) {
            JsfUtil.agregarMensajeAlerta(
                            "El usuario a�n est� en terreno.No se permite registrar pago");
            return;
        }
    }

    private void etapa8CodigoAfterUpdate() {
        if (!"".equals(SysmanFunciones.nvl(
                        regUsuario.getCampos().get(bancoPerProcesoCons), ""))
            && (("CAJEROS").equals(SessionUtil.getGrupo(SessionUtil.getModulo())
                            .getCodigo()))) {
            String mensaje = "Pago ya registrado en: <br> Banco: "
                + regUsuario.getCampos().get(bancoPerProcesoCons)
                + ". <br> Fecha: "
                + regUsuario.getCampos().get("FECHAPAGOPERPROCESO")
                + ". <br> Paquete: "
                + regUsuario.getCampos().get("PAQUETEPAGOPERPROCESO")
                + ". <br> Valor: "
                + regUsuario.getCampos().get("RECAUDADOPROCESO")
                + ". <br> NO SE PUEDE REGISTRAR ";

            JsfUtil.agregarMensajeAlerta(mensaje);
            return;

        }

    }

    private void etapa9CodigoAfterUpdate() {
        if ("".equals(SysmanFunciones.nvl(
                        regUsuario.getCampos().get(bancoPerProcesoCons), ""))) {
            dialogoVisiblePagosDobles = true;
        }
    }

    private void etapa10CodigoAfterUpdate() {
        try {
            if ("1".equals(registroSub.getCampos().get("TIPO"))) {
                registroSub.getCampos().put(valorPagoCons,
                                SysmanFunciones.nvl(
                                                regUsuario.getCampos()
                                                                .get(totFacturaPerActualCons),
                                                0));
            }
            else if ("2".equals(registroSub.getCampos().get("TIPO"))) {
                if (("SI").equals(SysmanFunciones
                                .nvl(Acciones.getParametro(
                                                ConectorPool.ESQUEMA_SYSMAN,
                                                compania,
                                                "RECARGO SEGUNDA FECHA EN PERIODO SIGUIENTE",
                                                SessionUtil.getModulo(),
                                                GeneralParameterEnum.FECHA
                                                                .getName()),
                                                "NO"))) {
                    registroSub.getCampos().put(valorPagoCons,
                                    SysmanFunciones.nvl(
                                                    regUsuario.getCampos()
                                                                    .get(totFacturaPerActualCons),
                                                    0));
                }
                else {
                    obtenerValoresPago();

                    String campos = "COMPANIA,"
                        + " CICLO, "
                        + " CODIGORUTA,"
                        + " ANO,"
                        + " PERIODO, "
                        + " CONCEPTO, "
                        + " VALOR_FACTURADO,"
                        + " DEUDA, "
                        + " VALOR_FACTURADOANT,"
                        + " DEUDAANT,"
                        + " VALOR_FACTURADOIN,"
                        + " DEUDAIN, "
                        + " RECAUDADOPERIODO,"
                        + " DOBLEPAGO, "
                        + " SALDOCREDITO,"
                        + " ABONO,"
                        + " CREDITOABONADO, "
                        + " VALORFINACT, "
                        + " VALORFINANT, "
                        + " PORFINANCIACION,"
                        + " VALORABONOACT,"
                        + " VALORABONOANT ";
                    String valores = "'" + compania + "',"
                        + regUsuario.getCampos().get(cicloCons) + ", '"
                        + regUsuario.getCampos().get(codigoRutaCons) + "',"
                        + regUsuario.getCampos().get("ANO") + ",'"
                        + regUsuario.getCampos().get(periodoCons) + "',247,("
                        + regUsuario.getCampos().get(totFacturaPago2Cons) + "-"
                        + regUsuario.getCampos().get(totFacturaPerActualCons)
                        + "),0,0,0,0,0,0,0,0,0,0,0,0,0,0,0";

                    Acciones.insertar(ConectorPool.ESQUEMA_SYSMAN,
                                    "SP_FACTURADO", campos, valores);

                    String camposUpdate = "VALOR_FACTURADO=("
                        + regUsuario.getCampos().get(totFacturaPago2Cons) + "-"
                        + regUsuario.getCampos().get(totFacturaPerActualCons)
                        + ")";
                    String condicionUpdate = "COMPANIA='" + compania
                        + "' AND CICLO= "
                        + regUsuario.getCampos().get(cicloCons)
                        + " AND CODIGORUTA='"
                        + regUsuario.getCampos().get(codigoRutaCons)
                        + "' AND ANO=" + regUsuario.getCampos().get("ANO")
                        + " AND PERIODO='"
                        + regUsuario.getCampos().get(periodoCons)
                        + "' AND CONCEPTO=247";
                    Acciones.actualizar(ConectorPool.ESQUEMA_SYSMAN,
                                    "SP_FACTURADO", camposUpdate,
                                    condicionUpdate);
                }

            }
        }
        catch (NamingException | SQLException | IllegalAccessException
                        | InstantiationException | ClassNotFoundException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void obtenerValoresPago() {
        if (fechaUsuario) {
            if ("1".equals(regUsuario.getCampos()
                            .get(periodosAtrasoCons))) {
                registroSub.getCampos().put(valorPagoCons,
                                SysmanFunciones.nvl(
                                                regUsuario.getCampos()
                                                                .get(totFacturaPago2Cons),
                                                0));
            }
            else {
                registroSub.getCampos().put(valorPagoCons,
                                SysmanFunciones.nvl(
                                                regUsuario.getCampos()
                                                                .get(totFacturaPerActualCons),
                                                0));
            }
        }
        else {
            registroSub.getCampos().put(valorPagoCons,
                            SysmanFunciones.nvl(
                                            regUsuario.getCampos()
                                                            .get(totFacturaPago2Cons),
                                            0));
        }
    }

    private void etapa11CodigoAfterUpdate() {
        try {
            if ("D".equals(registroSub.getCampos().get("TIPO"))) {
                if (("SI").equals(SysmanFunciones
                                .nvl(Acciones.getParametro(
                                                ConectorPool.ESQUEMA_SYSMAN,
                                                compania,
                                                "RECARGO SEGUNDA FECHA EN PERIODO SIGUIENTE",
                                                SessionUtil.getModulo(),
                                                GeneralParameterEnum.FECHA
                                                                .getName()),
                                                "NO"))) {
                    registroSub.getCampos().put(valorPagoCons,
                                    SysmanFunciones.nvl(
                                                    regUsuario.getCampos()
                                                                    .get(totFacturaPerActualCons),
                                                    0));

                }
                else {
                    obtenerValoresPago();
                }
            }
            else {
                registroSub.getCampos().put(valorPagoCons, 0);

            }
        }

        catch (NamingException | SQLException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void etapa12CodigoAfterUpdate() {
        try {
            String campos = "COMPANIA, FECHA,BANCO,NUMEROPAQUETE,CONCEPTO,VALORDEUDA,VALORPAGOPERIODO,CREDITOABONADO";
            String valores = "SELECT '" + compania + "', "
                + SysmanFunciones.convertirAFechaCadena(
                                (Date) registro.getCampos()
                                                .get(GeneralParameterEnum.FECHA
                                                                .getName()))
                +
                "      ,'"
                + registro.getCampos().get(GeneralParameterEnum.BANCO.getName())
                + "'," +
                "      '" + registro.getCampos().get(numeroPaqueteCons) + "', "
                +
                "      CONCEPTO " +
                "      , 0 , 0,0   " +
                "     FROM SP_FACTURADO   " +
                "     WHERE COMPANIA = '" + compania + "' " +
                "    AND CICLO = '" + regUsuario.getCampos().get(cicloCons) +
                "    AND CODIGORUTA = '"
                + regUsuario.getCampos().get(codigoRutaCons) + "' " +
                "    AND ANO = " + regUsuario.getCampos().get("ANO") +
                "    AND PERIODO = '" + regUsuario.getCampos().get(periodoCons)
                + "' " +
                "     AND CONCEPTO NOT IN   " +
                "     (SELECT CONCEPTO   " +
                "     FROM DRECAUDO   " +
                "     WHERE COMPANIA = '" + compania + "' " +
                "     AND TO_CHAR(FECHA,'DD/MM/YYYY') = "
                + SysmanFunciones.convertirAFechaCadena(
                                (Date) registro.getCampos()
                                                .get(GeneralParameterEnum.FECHA
                                                                .getName()))
                +
                "     AND BANCO='"
                + registro.getCampos().get(GeneralParameterEnum.BANCO.getName())
                + "' " +
                "     AND NUMEROPAQUETE='"
                + registro.getCampos().get(numeroPaqueteCons) + "')";
            Acciones.insertar(ConectorPool.ESQUEMA_SYSMAN, "SP_D_RECAUDO",
                            campos, valores);

        }
        catch (ParseException | IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SQLException
                        | NamingException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void actualizarRegUsuario() {
        String consulta = " SELECT COMPANIA," +
            "        CICLO," +
            "        CODIGORUTA," +
            "        ANO," +
            "        PERIODO ," +
            "        CODIGOINTERNO," +
            "        BANCOPERPROCESO," +
            "        FECHAPAGOPERPROCESO," +
            "        FIMM," +
            "        PAQUETEPAGOPERPROCESO," +
            "        RECAUDADOPROCESO," +
            "        TOTFACTURAPERACTUAL," +
            "        TOTFACTURAPAGO2," +
            "        NOFECHAPAGOPERANTERIOR," +
            "        FECHALIMITE," +
            "        NOFECHAPAGOPERPROCESO," +
            "        NOTACREDITO," +
            "        CREDITOABONADO" +
            "   FROM SP_USUARIO" +
            "  WHERE COMPANIA    ='" + compania + "' " +
            "    AND CODIGOINTERNO ='"
            + registroSub.getCampos().get("CODIGOINTERNO") + "'";

        regUsuario = service.getRegistro(ConectorPool.ESQUEMA_SYSMAN, consulta);

    }

}

package com.sysman.predial;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.ejb.EjbPredialCeroRemote;
import com.sysman.predial.ejb.EjbPredialFinRemote;
import com.sysman.predial.ejb.EjbPredialSeisRemote;
import com.sysman.predial.ejb.EjbPredialSieteRemote;
import com.sysman.predial.enums.AcuerdosusuariosControladorEnum;
import com.sysman.predial.enums.AcuerdosusuariosControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author sdaza
 * @version 1, 29/06/2016
 *
 * @version-- Modificado por lcortes 07,08/02/2017 Se implementa
 * funcionalidad boton Calcular
 * @version-- Modificado por lcortes 28/02/2017, 02,07/03/2017 Se
 * implementa funcionalidad boton Eliminar, se agregan los metodos
 * aceptar y cancelar de los dialogos CuotasCanceladas y
 * DescEspeciales
 * 
 * @author spina - refactorizo conexiones
 * @version 2, 13/06/2017
 * 
 * @author eamaya
 * @version 3.0, 21/06/2017 Proceso de Refactoring,Manejo de EJBs,
 * correcciones SonarLint y creaciï¿½n de Textos en Bean
 * 
 */
@ManagedBean
@ViewScoped

public class AcuerdosusuariosControlador extends BeanBaseContinuoAcmeImpl {
    private final String compania;

    /** Constante a nivel de clase que aloja el valor CODIGO */
    private final String codigo;

    /** Constante a nivel de clase que aloja el valor CODIGOACUERDO */
    private final String codAcuerdo;

    /** Constante a nivel de clase que aloja el valor codigoAcuerdo */
    private final String codAcuerdoMinus;

    /** Constante a nivel de clase que aloja el valor TB_TB393 */
    private final String mensaje;

    /** Constante a nivel de clase que aloja el valor TB_TB405 */
    private final String mensajeA;

    /** Constante a nivel de clase que aloja el valor TB_TB404 */
    private final String mensajeB;

    /** Constante a nivel de clase que aloja el valor TB_TB403 */
    private final String mensajeC;

    /** Constante a nivel de clase que aloja el valor TB_TB2759 */
    private final String mensajeD;

    /** Constante a nivel de clase que aloja el valor RESOLUCION */
    private final String resolucion;

    /** Constante a nivel de clase que aloja el valor ANULADO */
    private final String anulado;

    /** Constante a nivel de clase que aloja el valor SYSDATE */
    private final String sysdate;

    /** Constante a nivel de clase que aloja el valor PREDIO */
    private final String predio;

    /** Constante a nivel de clase que aloja el valor codigoPredio */
    private final String codPredio;

    /**
     * Constante a nivel de clase que aloja el valor codigoPredioOrig
     */
    private final String codPredioOrig;

    /**
     * Constante a nivel de clase que identifica el campo
     * APLICA_DSCESP
     */
    private final String campoAplicaDscesp;
    /**
     * Constante a nivel de clase que identifica el campo CANCELADO
     */
    private final String campoCancelado;
    /**
     * Constante a nivel de clase que identifica el campo CUOTA
     */
    private final String campoCuota;
    /**
     * Constante a nivel de clase que identifica el campo PREANO
     */
    private final String campoPreAno;
    /**
     * Constante a nivel de clase que identifica el campo PREANOI
     */
    private final String campoPreAnoI;
    private String modulo;
    // <DECLARAR_ATRIBUTOS>
    private String buscarAcuerdo;
    private Map<String, Object> rid;
    private String codigoAcuerdo;
    private String estadoAcuerdo;
    private String resAcuerdo;
    private String codigoPredio;
    private String codigoPredioOrig;
    private String anuladoAcu;
    private boolean indPagoAnticipado;
    private boolean indLiqIntAcuerdo = false;
    private boolean indLiqIntRec = false;
    private String plantillaRes;
    private String plantillaFecha;
    private String plantillaNombre;
    private StreamedContent archivoDescarga;
    private boolean verBotAbonar;
    private boolean acuerdoInactivo;
    private boolean tienePermisoAbonos;
    private boolean generaFacturacion;
    /**
     * Variable que permite hacer visible el dialogo Calcular para
     * consultar si se desea calcular los intereses de mora para el
     * acuerdo.
     */
    private boolean mostrarDgCalcular;
    /**
     * Variable que permite hacer visible el dialogo para consultar si
     * se desea eliminar el acuerdo.
     */
    private boolean mostrarDgEliminar;
    /**
     * Variable que permite hacer visible el dialogo para consultar si
     * se desea eliminar el acuerdo pues reporta cuotas canceladas.
     */
    private boolean mostrarDgCuotasCanc;
    /**
     * Variable que permite hacer visible el dialogo para consultar si
     * se desea ingresar un excedente por cobrar de los descuentos
     * realizados.
     */
    private boolean mostrarDgDescEsp;
    /**
     * Variable que permite hacer visible el dialogo para imprimir un
     * recibo de pago.
     */
    private boolean mostrarDgImprimir;

    private int fechaVencida;
    private int cuotaAnterior;
    private int facturarCuota;
    private int anulacionRecibo;
    private int tipoMensaje;
    private String docNum;
    private String textoDialogo;
    private String cuota;
    private String cancelado;
    private String numeroOrden;
    /**
     * Atributo que permite identificar la vigencia a la cual se le va
     * a crear el excedente por cobrar por el valor de los descuentos
     * realizados.
     */
    private String vigenciaSaldo;
    /**
     * Atributo que permite identificar la ley que se esta
     * incumpliendo para eliminar el acuerdo de pago.
     */
    private String leyDescEsp;
    /**
     * Atributo que permite identificar si el acuerdo de pago tiene
     * cuotas canceladas. Valor SI o NO
     */
    private String cuotasCanceladas;
    /**
     * Atributo que permite identificar si se sanula un acuerdo de
     * pago con cuotas canceladas. Valor SI o NO
     */
    private String eliminarAcCuoCan;
    /**
     * Atributo que identifica el valor del campo APLICA_DSCESP del
     * acuerdo seleccionado.
     */
    private boolean aplicaDscesp;
    /**
     * Atributo que identifica el valor del campo PREANOI del acuerdo
     * seleccionado.
     */
    private String preAnioI;
    /**
     * Atributo que identifica el valor del campo PREANO del acuerdo
     * seleccionado.
     */
    private String preAnio;
    private String aplicarDescEsp;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    private String manPagosAntAcuerdos;
    private String activarBtAbonar;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de las vigencias para las cuales es posible crear el
     * excedente por cobrar.
     */
    private List<Registro> listaVigenciaSaldo;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtl;

    @EJB
    private EjbPredialCeroRemote ejbPredialCero;

    @EJB
    private EjbPredialSeisRemote ejbPredialSeis;

    @EJB
    private EjbPredialSieteRemote ejbPredialSiete;

    @EJB
    private EjbPredialFinRemote ejbPredialFin;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCmbAcuerdos;
    private RegistroDataModelImpl listaCmbAcuerdosE;
    private RegistroDataModelImpl listaNoAcuerdo;
    private RegistroDataModelImpl listaNoAcuerdoE;
    private RegistroDataModelImpl listanroPlantilla;
    private RegistroDataModelImpl listanroPlantillaE;
    private String auxiliar;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    private Map<String, Object> parametrosEntrada;

    /**
     * Creates a new instance of AcuerdosusuariosControlador
     */
    @SuppressWarnings("unchecked")
    public AcuerdosusuariosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        codigo = "CODIGO";
        codAcuerdo = "CODIGOACUERDO";
        codAcuerdoMinus = "codigoAcuerdo";
        resolucion = "RESOLUCION";
        anulado = "ANULADO";
        predio = "PREDIO";
        codPredio = "codigoPredio";
        codPredioOrig = "codigoPredioOrig";

        mensaje = "TB_TB393";
        mensajeA = "TB_TB405";
        mensajeB = "TB_TB404";
        mensajeC = "TB_TB403";
        mensajeD = "TB_TB2759";

        sysdate = "SYSDATE";

        campoAplicaDscesp = "APLICA_DSCESP";
        campoCancelado = "CANCELADO";
        campoCuota = "CUOTA";
        campoPreAno = "PREANO";
        campoPreAnoI = "PREANOI";

        try {
            numFormulario = GeneralCodigoFormaEnum.ACUERDOSUSUARIOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            parametrosEntrada = SessionUtil.getFlash();

            if (parametrosEntrada != null) {
                rid = (Map<String, Object>) parametrosEntrada.get("rid");
                codigoPredio = (String) parametrosEntrada.get(codPredio);
                numeroOrden = (String) parametrosEntrada.get("numOrden");
                codigoPredioOrig = parametrosEntrada
                                .get(codPredioOrig) == null
                                    ? (String) parametrosEntrada
                                                    .get(codPredio)
                                    : (String) parametrosEntrada
                                                    .get(codPredioOrig);
                codigoAcuerdo = (String) parametrosEntrada.get(codAcuerdoMinus);
                if (codigoAcuerdo != null) {
                    estadoAcuerdo = (String) parametrosEntrada
                                    .get("estadoAcuerdo");
                    resAcuerdo = (String) parametrosEntrada.get("resAcuerdo");
                    acuerdoInactivo = (boolean) parametrosEntrada
                                    .get("acuerdoInactivo");
                    reasignarOrigen();
                }
            }
            else {
                SessionUtil.redireccionarMenuPermisos();
                return;
            }
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(AcuerdosusuariosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void inicializar() {

        enumBase = GenericUrlEnum.IP_FACTURADOSACUERDOS;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaVigenciaSaldo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCmbAcuerdos();
        cargarListaCmbAcuerdosE();
        cargarListaNoAcuerdo();
        cargarListaNoAcuerdoE();
        cargarListanroPlantilla();
        cargarListanroPlantillaE();
        // </CARGAR_LISTA_COMBO_GRANDE>
        cargarParametros();
        abrirFormulario();

    }

    @Override
    public void reasignarOrigen() {

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosListado.put(GeneralParameterEnum.CODIGOACUERDO.getName(),
                        codigoAcuerdo);

        urlListado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AcuerdosusuariosControladorUrlEnum.URL6969
                                                        .getValue());

    }

    // <METODOS_CARGAR_LISTA>

    /**
     *
     * Carga la lista listaVigenciaSaldo del dialogo DescEspeciales
     *
     */
    public void cargarListaVigenciaSaldo() {

        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaVigenciaSaldo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AcuerdosusuariosControladorUrlEnum.URL13477
                                                                            .getValue())
                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListanroPlantilla() {

        Map<String, Object> param = new TreeMap<>();
        param.put(AcuerdosusuariosControladorEnum.TIPO.getValue(),
                        "24");
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AcuerdosusuariosControladorUrlEnum.URL14084
                                                        .getValue());

        listanroPlantilla = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigo);
    }

    public void cargarListanroPlantillaE() {
        listanroPlantillaE = listanroPlantilla;

    }

    public void cargarListaCmbAcuerdos() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AcuerdosusuariosControladorUrlEnum.URL15258
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.PREDIO.getName(),
                        codigoPredio);

        listaCmbAcuerdos = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codAcuerdo);
    }

    public void cargarListaCmbAcuerdosE() {
        listaCmbAcuerdosE = listaCmbAcuerdos;

    }

    public void cargarListaNoAcuerdo() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AcuerdosusuariosControladorUrlEnum.URL17796
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaNoAcuerdo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codAcuerdo);
    }

    public void cargarListaNoAcuerdoE() {
        listaNoAcuerdoE = listaNoAcuerdo;
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimircmdestadoap() {
        // <CODIGO_DESARROLLADO>
        // el boton no se cambia a estado visible en ningun parte,
        // este ejecuta un reporte que no se migra tampoco.
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCmdResolucion() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCmdPagoA() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCmdDetalle() {
        // <CODIGO_DESARROLLADO>
        if ((codigoAcuerdo == null) || codigoAcuerdo.isEmpty()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString(mensaje));
            return;
        }
        Map<String, Object> parametros = new HashMap<>();
        String ruta = "/facturadosacuerdosdet.sysman";
        parametros.put(codPredio, codigoPredio);
        parametros.put(codPredioOrig, codigoPredioOrig);
        parametros.put(codAcuerdoMinus, codigoAcuerdo);
        parametros.put("resAcuerdo", resAcuerdo);
        parametros.put("estadoAcuerdo", estadoAcuerdo);
        parametros.put("acuerdoInactivo", acuerdoInactivo);
        parametros.put("rid", rid);
        Direccionador direccionador = new Direccionador();
        direccionador.setRuta(ruta);
        direccionador.setParametros(parametros);
        SessionUtil.setSessionVar("retornoFormulario", "retorna");
        SessionUtil.redireccionar(direccionador);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCmEliminarAcuerdo() {
        // <CODIGO_DESARROLLADO>
        mostrarDgEliminar = true;
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCmdCalcularAcuerdo() {
        // <CODIGO_DESARROLLADO>
        if ((codigoAcuerdo == null) || codigoAcuerdo.isEmpty()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString(mensaje));
            return;
        }
        else {
            mostrarDgCalcular = true;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCmdBuscarAP() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Verifica que existan IP_FACTURADOSACUERDOS en la lista
     *
     * @param list
     * Lista de registros.
     * @return true si la cantidad de elementos es mayor a cero
     */
    private static boolean isAuxCtasPend(List<Registro> list) {
        return Integer.parseInt(list.get(0).getCampos().get("CANT")
                        .toString()) != 0;
    }

    public void oprimirCmdAbonar(Registro reg, int indice) {
        if (!tienePermisoAbonos) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB396"));
            return;
        }

        HashMap<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.CODIGOACUERDO.getName(), codigoAcuerdo);
        param.put(GeneralParameterEnum.CUOTA.getName(),
                        reg.getCampos().get(campoCuota));

        List<Registro> auxCtasPend;
        try {
            auxCtasPend = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AcuerdosusuariosControladorUrlEnum.URL18701
                                                                            .getValue())
                                            .getUrl(),
                                            param));

            if (!auxCtasPend.isEmpty() && isAuxCtasPend(auxCtasPend)) {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString("MSM_TRANS_INTERRUMPIDA")
                                    + idioma.getString("TB_TB398"));
                return;

            }

            String[] campos = new String[4];
            String[] valores = new String[4];

            campos[0] = codPredio;
            campos[1] = codAcuerdoMinus;
            campos[2] = "nroCuota";
            campos[3] = "vlrCuota";
            valores[0] = codigoPredio;
            valores[1] = codigoAcuerdo;
            valores[2] = reg.getCampos().get(campoCuota).toString();
            valores[3] = reg.getCampos().get("TOTAL").toString();
            SessionUtil.cargarModalDatosFlash(Integer.toString(
                            GeneralCodigoFormaEnum.FACTURAR_AB_ACUERDOS_CONTROLADOR
                                            .getCodigo()),
                            SessionUtil.getModulo(),
                            campos, valores);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirCmdImprimir(Registro reg, int indice) {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        try {
            textoDialogo = "";
            tipoMensaje = 1;
            cuota = reg.getCampos().get(campoCuota).toString();
            fechaVencida = ejbPredialSeis
                            .verificarFechaLimiteCuota(codigoAcuerdo,
                                            Long.parseLong(cuota));

            cuotaAnterior = 0;

            facturarCuota = ejbPredialSeis
                            .verificarFacCuotaAnterior(codigoAcuerdo,
                                            Long.parseLong(cuota));

            anulacionRecibo = ejbPredialSeis.verificarAnulacionReciboPendiente(
                            codigoAcuerdo, codigoPredio, compania,
                            Long.parseLong(cuota));

            mostrarDgImprimir = true;
            if (fechaVencida == 1) {
                textoDialogo = idioma.getString("TB_TB3245");
                tipoMensaje = 2;
            }
            else if (cuotaAnterior == 1) {
                textoDialogo = idioma.getString("TB_TB3246");
                tipoMensaje = 3;
            }
            else if (facturarCuota == 1) {
                textoDialogo = idioma.getString("TB_TB3247");
                tipoMensaje = 4;
            }
            else if (anulacionRecibo == 1) {
                textoDialogo = idioma.getString("TB_TB3248");
                tipoMensaje = 5;
            }
            else {
                tipoMensaje = 6;
                mostrarDgImprimir = false;
                llamarfuncion();
            }

        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if ("SI".equals(manPagosAntAcuerdos) && indPagoAnticipado) {
            if (indLiqIntAcuerdo) {
                JsfUtil.agregarMensajeAlerta(idioma.getString(mensajeD));
            }
            if (indLiqIntRec) {
                JsfUtil.agregarMensajeAlerta(idioma.getString(mensajeD));
            }
            /* Si alguno de los indicadores tiene valor false */
            if (!indLiqIntAcuerdo || !indLiqIntRec) {
                JsfUtil.agregarMensajeAlerta(idioma.getString(mensajeD));
            }
        }

        if (!generaFacturacion) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB396"));

        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarCmbAcuerdos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarBuscar() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void aceptarImprimirPregunta() {
        if ((cuotaAnterior == 1) && (tipoMensaje == 2)) {
            textoDialogo = idioma.getString("TB_TB3246");
            tipoMensaje += 1;
        }
        else {
            if ((cuotaAnterior == 0) && (tipoMensaje == 2)) {
                tipoMensaje += 1;
            }
            if ((facturarCuota == 1) && (tipoMensaje == 3)) {
                textoDialogo = idioma.getString("TB_TB3247");
                tipoMensaje += 1;
            }
            else {
                if ((facturarCuota == 0) && (tipoMensaje == 3)) {
                    tipoMensaje += 1;
                }
                if ((anulacionRecibo == 1) && (tipoMensaje == 4)) {
                    textoDialogo = idioma.getString("TB_TB3248");
                    tipoMensaje += 1;
                }
                else {
                    if ((anulacionRecibo == 0) && (tipoMensaje == 4)) {
                        tipoMensaje += 1;
                    }
                    if (tipoMensaje >= 5) {
                        tipoMensaje += 1;
                    }
                }
            }
        }

        llamarfuncion();

    }

    public void llamarfuncion() {
        if (tipoMensaje > 5) {

            try {
                int anuladoAux = ("No").equals(anuladoAcu) ? 0 : 1;
                int canceladoAux = ("No").equals(cancelado) ? 0 : 1;
                docNum = ejbPredialSeis.imprimirCuotaAcuerdoDePago(
                                codigoAcuerdo,
                                compania,
                                SessionUtil.getUser().getCodigo(),
                                fechaVencida,
                                Boolean.parseBoolean(
                                                Integer.toString(anuladoAux)),
                                Boolean.parseBoolean(
                                                Integer.toString(canceladoAux)),
                                cuotaAnterior,
                                Long.parseLong(cuota),
                                facturarCuota,
                                anulacionRecibo,
                                codigoPredio);

                mostrarDgImprimir = false;
                archivoDescarga = null;
                generarReporte(FORMATOS.PDF);
                reasignarOrigen();

            }
            catch (NumberFormatException | SystemException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        }
    }

    private void generarReporte(FORMATOS formato) {
        try {
            String codigoEAN = SysmanFunciones.nvlStr(ejbSysmanUtl
                            .consultarParametro(compania, "CODIGO EAN", modulo,
                                            new Date(), false),
                            "CONFIGURE EL PARAMETRO CODIGO EAN");

            Map<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            reemplazar.put("compania", "'" + compania + "'");
            reemplazar.put("codigo", "'" + codigoPredio + "'");
            reemplazar.put("numeroOrden", "'" + numeroOrden + "'");
            reemplazar.put("strCodigoEAN", "'" + codigoEAN + "'");
            reemplazar.put("docnum", "'" + docNum + "'");

            // PARAMETROS
            parametros.put("PR_COPIA", "SI".equals(SysmanFunciones
                            .nvlStr(ejbSysmanUtl.consultarParametro(compania,
                                            "MANEJA COPIA DE RECIBO EN PREDIAL",
                                            modulo, new Date(), false),
                                            "SI")));

            parametros.put("PR_NOMBREBANCO", " ");
            String textoCuotas = ejbPredialSeis.obtenerCuotasRecibos(
                            codigoAcuerdo, codigoPredio, docNum);

            parametros.put("PR_CUOTASRECIBOS", textoCuotas);
            parametros.put("PR_FECHA", sysdate);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_GETUSER", SessionUtil.getUser().getCodigo());
            parametros.put("PR_LEYENDA_USUARIO", SysmanFunciones
                            .nvlStr(ejbSysmanUtl.consultarParametro(compania,
                                            "LEYENDA USUARIO", modulo,
                                            new Date(), false), ""));

            parametros.put("PR_LEYENDA_LEGAL_ACUERDO",
                            SysmanFunciones.nvlStr(
                                            ejbSysmanUtl.consultarParametro(
                                                            compania,
                                                            "LEYENDA LEGAL ACUERDO",
                                                            modulo, new Date(),
                                                            false),
                                            ""));

            parametros.put("PR_GETPAGINAWEB", "sysman.com.co");

            parametros.put("PR_ENCABEZADO_COLUMNA1", ejbPredialCero
                            .consultarEncabezadoDeColumna(compania, 1));

            parametros.put("PR_ENCABEZADO_COLUMNA2", ejbPredialCero
                            .consultarEncabezadoDeColumna(compania, 2));

            parametros.put("PR_ENCABEZADO_COLUMNA13", ejbPredialCero
                            .consultarEncabezadoDeColumna(compania, 13));

            parametros.put("PR_ENCABEZADO_COLUMNA14", ejbPredialCero
                            .consultarEncabezadoDeColumna(compania, 14));

            parametros.put("PR_ENCABEZADO_COLUMNA15", ejbPredialCero
                            .consultarEncabezadoDeColumna(compania, 15));

            parametros.put("PR_ENCABEZADO_COLUMNA17", ejbPredialCero
                            .consultarEncabezadoDeColumna(compania, 17));

            parametros.put("PR_ENCABEZADO_COLUMNA20", ejbPredialCero
                            .consultarEncabezadoDeColumna(compania, 20));
            
            // IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
            parametros.put("PR_EMPRESAPARAMETRIZADA", JsfUtil.obtenerParametroMarcaBlanca("TITULOLOGIN"));
            // FIN IMPLEMENTACION MARCA_BLANCA

            Reporteador.resuelveConsulta("001057FORMATOSTDACUERDO",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "001057FORMATOSTDACUERDO", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (OutOfMemoryError | JRException | IOException
                        | SysmanException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cancelarImprimirPregunta() {
        mostrarDgImprimir = false;
        JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2807"));

    }

    public void aceptarBuscar() {
        // <CODIGO_DESARROLLADO>
        codigoAcuerdo = buscarAcuerdo;
        buscarAcuerdo = null;
        reasignarOrigen();
        cargarListaCmbAcuerdos();
        // </CODIGO_DESARROLLADO>
    }

    public void aceptarResolucion() {
        // <CODIGO_DESARROLLADO>
        if ((codigoAcuerdo == null) || codigoAcuerdo.isEmpty()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString(mensaje));
            return;
        }
        try {
            String[] campos = new String[3];
            String[] valores = new String[3];

            campos[0] = "codigoPlantilla";
            campos[1] = "fechaPlantilla";
            campos[2] = "nombreDocDescarga";

            valores[0] = plantillaRes;
            valores[1] = SysmanFunciones.formatearFecha(
                            SysmanFunciones.convertirAFecha(plantillaFecha));
            valores[2] = plantillaNombre;

            HashMap<String, String> variablesConsultaW = new HashMap<>();

            variablesConsultaW.put("s$compania$s", "'" + compania + "'");
            variablesConsultaW.put("s$codigoAcuerdo$s",
                            "'" + codigoAcuerdo + "'");
            SessionUtil.setSessionVar("variablesConsultaWord",
                            variablesConsultaW);

            SessionUtil.cargarModalDatosFlash("281", SessionUtil.getModulo(),
                            campos, valores);
        }
        catch (ParseException e) {
            Logger.getLogger(
                            AcuerdosusuariosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void aceptarPagoAnticipado() {
        try {
            int valAcuerdo;
            int valRecargo;

            if (indLiqIntAcuerdo) {
                valAcuerdo = 1;
            }
            else {
                valAcuerdo = 0;
            }
            if (indLiqIntRec) {
                valRecargo = 1;
            }
            else {
                valRecargo = 0;
            }

            ejbPredialFin.manejarPagoAnticipado(compania, codigoAcuerdo,
                            valAcuerdo, valRecargo);

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void aceptarCalcular() {
        // <CODIGO_DESARROLLADO>
        mostrarDgCalcular = false;

        String anuladoAux = ("No").equals(anuladoAcu) ? "0" : "-1";

        try {
            String res = ejbPredialFin.calcularCuotasAcuerdo(compania,
                            codigoAcuerdo,
                            Boolean.parseBoolean(anuladoAux),
                            SessionUtil.getUser().getCodigo(), new Date(),
                            codigoPredioOrig);

            if (("-1").equals(res)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2805"));
            }
            if (("NO").equals(res)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2806"));
            }
            if (("OK").equals(res)) {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB874"));
                reasignarOrigen();
            }

        }
        catch (SystemException e) {
            Logger.getLogger(AcuerdosusuariosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    public void cancelarCalcular() {
        mostrarDgCalcular = false;
        JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2807"));
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * Eliminar en la vista
     *
     */
    public void aceptarEliminar() {
        // <CODIGO_DESARROLLADO>
        mostrarDgEliminar = false;
        if ((codigoAcuerdo == null) || codigoAcuerdo.isEmpty()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString(mensaje));
            return;
        }

        HashMap<String, Object> param = new HashMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGOACUERDO.getName(), codigoAcuerdo);
        param.put(GeneralParameterEnum.PREDIO.getName(), codigoPredio);

        Registro regAux;
        try {
            regAux = RegistroConverter
                            .toRegistro(requestManager
                                            .get(UrlServiceUtil
                                                            .getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            AcuerdosusuariosControladorUrlEnum.URL14672
                                                                                            .getValue())
                                                            .getUrl(),
                                                            param));

            if (regAux != null) {
                // Etapa 3: Valida si es un acuerdo con cuotas
                // canceladas
                String pago = regAux.getCampos().get("PAGO").toString();
                if (!("0").equals(pago)) {
                    cuotasCanceladas = "SI";
                    mostrarDgCuotasCanc = true;
                }
                else {
                    cuotasCanceladas = "NO";
                    if (aplicaDscesp) {
                        mostrarDgDescEsp = true;
                    }
                    else {
                        aplicarDescEsp = "NO";
                        ejecutarFuncionEliminar();
                    }
                }
            }

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * Eliminar (DG71) en la vista
     *
     */
    public void cancelarEliminar() {
        mostrarDgEliminar = false;
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * CuotasCanceladas en la vista
     *
     */
    public void aceptarCuotasCanceladas() {
        // <CODIGO_DESARROLLADO>
        mostrarDgCuotasCanc = false;
        eliminarAcCuoCan = "SI";
        if (aplicaDscesp) {
            mostrarDgDescEsp = true;
        }
        else {
            aplicarDescEsp = "NO";
            ejecutarFuncionEliminar();
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * CuotasCanceladas en la vista
     *
     */
    public void cancelarCuotasCanceladas() {
        // <CODIGO_DESARROLLADO>
        mostrarDgCuotasCanc = false;
        eliminarAcCuoCan = "NO";
        if (aplicaDscesp) {
            mostrarDgDescEsp = true;
        }
        else {
            aplicarDescEsp = "NO";
            ejecutarFuncionEliminar();
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * DescEspeciales en la vista
     *
     */
    public void aceptarDescEspeciales() {
        // <CODIGO_DESARROLLADO>
        aplicarDescEsp = "SI";
        ejecutarFuncionEliminar();
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * DescEspeciales en la vista
     *
     */
    public void cancelarDescEspeciales() {
        // <CODIGO_DESARROLLADO>
        aplicarDescEsp = "NO";
        ejecutarFuncionEliminar();
        // </CODIGO_DESARROLLADO>
    }

    private void ejecutarFuncionEliminar() {
        int vigSaldo = vigenciaSaldo == null ? 0
            : Integer.parseInt(vigenciaSaldo);

        try {
            String res = ejbPredialSiete.eliminarAcuerdoDePago(
                            compania,
                            codigoAcuerdo,
                            SessionUtil.getUser().getCodigo(),
                            codigoPredio,
                            Integer.parseInt(preAnioI),
                            Integer.parseInt(preAnio),
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL,
                            cuotasCanceladas,
                            eliminarAcCuoCan,
                            aplicarDescEsp,
                            leyDescEsp,
                            vigSaldo);

            if (("OK").equals(res)) {
                JsfUtil.agregarMensajeInformativo(idioma.getString(
                                "TB_TB874"));
                reasignarOrigen();
                acuerdoInactivo = true;
                estadoAcuerdo = idioma.getString("TB_TB404");
            }
            else {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2898"));
            }

        }
        catch (NumberFormatException | SystemException e) {
            Logger.getLogger(AcuerdosusuariosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCmbAcuerdos(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoAcuerdo = registroAux.getCampos().get(codAcuerdo).toString();
        codigoPredio = registroAux.getCampos().get(predio).toString();
        resAcuerdo = registroAux.getCampos().get(resolucion).toString();
        anuladoAcu = registroAux.getCampos().get(anulado).toString();
        cancelado = registroAux.getCampos().get(campoCancelado).toString();
        aplicaDscesp = (boolean) registroAux.getCampos().get(campoAplicaDscesp);
        preAnioI = registroAux.getCampos().get(campoPreAnoI).toString();
        preAnio = registroAux.getCampos().get(campoPreAno).toString();
        if ("Si".equals(registroAux.getCampos().get(campoCancelado))) {
            estadoAcuerdo = idioma.getString(mensajeC);
            acuerdoInactivo = true;
        }
        else if ("Si".equals(registroAux.getCampos().get(anulado))) {
            estadoAcuerdo = idioma.getString(mensajeB);
            acuerdoInactivo = true;
        }
        else {
            estadoAcuerdo = idioma.getString(mensajeA);
            acuerdoInactivo = false;
        }

        reasignarOrigen();
    }

    public void onRowSelectCmbAcuerdosE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(codAcuerdo).toString();
        codigoPredio = registroAux.getCampos().get(predio).toString();
        resAcuerdo = registroAux.getCampos().get(resolucion).toString();
        aplicaDscesp = (boolean) registroAux.getCampos().get(campoAplicaDscesp);
        preAnioI = registroAux.getCampos().get(campoPreAnoI).toString();
        preAnio = registroAux.getCampos().get(campoPreAno).toString();
        if ((boolean) registroAux.getCampos().get(campoCancelado)) {
            estadoAcuerdo = idioma.getString(mensajeC);
        }
        else if ((boolean) registroAux.getCampos().get(anulado)) {
            estadoAcuerdo = idioma.getString(mensajeB);
        }
        else {
            estadoAcuerdo = idioma.getString(mensajeA);
        }

        reasignarOrigen();
    }

    public void seleccionarFilaNoAcuerdo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        buscarAcuerdo = registroAux.getCampos().get(codAcuerdo).toString();
        codigoPredio = registroAux.getCampos().get(predio).toString();
        resAcuerdo = registroAux.getCampos().get(resolucion).toString();
        anuladoAcu = registroAux.getCampos().get(anulado).toString();
        cancelado = registroAux.getCampos().get("PAGO").toString();
        aplicaDscesp = (boolean) registroAux.getCampos().get(campoAplicaDscesp);
        preAnioI = registroAux.getCampos().get(campoPreAnoI).toString();
        preAnio = registroAux.getCampos().get(campoPreAno).toString();
        if ("Si".equals(registroAux.getCampos().get("PAGO"))) {
            estadoAcuerdo = idioma.getString(mensajeC);
            acuerdoInactivo = true;
        }
        else if ("Si".equals(registroAux.getCampos().get(anulado))) {
            estadoAcuerdo = idioma.getString(mensajeB);
            acuerdoInactivo = true;
        }
        else {
            estadoAcuerdo = idioma.getString(mensajeA);
            acuerdoInactivo = false;
        }

    }

    public void onRowSelectNoAcuerdoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(codAcuerdo).toString();
    }

    public void seleccionarFilanroPlantilla(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        plantillaRes = registroAux.getCampos().get(codigo).toString();
        plantillaFecha = registroAux.getCampos().get("FECHA").toString();
        plantillaNombre = registroAux.getCampos().get("NOMBRE").toString();
    }

    public void onRowSelectnroPlantillaE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = registroAux.getCampos().get(codigo).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        if (codigoAcuerdo == null) {
            acuerdoInactivo = true;
        }
        indPagoAnticipado = false;

        verBotAbonar = "SI".equals(activarBtAbonar) ? true : false;
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
        // Codigo
    }

    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        // verifica si el predio asociado al predio es diferente al
        // predio con el cual se accede a este formulario
        // si son diferentes debe hallar el rowid relacionado al
        // predio para cargar el formulario de usuarios predial
        if (codigoPredioOrig.compareTo(codigoPredio) != 0) {
            List<Registro> auxIdPredio = service.getListado(
                            ConectorPool.ESQUEMA_SYSMAN,
                            " SELECT  INDBORRADO, COMPANIA, CODIGO, NUMERO_ORDEN   \n"
                                + " FROM IP_USUARIOS_PREDIAL \n"
                                + " WHERE CODIGO = '" + codigoPredio + "' "
                                + "  AND NUMERO_ORDEN = '"
                                + SysmanConstantes.NUMERO_ORDEN_PREDIAL + "'");
            if (!auxIdPredio.isEmpty()) {
                if ((boolean) auxIdPredio.get(0).getCampos()
                                .get("INDBORRADO")) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB12")
                        + codigoPredioOrig);
                }
                else {
                    rid = new HashMap<>();
                    rid.put("COMPANIA", auxIdPredio.get(0).getCampos()
                                    .get("COMPANIA"));
                    rid.put(codigo, auxIdPredio.get(0).getCampos()
                                    .get(codigo));
                    rid.put("NUMERO_ORDEN", auxIdPredio.get(0).getCampos()
                                    .get("NUMERO_ORDEN"));
                }
            }
        }
        String ruta = "/usuariospredial.sysman";
        if (parametrosEntrada == null) {
            parametrosEntrada = new HashMap<>();
        }
        parametrosEntrada.put("rid", rid);
        Direccionador direccionador = new Direccionador();
        direccionador.setRuta(ruta);
        direccionador.setParametros(parametrosEntrada);
        SessionUtil.redireccionar(direccionador);
        // </CODIGO_DESARROLLADO>
    }

    public void cargarParametros() {
        try {
            // parï¿½metro que indica si la entidad permite el pago
            // anticipado de los acuerdos de pago
            manPagosAntAcuerdos = ejbSysmanUtl.consultarParametro(compania,
                            "MANEJA PAGOS ANTICIPADOS EN ACUERDOS", modulo,
                            new Date(), false);

            manPagosAntAcuerdos = manPagosAntAcuerdos == null ? "NO"
                : manPagosAntAcuerdos;

            // valida si el usuario tiene permisos para generar abonos
            // a los acuerdos de pago
            tienePermisoAbonos = ejbPredialCero
                            .consultarNombreUsuarioEnParametro(compania,
                                            Integer.parseInt(modulo),
                                            "AUTORIZADOS ABONOS EN ACUERDOS",
                                            SessionUtil.getUser().getCodigo());

            generaFacturacion = ejbPredialCero
                            .consultarNombreUsuarioEnParametro(compania,
                                            Integer.parseInt(modulo),
                                            "USUARIOS QUE GENERAN FACTURACION",
                                            SessionUtil.getUser().getCodigo());

            activarBtAbonar = ejbSysmanUtl.consultarParametro(compania,
                            "PERMITE ABONOS EN ACUERDOS", modulo, new Date(),
                            false);

            activarBtAbonar = activarBtAbonar == null ? "NO" : activarBtAbonar;

        }
        catch (SystemException e) {
            Logger.getLogger(
                            AcuerdosusuariosControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    @Override
    public void asignarValoresRegistro() {
        // Codigo
    }

    // <SET_GET_ATRIBUTOS>

    public String getCodigoAcuerdo() {
        return codigoAcuerdo;
    }

    public String getBuscarAcuerdo() {
        return buscarAcuerdo;
    }

    public void setBuscarAcuerdo(String buscarAcuerdo) {
        this.buscarAcuerdo = buscarAcuerdo;
    }

    public void setCodigoAcuerdo(String codigoAcuerdo) {
        this.codigoAcuerdo = codigoAcuerdo;
    }

    public String getEstadoAcuerdo() {
        return estadoAcuerdo;
    }

    public void setEstadoAcuerdo(String estadoAcuerdo) {
        this.estadoAcuerdo = estadoAcuerdo;
    }

    public String getResAcuerdo() {
        return resAcuerdo;
    }

    public void setResAcuerdo(String resAcuerdo) {
        this.resAcuerdo = resAcuerdo;
    }

    public String getCodigoPredio() {
        return codigoPredio;
    }

    public void setCodigoPredio(String codigoPredio) {
        this.codigoPredio = codigoPredio;
    }

    public boolean isIndLiqIntAcuerdo() {
        return indLiqIntAcuerdo;
    }

    public void setIndLiqIntAcuerdo(boolean indLiqIntAcuerdo) {
        this.indLiqIntAcuerdo = indLiqIntAcuerdo;
    }

    public boolean isIndLiqIntRec() {
        return indLiqIntRec;
    }

    public void setIndLiqIntRec(boolean indLiqIntRec) {
        this.indLiqIntRec = indLiqIntRec;
    }

    public String getPlantillaRes() {
        return plantillaRes;
    }

    public void setPlantillaRes(String plantillaRes) {
        this.plantillaRes = plantillaRes;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * Asigna la variable mostrarDgCalcular
     *
     * @return mostrarDgCalcular
     */
    public boolean isMostrarDgCalcular() {
        return mostrarDgCalcular;
    }

    /**
     * Retorna la variable mostrarDgCalcular
     *
     * @param mostrarDgCalcular
     */
    public void setMostrarDgCalcular(boolean mostrarDgCalcular) {
        this.mostrarDgCalcular = mostrarDgCalcular;
    }

    /**
     * Asigna la variable mostrarDgEliminar
     *
     * @return mostrarDgEliminar
     */
    public boolean isMostrarDgEliminar() {
        return mostrarDgEliminar;
    }

    /**
     * Retorna la variable mostrarDgEliminar
     *
     * @param mostrarDgEliminar
     */
    public void setMostrarDgEliminar(boolean mostrarDgEliminar) {
        this.mostrarDgEliminar = mostrarDgEliminar;
    }

    /**
     * Asigna la variable mostrarDgCuotasCanc
     *
     * @return mostrarDgCuotasCanc
     */
    public boolean isMostrarDgCuotasCanc() {
        return mostrarDgCuotasCanc;
    }

    /**
     * Retorna la variable mostrarDgCuotasCanc
     *
     * @param mostrarDgCuotasCanc
     */
    public void setMostrarDgCuotasCanc(boolean mostrarDgCuotasCanc) {
        this.mostrarDgCuotasCanc = mostrarDgCuotasCanc;
    }

    /**
     * Asigna la variable mostrarDgDescEsp
     *
     * @return mostrarDgDescEsp
     */
    public boolean isMostrarDgDescEsp() {
        return mostrarDgDescEsp;
    }

    /**
     * Retorna la variable mostrarDgDescEsp
     *
     * @param mostrarDgDescEsp
     */
    public void setMostrarDgDescEsp(boolean mostrarDgDescEsp) {
        this.mostrarDgDescEsp = mostrarDgDescEsp;
    }

    public boolean isMostrarDgImprimir() {
        return mostrarDgImprimir;
    }

    public void setMostrarDgImprimir(boolean mostrarDgImprimir) {
        this.mostrarDgImprimir = mostrarDgImprimir;
    }

    public String getTextoDialogo() {
        return textoDialogo;
    }

    public void setTextoDialogo(String textoDialogo) {
        this.textoDialogo = textoDialogo;
    }

    /**
     * Retorna la variable vigenciaSaldo
     *
     * @return vigenciaSaldo
     */
    public String getVigenciaSaldo() {
        return vigenciaSaldo;
    }

    /**
     * Asigna la variable vigenciaSaldo
     *
     * @param vigenciaSaldo
     * Variable a asignar en vigenciaSaldo
     */
    public void setVigenciaSaldo(String vigenciaSaldo) {
        this.vigenciaSaldo = vigenciaSaldo;
    }

    /**
     * Retorna la variable leyDescEsp
     *
     * @return leyDescEsp
     */
    public String getLeyDescEsp() {
        return leyDescEsp;
    }

    /**
     * Asigna la variable leyDescEsp
     *
     * @param leyDescEsp
     * Variable a asignar en leyDescEsp
     */
    public void setLeyDescEsp(String leyDescEsp) {
        this.leyDescEsp = leyDescEsp;
    }
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>

    /**
     * Retorna la lista listaVigenciaSaldo
     *
     * @return listaVigenciaSaldo
     */
    public List<Registro> getListaVigenciaSaldo() {
        return listaVigenciaSaldo;
    }

    /**
     * Asigna la lista listaVigenciaSaldo
     *
     * @param listaVigenciaSaldo
     * Variable a asignar en listaVigenciaSaldo
     */
    public void setListaVigenciaSaldo(List<Registro> listaVigenciaSaldo) {
        this.listaVigenciaSaldo = listaVigenciaSaldo;
    }

    public boolean isVerBotAbonar() {
        return verBotAbonar;
    }

    public void setVerBotAbonar(boolean verBotAbonar) {
        this.verBotAbonar = verBotAbonar;
    }

    public boolean isAcuerdoInactivo() {
        return acuerdoInactivo;
    }

    public void setAcuerdoInactivo(boolean acuerdoInactivo) {
        this.acuerdoInactivo = acuerdoInactivo;
    }

    public String getManPagosAntAcuerdos() {
        return manPagosAntAcuerdos;
    }

    public void setManPagosAntAcuerdos(String manPagosAntAcuerdos) {
        this.manPagosAntAcuerdos = manPagosAntAcuerdos;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCmbAcuerdos() {
        return listaCmbAcuerdos;
    }

    public void setListaCmbAcuerdos(RegistroDataModelImpl listaCmbAcuerdos) {
        this.listaCmbAcuerdos = listaCmbAcuerdos;
    }

    public RegistroDataModelImpl getListaCmbAcuerdosE() {
        return listaCmbAcuerdosE;
    }

    public void setListaCmbAcuerdosE(RegistroDataModelImpl listaCmbAcuerdosE) {
        this.listaCmbAcuerdosE = listaCmbAcuerdosE;
    }

    public RegistroDataModelImpl getListaNoAcuerdo() {
        return listaNoAcuerdo;
    }

    public void setListaNoAcuerdo(RegistroDataModelImpl listaNoAcuerdo) {
        this.listaNoAcuerdo = listaNoAcuerdo;
    }

    public RegistroDataModelImpl getListaNoAcuerdoE() {
        return listaNoAcuerdoE;
    }

    public void setListaNoAcuerdoE(RegistroDataModelImpl listaNoAcuerdoE) {
        this.listaNoAcuerdoE = listaNoAcuerdoE;
    }

    public RegistroDataModelImpl getListanroPlantilla() {
        return listanroPlantilla;
    }

    public void setListanroPlantilla(RegistroDataModelImpl listanroPlantilla) {
        this.listanroPlantilla = listanroPlantilla;
    }

    public RegistroDataModelImpl getListanroPlantillaE() {
        return listanroPlantillaE;
    }

    public void setListanroPlantillaE(
        RegistroDataModelImpl listanroPlantillaE) {
        this.listanroPlantillaE = listanroPlantillaE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public String getAnuladoAcu() {
        return anuladoAcu;
    }

    public void setAnuladoAcu(String anuladoAcu) {
        this.anuladoAcu = anuladoAcu;
    }
}

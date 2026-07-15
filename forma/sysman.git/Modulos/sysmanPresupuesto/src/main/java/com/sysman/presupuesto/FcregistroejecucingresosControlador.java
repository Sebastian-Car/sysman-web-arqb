package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.FcregistroejecucingresosControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author lcortes
 * @version 1, 06/07/2016
 * @modified jguerrero
 * @version 2. 18/04/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Adem�s se ajustaron los errores del sonar
 * 
 * @author jreina
 * @version 3, 13/06/2017 Cambio c�digo formulario y actualizaci�n
 * de ConnectorPool
 * 
 */
@ManagedBean
@ViewScoped

public class FcregistroejecucingresosControlador extends BeanBaseModal {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private boolean conCentros;
    private boolean conFuentes;
    private boolean indicador;
    private boolean enMiles;
    private String centroCostoInicial;
    private String centroCostoFinal;
    private String fuenteInicial;
    private String fuenteFinal;
    private String anio;
    private String mes;
    private String cuentaInicial;
    private String cuentaFinal;
    private String nomCentroInicial;
    private String nomCentroFinal;
    private String nomFuenteInicial;
    private String nomFuenteFinal;
    private String nomCuentaInicial;
    private String nomCuentaFinal;
    private String nivel;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAno;
    private List<Registro> listaMes;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCentroCostoInicial;
    private RegistroDataModelImpl listaCentroCostoFinal;
    private RegistroDataModelImpl listaFuenteInicial;
    private RegistroDataModelImpl listaFuenteFinal;
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    private static final String NOMBRE = "NOMBRE";
    private static final String CODIGO = "CODIGO";

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of FcregistroejecucingresosControlador
     */
    public FcregistroejecucingresosControlador() {
        super();
        compania = SessionUtil.getCompania();
        anio = String.valueOf(SysmanFunciones
                        .ano(new Date()));
        mes = String.valueOf(SysmanFunciones
                        .mes(new Date()));
        try {
            numFormulario = GeneralCodigoFormaEnum.FCREGISTROEJECUCINGRESOS_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(
                            FcregistroejecucingresosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>

        cargarListaAno();
        cargarListaMes();

        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        /*
         * FR977-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaAno() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FcregistroejecucingresosControladorUrlEnum.URL4579
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaMes() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        try {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FcregistroejecucingresosControladorUrlEnum.URL5098
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaCentroCostoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FcregistroejecucingresosControladorUrlEnum.URL5574
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCentroCostoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO);
        // 20013

    }

    public void cargarListaCentroCostoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FcregistroejecucingresosControladorUrlEnum.URL6238
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.CENTRO_COSTO.getName(),
                        centroCostoInicial);

        listaCentroCostoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO);

    }

    public void cargarListaFuenteInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FcregistroejecucingresosControladorUrlEnum.URL6981
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaFuenteInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO);
    }

    public void cargarListaFuenteFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FcregistroejecucingresosControladorUrlEnum.URL7598
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put("CODIGOINICIAL", fuenteInicial);

        listaFuenteFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO);

    }

    public void cargarListaCuentaInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FcregistroejecucingresosControladorUrlEnum.URL8291
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO);
    }

    public void cargarListaCuentaFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FcregistroejecucingresosControladorUrlEnum.URL9038
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.CUENTA.getName(), cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO);

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        generarInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCmdExcel() {
        generarInforme(ReportesBean.FORMATOS.EXCEL97);
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public boolean validarCampos() {
        boolean rta = false;
        if (SysmanFunciones.validarVariableVacio(anio) ||
            SysmanFunciones.validarVariableVacio(mes) ||
            SysmanFunciones.validarVariableVacio(cuentaInicial)) {
            rta = true;
        }
        if (SysmanFunciones.validarVariableVacio(cuentaFinal)
            || SysmanFunciones.validarVariableVacio(nivel)) {
            rta = true;
        }
        return rta;
    }

    public boolean validarCamposCentro() {
        boolean rta = false;
        if (SysmanFunciones.validarVariableVacio(centroCostoInicial) ||
            SysmanFunciones.validarVariableVacio(centroCostoFinal)) {
            rta = true;
        }
        return rta;
    }

    public void generarInforme(ReportesBean.FORMATOS formato) {
        archivoDescarga = null;
        if (validarCampos()) {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB197"));
            return;
        }
        String condicion = "";

        if (conCentros) {
            if (validarCamposCentro()) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB198"));
                return;
            }
            condicion = condicion
                + " AND NVL(PLAN_PRESUPUESTAL.CENTRO_COSTO,PCK_DATOS.FC_CONS_CENTRO) BETWEEN '"
                + centroCostoInicial + "' AND '" + centroCostoFinal
                + "' \r\n";
        }
        if (conFuentes) {
            if (SysmanFunciones.validarVariableVacio(fuenteInicial) ||
                SysmanFunciones.validarVariableVacio(fuenteFinal)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB199"));
                return;
            }
            condicion = condicion
                + " AND NVL(PLAN_PRESUPUESTAL.AUXILIAR,PCK_DATOS.FC_CONS_AUXILIAR) BETWEEN '"
                + fuenteInicial + "' AND '" + fuenteFinal + "' \r\n";
        }
        aplicarReemplazosYParametros(condicion, formato);

    }

    public void aplicarReemplazosYParametros(String condicion,
        FORMATOS formato) {

        String conSeccion;
        String seccion;
        String conUnidad;
        String unidad;
        String nivelcta = nivel;

        try {
            String parametroSeccion = SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "SECCION EN INFORMES RESOLUCION 036",
                                            String.valueOf(SessionUtil
                                                            .getModulo()),
                                            new Date(), true),
                                            "NO")
                            .toString();

            String parametroCargo1 = SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO1 EN RESOLUCION 036",
                                            String.valueOf(SessionUtil
                                                            .getModulo()),
                                            new Date(), true),
                                            " ")
                            .toString();
            String parametroCargo2 = SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO2 EN RESOLUCION 036",
                                            String.valueOf(SessionUtil
                                                            .getModulo()),
                                            new Date(), true), " ")
                            .toString();
            String parametroCargo3 = SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "CARGO3 EN RESOLUCION 036",
                                            String.valueOf(SessionUtil
                                                            .getModulo()),
                                            new Date(), true), " ")
                            .toString();

            String parametroFirma1 = SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "FIRMA1 EN RESOLUCION 036",
                                            String.valueOf(SessionUtil
                                                            .getModulo()),
                                            new Date(), true), " ")
                            .toString();

            String parametroFirma2 = SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "FIRMA2 EN RESOLUCION 036",
                                            String.valueOf(SessionUtil
                                                            .getModulo()),
                                            new Date(), true), " ")
                            .toString();

            String parametroFirma3 = SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "FIRMA3 EN RESOLUCION 036",
                                            String.valueOf(SessionUtil
                                                            .getModulo()),
                                            new Date(), true),
                                            " ")
                            .toString();

            String parametroCedula1 = SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "CEDULA1 EN RESOLUCION 036",
                                            String.valueOf(SessionUtil
                                                            .getModulo()),
                                            new Date(), true), " ")
                            .toString();

            String parametroCedula2 = SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "CEDULA2 EN RESOLUCION 036",
                                            String.valueOf(SessionUtil
                                                            .getModulo()),
                                            new Date(), true), " ")
                            .toString();

            String parametroCedula3 = SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "CEDULA3 EN RESOLUCION 036",
                                            String.valueOf(SessionUtil
                                                            .getModulo()),
                                            new Date(), true), " ")
                            .toString();

            if ("SI".equalsIgnoreCase(parametroSeccion)) {
                conSeccion = SysmanFunciones
                                .nvl(ejbSysmanUtil.consultarParametro(compania,
                                                "SECCION 036",
                                                String.valueOf(SessionUtil
                                                                .getModulo()),
                                                new Date(), true), " ")
                                .toString();

                if (!conSeccion.isEmpty()) {
                    seccion = idioma.getString("TG_SECCION");
                }
                else {
                    seccion = "";
                }
                conUnidad = SysmanFunciones
                                .nvl(ejbSysmanUtil.consultarParametro(compania,
                                                "UNIDAD EJECUTORA 036",
                                                String.valueOf(SessionUtil
                                                                .getModulo()),
                                                new Date(), true), " ")
                                .toString();
                if (!conUnidad.isEmpty()) {
                    unidad = idioma.getString("TG_UNIDAD_EJECUTORA");
                }
                else {
                    unidad = "";
                }
            }
            else {
                conSeccion = "";
                seccion = "";
                conUnidad = "";
                unidad = "";
            }

            HashMap<String, Object> reemplazos = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            // Reemplazos valores consulta reporte
            reemplazos.put("enMiles", enMiles ? "-1" : "0");
            reemplazos.put("mes", mes);
            reemplazos.put("anio", anio);
            reemplazos.put("cuentaInicial", cuentaInicial);
            reemplazos.put("cuentaFinal", cuentaFinal);
            reemplazos.put("nivel", nivelcta);
            reemplazos.put("condicion", condicion);

            // Reemplazo par�metros reporte
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_MES",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mes)]);
            parametros.put("PR_CONSECCION", conSeccion);
            parametros.put("PR_CONUNIDAD", conUnidad);
            parametros.put("PR_SECCION", seccion);
            parametros.put("PR_UNIDAD", unidad);
            parametros.put("PR_CARGO1_EN_RESOLUCION_036", parametroCargo1);
            parametros.put("PR_CARGO2_EN_RESOLUCION_036", parametroCargo2);
            parametros.put("PR_CARGO3_EN_RESOLUCION_036", parametroCargo3);
            parametros.put("PR_FIRMA1_EN_RESOLUCION_036", parametroFirma1);
            parametros.put("PR_FIRMA2_EN_RESOLUCION_036", parametroFirma2);
            parametros.put("PR_FIRMA3_EN_RESOLUCION_036", parametroFirma3);
            parametros.put("PR_CEDULA1_EN_RESOLUCION_036", parametroCedula1);
            parametros.put("PR_CEDULA2_EN_RESOLUCION_036", parametroCedula2);
            parametros.put("PR_CEDULA3_EN_RESOLUCION_036", parametroCedula3);
            parametros.put("PR_ANO", anio);
            String reporte = "000974FCREGISTROEJECUCINGRESOS036";
            if (indicador) {
                Reporteador.resuelveConsulta(
                                reporte,
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazos,
                                parametros);

                archivoDescarga = JsfUtil.exportarStreamed(
                                reporte, parametros,
                                ConectorPool.ESQUEMA_SYSMAN,
                                formato);
            }
            else {
                Reporteador.resuelveConsulta(
                                reporte,
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazos, parametros);

                archivoDescarga = JsfUtil.exportarStreamed(
                                "000975FCREGISTROEJECUCINGRESOS036SO",
                                parametros, ConectorPool.ESQUEMA_SYSMAN,
                                formato);
            }

        }

        catch (JRException | IOException
                        | SystemException | SysmanException ex) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA")
                                + ex.getMessage());
            Logger.getLogger(
                            FcregistroejecucingresosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarAno() {
        mes = "";
        cuentaInicial = "";
        nomCuentaInicial = "";
        cuentaFinal = "";
        nomCuentaFinal = "";
        listaCuentaFinal = null;
        cargarListaMes();
        cargarListaCuentaInicial();
        cargarListaCentroCostoInicial();
        cargarListaFuenteInicial();
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarConCentros() {
        cargarListaCentroCostoInicial();
    }

    public void cambiarConFuentes() {
        cargarListaFuenteInicial();
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCentroCostoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroCostoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(CODIGO), "")
                        .toString();
        nomCentroInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(NOMBRE), "")
                        .toString();
        listaCentroCostoFinal = null;
        nomCentroFinal = "";
        centroCostoFinal = "";
        cargarListaCentroCostoFinal();
    }

    public void seleccionarFilaCentroCostoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroCostoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(CODIGO), "")
                        .toString();
        nomCentroFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(NOMBRE), "")
                        .toString();
    }

    public void seleccionarFilaFuenteInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(CODIGO), "")
                        .toString();
        nomFuenteInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(NOMBRE), "")
                        .toString();
        listaFuenteFinal = null;
        nomFuenteFinal = "";
        fuenteFinal = "";
        cargarListaFuenteFinal();
    }

    public void seleccionarFilaFuenteFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fuenteFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(CODIGO), "")
                        .toString();
        nomFuenteFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(NOMBRE), "")
                        .toString();
    }

    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(CODIGO), "")
                        .toString();
        nomCuentaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(NOMBRE), "")
                        .toString();
        nomCuentaFinal = "";
        cuentaFinal = "";
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(CODIGO), "")
                        .toString();
        nomCuentaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(NOMBRE), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>

    public String getCentroCostoInicial() {
        return centroCostoInicial;
    }

    public boolean isConCentros() {
        return conCentros;
    }

    public void setConCentros(boolean conCentros) {
        this.conCentros = conCentros;
    }

    public boolean isConFuentes() {
        return conFuentes;
    }

    public void setConFuentes(boolean conFuentes) {
        this.conFuentes = conFuentes;
    }

    public boolean isIndicador() {
        return indicador;
    }

    public void setIndicador(boolean indicador) {
        this.indicador = indicador;
    }

    public boolean isEnMiles() {
        return enMiles;
    }

    public void setEnMiles(boolean enMiles) {
        this.enMiles = enMiles;
    }

    public void setCentroCostoInicial(String centroCostoInicial) {
        this.centroCostoInicial = centroCostoInicial;
    }

    public String getCentroCostoFinal() {
        return centroCostoFinal;
    }

    public void setCentroCostoFinal(String centroCostoFinal) {
        this.centroCostoFinal = centroCostoFinal;
    }

    public String getFuenteInicial() {
        return fuenteInicial;
    }

    public void setFuenteInicial(String fuenteInicial) {
        this.fuenteInicial = fuenteInicial;
    }

    public String getFuenteFinal() {
        return fuenteFinal;
    }

    public void setFuenteFinal(String fuenteFinal) {
        this.fuenteFinal = fuenteFinal;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public String getCuentaInicial() {
        return cuentaInicial;
    }

    public void setCuentaInicial(String cuentaInicial) {
        this.cuentaInicial = cuentaInicial;
    }

    public String getCuentaFinal() {
        return cuentaFinal;
    }

    public void setCuentaFinal(String cuentaFinal) {
        this.cuentaFinal = cuentaFinal;
    }

    public String getNomCentroInicial() {
        return nomCentroInicial;
    }

    public void setNomCentroInicial(String nomCentroInicial) {
        this.nomCentroInicial = nomCentroInicial;
    }

    public String getNomCentroFinal() {
        return nomCentroFinal;
    }

    public void setNomCentroFinal(String nomCentroFinal) {
        this.nomCentroFinal = nomCentroFinal;
    }

    public String getNomFuenteInicial() {
        return nomFuenteInicial;
    }

    public void setNomFuenteInicial(String nomFuenteInicial) {
        this.nomFuenteInicial = nomFuenteInicial;
    }

    public String getNomFuenteFinal() {
        return nomFuenteFinal;
    }

    public void setNomFuenteFinal(String nomFuenteFinal) {
        this.nomFuenteFinal = nomFuenteFinal;
    }

    public String getNomCuentaInicial() {
        return nomCuentaInicial;
    }

    public void setNomCuentaInicial(String nomCuentaInicial) {
        this.nomCuentaInicial = nomCuentaInicial;
    }

    public String getNomCuentaFinal() {
        return nomCuentaFinal;
    }

    public void setNomCuentaFinal(String nomCuentaFinal) {
        this.nomCuentaFinal = nomCuentaFinal;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public List<Registro> getListaMes() {
        return listaMes;
    }

    public void setListaMes(List<Registro> listaMes) {
        this.listaMes = listaMes;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCentroCostoInicial() {
        return listaCentroCostoInicial;
    }

    public void setListaCentroCostoInicial(
        RegistroDataModelImpl listaCentroCostoInicial) {
        this.listaCentroCostoInicial = listaCentroCostoInicial;
    }

    public RegistroDataModelImpl getListaCentroCostoFinal() {
        return listaCentroCostoFinal;
    }

    public void setListaCentroCostoFinal(
        RegistroDataModelImpl listaCentroCostoFinal) {
        this.listaCentroCostoFinal = listaCentroCostoFinal;
    }

    public RegistroDataModelImpl getListaFuenteInicial() {
        return listaFuenteInicial;
    }

    public void setListaFuenteInicial(
        RegistroDataModelImpl listaFuenteInicial) {
        this.listaFuenteInicial = listaFuenteInicial;
    }

    public RegistroDataModelImpl getListaFuenteFinal() {
        return listaFuenteFinal;
    }

    public void setListaFuenteFinal(RegistroDataModelImpl listaFuenteFinal) {
        this.listaFuenteFinal = listaFuenteFinal;
    }

    public RegistroDataModelImpl getListaCuentaInicial() {
        return listaCuentaInicial;
    }

    public void setListaCuentaInicial(
        RegistroDataModelImpl listaCuentaInicial) {
        this.listaCuentaInicial = listaCuentaInicial;
    }

    public RegistroDataModelImpl getListaCuentaFinal() {
        return listaCuentaFinal;
    }

    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
        this.listaCuentaFinal = listaCuentaFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}

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
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.LibroRegistroVigFuturasControladorEnum;
import com.sysman.presupuesto.enums.LibroRegistroVigFuturasControladorUrlEnum;
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
 * @author jrodriguezr
 * @version 1, 30/06/2016
 * 
 * @version 2, 19/04/2017, pespitia:<br>
 * Se aplico Refactoring.
 * 
 * @author eamaya
 * @version 3.0, 13/06/2017 Se cambi� el llamado del c�digo del
 * formulario y actualizaci�n de ConnectorPool
 * 
 */
@ManagedBean
@ViewScoped
public class LibroRegistroVigFuturasControlador extends BeanBaseModal {
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private boolean indicador;
    private boolean centroCosto;
    private boolean fuenteRecursos;
    private String cuentaInicial;
    private String cuentaFinal;
    private String mesInicial;
    private String mesFinal;
    private String centroInicial;
    private String centroFinal;
    private String fuenteInicial;
    private String fuenteFinal;
    private String anio;
    private String nmes1;
    private String nmes2;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaMes1;
    private List<Registro> listames2;
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    private RegistroDataModelImpl listacentrocostoInicial;
    private RegistroDataModelImpl listacentrocostoFinal;
    private RegistroDataModelImpl listaFuenteInicial;
    private RegistroDataModelImpl listaFuenteFinal;

    private static final String CODIGO = "CODIGO";

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of LibroRegistroVigFuturasControlador
     */
    public LibroRegistroVigFuturasControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.LIBRO_REGISTRO_VIG_FUTURAS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(LibroRegistroVigFuturasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaAno();
        anio = String.valueOf(SysmanFunciones.ano(new Date()));
        cargarListaMes1();
        mesInicial = String.valueOf(SysmanFunciones.mes(new Date()));
        cargarListames2();
        mesFinal = String.valueOf(SysmanFunciones.mes(new Date()) + 1);
        nmes1 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                        .parseInt(mesInicial)];
        nmes2 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                        .parseInt(mesInicial)
            + 1];
        indicador = true;
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaInicial();
        cargarListacentrocostoInicial();
        cargarListaFuenteInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaMes1() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        try {
            listaMes1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LibroRegistroVigFuturasControladorUrlEnum.URL4590
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListames2() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(LibroRegistroVigFuturasControladorEnum.MES.getValue(),
                        mesInicial);

        try {
            listames2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LibroRegistroVigFuturasControladorUrlEnum.URL5063
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LibroRegistroVigFuturasControladorUrlEnum.URL5634
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LibroRegistroVigFuturasControladorUrlEnum.URL6002
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
                                        LibroRegistroVigFuturasControladorUrlEnum.URL6901
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(LibroRegistroVigFuturasControladorEnum.CUENTAINICIAL
                        .getValue(), cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO);
    }

    public void cargarListacentrocostoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LibroRegistroVigFuturasControladorUrlEnum.URL7967
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listacentrocostoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO);
    }

    public void cargarListacentrocostoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LibroRegistroVigFuturasControladorUrlEnum.URL8674
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.CENTRO_COSTO.getName(), centroInicial);

        listacentrocostoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO);
    }

    public void cargarListaFuenteInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LibroRegistroVigFuturasControladorUrlEnum.URL9424
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
                                        LibroRegistroVigFuturasControladorUrlEnum.URL10068
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(LibroRegistroVigFuturasControladorEnum.CODIGOINICIAL
                        .getValue(), fuenteInicial);

        listaFuenteFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public boolean validarCentros(boolean centro) {
        return centroCosto
            && centro;
    }

    public boolean validarFuentes(boolean fuente) {
        return fuenteRecursos
            && fuente;
    }

    public boolean validarCampos() {
        boolean valida = false;
        if (validarCentros(
                        SysmanFunciones.validarVariableVacio(centroInicial))) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB661"));
            valida = true;
        }
        if (validarCentros(SysmanFunciones.validarVariableVacio(centroFinal))) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB662"));
            valida = true;
        }
        if (SysmanFunciones.validarVariableVacio(cuentaInicial)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB663"));
            valida = true;
        }
        if (SysmanFunciones.validarVariableVacio(cuentaFinal)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB664"));
            valida = true;
        }
        if (validarFuentes(
                        SysmanFunciones.validarVariableVacio(fuenteInicial))) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB665"));
            valida = true;
        }
        if (validarFuentes(SysmanFunciones.validarVariableVacio(fuenteFinal))) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB666"));
            valida = true;
        }
        if (SysmanFunciones.validarVariableVacio(mesInicial)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB667"));
            valida = true;
        }
        if (SysmanFunciones.validarVariableVacio(mesFinal)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB668"));
            valida = true;
        }
        if (SysmanFunciones.validarVariableVacio(anio)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB669"));
            valida = true;
        }
        return valida;
    }

    private void generaReporte(FORMATOS formato) {

        if (validarCampos()) {
            return;
        }

        HashMap<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();
        String reporte = "000959FCREGISTROVIGENCIASFUT036";
        reemplazar.put("cuentaInicial", cuentaInicial);
        reemplazar.put("cuentaFinal", cuentaFinal);
        reemplazar.put("centroInicial", centroInicial);
        reemplazar.put("centroFinal", centroFinal);
        reemplazar.put("fuenteInicial", fuenteInicial);
        reemplazar.put("fuenteFinal", fuenteFinal);
        reemplazar.put("mesInicial", mesInicial);
        reemplazar.put("mesFinal", mesFinal);
        reemplazar.put("anio", anio);
        reemplazar.put("centroCostoCond",
                        centroCosto
                            ? "  AND PLAN_PRESUPUESTAL.CENTRO_COSTO BETWEEN '"
                                + centroInicial + "'"
                                + " AND '" + centroFinal + "'"
                            : "");
        reemplazar.put("fuenteRecursoCond",
                        fuenteRecursos
                            ? " AND PLAN_PRESUPUESTAL.AUXILIAR BETWEEN '"
                                + fuenteInicial + "'" +
                                "       AND '" + fuenteFinal + "'"
                            : "");

        Reporteador.resuelveConsulta(reporte,
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar, parametros);
        enviarParametrosYReemplazar(parametros, reporte, formato);

    }

    private void enviarParametrosYReemplazar(Map<String, Object> parametros,
        String reporte, FORMATOS formato) {
        try {
            String unidad = "";
            String conUnidad = "";
            String seccionInfRes036;
            String conSeccion = "";
            String seccion = "";
            String conRegional = "";
            String regional = "";
            String nivel1i;
            String nivel1f;
            String nivel2i;
            String nivel2f;
            String nivel3i;
            String nivel3f;
            String nivel4i;
            String nivel4f;
            String nivel5i;
            String nivel5f;
            String nivel6i;
            String nivel6f;

            seccionInfRes036 = ejbSysmanUtil.consultarParametro(compania,
                            "SECCION EN INFORMES RESOLUCION 036",
                            SessionUtil.getModulo(),
                            new Date(), true);

            if ("SI".equals(seccionInfRes036 == null ? "NO"
                : seccionInfRes036)) {
                conSeccion = ejbSysmanUtil.consultarParametro(compania,
                                "SECCION 036",
                                SessionUtil.getModulo(),
                                new Date(), true);

                if (!(conSeccion == null ? "" : conSeccion).isEmpty()) {
                    seccion = "SECCION";
                }
                else {
                    seccion = "";
                }
                conUnidad = ejbSysmanUtil.consultarParametro(compania,
                                "UNIDAD EJECUTORA 036",
                                SessionUtil.getModulo(),
                                new Date(), true);

                if (!(conUnidad == null ? "" : conUnidad).isEmpty()) {
                    unidad = "UNIDAD EJECUTORA";
                }
                else {
                    unidad = "";
                }
                conRegional = ejbSysmanUtil.consultarParametro(compania,
                                "REGIONAL 036",
                                SessionUtil.getModulo(),
                                new Date(), true);

                if (!(conRegional == null ? "" : conRegional).isEmpty()) {
                    regional = idioma.getString("TG_REGIONAL");
                }
                else {
                    regional = "";
                }
            }
            nivel1i = ejbSysmanUtil.consultarParametro(compania,
                            "NIVEL 1I",
                            SessionUtil.getModulo(),
                            new Date(), true);

            nivel1f = ejbSysmanUtil.consultarParametro(compania,
                            "NIVEL 1F",
                            SessionUtil.getModulo(),
                            new Date(), true);

            nivel2i = ejbSysmanUtil.consultarParametro(compania,
                            "NIVEL 2I",
                            SessionUtil.getModulo(),
                            new Date(), true);

            nivel2f = ejbSysmanUtil.consultarParametro(compania,
                            "NIVEL 2F",
                            SessionUtil.getModulo(),
                            new Date(), true);

            nivel3i = ejbSysmanUtil.consultarParametro(compania,
                            "NIVEL 3I",
                            SessionUtil.getModulo(),
                            new Date(), true);

            nivel3f = ejbSysmanUtil.consultarParametro(compania,
                            "NIVEL 3F",
                            SessionUtil.getModulo(),
                            new Date(), true);

            nivel4i = ejbSysmanUtil.consultarParametro(compania,
                            "NIVEL 4I",
                            SessionUtil.getModulo(),
                            new Date(), true);

            nivel4f = ejbSysmanUtil.consultarParametro(compania,
                            "NIVEL 4F",
                            SessionUtil.getModulo(),
                            new Date(), true);

            nivel5i = ejbSysmanUtil.consultarParametro(compania,
                            "NIVEL 5I",
                            SessionUtil.getModulo(),
                            new Date(), true);

            nivel5f = ejbSysmanUtil.consultarParametro(compania,
                            "NIVEL 5F",
                            SessionUtil.getModulo(),
                            new Date(), true);

            nivel6i = ejbSysmanUtil.consultarParametro(compania,
                            "NIVEL 6I",
                            SessionUtil.getModulo(),
                            new Date(), true);

            nivel6f = ejbSysmanUtil.consultarParametro(compania,
                            "NIVEL 6F",
                            SessionUtil.getModulo(),
                            new Date(), true);

            parametros.put("PR_INDICADOR", indicador);
            parametros.put("PR_NIVEL_1I", nivel1i);
            parametros.put("PR_NIVEL_1F", nivel1f);
            parametros.put("PR_NIVEL_2I", nivel2i);
            parametros.put("PR_NIVEL_2F", nivel2f);
            parametros.put("PR_NIVEL_3I", nivel3i);
            parametros.put("PR_NIVEL_3F", nivel3f);
            parametros.put("PR_NIVEL_4I", nivel4i);
            parametros.put("PR_NIVEL_4F", nivel4f);
            parametros.put("PR_NIVEL_5I", nivel5i);
            parametros.put("PR_NIVEL_5F", nivel5f);
            parametros.put("PR_NIVEL_6I", nivel6i);
            parametros.put("PR_NIVEL_6F", nivel6f);

            parametros.put("PR_MES1", mesInicial);
            parametros.put("PR_MES2", mesFinal);
            parametros.put("PR_REGIONAL", regional);
            parametros.put("PR_CONREGIONAL", conRegional);
            parametros.put("PR_UNIDAD", unidad);
            parametros.put("PR_CONUNIDAD", conUnidad);
            parametros.put("PR_SECCION", seccion);
            parametros.put("PR_CONSECCION", conSeccion);
            parametros.put("PR_ANO", anio);
            parametros.put("PR_NMES2", nmes2);
            parametros.put("PR_NMES1", nmes1);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (SystemException | OutOfMemoryError | JRException
                        | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarMes1() {
        // <CODIGO_DESARROLLADO>
        mesFinal = nmes2 = "";

        if (mesInicial != null) {
            nmes1 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                            .parseInt(mesInicial)];

            cargarListames2();
        }
        else {
            nmes1 = "";
            listames2 = null;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarmes2() {
        // <CODIGO_DESARROLLADO>
        nmes2 = mesFinal == null ? ""
            : SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                            .parseInt(mesFinal)];
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        if (anio != null) {
            mesFinal = mesInicial = "1";
            nmes2 = nmes1 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                            .parseInt(mesInicial)];

            cuentaInicial = cuentaFinal = centroInicial = centroFinal = fuenteInicial = fuenteFinal = "";

            cargarListaMes1();
            cargarListames2();
            cargarListaCuentaInicial();
        }
        else {
            nmes1 = nmes2 = cuentaInicial = cuentaFinal = centroInicial = centroFinal = fuenteInicial = fuenteFinal = "";
            listaMes1 = listames2 = null;
            listaCuentaInicial = listaCuentaFinal = listacentrocostoInicial = listacentrocostoFinal = listaFuenteInicial = listaFuenteFinal = null;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarVerificacion97() {
        // <CODIGO_DESARROLLADO>
        cargarListacentrocostoInicial();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarVerificacion106() {
        // <CODIGO_DESARROLLADO>
        cargarListaFuenteInicial();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        cuentaInicial = asignarValorCampo((Registro) event.getObject(), CODIGO);
        cuentaFinal = null;

        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        cuentaFinal = asignarValorCampo((Registro) event.getObject(), CODIGO);
    }

    public void seleccionarFilacentrocostoInicial(SelectEvent event) {
        centroInicial = asignarValorCampo((Registro) event.getObject(), CODIGO);
        centroFinal = null;

        cargarListacentrocostoFinal();
    }

    public void seleccionarFilacentrocostoFinal(SelectEvent event) {
        centroFinal = asignarValorCampo((Registro) event.getObject(), CODIGO);
    }

    public void seleccionarFilaFuenteInicial(SelectEvent event) {
        fuenteInicial = asignarValorCampo((Registro) event.getObject(), CODIGO);
        fuenteFinal = null;

        cargarListaFuenteFinal();
    }

    public void seleccionarFilaFuenteFinal(SelectEvent event) {
        fuenteFinal = asignarValorCampo((Registro) event.getObject(), CODIGO);
    }

    // </METODOS_COMBOS_GRANDES>

    /**
     * Verifica que el registro <code>reg</code> tenga una coleccion
     * de campos que no sea nula.
     * 
     * @param reg
     * @param campo
     * El campo a evaluar en la coleccion.
     * @return El valor del campo segun la coleccion.
     */
    private String asignarValorCampo(Registro reg, String campo) {
        return reg.getCampos().isEmpty() ? ""
            : SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
                : reg.getCampos().get(campo).toString();
    }

    // <SET_GET_ATRIBUTOS>
    public boolean getIndicador() {
        return indicador;
    }

    public void setIndicador(boolean indicador) {
        this.indicador = indicador;
    }

    public boolean getCentroCosto() {
        return centroCosto;
    }

    public void setCentroCosto(boolean centroCosto) {
        this.centroCosto = centroCosto;
    }

    public boolean getFuenteRecursos() {
        return fuenteRecursos;
    }

    public void setFuenteRecursos(boolean fuenteRecursos) {
        this.fuenteRecursos = fuenteRecursos;
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

    public String getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(String mesInicial) {
        this.mesInicial = mesInicial;
    }

    public String getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(String mesFinal) {
        this.mesFinal = mesFinal;
    }

    public String getCentroInicial() {
        return centroInicial;
    }

    public void setCentroInicial(String centroInicial) {
        this.centroInicial = centroInicial;
    }

    public String getCentroFinal() {
        return centroFinal;
    }

    public void setCentroFinal(String centroFinal) {
        this.centroFinal = centroFinal;
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

    public String getNmes1() {
        return nmes1;
    }

    public void setNmes1(String nmes1) {
        this.nmes1 = nmes1;
    }

    public String getNmes2() {
        return nmes2;
    }

    public void setNmes2(String nmes2) {
        this.nmes2 = nmes2;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaMes1() {
        return listaMes1;
    }

    public void setListaMes1(List<Registro> listaMes1) {
        this.listaMes1 = listaMes1;
    }

    public List<Registro> getListames2() {
        return listames2;
    }

    public void setListames2(List<Registro> listames2) {
        this.listames2 = listames2;
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
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

    public RegistroDataModelImpl getListacentrocostoInicial() {
        return listacentrocostoInicial;
    }

    public void setListacentrocostoInicial(
        RegistroDataModelImpl listacentrocostoInicial) {
        this.listacentrocostoInicial = listacentrocostoInicial;
    }

    public RegistroDataModelImpl getListacentrocostoFinal() {
        return listacentrocostoFinal;
    }

    public void setListacentrocostoFinal(
        RegistroDataModelImpl listacentrocostoFinal) {
        this.listacentrocostoFinal = listacentrocostoFinal;
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
    // </SET_GET_LISTAS_COMBO_GRANDE>
}

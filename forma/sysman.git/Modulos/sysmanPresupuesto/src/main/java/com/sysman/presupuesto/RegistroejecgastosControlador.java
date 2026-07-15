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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.RegistroejecgastosControladorEnum;
import com.sysman.presupuesto.enums.RegistroejecgastosControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Calendar;
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
 * @author apineda
 * @version 1, 19/07/2016
 * 
 * @version 2, 20/04/2017, pespitia :<br>
 * Se aplicaron las recomendaciones de SonarLint.<br>
 * Refactoring. Manejo de EJBs.
 */
@ManagedBean
@ViewScoped
public class RegistroejecgastosControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;

    // <DECLARAR_ATRIBUTOS>
    private Boolean indicador;
    private Boolean enMiles;
    private Boolean especial;
    private Boolean nivel;
    private String cuentaInicial;
    private String cuentaFinal;
    private int anio;
    private int mes;
    private String nivelCuenta;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAno;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbParametro;

    /**
     * Creates a new instance of RegistroejecgastosControlador
     */
    public RegistroejecgastosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        anio = SysmanFunciones.getParteFecha(
                        new Date(),
                        Calendar.YEAR);
        mes = SysmanFunciones.getParteFecha(
                        new Date(),
                        Calendar.MONTH)
            + 1;
        cuentaInicial = "0";
        cuentaFinal = "9999999999999999";
        nivelCuenta = "6";

        try {
            numFormulario = GeneralCodigoFormaEnum.REGISTROEJECGASTOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(RegistroejecgastosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaAno();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
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
                                                            RegistroejecgastosControladorUrlEnum.URL4197
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
                                        RegistroejecgastosControladorUrlEnum.URL4576
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.NATURALEZA.getName(), "D");

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RegistroejecgastosControladorUrlEnum.URL5256
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.NATURALEZA.getName(), "D");
        param.put(RegistroejecgastosControladorEnum.CUENTAINICIAL.getValue(),
                        cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirImprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        generaInforme(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirImprimirFormatoPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        generaInforme(ReportesBean.FORMATOS.PDF);

        // </CODIGO_DESARROLLADO>
    }

    public void generaInforme(ReportesBean.FORMATOS formato) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();

            String informe = "001034REGISTROEJECUCGASTOS036SO";
            String conSeccion = "";
            String labelSeccion = "";
            String conUnidad = "";
            String labelUnidad = "";
            String aplicarMiles = "";
            String dividir = "";

            String aplicaSeccion = ejbParametro.consultarParametro(compania,
                            "SECCION EN INFORMES RESOLUCION 036", modulo,
                            new Date(), false);

            if ("SI".equals(aplicaSeccion)) {
                conSeccion = ejbParametro.consultarParametro(compania,
                                "SECCION 036", modulo, new Date(), false);

                if (!conSeccion.isEmpty()) {
                    labelSeccion = "SECCION";
                }

                conUnidad = ejbParametro.consultarParametro(compania,
                                "UNIDAD EJECUTORA 036", modulo, new Date(),
                                false);

                labelUnidad = !conUnidad.isEmpty() ? "UNIDAD EJECUTORA"
                    : labelUnidad;

            }

            if (enMiles) {
                aplicarMiles = "ROUND(";
                dividir = ",-3)/1000";
            }

            if (indicador) {
                informe = "001029REGISTROEJECUCGASTOS036";
            }
            else if (especial) {
                informe = "001033RegistroEjecucGastos036Dis";
            }
            else if (nivel) {
                informe = "001036RegistroEjecucGastosCO036";
            }

            reemplazar.put("mes", Integer.toString(mes));
            reemplazar.put("anio", Integer.toString(anio));
            reemplazar.put("cuentaInicial", "'" + cuentaInicial + "'");
            reemplazar.put("cuentaFinal", "'" + cuentaFinal + "'");
            reemplazar.put("aplicarMiles", aplicarMiles);
            reemplazar.put("dividir", dividir);
            reemplazar.put("nivelCuenta", nivelCuenta);

            String cargo1Resol = ejbParametro.consultarParametro(compania,
                            "CARGO1 EN RESOLUCION 036", modulo, new Date(),
                            false);

            String firma1Resol = ejbParametro.consultarParametro(compania,
                            "FIRMA1 EN RESOLUCION 036", modulo, new Date(),
                            false);

            String cedula1Resol = ejbParametro.consultarParametro(compania,
                            "CEDULA1 EN RESOLUCION 036", modulo, new Date(),
                            false);

            String cargo2Resol = ejbParametro.consultarParametro(compania,
                            "CARGO2 EN RESOLUCION 036", modulo, new Date(),
                            false);

            String firma2Resol = ejbParametro.consultarParametro(compania,
                            "FIRMA2 EN RESOLUCION 036", modulo, new Date(),
                            false);

            String cedula2Resol = ejbParametro.consultarParametro(compania,
                            "CEDULA2 EN RESOLUCION 036", modulo, new Date(),
                            false);

            parametros.put("PR_CARGO1_EN_RESOLUCION_036", cargo1Resol);
            parametros.put("PR_CARGO2_EN_RESOLUCION_036", cargo2Resol);
            parametros.put("PR_FIRMA1_EN_RESOLUCION_036", firma1Resol);
            parametros.put("PR_FIRMA2_EN_RESOLUCION_036", firma2Resol);
            parametros.put("PR_CEDULA1_EN_RESOLUCION_036", cedula1Resol);
            parametros.put("PR_CEDULA2_EN_RESOLUCION_036", cedula2Resol);
            parametros.put("PR_MES",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mes]
                                            .toUpperCase());
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_CONSECCION", conSeccion);
            parametros.put("PR_CONUNIDAD", conUnidad);
            parametros.put("PR_LBLSECCION_CAPTION", labelSeccion);
            parametros.put("PR_LBLUNIDAD_CAPTION", labelUnidad);
            parametros.put("PR_ANO", anio);

            Reporteador.resuelveConsulta(informe, Integer.parseInt(modulo),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(informe, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA"), " ",
                            e.getMessage()));
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        cuentaInicial = "";
        cuentaFinal = "";

        listaCuentaFinal = null;
        cargarListaCuentaInicial();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarNivel() {
        indicador = false;
        especial = false;
    }

    public void cambiarespecial() {
        indicador = false;
        nivel = false;
    }

    public void cambiarIndicador() {
        nivel = false;
        especial = false;
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("ID"), "").toString();

        cuentaFinal = "";
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("ID"), "").toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>

    public String getCuentaInicial() {
        return cuentaInicial;
    }

    public Boolean getIndicador() {
        return indicador;
    }

    public void setIndicador(Boolean indicador) {
        this.indicador = indicador;
    }

    public Boolean getEnMiles() {
        return enMiles;
    }

    public void setEnMiles(Boolean enMiles) {
        this.enMiles = enMiles;
    }

    public Boolean getEspecial() {
        return especial;
    }

    public void setEspecial(Boolean especial) {
        this.especial = especial;
    }

    public Boolean getNivel() {
        return nivel;
    }

    public void setNivel(Boolean nivel) {
        this.nivel = nivel;
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

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public String getNivelCuenta() {
        return nivelCuenta;
    }

    public void setNivelCuenta(String nivelCuenta) {
        this.nivelCuenta = nivelCuenta;
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
    // </SET_GET_LISTAS_COMBO_GRANDE>
}

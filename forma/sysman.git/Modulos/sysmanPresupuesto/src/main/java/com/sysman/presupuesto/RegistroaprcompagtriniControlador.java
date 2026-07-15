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
import com.sysman.presupuesto.enums.RegistroaprcompagtriniControladorEnum;
import com.sysman.presupuesto.enums.RegistroaprcompagtriniControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
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
 * @author NGOMEZ
 * @version 1, 18/07/2016
 *
 *
 * @author ybecerra
 * @version 2, 19/04/2017 Revision Sonar y Refactoring
 */
@ManagedBean
@ViewScoped

public class RegistroaprcompagtriniControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;

    // <DECLARAR_ATRIBUTOS>
    private String interno;
    private String conSaldos;
    private String cuentaInicial;
    private String cuentaFinal;
    private String mesInicial;
    private String mesFinal;
    private String anio;
    private String nivel;
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
    private static final String CODIGO = GeneralParameterEnum.CODIGO.getName();
    private static final String TRUE = "true";
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    @EJB
    private EjbSysmanUtilRemote ejbPresupuestoCero;

    /**
     * Creates a new instance of RegistroaprcompagtriniControlador
     */
    public RegistroaprcompagtriniControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.REGISTROAPRCOMPAGTRINI_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            interno = TRUE;
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(RegistroaprcompagtriniControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaAno();
        anio = String.valueOf(SysmanFunciones.getParteFecha(
                        new Date(),
                        Calendar.YEAR));
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
                                                            RegistroaprcompagtriniControladorUrlEnum.URL3658
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
                                        RegistroaprcompagtriniControladorUrlEnum.URL3939
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO);
    }

    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RegistroaprcompagtriniControladorUrlEnum.URL4704
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(RegistroaprcompagtriniControladorEnum.PARAM1.getValue(),
                        cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, CODIGO);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        genInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        genInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void genInforme(ReportesBean.FORMATOS formato) {
        try {
            if (Integer.parseInt(mesInicial) > Integer.parseInt(mesFinal)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB880"));
                return;
            }

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("anio", anio);
            reemplazar.put("mesInicial", mesInicial);
            reemplazar.put("mesFinal", mesFinal);
            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);
            reemplazar.put("nivel", nivel);
            reemplazar.put("conSaldos", conSaldos.equals(TRUE) ? ""
                : "WHERE DETALLE IN (1)");

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_IDVISIBLE", interno.equals(TRUE));
            parametros.put("PR_NIVEL_1I",
                            SysmanFunciones.nvl(ejbPresupuestoCero
                                            .consultarParametro(compania,
                                                            "NIVEL 1I", modulo,
                                                            new Date(), true),
                                            ""));
            parametros.put("PR_NIVEL_1F",
                            SysmanFunciones.nvl(ejbPresupuestoCero
                                            .consultarParametro(compania,
                                                            "NIVEL 1F", modulo,
                                                            new Date(), true),
                                            ""));
            parametros.put("PR_NIVEL_2I",
                            SysmanFunciones.nvl(ejbPresupuestoCero
                                            .consultarParametro(compania,
                                                            "NIVEL 2I", modulo,
                                                            new Date(), true),
                                            ""));
            parametros.put("PR_NIVEL_2F",
                            SysmanFunciones.nvl(ejbPresupuestoCero
                                            .consultarParametro(compania,
                                                            "NIVEL 2F", modulo,
                                                            new Date(), true),
                                            ""));
            parametros.put("PR_NIVEL_3I",
                            SysmanFunciones.nvl(ejbPresupuestoCero
                                            .consultarParametro(compania,
                                                            "NIVEL 3I", modulo,
                                                            new Date(), true),
                                            ""));
            parametros.put("PR_NIVEL_3F",
                            SysmanFunciones.nvl(ejbPresupuestoCero
                                            .consultarParametro(compania,
                                                            "NIVEL 3F", modulo,
                                                            new Date(), true),
                                            ""));
            parametros.put("PR_NIVEL_4I",
                            SysmanFunciones.nvl(ejbPresupuestoCero
                                            .consultarParametro(compania,
                                                            "NIVEL 4I", modulo,
                                                            new Date(), true),
                                            ""));
            parametros.put("PR_NIVEL_4F",
                            SysmanFunciones.nvl(ejbPresupuestoCero
                                            .consultarParametro(compania,
                                                            "NIVEL 4F", modulo,
                                                            new Date(), true),
                                            ""));
            parametros.put("PR_NIVEL_5I",
                            SysmanFunciones.nvl(ejbPresupuestoCero
                                            .consultarParametro(compania,
                                                            "NIVEL 5I", modulo,
                                                            new Date(), true),
                                            ""));
            parametros.put("PR_NIVEL_5F",
                            SysmanFunciones.nvl(ejbPresupuestoCero
                                            .consultarParametro(compania,
                                                            "NIVEL 5F", modulo,
                                                            new Date(), true),
                                            ""));
            parametros.put("PR_NIVEL_6I",
                            SysmanFunciones.nvl(ejbPresupuestoCero
                                            .consultarParametro(compania,
                                                            "NIVEL 6I", modulo,
                                                            new Date(), true),
                                            ""));
            parametros.put("PR_NIVEL_6F",
                            SysmanFunciones.nvl(ejbPresupuestoCero
                                            .consultarParametro(compania,
                                                            "NIVEL 6F", modulo,
                                                            new Date(), true),
                                            ""));

            String reporte = "001008REGISTROAPRCOMPAGTRINI";
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        cuentaInicial = null;
        cuentaFinal = null;
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = registroAux.getCampos().get(CODIGO) == null ? ""
            : registroAux.getCampos().get(CODIGO).toString();
        cuentaFinal = null;
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = registroAux.getCampos().get(CODIGO) == null ? ""
            : registroAux.getCampos().get(CODIGO).toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getInterno() {
        return interno;
    }

    public void setInterno(String interno) {
        this.interno = interno;
    }

    public String getConSaldos() {
        return conSaldos;
    }

    public void setConSaldos(String conSaldos) {
        this.conSaldos = conSaldos;
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

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
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

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
import com.sysman.presupuesto.enums.CbejecucionraControladorEnum;
import com.sysman.presupuesto.enums.CbejecucionraControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
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
 * @author acaceres
 * @version 1, 11/07/2016
 * @version 2, 17/04/2017 jrodriguezr Se refactoriza el codigo SQL de
 * las listas para utilizar dss.
 * @version 3, 24/04/2017 jrodriguezr Se refactoriza el codigo
 * ajustando los llamados a funciones, procedimientos y metodos de la
 * clase Acciones a llamados a EJB.
 * 
 * @author ybecerra
 * @version 4, 13/06/2017 Implementacion al llamado de
 * GeneralCodigoFormaEnum, para el codigo del formulario
 */
@ManagedBean
@ViewScoped
public class CbejecucionraControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    private boolean codigoEquiv;
    private String cuentaInicial;
    private String cuentaFinal;
    private String nombreCuentaIni;
    private String nombreCuentaFin;
    private boolean etiqueNivelVisible;
    private boolean nivelVisible;
    private int ano;
    private int mes;
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
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of CbejecucionraControlador
     */
    public CbejecucionraControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        ano = SysmanFunciones
                        .ano(new Date());
        mes = SysmanFunciones
                        .mes(new Date());
        nivel = "6";
        try {
            numFormulario = GeneralCodigoFormaEnum.CBEJECUCIONRA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(CbejecucionraControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        etiqueNivelVisible = true;
        nivelVisible = true;
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

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaAno() {
        // 4001
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CbejecucionraControladorUrlEnum.URL3969
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMes() {
        // 7007
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        try {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CbejecucionraControladorUrlEnum.URL4512
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaCuentaInicial
     *
     */
    public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CbejecucionraControladorUrlEnum.URL5124
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    /**
     *
     * Carga la lista listaCuentaFinal
     *
     */
    public void cargarListaCuentaFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CbejecucionraControladorUrlEnum.URL6522
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(CbejecucionraControladorEnum.CUENTAINICIAL.getValue(),
                        cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    // </METODOS_CARGAR_LISTA>
    public void obtenerCbEjecucionRaid(FORMATOS formatos) {
        String reporte = "";
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("ano", ano);
            reemplazar.put("mes", mes);
            reemplazar.put("cuentaInicial", cuentaInicial);
            reemplazar.put("cuentaFinal", cuentaFinal);
            reemplazar.put("nivel", nivel);
            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE

            if (!validarParametros(parametros)
                || !validarParametros2(parametros)) {
                return;
            }

            if (codigoEquiv) {
                reporte = "001001CBEJECUCIONRAEQ";
                Reporteador.resuelveConsulta(reporte,
                                Integer.valueOf(modulo), reemplazar,
                                parametros);
                archivoDescarga = JsfUtil.exportarStreamed(
                                reporte, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formatos);
            }
            else {
                reporte = "001018CBEJECUCIONRAID";
                Reporteador.resuelveConsulta(reporte,
                                Integer.valueOf(modulo), reemplazar,
                                parametros);

                archivoDescarga = JsfUtil.exportarStreamed(
                                reporte, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formatos);

            }
        }
        catch (FileNotFoundException e) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_INFORME_VAR_NO_EXISTE")
                                            .replace("s$reporte$s", reporte)
                                + e.getMessage());
            logger.error(e.getMessage(), e);
        }

        catch (JRException | IOException | SysmanException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }
    // <METODOS_BOTONES>

    private boolean validarParametros(Map<String, Object> parametros) {
        String firma1InfoContraloria = null;
        String cargo1InfoControlario = null;
        String cedula1InfoContraloria = null;
        String firma2InfoContraloria = null;
        try {
            firma1InfoContraloria = ejbSysmanUtil.consultarParametro(compania,
                            "FIRMA1 EN INFORMES CONTRALORIA",
                            modulo, new Date(), true);

            if (firma1InfoContraloria == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB884"));
                return false;
            }
            cargo1InfoControlario = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO1 EN INFORMES CONTRALORIA",
                            modulo, new Date(), true);

            if (cargo1InfoControlario == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB885"));
                return false;
            }
            cedula1InfoContraloria = ejbSysmanUtil.consultarParametro(compania,
                            "CEDULA1 EN INFORMES CONTRALORIA",
                            modulo, new Date(), true);
            if (cedula1InfoContraloria == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB886"));
                return false;
            }
            firma2InfoContraloria = ejbSysmanUtil.consultarParametro(compania,
                            "FIRMA2 EN INFORMES CONTRALORIA",
                            modulo, new Date(), true);
            if (firma2InfoContraloria == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB887"));
                return false;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        parametros.put("PR_FIRMA1_EN_INFORMES_CONTRALORIA",
                        firma1InfoContraloria);
        parametros.put("PR_CARGO1_EN_INFORMES_CONTRALORIA",
                        cargo1InfoControlario);
        parametros.put("PR_CEDULA1_EN_INFORMES_CONTRALORIA",
                        cedula1InfoContraloria);
        parametros.put("PR_FIRMA2_EN_INFORMES_CONTRALORIA",
                        firma2InfoContraloria);
        return true;
    }

    private boolean validarParametros2(Map<String, Object> parametros) {
        String cargo2InfoContraloria = null;
        String unidadContabilidad = null;
        String contraloriaGeneral = null;
        String cedula2InfoContraloria = null;
        try {
            cargo2InfoContraloria = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO2 EN INFORMES CONTRALORIA",
                            modulo, new Date(), true);
            if (cargo2InfoContraloria == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB888"));
                return false;
            }
            unidadContabilidad = ejbSysmanUtil.consultarParametro(compania,
                            "UNIDAD DE CONTABILIDAD PRESUPUESTAL Y DEL TESORO",
                            modulo, new Date(), true);

            if (unidadContabilidad == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB889"));
                return false;
            }
            contraloriaGeneral = ejbSysmanUtil.consultarParametro(compania,
                            "CONTRALORIA GENERAL DE LA REPUBLICA",
                            modulo, new Date(), true);
            if (contraloriaGeneral == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB890"));
                return false;
            }
            cedula2InfoContraloria = ejbSysmanUtil.consultarParametro(compania,
                            "CEDULA2 EN INFORMES CONTRALORIA",
                            modulo, new Date(), true);

            if (cedula2InfoContraloria == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB891"));
                return false;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        parametros.put("PR_CARGO2_EN_INFORMES_CONTRALORIA",
                        cargo2InfoContraloria);
        parametros.put("PR_ANO", ano);
        parametros.put("PR_UNIDAD_DE_CONTABILIDAD_PRESUPUESTAL_Y_DEL_TESORO",
                        unidadContabilidad);
        parametros.put("PR_CONTRALORIA_GENERAL_DE_LA_REPUBLICA",
                        contraloriaGeneral);
        parametros.put("PR_MES",
                        SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mes]
                                        .toUpperCase());
        parametros.put("PR_CEDULA2_EN_INFORMES_CONTRALORIA",
                        cedula2InfoContraloria);
        return true;
    }

    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerCbEjecucionRaid(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirComando59() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerCbEjecucionRaid(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        cuentaInicial = "";
        cuentaFinal = "";
        nombreCuentaIni = "";
        nombreCuentaFin = "";
        cargarListaCuentaInicial();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCodigoEquiv() {
        // <CODIGO_DESARROLLADO>
        if (codigoEquiv) {
            etiqueNivelVisible = nivelVisible = false;
        }
        else {
            etiqueNivelVisible = nivelVisible = true;
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();
        nombreCuentaIni = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
        cuentaFinal = "";
        nombreCuentaFin = "";
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();
        nombreCuentaFin = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>

    public String getCuentaInicial() {
        return cuentaInicial;
    }

    public boolean isCodigoEquiv() {
        return codigoEquiv;
    }

    public void setCodigoEquiv(boolean codigoEquiv) {
        this.codigoEquiv = codigoEquiv;
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

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
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

    public boolean isEtiqueNivelVisible() {
        return etiqueNivelVisible;
    }

    public void setEtiqueNivelVisible(boolean etiqueNivelVisible) {
        this.etiqueNivelVisible = etiqueNivelVisible;
    }

    public boolean isNivelVisible() {
        return nivelVisible;
    }

    public void setNivelVisible(boolean nivelVisible) {
        this.nivelVisible = nivelVisible;
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

    public String getNombreCuentaIni() {
        return nombreCuentaIni;
    }

    public void setNombreCuentaIni(String nombreCuentaIni) {
        this.nombreCuentaIni = nombreCuentaIni;
    }

    public String getNombreCuentaFin() {
        return nombreCuentaFin;
    }

    public void setNombreCuentaFin(String nombreCuentaFin) {
        this.nombreCuentaFin = nombreCuentaFin;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}

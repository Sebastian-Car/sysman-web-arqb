package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.AnalisisVerticalControladorEnum;
import com.sysman.contabilidad.enums.AnalisisVerticalControladorUrlEnum;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dsuesca
 * @version 1, 27/04/2016
 * @modifier amonroy
 * @version 2, 06/04/2017 Proceso de Refactoring y Revision de buenas
 * practicas SonarLint
 */
@ManagedBean
@ViewScoped
public class AnalisisVerticalControlador extends BeanBaseModal {
    private final String compania;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado de la palabra "CODIGO" en el formulario, almacena el
     * texto CODIGO
     */
    private final String cCodigo;
    private String ckCentroCosto;
    private String ckTercero;
    private String ckAuxiliar;
    private String ckSaldoCero;
    private String codigoInicial;
    private String codigoFinal;
    private String anio;
    private String mes;
    private String digitos;
    private String etiquetaMensajes;
    private String tituloMensajes;
    private StreamedContent archivoDescarga;
    private List<Registro> listaAnoTrabajo;
    private List<Registro> listaMesTrabajo;
    private RegistroDataModelImpl listaCodigoInicial;
    private RegistroDataModelImpl listaCodigoFinal;
    private String referencia;
    private String fuenteRecursos;

    private boolean visibleDialogo;
    private FORMATOS formato;

    /**
     * Creates a new instance of AnalisisVerticalControlador
     */
    public AnalisisVerticalControlador() {
        super();
        compania = SessionUtil.getCompania();
        cCodigo = "CODIGO";
        try {
            numFormulario = GeneralCodigoFormaEnum.ANALISIS_VERTICAL_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            initValores();
        }
        catch (Exception ex) {
            Logger.getLogger(AnalisisVerticalControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init() {
        cargarListaAnoTrabajo();
        cargarListaMesTrabajo();
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        abrirFormulario();
    }

    public void initValores() {
        anio = String.valueOf(SysmanFunciones.ano(new Date()));
        mes = String.valueOf(SysmanFunciones.mes(new Date()));
        codigoInicial = "0";
        codigoFinal = "9";
        digitos = "6";
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaAnoTrabajo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        try {
            listaAnoTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AnalisisVerticalControladorUrlEnum.URL4219
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMesTrabajo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        try {
            listaMesTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AnalisisVerticalControladorUrlEnum.URL4509
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AnalisisVerticalControladorUrlEnum.URL4910
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);

    }

    public void cargarListaCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AnalisisVerticalControladorUrlEnum.URL5995
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(AnalisisVerticalControladorEnum.PARAM0.getValue(),
                        codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        formato = FORMATOS.PDF;
        archivoDescarga = null;
        generarInforme();
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirComando44() {
        // <CODIGO_DESARROLLADO>
        formato = FORMATOS.EXCEL97;
        archivoDescarga = null;
        generarInforme();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiardialogo() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void aceptardialogo() {
        // <CODIGO_DESARROLLADO>
        // pendiente la funcion ActualizaPredecesor()
        generarInforme();

        // </CODIGO_DESARROLLADO>
    }

    public void cancelardialogo() {
        // <CODIGO_DESARROLLADO>
        generarInforme();

        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme() {

        visibleDialogo = false;

        String titulo = idioma.getString("TB_TB3073");
        titulo = titulo.replace("s$nombreMes$s",
                        SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                        .parseInt(mes)].toUpperCase());
        titulo = titulo.replace("s$snio$s", anio);

        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("mes", mes);
        reemplazar.put("anio", anio);
        reemplazar.put("codigoInicial", "'" + codigoInicial + "'");
        reemplazar.put("codigoFinal", "'" + codigoFinal + "9'");
        reemplazar.put("codigoFin", "'" + codigoFinal + "'");
        reemplazar.put("digitos", digitos);
        reemplazar.put("manTer", "true".equals(ckTercero) ? "1" : "0");
        reemplazar.put("manAux", "true".equals(ckAuxiliar) ? "1" : "0");
        reemplazar.put("manCen", "true".equals(ckCentroCosto) ? "1" : "0");
        reemplazar.put("manRef", "true".equals(referencia) ? "1" : "0");
        reemplazar.put("manFue", "true".equals(fuenteRecursos) ? "1" : "0");
        reemplazar.put("baseBalance", Reporteador.resuelveConsulta(
                        "800046BaseBalances",
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar));

        reemplazar.put("saldoCero", "false".equals(ckSaldoCero)
            ? " AND SALDOAUX.SALDO" + mes + " NOT IN (0) "
            : " ");

        // MANEJO DE PARAMETROS DE REEMPLAZO
        Map<String, Object> parametros = new HashMap<>();
        String reporte = "000683Analisisvertical";
        // MANEJO DE PARAMETROS DEL REPORTE
        Reporteador.resuelveConsulta(reporte,
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar, parametros);

        parametros.put("PR_TITULO", titulo);
        try {
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cambiarAnoTrabajo() {
        // <CODIGO_DESARROLLADO>
        cargarListaMesTrabajo();
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        mes = codigoInicial = codigoFinal = null;
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos().get(cCodigo).toString();
        codigoFinal = null;
        cargarListaCodigoFinal();

    }

    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos().get(cCodigo).toString();
    }

    public String getCkCentroCosto() {
        return ckCentroCosto;
    }

    public void setCkCentroCosto(String ckCentroCosto) {
        this.ckCentroCosto = ckCentroCosto;
    }

    public String getCkTercero() {
        return ckTercero;
    }

    public void setCkTercero(String ckTercero) {
        this.ckTercero = ckTercero;
    }

    public String getCkAuxiliar() {
        return ckAuxiliar;
    }

    public void setCkAuxiliar(String ckAuxiliar) {
        this.ckAuxiliar = ckAuxiliar;
    }

    public String getCkSaldoCero() {
        return ckSaldoCero;
    }

    public void setCkSaldoCero(String ckSaldoCero) {
        this.ckSaldoCero = ckSaldoCero;
    }

    public String getCodigoInicial() {
        return codigoInicial;
    }

    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    public String getCodigoFinal() {
        return codigoFinal;
    }

    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
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

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public List<Registro> getListaAnoTrabajo() {
        return listaAnoTrabajo;
    }

    public void setListaAnoTrabajo(List<Registro> listaAnoTrabajo) {
        this.listaAnoTrabajo = listaAnoTrabajo;
    }

    public List<Registro> getListaMesTrabajo() {
        return listaMesTrabajo;
    }

    public void setListaMesTrabajo(List<Registro> listaMesTrabajo) {
        this.listaMesTrabajo = listaMesTrabajo;
    }

    public RegistroDataModelImpl getListaCodigoInicial() {
        return listaCodigoInicial;
    }

    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial) {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    public RegistroDataModelImpl getListaCodigoFinal() {
        return listaCodigoFinal;
    }

    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
        this.listaCodigoFinal = listaCodigoFinal;
    }

    public String getDigitos() {
        return digitos;
    }

    public void setDigitos(String digitos) {
        this.digitos = digitos;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getEtiquetaMensajes() {
        return etiquetaMensajes;
    }

    public void setEtiquetaMensajes(String etiquetaMensajes) {
        this.etiquetaMensajes = etiquetaMensajes;
    }

    public String getTituloMensajes() {
        return tituloMensajes;
    }

    public void setTituloMensajes(String tituloMensajes) {
        this.tituloMensajes = tituloMensajes;
    }

    public boolean isVisibleDialogo() {
        return visibleDialogo;
    }

    public void setVisibleDialogo(boolean visibleDialogo) {
        this.visibleDialogo = visibleDialogo;
    }

    public FORMATOS getFormato() {
        return formato;
    }

    public void setFormato(FORMATOS formato) {
        this.formato = formato;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getFuenteRecursos() {
        return fuenteRecursos;
    }

    public void setFuenteRecursos(String fuenteRecursos) {
        this.fuenteRecursos = fuenteRecursos;
    }

}

package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contratos.enums.EstadocontratoControladorEnum;
import com.sysman.contratos.enums.EstadocontratoControladorUrlEnum;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dcastro
 * @version 1, 25/09/2015
 * 
 * @version 2, 09/08/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 */

@ManagedBean
@ViewScoped
public class EstadocontratoControlador extends BeanBaseModal {

    private final String compania;
    private final String moduloContratos;
    private final String fechaFinalCons;
    private final String tipoContratoIicialCons;
    private final String tipoContratoFinalCons;
    private final String terceroIniCons;
    private final String terceroFinCons;
    private final String prStrSqlCons;
    private final String fechaInicialCons;
    private final String formatoFechaCons;

    private boolean indResumen;
    private boolean vlrLiberado;
    private boolean incluyeRubro;
    private String estado;
    private String terceroInicial;
    private String terceroFinal;
    private String tipoContratoInicial;
    private String tipoContratoFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private String terceroIni;
    private String terceroFin;
    private List<Registro> listaTipoContratoInicial;
    private List<Registro> listaTipoContratoFinal;
    private RegistroDataModelImpl listaTerceroInicial;
    private RegistroDataModelImpl listaTerceroFinal;
    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of EstadocontratoControlador
     */
    public EstadocontratoControlador() {
        super();
        compania = SessionUtil.getCompania();
        moduloContratos = SessionUtil.getModulo();
        fechaFinalCons = "fechaFinal";
        tipoContratoFinalCons = "tipoContratoFinal";
        tipoContratoIicialCons = "tipoContratoInicial";
        terceroIniCons = "terceroIni";
        terceroFinCons = "terceroFin";
        prStrSqlCons = "PR_STRSQL";
        fechaInicialCons = "fechaInicial";
        formatoFechaCons = "dd/MM/yyyy";
        try {
            numFormulario = GeneralCodigoFormaEnum.ESTADOCONTRATO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(EstadocontratoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        cargarListaTipoContratoInicial();
        cargarListaTipoContratoFinal();
        cargarListaTerceroInicial();
        cargarListaTerceroFinal();
        fechaInicial = new Date();
        fechaFinal = new Date();
        abrirFormulario();
    }

    public void cargarListaTipoContratoInicial() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaTipoContratoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EstadocontratoControladorUrlEnum.URL4741
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaTipoContratoFinal() {

        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(EstadocontratoControladorEnum.PARAM0.getValue(),
                            tipoContratoInicial);
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaTipoContratoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            EstadocontratoControladorUrlEnum.URL4715
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaTerceroInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EstadocontratoControladorUrlEnum.URL1783
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");

    }

    public void cargarListaTerceroFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        EstadocontratoControladorUrlEnum.URL2578
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EstadocontratoControladorEnum.PARAM1.getValue(), terceroIni);

        listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NIT");
    }

    public void oprimircmdPantalla() {
        archivoDescarga = null;
        generarReporte(FORMATOS.PDF);
    }

    public void oprimirComando66() {
        archivoDescarga = null;
        generarReporte(FORMATOS.EXCEL97);
    }

    private void generarReporte(FORMATOS formato) {
        try {
            String reporte = generaReportePantalla();
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put(fechaInicialCons, SysmanFunciones
                            .convertirAFechaCadena(fechaInicial));
            reemplazar.put(fechaFinalCons,
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            reemplazar.put(tipoContratoIicialCons, tipoContratoInicial);
            reemplazar.put(tipoContratoFinalCons, tipoContratoFinal);
            reemplazar.put(terceroIniCons, terceroIni);
            reemplazar.put(terceroFinCons, terceroFin);
            reemplazar.put("estado", estado);
            if (incluyeRubro) {
                reemplazar.put("disponibilidad",
                                " ORDENDECOMPRAPPTO.NUMEROPPTO DISPONIBILIDAD,  ORDENDECOMPRAPPTO.RUBRO, ");
                reemplazar.put("disponibilidadg",
                                " ORDENDECOMPRAPPTO.NUMEROPPTO,  ORDENDECOMPRAPPTO.RUBRO, ");
            }
            else {
                reemplazar.put("disponibilidad",
                                " '' DISPONIBILIDAD,  '' RUBRO, ");
                reemplazar.put("disponibilidadg", " '', '', ");
            }
            String strWhere;
            if ("D".equals(estado)) {
                strWhere = " AND    ORDENDECOMPRA.ESTADO IS NOT NULL";
            }
            else {
                strWhere = " AND    ORDENDECOMPRA.ESTADO = '" + estado + "'  ";
            }
            reemplazar.put("strWhere", strWhere);

            String strSql = Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(moduloContratos), reemplazar);

            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            parametros.put(prStrSqlCons, strSql);
            parametros.put("PR_FECHAINICIAL", SysmanFunciones
                            .convertirAFechaCadena(fechaInicial,
                                            formatoFechaCons));
            parametros.put("PR_FECHAFINAL", SysmanFunciones
                            .convertirAFechaCadena(fechaFinal,
                                            formatoFechaCons));
            parametros.put("PR_VISIBLE_RUBRO", incluyeRubro);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * dmaldonado: TAR 3000000282 - Se separa del metodo
     * oprimirCmdPantalla, con el fin de generalizarlo tanto para
     * excel como para PDF.
     *
     * Los informes generados en el siguiente metodo, son los
     * pertenecientes al evento de click en el boton cmdPantalla.
     */
    public String generaReportePantalla() {
        String reporte;
        if (indResumen) {
            if (vlrLiberado) {
                reporte = "000238EstadoContratoSog012";
            }
            else {
                reporte = "000239EstadoContratoSog01";
            }
        }
        else {
            if (vlrLiberado) {
                reporte = "000229EstadoContratoSog2";
            }
            else {
                reporte = "000226EstadoContratoSog";
            }
        }

        return reporte;

    }

    public void cambiarTipoContratoInicial() {
        cargarListaTipoContratoFinal();
    }

    public void cambiarIndResumen() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarvlrliberado() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaTerceroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroIni = registroAux.getCampos().get("NIT").toString();
        terceroInicial = registroAux.getCampos().get("NOMBRE").toString();
        cargarListaTerceroFinal();

    }

    public void seleccionarFilaTerceroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroFin = registroAux.getCampos().get("NIT").toString();
        terceroFinal = registroAux.getCampos().get("NOMBRE").toString();
    }

    public boolean getIndResumen() {
        return indResumen;
    }

    public void setIndResumen(boolean indResumen) {
        this.indResumen = indResumen;
    }

    public boolean getVlrLiberado() {
        return vlrLiberado;
    }

    public void setVlrLiberado(boolean vlrLiberado) {
        this.vlrLiberado = vlrLiberado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTerceroInicial() {
        return terceroInicial;
    }

    public void setTerceroInicial(String terceroInicial) {
        this.terceroInicial = terceroInicial;
    }

    public String getTerceroFinal() {
        return terceroFinal;
    }

    public void setTerceroFinal(String terceroFinal) {
        this.terceroFinal = terceroFinal;
    }

    public String getTipoContratoInicial() {
        return tipoContratoInicial;
    }

    public void setTipoContratoInicial(String tipoContratoInicial) {
        this.tipoContratoInicial = tipoContratoInicial;
    }

    public String getTipoContratoFinal() {
        return tipoContratoFinal;
    }

    public void setTipoContratoFinal(String tipoContratoFinal) {
        this.tipoContratoFinal = tipoContratoFinal;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public List<Registro> getListaTipoContratoInicial() {
        return listaTipoContratoInicial;
    }

    public void setListaTipoContratoInicial(
        List<Registro> listaTipoContratoInicial) {
        this.listaTipoContratoInicial = listaTipoContratoInicial;
    }

    public List<Registro> getListaTipoContratoFinal() {
        return listaTipoContratoFinal;
    }

    public void setListaTipoContratoFinal(
        List<Registro> listaTipoContratoFinal) {
        this.listaTipoContratoFinal = listaTipoContratoFinal;
    }

    public RegistroDataModelImpl getListaTerceroInicial() {
        return listaTerceroInicial;
    }

    public void setListaTerceroInicial(
        RegistroDataModelImpl listaTerceroInicial) {
        this.listaTerceroInicial = listaTerceroInicial;
    }

    public RegistroDataModelImpl getListaTerceroFinal() {
        return listaTerceroFinal;
    }

    public void setListaTerceroFinal(RegistroDataModelImpl listaTerceroFinal) {
        this.listaTerceroFinal = listaTerceroFinal;
    }

    public boolean validarFechas(Date fechaInicial, Date fechaFinal) {
        boolean respuesta = true;
        if (fechaInicial.compareTo(fechaFinal) > 0) {
            respuesta = false;
        }
        return respuesta;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public boolean isIncluyeRubro() {
        return incluyeRubro;
    }

    public void setIncluyeRubro(boolean incluyeRubro) {
        this.incluyeRubro = incluyeRubro;
    }

}

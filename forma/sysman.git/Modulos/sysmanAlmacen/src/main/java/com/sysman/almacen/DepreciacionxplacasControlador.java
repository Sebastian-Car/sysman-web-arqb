package com.sysman.almacen;

import com.sysman.almacen.enums.DepreciacionxplacasControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;

import java.io.IOException;
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

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ybecerra
 * @version 1, 10/11/2015
 * 
 * @version 2, 27/04/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 */
@ManagedBean
@ViewScoped
public class DepreciacionxplacasControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private final String coserie;
    private String serie;
    private String elemento;
    private String placa;
    private String marca;
    private String serieDevolutivo;
    private String valor;
    private String serieAnterior;
    private String nombreLargo;
    private String nombreCodigo;
    private String nombreCedula;
    private String estado;
    private String placaAnulada;
    private String niif;
    private String niifVisible;
    private RegistroDataModelImpl listaSerie;
    private StreamedContent archivoDescarga;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;

    /**
     * Creates a new instance of DepreciacionxplacasControlador
     */
    public DepreciacionxplacasControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        coserie = "SERIE";
        try {
            numFormulario = GeneralCodigoFormaEnum.DEPRECIACIONXPLACAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(DepreciacionxplacasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaSerie();
        abrirFormulario();
    }

    public void cargarListaSerie() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DepreciacionxplacasControladorUrlEnum.URL3270
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaSerie = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, coserie);
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        if ("SI".equals(niif)) {
            generarInforme(ReportesBean.FORMATOS.EXCEL97,
                            "000369DepreciacionXPlacaNIIF");
        }
        else {
            generarInforme(ReportesBean.FORMATOS.EXCEL97,
                            "000368DepreciacionXPlaca");
        }
        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(ReportesBean.FORMATOS formato,
        String parReporte) {

        try {
            HashMap<String, Object> remplazar = new HashMap<>();
            remplazar.put("seriePlaca", serie);
            remplazar.put("placaSerie", serie);

            String strsql = Reporteador.resuelveConsulta(parReporte,
                            Integer.parseInt(modulo), remplazar);

            Map<String, Object> parametros = new HashMap<>();

            parametros.put("PR_STRSQL", strsql);

            archivoDescarga = JsfUtil.exportarStreamed(parReporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirPresentar() {

        // <CODIGO_DESARROLLADO>
        if ("SI".equals(niif)) {
            generarInforme(ReportesBean.FORMATOS.PDF,
                            "000369DepreciacionXPlacaNIIF");
        }
        else {
            generarInforme(ReportesBean.FORMATOS.PDF,
                            "000368DepreciacionXPlaca");
        }

        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaSerie(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        serie = registroAux.getCampos().get(coserie).toString();
        elemento = registroAux.getCampos().get("ELEMENTO").toString();
        marca = (String) registroAux.getCampos().get("MARCA");
        serieAnterior = registroAux.getCampos().get("SERIEANTERIOR").toString();
        valor = registroAux.getCampos().get("VALOR").toString();
        nombreLargo = registroAux.getCampos().get("TXTNOM").toString();
        placa = registroAux.getCampos().get(coserie).toString();
        serieDevolutivo = registroAux.getCampos().get("SERIEDEVOLUTIVO")
                        .toString();
        estado = registroAux.getCampos().get("ESTADO").toString();
        placaAnulada = registroAux.getCampos().get("PLACAANULADA").toString();
        nombreCodigo = registroAux.getCampos().get("DEPENDENCIA").toString();
        nombreCedula = registroAux.getCampos().get("RESPONSABLE").toString();

    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getElemento() {
        return elemento;
    }

    public void setElemento(String elemento) {
        this.elemento = elemento;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getSerieDevolutivo() {
        return serieDevolutivo;
    }

    public void setSerieDevolutivo(String serieDevolutivo) {
        this.serieDevolutivo = serieDevolutivo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getSerieAnterior() {
        return serieAnterior;
    }

    public void setSerieAnterior(String serieAnterior) {
        this.serieAnterior = serieAnterior;
    }

    public String getNombreLargo() {
        return nombreLargo;
    }

    public void setNombreLargo(String nombreLargo) {
        this.nombreLargo = nombreLargo;
    }

    public String getNombreCodigo() {
        return nombreCodigo;
    }

    public void setNombreCodigo(String nombreCodigo) {
        this.nombreCodigo = nombreCodigo;
    }

    public String getNombreCedula() {
        return nombreCedula;
    }

    public void setNombreCedula(String nombreCedula) {
        this.nombreCedula = nombreCedula;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNiifVisible() {
        return niifVisible;
    }

    public void setNiifVisible(String niifVisible) {
        this.niifVisible = niifVisible;
    }

    public String getPlacaAnulada() {
        return placaAnulada;
    }

    public void setPlacaAnulada(String placaAnulada) {
        this.placaAnulada = placaAnulada;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public RegistroDataModelImpl getListaSerie() {
        return listaSerie;
    }

    public void setListaSerie(RegistroDataModelImpl listaSerie) {
        this.listaSerie = listaSerie;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        try {
            niif = ejbSysmanUtilRemote.consultarParametro(compania,
                            "MANEJA NIIF EN ALMACEN", modulo, new Date(), true);

        }
        catch (SystemException ex) {
            Logger.getLogger(DepreciacionxplacasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }

}

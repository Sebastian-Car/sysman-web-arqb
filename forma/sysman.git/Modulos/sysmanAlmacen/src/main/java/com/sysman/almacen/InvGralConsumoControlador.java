package com.sysman.almacen;

import com.sysman.almacen.enums.InvGralConsumoControladorEnum;
import com.sysman.almacen.enums.InvGralConsumoControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
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
 * @author dmaldonado
 * @version 1, 28/01/2016
 * 
 * @author eamaya
 * @version 2, 03/05/2017 Proceso de Refactoring y Correcciones Sonar
 * Lint
 * 
 * @author eamaya
 * @version 3.0, 12/06/2017 Cambio c�digo formulario y actualizaci�n
 * de ConnectorPool
 */
@ManagedBean
@ViewScoped

public class InvGralConsumoControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private String menuActual = SessionUtil.getMenuActual();
    private boolean agrupado;
    private String depFinal;
    private String depInicial;
    private String elementoHasta;
    private String elementoDesde;
    private String nomElemDesde;
    private Date fechaHasta;
    private Date fechaDesde;
    private String nomElemHasta;
    private String nomDepInicial;
    private String nomDepFinal;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listacmbElementoHasta;
    private RegistroDataModelImpl listaDependenciaFinal;
    private RegistroDataModelImpl listaDependenciaInicial;
    private RegistroDataModelImpl listacmbElementoDesde;

    private String tipoElemento;
    private String tituloModal;

    /**
     * Creates a new instance of InvGralConsumoControlador
     */
    public InvGralConsumoControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        try {
            numFormulario = GeneralCodigoFormaEnum.INV_GRAL_CONSUMO_CONTROLADOR
                            .getCodigo();

            if ("1004020901".equals(menuActual)) {
                tituloModal = idioma.getString("TB_TB1869")
                                .toUpperCase();
                tipoElemento = "C";
            }
            else if ("1004020902".equals(menuActual)) {
                tituloModal = idioma.getString("TB_TB1870")
                                .toUpperCase();
                tipoElemento = "D";
            }

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(InvGralConsumoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init() {
        cargarListaDependenciaInicial();
        cargarListacmbElementoDesde();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListacmbElementoDesde() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InvGralConsumoControladorUrlEnum.URL5974
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(InvGralConsumoControladorEnum.PARAM2.getValue(),
                        tipoElemento);

        listacmbElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGOELEMENTO.getName());
    }

    public void cargarListacmbElementoHasta() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InvGralConsumoControladorUrlEnum.URL3890
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(InvGralConsumoControladorEnum.PARAM2.getValue(),
                        String.valueOf(tipoElemento));

        param.put(InvGralConsumoControladorEnum.PARAM3.getValue(),
                        String.valueOf(elementoDesde));

        listacmbElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGOELEMENTO.getName());
    }

    public void cargarListaDependenciaInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InvGralConsumoControladorUrlEnum.URL5363
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaDependenciaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListaDependenciaFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InvGralConsumoControladorUrlEnum.URL4679
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(InvGralConsumoControladorEnum.PARAM0.getValue(),
                        depInicial);

        listaDependenciaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void generaInforme(ReportesBean.FORMATOS formato) {

        archivoDescarga = null;
        String reporte = "";
        try {
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            String strSql;
            reemplazar.put("fechaDesde",
                            SysmanFunciones.formatearFecha(fechaDesde));
            reemplazar.put("fechaHasta",
                            SysmanFunciones.formatearFecha(fechaHasta));
            reemplazar.put("elementoInicial", elementoDesde);
            reemplazar.put("elementoFinal", elementoHasta);
            reemplazar.put("depInicial", depInicial);
            reemplazar.put("depFinal", depFinal);
            reemplazar.put("clase", tipoElemento);
            strSql = Reporteador.resuelveConsulta("000493IElConsumo",
                            Integer.parseInt(modulo), reemplazar);
            parametros.put("PR_ENTREFECHAS",
                            "Desde "
                                + SysmanFunciones.convertirAFechaCadena(
                                                fechaDesde,
                                                "EEEEE',' dd ' de 'MMMM' de ' yyyy")
                                + " hasta "
                                + SysmanFunciones.convertirAFechaCadena(
                                                fechaHasta,
                                                "EEEEE',' dd ' de 'MMMM' de ' yyyy")
                                + ".");
            parametros.put("PR_TITULO", tituloModal.toUpperCase());
            parametros.put("PR_STRSQL", strSql);

            if (!agrupado) {
                reporte = "000493IElConsumo";
                archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
            }
            else {
                reporte = "000498IElConsumoAgr";

                archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);

            }
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cambiarcmbElementoHasta() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFechaHasta() {
        // <CODIGO_DESARROLLADO>
        if ((fechaDesde == null) || (fechaHasta == null)) {
            return;
        }
        if (fechaDesde.after(fechaHasta)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB574"));
            fechaHasta = null;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFechaDesde() {
        // <CODIGO_DESARROLLADO>
        if ((fechaDesde == null) || (fechaHasta == null)) {
            return;
        }
        if (fechaDesde.after(fechaHasta)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB574"));
            fechaHasta = null;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaDependenciaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        depFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();

        nomDepFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
    }

    public void seleccionarFilaDependenciaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        depInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                                        "")
                        .toString();

        nomDepInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
        depFinal = null;
        nomDepFinal = null;
        cargarListaDependenciaFinal();
    }

    public void seleccionarFilacmbElementoDesde(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoDesde = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGOELEMENTO
                                                        .getName()),
                                        "")
                        .toString();

        nomElemDesde = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRELARGO"), "")
                        .toString();
        elementoHasta = null;
        nomElemHasta = null;
        cargarListacmbElementoHasta();
    }

    public void seleccionarFilacmbElementoHasta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoHasta = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGOELEMENTO
                                                        .getName()),
                                        "")
                        .toString();

        nomElemHasta = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRELARGO"), "")
                        .toString();

    }

    public boolean isAgrupado() {
        return agrupado;
    }

    public void setAgrupado(boolean agrupado) {
        this.agrupado = agrupado;
    }

    public String getDepFinal() {
        return depFinal;
    }

    public void setDepFinal(String depFinal) {
        this.depFinal = depFinal;
    }

    public String getDepInicial() {
        return depInicial;
    }

    public void setDepInicial(String depInicial) {
        this.depInicial = depInicial;
    }

    public String getElementoHasta() {
        return elementoHasta;
    }

    public void setElementoHasta(String elementoHasta) {
        this.elementoHasta = elementoHasta;
    }

    public String getElementoDesde() {
        return elementoDesde;
    }

    public void setElementoDesde(String elementoDesde) {
        this.elementoDesde = elementoDesde;
    }

    public String getNomElemDesde() {
        return nomElemDesde;
    }

    public void setNomElemDesde(String nomElemDesde) {
        this.nomElemDesde = nomElemDesde;
    }

    public Date getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(Date fechaHasta) {
        this.fechaHasta = fechaHasta;
    }

    public Date getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(Date fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public String getNomElemHasta() {
        return nomElemHasta;
    }

    public void setNomElemHasta(String nomElemHasta) {
        this.nomElemHasta = nomElemHasta;
    }

    public String getNomDepInicial() {
        return nomDepInicial;
    }

    public void setNomDepInicial(String nomDepInicial) {
        this.nomDepInicial = nomDepInicial;
    }

    public String getNomDepFinal() {
        return nomDepFinal;
    }

    public void setNomDepFinal(String nomDepFinal) {
        this.nomDepFinal = nomDepFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListacmbElementoHasta() {
        return listacmbElementoHasta;
    }

    public void setListacmbElementoHasta(
        RegistroDataModelImpl listacmbElementoHasta) {
        this.listacmbElementoHasta = listacmbElementoHasta;
    }

    public RegistroDataModelImpl getListaDependenciaFinal() {
        return listaDependenciaFinal;
    }

    public void setListaDependenciaFinal(
        RegistroDataModelImpl listaDependenciaFinal) {
        this.listaDependenciaFinal = listaDependenciaFinal;
    }

    public RegistroDataModelImpl getListaDependenciaInicial() {
        return listaDependenciaInicial;
    }

    public void setListaDependenciaInicial(
        RegistroDataModelImpl listaDependenciaInicial) {
        this.listaDependenciaInicial = listaDependenciaInicial;
    }

    public RegistroDataModelImpl getListacmbElementoDesde() {
        return listacmbElementoDesde;
    }

    public void setListacmbElementoDesde(
        RegistroDataModelImpl listacmbElementoDesde) {
        this.listacmbElementoDesde = listacmbElementoDesde;
    }

    public String getTituloModal() {
        return tituloModal;
    }

    public void setTituloModal(String tituloModal) {
        this.tituloModal = tituloModal;
    }

    @Override
    public int getNumFormulario() {
        return numFormulario;
    }
}
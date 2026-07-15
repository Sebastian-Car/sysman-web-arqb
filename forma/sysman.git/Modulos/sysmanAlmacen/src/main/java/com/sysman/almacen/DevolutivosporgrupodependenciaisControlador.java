package com.sysman.almacen;

import com.sysman.almacen.ejb.EjbAlmacenUnoRemote;
import com.sysman.almacen.enums.DevolutivosporgrupodependenciaisControladorEnum;
import com.sysman.almacen.enums.DevolutivosporgrupodependenciaisControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

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
 * @version 1, 26/10/2015
 * @author yrojas
 * @version 2, 27/04/2017 Se cambiaron las consultas por la invocacion
 * de los DSS. Se modifico controlador segun especificaciones del
 * SonarLint. El llamado a Acciones fue cambiado por la invocaci�n de
 * los EJB
 * 
 * @author eamaya
 * @version 3.0, 12/06/2017 Cambio c�digo formulario y actualizaci�n
 * de ConnectorPool
 */
@ManagedBean
@ViewScoped
public class DevolutivosporgrupodependenciaisControlador extends BeanBaseModal {

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbAlmacenUnoRemote ejbAlmacenUno;

    private final String compania;
    private final String modulo;
    private final String cCodigoElemento;
    private boolean resumen;
    private boolean soloResumen;
    private String elementoDesde;
    private String elementoHasta;
    private String codigoInicial;
    private String codigoFinal;
    private String actualizaDesde;
    private String actualizaHasta;
    private String codInicial;
    private String codFinal;
    private String agrupacion;
    private String opcion;
    private boolean soloResu;
    private boolean resu;
    private String grupo;
    private String dependencia;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaElementoDesde;
    private RegistroDataModelImpl listaElementoHasta;
    private RegistroDataModelImpl listaCodigoInicial;
    private RegistroDataModelImpl listaCodigoFinal;

    /**
     * Creates a new instance of
     * DevolutivosporgrupodependenciaisControlador
     */
    public DevolutivosporgrupodependenciaisControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cCodigoElemento = "CODIGOELEMENTO";
        try {
            numFormulario = GeneralCodigoFormaEnum.DEVOLUTIVOSPORGRUPODEPENDENCIAIS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(DevolutivosporgrupodependenciaisControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        abrirFormulario();

        opcion = "1";
        cargarListaElementoDesde();
        cargarListaElementoHasta();
        cargarListaCodigoInicial();

        cargarListaCodigoFinal();

    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        try {
            agrupacion = ejbSysmanUtil.consultarParametro(compania,
                            "DIGITOS AGRUPACION INVENTARIO", modulo, new Date(),
                            true);
        }
        catch (SystemException ex) {
            Logger.getLogger(DevolutivosporgrupodependenciaisControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1853"));
        }

        // </CODIGO_DESARROLLADO>

        if ((resumen) && (!soloResumen)) {
            resu = false;
            soloResu = true;

        }
        else if ((!resumen) && (soloResumen)) {
            resu = true;
            soloResu = false;
        }

        resumen = true;
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaElementoDesde() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DevolutivosporgrupodependenciaisControladorUrlEnum.URL4041
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(DevolutivosporgrupodependenciaisControladorEnum.PARAM0
                        .getValue(), agrupacion);

        listaElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        cCodigoElemento);
    }

    public void cargarListaElementoHasta() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DevolutivosporgrupodependenciaisControladorUrlEnum.URL4907
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(DevolutivosporgrupodependenciaisControladorEnum.PARAM0
                        .getValue(), agrupacion);
        param.put(DevolutivosporgrupodependenciaisControladorEnum.PARAM1
                        .getValue(), elementoDesde);

        listaElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        cCodigoElemento);
    }

    public void cargarListaCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DevolutivosporgrupodependenciaisControladorUrlEnum.URL6016
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        DevolutivosporgrupodependenciaisControladorUrlEnum.URL6857
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(DevolutivosporgrupodependenciaisControladorEnum.PARAM2
                        .getValue(), codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public boolean validarCheck() {
        if (!resumen && !soloResumen) {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2775"));
            return false;
        }

        return true;
    }

    public void generarInforme(FORMATOS formato) {
        if (!validarCheck()) {
            return;
        }

        Integer detalle = 1;
        Integer sub = 1;
        String parReporte = "";

        if (soloResumen) {
            parReporte = "000349ResumenDevolutivoDependencia";
            resumen = false;
        }

        if (parReporte == "") {
            if ("1".equals(opcion)) {
                detalle = 1;
                parReporte = "000351DevolutivosPorGrupoDependenciaIResumen";
            }
            else {
                sub = 1;
                parReporte = "000353DevolutivosPorDependenciaGrupoIResumen";
            }
        }
        try {

            HashMap<String, Object> reemplazar = new HashMap<>();

            reemplazar.put("elementoDesde", "'" + elementoDesde + "'");
            reemplazar.put("elementoHasta", "'" + elementoHasta + "'");
            reemplazar.put("codigoInicial", "'" + codigoInicial + "'");
            reemplazar.put("codigoFinal", "'" + codigoFinal + "'");

            String strSql = Reporteador.resuelveConsulta(parReporte,
                            Integer.parseInt(modulo), reemplazar);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_STRSQL", strSql);
            parametros.put("PR_STRSQL_SECUNDARIO74", strSql);
            parametros.put("PR_DETALLE", detalle);
            parametros.put("PR_SUB", sub);

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
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.EXCEL97);

    }

    public void cambiarSoloResumen() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarResumen() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </CODIGO_DESARROLLADO>
    public void cambiarlblElementoDesde() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaElementoDesde(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoDesde = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigoElemento), "")
                        .toString();
        actualizaDesde = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRELARGO"), "")
                        .toString();
        cargarListaElementoHasta();
    }

    public void seleccionarFilaElementoHasta(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        elementoHasta = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigoElemento), "")
                        .toString();
        actualizaHasta = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRELARGO"), "")
                        .toString();
    }

    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "")
                        .toString();
        codInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                        "")
                        .toString();
        cargarListaCodigoFinal();

    }

    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "")
                        .toString();
        codFinal = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                        "")
                        .toString();
    }

    public boolean getResumen() {
        return resumen;
    }

    public void setResumen(boolean resumen) {
        this.resumen = resumen;
    }

    public Boolean getSoloResumen() {
        return soloResumen;
    }

    public void setSoloResumen(Boolean soloResumen) {
        this.soloResumen = soloResumen;
    }

    public String getElementoDesde() {
        return elementoDesde;
    }

    public void setElementoDesde(String elementoDesde) {
        this.elementoDesde = elementoDesde;
    }

    public String getElementoHasta() {
        return elementoHasta;
    }

    public void setElementoHasta(String elementoHasta) {
        this.elementoHasta = elementoHasta;
    }

    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
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

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getActualizaDesde() {
        return actualizaDesde;
    }

    public void setActualizaDesde(String actualizaDesde) {
        this.actualizaDesde = actualizaDesde;
    }

    public String getActualizaHasta() {
        return actualizaHasta;
    }

    public void setActualizaHasta(String actualizaHasta) {
        this.actualizaHasta = actualizaHasta;
    }

    public String getCodInicial() {
        return codInicial;
    }

    public void setCodInicial(String codInicial) {
        this.codInicial = codInicial;
    }

    public String getCodFinal() {
        return codFinal;
    }

    public void setCodFinal(String codFinal) {
        this.codFinal = codFinal;
    }

    public String getAgrupacion() {
        return agrupacion;
    }

    public void setAgrupacion(String agrupacion) {
        this.agrupacion = agrupacion;
    }

    public RegistroDataModelImpl getListaElementoDesde() {
        return listaElementoDesde;
    }

    public void setListaElementoDesde(
        RegistroDataModelImpl listaElementoDesde) {
        this.listaElementoDesde = listaElementoDesde;
    }

    public RegistroDataModelImpl getListaElementoHasta() {
        return listaElementoHasta;
    }

    public void setListaElementoHasta(
        RegistroDataModelImpl listaElementoHasta) {
        this.listaElementoHasta = listaElementoHasta;
    }

    public RegistroDataModelImpl getListaCodigoInicial() {
        return listaCodigoInicial;
    }

    public boolean getSoloResu() {
        return soloResu;
    }

    public void setSoloResu(boolean soloResu) {
        this.soloResu = soloResu;
    }

    public boolean getResu() {
        return resu;
    }

    public void setResu(boolean resu) {
        this.resu = resu;
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

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getDependencia() {
        return dependencia;
    }

    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }

}

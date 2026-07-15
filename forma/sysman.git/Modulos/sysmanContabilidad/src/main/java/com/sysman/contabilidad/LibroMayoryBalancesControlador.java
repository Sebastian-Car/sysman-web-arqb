package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.LibroMayoryBalancesControladorEnum;
import com.sysman.contabilidad.enums.LibroMayoryBalancesControladorUrlEnum;
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
import javax.faces.event.ActionEvent;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 03/05/2016
 * @modified mzanguna
 * @version 2, 10/04/2017
 * @version 3,20/04/2017, Cambio EJB.
 */
@ManagedBean
@ViewScoped

public class LibroMayoryBalancesControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    private String claseImpresion;
    private boolean fecha = false;
    private boolean claseCero;
    private boolean centroCosto;
    private boolean conFecha;
    private Date fechaReporte;
    private String cuentaInicial;
    private String cuentaFinal;
    private String centroInicial;
    private String centroFinal;
    private String anoTrabajo;
    private String mesTrabajo;
    private String codigoLibro;
    private String numeroInicial;
    private String digitos;
    private StreamedContent archivoDescarga;
    private List<Registro> listaAnoTrabajo;
    private List<Registro> listaMesTrabajo;
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    private RegistroDataModelImpl listaCmbCentroCInicial;
    private RegistroDataModelImpl listaCmbCentroCFinal;
    private boolean valorNeto;


	@EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    private boolean formatoEsp;

    /**
     * Creates a new instance of LibroMayoryBalancesControlador
     */
    public LibroMayoryBalancesControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.LIBRO_MAYORY_BALANCES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            cuentaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
            cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        }
        catch (Exception ex) {
            Logger.getLogger(LibroMayoryBalancesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        cargarListaAnoTrabajo();
        anoTrabajo = String.valueOf(SysmanFunciones
                        .ano(new Date()));
        cargarListaMesTrabajo();
        mesTrabajo = String.valueOf(SysmanFunciones
                        .mes(new Date()));
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
        cargarListaCmbCentroCInicial();
        cargarListaCmbCentroCFinal();
        digitos = "1";
        claseImpresion = "1";
        conFecha = false;
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        String formatoEspecial = null;
        try {
            formatoEspecial = ejbSysmanUtil.consultarParametro(compania,
                            "FORMATO ESPECIAL IMPRESION", modulo, new Date(),
                            false);
        }
        catch (SystemException e) {
            Logger.getLogger(LibroMayoryBalancesControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        formatoEspecial = formatoEspecial == null ? "NO" : formatoEspecial;
        if ("SI".equals(formatoEspecial)) {
            formatoEsp = true;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaAnoTrabajo() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnoTrabajo = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            LibroMayoryBalancesControladorUrlEnum.URL4431
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {

            Logger.getLogger(LibroMayoryBalancesControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
        // listaAnoTrabajo =
        // RegistroConverter.toListRegistro(parameters)
    }

    public void cargarListaMesTrabajo() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoTrabajo);

        try {
            listaMesTrabajo = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            LibroMayoryBalancesControladorUrlEnum.URL4779
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            Logger.getLogger(LibroMayoryBalancesControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
    }

    public void cargarListaCuentaInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LibroMayoryBalancesControladorUrlEnum.URL5213
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoTrabajo);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        LibroMayoryBalancesControladorEnum.COD.getValue());
    }

    public void cargarListaCuentaFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LibroMayoryBalancesControladorUrlEnum.URL6250
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoTrabajo);
        param.put(LibroMayoryBalancesControladorEnum.CINICIAL.getValue(),
                        cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        LibroMayoryBalancesControladorEnum.COD.getValue());
    }

    public void cargarListaCmbCentroCInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LibroMayoryBalancesControladorUrlEnum.URL7411
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCmbCentroCInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        LibroMayoryBalancesControladorEnum.COD.getValue());

    }

    public void cargarListaCmbCentroCFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LibroMayoryBalancesControladorUrlEnum.URL8145
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LibroMayoryBalancesControladorEnum.COSTOIN.getValue(),
                        centroInicial);

        listaCmbCentroCFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        LibroMayoryBalancesControladorEnum.COD.getValue());
    }

    public void oprimirImprimir(ActionEvent ac) {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel(ActionEvent ac) {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    private void generarInforme(ReportesBean.FORMATOS formato) {
        try {

            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            String reporte = getReporte(reemplazar, parametros);
            if ((reporte == null) || ("").equals(reporte)) {
                return;
            }
            // MANEJO DE PARAMETROS DEL REPORTE

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private String getReporte(HashMap<String, Object> reemplazar,
        Map<String, Object> parametros) {
        String reporte;
        String nombreJefe = "";
        String cargoJefe = "";
        String formatoEspecial = "";
        String firmaLibroMayor = "";
        try {

            nombreJefe = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE DE JEFE DE CONTABILIDAD", modulo,
                            new Date(), false);

            cargoJefe = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO DE JEFE DE CONTABILIDAD", modulo,
                            new Date(), false);

            formatoEspecial = ejbSysmanUtil.consultarParametro(compania,
                            "FORMATO ESPECIAL IMPRESION", modulo,
                            new Date(), false);

            firmaLibroMayor = ejbSysmanUtil.consultarParametro(compania,
                            "FIRMA INFORME LIBRO MAYOR Y BALANCES", modulo,
                            new Date(), false);
        }
        catch (SystemException e) {
            Logger.getLogger(LibroMayoryBalancesControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (SysmanFunciones.validarVariableVacio(anoTrabajo)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB128"));
            return null;
        }
        if (SysmanFunciones.validarVariableVacio(mesTrabajo)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB129"));
            return null;
        }
        if (SysmanFunciones.validarVariableVacio(digitos)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB130"));
            return null;
        }
        if (SysmanFunciones.validarVariableVacio(cuentaInicial)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB131"));
            return null;
        }
        if (SysmanFunciones.validarVariableVacio(cuentaFinal)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB132"));
            return null;
        }

        numeroInicial = (numeroInicial == null) || ("").equals(numeroInicial)
            ? "0" : numeroInicial;
        reemplazar.put("anoTrabajo", anoTrabajo);
        reemplazar.put("anio", anoTrabajo);
        reemplazar.put("mesTrabajo", mesTrabajo);
        reemplazar.put("mesTrabajo-1", Integer.parseInt(mesTrabajo) - 1);
        reemplazar.put("digitos", digitos);
        reemplazar.put("cuentaInicial", cuentaInicial);
        reemplazar.put("cuentaFinal", cuentaFinal);
        reemplazar.put("centroCostoCond",
                        centroCosto
                            ? " AND PLAN_CONTABLE.CENTRO_COSTO_INFORME BETWEEN '"
                                + centroInicial + "' "
                                + "AND '" + centroFinal + "'"
                            : " AND PLAN_CONTABLE.CENTRO_COSTO_INFORME IS NULL ");
        reemplazar.put("claseCero", claseCero ? "-1" : "0");
        reemplazar.put("mayoriza",
                        ("4").equals(claseImpresion) ? "1" : "0");
        reemplazar.put("noMayoriza",
                        centroCosto && !("4").equals(claseImpresion)
                            ? "SALDO_AUX_CONTABLE.CODIGO"
                            : "SUBSTR(SALDO_AUX_CONTABLE.CODIGO, 1, LENGTH(PLAN_CONTABLE.CODIGO)) ");

        if (("3").equals(claseImpresion)) {
            reporte = "000732LisMayorYBalancesCO";
        }
        else if (("4").equals(claseImpresion)) {
            if (centroCosto) {
                if (SysmanFunciones.validarVariableVacio(centroInicial)) {
                    JsfUtil.agregarMensajeAlerta(idioma
                                    .getString(LibroMayoryBalancesControladorEnum.MSJ1
                                                    .getValue()));
                    return null;
                }

                if (SysmanFunciones.validarVariableVacio(centroFinal)) {
                    JsfUtil.agregarMensajeAlerta(idioma
                                    .getString(LibroMayoryBalancesControladorEnum.MSJ2
                                                    .getValue()));
                    return null;
                }

            }
            else {
                centroInicial = "0";
                centroFinal = SysmanConstantes.CONS_CENTRO;
            }

            reporte = "000741LisMayorYBalancesMayorizado";
            reemplazar.put("centroInicial", centroInicial);
            reemplazar.put("centroFinal", centroFinal);

        }
        else {
        	if (valorNeto) {
        		reporte = "002726LisMayorYBalances_IDCBIS";
        	}
        	else {
        		reporte = "000729LisMayorYBalances";
        	}
            if (centroCosto) {

                if (SysmanFunciones.validarVariableVacio(centroInicial)) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB133"));
                    return null;
                }
                if (SysmanFunciones.validarVariableVacio(centroFinal)) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB134"));
                    return null;
                }

                reporte = "000741LisMayorYBalancesMayorizado";
                reemplazar.put("centroInicial", centroInicial);
                reemplazar.put("centroFinal", centroFinal);

            }
        }

        Reporteador.resuelveConsulta(reporte,
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar, parametros);

        parametros.put("PR_ANOTRABAJO", anoTrabajo);
        parametros.put("PR_MESTRABAJO",
                        SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                        .parseInt(mesTrabajo)]
                                                        .toUpperCase());
        parametros.put("PR_SUMAS", !("4").equals(claseImpresion));

        parametros.put("PR_CLASEIMPRESION", claseImpresion);
        parametros.put("PR_FECHACOND", fecha);
        parametros.put("PR_FORMATOESPECIAL", formatoEspecial);
        parametros.put("PR_CLASEIMPRESION", claseImpresion);
        parametros.put("PR_NOMBRECOMPANIA",
                        SessionUtil.getCompaniaIngreso().getNombre());
        parametros.put("PR_NITCOMPANIA",
                        SessionUtil.getCompaniaIngreso().getNit());
        parametros.put("PR_CODIGO_LIBRO",
                        (codigoLibro == null) || ("").equals(codigoLibro) ? ""
                            : "Código. " + codigoLibro);
        parametros.put("PR_NUMERO_INICIAL",
                        (Integer.parseInt(numeroInicial)) == 0 ? 0
                            : (Integer.parseInt(numeroInicial)) - 1);
        parametros.put("PR_NOMBRE_DE_JEFE_DE_CONTABILIDAD", nombreJefe);
        parametros.put("PR_CARGO_DE_JEFE_DE_CONTABILIDAD", cargoJefe);
        parametros.put("PR_INFORMELIBROMAYOR", firmaLibroMayor);
        
        if(conFecha) {
        	parametros.put("FECHA_D", SysmanFunciones.nvl(fechaReporte, new Date()));
        }else {
        	parametros.put("FECHA_D", new Date());
        }
        return reporte;
    }

    public Boolean validarParametros() {
        Boolean rta = true;

        if (SysmanFunciones.validarVariableVacio(anoTrabajo)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB128"));
            rta = false;
        }
        if (SysmanFunciones.validarVariableVacio(mesTrabajo)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB129"));
            rta = false;
        }
        if (SysmanFunciones.validarVariableVacio(digitos)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB130"));
            rta = false;
        }
        if (SysmanFunciones.validarVariableVacio(cuentaInicial)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB131"));
            rta = false;
        }
        if (SysmanFunciones.validarVariableVacio(cuentaFinal)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB132"));
            rta = false;
        }

        if ("4".equals(claseImpresion) && centroCosto) {

            if (SysmanFunciones.validarVariableVacio(centroInicial)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB133"));
                rta = false;
            }
            if (SysmanFunciones.validarVariableVacio(centroFinal)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB134"));
                rta = false;
            }

        }
        return rta;
    }

    public void cambiarAnoTrabajo() {
        // <CODIGO_DESARROLLADO>
        cargarListaCuentaInicial();
        cuentaInicial = "0";
        cuentaFinal = SysmanConstantes.CONS_MAX_ID;
        cargarListaMesTrabajo();
        mesTrabajo = null;
        if (centroCosto) {
            centroInicial = null;
            centroFinal = null;
        }
        // </CODIGO_DESARROLLADO>
    }
    
    public void cambiarconFecha() {
    	
    }

    public void cambiarcentroCosto() {
        // <CODIGO_DESARROLLADO>
        centroInicial = "0";
        centroFinal = SysmanConstantes.CONS_CENTRO;
        cargarListaCmbCentroCInicial();
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = registroAux.getCampos()
                        .get(LibroMayoryBalancesControladorEnum.COD.getValue())
                        .toString();
        cargarListaCuentaFinal();
        cuentaFinal = null;
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = registroAux.getCampos()
                        .get(LibroMayoryBalancesControladorEnum.COD.getValue())
                        .toString();
    }

    public void seleccionarFilaCmbCentroCInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroInicial = registroAux.getCampos()
                        .get(LibroMayoryBalancesControladorEnum.COD.getValue())
                        .toString();
        cargarListaCmbCentroCFinal();
        centroFinal = null;
    }

    public void seleccionarFilaCmbCentroCFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        centroFinal = registroAux.getCampos()
                        .get(LibroMayoryBalancesControladorEnum.COD.getValue())
                        .toString();
    }

    public String getClaseImpresion() {
        return claseImpresion;
    }

    public void setClaseImpresion(String claseImpresion) {
        this.claseImpresion = claseImpresion;
    }

    public boolean isFecha() {
        return fecha;
    }

    public void setFecha(boolean fecha) {
        this.fecha = fecha;
    }

    public boolean isClaseCero() {
        return claseCero;
    }

    public void setClaseCero(boolean claseCero) {
        this.claseCero = claseCero;
    }

    public boolean isCentroCosto() {
        return centroCosto;
    }

    public void setCentroCosto(boolean centroCosto) {
        this.centroCosto = centroCosto;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
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

    public String getAnoTrabajo() {
        return anoTrabajo;
    }

    public void setAnoTrabajo(String anoTrabajo) {
        this.anoTrabajo = anoTrabajo;
    }

    public String getMesTrabajo() {
        return mesTrabajo;
    }

    public void setMesTrabajo(String mesTrabajo) {
        this.mesTrabajo = mesTrabajo;
    }

    public String getCodigoLibro() {
        return codigoLibro;
    }

    public void setCodigoLibro(String codigoLibro) {
        this.codigoLibro = codigoLibro;
    }

    public String getNumeroInicial() {
        return numeroInicial;
    }

    public void setNumeroInicial(String numeroInicial) {
        this.numeroInicial = numeroInicial;
    }

    public String getDigitos() {
        return digitos;
    }

    public void setDigitos(String digitos) {
        this.digitos = digitos;
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

    public RegistroDataModelImpl getListaCmbCentroCInicial() {
        return listaCmbCentroCInicial;
    }

    public void setListaCmbCentroCInicial(
        RegistroDataModelImpl listaCmbCentroCInicial) {
        this.listaCmbCentroCInicial = listaCmbCentroCInicial;
    }

    public RegistroDataModelImpl getListaCmbCentroCFinal() {
        return listaCmbCentroCFinal;
    }

    public void setListaCmbCentroCFinal(
        RegistroDataModelImpl listaCmbCentroCFinal) {
        this.listaCmbCentroCFinal = listaCmbCentroCFinal;
    }

    public boolean isFormatoEsp() {
        return formatoEsp;
    }

    public void setFormatoEsp(boolean formatoEsp) {
        this.formatoEsp = formatoEsp;
    }

	/**
	 * @return the conFecha
	 */
	public boolean isConFecha() {
		return conFecha;
	}

	/**
	 * @param conFecha the conFecha to set
	 */
	public void setConFecha(boolean conFecha) {
		this.conFecha = conFecha;
	}

	/**
	 * @return the fechaReporte
	 */
	public Date getFechaReporte() {
		return fechaReporte;
	}

	/**
	 * @param fechaReporte the fechaReporte to set
	 */
	public void setFechaReporte(Date fechaReporte) {
		this.fechaReporte = fechaReporte;
	}
	
    public boolean getValorNeto() {
		return valorNeto;
	}

	public void setValorNeto(boolean valorNeto) {
		this.valorNeto = valorNeto;
	}

    
}

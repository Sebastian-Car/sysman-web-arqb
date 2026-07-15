package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.BalancegeneralControladorEnum;
import com.sysman.contabilidad.enums.BalancegeneralControladorUrlEnum;
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

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author apineda
 * @version 1, 10/03/2016
 *
 * @version 1.1, Nov 2016 - Modificado por: sdaza. Se modifica las
 * consultas de los informes con el fin de unificar con la consulta
 * base 800046
 *
 *
 * Revision Sonar y Refactoring
 *
 * @author ybecerra
 * @version 2, 17/04/2017
 * 
 * @version 3.0, 12/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.<br>
 * Remover y/o reemplazar los llamados a ConectorPool por el esquema
 * definido.
 */
@ManagedBean
@ViewScoped
public class BalancegeneralControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private Boolean centrocosto;
    private Boolean tercero;
    private Boolean auxiliar;
    private Boolean referencia;
    private Boolean fteRecurso;
    private Boolean saldocero;
    private Boolean formato;
    private Boolean verificaExcel;
    private Boolean deducible;
    private Boolean formatoEspecialExcel;
    private Boolean compAnioAntAct;
    private String codInicial;
    private String codFinal;
    private Boolean tiporesumen;
    private Boolean paralelo;
    private String visible = "none";
    private int anotrabajo = SysmanFunciones.ano(new Date());
    private int mestrabajo = SysmanFunciones.mes(new Date()) + 1;
    private int mesanterior;
    private String digitos = "6";
    private String condicion;
    private String condicionSub;
    private String calidad;
    private String centroCInicial = "0";
    private String centroCFinal = "9999999999999999";
    private String comparativoAnio= "";
    private String comparativo;
    private RegistroDataModelImpl listaCodigoInicial;
    private RegistroDataModelImpl listaCodigoFinal;
    String cod;

    private StreamedContent archivoDescarga;
    private List<Registro> listaAnoTrabajo;
    private List<Registro> listacbCentroIni;
    private List<Registro> listacbCentroFin;
    private List<Registro> referenciainicial;
    private List<Registro> listacmbReferenciaI;
    private List<Registro> referenciafinal;
    private List<Registro> listacmbReferenciaF;
    
    

	@EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of BalancegeneralControlador
     */
    public BalancegeneralControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        codInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        codFinal = SysmanConstantes.DEFECTOFINAL_STRING;

        try {
            //2210
            numFormulario = GeneralCodigoFormaEnum.BALANCEGENERAL_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(BalancegeneralControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

	@PostConstruct
    public void init() {
        cargarListaANIO();
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaANIO() {
    	
    	Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		
		try {
			listaAnoTrabajo = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											BalancegeneralControladorUrlEnum.URL3837.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
    }

    public void cargarListaCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalancegeneralControladorUrlEnum.URL4143
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anotrabajo);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListaCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalancegeneralControladorUrlEnum.URL4737
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anotrabajo);
        param.put(BalancegeneralControladorEnum.PARAM0.getValue(),
                        codInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    public void cargarListacbCentroIni() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listacbCentroIni = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            BalancegeneralControladorUrlEnum.URL5361
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListacbCentroFin() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(BalancegeneralControladorEnum.PARAM2.getValue(),
                        centroCInicial);

        try {
            listacbCentroFin = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            BalancegeneralControladorUrlEnum.URL5748
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirImprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        generaInforme(ReportesBean.FORMATOS.PDF);

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirImprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.EXCEL97);

        // </CODIGO_DESARROLLADO>
    }

    private String cod() {

        cod = codFinal + "9";

        return cod;
    }

    public void generaInforme(ReportesBean.FORMATOS formato) {
        String informe = "";

        Map<String, Object> parametros = new HashMap<>();
        HashMap<String, Object> reemplazar = new HashMap<>();

        String titulo;
        condicion = " ";
        condicionSub = " ";

        try {
            calidad = ejbSysmanUtil.consultarParametro(compania,
                            "FORMATO CALIDAD", modulo, new Date(), true);

            titulo = titulo(calidad);

            reemplazar.put("mestrabajo", mestrabajo);
            reemplazar.put("anio", anotrabajo);
            reemplazar.put("codigoInicial", "'" + codInicial + "'");
            reemplazar.put("codigoFinal", "'" + cod() + "'");
            reemplazar.put("codigoFin", "'" + codFinal + "'");
            reemplazar.put("digitos", digitos);
            reemplazar.put("digitos1", digitos);
            reemplazos(reemplazar);
            reemplazar.put("manTer", tercero ? "1" : "0");
            reemplazar.put("manAux", auxiliar ? "1" : "0");
            reemplazar.put("manCen", centrocosto ? "1" : "0");
            reemplazar.put("manRef", referencia ? "1" : "0");
            reemplazar.put("manFue", fteRecurso ? "1" : "0");
            reemplazar.put("baseBalance", Reporteador.resuelveConsulta(
                            "800046BaseBalances",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar));

            if (seleccionado()) {
                reem(reemplazar);
                informe = informe(calidad, "1");
            }
            else {
                reem(reemplazar);
                informe = informe(calidad, "2");
            }

            Reporteador.resuelveConsulta(informe, Integer.parseInt(modulo),
                            reemplazar, parametros);

            String firmaCont1 = ejbSysmanUtil.consultarParametro(compania,
                            "FIRMA CONTABLE 1", modulo, new Date(), true);

            String cargoCont1 = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO CONTABLE 1", modulo, new Date(), true);

            String documentoCont1 = ejbSysmanUtil.consultarParametro(
                            compania, "DOCUMENTO CONTABLE 1", modulo,
                            new Date(), true);

            String firmaCont2 = ejbSysmanUtil.consultarParametro(compania,
                            "FIRMA CONTABLE 2", modulo, new Date(), true);

            String cargoCont2 = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO CONTABLE 2", modulo, new Date(), true);

            String documentoCont2 = ejbSysmanUtil.consultarParametro(
                            compania, "DOCUMENTO CONTABLE 2", modulo,
                            new Date(), true);

            String firmaCont3 = ejbSysmanUtil.consultarParametro(compania,
                            "FIRMA CONTABLE 3", modulo, new Date(), true);

            String cargoCont3 = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO CONTABLE 3", modulo, new Date(), true);

            String documentoCont3 = ejbSysmanUtil.consultarParametro(
                            compania, "DOCUMENTO CONTABLE 3", modulo,
                            new Date(), true);

            parametros.put("PR_TITULO", titulo);
            parametros.put("PR_FIRMA_CONTABLE_1", firmaCont1);
            parametros.put("PR_FIRMA_CONTABLE_2", firmaCont2);
            parametros.put("PR_CARGO_CONTABLE_1", cargoCont1);
            parametros.put("PR_CARGO_CONTABLE_2", cargoCont2);
            parametros.put("PR_DOCUMENTO_CONTABLE_1", documentoCont1);
            parametros.put("PR_DOCUMENTO_CONTABLE_2", documentoCont2);

            String firmaTres = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(
                                            compania, "GENERA FIRMA CONTABLE 3",
                                            modulo,
                                            new Date(), true), "NO");

            if ("SI".equals(firmaTres)) {
                parametros.put("PR_FIRMA_CONTABLE_3", firmaCont3);
                parametros.put("PR_CARGO_CONTABLE_3", cargoCont3);
                parametros.put("PR_DOCUMENTO_CONTABLE_3", documentoCont3);
            }

            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            parametros.put("PR_FORMATO_ESPECIAL_EXCEL", formatoEspecialExcel);

            archivoDescarga = JsfUtil.exportarStreamed(informe, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * se llama en el metodo generarInforme
     *
     * @param reemplazar
     */
    private void reemplazos(HashMap<String, Object> reemplazar) {
        if (saldocero) {
            reemplazar.put("saldoCero", " ");
            reemplazar.put("saldoCeroExt", " ");
        }
        else {
            reemplazar.put("saldoCero", " AND PLAN_CONTABLE.SALDO"
                + mestrabajo + "<> 0");
            reemplazar.put("saldoCeroExt",
                            " AND SALDO" + mestrabajo + "<> 0");
        }
    }

    private void reem(HashMap<String, Object> reemplazar) {
        if (saldocero) {
            reemplazar.put("condicion", " ");
        }
        else {
            reemplazar.put("condicion", "AND V_PLAN_CONTABLE.SALDO"
                + mestrabajo + " <> 0");
        }
    }

    private boolean seleccionado() {
        if (auxiliar || tercero || centrocosto) {
            return true;
        }
        if (referencia || fteRecurso) {
            return true;
        }
        return false;
    }

    /**
     * Retorna el titulo del reporte dependiendo de los valores
     * recibidos por parametro
     *
     * @param calidad
     * @return
     */
    private String titulo(String calidad) {
        String titulo;
        if ("SI".equals(calidad)) {
            titulo = "DEL MES DE "
                + (SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mestrabajo])
                                .toUpperCase()
                + " DE "
                + anotrabajo + "";
        }
        else {
            titulo = "BALANCE GENERAL DEL MES DE "
                + (SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mestrabajo])
                                .toUpperCase()
                + " DE " + anotrabajo
                + "";
        }
        return titulo;
    }

    /**
     * Retorna el nombre del reporte segun el valor del parametro
     * recibido
     *
     * @param calidad
     * @return
     */
    private String informe(String calidad, String opcion) {
        String informe = "";
        if ("1".equals(opcion)) {
            if ("SI".equals(calidad)) {
                informe = "000568BalanceGeneralCOS";
            }
            else {
                informe = "000570BalanceGeneral";
            }
        }
        if ("2".equals(opcion)) {
            if ("SI".equals(calidad)) {
                informe = "000578BalanceGeneralMECOS";
            }
            else {
                informe = "000579BalanceGeneralME";
            }
        }
        return informe;
    }

    public void cambiarANIO() {
        // <CODIGO_DESARROLLADO>
        codInicial = null;
        codFinal = null;
        cargarListaCodigoInicial();
        // </CODIGO_DESARROLLADO>
    }
    
    public void cambiarAnoTrabajo() {
        // <CODIGO_DESARROLLADO>
        codInicial = null;
        codFinal = null;
        cargarListaCodigoInicial();
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        codFinal = "";
        cargarListaCodigoFinal();
    }

    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
    }
    
    public void seleccionarFilaAnoTrabajo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        anotrabajo = (int) registroAux.getCampos()
                        .get(GeneralParameterEnum.ANO.getName());
    }

    public void cambiarcentroCosto() {
        // <CODIGO_DESARROLLADO>
        if (centrocosto) {
            cargarListacbCentroIni();
            cargarListacbCentroFin();
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarcbCentroIni() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public Boolean getCentrocosto() {
        return centrocosto;
    }

    public void setCentrocosto(Boolean centrocosto) {
        this.centrocosto = centrocosto;
    }

    public Boolean getTercero() {
        return tercero;
    }

    public void setTercero(Boolean tercero) {
        this.tercero = tercero;
    }

    public Boolean getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(Boolean auxiliar) {
        this.auxiliar = auxiliar;
    }

    public Boolean getReferencia() {
        return referencia;
    }

    public void setReferencia(Boolean referencia) {
        this.referencia = referencia;
    }

    public Boolean getFteRecurso() {
        return fteRecurso;
    }

    public void setFteRecurso(Boolean fteRecurso) {
        this.fteRecurso = fteRecurso;
    }

    public Boolean getSaldocero() {
        return saldocero;
    }

    public void setSaldocero(Boolean saldocero) {
        this.saldocero = saldocero;
    }

    public String getCalidad() {
        return calidad;
    }

    public void setCalidad(String calidad) {
        this.calidad = calidad;
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

    public int getAnotrabajo() {
        return anotrabajo;
    }

    public void setAnotrabajo(int anotrabajo) {
        this.anotrabajo = anotrabajo;
    }

    public String getDigitos() {
        return digitos;
    }

    public void setDigitos(String digitos) {
        this.digitos = digitos;
    }

    public RegistroDataModelImpl getListaCodigoInicial() {
        return listaCodigoInicial;
    }

    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }

    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial) {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    public String getCentroCInicial() {
        return centroCInicial;
    }

    public void setCentroCInicial(String centroCInicial) {
        this.centroCInicial = centroCInicial;
    }

    public String getCentroCFinal() {
        return centroCFinal;
    }

    public void setCentroCFinal(String centroCFinal) {
        this.centroCFinal = centroCFinal;
    }

    public RegistroDataModelImpl getListaCodigoFinal() {
        return listaCodigoFinal;
    }

    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
        this.listaCodigoFinal = listaCodigoFinal;
    }

    public int getMestrabajo() {
        return mestrabajo;
    }

    public void setMestrabajo(int mestrabajo) {
        this.mestrabajo = mestrabajo;
    }

    public int getMesanterior() {
        return mesanterior;
    }

    public void setMesanterior(int mesanterior) {
        this.mesanterior = mesanterior;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getCondicionSub() {
        return condicionSub;
    }

    public void setCondicionSub(String condicionSub) {
        this.condicionSub = condicionSub;
    }

  
    public List<Registro> getListaAnoTrabajo() {
		return listaAnoTrabajo;
	}

	public void setListaAnoTrabajo(List<Registro> listaAnoTrabajo) {
		this.listaAnoTrabajo = listaAnoTrabajo;
	}

	public List<Registro> getListacbCentroIni() {
        return listacbCentroIni;
    }

    public void setListacbCentroIni(List<Registro> listacbCentroIni) {
        this.listacbCentroIni = listacbCentroIni;
    }

    public List<Registro> getListacbCentroFin() {
        return listacbCentroFin;
    }

    public void setListacbCentroFin(List<Registro> listacbCentroFin) {
        this.listacbCentroFin = listacbCentroFin;
    }

	public Boolean getFormatoEspecialExcel() {
		return formatoEspecialExcel;
	}

	public void setFormatoEspecialExcel(Boolean formatoEspecialExcel) {
		this.formatoEspecialExcel = formatoEspecialExcel;
	}

	public Boolean getFormato() {
		return formato;
	}

	public void setFormato(Boolean formato) {
		this.formato = formato;
	}

	public Boolean getVerificaExcel() {
		return verificaExcel;
	}

	public void setVerificaExcel(Boolean verificaExcel) {
		this.verificaExcel = verificaExcel;
	}

	public Boolean getTiporesumen() {
		return tiporesumen;
	}

	public void setTiporesumen(Boolean tiporesumen) {
		this.tiporesumen = tiporesumen;
	}

	public List<Registro> getReferenciainicial() {
		return referenciainicial;
	}

	public void setReferenciainicial(List<Registro> referenciainicial) {
		this.referenciainicial = referenciainicial;
	}

	public List<Registro> getListacmbReferenciaI() {
		return listacmbReferenciaI;
	}

	public void setListacmbReferenciaI(List<Registro> listacmbReferenciaI) {
		this.listacmbReferenciaI = listacmbReferenciaI;
	}

	public Boolean getDeducible() {
		return deducible;
	}

	public void setDeducible(Boolean deducible) {
		this.deducible = deducible;
	}

	public Boolean getParalelo() {
		return paralelo;
	}

	public void setParalelo(Boolean paralelo) {
		this.paralelo = paralelo;
	}

	public String getVisible() {
		return visible;
	}

	public void setVisible(String visible) {
		this.visible = visible;
	}

	public String getComparativoAnio() {
		return comparativoAnio;
	}

	public void setComparativoAnio(String comparativoAnio) {
		this.comparativoAnio = comparativoAnio;
	}

	public Boolean getCompAnioAntAct() {
		return compAnioAntAct;
	}

	public void setCompAnioAntAct(Boolean compAnioAntAct) {
		this.compAnioAntAct = compAnioAntAct;
	}

	public List<Registro> getListacmbReferenciaF() {
		return listacmbReferenciaF;
	}

	public void setListacmbReferenciaF(List<Registro> listacmbReferenciaF) {
		this.listacmbReferenciaF = listacmbReferenciaF;
	}

	/**
	 * @return the comparativo
	 */
	public String getComparativo() {
		return comparativo;
	}

	/**
	 * @param comparativo the comparativo to set
	 */
	public void setComparativo(String comparativo) {
		this.comparativo = comparativo;
	}

    public List<Registro> getReferenciafinal() {
		return referenciafinal;
	}

	public void setReferenciafinal(List<Registro> referenciafinal) {
		this.referenciafinal = referenciafinal;
	}
	
	

}

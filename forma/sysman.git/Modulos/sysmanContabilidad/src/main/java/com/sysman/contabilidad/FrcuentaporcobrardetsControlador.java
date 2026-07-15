/*-
 * FrcuentaporcobrardetsControlador.java
 *
 * 1.0
 * 
 * 28/04/2025
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.util.SysmanConstantes;
import com.sysman.contabilidad.enums.AnalisiscarteracxcControladorEnum;
import com.sysman.contabilidad.enums.AnalisiscarteracxcControladorUrlEnum;
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
import java.text.ParseException;
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
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 28/04/2025
 * @author User
 */
@ManagedBean
@ViewScoped
public class FrcuentaporcobrardetsControlador extends BeanBaseModal {
	private final String compania;
	// <DECLARAR_ATRIBUTOS>
	private String cuentaInicial;
	private String cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;;
	private String terceroInicial;
	private String terceroFinal = SysmanConstantes.DEFECTOFINAL_STRING;
	private String nombreCuentaInicial;

	private String nombreCuentaFinal;

	private String nombreTerceroInicial;

	private String nombreTerceroFinal;

	private Date fechaCorte;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listaComboCuentaInicial;
	private RegistroDataModelImpl listaComboCuentaFinal;
	private RegistroDataModelImpl listaTerceroInicial;
	private RegistroDataModelImpl listaComboTerceroFinal;
	private int ano;
	private Boolean terceroVisible;
	private Boolean consolidado;
	private Boolean porTercero = false;
	private Boolean detallado = false;
	private Boolean agrupado = false;
	private Boolean mostrarFecha = false;
	private Boolean conFecha = false;
	private String reporte;
	private Boolean rangosVencimiento = false;
	
	@EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
	// </DECLARAR_LISTAS_COMBO_GRANDE>

	public FrcuentaporcobrardetsControlador() {
		super();
		compania = SessionUtil.getCompania();
		fechaCorte = new Date();
		ano = SysmanFunciones.ano(fechaCorte);
		try {
			numFormulario = GeneralCodigoFormaEnum.FRCUENTAPORCOBRARDETSCONTROLADOR.getCodigo();
			validarPermisos();
		} catch (Exception ex) {
			Logger.getLogger(FrcuentaporcobrardetsControlador.class.getName()).log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
		}
	}

	@PostConstruct
	public void inicializar() {
		cargarListaTerceroInicial();
		cargarListaComboCuentaInicial();
		cargarListaComboCuentaFinal();
		abrirFormulario();
	}

	@Override
	public void abrirFormulario() {
		terceroVisible = false;
		
		try {
			mostrarFecha = "SI".equals(SysmanFunciones
						.nvl(ejbSysmanUtil.consultarParametro(compania, "MOSTRAR FECHA EN REPORTE CARTERA POR TERCERO",
								SessionUtil.getModulo(), new Date(), true), "NO"));
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void cargarListaComboCuentaInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(AnalisiscarteracxcControladorUrlEnum.URL16209.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);

		listaComboCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());

	}

	public void cargarListaComboCuentaFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(AnalisiscarteracxcControladorUrlEnum.URL16207.getValue());

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(AnalisiscarteracxcControladorEnum.PARAM0.getValue(), cuentaInicial);

		listaComboCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaTerceroInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(AnalisiscarteracxcControladorUrlEnum.URL2759.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"NIT");
	}

	public void cargarListaComboTerceroFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(AnalisiscarteracxcControladorUrlEnum.URL3332.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), String.valueOf(compania));
		param.put(AnalisiscarteracxcControladorEnum.TERCEROINICIAL.getValue(), terceroInicial);

		listaComboTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
				true, "NIT");
	}

	public void genInforme(ReportesBean.FORMATOS formato) {
		archivoDescarga = null;
		String consulta;
		// Antes de generar el informe se ejecuta la rutina:
		// RevisarAfectacionesCarteraAFechaXTERCEROCXC
		try {
			HashMap<String, Object> reemplazar = new HashMap<>();
			reemplazar.put("fechaCorte", SysmanFunciones.convertirAFechaCadena(fechaCorte));
			reemplazar.put("terceroInicial", SysmanFunciones.nvl(terceroInicial, " ").toString());
			reemplazar.put("terceroFinal", terceroFinal);
			// reemplazar.put("porVencimiento", porVencimiento
			// ? "COMPROBANTE_CNT.FECHA_VCN_DOC" : "DETALLE_COMPROBANTE_CNT.FECHA");
			reemplazar.put("cuentaInicial", SysmanFunciones.nvl(cuentaInicial, " ").toString());
			reemplazar.put("cuentaFinal", cuentaFinal);

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("PR_FORMS_ANALISISCARTERACXC_TERCEROINICIAL", nombreTerceroInicial);
			parametros.put("PR_FORMS_ANALISISCARTERACXC_TERCEROFINAL", nombreTerceroFinal);
			parametros.put("PR_FORMS_ANALISISCARTERACXC_FECHACORTE", SysmanFunciones.convertirAFechaCadena(fechaCorte));
			parametros.put("PR_FECHACORTE", SysmanFunciones.convertirAFechaCadena(fechaCorte,"EEEEE, d 'de' MMMMM 'de' yyyy"));
			parametros.put("PR_NOMBREMES", SysmanFunciones.convertirAFechaCadena(fechaCorte,"MMMMM").toUpperCase());
			parametros.put("PR_ANO", SysmanFunciones.convertirAFechaCadena(fechaCorte,"yyyy"));
			parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso().getNombre());
			

			parametros.put("PR_EXCEL", formato.equals(FORMATOS.EXCEL) ? false : true);

			// String reporte = consolidado ? "002678AnalisisCarteraCXC_Consolidado" :
			// "000748AnalisisCarteraCXCFechavcntoxtercero";

//			if (!detallado && !porTercero && !agrupado) {
//				JsfUtil.agregarMensajeAlertaDialogo(
//						"Por favor, Seleccione alguna Indicador para generar el informe");
//				return false;
//			}
			if (detallado) {
				if(rangosVencimiento) {
					reporte = "002843AnalisisCarteraRVDetallado";
				} else {
					reporte = "002771AnalisisCarteraCXCDetallado";
				}
			} else if (porTercero) {
				if(rangosVencimiento) 
				{
					if(conFecha && mostrarFecha)
					{
						reporte = "002924AnalisisCarteraRVTerceroConFecha";
					}
					else
					{
						reporte = "002842AnalisisCarteraRVTercero";					
					}
					
				} else {
					reporte = "002776AnalisisCarteraCXCTercero";
				}
			} else if (agrupado) {
				if(rangosVencimiento) {
					reporte = "002841AnalisisCarteraRVAgrupado";
				} else {
					reporte = "002777AnalisisCarteraCXCAgrupado";
				}
			}else {
				reporte = "002771AnalisisCarteraCXCDetallado";
			}
			
			if ("002771AnalisisCarteraCXCDetallado".equalsIgnoreCase(reporte) && ReportesBean.FORMATOS.EXCEL == formato) {
				
				reporte = "002771AnalisisCarteraCXCDetalladoxls";
				consulta = "002771AnalisisCarteraCXCDetallado";
			}else{
				
				consulta = reporte;
			}
			
			Reporteador.resuelveConsulta(consulta, Integer.parseInt(SessionUtil.getModulo()), reemplazar, parametros);

			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);

		} catch (OutOfMemoryError | JRException | IOException | ParseException | SysmanException
				| NumberFormatException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	public void oprimirImprimir() {
		genInforme(ReportesBean.FORMATOS.PDF);
	}

	public void oprimirExcel() {
		genInforme(ReportesBean.FORMATOS.EXCEL);
	}	
	/**
     * Metodo ejecutado al cambiar el control ckdetallado
     * 
     * 
     */
	public void cambiarckdetallado() {}
    /**
     * Metodo ejecutado al cambiar el control agrupado
     * 
     * 
     */
	public void cambiaragrupado() {}
    /**
     * Metodo ejecutado al cambiar el control porTercero
     * 
     * 
     */
	public void cambiarporTercero() {}

	public void seleccionarFilaComboCuentaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaInicial = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), "").toString();
		nombreCuentaInicial = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRE"), "").toString();
		cuentaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		cargarListaComboCuentaFinal();
	}

	public void seleccionarFilaComboCuentaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaFinal = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), "").toString();
		nombreCuentaFinal = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRE"), "").toString();
	}

	public void seleccionarFilaTerceroInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		terceroInicial = SysmanFunciones.nvl(registroAux.getCampos().get("NIT"), "").toString();
		nombreTerceroInicial = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRE"), "").toString();
		terceroFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		nombreTerceroFinal = null;
		cargarListaComboTerceroFinal();
	}

	public void seleccionarFilaComboTerceroFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		terceroFinal = SysmanFunciones.nvl(registroAux.getCampos().get("NIT"), "").toString();
		nombreTerceroFinal = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRE"), "").toString();
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

	public String getNombreCuentaInicial() {
		return nombreCuentaInicial;
	}

	public void setNombreCuentaInicial(String nombreCuentaInicial) {
		this.nombreCuentaInicial = nombreCuentaInicial;
	}

	public String getNombreCuentaFinal() {
		return nombreCuentaFinal;
	}

	public void setNombreCuentaFinal(String nombreCuentaFinal) {
		this.nombreCuentaFinal = nombreCuentaFinal;
	}

	public String getNombreTerceroInicial() {
		return nombreTerceroInicial;
	}

	public void setNombreTerceroInicial(String nombreTerceroInicial) {
		this.nombreTerceroInicial = nombreTerceroInicial;
	}

	public String getNombreTerceroFinal() {
		return nombreTerceroFinal;
	}

	public void setNombreTerceroFinal(String nombreTerceroFinal) {
		this.nombreTerceroFinal = nombreTerceroFinal;
	}

	public Date getFechaCorte() {
		return fechaCorte;
	}

	public void setFechaCorte(Date fechaCorte) {
		this.fechaCorte = fechaCorte;
	}

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public RegistroDataModelImpl getlistaComboCuentaInicial() {
		return listaComboCuentaInicial;
	}

	public void setListaComboCuentaInicial(RegistroDataModelImpl listaComboCuentaInicial) {
		this.listaComboCuentaInicial = listaComboCuentaInicial;
	}

	public RegistroDataModelImpl getListaComboCuentaFinal() {
		return listaComboCuentaFinal;
	}

	public void setListaComboCuentaFinal(RegistroDataModelImpl listaComboCuentaFinal) {
		this.listaComboCuentaFinal = listaComboCuentaFinal;
	}

	public RegistroDataModelImpl getListaTerceroInicial() {
		return listaTerceroInicial;
	}

	public void setListaTerceroInicial(RegistroDataModelImpl listaTerceroInicial) {
		this.listaTerceroInicial = listaTerceroInicial;
	}

	public RegistroDataModelImpl getListaComboTerceroFinal() {
		return listaComboTerceroFinal;
	}

	public void setListaComboTerceroFinal(RegistroDataModelImpl listaComboTerceroFinal) {
		this.listaComboTerceroFinal = listaComboTerceroFinal;
	}

	public Boolean getTerceroVisible() {
		return terceroVisible;
	}

	public void setTerceroVisible(Boolean terceroVisible) {
		this.terceroVisible = terceroVisible;
	}

	public Boolean getConsolidado() {
		return consolidado;
	}

	public void setConsolidado(Boolean consolidado) {
		this.consolidado = consolidado;
	}

	public Boolean getDetallado() {
		return detallado;
	}

	public void setDetallado(Boolean detallado) {
		this.detallado = detallado;
	}

	public Boolean getAgrupado() {
		return agrupado;
	}

	public void setAgrupado(Boolean agrupado) {
		this.agrupado = agrupado;
	}

	public Boolean getPorTercero() {
		return porTercero;
	}

	public void setPorTercero(Boolean porTercero) {
		this.porTercero = porTercero;
	}

	public Boolean getRangosVencimiento() {
        return rangosVencimiento;
    }
	
	public void setRangosVencimiento(Boolean rangosVencimiento) {
        this.rangosVencimiento = rangosVencimiento;
    }

	public Boolean getMostrarFecha() {
		return mostrarFecha;
	}

	public void setMostrarFecha(Boolean mostrarFecha) {
		this.mostrarFecha = mostrarFecha;
	}

	public Boolean getConFecha() {
		return conFecha;
	}

	public void setConFecha(Boolean conFecha) {
		this.conFecha = conFecha;
	}

	// </SET_GET_LISTAS_COMBO_GRANDE>
}

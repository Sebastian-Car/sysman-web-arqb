package com.sysman.contabilidad;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.BalanceSaldosInicialesControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author sdaza
 * @version 1, 04/05/2016
 * 
 * @version 2, 06/04/2017, pespitia : <br>
 *          Se aplicaron las recomendaciones de SonarLint.<br>
 *          Se aplico refactoring.
 * 
 * @version 3, 07/03/2018. fperez: <br>
 *          Se añadió botón para imprimir reporte en Excel.
 */
@ManagedBean
@ViewScoped
public class BalanceSaldosInicialesControlador extends BeanBaseModal {
	private final String compania;
	private String modulo;
	private StreamedContent archivoDescarga;
	private String anoTrabajo;
	private List<Registro> listaAno;

	/**
	 * Creates a new instance of BalanceSaldosInicialesControlador
	 */
	public BalanceSaldosInicialesControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			// Número de formulario 683.
			numFormulario = GeneralCodigoFormaEnum.BALANCE_SALDOS_INICIALES_CONTROLADOR.getCodigo();
			validarPermisos();
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	@PostConstruct
	public void init() {
		cargarListaAno();
		abrirFormulario();
	}

	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		anoTrabajo = String.valueOf(SysmanFunciones.ano(new Date()));
		// </CODIGO_DESARROLLADO>
	}

	public void cargarListaAno() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listaAno = RegistroConverter
					.toListRegistro(requestManager.getList(
							UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(
											BalanceSaldosInicialesControladorUrlEnum.URL2511.getValue())
									.getUrl(),
							param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton generarPdf en la vista.
	 *
	 */
	public void oprimirgenerarPdf() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generaInforme(ReportesBean.FORMATOS.PDF);
		// </CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al oprimir el boton generarExcel en la vista.
	 *
	 */
	public void oprimirgenerarExcel() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		generaInforme(ReportesBean.FORMATOS.EXCEL);
		// </CODIGO_DESARROLLADO>
	}

	public void generaInforme(ReportesBean.FORMATOS formato) {

		Map<String, Object> reemplazar = new HashMap<>();
		Map<String, Object> parametros = new HashMap<>();

		String reporte = "000733BalanceSaldosIniciales";

		archivoDescarga = null;

		reemplazar.put("compania", compania);
		reemplazar.put("anoTrabajo", anoTrabajo);

		// </REEMPLAZAR VARIABLES EN CONSULTA>
		try {
			// <ENVIAR PARAMETROS AL REPORTE>
			parametros.put("PR_TITULO", "BALANCE INICIAL DE " + anoTrabajo);
			parametros.put("PR_COMPANIA", compania);
			// </ENVIAR PARAMETROS AL REPORTE>

			Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazar, parametros);

			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
		} catch (JRException | IOException | SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public List<Registro> getListaAno() {
		return listaAno;
	}

	public void setListaAno(List<Registro> listaAno) {
		this.listaAno = listaAno;
	}

	public String getAnoTrabajo() {
		return anoTrabajo;
	}

	public void setAnoTrabajo(String anoTrabajo) {
		this.anoTrabajo = anoTrabajo;
	}

}

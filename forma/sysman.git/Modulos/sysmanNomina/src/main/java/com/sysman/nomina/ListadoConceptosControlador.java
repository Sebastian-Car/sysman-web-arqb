package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.nomina.ejb.EjbNominaCeroRemote;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ngomez
 * @version 1, 31/07/2015
 * @modified spina 23/03/2017 Depuracion sonar - se eliminan parametros innecesarios en los metodo oprimir*
 *
 * @author asana
 * @version 2, 09,11/10/2017 sSe realiza refactoring de controlador
 */
@ManagedBean
@ViewScoped
public class ListadoConceptosControlador extends BeanBaseModal {

	private final String compania;
	private String opcion;

	private StreamedContent archivoDescarga;

	@EJB
	private EjbNominaCeroRemote ejbNominaCeroRemote;

	/**
	 * Creates a new instance of ListadoConceptosControlador
	 */
	public ListadoConceptosControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = GeneralCodigoFormaEnum.LISTADO_CONCEPTOS_CONTROLADOR.getCodigo();
			validarPermisos();
		}
		catch (Exception ex) {
			Logger.getLogger(ListadoConceptosControlador.class.getName())
			.log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	@PostConstruct
	public void inicializar() {
		abrirFormulario();
	}

	public void oprimirPreliminar() {
		// <CODIGO_DESARROLLADO>
		genInforme(FORMATOS.PDF);
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirExcel() {
		// <CODIGO_DESARROLLADO>
		genInforme(FORMATOS.EXCEL);
		// </CODIGO_DESARROLLADO>
	}

	private void genInforme(FORMATOS formato) {
		archivoDescarga = null;
		try {

			Map<String, Object> parametros = new HashMap<>();

			// MANEJO DE PARAMETROS DEL REPORTE
			HashMap<String, Object> reemplazar = new HashMap<>();
			String reporte;

			if ("2".equals(opcion)) {
				reemplazar.put("orden", "NOMBRE_CONCEPTO");
				reporte = "000136ConceptosAlfabeticos";
			}
			else {
				reemplazar.put("orden", "ID_DE_CONCEPTO");
				reporte = "000138Conceptos";
			}

			if(opcion.equals("1") && formato.equals(FORMATOS.EXCEL)) {
				String sql= Reporteador.resuelveConsulta(reporte,
						Integer.parseInt(SessionUtil.getModulo()),
						reemplazar);
				archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql, ConectorPool.ESQUEMA_SYSMAN, formato, reporte);
				
			}else {
				String strSql = Reporteador.resuelveConsulta(reporte,
						Integer.parseInt(SessionUtil.getModulo()),
						reemplazar);
				String nombreEmpresa = ejbNominaCeroRemote.getDatoEmpresa(compania, 10);
				parametros.put("PR_STRSQL", strSql);
				parametros.put("PR_NOMBREEMPRESA", nombreEmpresa.toUpperCase());

				archivoDescarga = JsfUtil.exportarStreamed(
						reporte, parametros,
						ConectorPool.ESQUEMA_SYSMAN, formato);
			}


		}
		catch (FileNotFoundException ex) {
			JsfUtil.agregarMensajeInformativo(
					SysmanFunciones.concatenar(idioma.getString(Constantes.MSM_INFORME_NO_EXISTE), " ", ex.getMessage()));
			Logger.getLogger(ListadoConceptosControlador.class.getName())
			.log(Level.SEVERE, null, ex);

		}
		catch (JRException | IOException | SystemException ex) {
			JsfUtil.agregarMensajeError(
					SysmanFunciones.concatenar(idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA), " ", ex.getMessage()));
			Logger.getLogger(ListadoConceptosControlador.class.getName())
			.log(Level.SEVERE, null, ex);
		}
		catch (SysmanException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		opcion = "1";
		// </CODIGO_DESARROLLADO>
	}

	public String getOpcion() {
		return opcion;
	}

	public void setOpcion(String opcion) {
		this.opcion = opcion;
	}

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}
}

package com.sysman.almacen;

import com.sysman.almacen.enums.ListadoGeneralPlacasEnum;
import com.sysman.almacen.enums.ListadoGeneralPlacasUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;

import java.io.IOException;
import java.sql.SQLException;
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

import org.omg.CosCollection.EqualityKeySortedCollectionIRHelper;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dsuesca
 * @version 1, 03/02/2016
 * @modifier amonroy
 * @version 2, 04/05/2017 Proceso de Refactoring
 */
@ManagedBean
@ViewScoped
public class ListadoGeneralPlacas extends BeanBaseModal {

	private final String compania;
	/**
	 * Constante que almacena el codigo que identifica el modulo de
	 * Almacen
	 */
	private final String modulo;
	/** Constante a nivel de clase que aloja el valor SERIE */
	private final String serie;

	private String orden;
	private String elemntoDesde;
	private String elemntoHasta;
	private String nombreElementoInicial;
	private String nombreElementoFinal;
	private StreamedContent archivoDescarga;
	private RegistroDataModelImpl listacmbElementoDesde;
	private RegistroDataModelImpl listacmbElementoHasta;
	private boolean ckExcelPlano;
	private boolean visibleExcel; 
	
	
	@EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
	private String listadoCuenta;
	/**
	 * Creates a new instance of ListadoGeneralPlacas
	 */
	public ListadoGeneralPlacas() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		serie = "SERIE";
		ckExcelPlano = false;
		try {
			numFormulario = GeneralCodigoFormaEnum.LISTADO_GENERAL_PLACAS
					.getCodigo();
			validarPermisos();
		}
		catch (Exception ex) {
			Logger.getLogger(ListadoGeneralPlacas.class.getName())
			.log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		}
		orden = "DEVOLUTIVO.SERIE";
	}

	@PostConstruct
	public void inicializar() {
		cargarListacmbElementoDesde();
		abrirFormulario();
	}

	public void cargarListacmbElementoDesde() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						ListadoGeneralPlacasUrlEnum.URL2591
						.getValue());
		listacmbElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, serie);
	}

	public void cargarListacmbElementoHasta() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(ListadoGeneralPlacasEnum.PARAM0.getValue(), elemntoDesde);
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						ListadoGeneralPlacasUrlEnum.URL2901
						.getValue());
		listacmbElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, serie);
	}

	public void oprimircmdPantalla() {
		getInforme(FORMATOS.PDF);
	}

	public void oprimirexcel() {
		getInforme(FORMATOS.EXCEL);
	}

	public void getInforme(FORMATOS formato) {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;

		String txtOrden;

		switch (orden) {
		case "TERCERO.NOMBRE":
			txtOrden = "ORDENADO POR EMPLEADO";
			break;
		case "SUBSTR(CODIGOELEMENTO,0,PCK_SYSMAN_UTL.FC_PAR(UN_COMPANIA => INVENTARIO.COMPANIA,UN_NOMBRE => 'DIGITOS AGRUPACION INVENTARIO',UN_MODULO => PCK_DATOS.FC_MODULOALMACEN,UN_FECHA_PAR => SYSDATE))":
			txtOrden = "ORDENADO POR GRUPO";
			break;
		case "DEVOLUTIVO.VALOR":
			txtOrden = "ORDENADO POR VALOR";
			break;
		case "ESTADODEVOLUTIVO.DESCRIPCION":
			txtOrden = "ORDENADO POR ESTADO";
			break;
		case "DEPENDENCIA.NOMBRE":
			txtOrden = "ORDENADO POR DEPENDENCIA";
			break;
		case "DEVOLUTIVO.UBICACION":
			txtOrden = "ORDENADO POR UBICACIÓN";
			break;
		case "IDENTIFICACION":
			txtOrden = "CON IDENTIFICACIÓN";
			break;
		default:
			txtOrden = "ORDENADO POR PLACA";
			break;
		}

		try {
	        boolean porIdentificacion = "IDENTIFICACION".equals(orden);
	        boolean porUbicacion = "DEVOLUTIVO.UBICACION".equals(orden);      

	        String informe = porIdentificacion 	? "002775ListadoPorPlacasIden"
	        				:porUbicacion 		? "002953ListadoPorPlacas" 
	        						 			: "000512ListadoPorPlacas";
	       

			// PARAMETROS DE REEMPLAZO EN LA CONSULTA
			Map<String, Object> reemplazar = new HashMap<>();
			reemplazar.put("serieDesde", elemntoDesde);
			reemplazar.put("serieHasta", elemntoHasta);
			reemplazar.put("orden", orden);
			// PARAMETROS PARA GENERACION DE INFORME
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("PR_TXTORDEN", txtOrden);

			if(ckExcelPlano) {

				String reporte;
	            if ("SI".equals(listadoCuenta)) {
	                reporte = porIdentificacion ? "800708ListadoPorPlacasIdenCuenta" 
	                		 :porUbicacion		? "800733ListadoPorPlacas" 
	                				 			: "800668ListadoPorPlacas";
	            } else {
	                reporte = porIdentificacion ? "800707ListadoPorPlacasIden" 
	                		 :porUbicacion		? "800734ListadoPorPlacas"
	                				 			: "800590ListadoPorPlacas";
	            }

				String sql = Reporteador.resuelveConsulta(reporte,
						Integer.parseInt(modulo), reemplazar);

				archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql, ConectorPool.ESQUEMA_SYSMAN, formato, reporte);


			}else {

				Reporteador.resuelveConsulta(informe,
						Integer.parseInt(modulo), reemplazar,
						parametros);

				archivoDescarga = JsfUtil.exportarStreamed(
						informe,
						parametros,
						ConectorPool.ESQUEMA_SYSMAN,
						formato);
			}
		}
		catch (JRException | IOException | SysmanException | SQLException | DRException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		// </CODIGO_DESARROLLADO>
	}

	public void seleccionarFilacmbElementoDesde(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		elemntoDesde = registroAux.getCampos().get(serie).toString();
		nombreElementoInicial = registroAux.getCampos().get("ESPECIFICACION")
				.toString();
		elemntoHasta = null;
		nombreElementoFinal = null;
		cargarListacmbElementoHasta();
	}

	public void seleccionarFilacmbElementoHasta(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		elemntoHasta = registroAux.getCampos().get(serie).toString();
		nombreElementoFinal = registroAux.getCampos().get("ESPECIFICACION")
				.toString();
	}

	/**
	 * Metodo ejecutado al cambiar el control ckExcelPlano
	 * 
	 * 
	 */
	public void cambiarckExcelPlano() {
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	
	private String consultarParametro(String nombre, boolean mayus)
			throws SystemException {
		return ejbSysmanUtil.consultarParametro(compania, nombre, modulo,
				new Date(), mayus);
	}
	

	public String getOrden() {
		return orden;
	}

	public void setOrden(String orden) {
		this.orden = orden;
	}

	public String getElemntoDesde() {
		return elemntoDesde;
	}

	public void setElemntoDesde(String elemntoDesde) {
		this.elemntoDesde = elemntoDesde;
	}

	public String getElemntoHasta() {
		return elemntoHasta;
	}

	public void setElemntoHasta(String elemntoHasta) {
		this.elemntoHasta = elemntoHasta;
	}

	public String getNombreElementoInicial() {
		return nombreElementoInicial;
	}

	public void setNombreElementoInicial(String nombreElementoInicial) {
		this.nombreElementoInicial = nombreElementoInicial;
	}

	public String getNombreElementoFinal() {
		return nombreElementoFinal;
	}

	public void setNombreElementoFinal(String nombreElementoFinal) {
		this.nombreElementoFinal = nombreElementoFinal;
	}

	/**
	 * @return the ckExcelPlano
	 */
	public boolean isCkExcelPlano() {
		return ckExcelPlano;
	}

	/**
	 * @param ckExcelPlano the ckExcelPlano to set
	 */
	public void setCkExcelPlano(boolean ckExcelPlano) {
		this.ckExcelPlano = ckExcelPlano;
	}

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public RegistroDataModelImpl getListacmbElementoDesde() {
		return listacmbElementoDesde;
	}

	public void setListacmbElementoDesde(
			RegistroDataModelImpl listacmbElementoDesde) {
		this.listacmbElementoDesde = listacmbElementoDesde;
	}

	public RegistroDataModelImpl getListacmbElementoHasta() {
		return listacmbElementoHasta;
	}

	public void setListacmbElementoHasta(
			RegistroDataModelImpl listacmbElementoHasta) {
		this.listacmbElementoHasta = listacmbElementoHasta;
	}

	/**
	 * @return the visibleExcel
	 */
	public boolean isVisibleExcel() {
		return visibleExcel;
	}

	/**
	 * @param visibleExcel the visibleExcel to set
	 */
	public void setVisibleExcel(boolean visibleExcel) {
		this.visibleExcel = visibleExcel;
	}

	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		try {
			String valorParametro = consultarParametro(
					"INFORME ALMACEN EXCEL PLANO", false);

			visibleExcel = valorParametro.equals("SI")?true:false;
			
			listadoCuenta = consultarParametro(
					"LISTADO GENERAL POR PLACA CON CUENTA CONTABLE", false);

		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}
	
	public boolean getMostrarUbicacionFisica () {
		try {
			return "SI".equalsIgnoreCase(consultarParametro("MANEJA UBICACION FISICA DEVOLUTIVO", false));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		return false;
	}

}

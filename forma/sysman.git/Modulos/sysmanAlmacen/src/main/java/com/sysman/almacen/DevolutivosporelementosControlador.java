package com.sysman.almacen;

import com.sysman.almacen.enums.DevolutivosporelementosControladorEnum;
import com.sysman.almacen.enums.DevolutivosporelementosControladorUrlEnum;
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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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
 * @version 1, 12/11/2015
 * @author yrojas
 * @version 2, 27/04/2017 Se cambiaron las consultas por la invocacion de los
 *          DSS. Se modifico controlador segun especificaciones del SonarLint.
 *          
 * @author asana
 * @version 3, 12/06/2017 se implementa enum en formulario.
 * y de modifica conexion.
 */
@ManagedBean
@ViewScoped
public class DevolutivosporelementosControlador extends BeanBaseModal {

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;
	
	private String compania;

	/**
	 * Constante a nivel de clase que aloja el valor CODIGOELEMENTO
	 */
	private String codigoElemento;

	/**
	 * Constante a nivel de clase que aloja el valor
	 * MSM_INFORME_NO_EXISTE_APLICACION
	 */
	private String mensaje1;
	private String modulo;
	private String elementoDesde;
	private String elementoHasta;
	private String nombreElementoD;
	private String nombreElementoH;
	private StreamedContent archivoDescarga;
	private RegistroDataModelImpl listaelementoDesde;
	private RegistroDataModelImpl listaelementoHasta;

	/**
	 * Creates a new instance of DevolutivosporelementosControlador
	 */
	public DevolutivosporelementosControlador() {
		super();
		try {
			numFormulario = GeneralCodigoFormaEnum.DEVOLUTIVOSPORELEMENTOS_CONTROLADOR.getCodigo();
			compania = SessionUtil.getCompania();
			modulo = SessionUtil.getModulo();
			validarPermisos();

			codigoElemento = "CODIGOELEMENTO";
			mensaje1 = "MSM_INFORME_NO_EXISTE_APLICACION";
		} catch (Exception ex) {
			Logger.getLogger(DevolutivosporelementosControlador.class.getName()).log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	@PostConstruct
	public void inicializar() {
		cargarListaelementoDesde();
		abrirFormulario();
	}

	@Override
	public void abrirFormulario() {
		//NO ESTA IMPLEMENTADO
	}

	public void cargarListaelementoDesde() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(DevolutivosporelementosControladorUrlEnum.URL3983.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaelementoDesde = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				codigoElemento);
	}

	public void cargarListaelementoHasta() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(DevolutivosporelementosControladorUrlEnum.URL4970.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(DevolutivosporelementosControladorEnum.PARAM0.getValue(), elementoDesde);

		listaelementoHasta = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				codigoElemento);
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
		generarInforme(ReportesBean.FORMATOS.EXCEL);
		// </CODIGO_DESARROLLADO>
	}

	public void generarInforme(ReportesBean.FORMATOS formato) {

	    try {
			HashMap<String, Object> remplazar = new HashMap<>();

			remplazar.put("elementoDesde", elementoDesde);
			remplazar.put("elementoHasta", elementoHasta);

			String consulta = Reporteador.resuelveConsulta("000378DevolutivosPorelementos", Integer.parseInt(modulo),
					remplazar);

			HashMap<String, Object> parametros = new HashMap<>();

			parametros.put("PR_STRSQL", consulta);

			
                archivoDescarga = JsfUtil.exportarStreamed("000378DevolutivosPorelementos", 
                                parametros, 
                                ConectorPool.ESQUEMA_SYSMAN,formato);
            }
            catch (JRException | IOException | SysmanException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
	
	}

	public void seleccionarFilaelementoDesde(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		elementoDesde = SysmanFunciones.nvl(registroAux.getCampos().get(codigoElemento),"").toString();
		nombreElementoD = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRELARGO"),"").toString();
		cargarListaelementoHasta();
	}

	public void seleccionarFilaelementoHasta(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		elementoHasta = SysmanFunciones.nvl(registroAux.getCampos().get(codigoElemento),"").toString();
		nombreElementoH = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRELARGO"),"").toString();
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

	public String getNombreElementoD() {
		return nombreElementoD;
	}

	public void setNombreElementoD(String nombreElementoD) {
		this.nombreElementoD = nombreElementoD;
	}

	public String getNombreElementoH() {
		return nombreElementoH;
	}

	public void setNombreElementoH(String nombreElementoH) {
		this.nombreElementoH = nombreElementoH;
	}

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public RegistroDataModelImpl getListaelementoDesde() {
		return listaelementoDesde;
	}

	public void setListaelementoDesde(RegistroDataModelImpl listaelementoDesde) {
		this.listaelementoDesde = listaelementoDesde;
	}

	public RegistroDataModelImpl getListaelementoHasta() {
		return listaelementoHasta;
	}

	public void setListaelementoHasta(RegistroDataModelImpl listaelementoHasta) {
		this.listaelementoHasta = listaelementoHasta;
	}

}

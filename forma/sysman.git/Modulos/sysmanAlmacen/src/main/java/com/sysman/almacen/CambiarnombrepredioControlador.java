package com.sysman.almacen;

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

import com.sysman.almacen.enums.CambiarnombrepredioControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

/**
 *
 * @author apineda
 * @version 1, 17/02/2016
 * @author yrojas
 * @version 3, 21/04/2017 Se cambiaron las consultas por la invocaci�n de los
 *          DSS. Se cambio controlador segun especificaciones del SonarLint.
 * @author spina - refactorizo conexiones
 * @version 4, 12/06/2017
 */
@ManagedBean
@ViewScoped
public class CambiarnombrepredioControlador extends BeanBaseModal {

	private final String compania;
	private String predio;
	private String nuevoNombre;
	private String direccion;
	private String escritura;
	private String codPredial;
	private String codInventario;
	private String nombreActual;
	private RegistroDataModelImpl listacmbPredio;

	/**
	 * Creates a new instance of CambiarnombrepredioControlador
	 */
	public CambiarnombrepredioControlador() {
		super();
		numFormulario = GeneralCodigoFormaEnum.CAMBIARNOMBREPREDIO_CONTROLADOR.getCodigo();
		compania = SessionUtil.getCompania();

		try {
			validarPermisos();
		} catch (Exception ex) {
			Logger.getLogger(CambiarnombrepredioControlador.class.getName()).log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	@PostConstruct
	public void inicializar() {
		cargarListacmbPredio();
		abrirFormulario();
	}

	public void cargarListacmbPredio() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(CambiarnombrepredioControladorUrlEnum.URL2066.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listacmbPredio = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"ID_PREDIO");
	}

	public void oprimirbtGenerar() {
		// <CODIGO_DESARROLLADO>
		try {
			Map<String, Object> parametros = new HashMap<>();
			parametros.put(GeneralParameterEnum.NOMBRE.getName(), nuevoNombre);
			parametros.put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());
			parametros.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
			parametros.put("KEY_COMPANIA", compania);
			parametros.put("KEY_PREDIO", predio);
			Parameter parameter = new Parameter();

			parameter.setFields(parametros);
			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(CambiarnombrepredioControladorUrlEnum.URL2857.getValue());
			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1827"));
			return;
		} catch (SystemException ex) {
			Logger.getLogger(CambiarnombrepredioControlador.class.getName()).log(Level.SEVERE, null, ex);
		}
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirbtCancelar() {
		// <CODIGO_DESARROLLADO>
		JsfUtil.ejecutarJavaScript("cerrarModalDefault()");
		// </CODIGO_DESARROLLADO>
	}

	public void seleccionarFilacmbPredio(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		predio = SysmanFunciones.nvl(registroAux.getCampos().get("ID_PREDIO"), "").toString();
		nombreActual = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "")
				.toString();
		direccion = SysmanFunciones.nvl(registroAux.getCampos().get("DIRECCION"), "").toString();
		escritura = SysmanFunciones.nvl(registroAux.getCampos().get("ESCRITURA_NO"), "").toString();
		codPredial = SysmanFunciones.nvl(registroAux.getCampos().get("COD_PREDIAL"), "").toString();
		codInventario = SysmanFunciones.nvl(registroAux.getCampos().get("COD_INVENTARIO"), "").toString();
	}

	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	public String getPredio() {
		return predio;
	}

	public void setPredio(String predio) {
		this.predio = predio;
	}

	public String getNuevoNombre() {
		return nuevoNombre;
	}

	public void setNuevoNombre(String nuevoNombre) {
		this.nuevoNombre = nuevoNombre;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getEscritura() {
		return escritura;
	}

	public void setEscritura(String escritura) {
		this.escritura = escritura;
	}

	public String getCodPredial() {
		return codPredial;
	}

	public void setCodPredial(String codPredial) {
		this.codPredial = codPredial;
	}

	public String getCodInventario() {
		return codInventario;
	}

	public void setCodInventario(String codInventario) {
		this.codInventario = codInventario;
	}

	public String getNombreActual() {
		return nombreActual;
	}

	public void setNombreActual(String nombreActual) {
		this.nombreActual = nombreActual;
	}

	public RegistroDataModelImpl getListacmbPredio() {
		return listacmbPredio;
	}

	public void setListacmbPredio(RegistroDataModelImpl listacmbPredio) {
		this.listacmbPredio = listacmbPredio;
	}
}

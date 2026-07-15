package com.sysman.almacen;

import java.math.BigInteger;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import com.sysman.almacen.enums.CambiarNombreViaControladorEnum;
import com.sysman.almacen.enums.CambiarNombreViaControladorUrlEnum;
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

/**
 *
 * @author apineda
 * @version 1, 17/02/2016
 *
 *          - Modificado por lcortes 26/04/2017 14:30.Se refactoriza el codigo
 *          de las listas para utilizar dss y se ajusta los llamados a
 *          funciones, procedimientos y metodos de la clase Acciones.
 * 
 * @author asana
 * @version 3, 12/06/2017 se implementa enum en formulario.
 */
@ManagedBean
@ViewScoped
public class CambiarNombreViaControlador extends BeanBaseModal {

	private final String compania;
	private String seleccionVia;
	private String sector;
	private String nombreActual;
	private String tramo;
	private String codInventario;
	private String nuevoNombre;
	private RegistroDataModelImpl listaidVia;

	/**
	 * Creates a new instance of CambiarNombreViaControlador
	 */
	public CambiarNombreViaControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = GeneralCodigoFormaEnum.CAMBIAR_NOMBRE_VIA_CONTROLADOR.getCodigo();
			validarPermisos();
		} catch (Exception ex) {
			Logger.getLogger(CambiarNombreViaControlador.class.getName()).log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	@PostConstruct
	public void init() {
		cargarListaidVia();
		abrirFormulario();
	}

	public void cargarListaidVia() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(CambiarNombreViaControladorUrlEnum.URL2098.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

		listaidVia = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				"ID_VIA");
	}

	public void oprimirGENERAR() {
		// <CODIGO_DESARROLLADO>
		try {
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.NOMBRE.getName(), nuevoNombre);
			param.put(CambiarNombreViaControladorEnum.PARAM0.getValue(), seleccionVia);
			param.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
			param.put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());
			Parameter parameter = new Parameter();
			parameter.setFields(param);

			UrlBean urlUpdate = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(CambiarNombreViaControladorUrlEnum.URL2842.getValue());
			requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);
			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1827"));
		} catch (SystemException ex) {
			Logger.getLogger(CambiarNombreViaControlador.class.getName()).log(Level.SEVERE, null, ex);
		}

		// </CODIGO_DESARROLLADO>
	}

	public void oprimirCANCELAR() {
		// <CODIGO_DESARROLLADO>
		RequestContext.getCurrentInstance().closeDialog(null);
		// </CODIGO_DESARROLLADO>
	}

	public void seleccionarFilaidVia(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		seleccionVia = registroAux.getCampos().get("ID_VIA").toString();
		nombreActual = registroAux.getCampos().get("NOMBRE").toString();
		sector = registroAux.getCampos().get("SECTOR").toString();
		tramo = new BigInteger(registroAux.getCampos().get("NO_TRAMO").toString()).toString();
		codInventario = registroAux.getCampos().get("COD_INVENTARIO").toString();

	}

	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		/*
		 * FR522-AL_ABRIR Private Sub Form_Open(Cancel As Integer) formularioAbrir 15,
		 * Me.Name DoCmd.Restore End Sub
		 */
		// </CODIGO_DESARROLLADO>
	}

	public String getSeleccionVia() {
		return seleccionVia;
	}

	public void setSeleccionVia(String seleccionVia) {
		this.seleccionVia = seleccionVia;
	}

	public String getSector() {
		return sector;
	}

	public void setSector(String sector) {
		this.sector = sector;
	}

	public String getNombreActual() {
		return nombreActual;
	}

	public void setNombreActual(String nombreActual) {
		this.nombreActual = nombreActual;
	}

	public String getTramo() {
		return tramo;
	}

	public void setTramo(String tramo) {
		this.tramo = tramo;
	}

	public String getCodInventario() {
		return codInventario;
	}

	public void setCodInventario(String codInventario) {
		this.codInventario = codInventario;
	}

	public String getNuevoNombre() {
		return nuevoNombre;
	}

	public void setNuevoNombre(String nuevoNombre) {
		this.nuevoNombre = nuevoNombre;
	}

	public RegistroDataModelImpl getListaidVia() {
		return listaidVia;
	}

	public void setListaidVia(RegistroDataModelImpl listaidVia) {
		this.listaidVia = listaidVia;
	}
}

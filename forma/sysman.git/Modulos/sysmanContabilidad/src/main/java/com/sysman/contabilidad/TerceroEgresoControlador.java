package com.sysman.contabilidad;

import com.ibm.icu.math.BigDecimal;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.TerceroEgresoControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.services.RegistroDataModelImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author esarmiento
 * @version 1, 10/03/2016
 * @version 2, 17/04/2015 jrodriguezr. Se refactoriza el codigo SQL de
 * las listas para utilizar dss .
 * @version 3, 20/04/2017 jrodriguezr Se refactoriza el codigo
 * ajustando los llamdos a funciones, procedimiento y metodos de la
 * clase Acciones a llamados a EJB.
 * @version 4, 12/06/2017 Refactoring conexiones y n�mero frm.
 */
@ManagedBean
@ViewScoped

public class TerceroEgresoControlador extends BeanBaseModal {

	private final String compania;
	private String comprobante;
	private RegistroDataModelImpl listaListaTercero;
	private boolean inactivo;
	private boolean bloqCuenta;
	private String cuenta;
	private RegistroDataModelImpl listaCuenta;
	private Set<String> cuentasSeleccionadas = new HashSet<>();

	/**
	 * Creates a new instance of TerceroEgresoControlador
	 */
	public TerceroEgresoControlador() {

		numFormulario = 568;
		numFormulario = GeneralCodigoFormaEnum.TERCERO_EGRESO_CONTROLADOR
				.getCodigo();
		compania = SessionUtil.getCompania();
		cuenta = null;
		try {
			Map<String, Object> parametros = SessionUtil.getFlash();
			if (parametros != null) {
				comprobante = (String) parametros.get("comprobantes");
			}
			validarPermisos();
		}
		catch (Exception ex) {
			SessionUtil.redireccionarMenuPermisos();
			Logger.getLogger(TerceroEgresoControlador.class.getName())
			.log(Level.SEVERE, null, ex);
		}
		finally {
			SessionUtil.cleanFlash();
		}
	}

	@PostConstruct
	public void inicializar() {
		cargarListaListaTercero();
		cargarListaCuenta();
		abrirFormulario();
		inactivo(false);
	}

	public void cargarListaListaTercero() {
		try {
			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(
							TerceroEgresoControladorUrlEnum.URL39106
							.getValue());
			Map<String, Object> param = new TreeMap<>();
			String [] registro = comprobante.split(",");
			for (int i = 0; i < registro.length; i++) {    	
				String anio = registro[i].substring(0, 4);
				String tipo = registro[i].substring(4, 7);
				String comprobante = registro[i].substring(7, registro[i].length());

				param.put(GeneralParameterEnum.ANO.getName() + i,
						anio);
				param.put(GeneralParameterEnum.TIPO.getName() + i,
						tipo);
				param.put(GeneralParameterEnum.COMPROBANTE.getName() + i,
						comprobante);
			}

			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			//            param.put(GeneralParameterEnum.COMPROBANTE.getName(),
			//                            comprobante);

			listaListaTercero = new RegistroDataModelImpl(urlBean.getUrl(),
					urlBean.getUrlConteo().getUrl(), param,
					false,
					CacheUtil.getLlaveServicio(urlConexionCache,
							"DETALLE_COMPROBANTE_CNT"),
					true);
		}

		catch (SysmanException e) {
			//
			Logger.getLogger(TerceroEgresoControlador.class.getName())
			.log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());

		}
	}

	public void cargarListaCuenta(){
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
	            .getUrlServiceByUrlByEnumID(
	                    TerceroEgresoControladorUrlEnum.URL16226
	                    .getValue());
	    Map<String, Object> param = new TreeMap<>();
	    
	    param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	    
	    StringBuilder cadenaBuilder = new StringBuilder();
	    String[] listadoCptes = comprobante.split(",");
	    
	    for (int i = 0; i < listadoCptes.length; i++) {
	        cadenaBuilder.append(listadoCptes[i]);
	        if (i < listadoCptes.length - 1) {
	            cadenaBuilder.append("_");
	        }
	    }
	    
	    param.put("CADENA_COMPROBANTES", cadenaBuilder.toString());

	    listaCuenta = new RegistroDataModelImpl(urlBean.getUrl(),
	            urlBean.getUrlConteo().getUrl(), param,
	            true, GeneralParameterEnum.CUENTA.getName());

		
	}

	public void oprimirbtnAceptar() {
		if (listaListaTercero.getSeleccionados().isEmpty()) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB37"));
		}
		else {
			HashMap<String, Object> parametro = new HashMap<>();
			BigDecimal valorTercero = new BigDecimal(0);
			StringBuilder listaTer = new StringBuilder();
			for (Registro reg : listaListaTercero.getSeleccionados()) {

				listaTer.append("(''" + reg.getCampos().get("TERCERO")
						+ "'',''").append(reg.getCampos().get("SUCURSAL")
								+ "'',''").append(reg.getCampos().get("CONSECUTIVO"))
				.append("''),");
				valorTercero = valorTercero.add(new BigDecimal(
						reg.getCampos().get("SALDO").toString()));

			}

			String lista = (listaTer.toString()).substring(0,
					listaTer.length() - 1);
			parametro.put("listaTercero", lista);
			parametro.put("valorTercero", valorTercero);
			SessionUtil.setFlash(parametro);
			RequestContext.getCurrentInstance().closeDialog(this);
			listaTer = new StringBuilder();
		}
	}

	// 7702533_Contabilidad (MRosero)
	public void oprimirbtnSeleccionarTodos() {
		List<Registro> registros = new ArrayList<>();
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.COMPROBANTE.getName(), comprobante);		
		try {
			List<Registro> datos = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
									.getUrlServiceByUrlByEnumID(TerceroEgresoControladorUrlEnum.URL39112.getValue())
									.getUrl(), param));

			for (int i = 0; i < datos.size(); i++) {
				registros.add(datos.get(i));
			}
			listaListaTercero.setSeleccionados(registros);
			inactivo = true;
			bloqCuenta = true;


		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//7702533_Contabilidad (MRosero)


	/**
	 * 
	 * Metodo ejecutado al oprimir el boton btnSeleccionarxcuenta
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirbtnSeleccionarxcuenta() {
		//<CODIGO_DESARROLLADO>
		
		if (cuenta == null || cuenta.isEmpty() ) {
			
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4460"));
			
		}else {
			List<Registro> registros = new ArrayList<>();
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.COMPROBANTE.getName(), comprobante);	
			param.put(GeneralParameterEnum.CUENTA.getName(), cuenta);

			try {
				
				if (cuentasSeleccionadas.contains(cuenta)) {
					JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4461")
	                        .replace("s$cuenta$s",
	                        		String.valueOf(cuenta)));
					return;
		        }
				
				List<Registro> datos = RegistroConverter
						.toListRegistro(
								requestManager.getList(
										UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(TerceroEgresoControladorUrlEnum.URL39117.getValue())
										.getUrl(), param));

				List<Registro> seleccionadosActuales = listaListaTercero.getSeleccionados();

				if (seleccionadosActuales != null) {
					registros.addAll(seleccionadosActuales);
				}

				for (int i = 0; i < datos.size(); i++) {
					registros.add(datos.get(i));
				}

				listaListaTercero.setSeleccionados(registros);
				cuentasSeleccionadas.add(cuenta);
				inactivo(true);

			} catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		//</CODIGO_DESARROLLADO>
	}

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCuenta
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCuenta(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuenta = registroAux.getCampos()
				.get(GeneralParameterEnum.CUENTA.getName()).toString();
		inactivo = true;
	}

	public void oprimirbtnCancelar() {
		RequestContext.getCurrentInstance().closeDialog(this);
	}

	public void seleccionarFilaListaTercero(SelectEvent event) {
		// no realiza evento
	}

	public RegistroDataModelImpl getListaListaTercero() {
		return listaListaTercero;
	}

	public void setListaListaTercero(RegistroDataModelImpl listaListaTercero) {
		this.listaListaTercero = listaListaTercero;
	}
	
	public RegistroDataModelImpl getListaCuenta() {
        return listaCuenta;
    }
    
	public void setListaCuenta(RegistroDataModelImpl listaCuenta) {
        this.listaCuenta = listaCuenta;
    }

	@Override
	public void abrirFormulario() {
		// No realiza ninguna tarea
	}

	private void inactivo(boolean b) {
		// TODO Auto-generated method stub

	}
	public boolean getinactivo() {
		return inactivo;
	}

	public void setinactivo(boolean inactivo) {
		this.inactivo = inactivo;
	}

	public boolean getbloqCuenta() {
		return bloqCuenta;
	}

	public void setbloqCuenta(boolean bloqCuenta) {
		this.bloqCuenta = bloqCuenta;
	}

	public String getCuenta() {
		return cuenta;
	}

	public void setCuenta(String cuenta) {
		this.cuenta = cuenta;
	}

}

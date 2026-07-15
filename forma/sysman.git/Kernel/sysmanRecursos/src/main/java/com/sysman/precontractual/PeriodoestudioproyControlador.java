package com.sysman.precontractual;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.precontractual.enums.PeriodoestudioproyControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.context.RequestContext;

/**
 *
 * @author acaceres
 * @version 1, 23/01/2016
 * 
 * @author eamaya
 * @version 2.0, 05/09/2017, Proceso de Refactoring DSS y correcciones
 * SonarLint
 * 
 */
@ManagedBean
@ViewScoped

public class PeriodoestudioproyControlador extends BeanBaseModal {

	private final String compania;
	private String vigenciaPeriodo;
	private List<Registro> listaAno;
	private String dependencia;
	private Map<String,Object> parametroswf;
	/**
	 * Creates a new instance of PeriodoestudioproyControlador
	 */
	public PeriodoestudioproyControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = GeneralCodigoFormaEnum.PERIODOESTUDIOPROY_CONTROLADOR
					.getCodigo();
			parametroswf = (Map<String,Object>) SessionUtil.getSessionVarContainer("parametroswf");
			if(parametroswf != null) {
				SessionUtil.setSessionVar("modulo", "19");
			}
			validarPermisos();
		}
		catch (Exception ex) {
			Logger.getLogger(PeriodoestudioproyControlador.class.getName())
			.log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
        	try {
				SessionUtil.removeSessionVarContainer("parametroswf");
			} catch(NamingException e) {
				e.printStackTrace();
			}
        }

	}

	@PostConstruct
	public void init() {

		cargarListaAno();
		abrirFormulario();
		vigenciaPeriodo = String.valueOf(SysmanFunciones.ano(new Date()));
	}

	public void cargarListaAno() {

		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);

		try {
			listaAno = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									PeriodoestudioproyControladorUrlEnum.URL2367
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);

		}
	}

	public void oprimirAceptar() {
		// <CODIGO_DESARROLLADO>

		if (vigenciaPeriodo != null) {

			if (SessionUtil.getUser().getDependencia() == null) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4415"));

			}else {

				Map<String, Object> parametros = new TreeMap<>();
				parametros.put("vigenciaPeriodo", vigenciaPeriodo);

				Direccionador direccionador = new Direccionador();
				direccionador.setNumForm(Integer
						.toString(GeneralCodigoFormaEnum.FRMESTPREVIOPROYS_CONTROLADOR
								.getCodigo()));

				direccionador.setParametros(parametros);
				if(parametroswf != null) {
		        	try {
		        		parametros.put("parametroswf",parametroswf);
						SessionUtil.setSessionVarContainer("parametros",parametros);
					} catch(NamingException e) {
						e.printStackTrace();
					}
		        	JsfUtil.ejecutarJavaScript("window.location.href='/sysmanPrecontractual/frmestprevioproy.sysman';");
		        } else {
		        	RequestContext.getCurrentInstance().closeDialog(direccionador);
		        }
			}
		}
		else {
			JsfUtil.agregarMensajeError(idioma.getString("TB_TB2247"));

		}
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirCancelar() {
		// <CODIGO_DESARROLLADO>
		RequestContext.getCurrentInstance().closeDialog(null);
		// </CODIGO_DESARROLLADO>
	}




	public List<Registro> getListaAno() {
		return listaAno;
	}

	public void setListaAno(List<Registro> listaAno) {
		this.listaAno = listaAno;
	}

	@Override
	public void abrirFormulario() {
		// METODO_NO_IMPLEMENTADO

	}

	@Override
	public int getNumFormulario() {
		return numFormulario;
	}

	public String getVigenciaPeriodo() {
		return vigenciaPeriodo;
	}

	public void setVigenciaPeriodo(String vigenciaPeriodo) {
		this.vigenciaPeriodo = vigenciaPeriodo;
	}
}

package com.sysman.presupuesto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
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

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.presupuesto.ejb.impl.EjbPresupuestoDos;
import com.sysman.presupuesto.enums.LiberarComprobantesControladorEnum;
import com.sysman.presupuesto.enums.LiberarComprobantesControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

/**
 *
 * @author dmaldonado
 * @version 1, 13/06/2016
 * @modified jsforero
 * @version 2. 05/04/2017 Se realizo el refactory.
 * 
 * @author ybecerra
 * @version 3, 13/06/2017 Implementacion al llamado de GeneralCodigoFormaEnum,
 *          para el codigo del formulario
 * 
 * @modified jrojas
 * @version 4, 09/01/2019 creación de nuevo campo descripción para concatenar
 *          con las observaciones
 */
@ManagedBean
@ViewScoped
public class LiberarComprobantesControlador extends BeanBaseModal {
	private final String compania;
	private final String modulo;
	// <DECLARAR_ATRIBUTOS>
	private String tipo;
	private Date fechaCorte;
	private Date fechaLiberacion;
	private String descripcion;
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listaTipo;
	// </DECLARAR_LISTAS_COMBO_GRANDE>

	@EJB
	private EjbPresupuestoDos ejbPresupuestoDos;

	/**
	 * Creates a new instance of LiberarComprobantesControlador
	 */
	public LiberarComprobantesControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			numFormulario = GeneralCodigoFormaEnum.LIBERAR_COMPROBANTES_CONTROLADOR.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			Logger.getLogger(LiberarComprobantesControlador.class.getName()).log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		}

	}

	@PostConstruct
	public void inicializar() {
		// <CARGAR_LISTA>
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		cargarListaTipo();
		// </CARGAR_LISTA_COMBO_GRANDE>
		abrirFormulario();
	}

	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

	// <METODOS_CARGAR_LISTA>
	public void cargarListaTipo() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(LiberarComprobantesControladorUrlEnum.URL3039.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.name(), compania);

		listaTipo = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	public void oprimirIniciar() {
		// <CODIGO_DESARROLLADO>
		List<Registro> rsL;
		int cont = 0;
		try {
			if (fechaLiberacion.before(fechaCorte)) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1466"));
				return;
			}

			if (SysmanFunciones.getParteFecha(fechaLiberacion, Calendar.YEAR) != SysmanFunciones
					.getParteFecha(fechaCorte, Calendar.YEAR)) {
				JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1467"));
				return;
			}
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.name(), compania);
			param.put(LiberarComprobantesControladorEnum.TIPO.getValue(), tipo);
			param.put(LiberarComprobantesControladorEnum.FECHACORTE.getValue(), fechaCorte);
			param.put(LiberarComprobantesControladorEnum.ANOFECHACORTE.getValue(), SysmanFunciones.ano(fechaCorte));

			rsL = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													LiberarComprobantesControladorUrlEnum.URL4206.getValue())
											.getUrl(),
									param));

			for (Registro rs : rsL) {

				if (Double.parseDouble(rs.getCampos().get("NETO").toString()) > 0) {
					ejbPresupuestoDos.liberarComprobantePresupuestal(compania, Integer.parseInt(modulo),

							Integer.parseInt(rs.getCampos().get(GeneralParameterEnum.ANO.getName()).toString()),

							rs.getCampos().get(GeneralParameterEnum.TIPO_CPTE.getName()).toString(),

							new BigInteger(rs.getCampos().get(GeneralParameterEnum.COMPROBANTE.getName()).toString()),

							Integer.parseInt(rs.getCampos().get(GeneralParameterEnum.CONSECUTIVO.getName()).toString()),

							new BigDecimal(rs.getCampos().get("NETO").toString()),

							fechaLiberacion,

							rs.getCampos().get(GeneralParameterEnum.CUENTA.getName()).toString(),

							rs.getCampos().get(GeneralParameterEnum.TERCERO.getName()).toString(),

							rs.getCampos().get(GeneralParameterEnum.SUCURSAL.getName()).toString(),

							rs.getCampos().get(GeneralParameterEnum.CENTRO_COSTO.getName()).toString(),

							rs.getCampos().get(GeneralParameterEnum.AUXILIAR.getName()).toString(),

							rs.getCampos().get(GeneralParameterEnum.FUENTE_RECURSO.getName()).toString(),

							rs.getCampos().get(GeneralParameterEnum.REFERENCIA.getName()).toString(),

							rs.getCampos().get("NAT").toString(),

							descripcion,

							SessionUtil.getUser().getCodigo());

					cont++;
				}
			}

			JsfUtil.agregarMensajeInformativo(
					idioma.getString("TB_TB1468") + " " + cont + " " + idioma.getString("TB_TB1469"));

		} catch (SystemException e) {
			Logger.getLogger(LiberarComprobantesControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeErrorDialogo(e.getMessage());
		}
		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>
	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	public void seleccionarFilaTipo(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		tipo = registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString();
	}

	// </METODOS_COMBOS_GRANDES>

	// <SET_GET_ATRIBUTOS>
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Date getFechaCorte() {
		return fechaCorte;
	}

	public void setFechaCorte(Date fechaCorte) {
		this.fechaCorte = fechaCorte;
	}

	public Date getFechaLiberacion() {
		return fechaLiberacion;
	}

	public void setFechaLiberacion(Date fechaLiberacion) {
		this.fechaLiberacion = fechaLiberacion;
	}

	// <SET_GET_LISTAS_COMBO_GRANDE>
	public RegistroDataModelImpl getListaTipo() {
		return listaTipo;
	}

	public void setListaTipo(RegistroDataModelImpl listaTipo) {
		this.listaTipo = listaTipo;
	}
	// </SET_GET_LISTAS_COMBO_GRANDE>

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}

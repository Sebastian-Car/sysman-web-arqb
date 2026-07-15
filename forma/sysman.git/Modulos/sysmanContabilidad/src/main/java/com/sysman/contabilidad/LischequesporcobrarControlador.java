package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.LischequesporcobrarControladorEnum;
import com.sysman.contabilidad.enums.LischequesporcobrarControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dsuesca
 * @version 3, 19/05/2016 17:17:03 -- Modificado por dsuesca
 * @author yrojas
 * @version 4, 10/04/2017 Se cambiaron las consultas por la invocacion de los
 *          DSS. Se cambio controlador segun especificaciones del SonarLint.
 *          
 * @author jreina
 * @version 5, 13/06/2017 Cambio c�digo formulario y actualizaci�n
 * de ConnectorPool
 */
@ManagedBean
@ViewScoped
public class LischequesporcobrarControlador extends BeanBaseModal {
	private final String compania;
	// <DECLARAR_ATRIBUTOS>
	private String cuentaInicial;
	private String cuentaFinal;
	private Date fechaInicial;
	private Date fechaFinal;
	private StreamedContent archivoDescarga;
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>
	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listaCuentaInicial;
	private RegistroDataModelImpl listaCuentaFinal;
	// </DECLARAR_LISTAS_COMBO_GRANDE>

	/**
	 * Creates a new instance of LischequesporcobrarControlador
	 */
	public LischequesporcobrarControlador() {
		super();
		compania = SessionUtil.getCompania();
		try {
			numFormulario = GeneralCodigoFormaEnum.LISCHEQUESPORCOBRAR_CONTROLADOR.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			fechaInicial = new Date();
			fechaFinal = new Date();
			cuentaFinal = "ZZZZZZZZZZZZZZZZ";
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			Logger.getLogger(LischequesporcobrarControlador.class.getName()).log(Level.SEVERE, null, ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	@PostConstruct
	public void inicializar() {
		// <CARGAR_LISTA>
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		cargarListaCuentaInicial();
		cargarListaCuentaFinal();
		// </CARGAR_LISTA_COMBO_GRANDE>
		abrirFormulario();
	}

	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		/*
		 * FR713-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
		 * formularioAbrir 1, Me.Name DoCmd.Restore End Sub
		 */
		// </CODIGO_DESARROLLADO>
	}

	// <METODOS_CARGAR_LISTA>
	public void cargarListaCuentaInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(LischequesporcobrarControladorUrlEnum.URL3404.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.ano(fechaInicial));

		listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaCuentaFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(LischequesporcobrarControladorUrlEnum.URL4821.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), SysmanFunciones.ano(fechaInicial));
		param.put(LischequesporcobrarControladorEnum.PARAM0.getValue(), cuentaInicial);

		listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO.getName());
	}

	// </METODOS_CARGAR_LISTA>
	// <METODOS_BOTONES>
	public void oprimirComando64() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		getInforme(FORMATOS.EXCEL97);
		// </CODIGO_DESARROLLADO>
	}

	public void oprimirImprimir() {
		// <CODIGO_DESARROLLADO>
		archivoDescarga = null;
		getInforme(FORMATOS.PDF);
		// </CODIGO_DESARROLLADO>
	}

	public void getInforme(FORMATOS formato) {
	    try {	
	        HashMap<String, Object> reemplazar = new HashMap<>();
	        // MANEJO DE PARAMETROS DE REEMPLAZO
	        reemplazar.put("anioInicial", SysmanFunciones.ano(fechaInicial));
	        reemplazar.put("cuentaInicial", "'" + cuentaInicial + "'");
	        reemplazar.put("cuentaFinal", "'" + cuentaFinal + "'");
	        reemplazar.put("fechaInicial", SysmanFunciones.formatearFecha(fechaInicial));
	        reemplazar.put("fechaFinal", SysmanFunciones.formatearFecha(fechaFinal));

	        Map<String, Object> parametros = new HashMap<>();
	        String reporte = "000779LisChequesPorCobrar";
	        // MANEJO DE PARAMETROS DEL REPORTE

	        Reporteador.resuelveConsulta(reporte, Integer.parseInt(SessionUtil.getModulo()), reemplazar, parametros);

	        parametros.put("PR_FECHAFINAL", SysmanFunciones.convertirAFechaCadena(fechaFinal));
	        parametros.put("PR_FECHAINICIAL", SysmanFunciones.convertirAFechaCadena(fechaInicial));

	        archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
	    }
	    catch (JRException | IOException | SysmanException | ParseException e) {
	        logger.error(e.getMessage(), e);
	        JsfUtil.agregarMensajeError(e.getMessage());
	    }

	}

	// </METODOS_BOTONES>
	// <METODOS_CAMBIAR>
	public void cambiarFechaInicial() {
		// <CODIGO_DESARROLLADO>
		cuentaInicial = cuentaFinal = null;
		cargarListaCuentaFinal();
		cargarListaCuentaInicial();
		// </CODIGO_DESARROLLADO>
	}

	// </METODOS_CAMBIAR>
	// <METODOS_COMBOS_GRANDES>
	public void seleccionarFilaCuentaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaInicial = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "")
				.toString();
		cuentaFinal = null;
		cargarListaCuentaFinal();
	}

	public void seleccionarFilaCuentaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cuentaFinal = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "")
				.toString();
	}

	// </METODOS_COMBOS_GRANDES>

	// <SET_GET_ATRIBUTOS>
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

	public Date getFechaInicial() {
		return fechaInicial;
	}

	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}

	public Date getFechaFinal() {
		return fechaFinal;
	}

	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	// </SET_GET_ATRIBUTOS>
	// <SET_GET_PARAMETROS>
	// </SET_GET_PARAMETROS>
	// <SET_GET_LISTAS>
	// </SET_GET_LISTAS>
	// <SET_GET_LISTAS_COMBO_GRANDE>
	public RegistroDataModelImpl getListaCuentaInicial() {
		return listaCuentaInicial;
	}

	public void setListaCuentaInicial(RegistroDataModelImpl listaCuentaInicial) {
		this.listaCuentaInicial = listaCuentaInicial;
	}

	public RegistroDataModelImpl getListaCuentaFinal() {
		return listaCuentaFinal;
	}

	public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
		this.listaCuentaFinal = listaCuentaFinal;
	}
	// </SET_GET_LISTAS_COMBO_GRANDE>
}

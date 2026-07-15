/*-
 * FrmCircular09cgrEjPptalIngresosControlador.java
 *
 * 1.0
 * 
 * 11/06/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.cgr;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.context.RequestContext;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.cgr.enums.FrmCircular09cgrEjPptalGastosControladorEnum;
import com.sysman.cgr.enums.FrmCircular09cgrEjPptalGastosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModel;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
import javax.faces.event.ActionEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.event.SelectEvent;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 11/06/2021
 * @author sdaza
 */
@ManagedBean
@ViewScoped
public class  FrmCircular09cgrEjPptalGastosControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	private final String nitCompania ;
	private final String nomCompania;
	//<DECLARAR_ATRIBUTOS>
	/**
	 * atributo en el cual se almacena la vigencia seleccionada en el formulario para la 
	 * generación del informe
	 */
	private String anio;
	/**
	 * atributo en el cual se almacena el mes seleccionado en el formulario para la 
	 * generación del informe
	 */
	private String mes;
	/**
	 * atributo en el cual se almacena el rubro inicial seleccionado en el formulario para la 
	 * generación del informe
	 */
	private String codInicial;
	/**
	 * atributo en el cual se almacena el rubro final seleccionado en el formulario para la 
	 * generación del informe
	 */
	private String codFinal;
	/**
	 * atributo en el cual se almacena el nombre del rubro inicial seleccionado en el formulario
	 * para lageneración del informe
	 */
	private String nomCodInicial;
	/**
	 * atributo en el cual se almacena el nombre del rubro final seleccionado en el formulario
	 * para lageneración del informe
	 */
	private String nomCodFinal;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listavigencia;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listames;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listacodInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listacodFinal;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmCircular09cgrEjPptalIngresosControlador
	 */
	public FrmCircular09cgrEjPptalGastosControlador() {
		super();
		compania = SessionUtil.getCompania();
		nitCompania = SessionUtil.getCompaniaIngreso().getNit();
		nomCompania = SessionUtil.getCompaniaIngreso().getNombre();
		try {
			numFormulario=GeneralCodigoFormaEnum.FRMCIRCULAR09CGREJPPTALGASTOS
					.getCodigo();
			validarPermisos();
			anio = String.valueOf(SysmanFunciones.ano(new Date()));
			mes = String.valueOf(SysmanFunciones.mes(new Date()));
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
		}finally{
		}
	}
	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la
	 * clase del Bean ha sido creado, en este se realizan las
	 * asignaciones iniciales necesarias para la visualizacion del
	 * formulario, como son tablas, origenes de datos, inicializacion
	 * de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar(){
		//<CARGAR_LISTA>
		cargarListavigencia();
		cargarListames();
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListacodInicial(); 
		cargarListacodFinal();
		//</CARGAR_LISTA_COMBO_GRANDE>
		//<CREAR_ARBOLES>
		//</CREAR_ARBOLES>
		abrirFormulario();
	}
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listavigencia
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListavigencia(){
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listavigencia = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmCircular09cgrEjPptalGastosControladorUrlEnum.URL0001
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
	/**
	 * 
	 * Carga la lista listames
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListames(){


		try {

			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ANO.getName(),anio);

			listames = RegistroConverter.toListRegistro(
					requestManager.getList(UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmCircular09cgrEjPptalGastosControladorUrlEnum.URL0002
									.getValue())
							.getUrl(), param));
		}
		catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	/**
	 * 
	 * Carga la lista listacodInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacodInicial(){


		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmCircular09cgrEjPptalGastosControladorUrlEnum.URL0003
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
		param.put(GeneralParameterEnum.ANO.getName(),
				anio);


		listacodInicial = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "CODIGO");
	}
	/**
	 * 
	 * Carga la lista listacodFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacodFinal(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmCircular09cgrEjPptalGastosControladorUrlEnum.URL0004
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
				compania);
		param.put(FrmCircular09cgrEjPptalGastosControladorEnum.PARAM2.getValue(),
				codInicial);
		param.put(GeneralParameterEnum.ANO.getName(),
				anio);
		param.put(GeneralParameterEnum.NATURALEZA.getName(),
				"D");

		listacodFinal = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, "CODIGO");
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton cmdImprimir
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimircmdImprimir() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga = null;
		if (datosInvalidos()) {
			return;
		}
		generarArchivo("application/vnd.ms-excel", ".csv");           
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control vigencia
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarvigencia() {
		//<CODIGO_DESARROLLADO>
		mes = null;
		codInicial = null;
		codFinal = null;
		nomCodInicial = null;
		nomCodFinal = null;
		cargarListames();
		cargarListacodInicial();
		cargarListacodFinal();
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listacodInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacodInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		codInicial = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), " ").toString();
		nomCodInicial = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRE"), " " ).toString();
		codFinal = null;
		nomCodFinal = null;
		cargarListacodFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listacodFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacodFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		codFinal = SysmanFunciones.nvl(registroAux.getCampos().get("CODIGO"), " ").toString();
		nomCodFinal = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRE"), " " ).toString();
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	private boolean faltanCamposObligatorios() {
		if (SysmanFunciones.validarVariableVacio(anio)
				|| SysmanFunciones.validarVariableVacio(mes)
				|| SysmanFunciones.validarVariableVacio(codInicial)
				|| SysmanFunciones.validarVariableVacio(codFinal)) {
			return true;
		}

		return false;
	}

	private boolean datosInvalidos() {
		if (faltanCamposObligatorios()) {
			JsfUtil.agregarMensajeAlerta(
					idioma.getString("TB_TB105"));
			return true;
		}
		if (Integer.parseInt(mes) > 13) {
			JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB106"));
			mes = "13";
			return true;
		}
		return false;
	}

	private String resolverConsulta() {

		Map<String, Object> reemplazos = new HashMap<>();
		reemplazos.put("compania", compania);
		reemplazos.put("anio", anio);
		reemplazos.put("mes", mes);
		reemplazos.put("codinicial", codInicial);
		reemplazos.put("codfinal", codFinal);
		reemplazos.put("nitcompania", nitCompania);
		reemplazos.put("nomcompania", nomCompania);

		int modulo = Integer.parseInt(SessionUtil.getModulo());
		return Reporteador.resuelveConsulta("800437circular09EjPptalGastos",
				modulo, reemplazos);
	}

	private StringBuilder generarFilasTitulo(String[] titulos) {
		StringBuilder textoArchivo = new StringBuilder("");
		for (String celda : titulos) {
			textoArchivo.append(celda + ",");
		}
		return new StringBuilder(
				textoArchivo.substring(0, textoArchivo.length() - 1)
				+ "\n");
	}

	public void generarArchivo(String content, String extension) {
		String sql = resolverConsulta();
		try {
			List<Registro> listado = service
					.getListado(ConectorPool.ESQUEMA_SYSMAN, sql);
			if (listado == null || listado.isEmpty()) {
				JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB108"));
				return;
			}
			String nombreArchivo = "";
			StringBuilder textoArchivo = new StringBuilder("");

			if (".csv".equals(extension)) {
				nombreArchivo = idioma.getString("TT_FR1003") + extension;


				textoArchivo.append(idioma.getString("TG_VIGENCIA")
						+ ",");
				textoArchivo.append(idioma.getString("TB_TB4367")
						+ ",");
				textoArchivo.append(idioma.getString("TB_TB4368")
						+ ",");
				textoArchivo.append(idioma.getString("TB_TB4375")
						+ ",");
				textoArchivo.append(idioma.getString("TB_TB4376")
						+ ",");
				textoArchivo.append(idioma.getString("TB_TB4377")
						+ ",");
				textoArchivo.append(idioma.getString("TB_TB4378")
						+ ",");
				textoArchivo.append(idioma.getString("TC_CP45336").toUpperCase()
						+ ",");
				textoArchivo.append(idioma.getString("TG_RUBRO")
						+ ",");
				textoArchivo.append(idioma.getString("TG_NOMBRE_RUBRO").toUpperCase()
						+ ",");
				textoArchivo.append(idioma.getString("TB_TB4379")
						+ ",");
				textoArchivo.append(idioma.getString("TB_TB4380")
						+ ",");
				textoArchivo.append(idioma.getString("TG_OBJETO")
						+ ",");
				textoArchivo.append(idioma.getString("TB_TB4381")
						+ ",");
				textoArchivo.append(idioma.getString("TB_TB4382")
						+ ",");
				textoArchivo.append(idioma.getString("TB_TB4383")
						+ ",");
				textoArchivo.append(idioma.getString("TG_TIPO_DOCUMENTO").toUpperCase()            		
						+ ",");
				textoArchivo.append(idioma.getString("TC_CP17371").toUpperCase()            		
						+ ",");
				textoArchivo.append(idioma.getString("TG_NOMBRE_TERCERO").toUpperCase()
						+ ",");
				textoArchivo.append(idioma.getString("TB_TB4384")
						+ ",");
				textoArchivo.append(idioma.getString("TG_TIPO_CUENTA").toUpperCase()
						+ ",");
				textoArchivo.append(idioma.getString("TG_NO_CUENTA2").toUpperCase()
						+ ",");
				textoArchivo.append(idioma.getString("TG_APROPIACION_INICIAL")
						+ ",");
				textoArchivo.append(idioma.getString("TG_ADICIONES")
						+ ",");
				textoArchivo.append(idioma.getString("TB_TB4385")
						+ ",");
				textoArchivo.append(idioma.getString("TG_CREDITO2").toUpperCase()
						+ ",");
				textoArchivo.append(idioma.getString("TB_TB4386")
						+ ",");
				textoArchivo.append(idioma.getString("TG_APROPIACION_VIGENTE").toUpperCase()
						+ ",");
				textoArchivo.append(idioma.getString("TB_TB4387")
						+ ",");
				textoArchivo.append(idioma.getString("TG_DISPONIBILIDADES")
						+ ",");
				textoArchivo.append(idioma.getString("TG_COMPROMISOS")
						+ ",");
				textoArchivo.append(idioma.getString("TG_OBLIGACIONES")
						+ ",");
				textoArchivo.append(idioma.getString("TG_PAGOS_ACUMULADOS")
						+ "\n");
				for (Registro registro : listado) {
					textoArchivo.append(SysmanFunciones
							.nvl(registro.getCampos().get("ANO"), "")
							+ ",");
					textoArchivo.append(SysmanFunciones
							.nvl(registro.getCampos()
									.get("NIT_ENTIDAD"), 0)
							.toString().replace(",", "")
							+ ",");
					textoArchivo.append(SysmanFunciones
							.nvl(registro.getCampos()
									.get("NOMBRE_ENTIDAD"), 0)
							.toString().replace(",", "")
							+ ",");
					textoArchivo.append(SysmanFunciones
							.nvl(registro.getCampos()
									.get("NUMERO_DISPONIBILIDAD"), 0)
							.toString().replace(",", "")
							+ ",");
					textoArchivo.append(SysmanFunciones
							.nvl(registro.getCampos()
									.get("NUMERO_REGISTRO"), 0)
							.toString().replace(",", "")
							+ ",");
					textoArchivo.append(SysmanFunciones
							.nvl(registro.getCampos()
									.get("NUMERO_OBLIGACION"), 0)
							.toString().replace(",", "")
							+ ",");
					textoArchivo.append(SysmanFunciones
							.nvl(registro.getCampos()
									.get("NUMERO_PAGO"), 0)
							.toString().replace(",", "")
							+ ",");
					textoArchivo.append(SysmanFunciones
							.nvl(registro.getCampos()
									.get("TIPOVIGENCIA"), 0)
							.toString().replace(",", "")
							+ ",");
					textoArchivo.append(SysmanFunciones
							.nvl(registro.getCampos()
									.get("RUBRO"), 0)
							.toString().replace(",", "")
							+ ",");
					textoArchivo.append(SysmanFunciones
							.nvl(registro.getCampos()
									.get("NOMBRE_RUBRO"), 0)
							.toString().replace(",", "")
							+ ",");
					textoArchivo.append(SysmanFunciones
							.nvl(registro.getCampos()
									.get("FUENTE_RECURSO"), 0)
							.toString().replace(",", "")
							+ ",");
					textoArchivo.append(SysmanFunciones
							.nvl(registro.getCampos()
									.get("SITUACION_FONDOS"), 0)
							.toString().replace(",", "")
							+ ",");
					textoArchivo.append(SysmanFunciones
							.nvl(registro.getCampos().get("OBJETO"), "")
							+ ",");
					textoArchivo.append(SysmanFunciones
							.nvl(registro.getCampos()
									.get("FECHA_REGISTRO"), 0)
							.toString().replace(",", "")
							+ ",");
					textoArchivo.append(SysmanFunciones
							.nvl(registro.getCampos()
									.get("FEHA_OBLIGACION"), 0)
							.toString().replace(",", "")
							+ ",");
					textoArchivo.append(SysmanFunciones
							.nvl(registro.getCampos()
									.get("FECHA_PAGO"), 0)
							.toString().replace(",", "")
							+ ",");
					textoArchivo.append(SysmanFunciones
							.nvl(registro.getCampos()
									.get("TIPOID"), 0)
							.toString().replace(",", "")
							+ ",");
					textoArchivo.append(SysmanFunciones
							.nvl(registro.getCampos()
									.get("NIT_TERCERO_DEL_RES"), 0)
							.toString().replace(",", "")
							+ ",");
					textoArchivo.append(SysmanFunciones
							.nvl(registro.getCampos()
									.get("NOMBRE_TERCERO_DEL_RES"), 0)
							.toString().replace(",", "")
							+ ",");
					textoArchivo.append(SysmanFunciones
							.nvl(registro.getCampos()
									.get("NOMBREBANCO"), 0)
							.toString().replace(",", "")
							+ ",");
					textoArchivo.append(SysmanFunciones
							.nvl(registro.getCampos()
									.get("TIPOCUENTA"), 0)
							.toString().replace(",", "")
							+ ",");
					textoArchivo.append(SysmanFunciones
							.nvl(registro.getCampos()
									.get("CUENTA"), 0)
							.toString().replace(",", "")
							+ ",");
					textoArchivo.append(SysmanFunciones
							.nvl(registro.getCampos()
									.get("APROPIACIONINICIAL"), 0)
							.toString().replace(",", "")
							+ ",");
					textoArchivo.append(SysmanFunciones
							.nvl(registro.getCampos()
									.get("ADICIONES"), 0)
							.toString().replace(",", "")
							+ ",");
					textoArchivo.append(SysmanFunciones
							.nvl(registro.getCampos()
									.get("REDUCCIONES"), 0)
							.toString().replace(",", "")
							+ ",");
					textoArchivo.append(SysmanFunciones
							.nvl(registro.getCampos().get("TRASLADOCREDITO"), "")
							+ ",");
					textoArchivo.append(SysmanFunciones
							.nvl(registro.getCampos()
									.get("TRASLADOCONTRACREDITO"), 0)
							.toString().replace(",", "")
							+ ",");
					textoArchivo.append(SysmanFunciones
							.nvl(registro.getCampos()
									.get("APROPIACIONVIGENTE"), 0)
							.toString().replace(",", "")
							+ ",");
					textoArchivo.append(SysmanFunciones
							.nvl(registro.getCampos()
									.get("APROPIACIONDEFINITIVA"), 0)
							.toString().replace(",", "")
							+ ",");
					textoArchivo.append(SysmanFunciones
							.nvl(registro.getCampos()
									.get("DISPONIBILIDADES"), 0)
							.toString().replace(",", "")
							+ ",");
					textoArchivo.append(SysmanFunciones
							.nvl(registro.getCampos()
									.get("COMPROMISOS"), 0)
							.toString().replace(",", "")
							+ ",");
					textoArchivo.append(SysmanFunciones
							.nvl(registro.getCampos()
									.get("OBLIGACIONES"), 0)
							.toString().replace(",", "")
							+ ",");
					textoArchivo.append(SysmanFunciones
							.nvl(registro.getCampos()
									.get("PAGOSACUMULADOS"), 0)
							.toString().replace(",", "")
							+ "\n");

									}
			}

			ByteArrayInputStream archivo = JsfUtil
					.serializarPlano(textoArchivo.toString());
			archivoDescarga = JsfUtil.getArchivoDescarga(archivo,
					nombreArchivo, content);
		}
		catch ( JRException | IOException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}

	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable anio
	 * 
	 * @return  anio
	 */
	public String getAnio() {
		return anio;
	}
	/**
	 * Asigna la variable  anio
	 * 
	 * @param  anio
	 * Variable a asignar en  anio
	 */
	public void setAnio(String anio) {
		this.anio = anio;
	}
	/**
	 * Retorna la variable mes
	 * 
	 * @return  mes
	 */
	public String getMes() {
		return mes;
	}
	/**
	 * Asigna la variable  mes
	 * 
	 * @param  mes
	 * Variable a asignar en  mes
	 */
	public void setMes(String mes) {
		this.mes = mes;
	}
	/**
	 * Retorna la variable codInicial
	 * 
	 * @return  codInicial
	 */
	public String getCodInicial() {
		return codInicial;
	}
	/**
	 * Asigna la variable  codInicial
	 * 
	 * @param  codInicial
	 * Variable a asignar en  codInicial
	 */
	public void setCodInicial(String codInicial) {
		this.codInicial = codInicial;
	}
	/**
	 * Retorna la variable codFinal
	 * 
	 * @return  codFinal
	 */
	public String getCodFinal() {
		return codFinal;
	}
	/**
	 * Asigna la variable  codFinal
	 * 
	 * @param  codFinal
	 * Variable a asignar en  codFinal
	 */
	public void setCodFinal(String codFinal) {
		this.codFinal = codFinal;
	}
	/**
	 * Retorna la variable nomCodInicial
	 * 
	 * @return  nomCodInicial
	 */
	public String getNomCodInicial() {
		return nomCodInicial;
	}
	/**
	 * Asigna la variable  nomCodInicial
	 * 
	 * @param  nomCodInicial
	 * Variable a asignar en  nomCodInicial
	 */
	public void setNomCodInicial(String nomCodInicial) {
		this.nomCodInicial = nomCodInicial;
	}
	/**
	 * Retorna la variable nomCodFinal
	 * 
	 * @return  nomCodFinal
	 */
	public String getNomCodFinal() {
		return nomCodFinal;
	}
	/**
	 * Asigna la variable  nomCodFinal
	 * 
	 * @param  nomCodFinal
	 * Variable a asignar en  nomCodFinal
	 */
	public void setNomCodFinal(String nomCodFinal) {
		this.nomCodFinal = nomCodFinal;
	}
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listavigencia
	 * 
	 * @return listavigencia
	 */
	public List<Registro> getListavigencia() {
		return listavigencia;
	}
	/**
	 * Asigna la lista listavigencia
	 * 
	 * @param listavigencia
	 * Variable a asignar en  listavigencia
	 */
	public void setListavigencia(List<Registro> listavigencia) {
		this.listavigencia = listavigencia;
	}
	/**
	 * Retorna la lista listames
	 * 
	 * @return listames
	 */
	public List<Registro> getListames() {
		return listames;
	}
	/**
	 * Asigna la lista listames
	 * 
	 * @param listames
	 * Variable a asignar en  listames
	 */
	public void setListames(List<Registro> listames) {
		this.listames = listames;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listacodInicial
	 * 
	 * @return listacodInicial
	 */
	public RegistroDataModelImpl getListacodInicial() {
		return listacodInicial;
	}
	/**
	 * Asigna la lista listacodInicial
	 * 
	 * @param listacodInicial
	 * Variable a asignar en  listacodInicial
	 */
	public void setListacodInicial(RegistroDataModelImpl listacodInicial) {
		this.listacodInicial = listacodInicial;
	}
	/**
	 * Retorna la lista listacodFinal
	 * 
	 * @return listacodFinal
	 */
	public RegistroDataModelImpl getListacodFinal() {
		return listacodFinal;
	}
	/**
	 * Asigna la lista listacodFinal
	 * 
	 * @param listacodFinal
	 * Variable a asignar en  listacodFinal
	 */
	public void setListacodFinal(RegistroDataModelImpl listacodFinal) {
		this.listacodFinal = listacodFinal;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
}

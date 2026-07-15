/*-
 * FrmfacturacionlotesControlador.java
 *
 * 1.0
 * 
 * 28/05/2026
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.facturaciongeneral;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.facturaciongeneral.ejb.EjbFacturacionGeneralCuatroRemote;
import com.sysman.facturaciongeneral.enums.FrmfacturacionlotesControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.session.utl.ConstantesFacturacionGenEnum;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 28/05/2026
 * @author grojas
 */
@ManagedBean
@ViewScoped
public class  FrmfacturacionlotesControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	/**
	 * Variable encargada de almacenar temporalmente el ano de cobro
	 * de ingreso al modulo
	 */
	private String ano;
	/**
	 * Variable encargada de almacenar temporalmente el tipo de cobro
	 * de ingreso al modulo
	 */
	private String tipoCobro;
	//<DECLARAR_ATRIBUTOS>
	private String cobroInicial;
	private String cobroFinal;
	private Date fechaInicial;
	private Date fechaFinal;
	private String FechaIni;
	private String FechaFin;
	private String codigoEan;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listacobroInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listacobroFinal;
	/**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
	
	@EJB
	private EjbFacturacionGeneralCuatroRemote ejbFacturacionGeneralCuatro;
	
	@EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmfacturacionlotesControlador
	 */
	public FrmfacturacionlotesControlador() {
		super();
		compania = SessionUtil.getCompania();
		
		ano = (String) SessionUtil.getSessionVar(
				ConstantesFacturacionGenEnum.ANIO.getValue());

		tipoCobro = (String) SessionUtil.getSessionVar(
				ConstantesFacturacionGenEnum.TIPOCOBRO.getValue());
		
		try {
			numFormulario = GeneralCodigoFormaEnum.FRM_FACTURACION_LOTE
					.getCodigo();
			validarPermisos();
			//<INI_ADICIONAL>
			//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
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

		abrirFormulario();
		cargarListacobroInicial(); 
		cargarListacobroFinal();
	}
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){
		//<CODIGO_DESARROLLADO>
		fechaInicial = new Date();
		fechaFinal = new Date();
		
		//</CODIGO_DESARROLLADO>
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listacobroInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacobroInicial(){
		
		try {
		FechaIni = SysmanFunciones
				.convertirAFechaCadena(fechaInicial);
		FechaFin = SysmanFunciones.convertirAFechaCadena(fechaFinal);
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(FrmfacturacionlotesControladorUrlEnum.URL666022.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), ano);
		param.put(GeneralParameterEnum.TIPOCOBRO.getName(), tipoCobro);
		param.put(GeneralParameterEnum.FECHAINICIAL.getName(), FechaIni);
		param.put(GeneralParameterEnum.FECHAFINAL.getName(), FechaFin);

		listacobroInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				GeneralParameterEnum.CODIGO_COBRO.getName());
		}
		catch (ParseException  e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		
	}
	/**
	 * 
	 * Carga la lista listacobroFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListacobroFinal(){
		try {

			FechaIni = SysmanFunciones
					.convertirAFechaCadena(fechaInicial);
			FechaFin = SysmanFunciones.convertirAFechaCadena(fechaFinal);

			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(FrmfacturacionlotesControladorUrlEnum.URL666024.getValue());
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ANO.getName(), ano);
			param.put(GeneralParameterEnum.TIPOCOBRO.getName(), tipoCobro);
			param.put(GeneralParameterEnum.FECHAINICIAL.getName(), FechaIni);
			param.put(GeneralParameterEnum.FECHAFINAL.getName(), FechaFin);
			param.put("CODIGOCOBRO", cobroInicial);

			listacobroFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
					GeneralParameterEnum.CODIGO_COBRO.getName());
		}
		catch (ParseException  e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton facturar
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirfacturar() {

		try
		{
			ejbFacturacionGeneralCuatro.factutacionLote(compania, Integer.parseInt(ano), tipoCobro, 
					cobroInicial, cobroFinal, SessionUtil.getUser().getCodigo());
			
			genInforme(FORMATOS.PDF);

			JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));

		}
		catch (SystemException e)
		{
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

		
	}
	
	public void genInforme(ReportesBean.FORMATOS formato)
	{
		try {

			archivoDescarga = null;
			String informe = obtenerFormato();

			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param.put(GeneralParameterEnum.ANO.getName(), ano);
			param.put(GeneralParameterEnum.CODIGO.getName(), tipoCobro);
			param.put(GeneralParameterEnum.TIPOCOBRO.getName(), tipoCobro);
			param.put("NUMEROCOBRO", cobroInicial);
			Registro rsIni = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmfacturacionlotesControladorUrlEnum.URL661073
									.getValue())
							.getUrl(),param));

			String facturaInicial = rsIni.getCampos().get("NUMERO_FACTURA").toString();

			Map<String, Object> param2 = new TreeMap<>();
			param2.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			param2.put(GeneralParameterEnum.ANO.getName(), ano);
			param2.put(GeneralParameterEnum.CODIGO.getName(), tipoCobro);
			param2.put(GeneralParameterEnum.TIPOCOBRO.getName(), tipoCobro);
			param2.put("NUMEROCOBRO", cobroFinal);
			Registro rsFin = RegistroConverter
					.toRegistro(requestManager.get(
							UrlServiceUtil.getInstance()
							.getUrlServiceByUrlByEnumID(
									FrmfacturacionlotesControladorUrlEnum.URL661073
									.getValue())
							.getUrl(),param2));

			String facturaFinal = rsFin.getCampos().get("NUMERO_FACTURA").toString();

			Map<String, Object> reemplazar = new HashMap<>();

			if (ejbSysmanUtil.consultarParametro(compania,
					"SF CODIGO EAN POR CADA TIPO DE COBRO",
					SessionUtil.getModulo(), new Date(), false)
					.equals("NO"))
			{

				codigoEan = SysmanFunciones
						.nvl(ejbSysmanUtil.consultarParametro(compania,
								"SF CODIGO EAN",
								SessionUtil.getModulo(),
								new Date(), false), "")
						.toString();
			}
			else
			{
				Map<String, Object> param3 = new TreeMap<>();
				param3.put(GeneralParameterEnum.COMPANIA.getName(), compania);
				param3.put("ANO", ano);
				param3.put(GeneralParameterEnum.CODIGO.getName(), tipoCobro);
				Registro rs = RegistroConverter
						.toRegistro(requestManager.get(
								UrlServiceUtil.getInstance()
								.getUrlServiceByUrlByEnumID(
										FrmfacturacionlotesControladorUrlEnum.URL665023
										.getValue())
								.getUrl(),
								param3));
				codigoEan = rs.getCampos().get("CODIGOEAN").toString();
			}

			reemplazar.put("codigoEan", codigoEan);
			reemplazar.put("anio", ano);
			reemplazar.put("tipoFactura", tipoCobro);
			reemplazar.put("facturaInicial", facturaInicial);
			reemplazar.put("facturaFinal", facturaFinal);
			reemplazar.put("compania", compania);

			Map<String, Object> parametros = new HashMap<>();
			parametros.put("PR_NOMBRECOMPANIA",
					SessionUtil.getCompaniaIngreso().getNombre());
			parametros.put("PR_NITCOMPANIA",
					SessionUtil.getCompaniaIngreso().getNit());

			parametros.put("PR_DIRECCIONCOMPANIA",
					SessionUtil.getCompaniaIngreso().getDireccion());
			parametros.put("PR_TELEFONOCOMPANIA",
					SessionUtil.getCompaniaIngreso().getTelefono());
			parametros.put("PR_CIUDADCOMPANIA",
					SessionUtil.getCompaniaIngreso().getCiudad());
			parametros.put("PR_USUARIO", SessionUtil.getUser().getCodigo());

			parametros.put("PR_CUENTABANCO1",
					obtenerParametro("SF BANCO CUENTA 1", ""));
			parametros.put("PR_CUENTABANCO2",
					obtenerParametro("SF BANCO CUENTA 2", ""));
			parametros.put("PR_CUENTABANCO3",
					obtenerParametro("SF BANCO CUENTA 3", ""));
			parametros.put("PR_CUENTABANCO4",
					obtenerParametro("SF BANCO CUENTA 4", ""));

			Reporteador.resuelveConsulta(informe,
					Integer.parseInt(SessionUtil.getModulo()),
					reemplazar, parametros);
			archivoDescarga = JsfUtil.exportarStreamed(informe, parametros,
					ConectorPool.ESQUEMA_SYSMAN, formato);

		}
		catch (SystemException | JRException | IOException
                | SysmanException e)
		{
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
	
	private String obtenerFormato()
    {
        String formato = "";
        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        params.put(GeneralParameterEnum.ANO.getName(),ano);
        params.put(GeneralParameterEnum.TIPOCOBRO.getName(),tipoCobro);

        try
        {
        	Registro rs = RegistroConverter
        			.toRegistro(requestManager.get(
        					UrlServiceUtil.getInstance()
        					.getUrlServiceByUrlByEnumID(
        							FrmfacturacionlotesControladorUrlEnum.URL665010
        							.getValue())
        					.getUrl(),
        					params));

            if ((rs != null)
                && !SysmanFunciones.validarCampoVacio(rs.getCampos(),
                                "FORMATO_FACTURA"))
            {
                formato = rs.getCampos().get("FORMATO_FACTURA").toString();
            }
            else
            {
                formato = obtenerParametro("SF FORMATO FACTURACION",
                                "001493INFFACSTD010");
            }

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return formato;
    }
	
	private String obtenerParametro(String nombreParametro,
        String valorDefault)
    {
        String parametro = null;
        try
        {
            parametro = ejbSysmanUtil.consultarParametro(compania,
                            nombreParametro, SessionUtil.getModulo(),
                            new Date(),
                            true);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro != null ? parametro : valorDefault;
    }

	/**
	 * Metodo ejecutado al cambiar el control fechaInicial
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarfechaInicial() {
		//<CODIGO_DESARROLLADO>
		cargarListacobroInicial();
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control fechaFinal
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarfechaFinal() {
		//<CODIGO_DESARROLLADO>
		cargarListacobroInicial();
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listacobroInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacobroInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cobroInicial = SysmanFunciones.toString(registroAux.getCampos().get("CODIGO_COBRO"));
		cobroFinal = null;
		cargarListacobroFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listacobroFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilacobroFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cobroFinal = SysmanFunciones.toString(registroAux.getCampos().get("CODIGO_COBRO"));
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable cobroInicial
	 * 
	 * @return  cobroInicial
	 */
	public String getCobroInicial() {
		return cobroInicial;
	}
	/**
	 * Asigna la variable  cobroInicial
	 * 
	 * @param  cobroInicial
	 * Variable a asignar en  cobroInicial
	 */
	public void setCobroInicial(String cobroInicial) {
		this.cobroInicial = cobroInicial;
	}
	/**
	 * Retorna la variable cobroFinal
	 * 
	 * @return  cobroFinal
	 */
	public String getCobroFinal() {
		return cobroFinal;
	}
	/**
	 * Asigna la variable  cobroFinal
	 * 
	 * @param  cobroFinal
	 * Variable a asignar en  cobroFinal
	 */
	public void setCobroFinal(String cobroFinal) {
		this.cobroFinal = cobroFinal;
	}
	/**
	 * Retorna la variable fechaInicial
	 * 
	 * @return  fechaInicial
	 */
	public Date getFechaInicial() {
		return fechaInicial;
	}
	/**
	 * Asigna la variable  fechaInicial
	 * 
	 * @param  fechaInicial
	 * Variable a asignar en  fechaInicial
	 */
	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}
	/**
	 * Retorna la variable fechaFinal
	 * 
	 * @return  fechaFinal
	 */
	public Date getFechaFinal() {
		return fechaFinal;
	}
	/**
	 * Asigna la variable  fechaFinal
	 * 
	 * @param  fechaFinal
	 * Variable a asignar en  fechaFinal
	 */
	public void setFechaFinal(Date fechaFinal) {
		this.fechaFinal = fechaFinal;
	}
	//</SET_GET_ATRIBUTOS>
	/**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listacobroInicial
	 * 
	 * @return listacobroInicial
	 */
	public RegistroDataModelImpl getListacobroInicial() {
		return listacobroInicial;
	}
	/**
	 * Asigna la lista listacobroInicial
	 * 
	 * @param listacobroInicial
	 * Variable a asignar en  listacobroInicial
	 */
	public void setListacobroInicial(RegistroDataModelImpl listacobroInicial) {
		this.listacobroInicial = listacobroInicial;
	}
	/**
	 * Retorna la lista listacobroFinal
	 * 
	 * @return listacobroFinal
	 */
	public RegistroDataModelImpl getListacobroFinal() {
		return listacobroFinal;
	}
	/**
	 * Asigna la lista listacobroFinal
	 * 
	 * @param listacobroFinal
	 * Variable a asignar en  listacobroFinal
	 */
	public void setListacobroFinal(RegistroDataModelImpl listacobroFinal) {
		this.listacobroFinal = listacobroFinal;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
}

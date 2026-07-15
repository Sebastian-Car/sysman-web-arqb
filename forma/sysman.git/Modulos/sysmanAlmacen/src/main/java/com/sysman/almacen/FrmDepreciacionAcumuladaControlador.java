/*-
 * FrmDepreciacionAcumuladaControlador.java
 *
 * 1.0
 * 
 * 17/07/2025
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

import com.sysman.almacen.ejb.EjbAlmacenCincoRemote;
import com.sysman.almacen.enums.FrmDepreciacionAcumuladaControladorEnum;
import com.sysman.almacen.enums.FrmDepreciacionAcumuladaControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import net.sf.jasperreports.engine.JRException;
import org.primefaces.model.StreamedContent;
/**
 *
 * @version 1.0, 17/07/2025
 * @author User 1
 */
@ManagedBean
@ViewScoped
public class  FrmDepreciacionAcumuladaControlador  extends BeanBaseContinuoAcmeImpl{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania;
	private final String modulo;
	 /**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
	private int indice;
	//<DECLARAR_ATRIBUTOS>
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>

	private RegistroDataModelImpl listaElemento;
	private RegistroDataModelImpl listaElementoE;
	private RegistroDataModelImpl listaPlaca;
	private RegistroDataModelImpl listaPlacaE;
	/**
	 * Esta variable se usa como auxiliar para 
	 * subformularios y en esta se alamcena el
	 * identificador del registro que se selecciono
	 */
	private String auxiliar;
	private String elemento;
	private String valor;
	private String vidaUtil;
	private String reporte;
	
	private boolean bloqEditar = false;
	
	@EJB
	private EjbAlmacenCincoRemote cincoRemote;
	
	@EJB
	private EjbSysmanUtilRemote sysmanUtil;
	private String vlrLibros;
	private BigDecimal saldoDepreciar;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmDepreciacionAcumuladaControlador
	 */
	public FrmDepreciacionAcumuladaControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			//2524
			numFormulario = GeneralCodigoFormaEnum.FRM_DEPRECIACION_ACUMULADA_CONTROLADOR.getCodigo();
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
		enumBase = GenericUrlEnum.DEPRECIACION_ACUMULADA;
		reasignarOrigen();		    
		buscarLlave();
		registro= new Registro();
		//<CARGAR_LISTA>
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListaElemento(); cargarListaElementoE();
		//</CARGAR_LISTA_COMBO_GRANDE>
		abrirFormulario();
	}
	/**
	 * En este metodo se asigna al atributo origenDatos del bean base
	 * el valor de la consulta del formulario. Tambien carga la lista
	 * del formulario por primera vez
	 */
	@Override
	public void reasignarOrigen(){
		buscarUrls();
		parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	}
	//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaElemento
	 *
	 */
	public void cargarListaElemento(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmDepreciacionAcumuladaControladorUrlEnum.URL112044
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(FrmDepreciacionAcumuladaControladorEnum.TIPOELEMENTO.getValue(), "D,N,M,E");

		listaElemento = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, FrmDepreciacionAcumuladaControladorEnum.CODIGOELEMENTO.getValue());
	}
	/**
	 * 
	 * Carga la lista listaElemento
	 *
	 */
	public void  cargarListaElementoE(){
		listaElementoE = listaElemento;
	}
	/**
	 * 
	 * Carga la lista listaPlaca
	 *
	 */
	public void cargarListaPlaca(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmDepreciacionAcumuladaControladorUrlEnum.URL141158
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ELEMENTO.getName(), registro.getCampos().get(GeneralParameterEnum.ELEMENTO.getName()));

		listaPlaca = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.SERIE.getName());
	}
	/**
	 * 
	 * Carga la lista listaPlaca
	 *
	 */
	public void  cargarListaPlacaE(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmDepreciacionAcumuladaControladorUrlEnum.URL141158
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ELEMENTO.getName(), elemento);

		listaPlacaE = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.SERIE.getName());
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * Metodo ejecutado al oprimir el boton Pdf
	 * 
	 * 
	 * @param reg
	 * registro en el cual esta ubicado el boton oprimido dentro de la
	 * grilla
	 * @param indice
	 * indice en el cual esta ubicado el boton oprimido dentro de la
	 * grilla
	 */
	public void oprimirPdf(Registro reg, int indice) {
		//<CODIGO_DESARROLLADO>
		try {
			archivoDescarga = null;     
			HashMap<String, Object> reemplazar = new HashMap<>();
			HashMap<String, Object> parametros = new HashMap<>();
			reporte = "002812DepreciacionAcumNoCalculada"; 

			reemplazar.put("elemento", reg.getCampos().get(GeneralParameterEnum.ELEMENTO.getName())); 
			reemplazar.put("serie", reg.getCampos().get(GeneralParameterEnum.SERIE.getName()));  

			parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso().getNombre().toUpperCase());
			parametros.put("PR_LUGAR", SessionUtil.getCompaniaIngreso().getCiudad());
			parametros.put("PR_RESPONSABLE_ALMACEN", sysmanUtil.consultarParametro(compania, "RESPONSABLE ALMACEN", modulo, new Date(), false));
			
			Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo), reemplazar, parametros);
			
			archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros, ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);
		}
		catch (JRException | IOException | com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException | SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		} 
		//</CODIGO_DESARROLLADO>
	}
	
	 /**
     * Abre el formulario modal de carga de plantilla.
     * 
     *
     */
    public void oprimirCargarPlantilla() {
        String[] campos = { "modulo" };
        Object[] valores = { modulo };

        SessionUtil.cargarModalDatosFlash(
            Integer.toString(GeneralCodigoFormaEnum.FRM_SUBIR_DEPRECIACION_ACUMULADA_CONTROLADOR.getCodigo()),
            modulo,
            campos,
            valores
        );
    }
    
    /**
     * Se ejecuta al cerrar el modal de carga.
     * Recarga la lista inicial para actualizar la informacion en el formulario padre.
     */
    public void retornarFormularioCargarPlantilla(SelectEvent event) {
        listaInicial.load();
    }
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>

	/**
	 * Metodo ejecutado al cambiar el control Periodo
	 * 
	 * 
	 */
	public void cambiarPeriodo() {
		//<CODIGO_DESARROLLADO>
		Map<String, Object> campos = registro.getCampos();
		verificarDepreciacion(campos);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control ValorAcum
	 * 
	 * 
	 */
	public void cambiarValorAcum() {
		//<CODIGO_DESARROLLADO>
		Map<String, Object> campos = registro.getCampos();
		saldoPorDepreciar(campos);
		registro.getCampos().put(FrmDepreciacionAcumuladaControladorEnum.VALOR_LIBROS.getValue(), saldoDepreciar);

		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control Placa en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * 
	 * @param rowNum
	 * indice de la fila seleccionada
	 */
	public void cambiarPlacaC(int rowNum) {
		//<CODIGO_DESARROLLADO>
		listaInicial.getDatasource().get(rowNum % 10).getCampos().put(FrmDepreciacionAcumuladaControladorEnum.VALOR_HISTORICO.getValue(), valor);
		listaInicial.getDatasource().get(rowNum % 10).getCampos().put(FrmDepreciacionAcumuladaControladorEnum.VIDA_UTIL.getValue(), vidaUtil); 
		listaInicial.getDatasource().get(rowNum % 10).getCampos().put(FrmDepreciacionAcumuladaControladorEnum.NIIF_VLRLIBROS.getValue(), vlrLibros); 
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control Periodo en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * 
	 * @param rowNum
	 * indice de la fila seleccionada
	 */
	public void cambiarPeriodoC(int rowNum) {
		//<CODIGO_DESARROLLADO>
		Map<String, Object> campos = listaInicial.getDatasource().get(rowNum % 10).getCampos();
		verificarDepreciacion(campos);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control ValorAcum en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * 
	 * @param rowNum
	 * indice de la fila seleccionada
	 */
	public void cambiarValorAcumC(int rowNum) {
		//<CODIGO_DESARROLLADO>
		Map<String, Object> campos = listaInicial.getDatasource().get(rowNum % 10).getCampos();
		saldoPorDepreciar(campos);
		listaInicial.getDatasource().get(rowNum % 10).getCampos().put(FrmDepreciacionAcumuladaControladorEnum.VALOR_LIBROS.getValue(), saldoDepreciar); 
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaElemento
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaElemento(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(GeneralParameterEnum.ELEMENTO.getName(), registroAux.getCampos().get(FrmDepreciacionAcumuladaControladorEnum.CODIGOELEMENTO.getValue()));
		registro.getCampos().put(FrmDepreciacionAcumuladaControladorEnum.NOMBREELEMENTO.getValue(), registroAux.getCampos().get(FrmDepreciacionAcumuladaControladorEnum.NOMBRELARGO.getValue()));
		
		cargarListaPlaca();
		registro.getCampos().put(GeneralParameterEnum.SERIE.getName(), null);
		registro.getCampos().put(FrmDepreciacionAcumuladaControladorEnum.VALOR_HISTORICO.getValue(), null);
		registro.getCampos().put(FrmDepreciacionAcumuladaControladorEnum.VIDA_UTIL.getValue(), null);
		registro.getCampos().put(GeneralParameterEnum.PERIODO.getName(), null);
		registro.getCampos().put(FrmDepreciacionAcumuladaControladorEnum.VALOR_ACUMULADO.getValue(), null);
		registro.getCampos().put(FrmDepreciacionAcumuladaControladorEnum.VALOR_LIBROS.getValue(), null);
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaElemento
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaElementoE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  (String) registroAux.getCampos().get("CODIGOELEMENTO");
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaPlaca
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaPlaca(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put(GeneralParameterEnum.SERIE.getName(), registroAux.getCampos().get(GeneralParameterEnum.SERIE.getName()));
		registro.getCampos().put(FrmDepreciacionAcumuladaControladorEnum.VALOR_HISTORICO.getValue(), registroAux.getCampos().get("VALOR"));
		registro.getCampos().put(FrmDepreciacionAcumuladaControladorEnum.VIDA_UTIL.getValue(), registroAux.getCampos().get("NIIF_VIDA_UTIL"));
		registro.getCampos().put(FrmDepreciacionAcumuladaControladorEnum.NIIF_VLRLIBROS.getValue(), registroAux.getCampos().get("NIIF_VLRLIBROS"));
		
		
		registro.getCampos().put(GeneralParameterEnum.PERIODO.getName(), null);
		registro.getCampos().put(FrmDepreciacionAcumuladaControladorEnum.VALOR_ACUMULADO.getValue(), null);
		registro.getCampos().put(FrmDepreciacionAcumuladaControladorEnum.VALOR_LIBROS.getValue(), null);
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaPlaca
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaPlacaE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar = SysmanFunciones.toString(registroAux.getCampos().get("SERIE"));
		valor = SysmanFunciones.toString(registroAux.getCampos().get("VALOR"));
		vidaUtil = SysmanFunciones.toString(registroAux.getCampos().get("NIIF_VIDA_UTIL"));
		vlrLibros = SysmanFunciones.toString(registroAux.getCampos().get("NIIF_VLRLIBROS"));
	}
	//</METODOS_COMBOS_GRANDES>
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
	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
	 */
	@Override
	public void cancelarEdicion(RowEditEvent event) {
		JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));
		getListaInicial().load();
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion del registro
	 * 
	 */
	@Override
	public boolean insertarAntes(){
		//<CODIGO_DESARROLLADO>
		
		Map<String, Object> campos = registro.getCampos();
		
		existedepreciacion(campos);
		
		
		if(verificarDepreciacion(campos)) {
			return false;
		}
		registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
		registro.getCampos().remove(FrmDepreciacionAcumuladaControladorEnum.VALOR_HISTORICO.getValue());
		registro.getCampos().remove(FrmDepreciacionAcumuladaControladorEnum.VIDA_UTIL.getValue());
		registro.getCampos().remove(FrmDepreciacionAcumuladaControladorEnum.NOMBREELEMENTO.getValue());
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 * 
	 */
	@Override
	public boolean insertarDespues(){
		//<CODIGO_DESARROLLADO>
		generarDepreciacion("I");
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion y actualizacion
	 * del registro
	 * 
	 * 
	 */
	@Override
	public boolean actualizarAntes(){
		//<CODIGO_DESARROLLADO>
		Map<String, Object> campos = registro.getCampos();
		if(verificarDepreciacion(campos)) {
			return false;
		}
		registro.getCampos().remove(FrmDepreciacionAcumuladaControladorEnum.VALOR_HISTORICO.getValue());
		registro.getCampos().remove(FrmDepreciacionAcumuladaControladorEnum.VIDA_UTIL.getValue());
		registro.getCampos().remove(FrmDepreciacionAcumuladaControladorEnum.NOMBREELEMENTO.getValue());
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion
	 * del registro
	 * 
	 * 
	 */
	@Override   
	public boolean actualizarDespues(){
		//<CODIGO_DESARROLLADO>
		generarDepreciacion("U");
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado antes de realizar la eliminacion del
	 * registro
	 * 
	 * 
	 */
	@Override    
	public boolean eliminarAntes(){
		//<CODIGO_DESARROLLADO>
		Map<String, Object> campos = registro.getCampos();
		if(depreciacionPosterior(campos)) {
			return false;
		}
		generarDepreciacion("E");
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la eliminacion del
	 * registro
	 * 
	 * 
	 */
	@Override   
	public boolean eliminarDespues(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Este metodo se ejecuta antes enviar la accion de actualizacion,
	 * en el se pueden remover valores auxiliares que no se desee o se
	 * deban enviar en el registro
	 */
	@Override
	public void removerCombos() {
		/*No se utiliza*/
	}
	 /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     * 
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro) {
    	Map<String, Object> campos = registro.getCampos();
        indice = listaInicial.getRowIndex();
        listaInicial.getDatasource().get(indice).getCampos();
        elemento = SysmanFunciones.toString(registro.getCampos().get(GeneralParameterEnum.ELEMENTO.getName()));
        cargarListaPlacaE();
        bloqEditar = depreciacionPosterior(campos);
    }
	/**
	 * Este metodo es ejecutado despues de finalizar la insercion y
	 * edicion del registro se usa cuando se desean agregar valores
	 * al registro despues de dichas acciones
	 */
	@Override
	public void asignarValoresRegistro(){
		//No se utiliza.
	}
	
	public void generarDepreciacion(String accion) {

		try {
			Date fecha = (Date) registro.getCampos().get(GeneralParameterEnum.PERIODO.getName());
			String serie = SysmanFunciones.toString(registro.getCampos().get(GeneralParameterEnum.SERIE.getName()));
			String codElemento = SysmanFunciones.toString(registro.getCampos().get(GeneralParameterEnum.ELEMENTO.getName()));
			int anio = SysmanFunciones.ano(fecha);
			int mes =  SysmanFunciones.mes(fecha);
			
			cincoRemote.depreciacionAcumulada(compania, codElemento, serie, mes, anio, accion, SessionUtil.getUser().getCodigo());
			
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
	
	public boolean verificarDepreciacion(Map<String, Object> campos) {
	    try {
	        Date fecha = (Date) campos.get(GeneralParameterEnum.PERIODO.getName());
	        String serie = SysmanFunciones.toString(campos.get(GeneralParameterEnum.SERIE.getName()));
	        String codElemento = SysmanFunciones.toString(campos.get(GeneralParameterEnum.ELEMENTO.getName()));
	        int anio = SysmanFunciones.ano(fecha);
	        int mes = SysmanFunciones.mes(fecha);

	        Map<String, Object> param = new HashMap<>();
	        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	        param.put(GeneralParameterEnum.SERIE.getName(), serie);
	        param.put(GeneralParameterEnum.ELEMENTO.getName(), codElemento);
	        param.put(GeneralParameterEnum.MES.getName(), mes);
	        param.put(GeneralParameterEnum.ANO.getName(), anio);

	        Registro rs = RegistroConverter.toRegistro(
	                requestManager.get(
	                    UrlServiceUtil.getInstance()
	                        .getUrlServiceByUrlByEnumID(FrmDepreciacionAcumuladaControladorUrlEnum.URL179003.getValue())
	                        .getUrl(),
	                    param
	                )
	        );

	        if (rs != null) {
	            String existe = SysmanFunciones.toString(rs.getCampos().get(GeneralParameterEnum.VALOR.getName()));
	            if ("0".equals(existe)) {
	                JsfUtil.agregarMensajeAlerta("No fue posible procesar la operación para el elemento con código: " + codElemento
	                		+ " y placa: " + serie
	                		+ ". Verifique si ya existe una depreciación registrada para el período seleccionado o si el elemento no cuenta con depreciación generada.");
	                return true;
	            }
	        }
	    } catch (SystemException e) {
	        logger.error(e.getMessage(), e);
	        JsfUtil.agregarMensajeError(e.getMessage());
	    }
	    return false;
	}
	
	
	public void existedepreciacion(Map<String, Object> campos) {
	    try {
	        Date fecha = (Date) campos.get(GeneralParameterEnum.PERIODO.getName());
	        String serie = SysmanFunciones.toString(campos.get(GeneralParameterEnum.SERIE.getName()));
	        String codElemento = SysmanFunciones.toString(campos.get(GeneralParameterEnum.ELEMENTO.getName()));
	        int anio = SysmanFunciones.ano(fecha);
	        int mes = SysmanFunciones.mes(fecha);
	      
	        Map<String, Object> param = new HashMap<>();
	        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	        param.put(GeneralParameterEnum.SERIE.getName(), serie);
	        param.put(GeneralParameterEnum.ELEMENTO.getName(), codElemento);
	        param.put(GeneralParameterEnum.MES.getName(), mes);
	        param.put(GeneralParameterEnum.ANO.getName(), anio);

	        Registro rs = RegistroConverter.toRegistro(
	                requestManager.get(
	                    UrlServiceUtil.getInstance()
	                        .getUrlServiceByUrlByEnumID(FrmDepreciacionAcumuladaControladorUrlEnum.URL179008.getValue())
	                        .getUrl(),
	                    param
	                )
	        );

	        if (rs != null) {
	            String existe = SysmanFunciones.toString(rs.getCampos().get(GeneralParameterEnum.VALOR.getName()));
	            if ("0".equals(existe)) {
	            	cincoRemote.depreciacionInicial(compania, codElemento, serie, mes, anio, SessionUtil.getUser().getCodigo());
	                
	            }
	        }
	    } catch (SystemException e) {
	        logger.error(e.getMessage(), e);
	        JsfUtil.agregarMensajeError(e.getMessage());
	    }
	   
	}
	
	
	public boolean saldoPorDepreciar(Map<String, Object> campos) {
		
		Object valorHistoricoObj = campos.get(FrmDepreciacionAcumuladaControladorEnum.VALOR_HISTORICO.getValue());
		Object valorAcumuladoObj = campos.get(FrmDepreciacionAcumuladaControladorEnum.VALOR_ACUMULADO.getValue());

		BigDecimal valorHistorico = new BigDecimal(valorHistoricoObj.toString());
		BigDecimal valorAcumulado = new BigDecimal(valorAcumuladoObj.toString());

		saldoDepreciar = valorHistorico.subtract(valorAcumulado);
		if (saldoDepreciar.compareTo(BigDecimal.ZERO) < 0) {
		    JsfUtil.agregarMensajeAlerta("El saldo por depreciar es negativo. Por favor revise los valores históricos y acumulados.");
		    return true;
		}
		return false;
	}
	
	public boolean depreciacionPosterior(Map<String, Object> campos) {
	    try {
	        Date fecha = (Date) campos.get(GeneralParameterEnum.PERIODO.getName());
	        String serie = SysmanFunciones.toString(campos.get(GeneralParameterEnum.SERIE.getName()));
	        String codElemento = SysmanFunciones.toString(campos.get(GeneralParameterEnum.ELEMENTO.getName()));
	        int anio = SysmanFunciones.ano(fecha);
	        int mes = SysmanFunciones.mes(fecha);

	        Map<String, Object> param = new HashMap<>();
	        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	        param.put(GeneralParameterEnum.SERIE.getName(), serie);
	        param.put(GeneralParameterEnum.ELEMENTO.getName(), codElemento);
	        param.put(GeneralParameterEnum.MES.getName(), mes);
	        param.put(GeneralParameterEnum.ANO.getName(), anio);

	        Registro rs = RegistroConverter.toRegistro(
	                requestManager.get(
	                    UrlServiceUtil.getInstance()
	                        .getUrlServiceByUrlByEnumID(FrmDepreciacionAcumuladaControladorUrlEnum.URL179004.getValue())
	                        .getUrl(),
	                    param
	                )
	        );

	        if (rs != null) {
	            int existe = Integer.parseInt(SysmanFunciones.toString(rs.getCampos().get(GeneralParameterEnum.VALOR.getName())));
	            if (existe > 0) {
	                JsfUtil.agregarMensajeAlerta("Depreciación existente detectada para el devolutivo en periodos posteriores. Acción no permitida.");
	                return true;
	            }
	        }
	    } catch (SystemException e) {
	        logger.error(e.getMessage(), e);
	        JsfUtil.agregarMensajeError(e.getMessage());
	    }
	    return false;
	}
	//<SET_GET_ATRIBUTOS>
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
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la variable auxiliar
	 * 
	 * @return auxiliar
	 */
	public String getAuxiliar() {
		return auxiliar;
	}
	/**
	 * @return the listaElemento
	 */
	public RegistroDataModelImpl getListaElemento() {
		return listaElemento;
	}
	/**
	 * @param listaElemento the listaElemento to set
	 */
	public void setListaElemento(RegistroDataModelImpl listaElemento) {
		this.listaElemento = listaElemento;
	}
	/**
	 * @return the listaElementoE
	 */
	public RegistroDataModelImpl getListaElementoE() {
		return listaElementoE;
	}
	/**
	 * @param listaElementoE the listaElementoE to set
	 */
	public void setListaElementoE(RegistroDataModelImpl listaElementoE) {
		this.listaElementoE = listaElementoE;
	}
	/**
	 * @return the listaPlaca
	 */
	public RegistroDataModelImpl getListaPlaca() {
		return listaPlaca;
	}
	/**
	 * @param listaPlaca the listaPlaca to set
	 */
	public void setListaPlaca(RegistroDataModelImpl listaPlaca) {
		this.listaPlaca = listaPlaca;
	}
	/**
	 * @return the listaPlacaE
	 */
	public RegistroDataModelImpl getListaPlacaE() {
		return listaPlacaE;
	}
	/**
	 * @param listaPlacaE the listaPlacaE to set
	 */
	public void setListaPlacaE(RegistroDataModelImpl listaPlacaE) {
		this.listaPlacaE = listaPlacaE;
	}
	/**
	 * Asigna la variable auxiliar
	 * 
	 * @param auxiliar
	 * Variable a asignar en auxiliar
	 */
	public void setAuxiliar(String auxiliar) {
		this.auxiliar= auxiliar;
	}
	/**
	 * @return the indice
	 */
	public int getIndice() {
		return indice;
	}
	/**
	 * @param indice the indice to set
	 */
	public void setIndice(int indice) {
		this.indice = indice;
	}
	/**
	 * @return the bloqEditar
	 */
	public boolean isBloqEditar() {
		return bloqEditar;
	}
	/**
	 * @param bloqEditar the bloqEditar to set
	 */
	public void setBloqEditar(boolean bloqEditar) {
		this.bloqEditar = bloqEditar;
	}
	
	//</SET_GET_LISTAS_COMBO_GRANDE>
}

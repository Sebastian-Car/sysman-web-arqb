/*-
 * FrmDepreciacionAcumuladaInicialControlador.java
 *
 * 1.0
 * 
 * 19/08/2025
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
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
import com.sysman.almacen.enums.FrmDepreciacionAcumuladaInicialControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;
/**
 *
 * @version 1.0, 19/08/2025
 * @author User 1
 */
@ManagedBean
@ViewScoped
public class  FrmDepreciacionAcumuladaInicialControlador  extends BeanBaseContinuoAcmeImpl{
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
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_LISTAS>
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	private RegistroDataModelImpl listaElemento;
	private RegistroDataModelImpl listaElementoE;
	private RegistroDataModelImpl listaSerie;
	private RegistroDataModelImpl listaSerieE;
	/**
	 * Esta variable se usa como auxiliar para 
	 * subformularios y en esta se alamcena el
	 * identificador del registro que se selecciono
	 */
	private String auxiliar;

	@EJB
	private EjbSysmanUtilRemote sysmanUtil;
	
	@EJB
	private EjbAlmacenCincoRemote cincoRemote;
	
	private String fechaCorte;
	private Date ultimoDiaCorte;
	private BigDecimal saldoDepreciar;
	private Object estadoAlmacen;
	private boolean bloqueadoPeriodo;
	private boolean mostrarAcme;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de FrmDepreciacionAcumuladaInicialControlador
	 */
	public FrmDepreciacionAcumuladaInicialControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			//2534
			numFormulario = GeneralCodigoFormaEnum.FRM_DEPRECIACION_ACUMULADA_INICIAL_CONTROLADOR.getCodigo();
			validarPermisos();
			//<INI_ADICIONAL>
			//</INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			SessionUtil.redireccionarMenuPermisos();
		} finally {
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
		enumBase = GenericUrlEnum.DEPRECIACION_ACUMULADA_INICIAL;
		reasignarOrigen();		    
		buscarLlave();
		registro = new Registro();
		//<CARGAR_LISTA>
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListaElemento();
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
	/**
	 * Retorna la variable indice 
	 * @return indice
	 */
	public int getIndice() {
		return indice;
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
						FrmDepreciacionAcumuladaInicialControladorUrlEnum.URL112006
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
	 * Carga la lista listaSerie
	 *
	 */
	public void cargarListaSerie(){
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						FrmDepreciacionAcumuladaInicialControladorUrlEnum.URL141160
						.getValue());
        int anio = SysmanFunciones.ano(ultimoDiaCorte);
        int mes = SysmanFunciones.mes(ultimoDiaCorte);
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ELEMENTO.getName(), registro.getCampos().get(GeneralParameterEnum.ELEMENTO.getName()));
		param.put(GeneralParameterEnum.FECHA.getName(), fechaCorte);
        param.put(GeneralParameterEnum.MES.getName(), mes);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

		listaSerie = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, GeneralParameterEnum.SERIE.getName());
	}
	/**
	 * 
	 * Carga la lista listaSerie
	 *
	 */
	public void  cargarListaSerieE(){
		listaSerieE = listaSerie;
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton CargarExcel
	 * en la vista
	 *
	 *
	 */
	public void oprimirCargarExcel() {
		//<CODIGO_DESARROLLADO>
		String[] campos = {};
		String[] valores = {};
		
    	SessionUtil.cargarModalDatosFlash(
    			String.valueOf(GeneralCodigoFormaEnum.FRM_SUBIR_DEPRECIACION_INICIAL_CONTROLADOR
    					.getCodigo()),
    			SessionUtil.getModulo(), campos, valores);

		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control VidaUtil
	 * 
	 * 
	 */
	public void cambiarVidaUtil() {
		//<CODIGO_DESARROLLADO>
		Map<String, Object> campos = registro.getCampos();
		registro.getCampos().put("VALOR_PERIODO", valorPeriodo(campos));
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control ValorAcumulado
	 * 
	 * 
	 */
	public void cambiarValorAcumulado() {
		//<CODIGO_DESARROLLADO>
		Map<String, Object> campos = registro.getCampos();
		saldoPorDepreciar(campos);
		registro.getCampos().put("VALOR_DEPRECIAR", saldoDepreciar);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control VidaUtil en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * 
	 * @param rowNum
	 * indice de la fila seleccionada
	 */
	public void cambiarVidaUtilC(int rowNum) {
		//<CODIGO_DESARROLLADO>
		Map<String, Object> campos = listaInicial.getDatasource().get(rowNum % 10).getCampos();
		listaInicial.getDatasource().get(rowNum % 10).getCampos().put("VALOR_PERIODO", valorPeriodo(campos));
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado al cambiar el control ValorAcumulado en la fila
	 * seleccionada dentro de la grilla
	 * 
	 * 
	 * @param rowNum
	 * indice de la fila seleccionada
	 */
	public void cambiarValorAcumuladoC(int rowNum) {
		//<CODIGO_DESARROLLADO>		
		Map<String, Object> campos = listaInicial.getDatasource().get(rowNum % 10).getCampos();
		saldoPorDepreciar(campos);
		listaInicial.getDatasource().get(rowNum % 10).getCampos().put("VALOR_DEPRECIAR", saldoDepreciar); 
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
		registro.getCampos().put("ELEMENTO", registroAux.getCampos().get("CODIGOELEMENTO"));
		registro.getCampos().put("NOMBREELEMENTO", registroAux.getCampos().get("NOMBRELARGO"));

		cargarListaSerie();
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
	 * listaSerie
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaSerie(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		registro.getCampos().put("SERIE", registroAux.getCampos().get("SERIE"));
		registro.getCampos().put("VALOR_HISTORICO", registroAux.getCampos().get("VALOR"));
		registro.getCampos().put("VIDA_UTIL", registroAux.getCampos().get("NIIF_VIDA_UTIL"));
		registro.getCampos().put("FECHA_ADQUISICION", registroAux.getCampos().get("NIIF_FECHAADQUISICION"));
		registro.getCampos().put("VALOR_PERIODO", registroAux.getCampos().get("NIIF_VLRDEPRECIACION"));
		registro.getCampos().put("NIIF_VIDA_UTIL", registroAux.getCampos().get("NIIF_VIDA_UTIL"));
		registro.getCampos().put("NIIF_VLRLIBROS", registroAux.getCampos().get("NIIF_VLRLIBROS"));
		registro.getCampos().put("NIIF_VLRDEPRECIACION", registroAux.getCampos().get("NIIF_VLRDEPRECIACION"));
		registro.getCampos().put("NIIF_VLRACUMULADO", registroAux.getCampos().get("NIIF_VLRACUMULADO"));
		
		Map<String, Object> campos = registro.getCampos();
		verificarDepreciacion(campos);
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaSerie
	 *
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaSerieE(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliar =  (String) registroAux.getCampos().get("SERIE");
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
		try {
			fechaCorte = sysmanUtil.consultarParametro(compania, "FECHA DE CORTE PARA INICIO EN NIIF DEL ALMACEN",SessionUtil.getModulo(), new Date(), false);
			ultimoDiaCorte = SysmanFunciones.ultimoDiaDate(SysmanFunciones.convertirAFecha(fechaCorte));
			registro.getCampos().put("PERIODO_AFECTAR", ultimoDiaCorte);

			estadoAlmacen = sysmanUtil.verificarEstadoPeriodoMensual(
					compania, SysmanFunciones.ano(ultimoDiaCorte),
					SysmanFunciones.mes(ultimoDiaCorte), Integer.parseInt(SessionUtil.getModulo()),
					1);
			bloqueadoPeriodo = "A".equals(estadoAlmacen);
			mostrarAcme = bloqueadoPeriodo;
			if (!bloqueadoPeriodo) {
				
				String mensaje = "Periodo de inicio de almacén "+ fechaCorte +" se encuentra cerrado. No es posible realizar el proceso.";
				JsfUtil.agregarMensajeAlerta(mensaje);
			}

		} catch (SystemException | ParseException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
	 */
	@Override
	public void cancelarEdicion(RowEditEvent event) {
		getListaInicial().load();
	}
	
	public BigDecimal valorPeriodo(Map<String, Object> campos) {

		Object valorHistoricoObj = campos.get(FrmDepreciacionAcumuladaControladorEnum.VALOR_HISTORICO.getValue());
		Object vidaUtilObj = campos.get(FrmDepreciacionAcumuladaControladorEnum.VIDA_UTIL.getValue());

		BigDecimal valorHistorico = new BigDecimal(valorHistoricoObj.toString());
		BigDecimal vidaUtil = new BigDecimal(vidaUtilObj.toString());

		BigDecimal valorPeriodo = valorHistorico.divide(vidaUtil,2, RoundingMode.HALF_UP);
		
		return valorPeriodo;
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
	
	public boolean verificarDepreciacion(Map<String, Object> campos) {
	    try {
	        Date fecha = (Date) campos.get("PERIODO_AFECTAR");
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
	                        .getUrlServiceByUrlByEnumID(FrmDepreciacionAcumuladaInicialControladorUrlEnum.URL179008.getValue())
	                        .getUrl(),
	                    param
	                )
	        );

	        if (rs != null) {
	            String existe = SysmanFunciones.toString(rs.getCampos().get(GeneralParameterEnum.VALOR.getName()));
	            if ("0".equals(existe)) {
	                JsfUtil.agregarMensajeAlerta("No se encontró una depreciación registrada para el elemento con código: " + codElemento + " y placa: " + serie + " en el periodo de inicio de Almacén "+ SysmanFunciones.convertirAFechaCadena(ultimoDiaCorte) +".");
	                return true;
	            }
	        }
	    } catch (SystemException | ParseException e) {
	        logger.error(e.getMessage(), e);
	        JsfUtil.agregarMensajeError(e.getMessage());
	    }
	    return false;
	}
	
	public void generarDepreciacion(String accion) {

		try {
			Date fecha = (Date) registro.getCampos().get("PERIODO_AFECTAR");
			String serie = SysmanFunciones.toString(registro.getCampos().get(GeneralParameterEnum.SERIE.getName()));
			String codElemento = SysmanFunciones.toString(registro.getCampos().get(GeneralParameterEnum.ELEMENTO.getName()));
			int anio = SysmanFunciones.ano(fecha);
			int mes =  SysmanFunciones.mes(fecha);
			
			cincoRemote.depreciacionAcumuladaInicial(compania, codElemento, serie, mes, anio, accion, SessionUtil.getUser().getCodigo());
			
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}
	
	public boolean depreciacionAcumulada(Map<String, Object> campos) {

		Object valorAcumuladoObj = campos.get(FrmDepreciacionAcumuladaControladorEnum.VALOR_ACUMULADO.getValue());
		BigDecimal valorAcumulado = new BigDecimal(SysmanFunciones.toString(valorAcumuladoObj));

		if (valorAcumulado.compareTo(BigDecimal.ZERO) < 0) {
			JsfUtil.agregarMensajeAlerta("El valor de la Depreciación Acumulada no puede ser negativo. Ingrese un valor mayor a cero.");
			return true;
		}
		return false;
	}	
	
	/**
	 * Metodo ejecutado antes de realizar la insercion del registro
	 * 
	 */
	@Override
	public boolean insertarAntes(){
		//<CODIGO_DESARROLLADO>
		if(verificarDepreciacion(registro.getCampos())) {
			return false;
		}
		if(saldoPorDepreciar(registro.getCampos())) {
			return false;
		}
		if(depreciacionAcumulada(registro.getCampos())) {
			return false;
		}
		registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
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
		if(saldoPorDepreciar(registro.getCampos())) {
			return false;
		}
		if(depreciacionAcumulada(registro.getCampos())) {
			return false;
		}
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
		indice = listaInicial.getRowIndex();
        listaInicial.getDatasource().get(indice).getCampos();
	}
	/**
	 * Este metodo es ejecutado despues de finalizar la insercion y
	 * edicion del registro se usa cuando se desean agregar valores
	 * al registro despues de dichas acciones
	 */
	@Override
	public void asignarValoresRegistro()
	{
		registro.getCampos().put("PERIODO_AFECTAR", ultimoDiaCorte);
	}
	//<SET_GET_ATRIBUTOS>
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_LISTAS>
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaElemento
	 * 
	 * @return listaElemento
	 */
	public RegistroDataModelImpl getListaElemento() {
		return listaElemento;
	}
	/**
	 * Asigna la lista listaElemento
	 * 
	 * @param listaElemento
	 * Variable a asignar en  listaElemento
	 */
	public void setListaElemento(RegistroDataModelImpl listaElemento) {
		this.listaElemento = listaElemento;
	}
	/**
	 * Retorna la lista listaElemento
	 * 
	 * @return listaElemento
	 */
	public RegistroDataModelImpl getListaElementoE() {
		return listaElementoE;
	}
	/**
	 * Asigna la lista listaElemento
	 * 
	 * @param listaElemento
	 * Variable a asignar en  listaElemento
	 */
	public void setListaElementoE(RegistroDataModelImpl listaElementoE) {
		this.listaElementoE = listaElementoE;
	}
	/**
	 * Retorna la lista listaSerie
	 * 
	 * @return listaSerie
	 */
	public RegistroDataModelImpl getListaSerie() {
		return listaSerie;
	}
	/**
	 * Asigna la lista listaSerie
	 * 
	 * @param listaSerie
	 * Variable a asignar en  listaSerie
	 */
	public void setListaSerie(RegistroDataModelImpl listaSerie) {
		this.listaSerie = listaSerie;
	}
	/**
	 * Retorna la lista listaSerie
	 * 
	 * @return listaSerie
	 */
	public RegistroDataModelImpl getListaSerieE() {
		return listaSerieE;
	}
	/**
	 * Asigna la lista listaSerie
	 * 
	 * @param listaSerie
	 * Variable a asignar en  listaSerie
	 */
	public void setListaSerieE(RegistroDataModelImpl listaSerieE) {
		this.listaSerieE = listaSerieE;
	}
	/**
	 * Retorna la variable auxiliar
	 * 
	 * @return auxiliar
	 */
	public String getAuxiliar() {
		return auxiliar;
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
	//</SET_GET_LISTAS_COMBO_GRANDE>
	/**
	 * @param indice the indice to set
	 */
	public void setIndice(int indice) {
		this.indice = indice;
	}
	/**
	 * @return the mostrarAcme
	 */
	public boolean isMostrarAcme() {
		return mostrarAcme;
	}
	/**
	 * @param mostrarAcme the mostrarAcme to set
	 */
	public void setMostrarAcme(boolean mostrarAcme) {
		this.mostrarAcme = mostrarAcme;
	}
}

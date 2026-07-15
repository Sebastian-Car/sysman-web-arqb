/*-
 * InformeGeneralControladoControlador.java
 *
 * 1.0
 * 
 * 13/02/2026
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

import com.sysman.almacen.enums.InformeGeneralControladoControladorEnum;
import com.sysman.almacen.enums.InformeGeneralControladoControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
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
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;
/**
 * Esta clase es el controlador para el formulario INFORME GENERAL
 * DE CONSUMO CONTROLADO, el cual es llamado desde 
 * Almacen\Informes\Generales\Informe General De Consmumo Controlado
 *
 * @version 1.0, 13/02/2026
 * @author grojas
 */
@ManagedBean
@ViewScoped
public class  InformeGeneralControladoControlador extends BeanBaseModal{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ;
	/**
     * Constante a nivel de clase que almacena el codigo del modulo en
     * el cual esta trabajando el usuario, el valor de esta constante
     * es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String modulo;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo CODIGOELEMENTO en el formulario, almacena el
     * texto CODIGOELEMENTO
     */
    private final String cCodigoElemento;

    private final String tipoElemento;
	//<DECLARAR_ATRIBUTOS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private boolean ckExcelplano;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String elementoDesde;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String elementoHasta;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private Date fecha;

    private String fechaAux;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String nombreElementoDesde;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String nombreElementoHasta;
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
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaElementoDesde;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaElementoHasta;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * Crea una nueva instancia de InformeGeneralControladoControlador
	 */
	public InformeGeneralControladoControlador() {
		super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        tipoElemento = InformeGeneralControladoControladorEnum.ELEMENTO.getValue();
        cCodigoElemento = InformeGeneralControladoControladorEnum.CODIGOELEMENTO.getValue();
		try {
			numFormulario = GeneralCodigoFormaEnum.INFORME_GENERAL_CONSUMO_CONTROLADO_CONTROLADOR
                    .getCodigo();
			validarPermisos();
			fecha = new Date();
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
		//</CARGAR_LISTA>
		//<CARGAR_LISTA_COMBO_GRANDE>
		cargarListaElementoDesde(); 
		cargarListaElementoHasta();
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
	 * Carga la lista listaElementoDesde
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaElementoDesde(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						InformeGeneralControladoControladorUrlEnum.URL112044
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(InformeGeneralControladoControladorEnum.TIPOELEMENTO
				.getValue(), tipoElemento);
		
		listaElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, cCodigoElemento);
	}
	/**
	 * 
	 * Carga la lista listaElementoHasta
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 */
	public void cargarListaElementoHasta(){
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(
						InformeGeneralControladoControladorUrlEnum.URL112046
						.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(InformeGeneralControladoControladorEnum.TIPOELEMENTO
				.getValue(), tipoElemento);
		param.put(InformeGeneralControladoControladorEnum.ELEMENTOINICIAL
				.getValue(), elementoDesde);

		listaElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(),
				urlBean.getUrlConteo().getUrl(), param,
				true, cCodigoElemento);
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_BOTONES>
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BtnPdf
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirBtnPdf() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;
		generarInforme(FORMATOS.PDF);
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton BtnExcel
	 * en la vista
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 */
	public void oprimirBtnExcel() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;  
		generarInforme(FORMATOS.EXCEL);
		//</CODIGO_DESARROLLADO>
	}
	//</METODOS_BOTONES>
	/**
     * Define las acciones necesarias para generar el informe realiza
     * el reemplazo de valores en la consulta del informe y envía los
     * parámetros definidos
     * 
     * @param formato
     * Formato seleccionado por el usuario para generar el informe
     */
    private void generarInforme(FORMATOS formato) {

        try {
        	String informe = "";
        	informe = "002904InformeConsumoControlado";
        	
        	//Se deja el formato de la fecha con la ultima hora del dia para que se tenga en cuenta todos los movimientos de la fecha de corte
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy 23:59:59");
            fechaAux = sdf.format(fecha);            
            String fechaCadena = SysmanFunciones.convertirAFechaCadena(fecha);

            // PARAMETROS DE REEMPLAZO EN LA CONSULTA
            Map<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("ultimoDiaFecha", fechaAux);
            reemplazar.put("compania", compania);
            reemplazar.put("elementoInicial", elementoDesde);
            reemplazar.put("elementoFinal", elementoHasta);
            reemplazar.put("fechaCorte", fechaCadena);
            // PARAMETROS PARA GENERACION DE INFORME
            Map<String, Object> parametros = new HashMap<>();

            parametros.put("PR_FECHAS", fechaCadena);
            
            
            if(ckExcelplano == true) 
            {
            	try {
            		
            		String datosExcel = Reporteador.resuelveConsulta(informe, 
                			Integer.parseInt(modulo),
                			reemplazar);
            	
	            	archivoDescarga = JsfUtil.exportarHojaDatosStreamed(datosExcel, 
	            				ConectorPool.ESQUEMA_SYSMAN,
	            				FORMATOS.EXCEL,informe);
	            	
	            	return;

            	} catch (SQLException | DRException e) {
            		((Throwable) e).printStackTrace();
            	} catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    JsfUtil.agregarMensajeError(e.getMessage());
                }

            }
            
            Reporteador.resuelveConsulta(informe,
                            Integer.parseInt(modulo), reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            informe,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
	//<METODOS_CAMBIAR>
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaElementoDesde
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaElementoDesde(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
        elementoDesde = retornarString(registroAux, cCodigoElemento);
        nombreElementoDesde = retornarString(registroAux, "NOMBRELARGO");
        elementoHasta = nombreElementoHasta = null;
        cargarListaElementoHasta();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaElementoHasta
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaElementoHasta(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
        elementoHasta = retornarString(registroAux, cCodigoElemento);
        nombreElementoHasta = retornarString(registroAux, "NOMBRELARGO");
	}
	
	/**
     * Evalua si el campo ingresado por parametro se encuentra nulo
     * dentro del registro que tambien ha sido ingresado por parametro
     * 
     * @param reg
     * Registro en el que se desea evaluar el campo
     * @param campo
     * Campo que se desea consultar
     * @return Cadena vacia o el valor del campo
     */
    private String retornarString(Registro reg, String campo) {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>
	//</METODOS_ARBOL>
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable ckExcelplano
	 * 
	 * @return  ckExcelplano
	 */
	public boolean getCkExcelplano() {
		return ckExcelplano;
	}
	/**
	 * Asigna la variable  ckExcelplano
	 * 
	 * @param  ckExcelplano
	 * Variable a asignar en  ckExcelplano
	 */
	public void setCkExcelplano(boolean ckExcelplano) {
		this.ckExcelplano = ckExcelplano;
	}
	/**
	 * Retorna la variable elementoDesde
	 * 
	 * @return  elementoDesde
	 */
	public String getElementoDesde() {
		return elementoDesde;
	}
	/**
	 * Asigna la variable  elementoDesde
	 * 
	 * @param  elementoDesde
	 * Variable a asignar en  elementoDesde
	 */
	public void setElementoDesde(String elementoDesde) {
		this.elementoDesde = elementoDesde;
	}
	/**
	 * Retorna la variable elementoHasta
	 * 
	 * @return  elementoHasta
	 */
	public String getElementoHasta() {
		return elementoHasta;
	}
	/**
	 * Asigna la variable  elementoHasta
	 * 
	 * @param  elementoHasta
	 * Variable a asignar en  elementoHasta
	 */
	public void setElementoHasta(String elementoHasta) {
		this.elementoHasta = elementoHasta;
	}
	/**
	 * Retorna la variable fecha
	 * 
	 * @return  fecha
	 */
	public Date getFecha() {
		return fecha;
	}
	/**
	 * Asigna la variable  fecha
	 * 
	 * @param  fecha
	 * Variable a asignar en  fecha
	 */
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	/**
	 * Retorna la variable nombreElementoDesde
	 * 
	 * @return  nombreElementoDesde
	 */
	public String getNombreElementoDesde() {
		return nombreElementoDesde;
	}
	/**
	 * Asigna la variable  nombreElementoDesde
	 * 
	 * @param  nombreElementoDesde
	 * Variable a asignar en  nombreElementoDesde
	 */
	public void setNombreElementoDesde(String nombreElementoDesde) {
		this.nombreElementoDesde = nombreElementoDesde;
	}
	/**
	 * Retorna la variable nombreElementoHasta
	 * 
	 * @return  nombreElementoHasta
	 */
	public String getNombreElementoHasta() {
		return nombreElementoHasta;
	}
	/**
	 * Asigna la variable  nombreElementoHasta
	 * 
	 * @param  nombreElementoHasta
	 * Variable a asignar en  nombreElementoHasta
	 */
	public void setNombreElementoHasta(String nombreElementoHasta) {
		this.nombreElementoHasta = nombreElementoHasta;
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
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaElementoDesde
	 * 
	 * @return listaElementoDesde
	 */
	public RegistroDataModelImpl getListaElementoDesde() {
		return listaElementoDesde;
	}
	/**
	 * Asigna la lista listaElementoDesde
	 * 
	 * @param listaElementoDesde
	 * Variable a asignar en  listaElementoDesde
	 */
	public void setListaElementoDesde(RegistroDataModelImpl listaElementoDesde) {
		this.listaElementoDesde = listaElementoDesde;
	}
	/**
	 * Retorna la lista listaElementoHasta
	 * 
	 * @return listaElementoHasta
	 */
	public RegistroDataModelImpl getListaElementoHasta() {
		return listaElementoHasta;
	}
	/**
	 * Asigna la lista listaElementoHasta
	 * 
	 * @param listaElementoHasta
	 * Variable a asignar en  listaElementoHasta
	 */
	public void setListaElementoHasta(RegistroDataModelImpl listaElementoHasta) {
		this.listaElementoHasta = listaElementoHasta;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
}

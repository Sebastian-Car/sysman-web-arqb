/*-
 * EjecuciongastosauxclaControlador.java
 *
 * 1.0
 * 
 * 20/06/2024
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 20/06/2024
 * @author grojas
 */
@ManagedBean
@ViewScoped
public class EjecuciongastosauxclaControlador extends BeanBaseDatosAcmeImpl{
	/**
	 * Constante a nivel de clase que almacena el codigo de la
	 * compania en la cual inicio sesion el usuario, el valor de esta
	 * constante es asignado en el constructor a la variable de sesion
	 * correspondiente
	 */
	private final String compania ; 
    /**
     * Atributo que almacena el codigo del modulo en la que ingreso en
     * la aplicacion
     */
    private final String modulo;
	//<DECLARAR_ATRIBUTOS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private boolean indCentroCosto;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private boolean indReferencia;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private boolean indFuente;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private boolean indAuxiliar;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private boolean porAuxiliares;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private boolean indSector;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private boolean indSubPrograma;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private boolean indProducto;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private boolean indPrograma;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private boolean indBpin;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private boolean indCpc;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private boolean indUnidad;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private boolean indCcpet;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private boolean indFuenteCuipo;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private boolean indPolitica;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private boolean indSectorial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private boolean indCcpetRega;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private boolean indSgr;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private boolean indClasificador;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String ano;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String mes;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String codigoInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String codigoFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String centroCostoInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String centroCostoFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String referenciaInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String referenciaFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String fuenteInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String fuenteFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String auxiliarInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String auxiliarFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String sectorInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String sectorFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String subProgramaInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String subProgramaFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String productoInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String productoFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String programaInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String programaFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String bpinInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String bpinFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String cpcInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String cpcFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String uniEjeInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String uniEjeFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String ccpetInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String ccpetFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String fuenteCuipoInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String fuenteCuipoFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String politicaInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String politicafinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String sectorialInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String sectorialFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String ccpetRegaInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String ccpetRegaFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String sgrInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String sgrFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String nombreCodigoInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String nombreCodigoFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String columnas;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private String reporte;
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	private StreamedContent archivoDescarga;
	//</DECLARAR_ATRIBUTOS>
	//<DECLARAR_LISTAS>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaAno;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private List<Registro> listaMes;
	//</DECLARAR_LISTAS>
	//<DECLARAR_LISTAS_COMBO_GRANDE>
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCodigoInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCodigoFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCentroCostoInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCentroCostoFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaReferenciaInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaReferenciaFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaFuenteInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaFuenteFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaAuxiliarInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaAuxiliarFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaSectorInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaSectorFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaSubProgramaInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaSubProgramaFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaProductoInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaProductoFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaProgramaInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaProgramaFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaBpinInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaBpinFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCpcInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCpcFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaUnidadInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaUnidadFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCcpetInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCcpetFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaFuenteCuipoInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaFuenteCuipoFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaPoliticaInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaPoliticaFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaSectorialInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaSectorialFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCcpetRegaInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaCcpetRegaFinal;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaSgrInicial;
	/**
	 * TODO DOCUMENTACION NECESARIA
	 */
	private RegistroDataModelImpl listaSgrFinal;
	//</DECLARAR_LISTAS_COMBO_GRANDE>
	//<DECLARAR_LISTAS_SUBFORM>
	//</DECLARAR_LISTAS_SUBFORM>
	//<DECLARAR_PARAMETROS>
	//</DECLARAR_PARAMETROS>
	//<DECLARAR_ADICIONALES>
	//</DECLARAR_ADICIONALES>
	/**
	 * Crea una nueva instancia de EjecuciongastosauxclaControlador
	 */
	public EjecuciongastosauxclaControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			// 2473
			numFormulario = GeneralCodigoFormaEnum.EJECUCION_GASTOS_AUX_CLA_CONTROLADOR
					.getCodigo();
			validarPermisos();
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		}
		catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}
	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de
	 * todas las listas, menos las que son de subformularios
	 */
	@Override
	public void iniciarListas(){
		//<CARGAR_LISTA>
		cargarListaAno();
		cargarListaMes();
		//</CARGAR_LISTA>
	}
	/**
	 * En este metodo se hace la invocacion de lo metodos de carga de
	 * todas las listas que son de subformularios
	 */
	@Override
	public void iniciarListasSub(){
		//<CARGAR_LISTA_COMBO_GRANDE>
				cargarListaCodigoInicial(); 
				cargarListaCentroCostoInicial(); 
				cargarListaCentroCostoFinal(); 
				cargarListaReferenciaInicial(); 
				cargarListaReferenciaFinal(); 
				cargarListaFuenteInicial(); 
				cargarListaFuenteFinal(); 
				cargarListaAuxiliarInicial(); 
				cargarListaAuxiliarFinal(); 
				cargarListaSectorInicial(); 
				cargarListaSectorFinal(); 
				cargarListaSubProgramaInicial(); 
				cargarListaSubProgramaFinal(); 
				cargarListaProductoInicial(); 
				cargarListaProductoFinal(); 
				cargarListaProgramaInicial(); 
				cargarListaProgramaFinal(); 
				cargarListaBpinInicial(); 
				cargarListaBpinFinal(); 
				cargarListaCpcInicial(); 
				cargarListaCpcFinal(); 
				cargarListaUnidadInicial(); 
				cargarListaUnidadFinal(); 
				cargarListaCcpetInicial(); 
				cargarListaCcpetFinal(); 
				cargarListaFuenteCuipoInicial(); 
				cargarListaFuenteCuipoFinal(); 
				cargarListaPoliticaInicial(); 
				cargarListaPoliticaFinal(); 
				cargarListaSectorialInicial(); 
				cargarListaSectorialFinal(); 
				cargarListaCcpetRegaInicial(); 
				cargarListaCcpetRegaFinal(); 
				cargarListaSgrInicial(); 
				cargarListaSgrFinal();
				//</CARGAR_LISTA_COMBO_GRANDE>
	}
	/**
	 * En este metodo se iguala a null todas las listas de los
	 * subformularios
	 */
	@Override
	public void iniciarListasSubNulo(){
		//<CARGAR_LISTAS_SUBFORM_NULL>
		//</CARGAR_LISTAS_SUBFORM_NULL>
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
        cargarListaAno();
        cargarListaMes();
        cargarListaCodigoInicial();
        cargarListaCentroCostoInicial();
        cargarListaAuxiliarInicial();
        cargarListaReferenciaInicial();
        cargarListaFuenteInicial();
	}
	/**
	 * Se realiza la asignacion de la variable origenDatos por la
	 * consulta correspondiente del formulario
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	@Override
	public void asignarOrigenDatos() {
		origenDatos="";	
	}
	
	//<METODOS_CARGAR_LISTA>	
	public void cargarListaAno(){
		
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                            		      EjecuciongastosauxclaControladorUrlEnum.URL4001
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
	}

	public void cargarListaMes(){

		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano(new Date()));

        try {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                            			EjecuciongastosauxclaControladorUrlEnum.URL7001
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
		
	}

	public void cargarListaCodigoInicial(){

		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
		
		UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                            		EjecuciongastosauxclaControladorUrlEnum.URL45018
                                                            .getValue());

            listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, GeneralParameterEnum.CODIGO.getName());
        		
	}

	public void cargarListaCodigoFinal(){

		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(EjecuciongastosauxclaControladorEnum.CUENTAINICIAL.getValue(),
                        codigoInicial);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                            		EjecuciongastosauxclaControladorUrlEnum.URL45020
                                                            .getValue());
            listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, GeneralParameterEnum.CODIGO.getName());
		
	}

	public void cargarListaCentroCostoInicial(){

		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        			EjecuciongastosauxclaControladorUrlEnum.URL20013
                                                        .getValue());
        
        listaCentroCostoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
		
	}

	public void cargarListaCentroCostoFinal(){

		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(EjecuciongastosauxclaControladorEnum.CENTRO_COSTO.getValue(),
                        centroCostoInicial);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		EjecuciongastosauxclaControladorUrlEnum.URL20015
                                                        .getValue());
        
        listaCentroCostoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
		
	}

	public void cargarListaReferenciaInicial(){

		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		EjecuciongastosauxclaControladorUrlEnum.URL13001
                                                        .getValue());
        
        listaReferenciaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
		
	}

	public void cargarListaReferenciaFinal(){

		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(EjecuciongastosauxclaControladorEnum.REFERENCIAINICIAL
                        .getValue(), referenciaInicial);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		EjecuciongastosauxclaControladorUrlEnum.URL13035
                                                        .getValue());
        
        listaReferenciaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

		
	}

	public void cargarListaFuenteInicial(){

		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		EjecuciongastosauxclaControladorUrlEnum.URL34001
                                                        .getValue());
        
        listaFuenteInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
		
	}

	public void cargarListaFuenteFinal(){

		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(EjecuciongastosauxclaControladorEnum.FUENTEINICIAL.getValue(),
                        fuenteInicial);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		EjecuciongastosauxclaControladorUrlEnum.URL34003
                                                        .getValue());
        listaFuenteFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
		
	}

	public void cargarListaAuxiliarInicial(){

		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EjecuciongastosauxclaControladorEnum.ANIO.getValue(), ano);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		EjecuciongastosauxclaControladorUrlEnum.URL23006
                                                        .getValue());
        listaAuxiliarInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
		
	}

	public void cargarListaAuxiliarFinal(){

		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EjecuciongastosauxclaControladorEnum.ANIO.getValue(), ano);
        param.put(EjecuciongastosauxclaControladorEnum.CODIGOFINAL.getValue(),
                        auxiliarInicial);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		EjecuciongastosauxclaControladorUrlEnum.URL23008
                                                        .getValue());
        listaAuxiliarFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
		
	}

	public void cargarListaSectorInicial(){
		
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EjecuciongastosauxclaControladorEnum.ANO.getValue(), ano);
        param.put(EjecuciongastosauxclaControladorEnum.CLASECLASIFICADOR.getValue(), "001");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		EjecuciongastosauxclaControladorUrlEnum.URL1884058
                                                        .getValue());
        listaSectorInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaSectorFinal(){
		
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EjecuciongastosauxclaControladorEnum.ANO.getValue(), ano);
        param.put(EjecuciongastosauxclaControladorEnum.CLASECLASIFICADOR.getValue(), "001");
        param.put(EjecuciongastosauxclaControladorEnum.CODIGOINICIAL.getValue(),
                        sectorInicial);
        
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		EjecuciongastosauxclaControladorUrlEnum.URL1884070
                                                        .getValue());
        listaSectorFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaSubProgramaInicial(){
		
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EjecuciongastosauxclaControladorEnum.ANO.getValue(), ano);
        param.put(EjecuciongastosauxclaControladorEnum.CLASECLASIFICADOR.getValue(), "003");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		EjecuciongastosauxclaControladorUrlEnum.URL1884058
                                                        .getValue());
        listaSubProgramaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaSubProgramaFinal(){
		
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EjecuciongastosauxclaControladorEnum.ANO.getValue(), ano);
        param.put(EjecuciongastosauxclaControladorEnum.CLASECLASIFICADOR.getValue(), "003");
        param.put(EjecuciongastosauxclaControladorEnum.CODIGOINICIAL.getValue(),
                        subProgramaInicial);
        
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		EjecuciongastosauxclaControladorUrlEnum.URL1884070
                                                        .getValue());
        listaSubProgramaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaProductoInicial(){
		
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EjecuciongastosauxclaControladorEnum.ANO.getValue(), ano);
        param.put(EjecuciongastosauxclaControladorEnum.CLASECLASIFICADOR.getValue(), "004");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		EjecuciongastosauxclaControladorUrlEnum.URL1884058
                                                        .getValue());
        listaProductoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaProductoFinal(){
		
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EjecuciongastosauxclaControladorEnum.ANO.getValue(), ano);
        param.put(EjecuciongastosauxclaControladorEnum.CLASECLASIFICADOR.getValue(), "004");
        param.put(EjecuciongastosauxclaControladorEnum.CODIGOINICIAL.getValue(),
                        productoInicial);
        
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		EjecuciongastosauxclaControladorUrlEnum.URL1884070
                                                        .getValue());
        listaProductoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaProgramaInicial(){
		
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EjecuciongastosauxclaControladorEnum.ANO.getValue(), ano);
        param.put(EjecuciongastosauxclaControladorEnum.CLASECLASIFICADOR.getValue(), "002");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		EjecuciongastosauxclaControladorUrlEnum.URL1884058
                                                        .getValue());
        listaProgramaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaProgramaFinal(){
		
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EjecuciongastosauxclaControladorEnum.ANO.getValue(), ano);
        param.put(EjecuciongastosauxclaControladorEnum.CLASECLASIFICADOR.getValue(), "002");
        param.put(EjecuciongastosauxclaControladorEnum.CODIGOINICIAL.getValue(),
                        programaInicial);
        
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		EjecuciongastosauxclaControladorUrlEnum.URL1884070
                                                        .getValue());
        listaProgramaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaBpinInicial(){
		
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EjecuciongastosauxclaControladorEnum.ANO.getValue(), ano);
        param.put(EjecuciongastosauxclaControladorEnum.CLASECLASIFICADOR.getValue(), "005");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		EjecuciongastosauxclaControladorUrlEnum.URL1884058
                                                        .getValue());
        listaBpinInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaBpinFinal(){
		
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EjecuciongastosauxclaControladorEnum.ANO.getValue(), ano);
        param.put(EjecuciongastosauxclaControladorEnum.CLASECLASIFICADOR.getValue(), "005");
        param.put(EjecuciongastosauxclaControladorEnum.CODIGOINICIAL.getValue(),
                        bpinInicial);
        
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		EjecuciongastosauxclaControladorUrlEnum.URL1884070
                                                        .getValue());
        listaBpinFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaCpcInicial(){
		
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EjecuciongastosauxclaControladorEnum.ANO.getValue(), ano);
        param.put(EjecuciongastosauxclaControladorEnum.CLASECLASIFICADOR.getValue(), "007");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		EjecuciongastosauxclaControladorUrlEnum.URL1884058
                                                        .getValue());
        listaCpcInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaCpcFinal(){
		
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EjecuciongastosauxclaControladorEnum.ANO.getValue(), ano);
        param.put(EjecuciongastosauxclaControladorEnum.CLASECLASIFICADOR.getValue(), "007");
        param.put(EjecuciongastosauxclaControladorEnum.CODIGOINICIAL.getValue(),
                        cpcInicial);
        
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		EjecuciongastosauxclaControladorUrlEnum.URL1884070
                                                        .getValue());
        listaCpcFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaUnidadInicial(){
		
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EjecuciongastosauxclaControladorEnum.ANO.getValue(), ano);
        param.put(EjecuciongastosauxclaControladorEnum.CLASECLASIFICADOR.getValue(), "008");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		EjecuciongastosauxclaControladorUrlEnum.URL1884058
                                                        .getValue());
        listaUnidadInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaUnidadFinal(){
		
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EjecuciongastosauxclaControladorEnum.ANO.getValue(), ano);
        param.put(EjecuciongastosauxclaControladorEnum.CLASECLASIFICADOR.getValue(), "008");
        param.put(EjecuciongastosauxclaControladorEnum.CODIGOINICIAL.getValue(),
                        uniEjeInicial);
        
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		EjecuciongastosauxclaControladorUrlEnum.URL1884070
                                                        .getValue());
        listaUnidadFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaCcpetInicial(){
		
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EjecuciongastosauxclaControladorEnum.ANO.getValue(), ano);
        param.put(EjecuciongastosauxclaControladorEnum.CLASECLASIFICADOR.getValue(), "006");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		EjecuciongastosauxclaControladorUrlEnum.URL1884058
                                                        .getValue());
        listaCcpetInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaCcpetFinal(){
		
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EjecuciongastosauxclaControladorEnum.ANO.getValue(), ano);
        param.put(EjecuciongastosauxclaControladorEnum.CLASECLASIFICADOR.getValue(), "006");
        param.put(EjecuciongastosauxclaControladorEnum.CODIGOINICIAL.getValue(),
                        ccpetInicial);
        
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		EjecuciongastosauxclaControladorUrlEnum.URL1884070
                                                        .getValue());
        listaCcpetFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaFuenteCuipoInicial(){
		
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EjecuciongastosauxclaControladorEnum.ANO.getValue(), ano);
        param.put(EjecuciongastosauxclaControladorEnum.CLASECLASIFICADOR.getValue(), "009");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		EjecuciongastosauxclaControladorUrlEnum.URL1884058
                                                        .getValue());
        listaFuenteCuipoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaFuenteCuipoFinal(){
		
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EjecuciongastosauxclaControladorEnum.ANO.getValue(), ano);
        param.put(EjecuciongastosauxclaControladorEnum.CLASECLASIFICADOR.getValue(), "009");
        param.put(EjecuciongastosauxclaControladorEnum.CODIGOINICIAL.getValue(),
                        fuenteCuipoInicial);
        
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		EjecuciongastosauxclaControladorUrlEnum.URL1884070
                                                        .getValue());
        listaFuenteCuipoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaPoliticaInicial(){
		
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EjecuciongastosauxclaControladorEnum.ANO.getValue(), ano);
        param.put(EjecuciongastosauxclaControladorEnum.CLASECLASIFICADOR.getValue(), "011");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		EjecuciongastosauxclaControladorUrlEnum.URL1884058
                                                        .getValue());
        listaPoliticaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaPoliticaFinal(){
		
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EjecuciongastosauxclaControladorEnum.ANO.getValue(), ano);
        param.put(EjecuciongastosauxclaControladorEnum.CLASECLASIFICADOR.getValue(), "011");
        param.put(EjecuciongastosauxclaControladorEnum.CODIGOINICIAL.getValue(),
                        politicaInicial);
        
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		EjecuciongastosauxclaControladorUrlEnum.URL1884070
                                                        .getValue());
        listaPoliticaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaSectorialInicial(){
		
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EjecuciongastosauxclaControladorEnum.ANO.getValue(), ano);
        param.put(EjecuciongastosauxclaControladorEnum.CLASECLASIFICADOR.getValue(), "012");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		EjecuciongastosauxclaControladorUrlEnum.URL1884058
                                                        .getValue());
        listaSectorialInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaSectorialFinal(){
		
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EjecuciongastosauxclaControladorEnum.ANO.getValue(), ano);
        param.put(EjecuciongastosauxclaControladorEnum.CLASECLASIFICADOR.getValue(), "012");
        param.put(EjecuciongastosauxclaControladorEnum.CODIGOINICIAL.getValue(),
                        sectorialInicial);
        
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		EjecuciongastosauxclaControladorUrlEnum.URL1884070
                                                        .getValue());
        listaSectorialFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaCcpetRegaInicial(){
		
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EjecuciongastosauxclaControladorEnum.ANO.getValue(), ano);
        param.put(EjecuciongastosauxclaControladorEnum.CLASECLASIFICADOR.getValue(), "010");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		EjecuciongastosauxclaControladorUrlEnum.URL1884058
                                                        .getValue());
        listaCcpetRegaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaCcpetRegaFinal(){
		
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EjecuciongastosauxclaControladorEnum.ANO.getValue(), ano);
        param.put(EjecuciongastosauxclaControladorEnum.CLASECLASIFICADOR.getValue(), "010");
        param.put(EjecuciongastosauxclaControladorEnum.CODIGOINICIAL.getValue(),
                        ccpetRegaInicial);
        
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		EjecuciongastosauxclaControladorUrlEnum.URL1884070
                                                        .getValue());
        listaCcpetRegaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaSgrInicial(){
		
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EjecuciongastosauxclaControladorEnum.ANO.getValue(), ano);
        param.put(EjecuciongastosauxclaControladorEnum.CLASECLASIFICADOR.getValue(), "013");

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		EjecuciongastosauxclaControladorUrlEnum.URL1884058
                                                        .getValue());
        listaSgrInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
	}

	public void cargarListaSgrFinal(){
		
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(EjecuciongastosauxclaControladorEnum.ANO.getValue(), ano);
        param.put(EjecuciongastosauxclaControladorEnum.CLASECLASIFICADOR.getValue(), "013");
        param.put(EjecuciongastosauxclaControladorEnum.CODIGOINICIAL.getValue(),
                        sgrInicial);
        
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                        		EjecuciongastosauxclaControladorUrlEnum.URL1884070
                                                        .getValue());
        listaSgrFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
	}
	//</METODOS_CARGAR_LISTA>
	//<METODOS_CAMBIAR>	

	public void cambiarAno() {
		//<CODIGO_DESARROLLADO>
		codigoInicial = null;
        nombreCodigoInicial = null;
        codigoFinal = null;
        nombreCodigoFinal = null;
        centroCostoInicial = null;
        centroCostoFinal = null;
        auxiliarInicial = null;
        auxiliarFinal = null;
        referenciaInicial = null;
        referenciaFinal = null;
        fuenteInicial = null;
        fuenteFinal = null;
        codigoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        codigoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        centroCostoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        centroCostoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        auxiliarInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        auxiliarFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        referenciaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        referenciaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        fuenteInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        fuenteFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        iniciarListasSub();
		//</CODIGO_DESARROLLADO>
	}

	public void cambiarindCentroCosto() {
		//<CODIGO_DESARROLLADO>
		centroCostoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        centroCostoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		//</CODIGO_DESARROLLADO>
	}

	public void cambiarIndReferencia() {
		//<CODIGO_DESARROLLADO>
		referenciaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        referenciaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		//</CODIGO_DESARROLLADO>
	}

	public void cambiarIndFuenteRecursos() {
		//<CODIGO_DESARROLLADO>
		fuenteInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        fuenteFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		//</CODIGO_DESARROLLADO>
	}

	public void cambiarIndAuxiliar() {
		//<CODIGO_DESARROLLADO>
		auxiliarInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        auxiliarFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		//</CODIGO_DESARROLLADO>
	}

	public void cambiarIndPorAuxiliares() {
		//<CODIGO_DESARROLLADO>
		if(!porAuxiliares) {
			indCentroCosto = false;
			indReferencia = false;
			indFuente = false;
			indAuxiliar = false;
		}
		//</CODIGO_DESARROLLADO>
	}

	public void cambiarIndSector() {
		//<CODIGO_DESARROLLADO>
		sectorInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		sectorFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		cargarListaSectorInicial();
		//</CODIGO_DESARROLLADO>
	}

	public void cambiarIndSubPrograma() {
		//<CODIGO_DESARROLLADO>
		subProgramaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		subProgramaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		cargarListaSubProgramaInicial();
		//</CODIGO_DESARROLLADO>
	}

	public void cambiarIndProducto() {
		//<CODIGO_DESARROLLADO>
		productoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		productoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		cargarListaProductoInicial();
		//</CODIGO_DESARROLLADO>
	}

	public void cambiarIndPrograma() {
		//<CODIGO_DESARROLLADO>
		programaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		programaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		cargarListaProgramaInicial();
		//</CODIGO_DESARROLLADO>
	}

	public void cambiarIndBpin() {
		//<CODIGO_DESARROLLADO>
		bpinInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		bpinFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		cargarListaBpinInicial();
		//</CODIGO_DESARROLLADO>
	}

	public void cambiarIndCpc() {
		//<CODIGO_DESARROLLADO>
		cpcInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		cpcFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		cargarListaCpcInicial();
		//</CODIGO_DESARROLLADO>
	}

	public void cambiarIndUnidadEje() {
		//<CODIGO_DESARROLLADO>
		uniEjeInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		uniEjeFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		cargarListaUnidadInicial();
		//</CODIGO_DESARROLLADO>
	}

	public void cambiarIndCcpet() {
		//<CODIGO_DESARROLLADO>
		ccpetInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		ccpetFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		cargarListaCcpetInicial();
		//</CODIGO_DESARROLLADO>
	}

	public void cambiarIndFuenteCuipo() {
		//<CODIGO_DESARROLLADO>
		fuenteCuipoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		fuenteCuipoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		cargarListaFuenteCuipoInicial();
		//</CODIGO_DESARROLLADO>
	}

	public void cambiarIndPolitica() {
		//<CODIGO_DESARROLLADO>
		politicaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		politicafinal = SysmanConstantes.DEFECTOFINAL_STRING;
		cargarListaPoliticaInicial();
		//</CODIGO_DESARROLLADO>
	}

	public void cambiarIndSectorial() {
		//<CODIGO_DESARROLLADO>
		sectorialInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		sectorialFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		cargarListaSectorialInicial();
		//</CODIGO_DESARROLLADO>
	}

	public void cambiarIndCcpetRegalias() {
		//<CODIGO_DESARROLLADO>
		ccpetRegaInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		ccpetRegaFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		cargarListaCcpetRegaInicial();
		//</CODIGO_DESARROLLADO>
	}

	public void cambiarIndSgr() {
		//<CODIGO_DESARROLLADO>
		sgrInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
		sgrFinal = SysmanConstantes.DEFECTOFINAL_STRING;
		cargarListaSgrInicial();
		//</CODIGO_DESARROLLADO>
	}

	public void cambiarIndPorClasificador() {
	         //<CODIGO_DESARROLLADO>
			
			if(!indClasificador) {
				indSector = false;
				indSubPrograma = false;
				indProducto = false;
				indPrograma = false;
				indBpin = false;
				indCpc = false;
				indUnidad = false;
				indCcpet = false;
				indFuenteCuipo = false;
				indPolitica = false;
				indSectorial = false;
				indCcpetRega = false;
				indSgr = false;
			}
			
	        //</CODIGO_DESARROLLADO>
	    }	
	//</METODOS_CAMBIAR>
	//<METODOS_COMBOS_GRANDES>	
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodigoInicial
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
        codigoInicial = SysmanFunciones.nvl(
                        registroAux.getCampos().get(
                                        GeneralParameterEnum.CODIGO.getName()),
                        "").toString();
        nombreCodigoInicial = SysmanFunciones.nvl(
					                registroAux.getCampos().get(
					                        GeneralParameterEnum.NOMBRE.getName()),
					        "").toString();
        codigoFinal = null;
        cargarListaCodigoFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCodigoFinal
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCodigoFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		codigoFinal= SysmanFunciones.nvl(
			                registroAux.getCampos().get(
			                        GeneralParameterEnum.CODIGO.getName()),
			        "").toString();
		nombreCodigoFinal = SysmanFunciones.nvl(
			                registroAux.getCampos().get(
			                        GeneralParameterEnum.NOMBRE.getName()),
			        "").toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCentroCostoInicial
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCentroCostoInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		centroCostoInicial= SysmanFunciones.nvl(
					                registroAux.getCampos().get(
					                        GeneralParameterEnum.CODIGO.getName()),
					        "").toString();
		cargarListaCentroCostoFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCentroCostoFinal
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCentroCostoFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		centroCostoFinal= SysmanFunciones.nvl(
					                registroAux.getCampos().get(
					                        GeneralParameterEnum.CODIGO.getName()),
					        "").toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaReferenciaInicial
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaReferenciaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		referenciaInicial= SysmanFunciones.nvl(
					                registroAux.getCampos().get(
					                        GeneralParameterEnum.CODIGO.getName()),
					        "").toString();
		cargarListaReferenciaFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaReferenciaFinal
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaReferenciaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		referenciaFinal= SysmanFunciones.nvl(
					                registroAux.getCampos().get(
					                        GeneralParameterEnum.CODIGO.getName()),
					        "").toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaFuenteInicial
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFuenteInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		fuenteInicial= SysmanFunciones.nvl(
				                registroAux.getCampos().get(
				                        GeneralParameterEnum.CODIGO.getName()),
				        "").toString();
		cargarListaFuenteFinal(); 
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaFuenteFinal
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFuenteFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		fuenteFinal= SysmanFunciones.nvl(
			                registroAux.getCampos().get(
			                        GeneralParameterEnum.CODIGO.getName()),
			        "").toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaAuxiliarInicial
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaAuxiliarInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliarInicial= SysmanFunciones.nvl(
				                registroAux.getCampos().get(
				                        GeneralParameterEnum.CODIGO.getName()),
				        "").toString();
		cargarListaAuxiliarFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaAuxiliarFinal
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaAuxiliarFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		auxiliarFinal= SysmanFunciones.nvl(
				                registroAux.getCampos().get(
				                        GeneralParameterEnum.CODIGO.getName()),
				        "").toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaSectorInicial
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaSectorInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		sectorInicial= SysmanFunciones.nvl(
				                registroAux.getCampos().get(
				                        GeneralParameterEnum.CODIGO.getName()),
				        "").toString();
		cargarListaSectorFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaSectorFinal
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaSectorFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		sectorFinal= SysmanFunciones.nvl(
			                registroAux.getCampos().get(
			                        GeneralParameterEnum.CODIGO.getName()),
			        "").toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaSubProgramaInicial
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaSubProgramaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		subProgramaInicial= SysmanFunciones.nvl(
					                registroAux.getCampos().get(
					                        GeneralParameterEnum.CODIGO.getName()),
					        "").toString();
		cargarListaSubProgramaFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaSubProgramaFinal
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaSubProgramaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		subProgramaFinal= SysmanFunciones.nvl(
				                registroAux.getCampos().get(
				                        GeneralParameterEnum.CODIGO.getName()),
				        "").toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaProductoInicial
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaProductoInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		productoInicial= SysmanFunciones.nvl(
				                registroAux.getCampos().get(
				                        GeneralParameterEnum.CODIGO.getName()),
				        "").toString();
		cargarListaProductoFinal(); 
		}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaProductoFinal
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaProductoFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		productoFinal= SysmanFunciones.nvl(
				                registroAux.getCampos().get(
				                        GeneralParameterEnum.CODIGO.getName()),
				        "").toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaProgramaInicial
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaProgramaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		programaInicial= SysmanFunciones.nvl(
				                registroAux.getCampos().get(
				                        GeneralParameterEnum.CODIGO.getName()),
				        "").toString();
		cargarListaProgramaFinal(); 
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaProgramaFinal
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaProgramaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		programaFinal= SysmanFunciones.nvl(
				                registroAux.getCampos().get(
				                        GeneralParameterEnum.CODIGO.getName()),
				        "").toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaBpinInicial
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaBpinInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		bpinInicial= SysmanFunciones.nvl(
			                registroAux.getCampos().get(
			                        GeneralParameterEnum.CODIGO.getName()),
			        "").toString();
		cargarListaBpinFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaBpinFinal
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaBpinFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		bpinFinal= SysmanFunciones.nvl(
			                registroAux.getCampos().get(
			                        GeneralParameterEnum.CODIGO.getName()),
			        "").toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCpcInicial
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCpcInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cpcInicial= SysmanFunciones.nvl(
			                registroAux.getCampos().get(
			                        GeneralParameterEnum.CODIGO.getName()),
			        "").toString();
		cargarListaCpcFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCpcFinal
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCpcFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		cpcFinal= SysmanFunciones.nvl(
			                registroAux.getCampos().get(
			                        GeneralParameterEnum.CODIGO.getName()),
			        "").toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaUnidadInicial
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaUnidadInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		uniEjeInicial= SysmanFunciones.nvl(
				                registroAux.getCampos().get(
				                        GeneralParameterEnum.CODIGO.getName()),
				        "").toString();
		cargarListaUnidadFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaUnidadFinal
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaUnidadFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		uniEjeFinal= SysmanFunciones.nvl(
			                registroAux.getCampos().get(
			                        GeneralParameterEnum.CODIGO.getName()),
			        "").toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCcpetInicial
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCcpetInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		ccpetInicial= SysmanFunciones.nvl(
				                registroAux.getCampos().get(
				                        GeneralParameterEnum.CODIGO.getName()),
				        "").toString();
		cargarListaCcpetFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCcpetFinal
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCcpetFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		ccpetFinal= SysmanFunciones.nvl(
			                registroAux.getCampos().get(
			                        GeneralParameterEnum.CODIGO.getName()),
			        "").toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaFuenteCuipoInicial
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFuenteCuipoInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		fuenteCuipoInicial= SysmanFunciones.nvl(
					                registroAux.getCampos().get(
					                        GeneralParameterEnum.CODIGO.getName()),
					        "").toString();
		cargarListaFuenteCuipoFinal(); 
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaFuenteCuipoFinal
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaFuenteCuipoFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		fuenteCuipoFinal= SysmanFunciones.nvl(
					                registroAux.getCampos().get(
					                        GeneralParameterEnum.CODIGO.getName()),
					        "").toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaPoliticaInicial
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaPoliticaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		politicaInicial= SysmanFunciones.nvl(
				                registroAux.getCampos().get(
				                        GeneralParameterEnum.CODIGO.getName()),
				        "").toString();
		cargarListaPoliticaFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaPoliticaFinal
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaPoliticaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		politicafinal= SysmanFunciones.nvl(
				                registroAux.getCampos().get(
				                        GeneralParameterEnum.CODIGO.getName()),
				        "").toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaSectorialInicial
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaSectorialInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		sectorialInicial= SysmanFunciones.nvl(
					                registroAux.getCampos().get(
					                        GeneralParameterEnum.CODIGO.getName()),
					        "").toString();
		cargarListaSectorialFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaSectorialFinal
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaSectorialFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		sectorialFinal= SysmanFunciones.nvl(
				                registroAux.getCampos().get(
				                        GeneralParameterEnum.CODIGO.getName()),
				        "").toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCcpetRegaInicial
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCcpetRegaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		ccpetRegaInicial= SysmanFunciones.nvl(
					                registroAux.getCampos().get(
					                        GeneralParameterEnum.CODIGO.getName()),
					        "").toString();
		cargarListaCcpetRegaFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaCcpetRegaFinal
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaCcpetRegaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		ccpetRegaFinal= SysmanFunciones.nvl(
				                registroAux.getCampos().get(
				                        GeneralParameterEnum.CODIGO.getName()),
				        "").toString();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaSgrInicial
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaSgrInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		sgrInicial= SysmanFunciones.nvl(
			                registroAux.getCampos().get(
			                        GeneralParameterEnum.CODIGO.getName()),
			        "").toString();
		cargarListaSgrFinal();
	}
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista
	 * listaSgrFinal
	 * @param event
	 * objeto que encapsula la accion proveniente de la vista
	 */
	public void seleccionarFilaSgrFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		sgrFinal= SysmanFunciones.nvl(
			                registroAux.getCampos().get(
			                        GeneralParameterEnum.CODIGO.getName()),
			        "").toString();
	}
	//</METODOS_COMBOS_GRANDES>
	//<METODOS_ARBOL>	
	//</METODOS_ARBOL>
	//<METODOS_BOTONES>	
	/**
	 * 
	 * Metodo ejecutado al oprimir el boton EnviarExcel
	 * en la vista
	 */
	public void oprimirEnviarExcel() {
		//<CODIGO_DESARROLLADO>
		archivoDescarga=null;
		columnas = "";
		generarExcel();
		//</CODIGO_DESARROLLADO>
	}
		
	//<METODOS_ADICIONALES>	
	
	/**
     * Metodo que realiza el proceso de resolver la consulta y
     * exportarla en un archivo excel
     */
    private void generarExcel() {
        try {
        	reporte = "800632EjecucionAuxiliaresClasificadores";
            HashMap<String, Object> reemplazos = new HashMap<>();
            reemplazos.put("ano", ano);
            reemplazos.put("codigoInicial", codigoInicial);
            reemplazos.put("codigoFinal", codigoFinal);
            reemplazos.put("mesFinal", mes);
            reemplazos.put("naturaleza", "D");
            
            validarAuxiliares(reemplazos);
            validarClasificador(reemplazos);
            validarEjecucionCompleta();
            
            reemplazos.put("columnas", columnas);
            
            String strSql = Reporteador.resuelveConsulta(reporte,
                                Integer.parseInt(modulo), reemplazos);
            
            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                                ConectorPool.ESQUEMA_SYSMAN,
                                ReportesBean.FORMATOS.EXCEL);
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }
    
    public void validarAuxiliares(Map<String, Object> reemplazos) {
        reemplazos.put("centroCosto", "");
        reemplazos.put("auxiliar", "");
        reemplazos.put("referencia", "");
        reemplazos.put("fuenteRecurso", "");
        
        if(porAuxiliares) {
        
        	StringBuilder columnasBuilder = new StringBuilder();

            Object[][] condiciones = {
                    { "centroCosto", indCentroCosto, centroCostoInicial, centroCostoFinal, "CENTRO_COSTO" },
                    { "auxiliar", indAuxiliar, auxiliarInicial, auxiliarFinal, "AUXILIAR" },
                    { "referencia", indReferencia, referenciaInicial, referenciaFinal, "REFERENCIA" },
                    { "fuenteRecurso", indFuente, fuenteInicial, fuenteFinal, "FUENTE_RECURSO" }
                };

            for (Object[] condicion : condiciones) {
            	
                String llave = (String) condicion[0];
                boolean indicador = (Boolean) condicion[1];
                String valorInicial = (String) condicion[2];
                String valorFinal = (String) condicion[3];
                String nombreColumna = (String) condicion[4];

                if (indicador) {
                    reemplazos.put(llave, " AND " + nombreColumna + " BETWEEN '" + valorInicial + "' AND '" + valorFinal + "' ");
                    columnasBuilder.append(nombreColumna).append(", ");
                }
            }

            columnas = columnasBuilder.toString();
        
        }
    }
    
    public void validarClasificador(Map<String, Object> reemplazos) {
        reemplazos.put("sector", "");
        reemplazos.put("programa", "");
        reemplazos.put("subprograma", "");
        reemplazos.put("producto", "");
        reemplazos.put("bpin", "");
        reemplazos.put("ccpet", "");
        reemplazos.put("cpc", "");
        reemplazos.put("uniejecutora", "");
        reemplazos.put("fuentecuipo", "");
        reemplazos.put("ccpetrega", "");
        reemplazos.put("politica", "");
        reemplazos.put("sectorial", "");
        reemplazos.put("tiposgr", "");
        
        if (indClasificador) {
            StringBuilder columnasBuilder = new StringBuilder();

            Object[][] condiciones = {
                { "sector", indSector, sectorInicial, sectorFinal, "SECTOR" },
                { "programa", indPrograma, programaInicial, programaFinal, "PROGRAMA" },
                { "subprograma", indSubPrograma, subProgramaInicial, subProgramaFinal, "SUBPROGRAMA" },
                { "producto", indProducto, productoInicial, productoFinal, "CODIGOPRODUCTO" },
                { "bpin", indBpin, bpinInicial, bpinFinal, "CODIGOBPIN" },
                { "ccpet", indCcpet, ccpetInicial, ccpetFinal, "CODIGOCCPET" },
                { "cpc", indCpc, cpcInicial, cpcFinal, "CODIGOCPCDANE" },
                { "uniejecutora", indUnidad, uniEjeInicial, uniEjeFinal, "CODIGOUNIDADEJE" },
                { "fuentecuipo", indFuenteCuipo, fuenteCuipoInicial, fuenteCuipoFinal, "CODIGOFUENTE" },
                { "ccpetrega", indCcpetRega, ccpetRegaInicial, ccpetRegaFinal, "CODIGOCCPETREGA" },
                { "politica", indPolitica, politicaInicial, politicafinal, "POLITCA_PUBLICA_CUIPO" },
                { "sectorial", indSectorial, sectorialInicial, sectorialFinal, "DETALLE_SECTORIAL" },
                { "tiposgr", indSgr, sgrInicial, sgrFinal, "TIPO_RECURSO" }
            };

            for (Object[] condicion : condiciones) {
            	
                String llave = (String) condicion[0];
                boolean indicador = (Boolean) condicion[1];
                String valorInicial = (String) condicion[2];
                String valorFinal = (String) condicion[3];
                String nombreColumna = (String) condicion[4];

                if (indicador) {
                    reemplazos.put(llave, " AND " + nombreColumna + " BETWEEN '" + valorInicial + "' AND '" + valorFinal + "' ");
                    columnasBuilder.append(nombreColumna).append(", ");
                }
            }

            columnas += columnasBuilder.toString();
        }
    }
    
    public void validarEjecucionCompleta() {
    	
    	if(!porAuxiliares && !indClasificador) {
    		
    		try {
	    		StringBuilder columnasBuilder = new StringBuilder();
	    		
	    		columnasBuilder.append("CENTRO_COSTO, AUXILIAR, REFERENCIA, FUENTE_RECURSO,");
	    		Map<String, Object> param = new HashMap<>();
	    		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	            param.put(EjecuciongastosauxclaControladorEnum.ANO.getValue(), ano);
	    		
				List<Registro> listaClasificador = RegistroConverter.toListRegistro(
								requestManager.getList(
										UrlServiceUtil.getInstance()
										.getUrlServiceByUrlByEnumID(
												EjecuciongastosauxclaControladorUrlEnum.URL1884072.getValue())
										.getUrl(),
										param));
				
				for (Registro lista : listaClasificador) {
					
					columnasBuilder.append(lista.getCampos().get("NOMBRE")).append(", ");
					
				}
				
				columnas = columnasBuilder.toString();
				
			} catch (SystemException e) {
					JsfUtil.agregarMensajeError(e.getMessage());
		            logger.error(e.getMessage(), e);
			}
        
    	}
    	
    }
	
	
	
	
	//</METODOS_ADICIONALES>
	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las
	 * acciones a tener en cuenta en el momento de apertura del
	 * formulario
	 */
	@Override
	public void abrirFormulario(){
		//<CODIGO_DESARROLLADO>
		
		ano = String.valueOf(SysmanFunciones.ano(new Date()));
        mes = String.valueOf(SysmanFunciones.mes(new Date()));
        codigoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        codigoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
        		
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado en el momento despues de cargar el registro
	 */
	@Override
	public void cargarRegistro() {
		//<CODIGO_DESARROLLADO>
		precargarRegistro();
		//</CODIGO_DESARROLLADO>
	}
	/**
	 * Metodo ejecutado antes de realizar la insercion del registro
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean insertarAntes(){
		//<CODIGO_DESARROLLADO>
		registro.getCampos().put("COMPANIA", compania);
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion del registro
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
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
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean actualizarAntes(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la insercion y actualizacion
	 * del registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean actualizarDespues(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado antes de realizar la eliminacion del
	 * registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean eliminarAntes(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	/**
	 * Metodo ejecutado despues de realizar la eliminacion del
	 * registro
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 * @return TODO VARIABLE
	 */
	@Override
	public boolean eliminarDespues(){
		//<CODIGO_DESARROLLADO>
		//</CODIGO_DESARROLLADO>
		return true;
	}
	//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable indCentroCosto
	 * 
	 * @return  indCentroCosto
	 */
	public boolean getIndCentroCosto() {
		return indCentroCosto;
	}
	/**
	 * Asigna la variable  indCentroCosto
	 * 
	 * @param  indCentroCosto
	 * Variable a asignar en  indCentroCosto
	 */
	public void setIndCentroCosto(boolean indCentroCosto) {
		this.indCentroCosto = indCentroCosto;
	}
	/**
	 * Retorna la variable indReferencia
	 * 
	 * @return  indReferencia
	 */
	public boolean getIndReferencia() {
		return indReferencia;
	}
	/**
	 * Asigna la variable  indReferencia
	 * 
	 * @param  indReferencia
	 * Variable a asignar en  indReferencia
	 */
	public void setIndReferencia(boolean indReferencia) {
		this.indReferencia = indReferencia;
	}
	/**
	 * Retorna la variable indFuente
	 * 
	 * @return  indFuente
	 */
	public boolean getIndFuente() {
		return indFuente;
	}
	/**
	 * Asigna la variable  indFuente
	 * 
	 * @param  indFuente
	 * Variable a asignar en  indFuente
	 */
	public void setIndFuente(boolean indFuente) {
		this.indFuente = indFuente;
	}
	/**
	 * Retorna la variable indAuxiliar
	 * 
	 * @return  indAuxiliar
	 */
	public boolean getIndAuxiliar() {
		return indAuxiliar;
	}
	/**
	 * Asigna la variable  indAuxiliar
	 * 
	 * @param  indAuxiliar
	 * Variable a asignar en  indAuxiliar
	 */
	public void setIndAuxiliar(boolean indAuxiliar) {
		this.indAuxiliar = indAuxiliar;
	}
	/**
	 * Retorna la variable porAuxiliares
	 * 
	 * @return  porAuxiliares
	 */
	public boolean getPorAuxiliares() {
		return porAuxiliares;
	}
	/**
	 * Asigna la variable  porAuxiliares
	 * 
	 * @param  porAuxiliares
	 * Variable a asignar en  porAuxiliares
	 */
	public void setPorAuxiliares(boolean porAuxiliares) {
		this.porAuxiliares = porAuxiliares;
	}
	/**
	 * Retorna la variable indSector
	 * 
	 * @return  indSector
	 */
	public boolean getIndSector() {
		return indSector;
	}
	/**
	 * Asigna la variable  indSector
	 * 
	 * @param  indSector
	 * Variable a asignar en  indSector
	 */
	public void setIndSector(boolean indSector) {
		this.indSector = indSector;
	}
	/**
	 * Retorna la variable indSubPrograma
	 * 
	 * @return  indSubPrograma
	 */
	public boolean getIndSubPrograma() {
		return indSubPrograma;
	}
	/**
	 * Asigna la variable  indSubPrograma
	 * 
	 * @param  indSubPrograma
	 * Variable a asignar en  indSubPrograma
	 */
	public void setIndSubPrograma(boolean indSubPrograma) {
		this.indSubPrograma = indSubPrograma;
	}
	/**
	 * Retorna la variable indProducto
	 * 
	 * @return  indProducto
	 */
	public boolean getIndProducto() {
		return indProducto;
	}
	/**
	 * Asigna la variable  indProducto
	 * 
	 * @param  indProducto
	 * Variable a asignar en  indProducto
	 */
	public void setIndProducto(boolean indProducto) {
		this.indProducto = indProducto;
	}
	/**
	 * Retorna la variable indPrograma
	 * 
	 * @return  indPrograma
	 */
	public boolean getIndPrograma() {
		return indPrograma;
	}
	/**
	 * Asigna la variable  indPrograma
	 * 
	 * @param  indPrograma
	 * Variable a asignar en  indPrograma
	 */
	public void setIndPrograma(boolean indPrograma) {
		this.indPrograma = indPrograma;
	}
	/**
	 * Retorna la variable indBpin
	 * 
	 * @return  indBpin
	 */
	public boolean getIndBpin() {
		return indBpin;
	}
	/**
	 * Asigna la variable  indBpin
	 * 
	 * @param  indBpin
	 * Variable a asignar en  indBpin
	 */
	public void setIndBpin(boolean indBpin) {
		this.indBpin = indBpin;
	}
	/**
	 * Retorna la variable indCpc
	 * 
	 * @return  indCpc
	 */
	public boolean getIndCpc() {
		return indCpc;
	}
	/**
	 * Asigna la variable  indCpc
	 * 
	 * @param  indCpc
	 * Variable a asignar en  indCpc
	 */
	public void setIndCpc(boolean indCpc) {
		this.indCpc = indCpc;
	}
	/**
	 * Retorna la variable indUnidad
	 * 
	 * @return  indUnidad
	 */
	public boolean getIndUnidad() {
		return indUnidad;
	}
	/**
	 * Asigna la variable  indUnidad
	 * 
	 * @param  indUnidad
	 * Variable a asignar en  indUnidad
	 */
	public void setIndUnidad(boolean indUnidad) {
		this.indUnidad = indUnidad;
	}
	/**
	 * Retorna la variable indCcpet
	 * 
	 * @return  indCcpet
	 */
	public boolean getIndCcpet() {
		return indCcpet;
	}
	/**
	 * Asigna la variable  indCcpet
	 * 
	 * @param  indCcpet
	 * Variable a asignar en  indCcpet
	 */
	public void setIndCcpet(boolean indCcpet) {
		this.indCcpet = indCcpet;
	}
	/**
	 * Retorna la variable indFuenteCuipo
	 * 
	 * @return  indFuenteCuipo
	 */
	public boolean getIndFuenteCuipo() {
		return indFuenteCuipo;
	}
	/**
	 * Asigna la variable  indFuenteCuipo
	 * 
	 * @param  indFuenteCuipo
	 * Variable a asignar en  indFuenteCuipo
	 */
	public void setIndFuenteCuipo(boolean indFuenteCuipo) {
		this.indFuenteCuipo = indFuenteCuipo;
	}
	/**
	 * Retorna la variable indPolitica
	 * 
	 * @return  indPolitica
	 */
	public boolean getIndPolitica() {
		return indPolitica;
	}
	/**
	 * Asigna la variable  indPolitica
	 * 
	 * @param  indPolitica
	 * Variable a asignar en  indPolitica
	 */
	public void setIndPolitica(boolean indPolitica) {
		this.indPolitica = indPolitica;
	}
	/**
	 * Retorna la variable indSectorial
	 * 
	 * @return  indSectorial
	 */
	public boolean getIndSectorial() {
		return indSectorial;
	}
	/**
	 * Asigna la variable  indSectorial
	 * 
	 * @param  indSectorial
	 * Variable a asignar en  indSectorial
	 */
	public void setIndSectorial(boolean indSectorial) {
		this.indSectorial = indSectorial;
	}
	/**
	 * Retorna la variable indCcpetRega
	 * 
	 * @return  indCcpetRega
	 */
	public boolean getIndCcpetRega() {
		return indCcpetRega;
	}
	/**
	 * Asigna la variable  indCcpetRega
	 * 
	 * @param  indCcpetRega
	 * Variable a asignar en  indCcpetRega
	 */
	public void setIndCcpetRega(boolean indCcpetRega) {
		this.indCcpetRega = indCcpetRega;
	}
	/**
	 * Retorna la variable indSgr
	 * 
	 * @return  indSgr
	 */
	public boolean getIndSgr() {
		return indSgr;
	}
	/**
	 * Asigna la variable  indSgr
	 * 
	 * @param  indSgr
	 * Variable a asignar en  indSgr
	 */
	public void setIndSgr(boolean indSgr) {
		this.indSgr = indSgr;
	}
	/**
	 * Retorna la variable indClasificador
	 * 
	 * @return  indClasificador
	 */
	public boolean getIndClasificador() {
		return indClasificador;
	}
	/**
	 * Asigna la variable  indClasificador
	 * 
	 * @param  indClasificador
	 * Variable a asignar en  indClasificador
	 */
	public void setIndClasificador(boolean indClasificador) {
		this.indClasificador = indClasificador;
	}
	/**
	 * Retorna la variable ano
	 * 
	 * @return  ano
	 */
	public String getAno() {
		return ano;
	}
	/**
	 * Asigna la variable  ano
	 * 
	 * @param  ano
	 * Variable a asignar en  ano
	 */
	public void setAno(String ano) {
		this.ano = ano;
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
	 * Retorna la variable codigoInicial
	 * 
	 * @return  codigoInicial
	 */
	public String getCodigoInicial() {
		return codigoInicial;
	}
	/**
	 * Asigna la variable  codigoInicial
	 * 
	 * @param  codigoInicial
	 * Variable a asignar en  codigoInicial
	 */
	public void setCodigoInicial(String codigoInicial) {
		this.codigoInicial = codigoInicial;
	}
	/**
	 * Retorna la variable codigoFinal
	 * 
	 * @return  codigoFinal
	 */
	public String getCodigoFinal() {
		return codigoFinal;
	}
	/**
	 * Asigna la variable  codigoFinal
	 * 
	 * @param  codigoFinal
	 * Variable a asignar en  codigoFinal
	 */
	public void setCodigoFinal(String codigoFinal) {
		this.codigoFinal = codigoFinal;
	}
	/**
	 * Retorna la variable centroCostoInicial
	 * 
	 * @return  centroCostoInicial
	 */
	public String getCentroCostoInicial() {
		return centroCostoInicial;
	}
	/**
	 * Asigna la variable  centroCostoInicial
	 * 
	 * @param  centroCostoInicial
	 * Variable a asignar en  centroCostoInicial
	 */
	public void setCentroCostoInicial(String centroCostoInicial) {
		this.centroCostoInicial = centroCostoInicial;
	}
	/**
	 * Retorna la variable centroCostoFinal
	 * 
	 * @return  centroCostoFinal
	 */
	public String getCentroCostoFinal() {
		return centroCostoFinal;
	}
	/**
	 * Asigna la variable  centroCostoFinal
	 * 
	 * @param  centroCostoFinal
	 * Variable a asignar en  centroCostoFinal
	 */
	public void setCentroCostoFinal(String centroCostoFinal) {
		this.centroCostoFinal = centroCostoFinal;
	}
	/**
	 * Retorna la variable referenciaInicial
	 * 
	 * @return  referenciaInicial
	 */
	public String getReferenciaInicial() {
		return referenciaInicial;
	}
	/**
	 * Asigna la variable  referenciaInicial
	 * 
	 * @param  referenciaInicial
	 * Variable a asignar en  referenciaInicial
	 */
	public void setReferenciaInicial(String referenciaInicial) {
		this.referenciaInicial = referenciaInicial;
	}
	/**
	 * Retorna la variable referenciaFinal
	 * 
	 * @return  referenciaFinal
	 */
	public String getReferenciaFinal() {
		return referenciaFinal;
	}
	/**
	 * Asigna la variable  referenciaFinal
	 * 
	 * @param  referenciaFinal
	 * Variable a asignar en  referenciaFinal
	 */
	public void setReferenciaFinal(String referenciaFinal) {
		this.referenciaFinal = referenciaFinal;
	}
	/**
	 * Retorna la variable fuenteInicial
	 * 
	 * @return  fuenteInicial
	 */
	public String getFuenteInicial() {
		return fuenteInicial;
	}
	/**
	 * Asigna la variable  fuenteInicial
	 * 
	 * @param  fuenteInicial
	 * Variable a asignar en  fuenteInicial
	 */
	public void setFuenteInicial(String fuenteInicial) {
		this.fuenteInicial = fuenteInicial;
	}
	/**
	 * Retorna la variable fuenteFinal
	 * 
	 * @return  fuenteFinal
	 */
	public String getFuenteFinal() {
		return fuenteFinal;
	}
	/**
	 * Asigna la variable  fuenteFinal
	 * 
	 * @param  fuenteFinal
	 * Variable a asignar en  fuenteFinal
	 */
	public void setFuenteFinal(String fuenteFinal) {
		this.fuenteFinal = fuenteFinal;
	}
	/**
	 * Retorna la variable auxiliarInicial
	 * 
	 * @return  auxiliarInicial
	 */
	public String getAuxiliarInicial() {
		return auxiliarInicial;
	}
	/**
	 * Asigna la variable  auxiliarInicial
	 * 
	 * @param  auxiliarInicial
	 * Variable a asignar en  auxiliarInicial
	 */
	public void setAuxiliarInicial(String auxiliarInicial) {
		this.auxiliarInicial = auxiliarInicial;
	}
	/**
	 * Retorna la variable auxiliarFinal
	 * 
	 * @return  auxiliarFinal
	 */
	public String getAuxiliarFinal() {
		return auxiliarFinal;
	}
	/**
	 * Asigna la variable  auxiliarFinal
	 * 
	 * @param  auxiliarFinal
	 * Variable a asignar en  auxiliarFinal
	 */
	public void setAuxiliarFinal(String auxiliarFinal) {
		this.auxiliarFinal = auxiliarFinal;
	}
	/**
	 * Retorna la variable sectorInicial
	 * 
	 * @return  sectorInicial
	 */
	public String getSectorInicial() {
		return sectorInicial;
	}
	/**
	 * Asigna la variable  sectorInicial
	 * 
	 * @param  sectorInicial
	 * Variable a asignar en  sectorInicial
	 */
	public void setSectorInicial(String sectorInicial) {
		this.sectorInicial = sectorInicial;
	}
	/**
	 * Retorna la variable sectorFinal
	 * 
	 * @return  sectorFinal
	 */
	public String getSectorFinal() {
		return sectorFinal;
	}
	/**
	 * Asigna la variable  sectorFinal
	 * 
	 * @param  sectorFinal
	 * Variable a asignar en  sectorFinal
	 */
	public void setSectorFinal(String sectorFinal) {
		this.sectorFinal = sectorFinal;
	}
	/**
	 * Retorna la variable subProgramaInicial
	 * 
	 * @return  subProgramaInicial
	 */
	public String getSubProgramaInicial() {
		return subProgramaInicial;
	}
	/**
	 * Asigna la variable  subProgramaInicial
	 * 
	 * @param  subProgramaInicial
	 * Variable a asignar en  subProgramaInicial
	 */
	public void setSubProgramaInicial(String subProgramaInicial) {
		this.subProgramaInicial = subProgramaInicial;
	}
	/**
	 * Retorna la variable subProgramaFinal
	 * 
	 * @return  subProgramaFinal
	 */
	public String getSubProgramaFinal() {
		return subProgramaFinal;
	}
	/**
	 * Asigna la variable  subProgramaFinal
	 * 
	 * @param  subProgramaFinal
	 * Variable a asignar en  subProgramaFinal
	 */
	public void setSubProgramaFinal(String subProgramaFinal) {
		this.subProgramaFinal = subProgramaFinal;
	}
	/**
	 * Retorna la variable productoInicial
	 * 
	 * @return  productoInicial
	 */
	public String getProductoInicial() {
		return productoInicial;
	}
	/**
	 * Asigna la variable  productoInicial
	 * 
	 * @param  productoInicial
	 * Variable a asignar en  productoInicial
	 */
	public void setProductoInicial(String productoInicial) {
		this.productoInicial = productoInicial;
	}
	/**
	 * Retorna la variable productoFinal
	 * 
	 * @return  productoFinal
	 */
	public String getProductoFinal() {
		return productoFinal;
	}
	/**
	 * Asigna la variable  productoFinal
	 * 
	 * @param  productoFinal
	 * Variable a asignar en  productoFinal
	 */
	public void setProductoFinal(String productoFinal) {
		this.productoFinal = productoFinal;
	}
	/**
	 * Retorna la variable programaInicial
	 * 
	 * @return  programaInicial
	 */
	public String getProgramaInicial() {
		return programaInicial;
	}
	/**
	 * Asigna la variable  programaInicial
	 * 
	 * @param  programaInicial
	 * Variable a asignar en  programaInicial
	 */
	public void setProgramaInicial(String programaInicial) {
		this.programaInicial = programaInicial;
	}
	/**
	 * Retorna la variable programaFinal
	 * 
	 * @return  programaFinal
	 */
	public String getProgramaFinal() {
		return programaFinal;
	}
	/**
	 * Asigna la variable  programaFinal
	 * 
	 * @param  programaFinal
	 * Variable a asignar en  programaFinal
	 */
	public void setProgramaFinal(String programaFinal) {
		this.programaFinal = programaFinal;
	}
	/**
	 * Retorna la variable bpinInicial
	 * 
	 * @return  bpinInicial
	 */
	public String getBpinInicial() {
		return bpinInicial;
	}
	/**
	 * Asigna la variable  bpinInicial
	 * 
	 * @param  bpinInicial
	 * Variable a asignar en  bpinInicial
	 */
	public void setBpinInicial(String bpinInicial) {
		this.bpinInicial = bpinInicial;
	}
	/**
	 * Retorna la variable bpinFinal
	 * 
	 * @return  bpinFinal
	 */
	public String getBpinFinal() {
		return bpinFinal;
	}
	/**
	 * Asigna la variable  bpinFinal
	 * 
	 * @param  bpinFinal
	 * Variable a asignar en  bpinFinal
	 */
	public void setBpinFinal(String bpinFinal) {
		this.bpinFinal = bpinFinal;
	}
	/**
	 * Retorna la variable cpcInicial
	 * 
	 * @return  cpcInicial
	 */
	public String getCpcInicial() {
		return cpcInicial;
	}
	/**
	 * Asigna la variable  cpcInicial
	 * 
	 * @param  cpcInicial
	 * Variable a asignar en  cpcInicial
	 */
	public void setCpcInicial(String cpcInicial) {
		this.cpcInicial = cpcInicial;
	}
	/**
	 * Retorna la variable cpcFinal
	 * 
	 * @return  cpcFinal
	 */
	public String getCpcFinal() {
		return cpcFinal;
	}
	/**
	 * Asigna la variable  cpcFinal
	 * 
	 * @param  cpcFinal
	 * Variable a asignar en  cpcFinal
	 */
	public void setCpcFinal(String cpcFinal) {
		this.cpcFinal = cpcFinal;
	}
	/**
	 * Retorna la variable uniEjeInicial
	 * 
	 * @return  uniEjeInicial
	 */
	public String getUniEjeInicial() {
		return uniEjeInicial;
	}
	/**
	 * Asigna la variable  uniEjeInicial
	 * 
	 * @param  uniEjeInicial
	 * Variable a asignar en  uniEjeInicial
	 */
	public void setUniEjeInicial(String uniEjeInicial) {
		this.uniEjeInicial = uniEjeInicial;
	}
	/**
	 * Retorna la variable uniEjeFinal
	 * 
	 * @return  uniEjeFinal
	 */
	public String getUniEjeFinal() {
		return uniEjeFinal;
	}
	/**
	 * Asigna la variable  uniEjeFinal
	 * 
	 * @param  uniEjeFinal
	 * Variable a asignar en  uniEjeFinal
	 */
	public void setUniEjeFinal(String uniEjeFinal) {
		this.uniEjeFinal = uniEjeFinal;
	}
	/**
	 * Retorna la variable ccpetInicial
	 * 
	 * @return  ccpetInicial
	 */
	public String getCcpetInicial() {
		return ccpetInicial;
	}
	/**
	 * Asigna la variable  ccpetInicial
	 * 
	 * @param  ccpetInicial
	 * Variable a asignar en  ccpetInicial
	 */
	public void setCcpetInicial(String ccpetInicial) {
		this.ccpetInicial = ccpetInicial;
	}
	/**
	 * Retorna la variable ccpetFinal
	 * 
	 * @return  ccpetFinal
	 */
	public String getCcpetFinal() {
		return ccpetFinal;
	}
	/**
	 * Asigna la variable  ccpetFinal
	 * 
	 * @param  ccpetFinal
	 * Variable a asignar en  ccpetFinal
	 */
	public void setCcpetFinal(String ccpetFinal) {
		this.ccpetFinal = ccpetFinal;
	}
	/**
	 * Retorna la variable fuenteCuipoInicial
	 * 
	 * @return  fuenteCuipoInicial
	 */
	public String getFuenteCuipoInicial() {
		return fuenteCuipoInicial;
	}
	/**
	 * Asigna la variable  fuenteCuipoInicial
	 * 
	 * @param  fuenteCuipoInicial
	 * Variable a asignar en  fuenteCuipoInicial
	 */
	public void setFuenteCuipoInicial(String fuenteCuipoInicial) {
		this.fuenteCuipoInicial = fuenteCuipoInicial;
	}
	/**
	 * Retorna la variable fuenteCuipoFinal
	 * 
	 * @return  fuenteCuipoFinal
	 */
	public String getFuenteCuipoFinal() {
		return fuenteCuipoFinal;
	}
	/**
	 * Asigna la variable  fuenteCuipoFinal
	 * 
	 * @param  fuenteCuipoFinal
	 * Variable a asignar en  fuenteCuipoFinal
	 */
	public void setFuenteCuipoFinal(String fuenteCuipoFinal) {
		this.fuenteCuipoFinal = fuenteCuipoFinal;
	}
	/**
	 * Retorna la variable politicaInicial
	 * 
	 * @return  politicaInicial
	 */
	public String getPoliticaInicial() {
		return politicaInicial;
	}
	/**
	 * Asigna la variable  politicaInicial
	 * 
	 * @param  politicaInicial
	 * Variable a asignar en  politicaInicial
	 */
	public void setPoliticaInicial(String politicaInicial) {
		this.politicaInicial = politicaInicial;
	}
	/**
	 * Retorna la variable politicafinal
	 * 
	 * @return  politicafinal
	 */
	public String getPoliticafinal() {
		return politicafinal;
	}
	/**
	 * Asigna la variable  politicafinal
	 * 
	 * @param  politicafinal
	 * Variable a asignar en  politicafinal
	 */
	public void setPoliticafinal(String politicafinal) {
		this.politicafinal = politicafinal;
	}
	/**
	 * Retorna la variable sectorialInicial
	 * 
	 * @return  sectorialInicial
	 */
	public String getSectorialInicial() {
		return sectorialInicial;
	}
	/**
	 * Asigna la variable  sectorialInicial
	 * 
	 * @param  sectorialInicial
	 * Variable a asignar en  sectorialInicial
	 */
	public void setSectorialInicial(String sectorialInicial) {
		this.sectorialInicial = sectorialInicial;
	}
	/**
	 * Retorna la variable sectorialFinal
	 * 
	 * @return  sectorialFinal
	 */
	public String getSectorialFinal() {
		return sectorialFinal;
	}
	/**
	 * Asigna la variable  sectorialFinal
	 * 
	 * @param  sectorialFinal
	 * Variable a asignar en  sectorialFinal
	 */
	public void setSectorialFinal(String sectorialFinal) {
		this.sectorialFinal = sectorialFinal;
	}
	/**
	 * Retorna la variable ccpetRegaInicial
	 * 
	 * @return  ccpetRegaInicial
	 */
	public String getCcpetRegaInicial() {
		return ccpetRegaInicial;
	}
	/**
	 * Asigna la variable  ccpetRegaInicial
	 * 
	 * @param  ccpetRegaInicial
	 * Variable a asignar en  ccpetRegaInicial
	 */
	public void setCcpetRegaInicial(String ccpetRegaInicial) {
		this.ccpetRegaInicial = ccpetRegaInicial;
	}
	/**
	 * Retorna la variable ccpetRegaFinal
	 * 
	 * @return  ccpetRegaFinal
	 */
	public String getCcpetRegaFinal() {
		return ccpetRegaFinal;
	}
	/**
	 * Asigna la variable  ccpetRegaFinal
	 * 
	 * @param  ccpetRegaFinal
	 * Variable a asignar en  ccpetRegaFinal
	 */
	public void setCcpetRegaFinal(String ccpetRegaFinal) {
		this.ccpetRegaFinal = ccpetRegaFinal;
	}
	/**
	 * Retorna la variable sgrInicial
	 * 
	 * @return  sgrInicial
	 */
	public String getSgrInicial() {
		return sgrInicial;
	}
	/**
	 * Asigna la variable  sgrInicial
	 * 
	 * @param  sgrInicial
	 * Variable a asignar en  sgrInicial
	 */
	public void setSgrInicial(String sgrInicial) {
		this.sgrInicial = sgrInicial;
	}
	/**
	 * Retorna la variable sgrFinal
	 * 
	 * @return  sgrFinal
	 */
	public String getSgrFinal() {
		return sgrFinal;
	}
	/**
	 * Asigna la variable  sgrFinal
	 * 
	 * @param  sgrFinal
	 * Variable a asignar en  sgrFinal
	 */
	public void setSgrFinal(String sgrFinal) {
		this.sgrFinal = sgrFinal;
	}
	/**
	 * Retorna la variable nombreCodigoInicial
	 * 
	 * @return  nombreCodigoInicial
	 */
	public String getNombreCodigoInicial() {
		return nombreCodigoInicial;
	}
	/**
	 * Asigna la variable  nombreCodigoInicial
	 * 
	 * @param  nombreCodigoInicial
	 * Variable a asignar en  nombreCodigoInicial
	 */
	public void setNombreCodigoInicial(String nombreCodigoInicial) {
		this.nombreCodigoInicial = nombreCodigoInicial;
	}
	/**
	 * Retorna la variable nombreCodigoFinal
	 * 
	 * @return  nombreCodigoFinal
	 */
	public String getNombreCodigoFinal() {
		return nombreCodigoFinal;
	}
	/**
	 * Asigna la variable  nombreCodigoFinal
	 * 
	 * @param  nombreCodigoFinal
	 * Variable a asignar en  nombreCodigoFinal
	 */
	public void setNombreCodigoFinal(String nombreCodigoFinal) {
		this.nombreCodigoFinal = nombreCodigoFinal;
	}
	/**
	 * Atributo usado para descargar contenidos de archivos desde la
	 * vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}
	//</SET_GET_ATRIBUTOS>
	//<SET_GET_LISTAS>
	/**
	 * Retorna la lista listaAno
	 * 
	 * @return listaAno
	 */
	public List<Registro> getListaAno() {
		return listaAno;
	}
	/**
	 * Asigna la lista listaAno
	 * 
	 * @param listaAno
	 * Variable a asignar en  listaAno
	 */
	public void setListaAno(List<Registro> listaAno) {
		this.listaAno = listaAno;
	}
	/**
	 * Retorna la lista listaMes
	 * 
	 * @return listaMes
	 */
	public List<Registro> getListaMes() {
		return listaMes;
	}
	/**
	 * Asigna la lista listaMes
	 * 
	 * @param listaMes
	 * Variable a asignar en  listaMes
	 */
	public void setListaMes(List<Registro> listaMes) {
		this.listaMes = listaMes;
	}
	//</SET_GET_LISTAS>
	//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaCodigoInicial
	 * 
	 * @return listaCodigoInicial
	 */
	public RegistroDataModelImpl getListaCodigoInicial() {
		return listaCodigoInicial;
	}
	/**
	 * Asigna la lista listaCodigoInicial
	 * 
	 * @param listaCodigoInicial
	 * Variable a asignar en  listaCodigoInicial
	 */
	public void setListaCodigoInicial(RegistroDataModelImpl listaCodigoInicial) {
		this.listaCodigoInicial = listaCodigoInicial;
	}
	/**
	 * Retorna la lista listaCodigoFinal
	 * 
	 * @return listaCodigoFinal
	 */
	public RegistroDataModelImpl getListaCodigoFinal() {
		return listaCodigoFinal;
	}
	/**
	 * Asigna la lista listaCodigoFinal
	 * 
	 * @param listaCodigoFinal
	 * Variable a asignar en  listaCodigoFinal
	 */
	public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
		this.listaCodigoFinal = listaCodigoFinal;
	}
	/**
	 * Retorna la lista listaCentroCostoInicial
	 * 
	 * @return listaCentroCostoInicial
	 */
	public RegistroDataModelImpl getListaCentroCostoInicial() {
		return listaCentroCostoInicial;
	}
	/**
	 * Asigna la lista listaCentroCostoInicial
	 * 
	 * @param listaCentroCostoInicial
	 * Variable a asignar en  listaCentroCostoInicial
	 */
	public void setListaCentroCostoInicial(RegistroDataModelImpl listaCentroCostoInicial) {
		this.listaCentroCostoInicial = listaCentroCostoInicial;
	}
	/**
	 * Retorna la lista listaCentroCostoFinal
	 * 
	 * @return listaCentroCostoFinal
	 */
	public RegistroDataModelImpl getListaCentroCostoFinal() {
		return listaCentroCostoFinal;
	}
	/**
	 * Asigna la lista listaCentroCostoFinal
	 * 
	 * @param listaCentroCostoFinal
	 * Variable a asignar en  listaCentroCostoFinal
	 */
	public void setListaCentroCostoFinal(RegistroDataModelImpl listaCentroCostoFinal) {
		this.listaCentroCostoFinal = listaCentroCostoFinal;
	}
	/**
	 * Retorna la lista listaReferenciaInicial
	 * 
	 * @return listaReferenciaInicial
	 */
	public RegistroDataModelImpl getListaReferenciaInicial() {
		return listaReferenciaInicial;
	}
	/**
	 * Asigna la lista listaReferenciaInicial
	 * 
	 * @param listaReferenciaInicial
	 * Variable a asignar en  listaReferenciaInicial
	 */
	public void setListaReferenciaInicial(RegistroDataModelImpl listaReferenciaInicial) {
		this.listaReferenciaInicial = listaReferenciaInicial;
	}
	/**
	 * Retorna la lista listaReferenciaFinal
	 * 
	 * @return listaReferenciaFinal
	 */
	public RegistroDataModelImpl getListaReferenciaFinal() {
		return listaReferenciaFinal;
	}
	/**
	 * Asigna la lista listaReferenciaFinal
	 * 
	 * @param listaReferenciaFinal
	 * Variable a asignar en  listaReferenciaFinal
	 */
	public void setListaReferenciaFinal(RegistroDataModelImpl listaReferenciaFinal) {
		this.listaReferenciaFinal = listaReferenciaFinal;
	}
	/**
	 * Retorna la lista listaFuenteInicial
	 * 
	 * @return listaFuenteInicial
	 */
	public RegistroDataModelImpl getListaFuenteInicial() {
		return listaFuenteInicial;
	}
	/**
	 * Asigna la lista listaFuenteInicial
	 * 
	 * @param listaFuenteInicial
	 * Variable a asignar en  listaFuenteInicial
	 */
	public void setListaFuenteInicial(RegistroDataModelImpl listaFuenteInicial) {
		this.listaFuenteInicial = listaFuenteInicial;
	}
	/**
	 * Retorna la lista listaFuenteFinal
	 * 
	 * @return listaFuenteFinal
	 */
	public RegistroDataModelImpl getListaFuenteFinal() {
		return listaFuenteFinal;
	}
	/**
	 * Asigna la lista listaFuenteFinal
	 * 
	 * @param listaFuenteFinal
	 * Variable a asignar en  listaFuenteFinal
	 */
	public void setListaFuenteFinal(RegistroDataModelImpl listaFuenteFinal) {
		this.listaFuenteFinal = listaFuenteFinal;
	}
	/**
	 * Retorna la lista listaAuxiliarInicial
	 * 
	 * @return listaAuxiliarInicial
	 */
	public RegistroDataModelImpl getListaAuxiliarInicial() {
		return listaAuxiliarInicial;
	}
	/**
	 * Asigna la lista listaAuxiliarInicial
	 * 
	 * @param listaAuxiliarInicial
	 * Variable a asignar en  listaAuxiliarInicial
	 */
	public void setListaAuxiliarInicial(RegistroDataModelImpl listaAuxiliarInicial) {
		this.listaAuxiliarInicial = listaAuxiliarInicial;
	}
	/**
	 * Retorna la lista listaAuxiliarFinal
	 * 
	 * @return listaAuxiliarFinal
	 */
	public RegistroDataModelImpl getListaAuxiliarFinal() {
		return listaAuxiliarFinal;
	}
	/**
	 * Asigna la lista listaAuxiliarFinal
	 * 
	 * @param listaAuxiliarFinal
	 * Variable a asignar en  listaAuxiliarFinal
	 */
	public void setListaAuxiliarFinal(RegistroDataModelImpl listaAuxiliarFinal) {
		this.listaAuxiliarFinal = listaAuxiliarFinal;
	}
	/**
	 * Retorna la lista listaSectorInicial
	 * 
	 * @return listaSectorInicial
	 */
	public RegistroDataModelImpl getListaSectorInicial() {
		return listaSectorInicial;
	}
	/**
	 * Asigna la lista listaSectorInicial
	 * 
	 * @param listaSectorInicial
	 * Variable a asignar en  listaSectorInicial
	 */
	public void setListaSectorInicial(RegistroDataModelImpl listaSectorInicial) {
		this.listaSectorInicial = listaSectorInicial;
	}
	/**
	 * Retorna la lista listaSectorFinal
	 * 
	 * @return listaSectorFinal
	 */
	public RegistroDataModelImpl getListaSectorFinal() {
		return listaSectorFinal;
	}
	/**
	 * Asigna la lista listaSectorFinal
	 * 
	 * @param listaSectorFinal
	 * Variable a asignar en  listaSectorFinal
	 */
	public void setListaSectorFinal(RegistroDataModelImpl listaSectorFinal) {
		this.listaSectorFinal = listaSectorFinal;
	}
	/**
	 * Retorna la lista listaSubProgramaInicial
	 * 
	 * @return listaSubProgramaInicial
	 */
	public RegistroDataModelImpl getListaSubProgramaInicial() {
		return listaSubProgramaInicial;
	}
	/**
	 * Asigna la lista listaSubProgramaInicial
	 * 
	 * @param listaSubProgramaInicial
	 * Variable a asignar en  listaSubProgramaInicial
	 */
	public void setListaSubProgramaInicial(RegistroDataModelImpl listaSubProgramaInicial) {
		this.listaSubProgramaInicial = listaSubProgramaInicial;
	}
	/**
	 * Retorna la lista listaSubProgramaFinal
	 * 
	 * @return listaSubProgramaFinal
	 */
	public RegistroDataModelImpl getListaSubProgramaFinal() {
		return listaSubProgramaFinal;
	}
	/**
	 * Asigna la lista listaSubProgramaFinal
	 * 
	 * @param listaSubProgramaFinal
	 * Variable a asignar en  listaSubProgramaFinal
	 */
	public void setListaSubProgramaFinal(RegistroDataModelImpl listaSubProgramaFinal) {
		this.listaSubProgramaFinal = listaSubProgramaFinal;
	}
	/**
	 * Retorna la lista listaProductoInicial
	 * 
	 * @return listaProductoInicial
	 */
	public RegistroDataModelImpl getListaProductoInicial() {
		return listaProductoInicial;
	}
	/**
	 * Asigna la lista listaProductoInicial
	 * 
	 * @param listaProductoInicial
	 * Variable a asignar en  listaProductoInicial
	 */
	public void setListaProductoInicial(RegistroDataModelImpl listaProductoInicial) {
		this.listaProductoInicial = listaProductoInicial;
	}
	/**
	 * Retorna la lista listaProductoFinal
	 * 
	 * @return listaProductoFinal
	 */
	public RegistroDataModelImpl getListaProductoFinal() {
		return listaProductoFinal;
	}
	/**
	 * Asigna la lista listaProductoFinal
	 * 
	 * @param listaProductoFinal
	 * Variable a asignar en  listaProductoFinal
	 */
	public void setListaProductoFinal(RegistroDataModelImpl listaProductoFinal) {
		this.listaProductoFinal = listaProductoFinal;
	}
	/**
	 * Retorna la lista listaProgramaInicial
	 * 
	 * @return listaProgramaInicial
	 */
	public RegistroDataModelImpl getListaProgramaInicial() {
		return listaProgramaInicial;
	}
	/**
	 * Asigna la lista listaProgramaInicial
	 * 
	 * @param listaProgramaInicial
	 * Variable a asignar en  listaProgramaInicial
	 */
	public void setListaProgramaInicial(RegistroDataModelImpl listaProgramaInicial) {
		this.listaProgramaInicial = listaProgramaInicial;
	}
	/**
	 * Retorna la lista listaProgramaFinal
	 * 
	 * @return listaProgramaFinal
	 */
	public RegistroDataModelImpl getListaProgramaFinal() {
		return listaProgramaFinal;
	}
	/**
	 * Asigna la lista listaProgramaFinal
	 * 
	 * @param listaProgramaFinal
	 * Variable a asignar en  listaProgramaFinal
	 */
	public void setListaProgramaFinal(RegistroDataModelImpl listaProgramaFinal) {
		this.listaProgramaFinal = listaProgramaFinal;
	}
	/**
	 * Retorna la lista listaBpinInicial
	 * 
	 * @return listaBpinInicial
	 */
	public RegistroDataModelImpl getListaBpinInicial() {
		return listaBpinInicial;
	}
	/**
	 * Asigna la lista listaBpinInicial
	 * 
	 * @param listaBpinInicial
	 * Variable a asignar en  listaBpinInicial
	 */
	public void setListaBpinInicial(RegistroDataModelImpl listaBpinInicial) {
		this.listaBpinInicial = listaBpinInicial;
	}
	/**
	 * Retorna la lista listaBpinFinal
	 * 
	 * @return listaBpinFinal
	 */
	public RegistroDataModelImpl getListaBpinFinal() {
		return listaBpinFinal;
	}
	/**
	 * Asigna la lista listaBpinFinal
	 * 
	 * @param listaBpinFinal
	 * Variable a asignar en  listaBpinFinal
	 */
	public void setListaBpinFinal(RegistroDataModelImpl listaBpinFinal) {
		this.listaBpinFinal = listaBpinFinal;
	}
	/**
	 * Retorna la lista listaCpcInicial
	 * 
	 * @return listaCpcInicial
	 */
	public RegistroDataModelImpl getListaCpcInicial() {
		return listaCpcInicial;
	}
	/**
	 * Asigna la lista listaCpcInicial
	 * 
	 * @param listaCpcInicial
	 * Variable a asignar en  listaCpcInicial
	 */
	public void setListaCpcInicial(RegistroDataModelImpl listaCpcInicial) {
		this.listaCpcInicial = listaCpcInicial;
	}
	/**
	 * Retorna la lista listaCpcFinal
	 * 
	 * @return listaCpcFinal
	 */
	public RegistroDataModelImpl getListaCpcFinal() {
		return listaCpcFinal;
	}
	/**
	 * Asigna la lista listaCpcFinal
	 * 
	 * @param listaCpcFinal
	 * Variable a asignar en  listaCpcFinal
	 */
	public void setListaCpcFinal(RegistroDataModelImpl listaCpcFinal) {
		this.listaCpcFinal = listaCpcFinal;
	}
	/**
	 * Retorna la lista listaUnidadInicial
	 * 
	 * @return listaUnidadInicial
	 */
	public RegistroDataModelImpl getListaUnidadInicial() {
		return listaUnidadInicial;
	}
	/**
	 * Asigna la lista listaUnidadInicial
	 * 
	 * @param listaUnidadInicial
	 * Variable a asignar en  listaUnidadInicial
	 */
	public void setListaUnidadInicial(RegistroDataModelImpl listaUnidadInicial) {
		this.listaUnidadInicial = listaUnidadInicial;
	}
	/**
	 * Retorna la lista listaUnidadFinal
	 * 
	 * @return listaUnidadFinal
	 */
	public RegistroDataModelImpl getListaUnidadFinal() {
		return listaUnidadFinal;
	}
	/**
	 * Asigna la lista listaUnidadFinal
	 * 
	 * @param listaUnidadFinal
	 * Variable a asignar en  listaUnidadFinal
	 */
	public void setListaUnidadFinal(RegistroDataModelImpl listaUnidadFinal) {
		this.listaUnidadFinal = listaUnidadFinal;
	}
	/**
	 * Retorna la lista listaCcpetInicial
	 * 
	 * @return listaCcpetInicial
	 */
	public RegistroDataModelImpl getListaCcpetInicial() {
		return listaCcpetInicial;
	}
	/**
	 * Asigna la lista listaCcpetInicial
	 * 
	 * @param listaCcpetInicial
	 * Variable a asignar en  listaCcpetInicial
	 */
	public void setListaCcpetInicial(RegistroDataModelImpl listaCcpetInicial) {
		this.listaCcpetInicial = listaCcpetInicial;
	}
	/**
	 * Retorna la lista listaCcpetFinal
	 * 
	 * @return listaCcpetFinal
	 */
	public RegistroDataModelImpl getListaCcpetFinal() {
		return listaCcpetFinal;
	}
	/**
	 * Asigna la lista listaCcpetFinal
	 * 
	 * @param listaCcpetFinal
	 * Variable a asignar en  listaCcpetFinal
	 */
	public void setListaCcpetFinal(RegistroDataModelImpl listaCcpetFinal) {
		this.listaCcpetFinal = listaCcpetFinal;
	}
	/**
	 * Retorna la lista listaFuenteCuipoInicial
	 * 
	 * @return listaFuenteCuipoInicial
	 */
	public RegistroDataModelImpl getListaFuenteCuipoInicial() {
		return listaFuenteCuipoInicial;
	}
	/**
	 * Asigna la lista listaFuenteCuipoInicial
	 * 
	 * @param listaFuenteCuipoInicial
	 * Variable a asignar en  listaFuenteCuipoInicial
	 */
	public void setListaFuenteCuipoInicial(RegistroDataModelImpl listaFuenteCuipoInicial) {
		this.listaFuenteCuipoInicial = listaFuenteCuipoInicial;
	}
	/**
	 * Retorna la lista listaFuenteCuipoFinal
	 * 
	 * @return listaFuenteCuipoFinal
	 */
	public RegistroDataModelImpl getListaFuenteCuipoFinal() {
		return listaFuenteCuipoFinal;
	}
	/**
	 * Asigna la lista listaFuenteCuipoFinal
	 * 
	 * @param listaFuenteCuipoFinal
	 * Variable a asignar en  listaFuenteCuipoFinal
	 */
	public void setListaFuenteCuipoFinal(RegistroDataModelImpl listaFuenteCuipoFinal) {
		this.listaFuenteCuipoFinal = listaFuenteCuipoFinal;
	}
	/**
	 * Retorna la lista listaPoliticaInicial
	 * 
	 * @return listaPoliticaInicial
	 */
	public RegistroDataModelImpl getListaPoliticaInicial() {
		return listaPoliticaInicial;
	}
	/**
	 * Asigna la lista listaPoliticaInicial
	 * 
	 * @param listaPoliticaInicial
	 * Variable a asignar en  listaPoliticaInicial
	 */
	public void setListaPoliticaInicial(RegistroDataModelImpl listaPoliticaInicial) {
		this.listaPoliticaInicial = listaPoliticaInicial;
	}
	/**
	 * Retorna la lista listaPoliticaFinal
	 * 
	 * @return listaPoliticaFinal
	 */
	public RegistroDataModelImpl getListaPoliticaFinal() {
		return listaPoliticaFinal;
	}
	/**
	 * Asigna la lista listaPoliticaFinal
	 * 
	 * @param listaPoliticaFinal
	 * Variable a asignar en  listaPoliticaFinal
	 */
	public void setListaPoliticaFinal(RegistroDataModelImpl listaPoliticaFinal) {
		this.listaPoliticaFinal = listaPoliticaFinal;
	}
	/**
	 * Retorna la lista listaSectorialInicial
	 * 
	 * @return listaSectorialInicial
	 */
	public RegistroDataModelImpl getListaSectorialInicial() {
		return listaSectorialInicial;
	}
	/**
	 * Asigna la lista listaSectorialInicial
	 * 
	 * @param listaSectorialInicial
	 * Variable a asignar en  listaSectorialInicial
	 */
	public void setListaSectorialInicial(RegistroDataModelImpl listaSectorialInicial) {
		this.listaSectorialInicial = listaSectorialInicial;
	}
	/**
	 * Retorna la lista listaSectorialFinal
	 * 
	 * @return listaSectorialFinal
	 */
	public RegistroDataModelImpl getListaSectorialFinal() {
		return listaSectorialFinal;
	}
	/**
	 * Asigna la lista listaSectorialFinal
	 * 
	 * @param listaSectorialFinal
	 * Variable a asignar en  listaSectorialFinal
	 */
	public void setListaSectorialFinal(RegistroDataModelImpl listaSectorialFinal) {
		this.listaSectorialFinal = listaSectorialFinal;
	}
	/**
	 * Retorna la lista listaCcpetRegaInicial
	 * 
	 * @return listaCcpetRegaInicial
	 */
	public RegistroDataModelImpl getListaCcpetRegaInicial() {
		return listaCcpetRegaInicial;
	}
	/**
	 * Asigna la lista listaCcpetRegaInicial
	 * 
	 * @param listaCcpetRegaInicial
	 * Variable a asignar en  listaCcpetRegaInicial
	 */
	public void setListaCcpetRegaInicial(RegistroDataModelImpl listaCcpetRegaInicial) {
		this.listaCcpetRegaInicial = listaCcpetRegaInicial;
	}
	/**
	 * Retorna la lista listaCcpetRegaFinal
	 * 
	 * @return listaCcpetRegaFinal
	 */
	public RegistroDataModelImpl getListaCcpetRegaFinal() {
		return listaCcpetRegaFinal;
	}
	/**
	 * Asigna la lista listaCcpetRegaFinal
	 * 
	 * @param listaCcpetRegaFinal
	 * Variable a asignar en  listaCcpetRegaFinal
	 */
	public void setListaCcpetRegaFinal(RegistroDataModelImpl listaCcpetRegaFinal) {
		this.listaCcpetRegaFinal = listaCcpetRegaFinal;
	}
	/**
	 * Retorna la lista listaSgrInicial
	 * 
	 * @return listaSgrInicial
	 */
	public RegistroDataModelImpl getListaSgrInicial() {
		return listaSgrInicial;
	}
	/**
	 * Asigna la lista listaSgrInicial
	 * 
	 * @param listaSgrInicial
	 * Variable a asignar en  listaSgrInicial
	 */
	public void setListaSgrInicial(RegistroDataModelImpl listaSgrInicial) {
		this.listaSgrInicial = listaSgrInicial;
	}
	/**
	 * Retorna la lista listaSgrFinal
	 * 
	 * @return listaSgrFinal
	 */
	public RegistroDataModelImpl getListaSgrFinal() {
		return listaSgrFinal;
	}
	/**
	 * Asigna la lista listaSgrFinal
	 * 
	 * @param listaSgrFinal
	 * Variable a asignar en  listaSgrFinal
	 */
	public void setListaSgrFinal(RegistroDataModelImpl listaSgrFinal) {
		this.listaSgrFinal = listaSgrFinal;
	}
	//</SET_GET_LISTAS_COMBO_GRANDE>
	//<SET_GET_LISTAS_SUBFORM>
	//</SET_GET_LISTAS_SUBFORM>
	//<SET_GET_PARAMETROS>
	//</SET_GET_PARAMETROS>
	//<SET_GET_ADICIONALES>	
	//</SET_GET_ADICIONALES>
}

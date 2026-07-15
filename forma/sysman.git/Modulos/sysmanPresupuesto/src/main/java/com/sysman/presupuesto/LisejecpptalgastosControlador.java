/*-
 * LisejecpptalgastosControlador.java
 *
 * 1.0
 *
 * 06/12/2017
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.LisejecpptalgastosControladorEnum;
import com.sysman.presupuesto.enums.LisejecpptalgastosControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;



/**
 * Formulario que permite generar los informes que presentan la Ejecuci�n del
 * presupuesto de gastos e inversiones
 *
 * @version 1.0, 06/12/2017
 * @author lcortes
 *
 * @version 2.0, 27/12/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno de los
 *         combos, se agrego metodo de generacion de reportes.
 */

@ManagedBean
@ViewScoped
public class LisejecpptalgastosControlador extends BeanBaseModal {
	/**
	 * Constante a nivel de clase que almacena el codigo de la compania en la cual
	 * inicio sesion el usuario, el valor de esta constante es asignado en el
	 * constructor a la variable de sesion correspondiente
	 */
	private final String compania;
	/**
	 * Constante a nivel de clase que almacena el codigo del modulo
	 */
	private final String modulo;
	// <DECLARAR_ATRIBUTOS>
	/**
	 * Atributo que contiene el valor asignado al tipo de cuenta en la forma del
	 * formulario.
	 */
	private String tipoCuenta;
	/**
	 * Atributo que contiene el valor asignado a la cuenta inicial en la forma del
	 * formulario.
	 */
	private String cuentaInicial;
	/**
	 * Atributo que contiene el valor asignado a la cuenta final en la forma del
	 * formulario.
	 */
	private String cuentaFinal;
	/**
	 * Atributo que contiene el valor asignado al mes en la forma del formulario.
	 */
	private String mes;
	/**
	 * Atributo que contiene el valor asignado al anio en la forma del formulario.
	 */
	private String anio;
	/**
	 * Atributo que contiene el valor asignado al nivel en la forma del formulario.
	 */
	private String nivel;
	/**
	 * Atributo que contiene el valor asignado a la dependencia inicial en la forma
	 * del formulario.
	 */
	private String dependenciaInicial;
	/**
	 * Atributo que contiene el valor asignado a la dependencia final en la forma
	 * del formulario.
	 */
	private String dependenciaFinal;
	/**
	 * Atributo que contiene el valor del nombre de la dependencia inicial
	 * seleccionada en el combo.
	 */
	private String nombreDepInicial;
	/**
	 * Atributo que contiene el valor del nombre de la dependencia final
	 * seleccionada en el combo.
	 */
	private String nombreDepFinal;

	/**
	 * Obtiene el valor del check por dependencia para agregar filtros a la consulta
	 * y mostrar el reporte respectivo
	 */
	private boolean porDependencia;

	private boolean formatoEspecialExcel;
	/**
	 * listado inicial de dependencia por compania
	 */
	private RegistroDataModelImpl listaDependenciaInicial;

	/**
	 * listado final de dependencia por compania
	 */
	private RegistroDataModelImpl listaDependenciaFinal;

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	private StreamedContent archivoDescarga;
	// </DECLARAR_ATRIBUTOS>
	// <DECLARAR_PARAMETROS>
	// </DECLARAR_PARAMETROS>
	// <DECLARAR_LISTAS>

	/** Lista que contiene los detalles del combo anio */
	private List<Registro> listaAno;

	// </DECLARAR_LISTAS>
	// <DECLARAR_LISTAS_COMBO_GRANDE>
	/** Lista que contiene los detalles del combo cuenta inicial. */
	private RegistroDataModelImpl listaCuentaInicial;

	/** Lista que contiene los detalles del combo cuenta final. */
	private RegistroDataModelImpl listaCuentaFinal;

	// </DECLARAR_LISTAS_COMBO_GRANDE>

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

	/**
	 * Crea una nueva instancia de LisejecpptalgastosControlador
	 */
	public LisejecpptalgastosControlador() {
		super();
		compania = SessionUtil.getCompania();
		modulo = SessionUtil.getModulo();
		try {
			numFormulario = GeneralCodigoFormaEnum.LIS_EJECPPTAL_GASTOS_CONTROLADOR.getCodigo();
			validarPermisos();
			anio = String.valueOf(SysmanFunciones.ano(new Date()));
            mes = String.valueOf(SysmanFunciones.mes(new Date()));
            cuentaInicial = "0";
            cuentaFinal = "9999999999999999";
			// <INI_ADICIONAL>
			// </INI_ADICIONAL>
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			SessionUtil.redireccionarMenuPermisos();
		}
	}

	/**
	 * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
	 * sido creado, en este se realizan las asignaciones iniciales necesarias para
	 * la visualizacion del formulario, como son tablas, origenes de datos,
	 * inicializacion de listas y demas necesarios
	 */
	@PostConstruct
	public void inicializar() {
		// <CARGAR_LISTA>
		cargarListaCuentaInicial();
		cargarListaCuentaFinal();
		cargarListaAno();
		cargarListaDependenciaInicial();
		cargarListaDependenciaFinal();
		// </CARGAR_LISTA>
		// <CARGAR_LISTA_COMBO_GRANDE>
		// </CARGAR_LISTA_COMBO_GRANDE>
		// <CREAR_ARBOLES>
		// </CREAR_ARBOLES>
		abrirFormulario();
	}

	/**
	 * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
	 * tener en cuenta en el momento de apertura del formulario
	 */
	@Override
	public void abrirFormulario() {
		// <CODIGO_DESARROLLADO>
		tipoCuenta = "1";
		nivel = "6";
		porDependencia = false;
		// </CODIGO_DESARROLLADO>
	}

//<METODOS_CARGAR_LISTA>
	/**
	 * 
	 * Carga la lista listaAno
	 */
	public void cargarListaAno() {
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		try {
			listaAno = RegistroConverter
					.toListRegistro(
							requestManager.getList(
									UrlServiceUtil.getInstance()
											.getUrlServiceByUrlByEnumID(
													LisejecpptalgastosControladorUrlEnum.URL6866.getValue())
											.getUrl(),
									param));
		} catch (SystemException e) {
			logger.error(e.getMessage(), e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

	}

	/**
	 * 
	 * Carga la lista listaCuentaInicial
	 */
	public void cargarListaCuentaInicial() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(LisejecpptalgastosControladorUrlEnum.URL4589.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(GeneralParameterEnum.NATURALEZA.getName(), "D");

		listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				LisejecpptalgastosControladorEnum.ID.getValue());
	}

	/**
	 * 
	 * Carga la lista listaCuentaFinal
	 */
	public void cargarListaCuentaFinal() {
		UrlBean urlBean = UrlServiceUtil.getInstance()
				.getUrlServiceByUrlByEnumID(LisejecpptalgastosControladorUrlEnum.URL5292.getValue());
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
		param.put(GeneralParameterEnum.ANO.getName(), anio);
		param.put(LisejecpptalgastosControladorEnum.CUENTAINICIAL.getValue(), cuentaInicial);
		param.put(GeneralParameterEnum.NATURALEZA.getName(), "D");

		listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
				LisejecpptalgastosControladorEnum.ID.getValue());
	}

	 public void cargarListaDependenciaInicial()
	    {
	        UrlBean urlBean = UrlServiceUtil.getInstance()
	                        .getUrlServiceByUrlByEnumID(
	                                        LisejecpptalgastosControladorUrlEnum.URL6867
	                                                        .getValue());
	        Map<String, Object> param = new TreeMap<>();
	        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

	        listaDependenciaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
	                        urlBean.getUrlConteo().getUrl(), param, true,
	                        GeneralParameterEnum.CODIGO.getName());
	    }

	    public void cargarListaDependenciaFinal()
	    {
	        UrlBean urlBean = UrlServiceUtil.getInstance()
	                        .getUrlServiceByUrlByEnumID(
	                                        LisejecpptalgastosControladorUrlEnum.URL6868
	                                                        .getValue());
	        Map<String, Object> param = new TreeMap<>();
	        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
	        param.put(LisejecpptalgastosControladorEnum.CODIGOINICIAL.getValue(),
	                        dependenciaInicial);

	        listaDependenciaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
	                        urlBean.getUrlConteo().getUrl(), param, true,
	                        GeneralParameterEnum.CODIGO.getName());
	    }

//</METODOS_CARGAR_LISTA>
//<METODOS_BOTONES>

	    public void oprimirImprimir()
	    {
	        // <CODIGO_DESARROLLADO>
	        generarInforme(ReportesBean.FORMATOS.PDF);
	        // </CODIGO_DESARROLLADO>
	    }

	    public void oprimirExcel()
	    {
	    	// <CODIGO_DESARROLLADO>
	    	if (formatoEspecialExcel ) {
	    		Map<String, Object> reemplazos = new HashMap<>();
				reemplazos.put("compania", compania);
				reemplazos.put("anio", anio);
				reemplazos.put("mes", mes);
				reemplazos.put("cuentaInicial", cuentaInicial);
				reemplazos.put("cuentaFinal", cuentaFinal);
				reemplazos.put("tipoCuenta", tipoCuenta);
				reemplazos.put("nivel", nivel);
				reemplazos.put("dependenciaInicial", dependenciaInicial);
				reemplazos.put("dependenciaFinal", dependenciaFinal);
				
				String strSql = "";
				if(porDependencia) {
					 strSql = Reporteador.resuelveConsulta("800645LisEjecPptalGastosXDepPlano",
							Integer.parseInt(modulo), reemplazos);
				}else {
					 strSql = Reporteador.resuelveConsulta("800529LisEjecPptalGastos",
						Integer.parseInt(modulo), reemplazos);
				}

		        try (ByteArrayOutputStream out = new ByteArrayOutputStream();)

		        {
		                Workbook workbook = new XSSFWorkbook(
		                                JsfUtil.exportarHojaDatosStreamed(strSql,
		                                                ConectorPool.ESQUEMA_SYSMAN,
		                                                FORMATOS.EXCEL).getStream());
		                
		                workbook.setForceFormulaRecalculation(true);
		                workbook.write(out);
		                out.close();
		                workbook.close();
					
					archivoDescarga = JsfUtil.getArchivoDescarga(
							new ByteArrayInputStream(out.toByteArray()),
							  SysmanFunciones.concatenar("INFORME GASTOS", ".xlsx"));
					
				} catch (IOException | JRException | SQLException | DRException | SysmanException e) {
					logger.error(e.getMessage(), e);
					JsfUtil.agregarMensajeError(e.getMessage());
				}				

			} else {
				generarInforme(FORMATOS.EXCEL);
			}
	    }
	    
	    private void generarInforme(ReportesBean.FORMATOS formato)
	    {
	        archivoDescarga = null;
	        try
	        {	        	
	            String reporte;
	            Map<String, Object> reemplazos = new HashMap<>();
	            reemplazos.put("compania", compania);
	            reemplazos.put("anio", anio);
	            reemplazos.put("mes", mes);
	            reemplazos.put("cuentaInicial", cuentaInicial);
	            reemplazos.put("cuentaFinal", cuentaFinal);
	            reemplazos.put("tipoCuenta", tipoCuenta);
	            reemplazos.put("nivel", nivel);
	            reemplazos.put("dependenciaInicial", dependenciaInicial);
	            reemplazos.put("dependenciaFinal", dependenciaFinal);

	            Map<String, Object> parametros = new HashMap<>();
	            parametros.put("PR_NOMBREMES", ejbSysmanUtil
	                            .mostrarNombreDeMes(Integer.parseInt(mes)));
	            parametros.put("PR_ANIO", anio);

	            if (porDependencia)
	            {
	                reporte = "001540LisEjecPptalGastosXDependencia";
	            }
	            else
	            {
	                if ("SI".equals(ejbSysmanUtil.consultarParametro(compania,
	                                "FORMATO CALIDAD", modulo, new Date(), true)))
	                {
	                    reporte = "001535LisEjecPptalGastosCOS";
	                }
	                else
	                {
	                    reporte = "001534LisEjecPptalGastos";
	                }
	            }

	            Reporteador.resuelveConsulta(porDependencia
	                ? "001540LisEjecPptalGastosXDependencia"
	                : "001534LisEjecPptalGastos",
	                            Integer.parseInt(modulo),
	                            reemplazos, parametros);	            
	            

	            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
	                            ConectorPool.ESQUEMA_SYSMAN, formato);
	        }
	        catch (NumberFormatException | SystemException | JRException
	                        | IOException | SysmanException e)
	        {
	            logger.error(e.getMessage(), e);
	            JsfUtil.agregarMensajeError(e.getMessage());
	        }
	    }


//</METODOS_BOTONES>
//<METODOS_CAMBIAR>
	/**
	 * Metodo ejecutado al cambiar el control Ano
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	    public void cambiarAno()
	    {
	        cuentaInicial = cuentaFinal = null;
	        cargarListaCuentaInicial();
	        cargarListaCuentaFinal();
	    }

	/**
	 * Metodo ejecutado al cambiar el control PorDependencia
	 * 
	 * TODO DOCUMENTACION ADICIONAL
	 * 
	 */
	public void cambiarPorDependencia() {
		// <CODIGO_DESARROLLADO>
		// </CODIGO_DESARROLLADO>
	}

//</METODOS_CAMBIAR>
//<METODOS_COMBOS_GRANDES>
	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCuentaInicial
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	 public void seleccionarFilaCuentaInicial(SelectEvent event)
	    {
	        Registro registroAux = (Registro) event.getObject();
	        cuentaInicial = registroAux.getCampos()
	                        .get(LisejecpptalgastosControladorEnum.ID.getValue())
	                        .toString();
	        cuentaFinal = null;
	        cargarListaCuentaFinal();
	    }

	/**
	 * 
	 * Metodo ejecutado al seleccionar una fila de la lista listaCuentaFinal
	 *
	 * TODO DOCUMENTACION ADICIONAL
	 *
	 * @param event objeto que encapsula la accion proveniente de la vista
	 */
	 public void seleccionarFilaCuentaFinal(SelectEvent event)
	    {
	        Registro registroAux = (Registro) event.getObject();
	        cuentaFinal = registroAux.getCampos()
	                        .get(LisejecpptalgastosControladorEnum.ID.getValue())
	                        .toString();
	    }

	 public void seleccionarFilaDependenciaInicial(SelectEvent event)
	    {
	        Registro registroAux = (Registro) event.getObject();
	        dependenciaInicial = SysmanFunciones.nvl(registroAux.getCampos().get(
	                        GeneralParameterEnum.CODIGO.getName()), "").toString();
	        nombreDepInicial = SysmanFunciones.nvl(registroAux.getCampos().get(
	                        GeneralParameterEnum.NOMBRE.getName()), "").toString();
	        dependenciaFinal = null;
	        cargarListaDependenciaFinal();
	    }

	    public void seleccionarFilaDependenciaFinal(SelectEvent event)
	    {
	        Registro registroAux = (Registro) event.getObject();
	        dependenciaFinal = SysmanFunciones.nvl(registroAux.getCampos().get(
	                        GeneralParameterEnum.CODIGO.getName()), "").toString();
	        nombreDepFinal = SysmanFunciones.nvl(registroAux.getCampos().get(
	                        GeneralParameterEnum.NOMBRE.getName()), "").toString();
	    }

//</METODOS_COMBOS_GRANDES>
//<METODOS_ARBOL>
//</METODOS_ARBOL>
//<SET_GET_ATRIBUTOS>
	/**
	 * Retorna la variable tipoCuenta
	 * 
	 * @return tipoCuenta
	 */
	public String getTipoCuenta() {
		return tipoCuenta;
	}

	/**
	 * Asigna la variable tipoCuenta
	 * 
	 * @param tipoCuenta 
	 * Variable a asignar en tipoCuenta
	 */
	public void setTipoCuenta(String tipoCuenta) {
		this.tipoCuenta = tipoCuenta;
	}

	/**
	 * Retorna la variable porDependencia
	 * 
	 * @return porDependencia
	 */
	public boolean isPorDependencia()
    {
        return porDependencia;
    }

    public void setPorDependencia(boolean porDependencia)
    {
        this.porDependencia = porDependencia;
    }

	/**
	 * Retorna la variable formatoEspecialExcel
	 * 
	 * @return formatoEspecialExcel
	 */
    public boolean isFormatoEspecialExcel() {
        return formatoEspecialExcel;
    }

    public void setFormatoEspecialExcel(boolean formatoEspecialExcel) {
        this.formatoEspecialExcel = formatoEspecialExcel;
    }

	/**
	 * Retorna la variable cuentaInicial
	 * 
	 * @return cuentaInicial
	 */
	public String getCuentaInicial() {
		return cuentaInicial;
	}

	/**
	 * Asigna la variable cuentaInicial
	 * 
	 * @param cuentaInicial Variable a asignar en cuentaInicial
	 */
	public void setCuentaInicial(String cuentaInicial) {
		this.cuentaInicial = cuentaInicial;
	}

	/**
	 * Retorna la variable cuentaFinal
	 * 
	 * @return cuentaFinal
	 */
	public String getCuentaFinal() {
		return cuentaFinal;
	}

	/**
	 * Asigna la variable cuentaFinal
	 * 
	 * @param cuentaFinal Variable a asignar en cuentaFinal
	 */
	public void setCuentaFinal(String cuentaFinal) {
		this.cuentaFinal = cuentaFinal;
	}

	/**
	 * Retorna la variable mes
	 * 
	 * @return mes
	 */
	public String getMes() {
		return mes;
	}

	/**
	 * Asigna la variable mes
	 * 
	 * @param mes Variable a asignar en mes
	 */
	public void setMes(String mes) {
		this.mes = mes;
	}

	/**
	 * Retorna la variable anio
	 * 
	 * @return anio
	 */
	public String getAnio() {
		return anio;
	}

	/**
	 * Asigna la variable anio
	 * 
	 * @param anio Variable a asignar en anio
	 */
	public void setAnio(String anio) {
		this.anio = anio;
	}

	/**
	 * Retorna la variable dependenciaInicial
	 * 
	 * @return dependenciaInicial
	 */
	public String getDependenciaInicial() {
		return dependenciaInicial;
	}

	/**
	 * Asigna la variable dependenciaInicial
	 * 
	 * @param dependenciaInicial Variable a asignar en dependenciaInicial
	 */
	public void setDependenciaInicial(String dependenciaInicial) {
		this.dependenciaInicial = dependenciaInicial;
	}

	/**
	 * Retorna la variable dependenciaFinal
	 * 
	 * @return dependenciaFinal
	 */
	public String getDependenciaFinal() {
		return dependenciaFinal;
	}

	/**
	 * Asigna la variable dependenciaFinal
	 * 
	 * @param dependenciaFinal Variable a asignar en dependenciaFinal
	 */
	public void setDependenciaFinal(String dependenciaFinal) {
		this.dependenciaFinal = dependenciaFinal;
	}

	/**
	 * Retorna la variable nivel
	 * 
	 * @return nivel
	 */
	public String getNivel() {
		return nivel;
	}

	/**
	 * Asigna la variable nivel
	 * 
	 * @param nivel Variable a asignar en nivel
	 */
	public void setNivel(String nivel) {
		this.nivel = nivel;
	}

	/**
	 * Retorna la variable nombreDepInicial
	 * 
	 * @return nombreDepInicial
	 */
	public String getNombreDepInicial() {
		return nombreDepInicial;
	}

	/**
	 * Asigna la variable nombreDepInicial
	 * 
	 * @param nombreDepInicial Variable a asignar en nombreDepInicial
	 */
	public void setNombreDepInicial(String nombreDepInicial) {
		this.nombreDepInicial = nombreDepInicial;
	}

	/**
	 * Retorna la variable nombreDepFinal
	 * 
	 * @return nombreDepFinal
	 */
	public String getNombreDepFinal() {
		return nombreDepFinal;
	}

	/**
	 * Asigna la variable nombreDepFinal
	 * 
	 * @param nombreDepFinal Variable a asignar en nombreDepFinal
	 */
	public void setNombreDepFinal(String nombreDepFinal) {
		this.nombreDepFinal = nombreDepFinal;
	}

	/**
	 * Atributo usado para descargar contenidos de archivos desde la vista
	 */
	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

//</SET_GET_ATRIBUTOS>
//<SET_GET_PARAMETROS>
//</SET_GET_PARAMETROS>
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
	 * @param listaAno Variable a asignar en listaAno
	 */
	public void setListaAno(List<Registro> listaAno) {
		this.listaAno = listaAno;
	}

//</SET_GET_LISTAS>
//<SET_GET_LISTAS_COMBO_GRANDE>	
	/**
	 * Retorna la lista listaCuentaInicial
	 * 
	 * @return listaCuentaInicial
	 */
	public RegistroDataModelImpl getListaCuentaInicial() {
		return listaCuentaInicial;
	}

	/**
	 * Asigna la lista listaCuentaInicial
	 * 
	 * @param listaCuentaInicial Variable a asignar en listaCuentaInicial
	 */
	public void setListaCuentaInicial(RegistroDataModelImpl listaCuentaInicial) {
		this.listaCuentaInicial = listaCuentaInicial;
	}

	/**
	 * Retorna la lista listaCuentaFinal
	 * 
	 * @return listaCuentaFinal
	 */
	public RegistroDataModelImpl getListaCuentaFinal() {
		return listaCuentaFinal;
	}

	/**
	 * Asigna la lista listaCuentaFinal
	 * 
	 * @param listaCuentaFinal Variable a asignar en listaCuentaFinal
	 */
	public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
		this.listaCuentaFinal = listaCuentaFinal;
	}

	/**
	 * Retorna la lista listaDependenciaInicial
	 * 
	 * @return listaDependenciaInicial
	 */
	public RegistroDataModelImpl getListaDependenciaInicial() {
		return listaDependenciaInicial;
	}

	/**
	 * Asigna la lista listaDependenciaInicial
	 * 
	 * @param listaDependenciaInicial Variable a asignar en listaDependenciaInicial
	 */
	public void setListaDependenciaInicial(RegistroDataModelImpl listaDependenciaInicial) {
		this.listaDependenciaInicial = listaDependenciaInicial;
	}

	/**
	 * Retorna la lista listaDependenciaFinal
	 * 
	 * @return listaDependenciaFinal
	 */
	public RegistroDataModelImpl getListaDependenciaFinal() {
		return listaDependenciaFinal;
	}

	/**
	 * Asigna la lista listaDependenciaFinal
	 * 
	 * @param listaDependenciaFinal Variable a asignar en listaDependenciaFinal
	 */
	public void setListaDependenciaFinal(RegistroDataModelImpl listaDependenciaFinal) {
		this.listaDependenciaFinal = listaDependenciaFinal;
	}

//</SET_GET_LISTAS_COMBO_GRANDE>
}
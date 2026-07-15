/*-
 * InformeproyectosControlador.java
 *
 * 1.0
 * 
 * 23/08/2023
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

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
import com.sysman.almacen.enums.InformeproyectosControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;
import net.sf.jasperreports.engine.JRException;
/**
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 23/08/2023
 * @author jcrojas2
 */
@ManagedBean
@ViewScoped
public class  InformeproyectosControlador extends BeanBaseModal{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Atributo en el cual se almacena el proyecto inicial
     */
    private String proyectoInicial;
    /**
     * Atributo en el cual se almacena el proyecto final
     */
    private String proyectoFinal;
    /**
     * Atributo en el cual se almacena la bodega inicial
     */
    private String bodegaInicial;
    /**
     * Atributo en el cual se almacena la bodega final
     */
    private String bodegaFinal;
    /**
     * Atributo en el cual se almacena el elemento inicial
     */
    private String elementoInicial;
    /**
     * Atributo en el cual se almacena el elemento final
     */
    private String elementoFinal;
    /**
     * Atributo en el cual se almacena la fecha de corte
     */
    private Date fechaCorte;
    /**
     * Atributo en el cual se almacena el nombre del proyecto inicial
     */
    private String proyectoInicialNom;
    /**
     * Atributo en el cual se almacena el nombre del proyecto final
     */
    private String proyectoFinalNom;
    /**
     * Atributo en el cual se almacena el nombre de la bodega inicial
     */
    private String bodegaInicialNom;
    /**
     * Atributo en el cual se almacena el nombre de la bodega final
     */
    private String bodegaFinalNom;
    /**
     * Atributo en el cual se almacena el nombre del elemento inicial
     */
    private String elementoInicialNom;
    /**
     * Atributo en el cual se almacena el nombre del elemento final
     */
	private String elementoFinalNom;
	/**
	 * Atributo en el cual se almacena el titulo del formulario segun la opcion de menu
	 */
	private String titulo;
	/**
	 * Atributo que controla si los campos del formulario estaran bloqueados
	 */
	private boolean bloqCampos;
	/**
	 * Atributo que indica si los campos de elemento son visibles
	 */
	private boolean verCampElem;
	/**
	 * Atributo que almacena el codigo del menu
	 */
	private String menuActual;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
	private StreamedContent archivoDescarga;
    /**
     * Atributo en el cual se almacena la lista del proyecto
     */
	private RegistroDataModelImpl listaproyectoInicial;
    /**
     * Atributo en el cual se almacena la lista del proyecto
     */
	private RegistroDataModelImpl listaproyectoFinal;
    /**
     * Atributo en el cual se almacena la lista de la bodega
     */
	private RegistroDataModelImpl listabodegaInicial;
    /**
     * Atributo en el cual se almacena la lista de la bodega
     */
	private RegistroDataModelImpl listabodegaFinal;
    /**
     * Atributo en el cual se almacena la lista del elemento
     */
	private RegistroDataModelImpl listaelementoInicial;
    /**
     * Atributo en el cual se almacena la lista del elemento
     */
	private RegistroDataModelImpl listaelementoFinal;
	/**
	 * Atributo que almacena el valor del parámetro
	 */
	private String parPepsIdi;
	/**
	 * Atributo que almacena el valor del parámetro
	 */
	private String manMultiBodega;
	
	@EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    /**
     * Crea una nueva instancia de InformeproyectosControlador
     */
    public InformeproyectosControlador() {
    	super();
        compania = SessionUtil.getCompania();
        menuActual = SessionUtil.getMenuActual();
        
        try {
            numFormulario = GeneralCodigoFormaEnum.INFORME_PROYECTO.getCodigo();
            validarPermisos();
        } catch (Exception ex) {
        	logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }
    
    public void validaFormMenu() {
        
			switch (menuActual) {
	        case "100402021002":
	            titulo = "SALDOS POR PROYECTO";
	            verCampElem = false;
	            break;
	        case "100402021003":
	            titulo = "SALDO DE ENTRADAS POR ELEMENTO SIN PROMEDIAR";
	            verCampElem = true;
	            break;
	        default:
	            break;
	        }
	        
	        if (parPepsIdi.equals("NO")) {
	        	bloqCampos = true;
	        }
	        
	        if (manMultiBodega.equals("SI")) {
	        	bloqCampos = false;
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
    public void inicializar() {
    	cargarListaproyectoInicial(); 
    	cargarListabodegaInicial(); 
    	cargarListaelementoInicial();
    	cargarParametros();
    	abrirFormulario();
    }
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
	public void abrirFormulario() {
    	validaFormMenu();
    	
    	fechaCorte = new Date();
    	proyectoInicial = "0";
    	proyectoFinal = "999999999999";
    	bodegaInicial = "0";
    	bodegaFinal = "999999999999";
    	elementoInicial = "0";
    	elementoFinal = "999999999999";
    }
    /**
     * 
     * Carga la lista listaproyectoInicial
     *
     */
    public void cargarListaproyectoInicial() {
    	Map<String, Object> param = new TreeMap<>();
    	param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    	
    	UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(InformeproyectosControladorUrlEnum.URL32016.getValue());

    	listaproyectoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
    			urlBean.getUrlConteo().getUrl(), param, true,
                GeneralParameterEnum.CODIGO.getName());
    }
    /**
     * 
     * Carga la lista listaproyectoFinal
     *
     */
	public void cargarListaproyectoFinal() {
		Map<String, Object> param = new TreeMap<>();
    	param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    	param.put("CODIGOINICIAL", proyectoInicial);
    	
    	UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(InformeproyectosControladorUrlEnum.URL32018.getValue());

    	listaproyectoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
    			urlBean.getUrlConteo().getUrl(), param, true,
                GeneralParameterEnum.CODIGO.getName());
	}
    /**
     * 
     * Carga la lista listabodegaInicial
     *
     */
	public void cargarListabodegaInicial() {
		Map<String, Object> param = new TreeMap<>();
    	param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    	param.put("CLASE_BODEGA", "20");
    	
    	UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(InformeproyectosControladorUrlEnum.URL135009.getValue());

    	listabodegaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
    			urlBean.getUrlConteo().getUrl(), param, true,
                GeneralParameterEnum.CODIGO.getName());
	}
    /**
     * 
     * Carga la lista listabodegaFinal
     *
     */
	public void cargarListabodegaFinal() {
		Map<String, Object> param = new TreeMap<>();
    	param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    	param.put("CLASE_BODEGA", "20");
    	param.put("CODIGOINI", bodegaInicial);
    	
    	UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(InformeproyectosControladorUrlEnum.URL135011.getValue());

    	listabodegaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
    			urlBean.getUrlConteo().getUrl(), param, true,
                GeneralParameterEnum.CODIGO.getName());
	}
    /**
     * 
     * Carga la lista listaelementoInicial
     *
     */
	public void cargarListaelementoInicial() {
		Map<String, Object> param = new TreeMap<>();
    	param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    	param.put(GeneralParameterEnum.TIPO.getName(), "C");
    	
    	UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(InformeproyectosControladorUrlEnum.URL112183.getValue());

    	listaelementoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
    			urlBean.getUrlConteo().getUrl(), param, true,
                GeneralParameterEnum.CODIGOELEMENTO.getName());
	}
    /**
     * 
     * Carga la lista listaelementoFinal
     *
     */
	public void cargarListaelementoFinal() {
		Map<String, Object> param = new TreeMap<>();
    	param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
    	param.put(GeneralParameterEnum.TIPO.getName(), "C");
    	param.put("ELEMENTOINI", elementoInicial);
    	
    	UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(InformeproyectosControladorUrlEnum.URL112185.getValue());

    	listaelementoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
    			urlBean.getUrlConteo().getUrl(), param, true,
                GeneralParameterEnum.CODIGOELEMENTO.getName());
	}
    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel
     * en la vista
     *
     */
	public void oprimirExcel() {
		archivoDescarga = null;
		if (manMultiBodega.equals("SI")) {
			generaInforme("002631SaldosProyectoBodega");
        } else {
        	generaInforme("002493SaldosPorProyecto");
        }
		;
    }
	/**
     * 
     * Metodo ejecutado al oprimir el boton ExcelEntradas
     * en la vista
     *
     */
	public void oprimirExcelEntradas() {
		archivoDescarga = null;       
		generaInforme("002494SaldoEntrElemSinPromediar");
    }
	
	public void generaInforme(String reporte) {
        try {
        	HashMap<String, Object> reemplazar = new HashMap<>();
        	Map<String, Object> parametros = new HashMap<>();
			
			reemplazar.put("compania", compania);
			reemplazar.put("proyectoIni", proyectoInicial);
			reemplazar.put("proyectoFin", proyectoFinal);
			reemplazar.put("bodegaIni", bodegaInicial);
			reemplazar.put("bodegaFin", bodegaFinal);
			reemplazar.put("elementoIni", SysmanFunciones.nvl(elementoInicial, ""));
			reemplazar.put("elementoFin", SysmanFunciones.nvl(elementoFinal, ""));
			reemplazar.put("fechaCorte", SysmanFunciones.formatearFechaCadena(fechaCorte,"DD/MM/YYYY"));
			
			parametros.put("PR_FECHA_CORTE", SysmanFunciones.convertirAFechaCadena(fechaCorte));
			
			String strSql = Reporteador.resuelveConsulta(reporte, Integer.parseInt(SessionUtil.getModulo()),
	                reemplazar);
			
			parametros.put("PR_STRSQL", strSql);
			
	        archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
	                ConectorPool.ESQUEMA_SYSMAN, ReportesBean.FORMATOS.EXCEL);
        } catch (IOException | SysmanException | JRException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaproyectoInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaproyectoInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		proyectoInicial = SysmanFunciones
						.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "")
						.toString();
		proyectoInicialNom = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBREPROYECTO"), "")
						.toString();
		
		proyectoFinal = null;
		proyectoFinalNom = null;
		cargarListaproyectoFinal();
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaproyectoFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaproyectoFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		proyectoFinal = SysmanFunciones
						.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "")
						.toString();
		proyectoFinalNom = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBREPROYECTO"), "")
						.toString();
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listabodegaInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilabodegaInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		bodegaInicial = SysmanFunciones
						.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "")
						.toString();
		bodegaInicialNom = SysmanFunciones
						.nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "")
						.toString();
		
		bodegaFinal = null;
		bodegaFinalNom = null;
		cargarListabodegaFinal();
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listabodegaFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilabodegaFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		bodegaFinal = SysmanFunciones
						.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "")
						.toString();
		bodegaFinalNom = SysmanFunciones
						.nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "")
						.toString();
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaelementoInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaelementoInicial(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		elementoInicial = SysmanFunciones
						.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGOELEMENTO.getName()), "")
						.toString();
		elementoInicialNom = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRECORTO"), "")
						.toString();
		
		elementoFinal = null;
		elementoFinalNom = null;
		cargarListaelementoFinal();
	}
	    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaelementoFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaelementoFinal(SelectEvent event) {
		Registro registroAux = (Registro) event.getObject();
		elementoFinal = SysmanFunciones
						.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGOELEMENTO.getName()), "")
						.toString();
		elementoFinalNom = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRECORTO"), "")
						.toString();
	}
	
	public void cargarParametros() {
        try {
        	
			parPepsIdi = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					"MANEJA PEPS CONSUMO DE ALMACEN IDIPRON", SessionUtil.getModulo(), 
					new Date(), true), "NO").toString();
			
			manMultiBodega = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,
					"ALMACEN MULTIBODEGAS PROYECTOS", SessionUtil.getModulo(), 
					new Date(), true), "NO").toString();

		} catch (SystemException e) {
			e.printStackTrace();
		}
    }
	
    /**
     * Retorna la variable proyectoInicial
     * 
     * @return  proyectoInicial
     */
	public String getProyectoInicial() {
		return proyectoInicial;
    }
    /**
     * Asigna la variable  proyectoInicial
     * 
     * @param  proyectoInicial
     * Variable a asignar en  proyectoInicial
     */
    public void setProyectoInicial(String proyectoInicial) {
        this.proyectoInicial = proyectoInicial;
    }
    /**
     * Retorna la variable proyectoFinal
     * 
     * @return  proyectoFinal
     */
    public String getProyectoFinal() {
        return proyectoFinal;
    }
    /**
     * Asigna la variable  proyectoFinal
     * 
     * @param  proyectoFinal
     * Variable a asignar en  proyectoFinal
     */
    public void setProyectoFinal(String proyectoFinal) {
        this.proyectoFinal = proyectoFinal;
    }
    /**
     * Retorna la variable bodegaInicial
     * 
     * @return  bodegaInicial
     */
    public String getBodegaInicial() {
        return bodegaInicial;
    }
    /**
     * Asigna la variable  bodegaInicial
     * 
     * @param  bodegaInicial
     * Variable a asignar en  bodegaInicial
     */
    public void setBodegaInicial(String bodegaInicial) {
        this.bodegaInicial = bodegaInicial;
    }
    /**
     * Retorna la variable bodegaFinal
     * 
     * @return  bodegaFinal
     */
    public String getBodegaFinal() {
        return bodegaFinal;
    }
    /**
     * Asigna la variable  bodegaFinal
     * 
     * @param  bodegaFinal
     * Variable a asignar en  bodegaFinal
     */
    public void setBodegaFinal(String bodegaFinal) {
        this.bodegaFinal = bodegaFinal;
    }
    /**
     * Retorna la variable elementoInicial
     * 
     * @return  elementoInicial
     */
    public String getElementoInicial() {
        return elementoInicial;
    }
    /**
     * Asigna la variable  elementoInicial
     * 
     * @param  elementoInicial
     * Variable a asignar en  elementoInicial
     */
    public void setElementoInicial(String elementoInicial) {
        this.elementoInicial = elementoInicial;
    }
    /**
     * Retorna la variable elementoFinal
     * 
     * @return  elementoFinal
     */
    public String getElementoFinal() {
        return elementoFinal;
    }
    /**
     * Asigna la variable  elementoFinal
     * 
     * @param  elementoFinal
     * Variable a asignar en  elementoFinal
     */
    public void setElementoFinal(String elementoFinal) {
        this.elementoFinal = elementoFinal;
    }
    /**
     * Retorna la variable fechaCorte
     * 
     * @return  fechaCorte
     */
    public Date getFechaCorte() {
        return fechaCorte;
    }
    /**
     * Asigna la variable  fechaCorte
     * 
     * @param  fechaCorte
     * Variable a asignar en  fechaCorte
     */
    public void setFechaCorte(Date fechaCorte) {
        this.fechaCorte = fechaCorte;
    }
    /**
     * Retorna la variable proyectoInicialNom
     * 
     * @return  proyectoInicialNom
     */
    public String getProyectoInicialNom() {
        return proyectoInicialNom;
    }
    /**
     * Asigna la variable  proyectoInicialNom
     * 
     * @param  proyectoInicialNom
     * Variable a asignar en  proyectoInicialNom
     */
    public void setProyectoInicialNom(String proyectoInicialNom) {
        this.proyectoInicialNom = proyectoInicialNom;
    }
    /**
     * Retorna la variable proyectoFinalNom
     * 
     * @return  proyectoFinalNom
     */
    public String getProyectoFinalNom() {
        return proyectoFinalNom;
    }
    /**
     * Asigna la variable  proyectoFinalNom
     * 
     * @param  proyectoFinalNom
     * Variable a asignar en  proyectoFinalNom
     */
    public void setProyectoFinalNom(String proyectoFinalNom) {
        this.proyectoFinalNom = proyectoFinalNom;
    }
    /**
     * Retorna la variable bodegaInicialNom
     * 
     * @return  bodegaInicialNom
     */
    public String getBodegaInicialNom() {
        return bodegaInicialNom;
    }
    /**
     * Asigna la variable  bodegaInicialNom
     * 
     * @param  bodegaInicialNom
     * Variable a asignar en  bodegaInicialNom
     */
    public void setBodegaInicialNom(String bodegaInicialNom) {
        this.bodegaInicialNom = bodegaInicialNom;
    }
    /**
     * Retorna la variable bodegaFinalNom
     * 
     * @return  bodegaFinalNom
     */
    public String getBodegaFinalNom() {
        return bodegaFinalNom;
    }
    /**
     * Asigna la variable  bodegaFinalNom
     * 
     * @param  bodegaFinalNom
     * Variable a asignar en  bodegaFinalNom
     */
    public void setBodegaFinalNom(String bodegaFinalNom) {
        this.bodegaFinalNom = bodegaFinalNom;
    }
    /**
     * Retorna la variable elementoInicialNom
     * 
     * @return  elementoInicialNom
     */
    public String getElementoInicialNom() {
        return elementoInicialNom;
    }
    /**
     * Asigna la variable  elementoInicialNom
     * 
     * @param  elementoInicialNom
     * Variable a asignar en  elementoInicialNom
     */
    public void setElementoInicialNom(String elementoInicialNom) {
        this.elementoInicialNom = elementoInicialNom;
    }
    /**
     * Retorna la variable elementoFinalNom
     * 
     * @return  elementoFinalNom
     */
    public String getElementoFinalNom() {
        return elementoFinalNom;
    }
    /**
     * Asigna la variable  elementoFinalNom
     * 
     * @param  elementoFinalNom
     * Variable a asignar en  elementoFinalNom
     */
    public void setElementoFinalNom(String elementoFinalNom) {
        this.elementoFinalNom = elementoFinalNom;
    }
    /**
     * Retorna la variable titulo
     * 
     * @return  titulo
     */
    public String getTitulo() {
        return titulo;
    }
    /**
     * Asigna la variable  titulo
     * 
     * @param  titulo
     * Variable a asignar en  titulo
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    /**
     * Retorna la variable bloqCampos
     * 
     * @return  bloqCampos
     */
    public boolean isBloqCampos() {
        return bloqCampos;
    }
    /**
     * Asigna la variable  bloqCampos
     * 
     * @param  bloqCampos
     * Variable a asignar en  bloqCampos
     */
    public void setBloqCampos(boolean bloqCampos) {
        this.bloqCampos = bloqCampos;
    }
    /**
     * Retorna la variable verCampElem
     * 
     * @return  verCampElem
     */
    public boolean isVerCampElem() {
        return verCampElem;
    }
    /**
     * Asigna la variable  verCampElem
     * 
     * @param  verCampElem
     * Variable a asignar en  verCampElem
     */
    public void setVerCampElem(boolean verCampElem) {
        this.verCampElem = verCampElem;
    }
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }	
    /**
     * Retorna la lista listaproyectoInicial
     * 
     * @return listaproyectoInicial
     */
    public RegistroDataModelImpl getListaproyectoInicial() {
        return listaproyectoInicial;
    }
    /**
     * Asigna la lista listaproyectoInicial
     * 
     * @param listaproyectoInicial
     * Variable a asignar en  listaproyectoInicial
     */
    public void setListaproyectoInicial(RegistroDataModelImpl listaproyectoInicial) {
        this.listaproyectoInicial = listaproyectoInicial;
    }
    /**
     * Retorna la lista listaproyectoFinal
     * 
     * @return listaproyectoFinal
     */
    public RegistroDataModelImpl getListaproyectoFinal() {
        return listaproyectoFinal;
    }
    /**
     * Asigna la lista listaproyectoFinal
     * 
     * @param listaproyectoFinal
     * Variable a asignar en  listaproyectoFinal
     */
    public void setListaproyectoFinal(RegistroDataModelImpl listaproyectoFinal) {
        this.listaproyectoFinal = listaproyectoFinal;
    }
    /**
     * Retorna la lista listabodegaInicial
     * 
     * @return listabodegaInicial
     */
    public RegistroDataModelImpl getListabodegaInicial() {
        return listabodegaInicial;
    }
    /**
     * Asigna la lista listabodegaInicial
     * 
     * @param listabodegaInicial
     * Variable a asignar en  listabodegaInicial
     */
    public void setListabodegaInicial(RegistroDataModelImpl listabodegaInicial) {
        this.listabodegaInicial = listabodegaInicial;
    }
    /**
     * Retorna la lista listabodegaFinal
     * 
     * @return listabodegaFinal
     */
    public RegistroDataModelImpl getListabodegaFinal() {
        return listabodegaFinal;
    }
    /**
     * Asigna la lista listabodegaFinal
     * 
     * @param listabodegaFinal
     * Variable a asignar en  listabodegaFinal
     */
    public void setListabodegaFinal(RegistroDataModelImpl listabodegaFinal) {
        this.listabodegaFinal = listabodegaFinal;
    }
    /**
     * Retorna la lista listaelementoInicial
     * 
     * @return listaelementoInicial
     */
    public RegistroDataModelImpl getListaelementoInicial() {
        return listaelementoInicial;
    }
    /**
     * Asigna la lista listaelementoInicial
     * 
     * @param listaelementoInicial
     * Variable a asignar en  listaelementoInicial
     */
    public void setListaelementoInicial(RegistroDataModelImpl listaelementoInicial) {
        this.listaelementoInicial = listaelementoInicial;
    }
    /**
     * Retorna la lista listaelementoFinal
     * 
     * @return listaelementoFinal
     */
    public RegistroDataModelImpl getListaelementoFinal() {
        return listaelementoFinal;
    }
    /**
     * Asigna la lista listaelementoFinal
     * 
     * @param listaelementoFinal
     * Variable a asignar en  listaelementoFinal
     */
    public void setListaelementoFinal(RegistroDataModelImpl listaelementoFinal) {
        this.listaelementoFinal = listaelementoFinal;
    }
}

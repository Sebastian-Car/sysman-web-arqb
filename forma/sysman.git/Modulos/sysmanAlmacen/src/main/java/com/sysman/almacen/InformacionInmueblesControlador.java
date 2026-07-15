/*-
 * InformacionInmueblesControlador.java
 *
 * 1.0
 * 
 * 08/01/2021
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.primefaces.event.SelectEvent;
import com.sysman.almacen.enums.InformacionInmueblesControladorEnum;
import com.sysman.almacen.enums.InformacionInmueblesControladorUrlEnum;
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
 * TODO Ingrese una descripcion para la clase.
 *
 * @version 1.0, 08/01/2021
 * @author jcrojas2
 */
@ManagedBean
@ViewScoped
public class  InformacionInmueblesControlador extends BeanBaseModal{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante a nivel de clase que almacena el codigo del modulo
     */
    private final String modulo;
    /**
     * Variable que almacena el codigo del elemento
     */
    private String elementoInicial;
    /**
     * Variable que almacena el codigo del elemento
     */
    private String elementoFinal;
    /**
     * Variable que almacena el año
     */
    private String ano;
    /**
     * Variable que almacena el mes
     */
    private int mes;
    /**
     * Variable que almacena el nombre del elemento
     */
    private String nombreElementoInicial;
    /**
     * Variable que almacena el nombre del elemento
     */
    private String nombreElementoFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Lista Año
     */
    private List<Registro> listaAno;
    /**
     * Lista Mes
     */
    private List<Registro> listaMes;
    /**
     * Lista Elemento
     */
    private RegistroDataModelImpl listaCodigoElementoInicial;
    /**
     * Lista Elemento
     */
    private RegistroDataModelImpl listaCodigoElementoFinal;
    
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtl;
    /**
     * Crea una nueva instancia de InformacionInmueblesControlador
     */
    public InformacionInmueblesControlador() 
    {
    	super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        
        try 
        {
            numFormulario = GeneralCodigoFormaEnum.INFORMACION_INMUEBLES
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) 
        {
            logger.error(ex.getMessage(), ex);
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
    public void inicializar()
    {
		cargarListaAno();
		cargarListaMes();
		cargarListaCodigoElementoInicial(); 
		cargarListaCodigoElementoFinal();
		abrirFormulario();
    }
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
	public void abrirFormulario(){}
    /**
     * 
     * Carga la lista listaAno
     *
     */
	public void cargarListaAno()
	{
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        String urlEnumId = InformacionInmueblesControladorUrlEnum.URL4578
                        .getValue();
        String url = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId).getUrl();
        try
        {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(url, param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
	}
    /**
     * 
     * Carga la lista listaMes
     *
     */
	public void cargarListaMes()
	{
		Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        String urlEnumId = InformacionInmueblesControladorUrlEnum.URL8996.getValue();
        
        String url = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(urlEnumId).getUrl();
        try
        {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(url, param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
	}
    /**
     * 
     * Carga la lista listaCodigoElementoInicial
     *
     */
	public void cargarListaCodigoElementoInicial()
	{
		UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                		  InformacionInmueblesControladorUrlEnum.URL2977.getValue());
		
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
		
		listaCodigoElementoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
						             urlBean.getUrlConteo().getUrl(), param,
						             true, GeneralParameterEnum.CODIGOELEMENTO.getName());
	}
    /**
     * 
     * Carga la lista listaCodigoElementoFinal
     *
     */
	public void cargarListaCodigoElementoFinal()
	{
		UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
	                            InformacionInmueblesControladorUrlEnum.URL3765.getValue());
		
		Map<String, Object> param = new TreeMap<>();
		param.put(GeneralParameterEnum.COMPANIA.getName(),
		            compania);
		
		param.put(InformacionInmueblesControladorEnum.CODIGO.getValue(),
		            elementoInicial);
		
		listaCodigoElementoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
		            urlBean.getUrlConteo().getUrl(), param,
		            true, GeneralParameterEnum.CODIGOELEMENTO.getName());
	
	}
    /**
     * 
     * Metodo ejecutado al oprimir el boton cmdPantalla
     * en la vista
     *
     *
     */
	public void oprimircmdPantalla() 
	{
		try
    	{
    		archivoDescarga = null;
    		String reporte = "002070IBIENESINMUEBLES";
    		Map<String, Object> parametros = new HashMap<>();
    		Map<String, Object> reemplazar = new HashMap<>();

    		reemplazar.put("compania", compania);
    		reemplazar.put("ano", ano);
    		reemplazar.put("mes", mes);
    		reemplazar.put("elementoInicial", elementoInicial);
    		reemplazar.put("elementoFinal", elementoFinal);
    		
    		String almacenista = ejbSysmanUtl.consultarParametro(compania,"ALMACENISTA", modulo, new Date(), false);
    		String nombreContador = ejbSysmanUtl.consultarParametro(compania,"NOMBRE CONTADOR",modulo, new Date(), false);
    		
    		parametros.put("PR_COMPANIA", SessionUtil.getCompaniaIngreso().getNombre());
    		parametros.put("PR_MES", ejbSysmanUtl.mostrarNombreDeMes(mes));
    		parametros.put("PR_ANO", ano);
    		parametros.put("PR_ALMACENISTA", almacenista);
    		parametros.put("PR_NOMBRE_CONTADOR", nombreContador);
    		
    		Reporteador.resuelveConsulta(reporte,
    				Integer.parseInt(SessionUtil.getModulo()),
    				reemplazar, parametros);

    		archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
    				ConectorPool.ESQUEMA_SYSMAN,
    				ReportesBean.FORMATOS.PDF);
    	}
    	catch (JRException | IOException | SysmanException | SystemException e)
    	{
    		logger.error(e.getMessage(), e);
    		JsfUtil.agregarMensajeError(e.getMessage());
    	}

    }
	
	public void oprimirExcel() 
	{
		try
    	{
    		archivoDescarga = null;
    		String reporte = "002070IBIENESINMUEBLES";
    		Map<String, Object> parametros = new HashMap<>();
    		Map<String, Object> reemplazar = new HashMap<>();

    		reemplazar.put("compania", compania);
    		reemplazar.put("ano", ano);
    		reemplazar.put("mes", mes);
    		reemplazar.put("elementoInicial", elementoInicial);
    		reemplazar.put("elementoFinal", elementoFinal);
    		
    		String almacenista = ejbSysmanUtl.consultarParametro(compania,"ALMACENISTA", modulo, new Date(), false);
    		String nombreContador = ejbSysmanUtl.consultarParametro(compania,"NOMBRE CONTADOR",modulo, new Date(), false);
    		
    		parametros.put("PR_COMPANIA", SessionUtil.getCompaniaIngreso().getNombre());
    		parametros.put("PR_MES", ejbSysmanUtl.mostrarNombreDeMes(mes));
    		parametros.put("PR_ANO", ano);
    		parametros.put("PR_ALMACENISTA", almacenista);
    		parametros.put("PR_NOMBRE_CONTADOR", nombreContador);
    		
    		Reporteador.resuelveConsulta(reporte,
    				Integer.parseInt(SessionUtil.getModulo()),
    				reemplazar, parametros);

    		archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
    				ConectorPool.ESQUEMA_SYSMAN,
    				ReportesBean.FORMATOS.EXCEL);
    	}
    	catch (JRException | IOException | SysmanException | SystemException e)
    	{
    		logger.error(e.getMessage(), e);
    		JsfUtil.agregarMensajeError(e.getMessage());
    	}

    }
    /**
     * Metodo ejecutado al cambiar el control Ano
     * 
     * 
     */
	public void cambiarAno() 
	{
		cargarListaMes();
    }
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoElementoInicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaCodigoElementoInicial(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
	    elementoInicial = SysmanFunciones
	                    .nvl(registroAux.getCampos()
	                                    .get(GeneralParameterEnum.CODIGOELEMENTO
	                                                    .getName()),
	                                    "")
	                    .toString();
	
	    nombreElementoInicial = SysmanFunciones.nvl(registroAux.getCampos()
	                    .get("NOMBRELARGO"), "").toString();
	
	    cargarListaCodigoElementoFinal();
	}
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoElementoFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
	public void seleccionarFilaCodigoElementoFinal(SelectEvent event) 
	{
		Registro registroAux = (Registro) event.getObject();
	    elementoFinal = SysmanFunciones
	                    .nvl(registroAux.getCampos()
	                                    .get(GeneralParameterEnum.CODIGOELEMENTO
	                                                    .getName()),
	                                    "")
	                    .toString();
	
	    nombreElementoFinal = SysmanFunciones.nvl(registroAux.getCampos()
	                    .get("NOMBRELARGO"), "").toString();
	}
    /**
     * Retorna la variable elementoInicial
     * 
     * @return  elementoInicial
     */
	public String getElementoInicial() 
	{
        return elementoInicial;
    }
    /**
     * Asigna la variable  elementoInicial
     * 
     * @param  elementoInicial
     * Variable a asignar en  elementoInicial
     */
    public void setElementoInicial(String elementoInicial) 
    {
        this.elementoInicial = elementoInicial;
    }
    /**
     * Retorna la variable elementoFinal
     * 
     * @return  elementoFinal
     */
    public String getElementoFinal() 
    {
        return elementoFinal;
    }
    /**
     * Asigna la variable  elementoFinal
     * 
     * @param  elementoFinal
     * Variable a asignar en  elementoFinal
     */
    public void setElementoFinal(String elementoFinal) 
    {
        this.elementoFinal = elementoFinal;
    }
    /**
     * Retorna la variable ano
     * 
     * @return  ano
     */
    public String getAno() 
    {
        return ano;
    }
    /**
     * Asigna la variable  ano
     * 
     * @param  ano
     * Variable a asignar en  ano
     */
    public void setAno(String ano) 
    {
        this.ano = ano;
    }
    /**
     * Retorna la variable mes
     * 
     * @return  mes
     */
    public int getMes() 
    {
        return mes;
    }
    /**
     * Asigna la variable  mes
     * 
     * @param  mes
     * Variable a asignar en  mes
     */
    public void setMes(int mes) 
    {
        this.mes = mes;
    }
    /**
     * Retorna la variable nombreElementoInicial
     * 
     * @return  nombreElementoInicial
     */
    public String getNombreElementoInicial() 
    {
        return nombreElementoInicial;
    }
    /**
     * Asigna la variable  nombreElementoInicial
     * 
     * @param  nombreElementoInicial
     * Variable a asignar en  nombreElementoInicial
     */
    public void setNombreElementoInicial(String nombreElementoInicial) 
    {
        this.nombreElementoInicial = nombreElementoInicial;
    }
    /**
     * Retorna la variable nombreElementoFinal
     * 
     * @return  nombreElementoFinal
     */
    public String getNombreElementoFinal() 
    {
        return nombreElementoFinal;
    }
    /**
     * Asigna la variable  nombreElementoFinal
     * 
     * @param  nombreElementoFinal
     * Variable a asignar en  nombreElementoFinal
     */
    public void setNombreElementoFinal(String nombreElementoFinal)
    {
        this.nombreElementoFinal = nombreElementoFinal;
    }
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() 
    {
        return archivoDescarga;
    }
    /**
     * Retorna la lista listaAno
     * 
     * @return listaAno
     */
    public List<Registro> getListaAno() 
    {
        return listaAno;
    }
    /**
     * Asigna la lista listaAno
     * 
     * @param listaAno
     * Variable a asignar en  listaAno
     */
    public void setListaAno(List<Registro> listaAno) 
    {
        this.listaAno = listaAno;
    }
    /**
     * Retorna la lista listaMes
     * 
     * @return listaMes
     */
    public List<Registro> getListaMes() 
    {
        return listaMes;
    }
    /**
     * Asigna la lista listaMes
     * 
     * @param listaMes
     * Variable a asignar en  listaMes
     */
    public void setListaMes(List<Registro> listaMes) 
    {
        this.listaMes = listaMes;
    }
    /**
     * Retorna la lista listaCodigoElementoInicial
     * 
     * @return listaCodigoElementoInicial
     */
    public RegistroDataModelImpl getListaCodigoElementoInicial() 
    {
        return listaCodigoElementoInicial;
    }
    /**
     * Asigna la lista listaCodigoElementoInicial
     * 
     * @param listaCodigoElementoInicial
     * Variable a asignar en  listaCodigoElementoInicial
     */
    public void setListaCodigoElementoInicial(RegistroDataModelImpl listaCodigoElementoInicial) 
    {
        this.listaCodigoElementoInicial = listaCodigoElementoInicial;
    }
    /**
     * Retorna la lista listaCodigoElementoFinal
     * 
     * @return listaCodigoElementoFinal
     */
    public RegistroDataModelImpl getListaCodigoElementoFinal() 
    {
        return listaCodigoElementoFinal;
    }
    /**
     * Asigna la lista listaCodigoElementoFinal
     * 
     * @param listaCodigoElementoFinal
     * Variable a asignar en  listaCodigoElementoFinal
     */
    public void setListaCodigoElementoFinal(RegistroDataModelImpl listaCodigoElementoFinal) 
    {
        this.listaCodigoElementoFinal = listaCodigoElementoFinal;
    }
}

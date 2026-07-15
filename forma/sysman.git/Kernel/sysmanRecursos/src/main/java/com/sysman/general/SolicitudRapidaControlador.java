/*-
 * SolicitudRapidaControlador.java
 *
 * 1.0
 * 
 * 20/09/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.general;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.SolicitudRapidaControladorUrlEnum;
import com.sysman.general.enums.SolicitudRapidaControladorEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.RequestManager;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaCeroGeneralRemote;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanException;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.enums.ConstanteArchivo;
import com.sysman.util.rest.APIAutoServicio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * Permite generr reporte de Solicitud r�pida
 *
 * @version 1.0, 20/09/2018
 * @author asana
 */
@ManagedBean
@ViewScoped
public class SolicitudRapidaControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    private String claseSolicitud;
    private String anio;
    private String mes;
    private String periodo;
    private boolean verPeriodo = false;
    private String claseSolicitudNombre = "";
    private boolean verAnio = true;
    private boolean verMes = true ;
    
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    @EJB
    private EjbNominaCeroGeneralRemote ejbNominaCero;

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    /**
     * Constante que identifica el servicio que busca la URL y tipo de
     * conexi�n
     */
    private static final String SERVICIO_API = "1710001";

    /**
     * Constante que identifica el proceso el cual se envia en el
     * llamado de los autoservicios
     */
    private static final int PROCESO = 1;

    /**
     * ruta del servicio con el cual se consumen los servicios de
     * SysmanAPI
     */
    private static final String URL_AUTO = "/sysmanApi/autoservicio/v1/consultaspropia";

    /**
     * Codigo del servicio que se debe consumir para acceder al
     * autoservicio con firmas y en pdf
     */
    private static final String CODIGOSERVICIO = "98";

    /**
     * Identifica el periodo por defecto que se envia cuando en el
     * controlador no se pide el mismo
     */
    private String PERIODO = "3"; //JM MOD CC 2418

    /**
     * constante para que se guarde el nombre del parametro que indica
     * por donde se va el servicio
     */
    private static final String PAR_URL_AUTOSERVICIO = "AUTOSERVICIO CON URL EXTERNA";
    private static final String PAR_URL_AUTOSERVICIO2 = "AUTOSERVICIO URL DOMINIO";
    private List<Registro> listaAno;
    private List<Registro> listaMes;
    private List<Registro> listaPeriodo;
    private RegistroDataModelImpl listaClaseSolicitud;

    /**
     * Crea una nueva instancia de SolicitudRapidaControlador
     */
    public SolicitudRapidaControlador() {
        super();
        compania = SessionUtil.getCompania();       
        try {
            numFormulario = GeneralCodigoFormaEnum.SOLICITUDRAPIDA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
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
    public void inicializar() {
        cargarListaAno();
        cargarListaMes();
        cargarListaPeriodo();
        cargarListaClaseSolicitud();
        abrirFormulario();
        
        if ("NO".equals(getParametro("NOMINA MENSUAL", true, new Date()))) { //JM CC 2418
          	 PERIODO = "1";
           }
        
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Carga la lista listaAno
     */
    public void cargarListaAno() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), PROCESO);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SolicitudRapidaControladorUrlEnum.URL4735
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 
     * Carga la lista listaMes
     */
    public void cargarListaMes() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), PROCESO);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        try {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SolicitudRapidaControladorUrlEnum.URL5723
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * 
     * Carga la lista listaPeriodo
     */
    public void cargarListaPeriodo() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), PROCESO);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.MES.getName(), mes);

        try {
            listaPeriodo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SolicitudRapidaControladorUrlEnum.URL7274
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * 
     * Carga la lista listaClaseSolicitud
     */
    public void cargarListaClaseSolicitud() {

        UrlBean urlBean;

        urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SolicitudRapidaControladorUrlEnum.URL5724
                                                        .getValue());

        listaClaseSolicitud = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), null,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton BT26 en la vista
     *
     */
    public void oprimirImprimir() {
        FacesContext faceContext = FacesContext.getCurrentInstance();
        try {

            servicioAutoServicio();
        }
        catch (SystemException e) {
            manejaError(e.getMessage(), faceContext);
        }
        catch (MalformedURLException e) {
            manejaError(idioma.getString(
                            SolicitudRapidaControladorEnum.TB4226.getValue()),
                            faceContext);
        }
        catch (IOException e) {
            manejaError(idioma.getString(
                            SolicitudRapidaControladorEnum.TB4227.getValue()),
                            faceContext);
        }
        catch (SysmanException e) {
            manejaError(e.getMessage(), faceContext);
        }
        catch (NullPointerException e) {
            manejaError(idioma.getString(
                            SolicitudRapidaControladorEnum.TB4227.getValue()),
                            faceContext);
        }
    }

    private void manejaError(String mensaje, FacesContext faceContext) {
        FacesMessage facesMessage = new FacesMessage(
                        FacesMessage.SEVERITY_ERROR, mensaje, null);
        faceContext.addMessage(null, facesMessage);
        logger.error(mensaje);
        // return paginaRetorno;
    }

    /**
     * Permite resolver la conecci�n a directorio Activo o ldap
     * 
     * @author jgomez
     * 
     * @param compania
     * @param password
     * @return
     * @throws SysmanException
     * @throws NullPointerException
     * @throws SystemException
     * @throws MalformedURLException
     * @throws IOException
     */
    private void servicioAutoServicio()
                    throws SysmanException, NullPointerException,
                    SystemException, MalformedURLException, IOException {
        Boolean propio;
        propio = !("SI".equals(getParametro(PAR_URL_AUTOSERVICIO, true, new Date())));
        String url = armarUrl(propio);

        String entidad = SessionUtil.getCompaniaIngreso().getNit();

        entidad = entidad.replace(".", "");
        if (entidad.indexOf("-") > 0) {
            entidad = entidad.substring(0, entidad.indexOf("-"));
        }

        String cedula = SessionUtil.getUser().getCedula();
        String observacion = idioma.getString(
                        SolicitudRapidaControladorEnum.TB4229.getValue());
        if (cedula == null) {
            throw new SysmanException(idioma.getString(
                            SolicitudRapidaControladorEnum.TB4228.getValue()));
        }

        archivoDescarga = null;
        ByteArrayInputStream respuesta;
        boolean periodoActivo = false;
        
        if ("11".equals(claseSolicitud))  {

        periodoActivo = ejbNominaCero.validarPeriodoActivoNomina(compania,
                        Integer.parseInt("1"), Integer.parseInt(anio),
                        Integer.parseInt(mes), Integer.parseInt(periodo));
        
        }
        
        if (periodoActivo)  {
            
            JsfUtil.agregarMensajeAlerta("El periodo debe estar cerrado para generar el volante de pago");
        }
        
        else {
            
            if ("10".equals(claseSolicitud))  {

                Date sysdate = new Date();
                anio = String.valueOf(SysmanFunciones.ano(sysdate));
                mes = String.valueOf(SysmanFunciones.mes(SysmanFunciones.sumarRestarMesesFecha(sysdate, -1)));
	            	
                	if("12".equals(mes)) { // 7740551 
	            		anio = ""+(Integer.parseInt(anio) - 1);
	            	} 
                }
            
            APIAutoServicio api = new APIAutoServicio();
            try {
                if (propio) {
                    respuesta = api.autoServicioPropio(compania, entidad,
                                    Integer.parseInt(claseSolicitud),
                                    cedula, PROCESO,
                                    Integer.parseInt(anio),
                                    Integer.parseInt(mes),
                                    Integer.parseInt(periodo),
                                    observacion, url);
                }
                else {
                    respuesta = api.autoServicio(compania, entidad,
                                    Integer.parseInt(claseSolicitud),
                                    cedula, PROCESO,
                                    Integer.parseInt(anio),
                                    Integer.parseInt(mes),
                                    Integer.parseInt(periodo),
                                    observacion, url);
                }
    
                archivoDescarga = new DefaultStreamedContent(respuesta,
                                ConstanteArchivo.PDF.getContentType(),
                                claseSolicitudNombre + "-" + cedula
                                    + ConstanteArchivo.PDF.getExtension());
    
            }
            catch (IOException | SysmanException
                            | RuntimeException e) {
                JsfUtil.agregarMensajeError(e.getMessage());
            }
            
        }

    }

    /**
     * Armar la Url del servicio para autoservicio con base en el
     * contexto actual
     * 
     * @throws SysmanException
     * 
     */
    private String armarUrl(Boolean propio) throws SysmanException {

        String url = "";

        if (propio) {
            HttpServletRequest datoLocal = ((HttpServletRequest) FacesContext
                            .getCurrentInstance().getExternalContext()
                            .getRequest());
            url = datoLocal.getRequestURL().toString();
            
            String contexto = SysmanFunciones.concatenar(
                            new String[] { datoLocal.getContextPath(),
                                           datoLocal.getServletPath() });
            /*url= "https://fnd.sysman.com.co"+contexto; */
            url= getParametro(PAR_URL_AUTOSERVICIO2, false, new Date())+contexto;
            url = url.replace(contexto, URL_AUTO);
        }
        else {

            Map<String, Object> parametros = new TreeMap<>();
            parametros.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);
            parametros.put(GeneralParameterEnum.CODIGO.getName(),
                            CODIGOSERVICIO);
            Registro rs = new Registro();
            RequestManager requestManager = new RequestManager();
            try {
                rs = RegistroConverter.toRegistro(requestManager.get(
                                UrlServiceUtil.getUrlBeanById(SERVICIO_API)
                                                .getUrl(),
                                parametros));
            }
            catch (NullPointerException | SystemException e) {
                throw new SysmanException(idioma
                                .getString(SolicitudRapidaControladorEnum.TB4230
                                                .getValue()));
            }
            if (rs == null) {
                throw new SysmanException(idioma
                                .getString(SolicitudRapidaControladorEnum.TB4232
                                                .getValue()));
            }
            else if (rs.getCampos().get(GeneralParameterEnum.URL.getName())
            		.toString() == null) {
                throw new SysmanException(idioma
                                .getString(SolicitudRapidaControladorEnum.TB4231
                                                .getValue()));
            }
            url = rs.getCampos().get(GeneralParameterEnum.URL.getName())
                            .toString();
        }
        return url;
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaClaseSolicitud
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaClaseSolicitud(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        claseSolicitud = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
        verPeriodo = (boolean) registroAux.getCampos()
                        .get(SolicitudRapidaControladorEnum.REQUIERE_PERIODO
                                        .getValue());

        claseSolicitudNombre = SysmanFunciones.nvl(
                        registroAux.getCampos()
                                        .get(GeneralParameterEnum.NOMBRE
                                                        .getName()),

                        "").toString();
        
        if (claseSolicitud.equals("10")){
        	verAnio = false;
        	verMes = false;
        }
        if ((!verPeriodo) && periodo == null) {
            periodo = PERIODO;
        }
    }

    private String getParametro(String nombre, boolean indMayus, Date date) {
        try {
            
        	String modulo = "87"; //MOD JM CC 2418
        	if("NOMINA MENSUAL".equalsIgnoreCase(nombre)){
        		modulo = "6";
        	}
        	return ejbSysmanUtil.consultarParametro(compania, nombre,
        			modulo, date, indMayus);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return "";
    }

    public void cambiarAno() {
        cargarListaMes();
    }

    public void cambiarMes() {
        cargarListaPeriodo();
    }

    /**
     * Retorna la variable periodo
     * 
     * @return periodo
     */
    public String getPeriodo() {
        return periodo;
    }

    /**
     * Asigna la variable periodo
     * 
     * @param periodo
     * Variable a asignar en periodo
     */
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

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
     * Variable a asignar en listaAno
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
     * Variable a asignar en listaMes
     */
    public void setListaMes(List<Registro> listaMes) {
        this.listaMes = listaMes;
    }

    /**
     * Retorna la lista listaPeriodo
     * 
     * @return listaPeriodo
     */
    public List<Registro> getListaPeriodo() {
        return listaPeriodo;
    }

    /**
     * Asigna la lista listaPeriodo
     * 
     * @param listaPeriodo
     * Variable a asignar en listaPeriodo
     */
    public void setListaPeriodo(List<Registro> listaPeriodo) {
        this.listaPeriodo = listaPeriodo;
    }

    /**
     * Retorna la lista listaClaseSolicitud
     * 
     * @return listaClaseSolicitud
     */
    public RegistroDataModelImpl getListaClaseSolicitud() {
        return listaClaseSolicitud;
    }

    /**
     * Asigna la lista listaClaseSolicitud
     * 
     * @param listaClaseSolicitud
     * Variable a asignar en listaClaseSolicitud
     */
    public void setListaClaseSolicitud(
        RegistroDataModelImpl listaClaseSolicitud) {
        this.listaClaseSolicitud = listaClaseSolicitud;
    }

    public String getClaseSolicitud() {
        return claseSolicitud;
    }

    public void setClaseSolicitud(String claseSolicitud) {
        this.claseSolicitud = claseSolicitud;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public boolean isVerPeriodo() {
        return verPeriodo;
    }

    public void setVerPeriodo(boolean verPeriodo) {
        this.verPeriodo = verPeriodo;
    }

    public String getClaseSolicitudNombre() {
        return claseSolicitudNombre;
    }

    public void setClaseSolicitudNombre(String claseSolicitudNombre) {
        this.claseSolicitudNombre = claseSolicitudNombre;
    }

	public boolean isVerAnio() {
		return verAnio;
	}

	public void setVerAnio(boolean verAnio) {
		this.verAnio = verAnio;
	}

	public boolean isVerMes() {
		return verMes;
	}

	public void setVerMes(boolean verMes) {
		this.verMes = verMes;
	}



}

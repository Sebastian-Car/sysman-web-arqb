package com.sysman.almacen;

import static com.sysman.util.SysmanFunciones.nvl;

import com.sysman.almacen.ejb.EjbAlmacenCeroRemote;
import com.sysman.almacen.ejb.EjbAlmacenCincoRemote;
import com.sysman.almacen.ejb.EjbAlmacenCuatroRemote;
import com.sysman.almacen.ejb.EjbAlmacenTresRemote;
import com.sysman.almacen.enums.ParametrosAlmacenControladorEnum;
import com.sysman.almacen.enums.ParametrosAlmacenControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
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
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dsuesca
 * @version 1, 04/04/2016
 *
 * @author eamaya
 * @version 2, 04/05/2017 Proceso de Refactoring, Manejo de EJBs y Correcciones SonarLint
 *
 * @author ybecerra
 * @version 3, 13/06/2017 Implementacion al llamado de GeneralCodigoFormaEnum, para el codigo del formulario
 */
@ManagedBean
@ViewScoped
public class ParametrosAlmacenControlador extends BeanBaseModal
{

    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del usuario que inicio sesion en el modulo.
     */
    private final String usuario = SessionUtil.getUser().getCodigo();

    private final String codigoMenuCons;
    private final String codigoElementoCons;
    private final String hiCons;
    private final String mensajeAlertCons;
    private final String mensajeCons;
    private final String consDepreciaciones;

    private String verificarDependencia;
    private String revisarDepAnteriores;
    private String comboCompania;
    private String mesInicial;
    private String mesFinal;
    private String elementoInicial;
    private String elementoFinal;
    private String identificacion;
    private String anioInicial;
    private String placaInicial;
    private String placaFinal;
    private String anioFinal;
    private String nombreProceso;
    private List<Registro> listaCompania;
    private RegistroDataModelImpl listaElementoinicial;
    private RegistroDataModelImpl listaElementoFinal;

    private boolean visibleIdentificacion;
    private boolean visibleAdvertencia;
    private boolean visibleVerificarDependencia;
    private boolean visibleBtnInicialNIIF;
    private boolean visibleRevisarDepAnteriores;
    private boolean visibleRangoPlaca;
    private boolean ejecutarProcesosUtilitarioAlmacenSinControlDePeriodo;
    private String fechaDeCorteParaInicioDelAlmacen;
    private boolean verificarMovimientosDeCorreccionDeValor;
    private boolean manejaNiifEnAlmacen;
    private String salidaTexto;
    private boolean visibleConfirmacion;
    private StreamedContent archivoDescarga;
    private String hi;
    private int anio;
    private int mes;
    private boolean bloqueoMesFinal;
    private String ejeColgaapNiif;
    
    private boolean bloqueaConsulta;
    
    private String opcion;
    private Object idPR;
    

  

	@EJB
    private EjbSysmanUtilRemote ejbParametro;

    @EJB
    private EjbAlmacenTresRemote ejbAlmacenTres;

    @EJB
    private EjbAlmacenCuatroRemote ejbAlmacenCuatro;

    @EJB
    private EjbAlmacenCincoRemote ejbAlmacenCinco;

    @EJB
    private EjbAlmacenCeroRemote ejbAlmacenCero;

    /**
     * Creates a new instance of ParametrosAlmacenControlador
     */
    public ParametrosAlmacenControlador()
    {
        super();
        codigoElementoCons = GeneralParameterEnum.CODIGOELEMENTO.getName();
        hiCons = "#$hi#$";
        mensajeCons = "MSM_FALTAN_DATOS_PROCESO";
        consDepreciaciones = "10060305";
        compania = SessionUtil.getCompania();
        codigoMenuCons = "10060306";
        mensajeAlertCons = "TB_TB2005";
        visibleBtnInicialNIIF = true;

        try
        {
            numFormulario = GeneralCodigoFormaEnum.PARAMETROS_ALMACEN_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(ParametrosAlmacenControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

            SessionUtil.redireccionarMenuPermisos();
        }
    }

    public void initParametros()
    {
        try
        {
            String valorParametro = nvl(ejbParametro.consultarParametro(
                            compania,
                            "EJECUTAR PROCESOS UTILITARIO ALMACEN SIN CONTROL DE PERIODO",
                            SessionUtil.getModulo(), new Date(),
                            false), "NO").toString();
            ejecutarProcesosUtilitarioAlmacenSinControlDePeriodo = "SI"
                            .equalsIgnoreCase(valorParametro);

            valorParametro = nvl(ejbParametro.consultarParametro(compania,
                            "FECHA DE CORTE PARA INICIO DEL ALMACEN",
                            SessionUtil.getModulo(), new Date(), false), "")
                                            .toString();
            fechaDeCorteParaInicioDelAlmacen = valorParametro;

            valorParametro = nvl(ejbParametro.consultarParametro(compania,
                            "VERIFICAR MOVIMIENTOS DE CORRECCION DE VALOR",
                            SessionUtil.getModulo(), new Date(), false), "N")
                                            .toString();
            setVerificarMovimientosDeCorreccionDeValor(
                            "SI".equals(valorParametro));

            valorParametro = nvl(ejbParametro.consultarParametro(compania,
                            "MANEJA NIIF EN ALMACEN",
                            SessionUtil.getModulo(), new Date(), false), "NO")
                                            .toString();
            setManejaNiifEnAlmacen("SI".equals(valorParametro)
                && Arrays.asList("10060304", "10060305")
                                .contains(SessionUtil.getMenuActual()) 
                && "NO".equals(ejeColgaapNiif));

        }
        catch (SystemException ex)
        {
            Logger.getLogger(ParametrosAlmacenControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    public void initValoresInciales()
    {

        Date fechaCorte;
        try
        {
            fechaCorte = SysmanFunciones.convertirAFecha(
                            fechaDeCorteParaInicioDelAlmacen, "dd/MM/yyyy");

            anioInicial = String.valueOf(SysmanFunciones.ano(new Date()));
            mesInicial = String.valueOf(SysmanFunciones.mes(new Date()));
        }
        catch (ParseException e)
        {
            anioInicial = "1998";
            mesInicial = "12";
        }

        comboCompania = compania;

        anioFinal = String.valueOf(SysmanFunciones.ano(new Date()));

        mesFinal = String.valueOf(SysmanFunciones.mes(new Date()));

        placaInicial = "1";
        placaFinal = "99999999999";
        setVisibleRangoPlaca(true);
        bloqueaConsulta = false;

        if (codigoMenuCons.equals(SessionUtil.getMenuActual()))
        {
            visibleIdentificacion = false;
            visibleAdvertencia = false;
            visibleVerificarDependencia = false;
            verificarDependencia = "-1";
        }
        if ("10060304".equals(SessionUtil.getMenuActual()) || "10060305".equals(SessionUtil.getMenuActual()))
        {
        	if ("SI".equals(ejeColgaapNiif))
        	{
        		visibleBtnInicialNIIF = false;
        	}
        	else
        	{
        		visibleBtnInicialNIIF = true;
        	}
        	
        	if("10060305".equals(SessionUtil.getMenuActual())) {
        		bloqueoMesFinal = true;
        	}	
        	else {
        		bloqueoMesFinal = false;
        	
        	}
        	
        }
        else
        {
            visibleBtnInicialNIIF = false;
            bloqueoMesFinal = true;
        }

        if ("10060320".equals(SessionUtil.getMenuActual()))
        {
            setVisibleRangoPlaca(false);
        }
    }

    @PostConstruct
    public void inicializar()
    {
    	try 
        {
			ejeColgaapNiif = nvl(ejbParametro.consultarParametro(compania,
							"EJECUTA COLGAAP y NIIF", SessionUtil.getModulo(), 
							new Date(), false), "NO").toString();
		} 
        catch (SystemException e) 
        {
			e.printStackTrace();
		}
    	
        cargarListaCompania();

        abrirFormulario();
        cargarListaElementoinicial();
    }

    @Override
    public void abrirFormulario()
    {
        initParametros();
        initValoresInciales();
        switch (SessionUtil.getMenuActual())
        {
        case "10060302":
        	nombreProceso = "KARDEAR";
            opcion = "kardear";
            consultarEstado(opcion,nombreProceso);
            break;
        case "10060306":
            nombreProceso = "REVISAR PLACAS";
            opcion = "revisar placas";
            consultarEstado(opcion,nombreProceso);
            break;
        case "10060304":
            nombreProceso = "CALCULAR DEPRECIACIONES INICIALES";
            opcion = "calcular depreciaciones iniciales";
            consultarEstado(opcion,nombreProceso);
            break;
        case "10060305":
        	nombreProceso = "CALCULAR DEPRECIACIONES";
        	opcion = "calcular depreciaciones";
            consultarEstado(opcion,nombreProceso);
            break;
        case "10060320":
            nombreProceso = "ACTUALIZAR SALDO PEPS";
            opcion = "Actualizar saldo peps";
            consultarEstado(opcion,nombreProceso);
            break;
        default:
            break;
        }

        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaCompania()
    {
        try
        {
            Map<String, Object> parametros = new TreeMap<>();
            parametros.put("USUARIO", usuario);
            
            listaCompania = RegistroConverter
                            .toListRegistro(
                                            requestManager.getList(
                                                            UrlServiceUtil.getInstance()
                                                                            .getUrlServiceByUrlByEnumID(
                                                                                            ParametrosAlmacenControladorUrlEnum.URL59030
                                                                                                            .getValue())
                                                                            .getUrl(),
                                                            parametros)); 
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    
    public void cargarListaElementoinicial()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ParametrosAlmacenControladorUrlEnum.URL7979
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), comboCompania);

        listaElementoinicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        codigoElementoCons);
    }

    public void cargarListaElementoFinal()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ParametrosAlmacenControladorUrlEnum.URL8736
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), comboCompania);

        param.put(ParametrosAlmacenControladorEnum.PARAM0.getValue(),
                        elementoInicial);

        listaElementoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        codigoElementoCons);
    }
    
    public void cambiaranoInicial() {
        if ("10060305".equals(SessionUtil.getMenuActual())) {
        	anioFinal = anioInicial;
        	
        }
    }
        public void cambiarMesInicial() {
        if ("10060305".equals(SessionUtil.getMenuActual())) {
        	mesFinal = mesInicial;
        	 //this.campoCP18257 = anioFinal;
        	
        }
    }

    public void oprimirCancelar()
    {
        // <CODIGO_DESARROLLADO>
        JsfUtil.ejecutarJavaScript("cerrarModalDefault()");
        // </CODIGO_DESARROLLADO>
    }

    private boolean validarCampos()
    {
        boolean respuesta = true;

        if (SysmanFunciones.validarVariableVacio(comboCompania)
            || SysmanFunciones.validarVariableVacio(mesInicial)
            || SysmanFunciones.validarVariableVacio(mesFinal))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString(mensajeCons));
            respuesta = false;
        }

        if (SysmanFunciones.validarVariableVacio(elementoInicial)
            || SysmanFunciones.validarVariableVacio(elementoFinal)
            || SysmanFunciones.validarVariableVacio(anioInicial)
            || SysmanFunciones.validarVariableVacio(anioFinal))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString(mensajeCons));
            respuesta = false;
        }

        if (SysmanFunciones.validarVariableVacio(placaInicial)
            || SysmanFunciones.validarVariableVacio(placaFinal))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString(mensajeCons));
            respuesta = false;

        }

        return respuesta;
    }

    private boolean revisarPlacas()
    {
    	
    	boolean respuesta = true;
        try
        {
	        if(!consultarEstado(opcion,nombreProceso)) {
	    		
	        	
	            if ("10060306".equals(SessionUtil.getMenuActual()))
	            {
	            	agregarControl(nombreProceso);
	            	Map<String, Object> param = new HashMap<>();
            		param.put(GeneralParameterEnum.ESTADO.getName(), "INICIADO");
            		param.put(GeneralParameterEnum.COMPANIA.getName(), nvl(comboCompania,compania));
            		param.put(GeneralParameterEnum.NOM_PROCESO.getName(), nombreProceso);

            		Registro rs = RegistroConverter
            				.toRegistro(requestManager.get(
            						UrlServiceUtil.getInstance()
            						.getUrlServiceByUrlByEnumID(
            								ParametrosAlmacenControladorUrlEnum.URL1984001.getValue())
            						.getUrl(),
            						param));
            		idPR = rs.getCampos().get("ID_PROCESO");
	
	                if (SessionUtil.getMenuActual().equals(codigoMenuCons)
	                    && !placaInicial.equals(placaFinal))
	                {
	
	                    ejbAlmacenTres.revisarHoraH(comboCompania);
	
	                    ejbAlmacenTres.revisarHoras(comboCompania, elementoInicial,
	                                    elementoFinal, Long.parseLong(placaInicial),
	                                    Long.parseLong(placaFinal));
	
	                }
	                if (fechaDeCorteParaInicioDelAlmacen.isEmpty())
	                {        		
	                		              		
	                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2004"));
	                    respuesta = false;
	
	                }
	                else
	                {
	                    anio = Integer.parseInt(anioInicial);
	                    mes = Integer.parseInt(mesInicial);
	                    Date fechaDeCorte = SysmanFunciones.convertirAFecha(
	                                    fechaDeCorteParaInicioDelAlmacen);
	                    int anioDeCorte = SysmanFunciones.ano(fechaDeCorte);
	                    int mesDeCorte = SysmanFunciones.mes(fechaDeCorte);
	
	                    if (anio * 12 + mes < anioDeCorte * 12 + mesDeCorte)
	                    {
	                    	
	              		
	                        JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2002")
	                                        .replace("#$fechaDeCorteParaInicioDelAlmacen#$",
	                                                        fechaDeCorteParaInicioDelAlmacen));
	                        respuesta = false;
	                    }
	                    else
	                    {
	
	                        salidaTexto = ejbAlmacenTres.revisarDevolutivos(
	                                        comboCompania,
	                                        Integer.parseInt(anioInicial),
	                                        Integer.parseInt(mesInicial),
	                                        elementoInicial,
	                                        Long.parseLong(placaInicial),
	                                        Integer.parseInt(anioFinal),
	                                        Integer.parseInt(mesFinal), elementoFinal,
	                                        Long.parseLong(placaFinal), usuario);
	
	                        inconsistenciasDepreciaciones(salidaTexto);
	
	                        ejbAlmacenTres.actualizarDependencia(comboCompania,
	                                        elementoInicial, elementoFinal,
	                                        Long.parseLong(placaInicial),
	                                        Long.parseLong(placaFinal),
	                                        Integer.parseInt(mesFinal),
	                                        Integer.parseInt(anioFinal));
	
	                        ejbAlmacenTres.rectificarDevolutivos(comboCompania,
	                                        Long.parseLong(placaInicial),
	                                        Long.parseLong(placaFinal));
	
	                    }
	                }
	            }
	        }
	        else {
	        	 respuesta = false;
	        }
	        	
        }
        catch (ParseException | SystemException e)
        {
        	respuesta = false;
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }finally {
        	if ("10060306".equals(SessionUtil.getMenuActual())) {
	        	UrlBean updateEstado = UrlServiceUtil.getInstance()
	                    .getUrlServiceByUrlByEnumID(
	                    		ParametrosAlmacenControladorUrlEnum.URL1984002
	                                                    .getValue());
	    	
	        	 Map<String, Object> parametros = new HashMap<>();
	        	 parametros.put(GeneralParameterEnum.COMPANIA.getName(), nvl(comboCompania,compania));
	        	 parametros.put(GeneralParameterEnum.NOM_PROCESO.getName(), nombreProceso);
	        	 parametros.put(GeneralParameterEnum.MODIFIED_BY.getName(),
	                    SessionUtil.getUser().getCodigo());
	        	 parametros.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
	                    new Date());
	        	 parametros.put(GeneralParameterEnum.NO_PROCESO.getName(), idPR);
	        	 parametros.put(GeneralParameterEnum.FECHAFINAL.getName(),new Date());
	        	 
	        	if(respuesta) {
	        		 parametros.put(GeneralParameterEnum.ESTADO.getName(), "FINALIZADO");
	            	 
	           		Parameter parameter = new Parameter();
	           		parameter.setFields(parametros);
	           		try {
	 					requestManager.update(updateEstado.getUrl(),
	 					        updateEstado.getMetodo(),
	 					        parameter);
	 				} catch (SystemException e) {
	 					logger.error(e.getMessage(), e);
	 				}
	        		
	        	}else {
	        		
	            	 parametros.put(GeneralParameterEnum.ESTADO.getName(), "ERRORES");
	            	 
	          		Parameter parameter = new Parameter();
	          		parameter.setFields(parametros);
	          		try {
						requestManager.update(updateEstado.getUrl(),
						        updateEstado.getMetodo(),
						        parameter);
					} catch (SystemException e) {
						logger.error(e.getMessage(), e);
					}
	        	}
	        }
        }	
        return respuesta;

    }

    private void inconsistenciasDepreciaciones(String salidaTexto2)
    {
        if (!"TRUETRUE".equals(salidaTexto2))
        {
            generarArchivo(salidaTexto, "InconsistenciasDepreciaciones");
        }

    }

    private void menuKardear()
    {

        if ("10060302".equals(SessionUtil.getMenuActual()))
        {
        	if(consultarEstado(opcion,nombreProceso)) {
        		return;
        	}
        	agregarControl(nombreProceso);
            boolean revisaHoraAlrealizarKardeo;
            try
            {
                revisaHoraAlrealizarKardeo = "SI".equalsIgnoreCase(
                                nvl(ejbParametro.consultarParametro(compania,
                                                "REVISA HORA AL REALIZAR KARDEO",
                                                SessionUtil.getModulo(),
                                                new Date(), false), "NO")
                                                                .toString());

                if (revisaHoraAlrealizarKardeo)
                {

                    ejbAlmacenTres.revisarHoras(comboCompania, elementoInicial,
                                    elementoFinal,
                                    Long.parseLong(placaInicial),
                                    Long.parseLong(placaFinal));

                }

                salidaTexto = ejbAlmacenTres.obtenerInconsistenciasKardex(
                                comboCompania, Integer.parseInt(anioInicial),
                                Integer.parseInt(mesInicial),
                                Integer.parseInt(anioFinal),
                                Integer.parseInt(mesFinal),
                                elementoInicial, elementoFinal,
                                Integer.parseInt("0"),
                                new Date(),
                                new Date(),
                                "",
                                0);

                generarArchivo(salidaTexto, "InconsistenciasdeKardex");

            }

            catch (SystemException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }

    }

    private void generarArchivo(String salidaTexto2, String archivoNom)
    {

        archivoDescarga = null;
        try
        {
            if (salidaTexto2 != null)
            {
                ByteArrayInputStream archivo = JsfUtil
                                .serializarPlano(salidaTexto2);
                archivoDescarga = JsfUtil.getArchivoDescarga(archivo,
                                archivoNom + ".txt");
            }
        }
        catch (IOException | JRException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private boolean menuCalcularDepreciacionesIniciales()
    {
        boolean rta = true;
        if ("10060304".equals(SessionUtil.getMenuActual()))
        {
            try
            {
                salidaTexto = ejbAlmacenCuatro.calcularDepreciacionInicial(
                                comboCompania, elementoInicial,
                                elementoFinal, Long.parseLong(placaInicial),
                                Long.parseLong(placaFinal));

                if (!"TRUE".equals(salidaTexto))
                {
                    generarArchivo(salidaTexto,
                                    "InconsistenciasDepreciacionesIni");
                    rta = false;
                }

            }
            catch (NumberFormatException | SystemException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        return rta;
    }

    private String menuActualizarSaldoPeps() {
        String rta = "";
        boolean error = false;

        try {
            if ("10060320".equals(SessionUtil.getMenuActual())) {
            	
            	if(consultarEstado(opcion,nombreProceso)) {
            		return rta;
            	}
            	
            	agregarControl(nombreProceso);
            	Map<String, Object> param = new HashMap<>();
        		param.put(GeneralParameterEnum.ESTADO.getName(), "INICIADO");
        		param.put(GeneralParameterEnum.COMPANIA.getName(), nvl(comboCompania,compania));
        		param.put(GeneralParameterEnum.NOM_PROCESO.getName(), nombreProceso);

        		Registro rs = RegistroConverter
        				.toRegistro(requestManager.get(
        						UrlServiceUtil.getInstance()
        						.getUrlServiceByUrlByEnumID(
        								ParametrosAlmacenControladorUrlEnum.URL1984001.getValue())
        						.getUrl(),
        						param));
        		idPR = rs.getCampos().get("ID_PROCESO");
            	
                rta = ejbAlmacenCero.actualizarSaldoPeps(
                            comboCompania,
                            Integer.parseInt(anioInicial),
                            Integer.parseInt(mesInicial),
                            Integer.parseInt(anioFinal),
                            Integer.parseInt(mesFinal),
                            elementoInicial,
                            elementoFinal);
            }
        }
        catch (SystemException e) {
        	error = true;
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        finally {

        	UrlBean updateEstado = UrlServiceUtil.getInstance()
        			.getUrlServiceByUrlByEnumID(
        					ParametrosAlmacenControladorUrlEnum.URL1984002.getValue());

        	Map<String, Object> parametros = new HashMap<>();
        	parametros.put(GeneralParameterEnum.COMPANIA.getName(), nvl(comboCompania, compania));
        	parametros.put(GeneralParameterEnum.NOM_PROCESO.getName(), nombreProceso);
        	parametros.put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());
        	parametros.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
        	parametros.put(GeneralParameterEnum.NO_PROCESO.getName(), idPR);
        	parametros.put(GeneralParameterEnum.ESTADO.getName(),error ? "ERRORES" : "FINALIZADO");
        	parametros.put(GeneralParameterEnum.FECHAFINAL.getName(),new Date());

        	Parameter parameter = new Parameter();
        	parameter.setFields(parametros);

        	try {
        		requestManager.update(updateEstado.getUrl(),
        				updateEstado.getMetodo(),parameter);
        	} catch (SystemException e) {
        		logger.error(e.getMessage(), e);
        	}

        }

        return rta;
    }


    private boolean hallarPosteriores(boolean conExistentes)
    {
        boolean tienePosteriores = false;
        if (consDepreciaciones.equals(SessionUtil.getMenuActual())
            && !conExistentes)
        {

            try
            {

                tienePosteriores = ejbAlmacenCuatro
                                .evaluarDepreciacionesPosteriores(compania,
                                                Integer.parseInt(anioFinal),
                                                Integer.parseInt(mesFinal),
                                                elementoInicial, elementoFinal,
                                                Long.parseLong(placaInicial),
                                                Long.parseLong(placaFinal));

            }
            catch (NumberFormatException | SystemException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        return tienePosteriores;
    }

    private boolean menuCalcularDepreciaciones()
    {
        boolean rta = true;
        if ("10060305".equals(SessionUtil.getMenuActual()))
        {
            rta = calcularDepreciaciones();
        }
        return rta;
    }

    private boolean validarMeses()
    {

        boolean rta = true;
        int anioF = Integer.parseInt(anioInicial);
        int mesF = Integer.parseInt(mesFinal);
        int mesesFinal = (anioF - 1) * 12 + mesF;
        int meses;

        do
        {

            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), comboCompania);
            param.put(GeneralParameterEnum.ANO.getName(), anio);
            param.put(ParametrosAlmacenControladorEnum.PARAM1.getValue(), mes);

            Registro rs;
            try
            {
                rs = RegistroConverter
                                .toRegistro(requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                ParametrosAlmacenControladorUrlEnum.URL7666
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));

                if (rs == null)
                {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2003")
                                    .replace("#$anio#$", String.valueOf(anio))
                                    .replace("#$mes#$", String.valueOf(mes)));
                    return false;
                }

                if (!"A".equals(rs.getCampos().get("ESTADOALMACEN").toString())
                    && !ejecutarProcesosUtilitarioAlmacenSinControlDePeriodo)
                {

                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2000")
                                    .replace("#$anio#$", String.valueOf(anio))
                                    .replace("#$mes#$", String.valueOf(mes)));
                    return false;

                }

                mes++;
                if (mes > 12)
                {
                    anio++;
                    mes = 1;
                }

            }
            catch (SystemException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
            meses = (anio - 1) * 12 + mes;

        }
        while (meses <= mesesFinal);

        return rta;

    }

    
    private boolean validarMes()
    {

        boolean rta = true;
               
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), comboCompania);
        param.put(GeneralParameterEnum.ANO.getName(), anioInicial);
        param.put(ParametrosAlmacenControladorEnum.PARAM1.getValue(), mesInicial);

        Registro rs;
            try
            {
                rs = RegistroConverter
                                .toRegistro(requestManager.get(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                ParametrosAlmacenControladorUrlEnum.URL7666
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));

                if (rs == null)
                {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2003")
                                    .replace("#$anio#$", String.valueOf(anioInicial))
                                    .replace("#$mes#$", String.valueOf(mesInicial)));
                    return false;
                }

                if (!"A".equals(rs.getCampos().get("ESTADOALMACEN").toString()))
                {

                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB4478")
                                    .replace("#$anio#$", String.valueOf(anioInicial))
                                    .replace("#$mes#$", String.valueOf(mesInicial)));
                    return false;

                }

      

            }
            catch (SystemException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }

        return rta;

    }

    private boolean validarInicial(boolean conExistentes)
    {
        boolean rta = true;
        if (!validarCampos()) // VALIDA CAMPOS VACIOS
        {
            rta = false;
        }

        if (!validarMeses()) // VALIDA SI ESTADO DEL MES ESTA O NO
                             // ACTIVO
        {
            rta = false;
        }
        if (hallarPosteriores(conExistentes)) // PAQ4.FC_DEPRECIACIONES_POSTERIORES
        {
            visibleConfirmacion = true;
            rta = false;
        }
        return rta;
    }

    private boolean logicaDeNegocio(Boolean conExistentes)
    {

        boolean rta = true;
        String resultado = "";
        archivoDescarga = null;
        anio = Integer.parseInt(anioInicial);
        mes = Integer.parseInt(mesInicial);

        try
        {

            if (!validarInicial(conExistentes))
            {
                return false;
            }

            hi = SysmanFunciones.convertirAFechaCadena(new Date(),
                            "dd/MM/YYYY HH:mm:ss");
            // ***********************************************************
            // --
            // REVISAR PLACAS --
            // **************************************************************************************
            if (!revisarPlacas())
            {
                rta = false;
            }
            // ***********************************************************
            // --
            // Menu KARDEAR --
            // **************************************************************************************
            menuKardear(); // REVISAR HORAS MOPDIFICA LA D_MOVIMIENTO,
                           // Y PAQUETE3.FC_KARDEXELEMENTOTODOSHALM

            // ********************************************************************
            // -- Menu CALCULAR DEPRECIACIONES INICIALES --
            // *****************************************************************
            if (!menuCalcularDepreciaciones())
            {
                rta = false;
            }

            // ********************************************************************
            // -- Menu CALCULAR DEPRECIACIONES --
            // *****************************************************************

            if (!menuCalcularDepreciacionesIniciales())
            {
                rta = false;
            }

            // ********************************************************************
            // -- Menu ACTUALIZAR SALDO PEPS --
            // *****************************************************************

            resultado = menuActualizarSaldoPeps();
            if (!"".equals(resultado))
            {
                JsfUtil.agregarMensajeInformativo(resultado);
            }

            if (rta)
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(mensajeAlertCons).replace(
                                                hiCons, String.valueOf(hi)));
            }

        }
        catch (ParseException ex)
        {
            Logger.getLogger(ParametrosAlmacenControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        return rta;
    }

    public void oprimirAceptar()
    {
        if (!logicaDeNegocio(false))
        {
            return;
        }
    }

    public boolean calcularDepreciaciones()
    {
        boolean rta = true;
        if (consDepreciaciones.equals(SessionUtil.getMenuActual()))
        {
            try
            {
                salidaTexto = ejbAlmacenCuatro.calcularDepreciacionMensual(
                                comboCompania, Integer.parseInt(anioInicial),
                                Integer.parseInt(mesInicial),
                                Integer.parseInt(anioFinal),
                                Integer.parseInt(mesFinal),
                                elementoInicial, elementoFinal,
                                Long.parseLong(placaInicial),
                                Long.parseLong(placaFinal));

                if (!"TRUE".equals(salidaTexto))
                {
                    generarArchivo(salidaTexto,
                                    "IncosistenciasDepreciacionMes");
                    rta = false;
                }

            }
            catch (NumberFormatException | SystemException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }

        return rta;
    }

    public void ejecutarmostrarMensaje()
    {

        try
        {
            hi = SysmanFunciones.convertirAFechaCadena(new Date(),
                            "dd/MM/YYYY HH:mm:ss");
            JsfUtil.agregarMensajeInformativo(idioma.getString(mensajeAlertCons)
                            .replace(hiCons, String.valueOf(hi)));
        }
        catch (ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirBtnInicialNIIF(ActionEvent ac)
    {
        // <CODIGO_DESARROLLADO>
    	boolean error = false;
        // depreciacion niif inicial
        if ("10060304".equals(SessionUtil.getMenuActual()))
        {

            try
            {
            	if (consultarEstado(opcion, nombreProceso)) {
                    return;
                }

                agregarControl(nombreProceso);
                
                Map<String, Object> param = new HashMap<>();
        		param.put(GeneralParameterEnum.ESTADO.getName(), "INICIADO");
        		param.put(GeneralParameterEnum.COMPANIA.getName(), nvl(comboCompania,compania));
        		param.put(GeneralParameterEnum.NOM_PROCESO.getName(), nombreProceso);

        		Registro rs = RegistroConverter
        				.toRegistro(requestManager.get(
        						UrlServiceUtil.getInstance()
        						.getUrlServiceByUrlByEnumID(
        								ParametrosAlmacenControladorUrlEnum.URL1984001.getValue())
        						.getUrl(),
        						param));
        		idPR = rs.getCampos().get("ID_PROCESO");

                salidaTexto = ejbAlmacenCuatro.calcularDepreciacionInicialNiif(
                                comboCompania, elementoInicial,
                                elementoFinal, Long.parseLong(placaInicial),
                                Long.parseLong(placaFinal));
            	
            	if (!"TRUE".equals(salidaTexto)) {
                    generarArchivo(salidaTexto,"IncosistenciasDepreciacionInicialNIIF");
                    error = true;
                } else {
                    ejecutarmostrarMensaje();
                }
            	
            }
            catch (NumberFormatException | SystemException e)
            {
            	error = true;
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }finally {

                UrlBean updateEstado = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                ParametrosAlmacenControladorUrlEnum.URL1984002.getValue());

                Map<String, Object> parametros = new HashMap<>();
                parametros.put(GeneralParameterEnum.COMPANIA.getName(), nvl(comboCompania, compania));
                parametros.put(GeneralParameterEnum.NOM_PROCESO.getName(), nombreProceso);
                parametros.put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());
                parametros.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
                parametros.put(GeneralParameterEnum.NO_PROCESO.getName(), idPR);
                parametros.put(GeneralParameterEnum.ESTADO.getName(), error ? "ERRORES" : "FINALIZADO");
                parametros.put(GeneralParameterEnum.FECHAFINAL.getName(),new Date());

                Parameter parameter = new Parameter();
                parameter.setFields(parametros);

                try {
                    requestManager.update(updateEstado.getUrl(),updateEstado.getMetodo(),parameter);
                } catch (SystemException e) {
                    logger.error(e.getMessage(), e);
            }
            }

            return;
        }
        // depreciacion niif mensual
        else if ("10060305".equals(SessionUtil.getMenuActual()))
        {
        	if(consultarEstado(opcion,nombreProceso)) {
        		return;
        	}
			if (!validarMes())
			    {
			            return;
			     }
       
            try
            {
            	agregarControl(nombreProceso);
                salidaTexto = ejbAlmacenCinco.calcularDepreciacionHNiif(
                                comboCompania, Integer.parseInt(anioInicial),
                                Integer.parseInt(mesInicial),
                                Integer.parseInt(anioFinal),
                                Integer.parseInt(mesFinal),
                                elementoInicial, elementoFinal,
                                Long.parseLong(placaInicial),
                                Long.parseLong(placaFinal));

            }
            catch (NumberFormatException | SystemException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }

        if (!"TRUE".equals(salidaTexto))
        {
            generarArchivo(salidaTexto, "IncosistenciasDepreciacionMesNIIF");
        }
        else
        {
            ejecutarmostrarMensaje();
        }
    }
    
    public boolean consultarEstado(String opc, String proceso) {
    	boolean enProceso = false;
    	try {
    		Map<String, Object> param = new HashMap<>();
    		param.put(GeneralParameterEnum.ESTADO.getName(), "INICIADO");
    		param.put(GeneralParameterEnum.COMPANIA.getName(), nvl(comboCompania,compania));
    		param.put(GeneralParameterEnum.NOM_PROCESO.getName(), proceso);

    		Registro rs = RegistroConverter
    				.toRegistro(requestManager.get(
    						UrlServiceUtil.getInstance()
    						.getUrlServiceByUrlByEnumID(
    								ParametrosAlmacenControladorUrlEnum.URL1984001.getValue())
    						.getUrl(),
    						param));

    		if (rs != null)
    		{ 			
    			bloqueaConsulta = true;
    			enProceso = true;
    			String mensaje = String.format("Proceso %s ya se encuentra en ejecución, debe esperar su finalizacin para iniciar un nuevo proceso.", opc);
    			JsfUtil.agregarMensajeError(mensaje);
    		}
    	} catch (SystemException e) {
    		logger.error(e.getMessage(),e);
    		JsfUtil.agregarMensajeError(e.getMessage());
    	}
    	return enProceso;
    }
    
    public void agregarControl(String proceso) {
    	try {
    		
    		String ip = obtenerIpCliente();
    		String userName = System.getProperty("user.name");
    		String pcName = System.getenv("COMPUTERNAME"); // Windows
    		if (pcName == null) {
    		    pcName = System.getenv("HOSTNAME"); // Linux/Mac
    		}
    		Registro registroControl = new Registro();
    		registroControl.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), nvl(comboCompania,compania));
    		registroControl.getCampos().put("NOMBRE_PROCESO", proceso);
    		registroControl.getCampos().put("ESTADO_EJECUCION", "INICIADO");
    		registroControl.getCampos().put("FECHA_INICIO", new Date());
    		registroControl.getCampos().put("USUARIO_EJECUTOR", SessionUtil.getUser().getCodigo());
    		registroControl.getCampos().put("MAQUINA_INSTANCIA", "Usuario: " + userName + " / Equipo: " + pcName  + " / Ip: " + ip );
    		registroControl.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(), SessionUtil.getUser().getCodigo());
    		registroControl.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());
    		
    		UrlBean urlCreate = UrlServiceUtil.getInstance()
    				.getUrlServiceByUrlByEnumID(GenericUrlEnum.CONTROL_PROCESOS.getCreateKey());

    		requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
    				registroControl.getCampos());

    		JsfUtil.agregarMensajeInformativo(
    				idioma.getString("MSM_REGISTRO_INGRESADO"));
    	} catch (SystemException ex) {
    		logger.error(ex.getMessage(),ex);
    		JsfUtil.agregarMensajeError(ex.getMessage());
    	}
    }
    
    public String obtenerIpCliente() {
    	HttpServletRequest request = (HttpServletRequest) 
    			FacesContext.getCurrentInstance().getExternalContext().getRequest();

    	String ip = request.getHeader("X-FORWARDED-FOR");
    	if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
    		ip = request.getRemoteAddr();
    	}
    	return ip;
    }
    
    public void mensajesInicioModal() {
    	 if(SessionUtil.getMenuActual().equals("10060302")){
             consultarEstado(opcion,nombreProceso);
    	 }else if(SessionUtil.getMenuActual().equals("10060305")){
             consultarEstado(opcion,nombreProceso);
    	 }else if(SessionUtil.getMenuActual().equals("10060306")){
             consultarEstado(opcion,nombreProceso);
    	 }else if(SessionUtil.getMenuActual().equals("10060320")){
             consultarEstado(opcion,nombreProceso);
    	 }else if(SessionUtil.getMenuActual().equals("10060304")){
             consultarEstado(opcion,nombreProceso);
    	 }
    }

    // </CODIGO_DESARROLLADO>

    public void cambiarCompania()
    {
        // <CODIGO_DESARROLLADO>

        cargarListaElementoinicial();
        elementoFinal = elementoInicial = null;
        // </CODIGO_DESARROLLADO>
    }

    public void aceptarRecalcularDepreciaciones()
    {
        visibleConfirmacion = false;
        logicaDeNegocio(true);
    }

    public void seleccionarFilaElementoinicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        elementoInicial = registroAux.getCampos().get(codigoElementoCons)
                        .toString();

        cargarListaElementoFinal();
    }

    public void seleccionarFilaElementoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        elementoFinal = registroAux.getCampos().get(codigoElementoCons)
                        .toString();
    }

    public String getVerificarDependencia()
    {
        return verificarDependencia;
    }

    public void setVerificarDependencia(String verificarDependencia)
    {
        this.verificarDependencia = verificarDependencia;
    }

    public String getRevisarDepAnteriores()
    {
        return revisarDepAnteriores;
    }

    public void setRevisarDepAnteriores(String revisarDepAnteriores)
    {
        this.revisarDepAnteriores = revisarDepAnteriores;
    }

    public String getComboCompania()
    {
        return comboCompania;
    }

    public void setComboCompania(String comboCompania)
    {
        this.comboCompania = comboCompania;
    }

    public String getMesInicial()
    {
        return mesInicial;
    }

    public void setMesInicial(String mesInicial)
    {
        this.mesInicial = mesInicial;
    }

    public String getMesFinal()
    {
        return mesFinal;
    }

    public void setMesFinal(String mesFinal)
    {
        this.mesFinal = mesFinal;
    }

    public String getElementoInicial()
    {
        return elementoInicial;
    }

    public void setElementoInicial(String elementoInicial)
    {
        this.elementoInicial = elementoInicial;
    }

    public String getElementoFinal()
    {
        return elementoFinal;
    }

    public void setElementoFinal(String elementoFinal)
    {
        this.elementoFinal = elementoFinal;
    }

    public String getIdentificacion()
    {
        return identificacion;
    }

    public void setIdentificacion(String identificacion)
    {
        this.identificacion = identificacion;
    }

    public String getAnioInicial()
    {
        return anioInicial;
    }

    public void setAnioInicial(String anioInicial)
    {
        this.anioInicial = anioInicial;
    }

    public String getPlacaInicial()
    {
        return placaInicial;
    }

    public void setPlacaInicial(String placaInicial)
    {
        this.placaInicial = placaInicial;
    }

    public String getPlacaFinal()
    {
        return placaFinal;
    }

    public void setPlacaFinal(String placaFinal)
    {
        this.placaFinal = placaFinal;
    }

    public String getAnioFinal()
    {
        return anioFinal;
    }

    public void setAnioFinal(String anioFinal)
    {
        this.anioFinal = anioFinal;
    }

    public String getNombreProceso()
    {
        return nombreProceso;
    }

    public void setNombreProceso(String nombreProceso)
    {
        this.nombreProceso = nombreProceso;
    }

    public List<Registro> getListaCompania()
    {
        return listaCompania;
    }

    public void setListaCompania(List<Registro> listaCompania)
    {
        this.listaCompania = listaCompania;
    }

    public RegistroDataModelImpl getListaElementoinicial()
    {
        return listaElementoinicial;
    }

    public void setListaElementoinicial(
        RegistroDataModelImpl listaElementoinicial)
    {
        this.listaElementoinicial = listaElementoinicial;
    }

    public RegistroDataModelImpl getListaElementoFinal()
    {
        return listaElementoFinal;
    }

    public void setListaElementoFinal(
        RegistroDataModelImpl listaElementoFinal)
    {
        this.listaElementoFinal = listaElementoFinal;
    }

    public boolean isVisibleIdentificacion()
    {
        return visibleIdentificacion;
    }

    public void setVisibleIdentificacion(boolean visibleIdentificacion)
    {
        this.visibleIdentificacion = visibleIdentificacion;
    }

    public boolean isVisibleAdvertencia()
    {
        return visibleAdvertencia;
    }

    public void setVisibleAdvertencia(boolean visibleAdvertencia)
    {
        this.visibleAdvertencia = visibleAdvertencia;
    }

    public boolean isVisibleVerificarDependencia()
    {
        return visibleVerificarDependencia;
    }

    public void setVisibleVerificarDependencia(
        boolean visibleVerificarDependencia)
    {
        this.visibleVerificarDependencia = visibleVerificarDependencia;
    }

    public boolean isVisibleBtnInicialNIIF()
    {
        return visibleBtnInicialNIIF;
    }

    public void setVisibleBtnInicialNIIF(boolean visibleBtnInicialNIIF)
    {
        this.visibleBtnInicialNIIF = visibleBtnInicialNIIF;
    }

    public boolean isVisibleRevisarDepAnteriores()
    {
        return visibleRevisarDepAnteriores;
    }

    public void setVisibleRevisarDepAnteriores(
        boolean visibleRevisarDepAnteriores)
    {
        this.visibleRevisarDepAnteriores = visibleRevisarDepAnteriores;
    }

    public boolean isEjecutarProcesosUtilitarioAlmacenSinControlDePeriodo()
    {
        return ejecutarProcesosUtilitarioAlmacenSinControlDePeriodo;
    }

    public void setEjecutarProcesosUtilitarioAlmacenSinControlDePeriodo(
        boolean ejecutarProcesosUtilitarioAlmacenSinControlDePeriodo)
    {
        this.ejecutarProcesosUtilitarioAlmacenSinControlDePeriodo = ejecutarProcesosUtilitarioAlmacenSinControlDePeriodo;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public boolean isVisibleConfirmacion()
    {
        return visibleConfirmacion;
    }

    public void setVisibleConfirmacion(boolean visibleConfirmacion)
    {
        this.visibleConfirmacion = visibleConfirmacion;
    }

    public boolean isVerificarMovimientosDeCorreccionDeValor()
    {
        return verificarMovimientosDeCorreccionDeValor;
    }

    public void setVerificarMovimientosDeCorreccionDeValor(
        boolean verificarMovimientosDeCorreccionDeValor)
    {
        this.verificarMovimientosDeCorreccionDeValor = verificarMovimientosDeCorreccionDeValor;
    }

    public boolean isManejaNiifEnAlmacen()
    {
        return manejaNiifEnAlmacen;
    }

    public void setManejaNiifEnAlmacen(boolean manejaNiifEnAlmacen)
    {
        this.manejaNiifEnAlmacen = manejaNiifEnAlmacen;
    }

    public boolean isBloqueoMesFinal()
    {
        return bloqueoMesFinal;
    }

    public void setBloqueoMesFinal(boolean bloqueoMesFinal)
    {
        this.bloqueoMesFinal = bloqueoMesFinal;
    }

    /**
     * @return the visibleRangoPlaca
     */
    public boolean isVisibleRangoPlaca()
    {
        return visibleRangoPlaca;
    }

    /**
     * @param visibleRangoPlaca
     * the visibleRangoPlaca to set
     */
    public void setVisibleRangoPlaca(boolean visibleRangoPlaca)
    {
        this.visibleRangoPlaca = visibleRangoPlaca;
    }

	/**
	 * @return the bloqueaConsulta
	 */
	public boolean isBloqueaConsulta() {
		return bloqueaConsulta;
	}

	/**
	 * @param bloqueaConsulta the bloqueaConsulta to set
	 */
	public void setBloqueaConsulta(boolean bloqueaConsulta) {
		this.bloqueaConsulta = bloqueaConsulta;
	}
	
	public String getOpcion() {
		return opcion;
	}

	public void setOpcion(String opcion) {
		this.opcion = opcion;
	}
		
	


}

package com.sysman.almacen;

import com.sysman.almacen.enums.PazYsalvoTerceroControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
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
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author esarmiento
 * @version 1, 28/01/2016
 * @modifier amonroy
 * @version 2, 04/05/2017 Proceso de Refactoring e implementaci�n de
 * EJBs
 *
 * -- Modificado por lcortes 13/06/2017. Se reemplaza el valor del
 * atributo numFormulario por el enumerado correspondiente y se
 * suprimen las conexiones a la base de datos.
 */
@ManagedBean
@ViewScoped
public class PazYsalvoTerceroControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private final String cResponsable;
    private String empleado;
    private String nombreFuncionario;
    private Date fechaExp;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaCodigoInicial;
    private String nombreReporte;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
	private String numeroDoc;
	private Registro registroAux;
	private Map<String, Object> param;
	private boolean verCodigo;

    /**
     * Creates a new instance of PazYsalvoTerceroControlador
     */
    public PazYsalvoTerceroControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cResponsable = "responsable";
        try {
            numFormulario = GeneralCodigoFormaEnum.PAZ_YSALVO_TERCERO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(PazYsalvoTerceroControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaCodigoInicial();
        fechaExp = new Date();
        abrirFormulario();
    }

    public void cargarListaCodigoInicial() {
    	
    	 param = new TreeMap<>();
    	String urlEnumId;
    	
    	if("2109010103".equals(SessionUtil.getMenuActual())) {
    		
            numeroDoc = SessionUtil.getUser().getCedula();
            
            param.put(GeneralParameterEnum.DOCNUM.getName(), numeroDoc);
    		
    		urlEnumId = PazYsalvoTerceroControladorUrlEnum.URL2855.getValue();
    		
    	}else {
    		
    		urlEnumId = PazYsalvoTerceroControladorUrlEnum.URL2854.getValue();
       
         }
    	
    	 UrlBean urlBean = UrlServiceUtil.getInstance()
                 .getUrlServiceByUrlByEnumID(urlEnumId);
        
        
        
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CEDULA");
        
      
        
    }

    public boolean validarNulos() {
        if (SysmanFunciones.validarVariableVacio(empleado)) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB1875"));
            return false;
        }
        if (fechaExp == null) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB1876"));
            return false;
        }
        return true;
    }

    public void generarInforme(FORMATOS formato) {
        if (!validarNulos()) {
            return;
        }
        String parametroTunja = null;
        nombreReporte = null;
        String parametroAux = null;
        HashMap<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();
        String strsql = null;
        Registro reg = null;
        try {
            parametroTunja = getParametro(
                            "IMPRIMIR FORMATO DE PAZ Y SALVO TUNJA", true);
            parametroAux = getParametro("FORMATO DE PAZ Y SALVO A MARZO 2010",
                            true);

            Map<String, Object> paramsReg = new TreeMap<>();
            paramsReg.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            paramsReg.put(GeneralParameterEnum.RESPONSABLE.getName(), empleado);

            reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PazYsalvoTerceroControladorUrlEnum.URL001
                                                                            .getValue())
                                            .getUrl(), paramsReg));

            if ((reg == null)
                || "0".equals(reg.getCampos().get("CANT").toString())) {
                prepararReemplazos(reemplazar, parametros, parametroTunja,
                                parametroAux);

            }
            else {
                nombreReporte = "000007DevolutivoResponsable";
                reemplazar.put(cResponsable, empleado);
            }
            parametros.put("PR_NOMBRECOMPANIA", SessionUtil
                            .getCompaniaIngreso().getNombre());
            strsql = Reporteador.resuelveConsulta(nombreReporte,
                            Integer.parseInt(modulo), reemplazar);
            parametros.put("PR_STRSQL", strsql);

            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN,
                            formato);
        }
        catch (JRException | IOException | SysmanException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    private void prepararReemplazos(HashMap<String, Object> reemplazar,
        Map<String, Object> parametros, String parametroTunja,
        String parametroAux) {

        if ((parametroTunja != null) && "SI".equals(parametroTunja)) {
            nombreReporte = "000486PazySalvoPorTerceroTunja";
            reemplazar.put(cResponsable, empleado);

            String ciudad = initCap(SessionUtil.getCompaniaIngreso()
                            .getCiudad());
            parametros.put("PR_EXPEDIDA",
                            "Se expide en "
                                + ciudad
                                + " a los "
                                + SysmanFunciones.getParteFecha(
                                                fechaExp,
                                                Calendar.DAY_OF_MONTH)
                                + " "
                                + idioma.getString("TG_DIAS2").toLowerCase()
                                + " del mes de "
                                + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[SysmanFunciones
                                                .getParteFecha(fechaExp,
                                                                Calendar.MONTH)
                                    + 1]
                                + " de "
                                + SysmanFunciones.getParteFecha(
                                                fechaExp,
                                                Calendar.YEAR));
            parametros.put("PR_CIUDADCOMPANIA", SessionUtil
                            .getCompaniaIngreso().getCiudad());
        }
        else {
            if ((parametroAux != null)
                && ("PAZYSALVOPORTERCERO_VMARZO2010")
                                .equalsIgnoreCase(parametroAux)) {
                nombreReporte = "000489PazySalvoPorTerceroVMarzo2010";
                reemplazar.put(cResponsable, empleado);

                parametros.put("PR_EXPEDIDA",
                                SessionUtil.getCompaniaIngreso()
                                                .getCiudad()
                                    + ", "
                                    + SysmanFunciones.getParteFecha(
                                                    fechaExp,
                                                    Calendar.DAY_OF_MONTH)
                                    + " de "
                                    + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[SysmanFunciones
                                                    .getParteFecha(fechaExp,
                                                                    Calendar.MONTH)
                                        + 1]
                                    + " de "
                                    + SysmanFunciones.getParteFecha(
                                                    fechaExp,
                                                    Calendar.YEAR));
            }
            else {
                nombreReporte = getParametro(
                        	"FORMATO DE PAZ Y SALVO", false);
                
                String ciudad = initCap(SessionUtil.getCompaniaIngreso()
                        .getCiudad());

                reemplazar.put(cResponsable, empleado);

                String modificarTextoPazYsalvo = getParametro(
                                "MODIFICAR TEXTO EN PAZ Y SALVO", false);
                String textoPazySalvo = getParametro("TEXTO EN PAZ Y SALVO",
                                false);
                String encargadoBienes = getParametro(
                                "ENCARGADO DE LOS BIENES", false);
                String firmaTecnico = getParametro("FIRMA TECNICO PAZ Y SALVO",
                                false);
                String firmaDirector = getParametro(
                                "FIRMA DIRECTOR PAZ Y SALVO", false);
                String cargoAlmacenista = getParametro(
                        		"CARGO ALMACENISTA", false);
                String aprobacion = getParametro("APROBACION", false);
                String encabezado = getParametro("ENCABEZADO PAZ Y SALVO",
                                false);

                parametros.put("PR_CIUDADCOMPANIA", SessionUtil
                                .getCompaniaIngreso().getCiudad());
                parametros.put("PR_MODIFICAR_TEXTO_PAZ_Y_SALVO",
                                modificarTextoPazYsalvo == null ? "NO"
                                    : modificarTextoPazYsalvo);
                parametros.put("PR_TEXTO_PAZ_Y_SALVO",
                                textoPazySalvo);
                parametros.put("PR_ENCARGADO_BIENES",
                                encargadoBienes);
                parametros.put("PR_FIRMA_TECNICO_PAZ_Y_SALVO",
                                firmaTecnico);
                parametros.put("PR_FIRMA_DIRECTOR_PAZ_Y_SALVO",
                                firmaDirector);
                parametros.put("PR_FECHA_EXPEDICION",
                                SysmanFunciones.getParteFecha(fechaExp,
                                                Calendar.DAY_OF_MONTH)
                                    + " de "
                                    + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[SysmanFunciones
                                                    .getParteFecha(fechaExp,
                                                                    Calendar.MONTH)
                                        + 1]
                                    + " de "
                                    + SysmanFunciones.getParteFecha(
                                                    fechaExp,
                                                    Calendar.YEAR));
                parametros.put("PR_APROBACION", aprobacion);
                parametros.put("PR_ENCABEZADO_PAZ_Y_SALVO", encabezado);
                parametros.put("PR_CARGO_ALMACENISTA", cargoAlmacenista);
                parametros.put("PR_CIUDAD", ciudad);
            }
        }
    }

    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        empleado = registroAux.getCampos().get("CEDULA").toString();
        nombreFuncionario = registroAux.getCampos().get("NOMBRE").toString();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
    	  if("2109010103".equals(SessionUtil.getMenuActual())) {
          	try {
          	 registroAux = (Registro) listaCodigoInicial.getRegistroUnico(param);
         	 empleado = SysmanFunciones.nvl(registroAux.getCampos().get("CEDULA").toString(),"").toString();
  	         nombreFuncionario = SysmanFunciones.nvl(registroAux.getCampos().get("NOMBRE").toString(),"").toString();
          	 verCodigo = true;
  			} catch (SystemException e) {
  				  logger.error(e.getMessage(), e);
  		            JsfUtil.agregarMensajeError(e.getMessage());
  			}
          
          }else {
    	  verCodigo = false;
          }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Trae el valor almacenado en la base de datos para el parametro
     * ingresado.
     *
     * @param nombreParametro
     * Nombre del parametro a consultar en la base de datos.
     * @return valor asignado al parametro
     */
    private String getParametro(String nombreParametro, boolean original) {
        String parametro = null;
        try {
            parametro = ejbSysmanUtil.consultarParametro(compania,
                            nombreParametro, modulo, new Date(), original);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametro;
    }

    /**
     * Coloca la primera letra de la cadena en mayuscula
     *
     * @param cadena
     * Palabra qe se desea inicie por mayuscula
     * @return Valor de cadena iniciando por mayuscula
     */
    private String initCap(String cadena) {
        String aux = cadena.toLowerCase();
        char[] cadenaArray = aux.toCharArray();
        cadenaArray[0] = Character.toUpperCase(cadenaArray[0]);
        return new String(cadenaArray);
    }

    public String getEmpleado() {
        return empleado;
    }

    public void setEmpleado(String empleado) {
        this.empleado = empleado;
    }

    public String getNombreFuncionario() {
        return nombreFuncionario;
    }

    public void setNombreFuncionario(String nombreFuncionario) {
        this.nombreFuncionario = nombreFuncionario;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListaCodigoInicial() {
        return listaCodigoInicial;
    }

    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial) {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    public Date getFechaExp() {
        return fechaExp;
    }

    public void setFechaExp(Date fechaExp) {
        this.fechaExp = fechaExp;
    }

	public boolean isVerCodigo() {
		return verCodigo;
	}

	public void setVerCodigo(boolean verCodigo) {
		this.verCodigo = verCodigo;
	}
    
    

}

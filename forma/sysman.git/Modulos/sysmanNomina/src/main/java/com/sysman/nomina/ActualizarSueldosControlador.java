package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaDosRemote;
import com.sysman.nomina.enums.ActualizarSueldosControladorEnum;
import com.sysman.nomina.enums.ActualizarSueldosControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;
import com.sysman.reportes.Reporteador;
import java.util.HashMap;
import com.sysman.persistencia.ConectorPool;
import net.sf.dynamicreports.report.exception.DRException;
import com.sysman.jsfutil.ReportesBean;
import java.sql.SQLException;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import net.sf.jasperreports.engine.JRException;
import java.io.IOException;

/**
 *
 * @author jacelas arevalo
 * @version 1, 22/07/2015 f
 * 
 * @author asana
 * @version 2, 29/09/2017 - 03/11/2017
 */
@ManagedBean
@ViewScoped
public class ActualizarSueldosControlador extends BeanBaseModal
{

    private final String compania;
    private final String modulo;
    private String anobase;
    private String anoactualizar;
    private String porcentajeIncremento;
    private String porcentajeIntegral;
    private String porcentajePensional;
    private List<Registro> listaAnoBase;
    private List<Registro> listaAnoActual;
    private boolean eliminar;
    private String mensajeDialogo;
    private StreamedContent archivoDescarga;
    private Integer opcion;
    @EJB
    private EjbNominaDosRemote ejbNominaDosRemote;

    /**
     * Creates a new instance of ActualizarSueldosControlador
     *
     */
    public ActualizarSueldosControlador()
    {
        super();
        numFormulario = GeneralCodigoFormaEnum.ACTUALIZAR_SUELDOS_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        try
        {
            validarPermisos();
        }
        catch (Exception ex)
        {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(ActualizarSueldosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void inicializar()
    {
        porcentajeIntegral = "0";
        porcentajeIncremento = "0";
        porcentajePensional = "0";

        anobase = Integer.toString(SysmanFunciones.getParteFecha(new Date(),
                        Calendar.YEAR)
            - 1);
        anoactualizar = Integer.toString(SysmanFunciones.getParteFecha(new Date(),
                        Calendar.YEAR));
        cargarListaAnoBase();
        cargarlistaAnoActual();
        eliminar = false;
        abrirFormulario();
    }

    public void cargarListaAnoBase()
    {

        Map<String, Object> parametros = new TreeMap<>();
        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaAnoBase = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ActualizarSueldosControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(), parametros));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarlistaAnoActual()
    {
        Map<String, Object> parametros = new TreeMap<>();

        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametros.put(ActualizarSueldosControladorEnum.PARAM0.getValue(), anobase);

        try
        {
            listaAnoActual = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ActualizarSueldosControladorUrlEnum.URL0002
                                                                            .getValue())
                                            .getUrl(), parametros));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirActualiza()
    {
    	boolean validaAnio = false;
    	Double porPen = Double.parseDouble(SysmanFunciones.nvlStr(porcentajePensional, "0"));
    	Double porSueldo = Double.parseDouble(SysmanFunciones.nvlStr(porcentajeIncremento, "0"));
    	opcion = 1;
    
    	
    	if ((anoactualizar != null) && (anobase != null))
        {    		
        	if (porPen != 0) {    
        		if (porSueldo == 0) {
        			JsfUtil.agregarMensajeError(idioma.getString("TB_TB37701"));
                    eliminar = false;
                    return;
        		}
        		
        		try
                {
        			validaAnio = ejbNominaDosRemote.getValidaAnioMesada(compania, Integer.parseInt(anoactualizar),2);
        			
        			if (validaAnio) {
        				JsfUtil.agregarMensajeError(idioma.getString("TB_TB37301").replace("#$ano#$", anoactualizar));
        				return;
        			}
          
                }
                catch (NumberFormatException | SystemException ex)
                {
                    JsfUtil.agregarMensajeError(
                                    SysmanFunciones.concatenar(idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA), " ", ex.getMessage()));
                    Logger.getLogger(ActualizarSueldosControlador.class.getName())
                                    .log(Level.SEVERE, null, ex);
                }
        	}
        	
            mensajeDialogo = idioma.getString("TB_TB3818").replace("#$ano#$", anoactualizar).replace("#$sueldos#$", porcentajeIncremento)
                            .replace("#$integrales#$", porcentajeIntegral).replace("#$pensionales#$", porcentajePensional);
            eliminar = true;
        }
        else
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB3770"));
            eliminar = false;
        }
        
    }

    public void cambiarPorcentajeInc()
    {
        if (porcentajeIncremento == null)
        {
            porcentajeIncremento = "0";
        }

    }

    public void cambiarPorcentajeIncInt()
    {
        if (porcentajeIntegral == null)
        {
            porcentajeIntegral = "0";
        }
    }

    public void cambiarPorcentajeIncPen()
    {
        if (porcentajePensional == null)
        {
            porcentajePensional = "0";
        }

    }
    
    public void generarReporteMesada()
    {
    	HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("compania",compania);
        String sql = Reporteador.resuelveConsulta("800605ActualizarMesadaPensional",
                        Integer.parseInt(modulo),
                        reemplazar);

        try
        {
        	archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
            					ConectorPool.ESQUEMA_SYSMAN,
            					ReportesBean.FORMATOS.EXCEL97, "ActualizacionMesadaPensional");
        }
        catch (JRException | IOException | SQLException | DRException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void actualizarSueldos()
    {
    	try
        {
            ejbNominaDosRemote.putActualizarSueldos(compania, Integer.parseInt(anobase),
                            Integer.parseInt(SysmanFunciones.nvlStr(anoactualizar, "0")),
                            Double.parseDouble(SysmanFunciones.nvlStr(porcentajeIncremento, "0")),
                            Double.parseDouble(SysmanFunciones.nvlStr(porcentajePensional, "0")),
                            SessionUtil.getUser().toString());
            
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3763").replace("#$ano#$", anoactualizar));
        }
        catch (NumberFormatException | SystemException ex)
        {
            JsfUtil.agregarMensajeError(
                            SysmanFunciones.concatenar(idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA), " ", ex.getMessage()));
            Logger.getLogger(ActualizarSueldosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }
    
    public void revMesada()
    {
    	boolean valida = false;
    	
    	try
        {
    		valida = ejbNominaDosRemote.reversaMesada(compania, SessionUtil.getUser().toString());
    		
    		if (valida) {
    			JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB37302"));
			} else {
				JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB37303"));	
			}
        }
        catch (NumberFormatException | SystemException ex)
        {
            JsfUtil.agregarMensajeError(
                            SysmanFunciones.concatenar(idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA), " ", ex.getMessage()));
            Logger.getLogger(ActualizarSueldosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }
    
    public void oprimirBtnActMesada() {
        archivoDescarga=null;  
        //GENERAR ARCHIVO PENSIONADOS
        //if (Double.parseDouble(SysmanFunciones.nvlStr(porcentajePensional, "0")) != 0) {
        	generarReporteMesada();
        //}
   }
    
    public void oprimirBtnRevMesada() {
        archivoDescarga=null;  
        opcion = 2;
        
        mensajeDialogo = idioma.getString("TB_TB38181");
        eliminar = true;
   }

    public void cambiarAnoActual()
    {
        // Metodo necesario en la vista
    }

    public void cambiarAnoBase()
    {
        // <CODIGO_DESARROLLADO>
        cargarlistaAnoActual();

        // </CODIGO_DESARROLLADO>
    }

    public void cancelarconfirmacionEliminar()
    {
        eliminar = false;
    }

    @Override
    public void abrirFormulario()
    {
        // Metodo heredado
    }

    /**
     * @return the eliminar
     */
    public boolean isEliminar()
    {
        return eliminar;
    }

    public void aceptarconfirmacionEliminar()
    {
        // Metodo heredado
    	eliminar = false;
		if ( opcion == 1 ) {
    		actualizarSueldos();
    	} else {
    		revMesada();
    	}
    }

    /**
     * @param eliminar
     * the eliminar to set
     */
    public void setEliminar(boolean eliminar)
    {
        this.eliminar = eliminar;
    }

    public String getAnobase()
    {
        return anobase;
    }

    public void setAnobase(String anobase)
    {
        this.anobase = anobase;
    }

    public String getAnoactualizar()
    {
        return anoactualizar;
    }

    public void setAnoactualizar(String anoactualizar)
    {
        this.anoactualizar = anoactualizar;
    }

    public String getPorcentajeIncremento()
    {
        return porcentajeIncremento;
    }

    public void setPorcentajeIncremento(String porcentajeIncremento)
    {
        this.porcentajeIncremento = porcentajeIncremento;
    }

    public String getPorcentajeIntegral()
    {
        return porcentajeIntegral;
    }

    public void setPorcentajeIntegral(String porcentajeIntegral)
    {
        this.porcentajeIntegral = porcentajeIntegral;
    }

    public String getPorcentajePensional()
    {
        return porcentajePensional;
    }

    public void setPorcentajePensional(String porcentajePensional)
    {
        this.porcentajePensional = porcentajePensional;
    }

    public List<Registro> getListaAnoBase()
    {
        return listaAnoBase;
    }

    public void setListaAnoBase(List<Registro> listaAnoBase)
    {
        this.listaAnoBase = listaAnoBase;
    }

    public List<Registro> getListaAnoActual()
    {
        return listaAnoActual;
    }

    public void setListaAnoActual(List<Registro> listaAnoActual)
    {
        this.listaAnoActual = listaAnoActual;
    }

    public String getMensajeDialogo()
    {
        return mensajeDialogo;
    }

    public void setMensajeDialogo(String mensajeDialogo)
    {
        this.mensajeDialogo = mensajeDialogo;
    }

	public StreamedContent getArchivoDescarga() {
		return archivoDescarga;
	}

	public void setArchivoDescarga(StreamedContent archivoDescarga) {
		this.archivoDescarga = archivoDescarga;
	}

	public String getModulo() {
		return modulo;
	}

	public Integer getOpcion() {
		return opcion;
	}

	public void setOpcion(Integer opcion) {
		this.opcion = opcion;
	}

}

package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.recursos.ejb.EjbPrepararAnoRemote;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.model.StreamedContent;

/**
 *
 * @author ybecerra
 * @version 1, 25/04/2016
 * @modified spina 07/04/2017 - se refactoriza dss y depuracion sonar
 *
 * @author asana
 * @version 2, 12/06/2017 Se implementa enum en formulario.
 */
@ManagedBean
@ViewScoped
public class PrepararcontaControlador extends BeanBaseModal
{
    private final String compania;
    private int anoFuente;
    private int anoDestino;

    @EJB
    private EjbPrepararAnoRemote ejbPrepararAno;
    
    private StreamedContent archivoDescarga;
    /**
     * Activa la pantalla modal
     */
    private boolean confirmarCierre; 
    /**
     * Valida el estado del boton de la pantalla modal
     */
    private boolean copiarEquivalentes;   

    /**
     * Creates a new instance of PrepararcontaControlador
     */
    public PrepararcontaControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.PREPARARCONTA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(PrepararcontaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init()
    {
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        anoFuente = SysmanFunciones.ano(new Date());
        anoDestino = SysmanFunciones.ano(new Date())
            + 1;
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirIniciar(ActionEvent ac)
    {
        // <CODIGO_DESARROLLADO>
        if (anoFuente > anoDestino)
        {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB3270"));
            return;
        }
        
    	confirmarCierre =  true;
        
//         </CODIGO_DESARROLLADO>
    }

    public void aceptarConfirmacion() 
    {
        copiarEquivalentes = true;
        confirmarCierre = false;  
        ejecutarProceso();  
    }

    public void cancelarConfirmacion() 
    {
        copiarEquivalentes = false;
        confirmarCierre = false;  
        ejecutarProceso();  
    }

    private void ejecutarProceso()
    {
        try
        {
            if (copiarEquivalentes) {
               
                ejbPrepararAno.prepararAnoSiguiente(
                    compania, 
                    anoDestino, 
                    anoFuente,
                    compania
                );
                
                
                archivoDescarga = JsfUtil.getArchivoDescarga(
                    JsfUtil.serializarPlano(ejbPrepararAno.obtenerEquivalencias(
                        compania, 
                        anoDestino, 
                        anoFuente,
                        compania)),
                    "Reporte_Equivalencias_Presupuestales.txt");
                
            } else {
                
                ejbPrepararAno.prepararAnoSiguiente(
                    compania, 
                    anoDestino, 
                    anoFuente,
                    compania
                );
            }
            
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB506"));
        }
        catch (SystemException | JRException | IOException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    public void oprimirVerificar(ActionEvent ac)
    {
    	archivoDescarga = null;
    	
        // <CODIGO_DESARROLLADO>
        if (anoFuente > anoDestino)
        {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB3270"));
            return;
        }
        try
        {
        	String txt = ejbPrepararAno.validarRefyCC(compania,anoFuente,anoDestino);
        	txt = txt.replace("\\n", System.lineSeparator());
        	archivoDescarga = JsfUtil.getArchivoDescarga(
                    JsfUtil.serializarPlano(txt,"UTF-8"),
                    "Inconsistencias.txt");

        }
        catch (SystemException | JRException | IOException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public int getAnoFuente()
    {
        return anoFuente;
    }

    public void setAnoFuente(int anoFuente)
    {
        this.anoFuente = anoFuente;
    }

    public int getAnoDestino()
    {
        return anoDestino;
    }

    public void setAnoDestino(int anoDestino)
    {
        this.anoDestino = anoDestino;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

	/**
	 * @return the confirmarCierre
	 */
	public boolean isConfirmarCierre() {
		return confirmarCierre;
	}

	/**
	 * @param confirmarCierre the confirmarCierre to set
	 */
	public void setConfirmarCierre(boolean confirmarCierre) {
		this.confirmarCierre = confirmarCierre;
	}
    
}

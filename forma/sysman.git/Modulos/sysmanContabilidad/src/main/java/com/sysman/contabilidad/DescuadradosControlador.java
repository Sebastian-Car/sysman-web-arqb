package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.ejb.EjbContabilidadTresRemote;
import com.sysman.contabilidad.enums.DescuadradosControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author otorres
 * @version 1, 12/04/2016
 * @modificado_por acaceres
 * @fecha_modificacion 25/07/2016
 * @modified jguerrero
 * @version 2. 06/04/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Además se ajustaron los errores del sonar
 * 
 * @author ybecerra
 * @version 3, 12/06/2017 Implementacion al llamado de
 * GeneralCodigoFormaEnum, para el codigo del formulario
 * 
 */
@ManagedBean
@ViewScoped

public class DescuadradosControlador extends BeanBaseModal
{
    private final String compania;
    private final String modulo;
    private String anio;
    private int mesInicial;
    private int mesFinal;
    private StreamedContent archivoDescarga;
    private List<Registro> listaAno;
    private boolean centroCosto;

    @EJB
    private EjbContabilidadTresRemote ejbContabilidadTres;

    /**
     * Creates a new instance of DescuadradosControlador
     */
    public DescuadradosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.DESCUADRADOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(DescuadradosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void init()
    {
        anio = Integer.toString(SysmanFunciones.ano(new Date()));
        mesInicial = Integer.parseInt(
                        Integer.toString(SysmanFunciones.mes(new Date())));
        mesFinal = Integer.parseInt(
                        Integer.toString(SysmanFunciones.mes(new Date())));
        cargarListaAno();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        /*
         * FR625-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore formularioAbrir 1, Me.Name End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaAno()
    {

        try
        {

            Map<String, Object> params = new TreeMap<>();
            params.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DescuadradosControladorUrlEnum.URL3096
                                                                            .getValue())
                                            .getUrl(), params));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirAceptar()
    {
        // <CODIGO_DESARROLLADO>
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.PDF);

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMesInicial()
    {
        // <CODIGO_DESARROLLADO>
        mesFinal = 0;
        // </CODIGO_DESARROLLADO>
    }

    public void generaInforme(ReportesBean.FORMATOS formato)
    {
    	String respuesta = null;
    	String[] datos = null;
    	String informe = "";
        if (mesInicial <= mesFinal)
        {
            try
            {
            	respuesta = ejbContabilidadTres
            			.verificarInconsistenciasCuentasContables(
            					compania,
            					Integer.parseInt(anio));

            	datos = respuesta.split(Pattern.quote("$"));
            	
            	if (centroCosto) {
                    informe = "002970DescuadradoCentroCosto"; 
                } else {
                	informe = "000624IDescuadrado"; 
                }
            	

            		Map<String, Object> parametros = new HashMap<>();
            		HashMap<String, Object> reemplazar = new HashMap<>();
            		reemplazar.put("compania", compania);
            		reemplazar.put("anio", anio);
            		reemplazar.put("mesInicial", mesInicial);
            		reemplazar.put("mesFinal", mesFinal);
            		parametros.put("PR_ANIO", anio);
            		parametros.put("PR_MES_INICIAL",
            				SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mesInicial]);
            		parametros.put("PR_MES_FINAL",
            				SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mesFinal]);
            		parametros.put("PR_CUENTAS_CON_MOV_MAL", datos[0]);
            		parametros.put("PR_CUENTAS_CON_MOVIMIENTO", datos[1]);
            		parametros.put("PR_LONGITUD_MAL", datos[2]);
            		parametros.put("PR_CUENTAS_MAYORES_POR_CREAR", datos[3]);
            		parametros.put("ID_CODIGO_DIFERENTE", datos[4]);

            		Reporteador.resuelveConsulta(informe,
            				Integer.parseInt(modulo), reemplazar,
            				parametros);
            		archivoDescarga = JsfUtil.exportarStreamed(informe,parametros, ConectorPool.ESQUEMA_SYSMAN,  formato);
            	
            	} catch (JRException | IOException  | SysmanException | NumberFormatException | SystemException e) {
            		JsfUtil.agregarMensajeError(e.getMessage());
            		logger.error(e.getMessage(), e);
            	}
            	
            	try {
            	      	
            	if(archivoDescarga == null && !respuesta.equals("NINGUNO$NINGUNO$NINGUNO$NINGUNO$NINGUNO") ) {
            		
            		
            		StringBuilder inconsistencias = new StringBuilder();
					inconsistencias.append("****** RELACION DE INCONSISTENCIAS DEL PLAN CONTABLE ******").append("\r\n");
					inconsistencias.append("Cuentas con la estructura diferente a : 1-2-4-6: ").append(datos[0]).append("\r\n");
					inconsistencias.append("Cuentas con longitud menor a 6 y con movimiento: ").append(datos[1]).append("\r\n");
					inconsistencias.append("Cuentas que tienen subcuentas y  movimiento: ").append(datos[2]).append("\r\n");
					inconsistencias.append("Cuentas mayores que faltan en el plan contable: ").append(datos[3]).append("\r\n");
					inconsistencias.append("Cuentas con Id y código diferentes en el plan contable: ").append(datos[4]).append("\r\n");

            		ByteArrayInputStream streamTexto = JsfUtil.serializarPlano(inconsistencias.toString());
					archivoDescarga = JsfUtil.getArchivoDescarga(streamTexto,
							"Inconsistencias.txt");
            	} 
            }
            catch (JRException | IOException e)
            {
            	
            }

        }
        else
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB529"));
        }

    }

    /**
     * @return the archivoDescarga
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    /**
     * @param archivoDescarga
     * the archivoDescarga to set
     */
    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public String getAnio()
    {
        return anio;
    }

    public void setAnio(String anio)
    {
        this.anio = anio;
    }

    public int getMesInicial()
    {
        return mesInicial;
    }

    public void setMesInicial(int mesInicial)
    {
        this.mesInicial = mesInicial;
    }

    public int getMesFinal()
    {
        return mesFinal;
    }

    public void setMesFinal(int mesFinal)
    {
        this.mesFinal = mesFinal;
    }

    public List<Registro> getListaAno()
    {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno)
    {
        this.listaAno = listaAno;
    }
    
    public boolean isCentroCosto() {
            return centroCosto;
    }

    public void setCentroCosto(boolean centroCosto) {
            this.centroCosto = centroCosto;
    }
}

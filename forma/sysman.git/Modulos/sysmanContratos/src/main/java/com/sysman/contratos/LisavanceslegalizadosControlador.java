package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contratos.enums.LisavanceslegalizadosControladorUrlEnum;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Map;
import java.util.TreeMap;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ybecerra
 * @version 1, 23/09/2015
 * 
 * @author eamaya
 * @version 2.0, 09/08/2017 Proceso de Refactoring DSS cambio de
 * Sysdate por new Date() y cambio de numero de formulario por enum
 * 
 */
@ManagedBean
@ViewScoped

public class LisavanceslegalizadosControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private String estado;
    private String numeroInicial;
    private String numeroFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private RegistroDataModelImpl listaNumeroInicial;
    private RegistroDataModelImpl listaNumeroFinal;
    private StreamedContent archivoDescarga;
    private static final String NUMERO = "NUMERO";
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    /**
     * Creates a new instance of LisavanceslegalizadosControlador
     */
    public LisavanceslegalizadosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        try {
            numFormulario = GeneralCodigoFormaEnum.LISAVANCESLEGALIZADOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(LisavanceslegalizadosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
    	abrirFormulario();
        cargarListaNumeroInicial();
        cargarListaNumeroFinal();
        fechaInicial = new Date();
        fechaFinal = new Date();
       
    }

    public void cargarListaNumeroInicial() {
		try {
			
			
			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(LisavanceslegalizadosControladorUrlEnum.URL3064.getValue());
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			String parametroCondicion = SysmanFunciones.nvl(
					ejbSysmanUtil.consultarParametro(compania,"TIPOS CONTRATO AVANCE", modulo,
							new Date(), false),"").toString();

			String[] split = parametroCondicion.split(",");
			parametroCondicion = "";
			for (int i = 0; i < split.length; i++) {
				parametroCondicion = parametroCondicion + "" + split[i] + "";
				if (i + 1 == split.length) {
					break;
				} else {
					parametroCondicion = parametroCondicion + ",";
				}

			}
			param.put(GeneralParameterEnum.CLASEORDEN.getName(), parametroCondicion);
			//param.put(GeneralParameterEnum.CLASEORDEN.getName(), "RDA");
			listaNumeroInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
					true, NUMERO);
			
			
		} catch (SystemException e) {
			Logger.getLogger(LisavanceslegalizadosControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}
    }

    public void cargarListaNumeroFinal() {
		try {
			UrlBean urlBean = UrlServiceUtil.getInstance()
					.getUrlServiceByUrlByEnumID(LisavanceslegalizadosControladorUrlEnum.URL3737.getValue());
			Map<String, Object> param = new TreeMap<>();
			param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
			String parametroCondicion = SysmanFunciones.nvl(ejbSysmanUtil.consultarParametro(compania,"TIPOS CONTRATO AVANCE", modulo,
					new Date(), false),"").toString();

			String[] split = parametroCondicion.split(",");
			parametroCondicion = "";
			for (int i = 0; i < split.length; i++) {
				parametroCondicion = parametroCondicion + "" + split[i] + "";
				if (i + 1 == split.length) {
					break;
				} else {
					parametroCondicion = parametroCondicion + ",";
				}

			}
			param.put(GeneralParameterEnum.CLASEORDEN.getName(), parametroCondicion);
			//param.put(GeneralParameterEnum.CLASEORDEN.getName(), "RDA");
			param.put(GeneralParameterEnum.NUMERO.getName(), numeroInicial);

			listaNumeroFinal = new RegistroDataModelImpl(urlBean.getUrl(), 
					urlBean.getUrlConteo().getUrl(), param, true,
					NUMERO);
		} catch (SystemException e) {
			Logger.getLogger(LisavanceslegalizadosControlador.class.getName()).log(Level.SEVERE, null, e);
			JsfUtil.agregarMensajeError(e.getMessage());
		}

    }

    public void oprimircmdPantalla() {
        // <CODIGO_DESARROLLADO>
        generarReporte(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirComando96() {
        // <CODIGO_DESARROLLADO>
        generarReporte(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void generarReporte(ReportesBean.FORMATOS formato) {
        try {
            String condicion = "  AND ORDENDECOMPRA.NUMERO BETWEEN  "
                + numeroInicial + " AND " + numeroFinal + "\n";
            StringBuilder condicion2 = new StringBuilder();
            String parametroCondicion = SysmanFunciones.nvlStr(
						ejbSysmanUtil.consultarParametro(compania, "TIPOS CONTRATO AVANCE", SessionUtil.getModulo(), 
								new Date(), false), "");
			String[] split = parametroCondicion.split(",");
			parametroCondicion="";
             for (int i = 0; i < split.length; i++) {
            	 parametroCondicion=parametroCondicion +"'"+split[i]+"'";
            	 if (i+1==split.length) {
					break;
				}else {
					parametroCondicion=parametroCondicion +",";
				}
					
			}           
            
            condicion2.append(" AND ORDENDECOMPRA.CLASEORDEN in(").append(parametroCondicion).append(")");   
            
            
            String entreFechas = "Entre "
                + SysmanFunciones.convertirAFechaCadena(fechaInicial) + " y "
                + SysmanFunciones.convertirAFechaCadena(fechaFinal) + "";
            String entreNumeros = "Entre " + numeroInicial + " y " + numeroFinal
                + "";

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("condicion", condicion);
            reemplazar.put("condicionB", condicion2.toString());
            
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_ENTREFECHAS", entreFechas);
            parametros.put("PR_ENTRENUMEROS", entreNumeros);
            String strSql = Reporteador.resuelveConsulta("800504ContratosAvancesLegalizaciones",
            		Integer.parseInt(modulo), reemplazar);
            parametros.put("PR_STRSQL",strSql);
            archivoDescarga = JsfUtil.exportarStreamed(
                            "000246LisAvancesLegalizados", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (FileNotFoundException |SystemException ex) {

            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(LisavanceslegalizadosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

        }
        catch (ParseException | SysmanException | IOException
                        | JRException ex) {
            Logger.getLogger(LisavanceslegalizadosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

    }

    public void seleccionarFilaNumeroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        numeroInicial = registroAux.getCampos().get(NUMERO).toString();
        cargarListaNumeroFinal();
    }

    public void seleccionarFilaNumeroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        numeroFinal = registroAux.getCampos().get(NUMERO).toString();
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNumeroInicial() {
        return numeroInicial;
    }

    public void setNumeroInicial(String numeroInicial) {
        this.numeroInicial = numeroInicial;
    }

    public String getNumeroFinal() {
        return numeroFinal;
    }

    public void setNumeroFinal(String numeroFinal) {
        this.numeroFinal = numeroFinal;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public RegistroDataModelImpl getListaNumeroInicial() {
        return listaNumeroInicial;
    }

    public void setListaNumeroInicial(
        RegistroDataModelImpl listaNumeroInicial) {
        this.listaNumeroInicial = listaNumeroInicial;
    }

    public RegistroDataModelImpl getListaNumeroFinal() {
        return listaNumeroFinal;
    }

    public void setListaNumeroFinal(RegistroDataModelImpl listaNumeroFinal) {
        this.listaNumeroFinal = listaNumeroFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    @Override
    public void abrirFormulario() {
    	
    }
}

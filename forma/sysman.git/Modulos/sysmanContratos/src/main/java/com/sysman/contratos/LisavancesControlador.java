package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contratos.enums.LisavancesControladorEnum;
import com.sysman.contratos.enums.LisavancesControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
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
 * @author ybecerra
 * @version 1, 22/09/2015
 * 
 * @version 2, 10/08/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 */

@ManagedBean
@ViewScoped
public class LisavancesControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private String estado;
    private boolean avance;
    private String terceroInicial;
    private String terceroInicialNit;
    private String terceroFinal;
    private String terceroFinalNit;
    private Date fechaInicial;
    private Date fechaFinal;
    private RegistroDataModelImpl listaTerceroInicial;
    private RegistroDataModelImpl listaTerceroFinal;
    private StreamedContent archivoDescarga;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    /**
     * Creates a new instance of LisavancesControlador
     */
    public LisavancesControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.LISAVANCES_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(LisavancesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarListaTerceroInicial();
        cargarListaTerceroFinal();
        fechaInicial = new Date();
        fechaFinal = new Date();
        abrirFormulario();
    }

    public void cargarListaTerceroInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(LisavancesControladorUrlEnum.URL2760.getValue());     
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);

        listaTerceroInicial = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, "NIT");
    }

    public void cargarListaTerceroFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(LisavancesControladorUrlEnum.URL3239.getValue());     
        Map<String,Object> param = new TreeMap<>();
        param.put(LisavancesControladorEnum.PARAM1.getValue(),terceroInicialNit);
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);

        listaTerceroFinal = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, "NIT");
    }

    public void oprimircmdPantalla() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void generarReporte(ReportesBean.FORMATOS formato) {
        try { 
            StringBuilder condicion = new StringBuilder();
            condicion.append(" AND TERCERO.NIT BETWEEN  '").append(terceroInicialNit).append(
                "' AND '").append(terceroFinalNit).append("'");
            
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
            StringBuilder tercero = new StringBuilder();
            tercero.append("Entre ").append(terceroInicial).append(" y ").append(terceroFinal);
            
            StringBuilder encabezado = new StringBuilder();
            encabezado.append("Entre ").append(
                SysmanFunciones.convertirAFechaCadena(fechaInicial)).append(" y ").append(
                SysmanFunciones.convertirAFechaCadena(fechaFinal));
           
            
            
            if (avance) {
            	HashMap<String, Object> reemplazar = new HashMap<>();
                reemplazar.put("fechaInicial",
                                SysmanFunciones.formatearFecha(fechaInicial));
                reemplazar.put("fechaFinal",
                                SysmanFunciones.formatearFecha(fechaFinal));
                reemplazar.put("condicion", condicion.toString());
                Map<String, Object> parametros = new HashMap<>();
                // MANEJO DE PARAMETROS DEL REPORTE

                parametros.put("PR_TERCERO", tercero.toString());
                parametros.put("PR_ENCABEZADO", encabezado.toString());

                Reporteador.resuelveConsulta("000242LisAvances",
                                Integer.parseInt(modulo), reemplazar, parametros);
                archivoDescarga = JsfUtil.exportarStreamed("000242LisAvances",
                                parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
			}else {
				HashMap<String, Object> reemplazar = new HashMap<>();
	            reemplazar.put("fechaInicial",
	                            SysmanFunciones.formatearFecha(fechaInicial));
	            reemplazar.put("fechaFinal",
	                            SysmanFunciones.formatearFecha(fechaFinal));
	            reemplazar.put("condicion", condicion.toString());
	            reemplazar.put("condicionB", condicion2.toString());
	            Map<String, Object> parametros = new HashMap<>();
	            // MANEJO DE PARAMETROS DEL REPORTE
	            String strSql = Reporteador.resuelveConsulta("800504ContratosAvancesLegalizaciones",
	            		Integer.parseInt(modulo), reemplazar);
	            
	            parametros.put("PR_TERCERO", tercero.toString());
	            parametros.put("PR_ENCABEZADO", encabezado.toString());
	            parametros.put("PR_STRSQL",strSql);
	            archivoDescarga = JsfUtil.exportarStreamed("000242LisAvances",
	                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
			}
				
            
            

            

        }
        catch (FileNotFoundException | SystemException ex) {
            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            idioma.getString(Constantes.MSM_INFORME_NO_EXISTE),ex.getMessage()));
            Logger.getLogger(LisavancesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (ParseException | SysmanException | IOException
                        | JRException ex) {
            Logger.getLogger(LisavancesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }

    }

    public void oprimircmdExcel() {
        // <CODIGO_DESARROLLADO>
        generarReporte(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaTerceroInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroInicial = registroAux.getCampos().get("NOMBRE").toString();
        terceroInicialNit = registroAux.getCampos().get("NIT").toString();
        cargarListaTerceroFinal();
    }

    public void seleccionarFilaTerceroFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        terceroFinal = registroAux.getCampos().get("NOMBRE").toString();
        terceroFinalNit = registroAux.getCampos().get("NIT").toString();

    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTerceroInicial() {
        return terceroInicial;
    }

    public void setTerceroInicial(String terceroInicial) {
        this.terceroInicial = terceroInicial;
    }

    public String getTerceroFinal() {
        return terceroFinal;
    }

    public void setTerceroFinal(String terceroFinal) {
        this.terceroFinal = terceroFinal;
    }

    public String getTerceroInicialNit() {
        return terceroInicialNit;
    }

    public void setTerceroInicialNit(String terceroInicialNit) {
        this.terceroInicialNit = terceroInicialNit;
    }

    public String getTerceroFinalNit() {
        return terceroFinalNit;
    }

    public void setTerceroFinalNit(String terceroFinalNit) {
        this.terceroFinalNit = terceroFinalNit;
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

    public RegistroDataModelImpl getListaTerceroInicial() {
        return listaTerceroInicial;
    }

    public void setListaTerceroInicial(RegistroDataModelImpl listaTerceroInicial) {
        this.listaTerceroInicial = listaTerceroInicial;
    }

    public RegistroDataModelImpl getListaTerceroFinal() {
        return listaTerceroFinal;
    }

    public void setListaTerceroFinal(RegistroDataModelImpl listaTerceroFinal) {
        this.listaTerceroFinal = listaTerceroFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

	public boolean getAvance() {
		return avance;
	}

	public void setAvance(boolean avance) {
		this.avance = avance;
	}

}

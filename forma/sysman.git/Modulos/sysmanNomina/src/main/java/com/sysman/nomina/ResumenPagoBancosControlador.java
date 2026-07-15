package com.sysman.nomina;

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
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.ResumenPagoBancosControladorEnum;
import com.sysman.nomina.enums.ResumenPagoBancosControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.sesion.SessionBean;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
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

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dsuesca
 * @version 1, 24/08/2015
 * 
 * @author jcrodriguez,Refactoring y depuracion
 * @version 2, 26/10/2017
 */
@ManagedBean
@ViewScoped

public class ResumenPagoBancosControlador extends BeanBaseModal
{

    private final String compania;
    private final String modulo;
    private String ano1;
    private String mes1;
    private String periodo1;
    private List<Registro> listaAno1;
    private List<Registro> listaMes1;
    private List<Registro> listaPeriodo1;
    private String anioSession;
    private String mesSession;
    private String periodoSession;
    private StreamedContent archivoDescarga;
    

	@EJB
	private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of ResumenPagoBancosControlador
     */
    public ResumenPagoBancosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
    	modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.RESUMEN_PAGO_BANCOS_CONTROLADOR.getCodigo();
            validarPermisos();
            anioSession = SessionUtil
                            .getSessionVar("anioNomina").toString();
            mesSession = SessionUtil
                            .getSessionVar("mesNomina").toString();
            periodoSession = SessionUtil
                            .getSessionVar("periodoNomina").toString();
            ano1 = anioSession;
            mes1 = mesSession;
            periodo1 = periodoSession;
        }
        catch (Exception ex)
        {
            Logger.getLogger(ResumenPagoBancosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        cargarListaAno1();
        cargarListaMes1();
        cargarListaPeriodo1();
        abrirFormulario();
    }

    public void cargarListaAno1()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaAno1 = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(ResumenPagoBancosControladorUrlEnum.URL3367.getValue()).getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaMes1()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.ANO.getName(), ano1);
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaMes1 = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(ResumenPagoBancosControladorUrlEnum.URL4125.getValue()).getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaPeriodo1()
    {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano1);
        param.put(GeneralParameterEnum.MES.getName(), mes1);
        param.put(ResumenPagoBancosControladorEnum.ID_DE_PROCESO.getValue(), SessionUtil
                        .getSessionVar("procesoNomina").toString());

        try
        {
            listaPeriodo1 = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(ResumenPagoBancosControladorUrlEnum.URL4127.getValue()).getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirPDF()
    {
        getInforme(FORMATOS.PDF);
    }

    public void oprimirEXCEL()
    {
        getInforme(FORMATOS.EXCEL97);
    }

    public void getInforme(FORMATOS formato)
    {
        archivoDescarga = null;
        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();

            reemplazar.put("ano1", ano1);
            reemplazar.put("mes1", mes1);
            reemplazar.put("periodo1", periodo1);

            Map<String, Object> parametros = new HashMap<>();

            String jefeDesarrolloHumano = ejbSysmanUtil.consultarParametro(compania,
                    "NOMBRE JEFE DESARROLLO HUMANO", modulo, new Date(), false);
            String cargoJefeDesarrolloHumano = ejbSysmanUtil.consultarParametro(compania,
                    "CARGO JEFE DESARROLLO HUMANO", modulo, new Date(), false); 
            String jefeNomina = ejbSysmanUtil.consultarParametro(compania,
                    "NOMBRE JEFE NOMINA", modulo, new Date(), false);
            String cargoResponsableNomina = ejbSysmanUtil.consultarParametro(compania,
                    "CARGO RESPONSABLE DE NOMINA", modulo, new Date(), false);
            
            parametros.put("PR_NOMBRE_JEFE_DESARROLLO_HUMANO", jefeDesarrolloHumano);
            parametros.put("PR_CARGO_JEFE_DESARROLLO_HUMANO", cargoJefeDesarrolloHumano);
            parametros.put("PR_NOMBRE_JEFE_NOMINA", jefeNomina);
            parametros.put("PR_CARGO_RESPONSABLE_DE_NOMINA", cargoResponsableNomina);
            
            parametros.put("PR_NOMBREEMPRESA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            
            // IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
            parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
            // FIN IMPLEMENTACION MARCA_BLANCA
            
            String strsql = Reporteador.resuelveConsulta(ResumenPagoBancosControladorEnum.REPORTE000179.getValue(), Integer.parseInt(modulo), reemplazar);
            
            String nombreInforme = "";
            
            //Por lo general será 000179ResumenPagoBancos, pero para IDIPRON es 002125RESUMEPAGOBANCOSIDI
            nombreInforme = SysmanFunciones.nvlStr(ejbSysmanUtil.consultarParametro(compania,
					"FORMATO RESUMEN PAGO BANCOS", modulo, new Date(), false),
            		ResumenPagoBancosControladorEnum.REPORTE000179.getValue());
            parametros.put("PR_STRSQL", strsql);
            archivoDescarga = JsfUtil.exportarStreamed(nombreInforme, parametros, ConectorPool.ESQUEMA_SYSMAN,
					formato);

        }
        catch (FileNotFoundException | SystemException ex)
        {
            JsfUtil.agregarMensajeInformativo(
                            SysmanFunciones.concatenar(idioma.getString("MSM_INFORME_NO_EXISTE"), " ", ex.getMessage(), " ",
                                            ResumenPagoBancosControladorEnum.REPORTE000179.getValue()));
            Logger.getLogger(ResumenPagoBancosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (JRException | IOException ex)
        {
            JsfUtil.agregarMensajeError(SysmanFunciones.concatenar(idioma.getString("MSM_TRANS_INTERRUMPIDA"),
                            ex.getMessage()));
            Logger.getLogger(ResumenPagoBancosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cambiarAno1()
    {
        cargarListaMes1();
        cargarListaPeriodo1();
    }

    public void cambiarMes1()
    {
        cargarListaPeriodo1();
    }

    public void cambiarPeriodo1()
    {
        // heredado del bean base
    }

    @Override
    public void abrirFormulario()
    {
        // heredado del bean base
    }

    public String getAno1()
    {
        return ano1;
    }

    public void setAno1(String ano1)
    {
        this.ano1 = ano1;
    }

    public String getMes1()
    {
        return mes1;
    }

    public void setMes1(String mes1)
    {
        this.mes1 = mes1;
    }

    public String getPeriodo1()
    {
        return periodo1;
    }

    public void setPeriodo1(String periodo1)
    {
        this.periodo1 = periodo1;
    }

    public List<Registro> getListaAno1()
    {
        return listaAno1;
    }

    public void setListaAno1(List<Registro> listaAno1)
    {
        this.listaAno1 = listaAno1;
    }

    public List<Registro> getListaMes1()
    {
        return listaMes1;
    }

    public void setListaMes1(List<Registro> listaMes1)
    {
        this.listaMes1 = listaMes1;
    }

    public List<Registro> getListaPeriodo1()
    {
        return listaPeriodo1;
    }

    public void setListaPeriodo1(List<Registro> listaPeriodo1)
    {
        this.listaPeriodo1 = listaPeriodo1;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

}

package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.ListadoparafiscalescentrodecostosControladorEnum;
import com.sysman.nomina.enums.ListadoparafiscalescentrodecostosControladorUrlEnum;
import com.sysman.nomina.enums.volantesDePagoControladorEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanConstantes;
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
 * @author ybecerra
 * @version 1, 28/12/2015
 * @modified spina 23/03/2017 Depuracion sonar - se eliminan
 * parametros en los metodos oprimir*
 * 
 * @author asana
 * @version 2, 10/10/2017 Se realiza refactoring de controlador.
 * 
 */
@ManagedBean
@ViewScoped
public class ListadoparafiscalescentrodecostosControlador
                extends BeanBaseModal
{

    private final String compania;
    private final String modulo;
    private final String mees;
    private final String anio;
    private final String procesoNomina;
    private String ano;
    private String mes;
    private String proceso;
    private StreamedContent archivoDescarga;
    private List<Registro> listaAno1;
    private List<Registro> listaMes1;
    private List<Registro> listaProceso;
    
    private String headerEspecial;
    private String sticker;
    
    @EJB
    private EjbSysmanUtil ejbSysmanUtil; 
    

    /**
     * Creates a new instance of
     * ListadoparafiscalescentrodecostosControlador
     */
    public ListadoparafiscalescentrodecostosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        mees = (String) SessionUtil.getSessionVar("mesNomina");
        anio = (String) SessionUtil.getSessionVar("anioNomina");
        procesoNomina = (String) SessionUtil.getSessionVar("procesoNomina");
        try
        {
            numFormulario = GeneralCodigoFormaEnum.LISTADOPARAFISCALESCENTRODECOSTOS_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(ListadoparafiscalescentrodecostosControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        cargarListaAno1();
        cargarListaMes1();
        cargarListaProceso();
        abrirFormulario();
    }

    public void cargarListaAno1()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ListadoparafiscalescentrodecostosControladorEnum.PARAM1.getValue(), procesoNomina);

        try
        {
            listaAno1 = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(ListadoparafiscalescentrodecostosControladorUrlEnum.URL3084.getValue()).getUrl(),
                            param));
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
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(ListadoparafiscalescentrodecostosControladorEnum.PARAM2.getValue(), anio);
        param.put(ListadoparafiscalescentrodecostosControladorEnum.PARAM4.getValue(), procesoNomina);

        try
        {
            listaMes1 = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(ListadoparafiscalescentrodecostosControladorUrlEnum.URL3738.getValue()).getUrl(),
                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaProceso()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaProceso = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(
                                                            ListadoparafiscalescentrodecostosControladorUrlEnum.URL4547.getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirPresentar()
    {
        // <CODIGO_DESARROLLADO>

        generarInforme(ReportesBean.FORMATOS.PDF);

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        generarInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }
    
    private String getReporte() {

        String parReporte = "";
		try {
			parReporte = ejbSysmanUtil.consultarParametro(compania,
				        "FORMATO DE PARAFISCALES CENTROS DE COSTO",
				        SessionUtil.getModulo(), new Date(), false);
		} catch (SystemException e) {
			e.printStackTrace();
		}

		
        if (SysmanFunciones.validarVariableVacio(parReporte)) {
        	parReporte = ListadoparafiscalescentrodecostosControladorEnum.PARAM3.getValue();
            return  parReporte;
        }
        else {
            return parReporte;
        }
        
    }

    public void generarInforme(ReportesBean.FORMATOS formato)
    {

        try
        {
            archivoDescarga = null;
            String parReporte = getReporte();

            HashMap<String, Object> remplazar = new HashMap<>();
            try {
				headerEspecial = ejbSysmanUtil.consultarParametro(compania,
				        "FORMATOS ESPECIALES BUCARAMANGA", modulo,
				        new Date(),
				        true);
				
				
			} catch (SystemException e) {
	            logger.error(e.getMessage(), e);
	            JsfUtil.agregarMensajeError(e.getMessage());
			}            
            
            sticker = SessionUtil.getCompaniaIngreso().getRutaSticker();
            
            remplazar.put("proceso", proceso);
            remplazar.put("anio", ano);
            remplazar.put("mes", mes);

            String strsql = Reporteador.resuelveConsulta(parReporte,
                            Integer.parseInt(modulo), remplazar);

            Map<String, Object> parametros = new HashMap<>();
            
            try {
				String nombreFuncionario1 =  ejbSysmanUtil.consultarParametro(compania,
				        "NOMBRE FUNCIONARIO 1 RESPONSABLE DE NOMINA",
				        SessionUtil.getModulo(), new Date(), false);
	            String cargoFuncionario1 =  ejbSysmanUtil.consultarParametro(compania,
	                    "CARGO FUNCIONARIO 1 RESPONSABLE DE NOMINA",
	                    SessionUtil.getModulo(), new Date(), false);
	            
	            String nombreFuncionario2 =  ejbSysmanUtil.consultarParametro(compania,
	                    "NOMBRE FUNCIONARIO 2 RESPONSABLE DE NOMINA",
	                    SessionUtil.getModulo(), new Date(), false);
	            
	            String cargoFuncionario2 =  ejbSysmanUtil.consultarParametro(compania,
	                    "CARGO FUNCIONARIO 2 RESPONSABLE DE NOMINA",
	                    SessionUtil.getModulo(), new Date(), false);
			
            



            parametros.put("PR_NOMBRE_FUNCIONARIO_1", nombreFuncionario1);
            parametros.put("PR_CARGO_FUNCIONARIO_1", cargoFuncionario1);
            parametros.put("PR_NOMBRE_FUNCIONARIO_2", nombreFuncionario2);
            parametros.put("PR_CARGO_FUNCIONARIO_2", cargoFuncionario2);
            parametros.put("PR_STRSQL", strsql);
            parametros.put("PR_NOMBREEMPRESA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NOMBREMES",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mes)]);
            parametros.put("PR_ANIO", ano);
            parametros.put("PR_GETUSER", SessionUtil.getUser().getNombre1());
            
            parametros.put("PR_HEADER_ESPECIAL", headerEspecial.equals("SI")?true:false);
            
            parametros.put("PR_IMAGEN_ESPECIAL", sticker);
            

            archivoDescarga = JsfUtil.exportarStreamed(parReporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
            } catch (SystemException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        catch (FileNotFoundException ex)
        {
            JsfUtil.agregarMensajeError(
                            SysmanFunciones.concatenar(idioma.getString(Constantes.MSM_INFORME_NO_EXISTE), " ", ex.getMessage()));
            Logger.getLogger(ListadoparafiscalescentrodecostosControlador.class
                            .getName()).log(Level.SEVERE, null, ex);

        }
        catch (IOException | JRException ex)
        {
            JsfUtil.agregarMensajeError(
                            SysmanFunciones.concatenar(idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA), " ", ex.getMessage()));
            Logger.getLogger(ListadoparafiscalescentrodecostosControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        catch (SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>

        proceso = "0";
        ano = anio;
        cargarListaAno1();
        mes = mees;
        cargarListaMes1();
        // </CODIGO_DESARROLLADO>
    }

    public String getAno()
    {
        return ano;
    }

    public void setAno(String ano)
    {
        this.ano = ano;
    }

    public String getMes()
    {
        return mes;
    }

    public void setMes(String mes)
    {
        this.mes = mes;
    }

    public String getProceso()
    {
        return proceso;
    }

    public void setProceso(String proceso)
    {
        this.proceso = proceso;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
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

    public List<Registro> getListaProceso()
    {
        return listaProceso;
    }

    public void setListaProceso(List<Registro> listaProceso)
    {
        this.listaProceso = listaProceso;
    }

    public void cambiarProceso()
    {
        cargarListaAno1();
        cargarListaMes1();
        ano = null;
        mes = null;

    }

    public void cambiarAno1()
    {
        cargarListaMes1();
        mes = null;
    }

    @Override
    public int getNumFormulario()
    {
        return numFormulario;
    }

	public String getHeaderEspecial() {
		return headerEspecial;
	}

	public void setHeaderEspecial(String headerEspecial) {
		this.headerEspecial = headerEspecial;
	}

	public String getSticker() {
		return sticker;
	}

	public void setSticker(String sticker) {
		this.sticker = sticker;
	}
    
}

package com.sysman.almacen;

import com.sysman.almacen.enums.ListaInventarioExisControladorEnum;
import com.sysman.almacen.enums.ListaInventarioExisControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
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
 * @author apineda
 * @version 1, 03/02/2016
 * 
 * @author eamaya
 * @version 2, 04/05/2017 Proceso de Refactoring, Manejo de EJBs y Correcciones SonarLint
 * 
 * @author spina - refactorizo conexiones
 * @version 3, 12/06/2017
 */
@ManagedBean
@ViewScoped

public class ListaInventarioExisControlador extends BeanBaseModal
{

    private final String compania;
    private final String modulo;
    private String elementoDesde;
    private String elementoHasta;
    private String nombreDesde;
    private String nombreHasta;
    private String parametroDigitos;
    private boolean especial;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listacmbElementoDesde;
    private RegistroDataModelImpl listacmbElementoHasta;
    private boolean parametroEspecial;
    private boolean conFecha;
    private Date fecha;

    @EJB
    private EjbSysmanUtilRemote ejbParametro;

    /**
     * Creates a new instance of ListaInventarioExisControlador
     */
    public ListaInventarioExisControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        fecha = new Date();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.LISTA_INVENTARIO_EXIS_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(ListaInventarioExisControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void init()
    {
        cargarListacmbElementoDesde();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        try
        {
        	conFecha = false;
        	
        	parametroDigitos = ejbParametro.consultarParametro(compania,
                            "DIGITOS AGRUPACION INVENTARIO", modulo, new Date(),
                            false);
            
            parametroEspecial = "SI".equals(SysmanFunciones.nvl(ejbParametro.consultarParametro(compania,
								"GENERAR INFORME ESPECIAL EXISTENCIAS INVENTARIO",modulo,new Date(),true),"NO"));
            
            if (parametroEspecial) {
            	conFecha = true;
            }
            
        }
        catch (SystemException ex)
        {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(ListaInventarioExisControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    public void cargarListacmbElementoDesde()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListaInventarioExisControladorUrlEnum.URL3278
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listacmbElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGOELEMENTO.getName());
    }

    public void cargarListacmbElementoHasta()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ListaInventarioExisControladorUrlEnum.URL4093
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(ListaInventarioExisControladorEnum.PARAM0.getValue(),
                        elementoDesde);

        listacmbElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGOELEMENTO.getName());
    }

    public void oprimirPresentar()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaInforme(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>

    }

    public void generaInforme(ReportesBean.FORMATOS formato)
    {
        String parametroPeps = "NO";
        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            String strSQL;
            String informe;
            reemplazar.put("elementoDesde", elementoDesde);
            reemplazar.put("elementoHasta", elementoHasta);

            parametroPeps = ejbParametro.consultarParametro(compania,
                            "MANEJA PEPS EN CONSUMO ALMACEN", modulo,
                            new Date(), false);
            String fechaC = ejbParametro.consultarParametro(compania,
                    "FECHA DE CORTE PARA INICIO DEL ALMACEN", modulo,
                    new Date(), false);
    		Date fechaCorteAlm = SysmanFunciones.convertirAFecha(fechaC,"dd/MM/yyyy");
    		String fechaF = SysmanFunciones.convertirAFechaCadena(fecha);
    		Date fechaFinal = SysmanFunciones.convertirAFecha(fechaF,"dd/MM/yyyy");
    		
            if (("SI").equals(parametroPeps))
            {
                if (especial)
                {
                    reemplazar.put("digitosinventario", parametroDigitos);
                    informe = "000513IInventarioExisEspPEPS";
                }
                else
                {
                    informe = "000510IInventarioExisPEPS";
                }
            }
            else
            {
                if (especial)
                {
                	if (parametroEspecial) {
                		
                		reemplazar.put("fechaCorte",SysmanFunciones.formatearFecha(fechaCorteAlm));
                		reemplazar.put("fechaFinal",SysmanFunciones.formatearFecha(fechaFinal));
                		informe = "002911ExistenciasInventarioEsp";
                		
                    }else {
                    	reemplazar.put("digitosinventario", parametroDigitos);
                        informe = "000517IInventarioExisEsp";
                    }
                }
                else
                {
                    informe = "000515IInventarioExis";
                }
            }
            strSQL = Reporteador.resuelveConsulta(informe,
                            Integer.parseInt(modulo), reemplazar);
            parametros.put("PR_STRSQL", strSQL);
            archivoDescarga = JsfUtil.exportarStreamed(informe, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | SystemException | ParseException ex)
        {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(ListaInventarioExisControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    public void seleccionarFilacmbElementoDesde(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        elementoDesde = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGOELEMENTO
                                                        .getName()),
                                        "")
                        .toString();

        nombreDesde = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRELARGO"), "")
                        .toString();

        elementoHasta = null;
        nombreHasta = null;
        cargarListacmbElementoHasta();
    }

    public void seleccionarFilacmbElementoHasta(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        elementoHasta = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGOELEMENTO
                                                        .getName()),
                                        "")
                        .toString();

        nombreHasta = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRELARGO"), "")
                        .toString();
    }

    public String getParametroDigitos()
    {
        return parametroDigitos;
    }

    public void setParametroDigitos(String parametroDigitos)
    {
        this.parametroDigitos = parametroDigitos;
    }

    public boolean isEspecial()
    {
        return especial;
    }

    public void setEspecial(boolean especial)
    {
        this.especial = especial;
    }

    public String getElementoDesde()
    {
        return elementoDesde;
    }

    public void setElementoDesde(String elementoDesde)
    {
        this.elementoDesde = elementoDesde;
    }

    public String getElementoHasta()
    {
        return elementoHasta;
    }

    public void setElementoHasta(String elementoHasta)
    {
        this.elementoHasta = elementoHasta;
    }

    public String getNombreDesde()
    {
        return nombreDesde;
    }

    public void setNombreDesde(String nombreDesde)
    {
        this.nombreDesde = nombreDesde;
    }

    public String getNombreHasta()
    {
        return nombreHasta;
    }

    public void setNombreHasta(String nombreHasta)
    {
        this.nombreHasta = nombreHasta;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListacmbElementoDesde()
    {
        return listacmbElementoDesde;
    }

    public void setListacmbElementoDesde(
        RegistroDataModelImpl listacmbElementoDesde)
    {
        this.listacmbElementoDesde = listacmbElementoDesde;
    }

    public RegistroDataModelImpl getListacmbElementoHasta()
    {
        return listacmbElementoHasta;
    }

    public void setListacmbElementoHasta(
        RegistroDataModelImpl listacmbElementoHasta)
    {
        this.listacmbElementoHasta = listacmbElementoHasta;
    }
    
    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    
    public boolean getConFecha() {
        return conFecha;
    }

    public void setConFecha(boolean conFecha) {
    	this.conFecha = conFecha;
    }

}

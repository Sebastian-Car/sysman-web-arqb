package com.sysman.almacen;

import com.sysman.almacen.enums.InvgraldevolurControladorEnum;
import com.sysman.almacen.enums.InvgraldevolurControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
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
 * @author acaceres
 * @version 1, 28/01/2016
 * 
 * @author eamaya
 * @version 2, 03/05/2017 Proceso de Refactoring y Manejo de EJBs
 * 
 * @author ybecerra
 * @version 3, 13/06/2017 Implementacion al llamado de GeneralCodigoFormaEnum, para el codigo del formulario
 */
@ManagedBean
@ViewScoped

public class InvgraldevolurControlador extends BeanBaseModal
{

    private final String compania;
    private final String modulo;
    private String cmbElementoDesde;
    private String cmbElementoHasta;
    private String responsableInicial;
    private String responsableFinal;
    private String desde;
    private Date corte;
    private String nombreElementoDesde;
    private String nombreElementoHasta;
    private String nombreResponsableInicial;
    private String nombreResponsableFinal;
    private String agrupacion;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listacmbElementoDesde;
    private RegistroDataModelImpl listacmbElementoHasta;
    private RegistroDataModelImpl listaResponsableInicial;
    private RegistroDataModelImpl listaResponsableFinal;
    private final String cedulaCons;

    @EJB
    private EjbSysmanUtilRemote ejbParametro;

    /**
     * Creates a new instance of InvgraldevolurControlador
     */
    public InvgraldevolurControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cedulaCons = "CEDULA";

        try
        {
            numFormulario = GeneralCodigoFormaEnum.INVGRALDEVOLUR_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(InvgraldevolurControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar()
    {
        cargarListacmbElementoDesde();
        cargarListaResponsableInicial();
        corte = new Date();
        abrirFormulario();
    }

    public void cargarListacmbElementoDesde()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InvgraldevolurControladorUrlEnum.URL3302
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(InvgraldevolurControladorEnum.PARAM0.getValue(),
                        "D,N,M");

        listacmbElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGOELEMENTO.getName());
    }

    public void cargarListacmbElementoHasta()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InvgraldevolurControladorUrlEnum.URL4170
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(InvgraldevolurControladorEnum.PARAM0.getValue(),
                        "D,N,M");

        param.put(InvgraldevolurControladorEnum.PARAM1.getValue(),
                        cmbElementoDesde);

        listacmbElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGOELEMENTO.getName());
    }

    public void cargarListaResponsableInicial()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InvgraldevolurControladorUrlEnum.URL5185
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaResponsableInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cedulaCons);
    }

    public void cargarListaResponsableFinal()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InvgraldevolurControladorUrlEnum.URL6258
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(InvgraldevolurControladorEnum.PARAM2.getValue(),
                        responsableInicial);

        listaResponsableFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cedulaCons);
    }

    public void oprimirpresentar()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if ((corte != null) && !cmbElementoHasta.isEmpty()
                        && !responsableInicial.isEmpty() && !responsableFinal.isEmpty())
        {
            obtenerReporte(FORMATOS.PDF);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if ((corte != null) && !cmbElementoHasta.isEmpty()
                        && !responsableInicial.isEmpty() && !responsableFinal.isEmpty())
        {
            obtenerReporte(FORMATOS.EXCEL97);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void obtenerReporte(FORMATOS formatos)
    {
        String reporte = "000496IInvGralDevoluRDCC";
        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            String cargoOA = ejbParametro.consultarParametro(compania,
                            "CARGO ORDENADOR ALMACEN", modulo, new Date(),
                            false);

            String jefeAlmacen = ejbParametro.consultarParametro(compania,
                            "FIRMA JEFE ALMACEN", modulo, new Date(), false);
            
            String formatosUspec = SysmanFunciones.nvl(ejbParametro.consultarParametro(compania,
                    		"FORMATOS UNICOS USPEC", modulo, new Date(), false), "NO").toString();
            
            String ajusteInfInv = SysmanFunciones.nvl(ejbParametro.consultarParametro(compania,
            				"AJUSTE INFORME INVENTARIO INDIVIDUAL DE BIENES DEVOLUTIVOS", modulo, 
            				new Date(), false), "NO").toString();
            
            boolean verSupervisor = SysmanFunciones.nvl(ejbParametro.consultarParametro(compania,
    				"MANEJA RESPONSABLE Y CONTRATISTA EN DEVOLUTIVOS", modulo, 
    				new Date(), false), "NO").equals("SI");

            reemplazar.put("cargoOrdenadorAlmacen",
                            cargoOA == null ? "''" : cargoOA);
            reemplazar.put("cmbElementoDesde", cmbElementoDesde);
            reemplazar.put("cmbElementoHasta", cmbElementoHasta);
            reemplazar.put("responsableInicial", responsableInicial);
            reemplazar.put("responsableFinal", responsableFinal);
            reemplazar.put("corte", SysmanFunciones.formatearFecha(corte));
            // MANEJO DE PARAMETROS DE REEMPLAZO

            // MANEJO DE PARAMETROS DEL REPORTE
            String strSql = Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo), reemplazar);
            parametros.put("PR_STRSQL", strSql);
            parametros.put("PR_COMPANIA", compania);
            parametros.put("PR_VER_SUPERVISOR", verSupervisor);

            parametros.put("PR_FORMS_INVGRALDEVOLUR_CORTE",
                            SysmanFunciones.convertirAFechaCadena(corte,
                                            "EEEEE, dd MMMMM yyyy"));

            if (cargoOA == null)
            {
                parametros.put("PR_FIRMAS_ORDENADOR", false);
            }
            else
            {
                parametros.put("PR_FIRMAS_ORDENADOR", true);
            }

            if (jefeAlmacen == null)
            {
                parametros.put("PR_FIRMAS_JEFE", false);
                parametros.put("PR_JEFE_ALMACEN", "");
            }
            else
            {
                parametros.put("PR_FIRMAS_JEFE", true);
                parametros.put("PR_JEFE_ALMACEN", jefeAlmacen);
            }

            if ("SI".equals(formatosUspec)) {
            	parametros.put("PR_FORMATOS_USPEC", true);
            } else {
            	parametros.put("PR_FORMATOS_USPEC", false);
            }
            
            if ("SI".equals(ajusteInfInv)) {
            	parametros.put("PR_AJUSTEINVIND", true);
            } else {
            	parametros.put("PR_AJUSTEINVIND", false);
            }
            
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (FileNotFoundException e)
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_INFORME_VAR_NO_EXISTE")
                                            .replace("s$reporte$s", reporte)
                                            + e.getMessage());
            logger.error(e.getMessage(), e);
        }

        catch (ParseException | JRException | IOException | SysmanException
                        | SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    public void seleccionarFilacmbElementoDesde(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cmbElementoDesde = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGOELEMENTO
                                                        .getName()),
                                        "")
                        .toString();

        nombreElementoDesde = SysmanFunciones.nvl(registroAux.getCampos()
                        .get("NOMBRELARGO"), "").toString();

        cmbElementoHasta = null;
        nombreElementoHasta = null;
        cargarListacmbElementoHasta();
    }

    public void seleccionarFilacmbElementoHasta(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cmbElementoHasta = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGOELEMENTO
                                                        .getName()),
                                        "")
                        .toString();

        nombreElementoHasta = SysmanFunciones.nvl(registroAux.getCampos()
                        .get("NOMBRELARGO"), "").toString();
    }

    public void seleccionarFilaResponsableInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        responsableInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cedulaCons), "")
                        .toString();

        nombreResponsableInicial = SysmanFunciones.nvl(registroAux.getCampos()
                        .get("NOMBRE"), "").toString();

        responsableFinal = null;
        nombreResponsableFinal = null;
        cargarListaResponsableFinal();
    }

    public void seleccionarFilaResponsableFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        responsableFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cedulaCons), "")
                        .toString();

        nombreResponsableFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
    }

    public String getCmbElementoDesde()
    {
        return cmbElementoDesde;
    }

    public void setCmbElementoDesde(String cmbElementoDesde)
    {
        this.cmbElementoDesde = cmbElementoDesde;
    }

    public String getCmbElementoHasta()
    {
        return cmbElementoHasta;
    }

    public void setCmbElementoHasta(String cmbElementoHasta)
    {
        this.cmbElementoHasta = cmbElementoHasta;
    }

    public String getResponsableInicial()
    {
        return responsableInicial;
    }

    public void setResponsableInicial(String responsableInicial)
    {
        this.responsableInicial = responsableInicial;
    }

    public String getResponsableFinal()
    {
        return responsableFinal;
    }

    public void setResponsableFinal(String responsableFinal)
    {
        this.responsableFinal = responsableFinal;
    }

    public String getDesde()
    {
        return desde;
    }

    public void setDesde(String desde)
    {
        this.desde = desde;
    }

    public Date getCorte()
    {
        return corte;
    }

    public void setCorte(Date corte)
    {
        this.corte = corte;
    }

    public String getNombreElementoDesde()
    {
        return nombreElementoDesde;
    }

    public void setNombreElementoDesde(String nombreElementoDesde)
    {
        this.nombreElementoDesde = nombreElementoDesde;
    }

    public String getNombreElementoHasta()
    {
        return nombreElementoHasta;
    }

    public void setNombreElementoHasta(String nombreElementoHasta)
    {
        this.nombreElementoHasta = nombreElementoHasta;
    }

    public String getNombreResponsableInicial()
    {
        return nombreResponsableInicial;
    }

    public void setNombreResponsableInicial(String nombreResponsableInicial)
    {
        this.nombreResponsableInicial = nombreResponsableInicial;
    }

    public String getNombreResponsableFinal()
    {
        return nombreResponsableFinal;
    }

    public void setNombreResponsableFinal(String nombreResponsableFinal)
    {
        this.nombreResponsableFinal = nombreResponsableFinal;
    }

    public String getAgrupacion()
    {
        return agrupacion;
    }

    public void setAgrupacion(String agrupacion)
    {
        this.agrupacion = agrupacion;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
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

    public RegistroDataModelImpl getListaResponsableInicial()
    {
        return listaResponsableInicial;
    }

    public void setListaResponsableInicial(
                    RegistroDataModelImpl listaResponsableInicial)
    {
        this.listaResponsableInicial = listaResponsableInicial;
    }

    public RegistroDataModelImpl getListaResponsableFinal()
    {
        return listaResponsableFinal;
    }

    public void setListaResponsableFinal(
                    RegistroDataModelImpl listaResponsableFinal)
    {
        this.listaResponsableFinal = listaResponsableFinal;
    }

    @Override
    public void abrirFormulario()
    {
        // Metodo heredado de la clase BaseBean
    }

}

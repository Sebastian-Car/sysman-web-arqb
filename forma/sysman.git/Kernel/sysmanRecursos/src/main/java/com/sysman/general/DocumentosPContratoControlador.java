package com.sysman.general;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.general.enums.DocumentosPContratoControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.context.RequestContext;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 04/12/2015
 * @Modifier amonroy
 * @version 2, 04/04/2017 Proceso de Refactoring y Revision de Buenas
 * Practicas sugeridas porla herramientaSonarLint
 * 
 * @version 3.0, 12/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.<br>
 * Manejo de EJBs.<br>
 * Reemplazo de texto quemado por etiquetas de properties.
 */
@ManagedBean
@ViewScoped
public class DocumentosPContratoControlador extends BeanBaseContinuoAcmeImpl
{

    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del modulo desde
     * el cual el usuario abre el formulario
     */
    private String modulo;
    private String numeroOrden;
    private String claseOrden;
    private StreamedContent archivoDescarga;

    /**
     * Parametro Clase Orden
     */
    private static final String PARAMETRO_CLASE_ORDEN = "claseOrden";
    /**
     * Parametro Numero Orden
     */
    private static final String PARAMETRO_NUMERO_ORDEN = "numeroOrden";

    // <DECLARAR_EJBs>
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

	private Map<String, Object> parametroswf;
    // </DECLARAR_EJBs>

    /**
     * Crea una instancia de DocumentosPContratoControlador
     */
    public DocumentosPContratoControlador()
    {
        super();
        compania = SessionUtil.getCompania();

        try
        {
        	parametroswf = (Map<String,Object>) SessionUtil.getSessionVarContainer("parametroswf");
        	if(parametroswf != null) {
        		SessionUtil.setSessionVar("modulo", "9");
        	}
        	modulo = SessionUtil.getModulo();
        	numFormulario = GeneralCodigoFormaEnum.DOCUMENTOS_PCONTRATO_CONTROLADOR
        			.getCodigo();
        	validarPermisos();

        	Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null)
            {
                claseOrden = parametrosEntrada.get(PARAMETRO_CLASE_ORDEN)
                                .toString();
                numeroOrden = parametrosEntrada.get(PARAMETRO_NUMERO_ORDEN)
                                .toString();
            }

        }
        catch (SysmanException | NamingException ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.DOCUMENTOS;

        buscarLlave();
        registro = new Registro(new HashMap<String, Object>());
        abrirFormulario();
    }

    public String getNumeroOrden()
    {
        return numeroOrden;
    }

    public void setNumeroOrden(String numeroOrden)
    {
        this.numeroOrden = numeroOrden;
    }

    public String getClaseOrden()
    {
        return claseOrden;
    }

    public void setClaseOrden(String claseOrden)
    {
        this.claseOrden = claseOrden;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void oprimirINFORME()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("compania", compania);
            reemplazar.put(PARAMETRO_CLASE_ORDEN, claseOrden);
            reemplazar.put(PARAMETRO_NUMERO_ORDEN, numeroOrden);

            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            String reporte = "000417RELACIONDOCUMENTOS";
            String subReporte1 = "000418DocumentosEntregados";
            String subReporte2 = "000419DocumentosPendientes";

            // MANEJO DE PARAMETROS DEL REPORTE
            String strSql = Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);

            String strSql2 = Reporteador.resuelveConsulta(subReporte1,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);

            String strSql3 = Reporteador.resuelveConsulta(subReporte2,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar);

            String jefeAreaContratacion = ejbSysmanUtil.consultarParametro(
                            compania,
                            "NOMBRE JEFE AREA DE CONTRATACION", modulo,
                            new Date(), true);

            String nombreOficinaContratacion = ejbSysmanUtil.consultarParametro(
                            compania,
                            "NOMBRE DE LA OFICINA DE CONTRATACION", modulo,
                            new Date(), true);

            String areaContratacion = ejbSysmanUtil.consultarParametro(compania,
                            idioma.getString("TB_TB3208"), modulo,
                            new Date(), true);

            parametros.put("PR_STRSQL", strSql);
            parametros.put("PR_COMPANIA", compania);
            parametros.put("PR_NOMBRE_JEFE_AREA_DE_CONTRATACION",
                            jefeAreaContratacion);

            parametros.put("PR_NOMBRE_DE_LA_OFICINA_DE_CONTRATACION",
                            nombreOficinaContratacion);

            parametros.put("PR_AREA_DE_CONTRATACI�N", areaContratacion);
            parametros.put("PR_STRSQL_DOCENTREGADO", strSql2);
            parametros.put("PR_STRSQL_DOCPENDIENTES", strSql3);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            Date hoy = new Date();

            parametros.put("PR_DADOEN",
                            idioma.getString("TB_TB3209").replace("#CIUDAD#",
                                            SessionUtil.getCompaniaIngreso()
                                                            .getCiudad())
                                            .replace("#DIA#",
                                                            Integer.toString(
                                                                            SysmanFunciones
                                                                                            .getParteFecha(hoy,
                                                                                                            Calendar.DAY_OF_MONTH)))
                                            .replace("#MES#",
                                                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[SysmanFunciones
                                                                            .getParteFecha(hoy,
                                                                                            Calendar.MONTH)])
                                            .replace("#ANIO#", Integer.toString(
                                                            SysmanFunciones.getParteFecha(
                                                                            hoy,
                                                                            Calendar.YEAR))));

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.PDF);
        }
        catch (JRException | IOException | SysmanException
                        | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1766"));
        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimircheck()
    {
        // <CODIGO_DESARROLLADO>

        archivoDescarga = null;

        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("compania", compania);
        reemplazar.put(PARAMETRO_CLASE_ORDEN, claseOrden);
        reemplazar.put(PARAMETRO_NUMERO_ORDEN, numeroOrden);

        // MANEJO DE PARAMETROS DE REEMPLAZO
        Map<String, Object> parametros = new HashMap<>();
        String reporte = "000420CHECKLIST";
        // MANEJO DE PARAMETROS DEL REPORTE
        String strSql = Reporteador.resuelveConsulta(reporte,
                        Integer.parseInt(modulo),
                        reemplazar);

        parametros.put("PR_STRSQL", strSql);

        try
        {
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.PDF);
        }
        catch (JRException | IOException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB1766"));
        }

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        validarDocumentos();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event)
    {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.CREATED_BY.getName(),
                        SessionUtil.getUser().getCodigo());
        registro.getCampos().put(GeneralParameterEnum.DATE_CREATED.getName(),
                        new Date());
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    public void cerrarFormulario()
    {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    @Override
    public void asignarValoresRegistro()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void removerCombos()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.CREATED_BY.getName());
        registro.getCampos()
                        .remove(GeneralParameterEnum.DATE_CREATED.getName());
        registro.getCampos().remove(GeneralParameterEnum.NOMBRE.getName());
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void reasignarOrigen()
    {
        // <CODIGO_DESARROLLADO>
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CLASEORDEN.getName(),
                        claseOrden);
        parametrosListado.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        numeroOrden);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Identifica si existen documentos asociados a la clase de orden
     * con la que se esta trabajando
     */
    private void validarDocumentos()
    {
        try
        {
            Map<String, Object> params = new TreeMap<>();
            params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            params.put(GeneralParameterEnum.CLASEORDEN.getName(), claseOrden);

            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DocumentosPContratoControladorUrlEnum.URL13888
                                                                            .getValue())
                                            .getUrl(), params));

            if (rs != null)
            {
                realizarInsercion();
                asignarOrigenOinsercion();
            }
            else
            {
                String msj = idioma.getString("TB_TB3072");
                msj = msj.replace("s$claseOrden$s", claseOrden);
                JsfUtil.agregarMensajeAlerta(msj);
                RequestContext.getCurrentInstance().closeDialog(null);
                reasignarOrigen();
            }

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Realiza la insercion en la tabla DOCUMENTOS de los documentos
     * que han sido asociados a la clase de orden que se esta
     * trabajando en la tabla CLASIFICA_DOCUMENTOS
     */
    private void realizarInsercion()
    {
        try
        {
            Map<String, Object> params = new TreeMap<>();
            params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            params.put(GeneralParameterEnum.CLASEORDEN.getName(), claseOrden);
            params.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                            numeroOrden);

            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            DocumentosPContratoControladorUrlEnum.URL27631
                                                            .getValue());

            Parameter parameter = new Parameter();
            parameter.setFields(params);
            requestManager.save(urlCreate.getUrl(),
                            urlCreate.getMetodo(),
                            parameter);

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Verifica si la consulta del origen de grilla trae datos , si no
     * realiza una insercion a la tabla DOCUMENTOS y llama el
     * reasignar origen
     */
    private void asignarOrigenOinsercion()
    {
        try
        {
            Map<String, Object> paramCount = new TreeMap<>();
            paramCount.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            paramCount.put(GeneralParameterEnum.CLASEORDEN.getName(),
                            claseOrden);
            paramCount.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                            numeroOrden);
            Registro rs = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            DocumentosPContratoControladorUrlEnum.URL27630
                                                                            .getValue())
                                            .getUrl(), paramCount));

            if (Integer.parseInt(rs.getCampos().get("TOTAL").toString()) != 0)
            {
                reasignarOrigen();
            }
            else
            {

                Map<String, Object> params = new TreeMap<>();
                params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                params.put(GeneralParameterEnum.CLASEORDEN.getName(),
                                claseOrden);
                params.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                                numeroOrden);

                Registro rsInsertar = RegistroConverter
                                .toRegistro(requestManager
                                                .get(UrlServiceUtil
                                                                .getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                DocumentosPContratoControladorUrlEnum.URL27661
                                                                                                .getValue())
                                                                .getUrl(),
                                                                params));
                rsInsertar.getCampos().put(
                                GeneralParameterEnum.CREATED_BY.getName(),
                                SessionUtil.getUser().getCodigo());
                rsInsertar.getCampos().put(
                                GeneralParameterEnum.DATE_CREATED.getName(),
                                new Date());

                UrlBean urlCreate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                GenericUrlEnum.DOCUMENTOS
                                                                .getCreateKey());
                requestManager.save(urlCreate.getUrl(),
                                urlCreate.getMetodo(),
                                rsInsertar.getCampos());

                reasignarOrigen();

            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

}
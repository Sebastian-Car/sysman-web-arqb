package com.sysman.almacen;

import com.sysman.almacen.enums.InvgraldevoluControladorEnum;
import com.sysman.almacen.enums.InvgraldevoluControladorUrlEnum;
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
 *
 * @author acaceres
 * @version 1, 27/01/2016
 *
 * -- Modificado por lcortes 03/05/2017. Refactorizacion de codigo de las listas para utilizar dss y se ajusta los llamados a funciones, procedimientos y metodos de la clase Acciones.
 *
 * -- Modificado por lcortes 10/05/2017. Modificacion reemplazo hasta a la consulta base 800108BaseUltimoMovimiento para las consultas de los reportes.
 */
@ManagedBean
@ViewScoped
public class InvgraldevoluControlador extends BeanBaseModal
{

    private final String compania;
    private final String modulo;
    private final String strCodElemento;
    private final String strFormato;
    private String cmbElementoDesde;
    private String cmbElementoHasta;
    private Date desde;
    private Date hasta;
    private String elementoDesdeNombre;
    private String elementoHastaNombre;
    private String agrupacion;
    private String digitosAgrupacion;
    private int opcion;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listacmbElementoDesde;
    private RegistroDataModelImpl listacmbElementoHasta;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of InvgraldevoluControlador
     */
    public InvgraldevoluControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        strCodElemento = "CODIGOELEMENTO";
        strFormato = "EEEEE, dd MMMMM yyyy";
        try
        {
            numFormulario = GeneralCodigoFormaEnum.INVGRALDEVOLU_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(InvgraldevoluControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        cargarListacmbElementoDesde();
        opcion = 2;
        desde = new Date();
        hasta = new Date();
        abrirFormulario();
    }

    public void cargarListacmbElementoDesde()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InvgraldevoluControladorUrlEnum.URL3250
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(InvgraldevoluControladorEnum.PARAM0.getValue(), "D,N,M");

        listacmbElementoDesde = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodElemento);
    }

    public void cargarListacmbElementoHasta()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InvgraldevoluControladorUrlEnum.URL4125
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(InvgraldevoluControladorEnum.PARAM0.getValue(), "D,N,M");
        param.put(InvgraldevoluControladorEnum.PARAM1.getValue(),
                        String.valueOf(cmbElementoDesde));

        listacmbElementoHasta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodElemento);
    }

    public void oprimirPresentar()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (!validarFechas())
        {
            return;
        }
        if (opcion == 2)
        {
            generarReporte(FORMATOS.PDF, "000475IInvGralDevolu");
        }
        else if (opcion == 3)
        {

            generarReporte(FORMATOS.PDF, "000487IInvGralDevoluAGr");

        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (!validarFechas())
        {
            return;
        }
        if (opcion == 2)
        {
            generarReporte(FORMATOS.EXCEL, "000475IInvGralDevolu");

        }
        else if (opcion == 3)
        {
            generarReporte(FORMATOS.EXCEL, "000487IInvGralDevoluAGr");

        }

        // </CODIGO_DESARROLLADO>
    }

    private boolean validarFechas()
    {
        if (desde.after(hasta))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB147"));
            hasta = null;
            return false;
        }
        return true;
    }

    public void generarReporte(FORMATOS formato, String reporte)
    {

        try
        {

            digitosAgrupacion = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "DIGITOS AGRUPACION INVENTARIO",
                                            modulo, new Date(),
                                            true), "3");

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("hasta", "'" +
                            SysmanFunciones.convertirAFechaCadena(hasta) + "' ");
            reemplazar.put("cmbElementoDesde", cmbElementoDesde);
            reemplazar.put("cmbElementoHasta", cmbElementoHasta);
            reemplazar.put("digitosAgrupacion", digitosAgrupacion);
            reemplazar.put("ultimoMov", Reporteador.resuelveConsulta(
                            "800108BaseUltimoMovimiento",
                            Integer.parseInt(modulo), reemplazar));
            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            // MANEJO DE PARAMETROS DEL REPORTE
            String strSql = Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(modulo), reemplazar);
            parametros.put("PR_STRSQL", strSql);
            parametros.put("PR_CIUDADCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getCiudad());
            parametros.put("PR_FORMS_INVGRALDEVOLU_DESDE",
                            SysmanFunciones.convertirAFechaCadena(desde,
                                            strFormato));
            parametros.put("PR_FORMS_INVGRALDEVOLU_HASTA",
                            SysmanFunciones.convertirAFechaCadena(hasta,
                                            strFormato));
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex)
        {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_INFORME_NO_EXISTE") + " " + ex.getMessage() + " " + reporte);
            Logger.getLogger(InvgraldevoluControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (SystemException
                        | ParseException | OutOfMemoryError | IOException
                        | JRException e)
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA")
                                            + e.getMessage());
            Logger.getLogger(InvgraldevoluControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        catch (SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void seleccionarFilacmbElementoDesde(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cmbElementoDesde = registroAux.getCampos().get(strCodElemento)
                        .toString();
        elementoDesdeNombre = registroAux.getCampos().get("NOMBRELARGO")
                        .toString();
        cmbElementoHasta = null;
        elementoHastaNombre = null;
        cargarListacmbElementoHasta();
    }

    public void seleccionarFilacmbElementoHasta(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cmbElementoHasta = registroAux.getCampos().get(strCodElemento)
                        .toString();
        elementoHastaNombre = registroAux.getCampos().get("NOMBRELARGO")
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

    public Date getDesde()
    {
        return desde;
    }

    public void setDesde(Date desde)
    {
        this.desde = desde;
    }

    public Date getHasta()
    {
        return hasta;
    }

    public void setHasta(Date hasta)
    {
        this.hasta = hasta;
    }

    public String getElementoDesdeNombre()
    {
        return elementoDesdeNombre;
    }

    public void setElementoDesdeNombre(String elementoDesdeNombre)
    {
        this.elementoDesdeNombre = elementoDesdeNombre;
    }

    public String getElementoHastaNombre()
    {
        return elementoHastaNombre;
    }

    public void setElementoHastaNombre(String elementoHastaNombre)
    {
        this.elementoHastaNombre = elementoHastaNombre;
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

    public int getOpcion()
    {
        return opcion;
    }

    public void setOpcion(int opcion)
    {
        this.opcion = opcion;
    }

    public String getDigitosAgrupacion()
    {
        return digitosAgrupacion;
    }

    public void setDigitosAgrupacion(String digitosAgrupacion)
    {
        this.digitosAgrupacion = digitosAgrupacion;
    }

    @Override
    public void abrirFormulario()
    {
        // Metodo generado por herencia de la clase padre
    }

}

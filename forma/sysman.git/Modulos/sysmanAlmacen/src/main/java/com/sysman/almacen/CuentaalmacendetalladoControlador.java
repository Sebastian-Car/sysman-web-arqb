package com.sysman.almacen;

import com.sysman.almacen.enums.CuentaalmacendetalladoControladorEnum;
import com.sysman.almacen.enums.CuentaalmacendetalladoControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
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

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author NGOMEZ
 * @version 1, 15/04/2016
 * @version 2, 25/04/2017 modificado por jcrodriguez
 * descripcion:--depuracion del controlador --creacion de dss
 *
 * -- Modificado por lcortes 13/06/2017. Se reemplaza el valor del
 * atributo numFormulario por el enumerado correspondiente y se
 * suprimen las conexiones a la base de datos.
 * 
 * @author jcrodriguez, Se eliminan las concatenaciones y se llama a
 * la funcion de sysman funciones concatenar, se adiciona el metodo
 * validarCadenaCampo, creacion de enums.
 * @version 3, 18/09/2017 modificado por jcrodriguez
 * 
 */
@ManagedBean
@ViewScoped
public class CuentaalmacendetalladoControlador extends BeanBaseModal
{
    /**
     * variable que almacena la compania sel
     */
    private String companiaSel;
    /**
     * variable que almacena la fecha inicial
     */
    private String fechaInicial;
    /**
     * variable que almacena la fecha final
     */
    private String fechaFinal;
    /**
     * variable que almacena el archivo de descarga
     */
    private StreamedContent archivoDescarga;

    /**
     * variable ejb
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of CuentaalmacendetalladoControlador
     */
    public CuentaalmacendetalladoControlador()
    {
        super();

        try
        {
            numFormulario = GeneralCodigoFormaEnum.CUENTAALMACENDETALLADO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null)
            {
                companiaSel = validarCadenaCampo(parametrosEntrada, "companiaSel");
                fechaInicial = validarCadenaCampo(parametrosEntrada, "fechaInicial");
                fechaFinal = validarCadenaCampo(parametrosEntrada, "fechaFinal");
            }
        }
        catch (Exception ex)
        {
            Logger.getLogger(CuentaalmacendetalladoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally
        {
            SessionUtil.cleanFlash();
        }
    }

    /**
     * metodo que se llama al inicializar el formulario
     */
    @PostConstruct
    public void inicializar()
    {
        abrirFormulario();
    }

    /**
     * metodo que se llama al abrir el formulario
     */
    @Override
    public void abrirFormulario()
    {
        // METODO NO IMPLEMENTADO
    }

    /**
     * metodo que se llama al oprimir el boton pdf
     */
    public void oprimirPresentar()
    {
        genInforme(ReportesBean.FORMATOS.PDF);
    }

    /**
     * metodo que se llama al oprimir el boton excel
     */
    public void oprimirExcel()
    {
        genInforme(ReportesBean.FORMATOS.EXCEL);
    }

    /**
     * metodo para obtener un parametro
     */
    private String getParametro(String nombreParametro, boolean isMayMin)
    {
        try
        {
            return ejbSysmanUtil.consultarParametro(companiaSel,
                            nombreParametro, SessionUtil.getModulo(),
                            new Date(), isMayMin);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return null;
    }

    private String validarCadenaCampo(Map<String, Object> campos, String var)
    {
        return SysmanFunciones.validarCampoVacio(campos, var) ? "" : campos.get(var).toString();
    }

    /**
     * metodo que contiene la logica para genera los reportes en
     * formato pdf y excel
     *
     * @param formato
     */
    public void genInforme(ReportesBean.FORMATOS formato)
    {
        archivoDescarga = null;
        UrlBean url = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(CuentaalmacendetalladoControladorUrlEnum.URL3223.getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(CuentaalmacendetalladoControladorEnum.COMPANIASELL.getValue(), companiaSel);
        try
        {
            Registro regAux = RegistroConverter.toRegistro(
                            requestManager.get(url.getUrl(), param));
            String titulo = SysmanFunciones.concatenar(GeneralParameterEnum.ENTIDAD.getName(), ": ",
                            validarCadenaCampo(regAux.getCampos(), GeneralParameterEnum.NOMBRE.getName()), " ",
                            CuentaalmacendetalladoControladorEnum.NIT.getValue(), ": ",
                            validarCadenaCampo(regAux.getCampos(), CuentaalmacendetalladoControladorEnum.NITCOMPANIA.getValue()),
                            " Ciudad: ",
                            validarCadenaCampo(regAux.getCampos(), GeneralParameterEnum.CIUDAD.getName()),
                            idioma.getString(CuentaalmacendetalladoControladorEnum.TG_DIRECCION4.getValue()),
                            validarCadenaCampo(regAux.getCampos(), GeneralParameterEnum.DIRECCION.getName()),
                            idioma.getString(CuentaalmacendetalladoControladorEnum.TG_TELEFONO3.getValue()),
                            validarCadenaCampo(regAux.getCampos(), GeneralParameterEnum.TELEFONO.getName()));

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("companiaSel", companiaSel);
            reemplazar.put("fechaInicial", fechaInicial);
            reemplazar.put("fechaFinal", fechaFinal);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_FORMS_F_CUENTAALMACEN_FECHAINICIAL",
                            fechaInicial);
            parametros.put("PR_FORMS_F_CUENTAALMACEN_FECHAFINAL", fechaFinal);
            parametros.put("PR_FORMS_F_CUENTAALMACEN_COMPANIA_COLUMN(1)",
                            regAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()));
            parametros.put("PR_TITULOCOMPANIA", titulo);
            parametros.put("PR_FORMS_F_CUENTAALMACEN_ENMILES", "");

            parametros.put("PR_CONTRALORIA_DEPARTAMENTAL",
                            SysmanFunciones.nvl(getParametro(
                                            "CONTRALORIA DEPARTAMENTAL", false),
                                            ""));
            parametros.put("PR_NOMBRE_GERENTE",
                            SysmanFunciones.nvl(getParametro("NOMBRE GERENTE",
                                            true),
                                            ""));
            parametros.put("PR_CARGO_GERENTE",
                            SysmanFunciones.nvl(
                                            getParametro("CARGO GERENTE", true),
                                            ""));
            parametros.put("PR_NOMBRE_RESPONSABLE_CONSUMO",
                            SysmanFunciones.nvl(getParametro(
                                            "NOMBRE RESPONSABLE CONSUMO", true),
                                            ""));
            parametros.put("PR_CARGO_COORDINADOR_ALMACEN",
                            SysmanFunciones.nvl(getParametro(
                                            "CARGO COORDINADOR ALMACEN", true),
                                            ""));

            Reporteador.resuelveConsulta(CuentaalmacendetalladoControladorEnum.REPORTE000641.getValue(),
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(CuentaalmacendetalladoControladorEnum.REPORTE000641.getValue(), parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex)
        {
            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            idioma.getString("MSM_INFORME_NO_EXISTE"), " ", ex.getMessage(), " ",
                            CuentaalmacendetalladoControladorEnum.REPORTE000641.getValue()));
            Logger.getLogger(CuentaalmacendetalladoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

        catch (JRException | IOException | SysmanException
                        | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que se llama cuanso para cargar un modal
     */
    public void cargarModal()
    {
        // heredado del bean base
    }

    /**
     * metodos get y set
     *
     * @return
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public String getCompaniaSel()
    {
        return companiaSel;
    }

    public void setCompaniaSel(String companiaSel)
    {
        this.companiaSel = companiaSel;
    }

    public String getFechaInicial()
    {
        return fechaInicial;
    }

    public void setFechaInicial(String fechaInicial)
    {
        this.fechaInicial = fechaInicial;
    }

    public String getFechaFinal()
    {
        return fechaFinal;
    }

    public void setFechaFinal(String fechaFinal)
    {
        this.fechaFinal = fechaFinal;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

}

package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.RelcuentascanceladasmensualControladorEnum;
import com.sysman.contabilidad.enums.RelcuentascanceladasmensualControladorUrlEnum;
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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

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
 * @author lcortes
 * @version 1, 20/05/2016
 * @version 2, 12/04/2017 modificado por jcrodriguez
 * descripcion:--creacion de dss para el combo sencillo --depuracion
 * del controlador
 * @version 3, 21/04/2017--mzanguna, cambio Ejb.
 * 
 * @author ybecerra
 * @version 4, 12/06/2017 Implementacion al llamado de
 * GeneralCodigoFormaEnum, para el codigo del formulario
 */
@ManagedBean
@ViewScoped
public class RelcuentascanceladasmensualControlador extends BeanBaseModal
{
    /**
     * variable que almacena la compa�ia
     */
    private final String compania;
    private final String modulo;
    /**
     * variable que almacena el mes
     */
    private String mes;
    /**
     * variable que almacena el a�o
     */
    private String anio;
    /**
     * variable que almacena el archivo de descarga
     */
    private StreamedContent archivoDescarga;
    /**
     * lista los a�os
     */
    private List<Registro> listaAno;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of
     * RelcuentascanceladasmensualControlador
     */
    public RelcuentascanceladasmensualControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.RELCUENTASCANCELADASMENSUAL_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(RelcuentascanceladasmensualControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    /**
     * metodo que se llama al inicial el formulario
     */
    @PostConstruct
    public void init()
    {
        cargarListaAno();
        abrirFormulario();
    }

    /**
     * metod que se llama al abrir el formulario
     */
    @Override
    public void abrirFormulario()
    {
        anio = Integer.toString(SysmanFunciones.ano(new Date()));
        mes = Integer.toString(SysmanFunciones.mes(new Date()));
    }

    /**
     * metodo que carga la lista de a�os
     */
    public void cargarListaAno()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaAno = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            RelcuentascanceladasmensualControladorUrlEnum.URL3284
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que se llama al oprimir el boton pdf
     */
    public void oprimirImprimir()
    {
        archivoDescarga = null;
        if (!SysmanFunciones.validarVariableVacio(anio)
            || !SysmanFunciones.validarVariableVacio(mes))
        {
            generarInforme(ReportesBean.FORMATOS.PDF);
        }
        else
        {
            JsfUtil.agregarMensajeAlerta(idioma
                            .getString(RelcuentascanceladasmensualControladorEnum.IDIOMA1
                                            .getValue()));
            return;
        }

    }

    /**
     * metodo que se llama al oprimir el boton excel
     */
    public void oprimirExcel()
    {
        archivoDescarga = null;
        if (!SysmanFunciones.validarVariableVacio(anio)
            || !SysmanFunciones.validarVariableVacio(mes))
        {
            generarInforme(ReportesBean.FORMATOS.EXCEL97);
        }
        else
        {
            JsfUtil.agregarMensajeAlerta(idioma
                            .getString(RelcuentascanceladasmensualControladorEnum.IDIOMA1
                                            .getValue()));
            return;
        }

    }

    /**
     * metodo que contiene la logia para genera los reportes en
     * formato pdf y excel
     *
     * @param formato
     */
    public void generarInforme(ReportesBean.FORMATOS formato)
    {
        try
        {
            HashMap<String, Object> reemplazos = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            // Reemplazos valores consulta reporte
            reemplazos.put(RelcuentascanceladasmensualControladorEnum.MES
                            .getValue(), mes);
            reemplazos.put(RelcuentascanceladasmensualControladorEnum.ANIO
                            .getValue(), anio);
            // Inicio Par�metros Informe
            parametros.put(RelcuentascanceladasmensualControladorEnum.PR_NOMBRECOMPANIA
                            .getValue(),
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put(RelcuentascanceladasmensualControladorEnum.PR_DEPARTAMENTOCOMPANIA
                            .getValue(),
                            SessionUtil.getCompaniaIngreso().getDepartamento());
            parametros.put(RelcuentascanceladasmensualControladorEnum.PR_COMPANIA
                            .getValue(), compania);

            parametros.put(RelcuentascanceladasmensualControladorEnum.PR_TITULO1_CUENTAS_CANCELADAS
                            .getValue(),
                            SysmanFunciones.nvl(ejbSysmanUtil
                                            .consultarParametro(compania,
                                                            RelcuentascanceladasmensualControladorEnum.TITULO1_CUENTAS_CANCELADAS
                                                                            .getValue(),
                                                            modulo, new Date(),
                                                            true),
                                            " "));

            Reporteador.resuelveConsulta(
                            RelcuentascanceladasmensualControladorEnum.NOMBREINFORME
                                            .getValue(),
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazos,
                            parametros);

            archivoDescarga = JsfUtil
                            .exportarStreamed(
                                            RelcuentascanceladasmensualControladorEnum.NOMBREINFORME
                                                            .getValue(),
                                            parametros,
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            formato);
        }
        catch (JRException | IOException | SysmanException | SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * metodos get y set
     *
     * @return
     */
    public String getMes()
    {
        return mes;
    }

    public void setMes(String mes)
    {
        this.mes = mes;
    }

    public String getAnio()
    {
        return anio;
    }

    public void setAnio(String anio)
    {
        this.anio = anio;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaAno()
    {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno)
    {
        this.listaAno = listaAno;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}

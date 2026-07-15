package com.sysman.almacen;

import com.sysman.almacen.enums.FichaTecnicaClasificacionContableUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author OTORRES
 * @version 1, 19/02/2016
 * 
 * @author jlramirez
 * @version 2, 27/04/2017, Se realizo refactoring
 * 
 * @author spina - refactorizo conexiones
 * @version 3, 12/06/2017
 */
@ManagedBean
@ViewScoped
public class FichaTecnicaClasificacionContable extends BeanBaseModal
{

    private final String compania;
    private final String modulo;
    private String clasificacion;
    private String nombreClasificacion;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listaCODIGO;

    /**
     * Creates a new instance of FichaTecnicaClasificacionContable
     */
    public FichaTecnicaClasificacionContable()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FICHA_TECNICA_CLASIFICACION_CONTABLE.getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(FichaTecnicaClasificacionContable.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar()
    {

        cargarListaCODIGO();
        abrirFormulario();
    }

    public void cargarListaCODIGO()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FichaTecnicaClasificacionContableUrlEnum.URL2436
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCODIGO = new RegistroDataModelImpl(urlBean.getUrl(),
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
        generaInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void generaInforme(ReportesBean.FORMATOS formato)
    {
        String extension = null;
        if (formato == ReportesBean.FORMATOS.PDF)
        {
            extension = ".pdf";
        }
        else
        {
            extension = ".xls";
        }
        String[] nombresArchivos = { "FichaTecnicaVias" + extension,
                                     "FichaTecnicaPredios" + extension };
        ByteArrayInputStream fichaTecnicaVias = null;
        ByteArrayInputStream fichaTecnicaPredios = null;
        Map<String, Object> parametrosVias = new HashMap<>();
        try
        {
            Map<String, Object> parametrosPredios = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("compania", compania);
            reemplazar.put("condicion", " ");
            reemplazar.put("condicionSub",
                            " WHERE ADICIONES.COMPANIA = '" + compania
                                + "' AND ADICIONES.ID_PREDIO = '"
                                + clasificacion + "' ");
            parametrosVias.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametrosPredios.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            Reporteador.resuelveConsulta("000542FICHATECNICAVIAS",
                            Integer.parseInt(modulo), reemplazar,
                            parametrosVias);
            Reporteador.resuelveConsulta("000543FICHATECNICAPREDIOS",
                            Integer.parseInt(modulo), reemplazar,
                            parametrosPredios);
            fichaTecnicaVias = JsfUtil.serializarReporte(
                            "000542FICHATECNICAVIAS", parametrosVias,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
            fichaTecnicaPredios = JsfUtil.serializarReporte(
                            "000543FICHATECNICAPREDIOS", parametrosPredios,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
            ByteArrayInputStream[] salidas = new ByteArrayInputStream[2];
            salidas[0] = fichaTecnicaVias;
            salidas[1] = fichaTecnicaPredios;
            archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(salidas,
                            nombresArchivos);
        }
        catch (SQLException | JRException | IOException
                        | DRException | SysmanException ex)
        {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(FichaTecnicaClasificacionContable.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    public void seleccionarFilaCODIGO(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        clasificacion = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(GeneralParameterEnum.CODIGOELEMENTO
                                                        .getName()),
                                        "")
                        .toString();
        nombreClasificacion = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRELARGO"), "")
                        .toString();
    }

    public String getClasificacion()
    {
        return clasificacion;
    }

    public void setClasificacion(String clasificacion)
    {
        this.clasificacion = clasificacion;
    }

    public String getNombreClasificacion()
    {
        return nombreClasificacion;
    }

    public void setNombreClasificacion(String nombreClasificacion)
    {
        this.nombreClasificacion = nombreClasificacion;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListaCODIGO()
    {
        return listaCODIGO;
    }

    public void setListaCODIGO(RegistroDataModelImpl listaCODIGO)
    {
        this.listaCODIGO = listaCODIGO;
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

}

package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.PersonalCentroCostoControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author acaceres
 * @version 1, 30/07/2015
 *
 * @author spina
 * @version 2, 19/10/2017 - se refactoriza para dss, depuracion y ejbs
 *
 */
@ManagedBean
@ViewScoped
public class PersonalCentroCostoControlador extends BeanBaseModal
{
    /**
     * variable que almacena la compa�ia
     */
    private final String compania;
    /*
     * varible que almacena la opcion
     */
    private String opcion;
    /**
     * variable que almacena el centro de costo
     */
    private String centros;
    /**
     * variabla que alamcena el nombre de los centro de constos
     */
    private String nombreCentros;
    /**
     * lista que muestra los centro de costos
     */
    private RegistroDataModelImpl listaIDCentrodeCosto;
    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;

    private String ano;

    /**
     * Creates a new instance of PersonalCentroCostoControlador
     */
    public PersonalCentroCostoControlador()
    {
        super();

        numFormulario = GeneralCodigoFormaEnum.PERSONAL_CENTRO_COSTO_CONTROLADOR
                        .getCodigo();
        ano = SysmanFunciones.nvl(SessionUtil.getSessionVar("anioNomina"), "")
                        .toString();
        compania = SessionUtil.getCompania();
        try
        {
            validarPermisos();
        }
        catch (Exception ex)
        {
            SessionUtil.redireccionarMenuPermisos();
            Logger.getLogger(PersonalCentroCostoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void inicializar()
    {
        cargarListaIDCentrodeCosto();
        abrirFormulario();
    }

    /**
     * metodo que carga la lista de centro de constos
     */
    public void cargarListaIDCentrodeCosto()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PersonalCentroCostoControladorUrlEnum.URL28520
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaIDCentrodeCosto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * metodo llamado cuando se oprime el boton pdf
     *
     * @param ac
     */
    public void oprimirPresentar(ActionEvent ac)
    {
        // <CODIGO_DESARROLLADO>
        getReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * metodo llamado cuando se oprime el boton excel
     *
     * @param ac
     */
    public void oprimirExcel(ActionEvent ac)
    {
        // <CODIGO_DESARROLLADO>
        getReporte(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * metodo que genera el reporte en pdf o excel
     *
     * @param ac
     */
    public void getReporte(FORMATOS formatos)
    {
        archivoDescarga = null;
        if (SysmanFunciones.validarVariableVacio(centros)
            && "1".equals(opcion))
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TB_TB3734"));
            return;
        }
        StringBuilder condicion = new StringBuilder("");

        if ("1".equals(opcion))
        {
            condicion.append(SysmanFunciones.concatenar(
                            " AND PERSONAL.ID_CENTRO_DE_COSTO =", centros));
        }
        condicion.append(
                        " ORDER BY Personal.nombrecompleto");
        try
        {
            HashMap<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("compania", compania);
            reemplazar.put("condicion", condicion.toString());
            Reporteador.resuelveConsulta("000107PersonalCentrosdeCosto",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            parametros.put("PR_NOMBREEMPRESA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            archivoDescarga = JsfUtil.exportarStreamed(
                            "000107PersonalCentrosdeCosto", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);

        }
        catch (JRException | IOException | SysmanException ex)
        {
            Logger.getLogger(PersonalCentroCostoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * metodo que se llama cuando se cambia la opcion
     */
    public void cambiarOpcion()
    {
        // heredado del bean base
    }

    /**
     * metodo que se llama cuando se selecciona un centro de costo
     *
     * @param event
     */
    public void seleccionarFilaIDCentrodeCosto(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        centros = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
        nombreCentros = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()), "")
                        .toString();
    }

    public String getCentros()
    {
        return centros;
    }

    public String getOpcion()
    {
        return opcion;
    }

    public void setOpcion(String opcion)
    {
        this.opcion = opcion;
    }

    public void setCentros(String centros)
    {
        this.centros = centros;
    }

    public String getNombreCentros()
    {
        return nombreCentros;
    }

    public void setNombreCentros(String nombreCentros)
    {
        this.nombreCentros = nombreCentros;
    }

    public RegistroDataModelImpl getListaIDCentrodeCosto()
    {
        return listaIDCentrodeCosto;
    }

    public void setListaIDCentrodeCosto(
        RegistroDataModelImpl listaIDCentrodeCosto)
    {
        this.listaIDCentrodeCosto = listaIDCentrodeCosto;
    }

    @Override
    public void abrirFormulario()
    {
        opcion = "2";
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

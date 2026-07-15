package com.sysman.nomina;

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
import com.sysman.nomina.enums.PersonalCategoriaControladorEnum;
import com.sysman.nomina.enums.PersonalCategoriaControladorUrlEnum;
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

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 13/10/2015
 *
 * @version 2, 17/03/2017, <strong>pespitia</strong><br>
 *
 * @author spina
 * @version 3, 18/10/2017 - se refactoriza para dss, depuracion y ejbs
 *
 */
@ManagedBean
@ViewScoped
public class PersonalCategoriaControlador extends BeanBaseModal
{

    private final String compania;
    private String opcion;
    private String id;
    private String nombreCategoria;
    private String escalafon;
    private RegistroDataModelImpl listaCategoria;
    private String anio;
    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of PersonalCategoriaControlador
     */
    public PersonalCategoriaControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.PERSONAL_CATEGORIA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(PersonalCategoriaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        anio = SysmanFunciones.nvl(SessionUtil.getSessionVar("anioNomina"), "")
                        .toString();
        opcion = "2";
        cargarListaCategoria();
        abrirFormulario();
    }

    public void cargarListaCategoria()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PersonalCategoriaControladorUrlEnum.URL28520
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        listaCategoria = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        PersonalCategoriaControladorEnum.ID.getValue());
    }

    public void oprimirPresentar() throws JRException
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() throws JRException
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarOpcion()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    private void generarReporte(ReportesBean.FORMATOS formato)
    {
        String strWhere = "";

        if ("1".equals(opcion))
        {
            if (id == null)
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB2590"));
                return;
            }
            else
            {
                strWhere = SysmanFunciones.concatenar(
                                " AND PERSONAL.ID_DE_CATEGORIA = '", id, "' ");
            }
        }

        archivoDescarga = null;

        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("compania", compania);
            reemplazar.put("strWhere", strWhere);

            Map<String, Object> parametros = new HashMap<>();

            // MANEJO DE PARAMETROS DEL REPORTE
            Reporteador.resuelveConsulta("000126PersonalCategoria",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            parametros.put("PR_NOMBREEMPRESA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            archivoDescarga = JsfUtil.exportarStreamed(
                            "000126PersonalCategoria", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | SysmanException | IOException ex)
        {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(PersonalCategoriaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    public void seleccionarFilaCategoria(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        id = String.valueOf(registroAux.getCampos()
                        .get(PersonalCategoriaControladorEnum.ID.getValue()));
        nombreCategoria = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        "")
                        .toString();
        escalafon = SysmanFunciones
                        .nvl(registroAux.getCampos()
                                        .get(PersonalCategoriaControladorEnum.ESCALAFON
                                                        .getValue()),
                                        "")
                        .toString();
    }

    public String getOpcion()
    {
        return opcion;
    }

    public void setOpcion(String opcion)
    {
        this.opcion = opcion;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getNombreCategoria()
    {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria)
    {
        this.nombreCategoria = nombreCategoria;
    }

    public String getEscalafon()
    {
        return escalafon;
    }

    public void setEscalafon(String escalafon)
    {
        this.escalafon = escalafon;
    }

    public RegistroDataModelImpl getListaCategoria()
    {
        return listaCategoria;
    }

    public void setListaCategoria(RegistroDataModelImpl listaCategoria)
    {
        this.listaCategoria = listaCategoria;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }
}

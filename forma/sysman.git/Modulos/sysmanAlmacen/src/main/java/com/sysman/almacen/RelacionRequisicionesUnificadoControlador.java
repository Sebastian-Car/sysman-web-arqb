package com.sysman.almacen;

import com.sysman.almacen.enums.RelacionRequisicionesUnificadoControladorUrlEnum;
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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
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
 * @author jacelas
 * @version 1, 09/11/2015
 * 
 * @version 2, 08/05/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno de los combos.
 * 
 * @author spina - refactorizo conexiones
 * @version 3, 12/06/2017
 */

@ManagedBean
@ViewScoped
public class RelacionRequisicionesUnificadoControlador extends BeanBaseModal
{

    private final String compania;
    private final String modulo;
    private String opcion;
    private String suministro;
    private Date fechaInicial;
    private Date fechaFinal;
    private RegistroDataModelImpl listaSuministro;

    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of RelacionRequisicionesUnificadoControlador
     */
    public RelacionRequisicionesUnificadoControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.RELACION_REQUISICIONES_UNIFICADO_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(RelacionRequisicionesUnificadoControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    // COMENTARIO
    @PostConstruct
    public void inicializar()
    {
        fechaInicial = new Date();
        fechaFinal = new Date();
        opcion = "1";
        abrirFormulario();
        cargarListasuministro();

    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public void cargarListasuministro()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(RelacionRequisicionesUnificadoControladorUrlEnum.URL3853.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.FECHAINICIAL.getName(), fechaInicial);
        param.put(GeneralParameterEnum.FECHAFINAL.getName(), fechaFinal);
        listaSuministro = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, "NUMERO");

    }

    public void oprimirEXCEL()
    {
        // <CODIGO_DESARROLLADO>

        if ("2".equals(opcion))
        {
            genInforme(ReportesBean.FORMATOS.EXCEL97,
                            "000381RequisicionesConMovimiento");
        }
        else
        {
            genInforme(ReportesBean.FORMATOS.EXCEL97,
                            "000382requisicionesConOrdenCompra");
        }

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirinformePdf()
    {
        // <CODIGO_DESARROLLADO>

        if ("2".equals(opcion))
        {
            genInforme(ReportesBean.FORMATOS.PDF,
                            "000381RequisicionesConMovimiento");
        }
        else
        {
            genInforme(ReportesBean.FORMATOS.PDF,
                            "000382requisicionesConOrdenCompra");
        }

        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaSuministro(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        suministro = registroAux.getCampos().get("NUMERO").toString();
    }

    private void genInforme(ReportesBean.FORMATOS formato, String nombreReporte)

    {
        // <CODIGO_DESARROLLADO>

        archivoDescarga = null;
        HashMap<String, Object> variables = new HashMap<>();
        try
        {
            if ("2".equals(opcion))
            {
                if (suministro.isEmpty())
                {
                    JsfUtil.agregarMensajeError(idioma.getString("TB_TB2764"));
                    return;
                }

                variables.put("fecha_inicial",SysmanFunciones.convertirAFechaCadena(fechaInicial, "dd/MM/yyyy"));
                variables.put("fecha_final",SysmanFunciones.convertirAFechaCadena(fechaFinal, "dd/MM/yyyy"));
                variables.put("numero_orden", suministro);

            }
            else
            {
                variables.put("fecha_inicial",SysmanFunciones.convertirAFechaCadena(fechaInicial, "dd/MM/yyyy"));
                variables.put("fecha_final",SysmanFunciones.convertirAFechaCadena(fechaFinal, "dd/MM/yyyy"));
            }
        }
        catch (ParseException e)
        {
            Logger.getLogger(RelacionRequisicionesUnificadoControlador.class
                            .getName()).log(Level.SEVERE, null, e);
        }

        try
        {
            Map<String, Object> parametros = new HashMap<>();

            String sql = Reporteador.resuelveConsulta(nombreReporte,
                            Integer.valueOf(modulo), variables);
            parametros.put("PR_STRSQL", sql);
            SimpleDateFormat formateador = new SimpleDateFormat(
                            " dd 'de' MMMM  'de' yyyy", new Locale("es", "ES"));

            parametros.put("PR_FORMS_RELACIONREQUISICIONESCONMOVIMIENTO_DESDE",
                            formateador.format(fechaInicial));
            parametros.put("PR_FORMS_RELACIONREQUISICIONESCONMOVIMIENTO_HASTA",
                            formateador.format(fechaFinal));
            parametros.put("PR_FORMS_REQUISICIONESCONORDEN_FECINICIAL",
                            formateador.format(fechaInicial));
            parametros.put("PR_FORMS_REQUISICIONESCONORDEN_FECFINAL",
                            formateador.format(fechaFinal));

            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (JRException | IOException | SysmanException ex)
        {
            Logger.getLogger(RelacionRequisicionesUnificadoControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_TRANS_INTERRUMPIDA")
                                + ex.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarInicial()
    {
        // <CODIGO_DESARROLLADO>

        if ((fechaInicial != null) && (fechaFinal != null))
        {
            cargarListasuministro();
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFinal()
    {
        // <CODIGO_DESARROLLADO>
        if ((fechaInicial != null) && (fechaFinal != null))
        {
            cargarListasuministro();
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiartipo()
    {
        // </CODIGO_DESARROLLADO>
    }

    public String getOpcion()
    {
        return opcion;
    }

    public void setOpcion(String opcion)
    {
        this.opcion = opcion;
    }

    public String getSuministro()
    {
        return suministro;
    }

    public void setSuministro(String suministro)
    {
        this.suministro = suministro;
    }

    public Date getFechaInicial()
    {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial)
    {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal()
    {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal)
    {
        this.fechaFinal = fechaFinal;
    }

    public RegistroDataModelImpl getListaSuministro()
    {
        return listaSuministro;
    }

    public void setListaSuministro(RegistroDataModelImpl listasuministro)
    {
        this.listaSuministro = listasuministro;
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }
}

package com.sysman.general;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.general.enums.MovpptalesdesdecontaControladorEnum;
import com.sysman.general.enums.MovpptalesdesdecontaControladorUrlEnum;
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
import java.util.Date;
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
 * @version 1, 12/05/2016
 * @author ybecerra
 * @version 2, 04/04/2017
 * @author jreina Se modificaron los metodos cargarListaTipoInicial y
 * cargarListaTipoFinal segun el proceso de refactoring.
 * 
 * @author ybecerra
 * @version 3, 12/06/2017 Implementacion al llamado de
 * GeneralCodigoFormaEnum, para el codigo del formulario
 */
@ManagedBean
@ViewScoped
public class MovpptalesdesdecontaControlador extends BeanBaseModal
{
    private final String compania;
    private final String modulo;
    private final String consCodigo;
    // <DECLARAR_ATRIBUTOS>
    private String tipoInicial;
    private String tipoFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private String opcion;
    private boolean opcionVisible;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaTipoInicial;
    private RegistroDataModelImpl listaTipoFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of MovpptalesdesdecontaControlador
     */
    public MovpptalesdesdecontaControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        consCodigo = GeneralParameterEnum.CODIGO.getName();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.MOVPPTALESDESDECONTA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(MovpptalesdesdecontaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar()
    {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTipoInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
        opcion = "1";
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        fechaInicial = new Date();
        fechaFinal = new Date();
        // <CODIGO_DESARROLLADO>
        /*
         * FR701-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaTipoInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MovpptalesdesdecontaControladorUrlEnum.URL3717
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(MovpptalesdesdecontaControladorEnum.PARAM0.getValue(),
                        compania);

        listaTipoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, consCodigo);
    }

    public void cargarListaTipoFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        MovpptalesdesdecontaControladorUrlEnum.URL4501
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(MovpptalesdesdecontaControladorEnum.PARAM0.getValue(),
                        compania);
        param.put(MovpptalesdesdecontaControladorEnum.PARAM1.getValue(),
                        tipoInicial);

        listaTipoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, consCodigo);

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPresentar()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void generarInforme(ReportesBean.FORMATOS formato)
    {

        try
        {
            String parReporte = "";
            String detalleComprobante = "";
            String numeroPptal = "";
            String titulo = "";

            if ("1".equals(opcion))
            {
                detalleComprobante = "";
                numeroPptal = " AND (COMPROBANTE_PPTAL.NUMERO = 0 OR COMPROBANTE_PPTAL.NUMERO IS NULL )";
                titulo = idioma.getString("TB_TB963");
                parReporte = "000771MovPptalesDesdeConta1";

            }
            if ("2".equals(opcion))
            {
                parReporte = "000772MovPptalesDesdeConta2";

            }
            if ("3".equals(opcion))
            {
                detalleComprobante = "  LEFT JOIN DETALLE_COMPROBANTE_PPTAL \r\n"
                    +
                    "    ON COMPROBANTE_PPTAL.COMPANIA = DETALLE_COMPROBANTE_PPTAL.COMPANIA \r\n"
                    +
                    "    AND COMPROBANTE_PPTAL.ANO = DETALLE_COMPROBANTE_PPTAL.ANO \r\n"
                    +
                    "    AND COMPROBANTE_PPTAL.TIPO = DETALLE_COMPROBANTE_PPTAL.TIPO_CPTE \r\n"
                    +
                    "    AND COMPROBANTE_PPTAL.NUMERO = DETALLE_COMPROBANTE_PPTAL.COMPROBANTE";
                numeroPptal = " AND COMPROBANTE_PPTAL.NUMERO IS NOT NULL " +
                    " AND DETALLE_COMPROBANTE_PPTAL.COMPANIA IS NULL";
                titulo = idioma.getString("TB_TB964");
                parReporte = "000771MovPptalesDesdeConta1";
            }
            HashMap<String, Object> reemplazar = new HashMap<>();

            reemplazar.put("detalleComprobante", detalleComprobante);
            reemplazar.put("tipoInicial", "'" + tipoInicial + "'");
            reemplazar.put("tipoFinal", "'" + tipoFinal + "'");
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("numeroPptal", numeroPptal);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_COMPROBANTES", idioma.getString("TB_TB465") + " "
                + tipoInicial + " Y " + tipoFinal + ".");
            parametros.put("PR_FECHAS",
                            idioma.getString("TB_TB466").toUpperCase() + " "
                                + SysmanFunciones.convertirAFechaCadena(
                                                fechaInicial, "dd/MMM/yyyy")
                                                .toUpperCase()
                                + " "
                                + idioma.getString("TB_TB467").toUpperCase()
                                + " " + SysmanFunciones.convertirAFechaCadena(
                                                fechaFinal, "dd/MMM/yyyy")
                                                .toUpperCase());
            parametros.put("PR_TITULO", titulo);

            Reporteador.resuelveConsulta(parReporte, Integer.parseInt(modulo),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(parReporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void onRowSelectTipoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        tipoInicial = registroAux.getCampos().get(consCodigo).toString();
        tipoFinal = null;
        cargarListaTipoFinal();
    }

    public void onRowSelectTipoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        tipoFinal = registroAux.getCampos().get(consCodigo).toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getTipoInicial()
    {
        return tipoInicial;
    }

    public void setTipoInicial(String tipoInicial)
    {
        this.tipoInicial = tipoInicial;
    }

    public String getTipoFinal()
    {
        return tipoFinal;
    }

    public void setTipoFinal(String tipoFinal)
    {
        this.tipoFinal = tipoFinal;
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

    public String getOpcion()
    {
        return opcion;
    }

    public void setOpcion(String opcion)
    {
        this.opcion = opcion;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public boolean isOpcionVisible()
    {
        return opcionVisible;
    }

    public void setOpcionVisible(boolean opcionVisible)
    {
        this.opcionVisible = opcionVisible;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

    public RegistroDataModelImpl getListaTipoInicial()
    {
        return listaTipoInicial;
    }

    public RegistroDataModelImpl getListaTipoFinal()
    {
        return listaTipoFinal;
    }

    public void setListaTipoFinal(RegistroDataModelImpl listaTipoFinal)
    {
        this.listaTipoFinal = listaTipoFinal;
    }

    public void setListaTipoInicial(RegistroDataModelImpl listaTipoInicial)
    {
        this.listaTipoInicial = listaTipoInicial;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}

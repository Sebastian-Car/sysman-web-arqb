package com.sysman.predial;

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
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.enums.FacturacionPorCodigoControladorEnum;
import com.sysman.predial.enums.FacturacionPorCodigoControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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
 *
 * @author jrodriguezr
 * @version 1, 08/06/2016 09:09:29 -- Modificado por jrodriguezr
 *
 * @author spina
 * @version 2, 28/06/2017 - refactorizacion dss, depuracion sonar y ejbs
 */
@ManagedBean
@ViewScoped
public class FacturacionPorCodigoControlador extends BeanBaseModal
{
    private final String compania;
    private final String numeroOrden;
    // <DECLARAR_ATRIBUTOS>
    private String codigoInicial;
    private String codigoFinal;
    private Date fechaCorte;
    private String nombreInicial;
    private String nombreFinal;
    private StreamedContent archivoDescarga;
    private String masDeCuanto;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCodigoInicial;
    private RegistroDataModelImpl listaCodigoFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of FacturacionPorCodigoControlador
     */
    public FacturacionPorCodigoControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        numeroOrden = SysmanConstantes.NUMERO_ORDEN_PREDIAL;
        try
        {
            numFormulario = GeneralCodigoFormaEnum.FACTURACION_POR_CODIGO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(FacturacionPorCodigoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar()
    {
        // <CARGAR_LISTA>
        fechaCorte = new Date();
        masDeCuanto = "0.00";
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCodigoInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturacionPorCodigoControladorUrlEnum.URL3664
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO.getName(), numeroOrden);
        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaCodigoFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FacturacionPorCodigoControladorUrlEnum.URL3665
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO.getName(), numeroOrden);
        param.put(FacturacionPorCodigoControladorEnum.CODIGOINICIAL.getValue(),
                        codigoInicial);
        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPdf()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generaReporte(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public boolean validarParametros()
    {
        boolean rta = true;
        if ((codigoInicial == null) || codigoFinal.isEmpty())
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB709"));
            rta = false;
        }
        if ((codigoFinal == null) || codigoFinal.isEmpty())
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB710"));
            rta = false;
        }
        if ((masDeCuanto == null) || masDeCuanto.isEmpty())
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB711"));
            rta = false;
        }

        return rta;

    }

    private void generaReporte(FORMATOS formato)
    {

        try
        {
            if (validarParametros())
            {
                HashMap<String, Object> reemplazar = new HashMap<>();
                Map<String, Object> parametros = new HashMap<>();
                String reporte = "000878PREDIALLISFACTURACIONCOD";
                reemplazar.put("codigoInicial", codigoInicial);
                reemplazar.put("codigoFinal", codigoFinal);
                reemplazar.put("numeroOrden", numeroOrden);
                reemplazar.put("masDeCuanto", masDeCuanto);
                reemplazar.put("conAcuerdo", "1");

                parametros.put("PR_NOMBRECOMPANIA",
                                SessionUtil.getCompaniaIngreso().getNombre());

                // MANEJO DE PARAMETROS DEL REPORTE
                Reporteador.resuelveConsulta(reporte,
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazar, parametros);

                archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
            }
        }
        catch (JRException | IOException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCodigoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
        nombreInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
        codigoFinal = null;
        nombreFinal = null;
        cargarListaCodigoFinal();
    }

    public void seleccionarFilaCodigoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
        nombreFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>

    public String getCodigoInicial()
    {
        return codigoInicial;
    }

    public void setCodigoInicial(String codigoInicial)
    {
        this.codigoInicial = codigoInicial;
    }

    public String getCodigoFinal()
    {
        return codigoFinal;
    }

    public void setCodigoFinal(String codigoFinal)
    {
        this.codigoFinal = codigoFinal;
    }

    public Date getFechaCorte()
    {
        return fechaCorte;
    }

    public void setFechaCorte(Date fechaCorte)
    {
        this.fechaCorte = fechaCorte;
    }

    public String getNombreInicial()
    {
        return nombreInicial;
    }

    public void setNombreInicial(String nombreInicial)
    {
        this.nombreInicial = nombreInicial;
    }

    public String getNombreFinal()
    {
        return nombreFinal;
    }

    public void setNombreFinal(String nombreFinal)
    {
        this.nombreFinal = nombreFinal;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public String getMasDeCuanto()
    {
        return masDeCuanto;
    }

    public void setMasDeCuanto(String masDeCuanto)
    {
        this.masDeCuanto = masDeCuanto;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCodigoInicial()
    {
        return listaCodigoInicial;
    }

    public void setListaCodigoInicial(RegistroDataModelImpl listaCodigoInicial)
    {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    public RegistroDataModelImpl getListaCodigoFinal()
    {
        return listaCodigoFinal;
    }

    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal)
    {
        this.listaCodigoFinal = listaCodigoFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}

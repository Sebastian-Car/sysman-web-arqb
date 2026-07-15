package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.enums.PredialreldiacaringrereanoControladorEnum;
import com.sysman.predial.enums.PredialreldiacaringrereanoControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
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

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author acaceres
 * @version 1, 08/06/2016
 * @author jcrodriguez=>Refactoring y depuracion del controaldor
 * @version 2, 13/07/2017
 */
@ManagedBean
@ViewScoped

public class PredialreldiacaringrereanoControlador extends BeanBaseModal
{
    private final String compania;
    private final String modulo;
    private Boolean infDetallado;
    private String cmbFormato;
    private String codigoBancoInicial;
    private String codigoBancoFin;
    private String cmbPaqIni;
    private String cmbPaqFin;
    private Date fechaInicial;
    private Date fechaFinal;
    private String descAparte;
    private String nombreBancoInicial;
    private String nombreBancoFin;
    private StreamedContent archivoDescarga;
    private List<Registro> listaCmbPaqIni;
    private List<Registro> listaCmbPaqFin;
    private RegistroDataModelImpl listaBancoI;
    private RegistroDataModelImpl listaBancoF;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of PredialreldiacaringrereanoControlador
     */
    public PredialreldiacaringrereanoControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.PREDIALRELDIACARINGREREANO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(PredialreldiacaringrereanoControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {

        cargarListacodibancoini();
        abrirFormulario();
        cargarListaCmbPaqIni();
    }

    @Override
    public void abrirFormulario()
    {
        fechaInicial = new Date();
        fechaFinal = new Date();
    }

    // <METODOS_CARGAR_LISTA>

    public void cargarListaCmbPaqIni()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PredialreldiacaringrereanoControladorEnum.BANCOINICIAL.getValue(), codigoBancoInicial);
        param.put(PredialreldiacaringrereanoControladorEnum.BANCOFINAL.getValue(), codigoBancoFin);
        param.put(PredialreldiacaringrereanoControladorEnum.FECHAINICIAL.getValue(), fechaInicial);
        param.put(PredialreldiacaringrereanoControladorEnum.FECHAFINAL.getValue(), fechaFinal);

        try
        {
            listaCmbPaqIni = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(PredialreldiacaringrereanoControladorUrlEnum.URL4457.getValue()).getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCmbPaqFin()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PredialreldiacaringrereanoControladorEnum.BANCOINICIAL.getValue(), codigoBancoInicial);
        param.put(PredialreldiacaringrereanoControladorEnum.BANCOFINAL.getValue(), codigoBancoFin);
        param.put(PredialreldiacaringrereanoControladorEnum.FECHAINICIAL.getValue(), fechaInicial);
        param.put(PredialreldiacaringrereanoControladorEnum.FECHAFINAL.getValue(), fechaFinal);
        param.put(PredialreldiacaringrereanoControladorEnum.PAQUETEINICIAL.getValue(), cmbPaqIni);

        try
        {
            listaCmbPaqFin = RegistroConverter.toListRegistro(requestManager.getList(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(PredialreldiacaringrereanoControladorUrlEnum.URL5490.getValue()).getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListacodibancoini()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(PredialreldiacaringrereanoControladorUrlEnum.URL7039.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaBancoI = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, PredialreldiacaringrereanoControladorEnum.CODIGOBANCO.getValue());

    }

    public void cargarListacodibancofin()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(PredialreldiacaringrereanoControladorUrlEnum.URL7567.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PredialreldiacaringrereanoControladorEnum.BANCOINICIAL.getValue(), codigoBancoInicial);

        listaBancoF = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, PredialreldiacaringrereanoControladorEnum.CODIGOBANCO.getValue());

    }

    private String getParametro(String nombre, boolean indMayus)
    {
        try
        {
            return ejbSysmanUtil.consultarParametro(compania, nombre, modulo, new Date(), indMayus);
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return null;
    }

    public void obtenerReporteCarIngreAnoPto(FORMATOS formatos)
    {

        Map<String, Object> reemplazar = new HashMap<>();
        // MANEJO DE PARAMETROS DE REEMPLAZO
        Map<String, Object> parametros = new HashMap<>();
        if (!prepararInforme(reemplazar, parametros))
        {
            return;
        }
        // Parametros reporte VL

        if (PredialreldiacaringrereanoControladorEnum.CNIT.getValue().equals(
                        SessionUtil.getCompaniaIngreso().getNit()))
        {
            resuelveReporte(PredialreldiacaringrereanoControladorEnum.REPORTE000880.getValue(), reemplazar, parametros, formatos);
        }
        else if ("891801268-7".equals(
                        SessionUtil.getCompaniaIngreso().getNit()))
        {
            // Reporte VL

            resuelveReporte(PredialreldiacaringrereanoControladorEnum.REPORTE000888.getValue(), reemplazar, parametros, formatos);
        }
        else
        {
            if ("0".equals(reemplazar.get("descAparte").toString()))
            {

                parametros.put(PredialreldiacaringrereanoControladorEnum.PR_ENCABEZADO_UNO.getValue(), encabezadoConcepto(15));
                parametros.put(PredialreldiacaringrereanoControladorEnum.PR_ENCABEZADO_DOS.getValue(), encabezadoConcepto(16));
                parametros.put(PredialreldiacaringrereanoControladorEnum.PR_ENCABEZADO_TRES.getValue(), encabezadoConcepto(19));
                resuelveReporte(PredialreldiacaringrereanoControladorEnum.REPORTE000894.getValue(), reemplazar, parametros, formatos);
            }
            else
            {

                seleccioanrCmbFormato(reemplazar, parametros, formatos);
            }
        }

    }

    private void seleccioanrCmbFormato(Map<String, Object> reemplazar, Map<String, Object> parametros,
        FORMATOS formatos)
    {
        if ("2".equals(cmbFormato))
        {
            parametros.put(PredialreldiacaringrereanoControladorEnum.PR_ENCABEZADO_UNO.getValue(), nombreConcepto(14));
            parametros.put(PredialreldiacaringrereanoControladorEnum.PR_ENCABEZADO_DOS.getValue(), nombreConcepto(15));
            parametros.put("PR_ENCABEZADO_TRES", nombreConcepto(16));
            parametros.put("PR_ENCABEZADO_CUATRO", nombreConcepto(17));
            parametros.put("PR_OTRO", PredialreldiacaringrereanoControladorEnum.OTROS.getValue());
        }
        switch (cmbFormato)
        {
        case "1":
            parametros.put(PredialreldiacaringrereanoControladorEnum.PR_FORMS_DESCAPARTE.getValue(), nombreConcepto(
                            Integer.parseInt(getParametro(PredialreldiacaringrereanoControladorEnum.CONCEPTO_PARA_DESCUENTO_CAR.getValue(),
                                            false))));
            resuelveReporte(PredialreldiacaringrereanoControladorEnum.REPORTE001450.getValue(), reemplazar, parametros, formatos);
            break;
        case "2":
            resuelveReporte(PredialreldiacaringrereanoControladorEnum.REPORTE001452.getValue(), reemplazar, parametros, formatos);
            break;
        default:
            resuelveReporte(PredialreldiacaringrereanoControladorEnum.REPORTE000896.getValue(), reemplazar, parametros, formatos);
            break;
        }
    }

    public void resuelveReporte(String nombreReporte, Map<String, Object> reemplazar, Map<String, Object> parametros,
        FORMATOS formatos)
    {
        try
        {
            Reporteador.resuelveConsulta(
                            nombreReporte,
                            Integer.valueOf(modulo), reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN,
                            formatos);
        }
        catch (FileNotFoundException ex)
        {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString(PredialreldiacaringrereanoControladorEnum.MSM_INFORME_NO_EXISTE.getValue()) + " "
                                + ex.getMessage() + " " + nombreReporte);
            Logger.getLogger(PredialreldiacaringrereanoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        catch (JRException | IOException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private boolean prepararInforme(Map<String, Object> reemplazar,
        Map<String, Object> parametros)
    {
        if (!validaParametro(reemplazar) || !validaParametro3(parametros)
            || !validaParametro2(reemplazar))
        {
            return false;
        }
        if (!validaParametroObs(parametros) || !validarFechas())
        {
            return false;
        }
        reemplazar.put("codigoBancoInicial", codigoBancoInicial);
        reemplazar.put("codigoBancoFinal", codigoBancoFin);
        reemplazar.put("numeroOrden",
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        reemplazar.put("fechaInicial",
                        SysmanFunciones.formatearFecha(fechaInicial));
        reemplazar.put("fechaFinal",
                        SysmanFunciones.formatearFecha(fechaFinal));
        reemplazar.put("cmbPaqIni", cmbPaqIni);
        reemplazar.put("cmbPaqFin", cmbPaqFin);
        reemplazar.put("prcodbanccompen", getParametro(
                        PredialreldiacaringrereanoControladorEnum.CODIGO_BANCO_COMPENSACIONES.getValue(),
                        false));

        String entre = null;
        try
        {
            entre = "ENTRE "
                + SysmanFunciones.convertirAFechaCadena(fechaInicial) + " Y "
                + SysmanFunciones.convertirAFechaCadena(fechaFinal) + "";
        }
        catch (ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // MANEJO DE PARAMETROS DEL REPORTE
        parametros.put("PR_VISIBLE", infDetallado ? 0 : 1);
        parametros.put("PR_NOMBRECOMPANIA",
                        SessionUtil.getCompaniaIngreso().getNombre());
        parametros.put("PR_NOMBRE_USUARIO",
                        SessionUtil.getUser().getCodigo());
        parametros.put("PR_ENTRE", entre);
        parametros.put("PR_NITCOMPANIA",
                        SessionUtil.getCompaniaIngreso().getNit());
        return true;
    }

    private boolean validarFechas()
    {
        if ((fechaInicial == null) || (fechaFinal == null))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB587"));
            return false;
        }
        return true;
    }

    private boolean validaParametroObs(Map<String, Object> parametros)
    {
        String observaciones = getParametro(
                        PredialreldiacaringrereanoControladorEnum.OBSERVACIONES_INFORME_RECAUDO.getValue(),
                        false);

        if (observaciones == null)
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB586"));
            return false;
        }
        parametros.put("PR_OBSERVACIONES", observaciones);
        return true;
    }

    public void obtenerRepSoloPredial(FORMATOS formatos)
    {

        // MANEJO DE PARAMETROS DE REEMPLAZO
        Map<String, Object> parametros = new HashMap<>();

        HashMap<String, Object> reemplazar = new HashMap<>();
        // MANEJO DE PARAMETROS DEL REPORTE
        if (!preparaReemplazosParametros(reemplazar, parametros))
        {
            return;
        }

        if (PredialreldiacaringrereanoControladorEnum.CNIT.getValue().equals(
                        SessionUtil.getCompaniaIngreso().getNit()))
        {
            reemplazar.put("porcDescPredial",
                            getParametro(PredialreldiacaringrereanoControladorEnum.PORCENTAJE_DESCUENTO_PREDIAL.getValue(), false));
            parametros.put(PredialreldiacaringrereanoControladorEnum.PR_ENCABEZADO_UNO.getValue(), encabezadoConcepto(18));
            parametros.put(PredialreldiacaringrereanoControladorEnum.PR_ENCABEZADO_DOS.getValue(), encabezadoConcepto(19));
            resuelveReporte(PredialreldiacaringrereanoControladorEnum.REPORTE000901.getValue(), reemplazar, parametros, formatos);

        }
        else if ("891801268-7".equals(
                        SessionUtil.getCompaniaIngreso().getNit()))
        {
            resuelveReporte(PredialreldiacaringrereanoControladorEnum.REPORTE000902.getValue(), reemplazar, parametros, formatos);
        }
        else
        {
            parametros.put(PredialreldiacaringrereanoControladorEnum.PR_ENCABEZADO_UNO.getValue(), encabezadoConcepto(15));
            parametros.put(PredialreldiacaringrereanoControladorEnum.PR_ENCABEZADO_DOS.getValue(), encabezadoConcepto(16));
            resuelveReporte(PredialreldiacaringrereanoControladorEnum.REPORTE000905.getValue(), reemplazar, parametros, formatos);
        }

    }

    public void obtenerRepSoloCar(FORMATOS formatos)
    {

        HashMap<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();

        if (!preparaReemplazosParametros(reemplazar, parametros))
        {
            return;
        }
        if (PredialreldiacaringrereanoControladorEnum.CNIT.getValue().equals(
                        SessionUtil.getCompaniaIngreso().getNit()))
        {
            reemplazar.put("porcDescCar",
                            getParametro(PredialreldiacaringrereanoControladorEnum.PORCENTAJE_DESCUENTO_CAR.getValue(), false));
            resuelveReporte(PredialreldiacaringrereanoControladorEnum.REPORTE000908.getValue(), reemplazar, parametros, formatos);
        }
        else
        {
            resuelveReporte(PredialreldiacaringrereanoControladorEnum.REPORTE000910.getValue(), reemplazar, parametros, formatos);
        }

    }

    private boolean preparaReemplazosParametros(
        HashMap<String, Object> reemplazar,
        Map<String, Object> parametros)
    {
        try
        {
            if (!validaParametro(reemplazar) || !validaParametro2(reemplazar)
                || !validaParametro3(parametros))
            {
                return false;
            }
            if ((fechaInicial == null) || (fechaFinal == null))
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB587"));
                return false;
            }
            String entre = "ENTRE "
                + SysmanFunciones.convertirAFechaCadena(fechaInicial) + " Y "
                + SysmanFunciones.convertirAFechaCadena(fechaFinal) + "";

            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("codigoBancoInicial", codigoBancoInicial);
            reemplazar.put("codigoBancoFin", codigoBancoFin);
            reemplazar.put("numeroOrden",
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL);
            reemplazar.put("cmbPaqIni", cmbPaqIni);
            reemplazar.put("cmbPaqFin", cmbPaqFin);
            reemplazar.put("prcodbanccompen", getParametro(
                            PredialreldiacaringrereanoControladorEnum.CODIGO_BANCO_COMPENSACIONES.getValue(),
                            false));

            parametros.put("PR_VISIBLE", infDetallado ? 0 : 1);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_ENTRE", entre);
            parametros.put("PR_NOMBRE_USUARIO",
                            SessionUtil.getUser().getCodigo());
            parametros.put("PR_FIRMAJEFESECCION", getParametro(
                            PredialreldiacaringrereanoControladorEnum.FIRMA_JEFE_SECCION_RELACIONES_DETALLADAS.getValue(),
                            true));

        }
        catch (ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return true;
    }

    private boolean validaParametro3(Map<String, Object> parametros)
    {
        String firmaJefeRelacionesDetalladas = getParametro(
                        PredialreldiacaringrereanoControladorEnum.FIRMA_JEFE_SECCION_RELACIONES_DETALLADAS.getValue(), true);

        if (firmaJefeRelacionesDetalladas == null)
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB593"));
            return false;
        }
        parametros.put("PR_FIRMA_JEFE", firmaJefeRelacionesDetalladas);
        return true;
    }

    private boolean validaParametro2(Map<String, Object> reemplazar)
    {
        String codBanCompensacion = getParametro(PredialreldiacaringrereanoControladorEnum.CODIGO_BANCO_COMPENSACIONES.getValue(), false);

        if (codBanCompensacion == null)
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB592"));
            return false;
        }
        reemplazar.put("codBanCompens", codBanCompensacion);
        return true;
    }

    private boolean validaParametro(Map<String, Object> reemplazar)
    {
        String descAparte2 = getParametro(PredialreldiacaringrereanoControladorEnum.CONCEPTO_PARA_DESCUENTO_CAR.getValue(), false);
        if (descAparte2 == null)
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB591"));
            return false;
        }
        int descApar = Integer.parseInt(descAparte2);
        if ((descApar > 13) && (descApar < 20))
        {
            descAparte2 = "IP_RECIBOS_DE_PAGO.C" + descAparte2;
        }
        else
        {
            descAparte2 = "0";
        }

        reemplazar.put("descAparte", descAparte2);
        return true;
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirCompletoPdf()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporteCarIngreAnoPto(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirSoloPredialPdf()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerRepSoloPredial(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirSoloCarPdf()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerRepSoloCar(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirinfDetalladoPDF()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerRepSoloCarDetallado(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirinfDetalladoExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerRepSoloCarDetallado(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    private void obtenerRepSoloCarDetallado(FORMATOS formatos)
    {

        HashMap<String, Object> reemplazar = new HashMap<>();
        // MANEJO DE PARAMETROS DE REEMPLAZO
        Map<String, Object> parametros = new HashMap<>();

        if (!preparaReemplazosParametros(reemplazar, parametros))
        {
            return;
        }
        if (!adicionarRemplazosConceptos(reemplazar, PredialreldiacaringrereanoControladorEnum.CONCEPTO_PARA_DESCUENTO_CAR.getValue(),
                        "descCarD", "descCar"))
        {
            return;
        }

        if (!adicionarRemplazosConceptos(reemplazar, PredialreldiacaringrereanoControladorEnum.CONCEPTO_DE_DESCUENTO.getValue(), "descImpD",
                        "descImp"))
        {
            return;
        }

        resuelveReporte(PredialreldiacaringrereanoControladorEnum.REPORTE001419.getValue(), reemplazar, parametros, formatos);

    }

    private boolean adicionarRemplazosConceptos(HashMap<String, Object> reemplazar, String nombre, String nombreRemplazoUno,
        String nombreRemplazoDos)
    {
        String descCar = getParametro(nombre, false);
        String descCarD;
        if (descCar == null)
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB591"));
            return false;
        }
        int descApar = Integer.parseInt(descCar);
        if ((descApar >= 13) && (descApar <= 20))
        {
            descCarD = "DR.C" + descCar;
            descCar = "RE.C" + descCar;
        }
        else
        {
            descCarD = "0";
            descCar = "0";
        }

        reemplazar.put(nombreRemplazoUno, descCarD);
        reemplazar.put(nombreRemplazoDos, descCar);
        return true;
    }

    public void oprimirComando51()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCmdHonda()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCompletoExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporteCarIngreAnoPto(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirSoloPredialExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerRepSoloPredial(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirSoloCarExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerRepSoloCar(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarfechaini()
    {
        // <CODIGO_DESARROLLADO>
        cargarListaCmbPaqIni();
        cargarListaCmbPaqFin();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarfechafin()
    {
        // <CODIGO_DESARROLLADO>
        cargarListaCmbPaqIni();
        cargarListaCmbPaqFin();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCmbPaqIni()
    {
        cargarListaCmbPaqFin();
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaBancoI(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoBancoInicial = SysmanFunciones.validarCampoVacio(registroAux.getCampos(),
                        PredialreldiacaringrereanoControladorEnum.CODIGOBANCO.getValue()) ? ""
                            : registroAux.getCampos().get(PredialreldiacaringrereanoControladorEnum.CODIGOBANCO.getValue()).toString();
        nombreBancoInicial = SysmanFunciones
                        .validarCampoVacio(registroAux.getCampos(), PredialreldiacaringrereanoControladorEnum.NOMBREBANCO.getValue()) ? ""
                            : registroAux.getCampos()
                                            .get(PredialreldiacaringrereanoControladorEnum.NOMBREBANCO.getValue()).toString();
        codigoBancoFin = nombreBancoFin = cmbPaqIni = cmbPaqFin = null;
        cargarListacodibancofin();
        cargarListaCmbPaqIni();

    }

    public void seleccionarFilaBancoF(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoBancoFin = SysmanFunciones.validarCampoVacio(registroAux.getCampos(),
                        PredialreldiacaringrereanoControladorEnum.CODIGOBANCO.getValue()) ? ""
                            : registroAux.getCampos().get(PredialreldiacaringrereanoControladorEnum.CODIGOBANCO.getValue()).toString();
        nombreBancoFin = SysmanFunciones
                        .validarCampoVacio(registroAux.getCampos(), PredialreldiacaringrereanoControladorEnum.NOMBREBANCO.getValue()) ? ""
                            : registroAux.getCampos()
                                            .get(PredialreldiacaringrereanoControladorEnum.NOMBREBANCO.getValue()).toString();
        cmbPaqIni = cmbPaqFin = null;
        cargarListaCmbPaqIni();
        cargarListaCmbPaqFin();

    }

    private Registro consultarConcepto(int codigo)
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CONCEPTO.getName(), codigo);
        Registro reg = null;

        try
        {
            reg = RegistroConverter.toRegistro(requestManager.get(UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(PredialreldiacaringrereanoControladorUrlEnum.URL7029.getValue()).getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return reg;

    }

    private String encabezadoConcepto(int codigo)
    {

        Registro reg = consultarConcepto(codigo);

        if ("800095728-2".equals(
                        SessionUtil.getCompaniaIngreso().getNit()))
        {
            return SysmanFunciones.validarCampoVacio(reg.getCampos(), GeneralParameterEnum.NOMBRE.getName()) ? ""
                : reg.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();
        }
        else
        {
            return SysmanFunciones.validarCampoVacio(reg.getCampos(),
                            PredialreldiacaringrereanoControladorEnum.ENCABEZADO.getValue())
                                ? reg.getCampos().get(GeneralParameterEnum.CODIGO.getName()).toString()
                                : reg.getCampos().get(PredialreldiacaringrereanoControladorEnum.ENCABEZADO.getValue()).toString();
        }
    }

    private String nombreConcepto(int numero)
    {
        Registro reg = consultarConcepto(numero);

        if (reg != null)
        {
            return SysmanFunciones.validarCampoVacio(reg.getCampos(), GeneralParameterEnum.NOMBRE.getName()) ? ""
                : reg.getCampos().get(GeneralParameterEnum.NOMBRE.getName()).toString();
        }
        else
        {
            return "";
        }

    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>

    public String getCmbFormato()
    {
        return cmbFormato;
    }

    public Boolean getInfDetallado()
    {
        return infDetallado;
    }

    public void setInfDetallado(Boolean infDetallado)
    {
        this.infDetallado = infDetallado;
    }

    public void setCmbFormato(String cmbFormato)
    {
        this.cmbFormato = cmbFormato;
    }

    public String getCodigoBancoInicial()
    {
        return codigoBancoInicial;
    }

    public void setCodigoBancoInicial(String codigoBancoInicial)
    {
        this.codigoBancoInicial = codigoBancoInicial;
    }

    public String getCodigoBancoFin()
    {
        return codigoBancoFin;
    }

    public void setCodigoBancoFin(String codigoBancoFin)
    {
        this.codigoBancoFin = codigoBancoFin;
    }

    public String getCmbPaqIni()
    {
        return cmbPaqIni;
    }

    public void setCmbPaqIni(String cmbPaqIni)
    {
        this.cmbPaqIni = cmbPaqIni;
    }

    public String getCmbPaqFin()
    {
        return cmbPaqFin;
    }

    public void setCmbPaqFin(String cmbPaqFin)
    {
        this.cmbPaqFin = cmbPaqFin;
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

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public String getDescAparte()
    {
        return descAparte;
    }

    public void setDescAparte(String descAparte)
    {
        this.descAparte = descAparte;
    }

    public String getNombreBancoInicial()
    {
        return nombreBancoInicial;
    }

    public void setNombreBancoInicial(String nombreBancoInicial)
    {
        this.nombreBancoInicial = nombreBancoInicial;
    }

    public String getNombreBancoFin()
    {
        return nombreBancoFin;
    }

    public void setNombreBancoFin(String nombreBancoFin)
    {
        this.nombreBancoFin = nombreBancoFin;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public List<Registro> getListaCmbPaqIni()
    {
        return listaCmbPaqIni;
    }

    public void setListaCmbPaqIni(List<Registro> listaCmbPaqIni)
    {
        this.listaCmbPaqIni = listaCmbPaqIni;
    }

    public List<Registro> getListaCmbPaqFin()
    {
        return listaCmbPaqFin;
    }

    public void setListaCmbPaqFin(List<Registro> listaCmbPaqFin)
    {
        this.listaCmbPaqFin = listaCmbPaqFin;
    }

    public RegistroDataModelImpl getListaBancoI()
    {
        return listaBancoI;
    }

    public void setListaBancoI(RegistroDataModelImpl listaBancoI)
    {
        this.listaBancoI = listaBancoI;
    }

    public RegistroDataModelImpl getListaBancoF()
    {
        return listaBancoF;
    }

    public void setListaBancoF(RegistroDataModelImpl listaBancoF)
    {
        this.listaBancoF = listaBancoF;
    }

}

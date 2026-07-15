package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.LisresultadosControladorUrlEnum;
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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
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

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author acaceres
 * @version 1, 29/04/2016
 * 
 * @modified jguerrero
 * @version 2. 11/04/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Adem�s se ajustaron los errores del sonar
 * 
 * @author spina - refactorizo conexiones
 * @version 3, 12/06/2017
 */
@ManagedBean
@ViewScoped

public class LisresultadosControlador extends BeanBaseModal
{
    private final String compania;
    private final String modulo;
    private boolean centroCosto;
    private boolean tercero;
    private boolean auxiliar;
    private boolean manAuxFue;
    private boolean manAuxRef;
    private boolean saldoCero;
    private boolean formatoEspecial;
    private boolean soloSaldos;
    private String codigoInicial;
    private String codigoFinal;
    private String cmbCentroCInicial;
    private String cmbCentroCFinal;
    private String digitos;
    private String titulo;
    private String centroCostoFiltro;

    private int anoTrabajo;
    private int mesTrabajo;
    private boolean bloqueaSoloSaldos;
    private boolean centrosCInicialVisible;
    private boolean centrosCFinalVisible;
    private boolean etiquetaCentrosCInicialVisible;
    private boolean etiquetaCentrosCFinalVisible;

    private StreamedContent archivoDescarga;
    private List<Registro> listaTitulo;
    private List<Registro> listaAnoTrabajo;
    private List<Registro> listaMesTrabajo;
    private RegistroDataModelImpl listaCodigoInicial;
    private RegistroDataModelImpl listaCodigoFinal;

    private final String codigoCons;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of LisresultadosControlador
     */
    public LisresultadosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        codigoCons = "CODIGO";
        anoTrabajo = SysmanFunciones
                        .ano(new Date());
        mesTrabajo = SysmanFunciones
                        .mes(new Date());

        codigoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        codigoFinal = SysmanConstantes.DEFECTOFINAL_STRING;

        titulo = "1";
        digitos = "6";
        bloqueaSoloSaldos = false;
        try
        {
            numFormulario = GeneralCodigoFormaEnum.LISRESULTADOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(LisresultadosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar()
    {

        cargarListaTitulo();
        cargarListaAnoTrabajo();
        cargarListaMesTrabajo();
        cargarListaCodigoInicial();

        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaTitulo()
    {
        try
        {
            listaTitulo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LisresultadosControladorUrlEnum.URL4199
                                                                            .getValue())
                                            .getUrl(), null));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAnoTrabajo()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaAnoTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LisresultadosControladorUrlEnum.URL4828
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void cargarListaMesTrabajo()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoTrabajo);

        try
        {
            listaMesTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LisresultadosControladorUrlEnum.URL5322
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCodigoInicial()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisresultadosControladorUrlEnum.URL6648
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoTrabajo);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    public void cargarListaCodigoFinal()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LisresultadosControladorUrlEnum.URL7929
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anoTrabajo);
        param.put("CODIGOINICIAL", codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, codigoCons);
    }

    public void obtenerLisResultadosEspecial(FORMATOS formatos)

    {
        try
        {
            archivoDescarga = null;
            String condicionSaldoCero;
            condicionSaldoCero = saldoCero
                ? ""
                : "WHERE V_PLAN_CONTABLE.SALDO" + mesTrabajo + " NOT IN (0) ";

            titulo = service.buscarEnLista(titulo, codigoCons, "TITULO",
                            listaTitulo);
            int mesTrabajo1 = mesTrabajo - 1;
            HashMap<String, Object> reemplazar = new HashMap<>();

            reemplazar.put("anoTrabajo", anoTrabajo);

            reemplazar.put("anio", anoTrabajo);
            reemplazar.put("codigoInicial", "'" + codigoInicial + "'");
            reemplazar.put("codigoFinal", "'" + codigoFinal + "'");

            reemplazar.put("mesTrabajo", mesTrabajo);
            reemplazar.put("mesTrabajo1", mesTrabajo1);
            reemplazar.put("saldoCero", condicionSaldoCero);

            auxiliares(reemplazar);

            if (saldoCero)
            {
                reemplazar.put("digitosCond", digitos != null
                    ? "  WHERE LENGTH(V_PLAN_CONTABLE.CODIGO)<=" + digitos
                    : "");
            }
            else
            {
                reemplazar.put("digitosCond", digitos != null
                    ? " AND LENGTH(V_PLAN_CONTABLE.CODIGO)<=" + digitos
                    : "");
            }

            reemplazar.put("saldoCero", condicionSaldoCero);
            reemplazar.put("baseBalance", Reporteador.resuelveConsulta(
                            "800046BaseBalances",
                            Integer.parseInt(modulo), reemplazar));

            // MANEJO DE PARAMETROS DE REEMPLAZO
            String firmaResultados1 = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania, "FIRMA RESULTADOS 1", modulo, new Date(), true),
                            "FIRMA RESULTADOS 1");
            String cargoResultados1 = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania, "CARGO RESULTADOS 1", modulo, new Date(), true),
                            "CARGO RESULTADOS 1");
            String documentoResultados1 = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania, "DOCUMENTO RESULTADOS 1", modulo, new Date(), true),
                            "DOCUMENTO RESULTADOS 1");
            String firmaResultados2 = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania, "FIRMA RESULTADOS 2", modulo, new Date(), true),
                            "FIRMA RESULTADOS 2");
            String cargoResultados2 = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania, "CARGO RESULTADOS 2", modulo, new Date(), true),
                            "CARGO RESULTADOS 2");
            String docuemntoResultados2 = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania, "DOCUMENTO RESULTADOS 2", modulo, new Date(), true),
                            "DOCUMENTO RESULTADOS 2");
            String firmaResultados3 = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania, "FIRMA RESULTADOS 3", modulo, new Date(), true),
                            "FIRMA RESULTADOS 3");
            String cargoResultados3 = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania, "CARGO RESULTADOS 3", modulo, new Date(), true),
                            "CARGO RESULTADOS 3");
            String documentoResultados3 = SysmanFunciones.nvlStr(
                            ejbSysmanUtil.consultarParametro(compania, "DOCUMENTO RESULTADOS 3", modulo, new Date(), true),
                            "DOCUMENTO RESULTADOS 3");

            Map<String, Object> parametros = new HashMap<>();

            // MANEJO DE PARAMETROS DEL REPORTE
            String nombreMes = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mesTrabajo];

            String nombreMes1 = SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mesTrabajo1];
            String entre = titulo + " " + nombreMes.toUpperCase() + " DE "
                + anoTrabajo;

            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNit());
            parametros.put("PR_MESTRABAJO1", nombreMes1);
            parametros.put("PR_MESTRABAJO", nombreMes);
            parametros.put("PR_FIRMA_RESULTADOS_1", firmaResultados1);
            parametros.put("PR_CARGO_RESULTADOS_1", cargoResultados1);
            parametros.put("PR_DOCUMENTO_RESULTADOS_1", documentoResultados1);
            parametros.put("PR_FIRMA_RESULTADOS_2", firmaResultados2);
            parametros.put("PR_CARGO_RESULTADOS_2", cargoResultados2);
            parametros.put("PR_DOCUMENTO_RESULTADOS_2", docuemntoResultados2);
            parametros.put("PR_FIRMA_RESULTADOS_3", firmaResultados3);
            parametros.put("PR_CARGO_RESULTADOS_3", cargoResultados3);
            parametros.put("PR_DOCUMENTO_RESULTADOS_3", documentoResultados3);
            parametros.put("PR_ENTRE", entre);
            parametros.put("PR_NOMBRECOMPANIA",
                            SessionUtil.getCompaniaIngreso().getNombre());

            reporteador(reemplazar, parametros, formatos);
        }
        catch (SystemException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    private void auxiliares(HashMap<String, Object> reemplazar)
    {

        reemplazar.put("manTer", tercero ? "1" : "0");
        reemplazar.put("manAux", auxiliar ? "1" : "0");
        reemplazar.put("manCen", centroCosto ? "1" : "0");
        reemplazar.put("manRef", manAuxRef ? "1" : "0");
        reemplazar.put("manFue", manAuxFue ? "1" : "0");

    }

    private void reporteador(HashMap<String, Object> reemplazar,
        Map<String, Object> parametros,
        FORMATOS formatos)
    {
        try
        {
            if (!formatoEspecial && !soloSaldos)
            {
                archivoDescarga = null;

                Reporteador.resuelveConsulta("000706LisResultados",
                                Integer.parseInt(modulo), reemplazar,
                                parametros);

                archivoDescarga = JsfUtil.exportarStreamed(
                                "000706LisResultados", parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formatos);

            }

            else
            {
                if (!soloSaldos)
                {
                    archivoDescarga = null;
                    Reporteador.resuelveConsulta("000708LisResultadosEspecial",
                                    Integer.parseInt(modulo), reemplazar,
                                    parametros);
                    archivoDescarga = JsfUtil.exportarStreamed(
                                    "000708LisResultadosEspecial", parametros,
                                    ConectorPool.ESQUEMA_SYSMAN, formatos);
                }
                else
                {
                    archivoDescarga = null;
                    Reporteador.resuelveConsulta("000716LisresultadosSC",
                                    Integer.parseInt(modulo), reemplazar,
                                    parametros);
                    archivoDescarga = JsfUtil.exportarStreamed(
                                    "000716LisresultadosSC", parametros,
                                    ConectorPool.ESQUEMA_SYSMAN, formatos);
                }
            }
        }
        catch (JRException | IOException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirPdf()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        obtenerLisResultadosEspecial(FORMATOS.PDF);

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerLisResultadosEspecial(FORMATOS.EXCEL97);

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarTitulo()
    {
        //
    }

    public void cambiarAnoTrabajo()
    {
        // <CODIGO_DESARROLLADO>
        codigoInicial = null;
        codigoFinal = null;
        cargarListaMesTrabajo();
        cargarListaCodigoInicial();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarFormatoEspecial()
    {
        // <CODIGO_DESARROLLADO>
        if (formatoEspecial)
        {
            bloqueaSoloSaldos = true;

        }
        else
        {
            bloqueaSoloSaldos = false;

            // </CODIGO_DESARROLLADO>
        }
    }

    public void cambiarCentroCosto()
    {
        // <CODIGO_DESARROLLADO>

        cmbCentroCInicial = null;
        cmbCentroCFinal = null;

        if (centroCosto)
        {
            centrosCInicialVisible = true;
            centrosCFinalVisible = true;
            etiquetaCentrosCInicialVisible = true;
            etiquetaCentrosCFinalVisible = true;

        }
        else
        {
            centrosCInicialVisible = false;
            centrosCFinalVisible = false;
            etiquetaCentrosCInicialVisible = false;
            etiquetaCentrosCFinalVisible = false;

        }

        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaCodigoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos().get(codigoCons).toString();
        codigoFinal = null;
        cargarListaCodigoFinal();
    }

    public void seleccionarFilaCodigoFinal(SelectEvent event)
    {

        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos().get(codigoCons).toString();
    }

    public void seleccionarFilaCmbCentroCFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cmbCentroCFinal = registroAux.getCampos().get(codigoCons).toString();
    }

    public boolean isCentroCosto()
    {
        return centroCosto;
    }

    public void setCentroCosto(boolean centroCosto)
    {
        this.centroCosto = centroCosto;
    }

    public boolean isTercero()
    {
        return tercero;
    }

    public void setTercero(boolean tercero)
    {
        this.tercero = tercero;
    }

    public boolean isAuxiliar()
    {
        return auxiliar;
    }

    public void setAuxiliar(boolean auxiliar)
    {
        this.auxiliar = auxiliar;
    }

    public boolean isSaldoCero()
    {
        return saldoCero;
    }

    public void setSaldoCero(boolean saldoCero)
    {
        this.saldoCero = saldoCero;
    }

    public boolean isFormatoEspecial()
    {
        return formatoEspecial;
    }

    public void setFormatoEspecial(boolean formatoEspecial)
    {
        this.formatoEspecial = formatoEspecial;
    }

    public boolean isSoloSaldos()
    {
        return soloSaldos;
    }

    public void setSoloSaldos(boolean soloSaldos)
    {
        this.soloSaldos = soloSaldos;
    }

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

    public String getCmbCentroCInicial()
    {
        return cmbCentroCInicial;
    }

    public void setCmbCentroCInicial(String cmbCentroCInicial)
    {
        this.cmbCentroCInicial = cmbCentroCInicial;
    }

    public String getCmbCentroCFinal()
    {
        return cmbCentroCFinal;
    }

    public void setCmbCentroCFinal(String cmbCentroCFinal)
    {
        this.cmbCentroCFinal = cmbCentroCFinal;
    }

    public int getAnoTrabajo()
    {
        return anoTrabajo;
    }

    public void setAnoTrabajo(int anoTrabajo)
    {
        this.anoTrabajo = anoTrabajo;
    }

    public int getMesTrabajo()
    {
        return mesTrabajo;
    }

    public void setMesTrabajo(int mesTrabajo)
    {
        this.mesTrabajo = mesTrabajo;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public List<Registro> getListaAnoTrabajo()
    {
        return listaAnoTrabajo;
    }

    public void setListaAnoTrabajo(List<Registro> listaAnoTrabajo)
    {
        this.listaAnoTrabajo = listaAnoTrabajo;
    }

    /**
     * Retorna la lista listaMesTrabajo
     *
     * @return listaMesTrabajo
     */
    public List<Registro> getListaMesTrabajo()
    {
        return listaMesTrabajo;
    }

    /**
     * Asigna la lista listaMesTrabajo
     *
     * @param listaMesTrabajo
     * Variable a asignar en listaMesTrabajo
     */
    public void setListaMesTrabajo(List<Registro> listaMesTrabajo)
    {
        this.listaMesTrabajo = listaMesTrabajo;
    }

    public RegistroDataModelImpl getListaCodigoInicial()
    {
        return listaCodigoInicial;
    }

    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial)
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

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public String getDigitos()
    {
        return digitos;
    }

    public void setDigitos(String digitos)
    {
        this.digitos = digitos;
    }

    public String getTitulo()
    {
        return titulo;
    }

    public void setTitulo(String titulo)
    {
        this.titulo = titulo;
    }

    public boolean isBloqueaSoloSaldos()
    {
        return bloqueaSoloSaldos;
    }

    public void setBloqueaSoloSaldos(boolean bloqueaSoloSaldos)
    {
        this.bloqueaSoloSaldos = bloqueaSoloSaldos;
    }

    public List<Registro> getListaTitulo()
    {
        return listaTitulo;
    }

    public void setListaTitulo(List<Registro> listaTitulo)
    {
        this.listaTitulo = listaTitulo;
    }

    public boolean isCentrosCInicialVisible()
    {
        return centrosCInicialVisible;
    }

    public void setCentrosCInicialVisible(boolean centrosCInicialVisible)
    {
        this.centrosCInicialVisible = centrosCInicialVisible;
    }

    public boolean isCentrosCFinalVisible()
    {
        return centrosCFinalVisible;
    }

    public void setCentrosCFinalVisible(boolean centrosCFinalVisible)
    {
        this.centrosCFinalVisible = centrosCFinalVisible;
    }

    public boolean isEtiquetaCentrosCInicialVisible()
    {
        return etiquetaCentrosCInicialVisible;
    }

    public void setEtiquetaCentrosCInicialVisible(
        boolean etiquetaCentrosCInicialVisible)
    {
        this.etiquetaCentrosCInicialVisible = etiquetaCentrosCInicialVisible;
    }

    public boolean isEtiquetaCentrosCFinalVisible()
    {
        return etiquetaCentrosCFinalVisible;
    }

    public void setEtiquetaCentrosCFinalVisible(
        boolean etiquetaCentrosCFinalVisible)
    {
        this.etiquetaCentrosCFinalVisible = etiquetaCentrosCFinalVisible;
    }

    public String getCentroCostoFiltro()
    {
        return centroCostoFiltro;
    }

    public void setCentroCostoFiltro(String centroCostoFiltro)
    {
        this.centroCostoFiltro = centroCostoFiltro;
    }

    public boolean isManAuxFue()
    {
        return manAuxFue;
    }

    public void setManAuxFue(boolean manAuxFue)
    {
        this.manAuxFue = manAuxFue;
    }

    public boolean isManAuxRef()
    {
        return manAuxRef;
    }

    public void setManAuxRef(boolean manAuxRef)
    {
        this.manAuxRef = manAuxRef;
    }

}

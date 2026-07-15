package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.enums.BalancePruebaControladorEnum;
import com.sysman.contabilidad.enums.BalancePruebaControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
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
 * @author jrodriguezr
 * @version 1, 18/03/2016
 * @version 2, 12/04/2017 Se realiza la generación de los servicios para cuatro consultas. Se ajustan advertencias de SonarLint. No se ajusta la complejidad del método obtenerReportes debido a que se
 * puede perjudicar el funcionamiento del reporte.
 * @author jgomez
 * @version 3, 09/08/2018 Se realiza ajuste para que el informe se desacople del bean pues la mitad de la consulta estaba en el controlador
 * @author jgomez
 * @version 4, 28/08/2018 Se limpian variables que no se usan con sus respectivos metodos y se crea nuevo check para un formato especial
 */
@ManagedBean
@ViewScoped
public class BalancePruebaControlador extends BeanBaseModal
{

    private final String compania;
    private final String modulo;

    private boolean tercero;
    private boolean auxiliar;
    private boolean saldoCero;
    private boolean formatoEspecial;
    private boolean meses;
    private boolean centroCosto;
    private boolean referencia;
    private boolean fuenteRecurso;
    private boolean formatoEspecialExcel;
    private boolean digitoVerificacion;
    private boolean visibleDv;

    private String codigoInicial;
    private String codigoFinal;
    private String centroCInicial;
    private String centroCFinal;
    private String anioTrabajo;
    private String mesTrabajo;
    private String mesTrabajo1;
    private String digitos;
    private StreamedContent archivoDescarga;
    private List<Registro> listaAnoTrabajo;
    private List<Registro> listaMesTrabajo;
    private List<Registro> listaMesTrabajo1;
    private RegistroDataModelImpl listaCodigoInicial;
    private RegistroDataModelImpl listaCodigoFinal;
    private boolean mesTrabajo1Visible;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of BalancePruebaControlador
     */
    public BalancePruebaControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        codigoInicial = SysmanConstantes.DEFECTOINICIAL_STRING;
        codigoFinal = SysmanConstantes.DEFECTOFINAL_STRING;

        try
        {
            numFormulario = GeneralCodigoFormaEnum.BALANCE_PRUEBA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(BalancePruebaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        cargarListaAnoTrabajo();
        anioTrabajo = String.valueOf(SysmanFunciones
                        .ano(new Date()));
        cargarListaMesTrabajo();
        mesTrabajo = String.valueOf(SysmanFunciones
                        .mes(new Date()));
        cargarListaMesTrabajo1();
        mesTrabajo1 = String.valueOf(SysmanFunciones
                        .mes(new Date()));
        cargarListaCodigoInicial();

        digitos = "6";
        cargarListaCodigoFinal();

        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {

        // </CODIGO_DESARROLLADO>
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
                                                            BalancePruebaControladorUrlEnum.URL4789
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMesTrabajo()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anioTrabajo);
        try
        {
            listaMesTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            BalancePruebaControladorUrlEnum.URL5120
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaMesTrabajo1()
    {
        // 7004 NUMERO
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anioTrabajo);
        param.put(BalancePruebaControladorEnum.MESINICIAL.getValue(),
                        mesTrabajo);

        try
        {
            listaMesTrabajo1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            BalancePruebaControladorUrlEnum.URL5524
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
                                        BalancePruebaControladorUrlEnum.URL6041
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anioTrabajo);
        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaCodigoFinal()
    {
        // 16010
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        BalancePruebaControladorUrlEnum.URL6922
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(BalancePruebaControladorEnum.CODIGOINICIAL.getValue(),
                        codigoInicial);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anioTrabajo);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());
    }

    public void cambiarAnoTrabajo()
    {
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
        cargarListaMesTrabajo();
        cargarListaMesTrabajo1();
    }

    public void cambiarMesTrabajo()
    {
        cargarListaMesTrabajo1();
    }

    public void cambiarmeses()
    {
        mesTrabajo1 = String.valueOf(Integer.parseInt(mesTrabajo) + 1);
        cargarListaMesTrabajo1();
        if (meses)
        {
            mesTrabajo1Visible = true;
        }
        else
        {
            mesTrabajo1Visible = false;
        }

    }

    private void obtenerReportes(FORMATOS formatos)
    {

        archivoDescarga = null;
        String reporte;

        String tituloCC3 = "";

        if (meses && (mesTrabajo1 == null))
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TB_TB866"));
            return;
        }
        if (meses && (Integer.parseInt(mesTrabajo) > Integer
                        .parseInt(mesTrabajo1)))
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TB_TB864"));
            return;
        }
        try
        {

            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            mesTrabajo1 = meses ? mesTrabajo1 : mesTrabajo;
            reemplazar.put("mesIni", mesTrabajo);
            reemplazar.put("mesFin", mesTrabajo1);
            //reemplazar.put("compania", compania);
            reemplazar.put("anio", anioTrabajo);
            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal);
            reemplazar.put("digitos", digitos);
            reemplazar.put("manTer", tercero ? "1" : "0");
            reemplazar.put("manAux", auxiliar ? "1" : "0");
            reemplazar.put("manCen", centroCosto ? "1" : "0");
            reemplazar.put("manRef", referencia ? "1" : "0");
            reemplazar.put("manFue", fuenteRecurso ? "1" : "0");
            reemplazar.put("manDV", digitoVerificacion ? "1" : "0");

            reemplazar.put("saldoCero", saldoCero ? -1 : 0);

            reemplazar.put("baseBalance", Reporteador.resuelveConsulta(
                            "800046BaseBalancesPrueba",
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar));

            if (meses)
            {
                tituloCC3 = idioma.getString("TB_TB861") + " "
                                + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                .parseInt(mesTrabajo)]
                                                                .toUpperCase()
                                + " A "
                                + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                .parseInt(mesTrabajo1)]
                                                                .toUpperCase()
                                + " DE "
                                + anioTrabajo;

            }
            else
            {
                tituloCC3 = idioma.getString("TB_TB867")
                                + " "
                                + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                .parseInt(mesTrabajo)]
                                                                .toUpperCase()
                                + " DE " + anioTrabajo;
            }
            tituloCC3 = tituloCC3.replace("#salto#", "\n");
            if (tercero || auxiliar || referencia || centroCosto
            		|| fuenteRecurso)
            {
            	if (formatoEspecial)
            	{
            		reporte = "000588BalancePruebaEsp";
            	}
            	else {
            		if (digitoVerificacion)
            		{
            			reporte = "900021BALANCEPRUEBAIDIPRON"; // revisar conexiòn o subreporte Jasper

            		} else
            		{
            			//reporte = "000588BalancePrueba";
            			if (saldoCero) {
            				reporte = "000588BalancePruebaSinsaldo";

            			} else {
            				reporte = "000588BalancePrueba";
            			}
            		}
            	}
            }
            else
            {
            	if(formatoEspecialExcel || (formatoEspecialExcel && formatoEspecial))
				{
					reporte = "002693BalancePruebaME";
				}
            	else if (formatoEspecial)
                {
                    reporte = "000597BalancePruebaMEEsp";
                }
                else
                {
                	if (saldoCero) {
                		reporte = "000597BalancePruebaMEsinSaldo";
                		
					}                 		                	
                	else {
						reporte = "000597BalancePruebaME";
					}                    
                }
            }

            Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),
                            reemplazar, parametros);

            parametros.put("PR_TITULO", tituloCC3);
            parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso()
                            .getNombre().toUpperCase());
            parametros.put("PR_NITCOMPANIA", SessionUtil.getCompaniaIngreso()
                    .getNit());
            String firmaCont1 = ejbSysmanUtil.consultarParametro(compania,
                            "FIRMA CONTABLE 1", modulo, new Date(), true);
            String cargoCont1 = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO CONTABLE 1", modulo, new Date(), true);
            String documentoCont1 = ejbSysmanUtil.consultarParametro(compania,
                            "DOCUMENTO CONTABLE 1", modulo, new Date(), true);
            String firmaCont2 = ejbSysmanUtil.consultarParametro(compania,
                            "FIRMA CONTABLE 2", modulo, new Date(), true);
            String cargoCont2 = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO CONTABLE 2", modulo, new Date(), true);
            String documentoCont2 = ejbSysmanUtil.consultarParametro(compania,
                            "DOCUMENTO CONTABLE 2", modulo, new Date(), true);
            String firmaCont3 = ejbSysmanUtil.consultarParametro(compania,
                            "FIRMA CONTABLE 3", modulo, new Date(), true);
            String cargoCont3 = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO CONTABLE 3", modulo, new Date(), true);
            String documentoCont3 = ejbSysmanUtil.consultarParametro(compania,
                            "DOCUMENTO CONTABLE 3", modulo, new Date(), true);
            parametros.put("PR_FIRMA_CONTABLE_1", firmaCont1);
            parametros.put("PR_CARGO_CONTABLE_1", cargoCont1);
            parametros.put("PR_DOCUMENTO_CONTABLE_1", documentoCont1);
            parametros.put("PR_FIRMA_CONTABLE_2", firmaCont2);
            parametros.put("PR_CARGO_CONTABLE_2", cargoCont2);
            parametros.put("PR_DOCUMENTO_CONTABLE_2", documentoCont2);
            parametros.put("PR_FIRMA_CONTABLE_3", firmaCont3);
            parametros.put("PR_CARGO_CONTABLE_3", cargoCont3);
            parametros.put("PR_DOCUMENTO_CONTABLE_3", documentoCont3);
            parametros.put("PR_NIT_VISIBLE", tercero);
            parametros.put("PR_CENTRO_VISIBLE", centroCosto);
            parametros.put("PR_AUXILIAR_VISIBLE", auxiliar);
            parametros.put("PR_REFERENCIA_VISIBLE", referencia);
            parametros.put("PR_FUENTE_VISIBLE", fuenteRecurso);
            parametros.put("PR_FORMATO_ESPECIAL_EXCEL", formatoEspecialExcel);
            parametros.put("PR_FORMATO_ESPECIAL", formatoEspecial);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formatos);
        }
        catch (OutOfMemoryError | JRException
                        | IOException | SystemException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirPresentar()
    {
        archivoDescarga = null;
        obtenerReportes(ReportesBean.FORMATOS.PDF);
    }

    public void oprimirExcel()
    {
        archivoDescarga = null;
        obtenerReportes(ReportesBean.FORMATOS.EXCEL97);
    }

    public void seleccionarFilaCodigoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
        cargarListaCodigoFinal();
        codigoFinal = SysmanConstantes.DEFECTOFINAL_STRING;
    }

    public void seleccionarFilaCodigoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
    }

    public void seleccionarFilaCmbCentroCInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        centroCInicial = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
    }

    public void seleccionarFilaCmbCentroCFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        centroCFinal = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
    }

    public boolean getTercero()
    {
        return tercero;
    }

    public void setTercero(boolean tercero)
    {
        this.tercero = tercero;
    }

    public void cambiarTercero()
    {
        if (tercero)
        {
            visibleDv = true;
        }
        else
        {
            visibleDv = false;
        }
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

    public boolean isMeses()
    {
        return meses;
    }

    public void setMeses(boolean meses)
    {
        this.meses = meses;
    }

    public boolean isCentroCosto()
    {
        return centroCosto;
    }

    public void setCentroCosto(boolean centroCosto)
    {
        this.centroCosto = centroCosto;
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

    public String getCentroCInicial()
    {
        return centroCInicial;
    }

    public void setCentroCInicial(String centroCInicial)
    {
        this.centroCInicial = centroCInicial;
    }

    public String getCentroCFinal()
    {
        return centroCFinal;
    }

    public void setCentroCFinal(String centroCFinal)
    {
        this.centroCFinal = centroCFinal;
    }

    public String getAnioTrabajo()
    {
        return anioTrabajo;
    }

    public void setAnioTrabajo(String anioTrabajo)
    {
        this.anioTrabajo = anioTrabajo;
    }

    public String getMesTrabajo()
    {
        return mesTrabajo;
    }

    public void setMesTrabajo(String mesTrabajo)
    {
        this.mesTrabajo = mesTrabajo;
    }

    public String getMesTrabajo1()
    {
        return mesTrabajo1;
    }

    public void setMesTrabajo1(String mesTrabajo1)
    {
        this.mesTrabajo1 = mesTrabajo1;
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

    public List<Registro> getListaMesTrabajo()
    {
        return listaMesTrabajo;
    }

    public void setListaMesTrabajo(List<Registro> listaMesTrabajo)
    {
        this.listaMesTrabajo = listaMesTrabajo;
    }

    public List<Registro> getListaMesTrabajo1()
    {
        return listaMesTrabajo1;
    }

    public void setListaMesTrabajo1(List<Registro> listaMesTrabajo1)
    {
        this.listaMesTrabajo1 = listaMesTrabajo1;
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

    public String getDigitos()
    {
        return digitos;
    }

    public void setDigitos(String digitos)
    {
        this.digitos = digitos;
    }

    public boolean isMesTrabajo1Visible()
    {
        return mesTrabajo1Visible;
    }

    public void setMesTrabajo1Visible(boolean mesTrabajo1Visible)
    {
        this.mesTrabajo1Visible = mesTrabajo1Visible;
    }

    public boolean isReferencia()
    {
        return referencia;
    }

    public void setReferencia(boolean referencia)
    {
        this.referencia = referencia;
    }

    public boolean isFuenteRecurso()
    {
        return fuenteRecurso;
    }

    public void setFuenteRecurso(boolean fuenteRecurso)
    {
        this.fuenteRecurso = fuenteRecurso;
    }

    public boolean isFormatoEspecialExcel()
    {
        return formatoEspecialExcel;
    }

    public void setFormatoEspecialExcel(boolean formatoEspecialExcel)
    {
        this.formatoEspecialExcel = formatoEspecialExcel;
    }

    public boolean isDigitoVerificacion()
    {
        return digitoVerificacion;
    }

    public void setDigitoVerificacion(boolean digitoVerificacion)
    {
        this.digitoVerificacion = digitoVerificacion;
    }

    public boolean isVisibleDv()
    {
        return visibleDv;
    }

    public void setVisibleDv(boolean visibleDv)
    {
        this.visibleDv = visibleDv;
    }

}

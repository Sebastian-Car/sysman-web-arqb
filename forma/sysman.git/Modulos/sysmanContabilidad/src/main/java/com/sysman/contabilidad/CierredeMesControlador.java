package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.ejb.EjbContabilidadCincoRemote;
import com.sysman.contabilidad.ejb.EjbContabilidadCuatroRemote;
import com.sysman.contabilidad.enums.CierredeMesControladorEnum;
import com.sysman.contabilidad.enums.CierredeMesControladorUrlEnum;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.math.BigInteger;
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

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author jrodriguezr
 * @version 1, 26/04/2016
 * @version 2, 19/04/2017 jrodriguezr Se refactoriza el codigo SQL de las listas para utilizar dss.
 * @version 3, 20/04/2017 jrodriguezr Se refactoriza el codigo ajustando los llamados a funciones, procedimiento y metodos de la clase Acciones a llamados a EJB.
 * @author spina - refactorizo conexiones
 * @version 3, 12/06/2017
 */
@ManagedBean
@ViewScoped
public class CierredeMesControlador extends BeanBaseModal
{
    private final String compania;
    private String modulo;
    private boolean impuestos;
    private boolean utilidad;
    private String ano;
    private String mes;
    private String centroCosto;
    private String tipoComprobante;
    private String numero;
    private Date fechaInterface;
    private boolean verComprobante;
    private List<Registro> listaAno;
    private List<Registro> listaMes;
    private List<Registro> listaCentroCosto;
    private boolean verCentroCosto;
    private boolean indVisibleCtasImp;
    private boolean indVisibleMsg;
    private StreamedContent archivoDescarga;
    private boolean muestraDialogo;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    @EJB
    private EjbContabilidadCuatroRemote ejbContabilidadCuatro;
    @EJB
    private EjbContabilidadCincoRemote ejbContabilidadCinco;
    private String reporte;

    /**
     * Creates a new instance of CierredeMesControlador
     */
    public CierredeMesControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.CIERREDE_MES_CONTROLADOR.getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(CierredeMesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        ano = String.valueOf(SysmanFunciones.ano(new Date()));
        mes = String.valueOf(SysmanFunciones.mes(new Date()));
        tipoComprobante = "CIE";
        cargarListaAno();
        cargarListaMes();
        cambiarMes();
        cargarListaCentroCosto();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        try
        {
            // validar centro de costo teniendo en cuenta la
            // configuracion del parametro de sistema
            String manCierreXCCto = ejbSysmanUtil.consultarParametro(
                            compania,
                            "MANEJA CIERRE POR CENTRO DE COSTO", modulo,
                            new Date(), true);
            if ("SI".equals(manCierreXCCto))
            {
                verCentroCosto = true;
            }
            else
            {
                verCentroCosto = false;
            }
            String manCierreCtasImp = ejbSysmanUtil.consultarParametro(
                            compania,
                            "PERMITE CIERRE DE CUENTAS DE IMPUESTOS", modulo,
                            new Date(), true);
            if ("SI".equals(manCierreCtasImp))
            {
                indVisibleCtasImp = true;
            }
            else
            {
                indVisibleCtasImp = false;
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
        }
        verComprobante = true;
        utilidad = true;
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaAno()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CierredeMesControladorUrlEnum.URL4471
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMes()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        try
        {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CierredeMesControladorUrlEnum.URL4846
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCentroCosto()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        try
        {
            listaCentroCosto = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CierredeMesControladorUrlEnum.URL5332
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void generaComprobante(String numeroComp)
    {
        archivoDescarga = null;
        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            reemplazar.put("companiaInforme", compania);
            reemplazar.put("ano", ano);
            reemplazar.put("tipoCpte", tipoComprobante);
            reemplazar.put("numeroPptoInicial", numeroComp);
            reemplazar.put("numeroPptoFinal", numeroComp);

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil
                                            .getModulo()),
                            reemplazar, parametros);
            parametros.put("PR_NOMBRECOMPANIA", SessionUtil
                            .getCompaniaIngreso().getNombre());
            parametros.put("PR_NITCOMPANIA",
                            SessionUtil.getCompaniaIngreso()
                                            .getNit());
            archivoDescarga = JsfUtil
                            .exportarStreamed(reporte,
                                            parametros,
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            FORMATOS.EXCEL97);

        }
        catch (OutOfMemoryError | JRException | IOException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirAceptar()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(CierredeMesControladorEnum.TIPO.getValue(), tipoComprobante);
        param.put(GeneralParameterEnum.NUMERO.getName(), numero);
        Registro existeCpte = null;
        try
        {
            existeCpte = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CierredeMesControladorUrlEnum.URL17823
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if ((existeCpte != null)
            && !"0".equals(existeCpte.getCampos().get("CUENTA").toString()))
        {
            muestraDialogo = true;
            return;
        }
        if (cierreContable())
        {
            if (verComprobante && validarFormato())
            {
                generaComprobante(numero);
            }
            else
            {
                muestraMensaje();
            }
        }
    }

    private void muestraMensaje()
    {
        JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2749")
                        .replace("#numero#", numero)
                        .replace("#tipo#", tipoComprobante)
                        .replace("#anio#", ano));
    }

    private boolean validarFormato()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(CierredeMesControladorEnum.TIPOCOMPROBANTE.getValue(),
                        tipoComprobante);
        Registro rsFormato = null;
        try
        {
            rsFormato = RegistroConverter.toRegistro(
                            requestManager.get(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            CierredeMesControladorUrlEnum.URL4848
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if ((rsFormato == null)
            || SysmanFunciones.validarCampoVacio(rsFormato.getCampos(),
                            "FORMATO"))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2741"));
            return false;
        }
        else
        {
            reporte = rsFormato.getCampos().get("FORMATO").toString();
            return true;
        }
    }

    private boolean cierreContable()
    {
        centroCosto = verCentroCosto ? centroCosto
            : SysmanConstantes.CONS_CENTRO;
        centroCosto = centroCosto == null ? SysmanConstantes.CONS_CENTRO
            : centroCosto;
        boolean rta = false;
        try
        {
            rta = ejbContabilidadCuatro.cierreContableValidado(compania,
                            tipoComprobante, new BigInteger(numero),
                            Integer.parseInt(ano),
                            fechaInterface, Integer.parseInt(mes), centroCosto,
                            false, utilidad,
                            impuestos, SessionUtil.getUser().getCodigo());
        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return rta;
    }

    public void cambiarAno()
    {
        // <CODIGO_DESARROLLADO>
        mes = null;
        numero = null;
        cargarListaMes();
        cargarListaCentroCosto();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes()
    {
        // <CODIGO_DESARROLLADO>
        if ("12".equals(mes))
        {
            indVisibleMsg = false;
        }
        else
        {
            indVisibleMsg = true;
        }
        try
        {
            fechaInterface = SysmanFunciones.ultimoDiaDate(SysmanFunciones
                            .convertirAFecha("01/" + mes + "/" + ano));
        }
        catch (ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        genConsecutivo();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarUtilidad()
    {
        if (utilidad)
        {
            impuestos = false;
        }
        else
        {
            utilidad = true;
        }
    }

    public void cambiarImpuesto()
    {
        if (impuestos)
        {
            utilidad = false;
        }
        else
        {
            impuestos = true;
        }
    }

    public void aceptarverificaComprobante()
    {
        // <CODIGO_DESARROLLADO>
        if (eliminarComprobante() && cierreContable())
        {
            if (verComprobante && validarFormato())
            {
                generaComprobante(numero);
            }
            else
            {
                muestraMensaje();
            }
        }
        muestraDialogo = false;
        // </CODIGO_DESARROLLADO>
    }

    private boolean eliminarComprobante()
    {
        boolean rta = true;
        try
        {
            ejbContabilidadCinco.eliminarComprobantesCNT(compania,
                            Integer.parseInt(ano),
                            tipoComprobante,
                            new BigInteger(numero),
                            SessionUtil.getUser().getCodigo());
        }
        catch (NumberFormatException | SystemException e)
        {
            rta = false;
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return rta;
    }

    /**
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo verificaComprobante en la vista
     */
    public void cancelarverificaComprobante()
    {
        // <CODIGO_DESARROLLADO>
        if (verComprobante && validarFormato())
        {
            generaComprobante(numero);
        }
        else
        {
            muestraMensaje();
        }
        // </CODIGO_DESARROLLADO>
    }

    public void genConsecutivo()
    {
        if ((ano != null) && (mes != null))
        {
            String criterio = " COMPANIA = ''" + compania + "'' AND ANO = "
                + ano + " AND MES = " + mes + " AND TIPO_CPTE = ''CIE'' ";
            long numComprobante;
            try
            {
                numComprobante = ejbSysmanUtil
                                .generarConsecutivoConValorInicial(
                                                "DETALLE_COMPROBANTE_CNT",
                                                criterio, "COMPROBANTE", "1");
                numero = Long.toString(numComprobante);
                if (numComprobante == 1)
                {
                    numero = SysmanFunciones.padl(ano, 4, "0")
                        + SysmanFunciones.padl(mes, 2, "0")
                        + SysmanFunciones.padl(
                                        String.valueOf(SysmanFunciones
                                                        .dia(fechaInterface)),
                                        4, "0");
                }
            }
            catch (SystemException e)
            {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }
        else
        {
            numero = null;
        }
    }

    public boolean isImpuestos()
    {
        return impuestos;
    }

    public void setImpuestos(boolean impuestos)
    {
        this.impuestos = impuestos;
    }

    public boolean isUtilidad()
    {
        return utilidad;
    }

    public void setUtilidad(boolean utilidad)
    {
        this.utilidad = utilidad;
    }

    public String getAno()
    {
        return ano;
    }

    public void setAno(String ano)
    {
        this.ano = ano;
    }

    public String getMes()
    {
        return mes;
    }

    public void setMes(String mes)
    {
        this.mes = mes;
    }

    public String getCentroCosto()
    {
        return centroCosto;
    }

    public void setCentroCosto(String centroCosto)
    {
        this.centroCosto = centroCosto;
    }

    public String getTipoComprobante()
    {
        return tipoComprobante;
    }

    public void setTipoComprobante(String tipoComprobante)
    {
        this.tipoComprobante = tipoComprobante;
    }

    public String getnumero()
    {
        return numero;
    }

    public void setnumero(String numero)
    {
        this.numero = numero;
    }

    public List<Registro> getListaAno()
    {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno)
    {
        this.listaAno = listaAno;
    }

    public List<Registro> getListaMes()
    {
        return listaMes;
    }

    public void setListaMes(List<Registro> listaMes)
    {
        this.listaMes = listaMes;
    }

    public List<Registro> getListaCentroCosto()
    {
        return listaCentroCosto;
    }

    public void setListaCentroCosto(List<Registro> listaCentroCosto)
    {
        this.listaCentroCosto = listaCentroCosto;
    }

    public boolean isVerCentroCosto()
    {
        return verCentroCosto;
    }

    public void setVerCentroCosto(boolean verCentroCosto)
    {
        this.verCentroCosto = verCentroCosto;
    }

    public boolean isIndVisibleCtasImp()
    {
        return indVisibleCtasImp;
    }

    public void setIndVisibleCtasImp(boolean indVisibleCtasImp)
    {
        this.indVisibleCtasImp = indVisibleCtasImp;
    }

    public boolean isIndVisibleMsg()
    {
        return indVisibleMsg;
    }

    public void setIndVisibleMsg(boolean indVisibleMsg)
    {
        this.indVisibleMsg = indVisibleMsg;
    }

    public boolean getVerComprobante()
    {
        return verComprobante;
    }

    public void setVerComprobante(boolean verComprobante)
    {
        this.verComprobante = verComprobante;
    }

    public Date getFechaInterface()
    {
        return fechaInterface;
    }

    public void setFechaInterface(Date fechaInterface)
    {
        this.fechaInterface = fechaInterface;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public boolean isMuestraDialogo()
    {
        return muestraDialogo;
    }

    public void setMuestraDialogo(boolean muestraDialogo)
    {
        this.muestraDialogo = muestraDialogo;
    }
}

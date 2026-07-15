package com.sysman.contratos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contratos.enums.WsradicacioncontratosContraladorUrlEnum;
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
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
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

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author sdaza
 * @version 1, 09/06/2016
 *
 * @author spina
 * @version 2, 15/08/2017 - se refactoriza para dss, depuracion sonar y ejbs
 */
@ManagedBean
@ViewScoped
public class WsradicacioncontratosContralador extends BeanBaseModal
{
    private final String compania;
    private String modulo;
    private String tipo;
    private String numero;
    private StreamedContent archivoDescarga;
    private List<Registro> listaTIPO;
    private List<Registro> listaNUMERO;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of WsradicacioncontratosContralador
     */
    public WsradicacioncontratosContralador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.WSRADICACIONCONTRATOS_CONTRALADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(WsradicacioncontratosContralador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar()
    {
        cargarListaTIPO();
        cargarListaNUMERO();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaTIPO()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaTIPO = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            WsradicacioncontratosContraladorUrlEnum.URL3320
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaNUMERO()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CLASEORDEN.getName(), tipo);
        try
        {
            listaNUMERO = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            WsradicacioncontratosContraladorUrlEnum.URL3321
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirConsultar()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarTIPO()
    {
        // <CODIGO_DESARROLLADO>
        cargarListaNUMERO();
        // </CODIGO_DESARROLLADO>
    }

    public void generarReporte(ReportesBean.FORMATOS formato)
    {

        try
        {
            Date fechaActual = new Date();
            Map<String, Object> parametros = new HashMap<>();
            HashMap<String, Object> reemplazar = new HashMap<>();

            String reporte = "000801CertificadoRadicacionContrato";
            String formatoReporte = SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "FORMATO RADICACION DE CONTRATOS",
                                            modulo, new Date(), false),
                                            reporte);
            String cargoOfContr = SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "NOMBRE CARGO OFICINA DE CONTRATACION",
                                            modulo, new Date(), true),
                                            "NOMBRE CARGO OFICINA DE CONTRATACION")
                            .toString();
            String jefeAreaCont = SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "NOMBRE JEFE AREA DE CONTRATACION",
                                            modulo, new Date(), true), "")
                            .toString();

            reemplazar.put("tipocontrato", tipo);
            reemplazar.put("numerocontrato", numero);

            parametros.put("PR_NOMBRE_CARGO_OFICINA_DE_CONTRATACION",
                            cargoOfContr);

            if (formatoReporte.equalsIgnoreCase(reporte))
            {
                parametros.put("PR_NOMBRECOMPANIA",
                                SessionUtil.getCompaniaIngreso().getNombre()
                                                .toUpperCase());
                parametros.put("PR_NITCOMPANIA",
                                SessionUtil.getCompaniaIngreso().getNit());
                parametros.put("PR_CIUDADCOMPANIA",
                                SessionUtil.getCompaniaIngreso().getCiudad());
                parametros.put("PR_DEPARTAMENTOCOMPANIA", SessionUtil
                                .getCompaniaIngreso().getDepartamento());
                parametros.put("PR_DIRECCIONCOMPANIA", SessionUtil
                                .getCompaniaIngreso().getDireccion());
                parametros.put("PR_TELEFONOCOMPANIA",
                                SessionUtil.getCompaniaIngreso().getTelefono());
                parametros.put("DIA_EXPEDICION",
                                SysmanFunciones.dia(fechaActual));
                parametros.put("MES_EXPEDICION",
                                SysmanFunciones.mes(fechaActual));
                parametros.put("ANO_EXPEDICION",
                                SysmanFunciones.ano(fechaActual));

                parametros.put("PR_VERSION_FORMATO_CALIDAD_TUNJA",
                                jefeAreaCont);
            }
            else if ("001010rptRadicacionContratos"
                            .equalsIgnoreCase(formatoReporte))
            {
                reemplazar.put("codigoUNSPSC", "");
                parametros.put("PR_NOMBRE_JEFE_AREA_DE_CONTRATACION",
                                jefeAreaCont);

                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.CLASEORDEN.getName(), tipo);
                param.put(GeneralParameterEnum.NUMERO.getName(), numero);

                List<Registro> lSupervisores = RegistroConverter.toListRegistro(
                                requestManager.getList(
                                                UrlServiceUtil.getInstance()
                                                                .getUrlServiceByUrlByEnumID(
                                                                                WsradicacioncontratosContraladorUrlEnum.URL3322
                                                                                                .getValue())
                                                                .getUrl(),
                                                param));

                StringBuilder sql = new StringBuilder("");
                for (Registro rSupervisor : lSupervisores)
                {
                    sql.append(SysmanFunciones.concatenar(
                                    SysmanFunciones.nvl(
                                                    rSupervisor.getCampos()
                                                                    .get("SUPERVISOR"),
                                                    "").toString(),
                                    ","));
                }

                String supervisores = "";
                if (!"".equals(sql.toString()))
                {
                    supervisores = sql.substring(0, sql.length() - 1);
                }
                reemplazar.put("supervisores", supervisores);
            }

            Reporteador.resuelveConsulta(formatoReporte,
                            Integer.parseInt(modulo), reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(formatoReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | SystemException e)
        {
            Logger.getLogger(WsradicacioncontratosContralador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public String getTipo()
    {
        return tipo;
    }

    public void setTipo(String tipo)
    {
        this.tipo = tipo;
    }

    public String getNumero()
    {
        return numero;
    }

    public void setNumero(String numero)
    {
        this.numero = numero;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public List<Registro> getListaTIPO()
    {
        return listaTIPO;
    }

    public void setListaTIPO(List<Registro> listaTIPO)
    {
        this.listaTIPO = listaTIPO;
    }

    public List<Registro> getListaNUMERO()
    {
        return listaNUMERO;
    }

    public void setListaNUMERO(List<Registro> listaNUMERO)
    {
        this.listaNUMERO = listaNUMERO;
    }
}

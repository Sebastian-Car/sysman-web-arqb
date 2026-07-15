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
import com.sysman.predial.enums.PredialRelDiaPagBancControladorEnum;
import com.sysman.predial.enums.PredialRelDiaPagBancControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

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
 * @author dmaldonado
 * @version 1, 07/06/2016 15:51:19 -- Modificado por dmaldonado
 *
 * @author spina
 * @version 2, 13/07/2017 - se refactoriza para dss, depuracion sonar y ejbs, se corrije el envio de parámetros al reporte 879 para mostrar los nombres de los conceptos
 */
@ManagedBean
@ViewScoped
public class PredialRelDiaPagBancControlador extends BeanBaseModal
{
    private final String compania;
    /**
     * Constante a nivel de clase que aloja el valor MSM_TRANS_INTERRUMPIDA
     */
    private final String mensajeA;

    /** Constante a nivel de clase que aloja el valor CODIGOBANCO */
    private final String codigoBanco;
    private boolean verConcepto;
    private String bancoInicial;
    private String bancoFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private String nombreInicial;
    private String nombreFinal;
    private StreamedContent archivoDescarga;
    private RegistroDataModelImpl listabancofinal;
    private RegistroDataModelImpl listabancoini;
    private String modulo;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of PredialRelDiaPagBancControlador
     */
    public PredialRelDiaPagBancControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        mensajeA = "MSM_TRANS_INTERRUMPIDA";
        codigoBanco = "CODIGOBANCO";

        try
        {
            numFormulario = GeneralCodigoFormaEnum.PREDIAL_REL_DIA_PAG_BANC_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(PredialRelDiaPagBancControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        cargarListabancoini();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        Date fechaActual = new Date();
        fechaInicial = fechaActual;
        fechaFinal = fechaActual;
    }

    public void cargarListabancoini()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PredialRelDiaPagBancControladorUrlEnum.URL4841
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        listabancoini = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        codigoBanco);
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListabancofinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PredialRelDiaPagBancControladorUrlEnum.URL4842
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(PredialRelDiaPagBancControladorEnum.BANCOINICIAL.getValue(),
                        bancoInicial);
        listabancofinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        codigoBanco);
    }

    public void oprimirPresentar()
    {
        archivoDescarga = null;
        if (verConcepto)
        {
            generaInformeConcepto(FORMATOS.PDF);
        }
        else
        {
            generaInforme(FORMATOS.PDF);
        }
    }

    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        if (verConcepto)
        {
            generaInformeConcepto(FORMATOS.EXCEL97);
        }
        else
        {
            generaInforme(FORMATOS.EXCEL97);
        }
        // </CODIGO_DESARROLLADO>
    }

    public void generaInforme(FORMATOS formato)
    {
        if (fechaFinal.before(fechaInicial))
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB147"));
            return;
        }

        HashMap<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();
        String reporte = "000875PREDIALRELDIAPAGBANC";
        try
        {

            reemplazar.put("numeroOrden",
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL);
            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("bancoInicial", bancoInicial);
            reemplazar.put("bancoFinal", bancoFinal);

            parametros.put("PR_ENTREFECHAS", SysmanFunciones.concatenar(
                            "Entre fechas ",
                            SysmanFunciones.convertirAFechaCadena(fechaInicial),
                            " y ",
                            SysmanFunciones.convertirAFechaCadena(fechaFinal)));

            Reporteador.resuelveConsulta(reporte,
                            Integer.valueOf(modulo), reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(
                            reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (ParseException | OutOfMemoryError | JRException | IOException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(
                            SysmanFunciones.concatenar(
                                            idioma.getString(mensajeA),
                                            e.getMessage()));
        }
        catch (SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void generaInformeConcepto(FORMATOS formato)
    {
        if (fechaFinal.before(fechaInicial))
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB147"));
            return;
        }
        String reporte = "000879PREDIALRELDIAPAGBANCConcepto";

        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            reemplazar.put("fechaInicial",
                            SysmanFunciones.formatearFecha(fechaInicial));
            reemplazar.put("fechaFinal",
                            SysmanFunciones.formatearFecha(fechaFinal));
            reemplazar.put("bancoInicial", bancoInicial);
            reemplazar.put("bancoFinal", bancoFinal);

            // envio el hash de parametros para agregarles los valores de las columnas o conceptos al reporte
            recuperarNC(parametros);

            parametros.put("PR_ENTREFECHAS",
                            SysmanFunciones.concatenar("Entre fechas ",
                                            SysmanFunciones.convertirAFechaCadena(
                                                            fechaInicial),
                                            " y ",
                                            SysmanFunciones.convertirAFechaCadena(
                                                            fechaFinal)));

            String parametro = ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE CORPORACION AUTONOMA REGIONAL", modulo,
                            new Date(), true);
            if (parametro == null)
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB148"));
            }
            parametros.put("PR_NOMBRECAR",
                            SysmanFunciones.nvlStr(parametro, ""));

            Reporteador.resuelveConsulta("000879PREDIAL_RELDIAPAGBANC_Concepto",
                            Integer.valueOf(modulo), reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(
                            reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN,
                            formato);

        }
        catch (IOException | JRException | ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(
                            SysmanFunciones.concatenar(
                                            idioma.getString(mensajeA),
                                            e.getMessage()));
        }
        catch (SystemException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void recuperarNC(Map<String, Object> parametros)
                    throws SystemException
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano(fechaFinal));

        List<Registro> nombresConcepto = RegistroConverter
                        .toListRegistro(requestManager.getList(
                                        UrlServiceUtil.getInstance()
                                                        .getUrlServiceByUrlByEnumID(
                                                                        PredialRelDiaPagBancControladorUrlEnum.URL4843
                                                                                        .getValue())
                                                        .getUrl(),
                                        param));
        int aux = 0;
        for (int i = 1; i <= 20; i++)
        {
            boolean existe = false;
            for (int j = 0; j < nombresConcepto.size(); j++)
            {
                if (nombresConcepto.get(j).getCampos()
                                .get(GeneralParameterEnum.CODIGO.getName())
                                .toString().equals(Integer.toString(i)))
                {
                    existe = true;
                    aux = j;
                    break;
                }
            }

            if (existe)
            {
                parametros.put(SysmanFunciones.concatenar("PR_NC",
                                String.valueOf(i)),
                                nombresConcepto.get(aux)
                                                .getCampos().get("NOMBRE"));
            }
            else
            {
                parametros.put(SysmanFunciones.concatenar("PR_NC",
                                String.valueOf(i)),
                                SysmanFunciones.concatenar("CONCEPTO ",
                                                String.valueOf(i)));
            }
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarbancofinal()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilabancoini(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        bancoInicial = String.valueOf(registroAux.getCampos().get(codigoBanco));
        nombreInicial = String
                        .valueOf(registroAux.getCampos().get("NOMBREBANCO"));
        bancoFinal = null;
        nombreFinal = null;
        cargarListabancofinal();
    }

    public void seleccionarFilabancofinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        bancoFinal = String.valueOf(registroAux.getCampos().get(codigoBanco));
        nombreFinal = String
                        .valueOf(registroAux.getCampos().get("NOMBREBANCO"));
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public boolean getVerConcepto()
    {
        return verConcepto;
    }

    public void setVerConcepto(boolean verConcepto)
    {
        this.verConcepto = verConcepto;
    }

    public String getBancoInicial()
    {
        return bancoInicial;
    }

    public void setBancoInicial(String bancoInicial)
    {
        this.bancoInicial = bancoInicial;
    }

    public String getBancoFinal()
    {
        return bancoFinal;
    }

    public void setBancoFinal(String bancoFinal)
    {
        this.bancoFinal = bancoFinal;
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

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public RegistroDataModelImpl getListabancofinal()
    {
        return listabancofinal;
    }

    public void setListabancofinal(RegistroDataModelImpl listabancofinal)
    {
        this.listabancofinal = listabancofinal;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListabancoini()
    {
        return listabancoini;
    }

    public void setListabancoini(RegistroDataModelImpl listabancoini)
    {
        this.listabancoini = listabancoini;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}

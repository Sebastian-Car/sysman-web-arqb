package com.sysman.contabilidad;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilidad.ejb.EjbContabilidadCincoRemote;
import com.sysman.contabilidad.ejb.EjbContabilidadUnoRemote;
import com.sysman.contabilidad.enums.RevisarafectacionescntControladorEnum;
import com.sysman.contabilidad.enums.RevisarafectacionescntControladorUrlEnum;
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
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
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
 * @author dsuesca
 * @version 1, 19/05/2016 17:18:55 -- Modificado por dsuesca
 * @modified jsforero
 * @version 2. 17/04/2017 Se realizo el refactory. Ademas se hicieron las respectivas Correcciones del sonar.
 * @author spina - refactorizo conexiones
 * @version 3, 28/12/2017
 * @author jreina Se unificaron los metodos corregirOrdenes y verificaPeriodo al metodo oprimirAceptar
 * para una visualizacion correcta de los mensajes de error.
 */

@ManagedBean
@ViewScoped
public class RevisarafectacionescntControlador extends BeanBaseModal
{
    private final String compania;

    // <DECLARAR_ATRIBUTOS>
    private String anio;
    private String mesInicial;
    private String mesFinal;
    private StreamedContent archivoDescarga;
    private boolean bloqueadoAceptar;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    private boolean revisarAfectacionesContablesSinAbrirPeriodos;
    private boolean correrRevisarAfectacionesCartera;
    private boolean revisarPagosContratos;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAno;
    private List<Registro> listaMesInicial;
    private List<Registro> listaMesFinal;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbContabilidadUnoRemote ejbContabilidadUno;

    @EJB
    private EjbContabilidadCincoRemote ejbContabilidadCinco;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of RevisarafectacionescntControlador
     */
    public RevisarafectacionescntControlador()
    {
        super();
        compania = SessionUtil.getCompania();

        try
        {
            numFormulario = GeneralCodigoFormaEnum.REVISARAFECTACIONESCNT_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            anio = String.valueOf(SysmanFunciones.ano(new Date()));
            mesInicial = "1";
            mesFinal = "12";

            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(RevisarafectacionescntControlador.class.getName())
            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar()
    {
        // <CARGAR_LISTA>
        cargarParametros();
        cargarListaAno();
        cargarListaMesInicial();
        cargarListaMesFinal();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    public void cargarParametros()
    {

        try
        {
            String parametro;
            parametro = "REVISAR AFECTACIONES CONTABLES SIN ABRIR PERIODOS";
            Date fechaAct = new Date();
            revisarAfectacionesContablesSinAbrirPeriodos = "SI"
                            .equals(SysmanFunciones.nvl(ejbSysmanUtil
                                            .consultarParametro(compania,
                                                            parametro,
                                                            SessionUtil.getModulo(),
                                                            fechaAct, true),
                                            "NO"));

            parametro = "CORRER REVISAR AFECTACIONES CARTERA";
            correrRevisarAfectacionesCartera = "SI"
                            .equals(SysmanFunciones.nvl(ejbSysmanUtil
                                            .consultarParametro(compania,
                                                            parametro,
                                                            SessionUtil.getModulo(),
                                                            fechaAct, true),
                                            "NO"));

            parametro = "REVISAR PAGOS CONTRATOS";
            revisarPagosContratos = "SI"
                            .equals(SysmanFunciones.nvl(ejbSysmanUtil
                                            .consultarParametro(compania,
                                                            parametro,
                                                            SessionUtil.getModulo(),
                                                            fechaAct, true),
                                            "NO"));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaAno()
    {
        try
        {
            UrlBean urlAno = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            RevisarafectacionescntControladorUrlEnum.URL5836
                                            .getValue());
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(urlAno.getUrl(), null));
        }
        catch (SystemException e)
        {
            Logger.getLogger(RevisarafectacionescntControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMesInicial()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), anio);

        try
        {
            UrlBean urlmesI = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            RevisarafectacionescntControladorUrlEnum.URL6111
                                            .getValue());
            listaMesInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(urlmesI.getUrl(), param));
        }
        catch (SystemException e)
        {
            Logger.getLogger(RevisarafectacionescntControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMesFinal()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.name(), compania);
        param.put(GeneralParameterEnum.ANO.name(), anio);
        param.put(RevisarafectacionescntControladorEnum.MESINICIAL.getValue(),
                        mesInicial);

        try
        {
            UrlBean urlmesF = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            RevisarafectacionescntControladorUrlEnum.URL6449
                                            .getValue());
            listaMesFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(urlmesF.getUrl(), param));
        }
        catch (SystemException e)
        {
            Logger.getLogger(RevisarafectacionescntControlador.class
                            .getName()).log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        try {
            bloqueadoAceptar = true;
            archivoDescarga = null;
            ejbContabilidadCinco.revisaArAfectacionesCnth(compania,
                                Integer.parseInt(anio));  
            ejecutarmensajeArchivo();
        }
        catch (NumberFormatException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }
    
    /**
    *
    * Metodo invocado al ejecutar el comando remoto mensajeArchivo en
    * la vista
    *
    */
   public void ejecutarmensajeArchivo() {
       // <CODIGO_DESARROLLADO>
       JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB234"));
       // </CODIGO_DESARROLLADO>
   }
   
    public void oprimirInconsistencias() {
        archivoDescarga = null;

        Map<String, Object> reemplazos = new TreeMap<>();

        reemplazos.put("compania", compania);
        reemplazos.put("anio", anio);

        Map<String, Object> param = new TreeMap<>();

        Reporteador.resuelveConsulta("002035LisCompAfectInconsistentesContable",
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazos, param);

        try {
            archivoDescarga = JsfUtil.exportarStreamed(
                            "002035LisCompAfectInconsistentesContable", param,
                            ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public boolean verificaPeriodo() {
        Map<String, Object> paramEs = new TreeMap<>();
        paramEs.put(GeneralParameterEnum.COMPANIA.name(), compania);
        paramEs.put(GeneralParameterEnum.ANO.name(), anio);
        Registro rs;
        try {
            UrlBean urlRs = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            RevisarafectacionescntControladorUrlEnum.URL425
                                            .getValue());
            rs = RegistroConverter.toRegistro(
                            requestManager.get(urlRs.getUrl(), paramEs));

            if ((rs != null) && "C".equals(rs.getCampos().get("ESTADO"))) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB31"));
                return false;
            }

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.name(), compania);
            param.put(GeneralParameterEnum.ANO.name(), anio);
            List<Registro> rsList;

            UrlBean urlmesI = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            RevisarafectacionescntControladorUrlEnum.URL6111
                                            .getValue());
            rsList = RegistroConverter.toListRegistro(
                            requestManager.getList(urlmesI.getUrl(), param));

            String estado;
            for (Registro registro : rsList) {

                estado = ejbSysmanUtil.verificarEstadoPeriodoMensual(compania,
                                Integer.parseInt(anio),
                                Integer.parseInt(
                                                registro.getCampos()
                                                .get("NUMERO")

                                                .toString()),
                                Integer.parseInt(SessionUtil.getModulo()), 1);

                if ("C".equals(estado)) {
                    JsfUtil.agregarMensajeAlerta(
                                    idioma.getString("TB_TB992").replace(
                                                    "#mes#",
                                                    registro.getCampos()
                                                    .get("NUMERO")
                                                    .toString()));
                    return false;
                }

            }
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return true;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarAno()
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMesInicial()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getAnio()
    {
        return anio;
    }

    public void setAnio(String anio)
    {
        this.anio = anio;
    }

    public String getMesInicial()
    {
        return mesInicial;
    }

    public void setMesInicial(String mesInicial)
    {
        this.mesInicial = mesInicial;
    }

    public String getMesFinal()
    {
        return mesFinal;
    }

    public void setMesFinal(String mesFinal)
    {
        this.mesFinal = mesFinal;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public boolean isBloqueadoAceptar()
    {
        return bloqueadoAceptar;
    }

    public void setBloqueadoAceptar(boolean bloqueadoAceptar)
    {
        this.bloqueadoAceptar = bloqueadoAceptar;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaAno()
    {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno)
    {
        this.listaAno = listaAno;
    }

    public List<Registro> getListaMesInicial()
    {
        return listaMesInicial;
    }

    public void setListaMesInicial(List<Registro> listaMesInicial)
    {
        this.listaMesInicial = listaMesInicial;
    }

    public List<Registro> getListaMesFinal()
    {
        return listaMesFinal;
    }

    public void setListaMesFinal(List<Registro> listaMesFinal)
    {
        this.listaMesFinal = listaMesFinal;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}

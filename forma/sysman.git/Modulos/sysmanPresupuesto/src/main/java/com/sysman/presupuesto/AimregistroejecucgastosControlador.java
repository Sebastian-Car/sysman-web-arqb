package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
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
import com.sysman.presupuesto.enums.AimregistroejecucgastosControladorEnum;
import com.sysman.presupuesto.enums.AimregistroejecucgastosControladorUrlEnum;
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
 * @author ybecerra
 * @version 1, 05/07/2016
 * @version 2, 17/04/2017 modificado por jcrodriguez
 * descripcion:--depuracion del controlador --generacion de dss para
 * las consultas quemadas
 * @version 3, 12/06/2017 jrodriguezr Se refactoriza el c�digo: Se
 * pasa el numero del formulario al enumerado, se eliminan conexiones
 * y se ajustan metodos de generacion de reportes.
 */
@ManagedBean
@ViewScoped
public class AimregistroejecucgastosControlador extends BeanBaseModal
{
    /**
     * variable que alamcena la compa�ia
     */
    private final String compania;
    /**
     * variable que almacena el modulo
     */
    private final String modulo;
    /**
     * variable que almacena el estado miles
     */
    private boolean miles;
    /**
     * variable que almacena el estado codigo equivalente
     */
    private boolean codigoEquivalente;
    /**
     * variable que alamcena la cuenta inicial
     */
    private String cuentaInicial;
    /**
     * variable que almacena la cuenta final
     */
    private String cuentaFinal;
    /**
     * variable que almacena el mes
     */
    private int mes;
    /**
     * variable que almacena el a�o
     */
    private int ano;
    /**
     * variable que alamcena el nombre de la cuenta inicial
     */
    private String nombreCuentaInicial;
    /**
     * variable que almacena el nombre de la cuenta final
     */
    private String nombreCuentaFinal;
    /**
     * varible que alacmena el nivel
     */
    private int nivel;
    /**
     * variable que almacena el archivo de descarga
     */
    private StreamedContent archivoDescarga;
    /**
     * lista los meses
     */
    private List<Registro> listames;
    /**
     * lista los a�os
     */
    private List<Registro> listaAno;
    /**
     * lista la cuenta inicial
     */
    private RegistroDataModelImpl listaCuentaInicial;
    /**
     * variable que lista la cuenta final
     */
    private RegistroDataModelImpl listaCuentaFinal;

    @EJB

    private EjbSysmanUtilRemote ejbParametro;

    /**
     * Creates a new instance of AimregistroejecucgastosControlador
     */
    public AimregistroejecucgastosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.AIMREGISTROEJECUCGASTOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(AimregistroejecucgastosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * metodo que se llama al ejecutar el formulario
     */
    @PostConstruct
    public void inicializar()
    {
        abrirFormulario();
        // <CARGAR_LISTA>
        cargarListaAno();
        cargarListames();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
        // </CARGAR_LISTA_COMBO_GRANDE>

    }

    /**
     * metodo que se llama al abrir el formulario
     */
    @Override
    public void abrirFormulario()
    {
        ano = SysmanFunciones
                        .ano(new Date());
        nivel = 60;
        mes = SysmanFunciones
                        .mes(new Date());
    }

    /**
     * metodo que se llama al cargar la lista de meses
     */
    public void cargarListames()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(AimregistroejecucgastosControladorEnum.ANIO.getValue(), ano);

        try
        {
            listames = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AimregistroejecucgastosControladorUrlEnum.URL4897
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que se llama al cargar la lista de a�os
     */
    public void cargarListaAno()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AimregistroejecucgastosControladorUrlEnum.URL5604
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que se llama al carga la cuenta inicial
     */
    public void cargarListaCuentaInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AimregistroejecucgastosControladorUrlEnum.URL6411
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        AimregistroejecucgastosControladorEnum.ID.getValue());

    }

    /**
     * metodo que se llama al cargar la cuenta final
     */
    public void cargarListaCuentaFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AimregistroejecucgastosControladorUrlEnum.URL7704
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(AimregistroejecucgastosControladorEnum.CUENTAINICIAL
                        .getValue(), cuentaInicial);
        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        AimregistroejecucgastosControladorEnum.ID.getValue());
    }

    /**
     * metodo que se ejecuta cuando se presiona el boton pdf
     */
    public void oprimirPresentar()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * metodo que se ejecuta cuando se presiona el boton excel
     */
    public void oprimirExcel()
    {
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);

    }

    /**
     * metodo que contiene la logica para genera los reportes en
     * formato pdf y excel
     *
     * @param formato
     */
    private void generarInforme(ReportesBean.FORMATOS formato)
    {
        String apropiacionVigente;
        String compromisosAcum;
        String obligacionesAcum;
        String pagosAcum;
        String compromisosMes;
        String obligacionesMes;
        String pagosMes;
        String parReporte;
        try
        {
            if (miles)
            {

                apropiacionVigente = " ROUND((SUM(APROPIACION_DEBITO-APROPIACION_CREDITO) + SUM(V_SALDO_PLAN_PPTAL.ADICION) + \n"
                    +
                    "             SUM(V_SALDO_PLAN_PPTAL.TRASLADO_DEBITO)     +  SUM(V_SALDO_PLAN_PPTAL.REDUCCION)  + "
                    +
                    "             SUM(APLAZAM_DEBITO -APLAZAM_CREDITO)"
                    + "           )-\n" +
                    "            (SUM(V_SALDO_PLAN_PPTAL.TRASLADO_CREDITO)),-3"
                    + "         )/1000";
                compromisosAcum = "  ROUND( SUM(V_SALDO_PLAN_PPTAL.REG_CONTRACT  + V_SALDO_PLAN_PPTAL.REG_NO_CONTRACT + \n"
                    +
                    "               V_SALDO_PLAN_PPTAL.REG_REVERSION + V_SALDO_PLAN_PPTAL.MODIF_REG_CONT  + \n"
                    +
                    "               V_SALDO_PLAN_PPTAL.MODIF_REG_NOCONT),-3"
                    + "       )/1000";
                obligacionesAcum = " ROUND(SUM(REGISTRO_OBLIGACION + MODIF_REGISTRO_OBLIGACION),-3)/1000";
                pagosAcum = "    ROUND(SUM(V_SALDO_PLAN_PPTAL.EJE_PPT_DEBITO-V_SALDO_PLAN_PPTAL.EJE_PPT_CREDITO),-3)/1000";
                compromisosMes = " ROUND(V_SALDO_PLAN_PPTAL.REG_CONTRACT  + V_SALDO_PLAN_PPTAL.REG_NO_CONTRACT + \n"
                    +
                    "        V_SALDO_PLAN_PPTAL.REG_REVERSION + V_SALDO_PLAN_PPTAL.MODIF_REG_CONT + \n"
                    +
                    "        V_SALDO_PLAN_PPTAL.MODIF_REG_NOCONT,3)/1000";
                obligacionesMes = " ROUND(V_SALDO_PLAN_PPTAL.REGISTRO_OBLIGACION +  V_SALDO_PLAN_PPTAL.MODIF_REGISTRO_OBLIGACION,3)/1000";
                pagosMes = " ROUND((EJE_PPT_DEBITO - EJE_PPT_CREDITO),-3)/1000";
            }
            else
            {

                apropiacionVigente = "((SUM(APROPIACION_DEBITO-APROPIACION_CREDITO) + SUM(V_SALDO_PLAN_PPTAL.ADICION)     + \n"
                    +
                    "       SUM(V_SALDO_PLAN_PPTAL.TRASLADO_DEBITO)     +  SUM(V_SALDO_PLAN_PPTAL.REDUCCION)  + SUM(APLAZAM_DEBITO -APLAZAM_CREDITO))-\n"
                    +
                    "      (SUM(V_SALDO_PLAN_PPTAL.TRASLADO_CREDITO)))";
                compromisosAcum = "SUM(REG_CONTRACT + REG_NO_CONTRACT + REG_REVERSION + MODIF_REG_CONT + MODIF_REG_NOCONT)";
                obligacionesAcum = "SUM(REGISTRO_OBLIGACION + MODIF_REGISTRO_OBLIGACION)";
                pagosAcum = "SUM(V_SALDO_PLAN_PPTAL.EJE_PPT_DEBITO - V_SALDO_PLAN_PPTAL.EJE_PPT_CREDITO)";
                compromisosMes = "       V_SALDO_PLAN_PPTAL.REG_CONTRACT  + V_SALDO_PLAN_PPTAL.REG_NO_CONTRACT + \n"
                    +
                    "        V_SALDO_PLAN_PPTAL.REG_REVERSION + V_SALDO_PLAN_PPTAL.MODIF_REG_CONT + \n"
                    +
                    "        V_SALDO_PLAN_PPTAL.MODIF_REG_NOCONT";
                obligacionesMes = " V_SALDO_PLAN_PPTAL.REGISTRO_OBLIGACION +  V_SALDO_PLAN_PPTAL.MODIF_REGISTRO_OBLIGACION";
                pagosMes = "(EJE_PPT_DEBITO - EJE_PPT_CREDITO)";

            }

            HashMap<String, Object> reemplazar = new HashMap<>();

            reemplazar.put(AimregistroejecucgastosControladorEnum.APROPIACIONVIGENTE
                            .getValue(), apropiacionVigente);
            reemplazar.put(AimregistroejecucgastosControladorEnum.COMPROMISOSACUM
                            .getValue(), compromisosAcum);
            reemplazar.put(AimregistroejecucgastosControladorEnum.OBLIGACIONESACUM
                            .getValue(), obligacionesAcum);
            reemplazar.put(AimregistroejecucgastosControladorEnum.PAGOSACUM
                            .getValue(), pagosAcum);
            reemplazar.put(AimregistroejecucgastosControladorEnum.COMPROMISOSMES
                            .getValue(), compromisosMes);
            reemplazar.put(AimregistroejecucgastosControladorEnum.OBLIGACIONESMES
                            .getValue(), obligacionesMes);
            reemplazar.put(AimregistroejecucgastosControladorEnum.PAGOSMES
                            .getValue(), pagosMes);
            reemplazar.put(GeneralParameterEnum.ANO.getName().toLowerCase(),
                            ano);
            reemplazar.put(AimregistroejecucgastosControladorEnum.MES
                            .getValue(), mes);
            reemplazar.put(AimregistroejecucgastosControladorEnum.CUENTAINICIALL
                            .getValue(), "'" + cuentaInicial + "'");
            reemplazar.put(AimregistroejecucgastosControladorEnum.CUENTAFINAL
                            .getValue(), "'" + cuentaFinal + "'");
            reemplazar.put(AimregistroejecucgastosControladorEnum.NIVEL
                            .getValue(), nivel);
            reemplazar.put(AimregistroejecucgastosControladorEnum.MILES
                            .getValue(), miles ? "1" : "0");

            String contraloria = ejbParametro.consultarParametro(compania,
                            "CONTRALORIA DEPARTAMENTAL", modulo, new Date(),
                            false);

            String resolucionUno = ejbParametro.consultarParametro(compania,
                            "FIRMA1 EN RESOLUCION 036 ESPECIAL", modulo,
                            new Date(), false);

            String resolucionDos = ejbParametro.consultarParametro(compania,
                            "FIRMA2 EN RESOLUCION 036 ESPECIAL", modulo,
                            new Date(), false);

            String resolucionTres = ejbParametro.consultarParametro(compania,
                            "FIRMA3 EN RESOLUCION 036 ESPECIAL", modulo,
                            new Date(), false);

            String cargoResolucionUno = ejbParametro.consultarParametro(
                            compania, "CARGO1 EN RESOLUCION 036 ESPECIAL",
                            modulo, new Date(), false);

            String cargoResolucionDos = ejbParametro.consultarParametro(
                            compania, "CARGO2 EN RESOLUCION 036 ESPECIAL",
                            modulo, new Date(), false);

            String cargoResolucionTres = ejbParametro.consultarParametro(
                            compania, "CARGO3 EN RESOLUCION 036 ESPECIAL",
                            modulo, new Date(), false);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put(AimregistroejecucgastosControladorEnum.PR_CONTRALORIADEPARTAMENTAL
                            .getValue(), contraloria);
            parametros.put(AimregistroejecucgastosControladorEnum.PR_NOMBRECOMPANIA
                            .getValue(), SessionUtil.getCompaniaIngreso()
                                            .getNombre().toUpperCase());
            parametros.put(AimregistroejecucgastosControladorEnum.PR_NOMBREMES
                            .getValue(),
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mes]
                                            .toUpperCase());
            parametros.put(AimregistroejecucgastosControladorEnum.PR_FIRMARESOLUCION1
                            .getValue(), resolucionUno);
            parametros.put(AimregistroejecucgastosControladorEnum.PR_FIRMARESOLUCION2
                            .getValue(), resolucionDos);
            parametros.put(AimregistroejecucgastosControladorEnum.PR_FIRMARESOLUCION3
                            .getValue(), resolucionTres);
            parametros.put(AimregistroejecucgastosControladorEnum.PR_CARGORESOLUCION1
                            .getValue(), cargoResolucionUno);
            parametros.put(AimregistroejecucgastosControladorEnum.PR_CARGORESOLUCION2
                            .getValue(), cargoResolucionDos);
            parametros.put(AimregistroejecucgastosControladorEnum.PR_CARGORESOLUCION3
                            .getValue(), cargoResolucionTres);

            if (codigoEquivalente)
            {
                parReporte = AimregistroejecucgastosControladorEnum.NOMBREREOPRTE1
                                .getValue();
            }
            else
            {
                parReporte = AimregistroejecucgastosControladorEnum.NOMBREREPORTE2
                                .getValue();
            }
            Reporteador.resuelveConsulta(parReporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(parReporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
                        | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * metodo que se ejecuta al cambiar el a�o
     */
    public void cambiarAno()
    {
        cargarListames();
        cuentaInicial = null;
        nombreCuentaInicial = null;
        cuentaFinal = null;
        nombreCuentaFinal = null;
        cargarListaCuentaInicial();
    }

    /**
     * metodo que se ejecuta cuando se selecciona un registro de un
     * combo grande
     *
     * @param event
     */
    public void seleccionarFilaCuentaInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = registroAux.getCampos().get(
                        AimregistroejecucgastosControladorEnum.ID.getValue())
                        .toString();
        nombreCuentaInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
        cuentaFinal = null;
        nombreCuentaFinal = null;
        cargarListaCuentaFinal();
    }

    /**
     * metodo que se ejecuta cuando se selecciona un registro de un
     * combo grande
     *
     * @param event
     */
    public void seleccionarFilaCuentaFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = registroAux.getCampos().get(
                        AimregistroejecucgastosControladorEnum.ID.getValue())
                        .toString();
        nombreCuentaFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.NOMBRE.getName()).toString();
    }

    /**
     * metodos get y set
     *
     * @return
     */
    public boolean getMiles()
    {
        return miles;
    }

    public void setMiles(boolean miles)
    {
        this.miles = miles;
    }

    public boolean getCodigoEquivalente()
    {
        return codigoEquivalente;
    }

    public void setCodigoEquivalente(boolean codigoEquivalente)
    {
        this.codigoEquivalente = codigoEquivalente;
    }

    public String getCuentaInicial()
    {
        return cuentaInicial;
    }

    public void setCuentaInicial(String cuentaInicial)
    {
        this.cuentaInicial = cuentaInicial;
    }

    public String getCuentaFinal()
    {
        return cuentaFinal;
    }

    public void setCuentaFinal(String cuentaFinal)
    {
        this.cuentaFinal = cuentaFinal;
    }

    public int getMes()
    {
        return mes;
    }

    public void setMes(int mes)
    {
        this.mes = mes;
    }

    public int getAno()
    {
        return ano;
    }

    public void setAno(int ano)
    {
        this.ano = ano;
    }

    public String getNombreCuentaInicial()
    {
        return nombreCuentaInicial;
    }

    public void setNombreCuentaInicial(String nombreCuentaInicial)
    {
        this.nombreCuentaInicial = nombreCuentaInicial;
    }

    public String getNombreCuentaFinal()
    {
        return nombreCuentaFinal;
    }

    public void setNombreCuentaFinal(String nombreCuentaFinal)
    {
        this.nombreCuentaFinal = nombreCuentaFinal;
    }

    public int getNivel()
    {
        return nivel;
    }

    public void setNivel(int nivel)
    {
        this.nivel = nivel;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListames()
    {
        return listames;
    }

    public void setListames(List<Registro> listames)
    {
        this.listames = listames;
    }

    public List<Registro> getListaAno()
    {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno)
    {
        this.listaAno = listaAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCuentaInicial()
    {
        return listaCuentaInicial;
    }

    public void setListaCuentaInicial(
        RegistroDataModelImpl listaCuentaInicial)
    {
        this.listaCuentaInicial = listaCuentaInicial;
    }

    public RegistroDataModelImpl getListaCuentaFinal()
    {
        return listaCuentaFinal;
    }

    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal)
    {
        this.listaCuentaFinal = listaCuentaFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}

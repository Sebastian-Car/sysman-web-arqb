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
import com.sysman.presupuesto.enums.AimregistroejecucgastoscxpsControladorEnum;
import com.sysman.presupuesto.enums.AimregistroejecucgastoscxpsControladorUrlEnum;
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
 * @version 1, 06/07/2016
 * @author lcortes
 * @version 2, 06/12/2016 17:30:58 -- Modificado por lcortes
 * @version 3, 17/04/2017 -- Modificado por jcrodriguez
 * descripcion:--depuracion del controlador --creacion de dss para
 * consultas quemadas
 * 
 * @version 4.0, 13/06/2017, <strong>pespitia</strong>:<br>
 * Reemplazar numero del formulario por enumerado.<br>
 * Reemplazar los llamados a ConectorPool por el esquema
 * ConectorPool.ESQUEMA_SYSMAN.
 */
@ManagedBean
@ViewScoped
public class AimregistroejecucgastoscxpsControlador extends BeanBaseModal {
    /**
     * variable que almacena la compa�ia
     */
    private final String compania;
    /**
     * variable que almacena el modulo
     */
    private final String modulo;
    /**
     * variable que almacena el estado de miles
     */
    private boolean miles;
    /**
     * variable que almacena el estado de cuenta inicial
     */
    private String cuentaInicial;
    /**
     * variable que almacena la cuenta final
     */
    private String cuentaFinal;
    /**
     * variable que almacena el a�o
     */
    private int ano;
    /**
     * variable que almacena el mes
     */
    private int mes;
    /**
     * variable que almacena el nivel
     */
    private int nivel;
    /**
     * variable que almacena el encabezado
     */
    private String encabezado;
    /**
     * variable que almacena el titulo
     */
    private String titulo;
    /**
     * variable que almacena el archivo de descarga
     */
    private StreamedContent archivoDescarga;
    /**
     * variable que almacena el lista de a�os
     */
    private List<Registro> listaAno;
    /**
     * variable que almacena el lista de meses
     */
    private List<Registro> listaMes;
    /**
     * variable que almacena el lista de cuenta inicial
     */
    private RegistroDataModelImpl listaCuentaInicial;
    /**
     * variable que almacena el lista de cuenta final
     */
    private RegistroDataModelImpl listaCuentaFinal;

    @EJB

    private EjbSysmanUtilRemote ejbParametro;

    /**
     * Creates a new instance of
     * AimregistroejecucgastoscxpsControlador
     */
    public AimregistroejecucgastoscxpsControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        try {
            // 981
            numFormulario = GeneralCodigoFormaEnum.AIMREGISTROEJECUCGASTOSCXPS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(AimregistroejecucgastoscxpsControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    /**
     * metodo que se lanza cuando se inicializa el formulario
     */
    @PostConstruct
    public void inicializar() {
        abrirFormulario();
        cargarListaAno();
        cargarListaMes();
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        ano = SysmanFunciones
                        .ano(new Date());
        mes = SysmanFunciones
                        .mes(new Date());
        nivel = 60;
        if ("3041703".equals(SessionUtil.getMenuActual())) {
            encabezado = idioma
                            .getString(AimregistroejecucgastoscxpsControladorEnum.IDIOMA1
                                            .getValue());
            titulo = idioma.getString(
                            AimregistroejecucgastoscxpsControladorEnum.IDIOMA2
                                            .getValue());
        }
        else {

            encabezado = idioma
                            .getString(AimregistroejecucgastoscxpsControladorEnum.IDIOMA3
                                            .getValue());
            titulo = idioma.getString(
                            AimregistroejecucgastoscxpsControladorEnum.IDIOMA4
                                            .getValue());

        }

    }

    /**
     * metodo que carga la lista de a�os
     */
    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AimregistroejecucgastoscxpsControladorUrlEnum.URL4395
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que carga la lista de mes
     */
    public void cargarListaMes() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(AimregistroejecucgastoscxpsControladorEnum.ANIO.getValue(),
                        ano);
        try {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            AimregistroejecucgastoscxpsControladorUrlEnum.URL4902
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * metodo que carga lal ista de cuenta inicial
     */
    public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AimregistroejecucgastoscxpsControladorUrlEnum.URL5616
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, AimregistroejecucgastoscxpsControladorEnum.ID
                                        .getValue());

    }

    /**
     * metodo que carga lal ista de la cuenta final combo grande
     */
    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        AimregistroejecucgastoscxpsControladorUrlEnum.URL6657
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(AimregistroejecucgastoscxpsControladorEnum.CUENTAINICIAL
                        .getValue(), cuentaInicial);
        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, AimregistroejecucgastoscxpsControladorEnum.ID
                                        .getValue());
    }

    /**
     * metodo que se llama cuando se oprime el boton pdf
     */
    public void oprimirPresentar() {
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
    }

    /**
     * metodo que se llama cuando se oprime el boton excel
     */
    public void oprimirExcel() {
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL97);
    }

    /**
     * metodo que contiene la logica para generar los reportes en
     * excel o pdf
     * 
     * @param formato
     */
    public void generarInforme(ReportesBean.FORMATOS formato) {

        String apropiacionVigente;
        String obligacion;
        String pagosAcum;
        String pagosMes;
        String compromiso;
        String obligacionMes;
        String parReporte;

        if (miles) {

            apropiacionVigente = "  ROUND((SUM(APROPIACION_DEBITO-APROPIACION_CREDITO) + SUM(V_SALDO_PLAN_PPTAL.ADICION)+\n"
                +
                "              SUM(TRASLADO_DEBITO + APLAZAM_DEBITO) + SUM(V_SALDO_PLAN_PPTAL.REDUCCION))-\n"
                +
                "                             SUM(TRASLADO_CREDITO + APLAZAM_CREDITO),-3)/1000";
            obligacion = "  ROUND((SUM(V_SALDO_PLAN_PPTAL.REGISTRO_OBLIGACION) + SUM(V_SALDO_PLAN_PPTAL.MODIF_REGISTRO_OBLIGACION)),-3)/1000";
            pagosAcum = "  ROUND(SUM(EJE_PPT_DEBITO - EJE_PPT_CREDITO),-3)/1000";
            pagosMes = "  ROUND((EJE_PPT_DEBITO-EJE_PPT_CREDITO),-3)/1000";
            compromiso = "  ROUND(SUM(V_SALDO_PLAN_PPTAL.REG_CONTRACT)  + SUM(V_SALDO_PLAN_PPTAL.REG_NO_CONTRACT) + \n"
                +
                "     SUM(V_SALDO_PLAN_PPTAL.REG_REVERSION) + SUM(V_SALDO_PLAN_PPTAL.MODIF_REG_CONT)  + \n"
                +
                "     SUM(V_SALDO_PLAN_PPTAL.MODIF_REG_NOCONT),-3)/1000 ";
            obligacionMes = "ROUND(REGISTRO_OBLIGACION + MODIF_REGISTRO_OBLIGACION,-3)/1000";
        }
        else {
            apropiacionVigente = "(SUM(APROPIACION_DEBITO-APROPIACION_CREDITO) + SUM(V_SALDO_PLAN_PPTAL.ADICION)+ \n"
                +
                "      SUM(TRASLADO_DEBITO + APLAZAM_DEBITO) + SUM(V_SALDO_PLAN_PPTAL.REDUCCION))- \n"
                +
                "      SUM(TRASLADO_CREDITO + APLAZAM_CREDITO)";
            obligacion = "SUM(V_SALDO_PLAN_PPTAL.REGISTRO_OBLIGACION) + SUM(V_SALDO_PLAN_PPTAL.MODIF_REGISTRO_OBLIGACION)";
            pagosAcum = "SUM(EJE_PPT_DEBITO - EJE_PPT_CREDITO)";
            pagosMes = "(EJE_PPT_DEBITO-EJE_PPT_CREDITO)";
            compromiso = "      SUM(V_SALDO_PLAN_PPTAL.REG_CONTRACT)  + SUM(V_SALDO_PLAN_PPTAL.REG_NO_CONTRACT) + \r\n"
                +
                "   SUM(V_SALDO_PLAN_PPTAL.REG_REVERSION) + SUM(V_SALDO_PLAN_PPTAL.MODIF_REG_CONT)  + \r\n"
                +
                "   SUM(V_SALDO_PLAN_PPTAL.MODIF_REG_NOCONT) ";
            obligacionMes = "REGISTRO_OBLIGACION + MODIF_REGISTRO_OBLIGACION";

        }

        HashMap<String, Object> reemplazar = new HashMap<>();

        reemplazar.put(AimregistroejecucgastoscxpsControladorEnum.APROPIACIONVIGENTE
                        .getValue(), apropiacionVigente);
        reemplazar.put(AimregistroejecucgastoscxpsControladorEnum.OBLIGACION
                        .getValue(), obligacion);
        reemplazar.put(AimregistroejecucgastoscxpsControladorEnum.PAGOSACUM
                        .getValue(), pagosAcum);
        reemplazar.put(AimregistroejecucgastoscxpsControladorEnum.COMPROMISO
                        .getValue(), compromiso);
        reemplazar.put(GeneralParameterEnum.ANO.getName().toLowerCase(),
                        ano);
        reemplazar.put(AimregistroejecucgastoscxpsControladorEnum.CUENTAINICIALL
                        .getValue(), "'" + cuentaInicial + "'");
        reemplazar.put(AimregistroejecucgastoscxpsControladorEnum.CUENTAFINAL
                        .getValue(), "'" + cuentaFinal + "'");
        reemplazar.put(AimregistroejecucgastoscxpsControladorEnum.MES
                        .getValue(), mes);
        reemplazar.put(AimregistroejecucgastoscxpsControladorEnum.NIVEL
                        .getValue(), nivel);
        reemplazar.put(AimregistroejecucgastoscxpsControladorEnum.PAGOSMES
                        .getValue(), pagosMes);
        reemplazar.put(AimregistroejecucgastoscxpsControladorEnum.MILES
                        .getValue(), miles ? "1" : "0");
        reemplazar.put(AimregistroejecucgastoscxpsControladorEnum.OBLIGACIONMES
                        .getValue(), obligacionMes);

        try {
            String contraloria = ejbParametro.consultarParametro(compania,
                            "CONTRALORIA DEPARTAMENTAL", modulo, new Date(),
                            false);

            String resolucionUno = ejbParametro.consultarParametro(compania,
                            "FIRMA1 EN RESOLUCION 036 ESPECIAL", modulo,
                            new Date(), false);

            String resolucionDos = ejbParametro.consultarParametro(compania,
                            "FIRMA2 EN RESOLUCION 036 ESPECIAL", resolucionUno,
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
            parametros.put(AimregistroejecucgastoscxpsControladorEnum.PR_FORMATO
                            .getValue(),
                            miles ? "#,#00;(#,#00)" : "#,#00.00;(#,#00.00)");
            parametros.put(AimregistroejecucgastoscxpsControladorEnum.PR_CONTRALORIADEPARTAMENTAL
                            .getValue(), contraloria);
            parametros.put(AimregistroejecucgastoscxpsControladorEnum.PR_NOMBRECOMPANIA
                            .getValue(), SessionUtil.getCompaniaIngreso()
                                            .getNombre().toUpperCase());
            parametros.put(AimregistroejecucgastoscxpsControladorEnum.PR_NOMBREMES
                            .getValue(),
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mes]
                                            .toUpperCase());
            parametros.put(AimregistroejecucgastoscxpsControladorEnum.PR_FIRMARESOLUCION1
                            .getValue(), resolucionUno);
            parametros.put(AimregistroejecucgastoscxpsControladorEnum.PR_FIRMARESOLUCION2
                            .getValue(), resolucionDos);
            parametros.put(AimregistroejecucgastoscxpsControladorEnum.PR_FIRMARESOLUCION3
                            .getValue(), resolucionTres);
            parametros.put(AimregistroejecucgastoscxpsControladorEnum.PR_CARGORESOLUCION1
                            .getValue(), cargoResolucionUno);
            parametros.put(AimregistroejecucgastoscxpsControladorEnum.PR_CARGORESOLUCION2
                            .getValue(), cargoResolucionDos);
            parametros.put(AimregistroejecucgastoscxpsControladorEnum.PR_CARGORESOLUCION3
                            .getValue(), cargoResolucionTres);

            if ("3041703".equals(SessionUtil.getMenuActual())) {

                parReporte = AimregistroejecucgastoscxpsControladorEnum.NOMBREREOPRTE1
                                .getValue();
            }
            else {
                parReporte = AimregistroejecucgastoscxpsControladorEnum.NOMBREREPORTE2
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
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * metodo que se llama cuando se cambia un a�o
     */
    public void cambiarAno() {
        cuentaInicial = null;
        cuentaFinal = null;
        cargarListaCuentaInicial();
    }

    /**
     * metodo que se ejecuta cuando se selcciona un registro de un
     * combo grande
     * 
     * @param event
     */
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = registroAux.getCampos()
                        .get(AimregistroejecucgastoscxpsControladorEnum.ID
                                        .getValue())
                        .toString();
        cuentaFinal = null;
        cargarListaCuentaFinal();
    }

    /**
     * metodo que se ejecuta cuando se selcciona un registro de un
     * combo grande
     * 
     * @param event
     */
    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = registroAux.getCampos()
                        .get(AimregistroejecucgastoscxpsControladorEnum.ID
                                        .getValue())
                        .toString();
    }

    /**
     * metodos get y set
     * 
     * @return
     */
    public boolean getMiles() {
        return miles;
    }

    public void setMiles(boolean miles) {
        this.miles = miles;
    }

    public String getCuentaInicial() {
        return cuentaInicial;
    }

    public void setCuentaInicial(String cuentaInicial) {
        this.cuentaInicial = cuentaInicial;
    }

    public String getCuentaFinal() {
        return cuentaFinal;
    }

    public void setCuentaFinal(String cuentaFinal) {
        this.cuentaFinal = cuentaFinal;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public String getEncabezado() {
        return encabezado;
    }

    public void setEncabezado(String encabezado) {
        this.encabezado = encabezado;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public List<Registro> getListaMes() {
        return listaMes;
    }

    public void setListaMes(List<Registro> listaMes) {
        this.listaMes = listaMes;
    }

    public RegistroDataModelImpl getListaCuentaInicial() {
        return listaCuentaInicial;
    }

    public void setListaCuentaInicial(
        RegistroDataModelImpl listaCuentaInicial) {
        this.listaCuentaInicial = listaCuentaInicial;
    }

    public RegistroDataModelImpl getListaCuentaFinal() {
        return listaCuentaFinal;
    }

    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal) {
        this.listaCuentaFinal = listaCuentaFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}

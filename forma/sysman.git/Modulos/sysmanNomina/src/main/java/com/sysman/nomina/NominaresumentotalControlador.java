package com.sysman.nomina;

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
import com.sysman.nomina.enums.NominaresumentotalControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.sesion.SessionBean;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
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
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * @author acaceres
 * @version 1, 04/12/2015
 *
 *
 * Revision Sonar
 * @author ybecerra
 * @version 2, 17/03/2017
 *
 * Se eliminaron parametros de ActionEvent ac llamados en los metodos
 * de los botones
 * @author ybecerra
 * @version 3, 23/03/2017
 * 
 * @version 4, 17/10/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 *
 * @author obarragan
 * @version 3, 10/06/2019 - Se agrego opcion de imprimir header con
 * imagenes adicionales.
 */

@ManagedBean
@ViewScoped
public class NominaresumentotalControlador extends BeanBaseModal {

    private final String companiaNombre;
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    private final String modulo;
    /**
     * Constante definida para almacenar la cadena "fechaInicial"
     */
    private final String cFechaInicial;
    /**
     * Constante definida para almacenar la cadena "fechaFinal"
     */
    private final String cFechaFinal;
    /**
     * Constante definida para almacenar la cadena
     * "000423ResumenTotalDisponibilidadCC"
     */
    private final String cResumenTotal;
    /**
     * Constante definida para almacenar la cadena "FORMATO
     * DISPONIBILIDAD POR CENTROS DE COSTO"
     */
    private final String cFormatoDisponibilidad;
    /**
     * Constante definida para almacenar la cadena
     * "Resumen_Total_Disponibilidad_CC_YOPAL"
     */
    private final String cResumenTotalDis;
    private String nomReporte;
    // <DECLARAR_ATRIBUTOS>
    private String ano1;
    private String ano2;
    private String mes1;
    private String mes2;
    private String periodo1;
    private String periodo2;
    private String proceso;
    private String fondo;
    private String codigoFondo;
    private String codigoUbicacion;
    private String entre;
    private String nombreEstablecimiento;
    private String opcion;
    private String valorLetras;
    private Date fechaReporte;
    private boolean fondoVisible;
    private boolean ubicacionVisible;
    private String strSql;
    private String cargoJefePresupuesto;
    private boolean consultaReporte = false;
    private boolean validarTitulo = false;
    private boolean pdfVisible;
    private boolean mostrarCortolima = true;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAno1;
    private List<Registro> listaAno2;
    private List<Registro> listaMes1;
    private List<Registro> listaMes2;
    private List<Registro> listaPeriodo1;
    private List<Registro> listaPeriodo2;
    private List<Registro> listaProceso;
    private List<SelectItem> listaCortolima;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaNombre;
    private RegistroDataModelImpl listaFondo;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    private StreamedContent archivoDescarga;

    private String headerEspecial;

    private FacesContext context;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of NominaresumentotalControlador
     */
    public NominaresumentotalControlador() {
        super();
        companiaNombre = SessionUtil.getCompaniaIngreso().getNombre();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cFechaInicial = "fechaInicial";
        cFechaFinal = "fechaFinal";
        cResumenTotal = "000423ResumenTotalDisponibilidadCC";
        cFormatoDisponibilidad = "FORMATO DISPONIBILIDAD POR CENTROS DE COSTO";
        cResumenTotalDis = "Resumen_Total_Disponibilidad_CC_YOPAL";
        try {
            numFormulario = GeneralCodigoFormaEnum.NOMINARESUMENTOTAL_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(NominaresumentotalControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        proceso = (String) SessionUtil.getSessionVar("procesoNomina");
        ano1 = (String) SessionUtil.getSessionVar("anioNomina");
        ano2 = (String) SessionUtil.getSessionVar("anioNomina");
        mes1 = (String) SessionUtil.getSessionVar("mesNomina");
        mes2 = (String) SessionUtil.getSessionVar("mesNomina");
        periodo1 = (String) SessionUtil.getSessionVar("periodoNomina");
        periodo2 = (String) SessionUtil.getSessionVar("periodoNomina");
        fondoVisible = false;
        ubicacionVisible = false;
        pdfVisible = true;
        
        try {
			mostrarCortolima = "SI".equalsIgnoreCase(SysmanFunciones.nvlStr(consultarParametro("MOSTRAR OPCIONES PARA CORTOLIMA EN RESUMEN TOTAL DE NOMINA", false), "SI"));
		} catch (SystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        cargarListaAno1();
        cargarListaAno2();
        cargarListaMes1();
        cargarListaMes2();
        cargarListaPeriodo1();
        cargarListaPeriodo2();
        cargarListaProceso();
        cargarListaNombre();
        cargarListaFondo();
        abrirFormulario();
        fechaReporte = new Date();
        getCortolimaItems();

    }
    
    public void  getCortolimaItems() {
        List<SelectItem> items = new ArrayList<>();
        if (mostrarCortolima) {
            items.add(new SelectItem(11,  idioma.getString("ET_RD579")));
            items.add(new SelectItem(12,  idioma.getString("ET_RD580")));
        }
        listaCortolima = items;
    }

    public void cargarListaAno1() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaAno1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            NominaresumentotalControladorUrlEnum.URL6322
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAno2() {
        listaAno2 = listaAno1;
    }

    public void cargarListaMes1() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), ano1);

            listaMes1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            NominaresumentotalControladorUrlEnum.URL7253
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMes2() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), ano2);

            listaMes2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            NominaresumentotalControladorUrlEnum.URL7253
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaPeriodo1() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
            param.put(GeneralParameterEnum.ANO.getName(), ano1);
            param.put(GeneralParameterEnum.MES.getName(), mes1);

            listaPeriodo1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            NominaresumentotalControladorUrlEnum.URL5682
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaPeriodo2() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
            param.put(GeneralParameterEnum.ANO.getName(), ano2);
            param.put(GeneralParameterEnum.MES.getName(), mes2);

            listaPeriodo2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            NominaresumentotalControladorUrlEnum.URL5682
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaProceso() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            listaProceso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            NominaresumentotalControladorUrlEnum.URL10155
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaNombre() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NominaresumentotalControladorUrlEnum.URL10936
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaNombre = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NOMBRE");
    }

    public void cargarListaFondo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        NominaresumentotalControladorUrlEnum.URL11560
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaFondo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NOMBRE_FONDO_CESANTIAS");
    }

    public void cambiarMarco86() {
        // <CODIGO_DESARROLLADO>
        try {
            if (SysmanFunciones.validarVariableVacio(opcion)) {
                fondoVisible = false;
                ubicacionVisible = false;
                return;
            }
            if (!"3".equals(opcion) && !"7".equals(opcion)) {
                fondoVisible = ubicacionVisible = false;

            }

            if ("3".equals(opcion)) {
                String parametro;
                parametro = consultarParametro(cFormatoDisponibilidad, false);
                if (parametro.equals(cResumenTotalDis)) {
                    fondoVisible = false;
                    ubicacionVisible = true;
                }
                else {
                    fondoVisible = false;
                    ubicacionVisible = false;
                }

            }
            if ("7".equals(opcion)) {
                fondoVisible = true;
                ubicacionVisible = false;
            }
            
            if ("12".equals(opcion)) {
            	pdfVisible = false;
            }
            else {
            	pdfVisible = true;
            }
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void ejecutarrcAlerta() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarNombreReporte(FORMATOS.PDF);
    }

    private String fecInicial() {
        String fecInicial;
        fecInicial = SysmanFunciones.concatenar(ano1,
                        mes1.length() == 1 ? "0" + mes1 : mes1,
                        periodo1.length() == 1 ? "0" + periodo1 : periodo1);
        return fecInicial;
    }

    private String fecFinal() {
        String fecFinal;
        fecFinal = SysmanFunciones.concatenar(ano2,
                        mes2.length() == 1 ? "0" + mes2 : mes2,
                        periodo2.length() == 1 ? "0" + periodo2 : periodo2);
        return fecFinal;
    }

    private boolean validarFecha() {
        if (fecInicial().compareTo(fecFinal()) > 0) {
            JsfUtil.agregarMensajeError(
                            idioma.getString("TB_TB574"));
            return true;
        }
        return false;
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarNombreReporte(FORMATOS.EXCEL97);
    }

    public void cambiarAno1() {
        // <CODIGO_DESARROLLADO>
        mes1 = null;
        periodo1 = null;
        cargarListaMes1();
        cargarListaPeriodo1();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno2() {
        // <CODIGO_DESARROLLADO>
        mes2 = null;
        periodo2 = null;
        cargarListaMes2();
        cargarListaPeriodo2();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes1() {
        // <CODIGO_DESARROLLADO>
        periodo1 = null;
        cargarListaPeriodo1();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes2() {
        // <CODIGO_DESARROLLADO>
        periodo2 = null;
        cargarListaPeriodo2();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarProceso() {
        // <CODIGO_DESARROLLADO>
        ano1 = null;
        ano2 = null;
        mes1 = null;
        mes2 = null;
        periodo1 = null;
        periodo2 = null;
        fondo = "";
        nombreEstablecimiento = "";
        listaNombre = null;
        listaFondo = null;
        cargarListaAno1();
        cargarListaAno2();
        cargarListaMes1();
        cargarListaMes2();
        cargarListaPeriodo1();
        cargarListaPeriodo2();
        cargarListaNombre();
        cargarListaFondo();

        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaFondo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        fondo = registroAux.getCampos().get("NOMBRE_FONDO_CESANTIAS")
                        .toString();
        codigoFondo = registroAux.getCampos().get("FONDO_CESANTIAS").toString();
    }

    public void seleccionarFilaNombre(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        nombreEstablecimiento = registroAux.getCampos().get("NOMBRE")
                        .toString();
        codigoUbicacion = registroAux.getCampos().get("CODIGO").toString();
    }

    /**
     * @return
     */
    private String nombreReporte() {
        String nombreReporte = "";
        try {
            if (SysmanFunciones.validarVariableVacio(opcion)) {
                consultaReporte = true;
        		nombreReporte = SysmanFunciones.nvlStr(consultarParametro(
                        "FORMATO RESUMEN TOTAL NOMINA", false),
                        "000560ResumenTotal");
            }
            else if ("1".equals(opcion)) {
                nombreReporte = "000414RESUMENPORGRUPOCONTABLEPERSONAL";
            }
            else if ("2".equals(opcion)) {
                nombreReporte = "000411ResumenTotalFuenteRecurso";
            }
            else if ("4".equals(opcion)) {
                nombreReporte = "000434ResumenTotalDisponibilidad2";
                String parametro = consultarParametro(
                                "DISCRIMINAR RESUMENES NOMINA", true);
                if ("Resumen_Total".equals(nombreReporte)
                    && "SI".equals(parametro)) {
                    nombreReporte = "000435ResumenTotalCentral";
                }
            }
            else if ("11".equals(opcion)) {
                nombreReporte = "002419DisponibilidadCortolima";
            }
            generarNombreReporte();
        }
        catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        return nombreReporte;
    }

    /**
     * @return
     */
    public String generarNombreReporte() {
        if ("5".equals(opcion)) {
            nomReporte = "000408RESUMENPORESCALAFON";
        }
        else if ("6".equals(opcion)) {
            nomReporte = "000409RESUMENPORESCALAFONYNIVEL";
        }
        else if ("7".equals(opcion)) {
            nomReporte = "000413RESUMENFNA";
        }
        else if ("10".equals(opcion)) {
            nomReporte = "001711ResumenPATRONALESANEDETALLADO";
        }

        return nomReporte;
    }

    /**
     * Metodo llamado en el oprimir Presentar y Excel
     */
    public void generarNombreReporte(ReportesBean.FORMATOS formato) {

        setStrSql("");
        try {
            if (!nombreReporte().isEmpty()) {
                nomReporte = nombreReporte();
            }
            else if ("3".equals(opcion)) {
                String parametro = consultarParametro(cFormatoDisponibilidad,
                                false);
                if ("Resumen_Total_Disponibilidad_CC_GUAINIA".equals(parametro)
                    || "RESUMEN_TOTAL_DISPONIBILIDAD_CC".equals(parametro)) {
                    nomReporte = cResumenTotal;
                }
                if (cResumenTotalDis
                                .equals(parametro)
                    && SysmanFunciones.validarVariableVacio(codigoUbicacion)) {

                    ubicacionVisible = true;
                    fondoVisible = false;
                    JsfUtil.agregarMensajeAlerta(
                                    idioma.getString("TB_TB2593"));
                    return;

                }
                else if (cResumenTotalDis
                                .equals(parametro)) {
                    nomReporte = "000425ResumenTotalDisponibilidadCCYOPAL";
                }

                nomReporte = "000426ResumenTotalDisponibilidad1";

            }
            obtenerReporte(formato, nomReporte);
        }
        catch (SystemException ex) {
            Logger.getLogger(NominaresumentotalControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    private String consultarParametro(String nombre, boolean mayus)
                    throws SystemException {
        return ejbSysmanUtil.consultarParametro(compania, nombre, modulo,
                        new Date(), mayus);
    }

    /**
     * Metodo llamado en obtenerReporte
     *
     * @return
     */
    private String fechInicial() {
        String fecIni;
        fecIni = SysmanFunciones.concatenar(
                        proceso.length() == 1 ? "0" + proceso : proceso, ano1,
                        mes1.length() == 1 ? "0" + mes1 : mes1,
                        periodo1.length() == 1 ? "0" + periodo1 : periodo1);
        return fecIni;
    }

    /**
     * Metodo llamado en obtenerReporte
     *
     * @return
     */
    private String fechFinal() {
        String fecFinal;
        fecFinal = SysmanFunciones.concatenar(
                        proceso.length() == 1 ? "0" + proceso : proceso, ano2,
                        mes2.length() == 1 ? "0" + mes2 : mes2,
                        periodo2.length() == 1 ? "0" + periodo2 : periodo2);
        return fecFinal;

    }

    /**
     * Metodo llamado en obtenerReporte
     *
     * @param periodo
     * @return
     */
    private String periodo(String periodo) {
        String per;
        per = periodo.length() == 1 ? "0" + periodo : periodo;
        return per;
    }

    public boolean validarReporte(Map<String, Object> reemplazar) {
        try {
            if ("7".equals(opcion)) {
                if (SysmanFunciones.validarVariableVacio(fondo)) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2596"));
                    return true;
                }
                reemplazar.put("fondoCesantias", codigoFondo);
            }
            String parametro = consultarParametro(cFormatoDisponibilidad,
                            false);

            if ("3".equals(opcion) && cResumenTotalDis.equals(parametro)) {
                reemplazar.put("codigoEstablecimiento", codigoUbicacion);
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return false;
    }

    public void obtenerReporte(FORMATOS formatos, String reporte) {
        String encabezado;
        setContext(FacesContext.getCurrentInstance());
        try {
            if (validarFecha()) {
                return;
            }
            archivoDescarga = null;
            String fechaInicial = fechInicial();
            String fechaFinal = fechFinal();
            

            HashMap<String, Object> reemplazar = new HashMap<>();

            reemplazar.put(cFechaInicial, fechaInicial);
            reemplazar.put(cFechaFinal, fechaFinal);
            reemplazar.put("periodo1", periodo1);
            reemplazar.put("periodo2", periodo2);
            reemplazar.put("ano1", ano1);
            reemplazar.put("ano2", ano2);
            reemplazar.put("mes1", mes1);
            reemplazar.put("mes2", mes2);
            reemplazar.put("proceso", proceso);

            if (validarReporte(reemplazar)) {
                return;
            }

            Map<String, Object> parametros = new HashMap<>();
            entre = SysmanFunciones.concatenar("Entre: ",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mes1)],
                            " de ", ano1, " Periodo ", periodo(periodo1), " y ",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mes2)],
                            " de ", ano2, " Periodo ", periodo(periodo2));
            encabezado = SysmanFunciones.concatenar("Entre: ", "Per�odo ",
                            SysmanFunciones.initCap(
                                            service.buscarEnLista(periodo1,
                                                            GeneralParameterEnum.PERIODO
                                                                            .getName(),
                                                            "NOM_PERIODO",
                                                            listaPeriodo1)),
                            " de ",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mes1)],
                            " de ", ano1, " y Per�odo ",
                            SysmanFunciones.initCap(
                                            service.buscarEnLista(periodo2,
                                                            GeneralParameterEnum.PERIODO
                                                                            .getName(),
                                                            "NOM_PERIODO",
                                                            listaPeriodo2)),
                            " de ",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mes2)],
                            " de ", ano2);

            String nombreGerente = consultarParametro("NOMBRE DEL GERENTE",
                            false);

            String cargoGerente = consultarParametro("CARGO DEL GERENTE",
                            false);
            String nomCargoTesoreroPaga = consultarParametro(
                            "NOMBRE DEL CARGO TESORERO PAGADOR", false);
            String cargoJefeNomina = consultarParametro(
                            "CARGO DEL JEFE DE NOMINA", false);
            String cargoTesoreroPagador = consultarParametro(
                            "CARGO DEL TESORERO PAGADOR", false);
            String nombreJefeRecursosHumanos = consultarParametro(
                            "NOMBRE DEL JEFE DE RECURSOS HUMANOS", false);
            String nomQuienFirmaSolDisPresupuestal = consultarParametro(
                            "NOMBRE DE QUIEN FIRMA SOLICITUD DISPONIBILIDAD PRESUPUESTAL",
                            false);
            String cargoQuienLiquidaNomina = consultarParametro(
                            "CARGO DE QUIEN LIQUIDA NOMINA", false);
            String nomQuienLiquidaNomina = consultarParametro(
                            "NOMBRE DE QUIEN LIQUIDA NOMINA", false);
            String cargoQuienFirmaSolDispoPresu = consultarParametro(
                            "CARGO DE QUIEN FIRMA SOLICITUD DISPONIBILIDAD PRESUPUESTAL",
                            false);
            String nombreJefeTesoreroPresupuesto = consultarParametro(
                            "NOMBRE DE JEFE DE PRESUPUESTO", false);
            setCargoJefePresupuesto(consultarParametro(
                            "CARGO DEL JEFE DE PRESUPUESTO", false));
            // inicio dcastiblanco
            String nomQuienAutorizaNomina = consultarParametro(
                            "NOMBRE DE QUIEN AUTORIZA NOMINA", false);
            String cargoQuienAutorizaNomina = consultarParametro(
                            "CARGO DE QUIEN AUTORIZA NOMINA", false);
            String nomQuienRevisaNomina = consultarParametro(
                            "NOMBRE DE QUIEN REVISA NOMINA", false);
            String cargoQuienRevisaNomina = consultarParametro(
                            "CARGO DE QUIEN REVISA NOMINA", false);
            String elaboradoPor = consultarParametro(
                            "ELABORADO POR", false);
            String nombreJefeRecursos = consultarParametro(
                            "NOMBRE JEFE RECURSOS HUMANOS", false);
            String cargoJefeRecursos = consultarParametro(
                            "CARGO JEFE RECURSOS HUMANOS", false);
            String elaboro = consultarParametro(
                    "ELABORO RESUMEN TOTAL NOMINA", false);
            String jefeDesarrolloHumano = consultarParametro(
                    "NOMBRE JEFE DESARROLLO HUMANO", false);
            String cargoJefeDesarrolloHumano = consultarParametro(
                    "CARGO JEFE DESARROLLO HUMANO", false); 
            String jefeNomina = consultarParametro(
                    "NOMBRE JEFE NOMINA", false);
            String cargoResponsableNomina = consultarParametro(
                    "CARGO RESPONSABLE DE NOMINA", false);
            // fin dcastiblanco
            
            //INICIO (gportilla-10/03/2023)
            String nombreQuienFirmaNominaVB1 = consultarParametro(
                    "NOMBRE DE QUIEN FIRMA NOMINA VB1", false);
            String cargoQuienFirmaNominaVB1 = consultarParametro(
                    "CARGO DE QUIEN FIRMA NOMINA VB1", false);
            String nombreQuienFirmaNominaVB2 = consultarParametro(
                    "NOMBRE DE QUIEN FIRMA NOMINA VB2", false);
            String cargoQuienFirmaNominaVB2 = consultarParametro(
                    "CARGO DE QUIEN FIRMA NOMINA VB2", false);
            //FIN (gportilla-10/03/2023)
            
            
            //INICIO JM CC 1458 SINCHI 
            String nombreDelJefeDeRH = consultarParametro(
                    "NOMBRE DEL JEFE DE RECURSOS HUMANOS", false);
            String cargoDelJefeDeRH = consultarParametro(
                    "CARGO JEFE RECURSOS HUMANOS", false);
            String nombreSecretarioGeneral = consultarParametro(
                    "NOMBRE SECRETARIO GENERAL", false);
            String cargoSecretarioGeneral = consultarParametro(
                    "CARGO SECRETARIO GENERAL", false);
            //FIN JM CC 1458 SINCHI 

            headerEspecial = ejbSysmanUtil.consultarParametro(compania,
                            "FORMATOS ESPECIALES BUCARAMANGA", modulo,
                            new Date(),
                            true);
            String validarFirmas = ejbSysmanUtil.consultarParametro(compania,
                            "MOSTRAR FIRMAS FND", modulo,
                            new Date(),
                            true);

            String cargoJP = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO DEL JEFE DE PRESUPUESTO", modulo,
                            new Date(), false);

            String sticker = SessionUtil.getCompaniaIngreso().getRutaSticker();

            if ("890201222".equals(SessionUtil.getCompaniaIngreso().getNit())) {
                validarTitulo = "002".equals(
                                SessionUtil.getCompaniaIngreso().getCodigo())
                                    ? true
                                    : false;
            }

            parametros.put("PR_NOMBREEMPRESA", companiaNombre);
            parametros.put("PR_ELABORO_RESUMEN_TOTAL_NOMINA", elaboro);
            parametros.put("PR_ENTRE", entre);
            parametros.put("PR_ENCABEZADO", encabezado);
            parametros.put("PR_CARGO_DEL_JEFE_DE_PRESUPUESTO",
                            cargoJP);
            parametros.put("PR_NOMBRE_DE_QUIEN_FIRMA_SOLICITUD_DISPONIBILIDAD_PRESUPUESTAL",
                            nomQuienFirmaSolDisPresupuestal);
            parametros.put("PR_CARGO_DE_QUIEN_LIQUIDA_NOMINA",
                            cargoQuienLiquidaNomina);
            parametros.put("PR_NOMBRE_DE_QUIEN_LIQUIDA_NOMINA",
                            nomQuienLiquidaNomina);
            parametros.put("PR_CARGO_DE_QUIEN_FIRMA_SOLICITUD_DISPONIBILIDAD_PRESUPUESTAL",
                            cargoQuienFirmaSolDispoPresu);
            parametros.put("PR_NOMBRE_DEL_JEFE_DE_RECURSOS_HUMANOS",
                            nombreJefeRecursosHumanos);
            parametros.put("PR_CARGO_DEL_TESORERO_PAGADOR",
                            cargoTesoreroPagador);
            parametros.put("PR_ELABORADO_POR",
                            elaboradoPor);
            parametros.put("PR_NOMBRE_DEL_GERENTE", nombreGerente);
            parametros.put("PR_NOMBRE_DEL_CARGO_TESORERO_PAGADOR",
                            nomCargoTesoreroPaga);
            parametros.put("PR_CARGO_DEL_GERENTE", cargoGerente);
            parametros.put("PR_CARGO_DEL_JEFE_DE_NOMINA", cargoJefeNomina);
            parametros.put("PR_NOMBRE_DE_JEFE_DE_PRESUPUESTO",
                            nombreJefeTesoreroPresupuesto);
            parametros.put("PR_NOMBRE_DE_QUIEN_AUTORIZA_NOMINA",
                            nomQuienAutorizaNomina);
            parametros.put("PR_CARGO_DE_QUIEN_AUTORIZA_NOMINA",
                            cargoQuienAutorizaNomina);
            parametros.put("PR_NOMBRE_DE_QUIEN_REVISA_NOMINA",
                            nomQuienRevisaNomina);
            parametros.put("PR_CARGO_DE_QUIEN_REVISA_NOMINA",
                            cargoQuienRevisaNomina);
            parametros.put("PR_NOMBRE_JEFE_RECURSOS_HUMANOS",
                            nombreJefeRecursos);
            parametros.put("PR_CARGO_JEFE_RECURSOS_HUMANOS",
                            cargoJefeRecursos);

            parametros.put("PR_VALIDAR_FIRMAS",
                            validarFirmas.equals("SI") ? true : false);

            parametros.put("PR_HEADER_ESPECIAL",
                            headerEspecial.equals("SI") ? true : false);
            parametros.put("PR_IMAGEN_ESPECIAL", sticker);
            parametros.put("PR_VALIDAR_TITULO", validarTitulo);
            
            parametros.put("PR_NOMBRE_JEFE_DESARROLLO_HUMANO", jefeDesarrolloHumano);
            parametros.put("PR_CARGO_JEFE_DESARROLLO_HUMANO", cargoJefeDesarrolloHumano);
            parametros.put("PR_NOMBRE_JEFE_NOMINA", jefeNomina);
            parametros.put("PR_CARGO_RESPONSABLE_DE_NOMINA", cargoResponsableNomina);
            
            // IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz mu�oz)
            parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
            // FIN IMPLEMENTACION MARCA_BLANCA
            
            //INICIO (gportilla-10/03/2023)
            parametros.put("PR_NOMBRE_DE_QUIEN_FIRMA_NOMINA_VB1",
            		nombreQuienFirmaNominaVB1);
            parametros.put("PR_CARGO_DE_QUIEN_FIRMA_NOMINA_VB1",
            		cargoQuienFirmaNominaVB1);
            parametros.put("PR_NOMBRE_DE_QUIEN_FIRMA_NOMINA_VB2",
            		nombreQuienFirmaNominaVB2);
            parametros.put("PR_CARGO_DE_QUIEN_FIRMA_NOMINA_VB2",
            		cargoQuienFirmaNominaVB2);
            //FIN (gportilla-10/03/2023)
            
            //INICIO JM CC 1458 SINCHI 
            parametros.put("PR_NOMBREDELJEFEDERECURSOSHUMANOS",
            		nombreDelJefeDeRH);
            parametros.put("PR_CARGOJEFERECURSOSHUMANOS",
            		cargoDelJefeDeRH);
            parametros.put("PR_NOMBRE_SECRETARIO_GENERAL",
            		nombreSecretarioGeneral);
            parametros.put("PR_CARGO_SECRETARIO_GENERAL",
            		cargoSecretarioGeneral);
            //FIN JM CC 1458 SINCHI 
            
            
            generarInformeNomina(reemplazar, parametros, formatos, reporte);
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    public void generarInformeNomina(Map<String, Object> reemplazar,
        Map<String, Object> parametros,
        ReportesBean.FORMATOS formato, String reporte) {
        try {

            if ("8".equals(opcion) || "9".equals(opcion)) {
                if ("8".equals(opcion)) {
                    obtenerInformes("001709ResumenTotalDisponibilidadSALARIOSANE",
                                    "001748ResumenSalariosAneRubroPptal",
                                    reemplazar, parametros, formato);
                }

                if ("9".equals(opcion)) {
                    obtenerInformes("001710ResumenTotalDisponibilidadPATRONALESANE",
                                    "001752ResumenPatronalesAneRubroPptal",
                                    reemplazar, parametros, formato);
                }

            }
            else if ("12".equals(opcion)) {
               	archivoDescarga = null;
            	String sql = Reporteador.resuelveConsulta("800553ExogenasCortolima",
            			Integer.parseInt(SessionUtil.getModulo()),
            			reemplazar);

            	try
            	{
            		archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
            				ConectorPool.ESQUEMA_SYSMAN,
            				ReportesBean.FORMATOS.EXCEL, "ExogenasCORTOLIMA");
            	}
            	catch (JRException | IOException | SQLException | DRException
            			| SysmanException e)
            	{
            		logger.error(e.getMessage(), e);
            		JsfUtil.agregarMensajeError(e.getMessage());
            	}
            }
            else {

              /* Reporteador.resuelveConsulta(
                                consultaReporte ? "000560ResumenTotal"
                                    : reporte,
                                Integer.parseInt(modulo), reemplazar,
                                parametros);*/
               
               Reporteador.resuelveConsulta(reporte,
                       Integer.parseInt(SessionUtil.getModulo()),
                       reemplazar, parametros); 
            
            	
          
                archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
                consultaReporte = false;
            }
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void obtenerInformes(String informeUno, String informeDos,
        Map<String, Object> reemplazar, Map<String, Object> parametros,
        ReportesBean.FORMATOS formato) {
        String[] informe = new String[2];
        String[] nombresArchivos = new String[2];
        informe[0] = informeUno;
        informe[1] = informeDos;
        Reporteador.resuelveConsulta(informe[0],
                        Integer.valueOf(SessionUtil.getModulo()),
                        reemplazar, parametros);

        ByteArrayInputStream[] salidas = new ByteArrayInputStream[2];

        try {
            if (FORMATOS.PDF.equals(formato)) {
                salidas[0] = JsfUtil.serializarReporte(informe[0], parametros,
                                ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);

                Reporteador.resuelveConsulta(informe[1],
                                Integer.valueOf(SessionUtil.getModulo()),
                                reemplazar, parametros);

                salidas[1] = JsfUtil.serializarReporte(informe[1], parametros,
                                ConectorPool.ESQUEMA_SYSMAN, FORMATOS.PDF);

                nombresArchivos[0] = SysmanFunciones.concatenar(informeUno,
                                ".pdf");
                nombresArchivos[1] = SysmanFunciones.concatenar(informeDos,
                                ".pdf");
            }
            else {
                salidas[0] = JsfUtil.serializarReporte(informe[0], parametros,
                                ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL97);

                Reporteador.resuelveConsulta(informe[1],
                                Integer.valueOf(SessionUtil.getModulo()),
                                reemplazar, parametros);

                salidas[1] = JsfUtil.serializarReporte(informe[1], parametros,
                                ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL97);

                nombresArchivos[0] = SysmanFunciones.concatenar(informeUno,
                                ".xls");
                nombresArchivos[1] = SysmanFunciones.concatenar(informeDos,
                                ".xls");
            }

            archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(
                            salidas, nombresArchivos);
        }
        catch (JRException | IOException | SysmanException | DRException
                        | SQLException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    public boolean isFondoVisible() {
        return fondoVisible;
    }

    public void setFondoVisible(boolean fondoVisible) {
        this.fondoVisible = fondoVisible;
    }

    public boolean isUbicacionVisible() {
        return ubicacionVisible;
    }

    public void setUbicacionVisible(boolean ubicacionVisible) {
        this.ubicacionVisible = ubicacionVisible;
    }

    public String getEntre() {
        return entre;
    }

    public void setEntre(String entre) {
        this.entre = entre;
    }

    public String getValorLetras() {
        return valorLetras;
    }

    public void setValorLetras(String valorLetras) {
        this.valorLetras = valorLetras;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public Date getFechaReporte() {
        return fechaReporte;
    }

    public void setFechaReporte(Date fechaReporte) {
        this.fechaReporte = fechaReporte;
    }

    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    public String getAno1() {
        return ano1;
    }

    public void setAno1(String ano1) {
        this.ano1 = ano1;
    }

    public String getAno2() {
        return ano2;
    }

    public void setAno2(String ano2) {
        this.ano2 = ano2;
    }

    public String getMes1() {
        return mes1;
    }

    public void setMes1(String mes1) {
        this.mes1 = mes1;
    }

    public String getMes2() {
        return mes2;
    }

    public void setMes2(String mes2) {
        this.mes2 = mes2;
    }

    public String getPeriodo1() {
        return periodo1;
    }

    public void setPeriodo1(String periodo1) {
        this.periodo1 = periodo1;
    }

    public String getPeriodo2() {
        return periodo2;
    }

    public void setPeriodo2(String periodo2) {
        this.periodo2 = periodo2;
    }

    public String getProceso() {
        return proceso;
    }

    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    public String getFondo() {
        return fondo;
    }

    public void setFondo(String fondo) {
        this.fondo = fondo;
    }

    public String getNombreEstablecimiento() {
        return nombreEstablecimiento;
    }

    public void setNombreEstablecimiento(String nombreEstablecimiento) {
        this.nombreEstablecimiento = nombreEstablecimiento;
    }

    public List<Registro> getListaAno1() {
        return listaAno1;
    }

    public void setListaAno1(List<Registro> listaAno1) {
        this.listaAno1 = listaAno1;
    }

    public List<Registro> getListaAno2() {
        return listaAno2;
    }

    public void setListaAno2(List<Registro> listaAno2) {
        this.listaAno2 = listaAno2;
    }

    public List<Registro> getListaMes1() {
        return listaMes1;
    }

    public void setListaMes1(List<Registro> listaMes1) {
        this.listaMes1 = listaMes1;
    }

    public List<Registro> getListaMes2() {
        return listaMes2;
    }

    public void setListaMes2(List<Registro> listaMes2) {
        this.listaMes2 = listaMes2;
    }

    public List<Registro> getListaPeriodo1() {
        return listaPeriodo1;
    }

    public void setListaPeriodo1(List<Registro> listaPeriodo1) {
        this.listaPeriodo1 = listaPeriodo1;
    }

    public List<Registro> getListaPeriodo2() {
        return listaPeriodo2;
    }

    public void setListaPeriodo2(List<Registro> listaPeriodo2) {
        this.listaPeriodo2 = listaPeriodo2;
    }

    public List<Registro> getListaProceso() {
        return listaProceso;
    }

    public void setListaProceso(List<Registro> listaProceso) {
        this.listaProceso = listaProceso;
    }

    public RegistroDataModelImpl getListaNombre() {
        return listaNombre;
    }

    public void setListaNombre(RegistroDataModelImpl listaNombre) {
        this.listaNombre = listaNombre;
    }

    public RegistroDataModelImpl getListaFondo() {
        return listaFondo;
    }

    public void setListaFondo(RegistroDataModelImpl listaFondo) {
        this.listaFondo = listaFondo;
    }

    public String getCodigoFondo() {
        return codigoFondo;
    }

    public void setCodigoFondo(String codigoFondo) {
        this.codigoFondo = codigoFondo;
    }
    
    
    public List<SelectItem> getListaCortolima() {
        return listaCortolima;
    }

    public void seListaCortolima(List<SelectItem> listaCortolima) {
        this.listaCortolima = listaCortolima;
    }
    

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    public String getStrSql() {
        return strSql;
    }

    public void setStrSql(String strSql) {
        this.strSql = strSql;
    }

    public FacesContext getContext() {
        return context;
    }

    public void setContext(FacesContext context) {
        this.context = context;
    }

    public String getCargoJefePresupuesto() {
        return cargoJefePresupuesto;
    }

    public void setCargoJefePresupuesto(String cargoJefePresupuesto) {
        this.cargoJefePresupuesto = cargoJefePresupuesto;
    }
    
    public boolean isPdfVisible() {
        return pdfVisible;
    }

    public void setPdfVisible(boolean pdfVisible) {
        this.pdfVisible = pdfVisible;
    }
    
    public boolean isMostrarCortolima() {
        return mostrarCortolima;
    }

    public void setMostrarCortolima(boolean mostrarCortolima) {
        this.mostrarCortolima = mostrarCortolima;
    }

}

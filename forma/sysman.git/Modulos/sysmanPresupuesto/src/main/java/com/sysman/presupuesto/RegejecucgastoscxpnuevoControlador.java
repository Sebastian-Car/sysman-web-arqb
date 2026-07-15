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
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.RegejecucgastoscxpnuevoControladorEnum;
import com.sysman.presupuesto.enums.RegejecucgastoscxpnuevoControladorUrlEnum;
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
 * @version 1, 19/07/2016
 * 
 * @version 2, 20/04/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 * 
 * @author jlramirez
 * @version 3, 24/04/2017, Manejo de EJBs
 * 
 * @author eamaya
 * @version 4.0, 13/06/2017 Se cambió el llamado del código del
 * formulario y actualización de ConnectorPool
 * 
 */
@ManagedBean
@ViewScoped
public class RegejecucgastoscxpnuevoControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    private boolean indicador;
    private boolean enMiles;
    private String cuentaInicial;
    private String cuentaFinal;
    private int ano;
    private int mes;
    private int nivel;
    private String conSeccion;
    private String conUnidad;
    private String nombreCuentaIni;
    private String nombreCuentaFin;
    private StreamedContent archivoDescarga;
    @EJB
    private EjbSysmanUtilRemote sysmanUtil;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAno;
    private List<Registro> listaMes;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of RegejecucgastoscxpnuevoControlador
     */
    public RegejecucgastoscxpnuevoControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.REGEJECUCGASTOSCXPNUEVO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            nivel = 6;

            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(RegejecucgastoscxpnuevoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        abrirFormulario();
        // <CARGAR_LISTA>
        cargarListaAno();
        cargarListaMes();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaInicial();
        cargarListaCuentaFinal();
        // </CARGAR_LISTA_COMBO_GRANDE>
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        ano = SysmanFunciones
                        .ano(new Date());
        mes = SysmanFunciones
                        .mes(new Date());
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaAno() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RegejecucgastoscxpnuevoControladorUrlEnum.URL4104
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMes() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), ano);
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RegejecucgastoscxpnuevoControladorUrlEnum.URL4506
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RegejecucgastoscxpnuevoControladorUrlEnum.URL5037
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.NATURALEZA.getName(), "D");

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    public void cargarListaCuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RegejecucgastoscxpnuevoControladorUrlEnum.URL6062
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(RegejecucgastoscxpnuevoControladorEnum.PARAM0.getValue(),
                        cuentaInicial);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.NATURALEZA.getName(), "D");

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    public void obtenerReporte(FORMATOS formatos) {

        String lblSeccion = "";
        String lblUnidad = "";
        String parSeccionInfo = "";
        String connSeccion = "";
        String connUnidad = "";
        boolean estado = true;
        try {
            parSeccionInfo = sysmanUtil.consultarParametro(compania,
                            "SECCION EN INFORMES RESOLUCION 036", modulo,
                            new Date(), true);
            connSeccion = sysmanUtil.consultarParametro(
                            compania, "SECCION 036", modulo, new Date(), true);
            connUnidad = sysmanUtil.consultarParametro(compania,
                            "UNIDAD EJECUTORA 036", modulo,
                            new Date(), true);

            if (parSeccionInfo == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB39"));
                estado = false;
            }
            else if (connSeccion == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB41"));
                estado = false;
            }
            else if (connUnidad == null) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB42"));
                estado = false;
            }

            if (estado) {
                if ("SI".equals(parSeccionInfo)) {

                    if (!connSeccion.isEmpty()) {
                        lblSeccion = idioma.getString("TG_SECCION");
                    }
                    else {
                        lblSeccion = "";
                    }

                    if (!connUnidad.isEmpty()) {
                        lblUnidad = idioma.getString("TG_UNIDAD_EJECUTORA");
                    }
                    else {
                        lblUnidad = "";
                    }
                }
                else {
                    lblSeccion = "";
                    lblUnidad = "";
                    connSeccion = "";
                    connUnidad = "";
                }

                HashMap<String, Object> reemplazar = new HashMap<>();
                reemplazar.put("mes", mes);
                reemplazar.put("ano", ano);
                reemplazar.put("cuentaInicial", "'" + cuentaInicial + "'");
                reemplazar.put("cuentaFinal", "'" + cuentaFinal + "'");
                reemplazar.put("nivel", nivel);
                reemplazar.put("miles", enMiles ? "1" : "0");

                // MANEJO DE PARAMETROS DE REEMPLAZO
                Map<String, Object> parametros = new HashMap<>();
                String cargoDosResolucion = sysmanUtil.consultarParametro(
                                compania, "CARGO2 EN RESOLUCION 036", modulo,
                                new Date(), true);
                String firmaUnoResolucion = sysmanUtil.consultarParametro(
                                compania,
                                "FIRMA1 EN RESOLUCION 036", modulo,
                                new Date(), true);
                String firmaDosResolucion = sysmanUtil.consultarParametro(
                                compania,
                                "FIRMA2 EN RESOLUCION 036", modulo,
                                new Date(), true);
                String cedulaUnoResolucion = sysmanUtil.consultarParametro(
                                compania,
                                "CEDULA1 EN RESOLUCION 036", modulo,
                                new Date(), true);
                String cedulaDosResolucion = sysmanUtil.consultarParametro(
                                compania,
                                "CEDULA2 EN RESOLUCION 036", modulo,
                                new Date(), true);
                String cargoUnoResolucion = sysmanUtil.consultarParametro(
                                compania,
                                "CARGO1 EN RESOLUCION 036", modulo,
                                new Date(), true);

                if (cargoDosResolucion == null) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB915"));
                    estado = false;
                }
                else if (firmaUnoResolucion == null) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB916"));
                    estado = false;
                }
                else if (firmaDosResolucion == null) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB926"));
                    estado = false;
                }
                else if (cedulaUnoResolucion == null) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB927"));
                    estado = false;
                }
                else if (cedulaDosResolucion == null) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB928"));
                    estado = false;
                }
                else if (cargoUnoResolucion == null) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB929"));
                    estado = false;
                }
                // MANEJO DE PARAMETROS DEL REPORTE
                if (estado) {
                    parametros.put("PR_ANO", ano);
                    parametros.put("PR_NOMBRECOMPANIA",
                                    SessionUtil.getCompaniaIngreso()
                                                    .getNombre());
                    parametros.put("PR_MES",
                                    SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mes]);
                    parametros.put("PR_CARGO1_EN_RESOLUCION_036",
                                    cargoUnoResolucion);
                    parametros.put("PR_CARGO2_EN_RESOLUCION_036",
                                    cargoDosResolucion);
                    parametros.put("PR_FIRMA1_EN_RESOLUCION_036",
                                    firmaUnoResolucion);
                    parametros.put("PR_FIRMA2_EN_RESOLUCION_036",
                                    firmaDosResolucion);
                    parametros.put("PR_CEDULA1_EN_RESOLUCION_036",
                                    cedulaUnoResolucion);
                    parametros.put("PR_CEDULA2_EN_RESOLUCION_036",
                                    cedulaDosResolucion);
                    parametros.put("PR_CONSECCION", connSeccion);
                    parametros.put("PR_CONUNIDAD", connUnidad);
                    parametros.put("PR_LBLSECCION_CAPTION", lblSeccion);
                    parametros.put("PR_LBLUNIDAD_CAPTION", lblUnidad);

                    Reporteador.resuelveConsulta(
                                    "001026REGISTROEJECUCGASTOSCXP036",
                                    Integer.valueOf(modulo), reemplazar,
                                    parametros);
                    if (indicador) {

                        archivoDescarga = JsfUtil.exportarStreamed(
                                        "001026REGISTROEJECUCGASTOSCXP036",
                                        parametros,
                                        ConectorPool.ESQUEMA_SYSMAN, formatos);
                    }
                    else {

                        archivoDescarga = JsfUtil.exportarStreamed(
                                        "001032REGISTROEJECUCGASTOSCXP036SO",
                                        parametros, ConectorPool.ESQUEMA_SYSMAN,
                                        formatos);

                    }
                }

            }

        }
        catch (JRException | IOException | SysmanException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        obtenerReporte(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        cuentaInicial = "";
        cuentaFinal = "";
        nombreCuentaIni = "";
        nombreCuentaFin = "";
        cargarListaCuentaInicial();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = registroAux.getCampos().get("CODIGO").toString();
        nombreCuentaIni = registroAux.getCampos().get("NOMBRE").toString();
        cuentaFinal = "";
        nombreCuentaFin = "";
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = registroAux.getCampos().get("CODIGO").toString();
        nombreCuentaFin = registroAux.getCampos().get("NOMBRE").toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>

    public boolean isIndicador() {
        return indicador;
    }

    public boolean isEnMiles() {
        return enMiles;
    }

    public void setEnMiles(boolean enMiles) {
        this.enMiles = enMiles;
    }

    public void setIndicador(boolean indicador) {
        this.indicador = indicador;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
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

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public String getConSeccion() {
        return conSeccion;
    }

    public void setConSeccion(String conSeccion) {
        this.conSeccion = conSeccion;
    }

    public String getConUnidad() {
        return conUnidad;
    }

    public void setConUnidad(String conUnidad) {
        this.conUnidad = conUnidad;
    }

    public String getNombreCuentaIni() {
        return nombreCuentaIni;
    }

    public void setNombreCuentaIni(String nombreCuentaIni) {
        this.nombreCuentaIni = nombreCuentaIni;
    }

    public String getNombreCuentaFin() {
        return nombreCuentaFin;
    }

    public void setNombreCuentaFin(String nombreCuentaFin) {
        this.nombreCuentaFin = nombreCuentaFin;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
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

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>

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

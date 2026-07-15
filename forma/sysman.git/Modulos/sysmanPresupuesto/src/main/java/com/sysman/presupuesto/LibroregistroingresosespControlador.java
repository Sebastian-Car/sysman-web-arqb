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
import com.sysman.presupuesto.enums.LibroregistroingresosespControladorEnum;
import com.sysman.presupuesto.enums.LibroregistroingresosespControladorUrlEnum;
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
 * @author dsuesca
 * @version 1, 06/07/2016
 * @version 2, 18/04/2017 jrodriguezr Se refactoriza el codigo SQL de
 * las listas para utilizar dss.
 * @version 3, 24/04/2017 jrodriguezr Se refactoriza el codigo
 * ajustando los llamados a funciones, procedimientos y metodos de la
 * clase Acciones a llamados a EJB.
 * 
 * @author eamaya
 * @version 3.1, 28/12/2017 Se hicieron ajustes al metodo
 * cargarParametros() para que liste el combo inicial dado qu este
 * necesita de la naturaleza,Se cambio numero de formularo por enum y
 * se borro la constante de mensajeError porque no se utiliza
 */
@ManagedBean
@ViewScoped
public class LibroregistroingresosespControlador extends BeanBaseModal {
    private final String compania;

    // <DECLARAR_ATRIBUTOS>
    private String cuentaInicial;
    private String cuentaFinal;
    private String anio;
    private String mes;
    private StreamedContent archivoDescarga;
    private String encabezado;
    private String titulo;
    private String naturaleza;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    private String firma1Libro;
    private String cargo1Libro;
    private String firma2Libro;
    private String cargo2Libro;
    private boolean manejAuxiliar;
    private boolean visibleGastos;
    private boolean conaplazamiento;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaANIO;
    private List<Registro> listaMES;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listacuentaInicial;
    private RegistroDataModelImpl listacuentaFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    private String firma1Ejecucion;
    private String cargo1Ejecucion;
    private String firma2Ejecucion;
    private String cargo2Ejecucion;
    private String firma3Ejecucion;
    private String cargo3Ejecucion;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of LibroregistroingresosespControlador
     */
    public LibroregistroingresosespControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.LIBROREGISTROINGRESOSESP_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            anio = String.valueOf(SysmanFunciones.ano(new Date()));
            mes = String.valueOf(SysmanFunciones.mes(new Date()));
            cuentaInicial = "0";
            cuentaFinal = "9999999999999999";

            // </INI_ADICIONAL>
        } catch (Exception ex) {
            Logger.getLogger(
                            LibroregistroingresosespControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    public void cargarParametros() {
        try {
            manejAuxiliar = "SI".equals(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "MANEJA AUXILIAR POR FUENTE EN PRESUPUESTO",
                                            SessionUtil.getModulo(),
                                            new Date(), true), "NO")
                            .toString());
            if ("3040509".equals(SessionUtil.getMenuActual())) {
                titulo = idioma.getString("TB_TB325");
                encabezado = "Libro registro de ingresos (Especial)";
                naturaleza = "C";
                firma1Libro = SysmanFunciones
                                .nvl(ejbSysmanUtil.consultarParametro(compania,
                                                "FIRMA1 EN LIBRO REGISTRO INGRESOS",
                                                SessionUtil.getModulo(),
                                                new Date(), true), "")
                                .toString();

                cargo1Libro = SysmanFunciones
                                .nvl(ejbSysmanUtil.consultarParametro(compania,
                                                "CARGO1 EN LIBRO REGISTRO INGRESOS",
                                                SessionUtil.getModulo(),
                                                new Date(), true), "")
                                .toString();
                firma2Libro = SysmanFunciones
                                .nvl(ejbSysmanUtil.consultarParametro(compania,
                                                "FIRMA2 EN LIBRO REGISTRO INGRESOS",
                                                SessionUtil.getModulo(),
                                                new Date(), true), "")
                                .toString();
                cargo2Libro = SysmanFunciones
                                .nvl(ejbSysmanUtil.consultarParametro(compania,
                                                "CARGO2 EN LIBRO REGISTRO INGRESOS",
                                                SessionUtil.getModulo(),
                                                new Date(), true), "")
                                .toString();
                visibleGastos = false;
            }
            if ("3040502".equals(SessionUtil.getMenuActual())) {
                titulo = idioma.getString("TB_TB337");
                encabezado = idioma.getString("TB_TB3095");
                naturaleza = "D";
                firma1Ejecucion = SysmanFunciones
                                .nvl(ejbSysmanUtil.consultarParametro(compania,
                                                "FIRMA1 EJECUCION PRESUPUESTAL DE GASTOS",
                                                SessionUtil.getModulo(),
                                                new Date(), true), "")
                                .toString();
                cargo1Ejecucion = SysmanFunciones
                                .nvl(ejbSysmanUtil.consultarParametro(compania,
                                                "CARGO1 EJECUCION PRESUPUESTAL DE GASTOS",
                                                SessionUtil.getModulo(),
                                                new Date(), true), "")
                                .toString();
                firma2Ejecucion = SysmanFunciones
                                .nvl(ejbSysmanUtil.consultarParametro(compania,
                                                "FIRMA2 EJECUCION PRESUPUESTAL DE GASTOS",
                                                SessionUtil.getModulo(),
                                                new Date(), true), "")
                                .toString();
                cargo2Ejecucion = SysmanFunciones
                                .nvl(ejbSysmanUtil.consultarParametro(compania,
                                                "CARGO2 EJECUCION PRESUPUESTAL DE GASTOS",
                                                SessionUtil.getModulo(),
                                                new Date(), true), "")
                                .toString();
                firma3Ejecucion = SysmanFunciones
                                .nvl(ejbSysmanUtil.consultarParametro(compania,
                                                "FIRMA3 EJECUCION PRESUPUESTAL DE GASTOS",
                                                SessionUtil.getModulo(),
                                                new Date(), true), "")
                                .toString();
                cargo3Ejecucion = SysmanFunciones
                                .nvl(ejbSysmanUtil.consultarParametro(compania,
                                                "CARGO3 EJECUCION PRESUPUESTAL DE GASTOS",
                                                SessionUtil.getModulo(),
                                                new Date(), true), "")
                                .toString();
                visibleGastos = true;
            }
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        cargarListacuentaInicial();
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaANIO();
        cargarListaMES();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListacuentaInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        cargarParametros();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaANIO() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaANIO = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LibroregistroingresosespControladorUrlEnum.URL9385
                                                                            .getValue())
                                            .getUrl(), param));
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMES() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        try {
            listaMES = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LibroregistroingresosespControladorUrlEnum.URL9753
                                                                            .getValue())
                                            .getUrl(), param));
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListacuentaInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LibroregistroingresosespControladorUrlEnum.URL10219
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.NATURALEZA.getName(), naturaleza);

        listacuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    public void cargarListacuentaFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LibroregistroingresosespControladorUrlEnum.URL11137
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.NATURALEZA.getName(), naturaleza);
        param.put(LibroregistroingresosespControladorEnum.CUENTAINICIAL
                        .getValue(), cuentaInicial);

        listacuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPDF() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        getInforme(FORMATOS.PDF);

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirEXCEL() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        getInforme(FORMATOS.EXCEL97);

        // </CODIGO_DESARROLLADO>
    }

    private void getInforme(FORMATOS formato) {
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("anio", anio);
        reemplazar.put("cuentaInicial", cuentaInicial);
        reemplazar.put("cuentaFinal", cuentaFinal);
        reemplazar.put("mes", mes);
        // MANEJO DE PARAMETROS DE REEMPLAZO
        Map<String, Object> parametros = new HashMap<>();
        String consulta = "000979LIBROREGISTROINGRESOSESP";
        String reporte = consulta;
        // MANEJO DE PARAMETROS DEL REPORTE

        parametros.put("PR_ANIO", anio);
        parametros.put("PR_NOMBREDEMES",
                        SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                        .parseInt(mes)].toUpperCase());

        if ("3040509".equals(SessionUtil.getMenuActual())) {
            parametros.put("PR_CARGO2_EN_LIBRO_REGISTRO_INGRESOS",
                            cargo2Libro);
            parametros.put("PR_FIRMA1_EN_LIBRO_REGISTRO_INGRESOS",
                            firma1Libro);
            parametros.put("PR_FIRMA2_EN_LIBRO_REGISTRO_INGRESOS",
                            firma2Libro);
            parametros.put("PR_CARGO1_EN_LIBRO_REGISTRO_INGRESOS",
                            cargo1Libro);
        }
        if ("3040502".equals(SessionUtil.getMenuActual())) {
            consulta = "000981LisEjecPptalGas";
            reporte = consulta;
            if (manejAuxiliar) {
                reporte = "000981LisEjecPptalGasFuente";
            }
            parametros.put("PR_CARGO3_EJECUCION_PRESUPUESTAL_DE_GASTOS",
                            cargo3Ejecucion);
            parametros.put("PR_FIRMA2_EJECUCION_PRESUPUESTAL_DE_GASTOS",
                            firma2Ejecucion);
            parametros.put("PR_FIRMA3_EJECUCION_PRESUPUESTAL_DE_GASTOS",
                            firma3Ejecucion);
            parametros.put("PR_CARGO1_EJECUCION_PRESUPUESTAL_DE_GASTOS",
                            cargo1Ejecucion);
            parametros.put("PR_CARGO2_EJECUCION_PRESUPUESTAL_DE_GASTOS",
                            cargo2Ejecucion);
            parametros.put("PR_FIRMA1_EJECUCION_PRESUPUESTAL_DE_GASTOS",
                            firma1Ejecucion);
            
            if (conaplazamiento) {
                consulta = "0024441LisEjecPptalGasFuenteApl";
                reporte = "0024441LisEjecPptalGasFuenteApl";                
            }
                            
            
        }

        Reporteador.resuelveConsulta(consulta,
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar, parametros);

        try {
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        } catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarANIO() {
        // <CODIGO_DESARROLLADO>
        cuentaInicial = cuentaFinal = null;
        cargarListaMES();
        cargarListacuentaInicial();
        cargarListacuentaFinal();

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilacuentaInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("ID"), "").toString();
        cuentaFinal = null;
        cargarListacuentaFinal();
    }

    public void seleccionarFilacuentaFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = SysmanFunciones.nvl(registroAux.getCampos().get("ID"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    
    public boolean isconaplazamiento() {
        return conaplazamiento;
    }
    
    public void setconaplazamiento(boolean conaplazamiento) {
        this.conaplazamiento = conaplazamiento;
    }
    
    public boolean isvisibleGastos()
    {
        return visibleGastos;
    }

    public void setvisibleGastos(boolean visibleGastos)
    {
        this.visibleGastos = visibleGastos;
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

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
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

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaANIO() {
        return listaANIO;
    }

    public void setListaANIO(List<Registro> listaANIO) {
        this.listaANIO = listaANIO;
    }

    public List<Registro> getListaMES() {
        return listaMES;
    }

    public void setListaMES(List<Registro> listaMES) {
        this.listaMES = listaMES;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListacuentaInicial() {
        return listacuentaInicial;
    }

    public void setListacuentaInicial(
        RegistroDataModelImpl listacuentaInicial) {
        this.listacuentaInicial = listacuentaInicial;
    }

    public RegistroDataModelImpl getListacuentaFinal() {
        return listacuentaFinal;
    }

    public void setListacuentaFinal(RegistroDataModelImpl listacuentaFinal) {
        this.listacuentaFinal = listacuentaFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}

package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.FrminformesproyectosrevisionControlEnum;
import com.sysman.bancoproyectos.enums.FrminformesproyectosrevisionControlUrlEnum;
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
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
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
import javax.faces.event.ActionEvent;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author lcortes
 * @version 1, 27/08/2015
 * 
 * @version 2, 15/09/2017, <strong>pespitia</strong>
 * <li>Se reemplazo el numero del formulario por enumerado.
 * <li>Refactoring de sentencias SQL.
 * <li>Manejo de EJBs.
 * <li>Se aplicaron las recomendaciones de SonarLint.
 */
@ManagedBean
@ViewScoped
public class FrminformesproyectosrevisionControl extends BeanBaseModal {

    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del modulo desde
     * el cual el usuario esta interactuando.
     */
    private final String modulo;

    /**
     * Constante a nivel de clase que aloja el codigo de la
     * dependencia del usuario que inicio sesion.
     */
    private final String dependenciaUsuario;

    /**
     * Constante a nivel de clase que aloja el codigo del usuario que
     * inicio sesion.
     */
    private final String usuario;

    /**
     * Constante a nivel de clase que aloja el nivel del usuario que
     * inicio sesion.
     */
    private final int nivelUsuario;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>ANO</code>
     */
    private final String cAno;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>CODIGO</code>
     */
    private final String cCodigo;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>
     */
    private final String cCompania;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>PROYECTOINI</code>
     */
    private final String cProyectoIni;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>DEPENDENCIA</code>
     */
    private final String cDependencia;

    /**
     * Variable que controla el valor asignado en el parametro:
     * <code>CONTROLAR DEPENDENCIA EN BPPIM</code>. SI = true, NO =
     * false
     */
    private boolean parControlar;

    private String condicionPI;
    private String condicionPF;
    private String opcion;
    private int anioIni;
    private int anioFin;
    private String nomEtiqInforme;
    private List<Registro> listaAnoIni;
    private List<Registro> listaAnoFinal;
    private RegistroDataModelImpl listaProyectoinicial;
    private RegistroDataModelImpl listaProyectofinal;

    /**
     * Lista que contiene los items del combo informe
     */
    private RegistroDataModelImpl listaInforme;

    private StreamedContent archivoDescarga;

    // <MANEJO DE EJBs>
    /**
     * Atributo que permite acceder a las funciones y procedimientos
     * del paquete: <code>PCK_SYSMAN_UTL</code>
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    // </MANEJO DE EJBs>

    /**
     * Creates a new instance of FrminformesproyectosrevisionControl
     */
    public FrminformesproyectosrevisionControl() {
        super();

        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        dependenciaUsuario = SessionUtil.getUser().getDependencia().getCodigo();
        usuario = SessionUtil.getUser().getCodigo();
        nivelUsuario = SessionUtil.getNivelUsuario(modulo);

        cAno = GeneralParameterEnum.ANO.getName();
        cCodigo = GeneralParameterEnum.CODIGO.getName();
        cCompania = GeneralParameterEnum.COMPANIA.getName();
        cDependencia = GeneralParameterEnum.DEPENDENCIA.getName();

        cProyectoIni = FrminformesproyectosrevisionControlEnum.PROYECTOINI
                        .getValue();

        try {
            // 138
            numFormulario = GeneralCodigoFormaEnum.FRMINFORMESPROYECTOSREVISION_CONTROL
                            .getCodigo();

            validarPermisos();
        }
        catch (NumberFormatException
                        | SysmanException ex) {
            SessionUtil.redireccionarMenuPermisos();

            Logger.getLogger(
                            FrminformesproyectosrevisionControl.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
    }

    @PostConstruct
    public void inicializar() {
        verificarDependencia();
        cargarParametros();
        cargarListaInforme();
        cargarListaAnoIni();
        cargarListaAnoFinal();
        cargarListaProyectoinicial();
        abrirFormulario();

        opcion = "1";
    }

    /**
     * Metodo utilizado para inicializar los parametros necesarios al
     * abrir el formulario
     */
    private void cargarParametros() {
        try {
            // Inicializar CONTROLAR DEPENDENCIA EN BPPIM
            String valorPar = recuperarValorPar(
                            "CONTROLAR DEPENDENCIA EN BPPIM");

            parControlar = validarParametro("CONTROLAR DEPENDENCIA EN BPPIM",
                            valorPar)
                && "SI".equals(valorPar);

            // Inicializar VIGENCIA GUBERNAMENTAL ACTUAL
            valorPar = recuperarValorPar(
                            "VIGENCIA GUBERNAMENTAL ACTUAL");

            anioIni = validarParametro("VIGENCIA GUBERNAMENTAL ACTUAL",
                            valorPar) ? Integer.parseInt(valorPar)
                                : SysmanFunciones.ano(new Date());

            anioFin = anioIni + 3;
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Util para verificar que el parametro {@code nomPar} existe en
     * la base de datos. De lo contrario muestra un mensaje
     * informativo.
     * 
     * @param nomPar
     * Nombre del parametro.
     * @param valor
     * Valor asignado al parametro en la base de datos.
     * @return {@code true}: si el parametro existe y tiene valor
     * diferente a nulo.
     */
    private boolean validarParametro(String nomPar, String valor) {
        if (SysmanFunciones.validarVariableVacio(valor)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3572")
                            .replace("#PAR#", nomPar));
            return false;
        }

        return true;
    }

    /**
     * Consulta y retorna el valor asignado al parametro segun la base
     * de datos.
     * 
     * @param nombrePar
     * Nombre asignado al parametro
     * @return El valor del parametro asignado en la base de datos.
     * @throws SystemException
     */
    private String recuperarValorPar(String nombrePar) throws SystemException {
        return ejbSysmanUtil.consultarParametro(compania, nombrePar, modulo,
                        new Date(), false);
    }

    /** Carga la lista asociada al combo Informes. */
    public void cargarListaInforme() {
        Map<String, Object> param = new TreeMap<>();

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrminformesproyectosrevisionControlUrlEnum.URL0001
                                                        .getValue());

        listaInforme = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaAnoIni() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        try {
            listaAnoIni = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrminformesproyectosrevisionControlUrlEnum.URL4310
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAnoFinal() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(cAno, anioIni);

        try {
            listaAnoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrminformesproyectosrevisionControlUrlEnum.URL4737
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaProyectoinicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrminformesproyectosrevisionControlUrlEnum.URL5347
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(cDependencia, parControlar && nivelUsuario != 9
            ? dependenciaUsuario : "");

        listaProyectoinicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void cargarListaProyectofinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrminformesproyectosrevisionControlUrlEnum.URL7620
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(cProyectoIni, condicionPI);

        param.put(cDependencia, parControlar && nivelUsuario != 9
            ? dependenciaUsuario : "");

        listaProyectofinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigo);
    }

    public void oprimirImprimir(ActionEvent ac) {
        // <CODIGO_DESARROLLADO
        archivoDescarga = null;

        generarReporte(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirImprimirExcel(ActionEvent ac) {
        archivoDescarga = null;

        generarReporte(FORMATOS.EXCEL97);
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista:
     * <code>listaInforme</code> asociada al combo Informe.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaInforme(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        opcion = SysmanFunciones.nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();

        cargarListaAnoIni();
        cargarListaProyectoinicial();
    }

    public void seleccionarFilaProyectoinicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        condicionPI = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();

        condicionPF = null;

        cargarListaProyectofinal();
    }

    public void seleccionarFilaProyectofinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        condicionPF = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigo), "")
                        .toString();
    }

    /**
     * Metodo ejecutado al cambiar el control AnoIni, asociado al
     * combo anio inicial.
     */
    public void cambiarAnoIni() {
        // <CODIGO_DESARROLLADO>
        anioFin = 0;

        cargarListaAnoFinal();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Genera un reporte con un determinado formato.
     * 
     * @param formato
     * Tipo de documento a generar.
     */
    private void generarReporte(FORMATOS formato) {
        Map<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();

        String reporte = seleccionarInforme();

        // <REEMPLAZAR VARIABLES EN CONSULTA>
        reemplazar.put("proyInicial",
                        SysmanFunciones.colocarComillas(condicionPI));

        reemplazar.put("proyFinal",
                        SysmanFunciones.colocarComillas(condicionPF));

        reemplazar.put("anioInicial", anioIni);
        reemplazar.put("anioFinal", anioFin);
        // </REEMPLAZAR VARIABLES EN CONSULTA>

        // <ENVIAR PARAMETROS AL REPORTE>
        parametros.put("PR_PROYECTOINICIAL", condicionPI);
        parametros.put("PR_PROYECTOFINAL", condicionPF);
        parametros.put("PR_ANOINI", anioIni);
        parametros.put("PR_ANOFINAL", anioFin);
        // </ENVIAR PARAMETROS AL REPORTE>

        Reporteador.resuelveConsulta(reporte,
                        Integer.parseInt(modulo),
                        reemplazar, parametros);

        /*-aqui reporte hace referencia al nombre del reporte*/
        try {
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Determina el informe a generar respecto al item seleccionado en
     * el combo Informe.
     */
    private String seleccionarInforme() {
        String informe = "000212ProgramacionEjecutado";

        switch (opcion) {
        case "2":
            informe = "000198RPTERDPORPROYECTOS";
            break;
        case "3":
            informe = "000211rptDisponibilidadesPpto";
            break;
        case "4":
            informe = "000209ProgramacionEjecutadaVig";
            break;
        default:
            break;
        }

        return informe;
    }

    /**
     * Alerta al usuario en caso de que no tenga una dependencia
     * asociada
     */
    private void verificarDependencia() {
        if (dependenciaUsuario.isEmpty()) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2314")
                            .replace("#USER#", usuario));
        }
    }

    public String getCondicionPI() {
        return condicionPI;
    }

    public void setCondicionPI(String condicionPI) {
        this.condicionPI = condicionPI;
    }

    public String getCondicionPF() {
        return condicionPF;
    }

    public void setCondicionPF(String condicionPF) {
        this.condicionPF = condicionPF;
    }

    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    public int getAnioIni() {
        return anioIni;
    }

    public void setAnioIni(int anioIni) {
        this.anioIni = anioIni;
    }

    public int getAnioFin() {
        return anioFin;
    }

    public void setAnioFin(int anioFin) {
        this.anioFin = anioFin;
    }

    public String getNomEtiqInforme() {
        return nomEtiqInforme;
    }

    public void setNomEtiqInforme(String nomEtiqInforme) {
        this.nomEtiqInforme = nomEtiqInforme;
    }

    public List<Registro> getListaAnoIni() {
        return listaAnoIni;
    }

    public void setListaAnoIni(List<Registro> listaAnoIni) {
        this.listaAnoIni = listaAnoIni;
    }

    public List<Registro> getListaAnoFinal() {
        return listaAnoFinal;
    }

    public void setListaAnoFinal(List<Registro> listaAnoFinal) {
        this.listaAnoFinal = listaAnoFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public RegistroDataModelImpl getListaInforme() {
        return listaInforme;
    }

    public void setListaInforme(RegistroDataModelImpl listaInforme) {
        this.listaInforme = listaInforme;
    }

    public RegistroDataModelImpl getListaProyectoinicial() {
        return listaProyectoinicial;
    }

    public void setListaProyectoinicial(
        RegistroDataModelImpl listaProyectoinicial) {
        this.listaProyectoinicial = listaProyectoinicial;
    }

    public RegistroDataModelImpl getListaProyectofinal() {
        return listaProyectofinal;
    }

    public void setListaProyectofinal(
        RegistroDataModelImpl listaProyectofinal) {
        this.listaProyectofinal = listaProyectofinal;
    }
}

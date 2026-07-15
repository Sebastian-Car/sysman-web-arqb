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
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaCeroRemote;
import com.sysman.nomina.ejb.EjbNominaSeisRemote;
import com.sysman.nomina.enums.CerrarNominaControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author esarmiento
 * @version 1, 12/01/2016
 *
 * @author lcortes
 * @version 2, 15/03/2017 16:48, Ajustes de buenas practicas
 * SonarLint.
 * 
 * @author ybecerra
 * @version 2.1, 26/10/2017, proceso de Refactoring
 * 
 * 
 */
@ManagedBean
@ViewScoped

public class CerrarNominaControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida para almacenar el codigo del modulo por la
     * cual se ingresa en la aplicacion
     */
    private final int modulo;
    // <DECLARAR_ATRIBUTOS>
    private boolean actFondo;
    private boolean cierreNomina;
    private boolean totalesFondo;
    private boolean verDialogo;
    private String anio;
    private String mes;
    private String periodo;
    private String proceso;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAno;
    private List<Registro> listaMes;
    private List<Registro> listaPeriodo;
    private List<Registro> listaProceso;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    @EJB
    private EjbNominaCeroRemote ejbNominaCero;
    @EJB
    private EjbNominaSeisRemote ejbNominaSeis;

    /**
     * Crea una nueva instancia de CerrarNominaControlador
     */
    public CerrarNominaControlador() {
        super();
        // 448
        numFormulario = GeneralCodigoFormaEnum.CERRAR_NOMINA_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        modulo = Integer.parseInt(SessionUtil.getModulo());

        try {
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (SysmanException e) {

            logger.error(e.getMessage(), e);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la
     * clase del Bean ha sido creado, en este se realizan las
     * asignaciones iniciales necesarias para la visualizacion del
     * formulario, como son tablas, origenes de datos, inicializacion
     * de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        proceso = (String) SessionUtil.getSessionVar("procesoNomina");
        anio = (String) SessionUtil.getSessionVar("anioNomina");
        mes = (String) SessionUtil.getSessionVar("mesNomina");
        periodo = (String) SessionUtil.getSessionVar("periodoNomina");
        // <CARGAR_LISTA>
        cargarListaProceso();
        cargarListaAno();
        cargarListaMes();
        cargarListaPeriodo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>

        verDialogo = false;

        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaAno
     */
    public void cargarListaAno() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CerrarNominaControladorUrlEnum.URL5219
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Carga la lista listaMes
     */
    public void cargarListaMes() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);

        try {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CerrarNominaControladorUrlEnum.URL5876
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Carga la lista listaPeriodo
     */
    public void cargarListaPeriodo() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.MES.getName(), mes);

        try {
            listaPeriodo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CerrarNominaControladorUrlEnum.URL6834
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     * Carga la lista listaProceso
     */
    public void cargarListaProceso() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaProceso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CerrarNominaControladorUrlEnum.URL7612
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton CerrarNomina en la vista
     * 
     */
    public void oprimirCerrarNomina() {
        if (!validarVacios() || !verificarOpciones()) {
            return;
        }

        if (cierreNomina) {
            verDialogo = true;
        }
        else if (totalesFondo) {
            ejecutarCerrarNomina();

        }
        else if (actFondo) {
            if (Integer.parseInt(periodo) > 3) {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString("TB_TB2532"));
                return;
            }
            ejecutarActFondo();

        }
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton consulexcel en la vista
     *
     */
    public void oprimirconsulexcel() {
        // <CODIGO_DESARROLLADO>
        if (!validarVacios()) {
            return;
        }

        String nombreReporte = "800038ConsultaAutoliquidaciones";
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("anio", anio);
        reemplazar.put("mes", mes);
        String ssql = Reporteador.resuelveConsulta(nombreReporte, modulo,
                        reemplazar);

        try {
            Long conteo = service.getConteoConsulta(ssql);
            if (conteo == 0) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(
                                                "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
                return;
            }

            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(ssql,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL);
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException | SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ano
     * 
     */
    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        mes = null;
        periodo = null;
        cargarListaMes();
        cargarListaPeriodo();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Mes
     * 
     */
    public void cambiarMes() {
        // <CODIGO_DESARROLLADO>
        periodo = null;
        cargarListaPeriodo();

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Proceso
     * 
     */
    public void cambiarProceso() {
        // <CODIGO_DESARROLLADO>
        anio = null;
        mes = null;
        periodo = null;
        cargarListaAno();
        cargarListaMes();
        cargarListaPeriodo();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ActualizarFondos
     * 
     */
    public void cambiarActualizarFondos() {
        // <CODIGO_DESARROLLADO>
        cierreNomina = false;
        totalesFondo = false;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ckCierreNomina
     * 
     */
    public void cambiarckCierreNomina() {
        // <CODIGO_DESARROLLADO>
        totalesFondo = false;
        actFondo = false;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control totalesFondo
     * 
     */
    public void cambiartotalesFondo() {
        // <CODIGO_DESARROLLADO>
        actFondo = false;
        cierreNomina = false;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * dialogo en la vista
     *
     */
    public void aceptardialogo() {
        try {
            // <CODIGO_DESARROLLADO>
            boolean estado;

            estado = ejbNominaCero.validarPeriodoActivoNomina(compania,
                            Integer.parseInt(proceso), Integer.parseInt(anio),
                            Integer.parseInt(mes), Integer.parseInt(periodo));

            boolean cierre;
            if (estado) {
                cierre = ejbNominaSeis.cerrarPeriodo(compania,
                                Integer.parseInt(proceso),
                                Integer.parseInt(anio), Integer.parseInt(mes),
                                Integer.parseInt(periodo), SessionUtil.getUser()
                                                .getCodigo());

                if (!cierre) {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2521"));
                }
                else {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB2524")
                                                    .replace("s$periodo$s",
                                                                    periodo)
                                                    .replace("s$mes$s",
                                                                    SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                                                    .parseInt(mes)])
                                                    .replace("s$ano$s", anio));
                }
            }
            else {
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3894")
                                .replace("s$periodo$s",
                                                periodo)
                                .replace("s$mes$s",
                                                SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                                                .parseInt(mes)])
                                .replace("s$ano$s", anio));
            }
            // </CODIGO_DESARROLLADO>

        }
        catch (NumberFormatException | SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        finally {
            verDialogo = false;
        }
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * dialogo en la vista
     *
     */
    public void cancelardialogo() {
        verDialogo = false;
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>

    /**
     * 
     */
    private void ejecutarActFondo() {
        try {
            boolean estado;

            estado = ejbNominaCero.validarPeriodoActivoNomina(compania,
                            Integer.parseInt(proceso), Integer.parseInt(anio),
                            Integer.parseInt(mes), Integer.parseInt(periodo));

            if (!estado) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2533"));
            }
            else {
                ejecutarCierreNomina();
            }

        }
        catch (NumberFormatException | SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }

    /**
     * 
     */
    private void ejecutarCierreNomina() {
        boolean resultado;

        try {
            resultado = ejbNominaSeis.cerrarNomina(compania,
                            Integer.parseInt(proceso), Integer.parseInt(anio),
                            Integer.parseInt(mes), Integer.parseInt(periodo), 1,
                            SessionUtil.getUser().getCodigo());

            if (resultado) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB2534"));
            }
            else {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString("TB_TB2536"));
            }
        }
        catch (NumberFormatException | SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    /**
     * 
     */
    private void ejecutarCerrarNomina() {

        try {
            ejbNominaSeis.cerrarNomina(compania,
                            Integer.parseInt(proceso), Integer.parseInt(anio),
                            Integer.parseInt(mes), Integer.parseInt(periodo), 2,
                            SessionUtil.getUser().getCodigo());
        }
        catch (NumberFormatException | SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

        JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2531"));

    }

    /**
     * @return
     */
    private boolean verificarOpciones() {
        if (!(cierreNomina || actFondo || totalesFondo)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2529"));
            return false;
        }
        return true;
    }

    /**
     * @return
     */
    private boolean validarVacios() {
        if (SysmanFunciones.validarVariableVacio(proceso)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2525"));
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(anio)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2526"));
            return false;
        }

        if (SysmanFunciones.validarVariableVacio(mes)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2527"));
            return false;
        }
        if (SysmanFunciones.validarVariableVacio(periodo)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2528"));
            return false;
        }

        return true;
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable actFondo
     * 
     * @return actFondo
     */
    public boolean isActFondo() {
        return actFondo;
    }

    /**
     * Asigna la variable actFondo
     * 
     * @param actFondo
     * Variable a asignar en actFondo
     */
    public void setActFondo(boolean actFondo) {
        this.actFondo = actFondo;
    }

    /**
     * Retorna la variable cierreNomina
     * 
     * @return cierreNomina
     */
    public boolean isCierreNomina() {
        return cierreNomina;
    }

    /**
     * Asigna la variable cierreNomina
     * 
     * @param cierreNomina
     * Variable a asignar en cierreNomina
     */
    public void setCierreNomina(boolean cierreNomina) {
        this.cierreNomina = cierreNomina;
    }

    /**
     * Retorna la variable totalesFondo
     * 
     * @return totalesFondo
     */
    public boolean isTotalesFondo() {
        return totalesFondo;
    }

    /**
     * Asigna la variable totalesFondo
     * 
     * @param totalesFondo
     * Variable a asignar en totalesFondo
     */
    public void setTotalesFondo(boolean totalesFondo) {
        this.totalesFondo = totalesFondo;
    }

    /**
     * Retorna la variable anio
     * 
     * @return anio
     */
    public String getAnio() {
        return anio;
    }

    /**
     * Asigna la variable anio
     * 
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(String anio) {
        this.anio = anio;
    }

    /**
     * Retorna la variable mes
     * 
     * @return mes
     */
    public String getMes() {
        return mes;
    }

    /**
     * Asigna la variable mes
     * 
     * @param mes
     * Variable a asignar en mes
     */
    public void setMes(String mes) {
        this.mes = mes;
    }

    /**
     * Retorna la variable periodo
     * 
     * @return periodo
     */
    public String getPeriodo() {
        return periodo;
    }

    /**
     * Asigna la variable periodo
     * 
     * @param periodo
     * Variable a asignar en periodo
     */
    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    /**
     * Retorna la variable proceso
     * 
     * @return proceso
     */
    public String getProceso() {
        return proceso;
    }

    /**
     * Asigna la variable proceso
     * 
     * @param proceso
     * Variable a asignar en proceso
     */
    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    /**
     * Retorna la variable verDialogo
     * 
     * @return verDialogo
     */
    public boolean isVerDialogo() {
        return verDialogo;
    }

    /**
     * Asigna la variable verDialogo
     * 
     * @param verDialogo
     * Variable a asignar en verDialogo
     */
    public void setVerDialogo(boolean verDialogo) {
        this.verDialogo = verDialogo;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaAno
     * 
     * @return listaAno
     */
    public List<Registro> getListaAno() {
        return listaAno;
    }

    /**
     * Asigna la lista listaAno
     * 
     * @param listaAno
     * Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    /**
     * Retorna la lista listaMes
     * 
     * @return listaMes
     */
    public List<Registro> getListaMes() {
        return listaMes;
    }

    /**
     * Asigna la lista listaMes
     * 
     * @param listaMes
     * Variable a asignar en listaMes
     */
    public void setListaMes(List<Registro> listaMes) {
        this.listaMes = listaMes;
    }

    /**
     * Retorna la lista listaPeriodo
     * 
     * @return listaPeriodo
     */
    public List<Registro> getListaPeriodo() {
        return listaPeriodo;
    }

    /**
     * Asigna la lista listaPeriodo
     * 
     * @param listaPeriodo
     * Variable a asignar en listaPeriodo
     */
    public void setListaPeriodo(List<Registro> listaPeriodo) {
        this.listaPeriodo = listaPeriodo;
    }

    /**
     * Retorna la lista listaProceso
     * 
     * @return listaProceso
     */
    public List<Registro> getListaProceso() {
        return listaProceso;
    }

    /**
     * Asigna la lista listaProceso
     * 
     * @param listaProceso
     * Variable a asignar en listaProceso
     */
    public void setListaProceso(List<Registro> listaProceso) {
        this.listaProceso = listaProceso;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

}

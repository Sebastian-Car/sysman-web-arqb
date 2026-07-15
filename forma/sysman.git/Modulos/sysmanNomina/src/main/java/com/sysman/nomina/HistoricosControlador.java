package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaCeroRemote;
import com.sysman.nomina.ejb.EjbNominaSeisRemote;
import com.sysman.nomina.enums.HistoricosControladorEnum;
import com.sysman.nomina.enums.HistoricosControladorUrlEnum;
import com.sysman.recursos.ejb.impl.EjbSysmanUtil;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author esarmiento
 * @version 1, 10/08/2015
 * @author amonroy
 * @version 1.1, 17/03/2017 Ajustes de buenas practicas de
 * programacion sugeridas por la herramienta SonarLint
 *
 * @author eamaya
 * @version 2.0,05/10/2017 Proceso de Refactoring DSS,Manejo de EJBs y
 * cambio de numero de formulario por enum
 *
 * @author mzanguna
 * @version 3.0,03/01/2019 Se cambia metodo oprimirBORRAR, cambiando
 * llamado dss a ejbnominaseis
 */
@ManagedBean
@ViewScoped

public class HistoricosControlador extends BeanBaseContinuoAcmeImpl {

    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que almacena el codigo del modulo al
     * cual el usuario ingreso
     */

    private final String modulo;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo ID_DE_CONCEPTO en el formulario, almacena el
     * texto ID_DE_CONCEPTO
     */
    private final String cIdDeConcepto;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo ID_DE_EMPLEADO en el formulario, almacena el
     * texto ID_DE_EMPLEADO
     */
    private final String cIdDeEmpleado;

    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado al campo NOMBRE_CONCEPTO en el formulario, almacena el
     * texto NOMBRE_CONCEPTO
     */
    private final String cNombreConcepto;

    private String empleado;
    private String nombreEmpleado;
    private String datosIngreso;
    private RegistroDataModelImpl listaIDdeEmpleado;
    private RegistroDataModelImpl listaIDdeEmpleadoE;
    private RegistroDataModelImpl listaIDdeConcepto;
    private RegistroDataModelImpl listaIDdeConceptoE;
    private String auxiliar;
    private final String proceso;
    private final String anio;
    private final String mes;
    private final String periodo;
    private final String nombreperiodo;
    private final boolean activo;
    private final String usuario;

    private boolean verBorrarTodo;

    @EJB
    private EjbNominaCeroRemote ejbNominaCero;

    @EJB
    private EjbNominaSeisRemote ejbNominaSeis;
    @EJB
    private EjbSysmanUtil ejbsysmanutil;

    /**
     * Creates a new instance of HistoricosControlador
     */
    public HistoricosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        cIdDeConcepto = "ID_DE_CONCEPTO";
        cIdDeEmpleado = "ID_DE_EMPLEADO";
        cNombreConcepto = "NOMBRE_CONCEPTO";

        proceso = SessionUtil.getSessionVar("procesoNomina").toString();
        anio = SessionUtil.getSessionVar("anioNomina").toString();
        mes = SessionUtil.getSessionVar("mesNomina").toString();
        nombreperiodo = SessionUtil.getSessionVar("nombrePeriodoNomina")
                        .toString();
        periodo = SessionUtil.getSessionVar("periodoNomina").toString();
        activo = (boolean) SessionUtil.getSessionVar("periodoActivo");
        usuario = SessionUtil.getUser().getCodigo();

        verBorrarTodo = false;
        try {
            numFormulario = GeneralCodigoFormaEnum.HISTORICOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (SysmanException ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.HISTORICOS;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro(new HashMap<String, Object>());
        cargarListaIDdeEmpleado();
        cargarListaIDdeConcepto();
        cargarListaIDdeConceptoE();
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        try {
            datosIngreso = SysmanFunciones.concatenar(
                            "Datos correspondientes al mes de ",
                            ejbsysmanutil.mostrarNombreDeMes(
                                            Integer.parseInt(mes)),
                            " de ", anio, " para el Periodo ", periodo,
                            " ", nombreperiodo);
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        cambiarPermisos();

        validarBotonEliminarTodosHistoricos();
        
        registro.getCampos().put("MANUAL", true);

        // </CODIGO_DESARROLLADO>

    }

    private void validarBotonEliminarTodosHistoricos() {

        Map<String, Object> param = new TreeMap<>();

        param.put("APLICACION", modulo);
        param.put(GeneralParameterEnum.USUARIO.getName(), usuario);

        try {
            Registro regnivel = RegistroConverter.toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            HistoricosControladorUrlEnum.URL4444
                                                                            .getValue())
                                            .getUrl(),
                            param));

            if ("9".equals(SysmanFunciones
                            .nvl(regnivel.getCampos().get("NIVEL_USUARIO"), "0")
                            .toString())) {
                verBorrarTodo = true;
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void cambiarPermisos() {

        // if (anio.equals(Integer.toString(SysmanFunciones.ano(new
        // Date()))) &&
        // mes.equals(Integer.toString(SysmanFunciones.mes(new
        // Date())))) {
        // permisos[0] = true;
        // permisos[1] = true;
        // permisos[2] = true;
        // }
        // else {
        // permisos[0] = false;
        // permisos[1] = false;
        // permisos[2] = false;
        // }

    }

    @Override
    public void reasignarOrigen() {

        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        parametrosListado.put("EMPLEADO", empleado);

        parametrosListado.put("PROCESO", proceso);

        parametrosListado.put(GeneralParameterEnum.ANO.getName(), anio);

        parametrosListado.put(GeneralParameterEnum.MES.getName(), mes);

        parametrosListado.put(GeneralParameterEnum.PERIODO.getName(), periodo);
        
        

    }

    public void cargarListaIDdeEmpleado() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        HistoricosControladorUrlEnum.URL6890
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaIDdeEmpleado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        cIdDeEmpleado);
    }

    public void cargarListaIDdeEmpleadoE() {
        listaIDdeEmpleadoE = listaIDdeEmpleado;

    }

    public void cargarListaIDdeConcepto() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        HistoricosControladorUrlEnum.URL7627
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(HistoricosControladorEnum.TIPOCLASE.getValue(), "2");

        listaIDdeConcepto = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        cIdDeConcepto);
    }

    public void cargarListaIDdeConceptoE() {
        listaIDdeConceptoE = listaIDdeConcepto;

    }

    public void cambiarIDdeConceptoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirBORRAR() {

        try {

            if (SysmanFunciones.validarVariableVacio(empleado)) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2506"));
            }
            else {

                if (activo) {
                    int rta = ejbNominaSeis.borrarHistoricos(compania,
                                    Integer.parseInt(proceso),
                                    Integer.parseInt(anio),
                                    Integer.parseInt(mes),
                                    Integer.parseInt(periodo), empleado,
                                    usuario);

                    if (rta > 0) {
                        JsfUtil.agregarMensajeInformativo(
                                        SysmanFunciones.concatenar(idioma
                                                        .getString("TB_TB2624"),
                                                        " ", nombreEmpleado));
                    }
                    else {
                        JsfUtil.agregarMensajeInformativo(
                                        idioma.getString("TB_TB2611"));
                    }
                }
                else {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2614"));
                }

            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void oprimirBORRARPERIODO() {
        try {
            if (activo) {
                int rta = ejbNominaSeis.borrarHistoricos(compania,
                                Integer.parseInt(proceso),
                                Integer.parseInt(anio),
                                Integer.parseInt(mes),
                                Integer.parseInt(periodo), "00", usuario);

                if (rta == 0) {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB2611"));
                }
                else if (rta > 0) {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB2612"));
                }
                else {
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString("TB_TB2613"));
                }
            }
            else {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2614"));
            }
        }
        catch (NumberFormatException | SystemException ex) {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());

        }
    }

    public void seleccionarFilaIDdeEmpleado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        empleado = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cIdDeEmpleado), "")
                        .toString();
        nombreEmpleado = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRECOMPLETO"), "")
                        .toString();
        reasignarOrigen();
    }

    public void seleccionarFilaIDdeConcepto(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(cIdDeConcepto,
                        registroAux.getCampos().get(cIdDeConcepto));
        registro.getCampos().put(cNombreConcepto,
                        registroAux.getCampos().get(cNombreConcepto));
    }

    public void seleccionarFilaIDdeConceptoE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cIdDeConcepto), "")
                        .toString();
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(empleado)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2506"));
            return false;
        }
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(), "VALOR")) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2626"));
            return false;
        }
        if (SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        cIdDeConcepto)) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2627"));
            return false;
        }
        registro.getCampos().put("COMPANIA", compania);
        registro.getCampos().put("ID_DE_PROCESO", proceso);
        registro.getCampos().put("ANO", anio);
        registro.getCampos().put("MES", mes);
        registro.getCampos().put("PERIODO", periodo);
        registro.getCampos().put(cIdDeEmpleado, empleado);
        registro.getCampos().remove(cNombreConcepto);
        

        try {
            Date fecha = ejbNominaCero.getFechaPeriodoIniFin(compania,
                            Integer.parseInt(proceso),
                            Integer.parseInt(anio), Integer.parseInt(mes),
                            Integer.parseInt(periodo), false, false);

            registro.getCampos().put("FECHA", fecha);
            // </CODIGO_DESARROLLADO>
        }
        catch (NumberFormatException | SystemException ex) {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(HistoricosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
    	
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove("RANGO1");
        registro.getCampos().remove("RANGO2");
        registro.getCampos().remove(cNombreConcepto);

        if (!activo) {

            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2553"));
            return false;
        }
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarAntes() {
        if (!activo) {

            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2570"));
            return false;
        }
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        return true;
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void removerCombos() {
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
    	registro.getCampos().put("MANUAL", true);
        // </CODIGO_DESARROLLADO>
    }

    public String getCompania() {
        return compania;
    }

    public String getProceso() {
        return proceso;
    }

    public String getAnio() {
        return anio;
    }

    public String getMes() {
        return mes;
    }

    public String getPeriodo() {
        return periodo;
    }

    public RegistroDataModelImpl getListaIDdeEmpleado() {
        return listaIDdeEmpleado;
    }

    public void setListaIDdeEmpleado(RegistroDataModelImpl listaIDdeEmpleado) {
        this.listaIDdeEmpleado = listaIDdeEmpleado;
    }

    public RegistroDataModelImpl getListaIDdeEmpleadoE() {
        return listaIDdeEmpleadoE;
    }

    public void setListaIDdeEmpleadoE(
        RegistroDataModelImpl listaIDdeEmpleadoE) {
        this.listaIDdeEmpleadoE = listaIDdeEmpleadoE;
    }

    public RegistroDataModelImpl getListaIDdeConcepto() {
        return listaIDdeConcepto;
    }

    public void setListaIDdeConcepto(RegistroDataModelImpl listaIDdeConcepto) {
        this.listaIDdeConcepto = listaIDdeConcepto;
    }

    public RegistroDataModelImpl getListaIDdeConceptoE() {
        return listaIDdeConceptoE;
    }

    public void setListaIDdeConceptoE(
        RegistroDataModelImpl listaIDdeConceptoE) {
        this.listaIDdeConceptoE = listaIDdeConceptoE;
    }

    public String getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    public String getEmpleado() {
        return empleado;
    }

    public void setEmpleado(String empleado) {
        this.empleado = empleado;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
    }

    public boolean isVerBorrarTodo() {
        return verBorrarTodo;
    }

    public void setVerBorrarTodo(boolean verBorrarTodo) {
        this.verBorrarTodo = verBorrarTodo;
    }

    public String getNombreperiodo() {
        return nombreperiodo;
    }

    public String getDatosIngreso() {
        return datosIngreso;
    }

    public void setDatosIngreso(String datosIngreso) {
        this.datosIngreso = datosIngreso;
    }

}

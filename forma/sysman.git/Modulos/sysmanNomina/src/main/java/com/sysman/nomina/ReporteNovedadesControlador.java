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
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.ReporteNovedadesControladorEnum;
import com.sysman.nomina.enums.ReporteNovedadesControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author esarmiento
 * @version 1, 03/08/2015
 * 
 * @author eamaya
 * @version 2.0, 25/10/2017, Proceso de Refactoring DSS,cambio de
 * numero de formulario por enum y correcciones SonarQube
 * 
 */
@ManagedBean
@ViewScoped
public class ReporteNovedadesControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;
    private String opcion;
    private String opcionDetalle;
    private String proceso;
    private String ano1;
    private String mes1;
    private String periodo1;
    private String anio2;
    private String mes2;
    private String periodo2;
    private String empleadoI;
    private String empleado;
    private List<Registro> listaProceso;
    private List<Registro> listaAno1;
    private List<Registro> listaMes1;
    private List<Registro> listaPeriodo1;
    private List<Registro> listaAno2;
    private List<Registro> listaMes2;
    private List<Registro> listaPeriodo2;
    private RegistroDataModelImpl listaEmpleadoI;
    private String fechaInicial;
    private String fechaFinal;
    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of ReporteNovedadesControlador
     */
    public ReporteNovedadesControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            numFormulario = GeneralCodigoFormaEnum.REPORTE_NOVEDADES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(ReporteNovedadesControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {

        proceso = (String) SessionUtil.getSessionVar("procesoNomina");
        ano1 = (String) SessionUtil.getSessionVar("anioNomina");
        anio2 = (String) SessionUtil.getSessionVar("anioNomina");
        mes1 = (String) SessionUtil.getSessionVar("mesNomina");
        mes2 = (String) SessionUtil.getSessionVar("mesNomina");
        periodo1 = (String) SessionUtil.getSessionVar("periodoNomina");
        periodo2 = (String) SessionUtil.getSessionVar("periodoNomina");
        opcion = "1";
        opcionDetalle = "1";

        cargarListaProceso();
        cargarListaAno1();
        cargarListaMes1();
        cargarListaPeriodo1();
        cargarListaAno2();
        cargarListaMes2();
        cargarListaPeriodo2();
        cargarListaEmpleadoI();
        abrirFormulario();
    }

    public void cargarListaProceso() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaProceso = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReporteNovedadesControladorUrlEnum.URL3894
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAno1() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);

        try {
            listaAno1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReporteNovedadesControladorUrlEnum.URL4943
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMes1() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        ano1);
        param.put(ReporteNovedadesControladorEnum.ID_PROCESO.getValue(),
                        proceso);

        try {
            listaMes1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReporteNovedadesControladorUrlEnum.URL5546
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaPeriodo1() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.ANO.getName(), ano1);

        param.put(GeneralParameterEnum.MES.getName(), mes1);

        param.put(ReporteNovedadesControladorEnum.PROCESO.getValue(), proceso);

        try {
            listaPeriodo1 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReporteNovedadesControladorUrlEnum.URL5959
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAno2() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(),
                        proceso);

        try {
            listaAno2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReporteNovedadesControladorUrlEnum.URL4943
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMes2() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio2);
        param.put(ReporteNovedadesControladorEnum.ID_PROCESO.getValue(),
                        proceso);

        try {
            listaMes2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReporteNovedadesControladorUrlEnum.URL7879
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaPeriodo2() {

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        param.put(GeneralParameterEnum.ANO.getName(), anio2);

        param.put(GeneralParameterEnum.MES.getName(), mes2);

        param.put(ReporteNovedadesControladorEnum.PROCESO.getValue(), proceso);

        try {
            listaPeriodo2 = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ReporteNovedadesControladorUrlEnum.URL5959
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaEmpleadoI() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ReporteNovedadesControladorUrlEnum.URL9535
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        listaEmpleadoI = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID_DE_EMPLEADO");
    }

    public void validaciones(FORMATOS formato) {
        if (SysmanFunciones.validarVariableVacio(proceso)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2548"));
            return;
        }
        if (!validarCampos(ano1, mes1, periodo1)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2660"));
            return;
        }
        if (!validarCampos(anio2, mes2, periodo2)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2661"));
            return;
        }
        if (validacionFecha()) {
            return;
        }

        generarInforme(formato);
    }

    public boolean validacionOpcion() {
        if (SysmanFunciones.validarVariableVacio(opcion)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2662"));
            return true;
        }
        else if ("3".equals(opcion)
            && SysmanFunciones.validarVariableVacio(empleadoI)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2663"));
            return true;
        }
        if (SysmanFunciones.validarVariableVacio(opcionDetalle)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2664"));
            return true;
        }

        return false;
    }

    public boolean validacionFecha() {
        fechaInicial = ano1 + SysmanFunciones.padl(mes1, 2, "0")
            + SysmanFunciones.padl(periodo1, 2, "0");
        fechaFinal = anio2 + SysmanFunciones.padl(mes2, 2, "0")
            + SysmanFunciones.padl(periodo2, 2, "0");
        if (!validarFechas(fechaInicial, fechaFinal)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB49"));
            return true;
        }
        return false;

    }

    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        validaciones(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    private void generarInforme(FORMATOS formato) {
        archivoDescarga = null;
        Map<String, Object> parametros = new HashMap<>();

        if (validacionOpcion()) {
            return;
        }
        try {
            String nombreReporte;
            if ("1".equals(opcionDetalle)) {
                nombreReporte = "000159InformeNovedades";
            }
            else {
                nombreReporte = "000160InformeNovedadesporPersona";
            }
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("opcion", opcion);
            reemplazar.put("intervalo1", SysmanFunciones.padl(proceso, 2, "0")
                + fechaInicial);
            reemplazar.put("intervalo2",
                            SysmanFunciones.padl(proceso, 2, "0") + fechaFinal);
            reemplazar.put("empleado", SysmanFunciones.nvlStr(empleado, "0"));

            parametros.put("PR_NOMBREEMPRESA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            parametros.put("PR_ENTRE", SysmanFunciones.concatenar(
                            "Entre ",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mes1)],
                            " de ", ano1, " Periodo ", periodo1, " y ",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                            .parseInt(mes2)],
                            " de ", anio2, " Periodo ", periodo2));

            Reporteador.resuelveConsulta(nombreReporte,
                            Integer.parseInt(modulo), reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private boolean validarCampos(String anio, String mes, String periodo) {
        if (anio == null || "".equals(anio) || mes == null) {
            return false;
        }
        if ("".equals(mes)
            || periodo == null || "".equals(periodo)) {
            return false;
        }

        return true;

    }

    public boolean validarFechas(String fechaInicial, String fechaFinal) {
        if (fechaInicial.compareTo(fechaFinal) > 0) {
            return false;
        }

        return true;

    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        validaciones(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarProceso() {
        // <CODIGO_DESARROLLADO>
        mes1 = null;
        mes2 = null;
        periodo1 = null;
        periodo2 = null;
        cargarListaMes1();
        cargarListaMes2();
        cargarListaPeriodo1();
        cargarListaPeriodo2();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno1() {
        // <CODIGO_DESARROLLADO>
        mes1 = null;
        periodo1 = null;
        cargarListaMes1();
        cargarListaPeriodo1();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes1() {
        // <CODIGO_DESARROLLADO>
        periodo1 = null;
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

    public void cambiarMes2() {
        // <CODIGO_DESARROLLADO>
        periodo2 = null;
        cargarListaPeriodo2();
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaEmpleadoI(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        empleado = SysmanFunciones
                        .nvl(registroAux.getCampos().get("ID_DE_EMPLEADO"), "")
                        .toString();
        empleadoI = SysmanFunciones
                        .nvl(registroAux.getCampos().get(
                                        GeneralParameterEnum.NOMBRE.getName()),
                                        "")
                        .toString();
    }

    @Override
    public void abrirFormulario() {
        // METODO_NO_IMPLEMENTADO
    }

    public void cambiarOpcion() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    public String getOpcionDetalle() {
        return opcionDetalle;
    }

    public void setOpcionDetalle(String opcionDetalle) {
        this.opcionDetalle = opcionDetalle;
    }

    public String getProceso() {
        return proceso;
    }

    public void setProceso(String proceso) {
        this.proceso = proceso;
    }

    public String getAno1() {
        return ano1;
    }

    public void setAno1(String ano1) {
        this.ano1 = ano1;
    }

    public String getMes1() {
        return mes1;
    }

    public void setMes1(String mes1) {
        this.mes1 = mes1;
    }

    public String getPeriodo1() {
        return periodo1;
    }

    public void setPeriodo1(String periodo1) {
        this.periodo1 = periodo1;
    }

    public String getAnio2() {
        return anio2;
    }

    public void setAnio2(String anio2) {
        this.anio2 = anio2;
    }

    public String getMes2() {
        return mes2;
    }

    public void setMes2(String mes2) {
        this.mes2 = mes2;
    }

    public String getPeriodo2() {
        return periodo2;
    }

    public void setPeriodo2(String periodo2) {
        this.periodo2 = periodo2;
    }

    public String getEmpleadoI() {
        return empleadoI;
    }

    public void setEmpleadoI(String empleadoI) {
        this.empleadoI = empleadoI;
    }

    public List<Registro> getListaProceso() {
        return listaProceso;
    }

    public void setListaProceso(List<Registro> listaProceso) {
        this.listaProceso = listaProceso;
    }

    public List<Registro> getListaAno1() {
        return listaAno1;
    }

    public void setListaAno1(List<Registro> listaAno1) {
        this.listaAno1 = listaAno1;
    }

    public List<Registro> getListaMes1() {
        return listaMes1;
    }

    public void setListaMes1(List<Registro> listaMes1) {
        this.listaMes1 = listaMes1;
    }

    public List<Registro> getListaPeriodo1() {
        return listaPeriodo1;
    }

    public void setListaPeriodo1(List<Registro> listaPeriodo1) {
        this.listaPeriodo1 = listaPeriodo1;
    }

    public List<Registro> getListaAno2() {
        return listaAno2;
    }

    public void setListaAno2(List<Registro> listaAno2) {
        this.listaAno2 = listaAno2;
    }

    public List<Registro> getListaMes2() {
        return listaMes2;
    }

    public void setListaMes2(List<Registro> listaMes2) {
        this.listaMes2 = listaMes2;
    }

    public List<Registro> getListaPeriodo2() {
        return listaPeriodo2;
    }

    public void setListaPeriodo2(List<Registro> listaPeriodo2) {
        this.listaPeriodo2 = listaPeriodo2;
    }

    public RegistroDataModelImpl getListaEmpleadoI() {
        return listaEmpleadoI;
    }

    public void setListaEmpleadoI(RegistroDataModelImpl listaEmpleadoI) {
        this.listaEmpleadoI = listaEmpleadoI;
    }

    public String getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(String fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public String getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(String fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public String getNombreEmpleado() {
        return empleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.empleado = nombreEmpleado;
    }

    public void cambiaropcionDetalle() {
        // METODO_NO_IMPLEMENTADO
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

}

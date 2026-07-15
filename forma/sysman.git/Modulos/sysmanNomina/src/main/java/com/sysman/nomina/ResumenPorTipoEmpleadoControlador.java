package com.sysman.nomina;

import java.io.FileNotFoundException;
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
import com.sysman.nomina.enums.ResumenPorTipoEmpleadoControladorEnum;
import com.sysman.nomina.enums.ResumenPorTipoEmpleadoControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author dsuesca
 * @version 1, 27/08/2015
 * 
 * @author vmolano
 * @version 2, 16/01/2018 - Refactoring
 * 
 */
@ManagedBean
@ViewScoped
public class ResumenPorTipoEmpleadoControlador extends BeanBaseModal {

    private final String compania;
    private String nombreEmpresa;
    private String tipoEmpleado;
    private String resumen;
    private String opcion;
    private String ano1;
    private String ano2;
    private String mes1;
    private String mes2;
    private String periodo1;
    private String periodo2;
    private String proceso;
    private List<Registro> listaAno1;
    private List<Registro> listaAno2;
    private List<Registro> listaMes1;
    private List<Registro> listaMes2;
    private List<Registro> listaPeriodo1;
    private List<Registro> listaPeriodo2;
    private List<Registro> listaProceso;
    private RegistroDataModelImpl listaEmpleado;
    private final String anioSession;
    private final String mesSession;
    private final String periodoSession;
    private final String procesoSesion;
    private final String moduloNomina;
    private StreamedContent archivoDescarga;

    /**
     * Creates a new instance of ResumenPorTipoEmpleadoControlador
     */
    public ResumenPorTipoEmpleadoControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.RESUMEN_POR_TIPO_EMPLEADO_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        nombreEmpresa = SessionUtil.getCompaniaIngreso().getNombre();
        anioSession = (String) SessionUtil.getSessionVar("anioNomina");
        mesSession = (String) SessionUtil.getSessionVar("mesNomina");
        periodoSession = (String) SessionUtil.getSessionVar("periodoNomina");
        procesoSesion = (String) SessionUtil.getSessionVar("procesoNomina");
        moduloNomina = SessionUtil.getModulo();
        try {
            ano1 = ano2 = anioSession;
            mes1 = mes2 = mesSession;
            periodo1 = periodo2 = periodoSession;
            proceso = procesoSesion;
            opcion = "1";
            resumen = "1";
            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(ResumenPorTipoEmpleadoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar() {

        cargarListaAno1();
        cargarListaAno2();
        cargarListaMes1();
        cargarListaMes2();
        cargarListaPeriodo1();
        cargarListaPeriodo2();
        cargarListaProceso();
        cargarListaEmpleado();
        abrirFormulario();
    }

    public void cargarListaAno1() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno1 = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ResumenPorTipoEmpleadoControladorUrlEnum.URL0001
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
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

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano1);

        try {
            listaMes1 = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ResumenPorTipoEmpleadoControladorUrlEnum.URL0003
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaMes2() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano2);

        try {
            listaMes2 = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ResumenPorTipoEmpleadoControladorUrlEnum.URL0003
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
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
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);

        try {
            listaPeriodo1 = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ResumenPorTipoEmpleadoControladorUrlEnum.URL0004
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaPeriodo2() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano2);
        param.put(GeneralParameterEnum.MES.getName(), mes2);
        param.put(GeneralParameterEnum.ID_DE_PROCESO.getName(), proceso);

        try {
            listaPeriodo2 = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ResumenPorTipoEmpleadoControladorUrlEnum.URL0004
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaProceso() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaProceso = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            ResumenPorTipoEmpleadoControladorUrlEnum.URL005
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaEmpleado() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ResumenPorTipoEmpleadoControladorUrlEnum.URL0002
                                                        .getValue());
        listaEmpleado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, ResumenPorTipoEmpleadoControladorEnum.ID_TIPO
                                        .getValue());

    }

    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        getInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirImprimir() {
        // <CODIGO_DESARROLLADO>
        getInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    public void getInforme(FORMATOS formato) {
        String desde = SysmanFunciones.padl(proceso, 2, "0") + ano1
            + SysmanFunciones.padl(mes1, 2, "0")
            + SysmanFunciones.padl(periodo1, 2, "0");
        String hasta = SysmanFunciones.padl(proceso, 2, "0") + ano2
            + SysmanFunciones.padl(mes2, 2, "0")
            + SysmanFunciones.padl(periodo2, 2, "0");
        String todos;
        String conceptosDiferentes;
        String condIdConceptos;
        if ("3".equals(opcion)) {
            todos = tipoEmpleado;
        }
        else {
            todos = "todos";
        }
        if ("2".equals(resumen)) {
            conceptosDiferentes = "99";
            condIdConceptos = "";

        }
        else {
            conceptosDiferentes = "2,4,6,7";
            condIdConceptos = "AND (HISTORICOS.ID_DE_CONCEPTO<600 OR HISTORICOS.ID_DE_CONCEPTO>698) AND (HISTORICOS.ID_DE_CONCEPTO<700 OR HISTORICOS.ID_DE_CONCEPTO>798)";
        }
        String strReporte;
        strReporte = "000203ResumenPorTipoEmpleadoAlcaldia";

        try {

            HashMap<String, Object> reemplazar = new HashMap<>();
            // MANEJO DE PARAMETROS DE REEMPLAZO
            reemplazar.put("desde", desde);
            reemplazar.put("hasta", hasta);
            reemplazar.put("todos", todos);
            reemplazar.put("conceptosdiferentes", conceptosDiferentes);
            reemplazar.put("condidconceptos", condIdConceptos);

            String entre = "Entre: "
                + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                .parseInt(mes1)]
                + " de " + ano1 + " Periodo " + periodo1 + "  y  "
                + SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[Integer
                                .parseInt(mes2)]
                + " de " + ano2 + " Periodo " + periodo2;

            Map<String, Object> parametros = new HashMap<>();

            parametros.put("PR_ENTRE", entre);
            parametros.put("PR_NOMBREEMPRESA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            Reporteador.resuelveConsulta(strReporte,
                            Integer.parseInt(moduloNomina), reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(strReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException ex) {
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("TB_TB1766"));
            Logger.getLogger(ResumenPorTipoEmpleadoControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        catch (JRException | IOException ex) {
            Logger.getLogger(ResumenPorTipoEmpleadoControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cambiarAno1() {
        // <CODIGO_DESARROLLADO>
        cargarListaMes1();
        cargarListaPeriodo1();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAno2() {
        // <CODIGO_DESARROLLADO>
        cargarListaMes2();
        cargarListaPeriodo2();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes1() {
        // <CODIGO_DESARROLLADO>
        cargarListaPeriodo1();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes2() {
        // <CODIGO_DESARROLLADO>
        cargarListaPeriodo2();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPeriodo1() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarPeriodo2() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarOpcion() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaEmpleado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoEmpleado = (String) registroAux.getCampos().get("ID_DE_TIPO");
    }

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
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

    public RegistroDataModelImpl getListaEmpleado() {
        return listaEmpleado;
    }

    public void setListaEmpleado(RegistroDataModelImpl listaEmpleado) {
        this.listaEmpleado = listaEmpleado;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getTipoEmpleado() {
        return tipoEmpleado;
    }

    public void setTipoEmpleado(String tipoEmpleado) {
        this.tipoEmpleado = tipoEmpleado;
    }

    public void cambiarProceso() {
        ano1 = ano2 = mes1 = mes2 = periodo1 = periodo2 = null;
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
        /*
         * FR153-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * formularioAbrir 1, Me.Name Me.Caption = NombreEmpresa(0)
         * Opcion_AfterUpdate End Sub
         */
        // </CODIGO_DESARROLLADO>
    }
}

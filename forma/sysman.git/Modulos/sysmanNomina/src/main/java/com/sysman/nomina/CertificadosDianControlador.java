package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.Constantes;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.ejb.EjbNominaDosRemote;
import com.sysman.nomina.ejb.EjbNominaSieteRemote;

import com.sysman.nomina.ejb.EjbNominaDiezRemote;

import com.sysman.nomina.enums.CertificadosDianControladorEnum;
import com.sysman.nomina.enums.CertificadosDianControladorUrlEnum;
import com.sysman.nomina.enums.volantesDePagoControladorEnum;
import com.sysman.nomina.enums.volantesDePagoControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.services.ServidorCorreo;
import com.sysman.sesion.SessionBean;
import com.sysman.util.SysmanFunciones;
import com.sysman.util.reporte.PrepararReporte;
import com.sysman.util.reporte.RetornoReporte;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * * @author jacelas
 *
 * @version 1, 17/09/2015
 *
 * --Modificado por lcortes 16/03/2017 08:05 --> Ajustes de buenas
 * practicas SonarLint.
 *
 * @version 3, 27/03/2017, pespitia, Obs.Se pasa la consulta del
 * metodo {@code armarSqlDian2013} al informe: {@code 000140DIAN} de
 * la tabla CONSULTAS del esquema SYSMANIRIS.
 *
 * @author asana
 * @version 4, Se realiza refactoring de controlador.
 *
 * @author asana
 * @version 5.0, 01/11/2017, Se cambia de modal a datos sin grilla
 */
@ManagedBean
@ViewScoped
public class CertificadosDianControlador extends BeanBaseDatosAcmeImpl {

    private final String compania;
    private String opcion;
    private String estado;
    private String soloCrearPdf;
    private String fecha1;
    private String fecha2;
    private String documentoEmpleado;
    private String ano;
    private Date fechaExpedicion;
    private String original;
    private String cedula;
    private String labelNombre;
    private String modulo;
    private String empleado;
    private String permisoEnvioCorreo = "0";
    private String nit;
    private String procesoSesion;
    private String servidor;
    private String usuario;
    private String clave;

    private ServidorCorreo correo;
    private StreamedContent archivoDescarga;
    private List<Registro> listaCbofecha1;
    private List<Registro> listaCboFecha2;
    private List<Registro> listaAnno;
    private RegistroDataModelImpl listaEmpleado;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbNominaDosRemote ejbNominaDosRemote;
    
    
    @EJB
    private EjbNominaDiezRemote ejbNominaDiezRemote;
    
    
    

    @EJB
    private EjbNominaSieteRemote ejbNominaSieteRemote;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de CertificadosDianControlador
     */
    public CertificadosDianControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        correo = new ServidorCorreo();
        nit = SessionUtil.getCompaniaIngreso().getNit();
        procesoSesion = (String) SessionUtil.getSessionVar("procesoNomina");
        numFormulario = GeneralCodigoFormaEnum.CERTIFICADOS_DIAN_CONTROLADOR
                        .getCodigo();
        tabla = "";
        estado = "1";
        opcion = "1";
        fechaExpedicion = new Date();
        ano = String.valueOf(SysmanFunciones.ano(new Date()));
        fecha1 = SysmanFunciones.concatenar("01/01/", ano);
        fecha2 = SysmanFunciones.concatenar("31/12/", ano);
        original = "Original: Empleado";

        try {
            validarPermisos();
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas() {
        cargarListaCbofecha1();
        cargarListaCboFecha2();
        cargarListaanno();
        cargarListaEmpleado();
        abrirFormulario();

    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de
     * todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub() {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los
     * subformularios
     */
    @Override
    public void iniciarListasSubNulo() {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
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

        asignarOrigenDatos();
        iniciarListas();

        try {
            BigDecimal var = ejbNominaDosRemote.getAutorizarEnvioCorreo(
                            compania,
                            Integer.parseInt(SessionUtil.getModulo()));
            permisoEnvioCorreo = String.valueOf(var.intValue());
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    @Override
    public void asignarOrigenDatos() {
        origenDatos = "";
    }

    public void cargarListaCbofecha1() {
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(CertificadosDianControladorEnum.PARAM0.getValue(),
                        procesoSesion);
        try {
            listaCbofecha1 = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            CertificadosDianControladorUrlEnum.URL2288
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCboFecha2() {
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(CertificadosDianControladorEnum.PARAM0.getValue(),
                        procesoSesion);

        try {
            listaCboFecha2 = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            CertificadosDianControladorUrlEnum.URL2289
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaanno() {
        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(CertificadosDianControladorEnum.PARAM1.getValue(), ano);

        try {
            listaAnno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            CertificadosDianControladorUrlEnum.URL2290
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void cargarListaEmpleado() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CertificadosDianControladorUrlEnum.URL2291
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(CertificadosDianControladorEnum.PARAM2.getValue(), estado);
        param.put(CertificadosDianControladorEnum.FECHA.getValue(), fecha1);
        
        listaEmpleado = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        "ID_DE_EMPLEADO");
    }

    public void cambiarESTADO() {
        // <CODIGO DESARROLLADO>
        cargarListaEmpleado();
        // </CODIGO DESARROLLADO>
    }

    public void oprimirCmdCalcular() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarReporte(FORMATOS.PDF);

        // </CODIGO_DESARROLLADO>
    }

    private void generarReporte(FORMATOS formato) {

        Map<String, Object> reemplazar = new HashMap<>();
        Map<String, Object> parametros = new HashMap<>();

        // <REEMPLAZAR VARIABLES EN CONSULTA>
        try {
            String cedulaInicial = ("1").equals(opcion) ? "0"
                : documentoEmpleado;
            String cedulaFinal = ("1").equals(opcion) ? "999"
                : documentoEmpleado;

            RetornoReporte retornoReporte = new RetornoReporte();
            PrepararReporte prepararReporte = new PrepararReporte();
            try {

                retornoReporte = prepararReporte.prepararCertificadoDian(
                                compania, Integer.parseInt(ano),
                                cedulaInicial, cedulaFinal, new Date(),
                                "GENERADO VIA WEB");
            }
            catch (SysmanException e) {
                JsfUtil.agregarMensajeInformativo(
                                "Obteniendo parametros y reemplazos del informe =>");
                logger.error(e.getMessage(), e);
            }

            Registro rs = prepararReporte.obtenerDatosAnoDian(compania,
                            Integer.parseInt(ano));
            String parFormatoDIAN = SysmanFunciones
                            .nvl(rs.getCampos().get("FORMATO_DIAN"), "")
                            .toString();

            reemplazar = retornoReporte.getReemplazar();
            
            parametros = retornoReporte.getParametros();
            //Se agrega la captura y envio de la fecha de expedicion a los parametros esto se encontraba en PrepararReporte.java pero en este no toma el el valor ingresado en el campo del formulario sino la fecha actual.
            //se altera segun TAR 1000099858
                       
            parametros.put("PR_FECHA_EXPEDICION",
    				fechaExpedicion);
            
            // IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
            parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
            // FIN IMPLEMENTACION MARCA_BLANCA
            
            reemplazar.put("filtroNitONombre", "DT.TERCERO");
            
            // </ENVIAR PARAMETROS AL REPORTE>
            Reporteador.resuelveConsulta(parFormatoDIAN,
                            Integer.parseInt(modulo),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(parFormatoDIAN,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (FileNotFoundException | NumberFormatException ex) {
            JsfUtil.agregarMensajeInformativo(SysmanFunciones.concatenar(
                            idioma.getString(Constantes.MSM_TRANS_INTERRUMPIDA),
                            " ",
                            idioma.getString(Constantes.MSM_INFORME_NO_EXISTE),
                            " ", ex.getMessage()));
            logger.error(ex.getMessage(), ex);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    public void cambiarCbofecha1() {
        // <CODIGO_DESARROLLADO>
        cargarListaEmpleado();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCboFecha2() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAnno() {
        fecha1 = SysmanFunciones.concatenar("01/01/", ano);
        fecha2 = SysmanFunciones.concatenar("31/12/", ano);
        cargarListaCbofecha1();
        cargarListaCboFecha2();
        cargarListaEmpleado();
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarCertifica() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>

    public void seleccionarFilaEmpleado(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        empleado = SysmanFunciones
                        .nvl(registroAux.getCampos().get("ID_DE_EMPLEADO"), "")
                        .toString();
        labelNombre = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRECOMPLETO"), "")
                        .toString();
        documentoEmpleado = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NUMERO_DCTO"), "")
                        .toString();

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>

    public void oprimirconceptos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirACTUALIZAUVT() {
        // <CODIGO_DESARROLLADO>
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(Integer
                        .toString(GeneralCodigoFormaEnum.PARAMETROSCERTDIAN_CONTROLADOR
                                        .getCodigo()));
        SessionUtil.redireccionarForma(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirACTUALIZAPARAMETROS() {
        // <CODIGO_DESARROLLADO>
        try {
            boolean retornoActualizacion;
            retornoActualizacion = ejbNominaSieteRemote.actParametroCertDian(
                            compania, Integer.parseInt(modulo),
                            SessionUtil.getUser().toString());

            if (retornoActualizacion) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB3765"));
            }
        }
        catch (NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirpdf() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirEnviarCorreo() {
        // <CODIGO_DESARROLLADO>
        try {
            Map<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            correo.setSmtpHostName(servidor);
            correo.setSmtpHostPort(587);
            correo.setSmtpAuthUser(usuario);
            correo.setSmtpAuthName(usuario);
            correo.setSmtpAuthPwd(clave);
            String mi_empleado = "";

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ESTADO.getName(), estado);
            param.put(CertificadosDianControladorEnum.FECHA.getValue(), fecha1);
            param.put(CertificadosDianControladorEnum.PARAM1.getValue(), ano);

            if ("1".equals(opcion)) {
                param.put(GeneralParameterEnum.ID_DE_EMPLEADO.getName(),
                                "0");
              
            }
            else {
                param.put(GeneralParameterEnum.ID_DE_EMPLEADO.getName(), empleado);
             
            }
            
            String url = UrlServiceUtil.getInstance()
                    .getUrlServiceByUrlByEnumID(
                            CertificadosDianControladorUrlEnum.URL0008
                                            .getValue())
            .getUrl(); 

            List<Registro> listaCorreos = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            CertificadosDianControladorUrlEnum.URL0008
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
            RetornoReporte retornoReporte = new RetornoReporte();
            PrepararReporte prepararReporte = new PrepararReporte();
            
            
            Registro rs = prepararReporte.obtenerDatosAnoDian(compania,
                            Integer.parseInt(ano));
            
            String parFormatoDIAN = SysmanFunciones
                            .nvl(rs.getCampos().get("FORMATO_DIAN"), "")
                            .toString();

            for (Registro registro : listaCorreos) {
                if (registro.getCampos().get("EMAIL_CORPORATIVO") != null) {
                    String cedulaInicial = SysmanFunciones
                                    .nvl(registro.getCampos()
                                                    .get("NUMERO_DCTO"), "")
                                    .toString();

                    retornoReporte = prepararReporte.prepararCertificadoDian(
                                    compania, Integer.parseInt(ano),
                                    cedulaInicial, cedulaInicial, new Date(),
                                    "GENERADO VIA WEB");

                    reemplazar = retornoReporte.getReemplazar();
                    parametros = retornoReporte.getParametros();
                    parametros.put("PR_FECHA_EXPEDICION",
            				fechaExpedicion);
                    
                    // IMPLEMETACION PARAMETROS MARCA_BLANCA - ljdiaz (Luis Jacobo Diaz muñoz)
                    parametros.put("PR_EMPRESAPARAMETRIZADA", SessionBean.getImpresoPorEmpresaParamterizada());
                    // FIN IMPLEMENTACION MARCA_BLANCA
                    
                    parametros.put("filtroNitONombre", "DT.TERCERO"); // JM CC 4088
                    
                    // </ENVIAR PARAMETROS AL REPORTE>
                    Reporteador.resuelveConsulta(parFormatoDIAN,
                                    Integer.parseInt(modulo),
                                    reemplazar, parametros);

                    ByteArrayInputStream reporteSerializado = JsfUtil
                                    .serializarReporteConstrasenia(
                                                    parFormatoDIAN,
                                                    parametros,
                                                    ConectorPool.ESQUEMA_SYSMAN,
                                                    FORMATOS.PDF,
                                                    registro.getCampos()
                                                                    .get(GeneralParameterEnum.NUMERO_DCTO
                                                                                    .getName())
                                                                    .toString());

                    String strAsunto = "Certificado de Ingresos y Retenciones - Año "
                        + ano;

                    String strMensaje = SessionUtil
                                    .getCompaniaIngreso()
                                    .getNombre()
                        + " NIT " + SessionUtil.getCompaniaIngreso()
                                        .getNit()
                        + "<br/>" + strAsunto;

                    correo.enviarAdjunto(registro.getCampos()
                                    .get("EMAIL_CORPORATIVO").toString(),
                                    strAsunto, strMensaje,
                                    "Certificado de Ingresos y Retenciones.pdf",
                                    reporteSerializado,
                                    "application/pdf"); 
                    
                    
                    mi_empleado =  SysmanFunciones
                            .nvl(registro.getCampos()
                                    .get("ID_DE_EMPLEADO"), "")
                    .toString();
                    
                         ejbNominaDiezRemote.actEnvioCorreoDian(
                                        compania, mi_empleado,ano);
  
                }
            }
            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_PROCESO_EJECUTADO"));
        }
        catch (SystemException | JRException | IOException
                        | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        
    }
    
    
    public void oprimirListadosEnviadosDian () {
        try {
            String reporte = "900030CertificadoIngresosRentenciones";
            Map<String, Object> parametros = new HashMap<>();
            Map<String, Object> reemplazar = new HashMap<>();

            reemplazar .put(GeneralParameterEnum.COMPANIA.getName(), compania);
           
            reemplazar .put(CertificadosDianControladorEnum.PARAM2.getValue(), estado);
            reemplazar .put(CertificadosDianControladorEnum.FECHA.getValue(), ano);
            
            if ("1".equals(opcion)) {
            	reemplazar .put(GeneralParameterEnum.ID_DE_EMPLEADO.getName(),
                                "0");
            }
            else {
            	reemplazar .put(GeneralParameterEnum.ID_DE_EMPLEADO.getName(), empleado);
            }

            parametros.put("PR_NOMBREEMPRESA",
                            SessionUtil.getCompaniaIngreso().getNombre());
            
            parametros.put("PR_FECHA1",
    				fecha1);
            
            parametros.put("PR_FECHA2",
    				fecha2);

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.PDF);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        
    }
    

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        try {
            servidor = ejbSysmanUtil.consultarParametro(compania,
                            "SERVIDOR PARA ENVIO EMAIL",
                            SessionUtil.getModulo(), new Date(), false);
            usuario = ejbSysmanUtil.consultarParametro(compania,
                            "USUARIO PARA ENVIO EMAIL",
                            SessionUtil.getModulo(), new Date(), false);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>

    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    public String getEmpleado() {
        return empleado;
    }

    public void setEmpleado(String empleado) {
        this.empleado = empleado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getSoloCrearPdf() {
        return soloCrearPdf;
    }

    public void setSoloCrearPdf(String soloCrearPdf) {
        this.soloCrearPdf = soloCrearPdf;
    }

    public String getFecha1() {
        return fecha1;
    }

    public void setFecha1(String fecha1) {
        this.fecha1 = fecha1;
    }

    public String getFecha2() {
        return fecha2;
    }

    public void setFecha2(String fecha2) {
        this.fecha2 = fecha2;
    }

    public String getDocumentoEmpleado() {
        return documentoEmpleado;
    }

    public void setDocumentoEmpleado(String documentoEmpleado) {
        this.documentoEmpleado = documentoEmpleado;
    }

    public String getAno() {
        return ano;
    }

    public void setAno(String ano) {
        this.ano = ano;
    }

    public Date getFechaExpedicion() {
        return fechaExpedicion;
    }

    public void setFechaExpedicion(Date fechaExpedicion) {
        this.fechaExpedicion = fechaExpedicion;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getLabelNombre() {
        return labelNombre;
    }

    public void setLabelNombre(String labelNombre) {
        this.labelNombre = labelNombre;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>

    public List<Registro> getListaCbofecha1() {
        return listaCbofecha1;
    }

    public void setListaCbofecha1(List<Registro> listaCbofecha1) {
        this.listaCbofecha1 = listaCbofecha1;
    }

    public List<Registro> getListaCboFecha2() {
        return listaCboFecha2;
    }

    public void setListaCboFecha2(List<Registro> listaCboFecha2) {
        this.listaCboFecha2 = listaCboFecha2;
    }

    public String getPermisoEnvioCorreo() {
        return permisoEnvioCorreo;
    }

    public void setPermisoEnvioCorreo(String permisoEnvioCorreo) {
        this.permisoEnvioCorreo = permisoEnvioCorreo;
    }

    public List<Registro> getListaAnno() {
        return listaAnno;
    }

    public void setListaAnno(List<Registro> listaAnno) {
        this.listaAnno = listaAnno;
    }

    public RegistroDataModelImpl getListaEmpleado() {
        return listaEmpleado;
    }

    public void setListaEmpleado(RegistroDataModelImpl listaEmpleado) {
        this.listaEmpleado = listaEmpleado;
    }

    public String getServidor() {
        return servidor;
    }

    public void setServidor(String servidor) {
        this.servidor = servidor;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

}

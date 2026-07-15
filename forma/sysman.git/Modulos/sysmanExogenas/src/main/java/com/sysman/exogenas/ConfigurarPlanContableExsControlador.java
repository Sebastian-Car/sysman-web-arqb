/*-
 * ConfigurarPlanContableExsControlador.java
 *
 * 1.0
 * 
 * 03/12/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.exogenas;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.exogenas.ejb.EjbExogenasCeroRemote;
import com.sysman.exogenas.enums.ConfigurarPlanContableExsControladorEnum;
import com.sysman.exogenas.enums.ConfigurarPlanContableExsControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Clase migrada para configuracion de las cuentas contables en exogenas
 *
 * @version 1.0, 03/12/2018
 * @author ybecerra
 */
@ManagedBean
@ViewScoped
public class ConfigurarPlanContableExsControlador extends BeanBaseContinuoAcmeImpl {
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual
     * inicio sesion el usuario, el valor de esta constante es asignado en el
     * constructor a la variable de sesion correspondiente
     */
    private final String compania;
    /**
     * Atributo que almacena el codigo del modulo por el cual ingresa a la
     * aplicacion
     */
    private final String modulo;
    /**
     * Atributo auxliar el cual es asiganado en el momento que se activa la edicion
     * de un registro. Toma el valor del indice dentro de la grilla del registro
     * seleccionado para editar
     */
    private int indice;
    /**
     * Atributo que almacena el valor del concepto almacenado antes de ser
     * mofificado el registro
     */
    private String concepto;
    // <DECLARAR_ATRIBUTOS>
    // </DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el valor del formato seleccionado en el combo formato
     */
    private String formato;
    /**
     * Atributo que almacena el valor del nombre del formato seleccionado en el
     * combo
     */
    private String nombreFormato;
    /**
     * Atributo que almacena el valor del ano seleccionado en el combo ano
     */
    private String ano;
    /**
     * Variable que valida la visibilidad del formato 1001
     */
    private boolean visibleFormato;
    /**
     * Atributo que almacena el valor del codigo del combo tipo
     */
    private String tipo;
    /**
     * Atributo que almacena el valor del ano seleccionado en el combo anio
     */
    private String anioDestino;
    /**
     * Atributo que valida la visibilidad del boton migrar plan cuentas
     */
    private boolean visibleMigrar;
    
    private boolean verDeducible;
    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Atributo que almacena el texto que se visualiza en el boton
     * configuracionFormato.
     */
    private String nombreActualizaInd;
    /**
     * Variable que activa o inactiva el boton configuracionformato
     */
    private boolean bloquearActualiza = true;
    /**
     * Atributo que valida la visibilidad de la columna distrital
     */
    private boolean visibleDistrital;
    /**
     * Atributo que valida la visibilidad del boton Cuentas Configuradas
     */
    private boolean visibleCuentas = false;
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de registros de la tabla ano
     */
    private List<Registro> listaAno;
        /**
     * Lista de registros de la tabla ano
     */
    private List<Registro> listaAnioDestino;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de registros de la tabla formatos
     */
    private RegistroDataModelImpl listaFormato;
    /**
     * Lista de registros de la tabla conceptoEx
     */
    private RegistroDataModelImpl listaConceptoEx;
    /**
     * Lista de registros de la tabla conceptoEx
     */
    private RegistroDataModelImpl listaConceptoExE;
    /**
     * Esta variable se usa como auxiliar para subformularios y en esta se alamcena
     * el identificador del registro que se selecciono
     */
    private String auxiliar;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    @EJB
    private EjbExogenasCeroRemote ejbExogenasCero;

    /**
     * Crea una nueva instancia de ConfigurarPlanContableExsControlador
     */
    public ConfigurarPlanContableExsControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try {
            // 2002
            numFormulario = GeneralCodigoFormaEnum.CONFIGURACION_PLAN_CONTABLE_EX_CONTROLADOR.getCodigo();
            validarPermisos();

            verDeducible = false;
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha
     * sido creado, en este se realizan las asignaciones iniciales necesarias para
     * la visualizacion del formulario, como son tablas, origenes de datos,
     * inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar() {

        enumBase = GenericUrlEnum.CONFIGURACION_EXOGENA;
        buscarLlave();
        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaAno();
        cargarListaAnoDestino();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaConceptoEx();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();

    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base el valor de la
     * consulta del formulario. Tambien carga la lista del formulario por primera
     * vez
     */
    @Override
    public void reasignarOrigen() {
        buscarUrls();

        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametrosListado.put(GeneralParameterEnum.ANO.getName(), ano);
        parametrosListado.put(GeneralParameterEnum.FORMATO.getName(), formato);
        urlListado = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(ConfigurarPlanContableExsControladorUrlEnum.URL153.getValue());

    }

    // <METODOS_CARGAR_LISTA>

    /**
     * 
     * Carga la lista listaAno
     *F
     */
    public void cargarListaAno() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAno = RegistroConverter
                    .toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                            ConfigurarPlanContableExsControladorUrlEnum.URL203.getValue())
                                    .getUrl(),
                            param));
        } catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }
    
    public void cargarListaAnoDestino(){
    	
    	Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaAnioDestino = RegistroConverter
                    .toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                            ConfigurarPlanContableExsControladorUrlEnum.URL203.getValue())
                                    .getUrl(),
                            param));
        } catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
    }
   
    /**
     * Metodo encargado de confirmar la ejecucion del proceso.
     * 
     * Realiza las siguientes acciones:
     * - Oculta la ventana de confirmacion.
     * - Ejecuta el proceso de migracion de conceptos exogenas.
     */
    public void aceptarConfirmacion() 
    {
    	visibleMigrar = false;
        ejecutarProceso();  
    }

    /**
     * Metodo encargado de cancelar la ventana de confirmacion.
     * 
     * Realiza las siguientes acciones:
     * - Oculta la ventana de confirmacion.
     * - Ejecuta nuevamente el flujo definido del proceso.
     */
    public void cancelarConfirmacion() 
    {
    	visibleMigrar = false;  
        ejecutarProceso();  
    }
    
    /**
     * Metodo encargado de ejecutar el proceso de copia de conceptos exogenas.
     * 
     * Realiza las siguientes acciones:
     * - Ejecuta el proceso de copia hacia el ano destino.
     * - Genera archivo de novedades cuando existen resultados.
     * - Muestra mensaje informativo al finalizar el proceso.
     * - Controla errores durante la ejecucion.
     */
    private void ejecutarProceso() {
        try {
            String resultado = ejbExogenasCero.copiarConceptoExogenas(
                    compania, anioDestino, ano, compania, formato);

            if (resultado != null && resultado.startsWith("====")) {
                archivoDescarga = JsfUtil.getArchivoDescarga(
                        JsfUtil.serializarPlano(resultado),
                        "Reporte_novedades.txt");

            } 
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB506"));

        } catch (SystemException | JRException | IOException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }
    
    /**
     * 
     * Carga la lista listaFormato
     *
     */
    public void cargarListaFormato() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(ConfigurarPlanContableExsControladorUrlEnum.URL177.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(ConfigurarPlanContableExsControladorEnum.PREFIJO.getValue(), tipo);

        listaFormato = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
                GeneralParameterEnum.CODIGO.getName());
    }

    /**
     * 
     * Carga la lista listaConceptoEx
     *
     */
    public void cargarListaConceptoEx() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(ConfigurarPlanContableExsControladorUrlEnum.URL228.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(ConfigurarPlanContableExsControladorEnum.FORMATO.getValue(), formato);

        listaConceptoEx = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
                GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaConceptoEx
     *
     */
    public void cargarListaConceptoExE() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(ConfigurarPlanContableExsControladorUrlEnum.URL228.getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(ConfigurarPlanContableExsControladorEnum.FORMATO.getValue(), formato);

        listaConceptoExE = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param, true,
                GeneralParameterEnum.CODIGO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>

    /**
     * 
     * Metodo ejecutado al oprimir el boton ConfigurarDeducible en la vista
     *
     *
     */
    public void oprimirConfigurarDeducible() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();
        parametros.put(GeneralParameterEnum.ANO.getName(), ano);
        Direccionador direccionador = new Direccionador();
        direccionador.setParametros(parametros);
        direccionador
                .setNumForm(String.valueOf(GeneralCodigoFormaEnum.CONFIGURAR_DEDUCIBLEEXS_CONTROLADOR.getCodigo()));
        SessionUtil.redireccionarForma(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton ConfiguracionFormato en la vista
     *
     *
     */
    public void oprimirConfiguracionFormato() {
        // <CODIGO_DESARROLLADO>
        try {
            ejbExogenasCero.actualizarConceptoExogena(compania, Integer.parseInt(ano), formato,
                    SessionUtil.getUser().getCodigo());
            JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));
        } catch (NumberFormatException | SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton CuentasConfiguradas en la vista
     *
     *
     */
    public void oprimirCuentasConfiguradas() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> parametros = new HashMap<>();
        parametros.put(GeneralParameterEnum.ANO.getName().toLowerCase(), ano);
        parametros.put(GeneralParameterEnum.FORMATO.getName().toLowerCase(), formato);
        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(String.valueOf(GeneralCodigoFormaEnum.CUENTAS_CONFIGURADAS_CONTROLADOR.getCodigo()));
        direccionador.setParametros(parametros);
        SessionUtil.redireccionarForma(direccionador, modulo);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton ConfigurarCuentas en la vista
     *
     *
     */
    public void oprimirConfigurarCuentas() {
        // <CODIGO_DESARROLLADO>
        UrlBean urlUpdate = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(ConfigurarPlanContableExsControladorUrlEnum.URL409.getValue());
        Map<String, Object> parametros = new TreeMap<>();
        parametros.put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());
        parametros.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
        parametros.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametros.put(GeneralParameterEnum.ANO.getName(), ano);
        Parameter parameter = new Parameter();
        parameter.setFields(parametros);
        try {
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);
        } catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al presionar el boton
     * actualizar vigencia plan cuentas.
     * 
     * Control de cambio: 4284
     * 
     * Se ajusta el proceso para no ejecutar directamente el
     * procedimiento ejbExogenasCero.migrarPlanCuentas.
     * 
     * La migracion ahora se realiza mediante el metodo
     * copiarConceptoExogenas ejecutado desde ejecutarProceso(),
     * centralizando validaciones y manejo de novedades.
     */
    public void oprimirActualizarVigenciaPlanCuentas(){
    	try{

    		visibleMigrar = true;
    		Calendar calendario = new GregorianCalendar();
    		anioDestino = String.valueOf(calendario.get(Calendar.YEAR) + 1); 

    		JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_PROCESO_EJECUTADO"));
    	} catch(Exception e){
    		logger.error(e.getMessage(), e);
    		JsfUtil.agregarMensajeError(e.getMessage());
    	}
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton EnviarExcel en la vista
     *
     *
     */
    public void oprimirEnviarExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarExcel();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    /**
     * Metodo que valida que accion debe ejecutar al momento de activar la edicion
     * de un registro
     * 
     * @return
     */
    private boolean validarCuentaConfigurada() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.FORMATO.getName(), formato);
        param.put(GeneralParameterEnum.CONCEPTO.getName(), concepto);
        param.put(GeneralParameterEnum.CUENTA.getName(),
                registro.getCampos().get(GeneralParameterEnum.CUENTA.getName()));

        try {
            Registro rsCuentaConfigurada = RegistroConverter
                    .toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                            ConfigurarPlanContableExsControladorUrlEnum.URL268.getValue())
                                    .getUrl(),
                            param));
            if (rsCuentaConfigurada == null && !SysmanFunciones
                    .nvl(registro.getCampos().get(GeneralParameterEnum.CONCEPTO.getName()), "").toString().isEmpty()) {

                UrlBean urlCreate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(GenericUrlEnum.CONFIGURACION_EXOGENA.getCreateKey());
                param.put(GeneralParameterEnum.CONCEPTO.getName(),
                        registro.getCampos().get(GeneralParameterEnum.CONCEPTO.getName()));
                param.put(ConfigurarPlanContableExsControladorEnum.CLASE_EXOGENA.getValue(), tipo);
                param.put(GeneralParameterEnum.DATE_CREATED.getName(), new Date());
                param.put(GeneralParameterEnum.CREATED_BY.getName(), SessionUtil.getUser().getCodigo());
                Parameter parameter = new Parameter();
                parameter.setFields(param);
                requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(), parameter);
                return true;

            } else if (rsCuentaConfigurada != null && !SysmanFunciones
                    .nvl(registro.getCampos().get(GeneralParameterEnum.CONCEPTO.getName()), "").toString().isEmpty()) {
                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(GenericUrlEnum.CONFIGURACION_EXOGENA.getUpdateKey());
                Map<String, Object> paramUpdate = new TreeMap<>();
                paramUpdate.put(ConfigurarPlanContableExsControladorEnum.KEY_COMPANIA.getValue(), compania);
                paramUpdate.put(ConfigurarPlanContableExsControladorEnum.KEY_ANO.getValue(), ano);
                paramUpdate.put(ConfigurarPlanContableExsControladorEnum.KEY_FORMATO.getValue(), formato);
                paramUpdate.put(ConfigurarPlanContableExsControladorEnum.KEY_CONCEPTO.getValue(), concepto);
                paramUpdate.put(ConfigurarPlanContableExsControladorEnum.KEY_CUENTA.getValue(),
                        registro.getCampos().get(GeneralParameterEnum.CUENTA.getName()));

                paramUpdate.put(GeneralParameterEnum.CONCEPTO.getName(),
                        registro.getCampos().get(GeneralParameterEnum.CONCEPTO.getName()));
                paramUpdate.put(ConfigurarPlanContableExsControladorEnum.CLASE_EXOGENA.getValue(), rsCuentaConfigurada
                        .getCampos().get(ConfigurarPlanContableExsControladorEnum.CLASE_EXOGENA.getValue()));

                paramUpdate.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
                paramUpdate.put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());
                Parameter parameter = new Parameter();
                parameter.setFields(paramUpdate);

                requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);
                return true;

            } else if (rsCuentaConfigurada != null && SysmanFunciones
                    .nvl(registro.getCampos().get(GeneralParameterEnum.CONCEPTO.getName()), "").toString().isEmpty()) {

                UrlBean urlDelete = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(GenericUrlEnum.CONFIGURACION_EXOGENA.getDeleteKey());
                rsCuentaConfigurada.getCampos().put(ConfigurarPlanContableExsControladorEnum.KEY_COMPANIA.getValue(),
                        compania);
                rsCuentaConfigurada.getCampos().put(ConfigurarPlanContableExsControladorEnum.KEY_ANO.getValue(), ano);
                rsCuentaConfigurada.getCampos().put(ConfigurarPlanContableExsControladorEnum.KEY_FORMATO.getValue(),
                        formato);
                rsCuentaConfigurada.getCampos().put(ConfigurarPlanContableExsControladorEnum.KEY_CONCEPTO.getValue(),
                        concepto);
                rsCuentaConfigurada.getCampos().put(ConfigurarPlanContableExsControladorEnum.KEY_CUENTA.getValue(),
                        registro.getCampos().get(GeneralParameterEnum.CUENTA.getName()));

                requestManager.delete(urlDelete.getUrl(), rsCuentaConfigurada.getCampos());
                return true;
            } else {
                return true;
            }

        } catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }

        return false;
    }

    /**
     * Metodo que actualiza los indicadores Distrital,Fuente Practicada,Fuente
     * Asumida, Iva Comun,Iva Simplificado,Ica,Formato 1001
     */
    public void actualizarIndicadores() {

        UrlBean urlUpdate = UrlServiceUtil.getInstance()
                .getUrlServiceByUrlByEnumID(ConfigurarPlanContableExsControladorUrlEnum.URL427.getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), ano);
        param.put(GeneralParameterEnum.CUENTA.getName(),
                registro.getCampos().get(GeneralParameterEnum.CUENTA.getName()));
        param.put(ConfigurarPlanContableExsControladorEnum.EXDISTRITAL.getValue(),
                registro.getCampos().get(ConfigurarPlanContableExsControladorEnum.EXDISTRITAL.getValue()));

        param.put(ConfigurarPlanContableExsControladorEnum.RETEPRACTICADA.getValue(),
                registro.getCampos().get(ConfigurarPlanContableExsControladorEnum.RETEPRACTICADA.getValue()));
        param.put(ConfigurarPlanContableExsControladorEnum.RETEASUMIDA.getValue(),
                registro.getCampos().get(ConfigurarPlanContableExsControladorEnum.RETEASUMIDA.getValue()));
        param.put(ConfigurarPlanContableExsControladorEnum.IVACOMUN.getValue(),
                registro.getCampos().get(ConfigurarPlanContableExsControladorEnum.IVACOMUN.getValue()));
        param.put(ConfigurarPlanContableExsControladorEnum.IVASIMPLIFICADO.getValue(),
                registro.getCampos().get(ConfigurarPlanContableExsControladorEnum.IVASIMPLIFICADO.getValue()));
        param.put(ConfigurarPlanContableExsControladorEnum.RETEICA.getValue(),
                registro.getCampos().get(ConfigurarPlanContableExsControladorEnum.RETEICA.getValue()));
        param.put(ConfigurarPlanContableExsControladorEnum.MOSTRARF1001.getValue(),
                registro.getCampos().get(ConfigurarPlanContableExsControladorEnum.MOSTRARF1001.getValue()));
        param.put(ConfigurarPlanContableExsControladorEnum.IND_AGENTE_RETENCION.getValue(),
                registro.getCampos().get(ConfigurarPlanContableExsControladorEnum.IND_AGENTE_RETENCION.getValue()));
        param.put(ConfigurarPlanContableExsControladorEnum.IND_SUJETO_RETENCION.getValue(),
                registro.getCampos().get(ConfigurarPlanContableExsControladorEnum.IND_SUJETO_RETENCION.getValue()));
        param.put(GeneralParameterEnum.DATE_MODIFIED.getName(), new Date());
        param.put(GeneralParameterEnum.MODIFIED_BY.getName(), SessionUtil.getUser().getCodigo());

        Parameter parameter = new Parameter();
        parameter.setFields(param);

        try {
            requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(), parameter);
        } catch (SystemException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }

    }

    /**
     * Metodo que se invoca en el metodo de oprimirEnviarExcel
     */
    public void generarExcel() {
        String strSql = null;
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("formato", formato);
        reemplazar.put("ano", ano);
        strSql = Reporteador.resuelveConsulta("800281ConfiguracionExogena", Integer.parseInt(modulo), reemplazar);
        try {
            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql, ConectorPool.ESQUEMA_SYSMAN,
                    ReportesBean.FORMATOS.EXCEL);
        } catch (JRException | IOException | SQLException | DRException | SysmanException e) {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }
    }

    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Ano
     * 
     * 
     */
    public void cambiarAno() {
        // <CODIGO_DESARROLLADO>
        if (formato == null) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4269"));
        } else {
            reasignarOrigen();
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control tipo
     * 
     */
    public void cambiartipo() {
        // <CODIGO_DESARROLLADO>
        cargarListaFormato();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ReteFuentePracticada en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum indice de la fila seleccionada
     */
    public void cambiarReteFuentePracticadaC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        if ((boolean) listaInicial.getDatasource().get(rowNum % 10).getCampos()
                .get(ConfigurarPlanContableExsControladorEnum.RETEPRACTICADA.getValue())) {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                    .put(ConfigurarPlanContableExsControladorEnum.RETEASUMIDA.getValue(), false);
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                    .put(ConfigurarPlanContableExsControladorEnum.IVACOMUN.getValue(), false);
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                    .put(ConfigurarPlanContableExsControladorEnum.IVASIMPLIFICADO.getValue(), false);
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                    .put(ConfigurarPlanContableExsControladorEnum.RETEICA.getValue(), false);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ReteFuente en la fila seleccionada
     * dentro de la grilla
     * 
     * 
     * @param rowNum indice de la fila seleccionada
     */
    public void cambiarReteFuenteC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        if ((boolean) listaInicial.getDatasource().get(rowNum % 10).getCampos()
                .get(ConfigurarPlanContableExsControladorEnum.RETEASUMIDA.getValue())) {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                    .put(ConfigurarPlanContableExsControladorEnum.RETEPRACTICADA.getValue(), false);
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                    .put(ConfigurarPlanContableExsControladorEnum.IVACOMUN.getValue(), false);
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                    .put(ConfigurarPlanContableExsControladorEnum.IVASIMPLIFICADO.getValue(), false);
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                    .put(ConfigurarPlanContableExsControladorEnum.RETEICA.getValue(), false);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ReteIvaComun en la fila seleccionada
     * dentro de la grilla
     * 
     * 
     * @param rowNum indice de la fila seleccionada
     */
    public void cambiarReteIvaComunC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        if ((boolean) listaInicial.getDatasource().get(rowNum % 10).getCampos()
                .get(ConfigurarPlanContableExsControladorEnum.IVACOMUN.getValue())) {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                    .put(ConfigurarPlanContableExsControladorEnum.RETEPRACTICADA.getValue(), false);
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                    .put(ConfigurarPlanContableExsControladorEnum.RETEASUMIDA.getValue(), false);
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                    .put(ConfigurarPlanContableExsControladorEnum.IVASIMPLIFICADO.getValue(), false);
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                    .put(ConfigurarPlanContableExsControladorEnum.RETEICA.getValue(), false);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ReteIva en la fila seleccionada dentro
     * de la grilla
     * 
     * 
     * @param rowNum indice de la fila seleccionada
     */
    public void cambiarReteIvaC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        if ((boolean) listaInicial.getDatasource().get(rowNum % 10).getCampos()
                .get(ConfigurarPlanContableExsControladorEnum.IVASIMPLIFICADO.getValue())) {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                    .put(ConfigurarPlanContableExsControladorEnum.RETEPRACTICADA.getValue(), false);
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                    .put(ConfigurarPlanContableExsControladorEnum.RETEASUMIDA.getValue(), false);
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                    .put(ConfigurarPlanContableExsControladorEnum.IVACOMUN.getValue(), false);
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                    .put(ConfigurarPlanContableExsControladorEnum.RETEICA.getValue(), false);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ReteIca en la fila seleccionada dentro
     * de la grilla
     * 
     * 
     * @param rowNum indice de la fila seleccionada
     */
    public void cambiarReteIcaC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        if ((boolean) listaInicial.getDatasource().get(rowNum % 10).getCampos()
                .get(ConfigurarPlanContableExsControladorEnum.RETEICA.getValue())) {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                    .put(ConfigurarPlanContableExsControladorEnum.RETEPRACTICADA.getValue(), false);
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                    .put(ConfigurarPlanContableExsControladorEnum.RETEASUMIDA.getValue(), false);
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                    .put(ConfigurarPlanContableExsControladorEnum.IVACOMUN.getValue(), false);
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                    .put(ConfigurarPlanContableExsControladorEnum.IVASIMPLIFICADO.getValue(), false);
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaFormato
     *
     *
     * @param event objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaFormato(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        nombreFormato = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.NOMBRE.getName()), "")
                .toString();
        formato = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "")
                .toString();

        if (ano != null) {
            reasignarOrigen();
        }
        cargarListaConceptoExE();
        if ("1001".equals(formato)) {
            visibleFormato = verDeducible = true;
            visibleDistrital = false;
            nombreActualizaInd = idioma.getString("TB_TB4272");
        } else if ("1003".equals(formato)) {
            visibleFormato = true;
            nombreActualizaInd = idioma.getString("TB_TB4273");
            visibleDistrital = true;
            verDeducible = false;
        }

        visibleCuentas = (boolean) registroAux.getCampos()
                .get(ConfigurarPlanContableExsControladorEnum.IND_DETALLE.getValue());

        bloquearActualiza = false;
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaConceptoEx
     *
     *
     * @param event objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConceptoEx(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(GeneralParameterEnum.CONCEPTO.getName(),
                registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()));
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista listaConceptoEx
     *
     *
     * @param event objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConceptoExE(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones.nvl(registroAux.getCampos().get(GeneralParameterEnum.CODIGO.getName()), "")
                .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a
     * tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        ano = String.valueOf(SysmanFunciones.ano(new Date()));
        Calendar calendario = new GregorianCalendar();
		anioDestino = String.valueOf(calendario.get(Calendar.YEAR) + 1); 
        cambiarAno();
        nombreActualizaInd = idioma.getString("TB_TB4273");
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado
     */
    @Override
    public void cancelarEdicion(RowEditEvent event) {
        getListaInicial().load();
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     * 
     * @return true
     */
    @Override
    public boolean insertarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
     * 
     * 
     * @return true
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        if (validarCuentaConfigurada()) {
            actualizarIndicadores();
            JsfUtil.agregarMensajeInformativo(idioma.getString("MSM_REGISTRO_MODIFICADO"));
            reasignarOrigen();
        }
        // </CODIGO_DESARROLLADO>
        return false;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del
     * registro
     * 
     * 
     * @return true
     */
    @Override
    public boolean actualizarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     * 
     * 
     * @return true
     */
    @Override
    public boolean eliminarAntes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del registro
     * 
     * 
     * @return true
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion, en el se
     * pueden remover valores auxiliares que no se desee o se deban enviar en el
     * registro
     */
    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del formulario
     * 
     *
     * @param registro registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro) {
        indice = listaInicial.getRowIndex();
        concepto = SysmanFunciones.nvl(registro.getCampos().get(GeneralParameterEnum.CONCEPTO.getName()), "")
                .toString();
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y edicion del
     * registro se usa cuando se desean agregar valores al registro despues de
     * dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable tipo
     * 
     * @return tipo
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Asigna la variable tipo
     * 
     * @param tipo Variable a asignar en tipo
     */
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    /**
     * Retorna la variable formato
     * 
     * @return formato
     */
    public String getFormato() {
        return formato;
    }

    /**
     * Asigna la variable formato
     * 
     * @param formato Variable a asignar en formato
     */
    public void setFormato(String formato) {
        this.formato = formato;
    }

    /**
     * Retorna la variable ano
     * 
     * @return ano
     */
    public String getAno() {
        return ano;
    }

    /**
     * Asigna la variable ano
     * 
     * @param ano Variable a asignar en ano
     */
    public void setAno(String ano) {
        this.ano = ano;
    }

    /**
     * Retorna la variable visibleFormato
     * 
     * @return visibleFormato
     */
    public boolean isVisibleFormato() {
        return visibleFormato;
    }

    /**
     * Asigna la variable visibleFormato
     * 
     * @param visibleFormato Variable a asignar en visibleFormato
     */
    public void setVisibleFormato(boolean visibleFormato) {
        this.visibleFormato = visibleFormato;
    }

    /**
     * Retorna la variable nombreFormato
     * 
     * @return nombreFormato
     */
    public String getNombreFormato() {
        return nombreFormato;
    }

    /**
     * Asigna la variable nombreFormato
     * 
     * @param nombreFormato Variable a asignar en nombreFormato
     */
    public void setNombreFormato(String nombreFormato) {
        this.nombreFormato = nombreFormato;
    }

    /**
     * Retorna la variable nombreFormato
     * 
     * @return nombreFormato
     */
    public String getNombreActualizaInd() {
        return nombreActualizaInd;
    }

    /**
     * Asigna la variable nombreFormato
     * 
     * @param nombreFormato Variable a asignar en nombreFormato
     */
    public void setNombreActualizaInd(String nombreActualizaInd) {
        this.nombreActualizaInd = nombreActualizaInd;
    }

    /**
     * Retorna la variable nombreFormato
     * 
     * @return nombreFormato
     */
    public boolean isBloquearActualiza() {
        return bloquearActualiza;
    }

    /**
     * Asigna la variable nombreFormato
     * 
     * @param nombreFormato Variable a asignar en nombreFormato
     */
    public void setBloquearActualiza(boolean bloquearActualiza) {
        this.bloquearActualiza = bloquearActualiza;
    }

    /**
     * Retorna la variable visibleDistrital
     * 
     * @return visibleDistrital
     */
    public boolean isVisibleDistrital() {
        return visibleDistrital;
    }

    /**
     * Asigna la variable visibleDistrital
     * 
     * @param visibleDistrital Variable a asignar en visibleDistrital
     */
    public void setVisibleDistrital(boolean visibleDistrital) {
        this.visibleDistrital = visibleDistrital;
    }

    /**
     * Retorna la variable visibleDistrital
     * 
     * @return visibleDistrital
     */
    public boolean isVisibleCuentas() {
        return visibleCuentas;
    }

    /**
     * Asigna la variable visibleDistrital
     * 
     * @param visibleDistrital Variable a asignar en visibleDistrital
     */
    public void setVisibleCuentas(boolean visibleCuentas) {
        this.visibleCuentas = visibleCuentas;
    }

    /**
     * Asigna la variable indice
     * 
     * @param indice Variable a asignar en indice
     */
    public void setIndice(int indice) {
        this.indice = indice;
    }

    /**
     * Retorna la variable indice
     * 
     * @return indice
     */
    public int getIndice() {
        return indice;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
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
     * @param listaAno Variable a asignar en listaAno
     */
    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaFormato
     * 
     * @return listaFormato
     */
    public RegistroDataModelImpl getListaFormato() {
        return listaFormato;
    }

    /**
     * Asigna la lista listaFormato
     * 
     * @param listaFormato Variable a asignar en listaFormato
     */
    public void setListaFormato(RegistroDataModelImpl listaFormato) {
        this.listaFormato = listaFormato;
    }

    /**
     * Retorna la lista listaConceptoEx
     * 
     * @return listaConceptoEx
     */
    public RegistroDataModelImpl getListaConceptoEx() {
        return listaConceptoEx;
    }

    /**
     * Asigna la lista listaConceptoEx
     * 
     * @param listaConceptoEx Variable a asignar en listaConceptoEx
     */
    public void setListaConceptoEx(RegistroDataModelImpl listaConceptoEx) {
        this.listaConceptoEx = listaConceptoEx;
    }

    /**
     * Retorna la lista listaConceptoEx
     * 
     * @return listaConceptoEx
     */
    public RegistroDataModelImpl getListaConceptoExE() {
        return listaConceptoExE;
    }

    /**
     * Asigna la lista listaConceptoEx
     * 
     * @param listaConceptoEx Variable a asignar en listaConceptoEx
     */
    public void setListaConceptoExE(RegistroDataModelImpl listaConceptoExE) {
        this.listaConceptoExE = listaConceptoExE;
    }

    /**
     * Retorna la variable auxiliar
     * 
     * @return auxiliar
     */
    public String getAuxiliar() {
        return auxiliar;
    }

    /**
     * Asigna la variable auxiliar
     * 
     * @param auxiliar Variable a asignar en auxiliar
     */
    public void setAuxiliar(String auxiliar) {
        this.auxiliar = auxiliar;
    }

    /**
     * @return the verDeducible
     */
    public boolean isVerDeducible() {
        return verDeducible;
    }

    /**
     * @param verDeducible the verDeducible to set
     */
    public void setVerDeducible(boolean verDeducible) {
        this.verDeducible = verDeducible;
    }
	public String getAnioDestino() {
		return anioDestino;
	}

	public void setAnioDestino(String anioDestino) {
		this.anioDestino = anioDestino;
	}

	public List<Registro> getListaAnioDestino() {
		return listaAnioDestino;
	}

	public void setListaAnioDestino(List<Registro> listaAnioDestino) {
		this.listaAnioDestino = listaAnioDestino;
	}

	public boolean isVisibleMigrar() {
		return visibleMigrar;
	}

	public void setVisibleMigrar(boolean visibleMigrar) {
		this.visibleMigrar = visibleMigrar;
	}
    // </SET_GET_LISTAS_COMBO_GRANDE>
}

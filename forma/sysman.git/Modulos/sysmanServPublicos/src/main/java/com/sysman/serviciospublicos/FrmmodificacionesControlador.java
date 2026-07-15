/*-
 * FrmmodificacionesControlador.java
 *
 * 1.0
 *
 * 22/12/2016
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosCeroRemote;
import com.sysman.serviciospublicos.enums.FrmmodificacionesControladorEnum;
import com.sysman.serviciospublicos.enums.FrmmodificacionesControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.FileNotFoundException;
import java.io.IOException;
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

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Clase que se encarga de la logica de negocio de las modificaciones al facturado
 *
 * @version 1.0, 22/12/2016
 * @author jguerrero
 * @version 2.0, 01/06/2017
 * @author spina - se refactoriza para dss, depuracion sonar y ejbs
 *
 * @author ybecerra
 * @version 3, 13/06/2017 Implementacion al llamado de GeneralCodigoFormaEnum, para el codigo del formulario
 */
@ManagedBean
@ViewScoped
public class FrmmodificacionesControlador extends BeanBaseDatosAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>

    /**
     * Variable encargada de almacenar temporalmente el Nombreperiodo que es el resultado de la funcion FC_NOMBRE_PERIODO
     */
    private String periodo;
    /**
     * Variable encargada de almacenar temporalmente el periodo que es enviado desde el formulario pedirciclo correspondiente del combo ciclo.
     */
    private String numeroperiodo;
    /**
     * Variable encargada de almacenar temporalmente el ciclo que es enviado desde el formulario pedirciclo correspondiente del combo ciclo.
     */
    private String ciclo;
    /**
     * Variable encargada de almacenar temporalmente el a�o que es enviado desde el formulario pedirciclo correspondiente del combo ciclo.
     */
    private String ano;
    /**
     * Variable encargada de almacenar temporalmente la cosulta para el combo concepto, esta depende te un parametro
     */
    private String totalValorNuevo;
    /**
     * Variable encargada de almacenar temporalmente el resultado de la sumatoria de los valores facturados en la consulta que sera visualizado al final del subformulario
     */
    private String totalValorAnterior;
    /**
     * Variable encargada de almacenar temporalmente el campo TOTFACTURAPERACTUAL a la hora de cargar un registro .
     */
    private String valorAnterior;
    /**
     * Variable encargada de almacenar temporalmente la cosulta para el combo concepto, esta depende te un parametro
     */

    private String tasaRecargo;
    /**
     * Variable encargada de almacenar temporalmente la cosulta para el combo concepto, esta depende te un parametro
     */
    private String fimm;
    /**
     * Variable encargada de almacenar temporalmente la cosulta para el combo concepto, esta depende te un parametro
     */
    private boolean bloquearComponentes;

    /**
     * Esta variable se usa como auxiliar para subformularios y en esta se alamcena el identificador del registro que se selecciono
     */
    private String auxiliar;

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    /**
     * Lista encargada de almacenar los datos del resultado de la consulta de codigos de modificacione
     */
    private List<Registro> listaCodigo;
    /**
     * Lista encargada de almacenar los datos del resultado de la consulta a la base de datos a la tabla concept
     */
    private RegistroDataModelImpl listaconcepto;
    /**
    
     */
    private RegistroDataModelImpl listaconceptoE;

    private RegistroDataModelImpl listaCodigoRuta;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    /**
     * Lista encargada de almacenar temporalmente todos los datos que seran cargados en el subformulario frmmodioficacionesSub
     *
     *
     */
    private List<Registro> listaFrmmodificacionessub;
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    /**
     * Atributo de referencia para el subformulario
     */
    private Registro registroSub;

    public final String codigorutaCons;
    public final String nombreCons;
    public final String fechaCons;
    public final String spModifiDeudaCons;
    public final String codigoCons;
    public final String periodoCons;
    public final String nombreConceptoCons;

    private boolean conceptoFueraDerango;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbServiciosPublicosCeroRemote ejbServiciosPublicosCero;

    // </DECLARAR_ADICIONALES>
    /**
     * Crea una nueva instancia de FrmmodificacionesControlador
     */
    public FrmmodificacionesControlador()
    {
        super();
        codigorutaCons = GeneralParameterEnum.CODIGORUTA.getName();
        nombreCons = GeneralParameterEnum.NOMBRE.getName();
        fechaCons = GeneralParameterEnum.FECHA.getName();
        codigoCons = GeneralParameterEnum.CODIGO.getName();
        spModifiDeudaCons = "SP_MODIFICACIONESDEUDA";
        periodoCons = GeneralParameterEnum.PERIODO.getName();
        nombreConceptoCons = "NOMBRECONCEPTO";

        compania = SessionUtil.getCompania();
        try
        {

            Map<String, Object> parametros = SessionUtil.getFlash();
            if (parametros != null)
            {
                ciclo = parametros.get("ciclo").toString();
                ano = parametros.get("anio").toString();
                numeroperiodo = parametros.get("periodo").toString();
                tasaRecargo = parametros.get("TASARECARGO").toString();
                parametros.get("periodo").toString();
            }

            numFormulario = GeneralCodigoFormaEnum.FRMMODIFICACIONES_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            registroSub = new Registro(new HashMap<String, Object>());

            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas, menos las que son de subformularios
     */
    @Override
    public void iniciarListas()
    {
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCodigoRuta();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CARGAR_LISTA>
        cargarListaCodigo();

        // </CARGAR_LISTA>
    }

    /**
     * En este metodo se hace la invocacion de lo metodos de carga de todas las listas que son de subformularios
     */
    @Override
    public void iniciarListasSub()
    {
        // <CARGAR_LISTAS_SUBFORM>
        cargarListaFrmmodificacionessub();
        cargarTotales();
        // </CARGAR_LISTAS_SUBFORM>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
    }

    /**
     * En este metodo se iguala a null todas las listas de los subformularios
     */
    @Override
    public void iniciarListasSubNulo()
    {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        listaFrmmodificacionessub = null;
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        enumBase = GenericUrlEnum.SP_MODIFICACIONES;
        buscarLlave();
        asignarOrigenDatos();
    }

    /**
     * Se realiza la asignacion de la variable origenDatos por la consulta correspondiente del formulario
     *
     *
     *
     */
    @Override
    public void asignarOrigenDatos()
    {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        parametrosListado.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        parametrosListado.put(FrmmodificacionesControladorEnum.TIPO.getValue(),
                        2);
    }

    /**
     *
     * Carga la lista listaFrmmodificacionessub que sera mostroada en el subformulario
     *
     *
     */
    public void cargarListaFrmmodificacionessub()
    {
        try
        {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(FrmmodificacionesControladorEnum.CODIGORUTA.getValue(),
                            registro.getCampos().get(codigorutaCons));
            param.put(FrmmodificacionesControladorEnum.FECHA.getValue(),
                            registro.getCampos().get(fechaCons));
            param.put(FrmmodificacionesControladorEnum.HORA.getValue(),
                            registro.getCampos().get("HORA"));

            listaFrmmodificacionessub = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmmodificacionesControladorUrlEnum.URL9710
                                                                            .getValue())
                                            .getUrl(), param),
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            spModifiDeudaCons));
        }
        catch (SysmanException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaCodigo que sera visualizado en el formulario
     *
     *
     */
    public void cargarListaCodigo()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try
        {
            listaCodigo = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            FrmmodificacionesControladorUrlEnum.URL9711
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaconcepto()
    {

        try
        {
            Map<String, Object> param = new HashMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
            param.put(GeneralParameterEnum.CODIGORUTA.getName(),
                            registro.getCampos().get(codigorutaCons));
            param.put(GeneralParameterEnum.ANO.getName(), registro.getCampos()
                            .get(GeneralParameterEnum.ANO.getName()));
            param.put(GeneralParameterEnum.PERIODO.getName(),
                            registro.getCampos().get(periodoCons));
            UrlBean urlBean;

            if ("SI".equals(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "PERMITE MODIFICACIONES FACTURADO GENERAL",
                                            SessionUtil.getModulo(), new Date(),
                                            true),
                                            "NO"))
                && (SessionUtil.getNivelGrupo(SessionUtil.getModulo()) >= 8)
                && "ADMIN_FACTURACION".equals(
                                SessionUtil.getGrupo(SessionUtil.getModulo())
                                                .getCodigo()))
            {

                urlBean = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FrmmodificacionesControladorUrlEnum.URL9713
                                                                .getValue());
            }
            else
            {

                urlBean = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FrmmodificacionesControladorUrlEnum.URL9714
                                                                .getValue());
            }
            listaconcepto = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            true, codigoCons);

            if (listaconcepto.getDatasource().isEmpty())
            {
                conceptoFueraDerango = true;
            }
        }
        catch (SystemException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     *
     * Carga la lista listaconcepto
     *
     *
     */
    public void cargarListaconceptoE()
    {
        listaconceptoE = listaconcepto;
    }

    /**
     *
     * Carga la lista listaCodigoRuta y se ve grafricamente en el formulario
     *
     *
     */
    public void cargarListaCodigoRuta()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmmodificacionesControladorUrlEnum.URL9712
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        listaCodigoRuta = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        codigorutaCons);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control concepto, ademas pone el nombreConcepto en el campo del formulario
     *
     *
     *
     */

    public void seleccionarFilaconcepto(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registroSub.getCampos().put(GeneralParameterEnum.CONCEPTO.getName(),
                        registroAux.getCampos().get(codigoCons));

        registroSub.getCampos().put(nombreConceptoCons,
                        registroAux.getCampos().get(nombreCons).toString());
        registroSub.getCampos().put("VRANT",
                        registroAux.getCampos().get("VALOR_FACTURADO"));

    }

    public void seleccionarFilaconceptoE(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        auxiliar = SysmanFunciones
                        .nvl(registroAux.getCampos().get(nombreCons), "")
                        .toString();
    }

    /**
     * Metodo ejecutado al cambiar el control concepto en la fila seleccionada dentro de la grilla
     *
     *
     *
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarconceptoC(int rowNum)
    {

        listaFrmmodificacionessub.get(rowNum % 10).getCampos()
                        .put(nombreConceptoCons, auxiliar);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCodigoRuta
     *
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoRuta(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        registro.getCampos().put(codigorutaCons,
                        registroAux.getCampos().get(codigorutaCons));

        registro.getCampos().put(nombreCons,
                        registroAux.getCampos().get(nombreCons).toString());

        registro.getCampos().put("USO", registroAux.getCampos().get("USO"));
        registro.getCampos().put("ESTRATO",
                        registroAux.getCampos().get("ESTRATO"));

        if (registro.getCampos().get("FIMM") != null)
        {
            fimm = registro.getCampos().get("FIMM").toString();
        }

        if (validacionesCodigoRuta() == 1)
        {

            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB2713"));
            registro.getCampos().put(codigorutaCons, null);
            registro.getCampos().put(GeneralParameterEnum.NOMBRE.getName(),
                            null);
            return;

        }

        if (validacionesCodigoRuta() == 2)
        {
            JsfUtil.agregarMensajeAlerta(
                            idioma.getString("TB_TB2712"));
            registro.getCampos().put(codigorutaCons, null);
            registro.getCampos().put(nombreCons, null);
            return;

        }

    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Imprime en la vista y genera el informe
     *
     *
     *
     */
    public void oprimirImprime()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        genInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo invocado al ejecutar el comando remoto mostrarMensaje en la vista
     *
     *
     */
    public void ejecutarmostrarMensaje()
    {
        // <CODIGO_DESARROLLADO>
        if (conceptoFueraDerango)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2716"));
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    /**
     * Metodo de insercion del formulario Frmmodificacionessub
     *
     *
     */
    public void agregarRegistroSubFrmmodificacionessub()
    {
        try
        {

            cargarRegistroSub();
            registroSub.getCampos().put(
                            GeneralParameterEnum.CREATED_BY.getName(),
                            SessionUtil.getUser().getCodigo());
            registroSub.getCampos().put(
                            GeneralParameterEnum.DATE_CREATED.getName(),
                            new Date());
            UrlBean urlCreate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SP_MODIFICACIONESDEUDA
                                                            .getCreateKey());
            requestManager.save(urlCreate.getUrl(), urlCreate.getMetodo(),
                            registroSub.getCampos());

            cargarListaFrmmodificacionessub();
            cargarTotales();

            JsfUtil.agregarMensajeInformativo(
                            idioma.getString("MSM_REGISTRO_INGRESADO"));

        }
        catch (SystemException ex)
        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            registroSub = new Registro(new HashMap<String, Object>());
            actualizarTotatlesSub();
        }
    }

    /**
     * Metodo de edicion del formulario Frmmodificacionessub
     *
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void editarRegSubFrmmodificacionessub(RowEditEvent event)
    {
        Registro reg = (Registro) event.getObject();
        try
        {
            int conteo;
            reg.getCampos().remove(nombreConceptoCons);
            reg.getCampos().remove("RNUM");
            reg.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
            reg.getCampos().put(GeneralParameterEnum.DATE_MODIFIED.getName(),
                            new Date());
            reg.getCampos().put(GeneralParameterEnum.MODIFIED_BY.getName(),
                            SessionUtil.getUser().getCodigo());

            UrlBean urlUpdate = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SP_MODIFICACIONESDEUDA
                                                            .getUpdateKey());

            conteo = requestManager.update(urlUpdate.getUrl(),
                            urlUpdate.getMetodo(),
                            reg.getCampos(),
                            reg.getLlave());

            if (conteo > 0)
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_MODIFICADO"));
                cargarListaFrmmodificacionessub();
                cargarTotales();
            }
        }
        catch (SystemException ex)
        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
        finally
        {
            cargarListaFrmmodificacionessub();
            cargarTotales();
            actualizarTotatlesSub();
        }
    }

    /**
     * Metodo de eliminacion del formulario Frmmodificacionessub
     *
     *
     *
     * @param reg
     * registro seleccionado en el subformulario
     */
    public void eliminarRegSubFrmmodificacionessub(Registro reg)
    {
        try
        {
            UrlBean urlDelete = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            GenericUrlEnum.SP_MODIFICACIONESDEUDA
                                                            .getDeleteKey());
            int conteo = requestManager.delete(urlDelete.getUrl(),
                            reg.getLlave());

            if (conteo > 0)
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_REGISTRO_ELIMINADO"));
            }
            cargarListaFrmmodificacionessub();
            cargarTotales();
        }
        catch (SystemException ex)
        {
            logger.error(ex.getMessage(), ex);
            JsfUtil.agregarMensajeError(ex.getMessage());
        }
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro seleccionado para el subformulario Frmmodificacionessub
     *
     *
     */
    public void cancelarEdicionFrmmodificacionessub()
    {
        cargarListaFrmmodificacionessub();
        cargarTotales();
    }

    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>
    // </METODOS_ADICIONALES>
    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el momento despues de cargar el registro
     *
     *
     */
    @Override
    public void cargarRegistro()
    {
        // <CODIGO_DESARROLLADO>
        try
        {
            if (css == null)
            {
                registro.getCampos().put(fechaCons,
                                new Date());

                registro.getCampos().put("HORA",
                                new Date());

                registro.getCampos().put("OPERADOR",
                                SessionUtil.getUser().getCodigo());

                periodo = SysmanFunciones
                                .nvl(ejbServiciosPublicosCero
                                                .asignarNombrePeriodo(compania,
                                                                Integer.parseInt(
                                                                                ano),
                                                                numeroperiodo,
                                                                null),
                                                "")
                                .toString();
                registro.getCampos().put(periodoCons, periodo);
                bloquearComponentes = false;
                totalValorAnterior = "0";
                totalValorNuevo = "0";

            }
            else
            {

                bloquearComponentes = true;
                valorAnterior = registro.getCampos().get("TOTFACTURAPERACTUAL")
                                .toString();

                cargarListaconcepto();

                String nombrePeriodo = SysmanFunciones
                                .nvl(ejbServiciosPublicosCero
                                                .asignarNombrePeriodo(compania,
                                                                Integer.parseInt(
                                                                                SysmanFunciones.nvl(
                                                                                                registro.getCampos()
                                                                                                                .get(GeneralParameterEnum.ANO
                                                                                                                                .getName()),
                                                                                                "0")
                                                                                                .toString()),
                                                                SysmanFunciones
                                                                                .nvl(registro.getCampos()
                                                                                                .get(periodoCons),
                                                                                                "0")
                                                                                .toString(),
                                                                null),
                                                "")
                                .toString();

                registro.getCampos().put(periodoCons, nombrePeriodo);

            }

        }
        catch (NumberFormatException | SystemException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado antes de realizar la insercion del registro y pone el eregistro la compania,el ano, nuemroperiodo ciclo, tipo y nombre
     *
     *
     *
     */
    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registro.getCampos().put(GeneralParameterEnum.ANO.getName(), ano);
        registro.getCampos().put(periodoCons, numeroperiodo);
        registro.getCampos().put(GeneralParameterEnum.CICLO.getName(), ciclo);
        registro.getCampos().put(
                        FrmmodificacionesControladorEnum.TIPO.getValue(), 2);
        registro.getCampos().remove(nombreCons);

        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion del registro
     *
     *
     *
     */
    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion del registro
     *
     */
    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        if (accion.equals(ACCION_MODIFICAR))
        {
            registro.getCampos().remove(GeneralParameterEnum.COMPANIA
                            .getName());
            registro.getCampos().remove("NOMBRE");
            registro.getCampos().remove("TOTFACTURAPERACTUAL");
        }
        registro.getCampos().put(GeneralParameterEnum.PERIODO.getName(),
                        numeroperiodo);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y actualizacion del registro
     *
     *
     *
     *
     */
    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la eliminacion del registro
     *
     *
     *
     *
     */
    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la eliminacion del registroF
     */
    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>

    /**
     * Retorna la variable periodo
     *
     * @return periodo
     */
    public String getPeriodo()
    {
        return periodo;
    }

    /**
     * Asigna la variable periodo
     *
     * @param periodo
     * Variable a asignar en periodo
     */
    public void setPeriodo(String periodo)
    {
        this.periodo = periodo;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaCodigo
     *
     * @return listaCodigo
     */
    public List<Registro> getListaCodigo()
    {
        return listaCodigo;
    }

    /**
     * Asigna la lista listaCodigo
     *
     * @param listaCodigo
     * Variable a asignar en listaCodigo
     */
    public void setListaCodigo(List<Registro> listaCodigo)
    {
        this.listaCodigo = listaCodigo;
    }

    public RegistroDataModelImpl getListaconcepto()
    {
        return listaconcepto;
    }

    public void setListaconcepto(RegistroDataModelImpl listaconcepto)
    {
        this.listaconcepto = listaconcepto;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCodigoRuta
     *
     * @return listaCodigoRuta
     */
    public RegistroDataModelImpl getListaCodigoRuta()
    {
        return listaCodigoRuta;
    }

    /**
     * Asigna la lista listaCodigoRuta
     *
     * @param listaCodigoRuta
     * Variable a asignar en listaCodigoRuta
     */
    public void setListaCodigoRuta(RegistroDataModelImpl listaCodigoRuta)
    {
        this.listaCodigoRuta = listaCodigoRuta;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
    // <SET_GET_LISTAS_SUBFORM>
    /**
     * Retorna la lista listaFrmmodificacionessub
     *
     * @return listaFrmmodificacionessub
     */

    public String getAuxiliar()
    {
        return auxiliar;
    }

    public void setAuxiliar(String auxiliar)
    {
        this.auxiliar = auxiliar;
    }

    // </SET_GET_LISTAS_SUBFORM>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_ADICIONALES>
    /**
     * Retorna el objeto registroSub
     *
     * @return registroSub
     */
    public Registro getRegistroSub()
    {
        return registroSub;
    }

    public List<Registro> getListaFrmmodificacionessub()
    {
        return listaFrmmodificacionessub;
    }

    public void setListaFrmmodificacionessub(
        List<Registro> listaFrmmodificacionessub)
    {
        this.listaFrmmodificacionessub = listaFrmmodificacionessub;
    }

    public String getTotalValorNuevo()
    {
        return totalValorNuevo;
    }

    public void setTotalValorNuevo(String totalValorNuevo)
    {
        this.totalValorNuevo = totalValorNuevo;
    }

    public String getTotalValorAnterior()
    {
        return totalValorAnterior;
    }

    public void setTotalValorAnterior(String totalValorAnterior)
    {
        this.totalValorAnterior = totalValorAnterior;
    }

    public boolean isBloquearComponentes()
    {
        return bloquearComponentes;
    }

    public void setBloquearComponentes(boolean bloquearComponentes)
    {
        this.bloquearComponentes = bloquearComponentes;
    }

    public RegistroDataModelImpl getListaconceptoE()
    {
        return listaconceptoE;
    }

    public void setListaconceptoE(RegistroDataModelImpl listaconceptoE)
    {
        this.listaconceptoE = listaconceptoE;
    }

    /**
     * Asigna el objeto registroSub
     *
     * @param registroSub
     * Variable a asignar en registroSub
     */
    public void setRegistroSub(Registro registroSub)
    {
        this.registroSub = registroSub;
    }

    private void cargarRegistroSub()
    {
        registroSub.getCampos().put(codigorutaCons,
                        registro.getCampos().get(codigorutaCons));

        registroSub.getCampos().put("TIPOMODIFICACION",
                        2);
        registroSub.getCampos().put("CAUSAMODIFICACION",
                        1);
        registroSub.getCampos().put(GeneralParameterEnum.ANO.getName(),
                        ano);

        registroSub.getCampos().put(periodoCons,
                        numeroperiodo);

        registroSub.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        registroSub.getCampos().put(GeneralParameterEnum.CICLO.getName(),
                        ciclo);

        registroSub.getCampos().put(fechaCons,
                        registro.getCampos().get(fechaCons));

        registroSub.getCampos().put("HORA",
                        registro.getCampos().get("HORA"));

        registroSub.getCampos().remove(nombreConceptoCons);
    }

    private void cargarTotales()
    {

        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CODIGORUTA.getName(),
                        registro.getCampos().get(codigorutaCons));
        param.put(GeneralParameterEnum.FECHA.getName(),
                        registro.getCampos().get(fechaCons));
        param.put(FrmmodificacionesControladorEnum.HORA.getValue(),
                        registro.getCampos().get("HORA"));
        try
        {
            Registro registroTotales = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmmodificacionesControladorUrlEnum.URL9715
                                                                            .getValue())
                                            .getUrl(), param));
            if (registroTotales != null)
            {
                totalValorAnterior = registroTotales.getCampos().get("VALORANT")
                                .toString();
                totalValorNuevo = registroTotales.getCampos().get("VALORNUEVO")
                                .toString();

            }

        }
        catch (SystemException e)
        {
            Logger.getLogger(FrmmodificacionesControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void actualizarTotatlesSub()
    {

        try
        {
            Map<String, Object> param = new HashMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
            param.put(GeneralParameterEnum.CODIGORUTA.getName(),
                            registro.getCampos().get(codigorutaCons));

            Registro reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmmodificacionesControladorUrlEnum.URL9717
                                                                            .getValue())
                                            .getUrl(), param));

            double totSegFecha;
            if ((reg.getCampos().get("SVF") != null)
                && (valorAnterior != reg.getCampos().get("SVF")))
            {
                String valorNuevo = reg.getCampos().get("SVF").toString();

                if ("SI".equals(SysmanFunciones
                                .nvl(ejbSysmanUtil.consultarParametro(compania,
                                                "RECARGO SEGUNDA FECHA EN PERIODO SIGUIENTE",
                                                SessionUtil.getModulo(),
                                                new Date(), true),
                                                "NO")))
                {
                    totSegFecha = Double.parseDouble(valorNuevo);

                }
                else
                {
                    totSegFecha = Double.parseDouble(valorNuevo) * porRecargo();
                }

                Map<String, Object> parametros = new HashMap<>();
                parametros.put(GeneralParameterEnum.COMPANIA.getName(),
                                compania);
                parametros.put(GeneralParameterEnum.CICLO.getName(), ciclo);
                parametros.put(GeneralParameterEnum.CODIGORUTA.getName(),
                                registro.getCampos().get(codigorutaCons));

                parametros.put("TOTFACTURAPERACTUAL", valorNuevo);
                parametros.put("TOTFACTURAPAGO2", totSegFecha);
                parametros.put("DATE_MODIFIED", new Date());
                parametros.put("MODIFIED_BY",
                                SessionUtil.getUser().getCodigo());

                Parameter parameter = new Parameter();
                parameter.setFields(parametros);

                UrlBean urlUpdate = UrlServiceUtil.getInstance()
                                .getUrlServiceByUrlByEnumID(
                                                FrmmodificacionesControladorUrlEnum.URL9718
                                                                .getValue());
                requestManager.update(urlUpdate.getUrl(), urlUpdate.getMetodo(),
                                parameter);

            }
        }
        catch (SystemException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public double porRecargo()
    {
        double recargo = Double.parseDouble(tasaRecargo);
        if (recargo > 0)
        {
            return 1 + (recargo / 100);
        }
        else
        {
            return 1;

        }

    }

    private int validacionesCodigoRuta()
    {
        try
        {
            if ("SI".equals(SysmanFunciones
                            .nvl(ejbSysmanUtil.consultarParametro(compania,
                                            "FACTURACION EN SITIO",
                                            SessionUtil.getModulo(), new Date(),
                                            true),
                                            "NO"))
                && "P".equals(fimm))
            {

                return 1;
            }

            Map<String, Object> param = new HashMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
            param.put(GeneralParameterEnum.CODIGORUTA.getName(),
                            registro.getCampos().get(codigorutaCons));

            Registro reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmmodificacionesControladorUrlEnum.URL9716
                                                                            .getValue())
                                            .getUrl(), param));

            if (reg != null)
            {
                return 2;
            }
        }
        catch (SystemException e)
        {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return 0;

    }

    public void genInforme(ReportesBean.FORMATOS formato)
    {
        archivoDescarga = null;
        List<String> infromeParametros = generarParametrosReporte();
        String reporte = "001339INFMODIFICACIONES";
        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("codigo", registro.getCampos().get(codigoCons));
            reemplazar.put("fecha",
                            SysmanFunciones.formatearFecha((Date) registro
                                            .getCampos().get(fechaCons)));
            reemplazar.put("hora",
                            SysmanFunciones.formatearFecha((Date) registro
                                            .getCampos().get("HORA")));

            reemplazar.put("companiaNit",
                            SessionUtil.getCompaniaIngreso().getNit());
            reemplazar.put("companiaSigla",
                            SessionUtil.getCompaniaIngreso().getSigla());

            reemplazar.put("companiaNombre",
                            SessionUtil.getCompaniaIngreso().getNombre());

            reemplazar.put("tipo", 2);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_NOMBRE DEL JEFE DE COMERCIAL",
                            infromeParametros.get(0));
            parametros.put("PR_CARGO DIRECTOR COMERCIAL",
                            infromeParametros.get(1));
            parametros.put("PR_NOMBRE FUNCIONARIO PQR",
                            infromeParametros.get(2));
            parametros.put("CARGO FUNCIONARIO PQR", infromeParametros.get(3));
            parametros.put("PR_CARGO", infromeParametros.get(4));
            parametros.put("PR_NOMBRE", infromeParametros.get(4));
            parametros.put("PR_TITULO", infromeParametros.get(5));

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (FileNotFoundException e)
        {
            JsfUtil.agregarMensajeError(
                            idioma.getString("MSM_INFORME_VAR_NO_EXISTE")
                                            .replace("s$reporte$s", reporte)
                                + e.getMessage());
            logger.error(e.getMessage(), e);
        }

        catch (JRException | IOException | SysmanException e)
        {
            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);

        }

    }

    public List<String> generarParametrosReporte()
    {

        ArrayList<String> parametros = new ArrayList<>();
        try
        {
            parametros.add(ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE DEL JEFE DE COMERCIAL",
                            SessionUtil.getModulo(), new Date(),
                            true));
            parametros.add(ejbSysmanUtil.consultarParametro(compania,
                            "CARGO DIRECTOR COMERCIAL", SessionUtil.getModulo(),
                            new Date(),
                            true));
            parametros.add(ejbSysmanUtil.consultarParametro(compania,
                            "NOMBRE FUNCIONARIO PQR", SessionUtil.getModulo(),
                            new Date(), true));
            parametros.add(ejbSysmanUtil.consultarParametro(compania,
                            "CARGO FUNCIONARIO PQR", SessionUtil.getModulo(),
                            new Date(), true));
            parametros.add(SessionUtil.getUser().getCodigo());
            parametros.add(SessionUtil.getCompaniaIngreso().getNit());

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return parametros;
    }

    // </SET_GET_ADICIONALES>
}

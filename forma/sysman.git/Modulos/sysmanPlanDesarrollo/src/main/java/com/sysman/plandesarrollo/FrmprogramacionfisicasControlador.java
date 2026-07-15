/*-
 * FrmprogramacionfisicasControlador.java
 *
 * 1.0
 * 
 * 30/08/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.plandesarrollo;

import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.plandesarrollo.ejb.EjbPlanDesarrolloDosRemote;
import com.sysman.plandesarrollo.enums.FrmprogramacionfisicasControladorEnum;
import com.sysman.plandesarrollo.enums.FrmprogramacionfisicasControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Clase migrada para administrar la programacion fisica del plan de
 * accion
 *
 * @version 1.0, 30/08/2018
 * @author ybecerra
 */
@ManagedBean
@ViewScoped
public class FrmprogramacionfisicasControlador extends BeanBaseContinuoAcmeImpl
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante a nivel de clase que almacena el codigo del modulo
     * por el cual ingreso en la aplicacion
     */
    private final String modulo;
    /**
     * Atributo auxliar el cual es asiganado en el momento que se
     * activa la edicion de un registro. Toma el valor del indice
     * dentro de la grilla del registro seleccionado para editar
     */
    private int indice;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el nombre del id seleccionado en el arbol
     */
    private String nombrePlan;
    /**
     * Atributo que almacena el tipo de transaccion por la cual se
     * ingreso al formulario de plan de accion
     */
    private String tipo;
    /**
     * Atributo que almacena el numero de la transaccion
     */
    private String numero;
    /**
     * Atributo que almacena el id del plan seleccionado en el sub
     * formulario de plan de accion
     */
    private String idPlan;
    /**
     * Atributo que almacena el ano de la vigencia seleccionada.
     */
    private String vigencia;
    /**
     * Atributo utilizado para retorna al formulario de plan de accion
     */
    private boolean esAdministrador;

    /**
     * Atributo utilizado para retorna al formulario de plan de accion
     */
    private boolean esJefeUnidad;

    /**
     * Atributo utilizado para retorna al formulario de plan de accion
     */
    private String dependencia;

    /**
     * Atributo utilizado para retorna al formulario de plan de accion
     */
    private String predecesor;

    /**
     * Atributo utilizado para retorna al formulario de plan de accion
     */
    private int digitosMetaProducto;

    /**
     * Atributo utilizado para retorna al formulario de plan de accion
     */
    private boolean esUsuarioConsulta;

    /**
     * Atributo utilizado para retorna al formulario de plan de accion
     */
    private String vigenciaFinal;
    /**
     * Atributo que almacena los digitos de GetACCION()
     */
    private int digitosAccion;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;

    /**
     * Atributo que valida si el boton generar se activa o no
     */
    private boolean activarGenerar;
    /**
     * Atributo que almacena el la vigencia antes de cambiar del
     * registro selecciondo
     */
    private String vigenciaAnterior;
    /**
     * Atributo que valida si los campos de prograamdo permite editar
     * o no
     */
    private boolean bloquearProgramado;
    /**
     * Atributo que valida si los campos de ejecutado permite ser
     * editados o no
     */
    private boolean bloquearEjecutar;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    @EJB
    private EjbPlanDesarrolloDosRemote ejbPlanDesarrolloDos;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista de registros de los años
     */
    private List<Registro> listaVigencia;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmprogramacionfisicasControlador
     */
    public FrmprogramacionfisicasControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            // 1907
            numFormulario = GeneralCodigoFormaEnum.FRM_PROGRAMACION_FISICA_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null)
            {
                esAdministrador = (boolean) parametrosEntrada
                                .get("administrador");
                esJefeUnidad = (boolean) parametrosEntrada
                                .get("jefeUnidad");
                dependencia = SysmanFunciones.nvl(
                                parametrosEntrada.get("dependencia"),
                                "").toString();
                predecesor = SysmanFunciones.nvl(
                                parametrosEntrada.get("predecesor"),
                                "").toString();
                digitosMetaProducto = (int) SysmanFunciones
                                .nvl(parametrosEntrada
                                                .get("digitosMetaProducto"), 0);

                esUsuarioConsulta = (boolean) parametrosEntrada
                                .get("esUsuarioConsulta");

                vigenciaFinal = SysmanFunciones.nvl(parametrosEntrada
                                .get("vigenciaFinal"), "").toString();

                tipo = parametrosEntrada.get("tipo").toString();
                numero = parametrosEntrada.get(GeneralParameterEnum.NUMERO.getName().toLowerCase()).toString();
                vigencia = parametrosEntrada
                                .get("vigenciaGubernamental").toString();
                idPlan = parametrosEntrada.get("idPlan").toString();
                nombrePlan = parametrosEntrada.get("nombrePlan").toString();
                digitosAccion = Integer
                                .parseInt(parametrosEntrada.get("digitosAccion")
                                                .toString());
            }

            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
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

        enumBase = GenericUrlEnum.PI_PROGRAMACION_FISICA;
        buscarLlave();
        reasignarOrigen();
        registro = new Registro();
        // <CARGAR_LISTA>
        cargarListaVigencia();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();

    }

    /**
     * En este metodo se asigna al atributo origenDatos del bean base
     * el valor de la consulta del formulario. Tambien carga la lista
     * del formulario por primera vez
     */
    @Override
    public void reasignarOrigen() {
        buscarUrls();
        parametrosListado.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        parametrosListado.put(GeneralParameterEnum.TIPO.getName(), tipo);
        parametrosListado.put(GeneralParameterEnum.NUMERO.getName(), numero);
        parametrosListado.put(FrmprogramacionfisicasControladorEnum.ID_PLAN.getValue(), idPlan);
        parametrosListado.put(FrmprogramacionfisicasControladorEnum.VIGENCIA_INICIAL.getValue(), vigencia);

    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaVigencia
     *
     */
    public void cargarListaVigencia() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try
        {
            listaVigencia = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmprogramacionfisicasControladorUrlEnum.URL174
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton btGenerar en la vista
     *
     *
     */
    public void oprimirbtGenerar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("tipo", tipo);
        reemplazar.put("numero", numero);
        reemplazar.put("idPlan", idPlan);
        reemplazar.put("vigencia", vigencia);
        String strSql = Reporteador.resuelveConsulta(
                        "800191ProgramacionFisica",
                        Integer.parseInt(modulo), reemplazar);

        try
        {
            archivoDescarga = JsfUtil.exportarHojaDatosStreamed(strSql,
                            ConectorPool.ESQUEMA_SYSMAN,
                            ReportesBean.FORMATOS.EXCEL);
        }
        catch (JRException | IOException | SQLException | DRException | SysmanException e)
        {

            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Vigencia
     * 
     * 
     */
    public void cambiarVigencia() {
        // <CODIGO_DESARROLLADO>
        try
        {
            if (ejbPlanDesarrolloDos.verificarPlanAdquisiciones(compania,
                            Integer.parseInt(registro.getCampos().get(GeneralParameterEnum.VIGENCIA.getName()).toString()), "4"))
            {
                registro.getCampos().put(GeneralParameterEnum.VIGENCIA.getName(), null);
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4198"));
                bloquearProgramado = true;

            }
            else
            {
                bloquearProgramado = false;
            }

            if (ejbPlanDesarrolloDos.verificarPlanAdquisiciones(compania,
                            Integer.parseInt(registro.getCampos().get(GeneralParameterEnum.VIGENCIA.getName()).toString()), "5"))
            {

                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4199"));
                bloquearEjecutar = true;

            }
            else
            {
                bloquearEjecutar = false;
            }

        }
        catch (NumberFormatException | SystemException e)
        {

            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control FechaInicial
     * 
     */
    public void cambiarFechaInicial() {
        // <CODIGO_DESARROLLADO>
        validarFechas("1", 0);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control FechaFinal
     * 
     * 
     */
    public void cambiarFechaFinal() {
        // <CODIGO_DESARROLLADO>
        validarFechas("1", 0);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ProgramadoUno
     * 
     * 
     */
    public void cambiarProgramadoUno() {
        // <CODIGO_DESARROLLADO>

        if (SysmanFunciones.validarVariableVacio(SysmanFunciones
                        .nvl(registro.getCampos().get(FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR1_FIN.getValue()), "").toString()))
        {
            registro.getCampos().put(FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR1_FIN.getValue(), 0);
        }
        actualizarSumas("1", 0);
        actualizarEjecuciones("1", 0,
                        FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR1_FIN.getValue(),
                        FrmprogramacionfisicasControladorEnum.EJECUTADO_TR1_FIN.getValue(),
                        FrmprogramacionfisicasControladorEnum.EJECUCIONUNO.getValue());

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control EjecutadoDos
     * 
     * 
     */
    public void cambiarEjecutadoDos() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(SysmanFunciones
                        .nvl(registro.getCampos().get(FrmprogramacionfisicasControladorEnum.EJECUTADO_TR2_FIN.getValue()), "").toString()))
        {
            registro.getCampos().put(FrmprogramacionfisicasControladorEnum.EJECUTADO_TR2_FIN.getValue(), 0);
        }
        actualizarSumas("1", 0);
        actualizarEjecuciones("1", 0,
                        FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR2_FIN.getValue(),
                        FrmprogramacionfisicasControladorEnum.EJECUTADO_TR2_FIN.getValue(),
                        FrmprogramacionfisicasControladorEnum.EJECUCIONDOS.getValue());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ProgramadoDos
     * 
     * 
     */
    public void cambiarProgramadoDos() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(SysmanFunciones
                        .nvl(registro.getCampos().get(FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR2_FIN.getValue()), "").toString()))
        {
            registro.getCampos().put(FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR2_FIN.getValue(), 0);
        }
        actualizarSumas("1", 0);
        actualizarEjecuciones("1", 0,
                        FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR2_FIN.getValue(),
                        FrmprogramacionfisicasControladorEnum.EJECUTADO_TR2_FIN.getValue(),
                        FrmprogramacionfisicasControladorEnum.EJECUCIONDOS.getValue());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control EjecutadoUno
     * 
     * 
     */
    public void cambiarEjecutadoUno() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(SysmanFunciones
                        .nvl(registro.getCampos().get(FrmprogramacionfisicasControladorEnum.EJECUTADO_TR1_FIN.getValue()), "").toString()))
        {
            registro.getCampos().put(FrmprogramacionfisicasControladorEnum.EJECUTADO_TR1_FIN.getValue(), 0);
        }
        actualizarSumas("1", 0);
        actualizarEjecuciones("1", 0,
                        FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR1_FIN.getValue(),
                        FrmprogramacionfisicasControladorEnum.EJECUTADO_TR1_FIN.getValue(),
                        FrmprogramacionfisicasControladorEnum.EJECUCIONUNO.getValue());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ProgramadoTres
     * 
     * 
     */
    public void cambiarProgramadoTres() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(SysmanFunciones
                        .nvl(registro.getCampos().get(FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR3_FIN.getValue()), "").toString()))
        {
            registro.getCampos().put(FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR3_FIN.getValue(), 0);
        }
        actualizarSumas("1", 0);
        actualizarEjecuciones("1", 0,
                        FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR3_FIN.getValue(),
                        FrmprogramacionfisicasControladorEnum.EJECUTADO_TR3_FIN.getValue(),
                        FrmprogramacionfisicasControladorEnum.EJECUCIONTRES.getValue());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control EjecutadoTres
     * 
     * 
     */
    public void cambiarEjecutadoTres() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(SysmanFunciones
                        .nvl(registro.getCampos().get(FrmprogramacionfisicasControladorEnum.EJECUTADO_TR3_FIN.getValue()), "").toString()))
        {
            registro.getCampos().put(FrmprogramacionfisicasControladorEnum.EJECUTADO_TR3_FIN.getValue(), 0);
        }
        actualizarSumas("1", 0);
        actualizarEjecuciones("1", 0,
                        FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR3_FIN.getValue(),
                        FrmprogramacionfisicasControladorEnum.EJECUTADO_TR3_FIN.getValue(),
                        FrmprogramacionfisicasControladorEnum.EJECUCIONTRES.getValue());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ProgramadoCuatro
     * 
     * 
     */
    public void cambiarProgramadoCuatro() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(SysmanFunciones
                        .nvl(registro.getCampos().get(FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR4_FIN.getValue()), "").toString()))
        {
            registro.getCampos().put(FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR4_FIN.getValue(), 0);
        }
        actualizarSumas("1", 0);
        actualizarEjecuciones("1", 0,
                        FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR4_FIN.getValue(),
                        FrmprogramacionfisicasControladorEnum.EJECUTADO_TR4_FIN.getValue(),
                        FrmprogramacionfisicasControladorEnum.EJECUCIONCUATRO.getValue());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control EjecutadoCuatro
     * 
     * 
     */
    public void cambiarEjecutadoCuatro() {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(SysmanFunciones
                        .nvl(registro.getCampos().get(FrmprogramacionfisicasControladorEnum.EJECUTADO_TR4_FIN.getValue()), "").toString()))
        {
            registro.getCampos().put(FrmprogramacionfisicasControladorEnum.EJECUTADO_TR4_FIN.getValue(), 0);
        }
        actualizarSumas("1", 0);
        actualizarEjecuciones("1", 0,
                        FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR4_FIN.getValue(),
                        FrmprogramacionfisicasControladorEnum.EJECUTADO_TR4_FIN.getValue(),
                        FrmprogramacionfisicasControladorEnum.EJECUCIONCUATRO.getValue());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Vigencia en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarVigenciaC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        try
        {
            if (ejbPlanDesarrolloDos.verificarPlanAdquisiciones(compania,
                            Integer.parseInt(listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                            .get(GeneralParameterEnum.VIGENCIA.getName()).toString()),
                            "4"))
            {
                listaInicial.getDatasource().get(rowNum % 10).getCampos().put(GeneralParameterEnum.VIGENCIA.getName(), vigenciaAnterior);
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4198"));
                bloquearProgramado = true;

            }
            else
            {
                bloquearProgramado = false;
            }

            if (ejbPlanDesarrolloDos.verificarPlanAdquisiciones(compania,
                            Integer.parseInt(listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                            .get(GeneralParameterEnum.VIGENCIA.getName()).toString()),
                            "5"))
            {

                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4199"));
                bloquearEjecutar = true;

            }
            else
            {
                bloquearEjecutar = false;
            }

        }
        catch (NumberFormatException | SystemException e)
        {

            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control FechaInicial en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarFechaInicialC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        validarFechas("2", rowNum);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control FechaFinal en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarFechaFinalC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        validarFechas("2", rowNum);

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ProgramadoUno en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarProgramadoUnoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(SysmanFunciones
                        .nvl(listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                        .get(FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR1_FIN.getValue()), "")
                        .toString()))
        {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR1_FIN.getValue(), 0);
        }
        actualizarSumas("2", rowNum);
        actualizarEjecuciones("2", rowNum,
                        FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR1_FIN.getValue(),
                        FrmprogramacionfisicasControladorEnum.EJECUTADO_TR1_FIN.getValue(),
                        FrmprogramacionfisicasControladorEnum.EJECUCIONUNO.getValue());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control EjecutadoDos en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarEjecutadoDosC(int rowNum) {

        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(SysmanFunciones
                        .nvl(listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                        .get(FrmprogramacionfisicasControladorEnum.EJECUTADO_TR2_FIN.getValue()), "")
                        .toString()))
        {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(FrmprogramacionfisicasControladorEnum.EJECUTADO_TR2_FIN.getValue(), 0);
        }
        actualizarSumas("2", rowNum);
        actualizarEjecuciones("2", rowNum,
                        FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR2_FIN.getValue(),
                        FrmprogramacionfisicasControladorEnum.EJECUTADO_TR2_FIN.getValue(),
                        FrmprogramacionfisicasControladorEnum.EJECUCIONDOS.getValue());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ProgramadoDos en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarProgramadoDosC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(SysmanFunciones
                        .nvl(listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                        .get(FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR2_FIN.getValue()), "")
                        .toString()))
        {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR2_FIN.getValue(), 0);
        }
        actualizarSumas("2", rowNum);
        actualizarEjecuciones("2", rowNum,
                        FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR2_FIN.getValue(),
                        FrmprogramacionfisicasControladorEnum.EJECUTADO_TR2_FIN.getValue(),
                        FrmprogramacionfisicasControladorEnum.EJECUCIONDOS.getValue());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control EjecutadoUno en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarEjecutadoUnoC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(SysmanFunciones
                        .nvl(listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                        .get(FrmprogramacionfisicasControladorEnum.EJECUTADO_TR1_FIN.getValue()), "")
                        .toString()))
        {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(FrmprogramacionfisicasControladorEnum.EJECUTADO_TR1_FIN.getValue(), 0);
        }
        actualizarSumas("2", rowNum);
        actualizarEjecuciones("2", rowNum,
                        FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR1_FIN.getValue(),
                        FrmprogramacionfisicasControladorEnum.EJECUTADO_TR1_FIN.getValue(),
                        FrmprogramacionfisicasControladorEnum.EJECUCIONUNO.getValue());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ProgramadoTres en la
     * fila seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarProgramadoTresC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(SysmanFunciones
                        .nvl(listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                        .get(FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR3_FIN.getValue()), "")
                        .toString()))
        {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR3_FIN.getValue(), 0);
        }
        actualizarSumas("2", rowNum);
        actualizarEjecuciones("2", rowNum,
                        FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR3_FIN.getValue(),
                        FrmprogramacionfisicasControladorEnum.EJECUTADO_TR3_FIN.getValue(),
                        FrmprogramacionfisicasControladorEnum.EJECUCIONTRES.getValue());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control EjecutadoTres en la fila
     * seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarEjecutadoTresC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(SysmanFunciones
                        .nvl(listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                        .get(FrmprogramacionfisicasControladorEnum.EJECUTADO_TR3_FIN.getValue()), "")
                        .toString()))
        {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(FrmprogramacionfisicasControladorEnum.EJECUTADO_TR3_FIN.getValue(), 0);
        }
        actualizarSumas("2", rowNum);
        actualizarEjecuciones("2", rowNum,
                        FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR3_FIN.getValue(),
                        FrmprogramacionfisicasControladorEnum.EJECUTADO_TR3_FIN.getValue(),
                        FrmprogramacionfisicasControladorEnum.EJECUCIONTRES.getValue());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control ProgramadoCuatro en la
     * fila seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarProgramadoCuatroC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(SysmanFunciones
                        .nvl(listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                        .get(FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR4_FIN.getValue()), "")
                        .toString()))
        {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR4_FIN.getValue(), 0);
        }
        actualizarSumas("2", rowNum);
        actualizarEjecuciones("2", rowNum,
                        FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR4_FIN.getValue(),
                        FrmprogramacionfisicasControladorEnum.EJECUTADO_TR4_FIN.getValue(),
                        FrmprogramacionfisicasControladorEnum.EJECUCIONCUATRO.getValue());
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control EjecutadoCuatro en la
     * fila seleccionada dentro de la grilla
     * 
     * 
     * @param rowNum
     * indice de la fila seleccionada
     */
    public void cambiarEjecutadoCuatroC(int rowNum) {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.validarVariableVacio(SysmanFunciones
                        .nvl(listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                        .get(FrmprogramacionfisicasControladorEnum.EJECUTADO_TR4_FIN.getValue()), "")
                        .toString()))
        {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(FrmprogramacionfisicasControladorEnum.EJECUTADO_TR4_FIN.getValue(), 0);
        }
        actualizarSumas("2", rowNum);
        actualizarEjecuciones("2", rowNum,
                        FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR4_FIN.getValue(),
                        FrmprogramacionfisicasControladorEnum.EJECUTADO_TR4_FIN.getValue(),
                        FrmprogramacionfisicasControladorEnum.EJECUCIONCUATRO.getValue());
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        cargarValoresDefecto();
        contarRegistros();

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se cancela la edicion del registro
     * seleccionado
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
        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(), compania);
        registro.getCampos().put(FrmprogramacionfisicasControladorEnum.VIGENCIA_INICIAL.getValue(), vigencia);
        registro.getCampos().put(FrmprogramacionfisicasControladorEnum.ID_PLAN.getValue(), idPlan);
        registro.getCampos().put(GeneralParameterEnum.TIPO.getName(), tipo);
        registro.getCampos().put(GeneralParameterEnum.NUMERO.getName(), numero);
        registro.getCampos().put(FrmprogramacionfisicasControladorEnum.ES_NUEVO.getValue(), true);
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
        contarRegistros();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado antes de realizar la insercion y actualizacion
     * del registro
     * 
     * @return true
     */
    @Override
    public boolean actualizarAntes() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(FrmprogramacionfisicasControladorEnum.CANTIDADPROGRAMADA.getValue());
        registro.getCampos().remove(FrmprogramacionfisicasControladorEnum.CANTIDADEJECUTADA.getValue());
        registro.getCampos().remove(FrmprogramacionfisicasControladorEnum.EJECUCION.getValue());
        registro.getCampos().remove(FrmprogramacionfisicasControladorEnum.EJECUCIONUNO.getValue());
        registro.getCampos().remove(FrmprogramacionfisicasControladorEnum.EJECUCIONDOS.getValue());
        registro.getCampos().remove(FrmprogramacionfisicasControladorEnum.EJECUCIONTRES.getValue());
        registro.getCampos().remove(FrmprogramacionfisicasControladorEnum.EJECUCIONCUATRO.getValue());
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Metodo ejecutado despues de realizar la insercion y
     * actualizacion del registro
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
     * Metodo ejecutado despues de realizar la eliminacion del
     * registro
     * 
     * 
     * @return true
     */
    @Override
    public boolean eliminarDespues() {
        // <CODIGO_DESARROLLADO>
        listaInicial.load();
        // </CODIGO_DESARROLLADO>
        return true;
    }

    /**
     * Este metodo se ejecuta antes enviar la accion de actualizacion,
     * en el se pueden remover valores auxiliares que no se desee o se
     * deban enviar en el registro
     */
    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(GeneralParameterEnum.COMPANIA.getName());
        registro.getCampos().remove(GeneralParameterEnum.TIPO.getName());
        registro.getCampos().remove(GeneralParameterEnum.NUMERO.getName());
        registro.getCampos().remove(GeneralParameterEnum.ID_PLAN.getName());
        registro.getCampos().remove(GeneralParameterEnum.VIGENCIA_INICIAL.getName());

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado cuando se activa la edicion de un registro del
     * formulario
     * 
     *
     * @param registro
     * registro del cual se activo la edicion
     */
    public void activarEdicion(Registro registro) {
        try
        {
            indice = listaInicial.getRowIndex();
            vigenciaAnterior = String.valueOf(registro.getCampos().get(GeneralParameterEnum.VIGENCIA.getName()));

            if (ejbPlanDesarrolloDos.verificarPlanAdquisiciones(compania,
                            Integer.parseInt(registro.getCampos()
                                            .get(GeneralParameterEnum.VIGENCIA.getName()).toString()),
                            "4"))
            {
                registro.getCampos().put(GeneralParameterEnum.VIGENCIA.getName(), vigenciaAnterior);
                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4198"));
                bloquearProgramado = true;

            }
            else
            {
                bloquearProgramado = false;
            }

            if (ejbPlanDesarrolloDos.verificarPlanAdquisiciones(compania,
                            Integer.parseInt(registro.getCampos()
                                            .get(GeneralParameterEnum.VIGENCIA.getName()).toString()),
                            "5"))
            {

                JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB4199"));
                bloquearEjecutar = true;

            }
            else
            {
                bloquearEjecutar = false;
            }
        }
        catch (NumberFormatException | SystemException e)
        {

            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }

    }

    /**
     * Metodo ejecutado cuando se cierra el formulario
     * 
     */
    public void cerrarFormulario() {
        Map<String, Object> param = new TreeMap<>();

        param.put("administrador", esAdministrador);

        param.put("jefeUnidad", esJefeUnidad);

        param.put("dependencia", dependencia);

        param.put("predecesor", predecesor);

        param.put("digitosMetaProducto", digitosMetaProducto);

        param.put("esUsuarioConsulta", esUsuarioConsulta);

        param.put("vigenciaFinal", vigenciaFinal);

        param.put("tipo", tipo);

        param.put("numero", numero);

        param.put("vigencia", vigencia);

        param.put("idPlan", idPlan);

        param.put("digitosAccion", digitosAccion);
        param.put("nombrePlan", nombrePlan);

        Direccionador direccionador = new Direccionador();

        direccionador.setParametros(param);

        direccionador.setNumForm(Integer.toString(
                        GeneralCodigoFormaEnum.PLAN_DE_ACCION_CONTROLADOR
                                        .getCodigo()));

        SessionUtil.redireccionarDeModalAModal(direccionador,
                        SessionUtil.getModulo());
    }

    /**
     * Metodo ejecutado desde un comando remoto cuando se cierra el
     * formulario
     * 
     */
    public void ejecutarrcCerrar() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Este metodo es ejecutado despues de finalizar la insercion y
     * edicion del registro se usa cuando se desean agregar valores al
     * registro despues de dichas acciones
     */
    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        cargarValoresDefecto();
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta al abrir el formulario0, el cual carga
     * valores de 0 por defecto
     */
    public void cargarValoresDefecto() {
        registro.getCampos().put(FrmprogramacionfisicasControladorEnum.EJECUTADO_TR1_FIN.getValue(), 0);
        registro.getCampos().put(FrmprogramacionfisicasControladorEnum.EJECUTADO_TR2_FIN.getValue(), 0);
        registro.getCampos().put(FrmprogramacionfisicasControladorEnum.EJECUTADO_TR3_FIN.getValue(), 0);
        registro.getCampos().put(FrmprogramacionfisicasControladorEnum.EJECUTADO_TR4_FIN.getValue(), 0);
        registro.getCampos().put(FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR1_FIN.getValue(), 0);
        registro.getCampos().put(FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR2_FIN.getValue(), 0);
        registro.getCampos().put(FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR3_FIN.getValue(), 0);
        registro.getCampos().put(FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR4_FIN.getValue(), 0);
        registro.getCampos().put(FrmprogramacionfisicasControladorEnum.CANTIDADPROGRAMADA.getValue(), 0);
        registro.getCampos().put(FrmprogramacionfisicasControladorEnum.CANTIDADEJECUTADA.getValue(), 0);
        registro.getCampos().put(FrmprogramacionfisicasControladorEnum.EJECUCION.getValue(), 0);
        registro.getCampos().put(FrmprogramacionfisicasControladorEnum.EJECUCIONUNO.getValue(), 0);
        registro.getCampos().put(FrmprogramacionfisicasControladorEnum.EJECUCIONDOS.getValue(), 0);
        registro.getCampos().put(FrmprogramacionfisicasControladorEnum.EJECUCIONTRES.getValue(), 0);
        registro.getCampos().put(FrmprogramacionfisicasControladorEnum.EJECUCIONCUATRO.getValue(), 0);

    }

    /**
     * Metodo ejectado al abrir el formulario y al realizar una
     * insercion
     */
    public void contarRegistros() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.TIPO.getName(), tipo);
        param.put(GeneralParameterEnum.NUMERO.getName(), numero);
        param.put(FrmprogramacionfisicasControladorEnum.ID_PLAN.getValue(), idPlan);
        param.put(FrmprogramacionfisicasControladorEnum.VIGENCIA_INICIAL.getValue(), vigencia);
        try
        {
            Registro rsContar = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmprogramacionfisicasControladorUrlEnum.URL155
                                                                            .getValue())
                                            .getUrl(), param));
            if ("0".equals(rsContar.getCampos().get("EXISTE").toString()))
            {
                activarGenerar = true;
            }
            else
            {
                activarGenerar = false;
            }
        }
        catch (SystemException e)
        {

            JsfUtil.agregarMensajeError(e.getMessage());
            logger.error(e.getMessage(), e);
        }

    }

    /**
     * Metodo que se ejecuta al actualizar los campos de programado y
     * ejecutado
     * 
     * @param opcion
     * @param rowNum
     * @param campo
     */
    public void actualizarEjecuciones(String opcion, int rowNum, String campoProgramado, String campoEjecutado, String campoEjecucion) {
        double programadoUno = Double.parseDouble(SysmanFunciones
                        .nvl("1".equals(opcion)
                            ? registro.getCampos().get(campoProgramado)
                            : listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                            .get(campoProgramado),
                                        "0")
                        .toString());
        double ejecutadoUno = Double.parseDouble(SysmanFunciones
                        .nvl("1".equals(opcion)
                            ? registro.getCampos().get(campoEjecutado)
                            : listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                            .get(campoEjecutado),
                                        "0")
                        .toString());

        double cantidadEjecucion;
        if (programadoUno != 0 && ejecutadoUno != 0)
        {
            cantidadEjecucion = ejecutadoUno / programadoUno * 100;
        }
        else
        {
            cantidadEjecucion = 0;
        }

        if ("1".equals(opcion))
        {
            registro.getCampos().put(campoEjecucion, cantidadEjecucion);

        }
        else
        {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(campoEjecucion, cantidadEjecucion);

        }
    }

    /**
     * Metodo que se ejecuta al actualizar los valores de programado y
     * ejecutado
     */
    public void actualizarSumas(String opcion, int rowNum) {

        double programadoUno = Double.parseDouble(SysmanFunciones
                        .nvl("1".equals(opcion)
                            ? registro.getCampos().get(FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR1_FIN.getValue())
                            : listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                            .get(FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR1_FIN.getValue()),
                                        "0")
                        .toString());
        double programadoDos = Double.parseDouble(SysmanFunciones
                        .nvl("1".equals(opcion)
                            ? registro.getCampos().get(FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR2_FIN.getValue())
                            : listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                            .get(FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR2_FIN.getValue()),
                                        "0")
                        .toString());
        double programadoTres = Double.parseDouble(SysmanFunciones
                        .nvl("1".equals(opcion)
                            ? registro.getCampos().get(FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR3_FIN.getValue())
                            : listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                            .get(FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR3_FIN.getValue()),
                                        "0")
                        .toString());
        double programadoCuatro = Double.parseDouble(SysmanFunciones
                        .nvl("1".equals(opcion)
                            ? registro.getCampos().get(FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR4_FIN.getValue())
                            : listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                            .get(FrmprogramacionfisicasControladorEnum.PROGRAMADO_TR4_FIN.getValue()),
                                        "0")
                        .toString());
        double ejecutadoUno = Double.parseDouble(SysmanFunciones
                        .nvl("1".equals(opcion)
                            ? registro.getCampos().get(FrmprogramacionfisicasControladorEnum.EJECUTADO_TR1_FIN.getValue())
                            : listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                            .get(FrmprogramacionfisicasControladorEnum.EJECUTADO_TR1_FIN.getValue()),
                                        "0")
                        .toString());
        double ejecutadoDos = Double.parseDouble(SysmanFunciones
                        .nvl("1".equals(opcion)
                            ? registro.getCampos().get(FrmprogramacionfisicasControladorEnum.EJECUTADO_TR2_FIN.getValue())
                            : listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                            .get(FrmprogramacionfisicasControladorEnum.EJECUTADO_TR2_FIN.getValue()),
                                        "0")
                        .toString());
        double ejecutadoTres = Double.parseDouble(SysmanFunciones
                        .nvl("1".equals(opcion)
                            ? registro.getCampos().get(FrmprogramacionfisicasControladorEnum.EJECUTADO_TR3_FIN.getValue())
                            : listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                            .get(FrmprogramacionfisicasControladorEnum.EJECUTADO_TR3_FIN.getValue()),
                                        "0")
                        .toString());
        double ejecutadoCuatro = Double.parseDouble(SysmanFunciones
                        .nvl("1".equals(opcion)
                            ? registro.getCampos().get(FrmprogramacionfisicasControladorEnum.EJECUTADO_TR4_FIN.getValue())
                            : listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                            .get(FrmprogramacionfisicasControladorEnum.EJECUTADO_TR3_FIN.getValue()),
                                        "0")
                        .toString());

        double cantidadProgramada = programadoUno + programadoDos + programadoTres + programadoCuatro;
        double cantidadEjecutada = ejecutadoUno + ejecutadoDos + ejecutadoTres + ejecutadoCuatro;
        double cantidadPorcentual;
        if (cantidadEjecutada != 0 && cantidadProgramada != 0)
        {
            cantidadPorcentual = cantidadEjecutada / cantidadProgramada * 100;
        }
        else
        {
            cantidadPorcentual = 0;
        }

        if ("1".equals(opcion))
        {
            registro.getCampos().put(FrmprogramacionfisicasControladorEnum.CANTIDADPROGRAMADA.getValue(), cantidadProgramada);
            registro.getCampos().put(FrmprogramacionfisicasControladorEnum.CANTIDADEJECUTADA.getValue(), cantidadEjecutada);
            registro.getCampos().put(FrmprogramacionfisicasControladorEnum.EJECUCION.getValue(),
                            SysmanFunciones.redondear(cantidadPorcentual, 2));

        }
        else
        {
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(FrmprogramacionfisicasControladorEnum.CANTIDADPROGRAMADA.getValue(), cantidadProgramada);
            listaInicial.getDatasource().get(rowNum % 10).getCampos()
                            .put(FrmprogramacionfisicasControladorEnum.CANTIDADEJECUTADA.getValue(), cantidadEjecutada);
            listaInicial.getDatasource().get(rowNum % 10).getCampos().put(FrmprogramacionfisicasControladorEnum.EJECUCION.getValue(),
                            SysmanFunciones.redondear(cantidadPorcentual, 2));
        }

    }

    /**
     * Metodo que se ejecuta al cambiar alguna fecha del formulario
     * 
     * @param opcion
     * @param rowNum
     */
    public void validarFechas(String opcion, int rowNum) {

        if (!SysmanFunciones.validarCampoVacio(
                        "1".equals(opcion) ? registro.getCampos() : listaInicial.getDatasource().get(rowNum % 10).getCampos(),
                        FrmprogramacionfisicasControladorEnum.FECHA_FINAL_FIN.getValue()))
        {
            Date fechaInicial = (Date) ("1".equals(opcion) ? registro.getCampos()
                            .get(FrmprogramacionfisicasControladorEnum.FECHA_INICIAL_FIN.getValue())
                : listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                .get(FrmprogramacionfisicasControladorEnum.FECHA_INICIAL_FIN.getValue()));

            Date fechaFinal = (Date) ("1".equals(opcion) ? registro.getCampos()
                            .get(FrmprogramacionfisicasControladorEnum.FECHA_FINAL_FIN.getValue())
                : listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                .get(FrmprogramacionfisicasControladorEnum.FECHA_FINAL_FIN.getValue()));
            if (fechaFinal.before(fechaInicial))
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB147"));
                if ("1".equals(opcion))
                {
                    registro.getCampos().put(FrmprogramacionfisicasControladorEnum.FECHA_FINAL_FIN.getValue(), null);
                }
                else
                {
                    listaInicial.getDatasource().get(rowNum % 10).getCampos()
                                    .put(FrmprogramacionfisicasControladorEnum.FECHA_FINAL_FIN.getValue(), null);
                }
            }

        }
    }

    // <SET_GET_ATRIBUTOS>
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
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
     * @param indice
     */
    public void setIndice(int indice) {
        this.indice = indice;
    }

    /**
     * @return
     */
    public boolean isActivarGenerar() {
        return activarGenerar;
    }

    /**
     * @param activarGenerar
     */
    public void setActivarGenerar(boolean activarGenerar) {
        this.activarGenerar = activarGenerar;
    }

    /**
     * @return
     */
    public boolean isBloquearProgramado() {
        return bloquearProgramado;
    }

    /**
     * @param bloquearProgramado
     */
    public void setBloquearProgramado(boolean bloquearProgramado) {
        this.bloquearProgramado = bloquearProgramado;
    }

    /**
     * @return
     */
    public boolean isBloquearEjecutar() {
        return bloquearEjecutar;
    }

    /**
     * @param bloquearEjecutar
     */
    public void setBloquearEjecutar(boolean bloquearEjecutar) {
        this.bloquearEjecutar = bloquearEjecutar;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaVigencia
     * 
     * @return listaVigencia
     */
    public List<Registro> getListaVigencia() {
        return listaVigencia;
    }

    /**
     * Asigna la lista listaVigencia
     * 
     * @param listaVigencia
     * Variable a asignar en listaVigencia
     */
    public void setListaVigencia(List<Registro> listaVigencia) {
        this.listaVigencia = listaVigencia;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}

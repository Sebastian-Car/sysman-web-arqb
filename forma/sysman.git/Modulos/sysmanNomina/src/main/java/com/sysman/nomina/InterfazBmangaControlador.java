/*-
 * InterfazBmangaControlador.java
 *
 * 1.0
 * 
 * 19 jun. 2019
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */

package com.sysman.nomina;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.contabilizar.ejb.impl.EjbContabilizarNominaTres;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.nomina.enums.InterfazBmangaControladorUrlEnum;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.naming.NamingException;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que genera archivo de interfazar para Bucaramanga
 *
 * @version 1.0, 19/06/2019
 * @author eamaya
 */
@ManagedBean
@ViewScoped

public class InterfazBmangaControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    private final String usuario;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable que almacena el valor del anio seleccionado en el
     * formulario
     */
    private String anio;
    /**
     * Variable que almacena el valor del mes seleccionado en el
     * formulario
     */
    private String mes;
    /**
     * Variable que almacena el valor del periodo seleccionado en el
     * formulario
     */
    private String periodo;

    /**
     * Variable que almacena el valor del proceso de nomina
     * seleccionado al ingresar al modulo
     * 
     */
    private String proceso;
    /**
     * Variable que almacena el valor del tipo de comprobante
     * seleccionado en el formulario
     */
    private String tipoCpte;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Lista que carga los anios
     */
    private List<Registro> listaAnio;
    /**
     * Lista que carga los meses
     */
    private List<Registro> listaMes;
    /**
     * Lista que carga los periodos
     */
    private List<Registro> listaPeriodo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que carga los tipos de comprobante
     */
    private RegistroDataModelImpl listaTipoComprobante;

    @EJB
    private EjbContabilizarNominaTres ejbContabilizarNominaTres;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de InterfazBmangaControlador
     */
    public InterfazBmangaControlador() {
        super();
        compania = SessionUtil.getCompania();
        usuario = SessionUtil.getUser().getCodigo();

        try {
            numFormulario = GeneralCodigoFormaEnum.INTERFAZ_BMANGA_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
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
        anio = validaVacio(SessionUtil.getSessionVar("anioNomina"));
        mes = validaVacio(SessionUtil.getSessionVar("mesNomina"));
        periodo = validaVacio(SessionUtil.getSessionVar("periodoNomina"));
        proceso = validaVacio(SessionUtil.getSessionVar("procesoNomina"));

        // <CARGAR_LISTA>
        cargarListaAnio();
        cargarListaMes();
        cargarListaPeriodo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaTipoComprobante();
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
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
     * Carga la lista listaAnio
     *
     */
    public void cargarListaAnio() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);

        try {
            listaAnio = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InterfazBmangaControladorUrlEnum.URL4865
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaMes
     *
     */
    public void cargarListaMes() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        anio);

        try {
            listaMes = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            InterfazBmangaControladorUrlEnum.URL5255
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaPeriodo
     *
     */
    public void cargarListaPeriodo() {
        Map<String, Object> param = new HashMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);
        param.put(GeneralParameterEnum.MES.getName(), mes);

        try {
            listaPeriodo = RegistroConverter
                            .toListRegistro(requestManager.getList(
                                            UrlServiceUtil.getInstance()
                                                            .getUrlServiceByUrlByEnumID(
                                                                            InterfazBmangaControladorUrlEnum.URL5689
                                                                                            .getValue())
                                                            .getUrl(),
                                            param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Carga la lista listaTipoComprobante
     *
     */
    public void cargarListaTipoComprobante() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        InterfazBmangaControladorUrlEnum.URL6219
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("CLASECONTABLE", "P");

        listaTipoComprobante = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "CODIGO");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Interfazar en la vista
     *
     *
     */
    public void oprimirInterfazar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        String salida = null;
        Statement cstmt = null;
        ConectorPool cp = new ConectorPool();
        try {
            salida = ejbContabilizarNominaTres.contabilizarNominaHBucarama(
                            compania,
                            Integer.parseInt(proceso), Integer.parseInt(anio),
                            Integer.parseInt(mes),
                            Integer.parseInt(periodo), usuario);

            if (salida != null
                && !SysmanFunciones.validarVariableVacio(salida)) {

                String[] registro = salida.split(";");

                for (int i = 0; i < registro.length; i++) {

                    String query = registro[i];
                    cp.conectar(ConectorPool.ESQUEMA_INTERFAZBUCA);

                    cstmt = cp.getConection().createStatement();

                    cstmt.executeUpdate(query);

                }

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("MSM_PROCESO_EJECUTADO"));

            }
            else {

                JsfUtil.agregarMensajeAlerta(
                                idioma.getString(
                                                "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));

            }

        }
        catch (NumberFormatException | SystemException
                        | SQLException | NamingException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());

        }
        finally {

            try {
                cp.getConection().close();
                if (cstmt != null) {
                    cstmt.close();
                }

            }
            catch (SQLException e) {
                logger.error(e.getMessage(), e);
                JsfUtil.agregarMensajeError(e.getMessage());
            }
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton GenerarScript en la vista
     *
     *
     */
    public void oprimirGenerarScript() {
        archivoDescarga = null;

        String salida = null;
        ByteArrayInputStream streamTexto;
        ByteArrayInputStream salidaNombreExcel = null;
        ByteArrayInputStream salidaNombreExcel2 = null;

        ByteArrayInputStream[] salidaComprimido = new ByteArrayInputStream[3];
        String[] nombres = new String[3];

        Map<String, Object> reemplazos = new TreeMap<>();

        try {
            salida = ejbContabilizarNominaTres.contabilizarNominaHBucarama(
                            compania,
                            Integer.parseInt(proceso), Integer.parseInt(anio),
                            Integer.parseInt(mes),
                            Integer.parseInt(periodo), usuario);

            if (salida != null
                && !SysmanFunciones.validarVariableVacio(salida)) {

                streamTexto = JsfUtil.serializarPlano(salida);

                reemplazos.put("anio", anio);
                reemplazos.put("mes", mes);

                String consulta1 = Reporteador.resuelveConsulta(
                                "800350TEMPORALINTERFAZ",
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazos);

                String consulta2 = Reporteador.resuelveConsulta(
                                "800351TEMPORALINTERFAZASIENTO",
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazos);

                try {

                    salidaNombreExcel = JsfUtil.serializarHojaDatos(consulta1,
                                    ConectorPool.ESQUEMA_SYSMAN,
                                    ReportesBean.FORMATOS.EXCEL);
                }
                catch (SysmanException | JRException | IOException | DRException
                                | SQLException e) {
                    logger.error(e.getMessage(), e);
                    JsfUtil.agregarMensajeError(e.getMessage());
                }
                try {
                    salidaNombreExcel2 = JsfUtil.serializarHojaDatos(consulta2,
                                    ConectorPool.ESQUEMA_SYSMAN,
                                    ReportesBean.FORMATOS.EXCEL);

                }
                catch (SysmanException | JRException | IOException | DRException
                                | SQLException e1) {
                    logger.error(e1.getMessage(), e1);
                    JsfUtil.agregarMensajeError(e1.getMessage());
                }

                int cantidad = 0;
                if (streamTexto != null) {
                    salidaComprimido[cantidad] = streamTexto;
                    nombres[cantidad] = "ScriptsInterfaz.sql";
                    cantidad++;
                }

                if (salidaNombreExcel != null) {
                    salidaComprimido[cantidad] = salidaNombreExcel;
                    nombres[cantidad] = "TEMPORALINTERFAZ.xlsx";
                    cantidad++;
                }

                if (salidaNombreExcel2 != null) {
                    salidaComprimido[cantidad] = salidaNombreExcel2;
                    nombres[cantidad] = "TEMPORALINTERFAZASIENTO.xlsx";
                    cantidad++;
                }

                if (cantidad > 0) {
                    archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(
                                    salidaComprimido,
                                    nombres, "Interfaz");

                }
            }
            else {
                JsfUtil.agregarMensajeAlerta(
                                idioma.getString(
                                                "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));

            }
        }
        catch (NumberFormatException | SystemException | JRException
                        | IOException | SQLException | DRException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Anio
     * 
     * 
     */
    public void cambiarAnio() {
        mes = null;
        periodo = null;
        cargarListaMes();
    }

    /**
     * Metodo ejecutado al cambiar el control Mes
     * 
     * 
     */
    public void cambiarMes() {
        periodo = null;
        cargarListaPeriodo();
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaTipoComprobante
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaTipoComprobante(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        tipoCpte = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>

    public String validaVacio(Object parametro) {
        return parametro != null ? parametro.toString() : null;
    }

    // <SET_GET_ATRIBUTOS>
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
     * Retorna la variable tipoCpte
     * 
     * @return tipoCpte
     */
    public String getTipoCpte() {
        return tipoCpte;
    }

    /**
     * Asigna la variable tipoCpte
     * 
     * @param tipoCpte
     * Variable a asignar en tipoCpte
     */
    public void setTipoCpte(String tipoCpte) {
        this.tipoCpte = tipoCpte;
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
     * Retorna la lista listaAnio
     * 
     * @return listaAnio
     */
    public List<Registro> getListaAnio() {
        return listaAnio;
    }

    /**
     * Asigna la lista listaAnio
     * 
     * @param listaAnio
     * Variable a asignar en listaAnio
     */
    public void setListaAnio(List<Registro> listaAnio) {
        this.listaAnio = listaAnio;
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

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaTipoComprobante
     * 
     * @return listaTipoComprobante
     */
    public RegistroDataModelImpl getListaTipoComprobante() {
        return listaTipoComprobante;
    }

    /**
     * Asigna la lista listaTipoComprobante
     * 
     * @param listaTipoComprobante
     * Variable a asignar en listaTipoComprobante
     */
    public void setListaTipoComprobante(
        RegistroDataModelImpl listaTipoComprobante) {
        this.listaTipoComprobante = listaTipoComprobante;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}

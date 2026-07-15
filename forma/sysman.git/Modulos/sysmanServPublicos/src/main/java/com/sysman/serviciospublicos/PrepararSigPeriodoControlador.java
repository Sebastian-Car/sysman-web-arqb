/*-
 * PrepararSigPeriodoControlador.java
 *
 * 1.0
 * 
 * 30/01/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

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
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosCeroRemote;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosSeisRemote;
import com.sysman.serviciospublicos.enums.PrepararSigPeriodoControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
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

import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Controlador de la forma prepararsigperiodo asociada al formulario
 * Preparacion siguiente periodo. Que permite realizar el cierre de
 * periodo de facturacion, actualizando estadisticas. A su vez genera
 * un informe en caso de haber inconsistencias en los recaudos del
 * periodo del ciclo seleccionado y un comprimido, con un archivo pdf
 * de los usuarios a los que se les actualizara el peso aseo y un log
 * del proceso realizado en las diferentes etapas de la preparacion
 * del siguiente periodo de facturacion.
 * 
 * @version 1.0, 30/01/2017
 * @author jlramirez
 * 
 * @author eamaya
 * @version 2.0, 13/06/2017 Se cambi� el llamado del c�digo del
 * formulario y actualizaci�n de ConnectorPool
 * 
 * @modified jguerrero
 * @version 3. 14/06/2017 Se realizo el refactory de las consultas sql
 * en el controlador. Adem�s se ajustaron los errores del sonar
 * 
 * 
 */
@ManagedBean
@ViewScoped

public class PrepararSigPeriodoControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante que almacena la palabra "SYSDATE"
     */

    // <DECLARAR_ATRIBUTOS>
    /**
     * Numero del ciclo seleccionado en la combo ciclo.
     */
    private String ciclo;
    /**
     * periodo actual de facturacion.
     */
    private String periodo;
    /**
     * a�o actual de facturacion
     */
    private int anio;
    /**
     * periodo siguiente, el que se va a preparar.
     */
    private String periodoSig;
    /**
     * a�o del periodo de facturacion que se va a preparar.
     */
    private int anioSig;
    /**
     * atributo que hace visible o no un dialogo que pregunta al
     * usuario si desea continuar con el proceso de cierre y
     * preparacion de periodo.
     */
    private boolean mostrarDgProceso;
    /**
     * atributo que hace visible o no un dialogo que pregunta al
     * usuario si desea sobreescribir los datos para cerrar
     * descuentos, en caso de que el periodo siguiente ya se encuentre
     * preparado.
     */
    private boolean mostrarDgSobredes;
    /**
     * atributo que hace visible o no un dialogo que pregunta al
     * usuario si desea sobreescribir los datos para cerrar
     * productividad, en caso de que el periodo siguiente ya se
     * encuentre preparado.
     */
    private boolean mostrarDgSobrepro;
    /**
     * atributo que almacena la pregunta del dialogo que pregunta
     * acerca de sobreescribir la informacion para cerrar descuentos.
     */
    private String textoDialogod;
    /**
     * atributo que almacena la pregunta del dialogo que pregunta
     * acerca de sobreescribir la informacion para cerrar
     * productividad.
     */
    private String textoDialogop;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista.
     */
    private StreamedContent archivoDescarga;
    /**
     * atributo que almacena el valor del parametro
     * "MANEJA CONTROL DE DESCUENTOS POR CONCEPTOS"
     */
    private String descConceptos;

    /**
     * atributo que almacena el valor del parametro
     * "DESCONTAR PRODUCTIVIDAD"
     */
    private String descProductividad;
    /**
     * atributo que almacena el valor del parametro
     * "PESO ASEO PARA CALCULO DE ESTADISTICAS"
     */
    private Double pesoAseo;
    /**
     * atributo que almacena si o no, dependiendo si el usuario desea
     * sobreescribir la informacion de productividad
     */
    private String cerrarPro;
    /**
     * atributo que almacena si o no, dependiendo si el usuario desea
     * sobreescribir la informacion de descuentos
     */
    private String cerrarDes;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista que contiene la informaci�n de los detalles del combo
     * ciclo.
     */
    private List<Registro> listaCiclo;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbServiciosPublicosCeroRemote ejbSerPubCero;

    @EJB
    private EjbServiciosPublicosSeisRemote ejbSerPubSeis;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de PrepararSigPeriodoControlador
     */
    public PrepararSigPeriodoControlador() {
        super();
        compania = SessionUtil.getCompania();

        try {
            numFormulario = GeneralCodigoFormaEnum.PREPARAR_SIG_PERIODO_CONTROLADOR
                            .getCodigo();

            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(PrepararSigPeriodoControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
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
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCiclo();
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
        ciclo = "1";
        mostrarDgProceso = false;
        mostrarDgSobredes = false;
        mostrarDgSobrepro = false;
        cerrarDes = "NO";
        cerrarPro = "NO";
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaCiclo
     */
    public void cargarListaCiclo() {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try {
            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PrepararSigPeriodoControladorUrlEnum.URL8038
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * Metodo ejecutado al oprimir el boton Aceptar en la vista
     */
    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>

        cargarParametros();
        mostrarDgProceso = true;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Cancelar en la vista
     */
    public void oprimirCancelar() {
        // <CODIGO_DESARROLLADO>
        JsfUtil.ejecutarJavaScript("cerrarModalDefault()");
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo en el que se cargan parametros y valores (traidos de
     * funciones) para algunos atributos.
     * 
     * @param con
     * Conexion
     */
    public void cargarParametros() {
        try {
            anio = Integer.parseInt(service.buscarEnLista(ciclo, "NUMERO",
                            "ANO", listaCiclo));

            periodo = service.buscarEnLista(ciclo, "NUMERO", "PERIODO",
                            listaCiclo);

            descConceptos = ejbSysmanUtil.consultarParametro(compania,
                            "MANEJA CONTROL DE DESCUENTOS POR CONCEPTOS",
                            SessionUtil.getModulo(),
                            new Date(), true);

            descProductividad = ejbSysmanUtil.consultarParametro(compania,
                            "DESCONTAR PRODUCTIVIDAD", SessionUtil.getModulo(),
                            new Date(), true);

            pesoAseo = Double.parseDouble(SysmanFunciones
                            .nvlStr(ejbSysmanUtil.consultarParametro(compania,
                                            "PESO ASEO PARA CALCULO DE ESTADISTICAS",
                                            SessionUtil.getModulo(),
                                            new Date(), true), "1"));

            anioSig = Integer
                            .parseInt(ejbSerPubCero.prepararAnoPeriodoSiguiente(
                                            compania, anio, periodo, "0",
                                            null));

            periodoSig = ejbSerPubCero.prepararAnoPeriodoSiguiente(
                            compania, anio, periodo, "1", null);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * RealizarProceso en la vista. El cual pregunta si el usuario
     * esta seguro de realizar la preparacion del siguiente periodo,
     * ya que sera irreversible.
     *
     */
    public void aceptarRealizarProceso() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        mostrarDgProceso = false;
        cerrarProductividad();
        cerrarDescuentos();
        if (!mostrarDgSobredes && !mostrarDgSobrepro) {
            generarReporte();
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * RealizarProceso en la vista. Si el usuario selecciona "NO",
     * entonces se cierra el modal.
     *
     */
    public void cancelarRealizarProceso() {
        // <CODIGO_DESARROLLADO>
        mostrarDgProceso = false;

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo en el que se muestra un dialogo al usuario en caso de
     * que ya exista informacion de descuentos antes de realizar el
     * cierre.
     */
    public void cerrarDescuentos() {

        try {

            if ("SI".equals(descConceptos)) {

                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
                param.put(GeneralParameterEnum.ANO.getName(), anioSig);
                param.put(GeneralParameterEnum.PERIODO.getName(), periodoSig);
                Registro aux = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                PrepararSigPeriodoControladorUrlEnum.URL8039
                                                                                .getValue())
                                                .getUrl(), param));

                if (Integer.parseInt(aux.getCampos().get("CANTIDAD")
                                .toString()) > 0) {
                    textoDialogod = idioma.getString("TB_TB2844")
                                    .replace("#$aniosig$#",
                                                    String.valueOf(anioSig))
                                    .replace("#$periodosig$#", periodoSig);
                    mostrarDgSobredes = true;
                }
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Metodo en el que se muestra un dialogo al usuario en caso de
     * que ya exista informacion de productividad antes de realizar el
     * cierre.
     */
    public void cerrarProductividad() {

        try {

            if ("SI".equals(descProductividad)) {

                Map<String, Object> param = new TreeMap<>();
                param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
                param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
                param.put(GeneralParameterEnum.ANO.getName(), anioSig);
                param.put(GeneralParameterEnum.PERIODO.getName(), periodoSig);
                Registro aux;
                aux = RegistroConverter.toRegistro(
                                requestManager.get(UrlServiceUtil.getInstance()
                                                .getUrlServiceByUrlByEnumID(
                                                                PrepararSigPeriodoControladorUrlEnum.URL8040
                                                                                .getValue())
                                                .getUrl(), param));

                if (Integer.parseInt(aux.getCampos().get("CANTIDAD")
                                .toString()) > 0) {
                    textoDialogop = idioma.getString("TB_TB2849")
                                    .replace("#$aniosig$#",
                                                    String.valueOf(anioSig))
                                    .replace("#$periodosig$#", periodoSig);
                    mostrarDgSobrepro = true;
                }
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * Sobreescribir en la vista. Si el usuario decide sobreescribir
     * la informacion existente, se cierran descuentos controlados por
     * concepto y continua con la preparacion del periodo siguiente.
     * 
     * @throws InterruptedException
     */
    public void aceptarSobreescribirDescuento() {
        // <CODIGO_DESARROLLADO>
        mostrarDgSobredes = false;
        cerrarDes = "SI";
        if (!mostrarDgSobrepro) {
            generarReporte();
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * Sobreescribir en la vista. Si el usuario decide dejar los datos
     * existentes, continua con el proceso de preparacion de periodo
     * siguiente.
     */
    public void cancelarSobreescribirDescuento() {
        // <CODIGO_DESARROLLADO>
        mostrarDgSobredes = false;
        if (!mostrarDgSobrepro) {
            generarReporte();
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Aceptar del dialogo
     * SobreescribirProduc en la vista
     * 
     * @throws InterruptedException
     */
    public void aceptarSobreescribirProduc() {
        // <CODIGO_DESARROLLADO>
        mostrarDgSobrepro = false;
        cerrarPro = "SI";
        if (!mostrarDgSobredes) {
            generarReporte();
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton Cancelar del dialogo
     * SobreescribirProduc en la vista
     */
    public void cancelarSobreescribirProduc() {
        // <CODIGO_DESARROLLADO>
        mostrarDgSobrepro = false;
        if (!mostrarDgSobredes) {
            generarReporte();
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que genera un comprimido con un archivo pdf de los
     * usuarios a los que se les actualizara el peso aseo y un log del
     * proceso realizado en las diferentes etapas de la preparacion
     * del siguiente periodo de facturacion.
     * 
     * @param con
     * Conexion
     */
    private void generarReporte() {

        try {

            ByteArrayInputStream[] salidas = new ByteArrayInputStream[2];
            String[] nombres = new String[2];

            String cadena = ejbSerPubSeis.llamarPrepararSigPeriodo(compania,
                            Integer.parseInt(ciclo), anio, periodo, cerrarPro,
                            cerrarDes,
                            SessionUtil.getCompaniaIngreso().getNit(),
                            SessionUtil.getUser().getCodigo());

            String reporteLog = "preparar_" + compania + "_" + ciclo + "_"
                +
                SysmanFunciones.getParteFecha(new Date(),
                                Calendar.DAY_OF_MONTH)
                + SysmanFunciones.getParteFecha(
                                new Date(),
                                Calendar.MONTH)
                + SysmanFunciones.getParteFecha(new Date(),
                                Calendar.YEAR);
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            String reportePdf = "001420USUARIOSPESOASEO";
            // <REEMPLAZAR VARIABLES EN CONSULTA>
            reemplazar.put("ciclo", ciclo);
            reemplazar.put("pesoaseo", pesoAseo);
            // </REEMPLAZAR VARIABLES EN CONSULTA>
            // <ENVIAR PARAMETROS AL REPORTE>
            // </ENVIAR PARAMETROS AL REPORTE>
            Reporteador.resuelveConsulta(reportePdf,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            salidas[0] = JsfUtil
                            .serializarPlano(cadena);
            salidas[1] = JsfUtil
                            .serializarReporte(reportePdf, parametros,
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            FORMATOS.PDF);
            nombres[0] = reporteLog;
            nombres[1] = reportePdf + ".pdf";

            archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(salidas,
                            nombres, "Reportes");
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException | NumberFormatException
                        | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable ciclo
     * 
     * @return ciclo
     */
    public String getCiclo() {
        return ciclo;
    }

    /**
     * Asigna la variable ciclo
     * 
     * @param ciclo
     * Variable a asignar en ciclo
     */
    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
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
     * Retorna la variable anio
     * 
     * @return anio
     */
    public int getAnio() {
        return anio;
    }

    /**
     * Asigna la variable anio
     * 
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(int anio) {
        this.anio = anio;
    }

    /**
     * Retorna la variable periodoSig
     * 
     * @return periodoSig
     */
    public String getPeriodoSig() {
        return periodoSig;
    }

    /**
     * Asigna la variable periodoSig
     * 
     * @param periodoSig
     * Variable a asignar en periodoSig
     */
    public void setPeriodoSig(String periodoSig) {
        this.periodoSig = periodoSig;
    }

    /**
     * Retorna la variable anioSig
     * 
     * @return anioSig
     */
    public int getAnioSig() {
        return anioSig;
    }

    /**
     * Asigna la variable anioSig
     * 
     * @param anioSig
     * Variable a asignar en anioSig
     */
    public void setAnioSig(int anioSig) {
        this.anioSig = anioSig;
    }

    /**
     * Retorna la variable mostrarDgProceso
     * 
     * @return mostrarDgProceso
     */
    public boolean isMostrarDgProceso() {
        return mostrarDgProceso;
    }

    /**
     * Asigna la variable mostrarDgProceso
     * 
     * @param mostrarDgProceso
     * Variable a asignar en mostrarDgProceso
     */
    public void setMostrarDgProceso(boolean mostrarDgProceso) {
        this.mostrarDgProceso = mostrarDgProceso;
    }

    /**
     * Retorna la variable mostrarDgSobredes
     * 
     * @return mostrarDgSobredes
     */
    public boolean isMostrarDgSobredes() {
        return mostrarDgSobredes;
    }

    /**
     * Asigna la variable mostrarDgSobredes
     * 
     * @param mostrarDgSobredes
     * Variable a asignar en mostrarDgSobredes
     */
    public void setMostrarDgSobredes(boolean mostrarDgSobredes) {
        this.mostrarDgSobredes = mostrarDgSobredes;
    }

    /**
     * Retorna la variable mostrarDgSobrepro
     * 
     * @return mostrarDgSobrepro
     */
    public boolean isMostrarDgSobrepro() {
        return mostrarDgSobrepro;
    }

    /**
     * Asigna la variable mostrarDgSobrepro
     * 
     * @param mostrarDgSobrepro
     * Variable a asignar en mostrarDgSobrepro
     */
    public void setMostrarDgSobrepro(boolean mostrarDgSobrepro) {
        this.mostrarDgSobrepro = mostrarDgSobrepro;
    }

    /**
     * Retorna la variable textoDialogod
     * 
     * @return textoDialogod
     */
    public String getTextoDialogod() {
        return textoDialogod;
    }

    /**
     * Asigna la variable textoDialogod
     * 
     * @param textoDialogod
     * Variable a asignar en textoDialogod
     */
    public void setTextoDialogod(String textoDialogod) {
        this.textoDialogod = textoDialogod;
    }

    /**
     * Retorna la variable textoDialogop
     * 
     * @return textoDialogop
     */
    public String getTextoDialogop() {
        return textoDialogop;
    }

    /**
     * Asigna la variable textoDialogop
     * 
     * @param textoDialogop
     * Variable a asignar en textoDialogop
     */
    public void setTextoDialogop(String textoDialogop) {
        this.textoDialogop = textoDialogop;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista.
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCiclo
     * 
     * @return listaCiclo
     */
    public List<Registro> getListaCiclo() {
        return listaCiclo;
    }

    /**
     * Asigna la lista listaCiclo
     * 
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(List<Registro> listaCiclo) {
        this.listaCiclo = listaCiclo;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}

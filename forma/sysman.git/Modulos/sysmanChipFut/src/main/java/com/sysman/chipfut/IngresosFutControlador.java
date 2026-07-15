/*-
 * IngresosFutControlador.java
 *
 * 1.0
 * 
 * 17/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.chipfut;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.chipfut.enums.IngresosFutControladorUrlEnum;
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
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
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

import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Formulario que permite generar archivos planos de ingresos fut
 *
 * @version 1.0, 17/07/2018
 * @author lbotia
 */
@ManagedBean
@ViewScoped
public class IngresosFutControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Codigo del modulo
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Constante a nivel de clase que almacena variable booleana del
     * check en miles de pesos (CK1277)
     */
    private boolean pesos;
    /**
     * Constante a nivel de clase que almacena variable booleana del
     * ckeck transferencias bancarias (CK1280)
     */
    private boolean transferencia;
    /**
     * Constante a nivel de clase que almacena variable booleana del
     * check porMes (CK1281)
     */
    private boolean porMes;
    /**
     * Constante a nivel de clase que almacena variable booleana para
     * poner visible el combo mes (CB4530)
     */

    private boolean visibleMes;
    /**
     * Constante a nivel de clase que almacena variable booleana para
     * poner visible el combo trimestre (CB4516)
     */

    private boolean visibleTrimestre;
    /**
     * Constante a nivel de clase que almacena el año seleccionado en
     * el formulario
     */
    private int anoTrabajo;
    /**
     * Constante a nivel de clase que almacena el mes seleccionado en
     * el combo mes (CB4530)
     */
    private int mes;
    /**
     * Constante a nivel de clase que almacena mesInicial declarado en
     * el metodo validar mes para realizar el metodo de validar o por
     * mes o por trimestre
     */
    private int mesInicial;
    /**
     * Constante a nivel de clase que almacena mesFinal declarado en
     * el metodo validar mes para realizar el metodo de validar o por
     * mes o por trimestre
     */
    private int mesFinal;
    /**
     * Constante a nivel de clase que almacena el codigo de la entidad
     * digitado en el campo de texto (CP44237)
     */
    private String codigoEntidad;
    /**
     * Constante a
     */
    private int ano;
    /**
     * Constante a nivel de clase que almacena el trimestre
     * seleccionado en el combo (CB4516) del formulario
     */
    private int trimestre;

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
     * Lista sencilla que carga los años en el combo (CB4517)
     */
    private List<Registro> listaAnoTrabajo;
    /**
     * Lista sencilla que carga los meses en el combo (CB4530)
     */
    private List<Registro> listaMes;

    /**
     * Ejb que llama la función almacenada en el paquete PCK_CHIPFUT
     * (FC_GENERARINGRESOSFUT) de @NAME generarArchivoPlanoIngresosFut
     * para generar el archivo plano en el metodo oprimirImprimir y el
     * metodo oprimirExcel
     */

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de IngresosFutControlador
     */
    public IngresosFutControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        codigoEntidad = SessionUtil.getCompaniaIngreso().getCodigoContaduria();
        try {
            // 1389
            numFormulario = GeneralCodigoFormaEnum.INGRESOS_FUT_CONTROLADOR
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
        // <CARGAR_LISTA>
        cargarListaAnoTrabajo();

        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
        cargarListaMes();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        anoTrabajo = SysmanFunciones.ano(new Date());
        porMes = true;
        visibleMes = true;
        visibleTrimestre = false;
        trimestre = 1;
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Metodo que carga la lista listaAnoTrabajo (CB4517)
     *
     * 
     */
    public void cargarListaAnoTrabajo() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            listaAnoTrabajo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            IngresosFutControladorUrlEnum.URL0001
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * 
     * Metodo que carga la lista listaMes (CB4530)
     */
    public void cargarListaMes() {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(),
                        SysmanFunciones.ano(new Date()));
        try {
            listaMes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            IngresosFutControladorUrlEnum.URL0002
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
     * 
     * Metodo ejecutado al oprimir el botón Imprimir en la vista que
     * genera el archivo plano a traves del llamado al metodo
     * reportesFut del JsfUtil
     *
     *
     */
    public void oprimirImprimir() {
        GenerarConsulta(ReportesBean.FORMATOS.TXT);
    }

    /**
     * 
     * Metodo ejecutado al oprimir el botón Imprimir en la vista que
     * genera el archivo plano a traves del llamado al metodo
     * reportesFut del JsfUtil
     *
     */
    public void oprimirExcel() {
        GenerarConsulta(ReportesBean.FORMATOS.EXCEL);

    }

    /**
     * permite crear los reportes desde el modelo estandar de
     * consultas
     * 
     * @param formato
     */
    private void GenerarConsulta(ReportesBean.FORMATOS formato) {
        validarMeses();
        archivoDescarga = null;
        HashMap<String, Object> reemplazar = new HashMap<>();
        try {
            String parametro = ejbSysmanUtil.consultarParametro(
                            compania,
                            "DIGITO REDONDEO DE INFORMES FUT",
                            SessionUtil.getModulo(),
                            new Date(), true);

            parametro = SysmanFunciones.validarVariableVacio(parametro)
                ? "0"
                : parametro;
            reemplazar.put("redondeo", parametro);
        }
        catch (SystemException e1) {
            logger.error(e1.getMessage(), e1);
            JsfUtil.agregarMensajeError(e1.getMessage());
        }
        reemplazar.put("aniotrabajo", anoTrabajo);
        reemplazar.put("mesfinal", mesFinal);
        reemplazar.put("mesinicial", mesInicial);
        reemplazar.put("mesanterior", mesInicial - 1);
        reemplazar.put("enmiles", pesos);

        try {
            archivoDescarga = JsfUtil.reportesFut(
                            transferencia ? "800188InformeFUTTransferencias"
                                : "800187InformeFUTIngresos",
                            reemplazar,
                            GenerarEncabezado(), formato,
                            transferencia ? "TRANSFERENCIAS_RECIBIDAS"
                                : "INGRESOS",
                            SessionUtil.getModulo(), !transferencia);
        }
        catch (JRException | IOException | SQLException | DRException
                        | SysmanException | ParseException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * genera los encabezados para el reporte de FUT
     * 
     * @return
     * @throws ParseException
     */
    private String GenerarEncabezado() throws ParseException {
        String periodo = "";
        String separadorColumnas = "\t";
        switch (trimestre) {
        case 1:
            periodo = "0103";
            break;
        case 2:
            periodo = "0406";
            break;
        case 3:
            periodo = "0709";
            break;
        case 4:
            periodo = "1012";
            break;
        }
        return SysmanFunciones.concatenar("S",
                        separadorColumnas,
                        codigoEntidad,
                        separadorColumnas, "1", periodo,
                        separadorColumnas,
                        Integer.toString(anoTrabajo),
                        separadorColumnas,
                        transferencia ? "TRANSFERENCIAS_RECIBIDAS"
                            : "REPORTE_INFORMACION",
                        separadorColumnas,
                        SysmanFunciones.convertirAFechaCadena(
                                        new Date(),
                                        "dd-MM-yyyy"));
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton VerificarConfiguracion en
     * la vista
     *
     */
    public void oprimirVerificarConfiguracion() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        ByteArrayInputStream salidaNombreExcel = null;
        ByteArrayInputStream salidaNombreExcel2 = null;
        ByteArrayInputStream[] salida = new ByteArrayInputStream[2];
        String[] nombres = new String[2];
        Map<String, Object> reemplazos = new TreeMap<>();

        reemplazos.put("compania", compania);
        reemplazos.put("anio", anoTrabajo);

        String consulta1 = Reporteador.resuelveConsulta(
                        "800339CONFIGURACION_FUT_INGRESOS",
                        Integer.parseInt(modulo),
                        reemplazos);

        String consulta2 = Reporteador.resuelveConsulta(
                        "800340RUBROS_CONFIGURADOS_FUT_INGRESOS_DIFERENTES",
                        Integer.parseInt(modulo),
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
        if (salidaNombreExcel != null) {
            salida[cantidad] = salidaNombreExcel;
            nombres[cantidad] = "Configuracion Fut Ingresos.xlsx";
            cantidad++;
        }

        if (salidaNombreExcel2 != null) {
            salida[cantidad] = salidaNombreExcel2;
            nombres[cantidad] = "Rubros Configurados Fut Ingresos Diferentes.xlsx";
            cantidad++;
        }
        try {

            if (cantidad > 0) {
                archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(
                                salida,
                                nombres, "VerificarConfiguracion");

            }
            else {
                JsfUtil.agregarMensajeError(idioma.getString(
                                "TG_NO_EXISTE_INFORMACION_CON_LOS_PARAMETROS_SUMINISTRADOS"));
            }

        }
        catch (JRException | IOException | SQLException | DRException e2) {
            logger.error(e2.getMessage(), e2);
            JsfUtil.agregarMensajeError(e2.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado en el metodo cambiarMes para validar el mes o
     * trimestre seleccionados en los combos respectivamente (CB4530)
     * (CB4516)
     */

    public void validarMeses() {

        if (porMes) {
            mesInicial = mes;
            mesFinal = mes;
        }
        else {
            if (trimestre == 1) {
                mesInicial = 1;
                mesFinal = 3;
            }
            else if (trimestre == 2) {
                mesInicial = 4;
                mesFinal = 6;
            }
            else if (trimestre == 3) {
                mesInicial = 7;
                mesFinal = 9;
            }
            else {
                mesInicial = 10;
                mesFinal = 12;
            }
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Trimestre
     * 
     * 
     * 
     */
    public void cambiarTrimestre() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Pesos
     * 
     *
     * 
     */
    public void cambiarPesos() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control Transferencia
     * 
     * 
     */
    public void cambiarTransferencia() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control PorMes
     * 
     * 
     */
    public void cambiarPorMes() {
        // <CODIGO_DESARROLLADO>
        if (porMes) {
            visibleMes = true;
            visibleTrimestre = false;

        }
        else {
            visibleMes = false;
            visibleTrimestre = true;
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable pesos
     * 
     * @return pesos
     */
    public boolean getPesos() {
        return pesos;
    }

    /**
     * Asigna la variable pesos
     * 
     * @param pesos
     * Variable a asignar en pesos
     */
    public void setPesos(boolean pesos) {
        this.pesos = pesos;
    }

    /**
     * Retorna la variable transferencia
     * 
     * @return transferencia
     */
    public boolean getTransferencia() {
        return transferencia;
    }

    /**
     * Asigna la variable transferencia
     * 
     * @param transferencia
     * Variable a asignar en transferencia
     */
    public void setTransferencia(boolean transferencia) {
        this.transferencia = transferencia;
    }

    /**
     * Retorna la variable porMes
     * 
     * @return porMes
     */
    public boolean getPorMes() {
        return porMes;
    }

    /**
     * Asigna la variable porMes
     * 
     * @param porMes
     * Variable a asignar en porMesS
     */
    public void setPorMes(boolean porMes) {
        this.porMes = porMes;
    }

    /**
     * Retorna la variable trimestre
     * 
     * @return trimestre
     */
    public int getTrimestre() {
        return trimestre;
    }

    /**
     * Asigna la variable trimestre
     * 
     * @param trimestre
     * Variable a asignar en trimestre
     */
    public void setTrimestre(int trimestre) {
        this.trimestre = trimestre;
    }

    /**
     * Retorna la variable anoTrabajo
     * 
     * @return anoTrabajo
     */
    public int getAnoTrabajo() {
        return anoTrabajo;
    }

    /**
     * Asigna la variable anoTrabajo
     * 
     * @param anoTrabajo
     * Variable a asignar en anoTrabajo
     */
    public void setAnoTrabajo(int anoTrabajo) {
        this.anoTrabajo = anoTrabajo;
    }

    /**
     * Retorna la variable mes
     * 
     * @return mes
     */
    public int getMes() {
        return mes;
    }

    /**
     * Asigna la variable mes
     * 
     * @param mes
     * Variable a asignar en mes
     */
    public void setMes(int mes) {
        this.mes = mes;
    }

    /**
     * Retorna la variable codigoEntidad
     * 
     * @return codigoEntidad
     */
    public String getCodigoEntidad() {
        return codigoEntidad;
    }

    /**
     * Asigna la variable codigoEntidad
     * 
     * @param codigoEntidad
     * Variable a asignar en codigoEntidad
     */
    public void setCodigoEntidad(String codigoEntidad) {
        this.codigoEntidad = codigoEntidad;
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
     * Retorna la lista listaAnoTrabajo
     * 
     * @return listaAnoTrabajo
     */
    public List<Registro> getListaAnoTrabajo() {
        return listaAnoTrabajo;
    }

    /**
     * Asigna la lista listaAnoTrabajo
     * 
     * @param listaAnoTrabajo
     * Variable a asignar en listaAnoTrabajo
     */
    public void setListaAnoTrabajo(List<Registro> listaAnoTrabajo) {
        this.listaAnoTrabajo = listaAnoTrabajo;
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
     * @return the visible
     */
    public boolean isVisibleMes() {
        return visibleMes;
    }

    /**
     * @param visible
     * the visible to set
     */
    public void setVisibleMes(boolean visibleMes) {
        this.visibleMes = visibleMes;
    }

    /**
     * @return the visibleTrimestre
     */
    public boolean isVisibleTrimestre() {
        return visibleTrimestre;
    }

    /**
     * @param visibleTrimestre
     * the visibleTrimestre to set
     */
    public void setVisibleTrimestre(boolean visibleTrimestre) {
        this.visibleTrimestre = visibleTrimestre;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}

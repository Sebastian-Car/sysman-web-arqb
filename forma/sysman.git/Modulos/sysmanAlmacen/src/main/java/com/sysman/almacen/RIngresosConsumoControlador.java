/*-
 * RingresosConsumo.java
 *
 * 1.0
 * 
 * 04/07/2018
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.almacen;

import com.sysman.almacen.enums.RIngresosConsumoControladorEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Se generan informes de Almacen.
 *
 * @version 1, 04/07/2018 08:25:21 --
 * @author bcardenas Migracion formulario access a web
 * 
 * @version 2 05/07/2018 02:00 PM
 * @author asana Se modifica filtro menu informe
 */
@ManagedBean
@ViewScoped
public class RIngresosConsumoControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */

    /**
     * Variable que almacena la campañia
     */
    private final String compania;
    private final String modulo;
    private final String menuActual = SessionUtil.getMenuActual();

    // <DECLARAR_ATRIBUTOS>

    /**
     * Variable que almacena la fecha inicial
     */
    private Date fechaInicial;

    /**
     * Variable que almacena la fecha final
     */
    private Date fechaFinal;

    /**
     * Variable que almacena el archivo de descarga
     */
    private StreamedContent archivoDescarga;
    private String titulo;
    private String digitosInventario;
    private String tipo;
    private String reporte;
    private String reporteConsulta;
    private String subReporte;
    private String clase;
    private boolean ckDependencia;
    private String nombreResponsable;
    private String cargoResponsable;
    private boolean ocultarDependencia;
    

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>.
    /**
     * Variable que permite acceder a las funciones y procedimientos
     * del paquete PCK_SYSMAN_UTL.
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Crea una nueva instancia de RingresosConsumo
     */
    public RIngresosConsumoControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        ocultarDependencia = true;
        try {
            numFormulario = GeneralCodigoFormaEnum.RINGRESOS_CONSUMO_CONTROLADOR
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
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
        validaropcionMenu();

        try {
            digitosInventario = ejbSysmanUtil.consultarParametro(compania,
                            "DIGITOS AGRUPACION INVENTARIO", modulo, new Date(),
                            false);
            nombreResponsable = ejbSysmanUtil.consultarParametro(compania,
                            "COORDINADOR ALMACEN", modulo, new Date(), false);

            cargoResponsable = ejbSysmanUtil.consultarParametro(compania,
                            "CARGO COORDINADOR ALMACEN", modulo, new Date(),
                            false);

        }
        catch (SystemException e) {

            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las
     * acciones a tener en cuenta en el momento de apertura del
     * formulario
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        fechaInicial = new Date();
        fechaFinal = new Date();
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void validaropcionMenu() {

        switch (menuActual) {
        case "1004020320": // RIC
            titulo = "RELACIÓN DE INGRESOS DE CONSUMO";
            tipo = "C";
            clase = "E";
            break;
        case "1004020321": // REC
            titulo = "RELACIÓN DE EGRESOS DE CONSUMO";
            tipo = "C";
            clase = "S";
            break;
        case "1004020322": // RID
            titulo = "RELACIÓN DE INGRESOS DE DEVOLUTIVOS";
            tipo = "D";
            clase = "E";
            break;
        case "1004020323": // RED
            titulo = "RELACIÓN DE EGRESOS DE DEVOLUTIVOS";
            tipo = "D";
            clase = "S";
            break;
        case "1004020324":
            titulo = "RELACIÓN DE ELEMENTOS RECIBIDOS EN COMODATO";
            tipo = "E";
            clase = "E";
            break;
        case "1004020307":
            titulo = "RELACIÓN DE SALIDAS DEL SERVICIO";
            tipo = "D";
            clase = "E";
            ocultarDependencia = false;
            break;
        case "1004020308":
            titulo = "RELACIÓN DE ENTRADAS AL SERVICIO";
            tipo = "D";
            clase = "S";
            break;

        default:
        }
    }

    public void consultarReporte() {

        if ("1004020308".equals(menuActual)) { // RELACIÓN DE ENTRADAS
                                               // AL SERVICIO
            reporte = "001820CIngrEgrDev";
            reporteConsulta = "001808CIngresosConsumoFinal";
            subReporte = "001821SubCIngresosConsumo";
        }
        else if ("1004020307".equals(menuActual)) { // RELACIÓN DE
                                                    // SALIDAS DEL
                                                    // SERVICIO OK
            reporte = "001820CIngrEgrDev";
            reporteConsulta = "001820CIngrEgrDev";
            subReporte = "001820SubCIngrEgrDev";
        }
        else {
            if (ckDependencia) {
                reporte = "001822CIngresosConsumoDep";
                reporteConsulta = "001822CingresosConsumoFinal";
                subReporte = "001821SubCIngresosConsumo";
            }
            else {
                reporte = "001808CIngresosConsumo";
                reporteConsulta = "001808CIngresosConsumoFinal";
                subReporte = "001821SubCIngresosConsumo";
            }
        }
    }

    public void generarInforme(ReportesBean.FORMATOS formato) {
        try {

            consultarReporte();

            String subconsulta;
            String consulta;

            HashMap<String, Object> reemplazarCons = new HashMap<>();
            reemplazarCons.put("compania", compania);
            reemplazarCons.put("clase", clase);
            reemplazarCons.put("tipo", tipo);
            reemplazarCons.put("fechaInicial", SysmanFunciones
                            .formatearFechaCadena(fechaInicial, "DD/MM/YYYY"));
            reemplazarCons.put("fechaFinal", SysmanFunciones
                            .formatearFechaCadena(fechaFinal, "DD/MM/YYYY"));
            reemplazarCons.put("digitos", digitosInventario);

            subconsulta = Reporteador.resuelveConsulta(subReporte,
                            Integer.parseInt(modulo), reemplazarCons);

            consulta = Reporteador.resuelveConsulta(reporteConsulta,
                            Integer.parseInt(modulo), reemplazarCons);

            Map<String, Object> parametros = new HashMap<>();

            parametros.put(RIngresosConsumoControladorEnum.PR_FECHA_INICIAL
                            .getValue(),
                            SysmanFunciones.convertirAFechaCadena(
                                            fechaInicial));
            parametros.put(RIngresosConsumoControladorEnum.PR_FECHA_FINAL
                            .getValue(),
                            SysmanFunciones.convertirAFechaCadena(fechaFinal));
            parametros.put(RIngresosConsumoControladorEnum.PR_CLASE.getValue(),
                            clase);
            parametros.put(RIngresosConsumoControladorEnum.PR_TITULO.getValue(),
                            titulo);
            parametros.put(RIngresosConsumoControladorEnum.PR_NOMBRE_COORDINADOR
                            .getValue(),
                            nombreResponsable);
            parametros.put(RIngresosConsumoControladorEnum.PR_CARGO.getValue(),
                            cargoResponsable);
            parametros.put(RIngresosConsumoControladorEnum.PR_STRSQL_SUBC_INGRESOSCONSUMO
                            .getValue(),
                            subconsulta);
            parametros.put(RIngresosConsumoControladorEnum.PR_STRSQL.getValue(),
                            consulta);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (JRException | IOException | SysmanException | ParseException e) {
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
    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    /**
     * @return the titulo
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * @param titulo
     * the titulo to set
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * @return the digitosInventario
     */
    public String getDigitosInventario() {
        return digitosInventario;
    }

    /**
     * @param digitosInventario
     * the digitosInventario to set
     */
    public void setDigitosInventario(String digitosInventario) {
        this.digitosInventario = digitosInventario;
    }

    /**
     * @return the texto
     */
    public String getTexto() {
        return tipo;
    }

    /**
     * @param texto
     * the texto to set
     */
    public void setTexto(String texto) {
        this.tipo = texto;
    }

    /**
     * @return the elemento
     */
    public String getElemento() {
        return clase;
    }

    /**
     * @param elemento
     * the elemento to set
     */
    public void setElemento(String elemento) {
        this.clase = elemento;
    }

    public boolean isCkDependencia() {
        return ckDependencia;
    }

    public void setCkDependencia(boolean ckDependencia) {
        this.ckDependencia = ckDependencia;
    }

    public boolean isOcultarDependencia() {
        return ocultarDependencia;
    }

    public void setOcultarDependencia(boolean ocultarDependencia) {
        this.ocultarDependencia = ocultarDependencia;
    }

    
    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}

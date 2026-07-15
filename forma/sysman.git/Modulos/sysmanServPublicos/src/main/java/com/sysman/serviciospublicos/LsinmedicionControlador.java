/*-
 * LsinmedicionControlador.java
 *
 * 1.0
 *
 * 03/10/2016
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
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.serviciospublicos.enums.LsinmedicionControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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

import net.sf.jasperreports.engine.JRException;

/**
 * Clase que sirve de controlador para el formulario Lsinmedicion
 *
 * @version 1.0, 03/10/2016
 * @author NGOMEZ
 * 
 * @author eamaya
 * @version 2.0, 07/06/2017 Proceso de Refactoring y Manejo de EJBSs
 * 
 */
@ManagedBean
@ViewScoped

public class LsinmedicionControlador extends BeanBaseModal {
    /**
     * Constante que especifica el codigo de la compania
     */
    private final String compania;
    /**
     * Constante que especifica el modulo en que se esta trabajando
     */
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que especifica en especifica si selecciono el checkbox
     * Buenos
     */
    private String buenos;
    /**
     * Atributo que especifica en especifica si selecciono el checkbox
     * Averiados
     */
    private String averiados;
    /**
     * Atributo que especifica en especifica si selecciono el checkbox
     * Sin definir
     */
    private String sinDefinir;
    /**
     * Atributo que especifica el ciclo seleccionado mediante el combo
     * Ciclo
     */
    private String ciclo;
    /**
     * Atributo que especifica la opcion seleccionada mediante el
     * combo Filtrar
     */
    private String filtrar;
    /**
     * StreamedContent para descargar los reportes
     */
    private StreamedContent archivoDescarga;
    /**
     * Atributo para poner visible o no los checkbox Medidores
     */
    private boolean medidoresVisible;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Atributo que especifica la lista que se va a cargar en el combo
     * Ciclo
     */
    private List<Registro> listaCiclo;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Constante que representa la cadena true
     */
    private final String trueCons;
    /**
     * Constante que representa la cadena false
     */
    private final String falseCons;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of LsinmedicionControlador
     */
    public LsinmedicionControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        trueCons = "true";
        falseCons = "false";
        try {
            numFormulario = GeneralCodigoFormaEnum.LSINMEDICION_CONTROLADOR
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
     * Metodo init donde se cargan la listaCiclo
     */
    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        cargarListaCiclo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    /**
     * Metodo al abrir el formulario en el cual se dejan no visibles
     * los medidoress
     */
    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        medidoresVisible = false;
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>

    /**
     * Metodo que carga la lista del combo ciclo
     */
    public void cargarListaCiclo() {
        try {

            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LsinmedicionControladorUrlEnum.URL4803
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
     * Metodo que descarga el informe en formato PDF (Boton PDF)
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        genInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que descarga el informe en formato Excel (Boton Excel)
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        genInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>

    /**
     * Metodo que se ejecuta al cambiar el combo filtrar y hace
     * visible los checkcbox de medidores segun la opcion seleccionada
     */
    public void cambiarcmbFiltrar() {
        // <CODIGO_DESARROLLADO>
        medidoresVisible = "1".equals(filtrar);
        buenos = trueCons;
        averiados = falseCons;
        sinDefinir = falseCons;
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta al cambiar el buenos averiados y
     * desactiva los checkbox averiados y sinDefinir
     */
    public void cambiarverBuenos() {
        // <CODIGO_DESARROLLADO>
        if (buenos.equals(trueCons)) {
            averiados = falseCons;
            sinDefinir = falseCons;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta al cambiar el checkbox averiados y
     * desactiva los checkbox buenos y sinDefinir
     */
    public void cambiarverAveriados() {
        // <CODIGO_DESARROLLADO>
        if (averiados.equals(trueCons)) {
            buenos = falseCons;
            sinDefinir = falseCons;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta al cambiar el checkbox sinDefinir y
     * desactiva los checkbox buenos y averiados
     */
    public void cambiarverSindefinir() {
        // <CODIGO_DESARROLLADO>
        if (sinDefinir.equals(trueCons)) {
            buenos = falseCons;
            averiados = falseCons;
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>
    // <SET_GET_ATRIBUTOS>
    public String getBuenos() {
        return buenos;
    }

    public void setBuenos(String buenos) {
        this.buenos = buenos;
    }

    public String getAveriados() {
        return averiados;
    }

    public void setAveriados(String averiados) {
        this.averiados = averiados;
    }

    public String getSinDefinir() {
        return sinDefinir;
    }

    public void setSinDefinir(String sinDefinir) {
        this.sinDefinir = sinDefinir;
    }

    public String getCiclo() {
        return ciclo;
    }

    public void setCiclo(String ciclo) {
        this.ciclo = ciclo;
    }

    public String getFiltrar() {
        return filtrar;
    }

    public void setFiltrar(String filtrar) {
        this.filtrar = filtrar;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public boolean isMedidoresVisible() {
        return medidoresVisible;
    }

    public void setMedidoresVisible(boolean medidoresVisible) {
        this.medidoresVisible = medidoresVisible;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaCiclo() {
        return listaCiclo;
    }

    public void setListaCiclo(List<Registro> listaCiclo) {
        this.listaCiclo = listaCiclo;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

    /**
     * Retorna el nombre que del reporte que se debe generar, segun
     * los parametros FORMATO CALIDAD y MODELO INFORME SIN MEDICION Y
     * MEDIDORES PROBLEMA
     *
     * @return Nombre del reporte
     */
    public String getNameReport() {
        String informeCos = "001122LSinMedicionCOS";
        String informeYop = "001124LSinMedicionYOP";
        String informeBase = "001125LSinMedicion";

        boolean calidad = false;
        String informe = "";
        try {
            calidad = "SI".equals(SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "FORMATO CALIDAD", modulo,
                                            new Date(), false),
                            "NO"));
            informe = SysmanFunciones.nvl(
                            ejbSysmanUtil.consultarParametro(compania,
                                            "MODELO INFORME SIN MEDICION Y MEDIDORES PROBLEMA",
                                            modulo, new Date(), false),
                            "NORMAL").toString();

            return calidad ? informeCos
                : ("YOPAL".equals(informe)
                    ? informeYop
                    : informeBase);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            return informeBase;
        }

    }

    /**
     * Proceso en que se genera el reporte
     *
     * @param formato
     * Formato en que se desea generar el reporte
     *
     */
    public void genInforme(ReportesBean.FORMATOS formato) {
        try {
            boolean bolTodo = "Todos".equals(ciclo);
            boolean totales;
            String condicion;
            String titulo;
            String filtroIndProblema = "";
            String nombrePeriodo = service.buscarEnLista(
                            ciclo,
                            "NUMERO", "NOMPERIODO", listaCiclo);

            String strNombreReporte = getNameReport();

            if ("1".equals(filtrar)) {
                titulo = "Ciclo: " + ciclo + " Periodo: " + nombrePeriodo;
                totales = true;
                if (buenos.equals(trueCons)) {
                    condicion = "AND CASE WHEN (LECTURA1=LECTURA AND CASE WHEN (SP_USUARIO.INDDESHABITADO<>0 AND SP_USUARIO.CHAPETAS<>0) OR (SP_USUARIO.INDDESHABITADO=0 AND SP_USUARIO.CHAPETAS<>0) OR (SP_USUARIO.INDDESHABITADO<>0 AND SP_USUARIO.CHAPETAS=0) THEN -1 ELSE 0 END = -1 AND LECTURA<>0) OR (LECTURA1>LECTURA AND CASE WHEN (SP_MEDIDOR.CODIGO<>' ' AND SP_MEDIDOR.ELEMCAMBIO='Medidor') THEN -1 ELSE 0 END = -1) OR (LECTURA>LECTURA1) THEN -1 ELSE 0 END  = -1";
                }
                else if (averiados.equals(trueCons)) {
                    condicion = "AND CASE WHEN (LECTURA1=LECTURA AND CASE WHEN (SP_USUARIO.INDDESHABITADO<>0 AND SP_USUARIO.CHAPETAS<>0) OR (SP_USUARIO.INDDESHABITADO=0 AND SP_USUARIO.CHAPETAS<>0) OR (SP_USUARIO.INDDESHABITADO<>0 AND SP_USUARIO.CHAPETAS=0) THEN -1 ELSE 0 END = 0 AND LECTURA<>0) OR (LECTURA1>LECTURA AND CASE WHEN (SP_MEDIDOR.CODIGO<>' ' AND SP_MEDIDOR.ELEMCAMBIO='Medidor') THEN -1 ELSE 0 END = 0) THEN -1 ELSE 0 END  = -1";
                }
                else {
                    condicion = "AND (CASE WHEN (LECTURA1=LECTURA AND CASE WHEN (SP_USUARIO.INDDESHABITADO<>0 AND SP_USUARIO.CHAPETAS<>0) OR (SP_USUARIO.INDDESHABITADO=0 AND SP_USUARIO.CHAPETAS<>0) OR (SP_USUARIO.INDDESHABITADO<>0 AND SP_USUARIO.CHAPETAS=0) THEN -1 ELSE 0 END = -1 AND LECTURA<>0) OR (LECTURA1>LECTURA AND CASE WHEN (SP_MEDIDOR.CODIGO<>' ' AND SP_MEDIDOR.ELEMCAMBIO='Medidor') THEN -1 ELSE 0 END = -1) OR (LECTURA>LECTURA1) THEN -1 ELSE 0 END = 0 AND CASE WHEN (LECTURA1=LECTURA AND CASE WHEN (SP_USUARIO.INDDESHABITADO<>0 AND SP_USUARIO.CHAPETAS<>0) OR (SP_USUARIO.INDDESHABITADO=0 AND SP_USUARIO.CHAPETAS<>0) OR (SP_USUARIO.INDDESHABITADO<>0 AND SP_USUARIO.CHAPETAS=0) THEN -1 ELSE 0 END = 0 AND LECTURA<>0) OR (LECTURA1>LECTURA AND CASE WHEN (SP_MEDIDOR.CODIGO<>' ' AND SP_MEDIDOR.ELEMCAMBIO='Medidor') THEN -1 ELSE 0 END = 0) THEN -1 ELSE 0 END  = 0)";
                }
            }
            else if ("2".equals(filtrar)) {
                titulo = "Que tengan registrados problemas de lectura. Ciclo: "
                    + ciclo + " Periodo: " + nombrePeriodo;
                totales = false;
                filtroIndProblema = "AND SP_USUARIO.PROBLEMALECTURA NOT IN (0)";
                condicion = "AND CASE WHEN (LECTURA1=LECTURA AND CASE WHEN (SP_USUARIO.INDDESHABITADO<>0 AND SP_USUARIO.CHAPETAS<>0) OR (SP_USUARIO.INDDESHABITADO=0 AND SP_USUARIO.CHAPETAS<>0) OR (SP_USUARIO.INDDESHABITADO<>0 AND SP_USUARIO.CHAPETAS=0) THEN -1 ELSE 0 END = -1 AND LECTURA<>0) OR (LECTURA1>LECTURA AND CASE WHEN (SP_MEDIDOR.CODIGO<>' ' AND SP_MEDIDOR.ELEMCAMBIO='Medidor') THEN -1 ELSE 0 END = -1) OR (LECTURA>LECTURA1) THEN -1 ELSE 0 END  <> -1";
            }
            else {
                titulo = "Sin problemas de lectura registrados. Ciclo: "
                    + ciclo + " Periodo:  " + nombrePeriodo;
                totales = false;
                filtroIndProblema = "AND SP_USUARIO.PROBLEMALECTURA IN (0)";
                condicion = "AND CASE WHEN (LECTURA1=LECTURA AND CASE WHEN (SP_USUARIO.INDDESHABITADO<>0 AND SP_USUARIO.CHAPETAS<>0) OR (SP_USUARIO.INDDESHABITADO=0 AND SP_USUARIO.CHAPETAS<>0) OR (SP_USUARIO.INDDESHABITADO<>0 AND SP_USUARIO.CHAPETAS=0) THEN -1 ELSE 0 END = -1 AND LECTURA<>0) OR (LECTURA1>LECTURA AND CASE WHEN (SP_MEDIDOR.CODIGO<>' ' AND SP_MEDIDOR.ELEMCAMBIO='Medidor') THEN -1 ELSE 0 END = -1) OR (LECTURA>LECTURA1) THEN -1 ELSE 0 END  = -1";
            }

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("filtroCiclo", bolTodo ? ""
                : ("AND SP_USUARIO.CICLO=" + ciclo));
            reemplazar.put("filtroIndProblema", filtroIndProblema);
            reemplazar.put("condicion", condicion);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_TITULO", titulo);
            parametros.put("PR_TOTALES", totales);
            parametros.put("PR_VIGILADOPOR", SessionUtil.getCompaniaIngreso()
                            .getRutaVigiladoPor());
            Reporteador.resuelveConsulta(strNombreReporte,
                            Integer.parseInt(modulo),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(strNombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

}

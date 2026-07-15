/*-
 * LsubsobrepreciosControlador.java
 *
 * 1.0
 * 
 * 15/12/2016
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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.LsubsobrepreciosControladorEnum;
import com.sysman.serviciospublicos.enums.LsubsobrepreciosControladorUrlEnum;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.RegionUtil;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 * Controlador del formulario LsubsobrepreciosControlador
 *
 * @author cperez
 * @version 1.0, 15/12/2016
 * @modifier amonroy
 * @version 2, 15/05/2017 Proceso de Refactoring e implementaci�n de
 * EJBs
 */
@ManagedBean
@ViewScoped
public class LsubsobrepreciosControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado a la palabra "cicloFinal" en el formulario, almacena el
     * texto cicloFinal
     */
    private final String cCicloFinal;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado a la palabra "codigoInicial" en el formulario, almacena
     * el texto codigoInicial
     */
    private final String cCodigoInicial;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado a la palabra "codigoFinal" en el formulario, almacena
     * el texto codigoFinal
     */
    private final String cCodigoFinal;
    /**
     * Constante definida por el numero de veces que se realiza el
     * llamado a la palabra "CODIGORUTA" en el formulario, almacena el
     * texto CODIGORUTA
     */
    private final String cCodigoRuta;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Obtiene el estado true o false de verSoloResumen que el usuario
     * seleccione
     */
    private boolean verSoloResumen;
    /**
     * Obtiene el estado true o false de verSepararFijo que el usuario
     * seleccione
     */
    private boolean verSepararFijo;
    /**
     * Obtiene el estado true o false de indDane que el usuario
     * seleccione
     */
    private boolean indDane;
    /**
     * Obtiene el estado true o false de totales que el usuario
     * seleccione
     */
    private boolean totales;
    /**
     * Obtiene el estado true o false de vargoFijo que el usuario
     * seleccione
     */
    private boolean vargoFijo;
    /*
     * Obtiene el estado true o false de aseoPorComponentesVisible que
     * el usuario seleccione
     */
    private boolean aseoPorComponentesVisible;
    /*
     * Obtiene el estado true o false de pdfVisible que el usuario
     * seleccione
     */
    private boolean pdfVisible;
    /*
     * Obtiene el estado true o false de aseoPorComponentesVisible que
     * el usuario seleccione
     */
    private boolean excelVisible;
    /*
     * Obtiene el estado true o false de indDaneVisible que el usuario
     * seleccione
     */
    private boolean indDaneVisible;
    /*
     * Constante para el literal "NUMERO"
     */
    private static final String NUMERO = "NUMERO";
    /*
     * Constante para el literal "Hoja1"
     */
    private static final String NOMBREHOJA = "Report";
    /*
     * Constante para el literal "CODIGOINICIAL"
     */
    private static final String CODIGOINICIA = "CODIGOINICIAL";
    /*
     * Constante para el literal "CODIGOFINAL"
     */
    private static final String CODIGOFIN = "CODIGOFINAL";
    /*
     * Constante para el literal "Total"
     */
    private static final String TOTAL = "Total";
    /*
     * Constante para el literal "Subsidio Aseo"
     */
    private static final String SUBSIDIOASEO = "Subsidio Aseo";
    /*
     * Constante para el literal "SUBTOTAL"
     */
    private static final String SUBTOTAL = "SUBTOTAL";
    /*
     * Constalte para el literal "Sobrep. Consumo "
     */
    private static final String SOBREPCONSUMO = "Sobrep. Consumo ";
    /*
     * Costante para le literal "Sobrep. Fijo "
     */
    private static final String SOBREPFIJO = "Sobrep. Fijo ";
    /*
     * Constante para el literal "Consumo"
     */
    private static final String CONSUMO = "Consumo";
    /*
     * Constante para el literal "Subsidio fijo "
     */
    private static final String SUBSIDIOFIJO = "Subsidio fijo ";
    /*
     * Constante para el literal "SINMEDICION"
     */
    private final String cSinMedicion;
    /*
     * Constalte para el literal "Total Sobrep. "
     */
    private static final String TOTALSOBREP = "Total Sobrep. ";
    /*
     * Constante para el literal LSUBSOBREPRECIOSRESUMEN
     */
    private static final String LSUBSOBREPRECIOSRESUMEN = "001308LSubSobrePreiciosResumen";
    /*
     * Constante para el literal
     * "001321LSubSobreDiscResumenConDaneResumen"
     */
    private static final String LSUBSOBREDISCRESUMENCONDANE = "001321LSubSobreDiscResumenConDaneResumen";
    /*
     * Constante para el literal "LSubSobreDiscResumen"
     */
    private static final String LSUBSOBREDISCRESUMEN = "001328LSubSobreDiscResumenPrecio";
    /*
     * Constante para el literal "001332LSubSobreResumenConDanePrecio"
     */
    private static final String LSUBSOBRERESUMENCONDANEPRECIO = "001332LSubSobreResumenConDanePrecio";
    /*
     * Constante para el literal "LSubSobreCargofijoDiscResumen"
     */
    private static final String LSUBSOBRECARGOFIJODISCRESUMEN = "001335LSubSobreCargofijoDiscResumenPrecio";
    /*
     * Constante para el literal "001340LSubSobreResumenTotalesPrecio"
     */
    private static final String LSUBSOBRERESUMENTOTALES = "001340LSubSobreResumenTotalesPrecio";
    /*
     * Constante para el literal "LSubSobreDiscResumen_dos"
     */
    private static final String LSUBSOBREDISCRESUMENDOS = "001343LSubSobreDiscResumenDosPrecio";
    /*
     * Constante para el literal
     * "INFORME DE SUBSIDIOS CON SOLO INDICADOR"
     */
    private static final String INFORMEDESUBSIDIOSCONSOLOINDICADOR = "INFORME DE SUBSIDIOS CON SOLO INDICADOR";
    /*
     * constante para el literal
     * "NOMBRE SERVICIO A REMPLAZAR ACUEDUCTO"
     */
    private static final String NOMBRESERVICIOAREMPLAZARACUEDUCTO = "NOMBRE SERVICIO A REMPLAZAR ACUEDUCTO";
    /*
     * Constante para el literal
     * "NOMBRE SERVICIO A REMPLAZAR ALCANTARILLADO"
     */
    private static final String NOMBRESERVICIOAREMPLAZARALCANTARILLADO = "NOMBRE SERVICIO A REMPLAZAR ALCANTARILLADO";
    /*
     * Constanate para el literal "Sobrep. Aseo"
     */
    private static final String SOBREPASEO = "Sobrep. Aseo";
    /*
     * Constante para el literal "Sobrep. "
     */
    private static final String SOBREP = "Sobrep. ";
    /*
     * Constante para el literal "Subsidio Consumo "
     */
    private static final String SUBSIDIOCONSUMO = "Subsidio Consumo ";
    /*
     * Constante para el literal "Subsidio "
     */
    private static final String SUBSIDIO = "Subsidio ";
    /*
     * Constante para el literal "CAMBIAR NOMBRE SERVICIO ACUEDUCTO"
     */
    private static final String CAMBIARNOMBRESERVICIOACUEDUCTO = "CAMBIAR NOMBRE SERVICIO ACUEDUCTO";
    /*
     * Constante para el literal "Fijo"
     */
    private static final String FIJO = "Fijo";
    /*
     * Constante para el literal "Total Subsidio "
     */
    private static final String TOTALSUBSIDIO = "Total Subsidio ";
    /*
     * Constante para el literal
     * "CAMBIAR NOMBRE SERVICIO ALCANTARILLADO"
     */
    private static final String CAMBIARNOMBRESERVICIOALCANTARILLADO = "CAMBIAR NOMBRE SERVICIO ALCANTARILLADO";
    /*
     * Constante para el literal "CAMBIAR NOMBRE SERVICIO ASEO"
     */
    private static final String CAMBIARNOMBRESERVICIOASEO = "CAMBIAR NOMBRE SERVICIO ASEO";
    /*
     * Constante para el literal "NOMBRE SERVICIO A REMPLAZAR ASEO"
     */
    private static final String NOMBRESERVICIOAREMPLAZARASEO = "NOMBRE SERVICIO A REMPLAZAR ASEO";
    /**
     * Obtiene el estado true o false de verAseoComponentes que el
     * usuario seleccione
     */
    private boolean verAseoComponentes;
    /**
     * Obtiene el dato de la fila que seleccione el usuario en el
     * combo ciclo Inicial
     */
    private String cicloInicial;
    /**
     * Obtiene el dato de la fila que seleccione el usuario en el
     * combo ciclo final
     */
    private String cicloFinal;
    /**
     * Obtiene el dato de la fila que seleccione el usuario en el
     * combo ciclo inicial
     */
    private String codigoInicial;
    /**
     * Obtiene el dato de la fila que seleccione el usuario en el
     * combo ciclo final
     */
    private String codigoFinal;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /*
     * Constante para el literal de la private String etiqueta112
     */
    private String etiqueta112;
    /*
     * Constante para el literal de la private String etiqueta128
     */
    private String etiqueta128;
    /*
     * Constante para el literal de la private String etiqueta126
     */
    private String etiqueta126;
    /*
     * Constante para el literal de la private String etiqueta129
     */
    private String etiqueta129;
    /*
     * Constante para el literal de la private String etiqueta127
     */
    private String etiqueta127;
    /*
     * Constante para el literal de la private String etiqueta130
     */
    private String etiqueta130;
    /*
     * Constante para el literal de la private String etiquetaUno212
     */
    /*
     * LSubSobreDiscResumenConDane
     */
    private String etiquetaUno212;
    /*
     * Constante para el literal de la private String etiquetaUno220
     */
    private String etiquetaUno220;
    /*
     * Constante para el literal de la private String etiquetaUno112
     */
    private String etiquetaUno112;
    /*
     * Constante para el literal de la private String etiquetaUno244
     */
    private String etiquetaUno244;
    /*
     * Constante para el literal de la private String etiquetaUno252
     */
    private String etiquetaUno252;
    /*
     * Constante para el literal de la private String etiquetaUno128
     */
    private String etiquetaUno128;
    /*
     * Constante para el literal de la private String etiquetaUno228
     */
    private String etiquetaUno228;
    /*
     * Constante para el literal de la private String etiquetaUno236
     */
    private String etiquetaUno236;
    /*
     * Constante para el literal de la private String etiquetaUno126
     */
    private String etiquetaUno126;
    /*
     * Constante para el literal de la private String etiquetaUno260
     */
    private String etiquetaUno260;
    /*
     * Constante para el literal de la private String etiquetaUno261
     */
    private String etiquetaUno261;
    /*
     * Constante para el literal de la private String etiquetaUno129
     */
    private String etiquetaUno129;
    /*
     * Constante para el literal de la private String etiquetaUno127
     */
    private String etiquetaUno127;
    /*
     * Constante para el literal de la private String etiquetaUno130
     */
    private String etiquetaUno130;
    /*
     * Constante para el literal de la private String etiquetaDos212
     */
    // LSubSobreDiscResumen
    private String etiquetaDos212;
    /*
     * Constante para el literal de la private String etiquetaDos220
     */
    private String etiquetaDos220;
    /*
     * Constante para el literal de la private String etiquetaDos112
     */
    private String etiquetaDos112;
    /*
     * Constante para el literal de la private String etiquetaDos244
     */
    private String etiquetaDos244;
    /*
     * Constante para el literal de la private String etiquetaDos252
     */
    private String etiquetaDos252;
    /*
     * Constante para el literal de la private String etiquetaDos128
     */
    private String etiquetaDos128;
    /*
     * Constante para el literal de la private String etiquetaDos228
     */
    private String etiquetaDos228;
    /*
     * Constante para el literal de la private String etiquetaDos236
     */
    private String etiquetaDos236;
    /*
     * Constante para el literal de la private String etiquetaDos126
     */
    private String etiquetaDos126;
    /*
     * Constante para el literal de la private String etiquetaDos260
     */
    private String etiquetaDos260;
    /*
     * Constante para el literal de la private String etiquetaDos261
     */
    private String etiquetaDos261;
    /*
     * Constante para el literal de la private String etiquetaDos129
     */
    private String etiquetaDos129;
    /*
     * Constante para el literal de la private String etiquetaDos127
     */
    private String etiquetaDos127;
    /*
     * Constante para el literal de la private String etiquetaDos130
     */
    private String etiquetaDos130;
    /*
     * Constante para el literal de la private String nombreServicio
     */
    private String nombreServicio;
    /**
     * Implementacion del EJB de SysmanUtil para hacer el llamado a la
     * funcion FC_PAR
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    /**
     * Lista los datos de los combos ciclo Inicial
     */
    private RegistroDataModelImpl listaCicloInicial;
    /**
     * Lista los datos de los combos ciclo Final
     */
    private RegistroDataModelImpl listaCicloFinal;
    /**
     * Listado de registros para seleccionar el codigo inicial
     */
    private RegistroDataModelImpl listaCodigoInicial;
    /**
     * Listado de registros para seleccionar el codigo inicial
     */
    private RegistroDataModelImpl listaCodigoFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de LsubsobrepreciosControlador
     */
    public LsubsobrepreciosControlador() {
        super();
        compania = SessionUtil.getCompania();
        cCicloFinal = "cicloFinal";
        cCodigoInicial = "codigoInicial";
        cCodigoFinal = "codigoFinal";
        cSinMedicion = idioma.getString("TB_TB3325");
        cCodigoRuta = "CODIGORUTA";
        try {
            numFormulario = GeneralCodigoFormaEnum.LSUBSOBREPRECIOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
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
        cargarListaCicloInicial();
        cargarListaCicloFinal();
        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
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
        verSoloResumen = false;
        verSepararFijo = false;
        pdfVisible = true;
        totales = false;
        vargoFijo = false;
        if ("SI".equals(parametro("MANEJA CODIGO DANE PARA INFORMES", true))) {
            aseoPorComponentesVisible = false;
            indDaneVisible = true;
        }
        else {
            aseoPorComponentesVisible = false;
            indDane = false;
        }
        etiqueta112 = "Subsidio Acued.";
        etiqueta128 = "Sobrep. acued.";
        etiqueta126 = "Subsidio Alcant.";
        etiqueta129 = "Sobrep. Alcant.";
        etiqueta127 = SUBSIDIOASEO;
        etiqueta130 = SOBREPASEO;
        // LSubSobreDiscResumenConDane
        etiquetaUno212 = FIJO;
        etiquetaUno220 = CONSUMO;
        etiquetaUno112 = TOTAL;
        etiquetaUno244 = FIJO;
        etiquetaUno252 = CONSUMO;
        etiquetaUno128 = TOTAL;
        etiquetaUno228 = FIJO;
        etiquetaUno236 = CONSUMO;
        etiquetaUno126 = TOTAL;
        etiquetaUno260 = FIJO;
        etiquetaUno261 = CONSUMO;
        etiquetaUno129 = TOTAL;
        etiquetaUno127 = SUBSIDIOASEO;
        etiquetaUno130 = SOBREPASEO;
        // LSubSobreDiscResumen
        etiquetaDos212 = FIJO;
        etiquetaDos220 = CONSUMO;
        etiquetaDos112 = TOTAL;
        etiquetaDos244 = FIJO;
        etiquetaDos252 = CONSUMO;
        etiquetaDos128 = TOTAL;
        etiquetaDos228 = FIJO;
        etiquetaDos236 = CONSUMO;
        etiquetaDos126 = TOTAL;
        etiquetaDos260 = FIJO;
        etiquetaDos261 = CONSUMO;
        etiquetaDos129 = TOTAL;
        etiquetaDos127 = "Subsidio Aseo";
        etiquetaDos130 = SOBREPASEO;
        nombreServicio = null;
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * Carga la lista listaCicloInicial
     */
    public void cargarListaCicloInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LsubsobrepreciosControladorUrlEnum.URL18392
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCicloInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, NUMERO);
    }

    /**
     * Carga la lista listaCicloFinal
     */
    public void cargarListaCicloFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LsubsobrepreciosControladorUrlEnum.URL19055
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LsubsobrepreciosControladorEnum.PARAM0.getValue(),
                        cicloInicial);

        listaCicloFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, NUMERO);
    }

    /**
     * 
     * Carga la lista listaCodigoInicial
     *
     */
    public void cargarListaCodigoInicial() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LsubsobrepreciosControladorUrlEnum.URL002
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LsubsobrepreciosControladorEnum.CICLOINICIAL.getValue(),
                        cicloInicial);
        param.put(LsubsobrepreciosControladorEnum.CICLOFINAL.getValue(),
                        cicloFinal);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigoRuta);

    }

    /**
     * 
     * Carga la lista listaCodigoFinal
     *
     */
    public void cargarListaCodigoFinal() {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        LsubsobrepreciosControladorUrlEnum.URL003
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(LsubsobrepreciosControladorEnum.CICLOINICIAL.getValue(),
                        cicloInicial);
        param.put(LsubsobrepreciosControladorEnum.CICLOFINAL.getValue(),
                        cicloFinal);
        param.put(LsubsobrepreciosControladorEnum.CODIGOINICIAL.getValue(),
                        codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigoRuta);
    }

    /**
     * Carga la lista cargarListaSqlCompania
     */
    public String cargarListaSqlCompania() {
        String nombreCompania;
        Map<String, Object> params = new TreeMap<>();
        params.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        Registro reg = null;
        try {
            reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            LsubsobrepreciosControladorUrlEnum.URL001
                                                                            .getValue())
                                            .getUrl(), params));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        nombreCompania = reg != null ? reg.getCampos().get("NOMBRE").toString()
            : "";
        return nombreCompania;
    }

    /**
     * Carga la lista cargarListaSqlExcel
     * 
     * Obtiene la sentencia SQL para generar el informe en excel desde
     * una consulta almacenada en Base de Datos
     */
    public String cargarListaSqlExcel() {
        String parametro = parametro(INFORMEDESUBSIDIOSCONSOLOINDICADOR, true)
                        .toUpperCase();

        HashMap<String, Object> reemplazar = new HashMap<>();
        reemplazar.put("cicloInicial", cicloInicial);
        reemplazar.put(cCicloFinal, cicloFinal);
        reemplazar.put(cCodigoInicial, codigoInicial);
        reemplazar.put(cCodigoFinal, codigoFinal);
        reemplazar.put("parametro", parametro);

        return Reporteador.resuelveConsulta(
                        "800115ResumenSubSobreprecios",
                        Integer.parseInt(SessionUtil.getModulo()), reemplazar);
    }

    /**
     * Para saber el valor del parametro necesitado
     * 
     * @return valor_del_parametro
     */
    public String parametro(String nombre, boolean indicadorMayuscula) {
        // <CODIGO_DESARROLLADO>
        String valor = null;
        try {
            valor = ejbSysmanUtil.consultarParametro(compania,
                            nombre,
                            SessionUtil.getModulo(),
                            new Date(),
                            indicadorMayuscula);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        if (valor == null) {
            valor = "";
        }
        // </CODIGO_DESARROLLADO>
        return valor;
    }

    /*
     * me transforma el valor del boobleno en true o false segun la
     * necesidad
     * 
     * @return SI � NO
     */
    public String bolean(boolean dato) {
        String datoEnvio;
        if (dato) {
            datoEnvio = "SI";
        }
        else {
            datoEnvio = "NO";
        }
        return datoEnvio;
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /*
     * Metodo ejecutado al hacer clic el boton Excel
     */
    public void pdfClic() {

        if ("SI".equalsIgnoreCase(
                        parametro("MANEJA CODIGO DANE PARA INFORMES", true))) {
            if (!verSepararFijo && !indDane) {
                genInforme(FORMATOS.PDF, LSUBSOBREPRECIOSRESUMEN);
            }
            if (verSepararFijo && indDane) {
                genInforme(FORMATOS.PDF, LSUBSOBREDISCRESUMENCONDANE);
            }
            if (verSepararFijo && !indDane) {
                genInforme(FORMATOS.PDF, LSUBSOBREDISCRESUMEN);
            }
            if (!verSepararFijo && indDane) {
                genInforme(FORMATOS.PDF, LSUBSOBRERESUMENCONDANEPRECIO);
            }
        }
        else {
            imprimirCargoFijo();
        }
    }

    /*
     * Define el reporte a generar cuando el parametro
     * "MANEJA CODIGO DANE PARA INFORMES" posee el valor de NO
     * 
     */
    public void imprimirCargoFijo() {
        if (vargoFijo) {
            genInforme(FORMATOS.PDF, LSUBSOBRECARGOFIJODISCRESUMEN);
        }

        else if (verSepararFijo) {
            genInforme(FORMATOS.PDF, LSUBSOBREDISCRESUMENDOS);
        }
        else {
            if (totales) {
                genInforme(FORMATOS.PDF, LSUBSOBRERESUMENTOTALES);
            }
            else {
                genInforme(FORMATOS.PDF, LSUBSOBREPRECIOSRESUMEN);
            }
        }
    }

    /*
     * cambia el nombre de las etiquetas
     */
    public void lSubResumenPrecio(String reporte) {
        if (LSUBSOBREPRECIOSRESUMEN.equals(reporte)
            && "SI".equalsIgnoreCase(
                            parametro(CAMBIARNOMBRESERVICIOACUEDUCTO, true))) {
            nombreServicio = parametro(NOMBRESERVICIOAREMPLAZARACUEDUCTO,
                            false);
            etiqueta112 = SOBREP + nombreServicio;
            etiqueta128 = SUBSIDIO + nombreServicio;
        }
        if (LSUBSOBREPRECIOSRESUMEN.equals(reporte)
            && "SI".equalsIgnoreCase(parametro(
                            CAMBIARNOMBRESERVICIOALCANTARILLADO, true))) {
            nombreServicio = parametro(NOMBRESERVICIOAREMPLAZARALCANTARILLADO,
                            false);
            etiqueta126 = SUBSIDIO + nombreServicio;
            etiqueta129 = SOBREP + nombreServicio;
        }
        if (LSUBSOBREPRECIOSRESUMEN.equals(reporte)
            && "SI".equalsIgnoreCase(
                            parametro(CAMBIARNOMBRESERVICIOASEO, true))) {
            nombreServicio = parametro(NOMBRESERVICIOAREMPLAZARASEO, false);
            etiqueta127 = SUBSIDIO + nombreServicio;
            etiqueta130 = SOBREP + nombreServicio;
        }
    }

    /*
     * cambia el nombre de las etiquetas LSUBSOBRERESUMENTOTALES
     */
    public void lSubSobreResumenTotales(String reporte) {
        if (LSUBSOBRERESUMENTOTALES.equals(reporte)
            && "SI".equals(parametro(CAMBIARNOMBRESERVICIOACUEDUCTO, true))) {
            nombreServicio = parametro(NOMBRESERVICIOAREMPLAZARACUEDUCTO,
                            false);
            etiqueta112 = SOBREP + nombreServicio;
            etiqueta128 = SUBSIDIO + nombreServicio;
        }
        if (LSUBSOBRERESUMENTOTALES.equals(reporte)
            && "SI".equals(parametro(CAMBIARNOMBRESERVICIOALCANTARILLADO,
                            true))) {
            nombreServicio = parametro(NOMBRESERVICIOAREMPLAZARALCANTARILLADO,
                            false);
            etiqueta126 = SUBSIDIO + nombreServicio;
            etiqueta129 = SOBREP + nombreServicio;
        }
        if (LSUBSOBRERESUMENTOTALES.equals(reporte)
            && "SI".equals(parametro(CAMBIARNOMBRESERVICIOASEO, true))) {
            nombreServicio = parametro(NOMBRESERVICIOAREMPLAZARASEO, false);
            etiqueta127 = SUBSIDIO + nombreServicio;
            etiqueta130 = SOBREP + nombreServicio;
        }
    }

    /*
     * Cambia el nombre de las etiquetas
     */
    public void lSubResumenPrecioConDane(String reporte) {
        if (LSUBSOBRERESUMENCONDANEPRECIO.equals(reporte)
            && "SI".equals(parametro(CAMBIARNOMBRESERVICIOACUEDUCTO, true))) {
            nombreServicio = parametro(NOMBRESERVICIOAREMPLAZARACUEDUCTO,
                            false);
            etiqueta112 = SOBREP + nombreServicio;
            etiqueta128 = SUBSIDIO + nombreServicio;
        }
        if (LSUBSOBRERESUMENCONDANEPRECIO.equals(reporte)
            && "SI".equals(parametro(CAMBIARNOMBRESERVICIOALCANTARILLADO,
                            true))) {
            nombreServicio = parametro(NOMBRESERVICIOAREMPLAZARALCANTARILLADO,
                            false);
            etiqueta126 = SUBSIDIO + nombreServicio;
            etiqueta129 = SOBREP + nombreServicio;
        }
        if (LSUBSOBRERESUMENCONDANEPRECIO.equals(reporte)
            && "SI".equals(parametro(CAMBIARNOMBRESERVICIOASEO, true))) {
            nombreServicio = parametro(NOMBRESERVICIOAREMPLAZARASEO, false);
            etiqueta127 = SUBSIDIO + nombreServicio;
            etiqueta130 = SOBREP + nombreServicio;
        }
    }

    /*
     * cambia el nombre de las etiquetas
     */
    public void lSubResumenDisConDane(String reporte) {
        if (LSUBSOBREDISCRESUMENCONDANE.equals(reporte)
            && "SI".equals(parametro(CAMBIARNOMBRESERVICIOACUEDUCTO, true))) {
            nombreServicio = parametro(NOMBRESERVICIOAREMPLAZARACUEDUCTO,
                            false);
            etiquetaUno212 = SUBSIDIOFIJO + nombreServicio;
            etiquetaUno220 = SUBSIDIOCONSUMO + nombreServicio;
            etiquetaUno112 = TOTALSUBSIDIO + nombreServicio;
            etiquetaUno244 = SOBREPFIJO + nombreServicio;
            etiquetaUno252 = SOBREPCONSUMO + nombreServicio;
            etiquetaUno128 = TOTALSOBREP + nombreServicio;
        }
        if (LSUBSOBREDISCRESUMENCONDANE.equals(reporte)
            && "SI".equals(parametro(CAMBIARNOMBRESERVICIOALCANTARILLADO,
                            true))) {
            nombreServicio = parametro(NOMBRESERVICIOAREMPLAZARALCANTARILLADO,
                            false);
            etiquetaUno228 = SUBSIDIOFIJO + nombreServicio;
            etiquetaUno236 = SUBSIDIOCONSUMO + nombreServicio;
            etiquetaUno126 = TOTALSUBSIDIO + nombreServicio;
            etiquetaUno260 = SOBREPFIJO + nombreServicio;
            etiquetaUno261 = SOBREPCONSUMO + nombreServicio;
            etiquetaUno129 = TOTALSOBREP + nombreServicio;
        }
        if (LSUBSOBREDISCRESUMENCONDANE.equals(reporte)
            && "SI".equals(parametro(CAMBIARNOMBRESERVICIOASEO, true))) {
            nombreServicio = parametro(NOMBRESERVICIOAREMPLAZARASEO, false);
            etiquetaUno127 = SUBSIDIO + nombreServicio;
            etiquetaUno130 = SOBREP + nombreServicio;
        }
    }

    /*
     * cambia el nombre de las etiquetas
     */
    public void lSubResumenDisResumen(String reporte) {
        if (LSUBSOBREDISCRESUMEN.equals(reporte)
            && "SI".equals(parametro(CAMBIARNOMBRESERVICIOACUEDUCTO, true))) {
            nombreServicio = parametro(NOMBRESERVICIOAREMPLAZARACUEDUCTO,
                            false);
            etiquetaDos212 = SUBSIDIOFIJO + nombreServicio;
            etiquetaDos220 = SUBSIDIOCONSUMO + nombreServicio;
            etiquetaDos112 = TOTALSUBSIDIO + nombreServicio;
            etiquetaDos244 = SOBREPFIJO + nombreServicio;
            etiquetaDos252 = SOBREPCONSUMO + nombreServicio;
            etiquetaDos128 = TOTALSOBREP + nombreServicio;
        }
        if (LSUBSOBREDISCRESUMEN.equals(reporte)
            && "SI".equals(parametro(CAMBIARNOMBRESERVICIOALCANTARILLADO,
                            true))) {
            nombreServicio = parametro(NOMBRESERVICIOAREMPLAZARALCANTARILLADO,
                            false);
            etiquetaDos228 = SUBSIDIOFIJO + nombreServicio;
            etiquetaDos236 = SUBSIDIOCONSUMO + nombreServicio;
            etiquetaDos126 = TOTALSUBSIDIO + nombreServicio;
            etiquetaDos260 = SOBREPFIJO + nombreServicio;
            etiquetaDos261 = SOBREPCONSUMO + nombreServicio;
            etiquetaDos129 = TOTALSOBREP + nombreServicio;
        }
        if (LSUBSOBREDISCRESUMEN.equals(reporte)
            && "SI".equals(parametro(CAMBIARNOMBRESERVICIOASEO, true))) {
            nombreServicio = parametro(NOMBRESERVICIOAREMPLAZARASEO, false);
            etiquetaDos127 = "Subsidio " + nombreServicio;
            etiquetaDos130 = SOBREP + nombreServicio;
        }
    }

    /**
     * Genera el reporte de Excel y de Pdf
     */
    public void genInforme(ReportesBean.FORMATOS formato, String reporte) {

        try {
            archivoDescarga = null;
            // LSubSobreResumen
            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();
            reemplazar.put("cicloIncial", cicloInicial);
            reemplazar.put(cCicloFinal, cicloFinal);
            reemplazar.put(cCodigoInicial, codigoInicial);
            reemplazar.put(cCodigoFinal, codigoFinal);
            parametros.put("PR_FORMS_LSUBSOBRE_CICLO", cicloInicial);
            parametros.put("PR_FORMS_LSUBSOBRE_CMCICLOF", cicloFinal);
            parametros.put("PR_NOMBRECOMPANIA", cargarListaSqlCompania());
            parametros.put("PR_CARGO_FIRMA_INFORME_SUBSIDIOS",
                            parametro("CARGO FIRMA INFORME SUBSIDIOS", false));
            parametros.put("PR_NOMBRE_FIRMA_INFORME_SUBSIDIOS",
                            parametro("NOMBRE FIRMA INFORME SUBSIDIOS", false));
            parametros.put("PR_NOMBRE_GERENTE",
                            parametro("NOMBRE GERENTE", false));
            lSubResumenPrecio(reporte);
            lSubResumenPrecioConDane(reporte);
            lSubSobreResumenTotales(reporte);
            parametros.put("PR_ETIQUETA112", etiqueta112);
            parametros.put("PR_ETIQUETA128", etiqueta128);
            parametros.put("PR_ETIQUETA126", etiqueta126);
            parametros.put("PR_ETIQUETA129", etiqueta129);
            parametros.put("PR_ETIQUETA127", etiqueta127);
            parametros.put("PR_ETIQUETA130", etiqueta130);
            lSubResumenDisConDane(reporte);
            parametros.put("PR_ETIQUETAUNO212", etiquetaUno212);
            parametros.put("PR_ETIQUETAUNO220", etiquetaUno220);
            parametros.put("PR_ETIQUETAUNO112", etiquetaUno112);
            parametros.put("PR_ETIQUETAUNO244", etiquetaUno244);
            parametros.put("PR_ETIQUETAUNO252", etiquetaUno252);
            parametros.put("PR_ETIQUETAUNO128", etiquetaUno128);
            parametros.put("PR_ETIQUETAUNO228", etiquetaUno228);
            parametros.put("PR_ETIQUETAUNO236", etiquetaUno236);
            parametros.put("PR_ETIQUETAUNO126", etiquetaUno126);
            parametros.put("PR_ETIQUETAUNO260", etiquetaUno260);
            parametros.put("PR_ETIQUETAUNO261", etiquetaUno261);
            parametros.put("PR_ETIQUETAUNO129", etiquetaUno129);
            parametros.put("PR_ETIQUETAUNO127", etiquetaUno127);
            parametros.put("PR_ETIQUETAUNO130", etiquetaUno130);
            lSubResumenDisResumen(reporte);
            parametros.put("PR_ETIQUETADOS212", etiquetaDos212);
            parametros.put("PR_ETIQUETADOS220", etiquetaDos220);
            parametros.put("PR_ETIQUETADOS112", etiquetaDos112);
            parametros.put("PR_ETIQUETADOS244", etiquetaDos244);
            parametros.put("PR_ETIQUETADOS252", etiquetaDos252);
            parametros.put("PR_ETIQUETADOS128", etiquetaDos128);
            parametros.put("PR_ETIQUETADOS228", etiquetaDos228);
            parametros.put("PR_ETIQUETADOS236", etiquetaDos236);
            parametros.put("PR_ETIQUETADOS126", etiquetaDos126);
            parametros.put("PR_ETIQUETADOS260", etiquetaDos260);
            parametros.put("PR_ETIQUETADOS261", etiquetaDos261);
            parametros.put("PR_ETIQUETADOS129", etiquetaDos129);
            parametros.put("PR_ETIQUETADOS127", etiquetaDos127);
            parametros.put("PR_ETIQUETADOS130", etiquetaDos130);
            String condicionParametroSubLSus;
            String parCalcSuspen;
            if ("SI".equals(parametro(INFORMEDESUBSIDIOSCONSOLOINDICADOR,
                            true))) {
                condicionParametroSubLSus = "";

            }
            else {
                condicionParametroSubLSus = "AND (SP_USUARIO.SUBSIDIO NOT IN(0) OR SP_USUARIO.SOBREPRECIO NOT IN(0)) ";
            }
            if ("SI".equals(parametro("PERMITE CALCULO SUSPENDIDOS", true))) {
                parCalcSuspen = " AND SP_USUARIO.ESTADO NOT IN ('R') ";
            }
            else {
                parCalcSuspen = " AND SP_USUARIO.ESTADO NOT IN ('R','S') ";
            }
            reemplazar.put("condicionParametroSubLSus",
                            condicionParametroSubLSus);
            reemplazar.put("condicionParametroCalcSuspen", parCalcSuspen);
            if (LSUBSOBREDISCRESUMENDOS.equals(reporte)) {
                HashMap<String, Object> reemplazardos = new HashMap<>();
                Map<String, Object> parametrosdos = new HashMap<>();
                reemplazardos.put("cicloIncial", cicloInicial);
                reemplazardos.put(cCicloFinal, cicloFinal);
                reemplazardos.put(cCodigoInicial, codigoInicial);
                reemplazardos.put(cCodigoFinal, codigoFinal);
                parametrosdos.put("PR_FORMS_LSUBSOBRE_CICLO", cicloInicial);
                parametrosdos.put("PR_FORMS_LSUBSOBRE_CMCICLOF", cicloFinal);
                parametrosdos.put("PR_NOMBRECOMPANIA",
                                cargarListaSqlCompania());
                parametrosdos.put("PR_CARGO_FIRMA_INFORME_SUBSIDIOS",
                                parametro("CARGO FIRMA INFORME SUBSIDIOS",
                                                true));
                parametrosdos.put("PR_NOMBRE_FIRMA_INFORME_SUBSIDIOS",
                                parametro("NOMBRE FIRMA INFORME SUBSIDIOS",
                                                false));
                parametrosdos.put("PR_NOMBRE_GERENTE",
                                parametro("NOMBRE GERENTE", false));
                lSubResumenPrecio(reporte);
                lSubResumenPrecioConDane(reporte);
                lSubSobreResumenTotales(reporte);
                parametrosdos.put("PR_ETIQUETA112", etiqueta112);
                parametrosdos.put("PR_ETIQUETA128", etiqueta128);
                parametrosdos.put("PR_ETIQUETA126", etiqueta126);
                parametrosdos.put("PR_ETIQUETA129", etiqueta129);
                parametrosdos.put("PR_ETIQUETA127", etiqueta127);
                parametrosdos.put("PR_ETIQUETA130", etiqueta130);
                lSubResumenDisConDane(reporte);
                parametrosdos.put("PR_ETIQUETAUNO212", etiquetaUno212);
                parametrosdos.put("PR_ETIQUETAUNO220", etiquetaUno220);
                parametrosdos.put("PR_ETIQUETAUNO112", etiquetaUno112);
                parametrosdos.put("PR_ETIQUETAUNO244", etiquetaUno244);
                parametrosdos.put("PR_ETIQUETAUNO252", etiquetaUno252);
                parametrosdos.put("PR_ETIQUETAUNO128", etiquetaUno128);
                parametrosdos.put("PR_ETIQUETAUNO228", etiquetaUno228);
                parametrosdos.put("PR_ETIQUETAUNO236", etiquetaUno236);
                parametrosdos.put("PR_ETIQUETAUNO126", etiquetaUno126);
                parametrosdos.put("PR_ETIQUETAUNO260", etiquetaUno260);
                parametrosdos.put("PR_ETIQUETAUNO261", etiquetaUno261);
                parametrosdos.put("PR_ETIQUETAUNO129", etiquetaUno129);
                parametrosdos.put("PR_ETIQUETAUNO127", etiquetaUno127);
                parametrosdos.put("PR_ETIQUETAUNO130", etiquetaUno130);
                lSubResumenDisResumen(reporte);
                parametrosdos.put("PR_ETIQUETADOS212", etiquetaDos212);
                parametrosdos.put("PR_ETIQUETADOS220", etiquetaDos220);
                parametrosdos.put("PR_ETIQUETADOS112", etiquetaDos112);
                parametrosdos.put("PR_ETIQUETADOS244", etiquetaDos244);
                parametrosdos.put("PR_ETIQUETADOS252", etiquetaDos252);
                parametrosdos.put("PR_ETIQUETADOS128", etiquetaDos128);
                parametrosdos.put("PR_ETIQUETADOS228", etiquetaDos228);
                parametrosdos.put("PR_ETIQUETADOS236", etiquetaDos236);
                parametrosdos.put("PR_ETIQUETADOS126", etiquetaDos126);
                parametrosdos.put("PR_ETIQUETADOS260", etiquetaDos260);
                parametrosdos.put("PR_ETIQUETADOS261", etiquetaDos261);
                parametrosdos.put("PR_ETIQUETADOS129", etiquetaDos129);
                parametrosdos.put("PR_ETIQUETADOS127", etiquetaDos127);
                parametrosdos.put("PR_ETIQUETADOS130", etiquetaDos130);
                reemplazardos.put("condicionParametroSubLSus",
                                condicionParametroSubLSus);
                reemplazardos.put("condicionParametroCalcSuspen",
                                parCalcSuspen);
                Reporteador.resuelveConsulta(LSUBSOBREDISCRESUMEN,
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazar, parametros);
                Reporteador.resuelveConsulta(LSUBSOBREDISCRESUMENDOS,
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazardos, parametrosdos);
                ByteArrayInputStream[] salidas = new ByteArrayInputStream[2];
                salidas[0] = JsfUtil.serializarReporte(LSUBSOBREDISCRESUMENDOS,
                                parametrosdos,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
                salidas[1] = JsfUtil.serializarReporte(LSUBSOBREDISCRESUMEN,
                                parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
                String[] nombresArchivos = new String[2];
                if (formato == ReportesBean.FORMATOS.PDF) {
                    nombresArchivos[0] = LSUBSOBREDISCRESUMENDOS + ".pdf";
                    nombresArchivos[1] = LSUBSOBREDISCRESUMEN + ".pdf";
                }
                archivoDescarga = JsfUtil.exportarComprimidoGeneralStreamed(
                                salidas, nombresArchivos);
            }
            else {
                Reporteador.resuelveConsulta(reporte,
                                Integer.parseInt(SessionUtil.getModulo()),
                                reemplazar, parametros);

                archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                                ConectorPool.ESQUEMA_SYSMAN, formato);
            }

        }
        catch (JRException | IOException | SysmanException | SQLException
                        | DRException | OutOfMemoryError e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * Metodo ejecutado al oprimir el boton Excel en la vista
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        long cicloIni = Long.parseLong(cicloInicial);
        long cicloFin = Long.parseLong(cicloFinal);
        if (cicloIni > cicloFin) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1823"));
        }
        else {
            excelClic();
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al oprimir el boton PDF en la vista
     */
    public void oprimirPDF() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        long cicloIni = Long.parseLong(cicloInicial);
        long cicloFin = Long.parseLong(cicloFinal);
        if (cicloIni > cicloFin) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1823"));
        }
        else {
            pdfClic();
        }
        // </CODIGO_DESARROLLADO>
    }

    /*
     * metodo para dividir y agregar el subtotal del excel
     */
    public void dividirExcel(Sheet sheet, CellStyle style, CellStyle styleNum) {
        // Decide which rows to process
        int rowStart = 6;
        for (int rowNum = rowStart; rowNum < sheet.getLastRowNum(); rowNum++) {
            Row r = sheet.getRow(rowNum);
            Row r2 = sheet.getRow(rowNum + 1);
            if (r.getCell(1, Row.RETURN_BLANK_AS_NULL) == null
                || r2.getCell(1, Row.RETURN_BLANK_AS_NULL) == null) {
                // The spreadsheet is empty in this cell
            }
            else {
                Cell c = r.getCell(1, Row.RETURN_BLANK_AS_NULL);
                Cell c2 = r2.getCell(1, Row.RETURN_BLANK_AS_NULL);
                String a = c.getStringCellValue() == null ? "XX"
                    : c.getStringCellValue();
                String b = c2.getStringCellValue() == null ? "XX"
                    : c2.getStringCellValue();
                if (!a.equals(b)) {
                    sheet.shiftRows(rowNum + 1, sheet.getLastRowNum(), 1);
                    // hoo
                    Cell cell = sheet.getRow(rowNum + 1).createCell(1);
                    cell.setCellValue(SUBTOTAL);
                    cell.setCellStyle(style);

                    crearSubTotales(sheet, rowNum, rowStart, styleNum);
                    rowStart = rowNum + 2;
                    rowNum++; // Nota importante:no se puede quitar
                    // por que se estan creando filas al
                    // momento de generar el informe si se
                    // quita el el numero de filas seria
                    // infinito
                }
            }
        }
    }

    /*
     * crea el los subtotales del excel
     */
    public void crearSubTotales(Sheet sheet, int rowNum, int rowStart,
        CellStyle styleNum) {
        for (int j = 2; j <= 31; j++) {
            Cell cellTotalesFormula = sheet.getRow(rowNum + 1)
                            .createCell(j);
            cellTotalesFormula.setCellType(Cell.CELL_TYPE_FORMULA);
            CellReference cellRefIni = new CellReference(rowStart, j);
            CellReference cellRefFin = new CellReference(rowNum, j);
            String celdaIni = cellRefIni.formatAsString();
            String celdaFin = cellRefFin.formatAsString();
            cellTotalesFormula.setCellFormula(
                            "SUM(" + celdaIni + ":" + celdaFin + ")");
            cellTotalesFormula.setCellStyle(styleNum);

        }
    }

    /*
     * genera el excel al hacer clic
     */
    public void excelClic() {
        // <CODIGO_DESARROLLADO>
        String strSql = cargarListaSqlExcel();
        try {
            Workbook workbook = new HSSFWorkbook(
                            JsfUtil.exportarHojaDatosStreamed(strSql,
                                            ConectorPool.ESQUEMA_SYSMAN,
                                            FORMATOS.EXCEL97).getStream());
            Sheet sheet = workbook.getSheet(NOMBREHOJA);
            sheet.shiftRows(0, sheet.getLastRowNum(), 5);
            CellReference cellRefIniTitulo = new CellReference(0, 0);
            String celdaIniTitulo = cellRefIniTitulo.formatAsString();
            CellReference cellRefFinTitulo = new CellReference(0,
                            Math.max(sheet.getRow(5).getLastCellNum(), 0)
                                - 1);
            String celdaFinTitulo = cellRefFinTitulo.formatAsString();
            CellRangeAddress region = CellRangeAddress.valueOf(
                            "" + celdaIniTitulo + ":" + celdaFinTitulo);
            sheet.addMergedRegion(region);

            CellStyle styledos = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setFontName("Calibri");
            font.setBold(true);
            font.setFontHeightInPoints((short) 11);
            styledos.setFont(font);
            styledos.setAlignment(CellStyle.ALIGN_CENTER);
            Cell cell = sheet.getRow(0).createCell(0);
            cell.setCellValue(cargarListaSqlCompania());
            cell.setCellStyle(styledos);

            // titulo 2
            CellReference cellRefIniTituloDos = new CellReference(1, 0);
            String celdaIniTituloDos = cellRefIniTituloDos.formatAsString();
            CellReference cellRefFinTituloDos = new CellReference(1,
                            Math.max(sheet.getRow(5).getLastCellNum(), 0) - 1);
            String celdaFinTituloDos = cellRefFinTituloDos.formatAsString();
            CellRangeAddress regionDos = CellRangeAddress.valueOf(
                            "" + celdaIniTituloDos + ":" + celdaFinTituloDos);
            sheet.addMergedRegion(regionDos);
            Cell cellDos = sheet.getRow(1).createCell(0);
            cellDos.setCellValue("Resumen Subsidios y sobreprecios");
            cellDos.setCellStyle(styledos);

            // titulo 3
            CellReference cellRefIniTituloTres = new CellReference(2, 0);
            String celdaIniTituloTres = cellRefIniTituloTres.formatAsString();
            CellReference cellRefFinTituloTres = new CellReference(2,
                            Math.max(sheet.getRow(5).getLastCellNum(), 0) - 1);
            String celdaFinTituloTres = cellRefFinTituloTres.formatAsString();
            CellRangeAddress regionTres = CellRangeAddress.valueOf(
                            "" + celdaIniTituloTres + ":" + celdaFinTituloTres);
            sheet.addMergedRegion(regionTres);
            Cell cellTres = sheet.getRow(2).createCell(0);
            cellTres.setCellValue(
                            "Del ciclo " + cicloInicial + " al " + cicloFinal);
            cellTres.setCellStyle(styledos);
            // css consulta
            CellStyle style = workbook.createCellStyle();
            style.setBorderTop(CellStyle.BORDER_THIN);
            style.setTopBorderColor(IndexedColors.BLACK.getIndex());
            style.setBorderLeft(CellStyle.BORDER_THIN);
            style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
            style.setBorderRight(CellStyle.BORDER_THIN);
            style.setRightBorderColor(IndexedColors.BLACK.getIndex());
            style.setAlignment(CellStyle.ALIGN_RIGHT);
            style.setBorderBottom(CellStyle.BORDER_THIN);
            style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
            style.setFont(font);
            style.setAlignment(CellStyle.ALIGN_CENTER);
            // cssconsulta
            // titulo4--Estrato

            CellRangeAddress regionCuatro = CellRangeAddress.valueOf("A5:A6");
            sheet.addMergedRegion(regionCuatro);
            Cell cellCuatro = sheet.createRow(4).createCell(0);
            cellCuatro.setCellValue("Estrato");
            final short borderMediumDashed = CellStyle.BORDER_THIN;
            RegionUtil.setBorderBottom(borderMediumDashed,
                            regionCuatro, sheet, workbook);
            RegionUtil.setBorderTop(borderMediumDashed,
                            regionCuatro, sheet, workbook);
            RegionUtil.setBorderLeft(borderMediumDashed,
                            regionCuatro, sheet, workbook);
            RegionUtil.setBorderRight(borderMediumDashed,
                            regionCuatro, sheet, workbook);
            RegionUtil.setBottomBorderColor(IndexedColors.BLACK.getIndex(),
                            regionCuatro, sheet, workbook);
            RegionUtil.setTopBorderColor(IndexedColors.BLACK.getIndex(),
                            regionCuatro, sheet, workbook);
            RegionUtil.setLeftBorderColor(IndexedColors.BLACK.getIndex(),
                            regionCuatro, sheet, workbook);
            RegionUtil.setRightBorderColor(IndexedColors.BLACK.getIndex(),
                            regionCuatro, sheet, workbook);
            cellCuatro.setCellStyle(style);

            // titulo5--Uso
            CellRangeAddress regionCinco = CellRangeAddress.valueOf("B5:B6");
            sheet.addMergedRegion(regionCinco);
            Cell cellCinco = sheet.getRow(4).createCell(1);
            cellCinco.setCellValue("Uso");
            RegionUtil.setBorderBottom(borderMediumDashed,
                            regionCinco, sheet, workbook);
            RegionUtil.setBorderTop(borderMediumDashed,
                            regionCinco, sheet, workbook);
            RegionUtil.setBorderLeft(borderMediumDashed,
                            regionCinco, sheet, workbook);
            RegionUtil.setBorderRight(borderMediumDashed,
                            regionCinco, sheet, workbook);
            RegionUtil.setBottomBorderColor(IndexedColors.BLACK.getIndex(),
                            regionCinco, sheet, workbook);
            RegionUtil.setTopBorderColor(IndexedColors.BLACK.getIndex(),
                            regionCinco, sheet, workbook);
            RegionUtil.setLeftBorderColor(IndexedColors.BLACK.getIndex(),
                            regionCinco, sheet, workbook);
            RegionUtil.setRightBorderColor(IndexedColors.BLACK.getIndex(),
                            regionCinco, sheet, workbook);
            cellCinco.setCellStyle(style);

            // titulo6--Subsidio Acueducto
            CellRangeAddress regionSeis = CellRangeAddress.valueOf("C5:F5");
            sheet.addMergedRegion(regionSeis);
            Cell cellSeis = sheet.getRow(4).createCell(2);
            cellSeis.setCellValue("Subsidio Acueducto");
            RegionUtil.setBorderBottom(borderMediumDashed,
                            regionSeis, sheet, workbook);
            RegionUtil.setBorderTop(borderMediumDashed,
                            regionSeis, sheet, workbook);
            RegionUtil.setBorderLeft(borderMediumDashed,
                            regionSeis, sheet, workbook);
            RegionUtil.setBorderRight(borderMediumDashed,
                            regionSeis, sheet, workbook);
            RegionUtil.setBottomBorderColor(IndexedColors.BLACK.getIndex(),
                            regionSeis, sheet, workbook);
            RegionUtil.setTopBorderColor(IndexedColors.BLACK.getIndex(),
                            regionSeis, sheet, workbook);
            RegionUtil.setLeftBorderColor(IndexedColors.BLACK.getIndex(),
                            regionSeis, sheet, workbook);
            RegionUtil.setRightBorderColor(IndexedColors.BLACK.getIndex(),
                            regionSeis, sheet, workbook);
            cellSeis.setCellStyle(style);

            //// titulo6--Subsidio Alcantarillado
            CellRangeAddress regionSiete = CellRangeAddress.valueOf("G5:J5");
            sheet.addMergedRegion(regionSiete);
            Cell cellSiete = sheet.getRow(4).createCell(6);
            cellSiete.setCellValue("Subsidio Alcantarillado");
            RegionUtil.setBorderBottom(borderMediumDashed,
                            regionSiete, sheet, workbook);
            RegionUtil.setBorderTop(borderMediumDashed,
                            regionSiete, sheet, workbook);
            RegionUtil.setBorderLeft(borderMediumDashed,
                            regionSiete, sheet, workbook);
            RegionUtil.setBorderRight(borderMediumDashed,
                            regionSiete, sheet, workbook);
            RegionUtil.setBottomBorderColor(IndexedColors.BLACK.getIndex(),
                            regionSiete, sheet, workbook);
            RegionUtil.setTopBorderColor(IndexedColors.BLACK.getIndex(),
                            regionSiete, sheet, workbook);
            RegionUtil.setLeftBorderColor(IndexedColors.BLACK.getIndex(),
                            regionSiete, sheet, workbook);
            RegionUtil.setRightBorderColor(IndexedColors.BLACK.getIndex(),
                            regionSiete, sheet, workbook);
            cellSiete.setCellStyle(style);

            // titulo6--Subsidio Aseo
            CellRangeAddress regionOcho = CellRangeAddress.valueOf("K5:P5");
            sheet.addMergedRegion(regionOcho);
            Cell cellOcho = sheet.getRow(4).createCell(10);
            cellOcho.setCellValue(SUBSIDIOASEO);
            RegionUtil.setBorderBottom(borderMediumDashed,
                            regionOcho, sheet, workbook);
            RegionUtil.setBorderTop(borderMediumDashed,
                            regionOcho, sheet, workbook);
            RegionUtil.setBorderLeft(borderMediumDashed,
                            regionOcho, sheet, workbook);
            RegionUtil.setBorderRight(borderMediumDashed,
                            regionOcho, sheet, workbook);
            RegionUtil.setBottomBorderColor(IndexedColors.BLACK.getIndex(),
                            regionOcho, sheet, workbook);
            RegionUtil.setTopBorderColor(IndexedColors.BLACK.getIndex(),
                            regionOcho, sheet, workbook);
            RegionUtil.setLeftBorderColor(IndexedColors.BLACK.getIndex(),
                            regionOcho, sheet, workbook);
            RegionUtil.setRightBorderColor(IndexedColors.BLACK.getIndex(),
                            regionOcho, sheet, workbook);
            cellOcho.setCellStyle(style);

            // titulo6--Sobreprecio Acueducto
            CellRangeAddress regioNueve = CellRangeAddress.valueOf("Q5:T5");
            sheet.addMergedRegion(regioNueve);
            Cell cellNueve = sheet.getRow(4).createCell(16);
            cellNueve.setCellValue("Sobreprecio Acueducto");
            RegionUtil.setBorderBottom(borderMediumDashed,
                            regioNueve, sheet, workbook);
            RegionUtil.setBorderTop(borderMediumDashed,
                            regioNueve, sheet, workbook);
            RegionUtil.setBorderLeft(borderMediumDashed,
                            regioNueve, sheet, workbook);
            RegionUtil.setBorderRight(borderMediumDashed,
                            regioNueve, sheet, workbook);
            RegionUtil.setBottomBorderColor(IndexedColors.BLACK.getIndex(),
                            regioNueve, sheet, workbook);
            RegionUtil.setTopBorderColor(IndexedColors.BLACK.getIndex(),
                            regioNueve, sheet, workbook);
            RegionUtil.setLeftBorderColor(IndexedColors.BLACK.getIndex(),
                            regioNueve, sheet, workbook);
            RegionUtil.setRightBorderColor(IndexedColors.BLACK.getIndex(),
                            regioNueve, sheet, workbook);
            cellNueve.setCellStyle(style);

            // titulo6--Sobreprecio Alcantarillado
            CellRangeAddress regioDies = CellRangeAddress.valueOf("U5:X5");
            sheet.addMergedRegion(regioDies);
            Cell cellDies = sheet.getRow(4).createCell(20);
            cellDies.setCellValue("Sobreprecio Alcantarillado");
            RegionUtil.setBorderBottom(borderMediumDashed,
                            regioDies, sheet, workbook);
            RegionUtil.setBorderTop(borderMediumDashed,
                            regioDies, sheet, workbook);
            RegionUtil.setBorderLeft(borderMediumDashed,
                            regioDies, sheet, workbook);
            RegionUtil.setBorderRight(borderMediumDashed,
                            regioDies, sheet, workbook);
            RegionUtil.setBottomBorderColor(IndexedColors.BLACK.getIndex(),
                            regioDies, sheet, workbook);
            RegionUtil.setTopBorderColor(IndexedColors.BLACK.getIndex(),
                            regioDies, sheet, workbook);
            RegionUtil.setLeftBorderColor(IndexedColors.BLACK.getIndex(),
                            regioDies, sheet, workbook);
            RegionUtil.setRightBorderColor(IndexedColors.BLACK.getIndex(),
                            regioDies, sheet, workbook);
            cellDies.setCellStyle(style);

            // titulo6--Sobreprecio Aseo
            CellRangeAddress regioOnce = CellRangeAddress.valueOf("Y5:AD5");
            sheet.addMergedRegion(regioOnce);
            Cell cellOnce = sheet.getRow(4).createCell(24);
            cellOnce.setCellValue("Sobreprecio Aseo");
            RegionUtil.setBorderBottom(borderMediumDashed,
                            regioOnce, sheet, workbook);
            RegionUtil.setBorderTop(borderMediumDashed,
                            regioOnce, sheet, workbook);
            RegionUtil.setBorderLeft(borderMediumDashed,
                            regioOnce, sheet, workbook);
            RegionUtil.setBorderRight(borderMediumDashed,
                            regioOnce, sheet, workbook);
            RegionUtil.setBottomBorderColor(IndexedColors.BLACK.getIndex(),
                            regioOnce, sheet, workbook);
            RegionUtil.setTopBorderColor(IndexedColors.BLACK.getIndex(),
                            regioOnce, sheet, workbook);
            RegionUtil.setLeftBorderColor(IndexedColors.BLACK.getIndex(),
                            regioOnce, sheet, workbook);
            RegionUtil.setRightBorderColor(IndexedColors.BLACK.getIndex(),
                            regioOnce, sheet, workbook);
            cellOnce.setCellStyle(style);

            // titulo6--Total Subsidios
            CellRangeAddress regioDoce = CellRangeAddress.valueOf("AE5:AE6");
            sheet.addMergedRegion(regioDoce);
            Cell cellDoce = sheet.getRow(4).createCell(30);
            cellDoce.setCellValue("Total Subsidios");
            RegionUtil.setBorderBottom(borderMediumDashed,
                            regioDoce, sheet, workbook);
            RegionUtil.setBorderTop(borderMediumDashed,
                            regioDoce, sheet, workbook);
            RegionUtil.setBorderLeft(borderMediumDashed,
                            regioDoce, sheet, workbook);
            RegionUtil.setBorderRight(borderMediumDashed,
                            regioDoce, sheet, workbook);
            RegionUtil.setBottomBorderColor(IndexedColors.BLACK.getIndex(),
                            regioDoce, sheet, workbook);
            RegionUtil.setTopBorderColor(IndexedColors.BLACK.getIndex(),
                            regioDoce, sheet, workbook);
            RegionUtil.setLeftBorderColor(IndexedColors.BLACK.getIndex(),
                            regioDoce, sheet, workbook);
            RegionUtil.setRightBorderColor(IndexedColors.BLACK.getIndex(),
                            regioDoce, sheet, workbook);
            cellDoce.setCellStyle(style);

            // titulo6--Total Sobreprecio
            CellRangeAddress regioTrece = CellRangeAddress.valueOf("AF5:AF6");
            sheet.addMergedRegion(regioTrece);
            Cell cellTrece = sheet.getRow(4).createCell(31);
            RegionUtil.setBorderBottom(borderMediumDashed,
                            regioTrece, sheet, workbook);
            RegionUtil.setBorderTop(borderMediumDashed,
                            regioTrece, sheet, workbook);
            RegionUtil.setBorderLeft(borderMediumDashed,
                            regioTrece, sheet, workbook);
            RegionUtil.setBorderRight(borderMediumDashed,
                            regioTrece, sheet, workbook);
            RegionUtil.setBottomBorderColor(IndexedColors.BLACK.getIndex(),
                            regioTrece, sheet, workbook);
            RegionUtil.setTopBorderColor(IndexedColors.BLACK.getIndex(),
                            regioTrece, sheet, workbook);
            RegionUtil.setLeftBorderColor(IndexedColors.BLACK.getIndex(),
                            regioTrece, sheet, workbook);
            RegionUtil.setRightBorderColor(IndexedColors.BLACK.getIndex(),
                            regioTrece, sheet, workbook);
            cellTrece.setCellStyle(style);
            cellTrece.setCellValue("Total Sobreprecio");
            // titulo6--Fijo
            CellRangeAddress regio14 = CellRangeAddress.valueOf("C6:C6");
            sheet.addMergedRegion(regio14);
            Cell cell14 = sheet.getRow(5).createCell(2);
            cell14.setCellStyle(style);
            cell14.setCellValue("Fijo");
            // titulo6--Consumo
            CellRangeAddress regio15 = CellRangeAddress.valueOf("D6:D6");
            sheet.addMergedRegion(regio15);
            Cell cell15 = sheet.getRow(5).createCell(4);
            cell15.setCellStyle(style);
            cell15.setCellValue(cSinMedicion);
            // titulo6--Sin Medici�n
            CellRangeAddress regioDiesiseis = CellRangeAddress.valueOf("E6:E6");
            sheet.addMergedRegion(regioDiesiseis);
            Cell cellDiesiseis = sheet.getRow(5).createCell(3);
            cellDiesiseis.setCellStyle(style);
            cellDiesiseis.setCellValue(CONSUMO);
            // titulo6--Total
            CellRangeAddress regio17 = CellRangeAddress.valueOf("F6:F6");
            sheet.addMergedRegion(regio17);
            Cell cell17 = sheet.getRow(5).createCell(5);
            cell17.setCellStyle(style);
            cell17.setCellValue(TOTAL);
            // titulo6--Total
            CellRangeAddress regio18 = CellRangeAddress.valueOf("G6:G6");
            sheet.addMergedRegion(regio18);
            Cell cell18 = sheet.getRow(5).createCell(6);
            cell18.setCellStyle(style);
            cell18.setCellValue("Fijo");
            // titulo6--Consumo
            CellRangeAddress regio19 = CellRangeAddress.valueOf("H6:H6");
            sheet.addMergedRegion(regio19);
            Cell cell19 = sheet.getRow(5).createCell(7);
            cell19.setCellStyle(style);
            cell19.setCellValue(CONSUMO);
            // titulo6--Sin Medici�n
            CellRangeAddress regio20 = CellRangeAddress.valueOf("I6:I6");
            sheet.addMergedRegion(regio20);
            Cell cell20 = sheet.getRow(5).createCell(8);
            cell20.setCellStyle(style);
            cell20.setCellValue(cSinMedicion);
            // titulo6--Total
            CellRangeAddress regio21 = CellRangeAddress.valueOf("J6:J6");
            sheet.addMergedRegion(regio21);
            Cell cell21 = sheet.getRow(5).createCell(9);
            cell21.setCellStyle(style);
            cell21.setCellValue(TOTAL);
            // titulo6--Aseo
            CellRangeAddress regio22 = CellRangeAddress.valueOf("K6:K6");
            sheet.addMergedRegion(regio22);
            Cell cell22 = sheet.getRow(5).createCell(10);
            cell22.setCellStyle(style);
            cell22.setCellValue("Aseo");
            // titulo6--Barrido y Limpieza
            CellRangeAddress regio23 = CellRangeAddress.valueOf("L6:L6");
            sheet.addMergedRegion(regio23);
            Cell cell23 = sheet.getRow(5).createCell(11);
            cell23.setCellStyle(style);
            cell23.setCellValue("Barrido y Limpieza");
            // titulo6--Tratamiento y Disposicion Final
            CellRangeAddress regio24 = CellRangeAddress.valueOf("M6:M6");
            sheet.addMergedRegion(regio24);
            Cell cell24 = sheet.getRow(5).createCell(12);
            cell24.setCellStyle(style);
            cell24.setCellValue("Tratamiento y Disposicion Final");
            // titulo6--Comercio y manejo del recaudo
            CellRangeAddress regio25 = CellRangeAddress.valueOf("N6:N6");
            sheet.addMergedRegion(regio25);
            Cell cell25 = sheet.getRow(5).createCell(13);
            cell25.setCellStyle(style);
            cell25.setCellValue("Comercio y manejo del recaudo");
            // titulo6--Recoleccion y transorte
            CellRangeAddress regio26 = CellRangeAddress.valueOf("O6:O6");
            sheet.addMergedRegion(regio26);
            Cell cell26 = sheet.getRow(5).createCell(14);
            cell26.setCellStyle(style);
            cell26.setCellValue("Recoleccion y transporte");
            // titulo6--Tramo exedente
            CellRangeAddress regio27 = CellRangeAddress.valueOf("P6:P6");
            sheet.addMergedRegion(regio27);
            Cell cell27 = sheet.getRow(5).createCell(15);
            cell27.setCellStyle(style);
            cell27.setCellValue("Tramo excedente");
            // titulo6--Tramo exedente
            CellRangeAddress regio28 = CellRangeAddress.valueOf("Q6:Q6");
            sheet.addMergedRegion(regio28);
            Cell cell28 = sheet.getRow(5).createCell(16);
            cell28.setCellStyle(style);
            cell28.setCellValue("Fijo");
            // titulo6--Consumo
            CellRangeAddress regio29 = CellRangeAddress.valueOf("R6:R6");
            sheet.addMergedRegion(regio29);
            Cell cell29 = sheet.getRow(5).createCell(17);
            cell29.setCellStyle(style);
            cell29.setCellValue("Consumo");
            // titulo6--Consumo
            CellRangeAddress regio30 = CellRangeAddress.valueOf("S6:S6");
            sheet.addMergedRegion(regio30);
            Cell cell30 = sheet.getRow(5).createCell(18);
            cell30.setCellStyle(style);
            cell30.setCellValue(cSinMedicion);
            // titulo6--Total
            CellRangeAddress regio31 = CellRangeAddress.valueOf("T6:T6");
            sheet.addMergedRegion(regio31);
            Cell cell31 = sheet.getRow(5).createCell(19);
            cell31.setCellStyle(style);
            cell31.setCellValue("Total");
            // titulo6--Fijo
            CellRangeAddress regio32 = CellRangeAddress.valueOf("U6:U6");
            sheet.addMergedRegion(regio32);
            Cell cell32 = sheet.getRow(5).createCell(20);
            cell32.setCellStyle(style);
            cell32.setCellValue("Fijo");
            // titulo6--Consumo
            CellRangeAddress regio33 = CellRangeAddress.valueOf("V6:V6");
            sheet.addMergedRegion(regio33);
            Cell cell33 = sheet.getRow(5).createCell(21);
            cell33.setCellStyle(style);
            cell33.setCellValue(CONSUMO);
            // titulo6--Sin Medici�n
            CellRangeAddress regio34 = CellRangeAddress.valueOf("W6:W6");
            sheet.addMergedRegion(regio34);
            Cell cell34 = sheet.getRow(5).createCell(22);
            cell34.setCellStyle(style);
            cell34.setCellValue(cSinMedicion);
            // titulo6--Total
            CellRangeAddress regio35 = CellRangeAddress.valueOf("X6:X6");
            sheet.addMergedRegion(regio35);
            Cell cell35 = sheet.getRow(5).createCell(23);
            cell35.setCellStyle(style);
            cell35.setCellValue(TOTAL);
            // titulo6--Aseo
            CellRangeAddress regio36 = CellRangeAddress.valueOf("Y6:Y6");
            sheet.addMergedRegion(regio36);
            Cell cell36 = sheet.getRow(5).createCell(24);
            cell36.setCellStyle(style);
            cell36.setCellValue("Aseo");
            // titulo6--Barrido y Limpieza
            CellRangeAddress regio37 = CellRangeAddress.valueOf("Z6:Z6");
            sheet.addMergedRegion(regio37);
            Cell cell37 = sheet.getRow(5).createCell(25);
            cell37.setCellStyle(style);
            cell37.setCellValue("Barrido y Limpieza");
            // titulo6--Tratamiento y Disposicion Final
            CellRangeAddress regio38 = CellRangeAddress.valueOf("AA6:AA6");
            sheet.addMergedRegion(regio38);
            Cell cell38 = sheet.getRow(5).createCell(26);
            cell38.setCellStyle(style);
            cell38.setCellValue("Tratamiento y Disposicion Final");
            // titulo6--Comercio y manejo del recaudo
            CellRangeAddress regio39 = CellRangeAddress.valueOf("AB6:AB6");
            sheet.addMergedRegion(regio39);
            Cell cell39 = sheet.getRow(5).createCell(27);
            cell39.setCellStyle(style);
            cell39.setCellValue("Comercio y manejo del recaudo");
            // titulo6--Recoleccion y transorte
            CellRangeAddress regio40 = CellRangeAddress.valueOf("AC6:AC6");
            sheet.addMergedRegion(regio40);
            Cell cell40 = sheet.getRow(5).createCell(28);
            cell40.setCellStyle(style);
            cell40.setCellValue("Recoleccion y transporte");
            // titulo6--Tramo exedente
            CellRangeAddress regio41 = CellRangeAddress.valueOf("AD6:AD6");
            sheet.addMergedRegion(regio41);
            Cell cell41 = sheet.getRow(5).createCell(29);
            cell41.setCellStyle(style);
            cell41.setCellValue("Tramo exedente");

            CellStyle styleNum = workbook.createCellStyle();
            styleNum.setDataFormat(workbook.createDataFormat()
                            .getFormat(" #,##0.00"));
            styleNum.setBorderTop(CellStyle.BORDER_THIN);
            styleNum.setTopBorderColor(IndexedColors.BLACK.getIndex());
            styleNum.setBorderLeft(CellStyle.BORDER_THIN);
            styleNum.setLeftBorderColor(IndexedColors.BLACK.getIndex());
            styleNum.setBorderRight(CellStyle.BORDER_THIN);
            styleNum.setRightBorderColor(IndexedColors.BLACK.getIndex());
            styleNum.setAlignment(CellStyle.ALIGN_RIGHT);
            styleNum.setBorderBottom(CellStyle.BORDER_THIN);
            styleNum.setBottomBorderColor(IndexedColors.BLACK.getIndex());
            styleNum.setFont(font);

            dividirExcel(sheet, style, styleNum);
            // dividirTotalExcel(sheet,style)
            // hoja crea
            // hoja crea

            workbook.setForceFormulaRecalculation(true);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            out.close();
            archivoDescarga = JsfUtil.getArchivoDescarga(
                            new ByteArrayInputStream(out.toByteArray()),
                            "Resumen Subsidios y Sobreprecios.xls");
        }
        // </CODIGO_DESARROLLADO>
        catch (SysmanException | JRException | IOException | SQLException
                        | DRException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /*
     * Metodo ejecutado al cambiar el control VerAseoComponentes
     */
    public void cambiarVerAseoComponentes() {
        if (verAseoComponentes) {
            pdfVisible = false;
            excelVisible = true;
        }
        else {
            pdfVisible = true;
            excelVisible = false;
        }

    }

    /**
     * Metodo ejecutado al cambiar el control VerSepararFijo
     * 
     */
    public void cambiarVerSepararFijo() {
        // <CODIGO_DESARROLLADO>
        vargoFijo = false;
        totales = false;
        indDane = false;
        verAseoComponentes = false;
        if (verSepararFijo) {
            aseoPorComponentesVisible = true;
        }
        else {
            aseoPorComponentesVisible = false;
        }
        if ("SI".equals(bolean(verAseoComponentes))) {
            pdfVisible = false;
            excelVisible = true;
        }
        else {
            pdfVisible = true;
            excelVisible = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control CargoFijo
     * 
     */
    public void cambiarCargoFijo() {
        // <CODIGO_DESARROLLADO>
        verSepararFijo = false;
        totales = false;
        verAseoComponentes = false;
        aseoPorComponentesVisible = false;
        if ("SI".equals(bolean(verAseoComponentes))) {
            pdfVisible = false;
            excelVisible = true;
        }
        else {
            pdfVisible = true;
            excelVisible = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo ejecutado al cambiar el control totales
     * 
     */
    public void cambiartotales() {
        // <CODIGO_DESARROLLADO>
        vargoFijo = false;
        totales = false;
        indDane = false;
        verSepararFijo = false;
        verAseoComponentes = false;
        aseoPorComponentesVisible = false;
        if ("SI".equals(bolean(verAseoComponentes))) {
            pdfVisible = false;
            excelVisible = true;
        }
        else {
            pdfVisible = true;
            excelVisible = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCicloInicial
     * 
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCicloInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cicloInicial = registroAux.getCampos().get(NUMERO).toString();
        codigoInicial = registroAux.getCampos().get(CODIGOINICIA) == null ? ""
            : registroAux.getCampos().get(CODIGOINICIA).toString();
        codigoFinal = registroAux.getCampos().get(CODIGOFIN) == null ? ""
            : registroAux.getCampos().get(CODIGOFIN).toString();
        cicloFinal = registroAux.getCampos().get(NUMERO).toString();
        cargarListaCicloFinal();
        cargarListaCodigoInicial();
        cargarListaCicloFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos().get(cCodigoRuta).toString();
        codigoFinal = null;
        cargarListaCodigoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCodigoFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos().get(cCodigoRuta).toString();

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCicloFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCicloFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        cicloFinal = registroAux.getCampos().get(NUMERO).toString();
        codigoFinal = registroAux.getCampos().get(CODIGOFIN) == null ? ""
            : registroAux.getCampos().get(CODIGOFIN).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable pdfVisible
     * 
     * @return pdfVisible
     */
    public boolean isPdfVisible() {
        return pdfVisible;
    }

    /**
     * Asigna la variable excelVisible
     * 
     * @param pdfVisible
     * Variable a asignar en pdfVisible
     */
    public void setPdfVisible(boolean pdfVisible) {
        this.pdfVisible = pdfVisible;
    }

    /**
     * Retorna la variable verSoloResumen
     * 
     * @return verSoloResumen
     */
    public boolean isVerSoloResumen() {
        return verSoloResumen;
    }

    /**
     * Retorna la variable verSoloResumen
     * 
     * @return verSoloResumen
     */
    public boolean isExcelVisible() {
        return excelVisible;
    }

    /**
     * Asigna la variable excelVisible
     * 
     * @param excelVisible
     * Variable a asignar en excelVisible
     */
    public void setExcelVisible(boolean excelVisible) {
        this.excelVisible = excelVisible;
    }

    /**
     * Retorna la variable isIndDaneVisible
     * 
     * @return isIndDaneVisible
     */
    public boolean isIndDaneVisible() {
        return indDaneVisible;
    }

    /**
     * Asigna la variable setIndDaneVisible
     * 
     * @param isAseoPorComponentesVisible
     * Variable a asignar en setIndDaneVisible
     */
    public void setIndDaneVisible(boolean indDaneVisible) {
        this.indDaneVisible = indDaneVisible;
    }

    /**
     * Retorna la variable aseoPorComponentesVisible
     * 
     * @return aseoPorComponentesVisible
     */
    public boolean isAseoPorComponentesVisible() {
        return aseoPorComponentesVisible;
    }

    /**
     * Asigna la variable isAseoPorComponentesVisible
     * 
     * @param isAseoPorComponentesVisible
     * Variable a asignar en isAseoPorComponentesVisible
     */
    public void setAseoPorComponentesVisible(
        boolean aseoPorComponentesVisible) {
        this.aseoPorComponentesVisible = aseoPorComponentesVisible;
    }

    /**
     * Asigna la variable verSoloResumen
     * 
     * @param verSoloResumen
     * Variable a asignar en verSoloResumen
     */
    public void setVerSoloResumen(boolean verSoloResumen) {
        this.verSoloResumen = verSoloResumen;
    }

    /**
     * Retorna la variable codigoInicial
     * 
     * @return codigoInicial
     */
    public String getCodigoInicial() {
        return codigoInicial;
    }

    /**
     * Asigna la variable codigoInicial
     * 
     * @param codigoInicial
     * Variable a asignar en codigoInicial
     */
    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    /**
     * Retorna la variable codigoFinal
     * 
     * @return codigoFinal
     */
    public String getCodigoFinal() {
        return codigoFinal;
    }

    /**
     * Asigna la variable codigoFinal
     * 
     * @param codigoFinal
     * Variable a asignar en codigoFinal
     */
    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    /**
     * Retorna la variable verSepararFijo
     * 
     * @return verSepararFijo
     */
    public boolean isVerSepararFijo() {
        return verSepararFijo;
    }

    /**
     * Asigna la variable verSepararFijo
     * 
     * @param verSepararFijo
     * Variable a asignar en verSepararFijo
     */
    public void setVerSepararFijo(boolean verSepararFijo) {
        this.verSepararFijo = verSepararFijo;
    }

    /**
     * Retorna la variable indDane
     * 
     * @return indDane
     */
    public boolean isIndDane() {
        return indDane;
    }

    /**
     * Asigna la variable indDane
     * 
     * @param indDane
     * Variable a asignar en indDane
     */
    public void setIndDane(boolean indDane) {
        this.indDane = indDane;
    }

    /**
     * Retorna la variable totales
     * 
     * @return totales
     */
    public boolean isTotales() {
        return totales;
    }

    /**
     * Asigna la variable totales
     * 
     * @param totales
     * Variable a asignar en totales
     */
    public void setTotales(boolean totales) {
        this.totales = totales;
    }

    /**
     * Retorna la variable vargoFijo
     * 
     * @return vargoFijo
     */
    public boolean isVargoFijo() {
        return vargoFijo;
    }

    /**
     * Asigna la variable vargoFijo
     * 
     * @param vargoFijo
     * Variable a asignar en vargoFijo
     */
    public void setVargoFijo(boolean vargoFijo) {
        this.vargoFijo = vargoFijo;
    }

    /**
     * Retorna la variable verAseoComponentes
     * 
     * @return verAseoComponentes
     */
    public boolean isVerAseoComponentes() {
        return verAseoComponentes;
    }

    /**
     * Asigna la variable verAseoComponentes
     * 
     * @param verAseoComponentes
     * Variable a asignar en verAseoComponentes
     */
    public void setVerAseoComponentes(boolean verAseoComponentes) {
        this.verAseoComponentes = verAseoComponentes;
    }

    /**
     * Retorna la variable cicloInicial
     * 
     * @return cicloInicial
     */
    public String getCicloInicial() {
        return cicloInicial;
    }

    /**
     * Asigna la variable cicloInicial
     * 
     * @param cicloInicial
     * Variable a asignar en cicloInicial
     */
    public void setCicloInicial(String cicloInicial) {
        this.cicloInicial = cicloInicial;
    }

    /**
     * Retorna la variable cicloFinal
     * 
     * @return cicloFinal
     */
    public String getCicloFinal() {
        return cicloFinal;
    }

    /**
     * Asigna la variable cicloFinal
     * 
     * @param cicloFinal
     * Variable a asignar en cicloFinal
     */
    public void setCicloFinal(String cicloFinal) {
        this.cicloFinal = cicloFinal;
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
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCicloInicial
     * 
     * @return listaCicloInicial
     */
    public RegistroDataModelImpl getListaCicloInicial() {
        return listaCicloInicial;
    }

    /**
     * Asigna la lista listaCicloInicial
     * 
     * @param listaCicloInicial
     * Variable a asignar en listaCicloInicial
     */
    public void setListaCicloInicial(RegistroDataModelImpl listaCicloInicial) {
        this.listaCicloInicial = listaCicloInicial;
    }

    /**
     * Retorna la lista listaCicloFinal
     * 
     * @return listaCicloFinal
     */
    public RegistroDataModelImpl getListaCicloFinal() {
        return listaCicloFinal;
    }

    /**
     * Asigna la lista listaCicloFinal
     * 
     * @param listaCicloFinal
     * Variable a asignar en listaCicloFinal
     */
    public void setListaCicloFinal(RegistroDataModelImpl listaCicloFinal) {
        this.listaCicloFinal = listaCicloFinal;
    }

    /**
     * Retorna la lista listaCodigoInicial
     * 
     * @return listaCodigoInicial
     */
    public RegistroDataModelImpl getListaCodigoInicial() {
        return listaCodigoInicial;
    }

    /**
     * Asigna la lista listaCodigoInicial
     * 
     * @param listaCodigoInicial
     * Variable a asignar en listaCodigoInicial
     */
    public void setListaCodigoInicial(
        RegistroDataModelImpl listaCodigoInicial) {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    /**
     * Retorna la lista listaCodigoFinal
     * 
     * @return listaCodigoFinal
     */
    public RegistroDataModelImpl getListaCodigoFinal() {
        return listaCodigoFinal;
    }

    /**
     * Asigna la lista listaCodigoFinal
     * 
     * @param listaCodigoFinal
     * Variable a asignar en listaCodigoFinal
     */
    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal) {
        this.listaCodigoFinal = listaCodigoFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}

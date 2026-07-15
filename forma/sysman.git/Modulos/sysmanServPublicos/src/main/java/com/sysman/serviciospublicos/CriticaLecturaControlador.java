/*-
 * CriticaLecturaControlador.java
 *
 * 1.0
 * 
 * 05/10/2016
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
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosCeroRemote;
import com.sysman.serviciospublicos.enums.CriticaLecturaControladorEnum;
import com.sysman.serviciospublicos.enums.CriticaLecturaControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.HashMap;
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
 * Esta clase es el controlador para el formulario CriticaLectura en
 * Access "L_LecturaCritica", el cual es llamado desde
 * Facturacion\Informes\Generales\Cr�tica de lecturas
 *
 * @version 1.0, 05/10/2016
 * @author amonroy
 * 
 * @version 2.0, 18/05/2017, <strong>pespitia:</strong><br>
 * Refactoring<br>
 * Manejo de EJBs.
 */

@ManagedBean
@ViewScoped
public class CriticaLecturaControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante que almacena el codigo del modulo de servicios
     * publicos
     */
    private final String modulo;

    /**
     * Constante a nivel de clase que aloja el nombre del enumerado
     * <code>GeneralParameterEnum.NUMERO</code>
     */
    private final String cNumero;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>CODIGORUTA</code>
     */
    private final String cCodigoRuta;

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que almacena el ciclo seleccionado en el formulario
     */
    private String numero;
    /**
     * Atributo que almacena el tipo de informe que se desea generar
     */
    private String tipoInforme;
    /**
     * Atributo que almacena el porcentaje superior para definir si
     * una lectura es critica
     */
    private String limiteSuperior;
    /**
     * Atributo que almacena el porcentaje inferior para definir si
     * una lectura es critica
     */
    private String limiteInferior;
    /**
     * Atributo que almacena el codigo desde el cual se desea generar
     * los informes
     */
    private String codigoInicial;
    /**
     * Atributo que almacena el codigo final que se desea ver en el
     * informe
     */
    private String codigoFinal;
    /**
     * Atributo que almacena el consumo promedio que se ingreso en el
     * formulario
     */
    private String valorProm;
    /**
     * Atributo que almacena el mensaje que se visualiza en el
     * formulario cada vez que se selecciona un tipo de reporte
     */
    private String descripcion;
    /**
     * Atributo que almacena el anio del ciclo seleccionado
     */
    private String anio;
    /**
     * Atributo que almacena el periodo del ciclo seleccionado
     */
    private String periodo;
    /**
     * Contenido del archivo / informe que se va a generar
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Listado de registros para el comboBox de ciclo
     */
    private RegistroDataModelImpl listaCiclo;

    /** Lista que contiene los items del combo codigo inicial */
    private RegistroDataModelImpl listaCodigoInicial;

    /** Lista que contiene los items del combo codigo final */
    private RegistroDataModelImpl listaCodigoFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    // <EJBs>
    @EJB
    private EjbServiciosPublicosCeroRemote ejbServiciosPublicosCero;

    // </EJBs>
    /**
     * Crea una nueva instancia de CriticaLecturaControlador
     */
    public CriticaLecturaControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        cNumero = GeneralParameterEnum.NUMERO.getName();
        cCodigoRuta = GeneralParameterEnum.CODIGORUTA.getName();

        try {
            numFormulario = GeneralCodigoFormaEnum.CRITICA_LECTURA_CONTROLADOR
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
     * de as y demas necesarios
     */
    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCiclo();
        limiteSuperior = "100";
        limiteInferior = "0";
        valorProm = "5";
        tipoInforme = "1";
        descripcion = idioma.getString("TB_TB1724");
        // </CARGAR_LISTA_COMBO_GRANDE>
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
     * Realiza la carga del listado de registros para el comboBox que
     * permite seleccionar el Ciclo
     */
    public void cargarListaCiclo() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CriticaLecturaControladorUrlEnum.URL5855
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCiclo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cNumero);
    }

    /**
     * Carga la lista {@code listaCodigoInicial} asociada al combo
     * codigo inicial.
     */
    public void cargarListaCodigoInicial() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CriticaLecturaControladorUrlEnum.URL0001
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), numero);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigoRuta);
    }

    /**
     * Carga la lista {@code listaCodigoFinal} asociada al combo
     * codigo final.
     */
    public void cargarListaCodigoFinal() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        CriticaLecturaControladorUrlEnum.URL0002
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), numero);
        param.put(CriticaLecturaControladorEnum.CODIGOINICIAL.getValue(),
                        codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cCodigoRuta);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /*
     * Define las acciones a ejecutar cuando se oprime el boton de
     * generar informe en formato PDF
     */
    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /*
     * Define las acciones a ejecutar cuando se oprime el boton de
     * generar informe en formato EXCEL
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /*
     * Se actualiza el mensaje que describe cada tipo de formulario,
     * de acuerdo a la opcion seleccionada en el comboBox de tipo
     * informe
     */
    public void cambiarTipoInforme() {
        // <CODIGO_DESARROLLADO>
        switch (tipoInforme) {
        case "1":
            descripcion = idioma.getString("TB_TB1724");
            break;
        case "2":
            descripcion = idioma.getString("TB_TB1725");
            break;
        case "3":
            descripcion = idioma.getString("TB_TB1726");
            break;
        case "4":
            descripcion = idioma.getString("TB_TB1727");
            break;
        default:
            descripcion = "";
            break;
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     * Actualiza en los campos de limite superior e inferior los
     * valores limites del rango del ciclo seleccionado
     * 
     * @param event
     * Evento generado al seleccionar una opcion en el comboBox ciclo
     * 
     */
    public void seleccionarFilaCiclo(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        numero = SysmanFunciones.nvl(registroAux.getCampos().get(cNumero), "")
                        .toString();

        anio = SysmanFunciones.nvl(registroAux.getCampos().get("ANO"), "")
                        .toString();

        periodo = SysmanFunciones
                        .nvl(registroAux.getCampos().get("PERIODO"), "")
                        .toString();

        codigoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGOINICIAL"), "")
                        .toString();

        codigoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGOFINAL"), "")
                        .toString();

        cargarListaCodigoInicial();
        cargarListaCodigoFinal();
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * {@code listaCodigoInicial}.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoInicial(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        codigoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigoRuta), "")
                        .toString();

        listaCodigoFinal = null;
        codigoFinal = "";

        cargarListaCodigoFinal();
    }

    /**
     * Metodo ejecutado al seleccionar una fila de la lista
     * {@code listaCodigoFinal}.
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoFinal(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        codigoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(cCodigoRuta), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <METODOS_ADICIONALES>
    /**
     * Define el tipo de reporte a generar de acuerdo a la opcion de
     * tipo de informe seleccionada en el formulario
     */
    public String definirReporte(String destino) {
        String reporte = "";
        if ("Informe".equalsIgnoreCase(destino)) {
            switch (tipoInforme) {
            case "2":
                reporte = "001147infCriticaLecturaLec";
                break;
            case "3":
                reporte = "001142infCriticaLecturaDetallado";
                break;
            case "4":
                reporte = "001146infCriticaLecturaDetalladoLec";
                break;
            default:
                reporte = "001132infCriticaLectura";
                break;
            }
        }
        return reporte;
    }

    /**
     * Define las acciones necesarias para generar el informe
     * 
     * @param formato
     * Formato seleccionado por el usuario para generar el informe
     */
    public void generarInforme(FORMATOS formato) {
        String strReporte = definirReporte("Informe");
        String condicion = "";
        try {
            String anoSte = ejbServiciosPublicosCero
                            .prepararAnoPeriodoSiguiente(compania,
                                            Integer.parseInt(anio), periodo,
                                            "0", "");

            String periodoSte = ejbServiciosPublicosCero
                            .prepararAnoPeriodoSiguiente(compania,
                                            Integer.parseInt(anio), periodo,
                                            "1", "");
            // "
            // PCK_SERVICIOS_PUBLICOS.FC_NOMBREPERIODO(COMPANIA,ANO,PERIODO,NULL)
            // AS NOPERIODO " +
            String nombrePeriodo = ejbServiciosPublicosCero
                            .asignarNombrePeriodo(compania,
                                            Integer.parseInt(anoSte),
                                            periodoSte, "");

            // Define la condicipn de acuerdo al tipo de reporte
            // seleccionado
            if (("3").equals(tipoInforme) || ("4").equals(tipoInforme)) {
                condicion = "AND USUARIO.ESTADO NOT IN('R')";
            }
            else if (("1").equals(tipoInforme)
                || ("2").equals(tipoInforme)) {
                condicion = "AND USUARIO.ESTADO NOT IN('S','C','R') AND USUARIO.INDDESHABITADO IN (0) ";
            }
            // HashMap reemplazar es para que reemplace en la
            // consulta
            // almacenada en la tabla CONSULTAS
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("ciclo", numero);
            reemplazar.put("limiteInferior", limiteInferior);
            reemplazar.put("limiteSuperior", limiteSuperior);
            reemplazar.put("codigoInicial", codigoInicial);
            reemplazar.put("codigoFinal", codigoFinal);
            reemplazar.put("valorProm", valorProm);
            reemplazar.put("periodo", periodo);
            reemplazar.put("condicion", condicion);

            // MANEJO DE PARAMETROS DE REEMPLAZO
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_CICLO", numero);
            parametros.put("PR_PERIODO", nombrePeriodo);

            Reporteador.resuelveConsulta("001132infCriticaLectura",
                            Integer.parseInt(modulo), reemplazar,
                            parametros);
            archivoDescarga = JsfUtil.exportarStreamed(strReporte,
                            parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
                        | NumberFormatException | SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }
    // </METODOS_ADICIONALES>

    // <SET_GET_ATRIBUTOS>
    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getTipoInforme() {
        return tipoInforme;
    }

    public void setTipoInforme(String tipoInforme) {
        this.tipoInforme = tipoInforme;
    }

    public String getLimiteSuperior() {
        return limiteSuperior;
    }

    public void setLimiteSuperior(String limiteSuperior) {
        this.limiteSuperior = limiteSuperior;
    }

    public String getLimiteInferior() {
        return limiteInferior;
    }

    public void setLimiteInferior(String limiteInferior) {
        this.limiteInferior = limiteInferior;
    }

    public String getCodigoInicial() {
        return codigoInicial;
    }

    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    public String getCodigoFinal() {
        return codigoFinal;
    }

    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    public String getValorProm() {
        return valorProm;
    }

    public void setValorProm(String valorProm) {
        this.valorProm = valorProm;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga) {
        this.archivoDescarga = archivoDescarga;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCiclo() {
        return listaCiclo;
    }

    public void setListaCiclo(RegistroDataModelImpl listaCiclo) {
        this.listaCiclo = listaCiclo;
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

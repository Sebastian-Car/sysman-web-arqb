/*-
 * ConsumoSectorHidraulicoControlador.java
 *
 * 1.0
 *
 * 25/10/2016
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import static com.sysman.util.SysmanFunciones.nvl;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.ConsumoSectorHidraulicoControladorUrlEnum;

import java.io.IOException;
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

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador del formulario ConsumoSectorHidraulicoControlador
 *
 * @version 1.0, 25/10/2016
 * @author cperez
 * 
 * @author eamaya
 * @version 2, 18/05/2017 Proceso de Refactoring y Manejo de EJBs
 * 
 */
@ManagedBean
@ViewScoped

public class ConsumoSectorHidraulicoControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida para almacenar la cadena "NUMERO"
     */
    private final String numero;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    /**
     * Obtiene los id de la seleccion del usuario
     */
    private List<Registro> listaSeleccionados;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Obtiene los ciclos de la consulta
     */
    private RegistroDataModelImpl listaCiclo;
    /**
     * Obtiene los ciclos de la consulta ya para enviarlos a la
     * consulta final
     */
    private String listaCicloFinal;
    /**
     * consecutivo para el valor conSectorHidraulico que se llama del
     * paramtro
     */
    private String conSectorHidraulico;
    /**
     * obtiene el id seleccionado en el combo
     */
    private String redes;

    @EJB
    private EjbSysmanUtilRemote ejbParametro;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ConsumoSectorHidraulicoControlador
     */
    public ConsumoSectorHidraulicoControlador() {
        super();
        compania = SessionUtil.getCompania();
        listaCicloFinal = "";
        conSectorHidraulico = "ConSectorHidraulico";
        numero = "NUMERO";
        try {
            numFormulario = GeneralCodigoFormaEnum.CONSUMO_SECTOR_HIDRAULICO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
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
        /*
         * FR1151-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore formularioAbrir 74, Me.Name End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaCiclo
     */

    public void cargarListaCiclo() {
        try {

            UrlBean urlBean = UrlServiceUtil.getInstance()
                            .getUrlServiceByUrlByEnumID(
                                            ConsumoSectorHidraulicoControladorUrlEnum.URL5158
                                                            .getValue());
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(),
                            compania);

            listaCiclo = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param,
                            false,
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            "SP_CICLO"),
                            true);
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     *
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        seleccionarReporte(FORMATOS.PDF);

        // </CODIGO_DESARROLLADO>
    }

    private void seleccionarReporte(ReportesBean.FORMATOS formato) {
        try {
            if (listaCiclo.getSeleccionados().isEmpty()) {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1775"));
                return;
            }

            listaSeleccionados = listaCiclo.getSeleccionados();
            StringBuilder ciclos = new StringBuilder();

            for (Registro reg : listaSeleccionados) {
                ciclos.append("'" + reg.getCampos().get(numero) + "'" + ",");
                reg.getCampos().get(numero);
            }
            listaCicloFinal = ciclos.substring(0, ciclos.length() - 1);

            String valorParametro;

            valorParametro = nvl(ejbParametro.consultarParametro(compania,
                            "FORMATO REPORTE SECTOR HIDRAULICO",
                            SessionUtil.getModulo(), new Date(), false),
                            conSectorHidraulico).toString();
            if ("1".equals(redes)) {
                if (valorParametro.equalsIgnoreCase(conSectorHidraulico)) {
                    genInforme(formato, "001164ConSectorHidraulico");
                }
                else {
                    genInforme(formato, "001165ConSectorHidraulico2");
                }

            }
            else {
                genInforme(formato, "001166ConsumoRedAcueducto");
            }

        }
        catch (SystemException e) {
            Logger.getLogger(ConsumoSectorHidraulicoControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     *
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        seleccionarReporte(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    public void genInforme(ReportesBean.FORMATOS formato, String reporte) {

        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("ciclo", listaCicloFinal);
            reemplazar.put("compania", compania);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_FORMS_CONSUMOSECTORHIDRAULICO_CICLOS",
                            listaCicloFinal);
            String nombre;
            // parametro enviado para la columna Nombre red de
            // acuedocto del reporte 001166ConsumoRedAcueducto
            nombre = "ConsumoRedAcueducto";
            parametros.put("PR_NOMBRE", nombre);
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control Redes
     *
     */
    public void cambiarRedes() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCiclo() {
        // no hace nada
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public List<Registro> getListaSeleccionados() {
        return listaSeleccionados;
    }

    public void setListaSeleccionados(List<Registro> listaSeleccionados) {
        this.listaSeleccionados = listaSeleccionados;
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
    public RegistroDataModelImpl getListaCiclo() {
        return listaCiclo;
    }

    /**
     * Retorna la variable redes
     *
     * @return redes
     */
    public String getRedes() {
        return redes;
    }

    /**
     * Asigna la variable redes
     *
     * @param redes
     * Variable a asignar en redes
     */
    public void setRedes(String redes) {
        this.redes = redes;
    }

    /**
     * Asigna la lista listaCiclo
     *
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(RegistroDataModelImpl listaCiclo) {
        this.listaCiclo = listaCiclo;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}

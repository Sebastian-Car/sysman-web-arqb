/*-
 * FrmrecaudostrimestralesControlador.java
 *
 * 1.0
 * 
 * 09/11/2016
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
import com.sysman.reportes.Reporteador;
import com.sysman.serviciospublicos.enums.FrmrecaudostrimestralesControladorUrlEnum;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Controlador del formulario FrmrecaudostrimestralesControlador
 *
 * @version 1.0, 09/11/2016
 * @author cperez
 * 
 * @version 2, 31/05/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 * 
 * 
 */

@ManagedBean
@ViewScoped
public class FrmrecaudostrimestralesControlador extends BeanBaseModal {
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Obtiene el ciclo del combo ciclo
     */
    private String ciclo;
    /**
     * Obtiene el ano del combo a�o
     */
    private String ano;
    /**
     * Obtiene el mes del combo mes O TRIMESTRE
     */
    private String mes;

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
     * Necesario para obtener y mandar la lista del ciclo Inicial
     */
    private List<Registro> listacmbCiclo;
    /**
     * Necesario para obtener y mandar la lista del ciclo Final
     */
    private List<Registro> listacmbAno;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de FrmrecaudostrimestralesControlador
     */
    public FrmrecaudostrimestralesControlador() {
        super();
        compania = SessionUtil.getCompania();
        try {
            numFormulario = GeneralCodigoFormaEnum.FRMRECAUDOSTRIMESTRALES_CONTROLADOR
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
        cargarListacmbCiclo();
        cargarListacmbAno();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
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
     * Carga la lista listacmbCiclo
     */
    public void cargarListacmbCiclo() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            listacmbCiclo = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmrecaudostrimestralesControladorUrlEnum.URL4665
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
     * Carga la lista listacmbAno
     */
    public void cargarListacmbAno() {
        try {
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            listacmbAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmrecaudostrimestralesControladorUrlEnum.URL5258
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
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     *
     */
    public void oprimirPdf() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        genInforme(FORMATOS.PDF, "001227rptRecaudosTrimestrales");
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel
     *
     */
    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        genInforme(FORMATOS.EXCEL, "001227rptRecaudosTrimestrales");
        // </CODIGO_DESARROLLADO>
    }

    /*
     * Metodo para generar el informe ya sea de pdf o excel
     */
    public void genInforme(ReportesBean.FORMATOS formato, String reporte) {

        archivoDescarga = null;
        try {
            HashMap<String, Object> reemplazar = new HashMap<>();
            String condicionCiclo;
            String trimestre = null;
            String nombreCompania;
            if ("T".equals(ciclo)) {
                condicionCiclo = "";
            }
            else {
                condicionCiclo = " AND SP_HISTORICOFACTURA.CICLO = '" + ciclo
                    + "'";
            }
            if ("1".equals(mes)) {
                trimestre = "BETWEEN 1 and 3";
            }
            else if ("2".equals(mes)) {
                trimestre = "BETWEEN 4 and 6";
            }
            else if ("3".equals(mes)) {
                trimestre = "BETWEEN 7 and 9";
            }
            else if ("4".equals(mes)) {
                trimestre = "BETWEEN 10 and 12";
            }

            reemplazar.put("compania", compania);
            reemplazar.put("condicionCiclo", condicionCiclo);
            reemplazar.put("ano", ano);
            reemplazar.put("trimestre", trimestre);
            reemplazar.put("condicionCiclo", condicionCiclo);
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

            Registro reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmrecaudostrimestralesControladorUrlEnum.URL7622
                                                                            .getValue())
                                            .getUrl(), param));

            nombreCompania = reg != null
                ? (String) reg.getCampos().get("NOMBRE") : "";
            Map<String, Object> parametros = new HashMap<>();
            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);
            parametros.put("PR_NOMBRECOMPANIA", nombreCompania);
            parametros.put("PR_FORMS_FRMRECAUDOSTRIMESTRALES_CMBCICLO", ciclo);
            parametros.put("PR_FORMS_FRMRECAUDOSTRIMESTRALES_CMBANO", ano);
            parametros.put("PR_FORMS_FRMRECAUDOSTRIMESTRALES_CMBTRIMESTRE",
                            mes);
            parametros.put("PR_NOMBRECOMPANIA", nombreCompania);
            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
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
     * Retorna la variable ano
     * 
     * @return ano
     */
    public String getAno() {
        return ano;
    }

    /**
     * Asigna la variable ano
     * 
     * @param ano
     * Variable a asignar en ano
     */
    public void setAno(String ano) {
        this.ano = ano;
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
     * Retorna la lista listacmbCiclo
     * 
     * @return listacmbCiclo
     */
    public List<Registro> getListacmbCiclo() {
        return listacmbCiclo;
    }

    /**
     * Asigna la lista listacmbCiclo
     * 
     * @param listacmbCiclo
     * Variable a asignar en listacmbCiclo
     */
    public void setListacmbCiclo(List<Registro> listacmbCiclo) {
        this.listacmbCiclo = listacmbCiclo;
    }

    /**
     * Retorna la lista listacmbAno
     * 
     * @return listacmbAno
     */
    public List<Registro> getListacmbAno() {
        return listacmbAno;
    }

    /**
     * Asigna la lista listacmbAno
     * 
     * @param listacmbAno
     * Variable a asignar en listacmbAno
     */
    public void setListacmbAno(List<Registro> listacmbAno) {
        this.listacmbAno = listacmbAno;
    }
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>
}

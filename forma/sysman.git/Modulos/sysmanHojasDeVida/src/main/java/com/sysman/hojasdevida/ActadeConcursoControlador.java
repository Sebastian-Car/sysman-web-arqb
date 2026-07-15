/*-
 * ActadeConcursoControlador.java
 *
 * 1.0
 * 
 * 21/12/2017
 * 
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.hojasdevida;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.hojasdevida.enums.ActadeConcursoControladorUrlEnum;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Clase que genera el informe principal de las actas de concurso
 *
 * @version 1.0, 21/12/2017
 * @author jeguerrero
 */
@ManagedBean
@ViewScoped
public class ActadeConcursoControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Variable encargada de almacenar temporalmente lo seleccioando
     * en el combo convocatoria de la interfaz grafica
     */
    private String convocatoria;
    /**
     * Constante encargada de almacenar el String VALOR_CONCURSO
     */
    private final String valorConcurso;
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
     * Variable encargada de almacenar los datos de respuesta de la
     * base de datos
     */
    private RegistroDataModelImpl listaConvocatoria;

    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de ActadeConcursoControlador
     */
    public ActadeConcursoControlador()
    {
        super();
        valorConcurso = "VALOR_CONCURSO";
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.ACTADECONCURSO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
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
    public void inicializar()
    {
        // <CARGAR_LISTA>
        cargarListaConvocatoria();
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
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        /*
         * FR1544-AL_ABRIR Private Sub Form_Open(Cancel As Integer)
         * DoCmd.Restore End Sub
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listaConvocatoria
     *
     * Metodo encargado de hacer el llamado a la base de datos y
     * almacenar su respuesta en la lista listaConvocatoria
     */
    public void cargarListaConvocatoria()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        ActadeConcursoControladorUrlEnum.URL6230
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaConvocatoria = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NRO_CONVOCATORIA");

    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaConvocatoria
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaConvocatoria(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        convocatoria = retornarString(registroAux, "NRO_CONVOCATORIA");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Pdf en la vista
     *
     *
     */
    public void oprimirPdf()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;

        genInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     *
     */
    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        genInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
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
     * Retorna la variable convocatoria
     * 
     * @return convocatoria
     */
    public String getConvocatoria()
    {
        return convocatoria;
    }

    /**
     * Asigna la variable convocatoria
     * 
     * @param convocatoria
     * Variable a asignar en convocatoria
     */
    public void setConvocatoria(String convocatoria)
    {
        this.convocatoria = convocatoria;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public RegistroDataModelImpl getListaConvocatoria()
    {
        return listaConvocatoria;
    }

    public void setListaConvocatoria(RegistroDataModelImpl listaConvocatoria)
    {
        this.listaConvocatoria = listaConvocatoria;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    /**
     * Retorna la lista listaConvocatoria
     * 
     * @return listaConvocatoria
     */

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

    private void genInforme(ReportesBean.FORMATOS formato)
    {

        Map<String, Object> reemplazar = new HashMap<>();
        String[] porcentajes = traerPorcentajes();

        reemplazar.put("convocatoria", convocatoria);

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("PR_NOMBRE_COMPANIA",
                        SessionUtil.getCompaniaIngreso().getNombre());

        String p0 = "0.0";
        String p1 = "PR_P1";
        String p2 = "PR_P2";
        String p3 = "PR_P3";
        String p4 = "PR_P4";
        String p5 = "PR_P5";
        parametros.put(p1, validarParametrosPorcent(porcentajes[1]));
        parametros.put(p2, validarParametrosPorcent(porcentajes[2]));
        parametros.put(p3, validarParametrosPorcent(porcentajes[3]));
        parametros.put(p4, validarParametrosPorcent(porcentajes[4]));
        parametros.put(p5, validarParametrosPorcent(porcentajes[5]));

        /**
         * Creacion del order by
         */
        String valor1 = SysmanFunciones.toString(parametros.get(p1))
                        .equals(p0) ? "1"
                            : SysmanFunciones.toString(parametros.get(p1));
        String valor2 = SysmanFunciones.toString(parametros.get(p2))
                        .equals(p0) ? "1"
                            : SysmanFunciones.toString(parametros.get(p2));
        String valor3 = SysmanFunciones.toString(parametros.get(p3))
                        .equals(p0) ? "1"
                            : SysmanFunciones.toString(parametros.get(p3));
        String valor4 = SysmanFunciones.toString(parametros.get(p4))
                        .equals(p0) ? "1"
                            : SysmanFunciones.toString(parametros.get(p4));
        String valor5 = SysmanFunciones.toString(parametros.get(p5))
                        .equals(p0) ? "1"
                            : SysmanFunciones.toString(parametros.get(p5));

        String ordenamiento = " ORDER BY ((P01*"
            + valor1 + ")+(P02*" + valor2
            + ")+(P03*" + valor3 + ")+(P04*"
            + valor4 + ")+ (P05*" + valor5
            + "))";

        reemplazar.put("ordenamiento",
                        SysmanFunciones.validarVariableVacio(ordenamiento) ? ""
                            : ordenamiento);

        Reporteador.resuelveConsulta("001680ActaConcurso",
                        Integer.parseInt(SessionUtil.getModulo()),
                        reemplazar, parametros);
        try
        {
            archivoDescarga = JsfUtil.exportarStreamed(
                            "001680ActaConcurso", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private String[] traerPorcentajes()
    {

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put("CONVOCATORIA", convocatoria);

        String[] porcentajes = null;
        try
        {
            List<Registro> listaPorcentajes = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ActadeConcursoControladorUrlEnum.URL6229
                                                                            .getValue())
                                            .getUrl(), param));

            if (listaPorcentajes != null)
            {
                porcentajes = generarPorcentajes(listaPorcentajes);
            }

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        return porcentajes;

    }

    private String[] generarPorcentajes(List<Registro> listaPorcentajes)
    {
        String[] porcentajes = new String[6];
        for (Registro registro : listaPorcentajes)
        {
            switch (retornarString(registro, "PRUEBA"))
            {
            case "01":
                porcentajes[1] = retornarString(registro,
                                valorConcurso);
                break;
            case "02":
                porcentajes[2] = retornarString(registro,
                                valorConcurso);
                break;
            case "03":
                porcentajes[3] = retornarString(registro,
                                valorConcurso);
                break;
            case "04":
                porcentajes[4] = retornarString(registro,
                                valorConcurso);
                break;
            case "05":
                porcentajes[5] = retornarString(registro,
                                valorConcurso);
                break;

            default:
                break;
            }

        }
        return porcentajes;

    }

    private String retornarString(Registro reg, String campo)
    {
        return SysmanFunciones.validarCampoVacio(reg.getCampos(), campo) ? ""
            : reg.getCampos().get(campo).toString();
    }

    private double validarParametrosPorcent(String porcentaje)
    {
        return SysmanFunciones.validarVariableVacio(porcentaje) ? 0
            : Double.parseDouble(porcentaje);

    }
    /**
     * Este metodo se comentaero dado que se solicito que el
     * ordenamieto fuera por total MVENEGAS
     */
    /**
     * private String retornarOrdenamiento(String[] porcentajes) {
     * String ordenamiento = " ORDER BY ";
     * 
     * if ((porcentajes[4] != null) && (Double.doubleToRawLongBits(
     * Double.parseDouble(porcentajes[4])) != 0)) { ordenamiento =
     * SysmanFunciones.concatenar(ordenamiento, "P04", "||"); } if
     * ((porcentajes[3] != null) && (Double.doubleToRawLongBits(
     * Double.parseDouble(porcentajes[3])) != 0)) { ordenamiento =
     * SysmanFunciones.concatenar(ordenamiento, "P03", "||"); } if
     * ((porcentajes[2] != null) && (Double.doubleToRawLongBits(
     * Double.parseDouble(porcentajes[2])) != 0)) { ordenamiento =
     * SysmanFunciones.concatenar(ordenamiento, "P02", "||"); } if
     * ((porcentajes[1] != null) && (Double.doubleToRawLongBits(
     * Double.parseDouble(porcentajes[1])) != 0)) { ordenamiento =
     * SysmanFunciones.concatenar(ordenamiento, "P01", "||"); }
     * 
     * if (ordenamiento.length() > 10) { ordenamiento =
     * ordenamiento.substring(0, ordenamiento.length() - 2); } else {
     * ordenamiento = " "; }
     * 
     * return ordenamiento;
     * 
     * }
     */

}

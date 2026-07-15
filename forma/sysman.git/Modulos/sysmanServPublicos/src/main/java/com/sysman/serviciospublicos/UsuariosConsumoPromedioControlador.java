/*-
 * UsuariosConsumoPromedioControlador.java
 *
 * 1.0
 *
 * 03/11/2016
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
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.ejb.EjbServiciosPublicosDosRemote;
import com.sysman.serviciospublicos.enums.UsuariosConsumoPromedioControladorEnum;
import com.sysman.serviciospublicos.enums.UsuariosConsumoPromedioControladorUrlEnum;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
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
 * Esta clase es el controlador para el formulario Usuarios conConsumo Promedio en Access "FrmUsuarioConsumoProm", el cual es llamado desde Facturacion\Informes\Generales\Usuarios con consumos
 * Promedios
 *
 * @version 1.0, 03/11/2016
 * @author amonroy
 *
 * @version 2.0, 20/06/2017
 * @author jguerrero Se agrega refactoring a la clase, ajustando consultas y llamados a funciones.
 */
@ManagedBean
@ViewScoped
public class UsuariosConsumoPromedioControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que permite identificar si se ha seleccionado el check de agrupado
     */
    private boolean agrupado;
    /**
     * Atributo que almacena el codigo de ruta desde el cual se desea generar el informe
     */
    private String codigoInicial;
    /**
     * Atributo que almacena el codigo de ruta final que se desea ver en el informe
     */
    private String codigoFinal;
    /**
     * Atributo que almacena el anio seleccionado en el formulario
     */
    private String anio;
    /**
     * Atributo que almacena el periodo seleccionado en el formulario
     */
    private String periodo;
    /**
     * Atributo que almacena el ciclo seleccionado en el formulario
     */
    private String ciclo;
    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    /**
     * Listado de registros para el comboBox de anio
     */
    private List<Registro> listaAnio;
    /**
     * Listado de registros para el comboBox de periodo
     */
    private List<Registro> listaPeriodo;
    /**
     * Listado de registros para el comboBox de ciclo
     */
    private List<Registro> listaCiclo;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Listado de registros para el comboBox de codigo de ruta inicial
     */
    private RegistroDataModelImpl listaCodigoInicial;
    /**
     * Listado de registros para el comboBox de codigo de ruta final
     */
    private RegistroDataModelImpl listaCodigoFinal;

    @EJB
    private EjbServiciosPublicosDosRemote ejbServiciosPublicosDos;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de UsuariosConsumoPromedioControlador
     */
    public UsuariosConsumoPromedioControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.USUARIOS_CONSUMO_PROMEDIO_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            anio = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
            periodo = "01";
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    /**
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        // <CARGAR_LISTA>
        cargarListaCiclo();
        cargarListaAnio();
        cargarListaPeriodo();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        // <CREAR_ARBOLES>
        // </CREAR_ARBOLES>
        abrirFormulario();
    }

    /**
     * Este metodo es invocado el metodo inicializar, se ejecutan las acciones a tener en cuenta en el momento de apertura del formulario
     */
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaAnio
     *
     */
    public void cargarListaAnio()
    {
        // 21021
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaAnio = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(UsuariosConsumoPromedioControladorUrlEnum.URL5748.getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaPeriodo
     */
    public void cargarListaPeriodo()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.ANO.getName(), anio);

        try
        {
            listaPeriodo = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(UsuariosConsumoPromedioControladorUrlEnum.URL6194.getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     *
     * Carga la lista listaCiclo
     */
    public void cargarListaCiclo()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        try
        {
            listaCiclo = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(UsuariosConsumoPromedioControladorUrlEnum.URL6794.getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    /**
     * Carga la lista del combo grande listaCodigoInicial
     */
    public void cargarListaCodigoInicial()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(UsuariosConsumoPromedioControladorUrlEnum.URL7563.getValue());

        Map<String, Object> param = new TreeMap<>();

        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        listaCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGORUTA.getName());
    }

    /**
     *
     * Carga la lista del combo grande listaCodigoFinal
     *
     */
    public void cargarListaCodigoFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(UsuariosConsumoPromedioControladorUrlEnum.URL8348.getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(UsuariosConsumoPromedioControladorEnum.CODIGOINICIAL.getValue(), codigoInicial);

        listaCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(), urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGORUTA.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton BtnPdf en la vista Llama al metodo para generar el reporte, enviando como parametro el formato pdf
     *
     */
    public void oprimirBtnPdf()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     *
     * Metodo ejecutado al oprimir el boton BtnExcel en la vista Llama al metodo para generar el reporte, enviando como parametro el formato excel
     *
     */
    public void oprimirBtnExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Asigna los valores por omisi�n a los combos de codigo inicial y final de acuerdo al ciclo seleccionado. Llama el metodo para cargar la lista de codigo de ruta inicial
     */
    public void cambiarCiclo()
    {
        if (("T").equalsIgnoreCase(ciclo))
        {
            codigoInicial = "0000000000000000";
            codigoFinal = "9999999999999999";
        }
        else
        {
            codigoInicial = null;
            codigoFinal = null;
        }

        cargarListaCodigoInicial();
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCodigoInicial. Llama el metodo para cargar la lista de codigo de ruta final y reinicia el valor del codigo final
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos().get(GeneralParameterEnum.CODIGORUTA.getName()).toString();
        cargarListaCodigoFinal();
        codigoFinal = null;
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCodigoFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCodigoFinal(SelectEvent event)
    {

        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos().get(GeneralParameterEnum.CODIGORUTA.getName()).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    /**
     * Define las acciones necesarias para generar el informe realiza el reemplazo de valores en la consulta del informe y envia los parametros definidos
     *
     * @param formato
     * Formato seleccionado por el usuario para generar el informe
     */
    public void generarInforme(FORMATOS formato)
    {

        // El informe que se gernera depende del
        try
        {
            String reporte = agrupado
                ? "001209UsuarioConsumoPromAgrup"
                : "001210UsuarioConsumoProm";
            String condicionUsuario = !("T").equalsIgnoreCase(ciclo)
                ? "USUARIO.CICLO = " + ciclo + " AND"
                : "";
            String condicionHistorico = !("T").equalsIgnoreCase(ciclo)
                ? "HISTORICOFACTURA.CICLO = " + ciclo + " AND"
                : "";

            String nombrePeriodo = ejbServiciosPublicosDos.asignarNombrePeriodoCorto(compania, Integer.parseInt(anio), periodo, "");

            HashMap<String, Object> reemplazar = new HashMap<>();
            Map<String, Object> parametros = new HashMap<>();

            reemplazar.put("condicionUsuario", condicionUsuario);
            reemplazar.put("anio", anio);
            reemplazar.put("periodo", periodo);
            reemplazar.put("condicionHistorico", condicionHistorico);
            if (("001210UsuarioConsumoProm").equalsIgnoreCase(reporte))
            {
                reemplazar.put("codigoInicial", codigoInicial);
                reemplazar.put("codigoFinal", codigoFinal);
            }

            parametros.put("PR_CICLO", ciclo);
            parametros.put("PR_NOMBREPERIODO", nombrePeriodo);

            Reporteador.resuelveConsulta(reporte,
                            Integer.parseInt(SessionUtil.getModulo()),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException
                        | NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Asigna la variable agrupado
     *
     * @param agrupado
     * Variable a asignar en agrupado
     */
    public void setAgrupado(boolean agrupado)
    {
        this.agrupado = agrupado;
    }

    /**
     * Retorna la variable agrupado
     *
     * @return agrupado
     *
     */
    public boolean isAgrupado()
    {
        return agrupado;
    }

    /**
     * Retorna la variable codigoInicial
     *
     * @return codigoInicial
     */
    public String getCodigoInicial()
    {
        return codigoInicial;
    }

    /**
     * Asigna la variable codigoInicial
     *
     * @param codigoInicial
     * Variable a asignar en codigoInicial
     */
    public void setCodigoInicial(String codigoInicial)
    {
        this.codigoInicial = codigoInicial;
    }

    /**
     * Retorna la variable codigoFinal
     *
     * @return codigoFinal
     */
    public String getCodigoFinal()
    {
        return codigoFinal;
    }

    /**
     * Asigna la variable codigoFinal
     *
     * @param codigoFinal
     * Variable a asignar en codigoFinal
     */
    public void setCodigoFinal(String codigoFinal)
    {
        this.codigoFinal = codigoFinal;
    }

    /**
     * Retorna la variable anio
     *
     * @return anio
     */
    public String getAnio()
    {
        return anio;
    }

    /**
     * Asigna la variable anio
     *
     * @param anio
     * Variable a asignar en anio
     */
    public void setAnio(String anio)
    {
        this.anio = anio;
    }

    /**
     * Retorna la variable periodo
     *
     * @return periodo
     */
    public String getPeriodo()
    {
        return periodo;
    }

    /**
     * Asigna la variable periodo
     *
     * @param periodo
     * Variable a asignar en periodo
     */
    public void setPeriodo(String periodo)
    {
        this.periodo = periodo;
    }

    /**
     * Retorna la variable ciclo
     *
     * @return ciclo
     */
    public String getCiclo()
    {
        return ciclo;
    }

    /**
     * Asigna la variable ciclo
     *
     * @param ciclo
     * Variable a asignar en ciclo
     */
    public void setCiclo(String ciclo)
    {
        this.ciclo = ciclo;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    public StreamedContent getArchivoDescarga()
    {
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
    public List<Registro> getListaAnio()
    {
        return listaAnio;
    }

    /**
     * Asigna la lista listaAnio
     *
     * @param listaAnio
     * Variable a asignar en listaAnio
     */
    public void setListaAnio(List<Registro> listaAnio)
    {
        this.listaAnio = listaAnio;
    }

    /**
     * Retorna la lista listaPeriodo
     *
     * @return listaPeriodo
     */
    public List<Registro> getListaPeriodo()
    {
        return listaPeriodo;
    }

    /**
     * Asigna la lista listaPeriodo
     *
     * @param listaPeriodo
     * Variable a asignar en listaPeriodo
     */
    public void setListaPeriodo(List<Registro> listaPeriodo)
    {
        this.listaPeriodo = listaPeriodo;
    }

    /**
     * Retorna la lista listaCiclo
     *
     * @return listaCiclo
     */
    public List<Registro> getListaCiclo()
    {
        return listaCiclo;
    }

    /**
     * Asigna la lista listaCiclo
     *
     * @param listaCiclo
     * Variable a asignar en listaCiclo
     */
    public void setListaCiclo(List<Registro> listaCiclo)
    {
        this.listaCiclo = listaCiclo;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    /**
     * Retorna la lista listaCodigoInicial
     *
     * @return listaCodigoInicial
     */
    public RegistroDataModelImpl getListaCodigoInicial()
    {
        return listaCodigoInicial;
    }

    /**
     * Asigna la lista listaCodigoInicial
     *
     * @param listaCodigoInicial
     * Variable a asignar en listaCodigoInicial
     */
    public void setListaCodigoInicial(RegistroDataModelImpl listaCodigoInicial)
    {
        this.listaCodigoInicial = listaCodigoInicial;
    }

    /**
     * Retorna la lista listaCodigoFinal
     *
     * @return listaCodigoFinal
     */
    public RegistroDataModelImpl getListaCodigoFinal()
    {
        return listaCodigoFinal;
    }

    /**
     * Asigna la lista listaCodigoFinal
     *
     * @param listaCodigoFinal
     * Variable a asignar en listaCodigoFinal
     */
    public void setListaCodigoFinal(RegistroDataModelImpl listaCodigoFinal)
    {
        this.listaCodigoFinal = listaCodigoFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}

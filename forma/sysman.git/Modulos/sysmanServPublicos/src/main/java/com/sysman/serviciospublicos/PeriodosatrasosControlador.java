/*-
 * PeriodosatrasosControlador.java
 *
 * 1.0
 *
 * 24/10/2016
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
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.PeriodosatrasosControladorEnum;
import com.sysman.serviciospublicos.enums.PeriodosatrasosControladorUrlEnum;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 * Clase migrada para generar informe con el listado de los Usuarios con periodo de atrasos
 *
 * @version 1.0, 24/10/2016
 * @author ybecerra
 * 
 * @author spina - refactorizo conexiones
 * @version 2, 13/06/2017
 * 
 * @version 3, 14/06/2017
 * @author jreina se realizaron los cambios de refactoring 
 * en cada uno de los combos.
 */

@ManagedBean
@ViewScoped
public class PeriodosatrasosControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;

    /**
     * Constante a nivel de clase que almacena el codigo del modulo por la cual se ingresa a la aplicacion
     */
    private final String modulo;

    /**
     * Constante definida para almacenar la cadena ""NUMERO" se llama en los metodos cargarListaCicloinicial, cargarListaCiclofinal, seleccionarFilaCicloinicial,seleccionarFilaCiclofinal
     */
    private final String numero;
    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que valida que informe se generara, si en el formulario la casilla de verificacion esta seleccionada se generara el informe "" si no se genera "".
     */
    private boolean igual;
    /**
     * Atributo que almacena el ciclo seleccionado en el combo ciclo Inicial del formulario
     */
    private String cicloInicial;
    /**
     * Atributo que almacena el ciclo seleccionado en el combo ciclo Final del formulario
     */
    private String cicloFinal;
    /**
     * Atributo que almacena el codigo seleccionado en el combo Periodos de Atraso del formulario
     */
    private String periodoAtraso;
    /**
     * Atributo para validar si se carga o no el combo de periodo atraso del formulario
     */
    private boolean atrasoVisible;

    /**
     * Variable local definida para almacenar la condicion a enviar al resuelveConsulta del informe 001161RptPeriodosAtraso
     */
    private String condicion;
    /**
     * Atributo usado para descargar contenidos de archivos desde la vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Lista de objetos pertenecientes al combo Ciclo Final.
     */
    private RegistroDataModelImpl listaCicloinicial;
    /**
     * Lista de objetos pertenecientes al combo Ciclo Final
     */
    private RegistroDataModelImpl listaciclofinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Crea una nueva instancia de PeriodosatrasosControlador
     */
    public PeriodosatrasosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        numero = "NUMERO";
        try
        {
            numFormulario = GeneralCodigoFormaEnum.PERIODOSATRASOS_CONTROLADOR.getCodigo();
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
     * Este metodo se ejecuta justo despues de que el objeto de la clase del Bean ha sido creado, en este se realizan las asignaciones iniciales necesarias para la visualizacion del formulario, como
     * son tablas, origenes de datos, inicializacion de listas y demas necesarios
     */
    @PostConstruct
    public void inicializar()
    {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCicloinicial();

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

        atrasoVisible = true;
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     *
     * Carga la lista listaCicloinicial
     *
     * Metodo que realiza la carga de los elementos de la lista Ciclo Inicial.
     */
    public void cargarListaCicloinicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(PeriodosatrasosControladorUrlEnum.URL5802.getValue());        
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);

        listaCicloinicial = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, numero);
    }

    /**
     *
     * Carga la lista listaciclofinal
     *
     * Metodo que realiza la carga de los elementos de la lista Ciclo Final.
     */
    public void cargarListaciclofinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance().getUrlServiceByUrlByEnumID(PeriodosatrasosControladorUrlEnum.URL6734.getValue());        
        Map<String,Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(),compania);
        param.put(PeriodosatrasosControladorEnum.PARAM0.getValue(),cicloInicial);

        listaciclofinal = new RegistroDataModelImpl(urlBean.getUrl(),urlBean.getUrlConteo().getUrl(),param,
                        true, numero);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     *
     * Metodo ejecutado al oprimir el boton Presentar en la vista
     *
     *
     */
    public void oprimirPresentar()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
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
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta al oprimir el boton pdf o excel del formulario, recibe como parametro el formato del archivo
     *
     * @param formato
     */
    public void generarInforme(ReportesBean.FORMATOS formato)
    {

        String reporte = "";
        try
        {
            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("cicloInicial", "'" + cicloInicial + "'");
            reemplazar.put("cicloFinal", "'" + cicloFinal + "'");

            if (igual)
            {
                reporte = "001159rptperiodosatrasoLecIgu";

            }
            else
            {
                if (periodoAtraso == null)
                {
                    JsfUtil.agregarMensajeError(idioma.getString("TB_TB1774"));
                    return;
                }

                reporte = "001161RptPeriodosAtraso";
                reemplazar.put("periodoAtraso", periodoAtraso);
                reemplazar.put("condicion", periodo());

            }
            Map<String, Object> parametros = new HashMap<>();

            parametros.put("PR_CICLOI", cicloInicial);
            parametros.put("PR_CICLOF", cicloFinal);
            Reporteador.resuelveConsulta(reporte, Integer.parseInt(modulo),
                            reemplazar,
                            parametros);

            archivoDescarga = JsfUtil.exportarStreamed(reporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);

        }
        catch (IOException | JRException | RuntimeException | SysmanException ex)
        {

            logger.error(ex);
            JsfUtil.agregarMensajeError(ex.getMessage());

        }
    }

    // </METODOS_BOTONES>

    public String periodo()
    {

        String perUno = "    AND SP_USUARIO.LECTURA  = SP_USUARIO.LECTURA1";
        String perDos = "    AND SP_USUARIO.LECTURA1 = SP_USUARIO.LECTURA2";
        String perTres = "   AND SP_USUARIO.LECTURA2 = SP_USUARIO.LECTURA3";
        String perCuatro = " AND SP_USUARIO.LECTURA3 = SP_USUARIO.LECTURA4";
        String perCinco = "  AND SP_USUARIO.LECTURA4 = SP_USUARIO.LECTURA5";
        String perSeis = "   AND SP_USUARIO.LECTURA5 = SP_USUARIO.LECTURA6";

        switch (periodoAtraso)
        {
        case "1":
            condicion = perUno;
            break;
        case "2":
            condicion = perUno
                + perDos;
            break;
        case "3":
            condicion = perUno
                + perDos
                + perTres;
            break;
        case "4":
            condicion = perUno
                + perDos
                + perTres
                + perCuatro;
            break;
        case "5":
            condicion = perUno + perDos
                + perTres + perCuatro
                + perCinco;
            break;
        case "6":

            condicion = perUno + perDos
                + perTres + perCuatro
                + perCinco + perSeis;
            break;
        default:
            break;
        }

        return condicion;
    }

    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al seleccionar la casilla de verificacion del formulario
     */
    public void cambiarigual()
    {
        if (igual)
        {
            atrasoVisible = false;
        }
        else
        {
            atrasoVisible = true;
        }

    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaCicloinicial
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCicloinicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cicloInicial = registroAux.getCampos().get(numero).toString();
        cicloFinal = null;
        cargarListaciclofinal();
    }

    /**
     *
     * Metodo ejecutado al seleccionar una fila de la lista listaciclofinal
     *
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaciclofinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cicloFinal = registroAux.getCampos().get(numero).toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_ARBOL>
    // </METODOS_ARBOL>
    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable igual
     *
     * @return igual
     */
    public boolean getIgual()
    {
        return igual;
    }

    /**
     * Asigna la variable igual
     *
     * @param igual
     * Variable a asignar en igual
     */
    public void setIgual(boolean igual)
    {
        this.igual = igual;
    }

    /**
     * Retorna la variable cicloInicial
     *
     * @return cicloInicial
     */
    public String getCicloInicial()
    {
        return cicloInicial;
    }

    /**
     * Asigna la variable cicloInicial
     *
     * @param cicloInicial
     * Variable a asignar en cicloInicial
     */
    public void setCicloInicial(String cicloInicial)
    {
        this.cicloInicial = cicloInicial;
    }

    /**
     * Retorna la variable cicloFinal
     *
     * @return cicloFinal
     */
    public String getCicloFinal()
    {
        return cicloFinal;
    }

    /**
     * Asigna la variable cicloFinal
     *
     * @param cicloFinal
     * Variable a asignar en cicloFinal
     */
    public void setCicloFinal(String cicloFinal)
    {
        this.cicloFinal = cicloFinal;
    }

    /**
     * Retorna la variable periodoAtraso
     *
     * @return periodoAtraso
     */
    public String getPeriodoAtraso()
    {
        return periodoAtraso;
    }

    /**
     * Asigna la variable periodoAtraso
     *
     * @param periodoAtraso
     * Variable a asignar en periodoAtraso
     */
    public void setPeriodoAtraso(String periodoAtraso)
    {
        this.periodoAtraso = periodoAtraso;
    }

    /**
     * Retorna la variable atrasoVisible
     *
     * @return atrasoVisible
     */
    public boolean isAtrasoVisible()
    {
        return atrasoVisible;
    }

    /**
     * Asigna la variable atrasoVisible
     *
     * @param atrasoVisible
     * Variable a asignar en atrasoVisible
     */
    public void setAtrasoVisible(boolean atrasoVisible)
    {
        this.atrasoVisible = atrasoVisible;
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
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCicloinicial() {
        return listaCicloinicial;
    }

    public void setListaCicloinicial(RegistroDataModelImpl listaCicloinicial) {
        this.listaCicloinicial = listaCicloinicial;
    }

    public RegistroDataModelImpl getListaciclofinal() {
        return listaciclofinal;
    }

    public void setListaciclofinal(RegistroDataModelImpl listaciclofinal) {
        this.listaciclofinal = listaciclofinal;
    }
    
    // </SET_GET_LISTAS_COMBO_GRANDE>
}

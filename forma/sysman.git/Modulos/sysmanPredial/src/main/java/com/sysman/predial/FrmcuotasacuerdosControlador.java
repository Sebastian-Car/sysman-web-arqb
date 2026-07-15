package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.enums.FrmcuotasacuerdosControladorEnum;
import com.sysman.predial.enums.FrmcuotasacuerdosControladorUrlEnum;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ybecerra
 * @version 1, 26/05/2016
 * 
 * @author ybecerra
 * @version 2, 30/06/2017, proceso de Refactoring y revisi�n sonar
 * 
 */
@ManagedBean
@ViewScoped

public class FrmcuotasacuerdosControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la
     * compania en la cual inicio sesion el usuario, el valor de esta
     * constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante definida para almacenar el codigo del modulo por el
     * cual se ingresa en la aplicacion
     */
    private final String modulo;
    private final String tbTb414;
    // <DECLARAR_ATRIBUTOS>
    private boolean acuerdos;
    private boolean predios;
    private boolean vencimiento;
    private String estado;
    private String bancoInicial;
    private String bancoFinal;
    private String acuerdoInicial;
    private String acuerdoFinal;
    private String predioInicial;
    private String predioFinal;

    private String comboActivo;

    private Date fechaInicial;
    private Date fechaFinal;
    private boolean predialVisible;
    private boolean acuerdoVisible;
    private boolean pagoVisible;
    private boolean pendientesVisible;
    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
     */
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listacmbbancoinicial;
    private RegistroDataModelImpl listacmbbancofinal;
    private RegistroDataModelImpl listaCmbAcuerdoInicial;
    private RegistroDataModelImpl listaCmbAcuerdoFinal;
    private RegistroDataModelImpl listacmbPredioInicial;
    private RegistroDataModelImpl listaCmbPredioFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Crea una nueva instancia de FrmcuotasacuerdosControlador
     */
    public FrmcuotasacuerdosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        tbTb414 = "TB_TB414";

        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRMCUOTASACUERDOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(FrmcuotasacuerdosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
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
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListacmbbancoinicial();
        cargarListaCmbAcuerdoInicial();
        cargarListacmbPredioInicial();

        // </CARGAR_LISTA_COMBO_GRANDE>
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
        acuerdos = true;
        acuerdoVisible = true;
        estado = "P";
        pagoVisible = true;
        fechaInicial = fechaFinal = new Date();
        combosValidar();
        /*
        
         */
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    /**
     * 
     * Carga la lista listacmbbancoinicial
     */
    public void cargarListacmbbancoinicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcuotasacuerdosControladorUrlEnum.URL4397
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacmbbancoinicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmcuotasacuerdosControladorEnum.PARAM0.getValue());
    }

    /**
     * 
     * Carga la lista listacmbbancofinal
     */
    public void cargarListacmbbancofinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcuotasacuerdosControladorUrlEnum.URL4969
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmcuotasacuerdosControladorEnum.PARAM1.getValue(),
                        bancoInicial);

        listacmbbancofinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmcuotasacuerdosControladorEnum.PARAM0.getValue());

    }

    /**
     * 
     * Carga la lista listaCmbAcuerdoInicial
     */
    public void cargarListaCmbAcuerdoInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcuotasacuerdosControladorUrlEnum.URL5637
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCmbAcuerdoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmcuotasacuerdosControladorEnum.PARAM2.getValue());

    }

    /**
     * 
     * Carga la lista listaCmbAcuerdoFinal
     */
    public void cargarListaCmbAcuerdoFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcuotasacuerdosControladorUrlEnum.URL6362
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmcuotasacuerdosControladorEnum.PARAM3.getValue(),
                        acuerdoInicial);

        listaCmbAcuerdoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true,
                        FrmcuotasacuerdosControladorEnum.PARAM2.getValue());
    }

    /**
     * 
     * Carga la lista listacmbPredioInicial
     */
    public void cargarListacmbPredioInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcuotasacuerdosControladorUrlEnum.URL7207
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listacmbPredioInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    /**
     * 
     * Carga la lista listaCmbPredioFinal
     */
    public void cargarListaCmbPredioFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmcuotasacuerdosControladorUrlEnum.URL7946
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(FrmcuotasacuerdosControladorEnum.PARAM4.getValue(),
                        predioInicial);

        listaCmbPredioFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, GeneralParameterEnum.CODIGO.getName());

    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    /**
     * 
     * Metodo ejecutado al oprimir el boton Presentar en la vista
     *
     */
    public void oprimirPresentar()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    /**
     * 
     * Metodo ejecutado al oprimir el boton Excel en la vista
     *
     */
    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(ReportesBean.FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar el control ChkAcuerdos
     * 
     */
    public void cambiarChkAcuerdos()
    {
        acuerdoVisible = acuerdos;
        predialVisible = !acuerdos;
        if (acuerdos)
        {
            predios = false;
            bancoInicial = null;
            bancoFinal = null;

        }
        else
        {
            predios = true;
        }
        combosValidar();

    }

    /**
     * Metodo ejecutado al cambiar el control ChkPredios
     * 
     */
    public void cambiarChkPredios()
    {
        predialVisible = predios;
        acuerdoVisible = !predios;
        if (predios)
        {
            acuerdos = false;
            predialVisible = true;
            bancoInicial = null;
            bancoFinal = null;
        }
        else
        {
            acuerdos = true;
        }

        combosValidar();
    }

    /**
     * Metodo ejecutado al cambiar el control cmbestado
     * 
     */
    public void cambiarcmbestado()
    {
        pagoVisible = "P".equals(estado);
        pendientesVisible = "D".equals(estado);

        if ("D".equals(estado))
        {
            vencimiento = false;
        }
    }

    // </METODOS_CAMBIAR>
    /**
     * Metodo ejecutado al cambiar seleccion de casilla de
     * verificacion del formulario
     */
    private void combosValidar()
    {
        comboActivo = acuerdos ? "'CB2664','CB2665'" : "'CB2666','CB2667'";
    }

    /**
     * Metodo ejecutado al oprimir los botones PDF o Excel
     * 
     * @param formato
     */
    public void generarInforme(ReportesBean.FORMATOS formato)
    {
        try
        {
            String condicion = "";
            String titulo;

            if ("P".equals(estado))
            {
                titulo = idioma.getString("OD_CB2661_0").toUpperCase();

            }
            else
            {
                titulo = idioma.getString("OD_CB2661_1").toUpperCase();
            }
            condicion = evaluarCondicionAcuerdosEstadoP(condicion);
            condicion = evaluarCondicionAcuerdosEstadoD(condicion);
            condicion = evaluarCondicionPrediosEstadoP(condicion);
            condicion = evaluarCondicionPrediosEstadoD(condicion);

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("condicion", condicion);
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso()
                            .getNombre().toUpperCase());
            parametros.put("PR_TITULO",
                            idioma.getString("TB_TB427") + " " + titulo);
            parametros.put("PR_FECHAS", "ENTRE EL "
                + SysmanFunciones.convertirAFechaCadena(fechaInicial) + " Y EL "
                + SysmanFunciones.convertirAFechaCadena(fechaFinal));

            Reporteador.resuelveConsulta("000823INFCUOTASACUERDOS",
                            Integer.parseInt(modulo), reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(
                            "000823INFCUOTASACUERDOS", parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException | ParseException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    /**
     * 
     * @param condicion
     * @return
     */
    public String evaluarCondicionAcuerdosEstadoP(String condicion)
    {
        String condicionAux = condicion;
        if (acuerdos && "P".equals(estado))
        {
            if (validarCampos()
                || SysmanFunciones.validarVariableVacio(acuerdoInicial)
                || SysmanFunciones.validarVariableVacio(acuerdoFinal))
            {
                JsfUtil.agregarMensajeError(idioma.getString(tbTb414));
            }
            else
            {
                String[] cond = { " AND IP_ACUERDOS.ANULADO IN (0) \n",
                                  "  AND IP_FACTURADOSACUERDOS.PAGADO NOT IN (0) \n",
                                  "  AND IP_FACTURADOSACUERDOS.PAG_BAN BETWEEN '",
                                  bancoInicial, "'   AND '", bancoFinal, "' \n",
                                  "  AND IP_FACTURADOSACUERDOS.FECHAPAGO BETWEEN "
                                      + SysmanFunciones
                                                      .formatearFecha(fechaInicial),
                                  " AND    "
                                      + SysmanFunciones
                                                      .formatearFecha(fechaFinal),
                                  "\n" +
                                      "  AND IP_ACUERDOS.CODIGOACUERDO BETWEEN '",
                                  acuerdoInicial
                                      + "'  AND      '",
                                  acuerdoFinal, "' "

                };
                condicionAux = SysmanFunciones.concatenar(cond);
            }
        }
        return condicionAux;
    }

    /**
     * Metodo para validar campos vacios
     * 
     * @return
     */
    private boolean validarCampos()
    {
        if ((fechaInicial == null) || (fechaFinal == null))
        {
            return true;
        }
        if (SysmanFunciones.validarVariableVacio(bancoInicial)
            || SysmanFunciones.validarVariableVacio(bancoFinal))
        {
            return true;
        }
        return false;

    }

    /**
     * @param condicion
     * @return
     */
    public String evaluarCondicionAcuerdosEstadoD(String condicion)
    {
        String condicionAux = condicion;
        if (acuerdos && "D".equals(estado))
        {
            if ((fechaInicial == null) || (fechaFinal == null)
                || (acuerdoInicial.isEmpty()) || (acuerdoFinal.isEmpty()))
            {
                JsfUtil.agregarMensajeError(idioma.getString(tbTb414));
            }
            else
            {
                condicionAux = "AND IP_ACUERDOS.ANULADO  IN (0) \n" +
                    "   AND IP_ACUERDOS.CANCELADO IN (0)  \n" +
                    "   AND IP_FACTURADOSACUERDOS.PAGADO IN (0) \n" +
                    "   AND IP_ACUERDOS.CODIGOACUERDO BETWEEN '"
                    + acuerdoInicial + "'  AND '" + acuerdoFinal + "' ";

                if (vencimiento)
                {
                    String[] cond = { condicionAux,
                                      " AND IP_FACTURADOSACUERDOS.FECHAFACTURADO BETWEEN ",
                                      SysmanFunciones.formatearFecha(
                                                      fechaInicial),
                                      " AND          ", SysmanFunciones
                                                      .formatearFecha(fechaFinal),
                                      " "

                    };
                    condicionAux = SysmanFunciones.concatenar(cond);
                }
                else
                {
                    String[] cond = { condicionAux,
                                      " AND IP_ACUERDOS.FECHAACUERDO BETWEEN ",
                                      SysmanFunciones.formatearFecha(
                                                      fechaInicial),
                                      " AND      ",
                                      SysmanFunciones.formatearFecha(
                                                      fechaFinal),
                                      " "

                    };
                    condicionAux = SysmanFunciones.concatenar(cond);
                }
            }

        }
        return condicionAux;
    }

    /**
     * @param condicion
     * @return
     */
    public String evaluarCondicionPrediosEstadoP(String condicion)
    {
        String condicionAux = condicion;
        if (predios && "P".equals(estado))
        {
            if (validarCampos()
                || SysmanFunciones.validarVariableVacio(predioInicial)
                || SysmanFunciones.validarVariableVacio(predioFinal))
            {
                JsfUtil.agregarMensajeError(idioma.getString(tbTb414));
            }
            else
            {
                String[] cond = { " AND IP_ACUERDOS.ANULADO IN (0)  \n",
                                  "  AND IP_FACTURADOSACUERDOS.PAGADO NOT IN (0) \n",
                                  "  AND IP_FACTURADOSACUERDOS.PAG_BAN BETWEEN '",
                                  bancoInicial, "'  AND '", bancoFinal, "' \n" +
                                      "  AND IP_FACTURADOSACUERDOS.FECHAPAGO BETWEEN ",
                                  SysmanFunciones.formatearFecha(fechaInicial),
                                  " AND     ",
                                  SysmanFunciones.formatearFecha(fechaFinal),
                                  " \n",
                                  "  AND IP_FACTURADOSACUERDOS.PREDIO BETWEEN '",
                                  predioInicial, "' AND '", predioFinal, "'"

                };

                condicionAux = SysmanFunciones.concatenar(cond);
            }
        }
        return condicionAux;
    }

    /**
     * @param condicion
     * @return
     */
    public String evaluarCondicionPrediosEstadoD(String condicion)
    {
        String condicionAux = condicion;
        if (predios && "D".equals(estado))
        {
            if ((fechaInicial == null) || (fechaFinal == null)
                || (predioInicial.isEmpty()) || (predioFinal.isEmpty()))
            {
                JsfUtil.agregarMensajeError(idioma.getString(tbTb414));
            }
            else
            {
                String[] cond = { " AND IP_ACUERDOS.ANULADO IN (0) \n" +
                    "  AND IP_ACUERDOS.CANCELADO IN (0) \n" +
                    "  AND IP_FACTURADOSACUERDOS.PAGADO IN(0) \n" +
                    "  AND IP_FACTURADOSACUERDOS.PREDIO BETWEEN '",
                                  predioInicial, "' AND '", predioFinal, "' "

                };

                condicionAux = SysmanFunciones.concatenar(cond);
                if (vencimiento)
                {
                    String[] con = { condicionAux,
                                     "   AND IP_FACTURADOSACUERDOS.FECHAFACTURADO BETWEEN ",
                                     SysmanFunciones.formatearFecha(
                                                     fechaInicial),
                                     " AND ",
                                     SysmanFunciones.formatearFecha(fechaFinal),
                                     " "

                    };
                    condicionAux = SysmanFunciones.concatenar(con);
                }
                else
                {
                    String[] condi = { condicionAux,
                                       " AND IP_ACUERDOS.FECHAACUERDO BETWEEN ",
                                       SysmanFunciones.formatearFecha(
                                                       fechaInicial),
                                       " AND ", SysmanFunciones.formatearFecha(
                                                       fechaFinal),
                                       " " };

                    condicionAux = SysmanFunciones.concatenar(condi);

                }
            }
        }
        return condicionAux;
    }

    // <METODOS_COMBOS_GRANDES>
    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbbancoinicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbbancoinicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        bancoInicial = registroAux.getCampos()
                        .get(FrmcuotasacuerdosControladorEnum.PARAM0.getValue())
                        .toString();
        bancoFinal = null;
        cargarListacmbbancofinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbbancofinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        bancoFinal = registroAux.getCampos()
                        .get(FrmcuotasacuerdosControladorEnum.PARAM0.getValue())
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbAcuerdoInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbAcuerdoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        acuerdoInicial = registroAux.getCampos()
                        .get(FrmcuotasacuerdosControladorEnum.PARAM2.getValue())
                        .toString();
        acuerdoFinal = null;
        cargarListaCmbAcuerdoFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbAcuerdoFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbAcuerdoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        acuerdoFinal = registroAux.getCampos()
                        .get(FrmcuotasacuerdosControladorEnum.PARAM2.getValue())
                        .toString();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listacmbPredioInicial
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilacmbPredioInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        predioInicial = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
        predioFinal = null;
        cargarListaCmbPredioFinal();
    }

    /**
     * 
     * Metodo ejecutado al seleccionar una fila de la lista
     * listaCmbPredioFinal
     *
     * @param event
     * objeto que encapsula la accion proveniente de la vista
     */
    public void seleccionarFilaCmbPredioFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        predioFinal = registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()).toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    /**
     * Retorna la variable acuerdos
     * 
     * @return acuerdos
     */
    public boolean getAcuerdos()
    {
        return acuerdos;
    }

    /**
     * Asigna la variable acuerdos
     * 
     * @param acuerdos
     * Variable a asignar en acuerdos
     */
    public void setAcuerdos(boolean acuerdos)
    {
        this.acuerdos = acuerdos;
    }

    /**
     * Retorna la variable predios
     * 
     * @return predios
     */
    public boolean getPredios()
    {
        return predios;
    }

    /**
     * Asigna la variable predios
     * 
     * @param predios
     * Variable a asignar en predios
     */
    public void setPredios(boolean predios)
    {
        this.predios = predios;
    }

    /**
     * Retorna la variable vencimiento
     * 
     * @return vencimiento
     */
    public boolean getVencimiento()
    {
        return vencimiento;
    }

    /**
     * Asigna la variable vencimiento
     * 
     * @param vencimiento
     * Variable a asignar en vencimiento
     */
    public void setVencimiento(boolean vencimiento)
    {
        this.vencimiento = vencimiento;
    }

    /**
     * Retorna la variable estado
     * 
     * @return estado
     */
    public String getEstado()
    {
        return estado;
    }

    /**
     * Asigna la variable estado
     * 
     * @param estado
     * Variable a asignar en estado
     */
    public void setEstado(String estado)
    {
        this.estado = estado;
    }

    /**
     * Retorna la variable bancoInicial
     * 
     * @return bancoInicial
     */
    public String getBancoInicial()
    {
        return bancoInicial;
    }

    /**
     * Asigna la variable bancoInicial
     * 
     * @param bancoInicial
     * Variable a asignar en bancoInicial
     */
    public void setBancoInicial(String bancoInicial)
    {
        this.bancoInicial = bancoInicial;
    }

    /**
     * Retorna la variable bancoFinal
     * 
     * @return bancoFinal
     */
    public String getBancoFinal()
    {
        return bancoFinal;
    }

    /**
     * Asigna la variable bancoFinal
     * 
     * @param bancoFinal
     * Variable a asignar en bancoFinal
     */
    public void setBancoFinal(String bancoFinal)
    {
        this.bancoFinal = bancoFinal;
    }

    /**
     * Retorna la variable acuerdoInicial
     * 
     * @return acuerdoInicial
     */
    public String getAcuerdoInicial()
    {
        return acuerdoInicial;
    }

    /**
     * Asigna la variable acuerdoInicial
     * 
     * @param acuerdoInicial
     * Variable a asignar en acuerdoInicial
     */
    public void setAcuerdoInicial(String acuerdoInicial)
    {
        this.acuerdoInicial = acuerdoInicial;
    }

    /**
     * Retorna la variable acuerdoFinal
     * 
     * @return acuerdoFinal
     */
    public String getAcuerdoFinal()
    {
        return acuerdoFinal;
    }

    /**
     * Asigna la variable acuerdoFinal
     * 
     * @param acuerdoFinal
     * Variable a asignar en acuerdoFinal
     */
    public void setAcuerdoFinal(String acuerdoFinal)
    {
        this.acuerdoFinal = acuerdoFinal;
    }

    /**
     * Retorna la variable predioInicial
     * 
     * @return predioInicial
     */
    public String getPredioInicial()
    {
        return predioInicial;
    }

    /**
     * Asigna la variable predioInicial
     * 
     * @param predioInicial
     * Variable a asignar en predioInicial
     */
    public void setPredioInicial(String predioInicial)
    {
        this.predioInicial = predioInicial;
    }

    /**
     * Retorna la variable predioFinal
     * 
     * @return predioFinal
     */
    public String getPredioFinal()
    {
        return predioFinal;
    }

    /**
     * Asigna la variable predioFinal
     * 
     * @param predioFinal
     * Variable a asignar en predioFinal
     */
    public void setPredioFinal(String predioFinal)
    {
        this.predioFinal = predioFinal;
    }

    /**
     * Retorna la variable fechaInicial
     * 
     * @return fechaInicial
     */
    public Date getFechaInicial()
    {
        return fechaInicial;
    }

    /**
     * Asigna la variable fechaInicial
     * 
     * @param fechaInicial
     * Variable a asignar en fechaInicial
     */
    public void setFechaInicial(Date fechaInicial)
    {
        this.fechaInicial = fechaInicial;
    }

    /**
     * Retorna la variable fechaFinal
     * 
     * @return fechaFinal
     */
    public Date getFechaFinal()
    {
        return fechaFinal;
    }

    /**
     * Asigna la variable fechaFinal
     * 
     * @param fechaFinal
     * Variable a asignar en fechaFinal
     */
    public void setFechaFinal(Date fechaFinal)
    {
        this.fechaFinal = fechaFinal;
    }

    /**
     * Retorna la variable predialVisible
     * 
     * @return predialVisible
     */
    public boolean isPredialVisible()
    {
        return predialVisible;
    }

    /**
     * Asigna la variable predialVisible
     * 
     * @param predialVisible
     * Variable a asignar en predialVisible
     */
    public void setPredialVisible(boolean predialVisible)
    {
        this.predialVisible = predialVisible;
    }

    /**
     * Retorna la variable acuerdoVisible
     * 
     * @return acuerdoVisible
     */
    public boolean isAcuerdoVisible()
    {
        return acuerdoVisible;
    }

    /**
     * Asigna la variable acuerdoVisible
     * 
     * @param acuerdoVisible
     * Variable a asignar en acuerdoVisible
     */
    public void setAcuerdoVisible(boolean acuerdoVisible)
    {
        this.acuerdoVisible = acuerdoVisible;
    }

    public boolean isPagoVisible()
    {
        return pagoVisible;
    }

    public void setPagoVisible(boolean pagoVisible)
    {
        this.pagoVisible = pagoVisible;
    }

    public boolean isPendientesVisible()
    {
        return pendientesVisible;
    }

    public void setPendientesVisible(boolean pendientesVisible)
    {
        this.pendientesVisible = pendientesVisible;
    }

    public String getComboActivo()
    {
        return comboActivo;
    }

    public void setComboActivo(String comboActivo)
    {
        this.comboActivo = comboActivo;
    }

    /**
     * Atributo usado para descargar contenidos de archivos desde la
     * vista
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
    /**
     * Retorna la lista listacmbbancoinicial
     * 
     * @return listacmbbancoinicial
     */
    public RegistroDataModelImpl getListacmbbancoinicial()
    {
        return listacmbbancoinicial;
    }

    /**
     * Asigna la lista listacmbbancoinicial
     * 
     * @param listacmbbancoinicial
     * Variable a asignar en listacmbbancoinicial
     */
    public void setListacmbbancoinicial(
        RegistroDataModelImpl listacmbbancoinicial)
    {
        this.listacmbbancoinicial = listacmbbancoinicial;
    }

    /**
     * Retorna la lista listacmbbancofinal
     * 
     * @return listacmbbancofinal
     */
    public RegistroDataModelImpl getListacmbbancofinal()
    {
        return listacmbbancofinal;
    }

    /**
     * Asigna la lista listacmbbancofinal
     * 
     * @param listacmbbancofinal
     * Variable a asignar en listacmbbancofinal
     */
    public void setListacmbbancofinal(RegistroDataModelImpl listacmbbancofinal)
    {
        this.listacmbbancofinal = listacmbbancofinal;
    }

    /**
     * Retorna la lista listaCmbAcuerdoInicial
     * 
     * @return listaCmbAcuerdoInicial
     */
    public RegistroDataModelImpl getListaCmbAcuerdoInicial()
    {
        return listaCmbAcuerdoInicial;
    }

    /**
     * Asigna la lista listaCmbAcuerdoInicial
     * 
     * @param listaCmbAcuerdoInicial
     * Variable a asignar en listaCmbAcuerdoInicial
     */
    public void setListaCmbAcuerdoInicial(
        RegistroDataModelImpl listaCmbAcuerdoInicial)
    {
        this.listaCmbAcuerdoInicial = listaCmbAcuerdoInicial;
    }

    /**
     * Retorna la lista listaCmbAcuerdoFinal
     * 
     * @return listaCmbAcuerdoFinal
     */
    public RegistroDataModelImpl getListaCmbAcuerdoFinal()
    {
        return listaCmbAcuerdoFinal;
    }

    /**
     * Asigna la lista listaCmbAcuerdoFinal
     * 
     * @param listaCmbAcuerdoFinal
     * Variable a asignar en listaCmbAcuerdoFinal
     */
    public void setListaCmbAcuerdoFinal(
        RegistroDataModelImpl listaCmbAcuerdoFinal)
    {
        this.listaCmbAcuerdoFinal = listaCmbAcuerdoFinal;
    }

    /**
     * Retorna la lista listacmbPredioInicial
     * 
     * @return listacmbPredioInicial
     */
    public RegistroDataModelImpl getListacmbPredioInicial()
    {
        return listacmbPredioInicial;
    }

    /**
     * Asigna la lista listacmbPredioInicial
     * 
     * @param listacmbPredioInicial
     * Variable a asignar en listacmbPredioInicial
     */
    public void setListacmbPredioInicial(
        RegistroDataModelImpl listacmbPredioInicial)
    {
        this.listacmbPredioInicial = listacmbPredioInicial;
    }

    /**
     * Retorna la lista listaCmbPredioFinal
     * 
     * @return listaCmbPredioFinal
     */
    public RegistroDataModelImpl getListaCmbPredioFinal()
    {
        return listaCmbPredioFinal;
    }

    /**
     * Asigna la lista listaCmbPredioFinal
     * 
     * @param listaCmbPredioFinal
     * Variable a asignar en listaCmbPredioFinal
     */
    public void setListaCmbPredioFinal(
        RegistroDataModelImpl listaCmbPredioFinal)
    {
        this.listaCmbPredioFinal = listaCmbPredioFinal;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}

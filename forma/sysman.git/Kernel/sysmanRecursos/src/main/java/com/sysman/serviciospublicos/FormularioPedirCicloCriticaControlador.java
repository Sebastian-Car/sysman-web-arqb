/*-
 * FormularioPedirCicloCriticaControlador.java
 *
 * 1.0
 *
 * 22/09/2016
 *
 * Copyright (c) 2016 Stefanini Sysman.
 * Paipa, Boyaca.
 * All rights reserved.
 */
package com.sysman.serviciospublicos;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.componentes.Direccionador;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.serviciospublicos.enums.FormularioPedirCicloCriticaControladorEnum;
import com.sysman.serviciospublicos.enums.FormularioPedirCicloCriticaControladorUrlEnum;
import com.sysman.servpublicos.ejb.EjbServiciosPublicosCeroGeneralRemote;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 * Esta clase es el controlador de la forma formulariopedirciclo, llamada desde Panel Principal/Facturacion de Servicios Publicos/Novedades/Correccion de Critica, y prepara los datos para la
 * correccion de critica.
 *
 * @version 1, 23/09/2016 08:57:13 -- Modificado por dmaldonado
 * @author dmaldonado
 *
 * @version 2.0, 11/05/2017, pespitia:<br>
 * Refactoring.<br>
 * Manejo de EJBs.
 *
 * -- Modificado por lcortes 12/06/2017. Se reemplaza el valor del atributo numero de formulario por el enumerado correspondiente. Cambio en la concatenacion de las cadenas que envian los parametros a
 * las funciones.
 *
 * @version 3, 31/07/2017
 * @author spina - se modifica el envio de parametros al redireccionar al formulario FORMULARIO_CORRECCION_CRITICA - 1116 de tal forma que la consulta y los procesos sean realizados en ese formulario
 *
 */
@ManagedBean
@ViewScoped
public class FormularioPedirCicloCriticaControlador extends BeanBaseModal
{

    // <DECLARAR_ATRIBUTOS>
    /**
     * Atributo que contiene el valor de la compañia con la que se inicia sesion
     */
    private final String compania;
    /**
     * Atributo que contiene el valor del modulo que se esta trabajando
     */
    private final String modulo;
    /**
     * Atributo que contiene el valor del check de Suscriptores Sin Critica.
     */
    private boolean normales;
    /**
     * Atributo que contiene el valor del check de Excluir Lecturas Iguales.
     */
    private boolean iguales;
    /**
     * Atributo que contiene el valor del check de Excluir Consumos Manuales.
     */
    private boolean manual;
    /**
     * Atributo que contiene el valor del check de Desviaci n Significativa Anterior.
     */
    private boolean desviacion;
    /**
     * Atributo que contiene el valor del combobox de Ciclo.
     */
    private String ciclo;
    /**
     * Atributo que contiene el valor del combobox de C digo de Ruta Inicial
     */
    private String codigoInicial;
    /**
     * Atributo que contiene el valor del combobox de C digo de Ruta Inicial
     */
    private String codigoFinal;
    /**
     * Atributo que contiene el valor del campo de Porcentaje Menor de Critica.
     */
    private int porcentajeMenor;
    /**
     * Atributo que contiene el valor del campo de Consumo Menor de Critica.
     */
    private int consumoMenor;
    /**
     * Atributo que contiene el valor del campo de Porcentaje Mayor de Critica.
     */
    private int porcentajeMayor;
    /**
     * Atributo que contiene el valor del ano perteneciente al ciclo seleccionado.
     */
    private String anoCiclo;
    /**
     * Atributo que contiene el valor del periodo perteneciente al ciclo seleccionado.
     */
    private String periodoCiclo;
    /**
     * Atributo que contiene el valor de la fecha de preparaci n perteneciente al ciclo seleccionado.
     */
    private Date fechaPreparacionCiclo;
    /**
     * Atributo que contiene el valor del indicador "Prefacturando" del ciclo seleccionado.
     */
    private boolean prefacturandoCiclo;

    /**
     * Constante a nivel de clase que aloja el nombre del enumerado <code>GeneralParameterEnum.CODIGORUTA</code>
     */
    private String strCodigoRuta;
    /**
     * Atributo que contiene la frecuencia proveniente del par metro FRECUENCIA PERIODOS DE FACTURACION.
     */
    private String frecuencia;
    /**
     * Atributo que define si el indicador y la etiqueta de Excluir Consumos manuales estar visible.
     */
    private boolean visibleManual;
    /**
     * Atributo que define si el boton de Calcular Micromedicion estara visible.
     */
    private boolean visibleMicro;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    /**
     * Atributo que contiene los datos de la p gina actual en la listaCiclo.
     */
    private RegistroDataModelImpl listaCiclo;
    /**
     * Atributo que contiene los datos de la pgina actual en la listaMiCodigoInicial.
     */
    private RegistroDataModelImpl listaMiCodigoInicial;
    /**
     * Atributo que contiene los datos de la pgina actual en la listaMiCodigoFinal.
     */
    private RegistroDataModelImpl listaMiCodigoFinal;

    // </DECLARAR_LISTAS_COMBO_GRANDE>

    // <EJBs>
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    @EJB
    private EjbServiciosPublicosCeroGeneralRemote ejbServiciosPublicosCeroGeneral;

    // </EJBs>

    // <PARAMETROS>
    // </PARAMETROS>

    /**
     * Creates a new instance of FormularioPedirCicloCriticaControlador
     */
    public FormularioPedirCicloCriticaControlador()
    {
        super();

        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        strCodigoRuta = GeneralParameterEnum.CODIGORUTA.getName();

        try
        {
            numFormulario = GeneralCodigoFormaEnum.FORMULARIO_PEDIR_CICLO_CRITICA_CONTROLADOR
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

    @PostConstruct
    public void inicializar()
    {
        // <CARGAR_LISTA>

        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCiclo();

        consumoMenor = 40;
        porcentajeMenor = 65;
        porcentajeMayor = 35;

        cargarListaMiCodigoInicial();
        cargarListaMiCodigoFinal();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        try
        {
            frecuencia = recuperarValorPar("FRECUENCIA PERIODOS DE FACTURACION",
                            true);

            visibleManual = "SI".equals(SysmanFunciones.nvl(
                            recuperarValorPar("MANEJA CONSUMO MANUAL", true),
                            "NO").toString());

            visibleMicro = ejbServiciosPublicosCeroGeneral
                            .autorizarMicromedicion(compania,
                                            SessionUtil.getCompaniaIngreso()
                                                            .getNit());

        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    /**
     * Util para verificar que el parametro {@code nomPar} existe en la base de datos. De lo contrario muestra un mensaje informativo.
     *
     * @param nomPar
     * Nombre del parametro.
     * @param valor
     * Valor asignado al parametro en la base de datos.
     * @return {@code true}: si el parametro existe y tiene valor diferente a nulo.
     */
    private boolean validarParametro(String nomPar, String valor)
    {
        if (SysmanFunciones.validarVariableVacio(valor))
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2908")
                            .replace("#PAR#", nomPar));
            return false;
        }

        return true;
    }

    // <METODOS_CARGAR_LISTA>

    public void cargarListaCiclo()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FormularioPedirCicloCriticaControladorUrlEnum.URL0001
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);

        listaCiclo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "NUMERO");

    }

    public void cargarListaMiCodigoInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FormularioPedirCicloCriticaControladorUrlEnum.URL0002
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);

        listaMiCodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigoRuta);
    }

    public void cargarListaMiCodigoFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FormularioPedirCicloCriticaControladorUrlEnum.URL0003
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.CICLO.getName(), ciclo);
        param.put(FormularioPedirCicloCriticaControladorEnum.CODIGOINICIAL
                        .getValue(), codigoInicial);

        listaMiCodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigoRuta);
    }

    // </METODOS_CARGAR_LISTA>

    public boolean permitePrepararCritica()
    {
        try
        {
            if ("SI".equals(SysmanFunciones.nvlStr(
                            recuperarValorPar("MANEJA PREFACTURACIÓN", true),
                            "NO"))
                && prefacturandoCiclo)
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1636"));
                return false;
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
            return false;
        }

        if (fechaPreparacionCiclo == null)
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1639"));
            return false;
        }
        return true;
    }

    /**
     * Consulta y retorna el valor asigando al parametro segun la base de datos.
     *
     * @param nombrePar
     * Nombre asignado al parametro
     * @param ind
     * Determina si el valor del parametro debe ser retornado en mayuscula sostenida.
     * @return El valor del parametro asignado en la base de datos.
     * @throws SystemException
     */
    private String recuperarValorPar(String nombrePar, boolean ind)
                    throws SystemException
    {
        String valor = ejbSysmanUtil.consultarParametro(compania, nombrePar,
                        modulo,
                        new Date(), ind);

        validarParametro(nombrePar, valor);

        return valor;
    }

    // <METODOS_BOTONES>
    public void oprimirAceptar()
    {
        // <CODIGO_DESARROLLADO>
        if (!permitePrepararCritica())
        {
            return;
        }

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("ciclo", ciclo);
        parametros.put("codigoInicial", codigoInicial);
        parametros.put("codigoFinal", codigoFinal);
        parametros.put("periodoCiclo", periodoCiclo);
        parametros.put("anoCiclo", anoCiclo);
        parametros.put("frecuencia", frecuencia);
        parametros.put("manual", manual);
        parametros.put("iguales", iguales);
        parametros.put("normales", normales);
        parametros.put("desviacion", desviacion);
        parametros.put("consumoMenor", consumoMenor);
        parametros.put("porcentajeMenor", porcentajeMenor);
        parametros.put("porcentajeMayor", porcentajeMayor);

        Direccionador direccionador = new Direccionador();
        direccionador.setNumForm(
                        String.valueOf(GeneralCodigoFormaEnum.FORMULARIO_CORRECCION_CRITICA
                                        .getCodigo()));
        direccionador.setParametros(parametros);
        RequestContext.getCurrentInstance().closeDialog(direccionador);

        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCancelar()
    {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirMicro()
    {
        // <CODIGO_DESARROLLADO>
        try
        {
            if ("SI".equals(SysmanFunciones.nvlStr(
                            recuperarValorPar("MANEJA PREFACTURACIÓN", true),
                            "NO"))
                && prefacturandoCiclo)
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1636"));
                return;
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        try
        {

            String mensaje = ejbServiciosPublicosCeroGeneral
                            .prepararAnoPeriodoSiguiente(
                                            compania,
                                            Integer.parseInt(anoCiclo),
                                            periodoCiclo, "/", "");

            int anoSig = Integer.parseInt(mensaje.split("/")[0]);
            String periodoSig = mensaje.split("/")[1];

            boolean generaConsumosMicro = ejbServiciosPublicosCeroGeneral
                            .generarMicroconsumos(mensaje,
                                            Integer.parseInt(ciclo),
                                            periodoSig, anoSig);

            if (generaConsumosMicro)
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1637"));
            }
            else
            {
                JsfUtil.agregarMensajeInformativo(
                                idioma.getString("TB_TB1638"));
            }
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarConsumoMenor()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCiclo(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        ciclo = SysmanFunciones.nvl(registroAux.getCampos().get("NUMERO"), "")
                        .toString();

        anoCiclo = SysmanFunciones.nvl(registroAux.getCampos().get("ANO"), "")
                        .toString();

        periodoCiclo = SysmanFunciones
                        .nvl(registroAux.getCampos().get("PERIODO"), "")
                        .toString();

        fechaPreparacionCiclo = (Date) registroAux.getCampos()
                        .get("FECHA_PREPARACION");

        prefacturandoCiclo = !"0".equals(SysmanFunciones.nvl(
                        registroAux.getCampos().get("PREFACTURANDO"), "0")
                        .toString());

        codigoInicial = codigoFinal = null;

        cargarListaMiCodigoInicial();
    }

    public void seleccionarFilaMiCodigoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        codigoInicial = SysmanFunciones
                        .nvl(registroAux.getCampos().get(strCodigoRuta), "")
                        .toString();

        codigoFinal = null;

        cargarListaMiCodigoFinal();
    }

    public void seleccionarFilaMiCodigoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();

        codigoFinal = SysmanFunciones
                        .nvl(registroAux.getCampos().get(strCodigoRuta), "")
                        .toString();
    }

    // </METODOS_COMBOS_GRANDES>
    // <SET_GET_ATRIBUTOS>
    public boolean getNormales()
    {
        return normales;
    }

    public void setNormales(boolean normales)
    {
        this.normales = normales;
    }

    public boolean getIguales()
    {
        return iguales;
    }

    public void setIguales(boolean iguales)
    {
        this.iguales = iguales;
    }

    public boolean getManual()
    {
        return manual;
    }

    public void setManual(boolean manual)
    {
        this.manual = manual;
    }

    public boolean getDesviacion()
    {
        return desviacion;
    }

    public void setDesviacion(boolean desviacion)
    {
        this.desviacion = desviacion;
    }

    public String getCiclo()
    {
        return ciclo;
    }

    public void setCiclo(String ciclo)
    {
        this.ciclo = ciclo;
    }

    public String getCodigoInicial()
    {
        return codigoInicial;
    }

    public void setCodigoInicial(String codigoInicial)
    {
        this.codigoInicial = codigoInicial;
    }

    public String getCodigoFinal()
    {
        return codigoFinal;
    }

    public void setCodigoFinal(String codigoFinal)
    {
        this.codigoFinal = codigoFinal;
    }

    public int getPorcentajeMenor()
    {
        return porcentajeMenor;
    }

    public void setPorcentajeMenor(int porcentajeMenor)
    {
        this.porcentajeMenor = porcentajeMenor;
    }

    public int getConsumoMenor()
    {
        return consumoMenor;
    }

    public void setConsumoMenor(int consumoMenor)
    {
        this.consumoMenor = consumoMenor;
    }

    public int getPorcentajeMayor()
    {
        return porcentajeMayor;
    }

    public void setPorcentajeMayor(int porcentajeMayor)
    {
        this.porcentajeMayor = porcentajeMayor;
    }

    public boolean isVisibleManual()
    {
        return visibleManual;
    }

    public void setVisibleManual(boolean visibleManual)
    {
        this.visibleManual = visibleManual;
    }

    public boolean isVisibleMicro()
    {
        return visibleMicro;
    }

    public void setVisibleMicro(boolean visibleMicro)
    {
        this.visibleMicro = visibleMicro;
    }
    // </SET_GET_ATRIBUTOS>

    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>

    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>

    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCiclo()
    {
        return listaCiclo;
    }

    public void setListaCiclo(RegistroDataModelImpl listaCiclo)
    {
        this.listaCiclo = listaCiclo;
    }

    public RegistroDataModelImpl getListaMiCodigoInicial()
    {
        return listaMiCodigoInicial;
    }

    public void setListaMiCodigoInicial(
        RegistroDataModelImpl listaMiCodigoInicial)
    {
        this.listaMiCodigoInicial = listaMiCodigoInicial;
    }

    public RegistroDataModelImpl getListaMiCodigoFinal()
    {
        return listaMiCodigoFinal;
    }

    public void setListaMiCodigoFinal(
        RegistroDataModelImpl listaMiCodigoFinal)
    {
        this.listaMiCodigoFinal = listaMiCodigoFinal;
    }

    // </SET_GET_LISTAS_COMBO_GRANDE>
}

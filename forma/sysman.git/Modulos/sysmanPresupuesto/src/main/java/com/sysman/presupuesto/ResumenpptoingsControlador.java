package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.presupuesto.enums.ResumenpptoingsControladorEnum;
import com.sysman.presupuesto.enums.ResumenpptoingsControladorUrlEnum;
import com.sysman.util.SysmanFunciones;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author NGOMEZ
 * @version 1, 21/06/2016
 * 
 * @version 2, 20/04/2017
 * @author jreina se realizaron los cambios de refactoring en el
 * metodo cargar datos.
 * 
 * @author jlramirez
 * @version 3, 24/04/2017, se cambiaron llamados de accion para
 * SYSDATE por new Date()
 * 
 * @author ybecerra
 * @version 4, 13/06/2017 Implementacion al llamado de
 * GeneralCodigoFormaEnum, para el codigo del formulario
 */
@ManagedBean
@ViewScoped
public class ResumenpptoingsControlador extends BeanBaseDatosAcmeImpl
{
    private final String compania;
    private final String modulo;
    // <DECLARAR_ATRIBUTOS>
    private String op1;
    private String op2;
    private String op3;
    private String op4;
    private String titulo;
    private String mesInicial;
    private String mesFinal;
    private String apropiado;
    private String adicion;
    private String reduccion;
    private String traslado;
    private String aplazamiento;
    private String aprDefinitiva;
    private String rezago;
    private String pacProgXEjec;
    private String porcApropiado;
    private String ejecucionCntF;
    private String saldoPorEjecutar;
    private String ingresosA;
    private String totalRecaudado;
    private String modificacionICAA;
    private String ingresosCausadosA;
    private String totalICAF;
    private String ingresosP;
    private String ingresosF;
    private String ingresosCausadosP;
    private String ingresosCausadosF;
    private String modificacionICAP;
    private String modificacionICAF;
    private String pacTotalA;
    private String pacTotalP;
    private String pacTotalF;
    private String apropiacionVigente;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    private String anio;
    private String codigo;
    private String nombre;
    private Map<String, Object> ridParam;
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of ResumenpptoingsControlador
     */
    public ResumenpptoingsControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        try
        {
            numFormulario = GeneralCodigoFormaEnum.RESUMENPPTOINGS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null)
            {
                anio = (String) parametrosEntrada.get("anio");
                codigo = (String) parametrosEntrada.get("codigo");
                nombre = (String) parametrosEntrada.get("nombre");
                ridParam = (Map<String, Object>) parametrosEntrada.get("rid");
            }
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(ResumenpptoingsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar()
    {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        mesInicial = "1";
        mesFinal = String
                        .valueOf(SysmanFunciones.getParteFecha(
                                        new Date(),
                                        Calendar.MONTH)
                            + 1);
        titulo = "RESUMEN PRESUPUESTAL \n RUBRO: " + codigo + " \n "
            + nombre.toUpperCase();
        cargarDatos();
        // </CODIGO_DESARROLLADO>
    }

    public void cargarModal()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPresentar()
    {
        JsfUtil.ejecutarJavaScript("window.print()");
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    public void cambiarMes()
    {
        // <CODIGO_DESARROLLADO>
        if ((mesInicial != null) && !mesInicial.isEmpty() && (mesFinal != null)
            && !mesFinal.isEmpty())
        {
            if (Integer.parseInt(mesInicial) <= Integer.parseInt(mesFinal))
            {
                cargarDatos();
            }
            else
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB259"));
                limpiarCampos();
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarMes1()
    {
        // <CODIGO_DESARROLLADO>
        if ((mesInicial != null) && !mesInicial.isEmpty() && (mesFinal != null)
            && !mesFinal.isEmpty())
        {
            if (Integer.parseInt(mesInicial) <= Integer.parseInt(mesFinal))
            {
                cargarDatos();
            }
            else
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB259"));
                limpiarCampos();
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cargarDatos()
    {
        try
        {
            Map<String, Object> param = new TreeMap<>();

            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), anio);
            param.put(GeneralParameterEnum.CODIGO.getName(), codigo);
            param.put(ResumenpptoingsControladorEnum.PARAM0.getValue(),
                            mesInicial);
            param.put(ResumenpptoingsControladorEnum.PARAM1.getValue(),
                            mesFinal);

            Registro regAux = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            ResumenpptoingsControladorUrlEnum.URL7543
                                                                            .getValue())
                                            .getUrl(), param));

            apropiado = regAux.getCampos().get("APRDEFINITIVA").toString();
            adicion = regAux.getCampos().get("ADICION").toString();
            reduccion = regAux.getCampos().get("REDUCCION").toString();
            traslado = regAux.getCampos().get("TRASLADO").toString();
            aplazamiento = regAux.getCampos().get("APLAZAMIENTO").toString();
            aprDefinitiva = regAux.getCampos().get("APRDEFINITIVACAMPO")
                            .toString();
            apropiacionVigente = regAux.getCampos().get("APROPIACIONVIGENTE")
                            .toString();
            ingresosA = regAux.getCampos().get("INGRESOSA").toString();
            ingresosP = regAux.getCampos().get("INGRESOSP").toString();
            ingresosF = regAux.getCampos().get("INGRESOSF").toString();
            ejecucionCntF = regAux.getCampos().get("EJECUCIONCNTF").toString();
            totalRecaudado = regAux.getCampos().get("TOTALRECAUDADO")
                            .toString();
            saldoPorEjecutar = regAux.getCampos().get("SALDOPOREJECUTAR")
                            .toString();
            pacTotalA = regAux.getCampos().get("PACTOTALA").toString();
            pacTotalP = regAux.getCampos().get("PACTOTALP").toString();
            pacTotalF = regAux.getCampos().get("PACTOTALF").toString();
            porcApropiado = regAux.getCampos().get("PORCAPROPIADO").toString();
            rezago = regAux.getCampos().get("REZAGO").toString();
            pacProgXEjec = regAux.getCampos().get("PACPROGXEJEC").toString();
            ingresosCausadosA = regAux.getCampos().get("INGRESOSCAUSADOSA")
                            .toString();
            ingresosCausadosP = regAux.getCampos().get("INGRESOSCAUSADOSP")
                            .toString();
            ingresosCausadosF = regAux.getCampos().get("INGRESOSCAUSADOSF")
                            .toString();
            modificacionICAA = regAux.getCampos().get("MODIFICACIONICAA")
                            .toString();
            modificacionICAP = regAux.getCampos().get("MODIFICACIONICAP")
                            .toString();
            modificacionICAF = regAux.getCampos().get("MODIFICACIONICAF")
                            .toString();
            totalICAF = regAux.getCampos().get("TOTALICAF").toString();
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void limpiarCampos()
    {
        apropiado = null;
        adicion = null;
        reduccion = null;
        traslado = null;
        aplazamiento = null;
        aprDefinitiva = null;
        apropiacionVigente = null;
        ingresosA = null;
        ingresosP = null;
        ingresosF = null;
        ejecucionCntF = null;
        totalRecaudado = null;
        saldoPorEjecutar = null;
        pacTotalA = null;
        pacTotalP = null;
        pacTotalF = null;
        porcApropiado = null;
        rezago = null;
        pacProgXEjec = null;
        ingresosCausadosA = null;
        ingresosCausadosP = null;
        ingresosCausadosF = null;
        modificacionICAA = null;
        modificacionICAP = null;
        modificacionICAF = null;
        totalICAF = null;
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getMesInicial()
    {
        return mesInicial;
    }

    public void setMesInicial(String mesInicial)
    {
        this.mesInicial = mesInicial;
    }

    public String getMesFinal()
    {
        return mesFinal;
    }

    public void setMesFinal(String mesFinal)
    {
        this.mesFinal = mesFinal;
    }

    public String getApropiado()
    {
        return apropiado;
    }

    public void setApropiado(String apropiado)
    {
        this.apropiado = apropiado;
    }

    public String getAdicion()
    {
        return adicion;
    }

    public void setAdicion(String adicion)
    {
        this.adicion = adicion;
    }

    public String getReduccion()
    {
        return reduccion;
    }

    public void setReduccion(String reduccion)
    {
        this.reduccion = reduccion;
    }

    public String getTraslado()
    {
        return traslado;
    }

    public void setTraslado(String traslado)
    {
        this.traslado = traslado;
    }

    public String getAplazamiento()
    {
        return aplazamiento;
    }

    public void setAplazamiento(String aplazamiento)
    {
        this.aplazamiento = aplazamiento;
    }

    public String getAprDefinitiva()
    {
        return aprDefinitiva;
    }

    public void setAprDefinitiva(String aprDefinitiva)
    {
        this.aprDefinitiva = aprDefinitiva;
    }

    public String getRezago()
    {
        return rezago;
    }

    public void setRezago(String rezago)
    {
        this.rezago = rezago;
    }

    public String getPacProgXEjec()
    {
        return pacProgXEjec;
    }

    public void setPacProgXEjec(String pacProgXEjec)
    {
        this.pacProgXEjec = pacProgXEjec;
    }

    public String getPorcApropiado()
    {
        return porcApropiado;
    }

    public void setPorcApropiado(String porcApropiado)
    {
        this.porcApropiado = porcApropiado;
    }

    public String getEjecucionCntF()
    {
        return ejecucionCntF;
    }

    public void setEjecucionCntF(String ejecucionCntF)
    {
        this.ejecucionCntF = ejecucionCntF;
    }

    public String getSaldoPorEjecutar()
    {
        return saldoPorEjecutar;
    }

    public void setSaldoPorEjecutar(String saldoPorEjecutar)
    {
        this.saldoPorEjecutar = saldoPorEjecutar;
    }

    public String getIngresosA()
    {
        return ingresosA;
    }

    public void setIngresosA(String ingresosA)
    {
        this.ingresosA = ingresosA;
    }

    public String getTotalRecaudado()
    {
        return totalRecaudado;
    }

    public void setTotalRecaudado(String totalRecaudado)
    {
        this.totalRecaudado = totalRecaudado;
    }

    public String getModificacionICAA()
    {
        return modificacionICAA;
    }

    public void setModificacionICAA(String modificacionICAA)
    {
        this.modificacionICAA = modificacionICAA;
    }

    public String getIngresosCausadosA()
    {
        return ingresosCausadosA;
    }

    public void setIngresosCausadosA(String ingresosCausadosA)
    {
        this.ingresosCausadosA = ingresosCausadosA;
    }

    public String getTotalICAF()
    {
        return totalICAF;
    }

    public void setTotalICAF(String totalICAF)
    {
        this.totalICAF = totalICAF;
    }

    public String getIngresosP()
    {
        return ingresosP;
    }

    public void setIngresosP(String ingresosP)
    {
        this.ingresosP = ingresosP;
    }

    public String getIngresosF()
    {
        return ingresosF;
    }

    public void setIngresosF(String ingresosF)
    {
        this.ingresosF = ingresosF;
    }

    public String getIngresosCausadosP()
    {
        return ingresosCausadosP;
    }

    public void setIngresosCausadosP(String ingresosCausadosP)
    {
        this.ingresosCausadosP = ingresosCausadosP;
    }

    public String getIngresosCausadosF()
    {
        return ingresosCausadosF;
    }

    public void setIngresosCausadosF(String ingresosCausadosF)
    {
        this.ingresosCausadosF = ingresosCausadosF;
    }

    public String getModificacionICAP()
    {
        return modificacionICAP;
    }

    public void setModificacionICAP(String modificacionICAP)
    {
        this.modificacionICAP = modificacionICAP;
    }

    public String getModificacionICAF()
    {
        return modificacionICAF;
    }

    public void setModificacionICAF(String modificacionICAF)
    {
        this.modificacionICAF = modificacionICAF;
    }

    public String getPacTotalA()
    {
        return pacTotalA;
    }

    public void setPacTotalA(String pacTotalA)
    {
        this.pacTotalA = pacTotalA;
    }

    public String getPacTotalP()
    {
        return pacTotalP;
    }

    public void setPacTotalP(String pacTotalP)
    {
        this.pacTotalP = pacTotalP;
    }

    public String getPacTotalF()
    {
        return pacTotalF;
    }

    public void setPacTotalF(String pacTotalF)
    {
        this.pacTotalF = pacTotalF;
    }

    public String getApropiacionVigente()
    {
        return apropiacionVigente;
    }

    public void setApropiacionVigente(String apropiacionVigente)
    {
        this.apropiacionVigente = apropiacionVigente;
    }

    public String getTitulo()
    {
        return titulo;
    }

    public void setTitulo(String titulo)
    {
        this.titulo = titulo;
    }

    public String getOp1()
    {
        return op1;
    }

    public void setOp1(String op1)
    {
        this.op1 = op1;
    }

    public String getOp2()
    {
        return op2;
    }

    public void setOp2(String op2)
    {
        this.op2 = op2;
    }

    public String getOp3()
    {
        return op3;
    }

    public void setOp3(String op3)
    {
        this.op3 = op3;
    }

    public String getOp4()
    {
        return op4;
    }

    public void setOp4(String op4)
    {
        this.op4 = op4;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    public String getAnio()
    {
        return anio;
    }

    public void setAnio(String anio)
    {
        this.anio = anio;
    }

    public String getCodigo()
    {
        return codigo;
    }

    public void setCodigo(String codigo)
    {
        this.codigo = codigo;
    }

    public String getNombre()
    {
        return nombre;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    @Override
    public Map<String, Object> getRid()
    {
        return ridParam;
    }

    @Override
    public void setRid(Map<String, Object> rid)
    {
        this.ridParam = rid;
    }

    @Override
    public void cargarRegistro()
    {
        // Metodo heredado de BeanBaseDatosAcme

    }

    @Override
    public void iniciarListasSubNulo()
    {
        // Metodo heredado de BeanBaseDatosAcme

    }

    @Override
    public void iniciarListasSub()
    {
        // Metodo heredado de BeanBaseDatosAcme

    }

    @Override
    public void iniciarListas()
    {
        // Metodo heredado de BeanBaseDatosAcme

    }

    @Override
    public void asignarOrigenDatos()
    {
        // Metodo heredado de BeanBaseDatosAcme

    }

    @Override
    public boolean insertarAntes()
    {
        // Metodo heredado de BeanBaseDatosAcme
        return false;
    }

    @Override
    public boolean insertarDespues()
    {
        // Metodo heredado de BeanBaseDatosAcme
        return false;
    }

    @Override
    public boolean actualizarAntes()
    {
        // Metodo heredado de BeanBaseDatosAcme
        return false;
    }

    @Override
    public boolean actualizarDespues()
    {
        // Metodo heredado de BeanBaseDatosAcme
        return false;
    }

    @Override
    public boolean eliminarAntes()
    {
        // Metodo heredado de BeanBaseDatosAcme
        return false;
    }

    @Override
    public boolean eliminarDespues()
    {
        // Metodo heredado de BeanBaseDatosAcme
        return false;
    }

    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public void ejecutarrcCerrar()
    {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "rid", "anio" };
        Object[] valores = { ridParam, anio };
        SessionUtil.redireccionarPorFormulario(modulo,
                        Integer.toString(
                                        GeneralCodigoFormaEnum.PLANPRESUPUESTALPTOS_CONTROLADOR
                                                        .getCodigo()),
                        campos, valores, true);

        // </CODIGO_DESARROLLADO>
    }
}

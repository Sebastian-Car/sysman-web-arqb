
package com.sysman.predial;

import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.predial.ejb.EjbPredialCuatroRemote;
import com.sysman.predial.enums.FrmExoneracionVigenciasControladorEnum;
import com.sysman.predial.enums.FrmExoneracionVigenciasControladorUrlEnum;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

/**
 *
 * @author dmaldonado
 * @version 1, 08/08/2016 11:53:29 -- Modificado por jlozano
 *
 * @author spina
 * @version 2, 04/07/2017 - refactorizacion dss, depuracion sonar y ejbs
 */
@ManagedBean
@ViewScoped
public class FrmExoneracionVigenciasControlador extends BeanBaseDatosAcmeImpl
{
    private final String compania;
    private final String modulo;
    // <DEFINICION_CONSTANTES>
    /**
     * Constante definida por las veces que se llama el campo PAGO_ANO
     */
    private final String strPagoAno;
    // </DEFINICION_CONSTANTES>
    // <DECLARAR_ATRIBUTOS>
    private boolean indAplicar;
    private boolean indQuitar;
    private boolean indRegistro;
    private String codigo;
    private String formatoResolucion;
    private String noResolucion;
    private Date fecResolucion;
    private String elabResolucion;
    private String firmaResolucion;
    private String vigInicial;
    private String vigFinal;
    private String txtObservaciones;
    private String txtCertTalyLib;
    private String txtEscritura;
    private String txtMatInmo;
    private boolean bloqCodigo;
    private boolean bloqVigInicial;
    private boolean bloqVigFinal;
    private boolean bloqNoResolucion;
    private boolean bloqFecResolucion;
    private boolean bloqElabResolucion;
    private boolean bloqFirmaResolucion;
    private boolean bloqtxtCertTalyLib;
    private boolean bloqtxtEscritura;
    private boolean bloqtxtMatInmo;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listacodigo;
    private RegistroDataModelImpl listaFormatoRes;
    private boolean visibleBtnExo;
    private String plantilla;
    private String consultaPlantilla;
    private String nombrePlantilla;
    private Date fechaPlantilla;
    private String tipoPlantilla;
    private String versionPlantilla;
    private String tipoVarPlantilla;

    @EJB
    private EjbPredialCuatroRemote ejbPredialCuatro;

    // </DECLARAR_LISTAS_COMBO_GRANDE>
    // <DECLARAR_LISTAS_SUBFORM>
    // </DECLARAR_LISTAS_SUBFORM>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_ADICIONALES>
    // </DECLARAR_ADICIONALES>
    public FrmExoneracionVigenciasControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        strPagoAno = "PAGO_ANO";

        try
        {
            numFormulario = GeneralCodigoFormaEnum.FRM_EXONERACION_VIGENCIAS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
        }
        catch (Exception ex)
        {
            Logger.getLogger(FrmExoneracionVigenciasControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

        indAplicar = true;
        cambiarIndAplicar();
    }

    @Override
    public void iniciarListas()
    {
        cargarListacodigo();
        cargarListaFormatoRes();
    }

    @Override
    public void iniciarListasSub()
    {
        // <CARGAR_LISTAS_SUBFORM>
        // </CARGAR_LISTAS_SUBFORM>
    }

    @Override
    public void iniciarListasSubNulo()
    {
        // <CARGAR_LISTAS_SUBFORM_NULL>
        // </CARGAR_LISTAS_SUBFORM_NULL>
    }

    @PostConstruct
    public void inicializar()
    {
        registro = new Registro();
        tabla = "";
        asignarOrigenDatos();
        cargarListacodigo();
        cargarListaFormatoRes();
    }

    @Override
    public void asignarOrigenDatos()
    {
        // Metodo heredado no implementado
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListacodigo()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmExoneracionVigenciasControladorUrlEnum.URL3958
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        listacodigo = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    public void cargarListaFormatoRes()
    {

        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmExoneracionVigenciasControladorUrlEnum.URL3959
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(FrmExoneracionVigenciasControladorEnum.TIPO.getValue(), 18);
        listaFormatoRes = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        GeneralParameterEnum.CODIGO.getName());
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_CAMBIAR>
    public void cambiarIndAplicar()
    {
        // <CODIGO_DESARROLLADO>
        if (indAplicar)
        {
            indQuitar = false;
            visibleBtnExo = true;
        }
        else
        {
            indQuitar = true;
            visibleBtnExo = false;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarIndQuitar()
    {
        // <CODIGO_DESARROLLADO>
        if (indQuitar)
        {
            indAplicar = false;
            visibleBtnExo = false;
        }
        else
        {
            indAplicar = true;
            visibleBtnExo = true;
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarVigInicial()
    {
        // <CODIGO_DESARROLLADO>
        int ano = SysmanFunciones.ano(new Date());
        String vig = SysmanFunciones.nvl(registro.getCampos().get(strPagoAno),
                        "").toString();
        if (indAplicar)
        {

            if (Integer.parseInt(vigInicial) < (Integer.parseInt(vig) + 1))
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1091"));
                vigInicial = vig;
            }
            else if (Integer.parseInt(vigInicial) > ano)
            {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1092"));
                vigInicial = vig;
            }
        }
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarVigFinal()
    {
        // <CODIGO_DESARROLLADO>
        int ano = SysmanFunciones.ano(new Date());
        if ((int) SysmanFunciones.nvl(Integer.parseInt(vigFinal.trim()),
                        0) < (int) SysmanFunciones.nvl(
                                        Integer.parseInt(vigInicial.trim()),
                                        0))
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1093"));
            vigFinal = "";
        }
        else if (Integer.parseInt(vigFinal) > ano)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1094"));
            vigFinal = "";
        }
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilacodigo(SelectEvent event)
    {

        Registro registroAux = (Registro) event.getObject();

        codigo = SysmanFunciones.nvl(registroAux.getCampos()
                        .get(GeneralParameterEnum.CODIGO.getName()), "")
                        .toString();
        if ((registroAux.getCampos().get(strPagoAno) == null)
            || "".equals(registroAux.getCampos().get(strPagoAno)))
        {
            vigInicial = "";
        }
        else
        {
            vigInicial = String.valueOf(Integer.parseInt(
                            SysmanFunciones.nvl(registroAux.getCampos()
                                            .get(strPagoAno), "0").toString())
                + 1);
        }
        txtMatInmo = SysmanFunciones.nvl(registroAux.getCampos()
                        .get("MATRICULA_INMOBILIARIA"), "").toString();
        registro = registroAux;
    }

    public void seleccionarFilaFormatoRes(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        formatoResolucion = SysmanFunciones
                        .nvl(registroAux.getCampos().get("CODIGO"), "")
                        .toString();
        setNombrePlantilla(SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString());
        setPlantilla(SysmanFunciones
                        .nvl(registroAux.getCampos().get("PLANTILLA"), "")
                        .toString());
        setConsultaPlantilla(SysmanFunciones
                        .nvl(registroAux.getCampos().get("CONSULTA"), "")
                        .toString());
        fechaPlantilla = (Date) registroAux.getCampos().get("FECHA");
        setTipoPlantilla(Double.toString(SysmanFunciones
                        .nvlDbl(registroAux.getCampos().get("TIPO"), 0)));
        setVersionPlantilla(SysmanFunciones
                        .nvl(registroAux.getCampos().get("VERSION"), "")
                        .toString());
        setTipoVarPlantilla(SysmanFunciones.nvl(registroAux.getCampos()
                        .get("TIPO_VARIABLES_CONSULTA"), "").toString());
    }

    // </METODOS_COMBOS_GRANDES>
    // <METODOS_BOTONES>
    private boolean validarRegistro()
    {
        boolean validar = true;
        if (SysmanFunciones.validarVariableVacio(codigo.trim())
            || SysmanFunciones.validarVariableVacio(vigInicial.trim())
            || SysmanFunciones.validarVariableVacio(vigFinal.trim())
            || SysmanFunciones.validarVariableVacio(noResolucion.trim()))
        {
            validar = false;
        }

        if ((SysmanFunciones.nvl(fecResolucion, null) == null)
            || SysmanFunciones.validarVariableVacio(elabResolucion.trim())
            || SysmanFunciones.validarVariableVacio(firmaResolucion.trim())
            || SysmanFunciones.validarVariableVacio(txtEscritura.trim()))
        {
            validar = false;
        }

        if (SysmanFunciones.validarVariableVacio(txtCertTalyLib.trim()))
        {
            validar = false;
        }
        return validar;

    }

    public void ejecutarFuncion()
    {
        Boolean retorno = false;
        try
        {
            retorno = ejbPredialCuatro
                            .registrarExoneracionVigencias(compania,
                                            codigo, fecResolucion, noResolucion,
                                            txtObservaciones,
                                            Integer.parseInt(vigInicial),
                                            Integer.parseInt(vigFinal),
                                            elabResolucion, firmaResolucion,
                                            SessionUtil.getUser()
                                                            .getCodigo(),
                                            indAplicar);
            if (retorno)
            {
                indRegistro = true;
            }
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB874"));
        }
        catch (NumberFormatException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return;
    }

    public void oprimirCmdRegistrar()
    {

        if (SessionUtil.getNivelUsuario(SessionUtil.getModulo()) > 6)
        {
            if (!validarRegistro())
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB1122"));
                return;
            }
            else
            {
                ejecutarFuncion();
            }
        }
        else
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB1126"));
        }
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirbtnExo()
    {
        // <CODIGO_DESARROLLADO>
        if (!indRegistro)
        {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1128"));
        }
        else if (!"".equals(SysmanFunciones.nvlStr(codigo, "")))
        {
            generaInforme();
        }

        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_SUBFORM>
    // </METODOS_SUBFORM>
    // <METODOS_ADICIONALES>

    public void generaInforme()
    {
        if (formatoResolucion == null)
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB715"));
            return;
        }
        else if ("".equals(formatoResolucion))
        {
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB716"));
            return;
        }

        String strNombreDocumento = idioma.getString("TB_TB3275")
                        .replace("s$codigo$s", codigo);
        String[] campos = new String[3];
        String[] valores = new String[3];
        campos[0] = "codigoPlantilla";
        campos[1] = "fechaPlantilla";
        campos[2] = "nombreDocDescarga";

        valores[0] = formatoResolucion;

        valores[1] = SysmanFunciones.formatearFecha(fechaPlantilla);

        valores[2] = strNombreDocumento;
        try
        {
            Date fechaActual = new Date();
            HashMap<String, String> variablesConsultaW = new HashMap<>();
            variablesConsultaW.put("s$compania$s", "'" + compania + "'");
            variablesConsultaW.put("s$fechaActual$s", "'"
                + SysmanFunciones.convertirAFechaCadena(fechaActual) + "'");
            variablesConsultaW.put("s$diaActual$s", Integer.toString(
                            SysmanFunciones.dia(fechaActual)));
            variablesConsultaW.put("s$mesActual$s", Integer.toString(
                            SysmanFunciones.mes(fechaActual)));
            variablesConsultaW.put("s$anoActual$s", Integer.toString(
                            SysmanFunciones.ano(fechaActual)));
            variablesConsultaW.put("s$resolucion$s", "'" + noResolucion + "'");
            variablesConsultaW.put("s$fecResolucion$s", "'"
                + SysmanFunciones.convertirAFechaCadena(fecResolucion) + "'");
            variablesConsultaW.put("s$elabResolucion$s",
                            "'" + elabResolucion + "'");
            variablesConsultaW.put("s$firmaResolucion$s",
                            "'" + firmaResolucion + "'");
            variablesConsultaW.put("s$txtEscritura$s",
                            "'" + txtEscritura + "'");
            variablesConsultaW.put("s$txtCertTalyLib$s",
                            "'" + txtCertTalyLib + "'");
            variablesConsultaW.put("s$txtMatInmo$s", "'" + txtMatInmo + "'");
            variablesConsultaW.put("s$codigo$s", "'" + codigo + "'");

            // variables por parametro para documento word
            SessionUtil.setSessionVar("variablesConsultaWord",
                            variablesConsultaW);
            SessionUtil.cargarModalDatosFlash(
                            String.valueOf(GeneralCodigoFormaEnum.IMPRIMIRWORDS_CONTROLADOR
                                            .getCodigo()),
                            SessionUtil.getModulo(),
                            campos,
                            valores);
        }
        catch (ParseException e)
        {
            Logger.getLogger(FrmExoneracionVigenciasControlador.class.getName())
                            .log(Level.SEVERE, null, e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // </METODOS_ADICIONALES>
    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        if (SysmanFunciones.nvlDbl(SessionUtil.getNivelUsuario(modulo),
                        0) > 6)
        {
            codigo = "";
            bloqCodigo = false;
            vigInicial = "";
            bloqVigInicial = false;
            vigFinal = "";
            bloqVigFinal = false;
            noResolucion = "";
            bloqNoResolucion = false;
            fecResolucion = new Date();
            bloqFecResolucion = false;
            elabResolucion = "";
            bloqElabResolucion = false;
            firmaResolucion = "";
            bloqFirmaResolucion = false;
            txtCertTalyLib = "";
            bloqtxtCertTalyLib = false;
            txtEscritura = "";
            bloqtxtEscritura = false;
            txtMatInmo = "";
            bloqtxtMatInmo = false;

        }
        else
        {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB717"));

            codigo = "";
            bloqCodigo = true;
            vigInicial = "";
            bloqVigInicial = true;
            vigFinal = "";
            bloqVigFinal = true;
            noResolucion = "";
            bloqNoResolucion = true;
            fecResolucion = new Date();
            bloqFecResolucion = true;
            elabResolucion = "";
            bloqElabResolucion = true;
            firmaResolucion = "";
            bloqFirmaResolucion = true;
            txtCertTalyLib = "";
            bloqtxtCertTalyLib = true;
            txtEscritura = "";
            bloqtxtEscritura = true;
            txtMatInmo = "";
            bloqtxtMatInmo = true;
        }
        indRegistro = false;

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro()
    {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes()
    {
        // <CODIGO_DESARROLLADO>

        registro.getCampos().put(GeneralParameterEnum.COMPANIA.getName(),
                        compania);
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean insertarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean actualizarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarAntes()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    @Override
    public boolean eliminarDespues()
    {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
        return true;
    }

    // <SET_GET_ATRIBUTOS>
    public boolean getIndAplicar()
    {
        return indAplicar;
    }

    public void setIndAplicar(boolean indAplicar)
    {
        this.indAplicar = indAplicar;
    }

    public boolean getIndQuitar()
    {
        return indQuitar;
    }

    public void setIndQuitar(boolean indQuitar)
    {
        this.indQuitar = indQuitar;
    }

    public String getCodigo()
    {
        return codigo;
    }

    public void setCodigo(String codigo)
    {
        this.codigo = codigo;
    }

    public String getFormatoResolucion()
    {
        return formatoResolucion;
    }

    public void setFormatoResolucion(String formatoResolucion)
    {
        this.formatoResolucion = formatoResolucion;
    }

    public String getNoResolucion()
    {
        return noResolucion;
    }

    public void setNoResolucion(String noResolucion)
    {
        this.noResolucion = noResolucion;
    }

    public Date getFecResolucion()
    {
        return fecResolucion;
    }

    public void setFecResolucion(Date fecResolucion)
    {
        this.fecResolucion = fecResolucion;
    }

    public String getElabResolucion()
    {
        return elabResolucion;
    }

    public void setElabResolucion(String elabResolucion)
    {
        this.elabResolucion = elabResolucion;
    }

    public String getFirmaResolucion()
    {
        return firmaResolucion;
    }

    public void setFirmaResolucion(String firmaResolucion)
    {
        this.firmaResolucion = firmaResolucion;
    }

    public String getVigInicial()
    {
        return vigInicial;
    }

    public void setVigInicial(String vigInicial)
    {
        this.vigInicial = vigInicial;
    }

    public String getVigFinal()
    {
        return vigFinal;
    }

    public void setVigFinal(String vigFinal)
    {
        this.vigFinal = vigFinal;
    }

    public boolean isBloqCodigo()
    {
        return bloqCodigo;
    }

    public void setBloqCodigo(boolean bloqCodigo)
    {
        this.bloqCodigo = bloqCodigo;
    }

    public boolean isBloqVigInicial()
    {
        return bloqVigInicial;
    }

    public void setBloqVigInicial(boolean bloqVigInicial)
    {
        this.bloqVigInicial = bloqVigInicial;
    }

    public boolean isBloqVigFinal()
    {
        return bloqVigFinal;
    }

    public void setBloqVigFinal(boolean bloqVigFinal)
    {
        this.bloqVigFinal = bloqVigFinal;
    }

    public boolean isBloqNoResolucion()
    {
        return bloqNoResolucion;
    }

    public void setBloqNoResolucion(boolean bloqNoResolucion)
    {
        this.bloqNoResolucion = bloqNoResolucion;
    }

    public boolean isBloqFecResolucion()
    {
        return bloqFecResolucion;
    }

    public void setBloqFecResolucion(boolean bloqFecResolucion)
    {
        this.bloqFecResolucion = bloqFecResolucion;
    }

    public boolean isBloqElabResolucion()
    {
        return bloqElabResolucion;
    }

    public void setBloqElabResolucion(boolean bloqElabResolucion)
    {
        this.bloqElabResolucion = bloqElabResolucion;
    }

    public boolean isBloqFirmaResolucion()
    {
        return bloqFirmaResolucion;
    }

    public void setBloqFirmaResolucion(boolean bloqFirmaResolucion)
    {
        this.bloqFirmaResolucion = bloqFirmaResolucion;
    }

    public boolean isBloqtxtCertTalyLib()
    {
        return bloqtxtCertTalyLib;
    }

    public void setBloqtxtCertTalyLib(boolean bloqtxtCertTalyLib)
    {
        this.bloqtxtCertTalyLib = bloqtxtCertTalyLib;
    }

    public boolean isBloqtxtEscritura()
    {
        return bloqtxtEscritura;
    }

    public void setBloqtxtEscritura(boolean bloqtxtEscritura)
    {
        this.bloqtxtEscritura = bloqtxtEscritura;
    }

    public boolean isBloqtxtMatInmo()
    {
        return bloqtxtMatInmo;
    }

    public void setBloqtxtMatInmo(boolean bloqtxtMatInmo)
    {
        this.bloqtxtMatInmo = bloqtxtMatInmo;
    }

    public String getTxtObservaciones()
    {
        return txtObservaciones;
    }

    public void setTxtObservaciones(String txtObservaciones)
    {
        this.txtObservaciones = txtObservaciones;
    }

    public String getTxtCertTalyLib()
    {
        return txtCertTalyLib;
    }

    public void setTxtCertTalyLib(String txtCertTalyLib)
    {
        this.txtCertTalyLib = txtCertTalyLib;
    }

    public String getTxtEscritura()
    {
        return txtEscritura;
    }

    public void setTxtEscritura(String txtEscritura)
    {
        this.txtEscritura = txtEscritura;
    }

    public String getTxtMatInmo()
    {
        return txtMatInmo;
    }

    public void setTxtMatInmo(String txtMatInmo)
    {
        this.txtMatInmo = txtMatInmo;
    }

    public boolean isVisibleBtnExo()
    {
        return visibleBtnExo;
    }

    public void setVisibleBtnExo(boolean visibleBtnExo)
    {
        this.visibleBtnExo = visibleBtnExo;
    }

    public RegistroDataModelImpl getListacodigo()
    {
        return listacodigo;
    }

    public void setListacodigo(RegistroDataModelImpl listacodigo)
    {
        this.listacodigo = listacodigo;
    }

    public RegistroDataModelImpl getListaFormatoRes()
    {
        return listaFormatoRes;
    }

    public void setListaFormatoRes(RegistroDataModelImpl listaFormatoRes)
    {
        this.listaFormatoRes = listaFormatoRes;
    }

    public String getPlantilla()
    {
        return plantilla;
    }

    public void setPlantilla(String plantilla)
    {
        this.plantilla = plantilla;
    }

    public String getConsultaPlantilla()
    {
        return consultaPlantilla;
    }

    public void setConsultaPlantilla(String consultaPlantilla)
    {
        this.consultaPlantilla = consultaPlantilla;
    }

    public String getNombrePlantilla()
    {
        return nombrePlantilla;
    }

    public void setNombrePlantilla(String nombrePlantilla)
    {
        this.nombrePlantilla = nombrePlantilla;
    }

    public String getTipoPlantilla()
    {
        return tipoPlantilla;
    }

    public void setTipoPlantilla(String tipoPlantilla)
    {
        this.tipoPlantilla = tipoPlantilla;
    }

    public String getVersionPlantilla()
    {
        return versionPlantilla;
    }

    public void setVersionPlantilla(String versionPlantilla)
    {
        this.versionPlantilla = versionPlantilla;
    }

    public String getTipoVarPlantilla()
    {
        return tipoVarPlantilla;
    }

    public void setTipoVarPlantilla(String tipoVarPlantilla)
    {
        this.tipoVarPlantilla = tipoVarPlantilla;
    }

}

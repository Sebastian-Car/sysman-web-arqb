package com.sysman.predial;

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
import com.sysman.persistencia.Acciones;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.enums.PredialultimospagosControladorEnum;
import com.sysman.predial.enums.PredialultimospagosControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;
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
import javax.naming.NamingException;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;

import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ybecerra
 * @version 1, 09/06/2016
 *
 * @author asana
 * @version 2, 13/06/2017 Se implementa enum en formulario, y se ajusta conexión.
 *
 * @author spina
 * @version 3, 13/07/2017 - se refactoriza para dss, depuracion sonar y ejbs
 */
@ManagedBean
@ViewScoped
public class PredialultimospagosControlador extends BeanBaseModal
{
    private final String compania;
    private final String modulo;
    private final String strCodigo;
    private final String strNombre;
    private final String strTb218;
    // <DECLARAR_ATRIBUTOS>
    private boolean resumido;
    private String generar;
    private String ordenar;
    private String generarPred;
    private String codigoInicial;
    private String codigoFinal;
    private String nombreInicial;
    private String nombreFinal;
    private String nombreCodigoInicial;
    private String nombreCodigoFinal;
    private Date fechaInicial;
    private Date fechaFinal;
    private String deudas;
    private StreamedContent archivoDescarga;
    private boolean visibleCodigo;
    private boolean visibleNombre;
    private boolean visibleFecha;
    private boolean visibleDeuda;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listacodigoInicial;
    private RegistroDataModelImpl listacodigoFinal;
    private RegistroDataModelImpl listanombreInicial;
    private RegistroDataModelImpl listanombreFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>
    private String condicion;
    private List<Registro> nombresConcepto;
    private String union;
    private String titulo;
    private boolean existe;
    private int aux;
    private String conceptosAdicionales;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of PredialultimospagosControlador
     */
    public PredialultimospagosControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        strCodigo = "CODIGO";
        strNombre = "NOMBRE";
        strTb218 = "TB_TB218";
        conceptosAdicionales = "";
        try
        {
            numFormulario = GeneralCodigoFormaEnum.PREDIALULTIMOSPAGOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(PredialultimospagosControlador.class.getName())
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
        cargarListacodigoInicial();
        cargarListanombreInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario()
    {
        generar = "1";
        visibleCodigo = true;
        ordenar = "1";
        generarPred = "3";
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>

    public void cargarListacodigoInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PredialultimospagosControladorUrlEnum.URL4841
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        listacodigoInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        strCodigo);
    }

    public void cargarListacodigoFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PredialultimospagosControladorUrlEnum.URL4842
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        param.put(PredialultimospagosControladorEnum.CODIGOINICIAL.getValue(),
                        codigoInicial);

        listacodigoFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        strCodigo);
    }

    public void cargarListanombreInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PredialultimospagosControladorUrlEnum.URL4843
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);

        listanombreInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        strNombre);
    }

    public void cargarListanombreFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PredialultimospagosControladorUrlEnum.URL4844
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        param.put(PredialultimospagosControladorEnum.NOMBREINICIAL.getValue(),
                        nombreInicial);

        listanombreFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param, true,
                        strNombre);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPresentar()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarExcel();
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    private void cambiargenerarPorCaso2()
    {
        fechaInicial = new Date();
        fechaFinal = new Date();
        visibleFecha = true;
        visibleCodigo = false;
        visibleNombre = false;
        visibleDeuda = false;
    }

    public void cambiargenerarPor()
    {

        switch (generar)
        {
        case "1":
            visibleCodigo = true;
            visibleNombre = false;
            visibleFecha = false;
            visibleDeuda = false;
            break;
        case "2":
            cambiargenerarPorCaso2();
            break;
        case "3":
            visibleNombre = true;
            visibleCodigo = false;
            visibleFecha = false;
            visibleDeuda = false;
            break;
        case "4":
            visibleDeuda = true;
            visibleCodigo = false;
            visibleFecha = false;
            visibleNombre = false;
            break;
        default:
            break;
        }
    }

    public boolean condicionCaso1()

    {
        return SysmanFunciones.validarVariableVacio(codigoInicial)
            || SysmanFunciones.validarVariableVacio(codigoFinal);
    }

    public boolean condicionCaso2()
    {
        return SysmanFunciones.validarVariableVacio(fechaInicial.toString())
            || SysmanFunciones.validarVariableVacio(fechaFinal.toString());
    }

    public boolean condicionCaso3()
    {
        return SysmanFunciones.validarVariableVacio(nombreInicial)
            || SysmanFunciones.validarVariableVacio(nombreFinal);
    }

    private void generarExcelResum(HashMap<String, Object> reemplazar, int i,
        List<Registro> nombresConcepto, int aux)
    {
        if (resumido)
        {
            reemplazar.put("totalC" + (i), nombresConcepto.get(aux)
                            .getCampos().get(strNombre));
        }
        else
        {
            reemplazar.put("nombreConcepto" + (i), nombresConcepto
                            .get(aux).getCampos().get(strNombre));
        }
    }

    private void generarExcelIncremental(HashMap<String, Object> reemplazar,
        int i)
    {
        if (i < 9)
        {
            if (resumido)
            {
                reemplazar.put("totalC" + (i), "CONCEPTO " + (i));
            }
            else
            {
                reemplazar.put("nombreConcepto" + (i),
                                "CONCEPTO " + (i));

            }
        }
    }

    public void generarExcel()
    {
        try
        {

            String parametro = ejbSysmanUtil.consultarParametro(compania,
                            "CODIGOS PREDIOS RURALES", modulo, new Date(),
                            true);
            if (parametro == null)
            {

                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB216"));
                return;
            }
            if (!validarCondicionesFiltro())
            {
                return;
            }
            String condTipoPred = (String) Acciones.ejecutarFuncion(
                            ConectorPool.ESQUEMA_SYSMAN,
                            "PCK_SYSMAN_UTL.FC_COLOCARCOMILLAS",
                            "'" + SysmanFunciones.nvlStr(parametro, " ") + "'",
                            Types.VARCHAR);

            // Condicion del filtro del Excel por predio
            if ("1".equals(generarPred))
            {
                condicion = condicion
                    + "   AND SUBSTR(IP_USUARIOS_PREDIAL.CODIGO, 1, 2) IN ( "
                    + condTipoPred + ")";
                titulo = "Rurales";

            }
            else if ("2".equals(generarPred))
            {
                condicion = condicion
                    + "   AND SUBSTR(IP_USUARIOS_PREDIAL.CODIGO, 1, 2) NOT IN("
                    + condTipoPred + ")";
                titulo = "Urbanos";

            }

            ponerNombreConceptoYGenerar(condTipoPred);
        }
        catch (SQLException | NamingException
                        | IllegalAccessException | InstantiationException
                        | ClassNotFoundException | SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    public void ponerNombreConceptoYGenerar(String condTipoPred)

    {
        try
        {
            StringBuilder conceptosAdicionalesp = new StringBuilder();
            conceptosAdicionales = "";
            HashMap<String, Object> reemplazar = new HashMap<>();

            int ano = SysmanFunciones.ano(new Date());
            String ordenamiento;
            union = "";
            String sql;

            // Procedimiento para poner Nombre de Concepto y Poner
            // Visibles algunos campos del Excel
            Map<String, Object> param = new TreeMap<>();
            param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
            param.put(GeneralParameterEnum.ANO.getName(), ano);
            nombresConcepto = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            PredialultimospagosControladorUrlEnum.URL4845
                                                                            .getValue())
                                            .getUrl(),
                                            param));

            existe = false;
            aux = 0;
            for (int i = 1; i <= 20; i++)
            {
                recorrerNombreConcepto(i, reemplazar, conceptosAdicionalesp,
                                condTipoPred);
            }

            ordenamiento = crearCondicionOrdenamiento(ordenar);

            reemplazar.put("camposAdicionales", conceptosAdicionales);
            reemplazar.put("condicion", condicion);
            reemplazar.put("ordenamiento", ordenamiento);
            reemplazar.put("titulo", "'" + titulo + "'");
            reemplazar.put("union", union);

            String nombreArchivo = "800053ResUltimosPagos";
            if (resumido)
            {
                reemplazar.put("condTipoPred", condTipoPred);
                sql = Reporteador.resuelveConsulta(nombreArchivo,
                                Integer.parseInt(modulo), reemplazar);
            }
            else
            {
                nombreArchivo = "800052UltimosPagos";
                sql = Reporteador.resuelveConsulta(nombreArchivo,
                                Integer.parseInt(modulo), reemplazar);
            }

            long total = service.getConteoConsulta(sql);

            if (total > 0)
            {
                archivoDescarga = JsfUtil.exportarHojaDatosStreamed(sql,
                                ConectorPool.ESQUEMA_SYSMAN, FORMATOS.EXCEL97,
                                nombreArchivo);
            }
            else
            {
                JsfUtil.agregarMensajeError(idioma.getString("TB_TB3355"));
                return;
            }

        }
        catch (SystemException | JRException | SQLException
                        | DRException | SysmanException | IOException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private void recorrerNombreConcepto(int i,
        HashMap<String, Object> reemplazar,
        StringBuilder conceptosAdicionalesp, String condTipoPred)
    {

        existe = false;
        for (int j = 0; j < nombresConcepto.size(); j++)
        {
            if (nombresConcepto.get(j).getCampos().get(strCodigo)
                            .toString().equals(Integer.toString(i)))
            {
                existe = true;
                aux = j;
                break;
            }
        }

        if (existe)
        {

            generarExcelResum(reemplazar, i, nombresConcepto, aux);

            if (i > 12 && ((boolean) nombresConcepto.get(aux)
                            .getCampos().get("VER_RESOL")))
            {
                if (resumido)
                {
                    titulo = "Rurales";
                    conceptosAdicionalesp.append(
                                    "        SUM(IP_RECIBOS_DE_PAGO.C"
                                        + i + ") " + " \""
                                        + nombresConcepto.get(aux)
                                                        .getCampos()
                                                        .get(strNombre)
                                        + "\" ,\n");
                    conceptosAdicionales = conceptosAdicionalesp
                                    .toString();

                    setUnion("3".equals(generarPred)
                        ? "UNION SELECT    'Urbanos' AS TIPO_PREDIO, \n"
                            +
                            "        IP_RECIBOS_DE_PAGO.PREANOI || '-' || IP_RECIBOS_DE_PAGO.PREANOF AS ANO_PAGO, \n"
                            +
                            "        SUM(IP_RECIBOS_DE_PAGO.C1) AS TOTALC1, \n"
                            +
                            "        SUM(IP_RECIBOS_DE_PAGO.C2) AS TOTALC2,\n"
                            +
                            "        SUM(IP_RECIBOS_DE_PAGO.C3) AS TOTALC3, \n"
                            +
                            "        SUM(IP_RECIBOS_DE_PAGO.C4) AS TOTALC4, \n"
                            +
                            "        SUM(IP_RECIBOS_DE_PAGO.C5+IP_RECIBOS_DE_PAGO.C9) AS TOTALC5, \n"
                            +
                            "        SUM(IP_RECIBOS_DE_PAGO.C6+IP_RECIBOS_DE_PAGO.C10) AS TOTALC6, \n"
                            +
                            "        SUM(IP_RECIBOS_DE_PAGO.C7+IP_RECIBOS_DE_PAGO.C11) AS TOTALC7, \n"
                            +
                            "        SUM(IP_RECIBOS_DE_PAGO.C8+IP_RECIBOS_DE_PAGO.C12) AS TOTALC8, \n"
                            +
                            "        " + conceptosAdicionales
                            + "  \n" +
                            "        SUM(IP_RECIBOS_DE_PAGO.PREVAL) AS ULTIMO_PAGO\n"
                            +
                            "FROM IP_USUARIOS_PREDIAL \n" +
                            "  INNER JOIN IP_RECIBOS_DE_PAGO \n" +
                            "    ON IP_USUARIOS_PREDIAL.COMPANIA    = IP_RECIBOS_DE_PAGO.COMPANIA \n"
                            +
                            "   AND IP_USUARIOS_PREDIAL.CODIGO        = IP_RECIBOS_DE_PAGO.PRECOD \n"
                            +
                            "   AND IP_USUARIOS_PREDIAL.NUMERO_ORDEN  = IP_RECIBOS_DE_PAGO.NUMERO_ORDEN \n"
                            +
                            "   AND IP_USUARIOS_PREDIAL.NUM_COM       = IP_RECIBOS_DE_PAGO.DOCNUM \n"
                            +
                            " WHERE IP_USUARIOS_PREDIAL.COMPANIA = '"
                            + compania + "'\n" +
                            "  " + condicion + "" +
                            " AND IP_RECIBOS_DE_PAGO.PAGO NOT IN(0)"
                            +
                            "  AND SUBSTR(IP_USUARIOS_PREDIAL.CODIGO, 1 , 2) NOT IN ("
                            + condTipoPred + ") \n" +
                            " GROUP BY IP_RECIBOS_DE_PAGO.PREANOI || '-' || PREANOF "
                        : " ");

                }
                else
                {

                    conceptosAdicionalesp
                                    .append(", IP_RECIBOS_DE_PAGO.C"
                                        + i + " \""
                                        + nombresConcepto.get(aux)
                                                        .getCampos()
                                                        .get(strNombre)
                                        + "\"");

                    conceptosAdicionales = conceptosAdicionalesp
                                    .toString();
                }

            }

        }
        else
        {
            generarExcelIncremental(reemplazar, i);
        }

    }

    private String crearCondicionOrdenamiento(String ordenar)
    {
        // Condicion de Ordenamiento del Excel
        String ordenamiento = "";

        switch (ordenar)
        {
        case "1":
            ordenamiento = "    IP_USUARIOS_PREDIAL.CODIGO";
            break;
        case "2":
            ordenamiento = "    IP_USUARIOS_PREDIAL.PAG_FEC";
            break;
        case "3":
            ordenamiento = "    IP_USUARIOS_PREDIAL.NOMBRE";
            break;
        case "4":
            ordenamiento = "    IP_RECIBOS_DE_PAGO.PREVAL";
            break;
        case "5":
            ordenamiento = "    IP_USUARIOS_PREDIAL.PAGO_ANO";
            break;
        default:
            break;
        }
        return ordenamiento;
    }

    private boolean validarCondicionesFiltro()
    {
        boolean retorno = true;
        // Condicion de filtro del Excel
        if ("1".equals(generar))
        {
            if (condicionCaso1())
            {
                JsfUtil.agregarMensajeError(idioma.getString(strTb218));
                retorno = false;
            }
            else
            {
                condicion = "   AND IP_USUARIOS_PREDIAL.CODIGO BETWEEN '"
                    + codigoInicial + "' AND '" + codigoFinal + "'  ";
            }
        }
        else if ("2".equals(generar))
        {
            if (condicionCaso2())
            {
                JsfUtil.agregarMensajeError(idioma.getString(strTb218));
                retorno = false;
            }
            else
            {
                condicion = "   AND IP_USUARIOS_PREDIAL.PAG_FEC BETWEEN "
                    + SysmanFunciones.formatearFecha(fechaInicial) + " AND "
                    + SysmanFunciones.formatearFecha(fechaFinal) + "";
            }
        }
        else if ("3".equals(generar))
        {
            if (condicionCaso3())
            {
                JsfUtil.agregarMensajeError(idioma.getString(strTb218));
                retorno = false;
            }
            else
            {
                condicion = "   AND IP_USUARIOS_PREDIAL.NOMBRE BETWEEN '"
                    + nombreInicial + "' AND '" + nombreFinal + "'  ";
            }
        }
        else if ("4".equals(generar))
        {
            if (SysmanFunciones.validarVariableVacio(deudas))
            {
                JsfUtil.agregarMensajeError(idioma.getString(strTb218));
                retorno = false;
            }
            else
            {
                condicion = "   AND IP_RECIBOS_DE_PAGO.PREVAL >= " + deudas
                    + " ";
            }
        }
        return retorno;
    }

    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilacodigoInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos().get(strCodigo).toString();
        nombreCodigoInicial = registroAux.getCampos().get(strNombre).toString();
        codigoFinal = null;
        nombreCodigoFinal = null;
        cargarListacodigoFinal();
    }

    public void seleccionarFilacodigoFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = String.valueOf(registroAux.getCampos().get(strCodigo));
        nombreCodigoFinal = String
                        .valueOf(registroAux.getCampos().get(strNombre));
    }

    public void seleccionarFilanombreInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        nombreInicial = String.valueOf(registroAux.getCampos().get(strNombre));
        nombreFinal = null;
        cargarListanombreFinal();
    }

    public void seleccionarFilanombreFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        nombreFinal = registroAux.getCampos().get(strNombre).toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public boolean getResumido()
    {
        return resumido;
    }

    public void setResumido(boolean resumido)
    {
        this.resumido = resumido;
    }

    public String getGenerar()
    {
        return generar;
    }

    public void setGenerar(String generar)
    {
        this.generar = generar;
    }

    public String getOrdenar()
    {
        return ordenar;
    }

    public void setOrdenar(String ordenar)
    {
        this.ordenar = ordenar;
    }

    public String getGenerarPred()
    {
        return generarPred;
    }

    public void setGenerarPred(String generarPred)
    {
        this.generarPred = generarPred;
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

    public String getNombreInicial()
    {
        return nombreInicial;
    }

    public void setNombreInicial(String nombreInicial)
    {
        this.nombreInicial = nombreInicial;
    }

    public String getNombreFinal()
    {
        return nombreFinal;
    }

    public void setNombreFinal(String nombreFinal)
    {
        this.nombreFinal = nombreFinal;
    }

    public String getNombreCodigoInicial()
    {
        return nombreCodigoInicial;
    }

    public void setNombreCodigoInicial(String nombreCodigoInicial)
    {
        this.nombreCodigoInicial = nombreCodigoInicial;
    }

    public String getNombreCodigoFinal()
    {
        return nombreCodigoFinal;
    }

    public void setNombreCodigoFinal(String nombreCodigoFinal)
    {
        this.nombreCodigoFinal = nombreCodigoFinal;
    }

    public Date getFechaInicial()
    {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial)
    {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal()
    {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal)
    {
        this.fechaFinal = fechaFinal;
    }

    public String getDeudas()
    {
        return deudas;
    }

    public void setDeudas(String deudas)
    {
        this.deudas = deudas;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    public boolean isVisibleCodigo()
    {
        return visibleCodigo;
    }

    public void setVisibleCodigo(boolean visibleCodigo)
    {
        this.visibleCodigo = visibleCodigo;
    }

    public void setArchivoDescarga(StreamedContent archivoDescarga)
    {
        this.archivoDescarga = archivoDescarga;
    }

    public boolean isVisibleNombre()
    {
        return visibleNombre;
    }

    public void setVisibleNombre(boolean visibleNombre)
    {
        this.visibleNombre = visibleNombre;
    }

    public boolean isVisibleFecha()
    {
        return visibleFecha;
    }

    public void setVisibleFecha(boolean visibleFecha)
    {
        this.visibleFecha = visibleFecha;
    }

    public boolean isVisibleDeuda()
    {
        return visibleDeuda;
    }

    public void setVisibleDeuda(boolean visibleDeuda)
    {
        this.visibleDeuda = visibleDeuda;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListacodigoInicial()
    {
        return listacodigoInicial;
    }

    public void setListacodigoInicial(RegistroDataModelImpl listacodigoInicial)
    {
        this.listacodigoInicial = listacodigoInicial;
    }

    public RegistroDataModelImpl getListacodigoFinal()
    {
        return listacodigoFinal;
    }

    public void setListacodigoFinal(RegistroDataModelImpl listacodigoFinal)
    {
        this.listacodigoFinal = listacodigoFinal;
    }

    public RegistroDataModelImpl getListanombreInicial()
    {
        return listanombreInicial;
    }

    public void setListanombreInicial(RegistroDataModelImpl listanombreInicial)
    {
        this.listanombreInicial = listanombreInicial;
    }

    public RegistroDataModelImpl getListanombreFinal()
    {
        return listanombreFinal;
    }

    public void setListanombreFinal(RegistroDataModelImpl listanombreFinal)
    {
        this.listanombreFinal = listanombreFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>

    public String getUnion()
    {
        return union;
    }

    public void setUnion(String union)
    {
        this.union = union;
    }

}

package com.sysman.presupuesto;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.presupuesto.enums.RegistroejecucgastosrpsControladorEnum;
import com.sysman.presupuesto.enums.RegistroejecucgastosrpsControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
import java.sql.SQLException;
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

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ybecerra
 * @version 1, 19/07/2016
 * 
 * @author jrodrigueza
 * @version 2, 20/04/2017 Proceso de refactoring.
 * 
 * @author spina - refactorizo conexiones
 * @version 3, 12/06/2017
 */
@ManagedBean
@ViewScoped
public class RegistroejecucgastosrpsControlador extends BeanBaseModal
{
    /**
     * Constante a nivel de clase que almacena el codigo de la compania en la cual inicio sesion el usuario, el valor de esta constante es asignado en el constructor a la variable de sesion
     * correspondiente
     */
    private final String compania;
    /**
     * Constante a nivel de clase que almacena el codigo del modulo actual, el valor de esta constante es asignado en el constructor a la variable de sesion correspondiente
     */
    private final String modulo;
    /**
     * Constante definida para almacenar la cadena "PR_TITULOUNIDAD"
     */
    private final String tituloUni;
    /**
     * Constante definida para almacenar la cadena "PR_TITULOSECCION"
     */
    private final String tituloSec;

    /**
     * Constante definida para almacenar la cadena "PR_SECCION"
     */
    private final String sec;

    /**
     * Constante definida para almacenar la cadena "PR_UNIDAD"
     */
    private final String uni;
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    // <DECLARAR_ATRIBUTOS>
    private boolean indicador;
    private boolean miles;
    private String cuentaInicial;
    private String cuentaFinal;
    private int ano;
    private int mes;
    private int nivel;
    private StreamedContent archivoDescarga;
    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    private List<Registro> listaAno;
    private List<Registro> listaMes;
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCuentaInicial;
    private RegistroDataModelImpl listaCuentaFinal;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    /**
     * Creates a new instance of RegistroejecucgastosrpsControlador
     */
    public RegistroejecucgastosrpsControlador()
    {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        tituloUni = "PR_TITULOUNIDAD";
        tituloSec = "PR_TITULOSECCION";
        sec = "PR_SECCION";
        uni = "PR_UNIDAD";
        try
        {
            numFormulario = GeneralCodigoFormaEnum.REGISTROEJECUCGASTOSRPS_CONTROLADOR.getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex)
        {
            Logger.getLogger(RegistroejecucgastosrpsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }

    }

    @PostConstruct
    public void inicializar()
    {
        abrirFormulario();
        // <CARGAR_LISTA>
        cargarListaAno();
        cargarListaMes();
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCuentaInicial();
        // </CARGAR_LISTA_COMBO_GRANDE>
    }

    @Override
    public void abrirFormulario()
    {
        // <CODIGO_DESARROLLADO>
        ano = SysmanFunciones.ano(new Date());
        mes = SysmanFunciones.mes(new Date());
        nivel = 90;
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaAno()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(RegistroejecucgastosrpsControladorEnum.PARAM0.getValue(),
                        compania);
        try
        {
            listaAno = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RegistroejecucgastosrpsControladorUrlEnum.URL4977
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaMes()
    {
        Map<String, Object> param = new TreeMap<>();
        param.put(RegistroejecucgastosrpsControladorEnum.PARAM0.getValue(),
                        compania);
        try
        {
            listaMes = RegistroConverter.toListRegistro(requestManager.getList(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            RegistroejecucgastosrpsControladorUrlEnum.URL5397
                                                                            .getValue())
                                            .getUrl(),
                            param));
        }
        catch (SystemException e)
        {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaCuentaInicial()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RegistroejecucgastosrpsControladorUrlEnum.URL5948
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(RegistroejecucgastosrpsControladorEnum.PARAM0.getValue(),
                        compania);
        param.put(RegistroejecucgastosrpsControladorEnum.PARAM1.getValue(),
                        ano);

        listaCuentaInicial = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    public void cargarListaCuentaFinal()
    {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        RegistroejecucgastosrpsControladorUrlEnum.URL7089
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(RegistroejecucgastosrpsControladorEnum.PARAM0.getValue(),
                        compania);
        param.put(RegistroejecucgastosrpsControladorEnum.PARAM1.getValue(),
                        ano);
        param.put(RegistroejecucgastosrpsControladorEnum.PARAM2.getValue(),
                        cuentaInicial);

        listaCuentaFinal = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, "ID");
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPresentar()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel()
    {
        // <CODIGO_DESARROLLADO>
        archivoDescarga = null;
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    // </METODOS_BOTONES>

    /**
     * Retorna el nombre del reporte a generar
     *
     * @return parReporte
     */
    public String parReporte()
    {
        String parReporte;
        if (indicador)
        {
            parReporte = "001031REGISTROEJECUCGASTOSRP036";
        }
        else
        {
            parReporte = "001030REGISTROEJECUCGASTOSRP036SO";
        }
        return parReporte;
    }

    /**
     * Es llamado en el metodo generarInforme
     *
     * @param seccionInforme
     * @param seccion
     * @param unidad
     * @return parametros
     */
    public String seccionIn(String seccionInforme, String seccion,
        String unidad)
    {
        String parametros;
        if ("SI".equals(seccionInforme))
        {
            if (!seccion.isEmpty())
            {
                parametros = "SECCION: #" + seccion + "";
            }
            else
            {
                parametros = " # ";
            }
            if (!unidad.isEmpty())
            {
                parametros = parametros + "#" + "UNIDAD EJECUTORA: #" + unidad
                    + "";
            }
            else
            {
                parametros = parametros + "#" + " # ";
            }
        }
        else
        {
            parametros = " # # # ";
        }
        return parametros;
    }

    /**
     * Se llama en el metodo generarInforme
     *
     * @param parametro
     * @param mensaje
     * @return
     * @throws NamingException
     * @throws SQLException
     * @throws SystemException
     */
    private String validacionParametros(String parametro, String mensaje)
                    throws SystemException
    {
        String cargoResolucionUno = ejbSysmanUtil.consultarParametro(compania,
                        parametro, modulo, new Date(), true);
        boolean para = cargoResolucionUno == null ? true : false;

        if (para)
        {
            JsfUtil.agregarMensajeError(idioma.getString(mensaje));
        }

        return !para ? cargoResolucionUno : null;
    }

    /**
     * Se llama en el metodo generarInforme
     * 
     * @param cargoResolucionUno
     * @param cargoResolucionDos
     * @param firmaResolucionUno
     * @param firmaResolucionDos
     * @param cedulaResolucionUno
     * @param cedulaResolucionDos
     * @return
     */
    public boolean validacionParametrosUno(String cargoResolucionUno,
        String cargoResolucionDos, String firmaResolucionUno,
        String firmaResolucionDos, String cedulaResolucionUno,
        String cedulaResolucionDos)
    {
        if ((cargoResolucionUno == null) || (cargoResolucionDos == null)
            || (firmaResolucionUno == null))
        {
            return true;
        }
        if ((firmaResolucionDos == null) || (cedulaResolucionUno == null)
            || (cedulaResolucionDos == null))
        {
            return true;
        }
        return false;
    }

    /**
     * Se llama en el metodo generarInforme
     * 
     * @param seccionInformes
     * @param seccion
     * @param unidad
     * @return
     */
    public boolean validacionParametrosDos(String seccionInformes,
        String seccion,
        String unidad)
    {
        if ((seccionInformes == null) || (seccion == null)
            || (unidad == null))
        {
            return true;
        }
        return false;
    }

    public void generarInforme(ReportesBean.FORMATOS formato)
    {
        archivoDescarga = null;
        try
        {

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("miles", miles ? '1' : '0');
            reemplazar.put("ano", ano);
            reemplazar.put("cuentaInicial", "'" + cuentaInicial + "'");
            reemplazar.put("cuentaFinal", "'" + cuentaFinal + "'");
            reemplazar.put("mes", mes);
            reemplazar.put("nivel", nivel);

            String cargoResolucionUno = validacionParametros(
                            "CARGO1 EN RESOLUCION 036",
                            "TB_TB905");
            String cargoResolucionDos = validacionParametros(
                            "CARGO2 EN RESOLUCION 036",
                            "TB_TB906");
            String firmaResolucionUno = validacionParametros(
                            "FIRMA1 EN RESOLUCION 036",
                            "TB_TB907");

            String firmaResolucionDos = validacionParametros(
                            "FIRMA2 EN RESOLUCION 036",
                            "TB_TB908");
            String cedulaResolucionUno = validacionParametros(
                            "CEDULA1 EN RESOLUCION 036",
                            "TB_TB909");
            String cedulaResolucionDos = validacionParametros(
                            "CEDULA2 EN RESOLUCION 036",
                            "TB_TB910");
            String seccionInformes = validacionParametros(
                            "SECCION EN INFORMES RESOLUCION 036", "TB_TB911");
            String seccion = validacionParametros("SECCION 036", "TB_TB912");

            String unidad = validacionParametros("UNIDAD EJECUTORA 036",
                            "TB_TB913");

            if (validacionParametrosUno(cargoResolucionUno, cargoResolucionDos,
                            firmaResolucionUno, firmaResolucionDos,
                            cedulaResolucionUno, cedulaResolucionDos)
                || validacionParametrosDos(seccionInformes, seccion, unidad))
            {
                return;
            }

            seccionInformes = SysmanFunciones.nvlStr(seccionInformes, "NO");

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_CARGOUNO", cargoResolucionUno);
            parametros.put("PR_CARGODOS", cargoResolucionDos);
            parametros.put("PR_FIRMAUNO", firmaResolucionUno);
            parametros.put("PR_FIRMADOS", firmaResolucionDos);
            parametros.put("PR_CEDULAUNO", cedulaResolucionUno);
            parametros.put("PR_CEDULADOS", cedulaResolucionDos);
            parametros.put("PR_NOMBRECOMPANIA", SessionUtil.getCompaniaIngreso()
                            .getNombre().toUpperCase());
            parametros.put("PR_NOMBREMES",
                            SysmanConstantes.NOMBRE_MESES_CONTABILIDAD[mes]);
            parametros.put("PR_ANO", ano);

            parametros.put(tituloSec,
                            seccionIn(seccionInformes, seccion, unidad)
                                            .split("#")[0]);
            parametros.put(sec,
                            seccionIn(seccionInformes, seccion, unidad)
                                            .split("#")[1]);

            parametros.put(tituloUni,
                            seccionIn(seccionInformes, seccion, unidad)
                                            .split("#")[2]);
            parametros.put(uni,
                            seccionIn(seccionInformes, seccion, unidad)
                                            .split("#")[3]);

            Reporteador.resuelveConsulta(parReporte(), Integer.parseInt(modulo),
                            reemplazar, parametros);
            archivoDescarga = JsfUtil.exportarStreamed(parReporte(), parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SystemException | SysmanException ex)
        {
            JsfUtil.agregarMensajeError(ex.getMessage());
            Logger.getLogger(RegistroejecucgastosrpsControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
        }

    }
    // <METODOS_CAMBIAR>

    public void cambiarAno()
    {
        cuentaInicial = null;
        cuentaFinal = null;
        cargarListaCuentaInicial();
    }
    // </METODOS_CAMBIAR>

    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCuentaInicial(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaInicial = registroAux.getCampos().get("ID").toString();
        cuentaFinal = null;
        cargarListaCuentaFinal();
    }

    public void seleccionarFilaCuentaFinal(SelectEvent event)
    {
        Registro registroAux = (Registro) event.getObject();
        cuentaFinal = registroAux.getCampos().get("ID").toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public boolean getIndicador()
    {
        return indicador;
    }

    public void setIndicador(boolean indicador)
    {
        this.indicador = indicador;
    }

    public boolean getMiles()
    {
        return miles;
    }

    public void setMiles(boolean miles)
    {
        this.miles = miles;
    }

    public String getCuentaInicial()
    {
        return cuentaInicial;
    }

    public void setCuentaInicial(String cuentaInicial)
    {
        this.cuentaInicial = cuentaInicial;
    }

    public String getCuentaFinal()
    {
        return cuentaFinal;
    }

    public void setCuentaFinal(String cuentaFinal)
    {
        this.cuentaFinal = cuentaFinal;
    }

    public int getAno()
    {
        return ano;
    }

    public void setAno(int ano)
    {
        this.ano = ano;
    }

    public int getMes()
    {
        return mes;
    }

    public void setMes(int mes)
    {
        this.mes = mes;
    }

    public int getNivel()
    {
        return nivel;
    }

    public void setNivel(int nivel)
    {
        this.nivel = nivel;
    }

    public StreamedContent getArchivoDescarga()
    {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    public List<Registro> getListaAno()
    {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno)
    {
        this.listaAno = listaAno;
    }

    public List<Registro> getListaMes()
    {
        return listaMes;
    }

    public void setListaMes(List<Registro> listaMes)
    {
        this.listaMes = listaMes;
    }

    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCuentaInicial()
    {
        return listaCuentaInicial;
    }

    public void setListaCuentaInicial(
        RegistroDataModelImpl listaCuentaInicial)
    {
        this.listaCuentaInicial = listaCuentaInicial;
    }

    public RegistroDataModelImpl getListaCuentaFinal()
    {
        return listaCuentaFinal;
    }

    public void setListaCuentaFinal(RegistroDataModelImpl listaCuentaFinal)
    {
        this.listaCuentaFinal = listaCuentaFinal;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}

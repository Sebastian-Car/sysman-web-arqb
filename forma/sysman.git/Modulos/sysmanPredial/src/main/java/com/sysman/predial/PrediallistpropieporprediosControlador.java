package com.sysman.predial;

import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.ReportesBean;
import com.sysman.jsfutil.ReportesBean.FORMATOS;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.predial.enums.PrediallistpropieporprediosControladorEnum;
import com.sysman.predial.enums.PrediallistpropieporprediosControladorUrlEnum;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanConstantes;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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
import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author ybecerra
 * @version 1, 27/05/2016
 *
 * -- Modificado por lcortes 17/02/2017 --> Se incluye el indicador
 * indBloqueados para determinar si el informe debe incluir los
 * predios borrados.
 * @version 1 -- Modificado por lcortes 24/02/2017 --> Se cambia el
 * nombre del indicador indBloqueados por indNoActivos para aclarar
 * que dicho indicador determina si en el informe se incluyen o no los
 * predios con codigo no activo o borrados.
 *
 * @author eamaya
 * @version 2.0, 13/06/2017 Se cambió el llamado del código del
 * formulario y actualización de ConnectorPool
 *
 * @version 3, 11/07/2017 jrodriguezr Se refactoriza el código SQL de
 * las listas para utilizar DSS. También los llamados a funciones,
 * procedimientos y métodos de la clase Acciones a llamados a EJB.
 * Textos al archivo properties.
 *
 */
@ManagedBean
@ViewScoped
public class PrediallistpropieporprediosControlador extends BeanBaseModal {
    private final String compania;
    private final String modulo;

    private final String strCodigo;

    // <DECLARAR_ATRIBUTOS>
    private String opcion;
    private boolean resumido;
    private boolean indBloqueados;
    private String codigoInicial;
    private String codigoFinal;
    private String ordenar;
    private String generar;
    private String nombreCodigoInicial;
    private String nombreCodigoFinal;
    private StreamedContent archivoDescarga;

    // </DECLARAR_ATRIBUTOS>
    // <DECLARAR_PARAMETROS>
    // </DECLARAR_PARAMETROS>
    // <DECLARAR_LISTAS>
    // </DECLARAR_LISTAS>
    // <DECLARAR_LISTAS_COMBO_GRANDE>
    private RegistroDataModelImpl listaCODIGOINICIAL;
    private RegistroDataModelImpl listaCODIGOFINAL;
    // </DECLARAR_LISTAS_COMBO_GRANDE>

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of
     * PrediallistpropieporprediosControlador
     */
    public PrediallistpropieporprediosControlador() {
        super();
        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();
        strCodigo = GeneralParameterEnum.CODIGO.getName();
        try {
            numFormulario = GeneralCodigoFormaEnum.PREDIALLISTPROPIEPORPREDIOS_CONTROLADOR
                            .getCodigo();
            validarPermisos();
            // <INI_ADICIONAL>
            // </INI_ADICIONAL>
        }
        catch (Exception ex) {
            Logger.getLogger(PrediallistpropieporprediosControlador.class
                            .getName()).log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        // <CARGAR_LISTA>
        // </CARGAR_LISTA>
        // <CARGAR_LISTA_COMBO_GRANDE>
        cargarListaCODIGOINICIAL();

        // </CARGAR_LISTA_COMBO_GRANDE>
        abrirFormulario();
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        generar = "3";
        opcion = "1";
        ordenar = "1";
        // </CODIGO_DESARROLLADO>
    }

    // <METODOS_CARGAR_LISTA>
    public void cargarListaCODIGOINICIAL() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PrediallistpropieporprediosControladorUrlEnum.URL4745
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        listaCODIGOINICIAL = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);
    }

    public void cargarListaCODIGOFINAL() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        PrediallistpropieporprediosControladorUrlEnum.URL5806
                                                        .getValue());
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.NUMERO_ORDEN.getName(),
                        SysmanConstantes.NUMERO_ORDEN_PREDIAL);
        param.put(PrediallistpropieporprediosControladorEnum.CODIGOINICIAL
                        .getValue(), codigoInicial);
        listaCODIGOFINAL = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, strCodigo);
    }

    // </METODOS_CARGAR_LISTA>
    // <METODOS_BOTONES>
    public void oprimirPresentar() {
        // <CODIGO_DESARROLLADO>
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirExcel() {
        // <CODIGO_DESARROLLADO>
        generarInforme(FORMATOS.EXCEL);
        // </CODIGO_DESARROLLADO>
    }

    private void generarInforme(ReportesBean.FORMATOS formato) {

        archivoDescarga = null;
        String condicion = "";
        String ordenado = "";
        String union = "";
        String titulo = "";
        String parReporte = "";
        String condBloqueado = "";
        String parametro = evaluarCondTipoPredio();
        try {
            if (!indBloqueados) {
                condBloqueado = "       AND IP_USUARIOS_PREDIAL.BLOQUEADO IN (0) \n";
            }
            if (resumido) {
                if ("3".equals(generar)) {
                    titulo = "Rurales";
                    condicion = SysmanFunciones.concatenar(
                                    "    AND SUBSTR(IP_USUARIOS_PREDIAL.CODIGO, 1, 2) IN(",
                                    parametro, ")");

                    union = SysmanFunciones.concatenar(
                                    "  UNION  SELECT 'Urbanos' AS TIPO_PREDIO, \n",
                                    "        COUNT(IP_USUARIOS_PREDIAL.CODIGO) AS NUMERO_PREDIOS,\n",
                                    "        COUNT(IP_USUARIOS_PREDIAL.NIT) AS NUMERO_PROPIE, \n",
                                    "        SUM(IP_USUARIOS_PREDIAL.AREA_HA) AS SUPERFICIE_HA, \n",
                                    "        SUM(IP_USUARIOS_PREDIAL.AREA_CONSTRUIDA) AS TOTAL_AREACONST,\n",
                                    "        SUM(IP_USUARIOS_PREDIAL.AVALUO_ANO) AS TOTAL_AVALUO \n",
                                    "   FROM IP_USUARIOS_PREDIAL \n",
                                    "   WHERE IP_USUARIOS_PREDIAL.COMPANIA = '",
                                    compania, "'",
                                    "     AND IP_USUARIOS_PREDIAL.CODIGO BETWEEN '",
                                    codigoInicial, "' AND '", codigoFinal,
                                    "' \n",
                                    "       AND IP_USUARIOS_PREDIAL.INDBORRADO IN(0)\n",
                                    "       AND IP_USUARIOS_PREDIAL.CODIGO_NO_ACTIVO IN(0) \n",
                                    condBloqueado,
                                    "       AND SUBSTR(IP_USUARIOS_PREDIAL.CODIGO, 1, 2) NOT IN(",
                                    parametro, ")");

                    union += validaOpcion();

                }
                parReporte = "000837PREDIALLISTPROPIEPORPREDIORES";
            }
            else {
                parReporte = "000832PREDIALLISTPROPIEPORPREDIO";
            }

            if ("1".equals(generar)) {
                condicion = SysmanFunciones.concatenar(
                                "    AND SUBSTR(IP_USUARIOS_PREDIAL.CODIGO, 1, 2) IN(",
                                parametro, ")");
                titulo = "Rurales";

            }
            else if ("2".equals(generar)) {
                condicion = SysmanFunciones.concatenar(
                                " AND SUBSTR(IP_USUARIOS_PREDIAL.CODIGO, 1, 2) NOT IN(",
                                parametro, ")");
                titulo = "Urbanos";
            }
            else {
                if (!resumido) {
                    titulo = "Todos";
                }
            }

            if ("1".equals(opcion)) {
                condicion = SysmanFunciones.concatenar(condicion,
                                "AND IP_USUARIOS_PREDIAL.NUMERO_ORDEN = '",
                                SysmanConstantes.NUMERO_ORDEN_PREDIAL, "'");
            }

            ordenado = obtenerOrden();

            HashMap<String, Object> reemplazar = new HashMap<>();
            reemplazar.put("codigoInicial", "'" + codigoInicial + "'");
            reemplazar.put("codigoFinal", "'" + codigoFinal + "'");
            reemplazar.put("condicion", condicion);
            reemplazar.put("condBloqueado", condBloqueado);
            reemplazar.put("ordenado", ordenado);
            reemplazar.put("tipoPredio", "'" + titulo + "'");
            reemplazar.put("union", union);

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("PR_CODIGO", idioma.getString("TB_TB3295")
                            .replace("#codigoInicial#", codigoInicial)
                            .replace("#codigoFinal#", codigoFinal));
            parametros.put("PR_TIPO", SysmanFunciones.concatenar(
                            " TIPO DE PREDIO ", titulo.toUpperCase()));

            Reporteador.resuelveConsulta(parReporte, Integer.parseInt(modulo),
                            reemplazar, parametros);

            archivoDescarga = JsfUtil.exportarStreamed(parReporte, parametros,
                            ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    private String evaluarCondTipoPredio() {
        String parametro;
        String param = null;
        try {
            param = ejbSysmanUtil.consultarParametro(compania,
                            idioma.getString("TB_TB3294"), modulo, new Date(),
                            true);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        param = param == null ? "00" : param;
        parametro = SysmanFunciones.colocarComillas(param);
        return parametro;
    }

    private String validaOpcion() {
        String opcion1 = "";
        if ("1".equals(opcion)) {
            opcion1 = SysmanFunciones.concatenar(
                            " AND IP_USUARIOS_PREDIAL.NUMERO_ORDEN = '",
                            SysmanConstantes.NUMERO_ORDEN_PREDIAL, "'");
        }
        return opcion1;
    }

    private String obtenerOrden() {
        String ordenado = "";
        switch (ordenar) {
        case "1":
            // orden por codigo
            ordenado = "  ORDER BY IP_USUARIOS_PREDIAL.CODIGO , IP_USUARIOS_PREDIAL.NUMERO_ORDEN ";
            break;
        case "2":
            // orden por avaluo
            ordenado = "    ORDER BY IP_USUARIOS_PREDIAL.AVALUO_ANO";
            break;
        case "3":
            // orden por direccion
            ordenado = "    ORDER BY IP_USUARIOS_PREDIAL.DIRECCION";
            break;
        case "4":
            ordenado = "    ORDER BY IP_USUARIOS_PREDIAL.NOMBRE";
            break;

        default:
            break;
        }
        return ordenado;
    }

    // </METODOS_BOTONES>
    // <METODOS_CAMBIAR>
    // </METODOS_CAMBIAR>
    // <METODOS_COMBOS_GRANDES>
    public void seleccionarFilaCODIGOINICIAL(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoInicial = registroAux.getCampos().get(strCodigo).toString();
        nombreCodigoInicial = registroAux.getCampos().get("NOMBRE").toString();
        codigoFinal = null;
        nombreCodigoFinal = null;
        cargarListaCODIGOFINAL();
    }

    public void seleccionarFilaCODIGOFINAL(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();
        codigoFinal = registroAux.getCampos().get(strCodigo).toString();
        nombreCodigoFinal = registroAux.getCampos().get("NOMBRE").toString();
    }

    // </METODOS_COMBOS_GRANDES>

    // <SET_GET_ATRIBUTOS>
    public String getOpcion() {
        return opcion;
    }

    public void setOpcion(String opcion) {
        this.opcion = opcion;
    }

    public boolean getResumido() {
        return resumido;
    }

    public void setResumido(boolean resumido) {
        this.resumido = resumido;
    }

    public boolean isIndBloqueados() {
        return indBloqueados;
    }

    public void setIndBloqueados(boolean indBloqueados) {
        this.indBloqueados = indBloqueados;
    }

    public String getCodigoInicial() {
        return codigoInicial;
    }

    public void setCodigoInicial(String codigoInicial) {
        this.codigoInicial = codigoInicial;
    }

    public String getCodigoFinal() {
        return codigoFinal;
    }

    public void setCodigoFinal(String codigoFinal) {
        this.codigoFinal = codigoFinal;
    }

    public String getOrdenar() {
        return ordenar;
    }

    public void setOrdenar(String ordenar) {
        this.ordenar = ordenar;
    }

    public String getGenerar() {
        return generar;
    }

    public void setGenerar(String generar) {
        this.generar = generar;
    }

    public String getNombreCodigoInicial() {
        return nombreCodigoInicial;
    }

    public void setNombreCodigoInicial(String nombreCodigoInicial) {
        this.nombreCodigoInicial = nombreCodigoInicial;
    }

    public String getNombreCodigoFinal() {
        return nombreCodigoFinal;
    }

    public void setNombreCodigoFinal(String nombreCodigoFinal) {
        this.nombreCodigoFinal = nombreCodigoFinal;
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    // </SET_GET_ATRIBUTOS>
    // <SET_GET_PARAMETROS>
    // </SET_GET_PARAMETROS>
    // <SET_GET_LISTAS>
    // </SET_GET_LISTAS>
    // <SET_GET_LISTAS_COMBO_GRANDE>
    public RegistroDataModelImpl getListaCODIGOINICIAL() {
        return listaCODIGOINICIAL;
    }

    public void setListaCODIGOINICIAL(
        RegistroDataModelImpl listaCODIGOINICIAL) {
        this.listaCODIGOINICIAL = listaCODIGOINICIAL;
    }

    public RegistroDataModelImpl getListaCODIGOFINAL() {
        return listaCODIGOFINAL;
    }

    public void setListaCODIGOFINAL(RegistroDataModelImpl listaCODIGOFINAL) {
        this.listaCODIGOFINAL = listaCODIGOFINAL;
    }
    // </SET_GET_LISTAS_COMBO_GRANDE>
}

package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.FrmInversionDistribuidaControladorUrlEnum;
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
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.persistencia.ConectorPool;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.reportes.Reporteador;
import com.sysman.util.SysmanFunciones;

import java.io.IOException;
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

import org.primefaces.model.StreamedContent;

import net.sf.jasperreports.engine.JRException;

/**
 *
 * @author esarmiento
 * @version 1, 23/09/2015
 * 
 * @version 2, 19/09/2017, <strong>pespitia</strong>
 * <li>Se reemplazo la creación de conexiones:{@link ConectorPool}.
 * <li>Se reemplazo el numero del formulario por enumerado.
 * <li>Manejo de EJBs.
 * <li>Se aplicaron las recomendaciones de SonarLint.
 */
@ManagedBean
@ViewScoped
public class FrmInversionDistribuidaControlador extends BeanBaseModal {

    private final String compania;
    private final String modulo;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>
     */
    private final String cCompania;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>ANO</code>
     */
    private final String cAno;

    private int anioInicial;
    private int anioFinal;
    private String nivel;
    private List<Registro> listaAnoInicial;
    private List<Registro> listaAnoFinal;
    private List<Registro> listaNIVEL;
    private StreamedContent archivoDescarga;

    // <MANEJO DE EJBs>
    /**
     * Variable que permite acceder a las funCiones y procedimientos
     * del paquete: <code>PCK_SYMSMAN_UTL</code>
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;
    // </MANEJO DE EJBs>

    /**
     * Creates a new instance of FrmInversionDistribuidaControlador
     */
    public FrmInversionDistribuidaControlador() {
        super();

        compania = SessionUtil.getCompania();
        modulo = SessionUtil.getModulo();

        cCompania = GeneralParameterEnum.COMPANIA.getName();
        cAno = GeneralParameterEnum.ANO.getName();

        try {
            // 241
            numFormulario = GeneralCodigoFormaEnum.FRM_INVERSION_DISTRIBUIDA_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(FrmInversionDistribuidaControlador.class.getName())
                            .log(Level.SEVERE, null, ex);
            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        cargarParametros();

        anioFinal = anioInicial + 3;

        cargarListaAnoInicial();
        cargarListaAnoFinal();
        cargarListaNIVEL();
        abrirFormulario();
    }

    /**
     * Recupera el valor de los parametros necesarios al abrir el
     * formulario
     */
    private void cargarParametros() {
        try {
            String valor = recuperarValorPar("VIGENCIA GUBERNAMENTAL ACTUAL");

            anioInicial = validarParametro("VIGENCIA GUBERNAMENTAL ACTUAL",
                            valor) ? Integer.parseInt(valor)
                                : SysmanFunciones.ano(new Date());
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    @Override
    public void abrirFormulario() {
        // </CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaAnoInicial() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        try {
            listaAnoInicial = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmInversionDistribuidaControladorUrlEnum.URL3780
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaAnoFinal() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(cAno, anioInicial);

        try {
            listaAnoFinal = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmInversionDistribuidaControladorUrlEnum.URL4172
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cargarListaNIVEL() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        try {
            listaNIVEL = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmInversionDistribuidaControladorUrlEnum.URL4565
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void oprimirAceptar() {
        // <CODIGO_DESARROLLADO>
        generarInforme(FORMATOS.PDF);
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirBtExcel() {
        // <CODIGO_DESARROLLADO>
        generarInforme(FORMATOS.EXCEL97);
        // </CODIGO_DESARROLLADO>
    }

    private void generarInforme(FORMATOS formato) {
        archivoDescarga = null;

        Map<String, Object> parametros = new HashMap<>();
        Map<String, Object> reemplazar = new HashMap<>();

        String nombreReporte = "000241RPTINVERSIONDISTRIBUIDA";

        // <ENVIAR PARAMETROS AL REPORTE>
        parametros.put("PR_NIVEL", nivel);
        parametros.put("PR_ANIO1", anioInicial);
        parametros.put("PR_ANIO2", anioFinal);
        // </ENVIAR PARAMETROS AL REPORTE>

        // <REEMPLAZAR VARIABLES EN CONSULTA>
        reemplazar.put("anioInicial", anioInicial);
        reemplazar.put("anioFinal", anioFinal);
        reemplazar.put("nivel", SysmanFunciones.concatenar("'", nivel, "'"));
        // </REEMPLAZAR VARIABLES EN CONSULTA>

        Reporteador.resuelveConsulta(nombreReporte,
                        Integer.parseInt(modulo), reemplazar, parametros);

        try {
            archivoDescarga = JsfUtil.exportarStreamed(nombreReporte,
                            parametros, ConectorPool.ESQUEMA_SYSMAN, formato);
        }
        catch (JRException | IOException | SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cambiarAnoInicial() {
        // <CODIGO_DESARROLLADO>
        anioFinal = 0;

        cargarListaAnoFinal();
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarAnoFinal() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Util para verificar que el parametro {@code nomPar} existe en
     * la base de datos. De lo contrario muestra un mensaje
     * informativo.
     * 
     * @param nomPar
     * Nombre del parametro.
     * @param valor
     * Valor asignado al parametro en la base de datos.
     * @return {@code true}: si el parametro existe y tiene valor
     * diferente a nulo.
     */
    private boolean validarParametro(String nomPar, String valor) {
        if (SysmanFunciones.validarVariableVacio(valor)) {
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3611")
                            .replace("#PAR#", nomPar));
            return false;
        }

        return true;
    }

    /**
     * Consulta y retorna el valor asignado al parametro segun la base
     * de datos.
     * 
     * @param nombrePar
     * Nombre asignado al parametro
     * @return El valor del parametro asignado en la base de datos.
     * @throws SystemException
     */
    private String recuperarValorPar(String nombrePar) throws SystemException {
        return ejbSysmanUtil.consultarParametro(compania, nombrePar, modulo,
                        new Date(), false);
    }

    public StreamedContent getArchivoDescarga() {
        return archivoDescarga;
    }

    public int getAnioFinal() {
        return anioFinal;
    }

    public void setAnioFinal(int anioFinal) {
        this.anioFinal = anioFinal;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public int getAnioInicial() {
        return anioInicial;
    }

    public void setAnioInicial(int anioInicial) {
        this.anioInicial = anioInicial;
    }

    public List<Registro> getListaAnoInicial() {
        return listaAnoInicial;
    }

    public void setListaAnoInicial(List<Registro> listaAnoInicial) {
        this.listaAnoInicial = listaAnoInicial;
    }

    public List<Registro> getListaAnoFinal() {
        return listaAnoFinal;
    }

    public void setListaAnoFinal(List<Registro> listaAnoFinal) {
        this.listaAnoFinal = listaAnoFinal;
    }

    public List<Registro> getListaNIVEL() {
        return listaNIVEL;
    }

    public void setListaNIVEL(List<Registro> listaNIVEL) {
        this.listaNIVEL = listaNIVEL;
    }
}
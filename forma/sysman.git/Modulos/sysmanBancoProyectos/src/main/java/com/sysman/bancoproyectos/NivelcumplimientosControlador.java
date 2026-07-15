package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.enums.NivelcumplimientosControladorUrlEnum;
import com.sysman.beanbase.BeanBaseContinuoAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.util.SysmanFunciones;

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

import org.primefaces.event.RowEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author lcortes
 * @version 1, 13/08/2015
 * 
 * @version 2, 26/09/2017, <strong>pespitia</strong>:
 * <li>Se reemplazo el numero del formulario por enumerado.
 * <li>Refactoring de sentencias SQL.
 * <li>Manejo de EJBs.
 */
@ManagedBean
@ViewScoped
public class NivelcumplimientosControlador extends BeanBaseContinuoAcmeImpl {

    private final String compania;
    private final String modulo = SessionUtil.getModulo();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>ANO</code>
     */
    private final String cAno;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>
     */
    private final String cCompania;

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>VIGENCIA</code>
     */
    private final String cVigencia;

    /**
     * Atributo que contiene el anio seleccionado en el combo anio
     * gubernamental.
     */
    private int anio;

    private List<Registro> listaAno;

    /**
     * Variable util para acceder a las funciones y procedimientos del
     * paquete: PCK_SYSMAN_UTL
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Creates a new instance of NivelcumplimientosControlador
     */
    public NivelcumplimientosControlador() {
        super();

        compania = SessionUtil.getCompania();

        cAno = GeneralParameterEnum.ANO.getName();
        cCompania = GeneralParameterEnum.COMPANIA.getName();
        cVigencia = GeneralParameterEnum.VIGENCIA.getName();

        try {
            // 122
            numFormulario = GeneralCodigoFormaEnum.NIVELCUMPLIMIENTOS_CONTROLADOR
                            .getCodigo();

            validarPermisos();
        }
        catch (Exception ex) {
            Logger.getLogger(NivelcumplimientosControlador.class.getName())
                            .log(Level.SEVERE, null, ex);

            SessionUtil.redireccionarMenuPermisos();
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.BP_NIVEL_CUMPLIMIENTO;
        registro = new Registro(new HashMap<String, Object>());

        inicializarParametros();
        buscarLlave();
        reasignarOrigen();
        cargarListaAno();
        abrirFormulario();
    }

    @Override
    public void reasignarOrigen() {
        buscarUrls();

        parametrosListado.put(cCompania, compania);
        parametrosListado.put(cAno, anio);
    }

    public List<Registro> getListaAno() {
        return listaAno;
    }

    public void setListaAno(List<Registro> listaAno) {
        this.listaAno = listaAno;
    }

    public void cargarListaAno() {
        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);

        try {
            listaAno = RegistroConverter.toListRegistro(
                            requestManager.getList(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            NivelcumplimientosControladorUrlEnum.URL3772
                                                                            .getValue())
                                            .getUrl(), param));
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void cambiarAno() {
        cargado = false;

        reasignarOrigen();
    }

    public void oprimirPasa() {
        // <CODIGO_DESARROLLADO>
        String[] campos = { "opcion" };
        String[] valores = { "NivCum" };

        SessionUtil.cargarModalDatos(
                        Integer.toString(
                                        GeneralCodigoFormaEnum.FRMACTUVIG_CONTROLADOR
                                                        .getCodigo()),
                        modulo, campos, valores);
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cancelarEdicion(RowEditEvent event) {
        listaInicial.load();
    }

    public void retornarFormularioPasa(SelectEvent event) {
        // <CODIGO_DESARROLLADO>
        if (event.getObject() != null) {
            String[] dato = (String[]) event.getObject();
            anio = Integer.parseInt(dato[0]);

            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB2383")
                            .replace("#$dato1#$", dato[1])
                            .replace("#$dato2#$", dato[2])
                            .replace("#$dato0#$", dato[0]));

            cargarListaAno();
            cambiarAno();
        }

        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void removerCombos() {
        // <CODIGO_DESARROLLADO>
        registro.getCampos().remove(cCompania);
        registro.getCampos().remove(cVigencia);
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put(cCompania, compania);
        registro.getCampos().put(cVigencia, anio);

        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        return validarRangoInferior();
    }

    @Override
    public boolean actualizarDespues() {
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        return true;
    }

    @Override
    public boolean eliminarDespues() {
        return true;
    }

    @Override
    public void asignarValoresRegistro() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Util para inicializar las variables que dependen de parametros
     * del sistema
     */
    private void inicializarParametros() {
        try {
            String valor = recuperarValorPar("VIGENCIA GUBERNAMENTAL ACTUAL");

            anio = validarParametro("VIGENCIA GUBERNAMENTAL ACTUAL", valor)
                ? Integer.parseInt(valor) : SysmanFunciones.ano(new Date());
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
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
            JsfUtil.agregarMensajeInformativo(idioma.getString("TB_TB3649")
                            .replace("#PAR#", nomPar));
            return false;
        }

        return true;
    }

    /**
     * Verifica que el valor ingresado en el rango inferior sea menor
     * al valor del rango superior.
     * 
     * @return <code>true</code>: Cuando el rango inferior sea menor
     * al rango superior.
     */
    private boolean validarRangoInferior() {
        int rangoInferior = Integer
                        .parseInt(SysmanFunciones
                                        .nvl(registro.getCampos().get(
                                                        "LIM_INFERIOR"), "0")
                                        .toString());

        int rangoSuperior = Integer
                        .parseInt(SysmanFunciones
                                        .nvl(registro.getCampos().get(
                                                        "LIM_SUPERIOR"), "0")
                                        .toString());

        if (rangoInferior > rangoSuperior) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB3652"));
            return false;
        }

        return true;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }
}

package com.sysman.bancoproyectos;

import com.sysman.bancoproyectos.ejb.EjbBancoProyectoUnoRemote;
import com.sysman.bancoproyectos.enums.FrmprogramacionactividadesControladorEnum;
import com.sysman.bancoproyectos.enums.FrmprogramacionactividadesControladorUrlEnum;
import com.sysman.beanbase.BeanBaseDatosAcmeImpl;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.enums.GenericUrlEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.math.BigDecimal;
import java.math.BigInteger;
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
 *
 * @author dmaldonado
 * @version 1, 23/10/2015
 *
 * @author jguerrero
 * @version 2, 21/09/2017
 * 
 * @version 3, 23/10/2017, <strong>pespitia</strong>:
 * <li>Se reemplazo el numero del formulario por enumerado.
 * <li>Refactoring de sentencias SQL.
 * <li>Manejo de EJBs.
 * <li>Se aplicaron las recomendaciones de SonarLint.
 */
@ManagedBean
@ViewScoped
public class FrmprogramacionactividadesControlador
                extends BeanBaseDatosAcmeImpl {

    private final String compania;

    /**
     * Constante a nivel de clase que aloja el codigo del usuario que
     * inicio sesion.
     */
    private final String usuario = SessionUtil.getUser().getCodigo();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>ACTIVIDAD</code>
     */
    private final String cActividad = GeneralParameterEnum.ACTIVIDAD.getName();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>CODIGO</code>
     */
    private final String cCodigo = GeneralParameterEnum.CODIGO.getName();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>CODIGOCOMPONENTE</code>
     */
    private final String cCodigoComponente = FrmprogramacionactividadesControladorEnum.CODIGOCOMPONENTE
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>CODIGOPROYECTO</code>
     */
    private final String cCodigoProyecto = FrmprogramacionactividadesControladorEnum.CODIGOPROYECTO
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPANIA</code>
     */
    private final String cCompania = GeneralParameterEnum.COMPANIA.getName();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>COMPONENTE</code>
     */
    private final String cComponente = FrmprogramacionactividadesControladorEnum.COMPONENTE
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>MI_VIGENCIA</code>
     */
    private final String cMiVigencia = FrmprogramacionactividadesControladorEnum.MI_VIGENCIA
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>PROYECTO</code>
     */
    private final String cProyecto = GeneralParameterEnum.PROYECTO.getName();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>TIPOESTADO</code>
     */
    private final String cTipoEstado = FrmprogramacionactividadesControladorEnum.TIPOESTADO
                    .getValue();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>VALOR</code>
     */
    private final String cValor = GeneralParameterEnum.VALOR.getName();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>VALORTOTAL</code>
     */
    private final String cValorTotal = GeneralParameterEnum.VALORTOTAL
                    .getName();

    /**
     * Constante a nivel de clase que aloja la cadena:
     * <code>VIGENCIA</code>
     */
    private final String cVigencia = GeneralParameterEnum.VIGENCIA.getName();

    /**
     * Constante que almacenara la cadena "TIPOT_QUEAPRUEBA"
     */
    private final String tipoTQueaPruebaC;

    /**
     * Constante que almacenara la cadena "DEPENDENCIA_QUEAPRUEBA"
     */
    private final String dependenciaQuaPruebaC;

    /**
     * Constante que almacenara la cadena "CODIGO_QUEAPRUEBA"
     */
    private final String codigoQueApruebaC;

    /**
     * Constante que almacenara la cadena "CODIGOITEM_QUEAPRUEBA"
     */
    private final String codigoItemQeapruebaC;

    /**
     * Constante que almacenara la cadena "CODIGOACTIVIDAD"
     */
    private final String codigoActividadC;

    /**
     * Constante que almacenara la cadena "CANTIDAD"
     */
    private final String cantidadC;

    private RegistroDataModelImpl listaCodigoActividad;
    private String proyecto;
    private String componente;
    private int vigencia;
    private String tipoEstado;
    private String[] valoresVisibles = new String[12];
    private int periodicidad;
    private String tipoComponente;
    private String tipoTApProg;
    private String claseTApProg;
    private BigInteger codigoApProg;
    private BigInteger itemApProg;
    private String dependenciaApProg;
    private String nombreActividad;
    private String valorAprobadoItem;
    private String valorProgramadoItem;
    private BigDecimal totalProyecto;
    private int periodoProyecto;
    private String codigoActividad;

    private BigDecimal valorTotalActividad;

    // <MANEJO DE EJBs>
    /**
     * Variable que permite acceder a las funciones y procedimientos
     * del paquete: <code>PCK_SYSMAN_UTL</code>.
     */
    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtil;

    /**
     * Variable que permite acceder a las funciones y procedimientos
     * del paquete: <code>PCK_BANCOS_PROY1</code>.
     */
    @EJB
    private EjbBancoProyectoUnoRemote ejbBancoProyectoUno;
    // </MANEJO DE EJBs>

    public FrmprogramacionactividadesControlador() {
        super();

        compania = SessionUtil.getCompania();

        tipoTQueaPruebaC = "TIPOT_QUEAPRUEBA";
        dependenciaQuaPruebaC = "DEPENDENCIA_QUEAPRUEBA";
        codigoQueApruebaC = "CODIGO_QUEAPRUEBA";
        codigoItemQeapruebaC = "CODIGOITEM_QUEAPRUEBA";
        codigoActividadC = "CODIGOACTIVIDAD";
        cantidadC = "CANTIDAD";

        Map<String, Object> parametrosEntrada = SessionUtil.getFlash();

        if (parametrosEntrada != null) {
            proyecto = parametrosEntrada.get("proyecto").toString();
            componente = parametrosEntrada.get("componente").toString();

            vigencia = Integer.parseInt(
                            parametrosEntrada.get("vigencia").toString());

            tipoComponente = parametrosEntrada.get("tipoComponente").toString();
            tipoTApProg = parametrosEntrada.get("tipoTApProg").toString();
            claseTApProg = parametrosEntrada.get("claseTApProg").toString();
            codigoApProg = validarBigInteger(parametrosEntrada, "codigoApProg");
            itemApProg = validarBigInteger(parametrosEntrada, "itemApProg");

            dependenciaApProg = parametrosEntrada.get("dependenciaApProg")
                            .toString();

            valorAprobadoItem = parametrosEntrada.get("valorAprobadoItem")
                            .toString();

            valorProgramadoItem = parametrosEntrada.get("valorProgramadoItem")
                            .toString();

            totalProyecto = new BigDecimal(
                            parametrosEntrada.get("totalProyecto").toString());

            periodoProyecto = Integer.parseInt(parametrosEntrada
                            .get("periodoProyecto").toString());

            tipoEstado = parametrosEntrada.get("tipoEstado").toString();

            periodicidad = Integer.parseInt(
                            parametrosEntrada.get("periodicidad").toString());
        }

        try {
            // 304
            numFormulario = GeneralCodigoFormaEnum.FRMPROGRAMACIONACTIVIDADES_CONTROLADOR
                            .getCodigo();

            validarPermisos();

            registro = new Registro(new HashMap<String, Object>());
        }
        catch (SysmanException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @PostConstruct
    public void inicializar() {
        enumBase = GenericUrlEnum.PROGRAMACION;

        buscarLlave();
        asignarOrigenDatos();

        verValores(periodicidad);
    }

    @Override
    public void asignarOrigenDatos() {
        buscarUrls();

        parametrosListado.put(cCompania, compania);
        parametrosListado.put(cProyecto, proyecto);
        parametrosListado.put(cComponente, componente);
        parametrosListado.put(cMiVigencia, vigencia);
        parametrosListado.put(cTipoEstado, tipoEstado);
    }

    @Override
    public void iniciarListas() {
        cargarListaCodigoActividad();
    }

    @Override
    public void iniciarListasSub() {
        Map<String, Object> param = new HashMap<>();
        param.put(cActividad, registro.getCampos().get(codigoActividadC));

        try {
            Registro registroAuxiliar = listaCodigoActividad
                            .getRegistroUnico(param);

            registro.getCampos().put(cantidadC,
                            registroAuxiliar.getCampos().get(cantidadC));

            registro.getCampos().put(cValorTotal,
                            registroAuxiliar.getCampos().get(cValorTotal));

            nombreActividad = registroAuxiliar.getCampos().get("NOMBRE")
                            .toString();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        calcularTotal();
    }

    @Override
    public void iniciarListasSubNulo() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaCodigoActividad() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        FrmprogramacionactividadesControladorUrlEnum.URL0010
                                                        .getValue());

        Map<String, Object> param = new TreeMap<>();
        param.put(cCompania, compania);
        param.put(cProyecto, proyecto);
        param.put(cComponente, componente);

        listaCodigoActividad = new RegistroDataModelImpl(urlBean.getUrl(),
                        urlBean.getUrlConteo().getUrl(), param,
                        true, cActividad);
    }

    public void calcularTotal() {
        double total = 0;

        for (int i = 1; i <= 12; i++) {
            if (registro.getCampos().get(cValor + i) != null) {
                total += validarValor(i);
            }
        }

        valorTotalActividad = BigDecimal.valueOf(total);
    }

    public double validarValor(int i) {
        double total = 0;

        if (!SysmanFunciones.validarCampoVacio(registro.getCampos(),
                        cValor + i)) {
            total = total + Double.valueOf(registro.getCampos()
                            .get(cValor + i).toString());
        }

        return total;
    }

    public void cambiarValorCuatro() {
        // <CODIGO_DESARROLLADO>
        ejecutarCambioValor("4");
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarValorTres() {
        // <CODIGO_DESARROLLADO>
        ejecutarCambioValor("3");
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarValorDos() {
        // <CODIGO_DESARROLLADO>
        ejecutarCambioValor("2");
        // </CODIGO_DESARROLLADO>
    }

    /**
     * Metodo que se ejecuta al cambiar el valor del campo $1 en la
     * forma.
     */
    public void cambiarValorUno() {
        // <CODIGO_DESARROLLADO>
        ejecutarCambioValor("1");
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarValorDoce() {
        // <CODIGO_DESARROLLADO>
        ejecutarCambioValor("12");
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarValorOnce() {
        // <CODIGO_DESARROLLADO>
        ejecutarCambioValor("11");
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarValorDiez() {
        // <CODIGO_DESARROLLADO>
        ejecutarCambioValor("10");
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarValorNueve() {
        // <CODIGO_DESARROLLADO>
        ejecutarCambioValor("9");
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarValorCinco() {
        // <CODIGO_DESARROLLADO>
        ejecutarCambioValor("5");
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarValorSeis() {
        // <CODIGO_DESARROLLADO>
        ejecutarCambioValor("6");
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarValorSiete() {
        // <CODIGO_DESARROLLADO>
        ejecutarCambioValor("7");
        // </CODIGO_DESARROLLADO>
    }

    public void cambiarValorOcho() {
        // <CODIGO_DESARROLLADO>
        ejecutarCambioValor("8");
        // </CODIGO_DESARROLLADO>
    }

    public void seleccionarFilaCodigoActividad(SelectEvent event) {
        Registro registroAux = (Registro) event.getObject();

        registro.getCampos().put(codigoActividadC,
                        registroAux.getCampos().get(cActividad));

        registro.getCampos().put(cValorTotal,
                        registroAux.getCampos().get(cValorTotal));

        registro.getCampos().put(cantidadC,
                        registroAux.getCampos().get(cantidadC));

        nombreActividad = SysmanFunciones
                        .nvl(registroAux.getCampos().get("NOMBRE"), "")
                        .toString();
    }

    public void verValores(int periodos) {
        for (int i = 0; i < periodos; i++) {
            valoresVisibles[i] = "block";
        }

        if (periodos < 12) {
            for (int i = periodos; i < 12; i++) {
                valoresVisibles[i] = "none";
            }
        }
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void cargarRegistro() {
        // <CODIGO_DESARROLLADO>
        precargarRegistro();

        if (css == null) {
            for (int i = 1; i < 13; i++) {
                registro.getCampos().put(cValor + i, 0);
                registro.getCampos().put("PORCENTAJE" + i, 0);
            }

            try {
                String criterio = SysmanFunciones.concatenar("COMPANIA = ''",
                                compania, "'' AND CODIGOPROYECTO = ''",
                                proyecto, "'' AND CODIGOCOMPONENTE = ''",
                                componente, "'' AND VIGENCIA = ",
                                Integer.toString(vigencia),
                                " AND TIPOESTADO = ''", tipoEstado, "''");

                long consecutivo = ejbSysmanUtil.generarSiguienteConsecutivo(
                                GenericUrlEnum.PROGRAMACION.getTable(),
                                criterio, cCodigo);

                registro.getCampos().put(cCodigo, consecutivo);
            }
            catch (SystemException ex) {
                logger.error(ex.getMessage(), ex);
                JsfUtil.agregarMensajeError(ex.getMessage());
            }

            registro.getCampos().put(tipoTQueaPruebaC, "");
            registro.getCampos().put("CLASET_QUEAPRUEBA", "");
            registro.getCampos().put(codigoQueApruebaC, 0);
            registro.getCampos().put(dependenciaQuaPruebaC, "");
            registro.getCampos().put(codigoItemQeapruebaC, "");
            registro.getCampos().put(cCodigoProyecto, proyecto);
            registro.getCampos().put(cVigencia, vigencia);
            registro.getCampos().put(cTipoEstado, tipoEstado);
            registro.getCampos().put(cCodigoComponente, componente);

            asignarNombreTipoEstado();
        }
    }

    public void cerrarFormulario() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    /**
     * Ejecuta el proceso por el cual se calcula el porcentaje del
     * valor asociado al campo que este siendo modificado.
     * 
     * @param nomCampo
     * -> Nombre del campo que esta siendo modificado.
     * @param nomPorcentaje
     * -> Nombre del campo del porcentaje.
     */
    private void calcularPorcentajeValor(String nomCampo,
        String nomPorcentaje) {
        String valor = registro.getCampos().get(nomCampo).toString();
        String valorAnt = registroIni.get(nomCampo).toString();

        String tipoTQueAprueba = SysmanFunciones
                        .nvl(registro.getCampos().get("TIPOT_QUEAPRUEBA"), "")
                        .toString();

        String claseTQueAprueba = SysmanFunciones
                        .nvl(registro.getCampos().get("CLASET_QUEAPRUEBA"), "")
                        .toString();

        BigInteger codigoQueAprueba = validarBigInteger(registro.getCampos(),
                        "CODIGO_QUEAPRUEBA");

        BigInteger codigoItemQueAprueba = validarBigInteger(
                        registro.getCampos(), "CODIGOITEM_QUEAPRUEBA");

        String dependenciaQueAprueba = registro.getCampos()
                        .get("DEPENDENCIA_QUEAPRUEBA").toString();

        String codigo = SysmanFunciones
                        .nvl(registro.getCampos().get(cCodigo), "").toString();

        String cantidad = SysmanFunciones
                        .nvl(registro.getCampos().get("CANTIDAD"), "")
                        .toString();

        codigoActividad = registro.getCampos().get("CODIGOACTIVIDAD")
                        .toString();

        try {
            double porcentaje = ejbBancoProyectoUno
                            .calcularPorcentaje(compania, tipoComponente,
                                            componente, proyecto, vigencia,
                                            valorAprobadoItem,
                                            valorProgramadoItem, tipoEstado,
                                            totalProyecto,
                                            valor,
                                            valorAnt, periodoProyecto,
                                            tipoTApProg, tipoTQueAprueba,
                                            claseTApProg,
                                            claseTQueAprueba, codigoQueAprueba,
                                            codigoApProg,
                                            codigoItemQueAprueba, itemApProg,
                                            dependenciaQueAprueba,
                                            dependenciaApProg, codigoActividad,
                                            nombreActividad,
                                            codigo, cantidad,
                                            valorTotalActividad, usuario);

            calcularTotal();
            registro.getCampos().put(nomPorcentaje, porcentaje);
            agregarRegistroNuevo(false);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    // ---------------------------------------
    // ------------ GET AND SET --------------
    // ---------------------------------------
    public RegistroDataModelImpl getListaCodigoActividad() {
        return listaCodigoActividad;
    }

    public void setListaCodigoActividad(
        RegistroDataModelImpl listaCodigoActividad) {
        this.listaCodigoActividad = listaCodigoActividad;
    }

    public String getProyecto() {
        return proyecto;
    }

    public void setProyecto(String proyecto) {
        this.proyecto = proyecto;
    }

    public String getComponente() {
        return componente;
    }

    public void setComponente(String componente) {
        this.componente = componente;
    }

    public String getTipoEstado() {
        return tipoEstado;
    }

    public void setTipoEstado(String tipoEstado) {
        this.tipoEstado = tipoEstado;
    }

    public String[] getValoresVisibles() {
        return valoresVisibles;
    }

    public void setValoresVisibles(String[] valoresVisibles) {
        this.valoresVisibles = valoresVisibles;
    }

    public int getPeriodicidad() {
        return periodicidad;
    }

    public void setPeriodicidad(int periodicidad) {
        this.periodicidad = periodicidad;
    }

    public String getTipoComponente() {
        return tipoComponente;
    }

    public void setTipoComponente(String tipoComponente) {
        this.tipoComponente = tipoComponente;
    }

    public String getTipoTApProg() {
        return tipoTApProg;
    }

    public void setTipoTApProg(String tipoTApProg) {
        this.tipoTApProg = tipoTApProg;
    }

    public String getClaseTApProg() {
        return claseTApProg;
    }

    public void setClaseTApProg(String claseTApProg) {
        this.claseTApProg = claseTApProg;
    }

    public String getDependenciaApProg() {
        return dependenciaApProg;
    }

    public void setDependenciaApProg(String dependenciaApProg) {
        this.dependenciaApProg = dependenciaApProg;
    }

    public String getValorAprobadoItem() {
        return valorAprobadoItem;
    }

    public void setValorAprobadoItem(String valorAprobadoItem) {
        this.valorAprobadoItem = valorAprobadoItem;
    }

    public String getValorProgramadoItem() {
        return valorProgramadoItem;
    }

    public void setValorProgramadoItem(String valorProgramadoItem) {
        this.valorProgramadoItem = valorProgramadoItem;
    }

    public String getCodigoActividad() {
        return codigoActividad;
    }

    public void setCodigoActividad(String codigoActividad) {
        this.codigoActividad = codigoActividad;
    }

    @Override
    public boolean insertarAntes() {
        registro.getCampos().put(cCompania, compania);
        registro.getCampos().put(cCodigoProyecto, proyecto);
        registro.getCampos().put(cCodigoComponente, componente);
        registro.getCampos().put("TIPOCOMPONENTE", tipoComponente);
        registro.getCampos().put(cVigencia, vigencia);
        registro.getCampos().put(cTipoEstado, tipoEstado);

        return true;
    }

    @Override
    public boolean insertarDespues() {
        return true;
    }

    @Override
    public boolean actualizarAntes() {
        registro.getCampos().remove("NOMBRETIPOESTADO");

        if (css != null) {
            registro.getCampos().remove(cCompania);
            registro.getCampos().remove(cCodigoProyecto);
            registro.getCampos().remove(cCodigoComponente);
            registro.getCampos().remove(cVigencia);
            registro.getCampos().remove(cTipoEstado);
        }

        return true;
    }

    @Override
    public boolean actualizarDespues() {
        return true;
    }

    @Override
    public boolean eliminarAntes() {
        Registro auxReg = null;

        try {
            auxReg = RegistroConverter.toRegistro(requestManager.get(
                            UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            FrmprogramacionactividadesControladorUrlEnum.URL0011
                                                                            .getValue())
                                            .getUrl(),
                            registro.getLlave()));

        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        if (auxReg == null) {
            return false;
        }

        for (int i = 1; i <= periodicidad; i++) {
            if (!"0".equals(auxReg.getCampos().get(cValor + i)
                            .toString())) {
                JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB2391"));
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean eliminarDespues() {
        try {
            ejbBancoProyectoUno.eliminarProgramacionActividad(compania,
                            proyecto, tipoComponente, componente,
                            codigoActividad, vigencia, usuario);
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

        return true;
    }

    /**
     * Asigna el nombre del tipo de estado.
     * <li><strong>P </strong> : Programado
     * <li><strong>RP</strong> : Reprogramado
     * <li><strong>E </strong> : Ejecutado
     */
    private void asignarNombreTipoEstado() {
        String nombre;

        switch (tipoEstado) {
        case "P":
            nombre = "Programado";
            break;
        case "RP":
            nombre = "Reprogramado";
            break;
        default:
            nombre = "Ejecutado";
            break;
        }

        registro.getCampos().put("NOMBRETIPOESTADO", nombre);
    }

    /**
     * Realiza el casting de una cadena a un valor de tipo BigInteger.
     * Cuando el campo tiene valor nulo retorna cero.
     * 
     * @param param
     * -> Coleccion clave-valor que contiene el campo.
     * @param campo
     * -> Nombre del campo que contiene el numero.
     * @return El numero equivalente en BigInteger.
     */
    private BigInteger validarBigInteger(Map<String, Object> param,
        String campo) {
        return SysmanFunciones.validarCampoVacio(param, campo) ? BigInteger.ZERO
            : new BigInteger(param.get(campo).toString());
    }

    /**
     * Metodo que ejecuta el proceso de cambiar el valor contenido en
     * el campo <code>$n</code> en la vista, en donde <code>n</code>
     * representa un numero del 1 al 12.
     * 
     * @param num
     * -> Numero del 1 al 12. Hace referencia al numero del campo que
     * esta siendo modificado.
     */
    private void ejecutarCambioValor(String num) {
        if (css == null) { // Al insertar
            registro.getCampos().put(cValor.concat(num), 0);
            JsfUtil.agregarMensajeError(idioma.getString("TB_TB2384"));
            return;
        }

        calcularPorcentajeValor(cValor.concat(num), "PORCENTAJE".concat(num));
        cargarRegistro(css, accion, registro.getIndice());
    }
}

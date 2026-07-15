package com.sysman.almacen;

import com.sysman.almacen.ejb.EjbAlmacenCincoRemote;
import com.sysman.almacen.enums.MovimientosControladorUrlEnum;
import com.sysman.almacen.enums.SeleccionarPlacasControladorEnum;
import com.sysman.almacen.enums.SeleccionarPlacasControladorUrlEnum;
import com.sysman.beanbase.BeanBaseModal;
import com.sysman.controladores.SessionUtil;
import com.sysman.dao.Registro;
import com.sysman.enums.GeneralCodigoFormaEnum;
import com.sysman.enums.GeneralParameterEnum;
import com.sysman.exc.kernel.api.commons.util.exceptions.SysmanException;
import com.sysman.exception.SystemException;
import com.sysman.jsfutil.CacheUtil;
import com.sysman.jsfutil.JsfUtil;
import com.sysman.jsfutil.RegistroConverter;
import com.sysman.kernel.api.clientwso2.beans.Parameter;
import com.sysman.kernel.api.clientwso2.beans.UrlBean;
import com.sysman.kernel.api.clientwso2.connectors.UrlServiceUtil;
import com.sysman.recursos.ejb.EjbSysmanUtilRemote;
import com.sysman.services.RegistroDataModelImpl;
import com.sysman.util.SysmanFunciones;

import java.util.Date;
import java.util.List;
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
 * @author NGOMEZ
 * @version 1, 26/01/2016
 * 
 * @version 2, 09/08/2017
 * @author jreina se realizaron los cambios de refactoring en cada uno
 * de los combos.
 * 
 */
@ManagedBean
@ViewScoped
public class SeleccionarPlacasControlador extends BeanBaseModal {

    private final String compania;
    private final String user;
    private final String cValor;
    private final String cElementosIngresados;
    private final String cCodigo;
    private final String cDmovimiento;
    private String tipoMovp;
    private String movp;
    private String dependencia;
    private String responsable;
    private String sucursal;
    private String clase;
    private String concepto;
    private String tipoElemento;
    private Date fecha;
    private boolean cuadroVisible;
    private boolean activarAceptarSeleccion;
    private boolean activarSeleccionarTodo;
    private RegistroDataModelImpl listaListaDePlacas;

    @EJB
    private EjbSysmanUtilRemote ejbSysmanUtilRemote;

    @EJB
    private EjbAlmacenCincoRemote ejbAlmacenCinco;

    /**
     * Creates a new instance of SeleccionarPlacasControlador
     */
    public SeleccionarPlacasControlador() {
        super();
        numFormulario = GeneralCodigoFormaEnum.SELECCIONAR_PLACAS_CONTROLADOR
                        .getCodigo();
        compania = SessionUtil.getCompania();
        user = SessionUtil.getUser().getCodigo();
        cValor = "VALOR";
        cElementosIngresados = "TB_TB1879";
        cCodigo = "CODIGO";
        cDmovimiento = "D_MOVIMIENTO";
        activarAceptarSeleccion = false;
        activarSeleccionarTodo = false;

        try {
            validarPermisos();
            Map<String, Object> parametrosEntrada = SessionUtil.getFlash();
            if (parametrosEntrada != null) {
                tipoMovp = (String) parametrosEntrada.get("tipoMovp");
                movp = (String) parametrosEntrada.get("movp");
                dependencia = (String) parametrosEntrada.get("dependencia");
                responsable = (String) parametrosEntrada.get("responsable");
                sucursal = (String) parametrosEntrada.get("sucursal");
                fecha = (Date) parametrosEntrada.get("fecha");
            }
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            SessionUtil.redireccionarMenuPermisos();
        }
        finally {
            SessionUtil.cleanFlash();
        }
    }

    @PostConstruct
    public void inicializar() {
        abrirFormulario();
        cargarListaListaDePlacas();
    }

    public void cargarModal() {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void cargarListaListaDePlacas() {
        UrlBean urlBean = UrlServiceUtil.getInstance()
                        .getUrlServiceByUrlByEnumID(
                                        SeleccionarPlacasControladorUrlEnum.URL3677
                                                        .getValue()); 
        Map<String, Object> param = new TreeMap<>();
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        param.put(GeneralParameterEnum.DEPENDENCIA.getName(), dependencia);
        param.put(GeneralParameterEnum.RESPONSABLE.getName(), responsable);
        param.put(GeneralParameterEnum.SUCURSAL.getName(), sucursal);
        param.put(SeleccionarPlacasControladorEnum.PARAM3.getValue(),
                        tipoElemento);
        param.put(GeneralParameterEnum.CLASE.getName(), clase);

        try {
            listaListaDePlacas = new RegistroDataModelImpl(urlBean.getUrl(),
                            urlBean.getUrlConteo().getUrl(), param, false,
                            CacheUtil.getLlaveServicio(urlConexionCache,
                                            "DEVOLUTIVO"),
                            true);
        }
        catch (SysmanException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void seleccionarFilaListaDePlacas(SelectEvent event) {
        // <CODIGO_DESARROLLADO>
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirAceptarSeleccion() {
        // <CODIGO_DESARROLLADO>
        copiarDatos(listaListaDePlacas.getSeleccionados(), false);
        activarAceptarSeleccion = true;
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirSeleccionarTodo() {
        // <CODIGO_DESARROLLADO>
        cuadroVisible = true;
        activarSeleccionarTodo = true;
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirBorrarSeleccion() {
        // <CODIGO_DESARROLLADO>
        listaListaDePlacas.getSeleccionados().clear();
        listaListaDePlacas.getLlavesSeleccionadas().clear();
        // </CODIGO_DESARROLLADO>
    }

    @Override
    public void abrirFormulario() {
        // <CODIGO_DESARROLLADO>
        Map<String, Object> param = new TreeMap<>();
        param.put(SeleccionarPlacasControladorEnum.PARAM0.getValue(), tipoMovp);
        param.put(GeneralParameterEnum.COMPANIA.getName(), compania);
        try {
            Registro reg = RegistroConverter.toRegistro(
                            requestManager.get(UrlServiceUtil.getInstance()
                                            .getUrlServiceByUrlByEnumID(
                                                            SeleccionarPlacasControladorUrlEnum.URL3678
                                                                            .getValue())
                                            .getUrl(), param));
            clase = reg.getCampos().get("CLASE").toString();
            tipoElemento = reg.getCampos().get("TIPOELEMENTO").toString();
            concepto = reg.getCampos().get("CONCEPTO").toString();
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
        // </CODIGO_DESARROLLADO>
    }

    public void copiarDatos(List<Registro> lista, boolean select) {
        if (listaListaDePlacas.getPageSize() == 0) {
            JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1878"));
            return;
        }
        try {
            if (select) {

                ejbAlmacenCinco.copiarPlacasLote(compania, tipoMovp,
                                Long.parseLong(movp),
                                dependencia, responsable, sucursal, clase,
                                fecha, user);

                JsfUtil.agregarMensajeInformativo(
                                idioma.getString(cElementosIngresados));

            }
            else {
                if (!lista.isEmpty()) {
                    copiarDatos(lista);
                    JsfUtil.agregarMensajeInformativo(
                                    idioma.getString(cElementosIngresados));
                }
                else {
                    JsfUtil.agregarMensajeAlerta(idioma.getString("TB_TB1880"));
                }
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }

    }

    private void copiarDatos(List<Registro> lista) {
        try {
            for (Registro lista1 : lista) {
                String codigo;
                String criterio = SysmanFunciones.concatenar("COMPANIA = ''",
                                compania, "'' AND TIPOMOVIMIENTO = ''",
                                tipoMovp,
                                "'' AND MOVIMIENTO = ", movp);
                codigo = String.valueOf(ejbSysmanUtilRemote
                                .generarSiguienteConsecutivo(cDmovimiento,
                                                criterio, cCodigo));
                if ("D".equals(clase)) {
                    Map<String, Object> param = new TreeMap<>();
                    param.put(GeneralParameterEnum.COMPANIA.getName(),
                                    compania);
                    param.put(SeleccionarPlacasControladorEnum.PARAM0
                                    .getValue(), tipoMovp);
                    param.put(SeleccionarPlacasControladorEnum.PARAM1
                                    .getValue(), movp);
                    param.put(GeneralParameterEnum.ELEMENTO.getName(),
                                    lista1.getCampos().get("ELEMENTO"));
                    param.put(GeneralParameterEnum.SERIE.getName(),
                                    lista1.getCampos().get("SERIE"));
                    param.put(GeneralParameterEnum.IND_REG.getName(),
                                    0);
                    param.put(GeneralParameterEnum.VALORUNITARIO.getName(),
                                    lista1.getCampos().get(cValor));
                    param.put(GeneralParameterEnum.VALORTOTAL.getName(),
                                    lista1.getCampos().get(cValor));
                    param.put(GeneralParameterEnum.CODIGO.getName(),
                                    codigo);
                    param.put(SeleccionarPlacasControladorEnum.PARAM5
                                    .getValue(),
                                    1);
                    param.put(SeleccionarPlacasControladorEnum.PARAM6
                                    .getValue(),
                                    0);
                    param.put(GeneralParameterEnum.ESPECIFICACION.getName(),
                                    lista1.getCampos().get("DESCRIPCION"));
                    param.put(GeneralParameterEnum.MARCA.getName(),
                                    lista1.getCampos().get("MARCA"));
                    param.put("MODELO",
                            lista1.getCampos().get("MODELO"));
                    param.put("SERIEDEVOLUTIVO",
                            lista1.getCampos().get("SERIEDEVOLUTIVO"));
                    param.put(GeneralParameterEnum.CREATED_BY.getName(),
                                    user);
                    param.put(GeneralParameterEnum.DATE_CREATED.getName(),
                                    new Date());
                    param.put(SeleccionarPlacasControladorEnum.VLRUNITARIO_ANTESIVA
                                    .getValue(),
                                    lista1.getCampos().get(cValor));
                    param.put(GeneralParameterEnum.FECHA.getName(), fecha);

                    UrlBean urlCreate = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    SeleccionarPlacasControladorUrlEnum.URL2649
                                                                    .getValue());
                    requestManager.save(urlCreate.getUrl(),
                                    urlCreate.getMetodo(), param);
                }
                else {
                    Map<String, Object> param = new TreeMap<>();
                    param.put(GeneralParameterEnum.COMPANIA.getName(),
                                    compania);
                    param.put(SeleccionarPlacasControladorEnum.PARAM0
                                    .getValue(), tipoMovp);
                    param.put(SeleccionarPlacasControladorEnum.PARAM1
                                    .getValue(), movp);
                    param.put(GeneralParameterEnum.CODIGO.getName(),
                                    codigo);
                    param.put(GeneralParameterEnum.ELEMENTO.getName(),
                                    lista1.getCampos().get("ELEMENTO"));
                    param.put(GeneralParameterEnum.SERIE.getName(),
                                    lista1.getCampos().get("SERIE"));
                    param.put(GeneralParameterEnum.ESPECIFICACION.getName(),
                                    lista1.getCampos().get("DESCRIPCION"));
                    param.put(SeleccionarPlacasControladorEnum.PARAM5
                                    .getValue(),
                                    1);
                    param.put(GeneralParameterEnum.VALORUNITARIO.getName(),
                                    lista1.getCampos().get(cValor));
                    param.put(GeneralParameterEnum.VALORTOTAL.getName(),
                                    lista1.getCampos().get(cValor));
                    param.put(GeneralParameterEnum.AUXILIAR.getName(),
                                    "9999999999999999");
                    param.put(SeleccionarPlacasControladorEnum.PARAM7
                                    .getValue(),
                                    lista1.getCampos().get(cValor));
                    param.put(GeneralParameterEnum.TERCERO.getName(),
                                    responsable);
                    param.put(GeneralParameterEnum.SUCURSAL.getName(),
                                    sucursal);
                    param.put(GeneralParameterEnum.ORDENDESUMINISTRO.getName(),
                                    0);
                    param.put(SeleccionarPlacasControladorEnum.PARAM8
                                    .getValue(),
                                    0);
                    param.put(SeleccionarPlacasControladorEnum.PARAM9
                                    .getValue(),
                                    3);
                    param.put(GeneralParameterEnum.VALORIVA.getName(),
                                    0);
                    param.put(GeneralParameterEnum.PORCIVA.getName(),
                                    0);
                    param.put(GeneralParameterEnum.CREATED_BY.getName(),
                                    user);
                    param.put(GeneralParameterEnum.DATE_CREATED.getName(),
                                    new Date());

                    param.put(SeleccionarPlacasControladorEnum.VLRUNITARIO_ANTESIVA
                                    .getValue(),
                                    lista1.getCampos().get(cValor));
                    param.put(GeneralParameterEnum.MARCA.getName(),
                            lista1.getCampos().get("MARCA"));
		            param.put("MODELO",
		                    lista1.getCampos().get("MODELO"));
		            param.put("SERIEDEVOLUTIVO",
		                    lista1.getCampos().get("SERIEDEVOLUTIVO"));

                    param.put(GeneralParameterEnum.FECHA.getName(), fecha);

                    
                    Map<String, Object> fields = new TreeMap<>();
                    if ((clase.equals("T") && concepto.equals("DS")) ||
                                    (clase.equals("D") && concepto.equals("DT"))) {

                        param.put(GeneralParameterEnum.ESTADO.getName(),
                                        "M");
                        fields.put(GeneralParameterEnum.ESTADO.getName(),
        						"M");
                    }
                    else {
                        param.put(GeneralParameterEnum.ESTADO.getName(),
                                        "B");
                        fields.put(GeneralParameterEnum.ESTADO.getName(),
        						"B");
                    }
                    
                	UrlBean updateEstado = UrlServiceUtil.getInstance()
    						.getUrlServiceByUrlByEnumID(
    								MovimientosControladorUrlEnum.URL1411
    								.getValue());
    				
    				fields.put(GeneralParameterEnum.COMPANIA.getName(),
    						compania);
    				fields.put(GeneralParameterEnum.ELEMENTO.getName(),
    						 lista1.getCampos().get("ELEMENTO"));
    				
    				fields.put(GeneralParameterEnum.SERIE.getName(),
    						lista1.getCampos().get("SERIE"));
    				fields.put(GeneralParameterEnum.MODIFIED_BY.getName(),
    						SessionUtil.getUser().getCodigo());
    				fields.put(GeneralParameterEnum.DATE_MODIFIED.getName(),
    						new Date());
    				Parameter parameter = new Parameter();
    				parameter.setFields(fields);
    				
    				requestManager.update(updateEstado.getUrl(),
    						updateEstado.getMetodo(),
    						parameter);

                    UrlBean urlCreate = UrlServiceUtil.getInstance()
                                    .getUrlServiceByUrlByEnumID(
                                                    SeleccionarPlacasControladorUrlEnum.URL4896
                                                                    .getValue());

                    requestManager.save(urlCreate.getUrl(),
                                    urlCreate.getMetodo(), param);
                }
            }
        }
        catch (SystemException e) {
            logger.error(e.getMessage(), e);
            JsfUtil.agregarMensajeError(e.getMessage());
        }
    }

    public void aceptarCuadroConfirmacion() {
        // <CODIGO_DESARROLLADO>
        copiarDatos(null, true);
        cuadroVisible = false;
        // </CODIGO_DESARROLLADO>
    }

    public void oprimirCerrar() {
        // <CODIGO_DESARROLLADO>
        RequestContext.getCurrentInstance().closeDialog(null);
        // </CODIGO_DESARROLLADO>
    }

    public RegistroDataModelImpl getListaListaDePlacas() {
        return listaListaDePlacas;
    }

    public void setListaListaDePlacas(
        RegistroDataModelImpl listaListaDePlacas) {
        this.listaListaDePlacas = listaListaDePlacas;
    }

    public String getTipoMovp() {
        return tipoMovp;
    }

    public void setTipoMovp(String tipoMovp) {
        this.tipoMovp = tipoMovp;
    }

    public String getMovp() {
        return movp;
    }

    public void setMovp(String movp) {
        this.movp = movp;
    }

    public String getDependencia() {
        return dependencia;
    }

    public void setDependencia(String dependencia) {
        this.dependencia = dependencia;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public String getClase() {
        return clase;
    }

    public void setClase(String clase) {
        this.clase = clase;
    }

    public String getTipoElemento() {
        return tipoElemento;
    }

    public void setTipoElemento(String tipoElemento) {
        this.tipoElemento = tipoElemento;
    }

    public boolean isCuadroVisible() {
        return cuadroVisible;
    }

    public void setCuadroVisible(boolean cuadroVisible) {
        this.cuadroVisible = cuadroVisible;
    }

    public boolean isActivarAceptarSeleccion() {
        return activarAceptarSeleccion;
    }

    public void setActivarAceptarSeleccion(boolean activarAceptarSeleccion) {
        this.activarAceptarSeleccion = activarAceptarSeleccion;
    }

    public boolean isActivarSeleccionarTodo() {
        return activarSeleccionarTodo;
    }

    public void setActivarSeleccionarTodo(boolean activarSeleccionarTodo) {
        this.activarSeleccionarTodo = activarSeleccionarTodo;
    }

}

/*
 * To change this license header, choose License Headers in Project
 * Properties. To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sysman.services;

import com.sysman.dao.Registro;
import com.sysman.persistencia.ConectorPool;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;

import org.primefaces.component.api.UIColumn;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author cmanrique
 */
public class RegistroDataModel extends LazyDataModel<Registro> {

    private List<Registro> datasource;
    private String origen;

    private String conexion;
    private String tabla;
    private boolean vacio;
    private String rowKey;
    private int actual;

    private Map<String, Object> filters;

    private boolean multiple;
    private List<Registro> seleccionados;
    private List<Registro> filtradoMultiple;
    private List<Registro> seleccionadosAux;

    private List<String> llavesSeleccionadas;
    private ConectorPool con;
    private String[] nombreLlave;

    public RegistroDataModel(String conexion, String tabla, String origen) {
        this.conexion = conexion;
        this.tabla = tabla;
        this.origen = origen;
        datasource = new ArrayList<>();
        vacio = false;
        con = new ConectorPool();
    }

    public RegistroDataModel(String conexion, String tabla, String origen,
        String[] nombreLlave) {
        this.conexion = conexion;
        this.tabla = tabla;
        this.origen = origen;
        this.nombreLlave = nombreLlave;
        datasource = new ArrayList<>();
        vacio = false;
        con = new ConectorPool();
    }

    public RegistroDataModel(String conexion, String tabla, String origen,
        boolean vacio, String rowKey) {
        this.conexion = conexion;
        this.tabla = tabla;
        this.origen = origen;
        this.vacio = vacio;
        this.rowKey = rowKey;
        datasource = new ArrayList<>();
        con = new ConectorPool();
    }

    public RegistroDataModel(String conexion, String tabla, String origen,
        boolean vacio, String[] nombreLlave) {
        this.conexion = conexion;
        this.tabla = tabla;
        this.origen = origen;
        this.vacio = vacio;
        this.nombreLlave = nombreLlave;
        datasource = new ArrayList<>();
        con = new ConectorPool();
    }

    public RegistroDataModel(String conexion, String tabla, String origen,
        boolean vacio, String rowKey,
        boolean multiple) {
        this.conexion = conexion;
        this.tabla = tabla;
        this.origen = origen;
        this.vacio = vacio;
        this.rowKey = rowKey;
        this.multiple = multiple;
        datasource = new ArrayList<>();
        seleccionados = new ArrayList<>();
        llavesSeleccionadas = new ArrayList<>();
        con = new ConectorPool();

    }

    public RegistroDataModel(String conexion, String tabla, String origen,
        boolean vacio, String[] nombreLlave,
        boolean multiple) {
        this.conexion = conexion;
        this.tabla = tabla;
        this.origen = origen;
        this.vacio = vacio;
        this.nombreLlave = nombreLlave;
        this.multiple = multiple;
        datasource = new ArrayList<>();
        seleccionados = new ArrayList<>();
        llavesSeleccionadas = new ArrayList<>();
        con = new ConectorPool();

    }

    @Override
    public List<Registro> load(int first, int pageSize, String sortField,
        SortOrder sortOrder,
        Map<String, Object> filters) {

        if (origen != null && !origen.isEmpty()) {
            actual = first;

            Statement st = null;

            int inicio = !vacio || first == 0 ? first : (first / 11) * 10;
            int tamPagina = !vacio ? pageSize : 10;
            this.filters = filters;

            datasource.clear();
            try {
                String sql = origen;

                String filtro = "";

                if (filters != null && filters.size() > 0) {
                    Map<String, FilterMatchMode> constraints = getFiltros();
                    filtro = " WHERE ";
                    int aux = 0;
                    for (Iterator<String> it = filters.keySet().iterator(); it
                                    .hasNext();) {
                        try {
                            String filterProperty = it.next();
                            Object filterValue = filters.get(filterProperty);
                            FilterMatchMode modo = constraints
                                            .get(filterProperty);
                            filterProperty = filterProperty.substring(
                                            filterProperty.indexOf("['") + 2,
                                            filterProperty.indexOf("']"));
                            filtro = aux == 0 ? filtro : filtro + " AND ";
                            switch (modo) {
                            case STARTS_WITH:
                                filtro += "UPPER(" + filterProperty + ") like '"
                                    + filterValue.toString().toUpperCase()
                                    + "%'";
                                break;
                            case ENDS_WITH:
                                filtro += "UPPER(" + filterProperty
                                    + ") like '%"
                                    + filterValue.toString().toUpperCase()
                                    + "'";
                                break;
                            case CONTAINS:
                                filtro += "UPPER(" + filterProperty
                                    + ") like '%"
                                    + filterValue.toString().toUpperCase()
                                    + "%'";
                                break;
                            case EXACT:
                                filterValue = filterValue.toString()
                                                .replace("true", "-1");
                                filterValue = filterValue.toString()
                                                .replace("false", "0");
                                filtro += filterProperty + " like '"
                                    + filterValue + "'";
                                break;
                            }

                        }
                        catch (Exception e) {

                        }
                        finally {
                            aux++;
                        }

                    }
                }

                con.conectar(conexion);
                st = con.getConection().createStatement(
                                ResultSet.TYPE_SCROLL_INSENSITIVE,
                                ResultSet.CONCUR_READ_ONLY);
                st.setFetchSize(pageSize);
                sql = "SELECT * FROM  (" + sql + ")" + filtro;
                String sqlComp = "select * from ( select a.*, rownum rnum from (\n"
                    + sql + " \n"
                    + " ) a where rownum <= " + (inicio + tamPagina)
                    + " ) where rnum >" + inicio;
                System.out.println(sqlComp);

                try (ResultSet rs = st.executeQuery(sqlComp)) {

                    int indice = first;
                    int col = rs.getMetaData().getColumnCount();
                    HashMap<String, Object> aux;
                    Registro regTemp;
                    String valor;
                    if (rs.first()) {
                        if (vacio) {
                            aux = new HashMap<>();
                            for (int i = 1; i <= col; i++) {
                                aux.put(rs.getMetaData().getColumnName(i), "");
                            }
                            datasource.add(new Registro(indice, aux));
                        }
                        do {
                            aux = new HashMap<>();
                            for (int i = 1; i <= col; i++) {

                                if (rs.getMetaData().getColumnTypeName(i)
                                                .equals("DATE")) {
                                    aux.put(rs.getMetaData().getColumnName(i),
                                                    rs.getDate(i));
                                }
                                else if (rs.getMetaData().getColumnTypeName(i)
                                                .startsWith("TIMESTAMP")) {
                                    aux.put(rs.getMetaData().getColumnName(i),
                                                    rs.getTimestamp(i));
                                }
                                else {
                                    valor = rs.getString(i);
                                    if (rs.getMetaData().getColumnTypeName(i)
                                                    .equals("NUMBER")
                                        && valor != null
                                        && valor.length() > 15) {
                                        aux.put(rs.getMetaData()
                                                        .getColumnName(i),
                                                        new BigDecimal(valor));
                                    }
                                    else if (rs.getMetaData()
                                                    .getColumnTypeName(i)
                                                    .equals("NUMBER")
                                        && valor != null
                                        && valor.startsWith(".")) {
                                        aux.put(rs.getMetaData()
                                                        .getColumnName(i),
                                                        String.valueOf(Double
                                                                        .parseDouble(valor)));
                                    }
                                    else if (rs.getMetaData()
                                                    .getColumnTypeName(i)
                                                    .equals("NUMBER")
                                        && rs.getMetaData().getPrecision(i) == 1
                                        && rs.getMetaData().getScale(i) == 0) {
                                        aux.put(rs.getMetaData()
                                                        .getColumnName(i),
                                                        !"0".equals(valor));
                                    }
                                    else if (rs.getMetaData()
                                                    .getColumnTypeName(i)
                                                    .equals("NUMBER")
                                        && rs.getMetaData().getPrecision(i) == 4
                                        && rs.getMetaData().getScale(i) == 0) {
                                        aux.put(rs.getMetaData()
                                                        .getColumnName(i),
                                                        valor);
                                    }
                                    else if (rs.getMetaData()
                                                    .getColumnTypeName(i)
                                                    .equals("NUMBER")) {
                                        aux.put(rs.getMetaData()
                                                        .getColumnName(i),
                                                        rs.getObject(i));
                                    }
                                    else {
                                        aux.put(rs.getMetaData()
                                                        .getColumnName(i),
                                                        valor);
                                    }
                                }

                            }

                            regTemp = new Registro(indice, aux);
                            regTemp.asignarLlaveOLD(nombreLlave);
                            if (multiple) {
                                seleccionarParteGrafica(aux, regTemp);
                            }
                            datasource.add(regTemp);
                            indice++;
                        }
                        while (rs.next());

                        sql = "select count(8) TOTAL FROM (" + sql + ")";
                        try (ResultSet rst = st.executeQuery(sql)) {
                            rst.first();
                            int total = rst.getInt("TOTAL");
                            total = vacio
                                ? ((total / 10) + (total % 10 == 0 ? 0 : 1))
                                    * 11
                                : total;
                            this.setRowCount(total);
                        }

                    }
                    else {
                        this.setRowCount(0);
                    }
                }
            }
            catch (SQLException | NamingException ex) {
                Logger.getLogger(RegistroDataModel.class.getName())
                                .log(Level.SEVERE, null, ex);
            }
            finally {
                try {
                    if (con != null) {
                        con.getConection().close();
                    }
                    if (st != null) {
                        st.close();
                    }
                }
                catch (SQLException ex) {
                    Logger.getLogger(RegistroDataModel.class.getName())
                                    .log(Level.SEVERE, null, ex);
                }
            }
        }
        else {
            datasource.clear();
        }

        return datasource;
    }

    public void load(int first) {
        if (origen != null && !origen.isEmpty()) {
            actual = first;
            Statement st = null;
            datasource.clear();
            int inicio = !vacio || first == 0 ? first
                : (first / 11) * getPageSize();
            int tamPagina = !vacio ? this.getPageSize() : 10;
            try {
                String sql = origen;

                String filtro = "";

                if (filters != null && filters.size() > 0) {
                    Map<String, FilterMatchMode> constraints = getFiltros();
                    filtro = " WHERE ";
                    int aux = 0;
                    for (Iterator<String> it = filters.keySet().iterator(); it
                                    .hasNext();) {
                        try {
                            String filterProperty = it.next();
                            Object filterValue = filters.get(filterProperty);
                            FilterMatchMode modo = constraints
                                            .get(filterProperty);
                            filterProperty = filterProperty.substring(
                                            filterProperty.indexOf("['") + 2,
                                            filterProperty.indexOf("']"));
                            filtro = aux == 0 ? filtro : filtro + " AND ";
                            switch (modo) {
                            case STARTS_WITH:
                                filtro += "UPPER(" + filterProperty + ") like '"
                                    + filterValue.toString().toUpperCase()
                                    + "%'";
                                break;
                            case ENDS_WITH:
                                filtro += "UPPER(" + filterProperty
                                    + ") like '%"
                                    + filterValue.toString().toUpperCase()
                                    + "'";
                                break;
                            case CONTAINS:
                                filtro += "UPPER(" + filterProperty
                                    + ") like '%"
                                    + filterValue.toString().toUpperCase()
                                    + "%'";
                                break;
                            case EXACT:
                                filterValue = filterValue.toString()
                                                .replace("true", "-1");
                                filterValue = filterValue.toString()
                                                .replace("false", "0");
                                filtro += filterProperty + " like '"
                                    + filterValue + "'";
                                break;
                            }

                        }
                        catch (Exception e) {

                        }
                        finally {
                            aux++;
                        }

                    }
                }

                con.conectar(conexion);
                st = con.getConection().createStatement(
                                ResultSet.TYPE_SCROLL_INSENSITIVE,
                                ResultSet.CONCUR_READ_ONLY);
                st.setFetchSize(getPageSize());
                sql = "SELECT * FROM (" + sql + ")" + filtro;
                String sqlComp = "select * from ( select a.*, rownum rnum from (\n"
                    + sql + "                     \n"
                    + "        ) a where rownum <= " + (inicio + tamPagina)
                    + " ) where rnum >" + inicio;
                System.out.println(sqlComp);
                try (ResultSet rs = st.executeQuery(sqlComp)) {

                    int indice = first;
                    int col = rs.getMetaData().getColumnCount();
                    HashMap<String, Object> aux;
                    Registro regTemp;
                    if (rs.first()) {
                        if (vacio) {
                            aux = new HashMap<>();
                            for (int i = 1; i <= col; i++) {
                                aux.put(rs.getMetaData().getColumnName(i), "");
                            }
                            datasource.add(new Registro(indice, aux));
                        }
                        do {
                            aux = new HashMap<>();
                            String valor;
                            for (int i = 1; i <= col; i++) {
                                if (rs.getMetaData().getColumnTypeName(i)
                                                .equals("DATE")) {
                                    aux.put(rs.getMetaData().getColumnName(i),
                                                    rs.getDate(i));
                                }
                                else if (rs.getMetaData().getColumnTypeName(i)
                                                .startsWith("TIMESTAMP")) {
                                    aux.put(rs.getMetaData().getColumnName(i),
                                                    rs.getTimestamp(i));
                                }
                                else {
                                    valor = rs.getString(i);
                                    if (rs.getMetaData().getColumnTypeName(i)
                                                    .equals("NUMBER")
                                        && valor != null
                                        && valor.length() > 15) {
                                        aux.put(rs.getMetaData()
                                                        .getColumnName(i),
                                                        new BigDecimal(valor));
                                    }
                                    else if (rs.getMetaData()
                                                    .getColumnTypeName(i)
                                                    .equals("NUMBER")
                                        && valor != null
                                        && valor.startsWith(".")) {
                                        aux.put(rs.getMetaData()
                                                        .getColumnName(i),
                                                        String.valueOf(Double
                                                                        .parseDouble(valor)));
                                    }
                                    else if (rs.getMetaData()
                                                    .getColumnTypeName(i)
                                                    .equals("NUMBER")
                                        && rs.getMetaData().getPrecision(i) == 1
                                        && rs.getMetaData().getScale(i) == 0) {
                                        aux.put(rs.getMetaData()
                                                        .getColumnName(i),
                                                        !valor.equals("0"));
                                    }
                                    else if (rs.getMetaData()
                                                    .getColumnTypeName(i)
                                                    .equals("NUMBER")) {
                                        aux.put(rs.getMetaData()
                                                        .getColumnName(i),
                                                        rs.getObject(i));
                                    }
                                    else {

                                        aux.put(rs.getMetaData()
                                                        .getColumnName(i),
                                                        valor);
                                    }
                                }

                            }
                            regTemp = new Registro(indice, aux);
                            regTemp.asignarLlaveOLD(nombreLlave);
                            if (multiple) {
                                seleccionarParteGrafica(aux, regTemp);
                            }
                            datasource.add(regTemp);
                            indice++;
                        }
                        while (rs.next());

                        sql = "select count(8) TOTAL FROM (" + sql + ")";
                        try (ResultSet rst = st.executeQuery(sql)) {
                            rst.first();
                            int total = rst.getInt("TOTAL");
                            total = vacio
                                ? ((total / 10) + (total % 10 == 0 ? 0 : 1))
                                    * 11
                                : total;
                            this.setRowCount(total);
                        }
                    }
                    else {
                        this.setRowCount(0);
                    }
                }
            }
            catch (SQLException | NamingException ex) {
                Logger.getLogger(RegistroDataModel.class.getName())
                                .log(Level.SEVERE, null, ex);
            }
            finally {
                try {
                    if (con != null) {
                        con.getConection().close();
                    }
                    if (st != null) {
                        st.close();
                    }

                }
                catch (SQLException ex) {
                    Logger.getLogger(RegistroDataModel.class.getName())
                                    .log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void seleccionarParteGrafica(Map<String, Object> aux,
        Registro regTemp) {
        if (nombreLlave != null) {
            aux.put("SELECCIONADO_EN_PARTE_GRAFICA",
                            llavesSeleccionadas.contains(
                                            regTemp.getLlave()
                                                            .toString()));
        }
        else if (rowKey != null) {
            aux.put("SELECCIONADO_EN_PARTE_GRAFICA",
                            llavesSeleccionadas.contains(aux
                                            .get(this.rowKey)
                                            .toString()));
        }

    }

    @Override
    public Registro getRowData(String rowKey) {
        if (nombreLlave != null) {
            return buscarPorLlave(rowKey);
        }
        else if (rowKey != null) {
            return buscarPorColumna(rowKey);
        }
        return null;
    }

    private Registro buscarPorLlave(String rowKey) {
        for (Registro reg : datasource) {
            if (reg.getLlave().toString().equals(rowKey)) {

                if (multiple && this.rowKey != null
                    && !llavesSeleccionadas.contains(
                                    reg.getCampos().get(this.rowKey)
                                                    .toString())) {
                    seleccionados.add(reg);
                    llavesSeleccionadas.add(reg.getCampos().get(this.rowKey)
                                    .toString());
                    reg.getCampos().put("SELECCIONADO_EN_PARTE_GRAFICA", true);
                }
                else if (multiple && this.nombreLlave != null
                    && !llavesSeleccionadas.contains(
                                    reg.getLlave().toString())) {
                    seleccionados.add(reg);
                    llavesSeleccionadas.add(reg.getLlave()
                                    .toString());
                    reg.getCampos().put("SELECCIONADO_EN_PARTE_GRAFICA", true);
                }

                return reg;

            }
        }
        return null;
    }

    private Registro buscarPorColumna(String rowKey) {
        for (Registro reg : datasource) {
            if (reg.getCampos().get(this.rowKey).toString().equals(rowKey)) {
                if (multiple && !llavesSeleccionadas.contains(
                                reg.getCampos().get(this.rowKey).toString())) {
                    seleccionados.add(reg);
                    llavesSeleccionadas.add(reg.getCampos().get(this.rowKey)
                                    .toString());
                    reg.getCampos().put("SELECCIONADO_EN_PARTE_GRAFICA", true);
                }
                return reg;
            }
        }
        return null;
    }

    @Override
    public Object getRowKey(Registro object) {
        if (nombreLlave != null) {
            return object.getLlave();
        }
        else if (rowKey != null) {
            return object.getCampos().get(this.rowKey);
        }
        return null;
    }

    public void load() {
        load(actual);
    }

    public void deselecionar(Registro reg) {
        reg.getCampos().put("SELECCIONADO_EN_PARTE_GRAFICA", false);
        llavesSeleccionadas.remove(reg.getLlave().toString());
        this.seleccionados.remove(reg);
    }

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    public List<Registro> getSeleccionados() {
        return seleccionados;
    }

    public void setSeleccionados(List<Registro> seleccionados) {
        this.seleccionados = seleccionados;
    }

    public List<Registro> getFiltradoMultiple() {
        return filtradoMultiple;
    }

    public void setFiltradoMultiple(List<Registro> filtradoMultiple) {
        this.filtradoMultiple = filtradoMultiple;
    }

    public Map<String, FilterMatchMode> getFiltros() {
        FacesContext context = FacesContext.getCurrentInstance();
        DataTable table = (DataTable) context.getViewRoot()
                        .findComponent(tabla);
        Map<String, FilterMatchMode> constraints = new HashMap<>(
                        table.getColumns().size());

        for (UIColumn column : table.getColumns()) {
            ValueExpression filterExpression = column
                            .getValueExpression("filterBy");
            if (null != filterExpression) {
                String filterExpressionString = filterExpression
                                .getExpressionString();
                // evaluating filtered field id
                String filteredField = filterExpressionString.substring(
                                filterExpressionString.indexOf('.') + 1,
                                filterExpressionString.indexOf('}'));

                FilterMatchMode matchMode = FilterMatchMode
                                .fromUiParam(column.getFilterMatchMode());

                constraints.put(filteredField, matchMode);
            }
        }
        return constraints;
    }

    public Object get(int rowNum) {
        throw new UnsupportedOperationException("Not supported yet."); // To
        // change
        // body
        // of
        // generated
        // methods,
        // choose
        // Tools
        // |
        // Templates.
    }

    public enum FilterMatchMode {

        STARTS_WITH("startsWith"), ENDS_WITH("endsWith"), CONTAINS(
                        "contains"), EXACT("exact");

        /**
         * Value of p:column's filterMatchMode attribute which
         * corresponds to this math mode
         */
        private final String uiParam;

        FilterMatchMode(String uiParam) {
            this.uiParam = uiParam;
        }

        /**
         * @param uiParam
         * value of p:column's filterMatchMode attribute
         * @return MatchMode which corresponds to given UI parameter
         * @throws IllegalArgumentException
         * if no MatchMode is corresponding to given UI parameter
         */
        public static FilterMatchMode fromUiParam(String uiParam) {
            for (FilterMatchMode matchMode : values()) {
                if (matchMode.uiParam.equals(uiParam)) {
                    return matchMode;
                }
            }
            throw new IllegalArgumentException(
                            "No MatchMode found for " + uiParam);
        }

    }

    public List<Registro> getDatasource() {
        return datasource;
    }

    public void setDatasource(List<Registro> datasource) {
        this.datasource = datasource;
    }

    public Map<String, Object> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, Object> filters) {
        this.filters = filters;
    }

    public Registro getRegistroUnico(String condicion) {
        Statement st = null;
        String sql = "SELECT * FROM (" + origen + ") WHERE " + condicion;
        HashMap<String, Object> aux = null;
        Registro registro = null;
        try {
            con.conectar(conexion);
            st = con.getConection().createStatement();
            try (ResultSet rs = st.executeQuery(sql)) {
                int col = rs.getMetaData().getColumnCount();
                if (rs.next()) {
                    do {
                        aux = new HashMap<>();
                        String valor;
                        for (int i = 1; i <= col; i++) {
                            if (rs.getMetaData().getColumnTypeName(i)
                                            .equals("DATE")) {
                                aux.put(rs.getMetaData().getColumnName(i),
                                                rs.getDate(i));
                            }
                            else if (rs.getMetaData().getColumnTypeName(i)
                                            .startsWith("TIMESTAMP")) {
                                aux.put(rs.getMetaData().getColumnName(i),
                                                rs.getTimestamp(i));
                            }
                            else {
                                valor = rs.getString(i);
                                if (rs.getMetaData().getColumnTypeName(i)
                                                .equals("NUMBER")
                                    && valor != null
                                    && valor.length() > 15) {
                                    aux.put(rs.getMetaData().getColumnName(i),
                                                    new BigDecimal(valor));
                                }
                                else if (rs.getMetaData().getColumnTypeName(i)
                                                .equals("NUMBER")
                                    && valor != null
                                    && valor.startsWith(".")) {
                                    aux.put(rs.getMetaData().getColumnName(i),
                                                    String.valueOf(Double
                                                                    .parseDouble(valor)));
                                }
                                else if (rs.getMetaData().getColumnTypeName(i)
                                                .equals("NUMBER")
                                    && rs.getMetaData().getPrecision(i) == 1
                                    && rs.getMetaData().getScale(i) == 0) {
                                    aux.put(rs.getMetaData().getColumnName(i),
                                                    !"0".equals(valor));
                                }
                                else if (rs.getMetaData().getColumnTypeName(i)
                                                .equals("NUMBER")) {
                                    aux.put(rs.getMetaData().getColumnName(i),
                                                    rs.getObject(i));
                                }
                                else {
                                    aux.put(rs.getMetaData().getColumnName(i),
                                                    valor);
                                }
                            }
                        }
                    }
                    while (rs.next());
                    registro = new Registro(aux);
                }
            }
        }
        catch (NamingException | SQLException ex) {
            Logger.getLogger(RegistroDataModel.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        finally {
            try {
                if (con != null) {
                    con.getConection().close();
                }
                if (st != null) {
                    st.close();
                }

            }
            catch (SQLException ex) {
                Logger.getLogger(RegistroDataModel.class.getName())
                                .log(Level.SEVERE, null, ex);
            }
        }
        return registro;
    }

    public void setSeleccionados(String condicion) {
        llavesSeleccionadas.clear();
        seleccionados.clear();
        Statement st = null;
        String sql = "SELECT * FROM (" + origen + ") WHERE " + condicion;
        System.out.println(sql);
        HashMap<String, Object> aux = null;
        Registro registro = null;
        try {
            con.conectar(conexion);
            st = con.getConection().createStatement();
            try (ResultSet rs = st.executeQuery(sql)) {
                int col = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    do {
                        aux = new HashMap<>();
                        String valor;
                        for (int i = 1; i <= col; i++) {
                            if (rs.getMetaData().getColumnTypeName(i)
                                            .equals("DATE")) {
                                aux.put(rs.getMetaData().getColumnName(i),
                                                rs.getDate(i));
                            }
                            else {
                                valor = rs.getString(i);
                                if (rs.getMetaData().getColumnTypeName(i)
                                                .equals("NUMBER")
                                    && valor != null
                                    && valor.length() > 15) {
                                    aux.put(rs.getMetaData().getColumnName(i),
                                                    new BigDecimal(valor));
                                }
                                else if (rs.getMetaData().getColumnTypeName(i)
                                                .equals("NUMBER")
                                    && valor != null
                                    && valor.startsWith(".")) {
                                    aux.put(rs.getMetaData().getColumnName(i),
                                                    String.valueOf(Double
                                                                    .parseDouble(valor)));
                                }
                                else if (rs.getMetaData().getColumnTypeName(i)
                                                .equals("NUMBER")
                                    && rs.getMetaData().getPrecision(i) == 1
                                    && rs.getMetaData().getScale(i) == 0) {
                                    aux.put(rs.getMetaData().getColumnName(i),
                                                    !valor.equals("0"));
                                }
                                else if (rs.getMetaData().getColumnTypeName(i)
                                                .equals("NUMBER")) {
                                    aux.put(rs.getMetaData().getColumnName(i),
                                                    rs.getObject(i));
                                }
                                else {
                                    aux.put(rs.getMetaData().getColumnName(i),
                                                    valor);
                                }
                            }
                        }
                        registro = new Registro(aux);
                        registro.getCampos().put(
                                        "SELECCIONADO_EN_PARTE_GRAFICA",
                                        true);
                        llavesSeleccionadas.add(registro.getCampos()
                                        .get(this.rowKey).toString());
                        seleccionados.add(registro);
                    }
                    while (rs.next());

                }
            }
        }
        catch (NamingException | SQLException ex) {
            Logger.getLogger(RegistroDataModel.class.getName())
                            .log(Level.SEVERE, null, ex);
        }
        finally {
            try {
                if (con != null) {
                    con.getConection().close();
                }
                if (st != null) {
                    st.close();
                }

            }
            catch (SQLException ex) {
                Logger.getLogger(RegistroDataModel.class.getName())
                                .log(Level.SEVERE, null, ex);
            }
        }
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public List<String> getLlavesSeleccionadas() {
        return llavesSeleccionadas;
    }

    public void setLlavesSeleccionadas(List<String> llavesSeleccionadas) {
        this.llavesSeleccionadas = llavesSeleccionadas;
    }

    public List<Registro> getSeleccionadosAux() {
        return seleccionadosAux;
    }

    public void setSeleccionadosAux(List<Registro> seleccionadosAux) {
        this.seleccionadosAux = seleccionadosAux;
    }

    public String[] getNombreLlave() {
        return nombreLlave;
    }

    public void setNombreLlave(String[] nombreLlave) {
        this.nombreLlave = nombreLlave;
    }

}

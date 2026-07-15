package com.sysman.kernel.api.clientwso2.dbs;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

/**
 * Envoltorio "vivo" sobre DbsRegistry: carga los .dbs desde un
 * directorio externo del filesystem y, si se activa la vigilancia,
 * los recarga automaticamente (todos juntos, como un snapshot nuevo)
 * apenas detecta que algun .dbs se creo, modifico o borro.
 *
 * No hace falta reiniciar WildFly ni redesplegar la aplicacion:
 * basta con reemplazar el archivo .dbs en el directorio externo.
 */
public class DbsRegistryHolder implements DbsRegistryProvider {

    private static final Logger LOG = Logger.getLogger(DbsRegistryHolder.class.getName());

    /** Tiempo de calma sin nuevos eventos antes de recargar (evita recargar N veces
     *  cuando se copian varios .dbs juntos, por ejemplo con "cp *.dbs destino/"). */
    private static final long DEBOUNCE_MS = 500;

    private final Path directorio;
    private volatile DbsRegistry actual;

    private WatchService watchService;
    private Thread hiloVigilancia;
    private final Map<WatchKey, Path> clavesPorDirectorio = new ConcurrentHashMap<>();
    private volatile boolean activo = false;

    public DbsRegistryHolder(Path directorio) {
        this.directorio = directorio;
        this.actual = DbsRegistry.cargarDesdeDirectorio(directorio);
    }

    @Override
    public DbsQuery getQuery(String queryId) {
        return actual.getQuery(queryId);
    }

    @Override
    public DbsResource getResource(String codigo) {
        return actual.getResource(codigo);
    }

    /** Recarga inmediata y sincrona, por si se quiere exponer un boton "recargar ahora". */
    public void recargar() {
        this.actual = DbsRegistry.cargarDesdeDirectorio(directorio);
        LOG.info("DbsRegistry recargado desde " + directorio);
    }

    /**
     * Arranca un hilo daemon que vigila el directorio (y subdirectorios
     * creados despues) con WatchService y dispara recargar() cuando
     * detecta cambios relevantes (.dbs creado/modificado/borrado).
     */
    public synchronized void iniciarVigilancia() {
        if (activo) {
            return;
        }
        try {
            watchService = FileSystems.getDefault().newWatchService();
            registrarRecursivo(directorio);
        }
        catch (IOException e) {
            LOG.log(Level.SEVERE, "No se pudo iniciar la vigilancia de " + directorio, e);
            return;
        }

        activo = true;
        hiloVigilancia = new Thread(this::bucleVigilancia, "dbs-watch-" + directorio.getFileName());
        hiloVigilancia.setDaemon(true);
        hiloVigilancia.start();
        LOG.info("Vigilancia de cambios activa sobre " + directorio);
    }

    /** Detiene el hilo de vigilancia; llamar desde contextDestroyed. */
    public synchronized void detener() {
        activo = false;
        if (hiloVigilancia != null) {
            hiloVigilancia.interrupt();
        }
        try {
            if (watchService != null) {
                watchService.close();
            }
        }
        catch (IOException ignored) {
        }
    }

    private void registrarRecursivo(Path base) throws IOException {
        Files.walkFileTree(base, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                WatchKey key = dir.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
                clavesPorDirectorio.put(key, dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void bucleVigilancia() {
        long ultimoEventoRelevante = 0;
        while (activo) {
            WatchKey key;
            try {
                key = watchService.poll(DEBOUNCE_MS, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            catch (ClosedWatchServiceException e) {
                break;
            }

            boolean huboCambioRelevante = false;

            if (key != null) {
                Path dir = clavesPorDirectorio.get(key);
                for (WatchEvent<?> evento : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = evento.kind();
                    if (kind == OVERFLOW) {
                        huboCambioRelevante = true;
                        continue;
                    }

                    Path nombre = (Path) evento.context();
                    Path completo = dir == null ? null : dir.resolve(nombre);

                    // Si se creo un subdirectorio nuevo, hay que vigilarlo tambien.
                    if (completo != null && kind == ENTRY_CREATE && Files.isDirectory(completo)) {
                        try {
                            registrarRecursivo(completo);
                        }
                        catch (IOException ignored) {
                        }
                    }

                    if (completo == null || completo.toString().toLowerCase().endsWith(".dbs")) {
                        huboCambioRelevante = true;
                    }
                }

                boolean valido = key.reset();
                if (!valido) {
                    clavesPorDirectorio.remove(key);
                }
            }

            if (huboCambioRelevante) {
                ultimoEventoRelevante = System.currentTimeMillis();
            }

            // Recarga una sola vez, cuando paso el periodo de calma desde
            // el ultimo evento relevante.
            if (ultimoEventoRelevante != 0
                && System.currentTimeMillis() - ultimoEventoRelevante >= DEBOUNCE_MS) {
                try {
                    recargar();
                }
                catch (Exception e) {
                    LOG.log(Level.SEVERE, "Error recargando .dbs tras cambio detectado", e);
                }
                ultimoEventoRelevante = 0;
            }
        }
    }
}

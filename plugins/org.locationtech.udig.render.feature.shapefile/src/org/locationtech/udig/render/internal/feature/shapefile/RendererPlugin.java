/*
 *    uDig - User Friendly Desktop Internet GIS client
 *    http://udig.refractions.net
 *    (C) 2004, Refractions Research Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Refractions BSD
 * License v1.0 (http://udig.refractions.net/files/bsd3-v10.html).
 *
 */
package org.locationtech.udig.render.internal.feature.shapefile;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.geotools.renderer.lite.StreamingRenderer;
import org.osgi.framework.BundleContext;

/**
 * Plugin class for BasicGridCoverage
 * 
 * @author jones
 * @since 0.6.0
 */
public class RendererPlugin extends Plugin {

    private static RendererPlugin plugin;

    public static final String ID = "org.locationtech.udig.render.feature.shapefile"; //$NON-NLS-1$
    /**
     * Construct <code>RendererPlugin</code>.
     */
    public RendererPlugin() {
        super();
        plugin = this;

    }

    @Override
    public void start( BundleContext context ) throws Exception {
        super.start(context);
        ClassLoader current = Thread.currentThread().getContextClassLoader();
        try {
//            Thread.currentThread().setContextClassLoader(ShapefileRenderer.class.getClassLoader());
            Thread.currentThread().setContextClassLoader(StreamingRenderer.class.getClassLoader());
            Logger logger = Logger.getLogger("org.geotools.renderer.shape");//$NON-NLS-1$
            if (isDebugging("org.locationtech.udig.render.feature.shapefile/finest")) { //$NON-NLS-1$
                logger.setLevel(Level.FINEST);
                logger.addHandler(new Handler(){
                    @Override
                    public void publish( LogRecord record ) {
                        System.err.println(record.getMessage());
                    }
                    @Override
                    public void flush() {
                        System.err.flush();
                    }
                    @Override
                    public void close() throws SecurityException {
                    }
                });
            } else {
                logger.setLevel(Level.SEVERE);
            }
        } finally {
            Thread.currentThread().setContextClassLoader(current);
        }

    }

    public static Plugin getDefault() {
        return plugin;
    }
    /**
     * Writes an info log in the plugin's log.
     * <p>
     * This should be used for user level messages.
     * </p>
     * 
     * @param message Message to tell the user
     * @param e Throwable assocaited with this message
     */
    public static void log( String message, Throwable e ) {
        getDefault().getLog().log(new Status(IStatus.INFO, ID, 0, message, e));
    }
    /**
     * Messages that only engage if getDefault().isDebugging()
     * <p>
     * It is much prefered to do this:
     * 
     * <pre><code>
     * private static final String RENDERING = "org.locationtech.udig.project/render/trace";
     * if( ProjectUIPlugin.getDefault().isDebugging() && "true".equalsIgnoreCase( RENDERING ) ){
     *      System.out.println( "your message here" );
     * }
     * @param message Message to send to standard out
     * @param e Throwable associated with this trace
     */
    public static void trace( String message, Throwable e ) {
        if (getDefault().isDebugging()) {
            if (message != null)
                System.out.println(message);
            if (e != null)
                e.printStackTrace();
        }
    }
    /**
     * Performs the Platform.getDebugOption true check on the provided trace
     * <p>
     * Note: ProjectUIPlugin.getDefault().isDebugging() must also be on.
     * <ul>
     * <li>Trace.RENDER - trace rendering progress
     * </ul>
     * </p>
     * 
     * @param trace currently only RENDER is defined
     * @return true if -debug is used with a .options file to enable tracing
     */
    public static boolean isDebugging( final String trace ) {
        return getDefault().isDebugging()
                && "true".equalsIgnoreCase(Platform.getDebugOption(trace)); //$NON-NLS-1$    
    }
}

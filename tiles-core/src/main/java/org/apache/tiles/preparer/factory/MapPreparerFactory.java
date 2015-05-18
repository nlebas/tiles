package org.apache.tiles.preparer.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.tiles.preparer.ViewPreparer;
import org.apache.tiles.request.Request;

/**
 * Map-based implementation of the {@link PreparerFactory}.
 * This factory provides no contextual configuration.  It
 * simply stores the named preparerInstance and returns it.
 *
 * @since Tiles 2.0
 * @version $Rev$ $Date$
 */
public class MapPreparerFactory implements PreparerFactory {
    /**
     * Maps a preparer name to the instantiated preparer.
     */
    protected Map<String, ViewPreparer> preparers;

    /**
     * Constructor.
     */
    public MapPreparerFactory() {
        this.preparers = new ConcurrentHashMap<String, ViewPreparer>();
    }


    /**
     * Return the named preparerInstance. 
     *
     * @param name    the name of the preparerInstance
     * @param context current context
     * @return ViewPreparer instance
     */
    public ViewPreparer getPreparer(String name, Request context) {
        return preparers.get(name);
    }

    /**
     * Registers a new preparerInstance.
     * @param name    the name.
     * @param preparer the preparerInstance.
     */
    public void register(String name, ViewPreparer preparer) {
    	this.preparers.put(name, preparer);
    }
}

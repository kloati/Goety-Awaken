package com.k1sak1.goetyawaken.common.storage.network;

import java.util.Collection;

/**
 * Inspired by Refined Storage
 * 
 * @author raoulvdberge (Original Author)
 * @see <a href=
 *      "https://github.com/raoulvdberge/refinedstorage">Refined Storage
 *      Repository</a>
 */
public interface INetworkNodeGraph {

    Collection<INetworkNodeGraphEntry> all();

    void invalidate();

    void addListener(Runnable listener);

    void removeListener(Runnable listener);
}

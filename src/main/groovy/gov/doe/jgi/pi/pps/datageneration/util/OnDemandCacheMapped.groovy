package gov.doe.jgi.pi.pps.datageneration.util

/**
 * Created by dscott on 3/4/14.
 */
class OnDemandCacheMapped<K,V> {

    private final Map<K,OnDemandCache<V>> cacheMap = [:].withDefault{ new OnDemandCache<V>() }

    OnDemandCacheMapped() {}

    V fetch(K input, Closure fetchClosure) {
        return cacheMap[input].fetch {
            fetchClosure?.call()
        }
    }

    synchronized void clear(K key) {
        cacheMap.remove(key)
    }

    synchronized void clear() {
        cacheMap.clear()
    }
}

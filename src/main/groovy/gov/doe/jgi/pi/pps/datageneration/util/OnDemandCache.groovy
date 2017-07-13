package gov.doe.jgi.pi.pps.datageneration.util

/**
 * Created by dscott on 2/11/14.
 */
public class OnDemandCache<K> {

    private K cachedObject
    private boolean locked = false

    OnDemandCache() {}

    synchronized void forceCache(K objectToCache) {
        this.cachedObject = objectToCache
        this.locked = true
    }

    synchronized void cache(K objectToCache) {
        if (!locked) {
            this.cachedObject = objectToCache
            this.locked = true
        }
    }

    synchronized void cacheClosureOutput(Closure closure) {
        if (!locked) {
            this.cachedObject = closure?.call()
            this.locked = true
        }
    }

    K fetch(Closure fetchClosure) {
        if (!locked) {
            cacheClosureOutput(fetchClosure)
        }
        return cachedObject
    }

    K getCachedObject() {
        return	cachedObject
    }

    synchronized void clear() {
        cachedObject = null
        this.locked = false
    }

    boolean getLocked() {
        return locked
    }
}


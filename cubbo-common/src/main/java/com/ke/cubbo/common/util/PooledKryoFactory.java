package com.ke.cubbo.common.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;

public class PooledKryoFactory implements KryoFactory {
    private KryoPool pool;

    public PooledKryoFactory() {
        // Build pool with SoftReferences enabled (optional)
        pool = new KryoPool.Builder(this).softReferences().build();
    }

    public Kryo getKryo() {
        return pool.borrow();
    }

    public void returnKryo(Kryo kryo) {
        pool.release(kryo);
    }
    public Kryo create() {
        return new Kryo();
    }
}

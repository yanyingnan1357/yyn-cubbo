package com.ke.cubbo.common;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.ke.cubbo.common.util.PooledKryoFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

//@Component
public class SerializeKryo {

    private static PooledKryoFactory kryoFactory = new PooledKryoFactory();

    public static byte[] serialize(Object obj) {
        Kryo kryo = kryoFactory.getKryo();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Output output = new Output(outputStream);
        try {
            kryo.writeClassAndObject(output, obj);
            return output.toBytes();
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            output.close();
            if (kryo != null) {
                kryoFactory.returnKryo(kryo);
            }
        }
    }

    public static <T> T deserialize(byte[] bytes, Class<T> cls) {
        Kryo kryo = kryoFactory.getKryo();
        Input input = new Input(new ByteArrayInputStream(bytes));
        try {
            Object obj = kryo.readClassAndObject(input);
            return (T)obj;
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            input.close();
            if (kryo != null) {
                kryoFactory.returnKryo(kryo);
            }
        }
    }
}

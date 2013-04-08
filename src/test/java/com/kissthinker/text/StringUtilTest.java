package com.kissthinker.text;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.junit.Test;

/**
 * TODO Remove this test that simply tries out stuff i.e does not test any KISS functionality.
 * @author David Ainslie
 *
 */
public class StringUtilTest
{
    /** */
    private static final int BYTE_MASK = 0xff;

    /** */
    private static final int BYTE_SHIFT = 8;

    /**
     *
     */
    public StringUtilTest()
    {
        super();
    }

    /**
     *
     */
    @Test
    public void coding()
    {
        int hashCode = "Scooby".hashCode();
        String stringHashCode = String.valueOf(hashCode);
        byte[] bytesHashCode = stringHashCode.getBytes();

        String newStringHashCode = new String(bytesHashCode);
        int newHashCode = Integer.parseInt(newStringHashCode);

        assertEquals(hashCode, newHashCode);
        assertEquals(stringHashCode, newStringHashCode);
    }

    /**
     *
     * @throws IOException
     */
    @Test
    public void mmmmm() throws IOException
    {
        int hashCode = -1041829456;
        String stringHashCode = String.valueOf(hashCode);
        byte[] idBytes = stringHashCode.getBytes();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        dataOutputStream.write((idBytes.length >> BYTE_SHIFT) & BYTE_MASK);
        dataOutputStream.write(idBytes.length & BYTE_MASK);
        dataOutputStream.write(idBytes);

        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        int idLength = dataInputStream.readShort();

        idBytes = new byte[idLength];
        dataInputStream.readFully(idBytes);

        String newStringHashCode = new String(idBytes);
        assertEquals(stringHashCode, newStringHashCode);
    }
}
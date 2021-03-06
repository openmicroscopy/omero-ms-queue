package kew.providers.artemis.qchan;

import static java.util.Objects.requireNonNull;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.apache.activemq.artemis.api.core.client.ClientMessage;

import util.io.SinkWriter;
import util.lambda.ConsumerE;


/**
 * Writes the body of a message to the underlying Artemis buffer.
 * @see MessageBodyReader
 */
public class MessageBodyWriter
        implements SinkWriter<ConsumerE<OutputStream>, ClientMessage> {

    /**
     * Convenience method to instantiate a writer and have it write the
     * message body.
     * @param sink the Artemis message to write the body data to.
     * @param bodyWriter writes the body data to the given stream.
     * @throws NullPointerException if any argument is {@code null}.
     */
    public static void writeBody(ClientMessage sink,
                                 ConsumerE<OutputStream> bodyWriter) {
        MessageBodyWriter w = new MessageBodyWriter();
        w.write(sink, bodyWriter);
    }

    @Override
    public void write(ClientMessage sink, ConsumerE<OutputStream> bodyWriter) {
        requireNonNull(sink, "sink");
        requireNonNull(bodyWriter, "bodyWriter");

        ByteArrayOutputStream out = new ByteArrayOutputStream(4*1024);  // (*)
        bodyWriter.accept(out);
        byte[] serialized = out.toByteArray();

        sink.getBodyBuffer().writeInt(serialized.length);               // (*)
        sink.getBodyBuffer().writeBytes(serialized);
    }
    /* NOTE. Large messages.
     * If we ever going to need large messages (I doubt it!) we could easily
     * give this code an upgrade using NIO channels and byte buffers. (In
     * that case, don't forget to use a long instead of an int for the
     * serialised buffer size!)
     * However, bear in mind that for large messages (think GBs), holding
     * the whole thing into memory (as we do here) can cause severe indigestion
     * and possibly send Smuggler to the ER...So rather switch to stream
     * processing in that case!
     */
}

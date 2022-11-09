import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.vertx.core.buffer.Buffer;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

public class TestCase {
    private final boolean vertxBuffer;

    public TestCase(boolean vertxBuffer) {
        this.vertxBuffer = vertxBuffer;
        System.out.println("Using vertx byte buffer: " + vertxBuffer);
    }

    public void runTest() {
        String str = "\n";
        byte[] ref = new byte[]{0x0A};

        long iter = 0;
        long millis = System.currentTimeMillis();
        while (System.currentTimeMillis() - millis < 10_000) {
            byte[] buf = latin1StringToBytes(str);
            if (!Arrays.equals(ref, buf)) {
                throw new IllegalArgumentException("Arrays are not equal on iteration " + iter + ". Expected: " + ref[0] + ", detected: " + buf[0]);
            }
            ++iter;
        }
        System.out.printf("Passed %d iterations%n", iter);
    }

    public byte[] latin1StringToBytes(String str) {
        byte[] bytes = new byte[str.length()];
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            bytes[i] = (byte) (c & 0xFF);
        }

        return vertxBuffer ? vertxBuffer(bytes) : nettyBuffer(bytes);
    }

    private static byte[] vertxBuffer(byte[] bytes) {
        return Buffer.buffer(bytes).getBytes();
    }

    private static byte[] nettyBuffer(byte[] bytes) {
        ByteBuf buffer = new UnpooledByteBufAllocator(false).heapBuffer().writeBytes(bytes);
        byte[] arr = new byte[buffer.writerIndex()];
        buffer.getBytes(0, arr);
        return arr;
    }

    public static void main(String[] args) {
        System.out.println("Java: "  + javaVersion());
        System.out.println("Vertx: "  + vertxVersion());
        System.out.println("Netty: " + nettyVersion());
        try {
            new TestCase(true).runTest();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        try {
            new TestCase(false).runTest();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private static String javaVersion() {
        return System.getProperty("java.version");
    }

    private static String nettyVersion() {
        try {
            try (InputStream version = TestCase.class.getClassLoader().getResourceAsStream("META-INF/io.netty.versions.properties")) {
                Properties p = new Properties();
                p.load(version);
                String v = p.getProperty("netty-common.version");
                if (v == null) {
                    v = p.getProperty("netty-buffer.version");
                }
                return v;
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private static String vertxVersion() {
        try {
            try (InputStream input = TestCase.class.getClassLoader().getResourceAsStream("META-INF/vertx/vertx-version.txt")) {
                if (input == null) {
                    return null;
                }
                int n;
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                while (-1 != (n = input.read(buffer))) {
                    output.write(buffer, 0, n);
                }
                return output.toString("UTF-8");
            }
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}

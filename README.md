# vertx-byte-buffer-test
Reproducer for vertx(and underlying netty) byte buffer problem when buffer initialized byte array doesn't return it on subsequent getBytes() call

Tested reproduction on vertx 4.3.1 - 4.3.4 (netty-buffers 4.1.77.Final - 4.1.82.Final) and GraalVM EE 8 and 11. 
Is not reproduced on vertx 4.3.0 (netty-buffers 4.1.76.Final) or GraalVM CE 11/ Oracle JDK 8/11

- Build: `mvn install`
- Run: `java -jar target/vertx-byte-buffer-test-1.0.jar`
- Expected
```
Java: 11.0.16.1
Vertx: 4.3.0
Netty: 4.1.74.Final
Using vertx byte buffer: true
Passed 204437609 iterations
Using vertx byte buffer: false
Passed 137885709 iterations
```
- Detected
```
Java: 11.0.16.1
Vertx: 4.3.1
Netty: 4.1.77.Final
Using vertx byte buffer: true
java.lang.IllegalArgumentException: Arrays are not equal on iteration 45324. Expected: 10, detected: 0
	at TestCase.runTest(TestCase.java:26)
	at TestCase.main(TestCase.java:59)
Using vertx byte buffer: false
Passed 109503545 iterations
```

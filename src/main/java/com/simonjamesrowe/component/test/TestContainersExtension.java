package com.simonjamesrowe.component.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.lifecycle.Startable;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TestContainersExtension implements BeforeAllCallback, TestInstancePostProcessor {

    private static Network TEST_CONTAINERS_NETWORK = null;
    private static List<GenericContainer> runningContainers = new ArrayList<>();
    private static boolean shutDownHookRegisterd = false;

    @Override 
    public void beforeAll(ExtensionContext context) throws Exception {
        startUpTestContainers(context.getRequiredTestClass());
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        startUpTestContainers(context.getRequiredTestClass());
    }

    private void startUpTestContainers(Class<?> clazz) throws Exception {
        Annotation[] testAnnotations = clazz.getAnnotations();
        for (Annotation annotation : testAnnotations) {
            TestContainer[] testContainers = annotation.annotationType().getAnnotationsByType(TestContainer.class);
            for (TestContainer testContainer : testContainers) {
                initTestContainer(testContainer.initializers(), annotation);
            }
        }
    }

    private static synchronized Network createBridgeNetwork() {
        if (TEST_CONTAINERS_NETWORK == null) {
            try {
                TEST_CONTAINERS_NETWORK = Network.newNetwork();
            } catch (Exception e) {
                log.info("Could not create new bridge network", e);
            }
        }
        return TEST_CONTAINERS_NETWORK;
    }

    private void initTestContainer(Class<?>[] initializers, Annotation annotation) throws Exception {
        initializers[0].getConstructors()[0].newInstance(annotation);
    }

    public static void startContainer(GenericContainer testContainer) {
        synchronized (TestContainersExtension.class) {
            runningContainers.add(testContainer);
            if (!shutDownHookRegisterd) {
                Runtime.getRuntime().addShutdownHook(new Thread(DockerClientFactory.TESTCONTAINERS_THREAD_GROUP, TestContainersExtension::performCleanup));
                shutDownHookRegisterd = true;
            }
        }
        if (!bridgeNetworkExists()) {
            testContainer.withNetwork(createBridgeNetwork());
        }
        testContainer.start();
    }
    private static Boolean bridgeNetworkExists = null;

    private static boolean bridgeNetworkExists() {
        if (bridgeNetworkExists != null) {
            return bridgeNetworkExists;
        }

        try {
            com.github.dockerjava.api.model.Network bridge = DockerClientFactory.instance().client().inspectNetworkCmd()
                    .withNetworkId("bridge").exec();
            bridgeNetworkExists = bridge != null;
        } catch (Exception e) {
            bridgeNetworkExists = false;
        }
        return bridgeNetworkExists;
    }

    private static void performCleanup() {
        log.info("Cleaning up test container resources");
        runningContainers.forEach(Startable::close);
        if (TEST_CONTAINERS_NETWORK != null) {
            TEST_CONTAINERS_NETWORK.close();
        }
    }

}

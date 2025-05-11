package org.example.dependent;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import lombok.extern.slf4j.Slf4j;
import org.example.crd.Operator;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class ConfigMapDependentResource {

    /**
     * Manually handle ConfigMap creation without using owner references
     */
    public void reconcile(Operator resource, Context<Operator> context) {
        log.info("Manually reconciling ConfigMap");
        KubernetesClient client = context.getClient();

        // Get the namespace from the primary resource
        String namespace = resource.getMetadata().getNamespace();

        // Check if ConfigMap exists
        ConfigMap existingConfigMap = client.configMaps()
                .inNamespace(namespace)
                .withName("config")
                .get();

        ConfigMap desiredConfigMap = createDesiredConfigMap(resource, namespace);

        if (existingConfigMap == null) {
            // Create the ConfigMap if it doesn't exist
            log.info("Creating ConfigMap in namespace: {}", namespace);
            client.configMaps()
                    .inNamespace(namespace)
                    .resource(desiredConfigMap)
                    .create();
        } else {
            // Update if needed
            log.info("Updating ConfigMap in namespace: {}", namespace);
            client.configMaps()
                    .inNamespace(namespace)
                    .resource(desiredConfigMap)
                    .update();
        }
    }

    /**
     * Create the desired ConfigMap resource
     */
    private ConfigMap createDesiredConfigMap(Operator resource, String namespace) {
        return new ConfigMapBuilder()
                .withNewMetadata()
                .withName("config")
                .withNamespace(namespace)
                .endMetadata()
                .withData(Map.of("data", resource.getSpec().getEnv()))
                .build();
    }
}
package org.example.dependent;

import io.fabric8.istio.api.networking.v1beta1.Gateway;
import io.fabric8.istio.api.networking.v1beta1.GatewayBuilder;
import io.fabric8.istio.api.networking.v1beta1.ServerBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import lombok.extern.slf4j.Slf4j;
import org.example.crd.Operator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class GatewayDependentResource {

    /**
     * Manually handle gateway creation without using owner references
     */
    public void reconcile(Operator resource, Context<Operator> context) {
        log.info("Manually reconciling Gateway in istio-system namespace");
        KubernetesClient client = context.getClient();

        // Check if gateway exists
        Gateway existingGateway = client.resources(Gateway.class)
                .inNamespace("istio-system")
                .withName("test-gateway")
                .get();

        Gateway desiredGateway = createDesiredGateway();

        if (existingGateway == null) {
            // Create the gateway if it doesn't exist
            log.info("Creating Gateway in istio-system namespace");
            client.resources(Gateway.class)
                    .inNamespace("istio-system")
                    .resource(desiredGateway)
                    .create();
        } else {
            // Update if needed - simple comparison based on spec
            if (!existingGateway.getSpec().toString().equals(desiredGateway.getSpec().toString())) {
                log.info("Updating Gateway in istio-system namespace");
                client.resources(Gateway.class)
                        .inNamespace("istio-system")
                        .resource(desiredGateway)
                        .update();
            } else {
                log.info("Gateway already exists and is up-to-date");
            }
        }
    }

    /**
     * Create the desired Gateway resource
     */
    private Gateway createDesiredGateway() {
        return new GatewayBuilder()
                .withNewMetadata()
                .withName("test-gateway")
                .withNamespace("istio-system") // Explicitly set to istio-system namespace
                .endMetadata()
                .withNewSpec()
                .withSelector(Map.of("istio-gateway", "ingressgateway"))
                .withServers(List.of(new ServerBuilder()
                        .withNewPort("http", 80, "HTTP", null)
                        .addToHosts("*")
                        .build()))
                .endSpec()
                .build();
    }

    /**
     * Delete the gateway if needed
     */
    public void delete(KubernetesClient client) {
        log.info("Deleting Gateway from istio-system namespace");
        client.resources(Gateway.class)
                .inNamespace("istio-system")
                .withName("test-gateway")
                .delete();
    }
}
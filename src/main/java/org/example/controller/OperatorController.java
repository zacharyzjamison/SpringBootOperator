package org.example.controller;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.reconciler.*;
import io.javaoperatorsdk.operator.api.reconciler.dependent.Dependent;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.example.crd.Operator;
import org.example.crd.OperatorStatus;
import org.example.dependent.ConfigMapDependentResource;
import org.example.dependent.GatewayDependentResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@ControllerConfiguration
public class OperatorController implements Reconciler<Operator> {

    @Autowired
    private GatewayDependentResource gatewayDependentResource;

    @Autowired
    private ConfigMapDependentResource configMapDependentResource;

    @Override
    public UpdateControl<Operator> reconcile(Operator resource, Context<Operator> context) {
        log.info("Generation Detect: {}", resource.getMetadata().getGeneration());

        if (resource.getStatus() == null) {
            resource.setStatus(new OperatorStatus());
        }

        var status = resource.getStatus();

        var timestamp = Instant.now().toString();
        status.setCreatedAt(timestamp);

        gatewayDependentResource.reconcile(resource, context);

        configMapDependentResource.reconcile(resource, context);

        return UpdateControl.patchStatus(resource);
    }


    public DeleteControl cleanup(Operator resource, Context<Operator> context) {
        log.info("Cleaning up resources for: {}", resource.getMetadata().getName());

        // Get the Kubernetes client from the context
        KubernetesClient client = context.getClient();

        // Delete Gateway resource
        gatewayDependentResource.delete(client);

        return DeleteControl.defaultDelete();
    }
}

package org.example.controller;

import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import io.javaoperatorsdk.operator.api.reconciler.dependent.Dependent;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.example.crd.Operator;
import org.example.crd.OperatorStatus;
import org.example.dependent.ConfigMapDependentResource;
import org.example.dependent.GatewayDependentResource;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@ControllerConfiguration(
        dependents = {
                @Dependent(type= ConfigMapDependentResource.class),
                @Dependent(type = GatewayDependentResource.class)
        }
)
public class OperatorController implements Reconciler<Operator> {

    @Override
    public UpdateControl<Operator> reconcile(Operator resource, Context<Operator> context) {
        log.info("Generation Detect: {}", resource.getMetadata().getGeneration());

        if (resource.getStatus() == null) {
            resource.setStatus(new OperatorStatus());
        }

        var status = resource.getStatus();

        var timestamp = Instant.now().toString();
        status.setCreatedAt(timestamp);

        return UpdateControl.patchStatus(resource);
    }
}

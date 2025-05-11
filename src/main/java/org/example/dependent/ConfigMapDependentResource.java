package org.example.dependent;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;
import lombok.extern.slf4j.Slf4j;
import org.example.crd.Operator;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@KubernetesDependent
public class ConfigMapDependentResource extends CRUDKubernetesDependentResource<ConfigMap, Operator> {

    public ConfigMapDependentResource() {
        super(ConfigMap.class);
    }

    @Override
    protected ConfigMap desired(Operator resource, Context<Operator> context) {
        return new ConfigMapBuilder()
                .withNewMetadata()
                .withName("config")
                .endMetadata()
                .withData(Map.of("data",resource.getSpec().getEnv()))
                .build();
    }
}

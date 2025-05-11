package org.example.dependent;

import io.fabric8.istio.api.networking.v1beta1.Gateway;
import io.fabric8.istio.api.networking.v1beta1.GatewayBuilder;
import io.fabric8.istio.api.networking.v1beta1.Server;
import io.fabric8.istio.api.networking.v1beta1.ServerBuilder;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;
import lombok.extern.slf4j.Slf4j;
import org.example.crd.Operator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@KubernetesDependent
public class GatewayDependentResource extends CRUDKubernetesDependentResource<Gateway, Operator> {

    public GatewayDependentResource() {
        super(Gateway.class);
    }

    @Override
    protected Gateway desired(Operator resource, Context<Operator> context) {

        return new GatewayBuilder()
                .withNewMetadata()
                .withName("test-gateway")
                .endMetadata()
                .withNewSpec()
                .withSelector(Map.of("istio-gateway","ingressgateway"))
                .withServers(List.of(new ServerBuilder()
                                .withNewPort("http",80,"HTTP",null)
                                .addToHosts("*")
                        .build()))
                .withServers(List.of())
                .endSpec()
                .build();
    }
}


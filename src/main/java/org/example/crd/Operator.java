package org.example.crd;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;

@Group("org.example")
@Version("v1")
public class Operator extends CustomResource<OperatorSpec, OperatorStatus> implements Namespaced {
}

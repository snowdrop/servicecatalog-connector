package me.snowdrop.cloud.servicecatalog.connector;

import io.fabric8.kubernetes.api.model.SecretBuilder;
import me.snowdrop.servicecatalog.api.model.ServiceBinding;
import me.snowdrop.servicecatalog.api.model.ServiceBindingBuilder;
import me.snowdrop.servicecatalog.api.model.ServiceBindingListBuilder;
import me.snowdrop.servicecatalog.api.model.ServiceInstance;
import me.snowdrop.servicecatalog.api.model.ServiceInstanceBuilder;
import me.snowdrop.servicecatalog.api.model.ServiceInstanceListBuilder;
import me.snowdrop.servicecatalog.mock.ServiceCatalogServer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.service.ServiceInfo;
import java.util.Base64;

import static me.snowdrop.cloud.servicecatalog.connector.RelationalDatabaseServiceInfoCreator.DB_HOST;
import static me.snowdrop.cloud.servicecatalog.connector.RelationalDatabaseServiceInfoCreator.DB_NAME;
import static me.snowdrop.cloud.servicecatalog.connector.RelationalDatabaseServiceInfoCreator.DB_PASSWORD;
import static me.snowdrop.cloud.servicecatalog.connector.RelationalDatabaseServiceInfoCreator.DB_USER;

public class AbsractServiceCatalogConnectorTest {

    @Rule
    public ServiceCatalogServer server = new ServiceCatalogServer(true);

    protected ServiceInstance instance;
    protected ServiceBinding binding;

    protected Base64.Encoder encoder = Base64.getEncoder();

    @Before
    public void setup() {
        instance = new ServiceInstanceBuilder()
                .withNewMetadata()
                .withName("service-1")
                .withNamespace("test")
                .endMetadata()
                .withNewSpec()
                    .withClusterServiceClassExternalName("apb-mysql-persistent")
                .endSpec()
                .build();

        binding = new ServiceBindingBuilder()
                .withNewMetadata()
                .withName("binding-1")
                .withNamespace("test")
                .endMetadata()
                .withNewSpec()
                .withNewInstanceRef("service-1")
                .withSecretName("secret-1")
                .endSpec()
                .build();

        server.expect().get()
                .withPath("/apis/servicecatalog.k8s.io/v1beta1/serviceinstances")
                .andReturn(200, new ServiceInstanceListBuilder().addToItems(instance).build()).always();

        server.expect().get()
                .withPath("/apis/servicecatalog.k8s.io/v1beta1/namespaces/test/serviceinstances/service-1")
                .andReturn(200, instance)
                .always();

        server.expect().get()
                .withPath("/apis/servicecatalog.k8s.io/v1beta1/namespaces/test/servicebindings")
                .andReturn(200, new ServiceBindingListBuilder().addToItems(binding).build()).always();

        server.expect().get()
                .withPath("/apis/servicecatalog.k8s.io/v1beta1/namespaces/test/servicebindings/binding-1")
                .andReturn(200, binding)
                .always();


        server.expect().get()
                .withPath("/api/v1/namespaces/test/secrets/secret-1")
                .andReturn(200, new SecretBuilder()
                        .withNewMetadata()
                        .withName("secret-1")
                        .endMetadata()
                           .addToData(DB_HOST, new String(encoder.encode("myhost".getBytes())))
                           .addToData(DB_NAME, new String(encoder.encode("mydb".getBytes())))
                           .addToData(DB_USER, new String(encoder.encode("root".getBytes())))
                           .addToData(DB_PASSWORD, new String(encoder.encode("mypass".getBytes())))
                        .build())
                .always();

        ServiceCatalogClientRef.useClient(server.getServiceCatalogClient());
    }
}

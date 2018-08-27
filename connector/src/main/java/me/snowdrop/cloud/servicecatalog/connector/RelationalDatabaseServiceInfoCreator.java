/**
  * Copyright (C) 2018 Ioannis Canellos 
  *     
  * 
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  * 
  *         http://www.apache.org/licenses/LICENSE-2.0
  * 
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
**/
package me.snowdrop.cloud.servicecatalog.connector;

import io.fabric8.kubernetes.api.model.Secret;
import me.snowdrop.servicecatalog.api.client.ServiceCatalogClient;
import me.snowdrop.servicecatalog.api.model.ServiceBinding;
import me.snowdrop.servicecatalog.api.model.ServiceInstance;
import org.springframework.cloud.service.ServiceInfo;
import org.springframework.cloud.service.UriBasedServiceInfoCreator;
import org.springframework.cloud.service.common.MysqlServiceInfo;
import org.springframework.cloud.util.UriInfo;
import java.util.stream.Stream;
import java.util.Base64;

import java.util.Map;
import java.util.Optional;

public abstract class RelationalDatabaseServiceInfoCreator<S extends ServiceInfo> implements ServiceCatalogInfoCreator<S> {

    protected static final String DB_HOST = "DB_HOST";
    protected static final String DB_NAME = "DB_NAME";
    protected static final String DB_PORT = "DB_PORT";
    protected static final String DB_USER = "DB_USER";
    protected static final String DB_PASSWORD = "DB_PASSWORD";
    protected static final String DB_TYPE = "DB_TYPE";

    protected final ServiceCatalogClient client;
    protected final Base64.Decoder decoder = Base64.getDecoder();

    public RelationalDatabaseServiceInfoCreator() {
        this(ServiceCatalogClientRef.getClient());
    }

    public RelationalDatabaseServiceInfoCreator(ServiceCatalogClient client) {
        this.client = client;
    }

    public abstract String getScheme();

    public abstract String getDefaultPort();

    public abstract S createServiceInfo(String id, String uri);


    @Override
    public boolean accept(ServiceInstance instance) {
        Optional<Secret> secret = toSecrets(instance).findFirst();
        return secret.isPresent() &&
            secret.get().getData() != null &&
            !secret.get().getData().isEmpty();
    }

    public S createServiceInfo(ServiceInstance instance) {
       String binding = toBindings(instance)
                .map(b -> b.getMetadata().getName())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No binding found for service instance: " + instance.getMetadata().getName() + " in namespace: " + instance.getMetadata().getNamespace()+ "."));

        Secret secret = client.serviceBindings().inNamespace(instance.getMetadata().getNamespace()).withName(binding).getSecret();

        Map<String, String> data = secret.getData();
        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("Secret: " + secret.getMetadata().getName() + " is empty!");
        }

        UriInfo info = new UriInfo(getScheme(),
                                   getValue(data, DB_HOST),
                                   Integer.parseInt(getValue(data, DB_PORT, getDefaultPort())),
                                   getValue(data, DB_USER),
                                   getValue(data, DB_PASSWORD),
                                   getValue(data, DB_NAME),
                                   null);

        return createServiceInfo(instance.getMetadata().getName(), info.toString());
    }

    protected Stream<ServiceBinding> toBindings(ServiceInstance instance) {
        return client.serviceBindings().inNamespace(instance.getMetadata().getNamespace())
            .list()
            .getItems()
            .stream()
            .filter(b -> b.getSpec().getInstanceRef().getName().equals(instance.getMetadata().getName()));
    }

    protected Stream<Secret> toSecrets(ServiceInstance instance) {
        return client.serviceBindings().inNamespace(instance.getMetadata().getNamespace())
            .list()
            .getItems()
            .stream()
            .filter(b -> b.getSpec().getInstanceRef().getName().equals(instance.getMetadata().getName()))
            .map(b -> client.serviceBindings().inNamespace(instance.getMetadata().getNamespace()).withName(b.getMetadata().getName()).getSecret());
    }

    protected String getValue(Map<String, String> data, String key) {
        return getValue(data, key, null);
    }

    protected String getValue(Map<String, String> data, String key, String defaultValue) {
        String encoded = data.get(key);
        if (encoded == null) {
            return defaultValue;
        }
        return new String(decoder.decode(encoded));
    }
}

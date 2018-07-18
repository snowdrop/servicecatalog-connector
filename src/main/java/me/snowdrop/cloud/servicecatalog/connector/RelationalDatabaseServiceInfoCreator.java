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
		return true;
	}

    public S createServiceInfo(ServiceInstance instance) {

       String binding = client.serviceBindings().inNamespace(instance.getMetadata().getNamespace())
                .withField("spec.serviceInstanceRef.name", instance.getMetadata().getName())
                .list()
                .getItems()
                .stream()
                .map(b -> b.getMetadata().getName())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No binding found for service instance: " + instance.getMetadata().getName() + " in namespace: " + instance.getMetadata().getNamespace()+ "."));

        Secret secret = client.serviceBindings().inNamespace(instance.getMetadata().getNamespace()).withName(binding).getSecret();

        Map<String, String> data = secret.getStringData();
        UriInfo info = new UriInfo(getScheme(),
                data.get(DB_HOST),
                Integer.parseInt(data.getOrDefault(DB_PORT, getDefaultPort())),
                data.get(DB_USER),
                data.get(DB_PASSWORD),
                data.get(DB_NAME),
                null);

        return createServiceInfo(instance.getMetadata().getName(), info.toString());
    }

}

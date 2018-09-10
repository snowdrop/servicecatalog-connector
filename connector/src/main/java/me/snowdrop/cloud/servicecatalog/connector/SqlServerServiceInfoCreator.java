/**
  * Copyright (C) 2018 Red Hat Inc.
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
  * 
**/
package me.snowdrop.cloud.servicecatalog.connector;

import io.fabric8.kubernetes.api.model.Secret;
import me.snowdrop.servicecatalog.api.client.ServiceCatalogClient;
import me.snowdrop.servicecatalog.api.model.ServiceInstance;
import org.springframework.cloud.service.common.SqlServerServiceInfo;
import org.springframework.cloud.util.UriInfo;

import java.util.Map;
import java.util.Optional;

public class SqlServerServiceInfoCreator extends RelationalDatabaseServiceInfoCreator<SqlServerServiceInfo> {

    protected static final String SCHEME = "sqlserver";
    protected static final String IDENTIFIER = "mssql";

    protected static final String DB_ADMIN_USER = "DB_ADMIN_USER";
    protected static final String DB_ADMIN_PASSWORD = "DB_ADMIN_PASSWORD";

    protected static final String DEFAULT_USER = "sa";
    protected static final String DEFAULT_PORT = "1433";

    public SqlServerServiceInfoCreator() {
        super(ServiceCatalogClientRef.getClient());
    }

    public SqlServerServiceInfoCreator(ServiceCatalogClient client) {
        super(client);
    }

    public String getScheme() {
        return SCHEME;
    }

    public String getDefaultPort() {
        return DEFAULT_PORT;
    }

    @Override
    public SqlServerServiceInfo createServiceInfo(String id, String uri) {
        return new SqlServerServiceInfo(id, uri);
    }

    @Override
    public boolean accept(ServiceInstance instance) {
        String serviceClassName =  instance.getSpec().getClusterServiceClassExternalName();
        Optional<Secret> secret = toSecrets(instance).findFirst();
        return secret.isPresent() &&
            secret.get().getData() != null &&
            !secret.get().getData().isEmpty() &&
            serviceClassName != null && serviceClassName.toLowerCase().contains(IDENTIFIER);
    }

    @Override
    public SqlServerServiceInfo createServiceInfo(ServiceInstance instance) {
       String binding = toBindings(instance)
                .map(b -> b.getMetadata().getName())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No binding found for service instance: " + instance.getMetadata().getName() + " in namespace: " + instance.getMetadata().getNamespace()+ "."));

        Secret secret = client.serviceBindings().inNamespace(instance.getMetadata().getNamespace()).withName(binding).getSecret();
        if (secret == null) {
            throw new IllegalStateException("Secret: " + secret.getMetadata().getName() + " is missing!");
        }

        Map<String, String> data = secret.getData();
        if (data == null || data.isEmpty()) {
            throw new IllegalStateException("Secret: " + secret.getMetadata().getName() + " is empty!");
        }

        UriInfo info = new UriInfo(getScheme(),
                                   getValue(data, DB_HOST),
                                   Integer.parseInt(getValue(data, DB_PORT, getDefaultPort())),
                                   getValue(data, DB_USER, getValue(data, DB_ADMIN_USER, DEFAULT_USER)),
                                   getValue(data, DB_PASSWORD, getValue(data, DB_ADMIN_PASSWORD)),
                                   getValue(data, DB_NAME),
                                   null);

        return createServiceInfo(instance.getMetadata().getName(), info.toString());
    }
}

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

import me.snowdrop.servicecatalog.api.client.ServiceCatalogClient;
import me.snowdrop.servicecatalog.api.model.ServiceInstance;
import org.springframework.cloud.AbstractCloudConnector;
import org.springframework.cloud.FallbackServiceInfoCreator;
import org.springframework.cloud.app.ApplicationInstanceInfo;
import org.springframework.cloud.app.BasicApplicationInstanceInfo;
import org.springframework.cloud.service.BaseServiceInfo;
import org.springframework.cloud.util.EnvironmentAccessor;

import java.util.Collections;
import java.util.List;

public class ServiceCatalogConnector extends AbstractCloudConnector<ServiceInstance> {

    private final ServiceCatalogClient client;
    private final EnvironmentAccessor environment = new EnvironmentAccessor();

    public ServiceCatalogConnector() {
        this(ServiceCatalogClientRef.getClient());
    }

    public ServiceCatalogConnector(ServiceCatalogClient client) {
        super((Class) ServiceCatalogInfoCreator.class);
        this.client = client;
    }


    protected List<ServiceInstance> getServicesData() {
        return client.serviceInstances().list().getItems();
    }


    @Override
    protected FallbackServiceInfoCreator<?, ServiceInstance> getFallbackServiceInfoCreator() {
        return new FallbackServiceInfoCreator<BaseServiceInfo, ServiceInstance>() {
            @Override
            public BaseServiceInfo createServiceInfo(ServiceInstance instance) {

                return new BaseServiceInfo(instance.getMetadata().getName());
            }
        };
    }

    @Override
    public boolean isInMatchingCloud() {
        return true;
        //        return environment.getEnvValue("KUBERNETES_PORT") != null;
    }

    @Override
    public ApplicationInstanceInfo getApplicationInstanceInfo() {
        return new BasicApplicationInstanceInfo("TODO", "TOTO", Collections.<String, Object>emptyMap());
    }

}

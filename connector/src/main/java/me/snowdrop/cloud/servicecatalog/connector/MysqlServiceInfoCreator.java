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

import me.snowdrop.servicecatalog.api.client.ServiceCatalogClient;
import me.snowdrop.servicecatalog.api.model.ServiceInstance;
import org.springframework.cloud.service.common.MysqlServiceInfo;

public class MysqlServiceInfoCreator extends RelationalDatabaseServiceInfoCreator<MysqlServiceInfo> {

    protected static final String SCHEME = "mysql";
    protected static final String DEFAULT_PORT = "3306";


    public MysqlServiceInfoCreator() {
        super(ServiceCatalogClientRef.getClient());
    }

    public MysqlServiceInfoCreator(ServiceCatalogClient client) {
        super(client);
    }

    public String getScheme() {
        return SCHEME;
    }

    public String getDefaultPort() {
        return DEFAULT_PORT;
    }

    @Override
    public MysqlServiceInfo createServiceInfo(String id, String uri) {
        return new MysqlServiceInfo(id, uri);
    }

    @Override
    public boolean accept(ServiceInstance instance) {
        String serviceClassName =  instance.getSpec().getClusterServiceClassExternalName();
        return serviceClassName != null && serviceClassName.toLowerCase().contains(SCHEME);
    }
}

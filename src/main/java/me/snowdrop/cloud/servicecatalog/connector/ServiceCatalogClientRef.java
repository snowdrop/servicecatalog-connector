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

import me.snowdrop.servicecatalog.api.client.DefaultServiceCatalogClient;
import me.snowdrop.servicecatalog.api.client.ServiceCatalogClient;

import javax.xml.ws.Service;

public class ServiceCatalogClientRef {

    private static ServiceCatalogClient client;

    private ServiceCatalogClientRef() {
        this(new DefaultServiceCatalogClient());
    }

    private ServiceCatalogClientRef(ServiceCatalogClient client) {
        this.client = client;
    }

    public static synchronized ServiceCatalogClient getClient() {
        if (client == null) {
            client = new DefaultServiceCatalogClient();
        }
        return client;
    }

    //Let's use this for unit testing purposes.
    protected static synchronized void useClient(ServiceCatalogClient c) {
        client = c;
    }

}

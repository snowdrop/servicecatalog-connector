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
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.springframework.cloud.service.common.RelationalServiceInfo;

import java.util.Arrays;
import java.util.List;


@RunWith(Parameterized.class)
public class RelationalDatabaseServiceInfoCreatorTest extends AbsractServiceCatalogConnectorTest {

    @Parameters()
    public static List<Class> data() {
        return Arrays.asList(new Class[] {
                MysqlServiceInfoCreator.class,
                PostgressServiceInfoCreator.class,
                OracleServiceInfoCreator.class,
                SqlServerServiceInfoCreator.class
        });
    }

    @Parameter
    public static Class<? extends RelationalDatabaseServiceInfoCreator> creatorType;

    @Test
    public void testCreator() throws Exception {
        RelationalDatabaseServiceInfoCreator creator = creatorType.getConstructor(ServiceCatalogClient.class).newInstance(server.getServiceCatalogClient());
        RelationalServiceInfo info = (RelationalServiceInfo) creator.createServiceInfo(instance);
        Assert.assertNotNull(info);
        Assert.assertEquals("myhost", info.getHost());
    }
}

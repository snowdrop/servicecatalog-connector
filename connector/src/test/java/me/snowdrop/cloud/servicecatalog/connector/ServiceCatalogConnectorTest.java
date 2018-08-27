package me.snowdrop.cloud.servicecatalog.connector;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.service.ServiceInfo;

public class ServiceCatalogConnectorTest extends AbsractServiceCatalogConnectorTest {

    @Test
    public void testConnector() {
        CloudFactory factory = new CloudFactory();
        ServiceInfo info = factory.getCloud().getServiceInfo("service-1");
        Assert.assertNotNull(info);
    }
}
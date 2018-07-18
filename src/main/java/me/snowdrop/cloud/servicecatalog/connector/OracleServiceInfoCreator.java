package me.snowdrop.cloud.servicecatalog.connector;

import me.snowdrop.servicecatalog.api.client.ServiceCatalogClient;
import me.snowdrop.servicecatalog.api.model.ServiceInstance;
import org.springframework.cloud.service.common.OracleServiceInfo;

public class OracleServiceInfoCreator extends RelationalDatabaseServiceInfoCreator<OracleServiceInfo> {

    protected static final String SCHEME = "oracle";
    protected static final String DEFAULT_PORT = "1521";


    public OracleServiceInfoCreator() {
        super(ServiceCatalogClientRef.getClient());
    }

    public OracleServiceInfoCreator(ServiceCatalogClient client) {
        super(client);
    }

    public String getScheme() {
        return SCHEME;
    }

    public String getDefaultPort() {
        return DEFAULT_PORT;
    }

    @Override
    public OracleServiceInfo createServiceInfo(String id, String uri) {
        return new OracleServiceInfo(id, uri);
    }

        @Override
    public boolean accept(ServiceInstance instance) {
        String serviceClassName =  instance.getSpec().getClusterServiceClassExternalName();
        return serviceClassName != null && serviceClassName.toLowerCase().contains(SCHEME);
    }
}

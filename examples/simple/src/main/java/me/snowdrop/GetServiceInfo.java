package me.snowdrop;

import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.service.ServiceInfo;

public class GetServiceInfo
{
    private static final String DEFAULT_SERVICE = "dh-postgresql-apb-zcvv5";

    public static void main( String[] args )
    {
        String service = args.length < 1 ? DEFAULT_SERVICE : args[0];
        Cloud cloud = new CloudFactory().getCloud();
        ServiceInfo info = cloud.getServiceInfo(service);
        System.out.println("Service info for service:");
        System.out.println(info);
    }
}

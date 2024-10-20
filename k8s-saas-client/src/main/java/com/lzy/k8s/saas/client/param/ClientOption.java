package com.lzy.k8s.saas.client.param;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientOption {

    private String region;
    private String accessKeyId = "your-access-key-id";
    private String secretAccessKey = "your-secret-access-key";

}

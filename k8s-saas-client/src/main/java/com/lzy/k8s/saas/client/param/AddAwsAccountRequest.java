package com.lzy.k8s.saas.client.param;

import com.lzy.k8s.saas.client.model.AwsAccountInfo;
import lombok.Data;

@Data
public class AddAwsAccountRequest extends BaseParam {
    private static final long serialVersionUID = -2887808516561070545L;

    private SaasAccountParam saasAccountParam;

    private AwsAccountInfo awsAccountInfo;
}

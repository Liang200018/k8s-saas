package com.lzy.k8s.saas.infra.param;

import com.amazonaws.services.ec2.model.CreateKeyPairResult;
import com.amazonaws.services.ec2.model.KeyPair;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import lombok.Data;
import org.apache.commons.lang3.BooleanUtils;

@Data
public class Ec2ClientResult {

    private Boolean success;

    // create the instances
    private Reservation reservation;

    // create the keyPair
    private KeyPair keyPair;

    public boolean success() {
        return BooleanUtils.isTrue(success);
    }
}

package com.lzy.k8s.saas.infra.param;

import com.amazonaws.services.ec2.model.*;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.BooleanUtils;

import java.util.List;

@Data
@Builder
public class Ec2ClientResult {

    private Boolean success;

    // create the instances
    private Reservation reservation;

    // create the keyPair
    private KeyPair keyPair;

    // describe the instances
    private List<Instance> instances;

    public boolean success() {
        return BooleanUtils.isTrue(success);
    }
}

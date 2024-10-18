package com.lzy.k8s.saas.starter.controller;

import com.lzy.k8s.saas.client.dto.K8sClusterCreateDTO;
import com.lzy.k8s.saas.client.param.K8sClusterCreateParam;
import com.lzy.k8s.saas.client.result.Result;
import com.lzy.k8s.saas.core.checker.CreateClusterChecker;
import com.lzy.k8s.saas.core.service.K8sClusterCreateService;
import com.lzy.k8s.saas.infra.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@Slf4j
public class K8sClusterController {

    @Resource
    private K8sClusterCreateService k8sClusterCreateService;

    @PostMapping(value = "/cluster/create", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<K8sClusterCreateDTO> createCluster(@RequestBody K8sClusterCreateParam param) {
        try {
            CreateClusterChecker.checkCluster(param);
            K8sClusterCreateDTO clusterCreateDTO = k8sClusterCreateService.createCluster(param);
            return Result.ofSuccess(clusterCreateDTO);
        } catch (Throwable e) {
            if (param != null) {
                log.error("createCluster fail, requestId: {}, param: {}, err: ", param.getRequestId(), param, e);
            } else {
                log.error("createCluster fail, requestId: {}, param: {}, err: ", "null", param, e);
            }
            return ResultUtils.getResult(e);
        }
    }
}

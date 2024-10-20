# k8s-saas

## Module
My work aims to build those module:
+ manage k8s-saas platform account, save database
+ manage aws account, save database
+ manage k8s cluster instance configuration, save database
+ automate k8s setup procedure

## Improvement according to advices
### Done
+ The auth module integrates with [sa-token](https://sa-token.cc/). Set token to Response.
+ modify HTTP API style to make it more RESTFUL.
+ use HTTP response status code correctly.

## Testing

## Done
+ Register
+ Login
+ Logout
+ Create K8s Cluster


## Deploy
+ Ec2 instance: k8s master node, worker node
+ Database: MySQL  
+ Memory DataBase: Jimfs, save the pem file

## Support documentations

+ [Setup Your K8s Cluster with AWS EC2](https://milindasenaka96.medium.com/setup-your-k8s-cluster-with-aws-ec2-3768d78e7f05)
+ [AWS SDK for Java Documentation Examples](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/java)
+ [AWS CLI](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/index.html)
+ [Managing EC2 Instances in Java](https://www.baeldung.com/ec2-java)
+ [Jimfs](https://gitee.com/mirrors/Jimfs)
+ [sa-token](https://sa-token.cc/)

some video about introduce k8s
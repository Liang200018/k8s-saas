# k8s-saas

## Module
My work aims to build those module:
+ manage k8s-saas platform account, save database
+ manage aws account, save database
+ manage k8s cluster instance configuration, save database
  + coding about what to save about the instance 
  + **Importance: P2**
+ automate k8s setup procedure
  + coding about API call.
  + **Importance: P1**

## Testing

Now I only test those with simple function, and call the api by postman.
### Done
+ Register
+ Login
+ Logout

### TODO
+ EC2 Operation
+ Jsch shell batch

## Deploy
+ Ec2 instance: k8s master node, worker node
+ Database: MySQL  

## Support documentations

+ [Setup Your K8s Cluster with AWS EC2](https://milindasenaka96.medium.com/setup-your-k8s-cluster-with-aws-ec2-3768d78e7f05)
+ [AWS SDK for Java Documentation Examples](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/java)
+ [AWS CLI](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/index.html)
+ [Managing EC2 Instances in Java](https://www.baeldung.com/ec2-java)

some video about introduce k8s
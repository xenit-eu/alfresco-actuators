# Alfresco actuators

Health endpoint, unauthenticated, to be used as docker and load balancer health check.

The webscript is available at:

    alfresco/s/xenit/actuators/health

## Usage

Status code is to be used for the health check.

The output of the check is:

```
{"status":"UP"}
```

or

```
{"status":"DOWN"}
```

Currently the check looks at:

* system
  * os
  * java
  * cpu
* alfresco
  * id
  * version
  * edition
  * license
  * warManifest
  * modules
  * status

# Development

## Todo

### implment health groups

#### Design goal 
Organize health indicators into groups that can be used for different purposes. The load balancer can track one healthgroup, the orchestrator a different one.

#### References
* https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.endpoints.health.groups
* https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.spring-application.application-availability

See also branch [feature_healthgroups](https://github.com/xenit-eu/alfresco-actuators/tree/feature_healthgroups)

## How to run integration tests

```
./gradlew integrationTest
```

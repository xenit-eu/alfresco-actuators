# Alfresco actuators

Health endpoint, unauthenticated, to be used as load balancers checks and http checks for alerting.

Currently there are 2 endpoints implemented:

* A classical webscript:

    alfresco/s/xenit/actuators/health

* A dynamic-extension based REST endpoint:

    alfresco/s/xenit/api/v1/health/information

## Usage

Status code is to be used for the health check.

Output contains information about:

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
  * globalProperties (filtered)
  * status

## Integration tests

To be implemented.


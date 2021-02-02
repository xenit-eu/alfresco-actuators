# Alfresco actuators

Health endpoint, unauthenticated, to be used as docker and load balancer health check.

The webscript is available at:

    alfresco/s/xenit/actuators/health

## Usage

Status code is to be used for the health check.

The output of the check is:

    {"status":"UP"}

or

    {"status":"DOWN"}

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

## How to run integration tests

    ./gradlew integrationTest


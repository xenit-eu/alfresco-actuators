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

### implement health groups

#### Design goal 
Organize health indicators into groups that can be used for different purposes. The load balancer can track one healthgroup, the orchestrator a different one.

#### References
* https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.endpoints.health.groups
* https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.spring-application.application-availability

See also branch [feature_healthgroups](https://github.com/xenit-eu/alfresco-actuators/tree/feature_healthgroups)

### Down-on-readonly state

#### Design goal

Provide the means to extract a `DOWN` status from an unauthenticated endpoint when the system is in readonly modus.

#### Challenges

* Current implementation of the `StatusInfoService` provides info on this state, 
but does not interpret a readonly value as `DOWN`.
* We wish to avoid adding configuration to the `HealtIndicator` classes (at least the default ones).
* The general endpoints in the current implementation of `/xenit/actuators/health` aggregates all `HealthIndicators`.
Adding new indicators with conflicting interpretations of the system state will result in 'polluted' final state.
Adding the above-listed groups feature could work around this issue by defining the correct groups, and calling on those.

#### suggested solution

Introduce new `HealthIndicator` that returns `DOWN` on readonly state, with:
* either a flag to enable/disable it
* or as a separate artifact, 
with the administrator notice that it will affect the global state retrieved form the current health enpoint.

#### reference

[Xenit internal: ovam project](https://bitbucket.org/xenit/ovam-war/src/OVAMSLA-111/DownOnReadOnlyActuator/)

## How to run integration tests

```
./gradlew integrationTest
```

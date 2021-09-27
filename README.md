# Alfresco actuators

Health endpoints, both unauthenticated options for an aggregated status, and admin-authenticated options for status breakdown and additional details.

## Endpoints

| endpoint | authentication | returns | explanation |
| -------- | -------------- | ------- | ----------- |
| `xenit/actuators/health` | none | Json containing status element with status `UP` or `DOWN`. | Iterate over a list of all `HealthIndicators`, until one `DOWN` status is encountered or the list is run though completely. |
| `xenit/actuators/health/details` | admin | Json containing a map of indicators with substatus and details. | Iterate over all `HealthIndicators`, regardless of individual statuses, and returns a list of each Indicator's substatus and details. |
| `xenit/actuators/group/{groupName}` | none |  Json containing status element with status `UP` or `DOWN`. | Iterate over all indicators contained in the given group `{groupName}`, until a `DOWN` status is encountered or the list is completely run through. Groups must be predefined in config. |
| `xenit/actuators/group/{groupName}/details` | admin | Json containing a map of indicators with substatus and details. | Iterate over all `HealthIndicators` within the given group `{groupName}`, regardless of individual statuses, and returns a list of each Indicator's substatus and details. |

## Config

To use custom groups of `HealthIndicators`, add a json config file to your system and set the following alfresco global property to that file's url.
Example:
```properties
eu.xenit.actuators.groups.configfile.path=file:/opt/alfresco/actuators/custom.group.config.json
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

## How to run integration tests

    ./gradlew integrationTest


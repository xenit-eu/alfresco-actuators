# Alfresco actuators

Health endpoint, unauthenticated, to be used as docker and load balancer health check.

The webscript is available at:

```
    alfresco/s/xenit/actuators/health
    alfresco/s/xenit/actuators/health/details
```

with both having request parameter disabled with values :

* `SystemInfoService`
* `AlfrescoInfoService`
* `ContentInfoService`
* `LicenseInfoService`
* `StatusInfoService`

example:

* `alfresco/s/xenit/actuators/health?disabled=SystemInfoService,LicenseInfoService`
* `alfresco/s/xenit/actuators/health/details?disabled=SystemInfoService`

## Usage

Status code is to be used for the health check.

The output of the health check is:

```

{"status":"UP"}

```

or

```

{"status":"DOWN" , "message":"error message"}

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
    * content store

# Development

## Todo

See [TODO.md](TODO.md)

## How to run integration tests

```

./gradlew integrationTest

```

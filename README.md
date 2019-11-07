<img src="https://raw.githubusercontent.com/akvo/akvo-web/develop/code/wp-content/themes/Akvo-responsive/images/flow60px.png" />

[![Build Status](https://travis-ci.org/akvo/akvo-flow.svg?branch=master)](https://travis-ci.org/akvo/akvo-flow)

[Akvo Flow](http://akvo.org/products/akvoflow/) is a tool for collecting, evaluating and displaying of geographically referenced data. It is composed of an [android mobile app](https://github.com/akvo/akvo-flow-mobile/) and an online web-based platform. This repository contains code for the web-based platform that comprises a [backend engine](https://github.com/akvo/akvo-flow/tree/master/GAE) and a [dashboard user interface](https://github.com/akvo/akvo-flow/tree/master/Dashboard).  Alongside the dashboard and mobile apps, is a [data import and export component](https://github.com/akvo/akvo-flow-services).

You can read more about the [motivation and history of Akvo Flow](http://www.akvo.org/blog/?p=4836) as well as its place in [the platform of tools created by Akvo](http://www.akvo.org/blog/?p=4822).

## Development

### Start

To run Flow:

    docker-compose up --build -d && docker-compose logs -f

Flow should be running [here](http://localhost:8888) and you can login with user "akvo.flow.user.test@gmail.com"

You also have the appengine admin console [here](http://localhost:8888/_ah/admin)

The Docker-Compose environment will have:

1. A fake s3 server.
2. A fake flow services, that always returns 200.
3. The dev environment.

It downloads a prepopulated database from https://s3-eu-west-1.amazonaws.com/akvoflow/test-data/local_db.bin *if* there is none.

It will use the [dev appengine-web.xml](tests/dev-appengine-web.xml) *if* there is none.

See the [devserver.sh](ci/devserver.sh) for more details.

### UI development

Once Flow is started, any changes in the Dashboard folder will trigger a build of the UI code, except for the ClojureScript bit.

If you are going to work on the ClojureScript side, you can run a watch process with:

    docker-compose exec -u akvo akvo-flow /bin/bash -c "cd Dashboard/app/cljs && lein watch"

Or run the commands from a terminal inside the container:

    docker-compose exec -u akvo akvo-flow /bin/bash
    cd Dashboard/app/cljs
    lein watch

#### Editor Setup

The client-side (JS) code is linted using Eslint. It is recommended to install an Eslint plugin in your editor to display linting errors in realtime while making changes to the source code.

See here for a list of intergrations/plugins for various editors: https://eslint.org/docs/user-guide/integrations#editors

### Backend development

The appengine dev server is started in debug mode, listening in port 5005.

It is expected that your IDE understand the Maven pom and that it compiles the Java classes to the right place.

After you IDE compiles the classes, the dev server should refresh the webcontext. Due to some Mac performance issues with Docker, the refresh interval is 20 secs instead of the default 5 secs. You can change the scan interval by setting the property GAE_FULL_SCAN_SECS to the desired value. You can also trigger a reload hitting [the reload url](http://localhost:8888/_ah/reloadwebapp).

If you need to restart the server:

    docker-compose exec -u akvo -d akvo-flow /bin/bash -c "cd GAE && mvn appengine:stop appengine:run >> ./target/build.log"

Remember that you also can run those commands from a terminal inside the container.

### Stop

    docker-compose stop

### Tear down and reset

    docker-compose down
    rm -rf GAE/target

### Test instances and Manual deployments

If you want to use a configuration different from the dev one, checkout the akvo-flow-server-config directory into `..` and run:

    switch_tenant.sh akvoflowsandbox

To switch back to the dev setup:

    switch_to_local_tenant.sh

To deploy the current state of the docker container to whatever tenant you last switched to, run:


    docker-compose exec -u akvo akvo-flow ./dev-deploy.sh
    
### Running Flow Services and Flow together locally

If you want to run both Flow and Flow Services locally and talking to each other, you will need add some config to your `/etc/hosts`:

    127.0.0.1 services.akvoflow.local akvoflow.local

Then run:

    docker-compose -f docker-compose.together.yml up --build -d

**You will need to access Flow using the url [http://akvoflow.local:8888/](http://akvoflow.local:8888/)**

Then read the Flow Services documentation for the Flow Services specific instructions.   

The DNS alias is required because the UI is sending to Flow Services the baseUrl of the Flow service, which Flow Services needs to resolve to the Flow container.
The way Docker works, this baseUrl cannot be "localhost", as "localhost" for the Flow Service container is itself. 
Adding a DNS entry allows for one level of indirection where "akvoflow.local" will be resolved to "127.0.0.1" for the Browser, while it resolves to the flow container for the flow-services container.

#### Changing report Java classes

Once you have both flow and flow services running, if you are making changes to the Java report classes used in flow-services, 
you will need to run:

    docker-compose exec -u akvo akvo-flow /bin/bash -c "cd GAE && mvn install"

To package and install the Flow jar in your local maven repository. Then you can use this Flow jar as a dependency 
in your local Flow Services dev environment. See Flow Services README for how to set it up.

       
---

<p>&nbsp;</p>

<img src="http://www.yourkit.com/images/yklogo.png" />

YourKit supports open source projects with its full-featured Java Profiler.
YourKit, LLC is the creator of <a href="http://www.yourkit.com/java/profiler/index.jsp">YourKit Java Profiler</a>
and <a href="http://www.yourkit.com/.net/profiler/index.jsp">YourKit .NET Profiler</a>,
innovative and intelligent tools for profiling Java and .NET applications.

YourKit has offered an open source license to the Akvo FLOW team for the profiling of backend to improve its performance.

<img src="http://www.browserstack.com/images/layout/browserstack-logo-600x315.png" width="280"/>

[BrowserStack](http://www.browserstack.com) is supporting Akvo, allowing us to use their service and infrastructure to test the code in this repository. Thank you for supporting the open source community!

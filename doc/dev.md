# Development

## Start

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

## UI development

Once Flow is started, any changes in the Dashboard folder will trigger a build of the UI code, except for the ClojureScript bit.

If you are going to work on the ClojureScript side, you can run a watch process with:

    docker-compose exec -u akvo akvo-flow /bin/bash -c "cd Dashboard/app/cljs && lein watch"

Or run the commands from a terminal inside the container:

    docker-compose exec -u akvo akvo-flow /bin/bash
    cd Dashboard/app/cljs
    lein watch

### Editor Setup

The client-side (JS) code is linted using Eslint. It is recommended to install an Eslint plugin in your editor to display linting errors in realtime while making changes to the source code.

See here for a list of intergrations/plugins for various editors: https://eslint.org/docs/user-guide/integrations#editors

## Backend development

The appengine dev server is started in debug mode, listening in port 5005.

It is expected that your IDE understand the Maven pom and that it compiles the Java classes to the right place.

After you IDE compiles the classes, the dev server should refresh the webcontext. Due to some Mac performance issues with Docker, the refresh interval is 20 secs instead of the default 5 secs. You can change the scan interval by setting the property GAE_FULL_SCAN_SECS to the desired value. You can also trigger a reload hitting [the reload url](http://localhost:8888/_ah/reloadwebapp).

If you need to restart the server:

    docker-compose exec -u akvo -d akvo-flow /bin/bash -c "cd GAE && mvn appengine:stop appengine:run >> ./target/build.log"

Remember that you also can run those commands from a terminal inside the container.

## Stop

    docker-compose stop

## Tear down and reset

    docker-compose down
    rm -rf GAE/target

## Test instances and Manual deployments

If you want to use a configuration different from the dev one, checkout the akvo-flow-server-config directory into `..` and run:

    switch_tenant.sh akvoflowsandbox

To switch back to the dev setup:

    switch_to_local_tenant.sh

To deploy the current state of the working tree to whatever tenant you last switched to,
run this **from the host**, not from inside the dev container:

    ./dev-deploy.sh akvoflowsandbox $FLOW_GH_TOKEN

It used to be run inside the container, and it cannot be any more. The dev container is
`akvo/akvo-flow-builder`, whose Cloud SDK dates from 2020 and stages through the legacy
`AppCfg` tool; that tool requires a `<threadsafe>` element in `appengine-web.xml`, which
the second generation runtime rejects outright. The script now builds and runs
`ci/Dockerfile.gae-deploy` for you, which is the same image CI deploys from. The dev
container itself is unchanged and is still where the application runs while you work.

Only tenants already migrated to the second generation runtime can be deployed to. If you
switch to one that still declares `<runtime>java8</runtime>`, the script says so and stops
before doing any work.

## Running Flow Services and Flow together locally

If you want to run both Flow and Flow Services locally and talking to each other, you will need add some config to your `/etc/hosts`:

    127.0.0.1 akvoflow.test services.akvoflow.test

Then run:

    docker-compose -f docker-compose.together.yml up --build -d

**You will need to access Flow using the url [http://akvoflow.test:8888/](http://akvoflow.test:8888/)**

Then read the Flow Services documentation for the Flow Services specific instructions.   

The DNS alias is required because the UI is sending to Flow Services the baseUrl of the Flow service, which Flow Services needs to resolve to the Flow container.
The way Docker works, this baseUrl cannot be "localhost", as "localhost" for the Flow Service container is itself. 
Adding a DNS entry allows for one level of indirection where "akvoflow.test" will be resolved to "127.0.0.1" for the Browser, while it resolves to the flow container for the flow-services container.

We use the `.test` TLD rather than `.local` because `.local` is the mDNS domain: on Linux hosts running
Avahi with nss-mdns, the `hosts:` line in `/etc/nsswitch.conf` typically reads

    hosts: mymachines mdns_minimal [NOTFOUND=return] resolve files ...

`mdns_minimal` claims every `.local` name, finds nothing, and `[NOTFOUND=return]` aborts the lookup
before `/etc/hosts` is ever consulted. `.test` is reserved by RFC 6761 for exactly this kind of local
testing and no resolver special-cases it.

Both hostnames are needed on the host, for different reasons. "akvoflow.test" is the URL you browse
Flow with, and the one the UI hands to Flow Services as the Flow baseUrl. "services.akvoflow.test" is
the `flowServices` property from [dev-appengine-web.xml](tests/dev-appengine-web.xml), which
`EnvServlet` exports to the browser as `FLOW.Env.flowServices` -- the bulk upload and bulk image
upload views POST to it directly from the browser, so your host has to resolve it too. Flow's own
server-side calls to that name are resolved by Docker inside the container network.

### Changing report Java classes

Once you have both flow and flow services running, if you are making changes to the Java report classes used in flow-services, 
you will need to run:

    docker-compose exec -u akvo akvo-flow /bin/bash -c "cd GAE && mvn install"

To package and install the Flow jar in your local maven repository. Then you can use this Flow jar as a dependency 
in your local Flow Services dev environment. See Flow Services README for how to set it up.

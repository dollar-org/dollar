
[ ![Binary Distribution](download.png)](http://dollarscript.s3-website-eu-west-1.amazonaws.com/dist/dollar-0.3.3035.tgz)

[![GitHub License](https://img.shields.io/github/license/sillelien/dollar.svg)](https://raw.githubusercontent.com/sillelien/dollar/master/LICENSE) 
[![GitHub Issues](https://img.shields.io/github/issues/sillelien/dollar.svg)](https://github.com/sillelien/dollar/issues)
[![GitHub Release](https://img.shields.io/github/release/sillelien/dollar.svg)](https://github.com/sillelien/dollar)
[![Dependency Status](https://www.versioneye.com/user/projects/54ae285534ff3e2204000002/badge.svg?style=flat)](https://www.versioneye.com/user/projects/54ae285534ff3e2204000002)

[![Join the chat at https://gitter.im/sillelien/dollar](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/sillelien/dollar?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![GitQ](https://gitq.com/badge.svg)](https://gitq.com/sillelien/dollar)




![dollar](assets/gh-title-dollar.png)

Dollar is an integration centric, reactive dynamic programming language which runs on the JVM. It is a language built on top of the [dollar-core](https://github.com/sillelien/dollar-core) library which helps to provide a consistent view of multiple dynamic data types. 

It is being designed for the needs of server-side development such as integration, messaging etc. It is also an internet centric language where JSON and URIs are first class citizens.


**The language is currently in early but active development.**

Below is a fully functional persistent chat server in dollar:

```dollar
    //Fully working persistent chat server
    server= socketio://127.0.0.1:8092/bulletin?eventType=chatevent
    lastMessages= db:circular://messages/tmp/messages10.db?size=10

    message *= server
    timestampedMessage := (message + {"timestamp":DATE()})
    timestampedMessage >> lastMessages

    ("chatevent" : timestampedMessage) publish server

    http://127.0.0.1:8091/messages subscribe {
        {"body":all lastMessages, "headers":{"Access-Control-Allow-Origin":"*"}}
    }
```

Learn more at [http://sillelien.github.io/dollar](http://sillelien.github.io/dollar).

![Install](assets/gh-title-install.png)


Download the [distribution](http://dollarscript.s3-website-eu-west-1.amazonaws.com/dist/dollar-0.3.3035.tgz) then run the following in your shell:

```bash
    tar -zxvf dollar-0.3.3035.tgz
```

To use dollar:

```bash
    ./dollar/bin/dollar <filename>.ds
```

![Docs](assets/gh-title-docs.png)

Documentation is at [http://sillelien.github.io/dollar](http://sillelien.github.io/dollar).

Q&A: https://gitq.com/sillelien/dollar
 

![Maven](assets/gh-title-maven.png)

The artifacts used to build the Dollar project can be accessed using

```xml
            <repositories>
                <repository>
                    <id>dollar-s3-release-repo</id>
                    <name>S3 Release Repository for component1</name>
                    <url>s3://dollar-repo/release</url>
                    <releases>
                        <enabled>true</enabled>
                    </releases>
                    <snapshots>
                        <enabled>false</enabled>
                    </snapshots>
                </repository>
                <repository>
                    <id>dollar-s3-snapshot-repo</id>
                    <name>Component1-s3-snapshot-repo</name>
                    <url>s3://dollar-repo/snapshot</url>
                    <releases>
                        <enabled>false</enabled>
                    </releases>
                    <snapshots>
                        <enabled>true</enabled>
                    </snapshots>
                </repository>
            </repositories>
```

and 

```xml
        <dependency>
            <groupId>com.sillelien</groupId>
            <artifactId>dollar-xxx</artifactId>
            <version>0.3.3035</version>
        </dependency>
```


Dependencies: [![Dependency Status](https://www.versioneye.com/user/projects/54ae285534ff3e2204000002/badge.svg?style=flat)](https://www.versioneye.com/user/projects/54ae285534ff3e2204000002)



![Docker](assets/gh-title-docker.png)

There is a docker image, but it is in it's infancy so better to work with the  [distribution](http://dollarscript.s3-website-eu-west-1.amazonaws.com/dist/dollar-0.3.3035.tgz) for now.
```bash
docker run -v $HOME/.github:/root/.github -v $HOME/.dollar:/root/.dollar -v $(pwd):/build -it sillelien/dollarscript-headless:0.3  <filename>.ds
 ```
 
 [![Docker Registry](https://img.shields.io/docker/pulls/sillelien/dollarscript-headless.svg)](https://registry.hub.docker.com/u/sillelien/dollarscript-headless)
 

-------

** If you use this project please consider giving us a star on [GitHub](http://github.com/sillelien/dollar). **

Please contact me through Gitter (chat) or through GitHub Issues.

[![GitHub Issues](https://img.shields.io/github/issues/sillelien/dollar.svg)](https://github.com/sillelien/dollar/issues) [![Join the chat at https://gitter.im/sillelien/dollar](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/sillelien/dollar?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

For commercial support please <a href="mailto:hello@neilellis.me">contact me directly</a>.
-------

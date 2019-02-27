Analogweb Framework
===============================================

[![Build Status](https://travis-ci.org/analogweb/core.svg?branch=master)](https://travis-ci.org/analogweb/core)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.analogweb/analogweb-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.analogweb/analogweb-core)

Analogweb is a tiny HTTP oriented framework.
It depends only Java 8+.

## Example

```java
package org.analogweb.hello;

import java.net.URI;
import org.analogweb.annotation.Route;
import org.analogweb.core.Servers;

@Route("/")
public class Hello {

  public static void main(String... args) {
      Servers.run();
  }

  @Route
  public String hello() {
    return "Hello World";
  }

}
```


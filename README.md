Analogweb Framework
===============================================

[![Build Status](https://github.com/analogweb/core/actions/workflows/ci.yml/badge.svg)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.analogweb/analogweb-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.analogweb/analogweb-core)

Analogweb is a tiny HTTP oriented framework.
It depends only Java 11+.

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


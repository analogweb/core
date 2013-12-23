Analogweb Framework
===============================================

Analogweb is a tiny HTTP oriented framework.
It depends only Java 6+.

## Example

```java
package org.analogweb.hello;

import org.analogweb.annotation.Route;

@Route("/")
public class Hello {

  @Route
  public String hello() {
    return "Hello World";
  }

}
```

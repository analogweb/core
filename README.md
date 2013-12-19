Analog Web Framework
===============================================

AnalogWeb is a tiny HTTP orientied framework and it operated on Java 6+.

## Example

```java
package org.analogweb.hello;

import org.analogweb.annotation.Route;

@Route("/")
public class Hello {

  @Route("/")
  public String hello() {
    return "Hello World";
  }

}
```

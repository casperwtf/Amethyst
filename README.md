# Amethyst

Amethyst is a Java library that is based combines a varity of useful libraries
into a single library. It is designed to
be a simple and easy to use library that can be used in any project. Not all the
code is written by me, some of it is
from other libraries. I will try to give credit to the original author if I can
find it. If you find any code that is
not credited to the original author, please let me know.

## Features

* Storage System - Supports MongoDB, MySQL, MariaDB, SQLite and JSON under a
  single interface.
* Various Utilities - A variety of utilities that can be used in any project.
  This includes a logger, discord webhook,
  math utils, string utils, pasting service utils and more.

## Useful Stuff

YourKit supports open source projects with innovative and intelligent tools
for monitoring and profiling Java and .NET applications.
YourKit is the creator of (YourKit Java
Profiler)[https://www.yourkit.com/java/profiler/],
(YourKit .NET Profiler)[https://www.yourkit.com/dotnet-profiler/],
and (YourKit YouMonitor)[https://www.yourkit.com/youmonitor/].

## Used Libraries

* [BoostedYAML](https://github.com/dejvokep/boosted-yaml)
* [HikariCP](https://github.com/brettwooldridge/HikariCP)
* [Caffeine](https://github.com/ben-manes/caffeine)
* [Quartz](https://github.com/quartz-scheduler/quartz)
* [Crunch](https://github.com/Redempt/Crunch)
* [Lettuce](https://github.com/lettuce-io/lettuce-core)
* [MongoDB](https://github.com/mongodb/mongo-java-driver)
* [Gson](https://github.com/google/gson)
* [Fastboard](https://github.com/MrMicky-FR/FastBoard)

## Highlights + Useful Tips

When working with the packetevents API, converting spigot objects to packet
event objects is used
in [this class](https://github.com/retrooper/packetevents/blob/2.0/spigot/src/main/java/io/github/retrooper/packetevents/util/SpigotConversionUtil.java)

wtf.casper.amethyst.core.inject.Inject exists because I don't like Spring
Dependency Injection or Guice Dependency Injection. I like to keep things simple
and easy to use. This is why I created my own dependency injection system. It is
very simple and easy to use. Here is an example of how to use it:

```java
public class Main {
    
    public static void main(String[] args) {
        // Bind the Main.class to an instance + global gson instance in the global/default container
        Inject.bind(Main.class, new Main());
        Inject.bind(Gson.class, new Gson());
      
        // Create a container for containerized bindings of classes (optional, there is a global one)
        TestContainer container = new TestContainer();
        
        // Bind a Gson instance only for the container
        Inject.bind(Gson.class, new Gson(), container);
        
        // Override previous binding with a supplier for a gson instance for new instance every time in that container
        Inject.bind(Gson.class, () -> new Gson(), container);
        
        // Get the instance of Main.class
        Main main = Inject.get(Main.class); 
      
        // Get the instance of Gson.class from the container
        Gson gson = Inject.get(Gson.class, container);
        
        // Get the instance of Gson.class from the global container
        Gson gson = Inject.get(Gson.class); 
        
        // Get a lazy reference to Gson.class from the global container. This is useful if you want to get the object before its created and use it after its created.
        LazyReference<Gson> gsonLazyReference = Inject.getLater(Gson.class);

        // This will get the instance of Gson.class from the global container. When this is ran for the first time it'll run the get function and then cache the value.
        Gson lazyGson = gsonLazyReference.get();
    }
    public static class TestContainer extends InjectionContainer {
        public TestContainer() {
            super(InjectionContainer.GLOBAL, 10);
        }
    } 
    
}
```

I include BCrypt so you can hash passwords easily. Here is an example of how to
use it:

```java
public class Main {
    
    public static void main(String[] args) {
        // Hash a password
        String hashedPassword = BCrypt.hashpw("password", BCrypt.gensalt());
        
        // Check if a password matches a hash
        boolean matches = BCrypt.checkpw("password", hashedPassword);
    }
    
}
```
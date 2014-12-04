puni
====

Puni is an extremely lightweight, netty-based web container. It has one job, and that job is to serve HTTP requests.

## Usage
The first things you'll need are a configuration class and a configuration file. Puni unmarshalls its configuration from YAML files.
It also provides a base configuration class that should be subclassed so that we can include the basic configuration that puni requires.

```java
public class MyConfig extends AppConfiguration {
    private Boolean enabled = true;
    
    public Boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
```

Configurations are POJOs that extend AppConfiguration.
Our configuration file looks like so:

_config.yml_:
```yaml
bindAddress: 0.0.0.0    # the address to bind to. defined in AppConfiguration. Defaults to 0.0.0.0
port: 8081              # the port to bind to. defined in AppConfiguration. Defaults to 8080

enabled: true           # Out enabled field we defined in MyConfig
```

Note that bindAddress and enabled are explicitly set to their defaults. This isn't necessary and our config can be reduced to:

```yaml
port: 8081
```

Now that we have our configuration stuff, let's create an HttpHandler.

HttpHandlers are used to handle incoming HTTP requests. Internally, handlers are mapped to resources, and the mappings are in turn mapped to HTTP methods.
When a request is muxed, it looks up the resource mapping based on the request's method and tries to find a handler that matches the resource.

Let's create our own HttpHandler implementation.

```java
public class HelloWorldHandler implements HttpHandler {
    
    public FullHttpResponse handle(FullHttpRequest req) {
        resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                                          HttpResponseStatus.OK,
                                          Unpooled.copiedBuffer("Hello World", CharsetUtil.UTF_8);
    }
}
```

What this does, is create a new DefaultFullHttpResponse instance with status _200 OK_ and body _Hello World_.

Pretty simple, right?

But how do we actually serve this handler?

The answer to that lies in the final piece of the puzzle. The application.

The core piece of a puni app is the Application class. You need to subclass this abstract base class and implement its one abstract method.

```java
public class MyApp extends Application<MyConfig> {
    protected MyApp() {
        super(MyConfig.class)
    }
    
    @Override
    public void configure(MyConfig config, Muxer mux) throws Exception {
        if (!config.isEnabled())
            throw new Exception("Application not enabled.");
          
        mux.handle(HttpMethod.GET, "/hello", new HelloWorldHandler());
    }
    
    public static void main(String[] args) {
        MyApp app = new MyApp();
        try {
            app.loadConfiguration(new File(args[0]));
            app.start();
        } catch (Exception e) {
            e.printStackTrack(e);
        }
    }
}
```

Let's break this down. First of all, you'll notice that Application is a generic class. It requires the type of the configuration file that you will be using.
Also notice that Application's constructor requires the class of the same configuration type. This is because it needs this information when unmarshalling the yaml file.

Next up, we have teh overridden configure method. This is the abstract method that was mentioned earlier. This method is used to provide any last minute configuration before the application is started.
This is where you will be able to do things like validate configuration and register handlers with the muxer. Speaking of muxing, notice that we register our 
HelloWorldHandler with the muxer with the resource "/hello" and the method GET. Basically, whenever a GET method to the resource /test comes in, it will be routed to our hellow world handler.
the string passed in as the resource can be any valid regex.

The last thing to note is how we actually start the application. It is very important to note that configuration must be loaded using the loadConfiguration() method before the start() method is called. Otherwise, an exception will be thrown.

Now, with all of that out of the way, if you run the application with the first command line argument being the path to the config file, you will see that puni starts up all nice like. That's all fine and dandy, but the real magic comes into play when you point your web browser to http://localhost:8080/hello.

You should now see the text _Hello World_ displayed in your browser. Et Voila! Your first puni app is complete!

## More on muxing
So as you saw in the example applicaiton, requests are muxed and routed to a registered handler (if one exists). Puni comes packaged with a default muxer (creatively named DefaultMuxer), that muxes based on regular expressions. You are not limited to this muxer though and are more than welcome to create your own. To do so, you simply need to implement the Muxer interface. As an example, let's create a muxer that looks for exact resource matches.

```java
public class ExactMuxer implements Muxer {
    private Map<HttpMethod, Map<String, HttpHandler>> methodMap = new HashMap<>();
    
    public ExactMuxer() {
        methodMap.put(HttpMethod.CONNECT, new HashMap<>());
        methodMap.put(HttpMethod.DELETE, new HashMap<>());
        methodMap.put(HttpMethod.GET, new HashMap<>());
        methodMap.put(HttpMethod.HEAD, new HashMap<>());
        methodMap.put(HttpMethod.OPTIONS, new HashMap<>());
        methodMap.put(HttpMethod.PATCH, new HashMap<>());
        methodMap.put(HttpMethod.POST, new HashMap<>());
        methodMap.put(HttpMethod.PUT, new HashMap<>());
        methodMap.put(HttpMethod.TRACE, new HashMap<>());
    }
    
    @Override
    public void handle(HttpMethod method, String resource, HttpHandler handler) {
        methodMap.get(method).put(resource, handler);
    }
    
    @Override
    public HttpHandler mux(String resource, HttpMethod method) {
        Map<String, HttpHandler> handlerMap = methodMap.get(method);
        for (Map.Entry<String, HttpHandler> entry : handlerMap.entrySet()) {
            if (entry.getKey().equals(resource))
                return entry.getValue();
        }
        
        return null;
    }
}
```

This class is fairly straitforward. It keeps a map of registered handlers and uses tha map to mux a request. When a request arrives for muxing, it iterates the entries of the map and returns the first HttpHandler that is tied the request's resource.

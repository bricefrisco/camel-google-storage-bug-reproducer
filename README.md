# Camel Google Storage: Bug Reproducer


## The issue:

When using the Consumer and the Producer within the same route, the `objectName` can conflict with each other.
Consider the following example:
```java
public class MyRouteBuilder extends RouteBuilder {
    @Override
    public void configure() {
        from("timer:test?repeatCount=1")
                .pollEnrich("google-storage://{{google.cloud.bucket.name}}" +
                        "?objectName=hello.txt" +
                        "&deleteAfterRead=false")
                .log("File received with contents: '${body}'")

                .setBody(simple("hello2"))
                .log("Writing to hello2.txt with body: '${body}'")
                .to("google-storage://{{google.cloud.bucket.name}}?objectName=hello2.txt");
    }
}
```

The expectation would be that the file `hello.txt` would be read, and then a separate file `hello2.txt` would be written.
However, what actually happens is that the original file `hello.txt` is overwritten.

## The workaround:
A workaround is to add `.removeHeaders("*")` before writing the new file:
```java
.removeHeaders("*")
.to("google-storage://{{google.cloud.bucket.name}}?objectName=hello2.txt");
```

## To reproduce using this repository:
1. Create a Google cloud storage bucket, and upload any file to it named `hello.txt`
2. Update the `application.properties` file with the bucket name
3. Run `MyApplication` to start the route
4. Check the bucket, and you will see that `hello.txt` has been overwritten with the contents `hello2`
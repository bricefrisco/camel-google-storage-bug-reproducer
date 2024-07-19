package org.bfrisco.camel.reproducer;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.google.storage.GoogleCloudStorageComponent;
import org.apache.camel.component.google.storage.GoogleCloudStorageConstants;

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

                // Uncommenting the following line will result in the expected behavior:
                // .removeHeaders("*")
                .to("google-storage://{{google.cloud.bucket.name}}?objectName=hello2.txt");
    }
}

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;



public class AirQualityConsumer extends RouteBuilder {
    

    public void configure() throws Exception {
        from("timer:refresh?period=20000&fixedRate=true")
                .log("OpenAQ route running")
                .setHeader(Exchange.HTTP_METHOD).constant("GET")
                .to("https://api.openaq.org/v1/measurements?limit=10")
                .unmarshal().json()
                .wireTap("kafka:all-query?brokers=localhost:9092")
                .split().simple("${body[results]}")
                    .to("kafka:pm-data?brokers=localhost:9092");

        from("direct:tap")
                .setBody(simple("Received message from OpenAQ: ${body}"))
                .to("log:info");
    }
}

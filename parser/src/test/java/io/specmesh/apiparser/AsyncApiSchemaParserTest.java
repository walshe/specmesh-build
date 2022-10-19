package io.specmesh.apiparser;

import io.specmesh.apiparser.model.ApiSpec;
import io.specmesh.apiparser.model.Channel;
import io.specmesh.apiparser.model.Operation;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AsyncApiSchemaParserTest {
    final ApiSpec apiSpec = getAPISpecFromResource();

    @Test
    public void shouldReturnProducerMessageSchema() {
        final Map<String, Channel> channelsMap = apiSpec.channels();
        assertThat(channelsMap.size(), is(2));
        assertThat("Should have assembled 'id + channelname'", channelsMap.keySet(), hasItem("simple.schema-demo.public.user.signed"));

        final Operation publish = channelsMap.get("simple.schema-demo.public.user.signed").publish();
        assertThat(publish.message().schemaRef(), is("simple_schema_demo_user-signedup.avsc"));

        assertThat(publish.message().schemaFormat(), is("application/vnd.apache.avro+json;version=1.9.0"));
        assertThat(publish.message().contentType(), is("application/octet-stream"));
        assertThat(publish.message().bindings().kafka().schemaIdLocation(), is("header"));
    }

    @Test
    public void shouldReturnSubscriberMessageSchema() {
        final Map<String, Channel> channelsMap = apiSpec.channels();
        assertThat(channelsMap.size(), is(2));
        assertThat("Should have assembled 'id + channelname'", channelsMap.keySet(), hasItem("london.hammersmith.transport.public.tube"));

        final Operation subscribe = channelsMap.get("london.hammersmith.transport.public.tube").subscribe();
        assertThat(subscribe.message().schemaRef(), is("london_hammersmith_transport_public_passenger.avsc"));
        assertThat(subscribe.message().schemaFormat(), is("application/vnd.apache.avro+json;version=1.9.0"));
        assertThat(subscribe.message().contentType(), is("application/octet-stream"));
        assertThat(subscribe.message().bindings().kafka().schemaIdLocation(), is("header"));
    }




    private ApiSpec getAPISpecFromResource() {
        try {
            return new AsyncApiParser().loadResource(getClass().getClassLoader().getResourceAsStream("simple_schema_demo-api.yaml"));
        } catch (Throwable t) {
            throw new RuntimeException("Failed to load test resource", t);
        }
    }
}

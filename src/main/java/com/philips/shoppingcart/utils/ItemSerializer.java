package com.philips.shoppingcart.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.philips.shoppingcart.pojos.Item;

import java.io.IOException;

/**
 * Custom serializer logic to help serialize item product
 * properties embedded within Item entities
 */
public class ItemSerializer extends StdSerializer<Item> {

    public ItemSerializer() {
        this(null);
    }

    public ItemSerializer(Class<Item> t) {
        super(t);
    }

    @Override
    public void serialize(Item item, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("Name", item.getProduct().getName());
        gen.writeNumberField("Quantity", item.getQuantity());
        gen.writeNumberField("Price", item.getProduct().getPrice());
        gen.writeEndObject();
    }
}
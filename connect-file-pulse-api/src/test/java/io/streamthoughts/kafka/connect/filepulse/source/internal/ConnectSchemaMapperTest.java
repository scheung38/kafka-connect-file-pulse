/*
 * Copyright 2019-2020 StreamThoughts.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.streamthoughts.kafka.connect.filepulse.source.internal;

import io.streamthoughts.kafka.connect.filepulse.data.Schema;
import io.streamthoughts.kafka.connect.filepulse.data.TypedStruct;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.data.Struct;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ConnectSchemaMapperTest {

    @Test
    public void shouldMapGivenSimpleTypedStruct() {

        TypedStruct struct = TypedStruct.create()
                .put("field1", "value1")
                .put("field2", "value2");

        SchemaAndValue schemaAndValue = struct.schema().map(ConnectSchemaMapper.INSTANCE, struct);
        Assert.assertNotNull(schemaAndValue);

        Struct connectStruct = (Struct)schemaAndValue.value();
        Assert.assertEquals("value1", connectStruct.get("field1"));
        Assert.assertEquals("value2", connectStruct.get("field2"));
    }

    @Test
    public void shouldMapGivenNestedTypedStruct() {
        TypedStruct struct = TypedStruct.create()
                .put("field1", TypedStruct.create().put("field2", "value2"));

        SchemaAndValue schemaAndValue = struct.schema().map(ConnectSchemaMapper.INSTANCE, struct);
        Assert.assertNotNull(schemaAndValue);

        Struct connectStruct = (Struct)schemaAndValue.value();
        Struct field1 = (Struct)connectStruct.get("field1");
        Assert.assertNotNull(field1);

        Assert.assertEquals("value2", field1.get("field2"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldMapGivenTypeStructWithArrayField() {
        TypedStruct struct = TypedStruct.create()
                .put("field1", Collections.singletonList("value"));

        SchemaAndValue schemaAndValue = struct.schema().map(ConnectSchemaMapper.INSTANCE, struct);
        Assert.assertNotNull(schemaAndValue);

        Struct connectStruct = (Struct)schemaAndValue.value();
        List<String> field1 = (List<String>)connectStruct.get("field1");
        Assert.assertNotNull(field1);
        Assert.assertEquals("value", field1.get(0));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldMapGivenTypeStructWithArrayOfStruct() {
        TypedStruct struct = TypedStruct.create()
                .put("field1", Collections.singletonList(TypedStruct.create().put("field2", "value")));

        SchemaAndValue schemaAndValue = struct.schema().map(ConnectSchemaMapper.INSTANCE, struct);
        Assert.assertNotNull(schemaAndValue);

        Struct connectStruct = (Struct)schemaAndValue.value();
        List<Struct> field1 = (List<Struct>)connectStruct.get("field1");
        Assert.assertNotNull(field1);
        Assert.assertEquals("value", field1.get(0).getString("field2"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldMapGivenTypeStructWithMapField() {
        TypedStruct struct = TypedStruct.create()
                .put("field1", Collections.singletonMap("field2", "value"));

        SchemaAndValue schemaAndValue = struct.schema().map(ConnectSchemaMapper.INSTANCE, struct);
        Assert.assertNotNull(schemaAndValue);

        Struct connectStruct = (Struct)schemaAndValue.value();
        Map<String, String> field1 = (Map<String, String>)connectStruct.get("field1");
        Assert.assertNotNull(field1);
        Assert.assertEquals("value", field1.get("field2"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldMapGivenTypeStructWithMapWithStructValue() {
        TypedStruct struct = TypedStruct.create()
            .put("field1",
                Collections.singletonMap(
                    "field2",
                    TypedStruct.create().put("field3", "value")
                ));

        SchemaAndValue schemaAndValue = struct.schema().map(ConnectSchemaMapper.INSTANCE, struct);
        Assert.assertNotNull(schemaAndValue);

        Struct connectStruct = (Struct)schemaAndValue.value();
        Map<String, Struct> field1 = (Map<String, Struct>)connectStruct.get("field1");
        Assert.assertNotNull(field1);
        Assert.assertEquals("value", field1.get("field2").getString("field3"));
    }

    @Test
    public void shouldMapGivenTypeStructWithNullValue() {
        TypedStruct struct = TypedStruct.create()
                .put("field1", "value1")
                .put("field2", Schema.none(), null);

        SchemaAndValue schemaAndValue = struct.schema().map(ConnectSchemaMapper.INSTANCE, struct);
        Assert.assertNotNull(schemaAndValue);

        Struct connectStruct = (Struct)schemaAndValue.value();
        Assert.assertNotNull(connectStruct.schema().field("field1"));
        Assert.assertNull(connectStruct.schema().field("field2"));
    }

}
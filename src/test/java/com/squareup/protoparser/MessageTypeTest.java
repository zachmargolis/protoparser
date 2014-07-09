package com.squareup.protoparser;

import org.junit.Test;

import static com.squareup.protoparser.EnumType.Value;
import static com.squareup.protoparser.MessageType.Field;
import static com.squareup.protoparser.MessageType.Label.REQUIRED;
import static com.squareup.protoparser.TestUtils.NO_EXTENSIONS;
import static com.squareup.protoparser.TestUtils.NO_FIELDS;
import static com.squareup.protoparser.TestUtils.NO_OPTIONS;
import static com.squareup.protoparser.TestUtils.NO_TYPES;
import static com.squareup.protoparser.TestUtils.list;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;

public class MessageTypeTest {
  @Test public void emptyToString() {
    Type type = new MessageType.Builder().setName("Message")
        .setFullyQualifiedName("")
        .setDocumentation("")
        .setFields(NO_FIELDS)
        .setNestedTypes(NO_TYPES)
        .setExtensions(NO_EXTENSIONS)
        .setOptions(NO_OPTIONS)
        .build();
    String expected = "message Message {}\n";
    assertThat(type.toString()).isEqualTo(expected);
  }

  @Test public void simpleToString() {
    Field field = new Field.Builder().setLabel(REQUIRED)
        .setType("Type")
        .setName("name")
        .setTag(1)
        .setDocumentation("")
        .setOptions(NO_OPTIONS)
        .build();
    Type type =
        new MessageType.Builder().setName("Message")
            .setFullyQualifiedName("")
            .setDocumentation("")
            .setFields(list(field))
            .setNestedTypes(NO_TYPES)
            .setExtensions(NO_EXTENSIONS)
            .setOptions(NO_OPTIONS)
            .build();
    String expected = ""
        + "message Message {\n"
        + "  required Type name = 1;\n"
        + "}\n";
    assertThat(type.toString()).isEqualTo(expected);
  }

  @Test public void simpleWithDocumentationToString() {
    Field field = new Field.Builder().setLabel(REQUIRED)
        .setType("Type")
        .setName("name")
        .setTag(1)
        .setDocumentation("")
        .setOptions(NO_OPTIONS)
        .build();
    Type type =
        new MessageType.Builder().setName("Message")
            .setFullyQualifiedName("")
            .setDocumentation("Hello")
            .setFields(list(field))
            .setNestedTypes(NO_TYPES)
            .setExtensions(NO_EXTENSIONS)
            .setOptions(NO_OPTIONS)
            .build();
    String expected = ""
        + "// Hello\n"
        + "message Message {\n"
        + "  required Type name = 1;\n"
        + "}\n";
    assertThat(type.toString()).isEqualTo(expected);
  }

  @Test public void simpleWithOptionsToString() {
    Field field = new Field.Builder().setLabel(REQUIRED)
        .setType("Type")
        .setName("name")
        .setTag(1)
        .setDocumentation("")
        .setOptions(NO_OPTIONS)
        .build();
    Type type = new MessageType.Builder().setName("Message")
        .setFullyQualifiedName("")
        .setDocumentation("")
        .setFields(list(field))
        .setNestedTypes(NO_TYPES)
        .setExtensions(NO_EXTENSIONS)
        .setOptions(list(new Option("kit", "kat")))
        .build();
    String expected = ""
        + "message Message {\n"
        + "  option kit = \"kat\";\n"
        + "\n"
        + "  required Type name = 1;\n"
        + "}\n";
    assertThat(type.toString()).isEqualTo(expected);
  }

  @Test public void simpleWithNestedTypesToString() {
    Field field = new Field.Builder().setLabel(REQUIRED)
        .setType("Type")
        .setName("name")
        .setTag(1)
        .setDocumentation("")
        .setOptions(NO_OPTIONS)
        .build();
    Type nested =
        new MessageType.Builder().setName("Nested")
            .setFullyQualifiedName("")
            .setDocumentation("")
            .setFields(list(field))
            .setNestedTypes(NO_TYPES)
            .setExtensions(NO_EXTENSIONS)
            .setOptions(NO_OPTIONS)
            .build();
    Type type =
        new MessageType.Builder().setName("Message")
            .setFullyQualifiedName("")
            .setDocumentation("")
            .setFields(list(field))
            .setNestedTypes(list(nested))
            .setExtensions(NO_EXTENSIONS)
            .setOptions(NO_OPTIONS)
            .build();
    String expected = ""
        + "message Message {\n"
        + "  required Type name = 1;\n"
        + "\n"
        + "  message Nested {\n"
        + "    required Type name = 1;\n"
        + "  }\n"
        + "}\n";
    assertThat(type.toString()).isEqualTo(expected);
  }

  @Test public void simpleWithExtensionsToString() {
    Field field = new Field.Builder().setLabel(REQUIRED)
        .setType("Type")
        .setName("name")
        .setTag(1)
        .setDocumentation("")
        .setOptions(NO_OPTIONS)
        .build();
    Extensions extensions = new Extensions("", 500, 501);
    Type type =
        new MessageType.Builder().setName("Message")
            .setFullyQualifiedName("")
            .setDocumentation("")
            .setFields(list(field))
            .setNestedTypes(NO_TYPES)
            .setExtensions(list(extensions))
            .setOptions(NO_OPTIONS)
            .build();
    String expected = ""
        + "message Message {\n"
        + "  required Type name = 1;\n"
        + "\n"
        + "  extensions 500 to 501;\n"
        + "}\n";
    assertThat(type.toString()).isEqualTo(expected);
  }

  @Test public void multipleEverythingToString() {
    Field field1 = new Field.Builder().setLabel(REQUIRED)
        .setType("Type")
        .setName("name")
        .setTag(1)
        .setDocumentation("")
        .setOptions(NO_OPTIONS)
        .build();
    Field field2 = new Field.Builder().setLabel(REQUIRED)
        .setType("OtherType")
        .setName("other_name")
        .setTag(2)
        .setDocumentation("")
        .setOptions(NO_OPTIONS)
        .build();
    Extensions extensions1 = new Extensions("", 500, 501);
    Extensions extensions2 = new Extensions("", 503, 503);
    Type nested =
        new MessageType.Builder().setName("Nested")
            .setFullyQualifiedName("")
            .setDocumentation("")
            .setFields(list(field1))
            .setNestedTypes(NO_TYPES)
            .setExtensions(NO_EXTENSIONS)
            .setOptions(NO_OPTIONS)
            .build();
    Option option = new Option("kit", "kat");
    Type type = new MessageType.Builder().setName("Message")
        .setFullyQualifiedName("")
        .setDocumentation("")
        .setFields(list(field1, field2))
        .setNestedTypes(list(nested))
        .setExtensions(list(extensions1, extensions2))
        .setOptions(list(option))
        .build();
    String expected = ""
        + "message Message {\n"
        + "  option kit = \"kat\";\n"
        + "\n"
        + "  required Type name = 1;\n"
        + "  required OtherType other_name = 2;\n"
        + "\n"
        + "  extensions 500 to 501;\n"
        + "  extensions 503;\n"
        + "\n"
        + "  message Nested {\n"
        + "    required Type name = 1;\n"
        + "  }\n"
        + "}\n";
    assertThat(type.toString()).isEqualTo(expected);
  }

  @Test public void fieldToString() {
    Field field = new Field.Builder().setLabel(REQUIRED)
        .setType("Type")
        .setName("name")
        .setTag(1)
        .setDocumentation("")
        .setOptions(NO_OPTIONS)
        .build();
    String expected = "required Type name = 1;\n";
    assertThat(field.toString()).isEqualTo(expected);
  }

  @Test public void fieldWithDocumentationToString() {
    Field field = new Field.Builder().setLabel(REQUIRED)
        .setType("Type")
        .setName("name")
        .setTag(1)
        .setDocumentation("Hello")
        .setOptions(NO_OPTIONS)
        .build();
    String expected = ""
        + "// Hello\n"
        + "required Type name = 1;\n";
    assertThat(field.toString()).isEqualTo(expected);
  }

  @Test public void fieldWithOptions() {
    Field field = new Field.Builder().setLabel(REQUIRED)
        .setType("Type")
        .setName("name")
        .setTag(1)
        .setDocumentation("")
        .setOptions(list(new Option("kit", "kat")))
        .build();
    String expected = "required Type name = 1 [\n"
        + "  kit = \"kat\"\n"
        + "];\n";
    assertThat(field.toString()).isEqualTo(expected);
  }

  @Test public void duplicateTagValueThrows() {
    Field field1 = new Field.Builder().setLabel(REQUIRED)
        .setType("Type")
        .setName("name1")
        .setTag(1)
        .setDocumentation("")
        .setOptions(NO_OPTIONS)
        .build();
    Field field2 = new Field.Builder().setLabel(REQUIRED)
        .setType("Type")
        .setName("name2")
        .setTag(1)
        .setDocumentation("")
        .setOptions(NO_OPTIONS)
        .build();
    try {
      new MessageType.Builder().setName("Message")
          .setFullyQualifiedName("example.Message")
          .setDocumentation("")
          .setFields(list(field1, field2))
          .setNestedTypes(NO_TYPES)
          .setExtensions(NO_EXTENSIONS)
          .setOptions(NO_OPTIONS)
          .build();
      fail("Duplicate tag values are not allowed.");
    } catch (IllegalStateException e) {
      assertThat(e).hasMessage("Duplicate tag 1 in example.Message");
    }
  }

  @Test public void duplicateEnumValueTagInScopeThrows() {
    Value value = new Value("VALUE", 1, "", NO_OPTIONS);
    Type enum1 = new EnumType.Builder().setName("Enum1")
        .setFullyQualifiedName("example.Enum1")
        .setDocumentation("")
        .setOptions(NO_OPTIONS)
        .setValues(list(value))
        .build();
    Type enum2 = new EnumType.Builder().setName("Enum2")
        .setFullyQualifiedName("example.Enum2")
        .setDocumentation("")
        .setOptions(NO_OPTIONS)
        .setValues(list(value))
        .build();
    try {
      new MessageType.Builder().setName("Message")
          .setFullyQualifiedName("example.Message")
          .setDocumentation("")
          .setFields(NO_FIELDS)
          .setNestedTypes(list(enum1, enum2))
          .setExtensions(NO_EXTENSIONS)
          .setOptions(NO_OPTIONS)
          .build();
      fail("Duplicate name not allowed.");
    } catch (IllegalStateException e) {
      assertThat(e).hasMessage("Duplicate enum name VALUE in scope example.Message");
    }
  }

  @Test public void deprecatedTrue() {
    Field field =
        new Field.Builder().setLabel(REQUIRED)
            .setType("Type")
            .setName("name1")
            .setTag(1)
            .setDocumentation("")
            .setOptions(list(new Option("deprecated", "true")))
            .build();
    assertThat(field.isDeprecated()).isTrue();
  }

  @Test public void deprecatedFalse() {
    Field field =
        new Field.Builder().setLabel(REQUIRED)
            .setType("Type")
            .setName("name1")
            .setTag(1)
            .setDocumentation("")
            .setOptions(list(new Option("deprecated", "false")))
            .build();
    assertThat(field.isDeprecated()).isFalse();
  }

  @Test public void deprecatedMissing() {
    Field field = new Field.Builder().setLabel(REQUIRED)
        .setType("Type")
        .setName("name1")
        .setTag(1)
        .setDocumentation("")
        .setOptions(NO_OPTIONS)
        .build();
    assertThat(field.isDeprecated()).isFalse();
  }

  @Test public void packedTrue() {
    Field field = new Field.Builder().setLabel(REQUIRED)
        .setType("Type")
        .setName("name1")
        .setTag(1)
        .setDocumentation("")
        .setOptions(list(new Option("packed", "true")))
        .build();
    assertThat(field.isPacked()).isTrue();
  }

  @Test public void packedFalse() {
    Field field = new Field.Builder().setLabel(REQUIRED)
        .setType("Type")
        .setName("name1")
        .setTag(1)
        .setDocumentation("")
        .setOptions(list(new Option("packed", "false")))
        .build();
    assertThat(field.isPacked()).isFalse();
  }

  @Test public void packedMissing() {
    Field field = new Field.Builder().setLabel(REQUIRED)
        .setType("Type")
        .setName("name1")
        .setTag(1)
        .setDocumentation("")
        .setOptions(NO_OPTIONS)
        .build();
    assertThat(field.isPacked()).isFalse();
  }

  @Test public void defaultValue() {
    Field field = new Field.Builder().setLabel(REQUIRED)
        .setType("Type")
        .setName("name1")
        .setTag(1)
        .setDocumentation("")
        .setOptions(list(new Option("default", "foo")))
        .build();
    assertThat(field.getDefault()).isEqualTo("foo");
  }

  @Test public void defaultMissing() {
    Field field = new Field.Builder().setLabel(REQUIRED)
        .setType("Type")
        .setName("name1")
        .setTag(1)
        .setDocumentation("")
        .setOptions(NO_OPTIONS)
        .build();
    assertThat(field.getDefault()).isNull();
  }
}

package com.squareup.protoparser;

import org.junit.Test;

import static com.squareup.protoparser.MessageType.Field;
import static com.squareup.protoparser.MessageType.Label.REQUIRED;
import static com.squareup.protoparser.TestUtils.NO_FIELDS;
import static com.squareup.protoparser.TestUtils.NO_OPTIONS;
import static com.squareup.protoparser.TestUtils.list;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;

public class ExtendDeclarationTest {
  @Test public void emptyToString() {
    ExtendDeclaration extend = new ExtendDeclaration.Builder().setName("Name")
        .setFqname("Name")
        .setDocumentation("")
        .setFields(NO_FIELDS)
        .build();
    String expected = "extend Name {}\n";
    assertThat(extend.toString()).isEqualTo(expected);
  }

  @Test public void simpleToString() {
    Field field = new Field.Builder().setLabel(REQUIRED)
        .setType("Type")
        .setName("name")
        .setTag(1)
        .setDocumentation("")
        .setOptions(NO_OPTIONS)
        .build();
    ExtendDeclaration extend = new ExtendDeclaration.Builder().setName("Name")
        .setFqname("Name")
        .setDocumentation("")
        .setFields(list(field))
        .build();
    String expected = ""
        + "extend Name {\n"
        + "  required Type name = 1;\n"
        + "}\n";
    assertThat(extend.toString()).isEqualTo(expected);
  }

  @Test public void simpleWithDocumentationToString() {
    Field field = new Field.Builder().setLabel(REQUIRED)
        .setType("Type")
        .setName("name")
        .setTag(1)
        .setDocumentation("")
        .setOptions(NO_OPTIONS)
        .build();
    ExtendDeclaration extend = new ExtendDeclaration.Builder().setName("Name")
        .setFqname("Name")
        .setDocumentation("Hello")
        .setFields(list(field))
        .build();
    String expected = ""
        + "// Hello\n"
        + "extend Name {\n"
        + "  required Type name = 1;\n"
        + "}\n";
    assertThat(extend.toString()).isEqualTo(expected);
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
      new ExtendDeclaration.Builder().setName("Extend")
          .setFqname("example.Extend")
          .setDocumentation("")
          .setFields(list(field1, field2))
          .build();
      fail("Duplicate tag values are not allowed.");
    } catch (IllegalStateException e) {
      assertThat(e).hasMessage("Duplicate tag 1 in example.Extend");
    }
  }
}

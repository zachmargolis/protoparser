package com.squareup.protoparser;

import org.junit.Test;

import static com.squareup.protoparser.ProtoFile.MAX_TAG_VALUE;
import static com.squareup.protoparser.ProtoFile.MIN_TAG_VALUE;
import static com.squareup.protoparser.ProtoFile.isValidTag;
import static com.squareup.protoparser.TestUtils.NO_EXTEND_DECLARATIONS;
import static com.squareup.protoparser.TestUtils.NO_EXTENSIONS;
import static com.squareup.protoparser.TestUtils.NO_FIELDS;
import static com.squareup.protoparser.TestUtils.NO_METHODS;
import static com.squareup.protoparser.TestUtils.NO_OPTIONS;
import static com.squareup.protoparser.TestUtils.NO_SERVICES;
import static com.squareup.protoparser.TestUtils.NO_STRINGS;
import static com.squareup.protoparser.TestUtils.NO_TYPES;
import static com.squareup.protoparser.TestUtils.list;
import static org.fest.assertions.api.Assertions.assertThat;

public class ProtoFileTest {
  @Test public void tagValueValidation() {
    assertThat(isValidTag(MIN_TAG_VALUE - 1)).isFalse(); // Less than minimum.
    assertThat(isValidTag(MIN_TAG_VALUE)).isTrue();
    assertThat(isValidTag(1234)).isTrue();
    assertThat(isValidTag(19222)).isFalse(); // Reserved range.
    assertThat(isValidTag(2319573)).isTrue();
    assertThat(isValidTag(MAX_TAG_VALUE)).isTrue();
    assertThat(isValidTag(MAX_TAG_VALUE + 1)).isFalse(); // Greater than maximum.
  }

  @Test public void emptyToString() {
    ProtoFile file =
        new ProtoFile.Builder().setFileName("file.proto")
            .setPackageName(null)
            .setDependencies(NO_STRINGS)
            .setPublicDependencies(NO_STRINGS)
            .setTypes(NO_TYPES)
            .setServices(NO_SERVICES)
            .setOptions(NO_OPTIONS)
            .setExtendDeclarations(NO_EXTEND_DECLARATIONS)
            .build();
    String expected = "// file.proto\n";
    assertThat(file.toString()).isEqualTo(expected);
  }

  @Test public void emptyWithPackageToString() {
    ProtoFile file =
        new ProtoFile.Builder().setFileName("file.proto")
            .setPackageName("example.simple")
            .setDependencies(NO_STRINGS)
            .setPublicDependencies(NO_STRINGS)
            .setTypes(NO_TYPES)
            .setServices(NO_SERVICES)
            .setOptions(NO_OPTIONS)
            .setExtendDeclarations(NO_EXTEND_DECLARATIONS)
            .build();
    String expected = ""
        + "// file.proto\n"
        + "package example.simple;\n";
    assertThat(file.toString()).isEqualTo(expected);
  }

  @Test public void simpleToString() {
    Type type = new MessageType.Builder().setName("Message")
        .setFullyQualifiedName("")
        .setDocumentation("")
        .setFields(NO_FIELDS)
        .setNestedTypes(NO_TYPES)
        .setExtensions(NO_EXTENSIONS)
        .setOptions(NO_OPTIONS)
        .build();
    ProtoFile file =
        new ProtoFile.Builder().setFileName("file.proto")
            .setPackageName(null)
            .setDependencies(NO_STRINGS)
            .setPublicDependencies(NO_STRINGS)
            .setTypes(list(type))
            .setServices(NO_SERVICES)
            .setOptions(NO_OPTIONS)
            .setExtendDeclarations(NO_EXTEND_DECLARATIONS)
            .build();
    String expected = ""
        + "// file.proto\n"
        + "\n"
        + "message Message {}\n";
    assertThat(file.toString()).isEqualTo(expected);
  }

  @Test public void simpleWithImportsToString() {
    Type type = new MessageType.Builder().setName("Message")
        .setFullyQualifiedName("")
        .setDocumentation("")
        .setFields(NO_FIELDS)
        .setNestedTypes(NO_TYPES)
        .setExtensions(NO_EXTENSIONS)
        .setOptions(NO_OPTIONS)
        .build();
    ProtoFile file =
        new ProtoFile.Builder().setFileName("file.proto")
            .setPackageName(null)
            .setDependencies(list("example.other"))
            .setPublicDependencies(NO_STRINGS)
            .setTypes(list(type))
            .setServices(NO_SERVICES)
            .setOptions(NO_OPTIONS)
            .setExtendDeclarations(NO_EXTEND_DECLARATIONS)
            .build();
    String expected = ""
        + "// file.proto\n"
        + "\n"
        + "import \"example.other\";\n"
        + "\n"
        + "message Message {}\n";
    assertThat(file.toString()).isEqualTo(expected);
  }

  @Test public void simpleWithPublicImportsToString() {
    Type type = new MessageType.Builder().setName("Message")
        .setFullyQualifiedName("")
        .setDocumentation("")
        .setFields(NO_FIELDS)
        .setNestedTypes(NO_TYPES)
        .setExtensions(NO_EXTENSIONS)
        .setOptions(NO_OPTIONS)
        .build();
    ProtoFile file =
        new ProtoFile.Builder().setFileName("file.proto")
            .setPackageName(null)
            .setDependencies(NO_STRINGS)
            .setPublicDependencies(list("example.other"))
            .setTypes(list(type))
            .setServices(NO_SERVICES)
            .setOptions(NO_OPTIONS)
            .setExtendDeclarations(NO_EXTEND_DECLARATIONS)
            .build();
    String expected = ""
        + "// file.proto\n"
        + "\n"
        + "import public \"example.other\";\n"
        + "\n"
        + "message Message {}\n";
    assertThat(file.toString()).isEqualTo(expected);
  }

  @Test public void simpleWithBothImportsToString() {
    Type type = new MessageType.Builder().setName("Message")
        .setFullyQualifiedName("")
        .setDocumentation("")
        .setFields(NO_FIELDS)
        .setNestedTypes(NO_TYPES)
        .setExtensions(NO_EXTENSIONS)
        .setOptions(NO_OPTIONS)
        .build();
    ProtoFile file =
        new ProtoFile.Builder().setFileName("file.proto")
            .setPackageName(null)
            .setDependencies(list("example.thing"))
            .setPublicDependencies(list("example.other"))
            .setTypes(list(type))
            .setServices(NO_SERVICES)
            .setOptions(NO_OPTIONS)
            .setExtendDeclarations(NO_EXTEND_DECLARATIONS)
            .build();
    String expected = ""
        + "// file.proto\n"
        + "\n"
        + "import \"example.thing\";\n"
        + "import public \"example.other\";\n"
        + "\n"
        + "message Message {}\n";
    assertThat(file.toString()).isEqualTo(expected);
  }

  @Test public void simpleWithServicesToString() {
    Type type = new MessageType.Builder().setName("Message")
        .setFullyQualifiedName("")
        .setDocumentation("")
        .setFields(NO_FIELDS)
        .setNestedTypes(NO_TYPES)
        .setExtensions(NO_EXTENSIONS)
        .setOptions(NO_OPTIONS)
        .build();
    Service service = new Service.Builder().setName("Service")
        .setFullyQualifiedName("")
        .setDocumentation("")
        .setOptions(NO_OPTIONS)
        .setMethods(NO_METHODS)
        .build();
    ProtoFile file =
        new ProtoFile.Builder().setFileName("file.proto")
            .setPackageName(null)
            .setDependencies(NO_STRINGS)
            .setPublicDependencies(NO_STRINGS)
            .setTypes(list(type))
            .setServices(list(service))
            .setOptions(NO_OPTIONS)
            .setExtendDeclarations(NO_EXTEND_DECLARATIONS)
            .build();
    String expected = ""
        + "// file.proto\n"
        + "\n"
        + "message Message {}\n"
        + "\n"
        + "service Service {}\n";
    assertThat(file.toString()).isEqualTo(expected);
  }

  @Test public void simpleWithOptionsToString() {
    Type type = new MessageType.Builder().setName("Message")
        .setFullyQualifiedName("")
        .setDocumentation("")
        .setFields(NO_FIELDS)
        .setNestedTypes(NO_TYPES)
        .setExtensions(NO_EXTENSIONS)
        .setOptions(NO_OPTIONS)
        .build();
    Option option = new Option("kit", "kat", Option.Source.BUILTIN);
    ProtoFile file =
        new ProtoFile.Builder().setFileName("file.proto")
            .setPackageName(null)
            .setDependencies(NO_STRINGS)
            .setPublicDependencies(NO_STRINGS)
            .setTypes(list(type))
            .setServices(NO_SERVICES)
            .setOptions(list(option))
            .setExtendDeclarations(NO_EXTEND_DECLARATIONS)
            .build();
    String expected = ""
        + "// file.proto\n"
        + "\n"
        + "option kit = \"kat\";\n"
        + "\n"
        + "message Message {}\n";
    assertThat(file.toString()).isEqualTo(expected);
  }

  @Test public void simpleWithExtendsToString() {
    Type type = new MessageType.Builder().setName("Message")
        .setFullyQualifiedName("")
        .setDocumentation("")
        .setFields(NO_FIELDS)
        .setNestedTypes(NO_TYPES)
        .setExtensions(NO_EXTENSIONS)
        .setOptions(NO_OPTIONS)
        .build();
    ExtendDeclaration extend = new ExtendDeclaration.Builder().setName("Extend")
        .setFullyQualifiedName("Extend")
        .setDocumentation("")
        .setFields(NO_FIELDS)
        .build();
    ProtoFile file =
        new ProtoFile.Builder().setFileName("file.proto")
            .setPackageName(null)
            .setDependencies(NO_STRINGS)
            .setPublicDependencies(NO_STRINGS)
            .setTypes(list(type))
            .setServices(NO_SERVICES)
            .setOptions(NO_OPTIONS)
            .setExtendDeclarations(list(extend))
            .build();
    String expected = ""
        + "// file.proto\n"
        + "\n"
        + "message Message {}\n"
        + "\n"
        + "extend Extend {}\n";
    assertThat(file.toString()).isEqualTo(expected);
  }

  @Test public void multipleEverythingToString() {
    Type type1 = new MessageType.Builder().setName("Message1")
        .setFullyQualifiedName("example.simple.Message1")
        .setDocumentation("")
        .setFields(NO_FIELDS)
        .setNestedTypes(NO_TYPES)
        .setExtensions(NO_EXTENSIONS)
        .setOptions(NO_OPTIONS)
        .build();
    Type type2 = new MessageType.Builder().setName("Message2")
        .setFullyQualifiedName("example.simple.Message2")
        .setDocumentation("")
        .setFields(NO_FIELDS)
        .setNestedTypes(NO_TYPES)
        .setExtensions(NO_EXTENSIONS)
        .setOptions(NO_OPTIONS)
        .build();
    ExtendDeclaration extend1 =
        new ExtendDeclaration.Builder().setName("Extend1")
            .setFullyQualifiedName("example.simple.Extend1")
            .setDocumentation("")
            .setFields(NO_FIELDS)
            .build();
    ExtendDeclaration extend2 =
        new ExtendDeclaration.Builder().setName("Extend2")
            .setFullyQualifiedName("example.simple.Extend2")
            .setDocumentation("")
            .setFields(NO_FIELDS)
            .build();
    Option option1 = new Option("kit", "kat", Option.Source.BUILTIN);
    Option option2 = new Option("foo", "bar", Option.Source.BUILTIN);
    Service service1 =
        new Service.Builder().setName("Service1")
            .setFullyQualifiedName("example.simple.Service1")
            .setDocumentation("")
            .setOptions(NO_OPTIONS)
            .setMethods(NO_METHODS)
            .build();
    Service service2 =
        new Service.Builder().setName("Service2")
            .setFullyQualifiedName("example.simple.Service2")
            .setDocumentation("")
            .setOptions(NO_OPTIONS)
            .setMethods(NO_METHODS)
            .build();
    ProtoFile file =
        new ProtoFile.Builder().setFileName("file.proto")
            .setPackageName("example.simple")
            .setDependencies(list("example.thing"))
            .setPublicDependencies(list("example.other"))
            .setTypes(list(type1, type2))
            .setServices(list(service1, service2))
            .setOptions(list(option1, option2))
            .setExtendDeclarations(list(extend1, extend2))
            .build();
    String expected = ""
        + "// file.proto\n"
        + "package example.simple;\n"
        + "\n"
        + "import \"example.thing\";\n"
        + "import public \"example.other\";\n"
        + "\n"
        + "option kit = \"kat\";\n"
        + "option foo = \"bar\";\n"
        + "\n"
        + "message Message1 {}\n"
        + "message Message2 {}\n"
        + "\n"
        + "extend Extend1 {}\n"
        + "extend Extend2 {}\n"
        + "\n"
        + "service Service1 {}\n"
        + "service Service2 {}\n";
    assertThat(file.toString()).isEqualTo(expected);

    // Re-parse the expected string into a ProtoFile and ensure they're equal.
    ProtoFile parsed = ProtoSchemaParser.parse("file.proto", expected);
    assertThat(parsed).isEqualTo(file);
  }
}

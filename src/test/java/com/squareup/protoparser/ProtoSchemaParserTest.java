// Copyright 2013 Square, Inc.
package com.squareup.protoparser;

import com.squareup.protoparser.EnumType.Value;
import com.squareup.protoparser.MessageType.Label;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.fest.assertions.api.Fail;
import org.junit.Test;

import static com.squareup.protoparser.TestUtils.NO_EXTEND_DECLARATIONS;
import static com.squareup.protoparser.TestUtils.NO_EXTENSIONS;
import static com.squareup.protoparser.TestUtils.NO_FIELDS;
import static com.squareup.protoparser.TestUtils.NO_OPTIONS;
import static com.squareup.protoparser.TestUtils.NO_SERVICES;
import static com.squareup.protoparser.TestUtils.NO_STRINGS;
import static com.squareup.protoparser.TestUtils.NO_TYPES;
import static com.squareup.protoparser.TestUtils.list;
import static com.squareup.protoparser.TestUtils.map;
import static org.fest.assertions.api.Assertions.assertThat;

public final class ProtoSchemaParserTest {
  @Test public void field() throws Exception {
    MessageType.Field field = new MessageType.Field.Builder().setLabel(Label.OPTIONAL)
        .setType("CType")
        .setName("ctype")
        .setTag(1)
        .setDocumentation("")
        .setOptions(list(new Option("default", "STRING"), new Option("deprecated", "true")))
        .build();
    assertThat(field.isDeprecated()).isTrue();
    assertThat(field.getDefault()).isEqualTo("STRING");
    assertThat(field.getOptions()).containsOnly( //
        new Option("default", "STRING"), //
        new Option("deprecated", "true"));
  }

  @Test public void documentationFormats() {
    // Single-line comment.
    String proto1 = ""
        + "// Test all the things!\n"
        + "message Test {}";
    ProtoFile parsed1 = ProtoSchemaParser.parse("test.proto", proto1);
    MessageType type1 = (MessageType) parsed1.getTypes().get(0);
    assertThat(type1.getDocumentation()).isEqualTo("Test all the things!");

    // Multiple, single-line comment.
    String proto2 = ""
        + "// Test all\n"
        + "// the things!\n"
        + "message Test {}";
    String expected2 = ""
        + "Test all\n"
        + "the things!";
    ProtoFile parsed2 = ProtoSchemaParser.parse("test.proto", proto2);
    MessageType type2 = (MessageType) parsed2.getTypes().get(0);
    assertThat(type2.getDocumentation()).isEqualTo(expected2);

    // Single-line, Javadoc-esque comment.
    String proto3 = ""
        + "/** Test */\n"
        + "message Test {}";
    ProtoFile parsed3 = ProtoSchemaParser.parse("test.proto", proto3);
    MessageType type3 = (MessageType) parsed3.getTypes().get(0);
    assertThat(type3.getDocumentation()).isEqualTo("Test");

    // Multi-line, Javadoc-esque comment.
    String proto4 = ""
        + "/**\n"
        + " * Test\n"
        + " *\n"
        + " * Foo\n"
        + " */\n"
        + "message Test {}";
    String expected4 = ""
        + "Test\n"
        + "\n"
        + "Foo";
    ProtoFile parsed4 = ProtoSchemaParser.parse("test.proto", proto4);
    MessageType type4 = (MessageType) parsed4.getTypes().get(0);
    assertThat(type4.getDocumentation()).isEqualTo(expected4);

    // Multiple, single-line comment with leading whitespace
    String proto5 = ""
        + "// Test\n"
        + "//   All\n"
        + "//     The\n"
        + "//       Things!\n"
        + "message Test {}";
    String expected5 = ""
        + "Test\n"
        + "  All\n"
        + "    The\n"
        + "      Things!";
    ProtoFile parsed5 = ProtoSchemaParser.parse("test.proto", proto5);
    MessageType type5 = (MessageType) parsed5.getTypes().get(0);
    assertThat(type5.getDocumentation()).isEqualTo(expected5);

    // Multi-line, Javadoc-esque comment.
    String proto6 = ""
        + "/**\n"
        + " * Test\n"
        + " *   All\n"
        + " *     The\n"
        + " *       Things!\n"
        + " */\n"
        + "message Test {}";
    String expected6 = ""
        + "Test\n"
        + "  All\n"
        + "    The\n"
        + "      Things!";
    ProtoFile parsed6 = ProtoSchemaParser.parse("test.proto", proto6);
    MessageType type6 = (MessageType) parsed6.getTypes().get(0);
    assertThat(type6.getDocumentation()).isEqualTo(expected6);

    // Multi-line, poorly-formatted Javadoc-esque comment. The lack of leading asterisks prevents
    // us from preserving any leading whitespace.
    String proto7 = ""
        + "/**\n"
        + " Test\n"
        + "   All\n"
        + "     The\n"
        + "       Things!\n"
        + " */\n"
        + "message Test {}";
    String expected7 = ""
        + "Test\n"
        + "All\n"
        + "The\n"
        + "Things!";
    ProtoFile parsed7 = ProtoSchemaParser.parse("test.proto", proto7);
    MessageType type7 = (MessageType) parsed7.getTypes().get(0);
    assertThat(type7.getDocumentation()).isEqualTo(expected7);
  }

  @Test public void parseMessageAndFields() throws Exception {
    String proto = ""
        + "message SearchRequest {\n"
        + "  required string query = 1;\n"
        + "  optional int32 page_number = 2;\n"
        + "  optional int32 result_per_page = 3;\n"
        + "}";
    Type expected = new MessageType.Builder().setName("SearchRequest")
        .setFullyQualifiedName("SearchRequest")
        .setDocumentation("")
        .setFields(Arrays.asList(
            new MessageType.Field.Builder().setLabel(Label.REQUIRED)
                .setType("string")
                .setName("query")
                .setTag(1)
                .setDocumentation("")
                .setOptions(NO_OPTIONS)
                .build(),
            new MessageType.Field.Builder().setLabel(Label.OPTIONAL)
                .setType("int32")
                .setName("page_number")
                .setTag(2)
                .setDocumentation("")
                .setOptions(NO_OPTIONS)
                .build(),
            new MessageType.Field.Builder().setLabel(Label.OPTIONAL)
                .setType("int32")
                .setName("result_per_page")
                .setTag(3)
                .setDocumentation("")
                .setOptions(NO_OPTIONS)
                .build()))
        .setNestedTypes(NO_TYPES)
        .setExtensions(NO_EXTENSIONS)
        .setOptions(NO_OPTIONS)
        .build();
    ProtoFile protoFile =
        new ProtoFile.Builder().setFileName("search.proto")
            .setPackageName(null)
            .setDependencies(NO_STRINGS)
            .setPublicDependencies(NO_STRINGS)
            .setTypes(Arrays.asList(expected))
            .setServices(NO_SERVICES)
            .setOptions(NO_OPTIONS)
            .setExtendDeclarations(NO_EXTEND_DECLARATIONS)
            .build();
    assertThat(ProtoSchemaParser.parse("search.proto", new StringReader(proto))).isEqualTo(protoFile);
  }

  @Test public void parseEnum() throws Exception {
    String proto = ""
        + "/**\n"
        + " * What's on my waffles.\n"
        + " * Also works on pancakes.\n"
        + " */\n"
        + "enum Topping {\n"
        + "  FRUIT = 1;\n"
        + "  /** Yummy, yummy cream. */\n"
        + "  CREAM = 2;\n"
        + "\n"
        + "  // Quebec Maple syrup\n"
        + "  SYRUP = 3;\n"
        + "}\n";
    Type expected = new EnumType.Builder().setName("Topping")
        .setFullyQualifiedName("Topping")
        .setDocumentation("What's on my waffles.\nAlso works on pancakes.")
        .setOptions(NO_OPTIONS)
        .setValues(Arrays.asList(new Value("FRUIT", 1, "", NO_OPTIONS),
            new Value("CREAM", 2, "Yummy, yummy cream.",
                NO_OPTIONS), new Value("SYRUP", 3, "Quebec Maple syrup", NO_OPTIONS)))
        .build();
    ProtoFile protoFile =
        new ProtoFile.Builder().setFileName("waffles.proto")
            .setPackageName(null)
            .setDependencies(NO_STRINGS)
            .setPublicDependencies(NO_STRINGS)
            .setTypes(Arrays.asList(expected))
            .setServices(NO_SERVICES)
            .setOptions(NO_OPTIONS)
            .setExtendDeclarations(NO_EXTEND_DECLARATIONS)
            .build();
    ProtoFile actual = ProtoSchemaParser.parse("waffles.proto", proto);
    assertThat(actual).isEqualTo(protoFile);
  }

  @Test public void parseEnumWithOptions() throws Exception {
    String proto = ""
        + "/**\n"
        + " * What's on my waffles.\n"
        + " * Also works on pancakes.\n"
        + " */\n"
        + "enum Topping {\n"
        + "  option (max_choices) = 2;\n"
        + "\n"
        + "  FRUIT = 1 [(healthy) = true, (legit_level) = TOO_LEGIT];\n"
        + "  /** Yummy, yummy cream. */\n"
        + "  CREAM = 2;\n"
        + "\n"
        + "  // Quebec Maple syrup\n"
        + "  SYRUP = 3;\n"
        + "}\n";
    List<Option> fruitOptions = list(
        new Option("healthy", true),
        new Option("legit_level", EnumType.Value.anonymous("TOO_LEGIT")));
    List<Option> toppingOptions = list(new Option("max_choices", 2));
    Type expected = new EnumType.Builder().setName("Topping")
        .setFullyQualifiedName("Topping")
        .setDocumentation("What's on my waffles.\nAlso works on pancakes.")
        .setOptions(toppingOptions)
        .setValues(list(new Value("FRUIT", 1, "", fruitOptions),
            new Value("CREAM", 2, "Yummy, yummy cream.", NO_OPTIONS),
            new Value("SYRUP", 3, "Quebec Maple syrup", NO_OPTIONS)))
        .build();
    ProtoFile protoFile =
        new ProtoFile.Builder().setFileName("waffles.proto")
            .setPackageName(null)
            .setDependencies(NO_STRINGS)
            .setPublicDependencies(NO_STRINGS)
            .setTypes(Arrays.asList(expected))
            .setServices(NO_SERVICES)
            .setOptions(NO_OPTIONS)
            .setExtendDeclarations(NO_EXTEND_DECLARATIONS)
            .build();
    ProtoFile actual = ProtoSchemaParser.parse("waffles.proto", proto);
    assertThat(actual).isEqualTo(protoFile);
  }

  @Test public void packageDeclaration() throws Exception {
    String proto = ""
        + "package google.protobuf;\n"
        + "option java_package = \"com.google.protobuf\";\n"
        + "\n"
        + "// The protocol compiler can output a FileDescriptorSet containing the .proto\n"
        + "// files it parses.\n"
        + "message FileDescriptorSet {\n"
        + "}\n";
    Type message = new MessageType.Builder().setName("FileDescriptorSet")
        .setFullyQualifiedName("google.protobuf.FileDescriptorSet")
        .setDocumentation(""
            + "The protocol compiler can output a FileDescriptorSet containing the .proto\n"
            + "files it parses.")
        .setFields(Arrays.<MessageType.Field>asList())
        .setNestedTypes(NO_TYPES)
        .setExtensions(NO_EXTENSIONS)
        .setOptions(NO_OPTIONS)
        .build();
    ProtoFile expected =
        new ProtoFile.Builder().setFileName("descriptor.proto")
            .setPackageName("google.protobuf")
            .setDependencies(NO_STRINGS)
            .setPublicDependencies(NO_STRINGS)
            .setTypes(Arrays.asList(message))
            .setServices(NO_SERVICES)
            .setOptions(list(new Option("java_package", "com.google.protobuf")))
            .setExtendDeclarations(NO_EXTEND_DECLARATIONS)
            .build();
    assertThat(ProtoSchemaParser.parse("descriptor.proto", proto))
        .isEqualTo(expected);
  }

  @Test public void nestingInMessage() throws Exception {
    String proto = ""
        + "message FieldOptions {\n"
        + "  optional CType ctype = 1 [default = STRING, deprecated=true];\n"
        + "  enum CType {\n"
        + "    STRING = 0[(opt_a) = 1, (opt_b) = 2];\n"
        + "  };\n"
        + "  // Clients can define custom options in extensions of this message. See above.\n"
        + "  extensions 500;\n"
        + "  extensions 1000 to max;\n"
        + "}\n";
    Type enumType = new EnumType.Builder().setName("CType")
        .setFullyQualifiedName("FieldOptions.CType")
        .setDocumentation("")
        .setOptions(NO_OPTIONS)
        .setValues(Arrays.asList(new Value("STRING", 0, "",
            Arrays.asList(new Option("opt_a", 1), new Option("opt_b", 2)))))
        .build();
    MessageType.Field field = new MessageType.Field.Builder().setLabel(Label.OPTIONAL)
        .setType("CType")
        .setName("ctype")
        .setTag(1)
        .setDocumentation("")
        .setOptions(list(new Option("default", EnumType.Value.anonymous("STRING")),
            new Option("deprecated", true)))
        .build();
    assertThat(field.getOptions()).containsOnly( //
        new Option("default", EnumType.Value.anonymous("STRING")), //
        new Option("deprecated", true));

    Type messageType = new MessageType.Builder().setName("FieldOptions")
        .setFullyQualifiedName("FieldOptions")
        .setDocumentation("")
        .setFields(Arrays.asList(field))
        .setNestedTypes(Arrays.asList(enumType))
        .setExtensions(list(new Extensions(
            "Clients can define custom options in extensions of this message. See above.", 500,
            500),
            new Extensions("", 1000, ProtoFile.MAX_TAG_VALUE)))
        .setOptions(NO_OPTIONS)
        .build();
    ProtoFile expected =
        new ProtoFile.Builder().setFileName("descriptor.proto")
            .setPackageName(null)
            .setDependencies(NO_STRINGS)
            .setPublicDependencies(NO_STRINGS)
            .setTypes(Arrays.asList(messageType))
            .setServices(NO_SERVICES)
            .setOptions(NO_OPTIONS)
            .setExtendDeclarations(NO_EXTEND_DECLARATIONS)
            .build();
    ProtoFile actual = ProtoSchemaParser.parse("descriptor.proto", proto);
    assertThat(actual).isEqualTo(expected);
  }

  @Test public void imports() throws Exception {
    String proto = "import \"src/test/resources/unittest_import.proto\";\n";
    ProtoFile expected = new ProtoFile.Builder().setFileName("descriptor.proto")
        .setPackageName(null)
        .setDependencies(Arrays.asList("src/test/resources/unittest_import.proto"))
        .setPublicDependencies(NO_STRINGS)
        .setTypes(NO_TYPES)
        .setServices(NO_SERVICES)
        .setOptions(NO_OPTIONS)
        .setExtendDeclarations(NO_EXTEND_DECLARATIONS)
        .build();
    assertThat(ProtoSchemaParser.parse("descriptor.proto", proto))
        .isEqualTo(expected);
  }

  @Test public void publicImports() throws Exception {
    String proto = "import public \"src/test/resources/unittest_import.proto\";\n";
    ProtoFile expected = new ProtoFile.Builder().setFileName("descriptor.proto")
        .setPackageName(null)
        .setDependencies(NO_STRINGS)
        .setPublicDependencies(Arrays.asList("src/test/resources/unittest_import.proto"))
        .setTypes(NO_TYPES)
        .setServices(NO_SERVICES)
        .setOptions(NO_OPTIONS)
        .setExtendDeclarations(NO_EXTEND_DECLARATIONS)
        .build();
    assertThat(ProtoSchemaParser.parse("descriptor.proto", proto))
        .isEqualTo(expected);
  }

  @Test public void extend() throws Exception {
    String proto = ""
        + "// Extends Foo\n"
        + "extend Foo {\n"
        + "  optional int32 bar = 126;\n"
        + "}";
    List<ExtendDeclaration> extendDeclarations = new ArrayList<ExtendDeclaration>();
    extendDeclarations.add(new ExtendDeclaration.Builder().setName("Foo")
        .setFullyQualifiedName("Foo")
        .setDocumentation("Extends Foo")
        .setFields(Arrays.asList(new MessageType.Field.Builder().setLabel(Label.OPTIONAL)
            .setType("int32")
            .setName("bar")
            .setTag(126)
            .setDocumentation("")
            .setOptions(NO_OPTIONS)
            .build()))
        .build());
    ProtoFile expected =
        new ProtoFile.Builder().setFileName("descriptor.proto")
            .setPackageName(null)
            .setDependencies(NO_STRINGS)
            .setPublicDependencies(NO_STRINGS)
            .setTypes(NO_TYPES)
            .setServices(NO_SERVICES)
            .setOptions(NO_OPTIONS)
            .setExtendDeclarations(extendDeclarations)
            .build();
    assertThat(ProtoSchemaParser.parse("descriptor.proto", proto))
        .isEqualTo(expected);
  }

  @Test public void extendInMessage() throws Exception {
    String proto = ""
        + "message Bar {\n"
        + "  extend Foo {\n"
        + "    optional Bar bar = 126;\n"
        + "  }\n"
        + "}";
    List<ExtendDeclaration> extendDeclarations = new ArrayList<ExtendDeclaration>();
    extendDeclarations.add(new ExtendDeclaration.Builder().setName("Foo")
        .setFullyQualifiedName("Foo")
        .setDocumentation("")
        .setFields(Arrays.asList(new MessageType.Field.Builder().setLabel(Label.OPTIONAL)
            .setType("Bar")
            .setName("bar")
            .setTag(126)
            .setDocumentation("")
            .setOptions(NO_OPTIONS)
            .build()))
        .build());
    Type messageType =
        new MessageType.Builder().setName("Bar")
            .setFullyQualifiedName("Bar")
            .setDocumentation("")
            .setFields(NO_FIELDS)
            .setNestedTypes(NO_TYPES)
            .setExtensions(NO_EXTENSIONS)
            .setOptions(NO_OPTIONS)
            .build();
    ProtoFile expected =
        new ProtoFile.Builder().setFileName("descriptor.proto")
            .setPackageName(null)
            .setDependencies(NO_STRINGS)
            .setPublicDependencies(NO_STRINGS)
            .setTypes(Arrays.asList(messageType))
            .setServices(NO_SERVICES)
            .setOptions(NO_OPTIONS)
            .setExtendDeclarations(extendDeclarations)
            .build();
    assertThat(ProtoSchemaParser.parse("descriptor.proto", proto))
        .isEqualTo(expected);
  }

  @Test public void extendInMessageWithPackage() throws Exception {
    String proto = ""
        + "package kit.kat;\n"
        + ""
        + "message Bar {\n"
        + "  extend Foo {\n"
        + "    optional Bar bar = 126;\n"
        + "  }\n"
        + "}";
    List<ExtendDeclaration> extendDeclarations = new ArrayList<ExtendDeclaration>();
    extendDeclarations.add(new ExtendDeclaration.Builder().setName("Foo")
        .setFullyQualifiedName("kit.kat.Foo")
        .setDocumentation("")
        .setFields(Arrays.asList(new MessageType.Field.Builder().setLabel(Label.OPTIONAL)
            .setType("Bar")
            .setName("bar")
            .setTag(126)
            .setDocumentation("")
            .setOptions(NO_OPTIONS)
            .build()))
        .build());
    Type messageType =
        new MessageType.Builder().setName("Bar")
            .setFullyQualifiedName("kit.kat.Bar")
            .setDocumentation("")
            .setFields(NO_FIELDS)
            .setNestedTypes(NO_TYPES)
            .setExtensions(NO_EXTENSIONS)
            .setOptions(NO_OPTIONS)
            .build();
    ProtoFile expected =
        new ProtoFile.Builder().setFileName("descriptor.proto")
            .setPackageName("kit.kat")
            .setDependencies(NO_STRINGS)
            .setPublicDependencies(NO_STRINGS)
            .setTypes(Arrays.asList(messageType))
            .setServices(NO_SERVICES)
            .setOptions(NO_OPTIONS)
            .setExtendDeclarations(extendDeclarations)
            .build();
    assertThat(ProtoSchemaParser.parse("descriptor.proto", proto))
        .isEqualTo(expected);
  }

  @Test public void fqcnExtendInMessage() throws Exception {
    String proto = ""
        + "message Bar {\n"
        + "  extend example.Foo {\n"
        + "    optional Bar bar = 126;\n"
        + "  }\n"
        + "}";
    List<ExtendDeclaration> extendDeclarations = new ArrayList<ExtendDeclaration>();
    extendDeclarations.add(new ExtendDeclaration.Builder().setName("example.Foo")
        .setFullyQualifiedName("example.Foo")
        .setDocumentation("")
        .setFields(Arrays.asList(new MessageType.Field.Builder().setLabel(Label.OPTIONAL)
            .setType("Bar")
            .setName("bar")
            .setTag(126)
            .setDocumentation("")
            .setOptions(NO_OPTIONS)
            .build()))
        .build());
    Type messageType =
        new MessageType.Builder().setName("Bar")
            .setFullyQualifiedName("Bar")
            .setDocumentation("")
            .setFields(NO_FIELDS)
            .setNestedTypes(NO_TYPES)
            .setExtensions(NO_EXTENSIONS)
            .setOptions(NO_OPTIONS)
            .build();
    ProtoFile expected =
        new ProtoFile.Builder().setFileName("descriptor.proto")
            .setPackageName(null)
            .setDependencies(NO_STRINGS)
            .setPublicDependencies(NO_STRINGS)
            .setTypes(Arrays.asList(messageType))
            .setServices(NO_SERVICES)
            .setOptions(NO_OPTIONS)
            .setExtendDeclarations(extendDeclarations)
            .build();
    assertThat(ProtoSchemaParser.parse("descriptor.proto", proto))
        .isEqualTo(expected);
  }

  @Test public void fqcnExtendInMessageWithPackage() throws Exception {
    String proto = ""
        + "package kit.kat;\n"
        + ""
        + "message Bar {\n"
        + "  extend example.Foo {\n"
        + "    optional Bar bar = 126;\n"
        + "  }\n"
        + "}";
    List<ExtendDeclaration> extendDeclarations = new ArrayList<ExtendDeclaration>();
    extendDeclarations.add(new ExtendDeclaration.Builder().setName("example.Foo")
        .setFullyQualifiedName("example.Foo")
        .setDocumentation("")
        .setFields(Arrays.asList(new MessageType.Field.Builder().setLabel(Label.OPTIONAL)
            .setType("Bar")
            .setName("bar")
            .setTag(126)
            .setDocumentation("")
            .setOptions(NO_OPTIONS)
            .build()))
        .build());
    Type messageType =
        new MessageType.Builder().setName("Bar")
            .setFullyQualifiedName("kit.kat.Bar")
            .setDocumentation("")
            .setFields(NO_FIELDS)
            .setNestedTypes(NO_TYPES)
            .setExtensions(NO_EXTENSIONS)
            .setOptions(NO_OPTIONS)
            .build();
    ProtoFile expected =
        new ProtoFile.Builder().setFileName("descriptor.proto")
            .setPackageName("kit.kat")
            .setDependencies(NO_STRINGS)
            .setPublicDependencies(NO_STRINGS)
            .setTypes(Arrays.asList(messageType))
            .setServices(NO_SERVICES)
            .setOptions(NO_OPTIONS)
            .setExtendDeclarations(extendDeclarations)
            .build();
    assertThat(ProtoSchemaParser.parse("descriptor.proto", proto))
        .isEqualTo(expected);
  }

  @Test public void defaultFieldWithParen() throws Exception {
    String proto = ""
        + "message Foo {\n"
        + "  optional string claim_token = 2 [(squareup.redacted) = true];\n"
        + "}";
    MessageType.Field field = new MessageType.Field.Builder().setLabel(Label.OPTIONAL)
        .setType("string")
        .setName("claim_token")
        .setTag(2)
        .setDocumentation("")
        .setOptions(list(new Option("squareup.redacted", true)))
        .build();
    assertThat(field.getOptions()).containsOnly(new Option("squareup.redacted", true));

    Type messageType = new MessageType.Builder().setName("Foo")
        .setFullyQualifiedName("Foo")
        .setDocumentation("")
        .setFields(Arrays.asList(field))
        .setNestedTypes(NO_TYPES)
        .setExtensions(NO_EXTENSIONS)
        .setOptions(NO_OPTIONS)
        .build();
    ProtoFile expected = new ProtoFile.Builder().setFileName("descriptor.proto")
        .setPackageName(null)
        .setDependencies(NO_STRINGS)
        .setPublicDependencies(NO_STRINGS)
        .setTypes(Arrays.<Type>asList(messageType))
        .setServices(NO_SERVICES)
        .setOptions(NO_OPTIONS)
        .setExtendDeclarations(NO_EXTEND_DECLARATIONS)
        .build();
    assertThat(ProtoSchemaParser.parse("descriptor.proto", proto))
        .isEqualTo(expected);
  }

  // Parse \a, \b, \f, \n, \r, \t, \v, \[0-7]{1-3}, and \[xX]{0-9a-fA-F]{1,2}
  @Test public void defaultFieldWithStringEscapes() throws Exception {
    String proto = ""
        + "message Foo {\n"
        + "  optional string name = 1 "
        + "[default = \"\\a\\b\\f\\n\\r\\t\\v\1f\01\001\11\011\111\\xe\\Xe\\xE\\xE\\x41\\X41\"];\n"
        + "}";
    MessageType.Field field = new MessageType.Field.Builder().setLabel(Label.OPTIONAL)
        .setType("string")
        .setName("name")
        .setTag(1)
        .setDocumentation("")
        .setOptions(list(
            new Option("default",
                "\u0007\b\f\n\r\t\u000b\u0001f\u0001\u0001\u0009\u0009I\u000e\u000e\u000e\u000eAA")))
        .build();
    assertThat(field.getOptions()).containsOnly(new Option("default",
        "\u0007\b\f\n\r\t\u000b\u0001f\u0001\u0001\u0009\u0009I\u000e\u000e\u000e\u000eAA"));

    Type messageType = new MessageType.Builder().setName("Foo")
        .setFullyQualifiedName("Foo")
        .setDocumentation("")
        .setFields(Arrays.asList(field))
        .setNestedTypes(NO_TYPES)
        .setExtensions(NO_EXTENSIONS)
        .setOptions(NO_OPTIONS)
        .build();
    ProtoFile expected =
        new ProtoFile.Builder().setFileName("foo.proto")
            .setPackageName(null)
            .setDependencies(NO_STRINGS)
            .setPublicDependencies(NO_STRINGS)
            .setTypes(Arrays.<Type>asList(messageType))
            .setServices(NO_SERVICES)
            .setOptions(NO_OPTIONS)
            .setExtendDeclarations(NO_EXTEND_DECLARATIONS)
            .build();
    assertThat(ProtoSchemaParser.parse("foo.proto", proto))
        .isEqualTo(expected);
  }

  @Test public void invalidHexStringEscape() throws Exception {
    String proto = ""
        + "message Foo {\n"
        + "  optional string name = 1 "
        + "[default = \"\\xW\"];\n"
        + "}";
    try {
      ProtoSchemaParser.parse("foo.proto", proto);
      Fail.fail("Expected parse error");
    } catch (IllegalStateException e) {
      assertThat(e.getMessage().contains("expected a digit after \\x or \\X"));
    }
  }

  @Test public void service() throws Exception {
    String proto = ""
        + "service SearchService {\n"
        + "  option (default_timeout) = 30;\n"
        + "\n"
        + "  rpc Search (SearchRequest) returns (SearchResponse);"
        + "  rpc Purchase (PurchaseRequest) returns (PurchaseResponse) {\n"
        + "    option (squareup.sake.timeout) = 15; \n"
        + "    option (squareup.a.b) = { value: [FOO, BAR] };\n"
        + "  }\n"
        + "}";
    List<Option> options = list(new Option("default_timeout", 30));
    Service expected = new Service.Builder().setName("SearchService")
        .setFullyQualifiedName("SearchService")
        .setDocumentation("")
        .setOptions(options)
        .setMethods(list(
            new Service.Method.Builder().setName("Search")
                .setDocumentation("")
                .setRequestType("SearchRequest")
                .setResponseType("SearchResponse")
                .setOptions(NO_OPTIONS)
                .build(),
            new Service.Method.Builder().setName("Purchase")
                .setDocumentation("")
                .setRequestType("PurchaseRequest")
                .setResponseType("PurchaseResponse")
                .setOptions(list( //
                    new Option("squareup.sake.timeout", 15), //
                    new Option("squareup.a.b", map("value",
                        list(EnumType.Value.anonymous("FOO"), EnumType.Value.anonymous("BAR"))))))
                .build()))
        .build();
    ProtoFile protoFile = new ProtoFile.Builder().setFileName("descriptor.proto")
        .setPackageName(null)
        .setDependencies(NO_STRINGS)
        .setPublicDependencies(NO_STRINGS)
        .setTypes(NO_TYPES)
        .setServices(Arrays.asList(expected))
        .setOptions(NO_OPTIONS)
        .setExtendDeclarations(NO_EXTEND_DECLARATIONS)
        .build();
    assertThat(ProtoSchemaParser.parse("descriptor.proto", proto))
        .isEqualTo(protoFile);
  }

  @Test public void hexTag() throws Exception {
    String proto = ""
        + "message HexTag {\n"
        + "  required string hex = 0x10;\n"
        + "}";
    Type expected = new MessageType.Builder().setName("HexTag")
        .setFullyQualifiedName("HexTag")
        .setDocumentation("")
        .setFields(Arrays.asList(
            new MessageType.Field.Builder().setLabel(Label.REQUIRED)
                .setType("string")
                .setName("hex")
                .setTag(16)
                .setDocumentation("")
                .setOptions(NO_OPTIONS)
                .build()))
        .setNestedTypes(NO_TYPES)
        .setExtensions(NO_EXTENSIONS)
        .setOptions(NO_OPTIONS)
        .build();
    ProtoFile protoFile =
        new ProtoFile.Builder().setFileName("hex.proto")
            .setPackageName(null)
            .setDependencies(NO_STRINGS)
            .setPublicDependencies(NO_STRINGS)
            .setTypes(Arrays.asList(expected))
            .setServices(NO_SERVICES)
            .setOptions(NO_OPTIONS)
            .setExtendDeclarations(NO_EXTEND_DECLARATIONS)
            .build();
    assertThat(ProtoSchemaParser.parse("hex.proto", proto)).isEqualTo(protoFile);
  }

  @Test public void structuredOption() throws Exception {
    String proto = ""
        + "message ExoticOptions {\n"
        + "  option (squareup.one) = {name: \"Name\", class_name:\"ClassName\"};\n"
        + "  option (squareup.two.a) = {[squareup.options.type]: EXOTIC};\n"
        + "  option (squareup.two.b) = {names: [\"Foo\", \"Bar\"]};\n"
        + "  option (squareup.three) = {x: {y: 1 y: 2}};\n" // NOTE: Omitted optional comma
        + "  option (squareup.four) = {x: {y: {z: 1}, y: {z: 2}}};\n"
        + "}";

    List<Option> options = new ArrayList<Option>();
    Map<String, String> option_one_map = new LinkedHashMap<String, String>();
    option_one_map.put("name", "Name");
    option_one_map.put("class_name", "ClassName");
    options.add(new Option("squareup.one", option_one_map));
    Map<String, Object> option_two_a_map = new LinkedHashMap<String, Object>();
    option_two_a_map.put("[squareup.options.type]", EnumType.Value.anonymous("EXOTIC"));
    options.add(new Option("squareup.two.a", option_two_a_map));
    Map<String, List<String>> option_two_b_map = new LinkedHashMap<String, List<String>>();
    option_two_b_map.put("names", Arrays.asList("Foo", "Bar"));
    options.add(new Option("squareup.two.b", option_two_b_map));
    Map<String, Map<String, ?>> option_three_map = new LinkedHashMap<String, Map<String, ?>>();
    Map<String, Object> option_three_nested_map = new LinkedHashMap<String, Object>();
    option_three_nested_map.put("y", Arrays.asList(1, 2));
    option_three_map.put("x", option_three_nested_map);
    options.add(new Option("squareup.three", option_three_map));

    Map<String, Map<String, ?>> option_four_map = new LinkedHashMap<String, Map<String, ?>>();
    Map<String, Object> option_four_map_1 = new LinkedHashMap<String, Object>();
    Map<String, Object> option_four_map_2_a = new LinkedHashMap<String, Object>();
    option_four_map_2_a.put("z", 1);
    Map<String, Object> option_four_map_2_b = new LinkedHashMap<String, Object>();
    option_four_map_2_b.put("z", 2);
    option_four_map_1.put("y", Arrays.asList(option_four_map_2_a, option_four_map_2_b));
    option_four_map.put("x", option_four_map_1);
    options.add(new Option("squareup.four", option_four_map));

    Type expected =
        new MessageType.Builder().setName("ExoticOptions")
            .setFullyQualifiedName("ExoticOptions")
            .setDocumentation("")
            .setFields(Arrays.<MessageType.Field>asList())
            .setNestedTypes(NO_TYPES)
            .setExtensions(NO_EXTENSIONS)
            .setOptions(options)
            .build();
    ProtoFile protoFile =
        new ProtoFile.Builder().setFileName("exotic.proto")
            .setPackageName(null)
            .setDependencies(NO_STRINGS)
            .setPublicDependencies(NO_STRINGS)
            .setTypes(Arrays.asList(expected))
            .setServices(NO_SERVICES)
            .setOptions(NO_OPTIONS)
            .setExtendDeclarations(NO_EXTEND_DECLARATIONS)
            .build();
    assertThat(ProtoSchemaParser.parse("exotic.proto", proto)).isEqualTo(protoFile);
  }

  @Test public void optionsWithNestedMapsAndTrailingCommas() throws Exception {
    String proto = ""
        + "message StructuredOption {\n"
        + "    optional field.type has_options = 3 [\n"
        + "            (option_map) = {\n"
        + "                nested_map: {key:\"value\" key2:[\"value2a\",\"value2b\"]},\n"
        + "            }\n"
        + "            (option_string) = [\"string1\",\"string2\"]\n"
        + "    ];\n"
        + "}";
    MessageType.Field field = new MessageType.Field.Builder().setLabel(Label.OPTIONAL)
        .setType("field.type")
        .setName("has_options")
        .setTag(3)
        .setDocumentation("")
        .setOptions(list(new Option("option_map", map("nested_map", map("key", "value", "key2",
            list("value2a", "value2b")))), new Option("option_string", list("string1", "string2"))))
        .build();
    assertThat(field.getOptions()).containsOnly( //
        new Option("option_map",
            map("nested_map", map("key", "value", "key2", list("value2a", "value2b")))),
        new Option("option_string", list("string1", "string2")));

    Type expected = new MessageType.Builder().setName("StructuredOption")
        .setFullyQualifiedName("StructuredOption")
        .setDocumentation("")
        .setFields(Arrays.<MessageType.Field>asList(field))
        .setNestedTypes(NO_TYPES)
        .setExtensions(NO_EXTENSIONS)
        .setOptions(NO_OPTIONS)
        .build();
    ProtoFile protoFile =
        new ProtoFile.Builder().setFileName("nestedmaps.proto")
            .setPackageName(null)
            .setDependencies(NO_STRINGS)
            .setPublicDependencies(NO_STRINGS)
            .setTypes(Arrays.asList(expected))
            .setServices(NO_SERVICES)
            .setOptions(NO_OPTIONS)
            .setExtendDeclarations(NO_EXTEND_DECLARATIONS)
            .build();
    assertThat(ProtoSchemaParser.parse("nestedmaps.proto", proto))
        .isEqualTo(protoFile);
  }

  @Test public void extensionWithNestedMessage() throws Exception {
    String proto = ""
        + "message Foo {\n"
        + "  optional int32 bar = 1 [\n"
        + "      (validation.range).min = 1,\n"
        + "      (validation.range).max = 100,\n"
        + "      default = 20\n"
        + "  ];\n"
        + "}";
    MessageType.Field field = new MessageType.Field.Builder().setLabel(Label.OPTIONAL)
        .setType("int32")
        .setName("bar")
        .setTag(1)
        .setDocumentation("")
        .setOptions(list(new Option("validation.range", new Option("min", 1)),
            new Option("validation.range", new Option("max", 100)), new Option("default", 20)))
        .build();
    assertThat(field.getOptions()).containsOnly( //
        new Option("validation.range", new Option("min", 1)), //
        new Option("validation.range", new Option("max", 100)), //
        new Option("default", 20));

    Type expected = new MessageType.Builder().setName("Foo")
        .setFullyQualifiedName("Foo")
        .setDocumentation("")
        .setFields(Arrays.asList(field))
        .setNestedTypes(NO_TYPES)
        .setExtensions(NO_EXTENSIONS)
        .setOptions(NO_OPTIONS)
        .build();
    ProtoFile protoFile =
        new ProtoFile.Builder().setFileName("foo.proto")
            .setPackageName(null)
            .setDependencies(NO_STRINGS)
            .setPublicDependencies(NO_STRINGS)
            .setTypes(Arrays.asList(expected))
            .setServices(NO_SERVICES)
            .setOptions(NO_OPTIONS)
            .setExtendDeclarations(NO_EXTEND_DECLARATIONS)
            .build();
    assertThat(ProtoSchemaParser.parse("foo.proto", proto)).isEqualTo(protoFile);
  }

  @Test public void noWhitespace() {
    String proto = "message C {optional A.B ab = 1;}";
    ProtoFile parse = ProtoSchemaParser.parse("test.proto", proto);

    MessageType.Field field = new MessageType.Field.Builder().setLabel(Label.OPTIONAL)
        .setType("A.B")
        .setName("ab")
        .setTag(1)
        .setDocumentation("")
        .setOptions(NO_OPTIONS)
        .build();
    Type type = new MessageType.Builder().setName("C")
        .setFullyQualifiedName("C")
        .setDocumentation("")
        .setFields(list(field))
        .setNestedTypes(NO_TYPES)
        .setExtensions(NO_EXTENSIONS)
        .setOptions(NO_OPTIONS)
        .build();
    ProtoFile expected =
        new ProtoFile.Builder().setFileName("test.proto")
            .setPackageName(null)
            .setDependencies(NO_STRINGS)
            .setPublicDependencies(NO_STRINGS)
            .setTypes(list(type))
            .setServices(NO_SERVICES)
            .setOptions(NO_OPTIONS)
            .setExtendDeclarations(NO_EXTEND_DECLARATIONS)
            .build();

    assertThat(parse).isEqualTo(expected);
  }
}

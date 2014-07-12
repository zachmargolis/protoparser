package com.squareup.protoparser;

import org.junit.Test;

import static com.squareup.protoparser.Service.Method;
import static com.squareup.protoparser.TestUtils.NO_METHODS;
import static com.squareup.protoparser.TestUtils.NO_OPTIONS;
import static com.squareup.protoparser.TestUtils.list;
import static org.fest.assertions.api.Assertions.assertThat;

public class ServiceTest {
  @Test public void emptyToString() {
    Service service = new Service.Builder().setName("Service")
        .setFullyQualifiedName("")
        .setDocumentation("")
        .setOptions(NO_OPTIONS)
        .setMethods(NO_METHODS)
        .build();
    String expected = "service Service {}\n";
    assertThat(service.toString()).isEqualTo(expected);
  }

  @Test public void singleToString() {
    Method method = new Method.Builder().setName("Name")
        .setDocumentation("")
        .setRequestType("RequestType")
        .setResponseType("ResponseType")
        .setOptions(NO_OPTIONS)
        .build();
    Service service = new Service.Builder().setName("Service")
        .setFullyQualifiedName("")
        .setDocumentation("")
        .setOptions(NO_OPTIONS)
        .setMethods(list(method))
        .build();
    String expected = ""
        + "service Service {\n"
        + "  rpc Name (RequestType) returns (ResponseType);\n"
        + "}\n";
    assertThat(service.toString()).isEqualTo(expected);
  }

  @Test public void singleWithOptionsToString() {
    Method method = new Method.Builder().setName("Name")
        .setDocumentation("")
        .setRequestType("RequestType")
        .setResponseType("ResponseType")
        .setOptions(NO_OPTIONS)
        .build();
    Service service = new Service.Builder().setName("Service")
        .setFullyQualifiedName("")
        .setDocumentation("")
        .setOptions(list(new Option("foo", "bar", Option.Source.BUILTIN)))
        .setMethods(list(method))
        .build();
    String expected = ""
        + "service Service {\n"
        + "  option foo = \"bar\";\n"
        + "\n"
        + "  rpc Name (RequestType) returns (ResponseType);\n"
        + "}\n";
    assertThat(service.toString()).isEqualTo(expected);
  }

  @Test public void singleWithDocumentation() {
    Method method = new Method.Builder().setName("Name")
        .setDocumentation("")
        .setRequestType("RequestType")
        .setResponseType("ResponseType")
        .setOptions(NO_OPTIONS)
        .build();
    Service service = new Service.Builder().setName("Service")
        .setFullyQualifiedName("")
        .setDocumentation("Hello")
        .setOptions(NO_OPTIONS)
        .setMethods(list(method))
        .build();
    String expected = ""
        + "// Hello\n"
        + "service Service {\n"
        + "  rpc Name (RequestType) returns (ResponseType);\n"
        + "}\n";
    assertThat(service.toString()).isEqualTo(expected);
  }

  @Test public void multipleToString() {
    Method method = new Method.Builder().setName("Name")
        .setDocumentation("")
        .setRequestType("RequestType")
        .setResponseType("ResponseType")
        .setOptions(NO_OPTIONS)
        .build();
    Service service = new Service.Builder().setName("Service")
        .setFullyQualifiedName("")
        .setDocumentation("")
        .setOptions(NO_OPTIONS)
        .setMethods(list(method, method))
        .build();
    String expected = ""
        + "service Service {\n"
        + "  rpc Name (RequestType) returns (ResponseType);\n"
        + "  rpc Name (RequestType) returns (ResponseType);\n"
        + "}\n";
    assertThat(service.toString()).isEqualTo(expected);
  }

  @Test public void methodToString() {
    Method method = new Method.Builder().setName("Name")
        .setDocumentation("")
        .setRequestType("RequestType")
        .setResponseType("ResponseType")
        .setOptions(NO_OPTIONS)
        .build();
    String expected = "rpc Name (RequestType) returns (ResponseType);\n";
    assertThat(method.toString()).isEqualTo(expected);
  }

  @Test public void methodWithDocumentationToString() {
    Method method = new Method.Builder().setName("Name")
        .setDocumentation("Hello")
        .setRequestType("RequestType")
        .setResponseType("ResponseType")
        .setOptions(NO_OPTIONS)
        .build();
    String expected = ""
        + "// Hello\n"
        + "rpc Name (RequestType) returns (ResponseType);\n";
    assertThat(method.toString()).isEqualTo(expected);
  }

  @Test public void methodWithOptions() {
    Method method =
        new Method.Builder().setName("Name")
            .setDocumentation("")
            .setRequestType("RequestType")
            .setResponseType("ResponseType")
            .setOptions(list(new Option("foo", "bar", Option.Source.BUILTIN)))
            .build();
    String expected = ""
        + "rpc Name (RequestType) returns (ResponseType) {\n"
        + "  option foo = \"bar\";\n"
        + "};\n";
    assertThat(method.toString()).isEqualTo(expected);
  }
}

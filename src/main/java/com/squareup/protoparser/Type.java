// Copyright 2013 Square, Inc.
package com.squareup.protoparser;

import java.util.List;

/** A message type or enum type declaration. */
public interface Type {
  String getName();
  String getFullyQualifiedName();
  String getDocumentation();
  List<Option> getOptions();
  List<Type> getNestedTypes();

  Builder builder();

  public interface Builder {
    Builder setName(String name);
    Builder setFullyQualifiedName(String fqname);
    Builder setDocumentation(String documentation);
    Builder setOptions(List<Option> options);
    Builder setNestedTypes(List<Type> nestedTypes);

    Type build();
  }
}

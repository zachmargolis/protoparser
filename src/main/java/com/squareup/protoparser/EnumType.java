// Copyright 2013 Square, Inc.
package com.squareup.protoparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.squareup.protoparser.Utils.appendDocumentation;
import static com.squareup.protoparser.Utils.appendIndented;
import static java.util.Collections.unmodifiableList;

/** An enumerated type declaration. */
public final class EnumType implements Type {
  private static void validateTagUniqueness(String type, List<Value> values) {
    Set<Integer> tags = new LinkedHashSet<Integer>();
    for (Value value : values) {
      int tag = value.getTag();
      if (!tags.add(tag)) {
        throw new IllegalStateException("Duplicate tag " + tag + " in " + type);
      }
    }
  }

  private static boolean parseAllowAlias(List<Option> options) {
    Option option = Option.findByName(options, "allow_alias");
    if (option != null) {
      Object value = option.getValue();
      return value instanceof Boolean && (Boolean) value;
    }
    return false;
  }

  /**
   * Though not mentioned in the spec, enum names use C++ scoping rules, meaning that enum values
   * are siblings of their type, not children of it.
   */
  static void validateValueUniquenessInScope(String type, List<Type> nestedTypes) {
    Set<String> names = new LinkedHashSet<String>();
    for (Type nestedType : nestedTypes) {
      if (nestedType instanceof EnumType) {
        EnumType enumType = (EnumType) nestedType;
        for (Value value : enumType.getValues()) {
          String name = value.getName();
          if (!names.add(name)) {
            throw new IllegalStateException("Duplicate enum name " + name + " in scope " + type);
          }
        }
      }
    }
  }

  private final String name;
  private final String fqname;
  private final String documentation;
  private final List<Option> options;
  private final List<Value> values;
  private final boolean allowAlias;

  public EnumType(String name, String fqname, String documentation, List<Option> options,
      List<Value> values) {
    if (name == null) throw new NullPointerException("name");
    if (fqname == null) throw new NullPointerException("fqname");
    if (documentation == null) throw new NullPointerException("documentation");
    if (options == null) throw new NullPointerException("options");
    if (values == null) throw new NullPointerException("values");

    boolean allowAlias = parseAllowAlias(options);
    if (!allowAlias) {
      validateTagUniqueness(fqname, values);
    }

    this.name = name;
    this.fqname = fqname;
    this.documentation = documentation;
    this.options = unmodifiableList(new ArrayList<Option>(options));
    this.values = unmodifiableList(new ArrayList<Value>(values));
    this.allowAlias = allowAlias;
  }

  @Override public String getName() {
    return name;
  }

  @Override public String getFullyQualifiedName() {
    return fqname;
  }

  @Override public String getDocumentation() {
    return documentation;
  }

  @Override public List<Option> getOptions() {
    return options;
  }

  public List<Value> getValues() {
    return values;
  }

  /** True if allowing multiple values to have the same tag. */
  public boolean allowAlias() {
    return allowAlias;
  }

  @Override public List<Type> getNestedTypes() {
    return Collections.emptyList();
  }

  @Override public boolean equals(Object other) {
    if (this == other) return true;
    if (!(other instanceof EnumType)) return false;

    EnumType that = (EnumType) other;
    return name.equals(that.name) //
        && fqname.equals(that.fqname) //
        && documentation.equals(that.documentation) //
        && options.equals(that.options) //
        && values.equals(that.values);
  }

  @Override public int hashCode() {
    int result = name.hashCode();
    result = 31 * result + fqname.hashCode();
    result = 31 * result + documentation.hashCode();
    result = 31 * result + options.hashCode();
    result = 31 * result + values.hashCode();
    return result;
  }

  @Override public String toString() {
    StringBuilder builder = new StringBuilder();
    appendDocumentation(builder, documentation);
    builder.append("enum ")
        .append(name)
        .append(" {");
    if (!options.isEmpty()) {
      builder.append('\n');
      for (Option option : options) {
        appendIndented(builder, option.toDeclaration());
      }
    }
    if (!values.isEmpty()) {
      builder.append('\n');
      for (Value value : values) {
        appendIndented(builder, value.toString());
      }
    }
    return builder.append("}\n").toString();
  }

  public Builder builder() {
    return new Builder()
        .setName(name)
        .setFqname(fqname)
        .setDocumentation(documentation)
        .setOptions(options)
        .setValues(values);
  }

  public static final class Builder implements Type.Builder {
    private String name;
    private String fqname;
    private String documentation;
    private List<Option> options;
    private List<Value> values;

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder setFqname(String fqname) {
      this.fqname = fqname;
      return this;
    }

    public Builder setDocumentation(String documentation) {
      this.documentation = documentation;
      return this;
    }

    public Builder setOptions(List<Option> options) {
      this.options = options;
      return this;
    }

    public Builder setNestedTypes(List<Type> nestedTypes) {
      return this;
    }

    public Builder setValues(List<Value> values) {
      this.values = values;
      return this;
    }

    @Override public EnumType build() {
      return new EnumType(name, fqname, documentation, options, values);
    }
  }

  /** An enum constant. */
  public static final class Value {
    private final String name;
    private final int tag;
    private final String documentation;
    private final List<Option> options;

    public Value(String name, int tag, String documentation, List<Option> options) {
      if (name == null) throw new NullPointerException("name");
      if (documentation == null) throw new NullPointerException("documentation");
      if (options == null) throw new NullPointerException("options");
      this.name = name;
      this.tag = tag;
      this.documentation = documentation;
      this.options = unmodifiableList(new ArrayList<Option>(options));
    }

    public String getName() {
      return name;
    }

    public int getTag() {
      return tag;
    }

    public String getDocumentation() {
      return documentation;
    }

    public List<Option> getOptions() {
      return options;
    }

    @Override public boolean equals(Object other) {
      if (this == other) return true;
      if (!(other instanceof Value)) return false;

      Value that = (Value) other;
      return name.equals(that.name) //
          && tag == that.tag //
          && documentation.equals(that.documentation) //
          && options.equals(that.options);
    }

    @Override public int hashCode() {
      int result = name.hashCode();
      result = 31 * result + tag;
      result = 31 * result + documentation.hashCode();
      result = 31 * result + options.hashCode();
      return result;
    }

    @Override public String toString() {
      StringBuilder builder = new StringBuilder();
      appendDocumentation(builder, documentation);
      builder.append(name)
          .append(" = ")
          .append(tag);
      if (!options.isEmpty()) {
        builder.append(" [\n");
        for (Option option : options) {
          appendIndented(builder, option.toString());
        }
        builder.append(']');
      }
      return builder.append(";\n").toString();
    }
  }
}

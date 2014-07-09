// Copyright 2013 Square, Inc.
package com.squareup.protoparser;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

/** A single {@code .proto} file. */
public final class ProtoFile {
  public static final int MIN_TAG_VALUE = 1;
  public static final int MAX_TAG_VALUE = (1 << 29) - 1; // 536,870,911
  private static final int RESERVED_TAG_VALUE_START = 19000;
  private static final int RESERVED_TAG_VALUE_END = 19999;

  /** True if the supplied value is in the valid tag range and not reserved. */
  public static boolean isValidTag(int value) {
    return (value >= MIN_TAG_VALUE && value < RESERVED_TAG_VALUE_START)
        || (value > RESERVED_TAG_VALUE_END && value <= MAX_TAG_VALUE);
  }

  private final String fileName;
  private final String packageName;
  private final List<String> dependencies;
  private final List<String> publicDependencies;
  private final List<Type> types;
  private final List<Service> services;
  private final List<Option> options;
  private final List<ExtendDeclaration> extendDeclarations;

  public ProtoFile(String fileName, String packageName, List<String> dependencies,
      List<String> publicDependencies, List<Type> types, List<Service> services,
      List<Option> options, List<ExtendDeclaration> extendDeclarations) {
    if (fileName == null) throw new NullPointerException("fileName");
    if (dependencies == null) throw new NullPointerException("dependencies");
    if (publicDependencies == null) throw new NullPointerException("publicDependencies");
    if (types == null) throw new NullPointerException("types");
    if (services == null) throw new NullPointerException("services");
    if (options == null) throw new NullPointerException("options");
    if (extendDeclarations == null) throw new NullPointerException("extendDeclarations");

    this.fileName = fileName;
    this.packageName = packageName;
    this.dependencies = unmodifiableList(new ArrayList<String>(dependencies));
    this.publicDependencies = unmodifiableList(new ArrayList<String>(publicDependencies));
    this.types = unmodifiableList(new ArrayList<Type>(types));
    this.services = unmodifiableList(new ArrayList<Service>(services));
    this.options = unmodifiableList(new ArrayList<Option>(options));
    this.extendDeclarations =
        unmodifiableList(new ArrayList<ExtendDeclaration>(extendDeclarations));
  }

  public String getFileName() {
    return fileName;
  }

  public String getPackageName() {
    return packageName;
  }

  public List<String> getDependencies() {
    return dependencies;
  }

  public List<String> getPublicDependencies() {
    return publicDependencies;
  }

  public List<Type> getTypes() {
    return types;
  }

  public List<Service> getServices() {
    return services;
  }

  public List<Option> getOptions() {
    return options;
  }

  public List<ExtendDeclaration> getExtendDeclarations() {
    return extendDeclarations;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ProtoFile)) return false;

    ProtoFile that = (ProtoFile) o;
    return dependencies.equals(that.dependencies)
        && extendDeclarations.equals(that.extendDeclarations)
        && fileName.equals(that.fileName)
        && options.equals(that.options)
        && (packageName == null ? that.packageName == null : packageName.equals(that.packageName))
        && publicDependencies.equals(that.publicDependencies)
        && services.equals(that.services)
        && types.equals(that.types);
  }

  @Override public int hashCode() {
    int result = fileName.hashCode();
    result = 31 * result + (packageName != null ? packageName.hashCode() : 0);
    result = 31 * result + dependencies.hashCode();
    result = 31 * result + publicDependencies.hashCode();
    result = 31 * result + types.hashCode();
    result = 31 * result + services.hashCode();
    result = 31 * result + options.hashCode();
    result = 31 * result + extendDeclarations.hashCode();
    return result;
  }

  @Override public String toString() {
    StringBuilder builder = new StringBuilder();
    if (!fileName.isEmpty()) {
      builder.append("// ").append(fileName).append('\n');
    }
    if (packageName != null) {
      builder.append("package ").append(packageName).append(";\n");
    }
    if (!dependencies.isEmpty() || !publicDependencies.isEmpty()) {
      builder.append('\n');
      for (String dependency : dependencies) {
        builder.append("import \"").append(dependency).append("\";\n");
      }
      for (String publicDependency : publicDependencies) {
        builder.append("import public \"").append(publicDependency).append("\";\n");
      }
    }
    if (!options.isEmpty()) {
      builder.append('\n');
      for (Option option : options) {
        builder.append(option.toDeclaration());
      }
    }
    if (!types.isEmpty()) {
      builder.append('\n');
      for (Type type : types) {
        builder.append(type);
      }
    }
    if (!extendDeclarations.isEmpty()) {
      builder.append('\n');
      for (ExtendDeclaration extendDeclaration : extendDeclarations) {
        builder.append(extendDeclaration);
      }
    }
    if (!services.isEmpty()) {
      builder.append('\n');
      for (Service service : services) {
        builder.append(service);
      }
    }
    return builder.toString();
  }

  public Builder builder() {
    return new Builder()
        .setFileName(fileName)
        .setPackageName(packageName)
        .setDependencies(dependencies)
        .setPublicDependencies(publicDependencies)
        .setTypes(types)
        .setServices(services)
        .setOptions(options)
        .setExtendDeclarations(extendDeclarations);
  }

  public static final class Builder {
    private String fileName;
    private String packageName;
    private List<String> dependencies;
    private List<String> publicDependencies;
    private List<Type> types;
    private List<Service> services;
    private List<Option> options;
    private List<ExtendDeclaration> extendDeclarations;

    public Builder setFileName(String fileName) {
      this.fileName = fileName;
      return this;
    }

    public Builder setPackageName(String packageName) {
      this.packageName = packageName;
      return this;
    }

    public Builder setDependencies(List<String> dependencies) {
      this.dependencies = dependencies;
      return this;
    }

    public Builder setPublicDependencies(List<String> publicDependencies) {
      this.publicDependencies = publicDependencies;
      return this;
    }

    public Builder setTypes(List<Type> types) {
      this.types = types;
      return this;
    }

    public Builder setServices(List<Service> services) {
      this.services = services;
      return this;
    }

    public Builder setOptions(List<Option> options) {
      this.options = options;
      return this;
    }

    public Builder setExtendDeclarations(List<ExtendDeclaration> extendDeclarations) {
      this.extendDeclarations = extendDeclarations;
      return this;
    }

    public ProtoFile build() {
      return new ProtoFile(fileName, packageName, dependencies, publicDependencies, types, services,
          options, extendDeclarations);
    }
  }
}

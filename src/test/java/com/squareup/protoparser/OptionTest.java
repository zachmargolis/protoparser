package com.squareup.protoparser;

import java.util.List;
import java.util.Map;
import org.junit.Test;

import static com.squareup.protoparser.TestUtils.list;
import static com.squareup.protoparser.TestUtils.map;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;
import static org.fest.assertions.data.MapEntry.entry;

public class OptionTest {
  @Test public void simpleBuiltinToString() {
    Option option = new Option("foo", "bar", Option.Source.BUILTIN);
    String expected = "foo = \"bar\"";
    assertThat(option.toString()).isEqualTo(expected);
  }

  @Test public void simpleExtensionToString() {
    Option option = new Option("foo", "bar", Option.Source.CUSTOM);
    String expected = "(foo) = \"bar\"";
    assertThat(option.toString()).isEqualTo(expected);
  }

  @Test public void nestedToString() {
    Option option = new Option("foo.boo", new Option("bar", "baz", Option.Source.BUILTIN), Option.Source.CUSTOM);
    String expected = "(foo.boo).bar = \"baz\"";
    assertThat(option.toString()).isEqualTo(expected);
  }

  @Test public void listToString() {
    Option option = new Option("foo",
        list(new Option("ping", "pong", Option.Source.CUSTOM),
            new Option("kit", "kat", Option.Source.BUILTIN)),
        Option.Source.BUILTIN);
    String expected = ""
        + "foo = [\n"
        + "  (ping) = \"pong\",\n"
        + "  kit = \"kat\"\n"
        + "]";
    assertThat(option.toString()).isEqualTo(expected);
  }

  @Test public void booleanBuiltinToString() {
    Option option = new Option("foo", false, Option.Source.BUILTIN);
    String expected = "foo = false";
    assertThat(option.toString()).isEqualTo(expected);
  }

  @Test public void booleanExtensionToString() {
    Option option = new Option("foo", false, Option.Source.CUSTOM);
    String expected = "(foo) = false";
    assertThat(option.toString()).isEqualTo(expected);
  }

  @Test public void optionListToMap() {
    List<Option> options = list( //
        new Option("foo", "bar", Option.Source.CUSTOM), //
        new Option("ping", list( //
            new Option("kit", "kat", Option.Source.CUSTOM), //
            new Option("tic", "tac", Option.Source.CUSTOM), //
            new Option("up", "down", Option.Source.CUSTOM) //
        ), Option.Source.CUSTOM), //
        new Option("wire", map( //
            "omar", "little", //
            "proposition", "joe" //
        ), Option.Source.CUSTOM), //
        new Option("nested.option", new Option("one", "two", Option.Source.CUSTOM), Option.Source.CUSTOM), //
        new Option("nested.option", new Option("three", "four", Option.Source.CUSTOM), Option.Source.CUSTOM) //
    );
    Map<String, Object> optionMap = Option.optionsAsMap(options);
    assertThat(optionMap).contains( //
        entry("foo", "bar"), //
        entry("ping", list( //
            new Option("kit", "kat", Option.Source.CUSTOM), //
            new Option("tic", "tac", Option.Source.CUSTOM), //
            new Option("up", "down", Option.Source.CUSTOM) //
        )), //
        entry("wire", map( //
            "omar", "little", //
            "proposition", "joe" //
        )), //
        entry("nested.option", map( //
            "one", "two", //
            "three", "four" //
        )) //
    );
  }

  @Test public void findInList() {
    Option one = new Option("one", "1", Option.Source.CUSTOM);
    Option two = new Option("two", "2", Option.Source.CUSTOM);
    Option three = new Option("three", "3", Option.Source.CUSTOM);
    List<Option> options = list(one, two, three);
    assertThat(Option.findByName(options, "one")).isSameAs(one);
    assertThat(Option.findByName(options, "two")).isSameAs(two);
    assertThat(Option.findByName(options, "three")).isSameAs(three);
  }

  @Test public void findInListMissing() {
    Option one = new Option("one", "1", Option.Source.CUSTOM);
    Option two = new Option("two", "2", Option.Source.CUSTOM);
    List<Option> options = list(one, two);
    assertThat(Option.findByName(options, "three")).isNull();
  }

  @Test public void findInListDuplicate() {
    Option one = new Option("one", "1", Option.Source.CUSTOM);
    Option two = new Option("two", "2", Option.Source.CUSTOM);
    List<Option> options = list(one, two, one);
    try {
      Option.findByName(options, "one");
      fail("Multiple option matches not allowed.");
    } catch (IllegalStateException e) {
      assertThat(e).hasMessage("Multiple options match name: one");
    }
  }

  @Test public void escape() {
    assertThat(Option.escape("h\"i")).isEqualTo("h\\\"i");
    assertThat(Option.escape("h\ti")).isEqualTo("h\\ti");
    assertThat(Option.escape("h\ri")).isEqualTo("h\\ri");
    assertThat(Option.escape("h\\i")).isEqualTo("h\\\\i");
    assertThat(Option.escape("h\ni")).isEqualTo("h\\ni");
  }
}

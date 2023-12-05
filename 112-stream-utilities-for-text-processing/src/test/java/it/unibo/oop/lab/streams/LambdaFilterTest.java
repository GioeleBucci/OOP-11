package it.unibo.oop.lab.streams;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import it.unibo.oop.lab.streams.LambdaFilter.Command;

public class LambdaFilterTest {

  private static final String TEXT = "This is a STRING";

  @Test
  void testCommands() {
    assertEquals(TEXT.toLowerCase(), Command.TO_LOWERCASE.translate(TEXT));
    assertEquals(TEXT.toCharArray().length, Integer.parseInt(Command.COUNT_CHARS.translate(TEXT)));
    assertEquals(TEXT.lines().count(), Integer.parseInt(Command.COUNT_LINES.translate(TEXT)));
    assertEquals("a is STRING This", Command.SORT_WORDS.translate(TEXT));
    assertEquals("pippo -> 1 word -> 2", Command.COUNT_EACH_WORD.translate("word word pippo"));
  }
}

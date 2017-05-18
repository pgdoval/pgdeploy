/*
 * Copyright 2017, OnGres.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.ongres.pgdeploy.pgconfig;

import com.ongres.pgdeploy.pgconfig.properties.DataType;
import com.ongres.pgdeploy.pgconfig.properties.Property;
import com.ongres.pgdeploy.pgconfig.properties.Unit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

 
public class DefaultPropertyParser implements PropertyParser {

  private static String delimiter = "|";

  protected static final Path file = Paths.get(
      DefaultPropertyParser.class.getProtectionDomain().getCodeSource().getLocation().getPath())
      .resolve("pgprops.csv");

  private static final Map<String,DataType> typeFromString = typeFromString();

  private static final Map<String,DataType> typeFromString() {
    Map<String, DataType> result = new HashMap<>(6);

    result.put("bool", DataType.BOOLEAN);
    result.put("string", DataType.STRING);
    result.put("enum", DataType.STRING);
    result.put("real", DataType.DOUBLE);
    result.put("integer", DataType.INTEGER);

    return result;
  }


  @Override
  public Optional<Property> parse(String property) {
    try {
      Optional<String> optionalLine = Files.lines(file)
          .filter(line -> line.startsWith(property + delimiter))
          .findAny();

      if (!optionalLine.isPresent()) {
        return Optional.empty();
      }

      String[] split = optionalLine.get().split(Pattern.quote(delimiter));

      DataType type = typeFromString.getOrDefault(split[2], DataType.STRING);
      Unit unit = getUnitFromValue(split[1]);

      return Optional.of(
          new Property(property, true, type, Unit.getListFromUnit(unit))
      );

    } catch (IOException e) {
      return Optional.empty();
    }
  }

  private Unit getUnitFromValue(String value) {

    Optional<Unit> chosenUnitOptional = Stream.of(Unit.values())
        .filter(unit -> value.endsWith(unit.getUnitName()))
        .sorted(Comparator.comparingInt(unit -> -unit.getUnitName().length()))
        .findFirst();

    return chosenUnitOptional.orElse(Unit.NONE);
  }

  protected DefaultPropertyParser() {
  }

  public static DefaultPropertyParser getInstance() {
    return SingletonHolder.INSTANCE;
  }


  private static class SingletonHolder {
    private static final DefaultPropertyParser INSTANCE = new DefaultPropertyParser();
  }
}

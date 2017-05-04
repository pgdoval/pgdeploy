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
package com.ongres.pgdeploy.wrappers;

import com.ongres.pgdeploy.pgconfig.PostgresConfig;
import com.ongres.pgdeploy.pgconfig.properties.Property;
import com.ongres.pgdeploy.pgconfig.properties.PropertyValue;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by pablo on 3/05/17.
 */
public class PostgreSqlConfWrapper {

  private static String equals = "=";
  private static String comment = "#";

  public static void updateConfFile(Path path, PostgresConfig config) throws IOException {
    List<String> lines = Files.readAllLines(path);

    lines = updateLines(lines, config.asStream());

    Files.write(path, lines, Charset.forName("UTF-8"));

  }

  private static Map.Entry<String, String> getEntryFromLine(String line) {

    int eqPosition = line.indexOf(equals);

    if (eqPosition < 0) {
      return new AbstractMap.SimpleEntry<>("", "");
    }

    String key = line.substring(0, eqPosition).trim();
    if (key.startsWith(comment)) {
      key = comment + key.substring(1).trim();
    }

    String value = line.substring(eqPosition + 1).trim();

    return new AbstractMap.SimpleEntry<>(key, value);
  }

  static List<String> updateLines(
      List<String> currentProperties, Stream<Map.Entry<Property, PropertyValue>> newProperties) {

    List<Map.Entry<String, String>> adaptedProperties =
        currentProperties.stream()
            .map(PostgreSqlConfWrapper::getEntryFromLine)
            .collect(Collectors.toList());

    newProperties
        .map(entry -> new AbstractMap.SimpleEntry<>(
            entry.getKey().getName(), entry.getValue().getValue().toString()))
        .forEachOrdered(entry -> {

          int indexOfProperty = -1;
          int indexOfCommentedProperty = -1;

          String key = entry.getKey();
          String commentedKey = "#" + key;
          String value = entry.getValue();

          for (int i = 0; i < adaptedProperties.size(); i++) {
            if (Objects.equals(adaptedProperties.get(i).getKey(), key)) {
              indexOfProperty = i;
            }
            if (Objects.equals(adaptedProperties.get(i).getKey(), commentedKey)) {
              indexOfCommentedProperty = i;
            }
          }

          //If the property doesn't exist, we simply add it
          if (indexOfProperty == -1 && indexOfCommentedProperty == -1) {
            adaptedProperties.add(entry);
          }

          //If the property exists but not its commented version,
          //we have to comment it and add the new one afterwards
          if (indexOfProperty > -1) {

            String currentValue = adaptedProperties.get(indexOfProperty).getValue();
            if (!Objects.equals(currentValue, value)) {

              if (indexOfProperty < indexOfCommentedProperty && indexOfCommentedProperty > -1) {
                adaptedProperties.remove(indexOfCommentedProperty);
              }

              adaptedProperties.set(indexOfProperty,
                  new AbstractMap.SimpleEntry<>(commentedKey,currentValue));
              adaptedProperties.add(indexOfProperty + 1, entry);

              if (indexOfProperty > indexOfCommentedProperty && indexOfCommentedProperty > -1) {
                adaptedProperties.remove(indexOfCommentedProperty);
              }
            }

          }

          //If the property is commented, we have to tell its value
          if (indexOfProperty == -1 && indexOfCommentedProperty > -1) {
            String currentValue = adaptedProperties.get(indexOfCommentedProperty).getValue();
            if (Objects.equals(currentValue, value)) {
              adaptedProperties.set(indexOfCommentedProperty, entry);
            } else {
              adaptedProperties.add(indexOfCommentedProperty + 1, entry);
            }

          }


        });

    return adaptedProperties.stream()
        .map(entry -> entry.getKey().isEmpty() ? "" : (entry.getKey() + equals + entry.getValue()))
        .collect(Collectors.toList());
  }
}

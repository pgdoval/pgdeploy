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
package com.ongres.pgdeploy.wrappers.postgresqlconf;

import com.ongres.pgdeploy.pgconfig.properties.Property;
import com.ongres.pgdeploy.pgconfig.properties.PropertyValue;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class implements the {@link UpdateLinesStrategy} necessary for the
 * {@link PostgreSqlConfWrapper} to work. Its policy is to: <ul>
 *   <li>Comment the previous value of a property that is going to be changed and add the
 *   declaration of the new value just after it.</li>
 *   <li>In that case, if there was another commented line including that property, it is
 *   removed./li>
 *   <li>It takes care of other lines that don't contain properties and it keeps them in the
 *   same order as they were before</li>
 *   <li>If there is more than one uncommented version of a property, it only takes care of the
 *   last one. The same goes for when there is more than one commented version.</li>
 * </ul>
 */
public class BasicUpdateLinesStrategy implements UpdateLinesStrategy {

  private static String equals = "=";
  private static String comment = "#";

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

  @Override
  public List<String> updateLines(
      List<String> currentProperties, Stream<Map.Entry<Property, PropertyValue>> newProperties) {

    List<Map.Entry<String, String>> adaptedProperties =
        currentProperties.stream()
            .map(BasicUpdateLinesStrategy::getEntryFromLine)
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

          //Get the last indexes of the key and the commented key
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

          //If the property is commented, we will add the new one just afterwards,
          //unless the commented property has the same value, in which case we simply uncomment it
          if (indexOfProperty == -1 && indexOfCommentedProperty > -1) {
            String currentValue = adaptedProperties.get(indexOfCommentedProperty).getValue();
            if (Objects.equals(currentValue, value)) {
              adaptedProperties.set(indexOfCommentedProperty, entry);
            } else {
              adaptedProperties.add(indexOfCommentedProperty + 1, entry);
            }
          }

          //If the property exists, if it has the same value there's nothing to do, but if its
          //value is different, we comment it and add the new one just afterwards.
          //In that case, if there was also the commented version of the property, it is removed.
          if (indexOfProperty > -1) {

            String currentValue = adaptedProperties.get(indexOfProperty).getValue();
            if (!Objects.equals(currentValue, value)) {

              adaptedProperties.set(indexOfProperty,
                  new AbstractMap.SimpleEntry<>(commentedKey,currentValue));
              adaptedProperties.add(indexOfProperty + 1, entry);

              //Depending on the position of the commented vs the non commented, the position
              //to remove is one or another
              if (indexOfCommentedProperty > -1) {
                if (indexOfProperty > indexOfCommentedProperty) {
                  adaptedProperties.remove(indexOfCommentedProperty);
                } else {
                  adaptedProperties.remove(indexOfCommentedProperty + 1 );
                }
              }
            }

          }


        });

    return adaptedProperties.stream()
        .map(entry -> entry.getKey().isEmpty() ? "" : (entry.getKey() + equals + entry.getValue()))
        .collect(Collectors.toList());
  }
}

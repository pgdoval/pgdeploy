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

import com.ongres.pgdeploy.pgconfig.DefaultPropertyParser;
import com.ongres.pgdeploy.pgconfig.PostgresConfig;
import com.ongres.pgdeploy.pgconfig.properties.DataType;
import com.ongres.pgdeploy.pgconfig.properties.Property;
import com.ongres.pgdeploy.pgconfig.properties.Unit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 * Created by pablo on 4/05/17.
 */
@RunWith(Parameterized.class)
public class PostgreSqlConfWrapperTest {

  public PostgresConfig config;

  @Parameterized.Parameter(0)
  public List<String> lines;
  
  @Parameterized.Parameter(1)
  public List<String> expectedResult;

  public List<String> result;

  public DefaultPropertyParser mockedParser;
  public DefaultPropertyParser spy;

  public static String diffPropertyCommented = "#prop1=234";
  public static String diffPropertyUntrimmedCommented = "# prop1 =234";

  public static String diffProperty = "prop1=234";
  public static String untrimmedDiffProperty = " prop1 =234";

  public static String commented = "#prop=234";
  public static String untrimmedCommented = "# prop =234";

  public static String samePropertyDiffValue = "prop=234";
  public static String untrimmedSamePropertyDiffValue = " prop =234";

  public static String samePropertySameValue = "prop=value";
  public static String untrimmedSamePropertySameValue = " prop =value";
  public static String untrimmedSamePropertySameUntrimmedValue = " prop = value ";


  public static List<String> emptyList = new ArrayList<>();
  public static List<String> samePropertySameValueList = Arrays.asList(samePropertySameValue);
  public static List<String> samePropertyDiffValueList = Arrays.asList(samePropertyDiffValue);


  
  @Parameterized.Parameters
  public static Collection getParams() {
    List<Object[]> result = new ArrayList<>();

    result.add(new Object []{ samePropertySameValueList, samePropertySameValueList });

    return result;
  }


  @Before
  public void setup() throws Exception {
    mockedParser = mock(DefaultPropertyParser.class);
    spy = spy(mockedParser);

    try {
      Mockito.doReturn(
          Optional.of(new Property("prop",false, DataType.STRING, Unit.noneList))
      ).when(spy)
          .parse(anyString());
    } catch (Exception e) {
      e.printStackTrace();
    }

    config = new PostgresConfig.Builder(spy).withProperty("a", "value").build();

  }


  @Test
  public void updateLinesNormalChange() throws Exception {
     result = PostgreSqlConfWrapper.updateLines(lines, config.asStream());

     assertEquals(expectedResult.stream().collect(Collectors.joining("\n")),
         result.stream().collect(Collectors.joining("\n")));

  }

}
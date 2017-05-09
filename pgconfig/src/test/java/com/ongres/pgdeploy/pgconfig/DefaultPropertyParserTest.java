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
import com.ongres.pgdeploy.pgconfig.properties.PropertyValueParser;
import com.ongres.pgdeploy.pgconfig.properties.Unit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


import static com.ongres.pgdeploy.pgconfig.properties.DataType.*;
import static com.ongres.pgdeploy.pgconfig.properties.Unit.*;
import static org.junit.Assert.*;

/**
 * Created by pablo on 9/05/17.
 */
@RunWith(Parameterized.class)
public class DefaultPropertyParserTest {

  @Parameterized.Parameter(0)
  public String name;

  @Parameterized.Parameter(1)
  public boolean needToRestart;

  @Parameterized.Parameter(2)
  public List<Unit> availableUnits;

  @Parameterized.Parameter(3)
  public DataType type;

  @Parameterized.Parameter(4)
  public boolean isPresent;

  private PropertyParser parser = DefaultPropertyParser.getInstance();

  @Parameterized.Parameters
  public static Collection getParams() {
    List<Object[]> result = new ArrayList<>();

    //We check all the units in the csv file: s, ms, kB, 8kB, min, 16MB
    result.add(new Object []{"autovacuum_naptime", true, timeList, INTEGER, true});
    result.add(new Object []{"autovacuum_vacuum_cost_delay", true, timeList, INTEGER, true});
    result.add(new Object []{"autovacuum_work_mem", true, byteList, INTEGER, true});
    result.add(new Object []{"effective_cache_size", true, byteList, INTEGER, true});
    result.add(new Object []{"log_rotation_age", true, timeList, INTEGER, true});
    result.add(new Object []{"min_wal_size", true, byteList, INTEGER, true});

    //We check all the datatypes: double, boolean, string, enum (integers are above)
    result.add(new Object []{"cursor_tuple_fraction", true, noneList, DOUBLE, true});
    result.add(new Object []{"data_checksums", true, noneList, BOOLEAN, true});
    result.add(new Object []{"data_directory", true, noneList, STRING, true});
    result.add(new Object []{"client_min_messages", true, noneList, STRING, true});

    //Test non-properties
    result.add(new Object []{"not a property", true, noneList, STRING, false});

    return result;
  }


  @Test
  public void parse() throws Exception {

    Optional<Property> propertyOptional = parser.parse(name);

    assertEquals(isPresent, propertyOptional.isPresent());

    if (isPresent) {
      Property property = propertyOptional.get();

      assertEquals(name, property.getName());
      assertEquals(needToRestart, property.isNeedToRestart());
      assertEquals(availableUnits, property.getAvailableUnits());
      assertEquals(type, property.getType());
    }
  }

}
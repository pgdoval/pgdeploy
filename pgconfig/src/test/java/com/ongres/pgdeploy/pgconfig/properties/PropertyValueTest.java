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
package com.ongres.pgdeploy.pgconfig.properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;
import static com.ongres.pgdeploy.pgconfig.properties.PropertyValue.*;

@RunWith(Parameterized.class)
public class PropertyValueTest {

  @Parameterized.Parameter(0)
  public PropertyValue value;

  @Parameterized.Parameter(1)
  public String expectedResult;


  @Parameterized.Parameters
  public static Collection getParams() {
    List<Object[]> result = new ArrayList<>();

    result.add(new Object []{ from(1), "1" });
    result.add(new Object []{ from(1L), "1" });
    result.add(new Object []{ from(1F), "1.0" });
    result.add(new Object []{ from(1D), "1.0" });
    result.add(new Object []{ from(true), "true" });
    result.add(new Object []{ from("asd"), "asd" });
    result.add(new Object []{ mb(1), "1MB" });
    result.add(new Object []{ mb(1L), "1MB" });
    result.add(new Object []{ mb(1F), "1.0MB" });
    result.add(new Object []{ mb(1D), "1.0MB" });

    return result;
  }


  @Test
  public void toWritableString() throws Exception {
    assertEquals(expectedResult, value.toWritableString());
  }

}
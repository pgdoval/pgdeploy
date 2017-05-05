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

import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by pablo on 28/04/17.
 */
public enum Unit {
  NONE(""),
  KB("kB"),
  MB("MB"),
  GB("GB"),
  TB("TB"),
  MS("ms"),
  S("s"),
  MIN("min"),
  H("h"),
  D("d");

  private String unitName;

  public String getUnitName() {
    return unitName;
  }

  Unit(String unitName) {
    this.unitName = unitName;
  }

  public static final List<Unit> noneList = Collections.singletonList(NONE);

  public static final List<Unit> byteList = ImmutableList.copyOf(Arrays.asList(KB, MB, GB, TB));

  public static final List<Unit> timeList = ImmutableList.copyOf(Arrays.asList(MS, S, MIN, H, D));
}

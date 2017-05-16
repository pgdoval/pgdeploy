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
package com.ongres.pgdeploy.core.pgversion;

import org.junit.Test;

import static org.junit.Assert.*;

public class PostgresMajorVersionTest {


  @Test
  public void test() {
    PostgresMajorVersion ten = new Post10PostgresMajorVersion(10);
    PostgresMajorVersion ten2 = new Post10PostgresMajorVersion(10);
    PostgresMajorVersion eleven = new Post10PostgresMajorVersion(11);
    PostgresMajorVersion ninefive = new Pre10PostgresMajorVersion(9,5);
    PostgresMajorVersion ninefive2 = new Pre10PostgresMajorVersion(9,5);
    PostgresMajorVersion ninethree = new Pre10PostgresMajorVersion(9,3);
    PostgresMajorVersion eightfive = new Pre10PostgresMajorVersion(8,5);
    PostgresMajorVersion tenzero = new Pre10PostgresMajorVersion(10,0);

    assertEquals(ten, ten2);
    assertEquals(ninefive, ninefive2);

    assertNotEquals(ninefive, ninethree);
    assertNotEquals(ninefive, eightfive);
    assertNotEquals(ten, eleven);
    assertNotEquals(ten, tenzero);

  }
}
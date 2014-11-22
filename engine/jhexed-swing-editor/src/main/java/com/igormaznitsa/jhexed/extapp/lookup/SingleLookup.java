/*
 * Copyright 2014 Igor Maznitsa.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.igormaznitsa.jhexed.extapp.lookup;

public final class SingleLookup implements Lookup {

  private final Object obj;

  public SingleLookup(final Object obj) {
    if (obj == null) {
      throw new NullPointerException("Object must not be null");
    }
    this.obj = obj;
  }

  public Object getValue() {
    return this.obj;
  }

  @Override
  public <T> T lookup(final Class<T> klazz, final Object ... args) {
    final T result;
    if (obj.getClass().isAssignableFrom(klazz)) {
      result = klazz.cast(obj);
    }
    else {
      result = null;
    }
    return result;
  }

}

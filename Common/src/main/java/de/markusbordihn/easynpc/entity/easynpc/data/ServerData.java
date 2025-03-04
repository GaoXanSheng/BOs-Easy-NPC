/*
 * Copyright 2023 Markus Bordihn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.markusbordihn.easynpc.entity.easynpc.data;

import de.markusbordihn.easynpc.data.server.ServerDataAccessor;
import de.markusbordihn.easynpc.data.server.ServerEntityData;
import de.markusbordihn.easynpc.entity.easynpc.EasyNPC;
import net.minecraft.world.entity.PathfinderMob;

public interface ServerData<E extends PathfinderMob> extends EasyNPC<E> {

  default <T> void setServerEntityData(ServerDataAccessor<T> entityDataAccessor, T entityData) {
    getServerEntityData().set(entityDataAccessor, entityData);
  }

  default <T> T getServerEntityData(ServerDataAccessor<T> entityDataAccessor) {
    return getServerEntityData().get(entityDataAccessor);
  }

  default <T> void defineServerEntityData(ServerDataAccessor<T> entityDataAccessor, T entityData) {
    getServerEntityData().define(entityDataAccessor, entityData);
  }

  default boolean hasServerEntityData() {
    return getServerEntityData() != null;
  }

  void defineServerEntityData();

  ServerEntityData getServerEntityData();
}

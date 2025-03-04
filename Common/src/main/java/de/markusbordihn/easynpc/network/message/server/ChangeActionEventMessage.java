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

package de.markusbordihn.easynpc.network.message.server;

import de.markusbordihn.easynpc.Constants;
import de.markusbordihn.easynpc.data.action.ActionDataSet;
import de.markusbordihn.easynpc.data.action.ActionEventType;
import de.markusbordihn.easynpc.entity.easynpc.EasyNPC;
import de.markusbordihn.easynpc.entity.easynpc.data.ActionEventData;
import de.markusbordihn.easynpc.network.message.NetworkMessageRecord;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public record ChangeActionEventMessage(
    UUID uuid, ActionEventType actionEventType, ActionDataSet actionDataSet)
    implements NetworkMessageRecord {

  public static final ResourceLocation MESSAGE_ID =
      new ResourceLocation(Constants.MOD_ID, "change_action_event");

  public static ChangeActionEventMessage create(final FriendlyByteBuf buffer) {
    return new ChangeActionEventMessage(
        buffer.readUUID(),
        buffer.readEnum(ActionEventType.class),
        new ActionDataSet(buffer.readNbt()));
  }

  @Override
  public void write(final FriendlyByteBuf buffer) {
    buffer.writeUUID(this.uuid);
    buffer.writeEnum(this.actionEventType);
    buffer.writeNbt(this.actionDataSet.createTag());
  }

  @Override
  public ResourceLocation id() {
    return MESSAGE_ID;
  }

  @Override
  public void handleServer(final ServerPlayer serverPlayer) {
    EasyNPC<?> easyNPC = getEasyNPCAndCheckAccess(this.uuid, serverPlayer);
    if (easyNPC == null
        || this.actionEventType == null
        || this.actionEventType == ActionEventType.NONE
        || this.actionDataSet == null) {
      log.error("Failed to change action event for {}: Invalid data", easyNPC);
      return;
    }

    // Get Permission level for corresponding action.
    int permissionLevel = 0;
    MinecraftServer minecraftServer = serverPlayer.getServer();
    ActionEventData<?> actionEventData = easyNPC.getEasyNPCActionEventData();
    if (minecraftServer != null) {
      permissionLevel = minecraftServer.getProfilePermissions(serverPlayer.getGameProfile());
      log.debug(
          "Set action owner permission level {} for {} from {}",
          permissionLevel,
          easyNPC,
          serverPlayer);
      actionEventData.setActionPermissionLevel(permissionLevel);
    } else {
      log.warn("Unable to verify permission level from {} for {}", this, serverPlayer);
    }

    // Perform action.
    log.debug(
        "Set action event {} with {} for {} from {} with owner permission level {}.",
        actionEventType,
        actionDataSet,
        easyNPC,
        serverPlayer,
        permissionLevel);
    actionEventData.getActionEventSet().setActionEvent(actionEventType, actionDataSet);
  }
}

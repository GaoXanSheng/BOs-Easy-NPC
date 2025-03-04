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

package de.markusbordihn.easynpc.commands.suggestion;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.markusbordihn.easynpc.entity.LivingEntityManager;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.level.ServerPlayer;

public class EasyNPCSuggestions {

  private EasyNPCSuggestions() {}

  public static Stream<String> suggestUUID(ServerPlayer serverPlayer) {
    return serverPlayer.isCreative()
        ? LivingEntityManager.getUUIDStrings()
        : LivingEntityManager.getUUIDStringsByOwner(serverPlayer);
  }

  public static Stream<String> suggestUUID(String startWith) {
    return LivingEntityManager.getUUIDStrings().filter(uuid -> uuid.startsWith(startWith));
  }

  public static CompletableFuture<Suggestions> suggestUUID(
      CommandContext<CommandSourceStack> context, SuggestionsBuilder build)
      throws CommandSyntaxException {
    ServerPlayer serverPlayer = context.getSource().getPlayerOrException();
    return SharedSuggestionProvider.suggest(suggestUUID(serverPlayer), build);
  }
}

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

package de.markusbordihn.easynpc.data.dialog;

import de.markusbordihn.easynpc.utils.TextUtils;
import de.markusbordihn.easynpc.utils.UUIDUtils;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public record DialogTextData(UUID id, String text, boolean isTranslationKey) {

  public static final String DATA_TEXT_TAG = "Text";

  public DialogTextData(CompoundTag compoundTag) {
    this(
        UUIDUtils.textToUUID(compoundTag.getString(DATA_TEXT_TAG)),
        compoundTag.getString(DATA_TEXT_TAG),
        TextUtils.isTranslationKey(compoundTag.getString(DATA_TEXT_TAG)));
  }

  public DialogTextData(String text) {
    this(UUIDUtils.textToUUID(text), text, TextUtils.isTranslationKey(text));
  }

  public static DialogTextData create(CompoundTag compoundTag) {
    return new DialogTextData(compoundTag);
  }

  public String getText(int maxLength) {
    return this.text.length() > maxLength ? this.text.substring(0, maxLength - 1) + '…' : this.text;
  }

  public Component getDialogText() {
    return this.isTranslationKey
        ? new TranslatableComponent(this.text)
        : new TextComponent(this.text);
  }

  public CompoundTag write(CompoundTag compoundTag) {
    compoundTag.putString(DATA_TEXT_TAG, this.text.trim());
    return compoundTag;
  }

  @Override
  public String toString() {
    return "DialogTextData{"
        + "id="
        + id
        + ", text='"
        + text
        + '\''
        + ", isTranslationKey="
        + isTranslationKey
        + '}';
  }
}

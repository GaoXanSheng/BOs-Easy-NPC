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

package de.markusbordihn.easynpc.client.screen.editor.action;

import de.markusbordihn.easynpc.client.screen.editor.action.ActionDataListEntry.OnDown;
import de.markusbordihn.easynpc.client.screen.editor.action.ActionDataListEntry.OnEdit;
import de.markusbordihn.easynpc.client.screen.editor.action.ActionDataListEntry.OnRemove;
import de.markusbordihn.easynpc.client.screen.editor.action.ActionDataListEntry.OnUp;
import de.markusbordihn.easynpc.data.action.ActionDataEntry;
import de.markusbordihn.easynpc.data.action.ActionDataSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;

class ActionDataList extends ObjectSelectionList<ActionDataListEntry> {

  public ActionDataList(
      ActionDataSet actionDataSet,
      Minecraft minecraft,
      int width,
      int height,
      int left,
      int top,
      int bottom,
      int entryHeight,
      OnUp onUp,
      OnDown onDown,
      OnEdit onEdit,
      OnRemove onRemove) {
    super(minecraft, width, height, top, bottom, entryHeight);
    this.setRenderHeader(false, 0);
    this.setRenderBackground(false);
    this.setRenderTopAndBottom(false);

    // Add entries
    int topPos = top + 4;
    if (actionDataSet != null) {
      for (ActionDataEntry actionDataEntry : actionDataSet.getEntries()) {
        this.addEntry(
            new ActionDataListEntry(
                minecraft,
                actionDataEntry,
                actionDataSet,
                left,
                topPos,
                onUp,
                onDown,
                onEdit,
                onRemove));
        topPos += entryHeight;
      }
    }
  }
}
